/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.actions;


import Order.STABLE_ORDER;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.actions.Artifact.SpecialArtifact;
import com.google.devtools.build.lib.actions.Artifact.TreeFileArtifact;
import com.google.devtools.build.lib.analysis.actions.CustomCommandLine;
import com.google.devtools.build.lib.analysis.actions.CustomCommandLine.VectorArg;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.collect.nestedset.NestedSet;
import com.google.devtools.build.lib.collect.nestedset.NestedSetBuilder;
import com.google.devtools.build.lib.testutil.Scratch;
import com.google.devtools.build.lib.util.Fingerprint;
import com.google.devtools.build.lib.util.LazyString;
import com.google.devtools.build.lib.vfs.PathFragment;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for CustomCommandLine.
 */
@RunWith(JUnit4.class)
public class CustomCommandLineTest {
    private Scratch scratch;

    private ArtifactRoot rootDir;

    private Artifact artifact1;

    private Artifact artifact2;

    @Test
    public void testScalarAdds() throws Exception {
        assertThat(CustomCommandLine.builder().add("--arg").build().arguments()).containsExactly("--arg").inOrder();
        assertThat(CustomCommandLine.builder().addDynamicString("--arg").build().arguments()).containsExactly("--arg").inOrder();
        assertThat(CustomCommandLine.builder().addLabel(Label.parseAbsolute("//a:b", ImmutableMap.of())).build().arguments()).containsExactly("//a:b").inOrder();
        assertThat(CustomCommandLine.builder().addPath(PathFragment.create("path")).build().arguments()).containsExactly("path").inOrder();
        assertThat(CustomCommandLine.builder().addExecPath(artifact1).build().arguments()).containsExactly("dir/file1.txt").inOrder();
        assertThat(CustomCommandLine.builder().addLazyString(new LazyString() {
            @Override
            public String toString() {
                return "foo";
            }
        }).build().arguments()).containsExactly("foo").inOrder();
        assertThat(CustomCommandLine.builder().add("--arg", "val").build().arguments()).containsExactly("--arg", "val").inOrder();
        assertThat(CustomCommandLine.builder().addLabel("--arg", Label.parseAbsolute("//a:b", ImmutableMap.of())).build().arguments()).containsExactly("--arg", "//a:b").inOrder();
        assertThat(CustomCommandLine.builder().addPath("--arg", PathFragment.create("path")).build().arguments()).containsExactly("--arg", "path").inOrder();
        assertThat(CustomCommandLine.builder().addExecPath("--arg", artifact1).build().arguments()).containsExactly("--arg", "dir/file1.txt").inOrder();
        assertThat(CustomCommandLine.builder().addLazyString("--arg", new LazyString() {
            @Override
            public String toString() {
                return "foo";
            }
        }).build().arguments()).containsExactly("--arg", "foo").inOrder();
    }

    @Test
    public void testAddFormatted() throws Exception {
        assertThat(CustomCommandLine.builder().addFormatted("%s%s", "hello", "world").build().arguments()).containsExactly("helloworld").inOrder();
    }

    @Test
    public void testAddPrefixed() throws Exception {
        assertThat(CustomCommandLine.builder().addPrefixed("prefix-", "foo").build().arguments()).containsExactly("prefix-foo").inOrder();
        assertThat(CustomCommandLine.builder().addPrefixedLabel("prefix-", Label.parseAbsolute("//a:b", ImmutableMap.of())).build().arguments()).containsExactly("prefix-//a:b").inOrder();
        assertThat(CustomCommandLine.builder().addPrefixedPath("prefix-", PathFragment.create("path")).build().arguments()).containsExactly("prefix-path").inOrder();
        assertThat(CustomCommandLine.builder().addPrefixedExecPath("prefix-", artifact1).build().arguments()).containsExactly("prefix-dir/file1.txt").inOrder();
    }

