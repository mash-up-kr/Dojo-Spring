package com.mashup.dojo

class DojoException(
    val errorCode: String,
    val httpStatusCode: Int,
    override val message: String,
) : RuntimeException() {
    companion object {
        fun of(type: DojoExceptionType): DojoException {
            return DojoException(
                errorCode = type.errorCode,
                httpStatusCode = type.httpStatusCode,
                message = type.message,
            )
        }

        fun of(type: DojoExceptionType, message: String): DojoException {
            return DojoException(
                errorCode = type.errorCode,
                httpStatusCode = type.httpStatusCode,
                message = message,
            )
        }
    }
}
