/**
 * Copyright 2015-2019 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package springfox.documentation.swagger2.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.DecimalProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.StringProperty;
import org.junit.Assert;
import org.junit.Test;


public class PropertyExampleSerializerTest {
    private static final ObjectMapper SUT = new ObjectMapper();

    @Test
    public void serializePropertyExampleBoolean() throws Exception {
        BooleanProperty booleanPropertyTrue = new BooleanProperty();
        booleanPropertyTrue.setExample("true");
        BooleanProperty booleanPropertyFalse = new BooleanProperty();
        booleanPropertyFalse.setExample("false");
        String serializedTrue = PropertyExampleSerializerTest.SUT.writeValueAsString(booleanPropertyTrue);
        Assert.assertTrue(serializedTrue.contains("\"example\":true"));
        String serializedFalse = PropertyExampleSerializerTest.SUT.writeValueAsString(booleanPropertyFalse);
        Assert.assertTrue(serializedFalse.contains("\"example\":false"));
    }

    @Test
    public void serializePropertyExampleNumber() throws Exception {
        DecimalProperty decimalProperty = new DecimalProperty();
        decimalProperty.setExample("-0.42");
        String serialized = PropertyExampleSerializerTest.SUT.writeValueAsString(decimalProperty);
        Assert.assertTrue(serialized.contains("\"example\":-0.42"));
    }

    @Test
    public void serializePropertyExampleArray() throws Exception {
        ArrayProperty arrayProperty = new ArrayProperty();
        arrayProperty.setExample("[42, \"foo\", true]");
        String serialized = PropertyExampleSerializerTest.SUT.writeValueAsString(arrayProperty);
        Assert.assertTrue(serialized.contains("[42, \"foo\", true]"));
    }

    @Test
    public void serializePropertyExampleObject() throws Exception {
        MapProperty mapProperty = new MapProperty();
        mapProperty.setExample("{\"0\": {\"name\": \"foo\", \"age\": 42}, \"1\": {\"name\": \"bar\", \"valid\": true}}");
        String serialized = PropertyExampleSerializerTest.SUT.writeValueAsString(mapProperty);
        Assert.assertTrue(serialized.contains("\"example\":{\"0\": {\"name\": \"foo\", \"age\": 42}, \"1\": {\"name\": \"bar\", \"valid\": true}}"));
    }

    @Test
    public void serializePropertyExampleString() throws Exception {
        StringProperty stringProperty = new StringProperty();
        stringProperty.setExample("2017-07-30");
        String serialized = PropertyExampleSerializerTest.SUT.writeValueAsString(stringProperty);
        Assert.assertTrue(serialized.contains("\"example\":\"2017-07-30\""));
    }

    @Test
    public void serializePropertyExampleNull() throws Exception {
        StringProperty stringProperty = new StringProperty();
        String serialized = PropertyExampleSerializerTest.SUT.writeValueAsString(stringProperty);
        Assert.assertFalse(serialized.contains("\"example\":"));
    }

    @Test
    public void serializePropertyExampleEmpty() throws Exception {
        StringProperty stringProperty = new StringProperty();
        stringProperty.setExample("   ");
        String serialized = PropertyExampleSerializerTest.SUT.writeValueAsString(stringProperty);
        Assert.assertFalse(serialized.contains("\"example\":"));
    }
}

