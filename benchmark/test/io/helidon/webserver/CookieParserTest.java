/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.webserver;


import HashRequestHeaders.CookieParser;
import io.helidon.common.http.Parameters;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link HashRequestHeaders.CookieParser}.
 */
public class CookieParserTest {
    @Test
    public void emptyAndNull() throws Exception {
        Parameters p = CookieParser.parse(null);
        MatcherAssert.assertThat(p, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(p.toMap().isEmpty(), CoreMatchers.is(true));
        p = CookieParser.parse("");
        MatcherAssert.assertThat(p, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(p.toMap().isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void basicMultiValue() throws Exception {
        Parameters p = CookieParser.parse("foo=bar; aaa=bbb; c=what_the_hell; aaa=ccc");
        MatcherAssert.assertThat(p, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(p.all("foo"), contains("bar"));
        MatcherAssert.assertThat(p.all("aaa"), contains("bbb", "ccc"));
        MatcherAssert.assertThat(p.all("c"), contains("what_the_hell"));
    }

    @Test
    public void rfc2965() throws Exception {
        String header = "$version=1; foo=bar; $Domain=google.com, aaa=bbb, c=cool; $Domain=google.com; $Path=\"/foo\"";
        Parameters p = CookieParser.parse(header);
        MatcherAssert.assertThat(p, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(p.all("foo"), contains("bar"));
        MatcherAssert.assertThat(p.all("aaa"), contains("bbb"));
        MatcherAssert.assertThat(p.all("c"), contains("cool"));
        MatcherAssert.assertThat(p.first("$Domain").isPresent(), CoreMatchers.is(false));
        MatcherAssert.assertThat(p.first("$Path").isPresent(), CoreMatchers.is(false));
        MatcherAssert.assertThat(p.first("$Version").isPresent(), CoreMatchers.is(false));
    }

    @Test
    public void unquote() throws Exception {
        Parameters p = CookieParser.parse("foo=\"bar\"; aaa=bbb; c=\"what_the_hell\"; aaa=\"ccc\"");
        MatcherAssert.assertThat(p, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(p.all("foo"), contains("bar"));
        MatcherAssert.assertThat(p.all("aaa"), contains("bbb", "ccc"));
    }
}