    @Test
    public void testVectorAdds() throws Exception {
        assertThat(CustomCommandLine.builder().addAll(CustomCommandLineTest.list("val1", "val2")).build().arguments()).containsExactly("val1", "val2").inOrder();
        assertThat(CustomCommandLine.builder().addAll(CustomCommandLineTest.nestedSet("val1", "val2")).build().arguments()).containsExactly("val1", "val2").inOrder();
        assertThat(CustomCommandLine.builder().addPaths(CustomCommandLineTest.list(PathFragment.create("path1"), PathFragment.create("path2"))).build().arguments()).containsExactly("path1", "path2").inOrder();
        assertThat(CustomCommandLine.builder().addPaths(CustomCommandLineTest.nestedSet(PathFragment.create("path1"), PathFragment.create("path2"))).build().arguments()).containsExactly("path1", "path2").inOrder();
        assertThat(CustomCommandLine.builder().addExecPaths(CustomCommandLineTest.list(artifact1, artifact2)).build().arguments()).containsExactly("dir/file1.txt", "dir/file2.txt").inOrder();
        assertThat(CustomCommandLine.builder().addExecPaths(CustomCommandLineTest.nestedSet(artifact1, artifact2)).build().arguments()).containsExactly("dir/file1.txt", "dir/file2.txt").inOrder();
        assertThat(CustomCommandLine.builder().addAll(VectorArg.of(CustomCommandLineTest.list(CustomCommandLineTest.foo("1"), CustomCommandLineTest.foo("2"))).mapped(CustomCommandLineTest.Foo::expandToStr)).build().arguments()).containsExactly("1", "2").inOrder();
        assertThat(CustomCommandLine.builder().addAll(VectorArg.of(CustomCommandLineTest.nestedSet(CustomCommandLineTest.foo("1"), CustomCommandLineTest.foo("2"))).mapped(CustomCommandLineTest.Foo::expandToStr)).build().arguments()).containsExactly("1", "2").inOrder();
        assertThat(CustomCommandLine.builder().addAll("--arg", CustomCommandLineTest.list("val1", "val2")).build().arguments()).containsExactly("--arg", "val1", "val2").inOrder();
        assertThat(CustomCommandLine.builder().addAll("--arg", CustomCommandLineTest.nestedSet("val1", "val2")).build().arguments()).containsExactly("--arg", "val1", "val2").inOrder();
        assertThat(CustomCommandLine.builder().addPaths("--arg", CustomCommandLineTest.list(PathFragment.create("path1"), PathFragment.create("path2"))).build().arguments()).containsExactly("--arg", "path1", "path2").inOrder();
        assertThat(CustomCommandLine.builder().addPaths("--arg", CustomCommandLineTest.nestedSet(PathFragment.create("path1"), PathFragment.create("path2"))).build().arguments()).containsExactly("--arg", "path1", "path2").inOrder();
        assertThat(CustomCommandLine.builder().addExecPaths("--arg", CustomCommandLineTest.list(artifact1, artifact2)).build().arguments()).containsExactly("--arg", "dir/file1.txt", "dir/file2.txt").inOrder();
        assertThat(CustomCommandLine.builder().addExecPaths("--arg", CustomCommandLineTest.nestedSet(artifact1, artifact2)).build().arguments()).containsExactly("--arg", "dir/file1.txt", "dir/file2.txt").inOrder();
        assertThat(CustomCommandLine.builder().addAll("--arg", VectorArg.of(CustomCommandLineTest.list(CustomCommandLineTest.foo("1"), CustomCommandLineTest.foo("2"))).mapped(CustomCommandLineTest.Foo::expandToStr)).build().arguments()).containsExactly("--arg", "1", "2").inOrder();
        assertThat(CustomCommandLine.builder().addAll("--arg", VectorArg.of(CustomCommandLineTest.nestedSet(CustomCommandLineTest.foo("1"), CustomCommandLineTest.foo("2"))).mapped(CustomCommandLineTest.Foo::expandToStr)).build().arguments()).containsExactly("--arg", "1", "2").inOrder();
    }

