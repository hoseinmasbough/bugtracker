package com.assessment.code.bugtracker.exception.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public enum ResultStatus {
	INTERNAL_SERVER_ERROR("Internal server error"),
	RESOURCE_NOT_FOUND("Resource not found"),
	BUSINESS_VALIDATION_ERROR("Business validation error"),
	INVALID_PAYLOAD("Invalid payload"),
	ARGUMENT_TYPE_MISMATCH("Argument type mismatch"),
	INVALID_ARGUMENT("Invalid argument");

	@Getter
	private final String message;
}
