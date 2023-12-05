package com.assessment.code.bugtracker.service.ticket.impl;

import com.assessment.code.bugtracker.data.ticket.Ticket;
import com.assessment.code.bugtracker.data.ticket.TicketDao;
import com.assessment.code.bugtracker.data.user.User;
import com.assessment.code.bugtracker.data.user.UserDao;
import com.assessment.code.bugtracker.exception.BusinessValidationException;
import com.assessment.code.bugtracker.exception.TicketNotFoundException;
import com.assessment.code.bugtracker.service.ticket.TicketService;
import com.assessment.code.bugtracker.service.ticket.dto.CreateTicketRequestDto;
import com.assessment.code.bugtracker.service.ticket.dto.TicketResponseDto;
import com.assessment.code.bugtracker.service.ticket.dto.UpdateTicketRequestDto;
import com.assessment.code.bugtracker.service.ticket.mapper.TicketServiceMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

	private final TicketServiceMapper mapper;

	private final TicketDao ticketDao;

	private final UserDao userDao;

	@Override
	public void delete(Long ticketId) throws TicketNotFoundException {
		Ticket ticket = ticketDao.findById(ticketId).orElseThrow(() -> new TicketNotFoundException("no ticket for id: " + ticketId));
		ticketDao.delete(ticket);
	}

	@Override
	public Page<TicketResponseDto> findByAssignee(Long userId, Pageable pageable) {
		return mapper.toTicketResponse(ticketDao.findAllByAssignee(userId, pageable));
	}

	@Override
	public Page<TicketResponseDto> findAll(Pageable pageable) {
		Page<Ticket> tickets = ticketDao.findAll(pageable);
		return mapper.toTicketResponse(tickets);
	}

	@Override
	public TicketResponseDto save(CreateTicketRequestDto dto) throws BusinessValidationException {
		User reporter = getUser(dto.getReporter());
		User assignee = getUser(dto.getAssignee());
		Ticket ticket = mapper.toTicket(dto);
		ticket.setAssignee(assignee);
		ticket.setReporter(reporter);
		return mapper.toTicketResponse(ticketDao.save(ticket));
	}

	@Override
	public void update(UpdateTicketRequestDto request) throws TicketNotFoundException, BusinessValidationException {
		Ticket ticket = ticketDao.findById(request.getId()).orElseThrow(() -> new TicketNotFoundException("no ticket for id: " + request.getId()));
		mapper.merge(ticket, request);
		if (request.getAssignee() == null) {
			ticket.setAssignee(null);
		} else if (!request.getAssignee().equals(ticket.getAssignee().getId())) {
			ticket.setAssignee(getUser(request.getAssignee()));
		}
		ticketDao.save(ticket);
	}


	private User getUser(Long userId) throws BusinessValidationException {
		if (userId == null) {
			return null;
		}
		return userDao.findById(userId).orElseThrow(() -> new BusinessValidationException("user not found, userId: " + userId));
	}

}
