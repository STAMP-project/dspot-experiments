package org.mockserver.serialization.deserializers.collections;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.Header;
import org.mockserver.model.Headers;
import org.mockserver.model.NottableString;
import org.mockserver.serialization.ObjectMapperFactory;


/**
 *
 *
 * @author jamesdbloom
 */
public class HeadersDeserializerTest {
    private ObjectMapper objectMapper = ObjectMapperFactory.createObjectMapper();

    @Test
    public void shouldSerializeThenDeserializer() throws IOException {
        // given
        String serializedHeaders = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(new Headers().withEntries(Header.header(NottableString.string("some_name"), Arrays.asList(NottableString.string("some_value"), NottableString.string("some_other_value"))), Header.header(NottableString.string("some_other_name"), NottableString.string("some_value")), Header.header(NottableString.string("some_other_name"), NottableString.not("some_other_value"))));
        // when
        Headers actualHeaders = objectMapper.readValue(serializedHeaders, Headers.class);
        // then
        MatcherAssert.assertThat(actualHeaders, Is.is(new Headers().withEntries(Header.header(NottableString.string("some_name"), Arrays.asList(NottableString.string("some_value"), NottableString.string("some_other_value"))), Header.header(NottableString.string("some_other_name"), NottableString.string("some_value")), Header.header(NottableString.string("some_other_name"), NottableString.not("some_other_value")))));
    }

    @Test
    public void shouldDeserializeMap() throws IOException {
        // given
        Headers expectedHeaders = new Headers().withEntries(Header.header(NottableString.string("some_name"), Arrays.asList(NottableString.string("some_value"), NottableString.string("some_other_value"))), Header.header(NottableString.string("some_other_name"), NottableString.string("some_value")), Header.header(NottableString.string("some_other_name"), NottableString.not("some_other_value")));
        // when
        Headers actualHeaders = objectMapper.readValue((((((("{" + (NEW_LINE)) + "  \"some_name\" : [ \"some_value\", \"some_other_value\" ],") + (NEW_LINE)) + "  \"some_other_name\" : [ \"some_value\", \"!some_other_value\" ]") + (NEW_LINE)) + "}"), Headers.class);
        // then
        MatcherAssert.assertThat(actualHeaders, Is.is(expectedHeaders));
    }

    @Test
    public void shouldDeserializeArray() throws IOException {
        // given
        Headers expectedHeaders = new Headers().withEntries(Header.header(NottableString.string("some_name"), Arrays.asList(NottableString.string("some_value"), NottableString.string("some_other_value"))), Header.header(NottableString.string("some_other_name"), NottableString.string("some_value")), Header.header(NottableString.string("some_other_name"), NottableString.not("some_other_value")));
        // when
        Headers actualHeaders = objectMapper.readValue((((((((((((((((((((((((((((((("[" + (NEW_LINE)) + "    {") + (NEW_LINE)) + "        \"name\": \"some_name\", ") + (NEW_LINE)) + "        \"value\": [") + (NEW_LINE)) + "            \"some_value\", ") + (NEW_LINE)) + "            \"some_other_value\"") + (NEW_LINE)) + "        ]") + (NEW_LINE)) + "    }, ") + (NEW_LINE)) + "    {") + (NEW_LINE)) + "        \"name\": \"some_other_name\", ") + (NEW_LINE)) + "        \"value\": [") + (NEW_LINE)) + "            \"some_value\", ") + (NEW_LINE)) + "            \"!some_other_value\"") + (NEW_LINE)) + "        ]") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "]"), Headers.class);
        // then
        MatcherAssert.assertThat(actualHeaders, Is.is(expectedHeaders));
    }
}

