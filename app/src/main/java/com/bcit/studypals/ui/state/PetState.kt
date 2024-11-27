package com.bcit.studypals.ui.state

enum class State {
    IDLE,
    RUNNING,
    ATTACKING,
}

data class PetState(
    val state: State,
    val frames: Int,
    val sizePerFrame: Int = 320,
)

