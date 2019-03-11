package com.baeldung.jackson.test;


import com.baeldung.jackson.field.MyDtoAccessLevel;
import com.baeldung.jackson.field.MyDtoWithGetter;
import com.baeldung.jackson.field.MyDtoWithSetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;


public class JacksonFieldUnitTest {
    @Test
    public final void givenDifferentAccessLevels_whenPublic_thenSerializable() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final MyDtoAccessLevel dtoObject = new MyDtoAccessLevel();
        final String dtoAsString = mapper.writeValueAsString(dtoObject);
        Assert.assertThat(dtoAsString, Matchers.not(Matchers.containsString("stringValue")));
        Assert.assertThat(dtoAsString, Matchers.not(Matchers.containsString("intValue")));
        Assert.assertThat(dtoAsString, Matchers.not(Matchers.containsString("floatValue")));
        Assert.assertThat(dtoAsString, Matchers.containsString("booleanValue"));
        System.out.println(dtoAsString);
    }

    @Test
    public final void givenDifferentAccessLevels_whenGetterAdded_thenSerializable() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final MyDtoWithGetter dtoObject = new MyDtoWithGetter();
        final String dtoAsString = mapper.writeValueAsString(dtoObject);
        Assert.assertThat(dtoAsString, Matchers.containsString("stringValue"));
        Assert.assertThat(dtoAsString, Matchers.not(Matchers.containsString("intValue")));
        System.out.println(dtoAsString);
    }

    @Test
    public final void givenDifferentAccessLevels_whenGetterAdded_thenDeserializable() throws IOException {
        final String jsonAsString = "{\"stringValue\":\"dtoString\"}";
        final ObjectMapper mapper = new ObjectMapper();
        final MyDtoWithGetter dtoObject = mapper.readValue(jsonAsString, MyDtoWithGetter.class);
        Assert.assertNotNull(dtoObject);
        Assert.assertThat(dtoObject.getStringValue(), Matchers.equalTo("dtoString"));
    }

    @Test
    public final void givenDifferentAccessLevels_whenSetterAdded_thenDeserializable() throws IOException {
        final String jsonAsString = "{\"intValue\":1}";
        final ObjectMapper mapper = new ObjectMapper();
        final MyDtoWithSetter dtoObject = mapper.readValue(jsonAsString, MyDtoWithSetter.class);
        Assert.assertNotNull(dtoObject);
        Assert.assertThat(dtoObject.accessIntValue(), Matchers.equalTo(1));
    }

    @Test
    public final void givenDifferentAccessLevels_whenSetterAdded_thenStillNotSerializable() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final MyDtoWithSetter dtoObject = new MyDtoWithSetter();
        final String dtoAsString = mapper.writeValueAsString(dtoObject);
        Assert.assertThat(dtoAsString, Matchers.not(Matchers.containsString("intValue")));
        System.out.println(dtoAsString);
    }

    @Test
    public final void givenDifferentAccessLevels_whenSetVisibility_thenSerializable() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, ANY);
        final MyDtoAccessLevel dtoObject = new MyDtoAccessLevel();
        final String dtoAsString = mapper.writeValueAsString(dtoObject);
        Assert.assertThat(dtoAsString, Matchers.containsString("stringValue"));
        Assert.assertThat(dtoAsString, Matchers.containsString("intValue"));
        Assert.assertThat(dtoAsString, Matchers.containsString("booleanValue"));
        System.out.println(dtoAsString);
    }
}

