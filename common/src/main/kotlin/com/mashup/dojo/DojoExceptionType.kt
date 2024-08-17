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
    ARGUMENT_NOT_VALID("Method Argument Not Valid. Check argument validation.", "C009_ARGUMENT_NOT_VALID", 400),
    INVALID_MEMBER_GENDER("The gender does not exist.", "C011_INVALID_MEMBER_GENDER", 400),
    INVALID_MEMBER_PLATFORM("The platform does not exist.", "C010_INVALID_MEMBER_PLATFORM", 400),
    MEMBER_NOT_FOUND("Member not found", "C012_MEMBER_NOT_FOUND", 400),
    IMAGE_NOT_FOUND("Image not found", "C013_IMAGE_NOT_FOUND", 400),

    // pick
    PICK_NOT_FOUND("Pick not found", "C050_PICK_NOT_FOUND", 400),
    PICK_ALREADY_OPENED("Pick already opened", "C051_PICK_ALREADY_OPENED", 400),
    INVALID_PICK_OPEN_ITEM("Pick open item does not exist", "CO52_INVALID_PICK_OPEN_ITEM", 400),

    // Question
    QUESTION_SET_NOT_READY("QuestionSet for publish not ready", "Q001_QUESTION_SET_NOT_EXIST", 500),
    QUESTION_SET_OPERATING_NOT_EXIST("QuestionSet for operation not exist", "Q002_QUESTION_SET_NOT_EXIST", 500),
    QUESTION_SET_NOT_EXIST("QuestionSet is not exist", "Q003_QUESTION_SET_NOT_READY", 400),
    QUESTION_NOT_EXIST("Question is not exist", "Q004_QUESTION_NOT_EXIST", 500),
    QUESTION_SHEET_NOT_EXIST("QuestionSheet is not exist", "Q005_QUESTION_SHEET_NOT_EXIST", 500),
    QUESTION_LACK_FOR_CREATE_QUESTION_SET("Question is lack for creation QSet", "Q006_QUESTION_LACK", 500),
    QUESTION_INVALID_TYPE("Question Type is Invalid Type", "Q007_QUESTION_TYPE", 500),
    QUESTION_NOT_FOUND("Question is not found", "Q005_QUESTION_NOT_EXIST", 404),

    // friend
    FRIEND_NOT_FOUND("Friend not found", "C070_FRIEND_NOT_FOUND", 400),
    ALREADY_FRIEND("Already Friend", "C071_ALREADY_FRIEND", 400),

    // auth
    INVALID_TOKEN("invalid token", "C100_INVALID_TOKEN", 401),
    AUTHENTICATION_FAILURE("Authentication failed. Check login.", "C101_AUTHENTICATION_FAILURE", 401),

    // pickTime
    ACTIVE_PICK_TIME_NOT_FOUND("Active pick time not found", "PT001_ACTIVE_PICK_TIME_NOT_FOUND", 500),

    // MemberRelation
    RELATION_NOT_FOUND("Relation not found", "MR001_RELATION_NOT_FOUND", 500),

    // Pick Paging Sort
    SORT_CLIENT_NOT_FOUND("Bad SortType", "SORT_NOT_FOUND", 400),
    SORT_NOT_FOUND("Sort Type not found", "SORT_NOT_FOUND", 500),
}
