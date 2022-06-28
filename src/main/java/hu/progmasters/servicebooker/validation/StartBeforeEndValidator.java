package hu.progmasters.servicebooker.validation;

import hu.progmasters.servicebooker.dto.specificperiod.SpecificPeriodCreateCommand;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, SpecificPeriodCreateCommand> {
    @Override
    public boolean isValid(SpecificPeriodCreateCommand specificPeriodCreateCommand,
                           ConstraintValidatorContext constraintValidatorContext) {

        LocalDateTime start = specificPeriodCreateCommand.getStart();
        LocalDateTime end = specificPeriodCreateCommand.getEnd();
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

