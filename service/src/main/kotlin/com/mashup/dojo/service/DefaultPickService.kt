package com.mashup.dojo.service

import com.mashup.dojo.PickEntity
import com.mashup.dojo.PickRepository
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.Pick
import com.mashup.dojo.domain.PickId
import com.mashup.dojo.domain.PickSort
import com.mashup.dojo.domain.QuestionId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface PickService {
    val pickRepository: PickRepository

    fun getReceivedPickList(
        pickedMemberId: MemberId,
        sort: PickSort,
    ): List<Pick>

    fun create(
        questionId: QuestionId,
        pickerMemberId: MemberId,
        pickedMemberId: MemberId,
    ): PickId
}

@Service
@Transactional(readOnly = true)
class DefaultPickService(
    override val pickRepository: PickRepository,
) : PickService {
    override fun getReceivedPickList(
        pickedMemberId: MemberId,
        sort: PickSort,
    ): List<Pick> {
        return pickRepository.findAllByPickedId(pickedMemberId.value)
            .map { it.buildDomain() }
    }

    @Transactional
    override fun create(
        questionId: QuestionId,
        pickerMemberId: MemberId,
        pickedMemberId: MemberId,
    ): PickId {
        val pick =
            Pick.create(
                questionId = questionId,
                pickerId = pickerMemberId,
                pickedId = pickedMemberId
            )

        val id: String = pickRepository.save(pick.toEntity()).id
        return PickId(id)
    }

    companion object {
        private fun PickEntity.buildDomain(): Pick {
            return Pick(
                id = PickId("pickmepickme"),
                questionId = QuestionId("question"),
                pickerId = MemberId("뽑은놈"),
                pickedId = MemberId("뽑힌놈"),
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }

        private fun Pick.toEntity(): PickEntity {
            return PickEntity(
                id = id.value,
                questionId = questionId.value,
                pickerId = pickerId.value,
                pickedId = pickedId.value
            )
        }
    }
}
