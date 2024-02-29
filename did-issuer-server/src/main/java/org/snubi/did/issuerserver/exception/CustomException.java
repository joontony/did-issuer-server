package org.snubi.did.issuerserver.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.snubi.did.issuerserver.common.ErrorCode;

@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException {

    private ErrorCode errorCode;

    public CustomException(String message, ErrorCode errorCode) {
        super(message + errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
