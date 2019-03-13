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


import java.lang.reflect.Modifier;
import java.util.Map;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Checks general properties of includeAccessors and different configurations.
 *
 * @author Christian Trimble
 */
@SuppressWarnings({ "rawtypes" })
@RunWith(Parameterized.class)
public class IncludeAccessorsPropertiesIT {
    public static final String PACKAGE = "com.example";

    public static final String PRIMITIVE_JSON = "/schema/properties/primitiveProperties.json";

    public static final String PRIMITIVE_TYPE = "com.example.PrimitiveProperties";

    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    private String path;

    private String typeName;

    private Map<String, Object> includeAccessorsFalse;

    private Map<String, Object> includeAccessorsTrue;

    public IncludeAccessorsPropertiesIT(String path, String typeName, Map<String, Object> config) {
        this.path = path;
        this.typeName = typeName;
        this.includeAccessorsFalse = IncludeAccessorsPropertiesIT.configWithIncludeAccessors(config, false);
        this.includeAccessorsTrue = IncludeAccessorsPropertiesIT.configWithIncludeAccessors(config, true);
    }

    @Test
    public void noGettersOrSettersWhenFalse() throws ClassNotFoundException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile(path, IncludeAccessorsPropertiesIT.PACKAGE, includeAccessorsFalse);
        Class generatedType = resultsClassLoader.loadClass(typeName);
        Assert.assertThat("getters and setters should not exist", generatedType.getDeclaredMethods(), IncludeAccessorsPropertiesIT.everyItemInArray(anyOf(IncludeAccessorsPropertiesIT.methodWhitelist(), not(IncludeAccessorsPropertiesIT.fieldGetterOrSetter()))));
    }

    @Test
    public void hasGettersOrSettersWhenTrue() throws ClassNotFoundException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile(path, IncludeAccessorsPropertiesIT.PACKAGE, includeAccessorsTrue);
        Class generatedType = resultsClassLoader.loadClass(typeName);
        Assert.assertThat("a getter or setter should be found.", generatedType.getDeclaredMethods(), hasItemInArray(allOf(not(IncludeAccessorsPropertiesIT.methodWhitelist()), IncludeAccessorsPropertiesIT.fieldGetterOrSetter())));
    }

    @Test
    public void onlyHasPublicInstanceFieldsWhenFalse() throws ClassNotFoundException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile(path, IncludeAccessorsPropertiesIT.PACKAGE, includeAccessorsFalse);
        Class generatedType = resultsClassLoader.loadClass(typeName);
        Assert.assertThat("only public instance fields exist", generatedType.getDeclaredFields(), IncludeAccessorsPropertiesIT.everyItemInArray(anyOf(IncludeAccessorsPropertiesIT.hasModifiers(Modifier.STATIC), IncludeAccessorsPropertiesIT.fieldWhitelist(), IncludeAccessorsPropertiesIT.hasModifiers(Modifier.PUBLIC))));
    }

    @Test
    public void noPublicInstanceFieldsWhenTrue() throws ClassNotFoundException, SecurityException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile(path, IncludeAccessorsPropertiesIT.PACKAGE, includeAccessorsTrue);
        Class generatedType = resultsClassLoader.loadClass(typeName);
        Assert.assertThat("only public instance fields exist", generatedType.getDeclaredFields(), IncludeAccessorsPropertiesIT.everyItemInArray(anyOf(not(IncludeAccessorsPropertiesIT.hasModifiers(Modifier.PUBLIC)), IncludeAccessorsPropertiesIT.fieldWhitelist())));
    }
}

