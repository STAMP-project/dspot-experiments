package org.mockserver.serialization.model;


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.Cookie;
import org.mockserver.model.NottableString;


/**
 *
 *
 * @author jamesdbloom
 */
public class CookieDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        CookieDTO cookie = new CookieDTO(new Cookie("name", "value"));
        // then
        MatcherAssert.assertThat(cookie.getValue(), Is.is(NottableString.string("value")));
        MatcherAssert.assertThat(cookie.getName(), Is.is(NottableString.string("name")));
        MatcherAssert.assertThat(cookie.buildObject().getName(), Is.is(NottableString.string("name")));
        MatcherAssert.assertThat(cookie.buildObject().getValue(), Is.is(NottableString.string("value")));
    }
}

