package io.github.essay97.kastle.service

import io.github.essay97.kastle.model.ItemId
import io.github.essay97.kastle.model.RoomId

data class GameState(var currentRoom: RoomId) {
    val inventory =  mutableListOf<ItemId>()
}