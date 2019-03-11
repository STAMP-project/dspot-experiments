/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs;


import com.google.common.collect.Ordering;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.namenode.INodeId;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.PlatformAssumptions;
import org.junit.Assert;
import org.junit.Test;

import static Path.CUR_DIR;
import static Path.SEPARATOR;


public class TestGlobPaths {
    private static final UserGroupInformation unprivilegedUser = UserGroupInformation.createUserForTesting("myuser", new String[]{ "mygroup" });

    static class RegexPathFilter implements PathFilter {
        private final String regex;

        public RegexPathFilter(String regex) {
            this.regex = regex;
        }

        @Override
        public boolean accept(Path path) {
            return path.toString().matches(regex);
        }
    }

    private static MiniDFSCluster dfsCluster;

    private static FileSystem fs;

    private static FileSystem privilegedFs;

    private static FileContext fc;

    private static FileContext privilegedFc;

    private static final int NUM_OF_PATHS = 4;

    private static String USER_DIR;

    private final Path[] path = new Path[TestGlobPaths.NUM_OF_PATHS];

    /**
     * Test case to ensure that globs work on files with special characters.
     * Tests with a file pair where one has a \r at end and other does not.
     */
    @Test
    public void testCRInPathGlob() throws IOException {
        FileStatus[] statuses;
        Path d1 = new Path(TestGlobPaths.USER_DIR, "dir1");
        Path fNormal = new Path(d1, "f1");
        Path fWithCR = new Path(d1, "f1\r");
        TestGlobPaths.fs.mkdirs(d1);
        TestGlobPaths.fs.createNewFile(fNormal);
        TestGlobPaths.fs.createNewFile(fWithCR);
        statuses = TestGlobPaths.fs.globStatus(new Path(d1, "f1*"));
        Assert.assertEquals("Expected both normal and CR-carrying files in result: ", 2, statuses.length);
        cleanupDFS();
    }

