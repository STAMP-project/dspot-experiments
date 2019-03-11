/**
 * -\-\-
 * Spotify Apollo API Interfaces
 * --
 * Copyright (C) 2013 - 2017 Spotify AB
 * --
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
 * -/-/-
 */
package com.spotify.apollo;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class HeadersTest {
    private static final Map<String, String> TEST_MAP_DUPLICATE_KEYS = HeadersTest.createDuplicateKeysMap();

    private static final Map<String, String> TEST_BIG_MAP = HeadersTest.createBigMap();

    @Test
    public void testGetIsCaseInsensitiveLower() {
        Map<String, String> map = Collections.singletonMap("x-name", "value");
        Headers headers = Headers.create(map);
        Assert.assertThat(headers.get("X-NaMe"), CoreMatchers.is(Optional.of("value")));
    }

    @Test
    public void testGetIsCaseInsensitiveUpper() {
        Map<String, String> map = Collections.singletonMap("X-NAme", "value");
        Headers headers = Headers.create(map);
        Assert.assertThat(headers.get("x-name"), CoreMatchers.is(Optional.of("value")));
    }

    @Test
    public void testAsMapPreservesOrderForOverwrittenKeys() {
        Headers headers = Headers.create(HeadersTest.TEST_MAP_DUPLICATE_KEYS);
        List<Map.Entry<String, String>> asMapResult = new java.util.ArrayList(headers.asMap().entrySet());
        Assert.assertThat(headers.asMap().size(), CoreMatchers.is(2));
        // First key and value where overwritten, but kept on the same first position
        Assert.assertThat(asMapResult.get(0).getKey(), CoreMatchers.is("FirST-KEy"));
        Assert.assertThat(asMapResult.get(0).getValue(), CoreMatchers.is("value2"));
        // Second header key and value stayed on the second position
        Assert.assertThat(asMapResult.get(1).getKey(), CoreMatchers.is("second-key"));
        Assert.assertThat(asMapResult.get(1).getValue(), CoreMatchers.is("other-value"));
    }

    @Test
    public void testAsMapPreservesOrderFromConstructor() {
        Headers headers = Headers.create(HeadersTest.TEST_BIG_MAP);
        List<Map.Entry<String, String>> asMapResult = new java.util.ArrayList(headers.asMap().entrySet());
        Assert.assertThat(asMapResult.size(), CoreMatchers.is(HeadersTest.TEST_BIG_MAP.size()));
        for (int i = 0; i < (asMapResult.size()); i++) {
            Map.Entry<String, String> header = asMapResult.get(i);
            Assert.assertThat(header.getKey(), CoreMatchers.is(String.valueOf(i)));
        }
    }

    @Test
    public void testAsMapPreservesLetterCase() {
        Map<String, String> map = Collections.singletonMap("sTRangE-KEy", "value");
        Headers headers = Headers.create(map);
        Map.Entry<String, String> asMapResult = headers.asMap().entrySet().iterator().next();
        Assert.assertThat(asMapResult.getKey(), CoreMatchers.is("sTRangE-KEy"));
    }

    @Test
    public void testEntriesPreservesOrderForOverwrittenKeys() {
        Headers headers = Headers.create(HeadersTest.TEST_MAP_DUPLICATE_KEYS);
        List<Map.Entry<String, String>> entriesResult = new java.util.ArrayList(headers.entries());
        Assert.assertThat(headers.entries().size(), CoreMatchers.is(2));
        // First key and value where overwritten, but kept on the same first position
        Assert.assertThat(entriesResult.get(0).getKey(), CoreMatchers.is("FirST-KEy"));
        Assert.assertThat(entriesResult.get(0).getValue(), CoreMatchers.is("value2"));
        // Second header key and value stayed on the second position
        Assert.assertThat(entriesResult.get(1).getKey(), CoreMatchers.is("second-key"));
        Assert.assertThat(entriesResult.get(1).getValue(), CoreMatchers.is("other-value"));
    }

    @Test
    public void testEntriesPreservesOrderFromConstructor() {
        Headers headers = Headers.create(HeadersTest.TEST_BIG_MAP);
        List<Map.Entry<String, String>> entries = new java.util.ArrayList(headers.entries());
        Assert.assertThat(entries.size(), CoreMatchers.is(HeadersTest.TEST_BIG_MAP.size()));
        for (int i = 0; i < (entries.size()); i++) {
            Map.Entry<String, String> header = entries.get(i);
            Assert.assertThat(header.getKey(), CoreMatchers.is(String.valueOf(i)));
        }
    }

    @Test
    public void testEntriesPreservesLetterCase() {
        Map<String, String> map = Collections.singletonMap("sTRangE-KEy", "value");
        Headers headers = Headers.create(map);
        Map.Entry<String, String> asMapResult = headers.entries().get(0);
        Assert.assertThat(headers.entries().size(), CoreMatchers.is(1));
        Assert.assertThat(asMapResult.getKey(), CoreMatchers.is("sTRangE-KEy"));
    }

    @Test
    public void testEmptyConstructor() {
        Headers headers = Headers.create(Collections.emptyMap());
        Assert.assertThat(headers.asMap().size(), CoreMatchers.is(0));
        Assert.assertThat(headers.get("non-existent"), CoreMatchers.is(Optional.empty()));
    }
}

