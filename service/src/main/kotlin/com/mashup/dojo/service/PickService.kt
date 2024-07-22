package com.mashup.dojo.service

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.PickEntity
import com.mashup.dojo.PickRepository
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.Pick
import com.mashup.dojo.domain.PickId
import com.mashup.dojo.domain.PickOpenItem
import com.mashup.dojo.domain.PickSort
import com.mashup.dojo.domain.QuestionId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

interface PickService {
    fun getReceivedPickList(
        pickedMemberId: MemberId,
        sort: PickSort,
    ): List<Pick>

    fun openPick(
        pickId: PickId,
        pickedId: MemberId,
        pickOpenItem: PickOpenItem,
    ): String
}

@Transactional(readOnly = true)
@Service
class DefaultPickService(
    private val pickRepository: PickRepository,
    private val memberService: MemberService,
) : PickService {
    override fun getReceivedPickList(
        pickedMemberId: MemberId,
        sort: PickSort,
    ): List<Pick> {
        /*val pickList: List<Pick> = pickRepository.findAllByMemberId(pickedMemberId)
             .map { PickEnity.buildDomain() }*/
        return listOf(DEFAULT_PICK)
    }

    @Transactional
    override fun openPick(
        pickId: PickId,
        pickedId: MemberId,
        pickOpenItem: PickOpenItem,
    ): String {
        val pick = findPickById(pickId) ?: throw DojoException.of(DojoExceptionType.PICK_NOT_FOUND)

        if (pick.pickedId != pickedId) {
            throw DojoException.of(DojoExceptionType.ACCESS_DENIED)
        }

        if (pick.isOpened(pickOpenItem)) {
            throw DojoException.of(DojoExceptionType.PICK_ALREADY_OPENED)
        }

        pickRepository.save(
            pick.open(pickOpenItem).toEntity()
        )

        val picker = memberService.findMemberById(pick.pickerId)
        return pick.getOpenItem(pickOpenItem, picker)
    }

    private fun findPickById(pickId: PickId): Pick? {
        return pickRepository.findById(pickId.value).map {
            Pick.of(
                it.id,
                it.questionId,
                it.pickerId,
                it.pickedId,
                it.isGenderOpen,
                it.isPlatformOpen,
                it.isMidInitialNameOpen,
                it.isFullNameOpen,
                it.createdAt,
                it.updatedAt
            )
        }.orElse(null)
    }

    companion object {
        val DEFAULT_PICK =
            Pick(
                id = PickId("pickmepickme"),
                questionId = QuestionId("question"),
                pickerId = MemberId("뽑은놈"),
                pickedId = MemberId("뽑힌놈"),
                isGenderOpen = false,
                isPlatformOpen = false,
                isMidInitialNameOpen = false,
                isFullNameOpen = false,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
//        private fun PickEntity.buildDomain(): Pick {
//            return Pick(
//                id = PickId("pickmepickme"),
//                questionId = QuestionId("question"),
//                pickerId = MemberId("뽑은놈"),
//                pickedId = MemberId("뽑힌놈"),
//                isGenderOpen = false,
//                isPlatformOpen = false,
//                isMidInitialNameOpen = false,
//                isFullNameOpen = false,
//                createdAt = createdAt,
//                updatedAt = updatedAt,
//            )
//        }
    }
}

private fun Pick.toEntity(): PickEntity {
    return PickEntity(
        id = id.value,
        questionId = questionId.value,
        pickerId = pickerId.value,
        pickedId = pickedId.value,
        isGenderOpen = isGenderOpen,
        isPlatformOpen = isPlatformOpen,
        isMidInitialNameOpen = isMidInitialNameOpen,
        isFullNameOpen = isFullNameOpen
    )
}