    @Test
    public void testAddJoined() throws Exception {
        assertThat(CustomCommandLine.builder().addAll(VectorArg.join(":").each(CustomCommandLineTest.list("val1", "val2"))).build().arguments()).containsExactly("val1:val2").inOrder();
        assertThat(CustomCommandLine.builder().addAll(VectorArg.join(":").each(CustomCommandLineTest.nestedSet("val1", "val2"))).build().arguments()).containsExactly("val1:val2").inOrder();
        assertThat(CustomCommandLine.builder().addPaths(VectorArg.join(":").each(CustomCommandLineTest.list(PathFragment.create("path1"), PathFragment.create("path2")))).build().arguments()).containsExactly("path1:path2").inOrder();
        assertThat(CustomCommandLine.builder().addPaths(VectorArg.join(":").each(CustomCommandLineTest.nestedSet(PathFragment.create("path1"), PathFragment.create("path2")))).build().arguments()).containsExactly("path1:path2").inOrder();
        assertThat(CustomCommandLine.builder().addExecPaths(VectorArg.join(":").each(CustomCommandLineTest.list(artifact1, artifact2))).build().arguments()).containsExactly("dir/file1.txt:dir/file2.txt").inOrder();
        assertThat(CustomCommandLine.builder().addExecPaths(VectorArg.join(":").each(CustomCommandLineTest.nestedSet(artifact1, artifact2))).build().arguments()).containsExactly("dir/file1.txt:dir/file2.txt").inOrder();
        assertThat(CustomCommandLine.builder().addAll(VectorArg.join(":").each(CustomCommandLineTest.list(CustomCommandLineTest.foo("1"), CustomCommandLineTest.foo("2"))).mapped(CustomCommandLineTest.Foo::expandToStr)).build().arguments()).containsExactly("1:2").inOrder();
        assertThat(CustomCommandLine.builder().addAll(VectorArg.join(":").each(CustomCommandLineTest.nestedSet(CustomCommandLineTest.foo("1"), CustomCommandLineTest.foo("2"))).mapped(CustomCommandLineTest.Foo::expandToStr)).build().arguments()).containsExactly("1:2").inOrder();
        assertThat(CustomCommandLine.builder().addAll("--arg", VectorArg.join(":").each(CustomCommandLineTest.list("val1", "val2"))).build().arguments()).containsExactly("--arg", "val1:val2").inOrder();
        assertThat(CustomCommandLine.builder().addAll("--arg", VectorArg.join(":").each(CustomCommandLineTest.nestedSet("val1", "val2"))).build().arguments()).containsExactly("--arg", "val1:val2").inOrder();
        assertThat(CustomCommandLine.builder().addPaths("--arg", VectorArg.join(":").each(CustomCommandLineTest.list(PathFragment.create("path1"), PathFragment.create("path2")))).build().arguments()).containsExactly("--arg", "path1:path2").inOrder();
        assertThat(CustomCommandLine.builder().addPaths("--arg", VectorArg.join(":").each(CustomCommandLineTest.nestedSet(PathFragment.create("path1"), PathFragment.create("path2")))).build().arguments()).containsExactly("--arg", "path1:path2").inOrder();
        assertThat(CustomCommandLine.builder().addExecPaths("--arg", VectorArg.join(":").each(CustomCommandLineTest.list(artifact1, artifact2))).build().arguments()).containsExactly("--arg", "dir/file1.txt:dir/file2.txt").inOrder();
        assertThat(CustomCommandLine.builder().addExecPaths("--arg", VectorArg.join(":").each(CustomCommandLineTest.nestedSet(artifact1, artifact2))).build().arguments()).containsExactly("--arg", "dir/file1.txt:dir/file2.txt").inOrder();
        assertThat(CustomCommandLine.builder().addAll("--arg", VectorArg.join(":").each(CustomCommandLineTest.list(CustomCommandLineTest.foo("1"), CustomCommandLineTest.foo("2"))).mapped(CustomCommandLineTest.Foo::expandToStr)).build().arguments()).containsExactly("--arg", "1:2").inOrder();
        assertThat(CustomCommandLine.builder().addAll("--arg", VectorArg.join(":").each(CustomCommandLineTest.nestedSet(CustomCommandLineTest.foo("1"), CustomCommandLineTest.foo("2"))).mapped(CustomCommandLineTest.Foo::expandToStr)).build().arguments()).containsExactly("--arg", "1:2").inOrder();
    }

