/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.sql;


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public class IdentifiersTest {
    @Test
    public void testEscape() throws Exception {
        MatcherAssert.assertThat(Identifiers.escape(""), Is.is(""));
        MatcherAssert.assertThat(Identifiers.escape("\""), Is.is("\"\""));
        MatcherAssert.assertThat(Identifiers.escape("ABC\""), Is.is("ABC\"\""));
        MatcherAssert.assertThat(Identifiers.escape("\"\ud83d\udca9"), Is.is("\"\"\ud83d\udca9"));
        MatcherAssert.assertThat(Identifiers.escape("abcDEF"), Is.is("abcDEF"));
        MatcherAssert.assertThat(Identifiers.escape("?"), Is.is("?"));
    }

    @Test
    public void testQuote() throws Exception {
        MatcherAssert.assertThat(Identifiers.quote(""), Is.is("\"\""));
        MatcherAssert.assertThat(Identifiers.quote("\""), Is.is("\"\"\"\""));
        MatcherAssert.assertThat(Identifiers.quote("ABC"), Is.is("\"ABC\""));
        MatcherAssert.assertThat(Identifiers.quote("\ud83d\udca9"), Is.is("\"\ud83d\udca9\""));
    }

    @Test
    public void testQuoteIfNeeded() throws Exception {
        MatcherAssert.assertThat(Identifiers.quoteIfNeeded(""), Is.is(""));
        MatcherAssert.assertThat(Identifiers.quoteIfNeeded("\""), Is.is("\"\"\"\""));
        MatcherAssert.assertThat(Identifiers.quoteIfNeeded("fhjgadhjgfhs"), Is.is("fhjgadhjgfhs"));
        MatcherAssert.assertThat(Identifiers.quoteIfNeeded("fhjgadhjgfhs?"), Is.is("\"fhjgadhjgfhs\u00d6\""));
        MatcherAssert.assertThat(Identifiers.quoteIfNeeded("ABC"), Is.is("\"ABC\""));
        MatcherAssert.assertThat(Identifiers.quoteIfNeeded("abc\""), Is.is("\"abc\"\"\""));
        MatcherAssert.assertThat(Identifiers.quoteIfNeeded("select"), Is.is("\"select\""));// keyword

    }
}

