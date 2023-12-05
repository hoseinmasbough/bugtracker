package com.assessment.code.bugtracker.api.ticket.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.assessment.code.bugtracker.api.ticket.model.TicketListResponseModel;
import com.assessment.code.bugtracker.api.ticket.model.TicketResponseModel;
import com.assessment.code.bugtracker.api.ticket.model.CreateTicketRequestModel;
import com.assessment.code.bugtracker.api.ticket.model.PriorityType;
import com.assessment.code.bugtracker.api.ticket.model.StatusType;
import com.assessment.code.bugtracker.api.ticket.model.UpdateTicketRequestModel;
import com.assessment.code.bugtracker.service.ticket.dto.TicketResponseDto;
import com.assessment.code.bugtracker.service.ticket.dto.CreateTicketRequestDto;
import com.assessment.code.bugtracker.service.ticket.dto.UpdateTicketRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TicketResourceMapper {

	@Mapping(source = "status", target = "status", qualifiedByName = "toStatus")
	@Mapping(source = "priorityType", target = "priorityType", qualifiedByName = "toPriority")
	TicketResponseModel toBugResponse(TicketResponseDto dto);

	default TicketListResponseModel toTicketListResponseModel(Page<TicketResponseDto> tickets) {
		TicketListResponseModel response = new TicketListResponseModel();
		response.setTickets(CollectionUtils.isEmpty(tickets.getContent()) ? List.of() : tickets.getContent().stream().map(this::toBugResponse)
				.toList());
		response.setTotalElements(tickets.getTotalElements());
		response.setTotalPages(tickets.getTotalPages());
		return response;
	}

	@Mapping(source = "status", target = "status", qualifiedByName = "statusToInt")
	@Mapping(source = "priorityType", target = "priorityType", qualifiedByName = "priorityToInt")
	CreateTicketRequestDto toCreateTicketDto(CreateTicketRequestModel request);

	@Mapping(source = "ticketId", target = "id")
	@Mapping(source = "request.status", target = "status", qualifiedByName = "statusToInt")
	@Mapping(source = "request.priorityType", target = "priorityType", qualifiedByName = "priorityToInt")
	UpdateTicketRequestDto toUpdateTicketDto(UpdateTicketRequestModel request, Long ticketId);

	@Named("statusToInt")
	default Integer statusToInt(StatusType value) {
		return value == null ? null : value.getValue();
	}

	@Named("toStatus")
	default StatusType toStatus(Integer value) {
		return StatusType.fromValue(value);
	}

	@Named("priorityToInt")
	default Integer priorityToInt(PriorityType value) {
		return value == null ? null : value.getValue();
	}

	@Named("toPriority")
	default PriorityType toPriority(Integer value) {
		return PriorityType.fromValue(value);
	}
}
