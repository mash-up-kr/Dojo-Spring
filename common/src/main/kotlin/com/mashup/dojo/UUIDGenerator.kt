package com.mashup.dojo

/**
 * UUID generator
 */
object UUIDGenerator {
    fun generate(): String {
        return java.util.UUID.randomUUID().toString()
    }
}
