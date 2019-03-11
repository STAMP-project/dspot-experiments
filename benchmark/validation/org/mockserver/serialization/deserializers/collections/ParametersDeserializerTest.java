package org.mockserver.serialization.deserializers.collections;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.NottableString;
import org.mockserver.model.Parameter;
import org.mockserver.model.Parameters;
import org.mockserver.serialization.ObjectMapperFactory;


/**
 *
 *
 * @author jamesdbloom
 */
public class ParametersDeserializerTest {
    private ObjectMapper objectMapper = ObjectMapperFactory.createObjectMapper();

    @Test
    public void shouldSerializeThenDeserializer() throws IOException {
        // given
        String serializedParameters = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(new Parameters().withEntries(Parameter.param(NottableString.string("some_name"), Arrays.asList(NottableString.string("some_value"), NottableString.string("some_other_value"))), Parameter.param(NottableString.string("some_other_name"), NottableString.string("some_value")), Parameter.param(NottableString.string("some_other_name"), NottableString.not("some_other_value"))));
        // when
        Parameters actualParameters = objectMapper.readValue(serializedParameters, Parameters.class);
        // then
        MatcherAssert.assertThat(actualParameters, Is.is(new Parameters().withEntries(Parameter.param(NottableString.string("some_name"), Arrays.asList(NottableString.string("some_value"), NottableString.string("some_other_value"))), Parameter.param(NottableString.string("some_other_name"), NottableString.string("some_value")), Parameter.param(NottableString.string("some_other_name"), NottableString.not("some_other_value")))));
    }

    @Test
    public void shouldDeserializeMap() throws IOException {
        // given
        ObjectMapper objectMapper = this.objectMapper;
        Parameters expectedParameters = new Parameters().withEntries(Parameter.param(NottableString.string("some_name"), Arrays.asList(NottableString.string("some_value"), NottableString.string("some_other_value"))), Parameter.param(NottableString.string("some_other_name"), NottableString.string("some_value")), Parameter.param(NottableString.string("some_other_name"), NottableString.not("some_other_value")));
        // when
        Parameters actualParameters = objectMapper.readValue((((((("{" + (NEW_LINE)) + "  \"some_name\" : [ \"some_value\", \"some_other_value\" ],") + (NEW_LINE)) + "  \"some_other_name\" : [ \"some_value\", \"!some_other_value\" ]") + (NEW_LINE)) + "}"), Parameters.class);
        // then
        MatcherAssert.assertThat(actualParameters, Is.is(expectedParameters));
    }

    @Test
    public void shouldDeserializeArray() throws IOException {
        // given
        Parameters expectedParameters = new Parameters().withEntries(Parameter.param(NottableString.string("some_name"), Arrays.asList(NottableString.string("some_value"), NottableString.string("some_other_value"))), Parameter.param(NottableString.string("some_other_name"), NottableString.string("some_value")), Parameter.param(NottableString.string("some_other_name"), NottableString.not("some_other_value")));
        // when
        Parameters actualParameters = objectMapper.readValue((((((((((((((((((((((((((((((("[" + (NEW_LINE)) + "    {") + (NEW_LINE)) + "        \"name\": \"some_name\", ") + (NEW_LINE)) + "        \"value\": [") + (NEW_LINE)) + "            \"some_value\", ") + (NEW_LINE)) + "            \"some_other_value\"") + (NEW_LINE)) + "        ]") + (NEW_LINE)) + "    }, ") + (NEW_LINE)) + "    {") + (NEW_LINE)) + "        \"name\": \"some_other_name\", ") + (NEW_LINE)) + "        \"value\": [") + (NEW_LINE)) + "            \"some_value\", ") + (NEW_LINE)) + "            \"!some_other_value\"") + (NEW_LINE)) + "        ]") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "]"), Parameters.class);
        // then
        MatcherAssert.assertThat(actualParameters, Is.is(expectedParameters));
    }
}

