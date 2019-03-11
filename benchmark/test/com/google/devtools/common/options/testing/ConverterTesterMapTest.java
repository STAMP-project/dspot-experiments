/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.common.options.testing;


import ConverterTesterMap.Builder;
import Converters.BooleanConverter;
import Converters.DoubleConverter;
import Converters.IntegerConverter;
import Converters.StringConverter;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for the ConverterTesterMap map builder.
 */
@RunWith(JUnit4.class)
public final class ConverterTesterMapTest {
    @Test
    public void add_mapsTestedConverterClassToTester() throws Exception {
        ConverterTester stringTester = new ConverterTester(StringConverter.class);
        ConverterTester intTester = new ConverterTester(IntegerConverter.class);
        ConverterTester doubleTester = new ConverterTester(DoubleConverter.class);
        ConverterTester booleanTester = new ConverterTester(BooleanConverter.class);
        ConverterTesterMap map = new ConverterTesterMap.Builder().add(stringTester).add(intTester).add(doubleTester).add(booleanTester).build();
        assertThat(map).containsExactly(StringConverter.class, stringTester, IntegerConverter.class, intTester, DoubleConverter.class, doubleTester, BooleanConverter.class, booleanTester);
    }

    @Test
    public void addAll_mapsTestedConverterClassesToTester() throws Exception {
        ConverterTester stringTester = new ConverterTester(StringConverter.class);
        ConverterTester intTester = new ConverterTester(IntegerConverter.class);
        ConverterTester doubleTester = new ConverterTester(DoubleConverter.class);
        ConverterTester booleanTester = new ConverterTester(BooleanConverter.class);
        ConverterTesterMap map = new ConverterTesterMap.Builder().addAll(ImmutableList.of(stringTester, intTester, doubleTester, booleanTester)).build();
        assertThat(map).containsExactly(StringConverter.class, stringTester, IntegerConverter.class, intTester, DoubleConverter.class, doubleTester, BooleanConverter.class, booleanTester);
    }

    @Test
    public void addAll_dumpsConverterTesterMapIntoNewMap() throws Exception {
        ConverterTester stringTester = new ConverterTester(StringConverter.class);
        ConverterTester intTester = new ConverterTester(IntegerConverter.class);
        ConverterTester doubleTester = new ConverterTester(DoubleConverter.class);
        ConverterTester booleanTester = new ConverterTester(BooleanConverter.class);
        ConverterTesterMap baseMap = new ConverterTesterMap.Builder().addAll(ImmutableList.of(stringTester, intTester, doubleTester)).build();
        ConverterTesterMap map = new ConverterTesterMap.Builder().addAll(baseMap).add(booleanTester).build();
        assertThat(map).containsExactly(StringConverter.class, stringTester, IntegerConverter.class, intTester, DoubleConverter.class, doubleTester, BooleanConverter.class, booleanTester);
    }

    @Test
    public void build_forbidsDuplicates() throws Exception {
        ConverterTesterMap.Builder builder = new ConverterTesterMap.Builder().add(new ConverterTester(StringConverter.class)).add(new ConverterTester(IntegerConverter.class)).add(new ConverterTester(DoubleConverter.class)).add(new ConverterTester(BooleanConverter.class)).add(new ConverterTester(BooleanConverter.class));
        try {
            builder.build();
            Assert.fail("expected build() with duplicate to fail");
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessageThat().contains(BooleanConverter.class.getSimpleName());
        }
    }
}

