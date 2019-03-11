package com.baeldung.jackson.test;


import com.baeldung.jackson.dtos.withEnum.DistanceEnumSimple;
import com.baeldung.jackson.dtos.withEnum.DistanceEnumWithJsonFormat;
import com.baeldung.jackson.dtos.withEnum.DistanceEnumWithValue;
import com.baeldung.jackson.dtos.withEnum.MyDtoWithEnumCustom;
import com.baeldung.jackson.dtos.withEnum.MyDtoWithEnumJsonFormat;
import com.baeldung.jackson.enums.Distance;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class JacksonSerializationEnumsUnitTest {
    // tests - simple enum
    @Test
    public final void whenSerializingASimpleEnum_thenCorrect() throws JsonParseException, IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final String enumAsString = mapper.writeValueAsString(DistanceEnumSimple.MILE);
        Assert.assertThat(enumAsString, Matchers.containsString("MILE"));
    }

    // tests - enum with main value
    @Test
    public final void whenSerializingAEnumWithValue_thenCorrect() throws JsonParseException, IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final String enumAsString = mapper.writeValueAsString(DistanceEnumWithValue.MILE);
        Assert.assertThat(enumAsString, Matchers.is("1609.34"));
    }

    // tests - enum
    @Test
    public final void whenSerializingAnEnum_thenCorrect() throws JsonParseException, IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final String enumAsString = mapper.writeValueAsString(DistanceEnumWithJsonFormat.MILE);
        Assert.assertThat(enumAsString, Matchers.containsString("\"meters\":1609.34"));
    }

    @Test
    public final void whenSerializingEntityWithEnum_thenCorrect() throws JsonParseException, IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final String enumAsString = mapper.writeValueAsString(new MyDtoWithEnumJsonFormat("a", 1, true, DistanceEnumWithJsonFormat.MILE));
        Assert.assertThat(enumAsString, Matchers.containsString("\"meters\":1609.34"));
    }

    @Test
    public final void whenSerializingArrayOfEnums_thenCorrect() throws JsonParseException, IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final String json = mapper.writeValueAsString(new DistanceEnumWithJsonFormat[]{ DistanceEnumWithJsonFormat.MILE, DistanceEnumWithJsonFormat.KILOMETER });
        Assert.assertThat(json, Matchers.containsString("\"meters\":1609.34"));
    }

    // tests - enum with custom serializer
    @Test
    public final void givenCustomSerializer_whenSerializingEntityWithEnum_thenCorrect() throws JsonParseException, IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final String enumAsString = mapper.writeValueAsString(new MyDtoWithEnumCustom("a", 1, true, Distance.MILE));
        Assert.assertThat(enumAsString, Matchers.containsString("\"meters\":1609.34"));
    }
}

