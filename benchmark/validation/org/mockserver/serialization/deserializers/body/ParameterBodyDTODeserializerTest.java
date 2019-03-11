package org.mockserver.serialization.deserializers.body;


import java.io.IOException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.NottableString;
import org.mockserver.model.Parameter;
import org.mockserver.model.ParameterBody;
import org.mockserver.serialization.ObjectMapperFactory;
import org.mockserver.serialization.model.BodyDTO;


public class ParameterBodyDTODeserializerTest {
    @Test
    public void shouldSerializeArrayFormatParameterBodyDTO() throws IOException {
        // given
        String json = ((((((((((((((((("{" + (NEW_LINE)) + "  \"type\" : \"PARAMETERS\",") + (NEW_LINE)) + "  \"parameters\" : [ {") + (NEW_LINE)) + "    \"name\" : \"queryStringParameterOneName\",") + (NEW_LINE)) + "    \"values\" : [ \"queryStringParameterOneValueOne\", \"queryStringParameterOneValueTwo\" ]") + (NEW_LINE)) + "  }, {") + (NEW_LINE)) + "    \"name\" : \"queryStringParameterTwoName\",") + (NEW_LINE)) + "    \"values\" : [ \"queryStringParameterTwoValue\" ]") + (NEW_LINE)) + "  } ]") + (NEW_LINE)) + "}";
        // when
        BodyDTO bodyDTO = ObjectMapperFactory.createObjectMapper().readValue(json, BodyDTO.class);
        // then
        Assert.assertThat(bodyDTO, Is.<BodyDTO>is(new org.mockserver.serialization.model.ParameterBodyDTO(ParameterBody.params(Parameter.param("queryStringParameterOneName", "queryStringParameterOneValueOne", "queryStringParameterOneValueTwo"), Parameter.param("queryStringParameterTwoName", "queryStringParameterTwoValue")))));
    }

