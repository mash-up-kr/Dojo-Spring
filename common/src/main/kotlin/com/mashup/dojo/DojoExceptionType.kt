package com.mashup.dojo

enum class DojoExceptionType(
    val message: String,
    val errorCode: String,
    val httpStatusCode: Int,
) {
    // SAMPLE todo : 재정의시 제거 가능
    NOT_EXIST("존재하지 않습니다.", "C001_NOT_EXIST", 404),
    SYSTEM_FAIL("Internal Server Error.", "C002_SYSTEM_FAIL", 500),
    INVALID_ACCESS("Invalid Access", "C003_INVALID_ACCESS", 403),
    ALREADY_EXIST("Already Exist", "C004_ALREADY_EXIST", 409),
    INVALID_INPUT("Invalid Input", "C004_INVALID_INPUT", 400),
    METHOD_ARGUMENT_TYPE_MISMATCH_VALUE("Request method argument type mismatch", "C005_TYPE_MISMATCH_VALUE", 400),
    HTTP_REQUEST_METHOD_NOT_SUPPORTED("HTTP request method not supported", "C006_HTTP_METHOD_NOT_SUPPORTED", 400),
    ACCESS_DENIED("Access denied. Check authentication.", "C007_ACCESS_DENIED", 403),
    AUTHENTICATION_FAILURE("Authentication failed. Check login.", "C008_AUTHENTICATION_FAILURE", 401),
    ARGUMENT_NOT_VALID("Method Argument Not Valid. Check argument validation.", "C009_ARGUMENT_NOT_VALID", 400),
}
