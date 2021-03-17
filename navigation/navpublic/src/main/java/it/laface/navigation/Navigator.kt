package it.laface.navigation

interface Navigator {

    fun navigateBack()

    fun navigateForward(destination: Page, addToStack: Boolean = true)

    fun clearStack()
}
