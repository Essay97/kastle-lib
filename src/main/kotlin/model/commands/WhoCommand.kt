package io.github.essay97.kastle.model.commands

import arrow.core.Either
import arrow.core.right
import io.github.essay97.kastle.model.nextaction.DescribePlayer
import io.github.essay97.kastle.model.nextaction.NextAction
import io.github.essay97.kastle.error.KastleError

class WhoCommand : GameCommand() {
    override fun execute(): Either<KastleError, NextAction> = DescribePlayer.right()

}