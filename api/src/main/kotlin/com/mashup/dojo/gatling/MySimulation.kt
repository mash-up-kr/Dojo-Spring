package com.mashup.dojo.gatling

import io.gatling.javaapi.core.*
import io.gatling.javaapi.http.HttpDsl.*
import io.gatling.javaapi.core.CoreDsl.*

class MySimulation : Simulation() {

    private val httpProtocol = http
        .baseUrl("https://docker-ecs.net/") // 테스트할 서버의 URL
        .acceptHeader("application/json")

    private val header = mapOf("Authorization" to
            "Bearer eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJfaWQiOiIzNDFjM2Q1My05NmNmLTQzMmItOGY2My0wMzFjMDFhNGI0YzgiLCJleHAiOjQzMTczNzY0MzEsImlhdCI6MTcyNTM3NjQzMX0.UCxTykSGiQp8LYNbUtIYnit8rcP0qGk3fT53hOdffEw"
    )
    
    private val scn = scenario("Basic Load Test")
        .exec(
            http("Get Request")
                .get("/notification-stream") // API 엔드포인트
                .headers(header)
                .check(status().`is`(200)) // 200 상태 코드 확인
        )
        .pause(1) // 1초 대기

    init {
        setUp(
            scn.injectOpen(atOnceUsers(1)) // 한 번에 10명의 사용자 시뮬레이션
        ).protocols(httpProtocol)
    }
}
