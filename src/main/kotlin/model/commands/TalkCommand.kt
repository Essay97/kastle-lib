package io.github.essay97.kastle.model.commands

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import io.github.essay97.kastle.service.InteractableManager
import io.github.essay97.kastle.model.nextaction.ExecuteDialogue
import io.github.essay97.kastle.model.nextaction.NextAction
import io.github.essay97.kastle.error.GameRuntimeError
import io.github.essay97.kastle.error.KastleError
import io.github.essay97.kastle.model.Characters

class TalkCommand(private val matcher: String, private val interactableManager: InteractableManager) : GameCommand() {
    override fun execute(): Either<KastleError, NextAction> = either {
        val characterId = interactableManager.getForDialogue(matcher).bind()
        val character = Characters.getById(characterId)!!
        // getDialogue() must bind before checking if the character is idling or CharacterNotTalker error will never be raised
        val dialogue = character.getDialogue().bind()
        ensure(!character.idling) { GameRuntimeError.CharacterAlreadyTalked(character.name) }
        character.idling = true
        ExecuteDialogue(dialogue)
    }
}