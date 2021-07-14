package com.studyolle.modules.event;

import com.studyolle.modules.study.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long> {

    // 모임을 클릭할때마다 N+1 문제발생
    // 발생원인: Event.java > numberOfRemainSpots 실행할때마다 N+1 문제발생
    // 해결방안: event를 조회할때 enrollments를 함께 조회한다
    @EntityGraph(value = "Event.withEnrollments", type = EntityGraph.EntityGraphType.LOAD)
    List<Event> findByStudyOrderByStartDateTime(Study study);
}
