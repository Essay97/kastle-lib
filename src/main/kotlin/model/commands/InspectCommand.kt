package io.github.essay97.kastle.model.commands

import arrow.core.Either
import arrow.core.raise.either
import io.github.essay97.kastle.service.InteractableManager
import io.github.essay97.kastle.model.nextaction.DescribeInspectable
import io.github.essay97.kastle.model.nextaction.NextAction
import io.github.essay97.kastle.error.KastleError

class InspectCommand(
    private val matcher: String,
    private val interactableManager: InteractableManager
) : GameCommand() {

    override fun execute(): Either<KastleError, NextAction> = either {
        DescribeInspectable(interactableManager.getForInspection(matcher).bind())
    }
}