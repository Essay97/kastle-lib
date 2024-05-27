package io.github.essay97.kastle.model.commands

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import io.github.essay97.kastle.service.GameState
import io.github.essay97.kastle.service.InventoryManager
import io.github.essay97.kastle.model.Items
import io.github.essay97.kastle.model.nextaction.ConfirmDrop
import io.github.essay97.kastle.model.nextaction.NextAction
import io.github.essay97.kastle.error.GameRuntimeError
import io.github.essay97.kastle.error.KastleError

class DropCommand(
    private val matcher: String,
    private val inventoryManager: InventoryManager,
    private val state: GameState
) : GameCommand() {
    override fun execute(): Either<KastleError, NextAction> = either {
        val itemId = state.inventory.find { matcher in Items.getById(it)!!.matchers }
        ensureNotNull(itemId) { GameRuntimeError.ItemNotInInventory(matcher) }
        inventoryManager.removeItem(itemId)
        ConfirmDrop(Items.getStorableById(itemId)!!)
    }
}