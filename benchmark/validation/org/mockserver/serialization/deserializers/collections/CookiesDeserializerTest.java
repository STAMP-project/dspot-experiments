package org.mockserver.serialization.deserializers.collections;


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
public class CookiesDeserializerTest {
    private ObjectMapper objectMapper = ObjectMapperFactory.createObjectMapper();

    @Test
    public void shouldSerializeThenDeserializer() throws IOException {
        // given
        String serializedHeaders = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(new Cookies().withEntries(Cookie.cookie(NottableString.string("some_name"), NottableString.string("some_value")), Cookie.cookie(NottableString.string("some_other_name"), NottableString.not("some_other_value"))));
        // when
        Cookies actualHeaders = objectMapper.readValue(serializedHeaders, Cookies.class);
        // then
        MatcherAssert.assertThat(actualHeaders, Is.is(new Cookies().withEntries(Cookie.cookie(NottableString.string("some_name"), NottableString.string("some_value")), Cookie.cookie(NottableString.string("some_other_name"), NottableString.not("some_other_value")))));
    }

    @Test
    public void shouldDeserializeMap() throws IOException {
        // given
        Cookies expectedHeaders = new Cookies().withEntries(Cookie.cookie(NottableString.string("some_name"), NottableString.string("some_value")), Cookie.cookie(NottableString.string("some_other_name"), NottableString.not("some_other_value")));
        // when
        Cookies actualHeaders = objectMapper.readValue((((((("{" + (NEW_LINE)) + "  \"some_name\" : \"some_value\",") + (NEW_LINE)) + "  \"some_other_name\" : \"!some_other_value\"") + (NEW_LINE)) + "}"), Cookies.class);
        // then
        MatcherAssert.assertThat(actualHeaders, Is.is(expectedHeaders));
    }

    @Test
    public void shouldDeserializeArray() throws IOException {
        // given
        Cookies expectedHeaders = new Cookies().withEntries(Cookie.cookie(NottableString.string("some_name"), NottableString.string("some_value")), Cookie.cookie(NottableString.string("some_other_name"), NottableString.not("some_other_value")));
        // when
        Cookies actualHeaders = objectMapper.readValue((((((((((((((((((("[" + (NEW_LINE)) + "    {") + (NEW_LINE)) + "        \"name\": \"some_name\", ") + (NEW_LINE)) + "        \"value\": \"some_value\"") + (NEW_LINE)) + "    }, ") + (NEW_LINE)) + "    {") + (NEW_LINE)) + "        \"name\": \"some_other_name\", ") + (NEW_LINE)) + "        \"value\": \"!some_other_value\"") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "]"), Cookies.class);
        // then
        MatcherAssert.assertThat(actualHeaders, Is.is(expectedHeaders));
    }
}

