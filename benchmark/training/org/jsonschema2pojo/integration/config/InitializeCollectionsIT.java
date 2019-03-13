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


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import org.hamcrest.Matcher;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class InitializeCollectionsIT {
    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    private Map<String, Object> config;

    private Matcher<Object> resultMatcher;

    private String getterName;

    public InitializeCollectionsIT(String label, Map<String, Object> config, String getterName, Matcher<Object> resultMatcher) {
        this.config = config;
        this.getterName = getterName;
        this.resultMatcher = resultMatcher;
    }

    @Test
    public void correctResult() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/properties/initializeCollectionProperties.json", "com.example", config);
        Class<?> generatedType = resultsClassLoader.loadClass("com.example.InitializeCollectionProperties");
        Object instance = generatedType.newInstance();
        Method getter = generatedType.getMethod(getterName);
        Assert.assertThat(getter.invoke(instance), resultMatcher);
    }
}

