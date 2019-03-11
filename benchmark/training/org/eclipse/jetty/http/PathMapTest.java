/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.http;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 */
public class PathMapTest {
    @Test
    public void testPathMap() throws Exception {
        PathMap<String> p = new PathMap();
        p.put("/abs/path", "1");
        p.put("/abs/path/longer", "2");
        p.put("/animal/bird/*", "3");
        p.put("/animal/fish/*", "4");
        p.put("/animal/*", "5");
        p.put("*.tar.gz", "6");
        p.put("*.gz", "7");
        p.put("/", "8");
        p.put("/XXX:/YYY", "9");
        p.put("", "10");
        p.put("/\u20acuro/*", "11");
        String[][] tests = new String[][]{ new String[]{ "/abs/path", "1" }, new String[]{ "/abs/path/xxx", "8" }, new String[]{ "/abs/pith", "8" }, new String[]{ "/abs/path/longer", "2" }, new String[]{ "/abs/path/", "8" }, new String[]{ "/abs/path/xxx", "8" }, new String[]{ "/animal/bird/eagle/bald", "3" }, new String[]{ "/animal/fish/shark/grey", "4" }, new String[]{ "/animal/insect/bug", "5" }, new String[]{ "/animal", "5" }, new String[]{ "/animal/", "5" }, new String[]{ "/animal/x", "5" }, new String[]{ "/animal/*", "5" }, new String[]{ "/suffix/path.tar.gz", "6" }, new String[]{ "/suffix/path.gz", "7" }, new String[]{ "/animal/path.gz", "5" }, new String[]{ "/Other/path", "8" }, new String[]{ "/\u20acuro/path", "11" }, new String[]{ "/", "10" } };
        for (String[] test : tests) {
            Assertions.assertEquals(test[1], p.getMatch(test[0]).getValue(), test[0]);
        }
        Assertions.assertEquals("1", p.get("/abs/path"), "Get absolute path");
        Assertions.assertEquals("/abs/path", p.getMatch("/abs/path").getKey(), "Match absolute path");
        Assertions.assertEquals("[/animal/bird/*=3, /animal/*=5, *.tar.gz=6, *.gz=7, /=8]", p.getMatches("/animal/bird/path.tar.gz").toString(), "all matches");
        Assertions.assertEquals("[/animal/fish/*=4, /animal/*=5, /=8]", p.getMatches("/animal/fish/").toString(), "Dir matches");
        Assertions.assertEquals("[/animal/fish/*=4, /animal/*=5, /=8]", p.getMatches("/animal/fish").toString(), "Dir matches");
        Assertions.assertEquals("[=10, /=8]", p.getMatches("/").toString(), "Root matches");
        Assertions.assertEquals("[/=8]", p.getMatches("").toString(), "Dir matches");
        Assertions.assertEquals("/Foo/bar", PathMap.pathMatch("/Foo/bar", "/Foo/bar"), "pathMatch exact");
        Assertions.assertEquals("/Foo", PathMap.pathMatch("/Foo/*", "/Foo/bar"), "pathMatch prefix");
        Assertions.assertEquals("/Foo", PathMap.pathMatch("/Foo/*", "/Foo/"), "pathMatch prefix");
        Assertions.assertEquals("/Foo", PathMap.pathMatch("/Foo/*", "/Foo"), "pathMatch prefix");
        Assertions.assertEquals("/Foo/bar.ext", PathMap.pathMatch("*.ext", "/Foo/bar.ext"), "pathMatch suffix");
        Assertions.assertEquals("/Foo/bar.ext", PathMap.pathMatch("/", "/Foo/bar.ext"), "pathMatch default");
        Assertions.assertEquals(null, PathMap.pathInfo("/Foo/bar", "/Foo/bar"), "pathInfo exact");
        Assertions.assertEquals("/bar", PathMap.pathInfo("/Foo/*", "/Foo/bar"), "pathInfo prefix");
        Assertions.assertEquals("/*", PathMap.pathInfo("/Foo/*", "/Foo/*"), "pathInfo prefix");
        Assertions.assertEquals("/", PathMap.pathInfo("/Foo/*", "/Foo/"), "pathInfo prefix");
        Assertions.assertEquals(null, PathMap.pathInfo("/Foo/*", "/Foo"), "pathInfo prefix");
        Assertions.assertEquals(null, PathMap.pathInfo("*.ext", "/Foo/bar.ext"), "pathInfo suffix");
        Assertions.assertEquals(null, PathMap.pathInfo("/", "/Foo/bar.ext"), "pathInfo default");
        Assertions.assertEquals("9", p.getMatch("/XXX").getValue(), "multi paths");
        Assertions.assertEquals("9", p.getMatch("/YYY").getValue(), "multi paths");
        p.put("/*", "0");
        Assertions.assertEquals("1", p.get("/abs/path"), "Get absolute path");
        Assertions.assertEquals("/abs/path", p.getMatch("/abs/path").getKey(), "Match absolute path");
        Assertions.assertEquals("1", p.getMatch("/abs/path").getValue(), "Match absolute path");
        Assertions.assertEquals("0", p.getMatch("/abs/path/xxx").getValue(), "Mismatch absolute path");
        Assertions.assertEquals("0", p.getMatch("/abs/pith").getValue(), "Mismatch absolute path");
        Assertions.assertEquals("2", p.getMatch("/abs/path/longer").getValue(), "Match longer absolute path");
        Assertions.assertEquals("0", p.getMatch("/abs/path/").getValue(), "Not exact absolute path");
        Assertions.assertEquals("0", p.getMatch("/abs/path/xxx").getValue(), "Not exact absolute path");
        Assertions.assertEquals("3", p.getMatch("/animal/bird/eagle/bald").getValue(), "Match longest prefix");
        Assertions.assertEquals("4", p.getMatch("/animal/fish/shark/grey").getValue(), "Match longest prefix");
        Assertions.assertEquals("5", p.getMatch("/animal/insect/bug").getValue(), "Match longest prefix");
        Assertions.assertEquals("5", p.getMatch("/animal").getValue(), "mismatch exact prefix");
        Assertions.assertEquals("5", p.getMatch("/animal/").getValue(), "mismatch exact prefix");
        Assertions.assertEquals("0", p.getMatch("/suffix/path.tar.gz").getValue(), "Match longest suffix");
        Assertions.assertEquals("0", p.getMatch("/suffix/path.gz").getValue(), "Match longest suffix");
        Assertions.assertEquals("5", p.getMatch("/animal/path.gz").getValue(), "prefix rather than suffix");
        Assertions.assertEquals("0", p.getMatch("/Other/path").getValue(), "default");
        Assertions.assertEquals("", PathMap.pathMatch("/*", "/xxx/zzz"), "pathMatch /*");
        Assertions.assertEquals("/xxx/zzz", PathMap.pathInfo("/*", "/xxx/zzz"), "pathInfo /*");
        Assertions.assertTrue(PathMap.match("/", "/anything"), "match /");
        Assertions.assertTrue(PathMap.match("/*", "/anything"), "match /*");
        Assertions.assertTrue(PathMap.match("/foo", "/foo"), "match /foo");
        Assertions.assertTrue((!(PathMap.match("/foo", "/bar"))), "!match /foo");
        Assertions.assertTrue(PathMap.match("/foo/*", "/foo"), "match /foo/*");
        Assertions.assertTrue(PathMap.match("/foo/*", "/foo/"), "match /foo/*");
        Assertions.assertTrue(PathMap.match("/foo/*", "/foo/anything"), "match /foo/*");
        Assertions.assertTrue((!(PathMap.match("/foo/*", "/bar"))), "!match /foo/*");
        Assertions.assertTrue((!(PathMap.match("/foo/*", "/bar/"))), "!match /foo/*");
        Assertions.assertTrue((!(PathMap.match("/foo/*", "/bar/anything"))), "!match /foo/*");
        Assertions.assertTrue(PathMap.match("*.foo", "anything.foo"), "match *.foo");
        Assertions.assertTrue((!(PathMap.match("*.foo", "anything.bar"))), "!match *.foo");
        Assertions.assertEquals("10", p.getMatch("/").getValue(), "match / with ''");
        Assertions.assertTrue(PathMap.match("", "/"), "match \"\"");
    }

