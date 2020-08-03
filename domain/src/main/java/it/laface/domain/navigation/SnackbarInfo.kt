package it.laface.domain.navigation

data class SnackbarInfo(
    val text: Text,
    val duration: SnackbarDuration,
    val action: SnackbarAction?
) {

    companion object {

        operator fun invoke(
            text: String,
            duration: SnackbarDuration = SnackbarDuration.DEFAULT,
            action: SnackbarAction? = null
        ): SnackbarInfo =
            SnackbarInfo(Text.StringText(text), duration, action)

        operator fun invoke(
            resId: Int,
            duration: SnackbarDuration = SnackbarDuration.DEFAULT,
            action: SnackbarAction? = null
        ): SnackbarInfo =
            SnackbarInfo(Text.ResIdText(resId), duration, action)
    }
}

sealed class Text {
    class StringText(val value: String) : Text()
    class ResIdText(val value: Int) : Text()
}

enum class SnackbarDuration {
    Indefinite,
    Short,
    Long;

    companion object {
        val DEFAULT: SnackbarDuration
            get() = Long
    }
}

data class SnackbarAction(val text: Text, val callback: () -> Unit)
