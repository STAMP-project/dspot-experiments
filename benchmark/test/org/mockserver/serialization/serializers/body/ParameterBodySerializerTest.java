package org.mockserver.serialization.serializers.body;


import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.Not;
import org.mockserver.model.NottableString;
import org.mockserver.model.Parameter;
import org.mockserver.model.ParameterBody;
import org.mockserver.serialization.ObjectMapperFactory;


public class ParameterBodySerializerTest {
    @Test
    public void shouldSerializeParameterBodyDTO() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(ParameterBody.params(Parameter.param("queryStringParameterOneName", "queryStringParameterOneValueOne", "queryStringParameterOneValueTwo"), Parameter.param("queryStringParameterTwoName", "queryStringParameterTwoValue"))), Is.is((((((((((((("{" + (NEW_LINE)) + "  \"type\" : \"PARAMETERS\",") + (NEW_LINE)) + "  \"value\" : {") + (NEW_LINE)) + "    \"queryStringParameterOneName\" : [ \"queryStringParameterOneValueOne\", \"queryStringParameterOneValueTwo\" ],") + (NEW_LINE)) + "    \"queryStringParameterTwoName\" : [ \"queryStringParameterTwoValue\" ]") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldSerializeParameterBodyDTOWithNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(Not.not(ParameterBody.params(Parameter.param("queryStringParameterOneName", "queryStringParameterOneValueOne", "queryStringParameterOneValueTwo"), Parameter.param("queryStringParameterTwoName", "queryStringParameterTwoValue")))), Is.is((((((((((((((("{" + (NEW_LINE)) + "  \"not\" : true,") + (NEW_LINE)) + "  \"type\" : \"PARAMETERS\",") + (NEW_LINE)) + "  \"value\" : {") + (NEW_LINE)) + "    \"queryStringParameterOneName\" : [ \"queryStringParameterOneValueOne\", \"queryStringParameterOneValueTwo\" ],") + (NEW_LINE)) + "    \"queryStringParameterTwoName\" : [ \"queryStringParameterTwoValue\" ]") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldSerializeParameterBodyDTOWithAllNottedParameterKeys() throws IOException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(ParameterBody.params(Parameter.param(NottableString.not("queryStringParameterOneName"), NottableString.not("queryStringParameterOneValueOne"), NottableString.not("queryStringParameterOneValueTwo")), Parameter.param(NottableString.not("queryStringParameterTwoName"), NottableString.not("queryStringParameterTwoValue")))), Is.is((((((((((((("{" + (NEW_LINE)) + "  \"type\" : \"PARAMETERS\",") + (NEW_LINE)) + "  \"value\" : {") + (NEW_LINE)) + "    \"!queryStringParameterOneName\" : [ \"!queryStringParameterOneValueOne\", \"!queryStringParameterOneValueTwo\" ],") + (NEW_LINE)) + "    \"!queryStringParameterTwoName\" : [ \"!queryStringParameterTwoValue\" ]") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldSerializeParameterBodyDTOWithAllNottedParameterValues() throws IOException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(ParameterBody.params(Parameter.param(NottableString.not("queryStringParameterOneName"), NottableString.not("queryStringParameterOneValueOne"), NottableString.not("queryStringParameterOneValueTwo")), Parameter.param(NottableString.not("queryStringParameterTwoName"), NottableString.not("queryStringParameterTwoValue")))), Is.is((((((((((((("{" + (NEW_LINE)) + "  \"type\" : \"PARAMETERS\",") + (NEW_LINE)) + "  \"value\" : {") + (NEW_LINE)) + "    \"!queryStringParameterOneName\" : [ \"!queryStringParameterOneValueOne\", \"!queryStringParameterOneValueTwo\" ],") + (NEW_LINE)) + "    \"!queryStringParameterTwoName\" : [ \"!queryStringParameterTwoValue\" ]") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldSerializeParameterBodyDTOWithAllNottedParameterKeysAndValue() throws IOException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(ParameterBody.params(Parameter.param(NottableString.not("queryStringParameterOneName"), NottableString.not("queryStringParameterOneValueOne"), NottableString.not("queryStringParameterOneValueTwo")), Parameter.param(NottableString.not("queryStringParameterTwoName"), NottableString.not("queryStringParameterTwoValue")))), Is.is((((((((((((("{" + (NEW_LINE)) + "  \"type\" : \"PARAMETERS\",") + (NEW_LINE)) + "  \"value\" : {") + (NEW_LINE)) + "    \"!queryStringParameterOneName\" : [ \"!queryStringParameterOneValueOne\", \"!queryStringParameterOneValueTwo\" ],") + (NEW_LINE)) + "    \"!queryStringParameterTwoName\" : [ \"!queryStringParameterTwoValue\" ]") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldSerializeParameterBodyDTOWithAMixtureOfNottedAndStringParameterKeysAndValue() throws IOException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(ParameterBody.params(Parameter.param(NottableString.not("queryStringParameterOneName"), NottableString.string("queryStringParameterOneValueOne"), NottableString.not("queryStringParameterOneValueTwo")), Parameter.param(NottableString.string("queryStringParameterTwoName"), NottableString.not("queryStringParameterTwoValue")))), Is.is((((((((((((("{" + (NEW_LINE)) + "  \"type\" : \"PARAMETERS\",") + (NEW_LINE)) + "  \"value\" : {") + (NEW_LINE)) + "    \"!queryStringParameterOneName\" : [ \"queryStringParameterOneValueOne\", \"!queryStringParameterOneValueTwo\" ],") + (NEW_LINE)) + "    \"queryStringParameterTwoName\" : [ \"!queryStringParameterTwoValue\" ]") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}")));
    }
}

