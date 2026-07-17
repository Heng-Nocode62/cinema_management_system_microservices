package com.heng.cms.movieservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ProblemDetail handConflict(DataIntegrityViolationException ex, HttpServletRequest request) {
		log.warn("Data integrity vilolet. path: {}", request.getRequestURI());
		return problem(
				HttpStatus.CONFLICT,
				"Conflict",
				"Duplicate or invalid data",
				request.getRequestURI()
		);
	}


	@ExceptionHandler(BadRequestException.class)
	public ProblemDetail handleBadRequest(BadRequestException ex, HttpServletRequest request) {
		log.warn("Bad request. path: {}", request.getRequestURI());
		return problem(HttpStatus.BAD_REQUEST,
				"Bad Request",
				ex.getMessage(),
				request.getRequestURI());

	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail handleValidaton(MethodArgumentNotValidException ex, HttpServletRequest request) {
		log.warn("Validation error. path: {}", request.getRequestURI());

		ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Validation error", "Validation failed", request.getRequestURI());

		List<String> errors = new ArrayList<>();
		ex.getBindingResult().getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
		pd.setProperty("errors", errors);

		return pd;
	}

	@ExceptionHandler(IllegalStateException.class)
	public ProblemDetail handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
		log.warn("Illegal . path: {}", request.getRequestURI());
		log.warn("message {}", ex.getMessage());
		return problem(HttpStatus.BAD_REQUEST, "Action not permitted", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ProblemDetail handleFindingResource(ResourceNotFoundException ex, HttpServletRequest request) {
		log.warn("Not found. path: {}", request.getRequestURI());
		return problem(HttpStatus.NOT_FOUND, "Resoure not found", ex.getMessage(), request.getRequestURI());
	}


	@ExceptionHandler(Exception.class)
	public ProblemDetail handleGeneric(Exception exp, HttpServletRequest request) {
		log.warn("Unhandle error. path: {}", request.getRequestURL());
		exp.printStackTrace();
		return problem(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"Internal Error",
				"Unexpected error occur",
				request.getRequestURI()
				);
	}

	private ProblemDetail problem(HttpStatus status, String title, String detail, String path) {
		ProblemDetail pd = ProblemDetail.forStatus(status);
		pd.setTitle(title);
		pd.setDetail(detail);
		pd.setProperty("path", path);
		pd.setProperty("timestamp", LocalDateTime.now());
		return pd;
	}
}
