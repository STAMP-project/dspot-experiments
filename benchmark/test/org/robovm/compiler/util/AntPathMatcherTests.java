/**
 * Copyright 2002-2013 the original author or authors.
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
package org.robovm.compiler.util;


import org.junit.Assert;
import org.junit.Test;

import static AntPathMatcher.DEFAULT_PATH_SEPARATOR;


/**
 * RoboVM note: This test class was copied from Spring Framework.
 *
 * @author Alef Arendsen
 * @author Seth Ladd
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 */
public class AntPathMatcherTests {
    static class TestMatcher {
        String pathSeparator = DEFAULT_PATH_SEPARATOR;

        boolean match(String pattern, String path) {
            return new AntPathMatcher(pattern, pathSeparator).matches(path);
        }

        public void setPathSeparator(String pathSeparator) {
            this.pathSeparator = pathSeparator;
        }
    }

    private AntPathMatcherTests.TestMatcher pathMatcher;

    @Test
    public void match() {
        // test exact matching
        Assert.assertTrue(pathMatcher.match("test", "test"));
        Assert.assertTrue(pathMatcher.match("/test", "/test"));
        Assert.assertFalse(pathMatcher.match("/test.jpg", "test.jpg"));
        Assert.assertFalse(pathMatcher.match("test", "/test"));
        Assert.assertFalse(pathMatcher.match("/test", "test"));
        // test matching with ?'s
        Assert.assertTrue(pathMatcher.match("t?st", "test"));
        Assert.assertTrue(pathMatcher.match("??st", "test"));
        Assert.assertTrue(pathMatcher.match("tes?", "test"));
        Assert.assertTrue(pathMatcher.match("te??", "test"));
        Assert.assertTrue(pathMatcher.match("?es?", "test"));
        Assert.assertFalse(pathMatcher.match("tes?", "tes"));
        Assert.assertFalse(pathMatcher.match("tes?", "testt"));
        Assert.assertFalse(pathMatcher.match("tes?", "tsst"));
        // test matchin with *'s
        Assert.assertTrue(pathMatcher.match("*", "test"));
        Assert.assertTrue(pathMatcher.match("test*", "test"));
        Assert.assertTrue(pathMatcher.match("test*", "testTest"));
        Assert.assertTrue(pathMatcher.match("test/*", "test/Test"));
        Assert.assertTrue(pathMatcher.match("test/*", "test/t"));
        Assert.assertTrue(pathMatcher.match("test/*", "test/"));
        Assert.assertTrue(pathMatcher.match("*test*", "AnothertestTest"));
        Assert.assertTrue(pathMatcher.match("*test", "Anothertest"));
        Assert.assertTrue(pathMatcher.match("*.*", "test."));
        Assert.assertTrue(pathMatcher.match("*.*", "test.test"));
        Assert.assertTrue(pathMatcher.match("*.*", "test.test.test"));
        Assert.assertTrue(pathMatcher.match("test*aaa", "testblaaaa"));
        Assert.assertFalse(pathMatcher.match("test*", "tst"));
        Assert.assertFalse(pathMatcher.match("test*", "tsttest"));
        Assert.assertFalse(pathMatcher.match("test*", "test/"));
        Assert.assertFalse(pathMatcher.match("test*", "test/t"));
        Assert.assertFalse(pathMatcher.match("test/*", "test"));
        Assert.assertFalse(pathMatcher.match("*test*", "tsttst"));
        Assert.assertFalse(pathMatcher.match("*test", "tsttst"));
        Assert.assertFalse(pathMatcher.match("*.*", "tsttst"));
        Assert.assertFalse(pathMatcher.match("test*aaa", "test"));
        Assert.assertFalse(pathMatcher.match("test*aaa", "testblaaab"));
        // test matching with ?'s and /'s
        Assert.assertTrue(pathMatcher.match("/?", "/a"));
        Assert.assertTrue(pathMatcher.match("/?/a", "/a/a"));
        Assert.assertTrue(pathMatcher.match("/a/?", "/a/b"));
        Assert.assertTrue(pathMatcher.match("/??/a", "/aa/a"));
        Assert.assertTrue(pathMatcher.match("/a/??", "/a/bb"));
        Assert.assertTrue(pathMatcher.match("/?", "/a"));
        // test matching with **'s
        Assert.assertTrue(pathMatcher.match("/**", "/testing/testing"));
        Assert.assertTrue(pathMatcher.match("/*/**", "/testing/testing"));
        Assert.assertTrue(pathMatcher.match("/**/*", "/testing/testing"));
        Assert.assertTrue(pathMatcher.match("/bla/**/bla", "/bla/testing/testing/bla"));
        Assert.assertTrue(pathMatcher.match("/bla/**/bla", "/bla/testing/testing/bla/bla"));
        Assert.assertTrue(pathMatcher.match("/**/test", "/bla/bla/test"));
        Assert.assertTrue(pathMatcher.match("/bla/**/**/bla", "/bla/bla/bla/bla/bla/bla"));
        Assert.assertTrue(pathMatcher.match("/bla*bla/test", "/blaXXXbla/test"));
        Assert.assertTrue(pathMatcher.match("/*bla/test", "/XXXbla/test"));
        Assert.assertFalse(pathMatcher.match("/bla*bla/test", "/blaXXXbl/test"));
        Assert.assertFalse(pathMatcher.match("/*bla/test", "XXXblab/test"));
        Assert.assertFalse(pathMatcher.match("/*bla/test", "XXXbl/test"));
        Assert.assertFalse(pathMatcher.match("/????", "/bala/bla"));
        Assert.assertFalse(pathMatcher.match("/**/*bla", "/bla/bla/bla/bbb"));
        Assert.assertTrue(pathMatcher.match("/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing/"));
        Assert.assertTrue(pathMatcher.match("/*bla*/**/bla/*", "/XXXblaXXXX/testing/testing/bla/testing"));
        Assert.assertTrue(pathMatcher.match("/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing"));
        Assert.assertTrue(pathMatcher.match("/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing.jpg"));
        Assert.assertTrue(pathMatcher.match("*bla*/**/bla/**", "XXXblaXXXX/testing/testing/bla/testing/testing/"));
        Assert.assertTrue(pathMatcher.match("*bla*/**/bla/*", "XXXblaXXXX/testing/testing/bla/testing"));
        Assert.assertTrue(pathMatcher.match("*bla*/**/bla/**", "XXXblaXXXX/testing/testing/bla/testing/testing"));
        Assert.assertFalse(pathMatcher.match("*bla*/**/bla/*", "XXXblaXXXX/testing/testing/bla/testing/testing"));
        Assert.assertFalse(pathMatcher.match("/x/x/**/bla", "/x/x/x/"));
        Assert.assertTrue(pathMatcher.match("", ""));
        // assertTrue(pathMatcher.match("/{bla}.*", "/testing.html"));
    }

