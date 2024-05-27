package io.github.essay97.kastle.dsl

import io.github.essay97.kastle.dto.*
import kotlinx.datetime.LocalDate
import io.github.essay97.kastle.model.LinkBehavior

@DslMarker
annotation class KastleDsl

@KastleDsl
fun game(initialRoomId: String, init: GameScope.() -> Unit): GameConfiguration {
    val scope = GameScope(initialRoomId)
    scope.init()
    return scope.build()
}

@KastleDsl
class GameScope(private val initialRoomId: String) {
    private var metadata = MetadataDto()
    private var player = PlayerDto("Player", "Default player")
    private var rooms = mutableListOf<RoomDto>()
    private var items = mutableListOf<ItemDto>()
    private var characters = mutableListOf<CharacterDto>()
    private var winningConditions: WinningConditionsDto? = null

    var preface: String? = null


    fun metadata(init: MetadataScope.() -> Unit) {
        val scope = MetadataScope()
        scope.init()
        metadata = scope.build()
    }

    fun player(init: PlayerScope.() -> Unit) {
        val scope = PlayerScope()
        scope.init()
        player = scope.build()
    }

    fun room(roomId: String, init: RoomScope.() -> Unit) {
        val scope = RoomScope(roomId)
        scope.init()
        val result = scope.build()
        items += result.items
        characters += result.characters
        rooms += result.room
    }

    fun winIf(init: WinningConditionsScope.() -> Unit) {
        val scope = WinningConditionsScope()
        scope.init()
        winningConditions = scope.build()
    }

    fun build(): GameConfiguration = GameConfiguration(
        initialRoomId = initialRoomId,
        metadata = metadata,
        player = player,
        rooms = rooms,
        items = items,
        characters = characters,
        preface = preface,
        winningConditions = winningConditions
    )


}

@KastleDsl
class MetadataScope {
    var author: String? = null
    var version: String? = null
    var published: LocalDate? = null
    var kastleVersions: List<String>? = null
    var name: String? = null

    fun build(): MetadataDto = MetadataDto(
        author = author,
        version = version,
        published = published,
        kastleVersions = kastleVersions,
        name = name
    )
}

@KastleDsl
class PlayerScope {
    var name = "Player"
    var description: String? = null

    fun build(): PlayerDto = PlayerDto(
        name = name,
        description = description
    )
}

@KastleDsl
class RoomScope(private val roomId: String) {
    var name = roomId
    var description: String? = null

    private var north: DirectionDto? = null
    private var south: DirectionDto? = null
    private var east: DirectionDto? = null
    private var west: DirectionDto? = null
    private val items: MutableList<ItemDto> = mutableListOf()
    private val characters: MutableList<CharacterDto> = mutableListOf()

    fun north(roomId: String, init: DirectionScope.() -> Unit = {}) {
        val scope = DirectionScope(roomId)
        scope.init()
        north = scope.build()
    }

    fun south(roomId: String, init: DirectionScope.() -> Unit = {}) {
        val scope = DirectionScope(roomId)
        scope.init()
        south = scope.build()
    }

    fun west(roomId: String, init: DirectionScope.() -> Unit = {}) {
        val scope = DirectionScope(roomId)
        scope.init()
        west = scope.build()
    }

    fun east(roomId: String, init: DirectionScope.() -> Unit = {}) {
        val scope = DirectionScope(roomId)
        scope.init()
        east = scope.build()
    }

    fun build(): BuildResult = BuildResult(
        room = RoomDto(
            name = name,
            id = roomId,
            items = items.map { it.id },
            characters = characters.map { it.id },
            description = description,
            links = LinksDto(north, south, east, west)
        ),
        characters = characters,
        items = items
    )

    fun character(characterId: String, init: CharacterScope.() -> Unit) {
        val scope = CharacterScope(characterId)
        scope.init()
        val result = scope.build()
        characters += result.character
        items += result.items
    }

    fun item(itemId: String, init: ItemScope.() -> Unit) {
        val scope = ItemScope(itemId)
        scope.init()
        items += scope.build()
    }

    class BuildResult(val room: RoomDto, val items: List<ItemDto>, val characters: List<CharacterDto>)
}

