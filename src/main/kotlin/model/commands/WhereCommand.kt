package io.github.essay97.kastle.model.commands

import arrow.core.Either
import arrow.core.right
import io.github.essay97.kastle.service.MovementManager
import io.github.essay97.kastle.model.nextaction.DescribeCurrentRoom
import io.github.essay97.kastle.model.nextaction.NextAction
import io.github.essay97.kastle.error.KastleError

class WhereCommand(private val movementManager: MovementManager) : GameCommand() {
    override fun execute(): Either<KastleError, NextAction> = DescribeCurrentRoom.right()
}