package com.studyolle.modules.study;

import com.studyolle.modules.account.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long>, StudyRepositoryExtension {

    boolean existsByPath(String path);

    //    @EntityGraph(value = "Study.withAll", type = EntityGraph.EntityGraphType.LOAD) 
    // 아래와 같이 변경 가능
    @EntityGraph(attributePaths = {"tags", "zones", "managers", "members"}, type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

    //    @EntityGraph(value = "Study.withTagsAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    @EntityGraph(attributePaths = {"tags", "managers"})
    Study findStudyWithTagsByPath(String path);

    //    @EntityGraph(value = "Study.withZonesAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    @EntityGraph(attributePaths = {"zones", "managers"})
    Study findStudyWithZonesByPath(String path);

    //    @EntityGraph(value = "Study.withManagers", type = EntityGraph.EntityGraphType.FETCH)
    @EntityGraph(attributePaths = "managers")
    Study findStudyWithManagersByPath(String path);

    //    @EntityGraph(value = "Study.withMembers", type = EntityGraph.EntityGraphType.FETCH)
    @EntityGraph(attributePaths = "members")
    Study findStudyWithMembersByPath(String path);

    Study findStudyOnlyByPath(String path);

    //    @EntityGraph(value = "Study.withTagsAndZones", type = EntityGraph.EntityGraphType.FETCH)
    @EntityGraph(attributePaths = {"zones", "tags"})
    Study findStudyWithTagsAndZonesById(Long id);

    @EntityGraph(attributePaths = {"members", "managers"})
    Study findStudyWithManagersAndMembersById(Long id);

    // 최근 9개의 스터디를 조회해서 화면에 보여줄 것. (공개했고, 아직 종료하지 않은 스터디 중에서)
    @EntityGraph(attributePaths = {"zones", "tags"})
    List<Study> findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(boolean published, boolean closed);

    List<Study> findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);

    List<Study> findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);
}
