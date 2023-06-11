package com.rzqfy.moneymanager.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class CustomException extends RuntimeException{
    private final HttpStatus status;
    private final String field;
//    private final List<String> messages;
    private final String message;
}
