package com.mashup.dojo

enum class DojoExceptionType(
    val message: String,
    val errorCode: String,
    val httpStatusCode: Int,
) {
    // SAMPLE todo : 재정의시 제거 가능
    NOT_EXIST("존재하지 않습니다.", "C001_NOT_EXIST", 404),
}
