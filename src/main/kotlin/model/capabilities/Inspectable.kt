package io.github.essay97.kastle.model.capabilities

interface Inspectable {
    val description: String
    val matchers: List<String>
}