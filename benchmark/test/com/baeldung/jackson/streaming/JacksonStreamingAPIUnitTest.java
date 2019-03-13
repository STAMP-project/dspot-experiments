package com.baeldung.jackson.streaming;


import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;


public class JacksonStreamingAPIUnitTest {
    @Test
    public void givenJsonGenerator_whenAppendJsonToIt_thenGenerateJson() throws IOException {
        // given
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JsonFactory jfactory = new JsonFactory();
        JsonGenerator jGenerator = jfactory.createGenerator(stream, JsonEncoding.UTF8);
        // when
        jGenerator.writeStartObject();
        jGenerator.writeStringField("name", "Tom");
        jGenerator.writeNumberField("age", 25);
        jGenerator.writeFieldName("address");
        jGenerator.writeStartArray();
        jGenerator.writeString("Poland");
        jGenerator.writeString("5th avenue");
        jGenerator.writeEndArray();
        jGenerator.writeEndObject();
        jGenerator.close();
        // then
        String json = new String(stream.toByteArray(), "UTF-8");
        TestCase.assertEquals(json, "{\"name\":\"Tom\",\"age\":25,\"address\":[\"Poland\",\"5th avenue\"]}");
    }

    @Test
    public void givenJson_whenReadItUsingStreamAPI_thenShouldCreateProperJsonObject() throws IOException {
        // given
        String json = "{\"name\":\"Tom\",\"age\":25,\"address\":[\"Poland\",\"5th avenue\"]}";
        JsonFactory jfactory = new JsonFactory();
        JsonParser jParser = jfactory.createParser(json);
        String parsedName = null;
        Integer parsedAge = null;
        List<String> addresses = new LinkedList<>();
        // when
        while ((jParser.nextToken()) != (JsonToken.END_OBJECT)) {
            String fieldname = jParser.getCurrentName();
            if ("name".equals(fieldname)) {
                jParser.nextToken();
                parsedName = jParser.getText();
            }
            if ("age".equals(fieldname)) {
                jParser.nextToken();
                parsedAge = jParser.getIntValue();
            }
            if ("address".equals(fieldname)) {
                jParser.nextToken();
                while ((jParser.nextToken()) != (JsonToken.END_ARRAY)) {
                    addresses.add(jParser.getText());
                } 
            }
        } 
        jParser.close();
        // then
        TestCase.assertEquals(parsedName, "Tom");
        TestCase.assertEquals(parsedAge, ((Integer) (25)));
        TestCase.assertEquals(addresses, Arrays.asList("Poland", "5th avenue"));
    }

    @Test
    public void givenJson_whenWantToExtractPartOfIt_thenShouldExtractOnlyNeededFieldWithoutGoingThroughWholeJSON() throws IOException {
        // given
        String json = "{\"name\":\"Tom\",\"age\":25,\"address\":[\"Poland\",\"5th avenue\"]}";
        JsonFactory jfactory = new JsonFactory();
        JsonParser jParser = jfactory.createParser(json);
        String parsedName = null;
        Integer parsedAge = null;
        List<String> addresses = new LinkedList<>();
        // when
        while ((jParser.nextToken()) != (JsonToken.END_OBJECT)) {
            String fieldname = jParser.getCurrentName();
            if ("age".equals(fieldname)) {
                jParser.nextToken();
                parsedAge = jParser.getIntValue();
                return;
            }
        } 
        jParser.close();
        // then
        Assert.assertNull(parsedName);
        TestCase.assertEquals(parsedAge, ((Integer) (25)));
        Assert.assertTrue(addresses.isEmpty());
    }
}

