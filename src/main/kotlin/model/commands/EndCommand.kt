package io.github.essay97.kastle.model.commands

import arrow.core.Either
import arrow.core.right
import io.github.essay97.kastle.service.RunManager
import io.github.essay97.kastle.model.nextaction.EndGame
import io.github.essay97.kastle.model.nextaction.NextAction
import io.github.essay97.kastle.error.KastleError

class EndCommand(private val runManager: RunManager) : GameCommand() {
    override fun execute(): Either<KastleError, NextAction> {
        runManager.isRunning = false
        return EndGame.right()
    }

}