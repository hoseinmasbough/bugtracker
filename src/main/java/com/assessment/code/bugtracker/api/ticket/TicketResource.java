package com.assessment.code.bugtracker.api.ticket;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.assessment.code.bugtracker.api.ticket.mapper.TicketResourceMapper;
import com.assessment.code.bugtracker.api.ticket.model.TicketListResponseModel;
import com.assessment.code.bugtracker.api.ticket.model.TicketResponseModel;
import com.assessment.code.bugtracker.api.ticket.model.CreateTicketRequestModel;
import com.assessment.code.bugtracker.api.ticket.model.UpdateTicketRequestModel;
import com.assessment.code.bugtracker.exception.TicketNotFoundException;
import com.assessment.code.bugtracker.exception.BusinessValidationException;
import com.assessment.code.bugtracker.service.ticket.TicketService;
import com.assessment.code.bugtracker.service.ticket.dto.TicketResponseDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tickets")
public class TicketResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(TicketResource.class);

	private final TicketService service;

	private final TicketResourceMapper mapper;

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TicketResponseModel> createTicket(@Valid @RequestBody CreateTicketRequestModel request) throws BusinessValidationException {
		LOGGER.info("gonna create a new bug - request: {}", request);
		TicketResponseDto bug = service.save(mapper.toCreateTicketDto(request));
		return new ResponseEntity<>(mapper.toBugResponse(bug), HttpStatus.CREATED);
	}

	@GetMapping(path = "/assignee/{assigneeId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TicketListResponseModel> getAllTicketByAssignee(@NotNull @PathVariable Long assigneeId,
			@RequestParam(name = "page", defaultValue = "0") final Integer page,
			@RequestParam(name = "size", defaultValue = "10") final Integer size) {
		LOGGER.info("gonna get all tickets of user : {}", assigneeId);
		Page<TicketResponseDto> tickets = service.findByAssignee(assigneeId, PageRequest.of(page, size));
		return ResponseEntity.ok(mapper.toTicketListResponseModel(tickets));
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TicketListResponseModel> getAllTicket(@RequestParam(name = "page", defaultValue = "0") final Integer page,
			@RequestParam(name = "size", defaultValue = "10") final Integer size) {
		LOGGER.info("gonna get all tickets");
		Page<TicketResponseDto> tickets = service.findAll(PageRequest.of(page, size));
		return ResponseEntity.ok(mapper.toTicketListResponseModel(tickets));
	}

	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE,
			path = "/{ticketId}")
	public ResponseEntity<Void> updateTicket(@PathVariable(name = "ticketId") Long ticketId, @RequestBody @Valid UpdateTicketRequestModel request)
			throws TicketNotFoundException, BusinessValidationException {
		LOGGER.info("update bug request received for bug {}, {}", ticketId, request);
		service.update(mapper.toUpdateTicketDto(request, ticketId));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping(path = "/{ticketId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteTicket(@PathVariable(name = "ticketId") Long ticketId) throws TicketNotFoundException {
		LOGGER.info("delete bug request {}", ticketId);
		service.delete(ticketId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//put assignToUser

	//put changeState
}