    /**
     * See JIRA issue: JETTY-88.
     *
     * @throws Exception
     * 		failed test
     */
    @Test
    public void testPathMappingsOnlyMatchOnDirectoryNames() throws Exception {
        String spec = "/xyz/*";
        assertMatch(spec, "/xyz");
        assertMatch(spec, "/xyz/");
        assertMatch(spec, "/xyz/123");
        assertMatch(spec, "/xyz/123/");
        assertMatch(spec, "/xyz/123.txt");
        assertNotMatch(spec, "/xyz123");
        assertNotMatch(spec, "/xyz123;jessionid=99");
        assertNotMatch(spec, "/xyz123/");
        assertNotMatch(spec, "/xyz123/456");
        assertNotMatch(spec, "/xyz.123");
        assertNotMatch(spec, "/xyz;123");// as if the ; was encoded and part of the path

        assertNotMatch(spec, "/xyz?123");// as if the ? was encoded and part of the path

    }

    @Test
    public void testPrecidenceVsOrdering() throws Exception {
        PathMap<String> p = new PathMap();
        p.put("/dump/gzip/*", "prefix");
        p.put("*.txt", "suffix");
        Assertions.assertEquals(null, p.getMatch("/foo/bar"));
        Assertions.assertEquals("prefix", p.getMatch("/dump/gzip/something").getValue());
        Assertions.assertEquals("suffix", p.getMatch("/foo/something.txt").getValue());
        Assertions.assertEquals("prefix", p.getMatch("/dump/gzip/something.txt").getValue());
        p = new PathMap();
        p.put("*.txt", "suffix");
        p.put("/dump/gzip/*", "prefix");
        Assertions.assertEquals(null, p.getMatch("/foo/bar"));
        Assertions.assertEquals("prefix", p.getMatch("/dump/gzip/something").getValue());
        Assertions.assertEquals("suffix", p.getMatch("/foo/something.txt").getValue());
        Assertions.assertEquals("prefix", p.getMatch("/dump/gzip/something.txt").getValue());
    }
}

