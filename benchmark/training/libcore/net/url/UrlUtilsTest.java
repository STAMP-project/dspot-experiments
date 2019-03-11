/**
 * Copyright (C) 2011 The Android Open Source Project
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
package libcore.net.url;


import junit.framework.TestCase;


public final class UrlUtilsTest extends TestCase {
    public void testCanonicalizePath() {
        TestCase.assertEquals("", UrlUtils.canonicalizePath("", true));
        TestCase.assertEquals("", UrlUtils.canonicalizePath(".", true));
        TestCase.assertEquals("", UrlUtils.canonicalizePath("..", true));
        TestCase.assertEquals("...", UrlUtils.canonicalizePath("...", true));
        TestCase.assertEquals("", UrlUtils.canonicalizePath("./", true));
        TestCase.assertEquals("", UrlUtils.canonicalizePath("../", true));
        TestCase.assertEquals("a", UrlUtils.canonicalizePath("../a", true));
        TestCase.assertEquals("a", UrlUtils.canonicalizePath("a", true));
        TestCase.assertEquals("a/", UrlUtils.canonicalizePath("a/", true));
        TestCase.assertEquals("a/", UrlUtils.canonicalizePath("a/.", true));
        TestCase.assertEquals("a/b", UrlUtils.canonicalizePath("a/./b", true));
        TestCase.assertEquals("", UrlUtils.canonicalizePath("a/..", true));
        TestCase.assertEquals("b", UrlUtils.canonicalizePath("a/../b", true));
        TestCase.assertEquals("a/.../b", UrlUtils.canonicalizePath("a/.../b", true));
        TestCase.assertEquals("a/b", UrlUtils.canonicalizePath("a/b", true));
        TestCase.assertEquals("a/b/", UrlUtils.canonicalizePath("a/b/.", true));
        TestCase.assertEquals("a/b/", UrlUtils.canonicalizePath("a/b/./", true));
        TestCase.assertEquals("a/b/c", UrlUtils.canonicalizePath("a/b/./c", true));
        TestCase.assertEquals("a/", UrlUtils.canonicalizePath("a/b/..", true));
        TestCase.assertEquals("a/", UrlUtils.canonicalizePath("a/b/../", true));
        TestCase.assertEquals("a//", UrlUtils.canonicalizePath("a/b/..//", true));
        TestCase.assertEquals("a/c", UrlUtils.canonicalizePath("a/b/../c", true));
        TestCase.assertEquals("a//c", UrlUtils.canonicalizePath("a/b/..//c", true));
        TestCase.assertEquals("c", UrlUtils.canonicalizePath("a/b/../../c", true));
        TestCase.assertEquals("/", UrlUtils.canonicalizePath("/", true));
        TestCase.assertEquals("//", UrlUtils.canonicalizePath("//", true));
        TestCase.assertEquals("/", UrlUtils.canonicalizePath("/.", true));
        TestCase.assertEquals("/", UrlUtils.canonicalizePath("/./", true));
        TestCase.assertEquals("", UrlUtils.canonicalizePath("/..", true));
        TestCase.assertEquals("c", UrlUtils.canonicalizePath("/../c", true));
        TestCase.assertEquals("/a/b/c", UrlUtils.canonicalizePath("/a/b/c", true));
    }

    public void testGetProtocolPrefix() {
        TestCase.assertEquals("http", UrlUtils.getSchemePrefix("http:"));
        TestCase.assertEquals("http", UrlUtils.getSchemePrefix("HTTP:"));
        TestCase.assertEquals("http", UrlUtils.getSchemePrefix("http:x"));
        TestCase.assertEquals("a", UrlUtils.getSchemePrefix("a:"));
        TestCase.assertEquals("z", UrlUtils.getSchemePrefix("z:"));
        TestCase.assertEquals("a", UrlUtils.getSchemePrefix("A:"));
        TestCase.assertEquals("z", UrlUtils.getSchemePrefix("Z:"));
        TestCase.assertEquals("h0", UrlUtils.getSchemePrefix("h0:"));
        TestCase.assertEquals("h5", UrlUtils.getSchemePrefix("h5:"));
        TestCase.assertEquals("h9", UrlUtils.getSchemePrefix("h9:"));
        TestCase.assertEquals("h+", UrlUtils.getSchemePrefix("h+:"));
        TestCase.assertEquals("h-", UrlUtils.getSchemePrefix("h-:"));
        TestCase.assertEquals("h.", UrlUtils.getSchemePrefix("h.:"));
    }

    public void testGetProtocolPrefixInvalidScheme() {
        TestCase.assertNull(UrlUtils.getSchemePrefix(""));
        TestCase.assertNull(UrlUtils.getSchemePrefix("http"));
        TestCase.assertNull(UrlUtils.getSchemePrefix(":"));
        TestCase.assertNull(UrlUtils.getSchemePrefix("+:"));
        TestCase.assertNull(UrlUtils.getSchemePrefix("-:"));
        TestCase.assertNull(UrlUtils.getSchemePrefix(".:"));
        TestCase.assertNull(UrlUtils.getSchemePrefix("0:"));
        TestCase.assertNull(UrlUtils.getSchemePrefix("5:"));
        TestCase.assertNull(UrlUtils.getSchemePrefix("9:"));
        TestCase.assertNull(UrlUtils.getSchemePrefix("http//"));
        TestCase.assertNull(UrlUtils.getSchemePrefix("http/:"));
        TestCase.assertNull(UrlUtils.getSchemePrefix("ht tp://"));
        TestCase.assertNull(UrlUtils.getSchemePrefix(" http://"));
        TestCase.assertNull(UrlUtils.getSchemePrefix("http ://"));
        TestCase.assertNull(UrlUtils.getSchemePrefix(":://"));
    }
}

