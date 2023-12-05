package com.assessment.code.bugtracker.api.ticket.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class TicketResponseModel implements Serializable {

	@Serial
	private static final long serialVersionUID = -5298721827183745150L;

	private Long id;

	private String name;

	private List<String> affectsVersions;

	private List<String> fixVersions;

	private List<String> sprint;

	private String description;

	private StatusType status;

	private PriorityType priorityType;

	private Long reporter;

	private Long assignee;

	private LocalDateTime created;

	private LocalDateTime resolved;
}
