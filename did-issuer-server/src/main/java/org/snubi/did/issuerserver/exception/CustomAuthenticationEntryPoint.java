package org.snubi.did.issuerserver.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.snubi.did.issuerserver.common.CustomResponseEntity;
import org.snubi.did.issuerserver.common.ErrorCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(
			HttpServletRequest request, 
			HttpServletResponse response,
			AuthenticationException authException
			) throws IOException, ServletException {
        
        ObjectMapper objectMapper = new ObjectMapper();  
        CustomResponseEntity<Object> customResponseEntity = CustomResponseEntity.builder()
	        .code(ErrorCode.UNAUTHORIZED.getHttpStatus().toString())
	        .data(null)
	        .message(ErrorCode.UNAUTHORIZED.getMessage()) 
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
