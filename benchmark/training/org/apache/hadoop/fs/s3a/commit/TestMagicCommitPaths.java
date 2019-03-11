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
package org.apache.hadoop.fs.s3a.commit;


import java.util.List;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link MagicCommitPaths} path operations.
 */
public class TestMagicCommitPaths extends Assert {
    private static final List<String> MAGIC_AT_ROOT = TestMagicCommitPaths.list(MAGIC);

    private static final List<String> MAGIC_AT_ROOT_WITH_CHILD = TestMagicCommitPaths.list(MAGIC, "child");

    private static final List<String> MAGIC_WITH_CHILD = TestMagicCommitPaths.list("parent", MAGIC, "child");

    private static final List<String> MAGIC_AT_WITHOUT_CHILD = TestMagicCommitPaths.list("parent", MAGIC);

    private static final List<String> DEEP_MAGIC = TestMagicCommitPaths.list("parent1", "parent2", MAGIC, "child1", "child2");

    public static final String[] EMPTY = new String[]{  };

    @Test
    public void testSplitPathEmpty() throws Throwable {
        intercept(IllegalArgumentException.class, () -> splitPathToElements(new Path("")));
    }

    @Test
    public void testSplitPathDoubleBackslash() {
        assertPathSplits("//", TestMagicCommitPaths.EMPTY);
    }

    @Test
    public void testSplitRootPath() {
        assertPathSplits("/", TestMagicCommitPaths.EMPTY);
    }

    @Test
    public void testSplitBasic() {
        assertPathSplits("/a/b/c", new String[]{ "a", "b", "c" });
    }

    @Test
    public void testSplitTrailingSlash() {
        assertPathSplits("/a/b/c/", new String[]{ "a", "b", "c" });
    }

    @Test
    public void testSplitShortPath() {
        assertPathSplits("/a", new String[]{ "a" });
    }

    @Test
    public void testSplitShortPathTrailingSlash() {
        assertPathSplits("/a/", new String[]{ "a" });
    }

    @Test
    public void testParentsMagicRoot() {
        assertParents(TestMagicCommitPaths.EMPTY, TestMagicCommitPaths.MAGIC_AT_ROOT);
    }

    @Test
    public void testChildrenMagicRoot() {
        assertChildren(TestMagicCommitPaths.EMPTY, TestMagicCommitPaths.MAGIC_AT_ROOT);
    }

    @Test
    public void testParentsMagicRootWithChild() {
        assertParents(TestMagicCommitPaths.EMPTY, TestMagicCommitPaths.MAGIC_AT_ROOT_WITH_CHILD);
    }

    @Test
    public void testChildMagicRootWithChild() {
        assertChildren(TestMagicCommitPaths.a("child"), TestMagicCommitPaths.MAGIC_AT_ROOT_WITH_CHILD);
    }

    @Test
    public void testChildrenMagicWithoutChild() {
        assertChildren(TestMagicCommitPaths.EMPTY, TestMagicCommitPaths.MAGIC_AT_WITHOUT_CHILD);
    }

    @Test
    public void testChildMagicWithChild() {
        assertChildren(TestMagicCommitPaths.a("child"), TestMagicCommitPaths.MAGIC_WITH_CHILD);
    }

    @Test
    public void testParentMagicWithChild() {
        assertParents(TestMagicCommitPaths.a("parent"), TestMagicCommitPaths.MAGIC_WITH_CHILD);
    }

    @Test
    public void testParentDeepMagic() {
        assertParents(TestMagicCommitPaths.a("parent1", "parent2"), TestMagicCommitPaths.DEEP_MAGIC);
    }

    @Test
    public void testChildrenDeepMagic() {
        assertChildren(TestMagicCommitPaths.a("child1", "child2"), TestMagicCommitPaths.DEEP_MAGIC);
    }

    @Test
    public void testLastElementEmpty() throws Throwable {
        intercept(IllegalArgumentException.class, () -> lastElement(new ArrayList<>(0)));
    }

    @Test
    public void testLastElementSingle() {
        Assert.assertEquals("first", lastElement(TestMagicCommitPaths.l("first")));
    }

    @Test
    public void testLastElementDouble() {
        Assert.assertEquals("2", lastElement(TestMagicCommitPaths.l("first", "2")));
    }

    @Test
    public void testFinalDestinationNoMagic() {
        Assert.assertEquals(TestMagicCommitPaths.l("first", "2"), finalDestination(TestMagicCommitPaths.l("first", "2")));
    }

    @Test
    public void testFinalDestinationMagic1() {
        Assert.assertEquals(TestMagicCommitPaths.l("first", "2"), finalDestination(TestMagicCommitPaths.l("first", MAGIC, "2")));
    }

    @Test
    public void testFinalDestinationMagic2() {
        Assert.assertEquals(TestMagicCommitPaths.l("first", "3.txt"), finalDestination(TestMagicCommitPaths.l("first", MAGIC, "2", "3.txt")));
    }

    @Test
    public void testFinalDestinationRootMagic2() {
        Assert.assertEquals(TestMagicCommitPaths.l("3.txt"), finalDestination(TestMagicCommitPaths.l(MAGIC, "2", "3.txt")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFinalDestinationMagicNoChild() {
        finalDestination(TestMagicCommitPaths.l(MAGIC));
    }

    @Test
    public void testFinalDestinationBaseDirectChild() {
        finalDestination(TestMagicCommitPaths.l(MAGIC, BASE, "3.txt"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFinalDestinationBaseNoChild() {
        Assert.assertEquals(TestMagicCommitPaths.l(), finalDestination(TestMagicCommitPaths.l(MAGIC, BASE)));
    }

    @Test
    public void testFinalDestinationBaseSubdirsChild() {
        Assert.assertEquals(TestMagicCommitPaths.l("2", "3.txt"), finalDestination(TestMagicCommitPaths.l(MAGIC, "4", BASE, "2", "3.txt")));
    }

    /**
     * If the base is above the magic dir, it's ignored.
     */
    @Test
    public void testFinalDestinationIgnoresBaseBeforeMagic() {
        Assert.assertEquals(TestMagicCommitPaths.l(BASE, "home", "3.txt"), finalDestination(TestMagicCommitPaths.l(BASE, "home", MAGIC, "2", "3.txt")));
    }
}

