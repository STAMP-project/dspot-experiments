/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.query.impl.getters;


import com.hazelcast.config.Config;
import com.hazelcast.config.MapAttributeConfig;
import com.hazelcast.query.extractor.ValueCollector;
import com.hazelcast.query.extractor.ValueExtractor;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.annotation.QuickTest;
import groovy.util.GroovyTestCase;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category(QuickTest.class)
@SuppressWarnings("unused")
public class ExtractorHelperTest {
    @Parameterized.Parameter
    public boolean useClassloader;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void instantiate_extractor() {
        // GIVEN
        MapAttributeConfig config = new MapAttributeConfig("iq", "com.hazelcast.query.impl.getters.ExtractorHelperTest$IqExtractor");
        // WHEN
        ValueExtractor extractor = instantiateExtractor(config);
        // THEN
        MatcherAssert.assertThat(extractor, Matchers.instanceOf(ExtractorHelperTest.IqExtractor.class));
    }

    @Test
    public void instantiate_extractor_notExistingClass() {
        // GIVEN
        MapAttributeConfig config = new MapAttributeConfig("iq", "not.existing.class");
        // EXPECT
        expected.expect(IllegalArgumentException.class);
        expected.expectCause(Matchers.isA(ClassNotFoundException.class));
        // WHEN
        instantiateExtractor(config);
    }

    @Test
    public void instantiate_extractors() {
        // GIVEN
        MapAttributeConfig iqExtractor = new MapAttributeConfig("iq", "com.hazelcast.query.impl.getters.ExtractorHelperTest$IqExtractor");
        MapAttributeConfig nameExtractor = new MapAttributeConfig("name", "com.hazelcast.query.impl.getters.ExtractorHelperTest$NameExtractor");
        // WHEN
        Map<String, ValueExtractor> extractors = instantiateExtractors(Arrays.asList(iqExtractor, nameExtractor));
        // THEN
        MatcherAssert.assertThat(extractors.get("iq"), Matchers.instanceOf(ExtractorHelperTest.IqExtractor.class));
        MatcherAssert.assertThat(extractors.get("name"), Matchers.instanceOf(ExtractorHelperTest.NameExtractor.class));
    }

    @Test
    public void instantiate_extractors_withCustomClassLoader() {
        // GIVEN
        MapAttributeConfig iqExtractor = new MapAttributeConfig("iq", "com.hazelcast.query.impl.getters.ExtractorHelperTest$IqExtractor");
        MapAttributeConfig nameExtractor = new MapAttributeConfig("name", "com.hazelcast.query.impl.getters.ExtractorHelperTest$NameExtractor");
        Config config = new Config();
        // For other custom class loaders (from OSGi bundles, for example)
        ClassLoader customClassLoader = getClass().getClassLoader();
        config.setClassLoader(customClassLoader);
        // WHEN
        Map<String, ValueExtractor> extractors = instantiateExtractors(Arrays.asList(iqExtractor, nameExtractor));
        // THEN
        MatcherAssert.assertThat(extractors.get("iq"), Matchers.instanceOf(ExtractorHelperTest.IqExtractor.class));
        MatcherAssert.assertThat(extractors.get("name"), Matchers.instanceOf(ExtractorHelperTest.NameExtractor.class));
    }

    @Test
    public void instantiate_extractors_oneClassNotExisting() {
        // GIVEN
        MapAttributeConfig iqExtractor = new MapAttributeConfig("iq", "com.hazelcast.query.impl.getters.ExtractorHelperTest$IqExtractor");
        MapAttributeConfig nameExtractor = new MapAttributeConfig("name", "not.existing.class");
        // EXPECT
        expected.expect(IllegalArgumentException.class);
        expected.expectCause(Matchers.isA(ClassNotFoundException.class));
        // WHEN
        instantiateExtractors(Arrays.asList(iqExtractor, nameExtractor));
    }

    @Test
    public void instantiate_extractors_duplicateExtractor() {
        // GIVEN
        MapAttributeConfig iqExtractor = new MapAttributeConfig("iq", "com.hazelcast.query.impl.getters.ExtractorHelperTest$IqExtractor");
        MapAttributeConfig iqExtractorDuplicate = new MapAttributeConfig("iq", "com.hazelcast.query.impl.getters.ExtractorHelperTest$IqExtractor");
        // EXPECT
        expected.expect(IllegalArgumentException.class);
        // WHEN
        instantiateExtractors(Arrays.asList(iqExtractor, iqExtractorDuplicate));
    }

    @Test
    public void instantiate_extractors_wrongType() {
        // GIVEN
        MapAttributeConfig string = new MapAttributeConfig("iq", "java.lang.String");
        // EXPECT
        expected.expect(IllegalArgumentException.class);
        // WHEN
        instantiateExtractors(Collections.singletonList(string));
    }

