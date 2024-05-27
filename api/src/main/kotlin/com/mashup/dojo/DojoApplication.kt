package com.mashup.dojo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
class DojoApplication

fun main(args: Array<String>) {
    runApplication<DojoApplication>(*args)
}
