package com.assessment.code.bugtracker.api.ticket.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class TicketListResponseModel implements Serializable {
	@Serial
	private static final long serialVersionUID = -4518551547624136025L;

	private List<TicketResponseModel> tickets;

	private long totalElements;

	private long totalPages;
}