    @Test
    public void testAddFormatEach() throws Exception {
        assertThat(CustomCommandLine.builder().addAll(VectorArg.format("-D%s").each(CustomCommandLineTest.list("val1", "val2"))).build().arguments()).containsExactly("-Dval1", "-Dval2").inOrder();
        assertThat(CustomCommandLine.builder().addAll(VectorArg.format("-D%s").each(CustomCommandLineTest.nestedSet("val1", "val2"))).build().arguments()).containsExactly("-Dval1", "-Dval2").inOrder();
        assertThat(CustomCommandLine.builder().addPaths(VectorArg.format("-D%s").each(CustomCommandLineTest.list(PathFragment.create("path1"), PathFragment.create("path2")))).build().arguments()).containsExactly("-Dpath1", "-Dpath2").inOrder();
        assertThat(CustomCommandLine.builder().addPaths(VectorArg.format("-D%s").each(CustomCommandLineTest.nestedSet(PathFragment.create("path1"), PathFragment.create("path2")))).build().arguments()).containsExactly("-Dpath1", "-Dpath2").inOrder();
        assertThat(CustomCommandLine.builder().addExecPaths(VectorArg.format("-D%s").each(CustomCommandLineTest.list(artifact1, artifact2))).build().arguments()).containsExactly("-Ddir/file1.txt", "-Ddir/file2.txt").inOrder();
        assertThat(CustomCommandLine.builder().addExecPaths(VectorArg.format("-D%s").each(CustomCommandLineTest.nestedSet(artifact1, artifact2))).build().arguments()).containsExactly("-Ddir/file1.txt", "-Ddir/file2.txt").inOrder();
        assertThat(CustomCommandLine.builder().addAll(VectorArg.format("-D%s").each(CustomCommandLineTest.list(CustomCommandLineTest.foo("1"), CustomCommandLineTest.foo("2"))).mapped(CustomCommandLineTest.Foo::expandToStr)).build().arguments()).containsExactly("-D1", "-D2").inOrder();
        assertThat(CustomCommandLine.builder().addAll(VectorArg.format("-D%s").each(CustomCommandLineTest.nestedSet(CustomCommandLineTest.foo("1"), CustomCommandLineTest.foo("2"))).mapped(CustomCommandLineTest.Foo::expandToStr)).build().arguments()).containsExactly("-D1", "-D2").inOrder();
        assertThat(CustomCommandLine.builder().addAll("--arg", VectorArg.format("-D%s").each(CustomCommandLineTest.list("val1", "val2"))).build().arguments()).containsExactly("--arg", "-Dval1", "-Dval2").inOrder();
        assertThat(CustomCommandLine.builder().addAll("--arg", VectorArg.format("-D%s").each(CustomCommandLineTest.nestedSet("val1", "val2"))).build().arguments()).containsExactly("--arg", "-Dval1", "-Dval2").inOrder();
        assertThat(CustomCommandLine.builder().addPaths("--arg", VectorArg.format("-D%s").each(CustomCommandLineTest.list(PathFragment.create("path1"), PathFragment.create("path2")))).build().arguments()).containsExactly("--arg", "-Dpath1", "-Dpath2").inOrder();
        assertThat(CustomCommandLine.builder().addPaths("--arg", VectorArg.format("-D%s").each(CustomCommandLineTest.nestedSet(PathFragment.create("path1"), PathFragment.create("path2")))).build().arguments()).containsExactly("--arg", "-Dpath1", "-Dpath2").inOrder();
        assertThat(CustomCommandLine.builder().addExecPaths("--arg", VectorArg.format("-D%s").each(CustomCommandLineTest.list(artifact1, artifact2))).build().arguments()).containsExactly("--arg", "-Ddir/file1.txt", "-Ddir/file2.txt").inOrder();
        assertThat(CustomCommandLine.builder().addExecPaths("--arg", VectorArg.format("-D%s").each(CustomCommandLineTest.nestedSet(artifact1, artifact2))).build().arguments()).containsExactly("--arg", "-Ddir/file1.txt", "-Ddir/file2.txt").inOrder();
        assertThat(CustomCommandLine.builder().addAll("--arg", VectorArg.format("-D%s").each(CustomCommandLineTest.list(CustomCommandLineTest.foo("1"), CustomCommandLineTest.foo("2"))).mapped(CustomCommandLineTest.Foo::expandToStr)).build().arguments()).containsExactly("--arg", "-D1", "-D2").inOrder();
        assertThat(CustomCommandLine.builder().addAll("--arg", VectorArg.format("-D%s").each(CustomCommandLineTest.nestedSet(CustomCommandLineTest.foo("1"), CustomCommandLineTest.foo("2"))).mapped(CustomCommandLineTest.Foo::expandToStr)).build().arguments()).containsExactly("--arg", "-D1", "-D2").inOrder();
    }

