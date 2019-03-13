package org.mockserver.model;


import Body.Type.PARAMETERS;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class ParameterBodyTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        ParameterBody parameterBody = new ParameterBody(new Parameter("some", "value"));
        // then
        MatcherAssert.assertThat(parameterBody.getValue().getEntries(), Matchers.containsInAnyOrder(new Parameter("some", "value")));
        MatcherAssert.assertThat(parameterBody.getType(), Is.is(PARAMETERS));
    }

    @Test
    public void shouldReturnValuesSetInConstructorWithList() {
        // when
        ParameterBody parameterBody = new ParameterBody(Arrays.asList(new Parameter("some", "value")));
        // then
        MatcherAssert.assertThat(parameterBody.getValue().getEntries(), Matchers.containsInAnyOrder(new Parameter("some", "value")));
        MatcherAssert.assertThat(parameterBody.getType(), Is.is(PARAMETERS));
    }

    @Test
    public void shouldReturnValueSetInStaticConstructor() {
        // when
        ParameterBody parameterBody = ParameterBody.params(new Parameter("some", "value"));
        // then
        MatcherAssert.assertThat(parameterBody.getValue().getEntries(), Matchers.containsInAnyOrder(new Parameter("some", "value")));
        MatcherAssert.assertThat(parameterBody.getType(), Is.is(PARAMETERS));
    }

    @Test
    public void shouldReturnValueSetInStaticConstructorWithList() {
        // when
        ParameterBody parameterBody = ParameterBody.params(Arrays.asList(new Parameter("some", "value")));
        // then
        MatcherAssert.assertThat(parameterBody.getValue().getEntries(), Matchers.containsInAnyOrder(new Parameter("some", "value")));
        MatcherAssert.assertThat(parameterBody.getType(), Is.is(PARAMETERS));
    }
}

