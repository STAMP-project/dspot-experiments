package org.mockserver.model;


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public class KeyAndValueTest {
    @Test
    public void shouldConvertNottedCookieToHashMap() {
        // given
        KeyAndValue keyAndValue = new KeyAndValue("name", "value");
        // then
        MatcherAssert.assertThat(keyAndValue.getName(), Is.is(NottableString.string("name")));
        MatcherAssert.assertThat(keyAndValue.getValue(), Is.is(NottableString.string("value")));
    }
}

