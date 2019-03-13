package org.mockserver.serialization.serializers.collections;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.Cookie;
import org.mockserver.model.Cookies;
import org.mockserver.model.NottableString;
import org.mockserver.serialization.ObjectMapperFactory;


/**
 *
 *
 * @author jamesdbloom
 */
public class CookiesSerializerTest {
    private ObjectMapper objectMapper = ObjectMapperFactory.createObjectMapper();

    @Test
    public void shouldAllowSingleObjectForArray() throws IOException {
        // given
        String expectedString = ((((("{" + (NEW_LINE)) + "  \"some_name\" : \"some_value\",") + (NEW_LINE)) + "  \"some_other_name\" : \"!some_other_value\"") + (NEW_LINE)) + "}";
        // when
        String actualString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(new Cookies().withEntries(Cookie.cookie(NottableString.string("some_name"), NottableString.string("some_value")), Cookie.cookie(NottableString.string("some_other_name"), NottableString.string("some_value")), Cookie.cookie(NottableString.string("some_other_name"), NottableString.not("some_other_value"))));
        // then
        MatcherAssert.assertThat(actualString, Is.is(expectedString));
    }
}

