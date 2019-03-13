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
package com.hazelcast.query.impl;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CompositeConverterTest {
    @Test
    public void testTransient() {
        Assert.assertTrue(CompositeConverterTest.converter(TypeConverters.NULL_CONVERTER).isTransient());
        Assert.assertTrue(CompositeConverterTest.converter(TypeConverters.INTEGER_CONVERTER, TypeConverters.NULL_CONVERTER).isTransient());
        Assert.assertFalse(CompositeConverterTest.converter(TypeConverters.INTEGER_CONVERTER).isTransient());
        Assert.assertFalse(CompositeConverterTest.converter(TypeConverters.INTEGER_CONVERTER, TypeConverters.IDENTITY_CONVERTER).isTransient());
    }

    @Test
    public void testComponentConverters() {
        Assert.assertSame(TypeConverters.NULL_CONVERTER, CompositeConverterTest.converter(TypeConverters.NULL_CONVERTER).getComponentConverter(0));
        Assert.assertSame(TypeConverters.INTEGER_CONVERTER, CompositeConverterTest.converter(TypeConverters.INTEGER_CONVERTER).getComponentConverter(0));
        Assert.assertSame(TypeConverters.NULL_CONVERTER, CompositeConverterTest.converter(TypeConverters.INTEGER_CONVERTER, TypeConverters.NULL_CONVERTER).getComponentConverter(1));
        Assert.assertSame(TypeConverters.INTEGER_CONVERTER, CompositeConverterTest.converter(TypeConverters.INTEGER_CONVERTER, TypeConverters.NULL_CONVERTER).getComponentConverter(0));
        Assert.assertSame(TypeConverters.INTEGER_CONVERTER, CompositeConverterTest.converter(TypeConverters.INTEGER_CONVERTER, TypeConverters.NULL_CONVERTER, TypeConverters.LONG_CONVERTER).getComponentConverter(0));
        Assert.assertSame(TypeConverters.NULL_CONVERTER, CompositeConverterTest.converter(TypeConverters.INTEGER_CONVERTER, TypeConverters.NULL_CONVERTER, TypeConverters.LONG_CONVERTER).getComponentConverter(1));
        Assert.assertSame(TypeConverters.LONG_CONVERTER, CompositeConverterTest.converter(TypeConverters.INTEGER_CONVERTER, TypeConverters.NULL_CONVERTER, TypeConverters.LONG_CONVERTER).getComponentConverter(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConversionAcceptsOnlyCompositeValues() {
        CompositeConverterTest.converter(TypeConverters.STRING_CONVERTER).convert("value");
    }

    @Test
    public void testConversion() {
        Assert.assertEquals(CompositeConverterTest.value(1), CompositeConverterTest.converter(TypeConverters.INTEGER_CONVERTER).convert(CompositeConverterTest.value(1)));
        Assert.assertEquals(CompositeConverterTest.value(1), CompositeConverterTest.converter(TypeConverters.INTEGER_CONVERTER).convert(CompositeConverterTest.value("1")));
        Assert.assertEquals(CompositeConverterTest.value(1, true), CompositeConverterTest.converter(TypeConverters.INTEGER_CONVERTER, TypeConverters.BOOLEAN_CONVERTER).convert(CompositeConverterTest.value(1, true)));
        Assert.assertEquals(CompositeConverterTest.value(1, false), CompositeConverterTest.converter(TypeConverters.INTEGER_CONVERTER, TypeConverters.BOOLEAN_CONVERTER).convert(CompositeConverterTest.value(1.0, "non-true")));
        Assert.assertEquals(CompositeConverterTest.value(1, true, "foo"), CompositeConverterTest.converter(TypeConverters.INTEGER_CONVERTER, TypeConverters.BOOLEAN_CONVERTER, TypeConverters.STRING_CONVERTER).convert(CompositeConverterTest.value(1, true, "foo")));
        Assert.assertEquals(CompositeConverterTest.value(1, false, "1"), CompositeConverterTest.converter(TypeConverters.INTEGER_CONVERTER, TypeConverters.BOOLEAN_CONVERTER, TypeConverters.STRING_CONVERTER).convert(CompositeConverterTest.value(1.0, "non-true", 1)));
    }
}

