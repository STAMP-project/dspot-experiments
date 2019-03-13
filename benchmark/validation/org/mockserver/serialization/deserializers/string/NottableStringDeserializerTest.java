package org.mockserver.serialization.deserializers.string;


import java.io.IOException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.NottableString;
import org.mockserver.serialization.ObjectMapperFactory;
import org.mockserver.serialization.model.ExpectationDTO;
import org.mockserver.serialization.model.HttpRequestDTO;


/**
 *
 *
 * @author jamesdbloom
 */
/* "name" : {
"not" : false,
"value" : "!name"
},
"value" : {
"not" : true,
"value" : "!value"
}
 */
public class NottableStringDeserializerTest {
    @Test
    public void shouldDeserializeNottableString() throws IOException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().readValue("\"some_string\"", NottableString.class), Is.is(NottableString.string("some_string")));
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().readValue("{\"not\":false,\"value\":\"some_string\"}", NottableString.class), Is.is(NottableString.string("some_string")));
    }

    @Test
    public void shouldDeserializeNotNottableString() throws IOException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().readValue("\"!some_string\"", NottableString.class), Is.is(NottableString.not("some_string")));
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().readValue("{\"not\":true,\"value\":\"some_string\"}", NottableString.class), Is.is(NottableString.not("some_string")));
    }

    @Test
    public void shouldDeserializeNottableStringWithExclamationMark() throws IOException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().readValue("{\"not\":false,\"value\":\"!some_string\"}", NottableString.class), Is.is(NottableString.string("!some_string")));
    }

    @Test
    public void shouldDeserializeNottableStringWithNot() throws IOException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().readValue("{\"not\":true,\"value\":\"some_string\"}", NottableString.class), Is.is(NottableString.not("some_string")));
    }

    @Test
    public void shouldParseJSONWithMethodWithNot() throws IOException {
        // given
        String json = ((((((((((((("{" + (NEW_LINE)) + "    \"httpRequest\": {") + (NEW_LINE)) + "        \"method\" : {") + (NEW_LINE)) + "            \"not\" : true,") + (NEW_LINE)) + "            \"value\" : \"HEAD\"") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "}";
        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);
        // then
        Assert.assertThat(expectationDTO, Is.is(new ExpectationDTO().setHttpRequest(new HttpRequestDTO().setMethod(NottableString.not("HEAD")))));
    }

    @Test
    public void shouldParseJSONWithMethod() throws IOException {
        // given
        String json = ((((((("{" + (NEW_LINE)) + "    \"httpRequest\": {") + (NEW_LINE)) + "        \"method\" : \"HEAD\"") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "}";
        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);
        // then
        Assert.assertThat(expectationDTO, Is.is(new ExpectationDTO().setHttpRequest(new HttpRequestDTO().setMethod(NottableString.string("HEAD")))));
    }

    @Test
    public void shouldParseJSONWithPathWithNot() throws IOException {
        // given
        String json = ((((((((((((("{" + (NEW_LINE)) + "    \"httpRequest\": {") + (NEW_LINE)) + "        \"path\" : {") + (NEW_LINE)) + "            \"not\" : true,") + (NEW_LINE)) + "            \"value\" : \"/some/path\"") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "}";
        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);
        // then
        Assert.assertThat(expectationDTO, Is.is(new ExpectationDTO().setHttpRequest(new HttpRequestDTO().setPath(NottableString.not("/some/path")))));
    }

    @Test
    public void shouldParseJSONWithPath() throws IOException {
        // given
        String json = ((((((("{" + (NEW_LINE)) + "    \"httpRequest\": {") + (NEW_LINE)) + "        \"path\" : \"/some/path\"") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "}";
        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);
        // then
        Assert.assertThat(expectationDTO, Is.is(new ExpectationDTO().setHttpRequest(new HttpRequestDTO().setPath(NottableString.string("/some/path")))));
    }
}