    @Test
    public void testAddFormatEachJoined() throws Exception {
        assertThat(CustomCommandLine.builder().addAll(VectorArg.format("-D%s").join(":").each(CustomCommandLineTest.list("val1", "val2"))).build().arguments()).containsExactly("-Dval1:-Dval2").inOrder();
        assertThat(CustomCommandLine.builder().addAll(VectorArg.format("-D%s").join(":").each(CustomCommandLineTest.nestedSet("val1", "val2"))).build().arguments()).containsExactly("-Dval1:-Dval2").inOrder();
        assertThat(CustomCommandLine.builder().addPaths(VectorArg.format("-D%s").join(":").each(CustomCommandLineTest.list(PathFragment.create("path1"), PathFragment.create("path2")))).build().arguments()).containsExactly("-Dpath1:-Dpath2").inOrder();
        assertThat(CustomCommandLine.builder().addPaths(VectorArg.format("-D%s").join(":").each(CustomCommandLineTest.nestedSet(PathFragment.create("path1"), PathFragment.create("path2")))).build().arguments()).containsExactly("-Dpath1:-Dpath2").inOrder();
        assertThat(CustomCommandLine.builder().addExecPaths(VectorArg.format("-D%s").join(":").each(CustomCommandLineTest.list(artifact1, artifact2))).build().arguments()).containsExactly("-Ddir/file1.txt:-Ddir/file2.txt").inOrder();
        assertThat(CustomCommandLine.builder().addExecPaths(VectorArg.format("-D%s").join(":").each(CustomCommandLineTest.nestedSet(artifact1, artifact2))).build().arguments()).containsExactly("-Ddir/file1.txt:-Ddir/file2.txt").inOrder();
        assertThat(CustomCommandLine.builder().addAll(VectorArg.format("-D%s").join(":").each(CustomCommandLineTest.list(CustomCommandLineTest.foo("1"), CustomCommandLineTest.foo("2"))).mapped(CustomCommandLineTest.Foo::expandToStr)).build().arguments()).containsExactly("-D1:-D2").inOrder();
        assertThat(CustomCommandLine.builder().addAll(VectorArg.format("-D%s").join(":").each(CustomCommandLineTest.nestedSet(CustomCommandLineTest.foo("1"), CustomCommandLineTest.foo("2"))).mapped(CustomCommandLineTest.Foo::expandToStr)).build().arguments()).containsExactly("-D1:-D2").inOrder();
        assertThat(CustomCommandLine.builder().addAll("--arg", VectorArg.format("-D%s").join(":").each(CustomCommandLineTest.list("val1", "val2"))).build().arguments()).containsExactly("--arg", "-Dval1:-Dval2").inOrder();
        assertThat(CustomCommandLine.builder().addAll("--arg", VectorArg.format("-D%s").join(":").each(CustomCommandLineTest.nestedSet("val1", "val2"))).build().arguments()).containsExactly("--arg", "-Dval1:-Dval2").inOrder();
        assertThat(CustomCommandLine.builder().addPaths("--arg", VectorArg.format("-D%s").join(":").each(CustomCommandLineTest.list(PathFragment.create("path1"), PathFragment.create("path2")))).build().arguments()).containsExactly("--arg", "-Dpath1:-Dpath2").inOrder();
        assertThat(CustomCommandLine.builder().addPaths("--arg", VectorArg.format("-D%s").join(":").each(CustomCommandLineTest.nestedSet(PathFragment.create("path1"), PathFragment.create("path2")))).build().arguments()).containsExactly("--arg", "-Dpath1:-Dpath2").inOrder();
        assertThat(CustomCommandLine.builder().addExecPaths("--arg", VectorArg.format("-D%s").join(":").each(CustomCommandLineTest.list(artifact1, artifact2))).build().arguments()).containsExactly("--arg", "-Ddir/file1.txt:-Ddir/file2.txt").inOrder();
        assertThat(CustomCommandLine.builder().addExecPaths("--arg", VectorArg.format("-D%s").join(":").each(CustomCommandLineTest.nestedSet(artifact1, artifact2))).build().arguments()).containsExactly("--arg", "-Ddir/file1.txt:-Ddir/file2.txt").inOrder();
        assertThat(CustomCommandLine.builder().addAll("--arg", VectorArg.format("-D%s").join(":").each(CustomCommandLineTest.list(CustomCommandLineTest.foo("1"), CustomCommandLineTest.foo("2"))).mapped(CustomCommandLineTest.Foo::expandToStr)).build().arguments()).containsExactly("--arg", "-D1:-D2").inOrder();
        assertThat(CustomCommandLine.builder().addAll("--arg", VectorArg.format("-D%s").join(":").each(CustomCommandLineTest.nestedSet(CustomCommandLineTest.foo("1"), CustomCommandLineTest.foo("2"))).mapped(CustomCommandLineTest.Foo::expandToStr)).build().arguments()).containsExactly("--arg", "-D1:-D2").inOrder();
    }

