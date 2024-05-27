package io.github.essay97.kastle.model

enum class LinkBehavior {
    OPENABLE {
        override val canOpen: Boolean = true
        override val canClose: Boolean = false
    },
    LOCKABLE {
        override val canOpen: Boolean = false
        override val canClose: Boolean = true
    },
    COMPLETE {
        override val canOpen: Boolean = true
        override val canClose: Boolean = true
    },
    CONSTANT {
        override val canOpen: Boolean = false
        override val canClose: Boolean = false
    };

    abstract val canOpen: Boolean
    abstract val canClose: Boolean
}

data class DungeonNode(
    val north: Link? = null,
    val south: Link? = null,
    val east: Link? = null,
    val west: Link? = null
)

data class Link(
    val destination: RoomId,
    var open: Boolean = true,
    val behavior: LinkBehavior = LinkBehavior.COMPLETE,
    val triggeredBy: List<ItemId> = emptyList()
)