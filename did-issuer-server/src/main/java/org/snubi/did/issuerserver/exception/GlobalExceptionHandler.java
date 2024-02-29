package org.snubi.did.issuerserver.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.snubi.did.issuerserver.common.CustomResponseEntity;
import org.snubi.did.issuerserver.common.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NoHandlerFoundException.class)
	protected ResponseEntity<?> noHandlerFoundException(NoHandlerFoundException e) {
		log.error("NoHandlerFoundException {}", e.getMessage());
		return CustomResponseEntity.failResponse(ErrorCode.NOT_FOUND);
	}

	@ExceptionHandler({Exception.class,IOException.class,ServletException.class, SocketException.class, RestClientException.class})
	protected ResponseEntity<?> handleException(Exception e) {
		log.error("Exception : ", e);
		return CustomResponseEntity.failResponse(e.getMessage());
	}

	@ExceptionHandler(CustomException.class)
	protected ResponseEntity<?> handleCustomException(CustomException e) {
		log.error("CustomException : {}", e.getMessage());
		return CustomResponseEntity.failResponse(e.getErrorCode());
	}

	@ExceptionHandler(JsonProcessingException.class)
	protected ResponseEntity<?> handleJsonProcessingException(JsonProcessingException e) {
		log.error("JsonProcessingException : {} ", e.getMessage());
		return CustomResponseEntity.failResponse(ErrorCode.CONVERT_TO_JSON_FAIL);
	}
}