    @Test
    public void testAddBeforeEach() throws Exception {
        assertThat(CustomCommandLine.builder().addAll(VectorArg.addBefore("-D").each(CustomCommandLineTest.list("val1", "val2"))).build().arguments()).containsExactly("-D", "val1", "-D", "val2").inOrder();
        assertThat(CustomCommandLine.builder().addAll(VectorArg.addBefore("-D").each(CustomCommandLineTest.nestedSet("val1", "val2"))).build().arguments()).containsExactly("-D", "val1", "-D", "val2").inOrder();
        assertThat(CustomCommandLine.builder().addPaths(VectorArg.addBefore("-D").each(CustomCommandLineTest.list(PathFragment.create("path1"), PathFragment.create("path2")))).build().arguments()).containsExactly("-D", "path1", "-D", "path2").inOrder();
        assertThat(CustomCommandLine.builder().addPaths(VectorArg.addBefore("-D").each(CustomCommandLineTest.nestedSet(PathFragment.create("path1"), PathFragment.create("path2")))).build().arguments()).containsExactly("-D", "path1", "-D", "path2").inOrder();
        assertThat(CustomCommandLine.builder().addExecPaths(VectorArg.addBefore("-D").each(CustomCommandLineTest.list(artifact1, artifact2))).build().arguments()).containsExactly("-D", "dir/file1.txt", "-D", "dir/file2.txt").inOrder();
        assertThat(CustomCommandLine.builder().addExecPaths(VectorArg.addBefore("-D").each(CustomCommandLineTest.nestedSet(artifact1, artifact2))).build().arguments()).containsExactly("-D", "dir/file1.txt", "-D", "dir/file2.txt").inOrder();
        assertThat(CustomCommandLine.builder().addAll(VectorArg.addBefore("-D").each(CustomCommandLineTest.list(CustomCommandLineTest.foo("1"), CustomCommandLineTest.foo("2"))).mapped(CustomCommandLineTest.Foo::expandToStr)).build().arguments()).containsExactly("-D", "1", "-D", "2").inOrder();
        assertThat(CustomCommandLine.builder().addAll(VectorArg.addBefore("-D").each(CustomCommandLineTest.nestedSet(CustomCommandLineTest.foo("1"), CustomCommandLineTest.foo("2"))).mapped(CustomCommandLineTest.Foo::expandToStr)).build().arguments()).containsExactly("-D", "1", "-D", "2").inOrder();
    }

