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


import java.util.Map;
import org.hamcrest.Matchers;
import org.jsonschema2pojo.integration.util.CodeGenerationHelper;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class DynamicPropertiesIT {
    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    @Test
    public void shouldSetStringField() throws Throwable {
        setDeclaredPropertyTest("/schema/dynamic/parentType.json", "ParentType", String.class, "stringValue", "getStringValue", "value");
    }

    @Test
    public void shouldSetStringFieldJava7() throws Throwable {
        setDeclaredPropertyTest(CodeGenerationHelper.config("includeDynamicAccessors", true, "includeDynamicGetters", true, "includeDynamicSetters", true, "includeDynamicBuilders", true, "targetVersion", "1.7"), "/schema/dynamic/parentType.json", "ParentType", String.class, "stringValue", "getStringValue", "value");
    }

    @Test
    public void shouldSetNumericField() throws Throwable {
        setDeclaredPropertyTest("/schema/dynamic/parentType.json", "ParentType", Double.class, "numberValue", "getNumberValue", 1.0);
    }

    @Test
    public void shouldSetIntegerField() throws Throwable {
        setDeclaredPropertyTest("/schema/dynamic/parentType.json", "ParentType", Integer.class, "integerValue", "getIntegerValue", 1);
    }

    @Test
    public void shouldSetStringFieldOnParent() throws Throwable {
        setDeclaredPropertyTest("/schema/dynamic/childType.json", "ChildType", String.class, "stringValue", "getStringValue", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenSettingWrongType() throws Throwable {
        setDeclaredPropertyTest("/schema/dynamic/childType.json", "ChildType", String.class, "stringValue", "getStringValue", 1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenSettingUnknownField() throws Throwable {
        setPropertyTest("/schema/dynamic/noAdditionalProperties.json", "NoAdditionalProperties", "unknownField", 1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenGettingUnknownField() throws Throwable {
        getPropertyTest("/schema/dynamic/noAdditionalProperties.json", "NoAdditionalProperties", "unknownField");
    }

    @Test
    public void shouldGetStringField() throws Throwable {
        getDeclaredPropertyTest("/schema/dynamic/parentType.json", "ParentType", String.class, "stringValue", "setStringValue", "value");
    }

    @Test
    public void shouldGetNumericField() throws Throwable {
        getDeclaredPropertyTest("/schema/dynamic/parentType.json", "ParentType", Double.class, "numberValue", "setNumberValue", 1.0);
    }

    @Test
    public void shouldGetIntegerField() throws Throwable {
        getDeclaredPropertyTest("/schema/dynamic/parentType.json", "ParentType", Integer.class, "integerValue", "setIntegerValue", 1);
    }

    @Test
    public void shouldGetStringFieldOnParent() throws Throwable {
        getDeclaredPropertyTest("/schema/dynamic/childType.json", "ChildType", String.class, "stringValue", "setStringValue", "value");
    }

    @Test
    public void shouldBuildStringField() throws Throwable {
        withDeclaredPropertyTest("/schema/dynamic/parentType.json", "ParentType", String.class, "stringValue", "getStringValue", "value");
    }

    @Test
    public void shouldSetAdditionalProperty() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/dynamic/parentType.json", "com.example", CodeGenerationHelper.config("includeDynamicAccessors", true, "includeDynamicGetters", true, "includeDynamicSetters", true, "includeDynamicBuilders", true));
        Class<?> parentType = resultsClassLoader.loadClass("com.example.ParentType");
        Object instance = parentType.newInstance();
        @SuppressWarnings("unchecked")
        Map<String, Object> additionalProperties = ((Map<String, Object>) (parentType.getMethod("getAdditionalProperties").invoke(instance)));
        parentType.getMethod("set", String.class, Object.class).invoke(instance, "unknownValue", "value");
        Assert.assertThat("the string value was set", ((String) (additionalProperties.get("unknownValue"))), Matchers.equalTo("value"));
    }

    @Test
    public void shouldGetAdditionalProperty() throws Exception {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/dynamic/parentType.json", "com.example", CodeGenerationHelper.config("includeDynamicAccessors", true, "includeDynamicGetters", true, "includeDynamicSetters", true, "includeDynamicBuilders", true));
        Class<?> parentType = resultsClassLoader.loadClass("com.example.ParentType");
        Object instance = parentType.newInstance();
        @SuppressWarnings("unchecked")
        Map<String, Object> additionalProperties = ((Map<String, Object>) (parentType.getMethod("getAdditionalProperties").invoke(instance)));
        additionalProperties.put("unknownValue", "value");
        parentType.getMethod("set", String.class, Object.class).invoke(instance, "unknownValue", "value");
        Assert.assertThat("the string value was set", ((String) (parentType.getMethod("get", String.class).invoke(instance, "unknownValue"))), Matchers.equalTo("value"));
    }
}

