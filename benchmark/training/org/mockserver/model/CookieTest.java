package org.mockserver.model;


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class CookieTest {
    @Test
    public void shouldReturnValuesSetInConstructors() {
        // when
        Cookie firstCookie = new Cookie("name", "value");
        // then
        MatcherAssert.assertThat(firstCookie.getName(), Is.is(NottableString.string("name")));
        MatcherAssert.assertThat(firstCookie.getValue(), Is.is(NottableString.string("value")));
    }

    @Test
    public void shouldReturnValueSetInStaticConstructors() {
        // when
        Cookie firstCookie = Cookie.cookie("name", "value");
        // then
        MatcherAssert.assertThat(firstCookie.getName(), Is.is(NottableString.string("name")));
        MatcherAssert.assertThat(firstCookie.getValue(), Is.is(NottableString.string("value")));
    }
}

