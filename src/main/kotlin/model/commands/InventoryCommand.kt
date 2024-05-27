package io.github.essay97.kastle.model.commands

import arrow.core.Either
import arrow.core.right
import io.github.essay97.kastle.model.nextaction.NextAction
import io.github.essay97.kastle.model.nextaction.ShowInventory
import io.github.essay97.kastle.error.KastleError

class InventoryCommand : GameCommand() {
    override fun execute(): Either<KastleError, NextAction> = ShowInventory.right()
}