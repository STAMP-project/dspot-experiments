package com.baeldung.jackson.enums;


import Distance.MILE;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class JacksonEnumSerializationUnitTest {
    @Test
    public final void givenEnum_whenSerializingJson_thenCorrectRepresentation() throws JsonParseException, IOException {
        final String dtoAsString = new ObjectMapper().writeValueAsString(MILE);
        Assert.assertThat(dtoAsString, Matchers.containsString("1609.34"));
    }
}

