package com.rzqfy.moneymanager.service.impl;

import com.rzqfy.moneymanager.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Service
public class ValidationServiceImpl implements ValidationService {

    @Autowired
    private Validator validator;

    @Override
    public void validate(Object request) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(request);
        if (constraintViolations.size()>0){
            throw new ConstraintViolationException(constraintViolations);
        }
    }
}
