package com.mashup.dojo.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("com.mashup.dojo")
class DojoApplication

fun main(args: Array<String>) {
    runApplication<DojoApplication>(*args)
}
