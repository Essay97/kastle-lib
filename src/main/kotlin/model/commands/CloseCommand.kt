package io.github.essay97.kastle.model.commands

import arrow.core.Either
import arrow.core.raise.either
import io.github.essay97.kastle.service.GameState
import io.github.essay97.kastle.service.MovementManager
import io.github.essay97.kastle.model.Direction
import io.github.essay97.kastle.model.Rooms
import io.github.essay97.kastle.model.nextaction.ConfirmClose
import io.github.essay97.kastle.model.nextaction.NextAction
import io.github.essay97.kastle.error.KastleError

class CloseCommand(
    private val direction: Direction, private val movementManager: MovementManager, val state: GameState
) : GameCommand() {
    override fun execute(): Either<KastleError, NextAction> = either {
        val destination = (when (direction) {
            Direction.NORTH -> movementManager.closeNorth()
            Direction.SOUTH -> movementManager.closeSouth()
            Direction.WEST -> movementManager.closeWest()
            Direction.EAST -> movementManager.closeEast()
        }).bind()
        ConfirmClose(Rooms.getById(state.currentRoom)!!, destination)
    }
}