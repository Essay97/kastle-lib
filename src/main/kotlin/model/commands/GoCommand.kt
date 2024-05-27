package io.github.essay97.kastle.model.commands

import arrow.core.Either
import arrow.core.raise.either
import io.github.essay97.kastle.service.MovementManager
import io.github.essay97.kastle.model.Direction
import io.github.essay97.kastle.model.nextaction.DescribeCurrentRoom
import io.github.essay97.kastle.model.nextaction.NextAction
import io.github.essay97.kastle.error.KastleError

class GoCommand(private val direction: Direction, private val movementManager: MovementManager) : GameCommand() {
    override fun execute(): Either<KastleError, NextAction> = either {
        (when (direction) {
            Direction.NORTH -> movementManager.moveNorth()
            Direction.SOUTH -> movementManager.moveSouth()
            Direction.WEST -> movementManager.moveWest()
            Direction.EAST -> movementManager.moveEast()
        }).bind()

        DescribeCurrentRoom
    }

}