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
public class HeaderTest {
    @Test
    public void shouldReturnValuesSetInConstructors() {
        // when
        Header firstHeader = new Header("first", "first_one", "first_two");
        Header secondHeader = new Header("second", Arrays.asList("second_one", "second_two"));
        // then
        MatcherAssert.assertThat(firstHeader.getValues(), Matchers.containsInAnyOrder(NottableString.string("first_one"), NottableString.string("first_two")));
        MatcherAssert.assertThat(secondHeader.getValues(), Matchers.containsInAnyOrder(NottableString.string("second_one"), NottableString.string("second_two")));
    }

    @Test
    public void shouldReturnValueSetInStaticConstructors() {
        // when
        Header firstHeader = Header.header("first", "first_one", "first_two");
        Header secondHeader = Header.header("second", Arrays.asList("second_one", "second_two"));
        // then
        MatcherAssert.assertThat(firstHeader.getValues(), Matchers.containsInAnyOrder(NottableString.string("first_one"), NottableString.string("first_two")));
        MatcherAssert.assertThat(secondHeader.getValues(), Matchers.containsInAnyOrder(NottableString.string("second_one"), NottableString.string("second_two")));
    }
}

