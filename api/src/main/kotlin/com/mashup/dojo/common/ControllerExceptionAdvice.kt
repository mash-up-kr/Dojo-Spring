package com.mashup.dojo.common

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val log = KotlinLogging.logger {}

@RestControllerAdvice
class ControllerExceptionAdvice {
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<DojoApiResponse<Any>> {
        log.error { "Exception Handler $ex" }
        return errorResponse(DojoExceptionType.SYSTEM_FAIL, ex.message)
    }

    @ExceptionHandler(DojoException::class)
    fun handleDojoException(ex: DojoException): ResponseEntity<DojoApiResponse<Any>> {
        log.error { "Dojo Exception Handler $ex" }
        return errorResponse(ex.httpStatusCode, ex.toApiErrorResponse())
    }

    private fun DojoException.toApiErrorResponse(): DojoApiErrorResponse = DojoApiErrorResponse(code = errorCode, message = message)

    private fun errorResponse(
        exceptionType: DojoExceptionType,
        message: String?,
    ): ResponseEntity<DojoApiResponse<Any>> = errorResponse(exceptionType.httpStatusCode, DojoApiErrorResponse(exceptionType.name, message))

    private fun errorResponse(
        status: Int,
        errorResponse: DojoApiErrorResponse,
    ): ResponseEntity<DojoApiResponse<Any>> = errorResponse(HttpStatus.valueOf(status), errorResponse)

    private fun errorResponse(
        status: HttpStatus,
        errorResponse: DojoApiErrorResponse,
    ): ResponseEntity<DojoApiResponse<Any>> =
        ResponseEntity.status(status)
            .body(
                DojoApiResponse(
                    success = false,
                    data = null,
                    error = errorResponse
                )
            )
}
