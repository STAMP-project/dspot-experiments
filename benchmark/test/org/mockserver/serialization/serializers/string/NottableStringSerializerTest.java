package org.mockserver.serialization.serializers.string;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.NottableString;
import org.mockserver.model.org.mockserver.model.NottableString;
import org.mockserver.serialization.ObjectMapperFactory;


public class NottableStringSerializerTest {
    @Test
    public void shouldSerializeObjectWithNottableString() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new Object() {
            public NottableString getValue() {
                return NottableString.string("some_string");
            }
        }), Is.is("{\"value\":\"some_string\"}"));
    }

    @Test
    public void shouldSerializeObjectWithNottedNottableString() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(new Object() {
            public NottableString getValue() {
                return NottableString.not("some_string");
            }
        }), Is.is("{\"value\":\"!some_string\"}"));
    }

    @Test
    public void shouldSerializeNottableString() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(NottableString.string("some_string")), Is.is("\"some_string\""));
    }

    @Test
    public void shouldSerializeNotNottableString() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(org.mockserver.model.NottableString.not("some_string")), Is.is("\"!some_string\""));
    }

    @Test
    public void shouldSerializeNotNottableStringWithExclamationMark() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(org.mockserver.model.NottableString.not("!some_string")), Is.is("\"!!some_string\""));
    }

    @Test
    public void shouldSerializeNottableStringWithNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(NottableString.string("some_string", true)), Is.is("\"!some_string\""));
    }

    @Test
    public void shouldSerializeNottableStringWithExclamationMarkAndNot() throws JsonProcessingException {
        Assert.assertThat(ObjectMapperFactory.createObjectMapper().writeValueAsString(NottableString.string("!some_string", true)), Is.is("\"!!some_string\""));
    }
}

