package com.assessment.code.bugtracker.api.ticket.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class CreateTicketRequestModel implements Serializable {

	@Serial
	private static final long serialVersionUID = 9122457842103554034L;

	@NotBlank(message = "empty.ticket.name")
	private String name;

	private List<String> affectsVersions;

	private List<String> fixVersions;

	private List<String> sprint;

	private String description;

	@NotNull(message = "empty.status.type")
	private StatusType status;

	@NotNull(message = "empty.priority.type")
	private PriorityType priorityType;

	@NotNull(message = "empty.reporter.id")
	private Long reporter;

	private Long assignee;

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][.SS][.S]")
	private LocalDateTime resolved;
}
