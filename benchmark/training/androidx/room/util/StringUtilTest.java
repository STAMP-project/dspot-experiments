/**
 * Copyright (C) 2016 The Android Open Source Project
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
package androidx.room.util;


import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
@RunWith(JUnit4.class)
public class StringUtilTest {
    @Test
    public void testEmpty() {
        MatcherAssert.assertThat(StringUtil.splitToIntList(""), CoreMatchers.is(Collections.<Integer>emptyList()));
        MatcherAssert.assertThat(StringUtil.joinIntoString(Collections.<Integer>emptyList()), CoreMatchers.is(""));
    }

    @Test
    public void testNull() {
        MatcherAssert.assertThat(StringUtil.splitToIntList(null), CoreMatchers.nullValue());
        MatcherAssert.assertThat(StringUtil.joinIntoString(null), CoreMatchers.nullValue());
    }

    @Test
    public void testSingle() {
        MatcherAssert.assertThat(StringUtil.splitToIntList("4"), CoreMatchers.is(Arrays.asList(4)));
        MatcherAssert.assertThat(StringUtil.joinIntoString(Arrays.asList(4)), CoreMatchers.is("4"));
    }

    @Test
    public void testMultiple() {
        MatcherAssert.assertThat(StringUtil.splitToIntList("4,5"), CoreMatchers.is(Arrays.asList(4, 5)));
        MatcherAssert.assertThat(StringUtil.joinIntoString(Arrays.asList(4, 5)), CoreMatchers.is("4,5"));
    }

    @Test
    public void testNegative() {
        MatcherAssert.assertThat(StringUtil.splitToIntList("-4,-5,6,-7"), CoreMatchers.is(Arrays.asList((-4), (-5), 6, (-7))));
        MatcherAssert.assertThat(StringUtil.joinIntoString(Arrays.asList((-4), (-5), 6, (-7))), CoreMatchers.is("-4,-5,6,-7"));
    }

    @Test
    public void ignoreMalformed() {
        MatcherAssert.assertThat(StringUtil.splitToIntList("-4,a,5,7"), CoreMatchers.is(Arrays.asList((-4), 5, 7)));
    }
}

