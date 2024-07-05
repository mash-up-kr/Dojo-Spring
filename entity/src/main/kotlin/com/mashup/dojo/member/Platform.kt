package com.mashup.dojo.member

import java.lang.RuntimeException

enum class Platform {
    PRODUCT_DESIGN,
    WEB,
    IOS,
    ANDROID,
    SPRING,
    ;

    companion object {
        fun findByValue(value: String): Platform {
            return entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw RuntimeException("ToDo change to DoJoException")
            // ToDo
            // DojoException.of(DojoExceptionType.INVALID_MEMBER_PLATFORM)
        }
    }
}
