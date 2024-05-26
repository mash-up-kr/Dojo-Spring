package com.mashup.dojo.usecase

import com.mashup.dojo.domain.DojoDomain
import org.springframework.stereotype.Component

@Component // Todo Component & Autowired 사용할건지
class TestUsecase {
    fun dojoName(): String {
        val dojoDomain = DojoDomain("do~jo")
        return dojoDomain.name()
    }
}
