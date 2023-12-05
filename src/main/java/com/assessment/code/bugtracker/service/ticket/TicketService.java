package com.assessment.code.bugtracker.service.ticket;

import com.assessment.code.bugtracker.exception.TicketNotFoundException;
import com.assessment.code.bugtracker.exception.BusinessValidationException;
import com.assessment.code.bugtracker.service.ticket.dto.TicketResponseDto;
import com.assessment.code.bugtracker.service.ticket.dto.CreateTicketRequestDto;
import com.assessment.code.bugtracker.service.ticket.dto.UpdateTicketRequestDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface TicketService {

	void delete(Long bugId) throws TicketNotFoundException;

	Page<TicketResponseDto> findByAssignee(Long userId, Pageable pageable);

	Page<TicketResponseDto> findAll(Pageable pageable);

	TicketResponseDto save(CreateTicketRequestDto dto) throws BusinessValidationException;

	void update(UpdateTicketRequestDto bug) throws TicketNotFoundException, BusinessValidationException;
}
