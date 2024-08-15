package com.mashup.dojo.service

import com.mashup.dojo.DojoException
import com.mashup.dojo.DojoExceptionType
import com.mashup.dojo.MemberRelationEntity
import com.mashup.dojo.MemberRelationRepository
import com.mashup.dojo.domain.MemberId
import com.mashup.dojo.domain.MemberRelation
import com.mashup.dojo.domain.MemberRelationId
import com.mashup.dojo.domain.RelationType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface MemberRelationService {
    fun getAllRelationShip(fromId: MemberId): List<MemberId>

    fun getFriendRelationIds(fromId: MemberId): List<MemberId>

    fun getAccompanyRelationIds(fromId: MemberId): List<MemberId>

    fun createRelation(
        fromId: MemberId,
        toId: MemberId,
    )

    fun bulkCreateRelation(
        fromId: MemberId,
        theOtherMemberIds: List<MemberId>,
    ): List<MemberRelationId>

    fun updateRelationToFriend(
        fromId: MemberId,
        toId: MemberId,
    ): MemberRelationId

    fun isFriend(
        fromId: MemberId,
        toId: MemberId,
    ): Boolean

    fun findCandidateOfFriend(memberId: MemberId): List<MemberId>

    fun findCandidateOfAccompany(memberId: MemberId): List<MemberId>
}

@Service
@Transactional(readOnly = true)
class DefaultMemberRelationService(
    private val memberRelationRepository: MemberRelationRepository,
    @Value("\${dojo.candidate.size}")
    private val defaultCandidateSize: Long,
) : MemberRelationService {
    override fun getAllRelationShip(fromId: MemberId): List<MemberId> {
        return memberRelationRepository.findByFromId(fromId.value).map { MemberId(it) }
    }

    override fun getFriendRelationIds(fromId: MemberId): List<MemberId> {
        return memberRelationRepository.findFriendsByFromId(fromId.value).map { MemberId(it) }
    }

    override fun getAccompanyRelationIds(fromId: MemberId): List<MemberId> {
        return memberRelationRepository.findAccompanyByFromId(fromId.value).map { MemberId(it) }
    }

    @Transactional
    override fun createRelation(
        fromId: MemberId,
        toId: MemberId,
    ) {
        val memberRelation = MemberRelation.createAccompanyRelation(fromId, toId)
        memberRelationRepository.save(memberRelation.toEntity())
    }

    @Transactional
    override fun bulkCreateRelation(
        fromId: MemberId,
        theOtherMemberIds: List<MemberId>,
    ): List<MemberRelationId> {
        val memberRelationEntityList =
            theOtherMemberIds.map {
                MemberRelation.createAccompanyRelation(fromId, it).toEntity()
            }

        val entities = memberRelationRepository.saveAll(memberRelationEntityList)
        return entities.map { MemberRelationId(it.id) }
    }

    @Transactional
    override fun updateRelationToFriend(
        fromId: MemberId,
        toId: MemberId,
    ): MemberRelationId {
        val toDomain = memberRelationRepository.findByFromIdAndToId(fromId.value, toId.value)?.toDomain() ?: throw DojoException.of(DojoExceptionType.FRIEND_NOT_FOUND)
        if (toDomain.relation == RelationType.FRIEND) {
            throw DojoException.of(DojoExceptionType.ALREADY_FRIEND)
        }

        val updatedRelation = toDomain.updateToFriend()
        return MemberRelationId(memberRelationRepository.save(updatedRelation.toEntity()).id)
    }

    override fun isFriend(
        fromId: MemberId,
        toId: MemberId,
    ): Boolean {
        return memberRelationRepository.isFriend(fromId = fromId.value, toId = toId.value)
    }

    override fun findCandidateOfFriend(memberId: MemberId): List<MemberId> {
        return memberRelationRepository.findRandomOfFriend(
            memberId = memberId.value,
            limit = defaultCandidateSize
        ).map {
            MemberId(it)
        }
    }

    override fun findCandidateOfAccompany(memberId: MemberId): List<MemberId> {
        return memberRelationRepository.findRandomOfAccompany(
            memberId = memberId.value,
            limit = defaultCandidateSize
        ).map {
            MemberId(it)
        }
    }
}

private fun MemberRelation.toEntity(): MemberRelationEntity {
    return MemberRelationEntity(
        id = id.value,
        fromId = fromId.value,
        toId = toId.value,
        relationType = com.mashup.dojo.RelationType.valueOf(relation.name)
    )
}

private fun MemberRelationEntity.toDomain(): MemberRelation {
    return MemberRelation(
        id = MemberRelationId(id),
        fromId = MemberId(fromId),
        toId = MemberId(toId),
        relation = com.mashup.dojo.domain.RelationType.valueOf(relationType.name),
        lastUpdatedAt = updatedAt
    )
}
