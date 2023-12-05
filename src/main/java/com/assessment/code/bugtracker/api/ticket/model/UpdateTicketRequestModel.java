package com.assessment.code.bugtracker.api.ticket.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class UpdateTicketRequestModel {

//	@NotBlank
	private String name;

	private List<String> affectsVersions;

	private List<String> fixVersions;

	private List<String> sprint;

	private String description;

//	@NotNull(message = "empty.created.date")
	private StatusType status;

//	@NotNull(message = "empty.created.date")
	private PriorityType priorityType;

	private String assignee;

//	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime resolved;
}
