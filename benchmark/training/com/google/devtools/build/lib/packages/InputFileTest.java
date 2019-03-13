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


import com.google.common.testing.EqualsTester;
import com.google.devtools.build.lib.events.util.EventCollectionApparatus;
import com.google.devtools.build.lib.packages.util.PackageFactoryApparatus;
import com.google.devtools.build.lib.testutil.Scratch;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.Root;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * A test for {@link InputFile}.
 */
@RunWith(JUnit4.class)
public class InputFileTest {
    private Path pathX;

    private Path pathY;

    private Package pkg;

    private EventCollectionApparatus events = new EventCollectionApparatus();

    private Scratch scratch = new Scratch("/workspace");

    private PackageFactoryApparatus packages = new PackageFactoryApparatus(events.reporter());

    private Root root;

    @Test
    public void testGetAssociatedRule() throws Exception {
        assertWithMessage(null).that(getTarget("x").getAssociatedRule()).isNull();
    }

    @Test
    public void testInputFileInPackageDirectory() throws NoSuchTargetException {
        InputFile inputFileX = ((InputFile) (getTarget("x")));
        InputFileTest.checkPathMatches(inputFileX, pathX);
        InputFileTest.checkName(inputFileX, "x");
        InputFileTest.checkLabel(inputFileX, "//pkg:x");
        assertThat(inputFileX.getTargetKind()).isEqualTo("source file");
    }

    @Test
    public void testInputFileInSubdirectory() throws NoSuchTargetException {
        InputFile inputFileY = ((InputFile) (getTarget("subdir/y")));
        InputFileTest.checkPathMatches(inputFileY, pathY);
        InputFileTest.checkName(inputFileY, "subdir/y");
        InputFileTest.checkLabel(inputFileY, "//pkg:subdir/y");
    }

    @Test
    public void testEquivalenceRelation() throws NoSuchTargetException {
        InputFile inputFileX = ((InputFile) (getTarget("x")));
        assertThat(inputFileX).isSameAs(getTarget("x"));
        InputFile inputFileY = ((InputFile) (getTarget("subdir/y")));
        assertThat(inputFileY).isSameAs(getTarget("subdir/y"));
        new EqualsTester().addEqualityGroup(inputFileX).addEqualityGroup(inputFileY).testEquals();
    }
}

