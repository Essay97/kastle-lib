package io.github.essay97.kastle.model.commands

import arrow.core.Either
import arrow.core.raise.either
import io.github.essay97.kastle.service.GameState
import io.github.essay97.kastle.service.MovementManager
import io.github.essay97.kastle.model.Direction
import io.github.essay97.kastle.model.Rooms
import io.github.essay97.kastle.model.nextaction.ConfirmOpen
import io.github.essay97.kastle.model.nextaction.NextAction
import io.github.essay97.kastle.error.KastleError

class OpenCommand(private val direction: Direction, private val movementManager: MovementManager, private val state: GameState) : GameCommand() {
    override fun execute(): Either<KastleError, NextAction> = either {
        val destination = (when (direction) {
            Direction.NORTH -> movementManager.openNorth()
            Direction.SOUTH -> movementManager.openSouth()
            Direction.WEST -> movementManager.openWest()
            Direction.EAST -> movementManager.openEast()
        }).bind()
        ConfirmOpen(Rooms.getById(state.currentRoom)!!, destination)
    }
}