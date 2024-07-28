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
    INVALID_MEMBER_GENDER("The gender does not exist.", "C011_INVALID_MEMBER_GENDER", 400),
    INVALID_MEMBER_PLATFORM("The platform does not exist.", "C010_INVALID_MEMBER_PLATFORM", 400),
    MEMBER_NOT_FOUND("Member not found", "C012_MEMBER_NOT_FOUND", 400),

    // pick
    PICK_NOT_FOUND("Pick not found", "C050_PICK_NOT_FOUND", 400),
    PICK_ALREADY_OPENED("Pick already opened", "C051_PICK_ALREADY_OPENED", 400),
    INVALID_PICK_OPEN_ITEM("Pick open item does not exist", "CO52_INVALID_PICK_OPEN_ITEM", 400),

    // Question 관련
    QUESTION_SET_NOT_READY("QuestionSet for publish not ready", "Q001_QUESTIO_SET_NOT_EXIST", 500),
}
