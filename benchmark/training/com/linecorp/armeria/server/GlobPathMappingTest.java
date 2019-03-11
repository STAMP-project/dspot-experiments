/**
 * Copyright 2015 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server;


import org.junit.Test;


public class GlobPathMappingTest {
    @Test
    public void testSingleAsterisk() {
        GlobPathMappingTest.pass("*", "/foo", "/foo/bar", "/foo/bar/baz");
        GlobPathMappingTest.fail("*", "/foo/", "/foo/bar/", "/foo/bar/baz/");
        GlobPathMappingTest.pass("*/", "/foo/", "/foo/bar/", "/foo/bar/baz/");
        GlobPathMappingTest.fail("*/", "/foo", "/foo/bar", "/foo/bar/baz");
        GlobPathMappingTest.pass("*.js", "/.js", "/foo.js", "/foo/bar.js", "/foo/bar/baz.js");
        GlobPathMappingTest.fail("*.js", "/foo.js/", "/foo.js/bar", "/foo.json");
        GlobPathMappingTest.pass("/foo/*", "/foo/bar", "/foo/baz");
        GlobPathMappingTest.fail("/foo/*", "/foo", "/foo/", "/foo/bar/", "/foo/bar/baz", "/foo/bar/baz/", "/baz/foo/bar");
        GlobPathMappingTest.pass("/foo/*/", "/foo/bar/", "/foo/baz/");
        GlobPathMappingTest.fail("/foo/*/", "/foo/", "/foo//", "/foo/bar", "/foo/bar/baz", "/foo/bar/baz/", "/baz/foo/bar/");
        GlobPathMappingTest.pass("/*/baz", "/foo/baz", "/bar/baz");
        GlobPathMappingTest.fail("/*/baz", "/foo/baz/", "/bar/baz/", "//baz", "//baz/");
        GlobPathMappingTest.pass("/foo/*/bar/*/baz/*", "/foo/1/bar/2/baz/3");
        GlobPathMappingTest.fail("/foo/*/bar/*/baz/*", "/foo/1/bar/2/baz/3/");
    }

    @Test
    public void testDoubleAsterisk() {
        GlobPathMappingTest.pass("**/baz", "/baz", "/foo/baz", "/foo/bar/baz");
        GlobPathMappingTest.fail("**/baz", "/baz/", "/baz/bar");
        GlobPathMappingTest.pass("**/baz/", "/baz/", "/foo/baz/", "/foo/bar/baz/");
        GlobPathMappingTest.fail("**/baz/", "/baz", "/baz/bar");
        GlobPathMappingTest.pass("/foo/**", "/foo/", "/foo/bar", "/foo/bar/", "/foo/bar/baz", "/foo/bar/baz");
        GlobPathMappingTest.fail("/foo/**", "/foo", "/bar/foo/");
        GlobPathMappingTest.pass("/foo/**/baz", "/foo/baz", "/foo/bar/baz", "/foo/alice/bob/charles/baz");
        GlobPathMappingTest.fail("/foo/**/baz", "/foobaz");
        GlobPathMappingTest.pass("foo/**/baz", "/foo/baz", "/alice/foo/bar/baz", "/alice/bob/foo/bar/baz/baz");
    }

    @Test
    public void testRelativePattern() {
        GlobPathMappingTest.pass("baz", "/baz", "/bar/baz", "/foo/bar/baz");
        GlobPathMappingTest.fail("baz", "/baz/", "/bar/baz/", "/foo/bar/baz/", "/foo/bar/baz/quo");
        GlobPathMappingTest.pass("bar/baz", "/bar/baz", "/foo/bar/baz");
        GlobPathMappingTest.fail("bar/baz", "/bar/baz/", "/foo/bar/baz/", "/foo/bar/baz/quo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPathValidation() {
        GlobPathMappingTest.compile("**").apply(PathMappingContextTest.create("not/an/absolute/path"));
    }

    @Test
    public void testLoggerName() throws Exception {
        assertThat(PathMapping.ofGlob("/foo/bar/**").loggerName()).isEqualTo("foo.bar.__");
        assertThat(PathMapping.ofGlob("foo").loggerName()).isEqualTo("__.foo");
    }

    @Test
    public void testMetricName() throws Exception {
        assertThat(PathMapping.ofGlob("/foo/bar/**").meterTag()).isEqualTo("glob:/foo/bar/**");
        assertThat(PathMapping.ofGlob("foo").meterTag()).isEqualTo("glob:/**/foo");
    }

    @Test
    public void params() throws Exception {
        PathMapping m;
        m = PathMapping.ofGlob("baz");
        assertThat(m.paramNames()).isEmpty();
        // Should not create a param for 'bar'
        assertThat(m.apply(PathMappingContextTest.create("/bar/baz")).pathParams()).isEmpty();
        m = PathMapping.ofGlob("/bar/baz/*");
        assertThat(m.paramNames()).containsExactly("0");
        assertThat(m.apply(PathMappingContextTest.create("/bar/baz/qux")).pathParams()).containsEntry("0", "qux").hasSize(1);
        m = PathMapping.ofGlob("/foo/**");
        assertThat(m.paramNames()).containsExactly("0");
        assertThat(m.apply(PathMappingContextTest.create("/foo/bar/baz")).pathParams()).containsEntry("0", "bar/baz").hasSize(1);
        assertThat(m.apply(PathMappingContextTest.create("/foo/")).pathParams()).containsEntry("0", "").hasSize(1);
        m = PathMapping.ofGlob("/**/*.js");
        assertThat(m.paramNames()).containsExactlyInAnyOrder("0", "1");
        assertThat(m.apply(PathMappingContextTest.create("/lib/jquery.min.js")).pathParams()).containsEntry("0", "lib").containsEntry("1", "jquery.min").hasSize(2);
        assertThat(m.apply(PathMappingContextTest.create("/lodash.js")).pathParams()).containsEntry("0", "").containsEntry("1", "lodash").hasSize(2);
    }

    @Test
    public void utf8() throws Exception {
        final PathMapping m = PathMapping.ofGlob("/foo/*");
        final PathMappingResult res = m.apply(PathMappingContextTest.create("/foo/%C2%A2"));
        assertThat(res.path()).isEqualTo("/foo/%C2%A2");
        assertThat(res.decodedPath()).isEqualTo("/foo/?");
        assertThat(res.pathParams()).containsEntry("0", "?").hasSize(1);
    }
}

