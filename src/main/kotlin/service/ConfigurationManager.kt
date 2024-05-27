package io.github.essay97.kastle.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import io.github.essay97.kastle.dto.*
import io.github.essay97.kastle.error.KastleError
import io.github.essay97.kastle.error.SerializationError
import io.github.essay97.kastle.model.*
import java.io.File
import java.net.URLClassLoader
import java.util.*

interface GameProvider {
    fun provideConfiguration(): GameConfiguration
}

class ConfigurationManager {
    fun getManagersForGameClass(className: String): Either<KastleError, Managers> = either {
        val folder = File("${System.getProperty("user.home")}/.kastle/games/")

        val child = URLClassLoader(
            folder.listFiles().map { it.toURI().toURL() }.toTypedArray(),
            this.javaClass.classLoader
        )

        val gameProvider = ServiceLoader
            .load(GameProvider::class.java, child)
            .asIterable()
            .find { it::class.qualifiedName == className }

        ensureNotNull(gameProvider) {
            SerializationError("Could not load class $className")
        }

        val config = gameProvider.provideConfiguration()

        loadCharacters(config.characters ?: emptyList()).bind()
        loadItems(config.items ?: emptyList()).bind()
        loadRooms(config.rooms).bind()

        val dungeonMap = config.rooms.associate {
            RoomId(it.id).bind() to DungeonNode(
                north = it.links?.north?.toLink()?.bind(),
                south = it.links?.south?.toLink()?.bind(),
                east = it.links?.east?.toLink()?.bind(),
                west = it.links?.west?.toLink()?.bind()
            )
        }
        val state = GameState(RoomId(config.initialRoomId).bind())
        val movementManager = MovementManager(state, dungeonMap).bind()
        val interactableManager = InteractableManager(state)
        val informationManager =
            InformationManager(config.metadata?.toGameMetadata(), config.preface ?: "", config.player.toPlayer())
        val inventoryManager = InventoryManager(state)
        val runManager =
            RunManager(config.winningConditions?.toWinningConditions()?.bind(), state)

        Managers(
            state = state,
            movementManager = movementManager,
            interactableManager = interactableManager,
            informationManager = informationManager,
            inventoryManager = inventoryManager,
            runManager = runManager,
            commandManager = CommandManager()
        )
    }

    private fun loadRooms(rooms: List<RoomDto>): Either<KastleError, Unit> = either {
        rooms.forEach {
            Rooms.add(it.toRoom().bind())
        }
    }

    private fun loadItems(items: List<ItemDto>): Either<KastleError, Unit> = either {
        items.forEach {
            Items.add(it.toItem().bind())
        }
    }

    private fun loadCharacters(characters: List<CharacterDto>): Either<KastleError, Unit> = either {
        characters.forEach {
            Characters.add(it.toCharacter().bind())
        }
    }
}

class Managers(
    private val commandManager: CommandManager,
    private val interactableManager: InteractableManager,
    private val movementManager: MovementManager,
    private val informationManager: InformationManager,
    private val runManager: RunManager,
    private val inventoryManager: InventoryManager,
    private val state: GameState
) {
    operator fun component1(): CommandManager = commandManager
    operator fun component2(): InteractableManager = interactableManager
    operator fun component3(): MovementManager = movementManager
    operator fun component4(): InformationManager = informationManager
    operator fun component5(): RunManager = runManager
    operator fun component6(): InventoryManager = inventoryManager
    operator fun component7(): GameState = state
}