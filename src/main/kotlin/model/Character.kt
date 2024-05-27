package io.github.essay97.kastle.model

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import io.github.essay97.kastle.model.capabilities.Inspectable
import io.github.essay97.kastle.error.GameRuntimeError

data class Character(
    val id: CharacterId,
    val name: String,
    private val dialogue: Question?,
    override val description: String,
    override val matchers: List<String>
) : Inspectable {
    var idling: Boolean = dialogue == null // If the dialogue is null, the character is already idling, otherwise active

    fun getDialogue(): Either<GameRuntimeError.CharacterNotTalker, Dialogue> = either {
        ensureNotNull(dialogue) { GameRuntimeError.CharacterNotTalker(name) }
        Dialogue(dialogue, id)
    }
}