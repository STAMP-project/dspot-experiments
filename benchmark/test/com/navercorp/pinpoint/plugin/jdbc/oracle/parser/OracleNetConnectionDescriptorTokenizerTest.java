/**
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.plugin.jdbc.oracle.parser;


import org.junit.Test;


/**
 *
 *
 * @author emeroad
 */
public class OracleNetConnectionDescriptorTokenizerTest {
    @Test
    public void trimLeft() {
        assertTrimLeft("123", "123");
        assertTrimLeft(" 123", "123");
        assertTrimLeft("  123 ", "123 ");
        assertTrimLeft("  1 23 ", "1 23 ");
        assertTrimLeft("  1 23 ", "1 23 ");
        assertTrimLeft("", "");
        assertTrimLeft("  ", "");
        assertTrimLeft("12 ", "12 ");
        assertTrimLeft("12 ", "12 ");
    }

    @Test
    public void trimRight() {
        assertTrimRight("123", "123");
        assertTrimRight("123 ", "123");
        assertTrimRight("123    ", "123");
        assertTrimRight("1 23 ", "1 23");
        assertTrimRight("  1 23 ", "  1 23");
        assertTrimRight("", "");
        assertTrimRight("  ", "");
    }

    @Test
    public void parseLiteral() {
        AssertParseLiteral("abc", "abc");
        AssertParseLiteral(" abc", "abc");
        AssertParseLiteral("  abc", "abc");
        AssertParseLiteral("abc ", "abc");
        AssertParseLiteral("abc  ", "abc");
        AssertParseLiteral("a  c", "a  c");
        AssertParseLiteral(" a  c", "a  c");
        AssertParseLiteral("   a  c  ", "a  c");
    }

    @Test
    public void simpleParse() {
        assertCompareToken("a=b", "a", "=", "b");
        assertCompareToken("a = b", "a", "=", "b");
        assertCompareToken(" a = b ", "a", "=", "b");
    }
}

