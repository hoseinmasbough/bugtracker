package com.assessment.code.bugtracker.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.assessment.code.bugtracker.exception.model.ErrorMessageListResponse;
import com.assessment.code.bugtracker.exception.model.ResultStatus;
import com.fasterxml.jackson.core.JsonParseException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalHttpExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalHttpExceptionHandler.class);

	private static final String JSON_PARSE_EXCEPTION = "JsonParseException:";

	private Environment environment;

	@Autowired
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@ExceptionHandler(TicketNotFoundException.class)
	ResponseEntity<Object> resourceNotFoundHandler(TicketNotFoundException ex) {
		List<String> details = new ArrayList<>();
		details.add(ex.getLocalizedMessage());
		ErrorMessageListResponse error = new ErrorMessageListResponse(ResultStatus.RESOURCE_NOT_FOUND.getMessage(), details);
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BusinessValidationException.class)
	public ResponseEntity<Object> handleBusinessValidation(BusinessValidationException ex) {
		List<String> details = new ArrayList<>();
		details.add(ex.getLocalizedMessage());
		ErrorMessageListResponse error = new ErrorMessageListResponse(ResultStatus.BUSINESS_VALIDATION_ERROR.getMessage(), details);
		return new ResponseEntity<>(error, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleValidationExceptions(
			MethodArgumentNotValidException ex) {
		LOGGER.error("validation exception {0}", ex);
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.BAD_REQUEST.value());

		List<String> errors = ex.getBindingResult()
				.getAllErrors()
				.stream()
				.filter(fieldError -> StringUtils.hasText(fieldError.getDefaultMessage()))
				.map(fieldError -> {
					String customMessage = environment.getProperty(fieldError.getDefaultMessage());
					if (StringUtils.hasText(customMessage)) {
						return customMessage;
					} else {
						return fieldError.getDefaultMessage();
					}
				})
				.toList();

		body.put("errors", errors);

		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public final ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
		List<String> details = new ArrayList<>();
		details.add(ex.getLocalizedMessage());
		ErrorMessageListResponse error = new ErrorMessageListResponse(ResultStatus.INVALID_ARGUMENT.getMessage(), details);
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public final ResponseEntity<Object> handleMethodArgumentTypeMismatchException(
			MethodArgumentTypeMismatchException ex) {
		List<String> details = new ArrayList<>();
		details.add(Objects.requireNonNull(ex.getRootCause()).getMessage());
		ErrorMessageListResponse error = new ErrorMessageListResponse(ResultStatus.ARGUMENT_TYPE_MISMATCH.getMessage(), details);
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public final ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
		List<String> details = new ArrayList<>();
		String rootCause = ex.getLocalizedMessage();
		if (ex.getCause() instanceof JsonParseException) {
			details.add(rootCause.split(JSON_PARSE_EXCEPTION)[1]);
		} else {
			details.add(ex.getLocalizedMessage());
		}
		ErrorMessageListResponse error = new ErrorMessageListResponse(ResultStatus.INVALID_PAYLOAD.getMessage(), details);
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> handleAllExceptions(Exception ex) {
		List<String> details = new ArrayList<>();
		details.add(ex.getLocalizedMessage());
		ErrorMessageListResponse error = new ErrorMessageListResponse(ResultStatus.INTERNAL_SERVER_ERROR.getMessage(), details);
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}