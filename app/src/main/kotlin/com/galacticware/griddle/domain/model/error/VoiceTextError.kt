package com.galacticware.griddle.domain.model.error

enum class VoiceTextError(
    val code: Int,
    val message: String
) {
    ERROR_NETWORK_TIMEOUT(1, "Network timeout"),
    ERROR_NETWORK(2, "Network error"),
    ERROR_AUDIO(3, "Audio error"),
    ERROR_SERVER(4, "Server error"),
    ERROR_CLIENT(5, "Client error"),
    ERROR_SPEECH_TIMEOUT(6, "Speech timeout"),
    ERROR_NO_MATCH(7, "No match found"),
    ERROR_RECOGNIZER_BUSY(8, "Busy"),
    ERROR_INSUFFICIENT_PERMISSIONS(9, "Insufficient permissions"),
    ;
    companion object {
        fun valueFromErrorCode(errorCode: Int): VoiceTextError {
            return entries.firstOrNull { it.code == errorCode } ?: ERROR_CLIENT
        }
    }
}