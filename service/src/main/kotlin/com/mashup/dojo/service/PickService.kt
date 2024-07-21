package com.mashup.dojo.service

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
        pickOpenItem: PickOpenItem
    ): String
}

@Transactional(readOnly = true)
@Service
class DefaultPickService: PickService {
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
        pickOpenItem: PickOpenItem
    ): String {
        // todo : 로직 구현
        // Pick 조회
        // - pickId에 해당하는 Pick이 존재하지 않을 때 : 예외 반환
        // Pick 유효성 검사
        // - 전달된 pickedId와 조회된 Pick.pickedId 값이 같지 않을 때 : 예외 반환
        // Pick Open 처리
        // - 오픈하지 않은 정보일 때 : 해당 정보 오픈 처리, 해당 정보 반환  
        // - 이미 오픈한 정보일 때 : 예외 반환 
        
        return "MOCK_PICK_INFO_VALUE"
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
