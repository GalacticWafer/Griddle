package com.galacticware.griddle.domain.model.error;

enum class Errors(
    private val clazz: Class<out Exception>,
    private val message: String = "",
) {
    UNSATISFACTORY_LAYER_SET(IllegalArgumentException::class.java, ),
    BUTTON_WITH_NOT_FOUND(IllegalStateException::class.java, "Button with symbol not found"),
    NEGATIVE_SPAN(IllegalArgumentException::class.java, "Span must be greater than zero"),
    MISSING_LAYER_DEFINITION(IllegalArgumentException::class.java, "No alpha layer found"),
    UNKNOWN_GESTURE_TYPE(IllegalArgumentException::class.java),
    INVALID_BUTTON_TYPE(IllegalArgumentException::class.java, "Invalid button type"),
    UNKNOWN_LAYER_ALIAS(IllegalArgumentException::class.java, "No layer with alias"),
    UNKNOWN_OPERATION(IllegalArgumentException::class.java, "Unknown operation type:"),
    EXPIRED_TEST_APP(IllegalStateException::class.java, "This test app has expired. Please update to use the latest version."),
    WORD_PREDICTION_DISABLED(IllegalStateException::class.java, "Word prediction is disabled"),
    UNSUPPORTED_OPERATION_REMAPPING(UnsupportedOperationRemappingException::class.java),
    MISSING_CONTEXT(NullPointerException::class.java, "The Context has not been set for actions,"),
    ;

    fun send(vararg details: String?): Exception = run {
        val exception = clazz.constructors.first {
            it.parameterCount == 1 && it.parameterTypes[0] == String::class.java
        }.newInstance(
            "${clazz.name}:ERR_${code}:$name:$message:[${details.joinToString(". ")}]"
        ) as Exception
        exception
    }

    private val code = ordinal + 1

    /**
     * Todo, make an overload that sends as a response to a request.
     */
}

class UnsupportedOperationRemappingException(s: String): Exception(s)