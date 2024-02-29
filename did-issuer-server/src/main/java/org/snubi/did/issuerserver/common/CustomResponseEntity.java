package org.snubi.did.issuerserver.common;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
@Data
@Builder
public class CustomResponseEntity<T> {	
	
    private String code		;
    private T data		    ;
    private String message	;
    private String token 	;
    
    public static ResponseEntity<?> failResponse(ErrorCode e){    	
    	HttpHeaders headers = new HttpHeaders();    	
        return ResponseEntity        		
                .status(e.getHttpStatus())
                .headers(headers)
                .body(CustomResponseEntity.builder()
                        .code(e.getHttpStatus().value() + ":" + e.name()) 	
                        .data(null)
                        .message(e.getMessage()) 							
                        .token("")
                        .build()
                );
    }    
    public static ResponseEntity<?> failResponse(String e){    	
    	HttpHeaders headers = new HttpHeaders();      	
    	log.error("failResponse ------------- error :  {}", e);
        return ResponseEntity        		
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .headers(headers)
                .body(CustomResponseEntity.builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.name()) 	
                        .data(null)
                        .message(e) 							
                        .token("")
                        .build()
                );
    }      
	public static <T> ResponseEntity<T> succResponse(T data, String token){    	
    	HttpHeaders headers = new HttpHeaders();   
    	@SuppressWarnings("unchecked")
		ResponseEntity<T> result = (ResponseEntity<T>) ResponseEntity        		
                .status(HttpStatus.OK)
                .headers(headers)
                .body(CustomResponseEntity.builder()
                        .code("200")
                        .data(data)
                        .message("SUCC") 
                        .token(token)
                        .build()
                );    	
        return result;
    }

}














//https://velog.io/@sago_mungcci/SpringBoot-ResponseEntity-ResponseStatus-Custom
//https://velog.io/@dot2__/SpringBoot-Custom-Exception-Response-%EB%A7%8C%EB%93%A4%EA%B8%B0
//https://velog.io/@qotndus43/%EC%8A%A4%ED%94%84%EB%A7%81-API-%EA%B3%B5%ED%86%B5-%EC%9D%91%EB%8B%B5-%ED%8F%AC%EB%A7%B7-%EA%B0%9C%EB%B0%9C%ED%95%98%EA%B8%B0


