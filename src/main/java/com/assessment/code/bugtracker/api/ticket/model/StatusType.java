package com.assessment.code.bugtracker.api.ticket.model;

import java.util.Objects;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum StatusType {

	OPEN(0), TODO(1), IN_PROGRESS(2), CODE_REVIEW(3), QA(4), DONE(5);

	private final Integer value;

	@JsonCreator
	public static StatusType fromValue(Integer value) {
		return Stream.of(StatusType.values()).filter(origin -> Objects.equals(origin.value, value)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("invalid status found " + value));
	}

	@JsonValue
	public Integer getValue() {
		return value;
	}
}