@KastleDsl
class DirectionScope(private val roomId: String) {
    var state = LinkState.OPEN
    var behavior = LinkBehavior.CONSTANT

    private var triggers: List<String> = listOf()

    fun triggers(vararg itemIds: String) {
        triggers = itemIds.asList()
    }

    fun build(): DirectionDto = DirectionDto(
        roomId = roomId,
        state = DirectionStateDto(state, behavior, triggers),
    )
}

@KastleDsl
class CharacterScope(private val characterId: String) {
    var name = characterId
    var description: String? = null
    private var matchers: List<String> = listOf()
    private var dialogue: DialogueDto? = null
    // This is needed to store the items that are created as dialogue rewards
    private var items: List<ItemDto> = mutableListOf()

    fun matchers(vararg words: String) {
        matchers = words.asList()
    }

    fun dialogue(init: DialogueScope.() -> Unit) {
        val scope = DialogueScope()
        scope.init()
        val result = scope.build()
        dialogue = result.dialogue
        items = result.items
    }

    fun build(): BuildResult = BuildResult(
        character = CharacterDto(
            id = characterId,
            name = name,
            description = description,
            matchers = matchers,
            dialogue = dialogue
        ),
        items = items
    )

    class BuildResult(val character: CharacterDto, val items: List<ItemDto>)
}

@KastleDsl
class ItemScope(private val itemId: String) {
    var name = itemId
    var description: String? = null
    var storable: Boolean = false
    private var matchers: List<String> = listOf()

    fun matchers(vararg words: String) {
        matchers = words.asList()
    }

    fun build(): ItemDto = ItemDto(
        name = name,
        id = itemId,
        description = description,
        matchers = matchers,
        /* At the moment the use is a string, even if it's not really needed.
         This workaround makes the DSL more legible and makes more sense */
        use = if (storable) "" else null
    )
}

@KastleDsl
class DialogueScope {
    private var questions: MutableList<QuestionDto> = mutableListOf()
    private var firstQuestionId = "d-default-question"
    // This is needed to store the items that are created as dialogue rewards
    private var items: MutableList<ItemDto> = mutableListOf()

    fun firstQuestion(questionId: String, init: QuestionScope.() -> Unit) {
        val scope =  QuestionScope(questionId)
        scope.init()
        firstQuestionId = questionId
        val result = scope.build()
        questions += result.question
        if (result.item != null) {
            items += result.item
        }
    }

    fun question(questionId: String, init: QuestionScope.() -> Unit) {
        val scope =  QuestionScope(questionId)
        scope.init()
        val result = scope.build()
        questions += result.question
        if (result.item != null) {
            items += result.item
        }

    }

    fun build(): BuildResult = BuildResult(
        dialogue = DialogueDto(
            firstQuestion = firstQuestionId,
            questions = questions
        ),
        items = items
    )

    class BuildResult(val dialogue: DialogueDto, val items: List<ItemDto>)
}

@KastleDsl
class QuestionScope(private val questionId: String) {
    var text = "Default question"
    private var reward: ItemDto? = null //Item id of the object that will be dropped

    private var answers: MutableList<AnswerDto> = mutableListOf()

    fun answer(init: AnswerScope.() -> Unit) {
        val scope = AnswerScope()
        scope.init()
        answers += scope.build()
    }

    fun reward(itemId: String, init: ItemScope.() -> Unit) {
        val scope = ItemScope(itemId)
        scope.init()
        reward = scope.build()
    }

    fun build(): BuildResult = BuildResult(
        question = QuestionDto(
            id = questionId,
            question = text,
            answers = answers,
            reward = reward?.id
        ),
        item = reward
    )

    class BuildResult(val question: QuestionDto, val item: ItemDto?)
}

@KastleDsl
class AnswerScope {
    var text = "Default answer"
    var nextQuestion = "d-default-question"

    fun build(): AnswerDto = AnswerDto(
        text = text,
        nextQuestion = nextQuestion
    )
}

@KastleDsl
class WinningConditionsScope {
    var playerOwns: String? = null
    var playerEnters: String? = null

    fun build(): WinningConditionsDto = WinningConditionsDto(
        playerOwns = playerOwns,
        playerEnters = playerEnters
    )
}

