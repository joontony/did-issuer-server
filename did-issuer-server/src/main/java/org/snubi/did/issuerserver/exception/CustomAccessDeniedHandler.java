package org.snubi.did.issuerserver.exception;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.snubi.did.issuerserver.common.CustomResponseEntity;
import org.snubi.did.issuerserver.common.ErrorCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
    		throws IOException, ServletException {
        
        ObjectMapper objectMapper = new ObjectMapper();  
        CustomResponseEntity<Object> customResponseEntity = CustomResponseEntity.builder()
	        .code(ErrorCode.FORBIDDEN.getHttpStatus().toString())
	        .data(null)
	        .message(ErrorCode.FORBIDDEN.getMessage()) 
	        .token("")
	        .build();
        
        response.setStatus(404);
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Type", "applicaiton/json");
        response.getWriter().write(objectMapper.writeValueAsString(customResponseEntity));
    	response.getWriter().flush();
        response.getWriter().close();
    }
}