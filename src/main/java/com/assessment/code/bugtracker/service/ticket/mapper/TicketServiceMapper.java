package com.assessment.code.bugtracker.service.ticket.mapper;

import com.assessment.code.bugtracker.data.ticket.Ticket;
import com.assessment.code.bugtracker.service.ticket.dto.TicketResponseDto;
import com.assessment.code.bugtracker.service.ticket.dto.CreateTicketRequestDto;
import com.assessment.code.bugtracker.service.ticket.dto.UpdateTicketRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TicketServiceMapper {

	@Mapping(target = "assignee", source = "assignee.id")
	@Mapping(target = "reporter", source = "reporter.id")
	TicketResponseDto toTicketResponse(Ticket ticket);

	default Page<TicketResponseDto> toTicketResponse(Page<Ticket> tickets) {
		return tickets == null ? Page.empty() : tickets.map(this::toTicketResponse);
	}

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "assignee", ignore = true)
	@Mapping(target = "reporter", ignore = true)
	Ticket toTicket(CreateTicketRequestDto request);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "assignee", ignore = true)
	@Mapping(target = "reporter", ignore = true)
	void merge(@MappingTarget Ticket ticket, UpdateTicketRequestDto request);

}