    @Test
    public void testAddBeforeEachFormatted() throws Exception {
        assertThat(CustomCommandLine.builder().addAll(VectorArg.addBefore("-D").format("D%s").each(CustomCommandLineTest.list("val1", "val2"))).build().arguments()).containsExactly("-D", "Dval1", "-D", "Dval2").inOrder();
        assertThat(CustomCommandLine.builder().addAll(VectorArg.addBefore("-D").format("D%s").each(CustomCommandLineTest.nestedSet("val1", "val2"))).build().arguments()).containsExactly("-D", "Dval1", "-D", "Dval2").inOrder();
        assertThat(CustomCommandLine.builder().addPaths(VectorArg.addBefore("-D").format("D%s").each(CustomCommandLineTest.list(PathFragment.create("path1"), PathFragment.create("path2")))).build().arguments()).containsExactly("-D", "Dpath1", "-D", "Dpath2").inOrder();
        assertThat(CustomCommandLine.builder().addPaths(VectorArg.addBefore("-D").format("D%s").each(CustomCommandLineTest.nestedSet(PathFragment.create("path1"), PathFragment.create("path2")))).build().arguments()).containsExactly("-D", "Dpath1", "-D", "Dpath2").inOrder();
        assertThat(CustomCommandLine.builder().addExecPaths(VectorArg.addBefore("-D").format("D%s").each(CustomCommandLineTest.list(artifact1, artifact2))).build().arguments()).containsExactly("-D", "Ddir/file1.txt", "-D", "Ddir/file2.txt").inOrder();
        assertThat(CustomCommandLine.builder().addExecPaths(VectorArg.addBefore("-D").format("D%s").each(CustomCommandLineTest.nestedSet(artifact1, artifact2))).build().arguments()).containsExactly("-D", "Ddir/file1.txt", "-D", "Ddir/file2.txt").inOrder();
        assertThat(CustomCommandLine.builder().addAll(VectorArg.addBefore("-D").format("D%s").each(CustomCommandLineTest.list(CustomCommandLineTest.foo("1"), CustomCommandLineTest.foo("2"))).mapped(CustomCommandLineTest.Foo::expandToStr)).build().arguments()).containsExactly("-D", "D1", "-D", "D2").inOrder();
        assertThat(CustomCommandLine.builder().addAll(VectorArg.addBefore("-D").format("D%s").each(CustomCommandLineTest.nestedSet(CustomCommandLineTest.foo("1"), CustomCommandLineTest.foo("2"))).mapped(CustomCommandLineTest.Foo::expandToStr)).build().arguments()).containsExactly("-D", "D1", "-D", "D2").inOrder();
    }

    @Test
    public void testCombinedArgs() {
        CustomCommandLine cl = CustomCommandLine.builder().add("--arg").addAll("--args", ImmutableList.of("abc")).addExecPaths("--path1", ImmutableList.of(artifact1)).addExecPath("--path2", artifact2).build();
        assertThat(cl.arguments()).containsExactly("--arg", "--args", "abc", "--path1", "dir/file1.txt", "--path2", "dir/file2.txt").inOrder();
    }

    @Test
    public void testAddNulls() throws Exception {
        Artifact treeArtifact = createTreeArtifact("myTreeArtifact");
        assertThat(treeArtifact).isNotNull();
        CustomCommandLine cl = CustomCommandLine.builder().addDynamicString(null).addLabel(null).addPath(null).addExecPath(null).addLazyString(null).add("foo", null).addLabel("foo", null).addPath("foo", null).addExecPath("foo", null).addLazyString("foo", null).addPrefixed("prefix", null).addPrefixedLabel("prefix", null).addPrefixedPath("prefix", null).addPrefixedExecPath("prefix", null).addAll(((ImmutableList<String>) (null))).addAll(ImmutableList.of()).addPaths(((ImmutableList<PathFragment>) (null))).addPaths(ImmutableList.of()).addExecPaths(((ImmutableList<Artifact>) (null))).addExecPaths(ImmutableList.of()).addAll(((NestedSet<String>) (null))).addAll(NestedSetBuilder.emptySet(STABLE_ORDER)).addPaths(((NestedSet<PathFragment>) (null))).addPaths(NestedSetBuilder.emptySet(STABLE_ORDER)).addExecPaths(((NestedSet<Artifact>) (null))).addExecPaths(NestedSetBuilder.emptySet(STABLE_ORDER)).addAll(VectorArg.of(((NestedSet<String>) (null)))).addAll(VectorArg.of(NestedSetBuilder.<String>emptySet(STABLE_ORDER))).addAll("foo", ((ImmutableList<String>) (null))).addAll("foo", ImmutableList.of()).addPaths("foo", ((ImmutableList<PathFragment>) (null))).addPaths("foo", ImmutableList.of()).addExecPaths("foo", ((ImmutableList<Artifact>) (null))).addExecPaths("foo", ImmutableList.of()).addAll("foo", ((NestedSet<String>) (null))).addAll("foo", NestedSetBuilder.emptySet(STABLE_ORDER)).addPaths("foo", ((NestedSet<PathFragment>) (null))).addPaths("foo", NestedSetBuilder.emptySet(STABLE_ORDER)).addExecPaths("foo", ((NestedSet<Artifact>) (null))).addExecPaths("foo", NestedSetBuilder.emptySet(STABLE_ORDER)).addAll("foo", VectorArg.of(((NestedSet<String>) (null)))).addAll("foo", VectorArg.of(NestedSetBuilder.<String>emptySet(STABLE_ORDER))).addPlaceholderTreeArtifactExecPath("foo", null).build();
        assertThat(cl.arguments()).isEmpty();
    }

