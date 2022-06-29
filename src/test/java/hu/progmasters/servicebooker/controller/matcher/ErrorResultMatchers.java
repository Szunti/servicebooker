package hu.progmasters.servicebooker.controller.matcher;

import hu.progmasters.servicebooker.dto.error.SimpleError;
import hu.progmasters.servicebooker.dto.error.ValidationError;
import org.hamcrest.Matcher;
import org.springframework.test.web.servlet.ResultMatcher;

import static hu.progmasters.servicebooker.controller.matcher.SimpleErrorMatcher.simpleError;
import static hu.progmasters.servicebooker.controller.matcher.ValidationErrorMatcher.validationError;

public class ErrorResultMatchers {

    @SafeVarargs
    public static ResultMatcher validationErrors(Matcher<? super ValidationError>... matchers) {
        return new JsonListResultMatcher<>(ValidationError.class, matchers);
    }

    public static ResultMatcher singleValidationError(String field, Matcher<String> errorMessageMatcher) {
        Matcher<ValidationError> matcher = validationError(field, errorMessageMatcher);
        return new SingleElementResultMatcher<>(ValidationError.class, matcher);
    }

    @SafeVarargs
    public static ResultMatcher simpleErrors(Matcher<? super SimpleError>... matchers) {
        return new JsonListResultMatcher<>(SimpleError.class, matchers);
    }

    public static ResultMatcher singleError(Matcher<String> errorMessageMatcher) {
        Matcher<SimpleError> matcher = simpleError(errorMessageMatcher);
        return new SingleElementResultMatcher<>(SimpleError.class, matcher);
    }
}

