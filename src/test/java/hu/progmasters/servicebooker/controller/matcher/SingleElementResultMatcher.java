package hu.progmasters.servicebooker.controller.matcher;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.hamcrest.Matcher;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class SingleElementResultMatcher<T> implements ResultMatcher {
    private static final JsonMapper mapper = new JsonMapper();

    private final Class<T> elementClass;
    private final Matcher<? super T> matcher;

    public SingleElementResultMatcher(Class<T> elementClass, Matcher<? super T> matcher) {
        this.elementClass = elementClass;
        this.matcher = matcher;
    }

    @Override
    public void match(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        List<T> valueList;
        try (MappingIterator<T> iterator = mapper.readerFor(elementClass).readValues(content)) {
            valueList = iterator.readAll();
        }
        assertThat(valueList, hasSize(1));
        T element = valueList.get(0);
        assertThat(element, matcher);
    }
}
