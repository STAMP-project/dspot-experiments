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
package org.jsonschema2pojo.integration.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.Modifier;
import org.jsonschema2pojo.integration.util.CodeGenerationHelper;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class IncludeAccessorsIT {
    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    @Test
    public void beansIncludeGettersAndSettersByDefault() throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example");
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        // throws NoSuchMethodException if method is not found
        generatedType.getDeclaredMethod("getA");
        generatedType.getDeclaredMethod("setA", Integer.class);
        Assert.assertThat(generatedType.getDeclaredField("a").getModifiers(), is(Modifier.PRIVATE));
    }

    @Test
    public void beansOmitGettersAndSettersWhenAccessorsAreDisabled() throws ClassNotFoundException, NoSuchFieldException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("includeGetters", false, "includeSetters", false));
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        try {
            generatedType.getDeclaredMethod("getA");
            Assert.fail("Disabled accessors but getter was generated");
        } catch (NoSuchMethodException e) {
        }
        try {
            generatedType.getDeclaredMethod("setA", Integer.class);
            Assert.fail("Disabled accessors but setter was generated");
        } catch (NoSuchMethodException e) {
        }
        Assert.assertThat(generatedType.getDeclaredField("a").getModifiers(), is(Modifier.PUBLIC));
    }

    @Test
    public void beansWithoutAccessorsRoundTripJsonCorrectly() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", CodeGenerationHelper.config("includeGetters", false, "includeSetters", false));
        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");
        Object instance = generatedType.newInstance();
        generatedType.getDeclaredField("a").set(instance, 12);
        generatedType.getDeclaredField("b").set(instance, 1.12);
        generatedType.getDeclaredField("c").set(instance, true);
        ObjectMapper objectMapper = new ObjectMapper();
        String instanceAsJson = objectMapper.writeValueAsString(instance);
        Object instanceAfterRoundTrip = objectMapper.readValue(instanceAsJson, generatedType);
        Assert.assertThat(instanceAfterRoundTrip, is(equalTo(instance)));
    }
}