    @Test
    public void shouldSerializeObjectFormatParameterBodyDTO() throws IOException {
        // given
        String json = ((((((((((("{" + (NEW_LINE)) + "  \"type\" : \"PARAMETERS\",") + (NEW_LINE)) + "  \"parameters\" : {") + (NEW_LINE)) + "    \"queryStringParameterOneName\" : [ \"queryStringParameterOneValueOne\", \"queryStringParameterOneValueTwo\" ],") + (NEW_LINE)) + "    \"queryStringParameterTwoName\" : [ \"queryStringParameterTwoValue\" ]") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}";
        // when
        BodyDTO bodyDTO = ObjectMapperFactory.createObjectMapper().readValue(json, BodyDTO.class);
        // then
        Assert.assertThat(bodyDTO, Is.<BodyDTO>is(new org.mockserver.serialization.model.ParameterBodyDTO(ParameterBody.params(Parameter.param("queryStringParameterOneName", "queryStringParameterOneValueOne", "queryStringParameterOneValueTwo"), Parameter.param("queryStringParameterTwoName", "queryStringParameterTwoValue")))));
    }

    @Test
    public void shouldSerializeArrayFormatParameterBodyDTOWithNot() throws IOException {
        // given
        String json = ((((((((((((((((((("{" + (NEW_LINE)) + "  \"not\" : true,") + (NEW_LINE)) + "  \"type\" : \"PARAMETERS\",") + (NEW_LINE)) + "  \"parameters\" : [ {") + (NEW_LINE)) + "    \"name\" : \"queryStringParameterOneName\",") + (NEW_LINE)) + "    \"values\" : [ \"queryStringParameterOneValueOne\", \"queryStringParameterOneValueTwo\" ]") + (NEW_LINE)) + "  }, {") + (NEW_LINE)) + "    \"name\" : \"queryStringParameterTwoName\",") + (NEW_LINE)) + "    \"values\" : [ \"queryStringParameterTwoValue\" ]") + (NEW_LINE)) + "  } ]") + (NEW_LINE)) + "}";
        // when
        BodyDTO bodyDTO = ObjectMapperFactory.createObjectMapper().readValue(json, BodyDTO.class);
        // then
        Assert.assertThat(bodyDTO, Is.<BodyDTO>is(new org.mockserver.serialization.model.ParameterBodyDTO(ParameterBody.params(Parameter.param("queryStringParameterOneName", "queryStringParameterOneValueOne", "queryStringParameterOneValueTwo"), Parameter.param("queryStringParameterTwoName", "queryStringParameterTwoValue")), true)));
    }

    @Test
    public void shouldSerializeObjectFormatParameterBodyDTOWithNot() throws IOException {
        // given
        String json = ((((((((((((("{" + (NEW_LINE)) + "  \"not\" : true,") + (NEW_LINE)) + "  \"type\" : \"PARAMETERS\",") + (NEW_LINE)) + "  \"parameters\" : {") + (NEW_LINE)) + "    \"queryStringParameterTwoName\" : [ \"queryStringParameterTwoValue\" ],") + (NEW_LINE)) + "    \"queryStringParameterOneName\" : [ \"queryStringParameterOneValueOne\", \"queryStringParameterOneValueTwo\" ]") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}";
        // when
        BodyDTO bodyDTO = ObjectMapperFactory.createObjectMapper().readValue(json, BodyDTO.class);
        // then
        Assert.assertThat(bodyDTO, Is.<BodyDTO>is(new org.mockserver.serialization.model.ParameterBodyDTO(ParameterBody.params(Parameter.param("queryStringParameterTwoName", "queryStringParameterTwoValue"), Parameter.param("queryStringParameterOneName", "queryStringParameterOneValueOne", "queryStringParameterOneValueTwo")), true)));
    }

    @Test
    public void shouldSerializeArrayFormatParameterBodyDTOWithAllNottedParameterKeys() throws IOException {
        // given
        String json = ((((((((((((((((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"type\" : \"PARAMETERS\",") + (NEW_LINE)) + "  \"parameters\" : [ {") + (NEW_LINE)) + "    \"name\" : {") + (NEW_LINE)) + "      \"not\" : true,") + (NEW_LINE)) + "      \"value\" : \"queryStringParameterOneName\"") + (NEW_LINE)) + "    },") + (NEW_LINE)) + "    \"values\" : [ {") + (NEW_LINE)) + "      \"not\" : false,") + (NEW_LINE)) + "      \"value\" : \"queryStringParameterOneValueOne\"") + (NEW_LINE)) + "    }, {") + (NEW_LINE)) + "      \"not\" : false,") + (NEW_LINE)) + "      \"value\" : \"queryStringParameterOneValueTwo\"") + (NEW_LINE)) + "    } ]") + (NEW_LINE)) + "  }, {") + (NEW_LINE)) + "    \"name\" : {") + (NEW_LINE)) + "      \"not\" : true,") + (NEW_LINE)) + "      \"value\" : \"queryStringParameterTwoName\"") + (NEW_LINE)) + "    },") + (NEW_LINE)) + "    \"values\" : [ {") + (NEW_LINE)) + "      \"not\" : false,") + (NEW_LINE)) + "      \"value\" : \"queryStringParameterTwoValue\"") + (NEW_LINE)) + "    } ]") + (NEW_LINE)) + "  } ]") + (NEW_LINE)) + "}";
        // when
        BodyDTO bodyDTO = ObjectMapperFactory.createObjectMapper().readValue(json, BodyDTO.class);
        // then
        Assert.assertThat(bodyDTO, Is.<BodyDTO>is(new org.mockserver.serialization.model.ParameterBodyDTO(ParameterBody.params(Parameter.param(NottableString.not("queryStringParameterOneName"), NottableString.string("queryStringParameterOneValueOne"), NottableString.string("queryStringParameterOneValueTwo")), Parameter.param(NottableString.not("queryStringParameterTwoName"), NottableString.string("queryStringParameterTwoValue"))))));
    }

    @Test
    public void shouldSerializeObjectFormatParameterBodyDTOWithAllNottedParameterKeys() throws IOException {
        // given
        String json = ((((((((((("{" + (NEW_LINE)) + "  \"type\" : \"PARAMETERS\",") + (NEW_LINE)) + "  \"parameters\" : {") + (NEW_LINE)) + "    \"!queryStringParameterOneName\" : [ \"queryStringParameterOneValueOne\", \"queryStringParameterOneValueTwo\" ],") + (NEW_LINE)) + "    \"!queryStringParameterTwoName\" : [ \"queryStringParameterTwoValue\" ]") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}";
        // when
        BodyDTO bodyDTO = ObjectMapperFactory.createObjectMapper().readValue(json, BodyDTO.class);
        // then
        Assert.assertThat(bodyDTO, Is.<BodyDTO>is(new org.mockserver.serialization.model.ParameterBodyDTO(ParameterBody.params(Parameter.param(NottableString.not("queryStringParameterOneName"), NottableString.string("queryStringParameterOneValueOne"), NottableString.string("queryStringParameterOneValueTwo")), Parameter.param(NottableString.not("queryStringParameterTwoName"), NottableString.string("queryStringParameterTwoValue"))))));
    }

    @Test
    public void shouldSerializeArrayFormatParameterBodyDTOWithAllNottedParameterValues() throws IOException {
        // given
        String json = ((((((((((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"type\" : \"PARAMETERS\",") + (NEW_LINE)) + "  \"parameters\" : [ {") + (NEW_LINE)) + "    \"name\" : \"queryStringParameterOneName\",") + (NEW_LINE)) + "    \"values\" : [ {") + (NEW_LINE)) + "      \"not\" : true,") + (NEW_LINE)) + "      \"value\" : \"queryStringParameterOneValueOne\"") + (NEW_LINE)) + "    }, {") + (NEW_LINE)) + "      \"not\" : true,") + (NEW_LINE)) + "      \"value\" : \"queryStringParameterOneValueTwo\"") + (NEW_LINE)) + "    } ]") + (NEW_LINE)) + "  }, {") + (NEW_LINE)) + "    \"name\" : {") + (NEW_LINE)) + "      \"not\" : false,") + (NEW_LINE)) + "      \"value\" : \"queryStringParameterTwoName\"") + (NEW_LINE)) + "    },") + (NEW_LINE)) + "    \"values\" : [ {") + (NEW_LINE)) + "      \"not\" : true,") + (NEW_LINE)) + "      \"value\" : \"queryStringParameterTwoValue\"") + (NEW_LINE)) + "    } ]") + (NEW_LINE)) + "  } ]") + (NEW_LINE)) + "}";
        // when
        BodyDTO bodyDTO = ObjectMapperFactory.createObjectMapper().readValue(json, BodyDTO.class);
        // then
        Assert.assertThat(bodyDTO, Is.<BodyDTO>is(new org.mockserver.serialization.model.ParameterBodyDTO(ParameterBody.params(Parameter.param(NottableString.string("queryStringParameterOneName"), NottableString.not("queryStringParameterOneValueOne"), NottableString.not("queryStringParameterOneValueTwo")), Parameter.param(NottableString.string("queryStringParameterTwoName"), NottableString.not("queryStringParameterTwoValue"))))));
    }

    @Test
    public void shouldSerializeObjectFormatParameterBodyDTOWithAllNottedParameterValues() throws IOException {
        // given
        String json = ((((((((((("{" + (NEW_LINE)) + "  \"type\" : \"PARAMETERS\",") + (NEW_LINE)) + "  \"parameters\" : {") + (NEW_LINE)) + "    \"queryStringParameterOneName\" : [ \"!queryStringParameterOneValueOne\", \"!queryStringParameterOneValueTwo\" ],") + (NEW_LINE)) + "    \"queryStringParameterTwoName\" : [ \"!queryStringParameterTwoValue\" ]") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}";
        // when
        BodyDTO bodyDTO = ObjectMapperFactory.createObjectMapper().readValue(json, BodyDTO.class);
        // then
        Assert.assertThat(bodyDTO, Is.<BodyDTO>is(new org.mockserver.serialization.model.ParameterBodyDTO(ParameterBody.params(Parameter.param(NottableString.string("queryStringParameterOneName"), NottableString.not("queryStringParameterOneValueOne"), NottableString.not("queryStringParameterOneValueTwo")), Parameter.param(NottableString.string("queryStringParameterTwoName"), NottableString.not("queryStringParameterTwoValue"))))));
    }

    @Test
    public void shouldSerializeArrayFormatParameterBodyDTOWithAllNottedParameterKeysAndValue() throws IOException {
        // given
        String json = ((((((((((((((((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"type\" : \"PARAMETERS\",") + (NEW_LINE)) + "  \"parameters\" : [ {") + (NEW_LINE)) + "    \"name\" : {") + (NEW_LINE)) + "      \"not\" : true,") + (NEW_LINE)) + "      \"value\" : \"queryStringParameterOneName\"") + (NEW_LINE)) + "    },") + (NEW_LINE)) + "    \"values\" : [ {") + (NEW_LINE)) + "      \"not\" : true,") + (NEW_LINE)) + "      \"value\" : \"queryStringParameterOneValueOne\"") + (NEW_LINE)) + "    }, {") + (NEW_LINE)) + "      \"not\" : true,") + (NEW_LINE)) + "      \"value\" : \"queryStringParameterOneValueTwo\"") + (NEW_LINE)) + "    } ]") + (NEW_LINE)) + "  }, {") + (NEW_LINE)) + "    \"name\" : {") + (NEW_LINE)) + "      \"not\" : true,") + (NEW_LINE)) + "      \"value\" : \"queryStringParameterTwoName\"") + (NEW_LINE)) + "    },") + (NEW_LINE)) + "    \"values\" : [ {") + (NEW_LINE)) + "      \"not\" : true,") + (NEW_LINE)) + "      \"value\" : \"queryStringParameterTwoValue\"") + (NEW_LINE)) + "    } ]") + (NEW_LINE)) + "  } ]") + (NEW_LINE)) + "}";
        // when
        BodyDTO bodyDTO = ObjectMapperFactory.createObjectMapper().readValue(json, BodyDTO.class);
        // then
        Assert.assertThat(bodyDTO, Is.<BodyDTO>is(new org.mockserver.serialization.model.ParameterBodyDTO(ParameterBody.params(Parameter.param(NottableString.not("queryStringParameterOneName"), NottableString.not("queryStringParameterOneValueOne"), NottableString.not("queryStringParameterOneValueTwo")), Parameter.param(NottableString.not("queryStringParameterTwoName"), NottableString.not("queryStringParameterTwoValue"))))));
    }

    @Test
    public void shouldSerializeObjectFormatParameterBodyDTOWithAllNottedParameterKeysAndValue() throws IOException {
        // given
        String json = ((((((((((((("{" + (NEW_LINE)) + "  \"type\" : \"PARAMETERS\",") + (NEW_LINE)) + "  \"parameters\" : {") + (NEW_LINE)) + "    \"!queryStringParameterOneName\" : [ \"!queryStringParameterOneValueOne\", \"!queryStringParameterOneValueTwo\" ],") + (NEW_LINE)) + "    \"!queryStringParameterTwoName\" : [ \"!queryStringParameterTwoValue\" ],") + (NEW_LINE)) + "    \"!queryStringParameterThreeName\" : [ \"!queryStringParameterThreeValueOne\", \"!queryStringParameterThreeValueTwo\", \"!queryStringParameterThreeValueThree\" ]") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}";
        // when
        BodyDTO bodyDTO = ObjectMapperFactory.createObjectMapper().readValue(json, BodyDTO.class);
        // then
        Assert.assertThat(bodyDTO, Is.<BodyDTO>is(new org.mockserver.serialization.model.ParameterBodyDTO(ParameterBody.params(Parameter.param(NottableString.not("queryStringParameterOneName"), NottableString.not("queryStringParameterOneValueOne"), NottableString.not("queryStringParameterOneValueTwo")), Parameter.param(NottableString.not("queryStringParameterTwoName"), NottableString.not("queryStringParameterTwoValue")), Parameter.param(NottableString.not("queryStringParameterThreeName"), NottableString.not("queryStringParameterThreeValueOne"), NottableString.not("queryStringParameterThreeValueTwo"), NottableString.not("queryStringParameterThreeValueThree"))))));
    }

    @Test
    public void shouldSerializeArrayFormatParameterBodyDTOWithAMixtureOfNottedAndStringParameterKeysAndValue() throws IOException {
        // given
        String json = ((((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"type\" : \"PARAMETERS\",") + (NEW_LINE)) + "  \"parameters\" : [ {") + (NEW_LINE)) + "    \"name\" : {") + (NEW_LINE)) + "      \"not\" : true,") + (NEW_LINE)) + "      \"value\" : \"queryStringParameterOneName\"") + (NEW_LINE)) + "    },") + (NEW_LINE)) + "    \"values\" : [ \"queryStringParameterOneValueOne\", {") + (NEW_LINE)) + "      \"not\" : true,") + (NEW_LINE)) + "      \"value\" : \"queryStringParameterOneValueTwo\"") + (NEW_LINE)) + "    } ]") + (NEW_LINE)) + "  }, {") + (NEW_LINE)) + "    \"name\" : \"queryStringParameterTwoName\",") + (NEW_LINE)) + "    \"values\" : [ {") + (NEW_LINE)) + "      \"not\" : true,") + (NEW_LINE)) + "      \"value\" : \"queryStringParameterTwoValue\"") + (NEW_LINE)) + "    } ]") + (NEW_LINE)) + "  } ]") + (NEW_LINE)) + "}";
        // when
        BodyDTO bodyDTO = ObjectMapperFactory.createObjectMapper().readValue(json, BodyDTO.class);
        // then
        Assert.assertThat(bodyDTO, Is.<BodyDTO>is(new org.mockserver.serialization.model.ParameterBodyDTO(ParameterBody.params(Parameter.param(NottableString.not("queryStringParameterOneName"), NottableString.string("queryStringParameterOneValueOne"), NottableString.not("queryStringParameterOneValueTwo")), Parameter.param(NottableString.string("queryStringParameterTwoName"), NottableString.not("queryStringParameterTwoValue"))))));
    }

    @Test
    public void shouldSerializeObjectFormatParameterBodyDTOWithAMixtureOfNottedAndStringParameterKeysAndValue() throws IOException {
        // given
        String json = ((((((((((("{" + (NEW_LINE)) + "  \"type\" : \"PARAMETERS\",") + (NEW_LINE)) + "  \"parameters\" : {") + (NEW_LINE)) + "    \"!queryStringParameterOneName\" : [ \"queryStringParameterOneValueOne\", \"!queryStringParameterOneValueTwo\" ],") + (NEW_LINE)) + "    \"queryStringParameterTwoName\" : [ \"!queryStringParameterTwoValue\" ]") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}";
        // when
        BodyDTO bodyDTO = ObjectMapperFactory.createObjectMapper().readValue(json, BodyDTO.class);
        // then
        Assert.assertThat(bodyDTO, Is.<BodyDTO>is(new org.mockserver.serialization.model.ParameterBodyDTO(ParameterBody.params(Parameter.param(NottableString.not("queryStringParameterOneName"), NottableString.string("queryStringParameterOneValueOne"), NottableString.not("queryStringParameterOneValueTwo")), Parameter.param(NottableString.string("queryStringParameterTwoName"), NottableString.not("queryStringParameterTwoValue"))))));
    }
}

