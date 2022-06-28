package hu.progmasters.servicebooker.controller.matcher;

import hu.progmasters.servicebooker.dto.error.ValidationError;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class ValidationErrorMatcher extends TypeSafeMatcher<ValidationError> {
    private final String field;
    private final Matcher<String> errorMessageMatcher;

    public ValidationErrorMatcher(String field, Matcher<String> errorMessageMatcher) {
        this.field = field;
        this.errorMessageMatcher = errorMessageMatcher;
    }

    public static ValidationErrorMatcher validationError(String field, Matcher<String> errorMessageMatcher) {
        return new ValidationErrorMatcher(field, errorMessageMatcher);
    }

    @Override
    protected boolean matchesSafely(ValidationError validationError) {
        return field.equals(validationError.getField())
                && errorMessageMatcher.matches(validationError.getErrorMessage());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("ValidationError on field \"")
                .appendText(field)
                .appendText("\" with message that is ")
                .appendDescriptionOf(errorMessageMatcher);
    }
}
