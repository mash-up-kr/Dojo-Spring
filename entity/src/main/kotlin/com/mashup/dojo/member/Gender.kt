package com.mashup.dojo.member

import java.lang.RuntimeException

enum class Gender {
    MALE,
    FEMALE,
    ;

    companion object {
        fun findByValue(value: String): Gender {
            return Gender.entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw RuntimeException("ToDo change to DoJoException")
            // ToDo
            // DojoException.of(DojoExceptionType.INVALID_MEMBER_PLATFORM)
        }
    }
}