    @Test
    public void testMultiGlob() throws IOException {
        FileStatus[] status;
        /* /dir1/subdir1
         /dir1/subdir1/f1
         /dir1/subdir1/f2
         /dir1/subdir2/f1
         /dir2/subdir1
         /dir2/subdir2
         /dir2/subdir2/f1
         /dir3/f1
         /dir3/f1
         /dir3/f2(dir)
         /dir3/subdir2(file)
         /dir3/subdir3
         /dir3/subdir3/f1
         /dir3/subdir3/f1/f1
         /dir3/subdir3/f3
         /dir4
         */
        Path d1 = new Path(TestGlobPaths.USER_DIR, "dir1");
        Path d11 = new Path(d1, "subdir1");
        Path d12 = new Path(d1, "subdir2");
        Path f111 = new Path(d11, "f1");
        TestGlobPaths.fs.createNewFile(f111);
        Path f112 = new Path(d11, "f2");
        TestGlobPaths.fs.createNewFile(f112);
        Path f121 = new Path(d12, "f1");
        TestGlobPaths.fs.createNewFile(f121);
        Path d2 = new Path(TestGlobPaths.USER_DIR, "dir2");
        Path d21 = new Path(d2, "subdir1");
        TestGlobPaths.fs.mkdirs(d21);
        Path d22 = new Path(d2, "subdir2");
        Path f221 = new Path(d22, "f1");
        TestGlobPaths.fs.createNewFile(f221);
        Path d3 = new Path(TestGlobPaths.USER_DIR, "dir3");
        Path f31 = new Path(d3, "f1");
        TestGlobPaths.fs.createNewFile(f31);
        Path d32 = new Path(d3, "f2");
        TestGlobPaths.fs.mkdirs(d32);
        Path f32 = new Path(d3, "subdir2");// fake as a subdir!

        TestGlobPaths.fs.createNewFile(f32);
        Path d33 = new Path(d3, "subdir3");
        Path f333 = new Path(d33, "f3");
        TestGlobPaths.fs.createNewFile(f333);
        Path d331 = new Path(d33, "f1");
        Path f3311 = new Path(d331, "f1");
        TestGlobPaths.fs.createNewFile(f3311);
        Path d4 = new Path(TestGlobPaths.USER_DIR, "dir4");
        TestGlobPaths.fs.mkdirs(d4);
        /* basic */
        Path root = new Path(TestGlobPaths.USER_DIR);
        status = TestGlobPaths.fs.globStatus(root);
        checkStatus(status, root);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "x"));
        Assert.assertNull(status);
        status = TestGlobPaths.fs.globStatus(new Path("x"));
        Assert.assertNull(status);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "x/x"));
        Assert.assertNull(status);
        status = TestGlobPaths.fs.globStatus(new Path("x/x"));
        Assert.assertNull(status);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "*"));
        checkStatus(status, d1, d2, d3, d4);
        status = TestGlobPaths.fs.globStatus(new Path("*"));
        checkStatus(status, d1, d2, d3, d4);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "*/x"));
        checkStatus(status);
        status = TestGlobPaths.fs.globStatus(new Path("*/x"));
        checkStatus(status);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "x/*"));
        checkStatus(status);
        status = TestGlobPaths.fs.globStatus(new Path("x/*"));
        checkStatus(status);
        // make sure full pattern is scanned instead of bailing early with undef
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "x/x/x/*"));
        checkStatus(status);
        status = TestGlobPaths.fs.globStatus(new Path("x/x/x/*"));
        checkStatus(status);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "*/*"));
        checkStatus(status, d11, d12, d21, d22, f31, d32, f32, d33);
        status = TestGlobPaths.fs.globStatus(new Path("*/*"));
        checkStatus(status, d11, d12, d21, d22, f31, d32, f32, d33);
        /* one level deep */
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/*"));
        checkStatus(status, d11, d12, d21, d22, f31, d32, f32, d33);
        status = TestGlobPaths.fs.globStatus(new Path("dir*/*"));
        checkStatus(status, d11, d12, d21, d22, f31, d32, f32, d33);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir*"));
        checkStatus(status, d11, d12, d21, d22, f32, d33);
        status = TestGlobPaths.fs.globStatus(new Path("dir*/subdir*"));
        checkStatus(status, d11, d12, d21, d22, f32, d33);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/f*"));
        checkStatus(status, f31, d32);
        status = TestGlobPaths.fs.globStatus(new Path("dir*/f*"));
        checkStatus(status, f31, d32);
        /* subdir1 globs */
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir1"));
        checkStatus(status, d11, d21);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir1/*"));
        checkStatus(status, f111, f112);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir1/*/*"));
        checkStatus(status);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir1/x"));
        checkStatus(status);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir1/x*"));
        checkStatus(status);
        /* subdir2 globs */
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir2"));
        checkStatus(status, d12, d22, f32);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir2/*"));
        checkStatus(status, f121, f221);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir2/*/*"));
        checkStatus(status);
        /* subdir3 globs */
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir3"));
        checkStatus(status, d33);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir3/*"));
        checkStatus(status, d331, f333);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir3/*/*"));
        checkStatus(status, f3311);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir3/*/*/*"));
        checkStatus(status);
        /* file1 single dir globs */
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir1/f1"));
        checkStatus(status, f111);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir1/f1*"));
        checkStatus(status, f111);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir1/f1/*"));
        checkStatus(status);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir1/f1*/*"));
        checkStatus(status);
        /* file1 multi-dir globs */
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir*/f1"));
        checkStatus(status, f111, f121, f221, d331);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir*/f1*"));
        checkStatus(status, f111, f121, f221, d331);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir*/f1/*"));
        checkStatus(status, f3311);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir*/f1*/*"));
        checkStatus(status, f3311);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir*/f1*/*"));
        checkStatus(status, f3311);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir*/f1*/x"));
        checkStatus(status);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir*/f1*/*/*"));
        checkStatus(status);
        /* file glob multiple files */
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir*"));
        checkStatus(status, d11, d12, d21, d22, f32, d33);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir*/*"));
        checkStatus(status, f111, f112, f121, f221, d331, f333);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir*/f*"));
        checkStatus(status, f111, f112, f121, f221, d331, f333);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir*/f*/*"));
        checkStatus(status, f3311);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir*/*/f1"));
        checkStatus(status, f3311);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir*/*/*"));
        checkStatus(status, f3311);
        // doesn't exist
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir1/f3"));
        checkStatus(status);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "dir*/subdir1/f3*"));
        checkStatus(status);
        status = TestGlobPaths.fs.globStatus(new Path("{x}"));
        checkStatus(status);
        status = TestGlobPaths.fs.globStatus(new Path("{x,y}"));
        checkStatus(status);
        status = TestGlobPaths.fs.globStatus(new Path("dir*/{x,y}"));
        checkStatus(status);
        status = TestGlobPaths.fs.globStatus(new Path("dir*/{f1,y}"));
        checkStatus(status, f31);
        status = TestGlobPaths.fs.globStatus(new Path("{x,y}"));
        checkStatus(status);
        status = TestGlobPaths.fs.globStatus(new Path("/{x/x,y/y}"));
        checkStatus(status);
        status = TestGlobPaths.fs.globStatus(new Path("{x/x,y/y}"));
        checkStatus(status);
        status = TestGlobPaths.fs.globStatus(new Path(CUR_DIR));
        checkStatus(status, new Path(TestGlobPaths.USER_DIR));
        status = TestGlobPaths.fs.globStatus(new Path(((TestGlobPaths.USER_DIR) + "{/dir1}")));
        checkStatus(status, d1);
        status = TestGlobPaths.fs.globStatus(new Path(((TestGlobPaths.USER_DIR) + "{/dir*}")));
        checkStatus(status, d1, d2, d3, d4);
        status = TestGlobPaths.fs.globStatus(new Path(SEPARATOR), TestGlobPaths.trueFilter);
        checkStatus(status, new Path(SEPARATOR));
        status = TestGlobPaths.fs.globStatus(new Path(CUR_DIR), TestGlobPaths.trueFilter);
        checkStatus(status, new Path(TestGlobPaths.USER_DIR));
        status = TestGlobPaths.fs.globStatus(d1, TestGlobPaths.trueFilter);
        checkStatus(status, d1);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR), TestGlobPaths.trueFilter);
        checkStatus(status, new Path(TestGlobPaths.USER_DIR));
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "*"), TestGlobPaths.trueFilter);
        checkStatus(status, d1, d2, d3, d4);
        status = TestGlobPaths.fs.globStatus(new Path("/x/*"), TestGlobPaths.trueFilter);
        checkStatus(status);
        status = TestGlobPaths.fs.globStatus(new Path("/x"), TestGlobPaths.trueFilter);
        Assert.assertNull(status);
        status = TestGlobPaths.fs.globStatus(new Path("/x/x"), TestGlobPaths.trueFilter);
        Assert.assertNull(status);
        /* false filter */
        PathFilter falseFilter = new PathFilter() {
            @Override
            public boolean accept(Path path) {
                return false;
            }
        };
        status = TestGlobPaths.fs.globStatus(new Path(SEPARATOR), falseFilter);
        Assert.assertNull(status);
        status = TestGlobPaths.fs.globStatus(new Path(CUR_DIR), falseFilter);
        Assert.assertNull(status);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR), falseFilter);
        Assert.assertNull(status);
        status = TestGlobPaths.fs.globStatus(new Path(TestGlobPaths.USER_DIR, "*"), falseFilter);
        checkStatus(status);
        status = TestGlobPaths.fs.globStatus(new Path("/x/*"), falseFilter);
        checkStatus(status);
        status = TestGlobPaths.fs.globStatus(new Path("/x"), falseFilter);
        Assert.assertNull(status);
        status = TestGlobPaths.fs.globStatus(new Path("/x/x"), falseFilter);
        Assert.assertNull(status);
        cleanupDFS();
    }

    @Test
    public void testPathFilter() throws IOException {
        try {
            String[] files = new String[]{ (TestGlobPaths.USER_DIR) + "/a", (TestGlobPaths.USER_DIR) + "/a/b" };
            Path[] matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/*/*"), files, new TestGlobPaths.RegexPathFilter((("^.*" + (Pattern.quote(TestGlobPaths.USER_DIR))) + "/a/b")));
            Assert.assertEquals(1, matchedPath.length);
            Assert.assertEquals(path[1], matchedPath[0]);
        } finally {
            cleanupDFS();
        }
    }

    @Test
    public void testPathFilterWithFixedLastComponent() throws IOException {
        try {
            String[] files = new String[]{ (TestGlobPaths.USER_DIR) + "/a", (TestGlobPaths.USER_DIR) + "/a/b", (TestGlobPaths.USER_DIR) + "/c", (TestGlobPaths.USER_DIR) + "/c/b" };
            Path[] matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/*/b"), files, new TestGlobPaths.RegexPathFilter((("^.*" + (Pattern.quote(TestGlobPaths.USER_DIR))) + "/a/b")));
            Assert.assertEquals(matchedPath.length, 1);
            Assert.assertEquals(matchedPath[0], path[1]);
        } finally {
            cleanupDFS();
        }
    }

    @Test
    public void pTestLiteral() throws IOException {
        try {
            String[] files = new String[]{ (TestGlobPaths.USER_DIR) + "/a2c", (TestGlobPaths.USER_DIR) + "/abc.d" };
            Path[] matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/abc.d"), files);
            Assert.assertEquals(matchedPath.length, 1);
            Assert.assertEquals(matchedPath[0], path[1]);
        } finally {
            cleanupDFS();
        }
    }

    @Test
    public void pTestEscape() throws IOException {
        // Skip the test case on Windows because backslash will be treated as a
        // path separator instead of an escaping character on Windows.
        PlatformAssumptions.assumeNotWindows();
        try {
            String[] files = new String[]{ (TestGlobPaths.USER_DIR) + "/ab\\[c.d" };
            Path[] matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/ab\\[c.d"), files);
            Assert.assertEquals(matchedPath.length, 1);
            Assert.assertEquals(matchedPath[0], path[0]);
        } finally {
            cleanupDFS();
        }
    }

    @Test
    public void pTestAny() throws IOException {
        try {
            String[] files = new String[]{ (TestGlobPaths.USER_DIR) + "/abc", (TestGlobPaths.USER_DIR) + "/a2c", (TestGlobPaths.USER_DIR) + "/a.c", (TestGlobPaths.USER_DIR) + "/abcd" };
            Path[] matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/a?c"), files);
            Assert.assertEquals(matchedPath.length, 3);
            Assert.assertEquals(matchedPath[0], path[2]);
            Assert.assertEquals(matchedPath[1], path[1]);
            Assert.assertEquals(matchedPath[2], path[0]);
        } finally {
            cleanupDFS();
        }
    }

    @Test
    public void pTestClosure1() throws IOException {
        try {
            String[] files = new String[]{ (TestGlobPaths.USER_DIR) + "/a", (TestGlobPaths.USER_DIR) + "/abc", (TestGlobPaths.USER_DIR) + "/abc.p", (TestGlobPaths.USER_DIR) + "/bacd" };
            Path[] matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/a*"), files);
            Assert.assertEquals(matchedPath.length, 3);
            Assert.assertEquals(matchedPath[0], path[0]);
            Assert.assertEquals(matchedPath[1], path[1]);
            Assert.assertEquals(matchedPath[2], path[2]);
        } finally {
            cleanupDFS();
        }
    }

    @Test
    public void pTestClosure2() throws IOException {
        try {
            String[] files = new String[]{ (TestGlobPaths.USER_DIR) + "/a.", (TestGlobPaths.USER_DIR) + "/a.txt", (TestGlobPaths.USER_DIR) + "/a.old.java", (TestGlobPaths.USER_DIR) + "/.java" };
            Path[] matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/a.*"), files);
            Assert.assertEquals(matchedPath.length, 3);
            Assert.assertEquals(matchedPath[0], path[0]);
            Assert.assertEquals(matchedPath[1], path[2]);
            Assert.assertEquals(matchedPath[2], path[1]);
        } finally {
            cleanupDFS();
        }
    }

    @Test
    public void pTestClosure3() throws IOException {
        try {
            String[] files = new String[]{ (TestGlobPaths.USER_DIR) + "/a.txt.x", (TestGlobPaths.USER_DIR) + "/ax", (TestGlobPaths.USER_DIR) + "/ab37x", (TestGlobPaths.USER_DIR) + "/bacd" };
            Path[] matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/a*x"), files);
            Assert.assertEquals(matchedPath.length, 3);
            Assert.assertEquals(matchedPath[0], path[0]);
            Assert.assertEquals(matchedPath[1], path[2]);
            Assert.assertEquals(matchedPath[2], path[1]);
        } finally {
            cleanupDFS();
        }
    }

    @Test
    public void pTestClosure4() throws IOException {
        try {
            String[] files = new String[]{ (TestGlobPaths.USER_DIR) + "/dir1/file1", (TestGlobPaths.USER_DIR) + "/dir2/file2", (TestGlobPaths.USER_DIR) + "/dir3/file1" };
            Path[] matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/*/file1"), files);
            Assert.assertEquals(matchedPath.length, 2);
            Assert.assertEquals(matchedPath[0], path[0]);
            Assert.assertEquals(matchedPath[1], path[2]);
        } finally {
            cleanupDFS();
        }
    }

    @Test
    public void pTestClosure5() throws IOException {
        try {
            String[] files = new String[]{ (TestGlobPaths.USER_DIR) + "/dir1/file1", (TestGlobPaths.USER_DIR) + "/file1" };
            Path[] matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/*/file1"), files);
            Assert.assertEquals(matchedPath.length, 1);
            Assert.assertEquals(matchedPath[0], path[0]);
        } finally {
            cleanupDFS();
        }
    }

    @Test
    public void pTestSet() throws IOException {
        try {
            String[] files = new String[]{ (TestGlobPaths.USER_DIR) + "/a.c", (TestGlobPaths.USER_DIR) + "/a.cpp", (TestGlobPaths.USER_DIR) + "/a.hlp", (TestGlobPaths.USER_DIR) + "/a.hxy" };
            Path[] matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/a.[ch]??"), files);
            Assert.assertEquals(matchedPath.length, 3);
            Assert.assertEquals(matchedPath[0], path[1]);
            Assert.assertEquals(matchedPath[1], path[2]);
            Assert.assertEquals(matchedPath[2], path[3]);
        } finally {
            cleanupDFS();
        }
    }

    @Test
    public void pTestRange() throws IOException {
        try {
            String[] files = new String[]{ (TestGlobPaths.USER_DIR) + "/a.d", (TestGlobPaths.USER_DIR) + "/a.e", (TestGlobPaths.USER_DIR) + "/a.f", (TestGlobPaths.USER_DIR) + "/a.h" };
            Path[] matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/a.[d-fm]"), files);
            Assert.assertEquals(matchedPath.length, 3);
            Assert.assertEquals(matchedPath[0], path[0]);
            Assert.assertEquals(matchedPath[1], path[1]);
            Assert.assertEquals(matchedPath[2], path[2]);
        } finally {
            cleanupDFS();
        }
    }

    @Test
    public void pTestSetExcl() throws IOException {
        try {
            String[] files = new String[]{ (TestGlobPaths.USER_DIR) + "/a.d", (TestGlobPaths.USER_DIR) + "/a.e", (TestGlobPaths.USER_DIR) + "/a.0", (TestGlobPaths.USER_DIR) + "/a.h" };
            Path[] matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/a.[^a-cg-z0-9]"), files);
            Assert.assertEquals(matchedPath.length, 2);
            Assert.assertEquals(matchedPath[0], path[0]);
            Assert.assertEquals(matchedPath[1], path[1]);
        } finally {
            cleanupDFS();
        }
    }

    @Test
    public void pTestCombination() throws IOException {
        try {
            String[] files = new String[]{ "/user/aa/a.c", "/user/bb/a.cpp", "/user1/cc/b.hlp", "/user/dd/a.hxy" };
            Path[] matchedPath = prepareTesting("/use?/*/a.[ch]{lp,xy}", files);
            Assert.assertEquals(matchedPath.length, 1);
            Assert.assertEquals(matchedPath[0], path[3]);
        } finally {
            cleanupDFS();
        }
    }

    /* Test {xx,yy} */
    @Test
    public void pTestCurlyBracket() throws IOException {
        Path[] matchedPath;
        String[] files;
        try {
            files = new String[]{ (TestGlobPaths.USER_DIR) + "/a.abcxx", (TestGlobPaths.USER_DIR) + "/a.abxy", (TestGlobPaths.USER_DIR) + "/a.hlp", (TestGlobPaths.USER_DIR) + "/a.jhyy" };
            matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/a.{abc,jh}??"), files);
            Assert.assertEquals(matchedPath.length, 2);
            Assert.assertEquals(matchedPath[0], path[0]);
            Assert.assertEquals(matchedPath[1], path[3]);
        } finally {
            cleanupDFS();
        }
        // nested curlies
        try {
            files = new String[]{ (TestGlobPaths.USER_DIR) + "/a.abcxx", (TestGlobPaths.USER_DIR) + "/a.abdxy", (TestGlobPaths.USER_DIR) + "/a.hlp", (TestGlobPaths.USER_DIR) + "/a.jhyy" };
            matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/a.{ab{c,d},jh}??"), files);
            Assert.assertEquals(matchedPath.length, 3);
            Assert.assertEquals(matchedPath[0], path[0]);
            Assert.assertEquals(matchedPath[1], path[1]);
            Assert.assertEquals(matchedPath[2], path[3]);
        } finally {
            cleanupDFS();
        }
        // cross-component curlies
        try {
            files = new String[]{ (TestGlobPaths.USER_DIR) + "/a/b", (TestGlobPaths.USER_DIR) + "/a/d", (TestGlobPaths.USER_DIR) + "/c/b", (TestGlobPaths.USER_DIR) + "/c/d" };
            matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/{a/b,c/d}"), files);
            Assert.assertEquals(matchedPath.length, 2);
            Assert.assertEquals(matchedPath[0], path[0]);
            Assert.assertEquals(matchedPath[1], path[3]);
        } finally {
            cleanupDFS();
        }
        // cross-component absolute curlies
        try {
            files = new String[]{ "/a/b", "/a/d", "/c/b", "/c/d" };
            matchedPath = prepareTesting("{/a/b,/c/d}", files);
            Assert.assertEquals(matchedPath.length, 2);
            Assert.assertEquals(matchedPath[0], path[0]);
            Assert.assertEquals(matchedPath[1], path[3]);
        } finally {
            cleanupDFS();
        }
        try {
            // test standalone }
            files = new String[]{ (TestGlobPaths.USER_DIR) + "/}bc", (TestGlobPaths.USER_DIR) + "/}c" };
            matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/}{a,b}c"), files);
            Assert.assertEquals(matchedPath.length, 1);
            Assert.assertEquals(matchedPath[0], path[0]);
            // test {b}
            matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/}{b}c"), files);
            Assert.assertEquals(matchedPath.length, 1);
            Assert.assertEquals(matchedPath[0], path[0]);
            // test {}
            matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/}{}bc"), files);
            Assert.assertEquals(matchedPath.length, 1);
            Assert.assertEquals(matchedPath[0], path[0]);
            // test {,}
            matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/}{,}bc"), files);
            Assert.assertEquals(matchedPath.length, 1);
            Assert.assertEquals(matchedPath[0], path[0]);
            // test {b,}
            matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/}{b,}c"), files);
            Assert.assertEquals(matchedPath.length, 2);
            Assert.assertEquals(matchedPath[0], path[0]);
            Assert.assertEquals(matchedPath[1], path[1]);
            // test {,b}
            matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/}{,b}c"), files);
            Assert.assertEquals(matchedPath.length, 2);
            Assert.assertEquals(matchedPath[0], path[0]);
            Assert.assertEquals(matchedPath[1], path[1]);
            // test a combination of {} and ?
            matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/}{ac,?}"), files);
            Assert.assertEquals(matchedPath.length, 1);
            Assert.assertEquals(matchedPath[0], path[1]);
            // test ill-formed curly
            boolean hasException = false;
            try {
                prepareTesting(((TestGlobPaths.USER_DIR) + "}{bc"), files);
            } catch (IOException e) {
                Assert.assertTrue(e.getMessage().startsWith("Illegal file pattern:"));
                hasException = true;
            }
            Assert.assertTrue(hasException);
        } finally {
            cleanupDFS();
        }
    }

    /* test that a path name can contain Java regex special characters */
    @Test
    public void pTestJavaRegexSpecialChars() throws IOException {
        try {
            String[] files = new String[]{ (TestGlobPaths.USER_DIR) + "/($.|+)bc", (TestGlobPaths.USER_DIR) + "/abc" };
            Path[] matchedPath = prepareTesting(((TestGlobPaths.USER_DIR) + "/($.|+)*"), files);
            Assert.assertEquals(matchedPath.length, 1);
            Assert.assertEquals(matchedPath[0], path[0]);
        } finally {
            cleanupDFS();
        }
    }

    /**
     * A glob test that can be run on either FileContext or FileSystem.
     */
    private abstract class FSTestWrapperGlobTest {
        FSTestWrapperGlobTest(boolean useFc) {
            if (useFc) {
                this.privWrap = new FileContextTestWrapper(TestGlobPaths.privilegedFc);
                this.wrap = new FileContextTestWrapper(TestGlobPaths.fc);
            } else {
                this.privWrap = new FileSystemTestWrapper(TestGlobPaths.privilegedFs);
                this.wrap = new FileSystemTestWrapper(TestGlobPaths.fs);
            }
        }

        abstract void run() throws Exception;

        final FSTestWrapper privWrap;

        final FSTestWrapper wrap;
    }

    /**
     * Accept all paths.
     */
    private static class AcceptAllPathFilter implements PathFilter {
        @Override
        public boolean accept(Path path) {
            return true;
        }
    }

    private static final PathFilter trueFilter = new TestGlobPaths.AcceptAllPathFilter();

    /**
     * Accept only paths ending in Z.
     */
    private static class AcceptPathsEndingInZ implements PathFilter {
        @Override
        public boolean accept(Path path) {
            String stringPath = path.toUri().getPath();
            return stringPath.endsWith("z");
        }
    }

    /**
     * Test globbing through symlinks.
     */
    private class TestGlobWithSymlinks extends TestGlobPaths.FSTestWrapperGlobTest {
        TestGlobWithSymlinks(boolean useFc) {
            super(useFc);
        }

        void run() throws Exception {
            // Test that globbing through a symlink to a directory yields a path
            // containing that symlink.
            wrap.mkdir(new Path(((TestGlobPaths.USER_DIR) + "/alpha")), FsPermission.getDirDefault(), false);
            wrap.createSymlink(new Path(((TestGlobPaths.USER_DIR) + "/alpha")), new Path(((TestGlobPaths.USER_DIR) + "/alphaLink")), false);
            wrap.mkdir(new Path(((TestGlobPaths.USER_DIR) + "/alphaLink/beta")), FsPermission.getDirDefault(), false);
            // Test simple glob
            FileStatus[] statuses = wrap.globStatus(new Path(((TestGlobPaths.USER_DIR) + "/alpha/*")), new TestGlobPaths.AcceptAllPathFilter());
            Assert.assertEquals(1, statuses.length);
            Assert.assertEquals(((TestGlobPaths.USER_DIR) + "/alpha/beta"), statuses[0].getPath().toUri().getPath());
            // Test glob through symlink
            statuses = wrap.globStatus(new Path(((TestGlobPaths.USER_DIR) + "/alphaLink/*")), new TestGlobPaths.AcceptAllPathFilter());
            Assert.assertEquals(1, statuses.length);
            Assert.assertEquals(((TestGlobPaths.USER_DIR) + "/alphaLink/beta"), statuses[0].getPath().toUri().getPath());
            // If the terminal path component in a globbed path is a symlink,
            // we don't dereference that link.
            wrap.createSymlink(new Path("beta"), new Path(((TestGlobPaths.USER_DIR) + "/alphaLink/betaLink")), false);
            statuses = wrap.globStatus(new Path(((TestGlobPaths.USER_DIR) + "/alpha/betaLi*")), new TestGlobPaths.AcceptAllPathFilter());
            Assert.assertEquals(1, statuses.length);
            Assert.assertEquals(((TestGlobPaths.USER_DIR) + "/alpha/betaLink"), statuses[0].getPath().toUri().getPath());
            // todo: test symlink-to-symlink-to-dir, etc.
        }
    }

    /**
     * Test globbing symlinks to symlinks.
     *
     * Also test globbing dangling symlinks.  It should NOT throw any exceptions!
     */
    private class TestGlobWithSymlinksToSymlinks extends TestGlobPaths.FSTestWrapperGlobTest {
        TestGlobWithSymlinksToSymlinks(boolean useFc) {
            super(useFc);
        }

        void run() throws Exception {
            // Test that globbing through a symlink to a symlink to a directory
            // fully resolves
            wrap.mkdir(new Path(((TestGlobPaths.USER_DIR) + "/alpha")), FsPermission.getDirDefault(), false);
            wrap.createSymlink(new Path(((TestGlobPaths.USER_DIR) + "/alpha")), new Path(((TestGlobPaths.USER_DIR) + "/alphaLink")), false);
            wrap.createSymlink(new Path(((TestGlobPaths.USER_DIR) + "/alphaLink")), new Path(((TestGlobPaths.USER_DIR) + "/alphaLinkLink")), false);
            wrap.mkdir(new Path(((TestGlobPaths.USER_DIR) + "/alpha/beta")), FsPermission.getDirDefault(), false);
            // Test glob through symlink to a symlink to a directory
            FileStatus[] statuses = wrap.globStatus(new Path(((TestGlobPaths.USER_DIR) + "/alphaLinkLink")), new TestGlobPaths.AcceptAllPathFilter());
            Assert.assertEquals(1, statuses.length);
            Assert.assertEquals(((TestGlobPaths.USER_DIR) + "/alphaLinkLink"), statuses[0].getPath().toUri().getPath());
            statuses = wrap.globStatus(new Path(((TestGlobPaths.USER_DIR) + "/alphaLinkLink/*")), new TestGlobPaths.AcceptAllPathFilter());
            Assert.assertEquals(1, statuses.length);
            Assert.assertEquals(((TestGlobPaths.USER_DIR) + "/alphaLinkLink/beta"), statuses[0].getPath().toUri().getPath());
            // Test glob of dangling symlink (theta does not actually exist)
            wrap.createSymlink(new Path(((TestGlobPaths.USER_DIR) + "theta")), new Path(((TestGlobPaths.USER_DIR) + "/alpha/kappa")), false);
            statuses = wrap.globStatus(new Path(((TestGlobPaths.USER_DIR) + "/alpha/kappa/kappa")), new TestGlobPaths.AcceptAllPathFilter());
            Assert.assertNull(statuses);
            // Test glob of symlinks
            wrap.createFile(((TestGlobPaths.USER_DIR) + "/alpha/beta/gamma"));
            wrap.createSymlink(new Path(((TestGlobPaths.USER_DIR) + "gamma")), new Path(((TestGlobPaths.USER_DIR) + "/alpha/beta/gammaLink")), false);
            wrap.createSymlink(new Path(((TestGlobPaths.USER_DIR) + "gammaLink")), new Path(((TestGlobPaths.USER_DIR) + "/alpha/beta/gammaLinkLink")), false);
            wrap.createSymlink(new Path(((TestGlobPaths.USER_DIR) + "gammaLinkLink")), new Path(((TestGlobPaths.USER_DIR) + "/alpha/beta/gammaLinkLinkLink")), false);
            statuses = wrap.globStatus(new Path(((TestGlobPaths.USER_DIR) + "/alpha/*/gammaLinkLinkLink")), new TestGlobPaths.AcceptAllPathFilter());
            Assert.assertEquals(1, statuses.length);
            Assert.assertEquals(((TestGlobPaths.USER_DIR) + "/alpha/beta/gammaLinkLinkLink"), statuses[0].getPath().toUri().getPath());
            statuses = wrap.globStatus(new Path(((TestGlobPaths.USER_DIR) + "/alpha/beta/*")), new TestGlobPaths.AcceptAllPathFilter());
            Assert.assertEquals(((((((((TestGlobPaths.USER_DIR) + "/alpha/beta/gamma;") + (TestGlobPaths.USER_DIR)) + "/alpha/beta/gammaLink;") + (TestGlobPaths.USER_DIR)) + "/alpha/beta/gammaLinkLink;") + (TestGlobPaths.USER_DIR)) + "/alpha/beta/gammaLinkLinkLink"), TestPath.mergeStatuses(statuses));
            // Let's create two symlinks that point to each other, and glob on them.
            wrap.createSymlink(new Path(((TestGlobPaths.USER_DIR) + "tweedledee")), new Path(((TestGlobPaths.USER_DIR) + "/tweedledum")), false);
            wrap.createSymlink(new Path(((TestGlobPaths.USER_DIR) + "tweedledum")), new Path(((TestGlobPaths.USER_DIR) + "/tweedledee")), false);
            statuses = wrap.globStatus(new Path(((TestGlobPaths.USER_DIR) + "/tweedledee/unobtainium")), new TestGlobPaths.AcceptAllPathFilter());
            Assert.assertNull(statuses);
        }
    }

    /**
     * Test globbing symlinks with a custom PathFilter
     */
    private class TestGlobSymlinksWithCustomPathFilter extends TestGlobPaths.FSTestWrapperGlobTest {
        TestGlobSymlinksWithCustomPathFilter(boolean useFc) {
            super(useFc);
        }

        void run() throws Exception {
            // Test that globbing through a symlink to a symlink to a directory
            // fully resolves
            wrap.mkdir(new Path(((TestGlobPaths.USER_DIR) + "/alpha")), FsPermission.getDirDefault(), false);
            wrap.createSymlink(new Path(((TestGlobPaths.USER_DIR) + "/alpha")), new Path(((TestGlobPaths.USER_DIR) + "/alphaLinkz")), false);
            wrap.mkdir(new Path(((TestGlobPaths.USER_DIR) + "/alpha/beta")), FsPermission.getDirDefault(), false);
            wrap.mkdir(new Path(((TestGlobPaths.USER_DIR) + "/alpha/betaz")), FsPermission.getDirDefault(), false);
            // Test glob through symlink to a symlink to a directory, with a
            // PathFilter
            FileStatus[] statuses = wrap.globStatus(new Path(((TestGlobPaths.USER_DIR) + "/alpha/beta")), new TestGlobPaths.AcceptPathsEndingInZ());
            Assert.assertNull(statuses);
            statuses = wrap.globStatus(new Path(((TestGlobPaths.USER_DIR) + "/alphaLinkz/betaz")), new TestGlobPaths.AcceptPathsEndingInZ());
            Assert.assertEquals(1, statuses.length);
            Assert.assertEquals(((TestGlobPaths.USER_DIR) + "/alphaLinkz/betaz"), statuses[0].getPath().toUri().getPath());
            statuses = wrap.globStatus(new Path(((TestGlobPaths.USER_DIR) + "/*/*")), new TestGlobPaths.AcceptPathsEndingInZ());
            Assert.assertEquals(((((TestGlobPaths.USER_DIR) + "/alpha/betaz;") + (TestGlobPaths.USER_DIR)) + "/alphaLinkz/betaz"), TestPath.mergeStatuses(statuses));
            statuses = wrap.globStatus(new Path(((TestGlobPaths.USER_DIR) + "/*/*")), new TestGlobPaths.AcceptAllPathFilter());
            Assert.assertEquals(((((((((TestGlobPaths.USER_DIR) + "/alpha/beta;") + (TestGlobPaths.USER_DIR)) + "/alpha/betaz;") + (TestGlobPaths.USER_DIR)) + "/alphaLinkz/beta;") + (TestGlobPaths.USER_DIR)) + "/alphaLinkz/betaz"), TestPath.mergeStatuses(statuses));
        }
    }

    /**
     * Test that globStatus fills in the scheme even when it is not provided.
     */
    private class TestGlobFillsInScheme extends TestGlobPaths.FSTestWrapperGlobTest {
        TestGlobFillsInScheme(boolean useFc) {
            super(useFc);
        }

        void run() throws Exception {
            // Verify that the default scheme is hdfs, when we don't supply one.
            wrap.mkdir(new Path(((TestGlobPaths.USER_DIR) + "/alpha")), FsPermission.getDirDefault(), false);
            wrap.createSymlink(new Path(((TestGlobPaths.USER_DIR) + "/alpha")), new Path(((TestGlobPaths.USER_DIR) + "/alphaLink")), false);
            FileStatus[] statuses = wrap.globStatus(new Path(((TestGlobPaths.USER_DIR) + "/alphaLink")), new TestGlobPaths.AcceptAllPathFilter());
            Assert.assertEquals(1, statuses.length);
            Path path = statuses[0].getPath();
            Assert.assertEquals(((TestGlobPaths.USER_DIR) + "/alpha"), path.toUri().getPath());
            Assert.assertEquals("hdfs", path.toUri().getScheme());
            // FileContext can list a file:/// URI.
            // Since everyone should have the root directory, we list that.
            statuses = TestGlobPaths.fc.util().globStatus(new Path("file:///"), new TestGlobPaths.AcceptAllPathFilter());
            Assert.assertEquals(1, statuses.length);
            Path filePath = statuses[0].getPath();
            Assert.assertEquals("file", filePath.toUri().getScheme());
            Assert.assertEquals("/", filePath.toUri().getPath());
            // The FileSystem should have scheme 'hdfs'
            Assert.assertEquals("hdfs", TestGlobPaths.fs.getScheme());
        }
    }

    @Test
    public void testGlobFillsInSchemeOnFS() throws Exception {
        testOnFileSystem(new TestGlobPaths.TestGlobFillsInScheme(false));
    }

    @Test
    public void testGlobFillsInSchemeOnFC() throws Exception {
        testOnFileContext(new TestGlobPaths.TestGlobFillsInScheme(true));
    }

    /**
     * Test that globStatus works with relative paths.
     */
    private class TestRelativePath extends TestGlobPaths.FSTestWrapperGlobTest {
        TestRelativePath(boolean useFc) {
            super(useFc);
        }

        void run() throws Exception {
            String[] files = new String[]{ "a", "abc", "abc.p", "bacd" };
            Path[] path = new Path[files.length];
            for (int i = 0; i < (files.length); i++) {
                path[i] = wrap.makeQualified(new Path(files[i]));
                wrap.mkdir(path[i], FsPermission.getDirDefault(), true);
            }
            Path patternPath = new Path("a*");
            Path[] globResults = FileUtil.stat2Paths(wrap.globStatus(patternPath, new TestGlobPaths.AcceptAllPathFilter()), patternPath);
            for (int i = 0; i < (globResults.length); i++) {
                globResults[i] = wrap.makeQualified(globResults[i]);
            }
            Assert.assertEquals(globResults.length, 3);
            // The default working directory for FileSystem is the user's home
            // directory.  For FileContext, the default is based on the UNIX user that
            // started the jvm.  This is arguably a bug (see HADOOP-10944 for
            // details).  We work around it here by explicitly calling
            // getWorkingDirectory and going from there.
            String pwd = wrap.getWorkingDirectory().toUri().getPath();
            Assert.assertEquals((((((pwd + "/a;") + pwd) + "/abc;") + pwd) + "/abc.p"), TestPath.mergeStatuses(globResults));
        }
    }

    @Test
    public void testRelativePathOnFS() throws Exception {
        testOnFileSystem(new TestGlobPaths.TestRelativePath(false));
    }

    @Test
    public void testRelativePathOnFC() throws Exception {
        testOnFileContext(new TestGlobPaths.TestRelativePath(true));
    }

    /**
     * Test that trying to glob through a directory we don't have permission
     * to list fails with AccessControlException rather than succeeding or
     * throwing any other exception.
     */
    private class TestGlobAccessDenied extends TestGlobPaths.FSTestWrapperGlobTest {
        TestGlobAccessDenied(boolean useFc) {
            super(useFc);
        }

        void run() throws Exception {
            privWrap.mkdir(new Path("/nopermission/val"), new FsPermission(((short) (511))), true);
            privWrap.mkdir(new Path("/norestrictions/val"), new FsPermission(((short) (511))), true);
            privWrap.setPermission(new Path("/nopermission"), new FsPermission(((short) (0))));
            try {
                wrap.globStatus(new Path("/no*/*"), new TestGlobPaths.AcceptAllPathFilter());
                Assert.fail(("expected to get an AccessControlException when " + ("globbing through a directory we don't have permissions " + "to list.")));
            } catch (AccessControlException ioe) {
            }
            Assert.assertEquals("/norestrictions/val", TestPath.mergeStatuses(wrap.globStatus(new Path("/norestrictions/*"), new TestGlobPaths.AcceptAllPathFilter())));
        }
    }

    @Test
    public void testGlobAccessDeniedOnFS() throws Exception {
        testOnFileSystem(new TestGlobPaths.TestGlobAccessDenied(false));
    }

    @Test
    public void testGlobAccessDeniedOnFC() throws Exception {
        testOnFileContext(new TestGlobPaths.TestGlobAccessDenied(true));
    }

    /**
     * Test that trying to list a reserved path on HDFS via the globber works.
     */
    private class TestReservedHdfsPaths extends TestGlobPaths.FSTestWrapperGlobTest {
        TestReservedHdfsPaths(boolean useFc) {
            super(useFc);
        }

        void run() throws Exception {
            String reservedRoot = "/.reserved/.inodes/" + (INodeId.ROOT_INODE_ID);
            Assert.assertEquals(reservedRoot, TestPath.mergeStatuses(wrap.globStatus(new Path(reservedRoot), new TestGlobPaths.AcceptAllPathFilter())));
        }
    }

    @Test
    public void testReservedHdfsPathsOnFS() throws Exception {
        testOnFileSystem(new TestGlobPaths.TestReservedHdfsPaths(false));
    }

    @Test
    public void testReservedHdfsPathsOnFC() throws Exception {
        testOnFileContext(new TestGlobPaths.TestReservedHdfsPaths(true));
    }

    /**
     * Test trying to glob the root.  Regression test for HDFS-5888.
     */
    private class TestGlobRoot extends TestGlobPaths.FSTestWrapperGlobTest {
        TestGlobRoot(boolean useFc) {
            super(useFc);
        }

        void run() throws Exception {
            final Path rootPath = new Path("/");
            FileStatus oldRootStatus = wrap.getFileStatus(rootPath);
            String newOwner = UUID.randomUUID().toString();
            privWrap.setOwner(new Path("/"), newOwner, null);
            FileStatus[] status = wrap.globStatus(rootPath, new TestGlobPaths.AcceptAllPathFilter());
            Assert.assertEquals(1, status.length);
            Assert.assertEquals(newOwner, status[0].getOwner());
            privWrap.setOwner(new Path("/"), oldRootStatus.getOwner(), null);
        }
    }

    @Test
    public void testGlobRootOnFS() throws Exception {
        testOnFileSystem(new TestGlobPaths.TestGlobRoot(false));
    }

    @Test
    public void testGlobRootOnFC() throws Exception {
        testOnFileContext(new TestGlobPaths.TestGlobRoot(true));
    }

    /**
     * Test glob expressions that don't appear at the end of the path.  Regression
     * test for HADOOP-10957.
     */
    private class TestNonTerminalGlobs extends TestGlobPaths.FSTestWrapperGlobTest {
        TestNonTerminalGlobs(boolean useFc) {
            super(useFc);
        }

        void run() throws Exception {
            try {
                privWrap.mkdir(new Path("/filed_away/alpha"), new FsPermission(((short) (511))), true);
                privWrap.createFile(new Path("/filed"), 0);
                FileStatus[] statuses = wrap.globStatus(new Path("/filed*/alpha"), new TestGlobPaths.AcceptAllPathFilter());
                Assert.assertEquals(1, statuses.length);
                Assert.assertEquals("/filed_away/alpha", statuses[0].getPath().toUri().getPath());
                privWrap.mkdir(new Path("/filed_away/alphabet"), new FsPermission(((short) (511))), true);
                privWrap.mkdir(new Path("/filed_away/alphabet/abc"), new FsPermission(((short) (511))), true);
                statuses = wrap.globStatus(new Path("/filed*/alph*/*b*"), new TestGlobPaths.AcceptAllPathFilter());
                Assert.assertEquals(1, statuses.length);
                Assert.assertEquals("/filed_away/alphabet/abc", statuses[0].getPath().toUri().getPath());
            } finally {
                privWrap.delete(new Path("/filed"), true);
                privWrap.delete(new Path("/filed_away"), true);
            }
        }
    }

    @Test
    public void testNonTerminalGlobsOnFS() throws Exception {
        testOnFileSystem(new TestGlobPaths.TestNonTerminalGlobs(false));
    }

    @Test
    public void testNonTerminalGlobsOnFC() throws Exception {
        testOnFileContext(new TestGlobPaths.TestNonTerminalGlobs(true));
    }

    @Test
    public void testLocalFilesystem() throws Exception {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.getLocal(conf);
        String localTmp = System.getProperty("java.io.tmpdir");
        Path base = new Path(new Path(localTmp), UUID.randomUUID().toString());
        Assert.assertTrue(fs.mkdirs(base));
        Assert.assertTrue(fs.mkdirs(new Path(base, "e")));
        Assert.assertTrue(fs.mkdirs(new Path(base, "c")));
        Assert.assertTrue(fs.mkdirs(new Path(base, "a")));
        Assert.assertTrue(fs.mkdirs(new Path(base, "d")));
        Assert.assertTrue(fs.mkdirs(new Path(base, "b")));
        fs.deleteOnExit(base);
        FileStatus[] status = fs.globStatus(new Path(base, "*"));
        ArrayList list = new ArrayList();
        for (FileStatus f : status) {
            list.add(f.getPath().toString());
        }
        boolean sorted = Ordering.natural().isOrdered(list);
        Assert.assertTrue(sorted);
    }
}

