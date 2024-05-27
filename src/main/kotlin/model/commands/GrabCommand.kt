package io.github.essay97.kastle.model.commands

import arrow.core.Either
import arrow.core.raise.either
import io.github.essay97.kastle.service.InteractableManager
import io.github.essay97.kastle.service.InventoryManager
import io.github.essay97.kastle.model.Items
import io.github.essay97.kastle.model.nextaction.ConfirmGrab
import io.github.essay97.kastle.model.nextaction.NextAction
import io.github.essay97.kastle.error.KastleError

class GrabCommand(
    private val matcher: String,
    private val inventoryManager: InventoryManager,
    private val interactableManager: InteractableManager
) : GameCommand() {
    override fun execute(): Either<KastleError, NextAction> = either {
        val itemId = interactableManager.getForGrab(matcher).bind()
        inventoryManager.addItem(itemId).bind()
        ConfirmGrab(Items.getStorableById(itemId)!!)
    }
}