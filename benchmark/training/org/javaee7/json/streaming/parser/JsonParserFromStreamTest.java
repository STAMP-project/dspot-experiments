package org.javaee7.json.streaming.parser;


import JsonParser.Event.END_ARRAY;
import JsonParser.Event.END_OBJECT;
import JsonParser.Event.KEY_NAME;
import JsonParser.Event.START_ARRAY;
import JsonParser.Event.START_OBJECT;
import JsonParser.Event.VALUE_NUMBER;
import JsonParser.Event.VALUE_STRING;
import javax.json.Json;
import javax.json.stream.JsonParser;
import org.jboss.arquillian.junit.Arquillian;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Arun Gupta
 */
@RunWith(Arquillian.class)
public class JsonParserFromStreamTest {
    @Test
    public void testEmptyObject() throws JSONException {
        JsonParser parser = Json.createParser(Thread.currentThread().getContextClassLoader().getResourceAsStream("/1.json"));
        Assert.assertEquals(START_OBJECT, parser.next());
        Assert.assertEquals(END_OBJECT, parser.next());
    }

    @Test
    public void testSimpleObject() throws JSONException {
        JsonParser parser = Json.createParser(Thread.currentThread().getContextClassLoader().getResourceAsStream("/2.json"));
        Assert.assertEquals(START_OBJECT, parser.next());
        Assert.assertEquals(KEY_NAME, parser.next());
        Assert.assertEquals(VALUE_STRING, parser.next());
        Assert.assertEquals(KEY_NAME, parser.next());
        Assert.assertEquals(VALUE_STRING, parser.next());
        Assert.assertEquals(END_OBJECT, parser.next());
    }

    @Test
    public void testArray() throws JSONException {
        JsonParser parser = Json.createParser(Thread.currentThread().getContextClassLoader().getResourceAsStream("/3.json"));
        Assert.assertEquals(START_ARRAY, parser.next());
        Assert.assertEquals(START_OBJECT, parser.next());
        Assert.assertEquals(KEY_NAME, parser.next());
        Assert.assertEquals(VALUE_STRING, parser.next());
        Assert.assertEquals(END_OBJECT, parser.next());
        Assert.assertEquals(START_OBJECT, parser.next());
        Assert.assertEquals(KEY_NAME, parser.next());
        Assert.assertEquals(VALUE_STRING, parser.next());
        Assert.assertEquals(END_OBJECT, parser.next());
        Assert.assertEquals(END_ARRAY, parser.next());
    }

    @Test
    public void testNestedStructure() throws JSONException {
        JsonParser parser = Json.createParser(Thread.currentThread().getContextClassLoader().getResourceAsStream("/4.json"));
        Assert.assertEquals(START_OBJECT, parser.next());
        Assert.assertEquals(KEY_NAME, parser.next());
        Assert.assertEquals(VALUE_STRING, parser.next());
        Assert.assertEquals(KEY_NAME, parser.next());
        Assert.assertEquals(VALUE_NUMBER, parser.next());
        Assert.assertEquals(KEY_NAME, parser.next());
        Assert.assertEquals(START_ARRAY, parser.next());
        Assert.assertEquals(VALUE_STRING, parser.next());
        Assert.assertEquals(VALUE_STRING, parser.next());
        Assert.assertEquals(VALUE_STRING, parser.next());
        Assert.assertEquals(END_ARRAY, parser.next());
        Assert.assertEquals(END_OBJECT, parser.next());
    }
}

