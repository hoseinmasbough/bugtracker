package com.assessment.code.bugtracker.data.ticket;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketDao extends JpaRepository<Ticket, Long> {

	@Query(value = "select * from tickets where assignee_id =:assigneeId", nativeQuery = true)
	Page<Ticket> findAllByAssignee(@Param("assigneeId") Long assigneeId, Pageable pageable);
}