    @Test
    public void uniqueDeliminator() {
        pathMatcher.setPathSeparator(".");
        // test exact matching
        Assert.assertTrue(pathMatcher.match("test", "test"));
        Assert.assertTrue(pathMatcher.match(".test", ".test"));
        Assert.assertFalse(pathMatcher.match(".test/jpg", "test/jpg"));
        Assert.assertFalse(pathMatcher.match("test", ".test"));
        Assert.assertFalse(pathMatcher.match(".test", "test"));
        // test matching with ?'s
        Assert.assertTrue(pathMatcher.match("t?st", "test"));
        Assert.assertTrue(pathMatcher.match("??st", "test"));
        Assert.assertTrue(pathMatcher.match("tes?", "test"));
        Assert.assertTrue(pathMatcher.match("te??", "test"));
        Assert.assertTrue(pathMatcher.match("?es?", "test"));
        Assert.assertFalse(pathMatcher.match("tes?", "tes"));
        Assert.assertFalse(pathMatcher.match("tes?", "testt"));
        Assert.assertFalse(pathMatcher.match("tes?", "tsst"));
        // test matchin with *'s
        Assert.assertTrue(pathMatcher.match("*", "test"));
        Assert.assertTrue(pathMatcher.match("test*", "test"));
        Assert.assertTrue(pathMatcher.match("test*", "testTest"));
        Assert.assertTrue(pathMatcher.match("*test*", "AnothertestTest"));
        Assert.assertTrue(pathMatcher.match("*test", "Anothertest"));
        Assert.assertTrue(pathMatcher.match("*/*", "test/"));
        Assert.assertTrue(pathMatcher.match("*/*", "test/test"));
        Assert.assertTrue(pathMatcher.match("*/*", "test/test/test"));
        Assert.assertTrue(pathMatcher.match("test*aaa", "testblaaaa"));
        Assert.assertFalse(pathMatcher.match("test*", "tst"));
        Assert.assertFalse(pathMatcher.match("test*", "tsttest"));
        Assert.assertFalse(pathMatcher.match("*test*", "tsttst"));
        Assert.assertFalse(pathMatcher.match("*test", "tsttst"));
        Assert.assertFalse(pathMatcher.match("*/*", "tsttst"));
        Assert.assertFalse(pathMatcher.match("test*aaa", "test"));
        Assert.assertFalse(pathMatcher.match("test*aaa", "testblaaab"));
        // test matching with ?'s and .'s
        Assert.assertTrue(pathMatcher.match(".?", ".a"));
        Assert.assertTrue(pathMatcher.match(".?.a", ".a.a"));
        Assert.assertTrue(pathMatcher.match(".a.?", ".a.b"));
        Assert.assertTrue(pathMatcher.match(".??.a", ".aa.a"));
        Assert.assertTrue(pathMatcher.match(".a.??", ".a.bb"));
        Assert.assertTrue(pathMatcher.match(".?", ".a"));
        // test matching with **'s
        Assert.assertTrue(pathMatcher.match(".**", ".testing.testing"));
        Assert.assertTrue(pathMatcher.match(".*.**", ".testing.testing"));
        Assert.assertTrue(pathMatcher.match(".**.*", ".testing.testing"));
        Assert.assertTrue(pathMatcher.match(".bla.**.bla", ".bla.testing.testing.bla"));
        Assert.assertTrue(pathMatcher.match(".bla.**.bla", ".bla.testing.testing.bla.bla"));
        Assert.assertTrue(pathMatcher.match(".**.test", ".bla.bla.test"));
        Assert.assertTrue(pathMatcher.match(".bla.**.**.bla", ".bla.bla.bla.bla.bla.bla"));
        Assert.assertTrue(pathMatcher.match(".bla*bla.test", ".blaXXXbla.test"));
        Assert.assertTrue(pathMatcher.match(".*bla.test", ".XXXbla.test"));
        Assert.assertFalse(pathMatcher.match(".bla*bla.test", ".blaXXXbl.test"));
        Assert.assertFalse(pathMatcher.match(".*bla.test", "XXXblab.test"));
        Assert.assertFalse(pathMatcher.match(".*bla.test", "XXXbl.test"));
    }
}

