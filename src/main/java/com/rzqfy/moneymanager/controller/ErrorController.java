package com.rzqfy.moneymanager.controller;

import com.rzqfy.moneymanager.exception.CustomException;
import com.rzqfy.moneymanager.model.WebResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<WebResponse<Map<String, List<String>>>> constraintViolationException(ConstraintViolationException e){
        Map<String, List<String>> err = new HashMap<>();
        for (ConstraintViolation ex : e.getConstraintViolations()){
            String field = convertToSnakeCase(ex.getPropertyPath().toString());
            String message = ex.getMessage();
            if(err.containsKey(field)){
                List<String> messages = err.get(field);
                messages.add(message);
            } else{
                List<String> messages = new ArrayList<>();
                messages.add(message);
                err.put(field, messages);
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(WebResponse.<Map<String, List<String>>>builder()
                        .errors(err)
                        .build());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<WebResponse<String>>responseStatusException(ResponseStatusException e){
        return ResponseEntity.status(e.getRawStatusCode())
                .body(WebResponse.<String>builder()
                        .errors(e.getReason())
                        .build());
    }

    @ExceptionHandler(CustomException.class)
//    public ResponseEntity<WebResponse<Map<String, List<String>>>>customException(CustomException e){
//        Map<String, List<String>> err = new HashMap<>();
//        err.put(e.getField(), e.getMessages());
//
//        return ResponseEntity.status(e.getStatus())
//                .body(WebResponse.<Map<String, List<String>>>builder()
//                        .errors(err)
//                        .build());
//    }
    public ResponseEntity<WebResponse<Map<String, String>>>customException(CustomException e){
        Map<String, String> err = new HashMap<>();
        err.put(e.getField(), e.getMessage());

        return ResponseEntity.status(e.getStatus())
                .body(WebResponse.<Map<String, String>>builder()
                        .errors(err)
                        .build());
    }

    private String convertToSnakeCase(String field){
        StringBuilder snakeCaseBuilder = new StringBuilder();
        for (int i = 0; i < field.length(); i++) {
            char c = field.charAt(i);
            if (Character.isUpperCase(c)) {
                snakeCaseBuilder.append('_');
                snakeCaseBuilder.append(Character.toLowerCase(c));
            } else {
                snakeCaseBuilder.append(c);
            }
        }
        return snakeCaseBuilder.toString();
    }
}