    @Test
    public void testTreeFileArtifactExecPathArgs() {
        SpecialArtifact treeArtifactOne = createTreeArtifact("myArtifact/treeArtifact1");
        SpecialArtifact treeArtifactTwo = createTreeArtifact("myArtifact/treeArtifact2");
        CustomCommandLine commandLineTemplate = CustomCommandLine.builder().addPlaceholderTreeArtifactExecPath("--argOne", treeArtifactOne).addPlaceholderTreeArtifactExecPath("--argTwo", treeArtifactTwo).build();
        TreeFileArtifact treeFileArtifactOne = createTreeFileArtifact(treeArtifactOne, "children/child1");
        TreeFileArtifact treeFileArtifactTwo = createTreeFileArtifact(treeArtifactTwo, "children/child2");
        CustomCommandLine commandLine = commandLineTemplate.evaluateTreeFileArtifacts(ImmutableList.of(treeFileArtifactOne, treeFileArtifactTwo));
        assertThat(commandLine.arguments()).containsExactly("--argOne", "myArtifact/treeArtifact1/children/child1", "--argTwo", "myArtifact/treeArtifact2/children/child2").inOrder();
    }

    @Test
    public void testKeyComputation() {
        NestedSet<String> values = NestedSetBuilder.<String>stableOrder().add("a").add("b").build();
        ImmutableList<CustomCommandLine> commandLines = ImmutableList.<CustomCommandLine>builder().add(CustomCommandLine.builder().add("arg").build()).add(CustomCommandLine.builder().addFormatted("--foo=%s", "arg").build()).add(CustomCommandLine.builder().addPrefixed("--foo=%s", "arg").build()).add(CustomCommandLine.builder().addAll(values).build()).add(CustomCommandLine.builder().addAll(VectorArg.addBefore("--foo=%s").each(values)).build()).add(CustomCommandLine.builder().addAll(VectorArg.join("--foo=%s").each(values)).build()).add(CustomCommandLine.builder().addAll(VectorArg.format("--foo=%s").each(values)).build()).add(CustomCommandLine.builder().addAll(VectorArg.of(values).mapped(( s, args) -> args.accept((s + "_mapped")))).build()).build();
        // Ensure all these command lines have distinct keys
        ActionKeyContext actionKeyContext = new ActionKeyContext();
        Map<String, CustomCommandLine> digests = new HashMap<>();
        for (CustomCommandLine commandLine : commandLines) {
            Fingerprint fingerprint = new Fingerprint();
            commandLine.addToFingerprint(actionKeyContext, fingerprint);
            String digest = fingerprint.hexDigestAndReset();
            CustomCommandLine previous = digests.putIfAbsent(digest, commandLine);
            if (previous != null) {
                Assert.fail(String.format("Found two command lines with identical digest %s: '%s' and '%s'", digest, Joiner.on(' ').join(previous.arguments()), Joiner.on(' ').join(commandLine.arguments())));
            }
        }
    }

    @Test
    public void testTreeFileArtifactArgThrowWithoutSubstitution() {
        Artifact treeArtifactOne = createTreeArtifact("myArtifact/treeArtifact1");
        Artifact treeArtifactTwo = createTreeArtifact("myArtifact/treeArtifact2");
        CustomCommandLine commandLineTemplate = CustomCommandLine.builder().addPlaceholderTreeArtifactExecPath("--argOne", treeArtifactOne).addPlaceholderTreeArtifactExecPath("--argTwo", treeArtifactTwo).build();
        try {
            commandLineTemplate.arguments();
            Assert.fail("No substitution map provided, expected NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    private static class Foo {
        private final String str;

        Foo(String str) {
            this.str = str;
        }

        static void expandToStr(CustomCommandLineTest.Foo foo, Consumer<String> args) {
            args.accept(foo.str);
        }
    }
}

