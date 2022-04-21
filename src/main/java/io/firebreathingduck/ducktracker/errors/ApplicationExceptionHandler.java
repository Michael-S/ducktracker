package io.firebreathingduck.ducktracker.errors;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ResponseStatus(
            value = HttpStatus.BAD_REQUEST,
            reason = "Form input error")
    @ExceptionHandler(ConversionFailedException.class)
    public void handleException(ConversionFailedException e) {

    }

}
