package com.mashup.dojo.api

import com.mashup.dojo.DojoExceptionType

data class DojoApiResponse<T>(
    val success: Boolean = true,
    val data: T? = null,
    val error: DojoApiErrorResponse? = null,
) {
    companion object {
        fun success(): DojoApiResponse<Unit> {
            return DojoApiResponse(
                success = true,
                data = null
            )
        }

        fun <T> success(data: T): DojoApiResponse<T> {
            return DojoApiResponse(
                success = true,
                data = data
            )
        }

        fun fail(dojoExceptionType: DojoExceptionType, message: String?): DojoApiResponse<Unit> {
            return DojoApiResponse(
                success = true,
                data = null,
                error = DojoApiErrorResponse(
                    code = dojoExceptionType.errorCode,
                    message = message ?: dojoExceptionType.message,
                )
            )
        }
    }
}

data class DojoApiErrorResponse(
    val code: String,
    val message: String?,
)
