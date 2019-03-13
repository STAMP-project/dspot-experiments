/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.test.junit.rules;


import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.geode.test.junit.runners.TestRunner;
import org.junit.Rule;
import org.junit.Test;


/**
 * Unit tests for {@link UseJacksonForJsonPathRule}.
 */
public class UseJacksonForJsonPathRuleTest {
    private static final AtomicReference<JsonProvider> jsonProviderRef = new AtomicReference<>();

    private static final AtomicReference<MappingProvider> mappingProviderRef = new AtomicReference<>();

    private JsonProvider defaultJsonProvider;

    private MappingProvider defaultMappingProvider;

    @Test
    public void setsJsonProviderToJacksonJsonProviderBeforeTest() {
        TestRunner.runTestWithValidation(UseJacksonForJsonPathRuleTest.CaptureJsonProvider.class);
        assertThat(UseJacksonForJsonPathRuleTest.jsonProviderRef.get()).isInstanceOf(JacksonJsonProvider.class);
    }

    @Test
    public void setsMappingProviderToJacksonMappingProviderBeforeTest() {
        TestRunner.runTestWithValidation(UseJacksonForJsonPathRuleTest.CaptureMappingProvider.class);
        assertThat(UseJacksonForJsonPathRuleTest.mappingProviderRef.get()).isInstanceOf(JacksonMappingProvider.class);
    }

    @Test
    public void restoresJsonProviderToDefaultAfterTest() {
        TestRunner.runTestWithValidation(UseJacksonForJsonPathRuleTest.HasUseJacksonForJsonPathRule.class);
        Configuration configuration = Configuration.defaultConfiguration();
        assertThat(configuration.jsonProvider()).isSameAs(defaultJsonProvider);
    }

    @Test
    public void restoresMappingProviderToDefaultAfterTest() {
        TestRunner.runTestWithValidation(UseJacksonForJsonPathRuleTest.HasUseJacksonForJsonPathRule.class);
        Configuration configuration = Configuration.defaultConfiguration();
        assertThat(configuration.mappingProvider()).isSameAs(defaultMappingProvider);
    }

    public static class HasUseJacksonForJsonPathRule {
        @Rule
        public UseJacksonForJsonPathRule useJacksonForJsonPathRule = new UseJacksonForJsonPathRule();

        @Test
        public void doNothing() {
            assertThat(useJacksonForJsonPathRule).isNotNull();
        }
    }

    public static class CaptureJsonProvider {
        @Rule
        public UseJacksonForJsonPathRule useJacksonForJsonPathRule = new UseJacksonForJsonPathRule();

        @Test
        public void captureJsonProvider() {
            Configuration configuration = Configuration.defaultConfiguration();
            UseJacksonForJsonPathRuleTest.jsonProviderRef.set(configuration.jsonProvider());
            assertThat(UseJacksonForJsonPathRuleTest.jsonProviderRef.get()).isNotNull();
        }
    }

    public static class CaptureMappingProvider {
        @Rule
        public UseJacksonForJsonPathRule useJacksonForJsonPathRule = new UseJacksonForJsonPathRule();

        @Test
        public void captureMappingProvider() {
            Configuration configuration = Configuration.defaultConfiguration();
            UseJacksonForJsonPathRuleTest.mappingProviderRef.set(configuration.mappingProvider());
            assertThat(UseJacksonForJsonPathRuleTest.mappingProviderRef.get()).isNotNull();
        }
    }
}

