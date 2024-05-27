package io.github.essay97.kastle.model

object Rooms {
    private val rooms = mutableMapOf<RoomId, Room>()

    fun getById(id: RoomId): Room? {
        return rooms[id]
    }

    fun add(vararg rooms: Room) {
        rooms.forEach {
            this.rooms[it.id] = it
        }
    }
}