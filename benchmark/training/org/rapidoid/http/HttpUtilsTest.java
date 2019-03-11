/**
 * -
 * #%L
 * rapidoid-http-server
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
 * %%
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
 * #L%
 */
package org.rapidoid.http;


import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.test.TestCommons;


@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class HttpUtilsTest extends TestCommons {
    String[] invalidResNames = new String[]{ null, "", "\\r", "\\n", "/h\\r", "/\\n", "/\\t", "/\\x0", "?", "\\x200", "?", "/?", "/f?2", "*", "/f*fg", "..", "/g..html", "", "/xy\\z", "../aa", "..\\ff", "/a\\b", "/a\\b/c", "/a/xx/\\b/c", "/\\", "/afbbb.asd/ff\\b/c", "/..", "/../", "/../ad", "/xx/../ad", "/../11/g.ad" };

    @Test
    public void testView() {
        eq(HttpUtils.resName("/"), "index");
        eq(HttpUtils.resName("/abc"), "abc");
        eq(HttpUtils.resName("/x/y/z"), "x/y/z");
        eq(HttpUtils.resName("/foo.html"), "foo");
        eq(HttpUtils.resName("/aa/bb.html"), "aa/bb");
        eq(HttpUtils.resName("/aa/bb-c_d11.txt"), "aa/bb-c_d11.txt");
        eq(HttpUtils.resName("/a b"), "a b");
        eq(HttpUtils.resName("/c  d"), "c  d");
        eq(HttpUtils.resName("/   "), "   ");
        // eq(HttpUtils.inferViewNameFromRoutePath("/books/{x}"), "books/x");
        // eq(HttpUtils.inferViewNameFromRoutePath("/books/{id:\\d+}"), "books/id");
        // eq(HttpUtils.inferViewNameFromRoutePath("/books/{a:.*}-{b}/view"), "books/a-b/view");
    }

    @Test
    public void testUnicodeResourceNames() {
        eq(HttpUtils.resName("/???????"), "???????");
        eq(HttpUtils.resName("/??"), "??");
        eq(HttpUtils.resName("/??.html"), "??");
        eq(HttpUtils.resName("/foo/??/bar.html"), "foo/??/bar");
        eq(HttpUtils.resName("/??/??.html"), "??/??");
        // eq(HttpUtils.inferViewNameFromRoutePath("/???????/{x}"), "???????/x");
        // eq(HttpUtils.inferViewNameFromRoutePath("/Dlf3???/{id:\\d+}"), "Dlf3???/id");
        // eq(HttpUtils.inferViewNameFromRoutePath("/??/{a:.*}-{b}/foo"), "??/a-b/foo");
    }

    @Test
    public void testInvalidResources() {
        for (String resName : invalidResNames) {
            try {
                HttpUtils.resName(resName);
            } catch (Exception e) {
                continue;
            }
            fail("Expected error!");
        }
    }
}

