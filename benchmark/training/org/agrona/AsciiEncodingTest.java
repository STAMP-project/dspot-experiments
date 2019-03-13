/**
 * Copyright 2014-2019 Real Logic Ltd.
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
package org.agrona;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class AsciiEncodingTest {
    @Test
    public void shouldParseInt() {
        Assert.assertThat(AsciiEncoding.parseIntAscii("0", 0, 1), CoreMatchers.is(0));
        Assert.assertThat(AsciiEncoding.parseIntAscii("-0", 0, 2), CoreMatchers.is(0));
        Assert.assertThat(AsciiEncoding.parseIntAscii("7", 0, 1), CoreMatchers.is(7));
        Assert.assertThat(AsciiEncoding.parseIntAscii("-7", 0, 2), CoreMatchers.is((-7)));
        Assert.assertThat(AsciiEncoding.parseIntAscii("3333", 1, 2), CoreMatchers.is(33));
    }

    @Test
    public void shouldParseLong() {
        Assert.assertThat(AsciiEncoding.parseLongAscii("0", 0, 1), CoreMatchers.is(0L));
        Assert.assertThat(AsciiEncoding.parseLongAscii("-0", 0, 2), CoreMatchers.is(0L));
        Assert.assertThat(AsciiEncoding.parseLongAscii("7", 0, 1), CoreMatchers.is(7L));
        Assert.assertThat(AsciiEncoding.parseLongAscii("-7", 0, 2), CoreMatchers.is((-7L)));
        Assert.assertThat(AsciiEncoding.parseLongAscii("3333", 1, 2), CoreMatchers.is(33L));
    }
}

