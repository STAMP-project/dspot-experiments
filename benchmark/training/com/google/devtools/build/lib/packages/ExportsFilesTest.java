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
package com.google.devtools.build.lib.packages;


import com.google.common.base.Joiner;
import com.google.devtools.build.lib.events.util.EventCollectionApparatus;
import com.google.devtools.build.lib.packages.util.PackageFactoryApparatus;
import com.google.devtools.build.lib.testutil.Scratch;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.Root;
import com.google.devtools.build.lib.vfs.RootedPath;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * A test for the {@code exports_files} function defined in {@link PackageFactory}.
 */
@RunWith(JUnit4.class)
public class ExportsFilesTest {
    private Scratch scratch = new Scratch("/workspace");

    private EventCollectionApparatus events = new EventCollectionApparatus();

    private PackageFactoryApparatus packages = new PackageFactoryApparatus(events.reporter());

    private Root root;

    @Test
    public void testExportsFilesRegistersFilesWithPackage() throws Exception {
        List<String> names = ExportsFilesTest.getFileNamesOf(pkg());
        String expected = "//pkg:BUILD //pkg:bar.txt //pkg:foo.txt";
        assertThat(Joiner.on(' ').join(names)).isEqualTo(expected);
    }

    @Test
    public void testFileThatsNotRegisteredYieldsUnknownTargetException() throws Exception {
        try {
            getTarget("baz.txt");
            Assert.fail();
        } catch (NoSuchTargetException e) {
            assertThat(e).hasMessage(("no such target '//pkg:baz.txt':" + (" target 'baz.txt' not declared in package 'pkg' (did you mean 'bar.txt'?)" + " defined by /workspace/pkg/BUILD")));
        }
    }

    @Test
    public void testRegisteredFilesAreRetrievable() throws Exception {
        Package pkg = pkg();
        assertThat(getTarget("foo.txt").getName()).isEqualTo("foo.txt");
        assertThat(getTarget("bar.txt").getName()).isEqualTo("bar.txt");
    }

    @Test
    public void testExportsFilesAndRuleNameConflict() throws Exception {
        events.setFailFast(false);
        Path buildFile = scratch.file("pkg2/BUILD", "exports_files(['foo'])", "genrule(name = 'foo', srcs = ['bar'], outs = [],", "        cmd = '/bin/true')");
        Package pkg = packages.createPackage("pkg2", RootedPath.toRootedPath(root, buildFile));
        events.assertContainsError(("rule 'foo' in package 'pkg2' conflicts with " + "existing source file"));
        assertThat(((getTarget("foo")) instanceof InputFile)).isTrue();
    }
}

