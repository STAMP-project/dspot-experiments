package org.axonframework.test.aggregate;


import java.util.Collections;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.test.AxonAssertionError;
import org.axonframework.test.matchers.MatchAllFieldFilter;
import org.junit.Test;


public class ResultValidatorImplTest {
    private ResultValidator<?> validator = new ResultValidatorImpl(actualEvents(), new MatchAllFieldFilter(Collections.emptyList()), () -> null, null);

    @Test(expected = AxonAssertionError.class)
    public void shouldCompareValuesForEquality() {
        EventMessage<?> expected = actualEvents().iterator().next().andMetaData(Collections.singletonMap("key1", "otherValue"));
        validator.expectEvents(expected);
    }

    @Test(expected = AxonAssertionError.class)
    public void shouldCompareKeysForEquality() {
        EventMessage<?> expected = actualEvents().iterator().next().andMetaData(Collections.singletonMap("KEY1", "value1"));
        validator.expectEvents(expected);
    }

    @Test
    public void shouldSuccesfullyCompareEqualMetadata() {
        EventMessage<?> expected = actualEvents().iterator().next().andMetaData(Collections.singletonMap("key1", "value1"));
        validator.expectEvents(expected);
    }
}

