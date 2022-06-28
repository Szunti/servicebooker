package hu.progmasters.servicebooker.controller.matcher;

import hu.progmasters.servicebooker.dto.error.SimpleError;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class SimpleErrorMatcher extends TypeSafeMatcher<SimpleError> {
    private final Matcher<String> errorMessageMatcher;

    public SimpleErrorMatcher(Matcher<String> errorMessageMatcher) {
        this.errorMessageMatcher = errorMessageMatcher;
    }

    public static SimpleErrorMatcher simpleError(Matcher<String> errorMessageMatcher) {
        return new SimpleErrorMatcher(errorMessageMatcher);
    }

    @Override
    protected boolean matchesSafely(SimpleError validationError) {
        return errorMessageMatcher.matches(validationError.getErrorMessage());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("SimpleError with message that is ")
                .appendDescriptionOf(errorMessageMatcher);
    }
}
