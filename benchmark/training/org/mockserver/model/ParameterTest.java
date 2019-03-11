package org.mockserver.model;


import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class ParameterTest {
    @Test
    public void shouldReturnValuesSetInConstructors() {
        // when
        Parameter firstParameter = new Parameter("first", "first_one", "first_two");
        Parameter secondParameter = new Parameter("second", Arrays.asList("second_one", "second_two"));
        // then
        MatcherAssert.assertThat(firstParameter.getValues(), Matchers.containsInAnyOrder(NottableString.string("first_one"), NottableString.string("first_two")));
        MatcherAssert.assertThat(secondParameter.getValues(), Matchers.containsInAnyOrder(NottableString.string("second_one"), NottableString.string("second_two")));
    }

    @Test
    public void shouldReturnValueSetInStaticConstructors() {
        // when
        Parameter firstParameter = Parameter.param("first", "first_one", "first_two");
        Parameter secondParameter = Parameter.param("second", Arrays.asList("second_one", "second_two"));
        // then
        MatcherAssert.assertThat(firstParameter.getValues(), Matchers.containsInAnyOrder(NottableString.string("first_one"), NottableString.string("first_two")));
        MatcherAssert.assertThat(secondParameter.getValues(), Matchers.containsInAnyOrder(NottableString.string("second_one"), NottableString.string("second_two")));
    }
}

