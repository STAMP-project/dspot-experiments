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
package com.google.android.exoplayer2.util;


import android.net.Uri;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link UriUtil}.
 */
@RunWith(RobolectricTestRunner.class)
public final class UriUtilTest {
    /**
     * Tests normal usage of {@link UriUtil#resolve(String, String)}.
     * <p>
     * The test cases are taken from RFC-3986 5.4.1.
     */
    @Test
    public void testResolveNormal() {
        String base = "http://a/b/c/d;p?q";
        assertThat(UriUtil.resolve(base, "g:h")).isEqualTo("g:h");
        assertThat(UriUtil.resolve(base, "g")).isEqualTo("http://a/b/c/g");
        assertThat(UriUtil.resolve(base, "g/")).isEqualTo("http://a/b/c/g/");
        assertThat(UriUtil.resolve(base, "/g")).isEqualTo("http://a/g");
        assertThat(UriUtil.resolve(base, "//g")).isEqualTo("http://g");
        assertThat(UriUtil.resolve(base, "?y")).isEqualTo("http://a/b/c/d;p?y");
        assertThat(UriUtil.resolve(base, "g?y")).isEqualTo("http://a/b/c/g?y");
        assertThat(UriUtil.resolve(base, "#s")).isEqualTo("http://a/b/c/d;p?q#s");
        assertThat(UriUtil.resolve(base, "g#s")).isEqualTo("http://a/b/c/g#s");
        assertThat(UriUtil.resolve(base, "g?y#s")).isEqualTo("http://a/b/c/g?y#s");
        assertThat(UriUtil.resolve(base, ";x")).isEqualTo("http://a/b/c/;x");
        assertThat(UriUtil.resolve(base, "g;x")).isEqualTo("http://a/b/c/g;x");
        assertThat(UriUtil.resolve(base, "g;x?y#s")).isEqualTo("http://a/b/c/g;x?y#s");
        assertThat(UriUtil.resolve(base, "")).isEqualTo("http://a/b/c/d;p?q");
        assertThat(UriUtil.resolve(base, ".")).isEqualTo("http://a/b/c/");
        assertThat(UriUtil.resolve(base, "./")).isEqualTo("http://a/b/c/");
        assertThat(UriUtil.resolve(base, "..")).isEqualTo("http://a/b/");
        assertThat(UriUtil.resolve(base, "../")).isEqualTo("http://a/b/");
        assertThat(UriUtil.resolve(base, "../g")).isEqualTo("http://a/b/g");
        assertThat(UriUtil.resolve(base, "../..")).isEqualTo("http://a/");
        assertThat(UriUtil.resolve(base, "../../")).isEqualTo("http://a/");
        assertThat(UriUtil.resolve(base, "../../g")).isEqualTo("http://a/g");
    }

    /**
     * Tests abnormal usage of {@link UriUtil#resolve(String, String)}.
     * <p>
     * The test cases are taken from RFC-3986 5.4.2.
     */
    @Test
    public void testResolveAbnormal() {
        String base = "http://a/b/c/d;p?q";
        assertThat(UriUtil.resolve(base, "../../../g")).isEqualTo("http://a/g");
        assertThat(UriUtil.resolve(base, "../../../../g")).isEqualTo("http://a/g");
        assertThat(UriUtil.resolve(base, "/./g")).isEqualTo("http://a/g");
        assertThat(UriUtil.resolve(base, "/../g")).isEqualTo("http://a/g");
        assertThat(UriUtil.resolve(base, "g.")).isEqualTo("http://a/b/c/g.");
        assertThat(UriUtil.resolve(base, ".g")).isEqualTo("http://a/b/c/.g");
        assertThat(UriUtil.resolve(base, "g..")).isEqualTo("http://a/b/c/g..");
        assertThat(UriUtil.resolve(base, "..g")).isEqualTo("http://a/b/c/..g");
        assertThat(UriUtil.resolve(base, "./../g")).isEqualTo("http://a/b/g");
        assertThat(UriUtil.resolve(base, "./g/.")).isEqualTo("http://a/b/c/g/");
        assertThat(UriUtil.resolve(base, "g/./h")).isEqualTo("http://a/b/c/g/h");
        assertThat(UriUtil.resolve(base, "g/../h")).isEqualTo("http://a/b/c/h");
        assertThat(UriUtil.resolve(base, "g;x=1/./y")).isEqualTo("http://a/b/c/g;x=1/y");
        assertThat(UriUtil.resolve(base, "g;x=1/../y")).isEqualTo("http://a/b/c/y");
        assertThat(UriUtil.resolve(base, "g?y/./x")).isEqualTo("http://a/b/c/g?y/./x");
        assertThat(UriUtil.resolve(base, "g?y/../x")).isEqualTo("http://a/b/c/g?y/../x");
        assertThat(UriUtil.resolve(base, "g#s/./x")).isEqualTo("http://a/b/c/g#s/./x");
        assertThat(UriUtil.resolve(base, "g#s/../x")).isEqualTo("http://a/b/c/g#s/../x");
        assertThat(UriUtil.resolve(base, "http:g")).isEqualTo("http:g");
    }

    /**
     * Tests additional abnormal usage of {@link UriUtil#resolve(String, String)}.
     */
    @Test
    public void testResolveAbnormalAdditional() {
        assertThat(UriUtil.resolve("http://a/b", "c:d/../e")).isEqualTo("c:e");
        assertThat(UriUtil.resolve("a:b", "../c")).isEqualTo("a:c");
    }

    @Test
    public void removeOnlyQueryParameter() {
        Uri uri = Uri.parse("http://uri?query=value");
        assertThat(UriUtil.removeQueryParameter(uri, "query").toString()).isEqualTo("http://uri");
    }

    @Test
    public void removeFirstQueryParameter() {
        Uri uri = Uri.parse("http://uri?query=value&second=value2");
        assertThat(UriUtil.removeQueryParameter(uri, "query").toString()).isEqualTo("http://uri?second=value2");
    }

    @Test
    public void removeMiddleQueryParameter() {
        Uri uri = Uri.parse("http://uri?first=value1&query=value&last=value2");
        assertThat(UriUtil.removeQueryParameter(uri, "query").toString()).isEqualTo("http://uri?first=value1&last=value2");
    }

    @Test
    public void removeLastQueryParameter() {
        Uri uri = Uri.parse("http://uri?first=value1&query=value");
        assertThat(UriUtil.removeQueryParameter(uri, "query").toString()).isEqualTo("http://uri?first=value1");
    }

    @Test
    public void removeNonExistentQueryParameter() {
        Uri uri = Uri.parse("http://uri");
        assertThat(UriUtil.removeQueryParameter(uri, "foo").toString()).isEqualTo("http://uri");
        uri = Uri.parse("http://uri?query=value");
        assertThat(UriUtil.removeQueryParameter(uri, "foo").toString()).isEqualTo("http://uri?query=value");
    }
}

