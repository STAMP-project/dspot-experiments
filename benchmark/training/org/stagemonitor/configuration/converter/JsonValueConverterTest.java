package org.stagemonitor.configuration.converter;


import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class JsonValueConverterTest {
    private JsonValueConverter<Map<String, JsonValueConverterTest.TestObject>> jsonValueConverter = new JsonValueConverter<Map<String, JsonValueConverterTest.TestObject>>(new TypeReference<Map<String, JsonValueConverterTest.TestObject>>() {});

    @Test
    public void testConvert() throws Exception {
        Map<String, JsonValueConverterTest.TestObject> convert = jsonValueConverter.convert("{ \"1\": { \"test\": \"foobar\" } }");
        Assert.assertEquals("foobar", convert.get("1").getTest());
    }

    public static class TestObject {
        private String test;

        private String getTest() {
            return test;
        }

        private void setTest(String test) {
            this.test = test;
        }
    }
}

