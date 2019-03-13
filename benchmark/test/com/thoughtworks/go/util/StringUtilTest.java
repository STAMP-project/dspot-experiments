/**
 * Copyright 2018 ThoughtWorks, Inc.
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
package com.thoughtworks.go.util;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class StringUtilTest {
    @Test
    public void shouldQuoteJavascriptString() throws Exception {
        Assert.assertThat(StringUtil.StringUtil.quoteJavascriptString("a b \\c \"d"), Matchers.is("\"a b \\\\c \\\"d\""));
    }

    @Test
    public void shouldFindSimpleRegExMatch() throws Exception {
        String url = "http://java.sun.com:80/docs/books/tutorial/essential/regex/test_harness.html";
        String baseUrl = StringUtil.StringUtil.matchPattern("^(http://[^/]*)/", url);
        Assert.assertThat(baseUrl, Matchers.is("http://java.sun.com:80"));
    }

    @Test
    public void shouldHumanize() throws Exception {
        Assert.assertThat(humanize("camelCase"), Matchers.is("camel case"));
        Assert.assertThat(humanize("camel"), Matchers.is("camel"));
        Assert.assertThat(humanize("camelCaseForALongString"), Matchers.is("camel case for a long string"));
    }

    @Test
    public void shouldJoinWithCharEscaped() {
        Assert.assertThat(escapeAndJoinStrings("foo", "bar", "baz", "hi_bye"), Matchers.is("foo_bar_baz_hi__bye"));
    }

    @Test
    public void shouldJoinWithNullsRepresentedAsBlankStrings() {
        Assert.assertThat(escapeAndJoinStrings("foo", null, "hi_bye", null, null), Matchers.is("foo__hi__bye__"));
    }

    @Test
    public void shouldJoinSentences() {
        Assert.assertThat(joinSentences("foo.", "bar", "baz. "), Matchers.is("foo. bar. baz."));
    }

    @Test
    public void shouldStripTillLastOccurrenceOfGivenString() {
        Assert.assertThat(stripTillLastOccurrenceOf("HelloWorld@@\\nfoobar\\nquux@@keep_this", "@@"), Matchers.is("keep_this"));
        Assert.assertThat(stripTillLastOccurrenceOf("HelloWorld", "@@"), Matchers.is("HelloWorld"));
        Assert.assertThat(stripTillLastOccurrenceOf(null, "@@"), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(stripTillLastOccurrenceOf("HelloWorld", null), Matchers.is("HelloWorld"));
        Assert.assertThat(stripTillLastOccurrenceOf(null, null), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(stripTillLastOccurrenceOf("", "@@"), Matchers.is(""));
    }

    @Test
    public void shouldUnQuote() throws Exception {
        Assert.assertThat(unQuote("\"Hello World\""), Matchers.is("Hello World"));
        Assert.assertThat(unQuote(null), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(unQuote("\"Hello World\" to everyone\""), Matchers.is("Hello World\" to everyone"));
    }
}

