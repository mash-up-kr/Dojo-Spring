package com.mashup.dojo.service

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.PickEntity
import com.mashup.dojo.PickRepository
import com.mashup.dojo.PickTimeRepository
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.Pick
import com.mashup.dojo.domain.PickId
import com.mashup.dojo.domain.PickOpenItem
import com.mashup.dojo.domain.PickSort
import com.mashup.dojo.domain.QuestionId
import com.mashup.dojo.domain.QuestionSetId
import com.mashup.dojo.domain.QuestionSheetId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneId

interface PickService {
    fun getReceivedPickList(
        pickedMemberId: MemberId,
        sort: PickSort,
    ): List<Pick>

    fun getSolvedPickList(
        pickerMemberId: MemberId,
        questionSetId: QuestionSetId,
    ): List<Pick>

    fun create(
        questionId: QuestionId,
        questionSetId: QuestionSetId,
        questionSheetId: QuestionSheetId,
        pickerMemberId: MemberId,
        pickedMemberId: MemberId,
    ): PickId

    fun openPick(
        pickId: PickId,
        pickedId: MemberId,
        pickOpenItem: PickOpenItem,
    ): String

    fun getPickPaging(
        id: QuestionId,
        memberId: MemberId,
        pageNumber: Int,
        pageSize: Int,
    ): Page<Pick>

    fun getPickCount(
        id: QuestionId,
        memberId: MemberId,
    ): Int

    fun getNextPickTime(): LocalDateTime
}

@Transactional(readOnly = true)
@Service
class DefaultPickService(
    private val pickRepository: PickRepository,
    private val memberService: MemberService,
    private val pickTimeRepository: PickTimeRepository,
) : PickService {
    override fun getReceivedPickList(
        pickedMemberId: MemberId,
        sort: PickSort,
    ): List<Pick> {
        return pickRepository.findAllByPickedId(pickedMemberId.value)
            .map { it.toPick() }
        // return listOf(DEFAULT_PICK)
    }

    override fun getSolvedPickList(
        pickerMemberId: MemberId,
        questionSetId: QuestionSetId,
    ): List<Pick> {
        TODO("Not yet implemented")
    }

    @Transactional
    override fun create(
        questionId: QuestionId,
        questionSetId: QuestionSetId,
        questionSheetId: QuestionSheetId,
        pickerMemberId: MemberId,
        pickedMemberId: MemberId,
    ): PickId {
        val pick =
            Pick.create(
                questionId = questionId,
                questionSetId = questionSetId,
                questionSheetId = questionSheetId,
                pickerId = pickerMemberId,
                pickedId = pickedMemberId
            )

        val id: String = pickRepository.save(pick.toEntity()).id
        return PickId(id)
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
            pick.updateOpenItem(pickOpenItem).toEntity()
        )

        val picker = memberService.findMemberById(pick.pickerId) ?: throw DojoException.of(DojoExceptionType.MEMBER_NOT_FOUND)
        return pick.getOpenItem(pickOpenItem, picker)
    }

    private fun findPickById(pickId: PickId): Pick? {
        return pickRepository.findByIdOrNull(pickId.value)?.toPick()
    }

    override fun getPickPaging(
        id: QuestionId,
        memberId: MemberId,
        pageNumber: Int,
        pageSize: Int,
    ): Page<Pick> {
        val pageable = PageRequest.of(pageNumber, pageSize)
        return PageImpl(SAMPLE_PICK_LIST, pageable, 1L)
    }

    override fun getPickCount(
        id: QuestionId,
        memberId: MemberId,
    ): Int {
        // ToDo Pick getCount
        return 10
    }

    override fun getNextPickTime(): LocalDateTime {
        val currentTime = LocalDateTime.now(ZONE_ID)
        val today = currentTime.toLocalDate()

        val pickTimes = pickTimeRepository.findAllStartTimes()

        if (pickTimes.isEmpty()) {
            throw DojoException.of(DojoExceptionType.ACTIVE_PICK_TIME_NOT_FOUND)
        }

        val nextPickTime =
            pickTimes
                .map { today.atTime(it) }
                .firstOrNull { it.isAfter(currentTime) }

        // 다음 투표 시간이 오늘 안에 있다면 반환, 아니면 내일 첫 투표 시간 반환
        return nextPickTime ?: today.plusDays(1).atTime(pickTimes.first())
    }

    companion object {
        private val ZONE_ID = ZoneId.of("Asia/Seoul")

        val DEFAULT_PICK =
            Pick(
                id = PickId("pickmepickme"),
                questionId = QuestionId("question"),
                questionSetId = QuestionSetId("questionSetId"),
                questionSheetId = QuestionSheetId("questionSheetId"),
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

        val SAMPLE_PICK_LIST: List<Pick>
            get() = listOf(this.SAMPLE_PICK, SAMPLE_PICK, SAMPLE_PICK, SAMPLE_PICK, SAMPLE_PICK, SAMPLE_PICK, SAMPLE_PICK, SAMPLE_PICK, SAMPLE_PICK, SAMPLE_PICK)

        private val SAMPLE_PICK =
            Pick(
                id = PickId("SAMPLE_PICK_ID"),
                questionId = QuestionId("SAMPLE_QUESTION_ID"),
                questionSetId = QuestionSetId("questionSetId"),
                questionSheetId = QuestionSheetId("questionSheetId"),
                pickerId = MemberId("SAMPLE_MEMBER_ID"),
                pickedId = MemberId("SAMPLE_MEMBER_ID"),
                isGenderOpen = true,
                isPlatformOpen = true,
                isMidInitialNameOpen = true,
                isFullNameOpen = true,
                createdAt = LocalDateTime.MIN,
                updatedAt = LocalDateTime.now()
            )
    }
}

private fun Pick.toEntity(): PickEntity {
    return PickEntity(
        id = id.value,
        questionId = questionId.value,
        questionSetId = questionSetId.value,
        questionSheetId = questionSheetId.value,
        pickerId = pickerId.value,
        pickedId = pickedId.value,
        isGenderOpen = isGenderOpen,
        isPlatformOpen = isPlatformOpen,
        isMidInitialNameOpen = isMidInitialNameOpen,
        isFullNameOpen = isFullNameOpen
    )
}

private fun PickEntity.toPick(): Pick {
    return Pick(
        id = PickId(id),
        questionId = QuestionId(questionId),
        questionSetId = QuestionSetId(questionSetId),
        questionSheetId = QuestionSheetId(questionSheetId),
        pickerId = MemberId(pickerId),
        pickedId = MemberId(pickedId),
        isGenderOpen = isGenderOpen,
        isPlatformOpen = isPlatformOpen,
        isMidInitialNameOpen = isMidInitialNameOpen,
        isFullNameOpen = isFullNameOpen,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
