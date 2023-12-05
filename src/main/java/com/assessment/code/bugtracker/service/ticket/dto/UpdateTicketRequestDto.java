package com.assessment.code.bugtracker.service.ticket.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdateTicketRequestDto {

	private Long id;

	private String name;

	private List<String> affectsVersions;

	private List<String> fixVersions;

	private List<String> sprint;

	private String description;

	private Integer status;

	private Integer priorityType;

	private Long assignee;

	private LocalDateTime resolved;
}
