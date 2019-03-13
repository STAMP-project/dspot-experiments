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


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.Gson;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import org.jsonschema2pojo.integration.util.CodeGenerationHelper;
import org.jsonschema2pojo.integration.util.FileSearchMatcher;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class GsonIT {
    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void annotationStyleGsonProducesGsonAnnotations() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        Class generatedType = schemaRule.generateAndCompile("/json/examples/torrent.json", "com.example", CodeGenerationHelper.config("annotationStyle", "gson", "propertyWordDelimiters", "_", "sourceType", "json")).loadClass("com.example.Torrent");
        Assert.assertThat(schemaRule.getGenerateDir(), not(FileSearchMatcher.containsText("org.codehaus.jackson")));
        Assert.assertThat(schemaRule.getGenerateDir(), not(FileSearchMatcher.containsText("com.fasterxml.jackson")));
        Assert.assertThat(schemaRule.getGenerateDir(), FileSearchMatcher.containsText("com.google.gson"));
        Assert.assertThat(schemaRule.getGenerateDir(), FileSearchMatcher.containsText("@SerializedName"));
        Method getter = generatedType.getMethod("getBuild");
        Assert.assertThat(generatedType.getAnnotation(JsonPropertyOrder.class), is(nullValue()));
        Assert.assertThat(generatedType.getAnnotation(JsonInclude.class), is(nullValue()));
        Assert.assertThat(getter.getAnnotation(JsonProperty.class), is(nullValue()));
    }

    @Test
    public void annotationStyleGsonMakesTypesThatWorkWithGson() throws IOException, ClassNotFoundException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/json/examples/", "com.example", CodeGenerationHelper.config("annotationStyle", "gson", "propertyWordDelimiters", "_", "sourceType", "json", "useLongIntegers", true));
        assertJsonRoundTrip(resultsClassLoader, "com.example.Torrent", "/json/examples/torrent.json");
        assertJsonRoundTrip(resultsClassLoader, "com.example.GetUserData", "/json/examples/GetUserData.json");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void enumValuesAreSerializedCorrectly() throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InstantiationException, NoSuchMethodException, SecurityException, InvocationTargetException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/enum/typeWithEnumProperty.json", "com.example", CodeGenerationHelper.config("annotationStyle", "gson", "propertyWordDelimiters", "_"));
        Class generatedType = resultsClassLoader.loadClass("com.example.TypeWithEnumProperty");
        Class enumType = resultsClassLoader.loadClass("com.example.TypeWithEnumProperty$EnumProperty");
        Object instance = generatedType.newInstance();
        Method setter = generatedType.getMethod("setEnumProperty", enumType);
        setter.invoke(instance, enumType.getEnumConstants()[3]);
        String json = new Gson().toJson(instance);
        Map<String, String> jsonAsMap = new Gson().fromJson(json, Map.class);
        Assert.assertThat(jsonAsMap.get("enum_Property"), is("4 ! 1"));
    }
}

