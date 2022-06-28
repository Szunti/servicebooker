package hu.progmasters.servicebooker.validation;

import hu.progmasters.servicebooker.dto.CommandWithStartAndEnd;
import hu.progmasters.servicebooker.dto.specificperiod.SpecificPeriodCreateCommand;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class StartBeforeEndValidator
        implements ConstraintValidator<StartBeforeEnd, CommandWithStartAndEnd> {
    @Override
    public boolean isValid(CommandWithStartAndEnd command,
                           ConstraintValidatorContext constraintValidatorContext) {

        LocalDateTime start = command.getStart();
        LocalDateTime end = command.getEnd();
        if (start == null || end == null) {
            return true;
        }
        boolean valid = start.isBefore(end);
        if (!valid) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                            constraintValidatorContext.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("start")
                    .addConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                            constraintValidatorContext.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("end")
                    .addConstraintViolation();
        }
        return valid;
    }
}