    @Test
    public void instantiate_extractors_initException() {
        // GIVEN
        MapAttributeConfig string = new MapAttributeConfig("iq", "com.hazelcast.query.impl.getters.ExtractorHelperTest$InitExceptionExtractor");
        // EXPECT
        expected.expect(IllegalArgumentException.class);
        // WHEN
        instantiateExtractors(Collections.singletonList(string));
    }

    @Test
    public void instantiate_extractors_accessException() {
        // GIVEN
        MapAttributeConfig string = new MapAttributeConfig("iq", "com.hazelcast.query.impl.getters.ExtractorHelperTest$AccessExceptionExtractor");
        // EXPECT
        expected.expect(IllegalArgumentException.class);
        // WHEN
        instantiateExtractors(Collections.singletonList(string));
    }

    @Test
    public void extractArgument_correctArguments() {
        GroovyTestCase.assertEquals("left-front", ExtractorHelper.extractArgumentsFromAttributeName("car.wheel[left-front]"));
        GroovyTestCase.assertEquals("123", ExtractorHelper.extractArgumentsFromAttributeName("car.wheel[123]"));
        GroovyTestCase.assertEquals(".';'.", ExtractorHelper.extractArgumentsFromAttributeName("car.wheel[.';'.]"));
        GroovyTestCase.assertEquals("", ExtractorHelper.extractArgumentsFromAttributeName("car.wheel[]"));
        Assert.assertNull(ExtractorHelper.extractArgumentsFromAttributeName("car.wheel"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void extractArgument_wrongArguments_noClosing() {
        ExtractorHelper.extractArgumentsFromAttributeName("car.wheel[left");
    }

    @Test(expected = IllegalArgumentException.class)
    public void extractArgument_wrongArguments_noArgument() {
        ExtractorHelper.extractArgumentsFromAttributeName("car.wheel[");
    }

    @Test(expected = IllegalArgumentException.class)
    public void extractArgument_wrongArguments_noOpening() {
        ExtractorHelper.extractArgumentsFromAttributeName("car.wheelleft]");
    }

    @Test(expected = IllegalArgumentException.class)
    public void extractArgument_wrongArguments_noArgument_noOpening() {
        ExtractorHelper.extractArgumentsFromAttributeName("car.wheel]");
    }

    @Test
    public void extractArgument_wrongArguments_tooManySquareBrackets_lastExtracted() {
        GroovyTestCase.assertEquals("BAR", ExtractorHelper.extractArgumentsFromAttributeName("car.wheel[2].pressure[BAR]"));
    }

    @Test
    public void extractAttributeName_correctArguments() {
        GroovyTestCase.assertEquals("car.wheel", ExtractorHelper.extractAttributeNameNameWithoutArguments("car.wheel[left-front]"));
        GroovyTestCase.assertEquals("car.wheel", ExtractorHelper.extractAttributeNameNameWithoutArguments("car.wheel[123]"));
        GroovyTestCase.assertEquals("car.wheel", ExtractorHelper.extractAttributeNameNameWithoutArguments("car.wheel[.';'.]"));
        GroovyTestCase.assertEquals("car.wheel", ExtractorHelper.extractAttributeNameNameWithoutArguments("car.wheel[]"));
        GroovyTestCase.assertEquals("car.wheel", ExtractorHelper.extractAttributeNameNameWithoutArguments("car.wheel"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void extractAttributeName_wrongArguments_noClosing() {
        ExtractorHelper.extractAttributeNameNameWithoutArguments("car.wheel[left");
    }

    @Test(expected = IllegalArgumentException.class)
    public void extractAttributeName_wrongArguments_noArgument() {
        ExtractorHelper.extractAttributeNameNameWithoutArguments("car.wheel[");
    }

    @Test(expected = IllegalArgumentException.class)
    public void extractAttributeName_wrongArguments_noOpening() {
        ExtractorHelper.extractAttributeNameNameWithoutArguments("car.wheelleft]");
    }

    @Test(expected = IllegalArgumentException.class)
    public void extractAttributeName_wrongArguments_noArgument_noOpening() {
        ExtractorHelper.extractAttributeNameNameWithoutArguments("car.wheel]");
    }

    @Test
    public void extractAttributeName_wrongArguments_tooManySquareBrackets_lastExtracted() {
        GroovyTestCase.assertEquals("car.wheel[2].pressure", ExtractorHelper.extractAttributeNameNameWithoutArguments("car.wheel[2].pressure[BAR]"));
    }

    public abstract class InitExceptionExtractor extends ExtractorHelperTest.NameExtractor {}

    public static final class IqExtractor extends ValueExtractor<Object, Object> {
        @Override
        public void extract(Object target, Object arguments, ValueCollector collector) {
        }
    }

    public static final class AccessExceptionExtractor extends ExtractorHelperTest.NameExtractor {
        private AccessExceptionExtractor() {
        }
    }

    public static class NameExtractor extends ValueExtractor<Object, Object> {
        @Override
        public void extract(Object target, Object arguments, ValueCollector collector) {
        }
    }
}

