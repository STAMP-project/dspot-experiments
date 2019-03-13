package com.baeldung.jackson.try1;


import com.baeldung.jackson.dtos.ItemWithSerializer;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class JacksonDeserializationUnitTest {
    @Test
    public final void givenDeserializerIsOnClass_whenDeserializingCustomRepresentation_thenCorrect() throws JsonParseException, JsonMappingException, IOException {
        final String json = "{\"id\":1,\"itemName\":\"theItem\",\"owner\":2}";
        final ItemWithSerializer readValue = new ObjectMapper().readValue(json, ItemWithSerializer.class);
        Assert.assertThat(readValue, Matchers.notNullValue());
    }
}

