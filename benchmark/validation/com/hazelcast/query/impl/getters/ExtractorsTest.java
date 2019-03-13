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


import com.hazelcast.config.MapAttributeConfig;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.query.extractor.ValueCollector;
import com.hazelcast.query.extractor.ValueExtractor;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.annotation.QuickTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category(QuickTest.class)
@SuppressWarnings("unused")
public class ExtractorsTest {
    @Parameterized.Parameter
    public boolean useClassloader;

    private ExtractorsTest.Bond bond = new ExtractorsTest.Bond();

    private InternalSerializationService ss;

    @Test
    public void getGetter_reflection_cachingWorks() {
        // GIVEN
        Extractors extractors = createExtractors(null);
        // WHEN
        Getter getterFirstInvocation = extractors.getGetter(bond, "car.power");
        Getter getterSecondInvocation = extractors.getGetter(bond, "car.power");
        // THEN
        MatcherAssert.assertThat(getterFirstInvocation, Matchers.sameInstance(getterSecondInvocation));
        MatcherAssert.assertThat(getterFirstInvocation, Matchers.instanceOf(FieldGetter.class));
    }

    @Test
    public void extract_reflection_correctValue() {
        // WHEN
        Object power = createExtractors(null).extract(bond, "car.power", null);
        // THEN
        MatcherAssert.assertThat(((Integer) (power)), Matchers.equalTo(550));
    }

    @Test
    public void getGetter_extractor_cachingWorks() {
        // GIVEN
        MapAttributeConfig config = new MapAttributeConfig("gimmePower", "com.hazelcast.query.impl.getters.ExtractorsTest$PowerExtractor");
        Extractors extractors = createExtractors(config);
        // WHEN
        Getter getterFirstInvocation = extractors.getGetter(bond, "gimmePower");
        Getter getterSecondInvocation = extractors.getGetter(bond, "gimmePower");
        // THEN
        MatcherAssert.assertThat(getterFirstInvocation, Matchers.sameInstance(getterSecondInvocation));
        MatcherAssert.assertThat(getterFirstInvocation, Matchers.instanceOf(ExtractorGetter.class));
    }

    @Test
    public void extract_extractor_correctValue() {
        // GIVEN
        MapAttributeConfig config = new MapAttributeConfig("gimmePower", "com.hazelcast.query.impl.getters.ExtractorsTest$PowerExtractor");
        Extractors extractors = createExtractors(config);
        // WHEN
        Object power = extractors.extract(bond, "gimmePower", null);
        // THEN
        MatcherAssert.assertThat(((Integer) (power)), Matchers.equalTo(550));
    }

    @Test
    public void extract_nullTarget() {
        // WHEN
        Object power = createExtractors(null).extract(null, "gimmePower", null);
        // THEN
        Assert.assertNull(power);
    }

    @Test
    public void extract_nullAll() {
        // WHEN
        Object power = createExtractors(null).extract(null, null, null);
        // THEN
        Assert.assertNull(power);
    }

    @Test(expected = NullPointerException.class)
    public void extract_nullAttribute() {
        createExtractors(null).extract(bond, null, null);
    }

    private static class Bond {
        ExtractorsTest.Car car = new ExtractorsTest.Car();
    }

    private static class Car {
        int power = 550;
    }

    public static class PowerExtractor extends ValueExtractor<ExtractorsTest.Bond, Object> {
        @Override
        public void extract(ExtractorsTest.Bond target, Object arguments, ValueCollector collector) {
            collector.addObject(target.car.power);
        }
    }
}

