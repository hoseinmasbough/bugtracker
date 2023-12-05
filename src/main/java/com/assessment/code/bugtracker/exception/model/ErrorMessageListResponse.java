package com.assessment.code.bugtracker.exception.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ErrorMessageListResponse {
	private String message;
	private List<String> details;
}
