package com.assessment.code.bugtracker.api.ticket.model;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PriorityType {

	BLOCKER(0), CRITICAL(1), MINOR(2), MAJOR(3), LOW(4), MEDIUM(5);

	private final int value;

	@JsonCreator
	public static PriorityType fromValue(Integer value) {
		return Stream.of(PriorityType.values()).filter(origin -> origin.value == value).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("invalid type found " + value));
	}

	@JsonValue
	public int getValue() {
		return value;
	}
}
