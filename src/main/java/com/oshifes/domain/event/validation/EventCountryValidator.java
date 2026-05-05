package com.oshifes.domain.event.validation;

import com.oshifes.domain.event.type.EventCountry;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EventCountryValidator implements ConstraintValidator<ValidEventCountry, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || EventCountry.contains(value);
    }
}
