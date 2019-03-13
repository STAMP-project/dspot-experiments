/**
 * Copyright ? 2010-2017 Nokia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsonschema2pojo.integration;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class FormatIT {
    @ClassRule
    public static Jsonschema2PojoRule classSchemaRule = new Jsonschema2PojoRule();

    private static Class<?> classWithFormattedProperties;

    private String propertyName;

    private Class<?> expectedType;

    private Object jsonValue;

    private Object javaValue;

    public FormatIT(String propertyName, Class<?> expectedType, Object jsonValue, Object javaValue) {
        this.propertyName = propertyName;
        this.expectedType = expectedType;
        this.jsonValue = jsonValue;
        this.javaValue = javaValue;
    }

    @Test
    public void formatValueProducesExpectedType() throws IntrospectionException {
        Method getter = new PropertyDescriptor(propertyName, FormatIT.classWithFormattedProperties).getReadMethod();
        Assert.assertThat(getter.getReturnType().getName(), is(this.expectedType.getName()));
    }

    @Test
    public void valueCanBeSerializedAndDeserialized() throws IntrospectionException, IOException, IllegalAccessException, InvocationTargetException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode node = objectMapper.createObjectNode();
        node.put(propertyName, jsonValue.toString());
        Object pojo = objectMapper.treeToValue(node, FormatIT.classWithFormattedProperties);
        Method getter = new PropertyDescriptor(propertyName, FormatIT.classWithFormattedProperties).getReadMethod();
        Assert.assertThat(getter.invoke(pojo).toString(), is(equalTo(javaValue.toString())));
        JsonNode jsonVersion = objectMapper.valueToTree(pojo);
        Assert.assertThat(jsonVersion.get(propertyName).asText(), is(equalTo(jsonValue.toString())));
    }
}

