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
package org.apache.flink.container.entrypoint;


import PackagedProgram.MANIFEST_ATTRIBUTE_ASSEMBLER_CLASS;
import PackagedProgram.MANIFEST_ATTRIBUTE_MAIN_CLASS;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.apache.flink.container.entrypoint.JarManifestParser.JarFileWithEntryClass;
import org.apache.flink.shaded.guava18.com.google.common.collect.ImmutableList;
import org.apache.flink.shaded.guava18.com.google.common.collect.ImmutableMap;
import org.apache.flink.util.TestLogger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for JAR file manifest parsing.
 */
public class JarManifestParserTest extends TestLogger {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testFindEntryClassNoEntry() throws IOException {
        File jarFile = createJarFileWithManifest(ImmutableMap.of());
        Optional<String> entry = JarManifestParser.findEntryClass(jarFile);
        Assert.assertFalse(entry.isPresent());
    }

    @Test
    public void testFindEntryClassAssemblerClass() throws IOException {
        File jarFile = createJarFileWithManifest(ImmutableMap.of(MANIFEST_ATTRIBUTE_ASSEMBLER_CLASS, "AssemblerClass"));
        Optional<String> entry = JarManifestParser.findEntryClass(jarFile);
        Assert.assertTrue(entry.isPresent());
        Assert.assertThat(entry.get(), Is.is(CoreMatchers.equalTo("AssemblerClass")));
    }

    @Test
    public void testFindEntryClassMainClass() throws IOException {
        File jarFile = createJarFileWithManifest(ImmutableMap.of(MANIFEST_ATTRIBUTE_MAIN_CLASS, "MainClass"));
        Optional<String> entry = JarManifestParser.findEntryClass(jarFile);
        Assert.assertTrue(entry.isPresent());
        Assert.assertThat(entry.get(), Is.is(CoreMatchers.equalTo("MainClass")));
    }

    @Test
    public void testFindEntryClassAssemblerClassAndMainClass() throws IOException {
        // We want the assembler class entry to have precedence over main class
        File jarFile = createJarFileWithManifest(ImmutableMap.of(MANIFEST_ATTRIBUTE_ASSEMBLER_CLASS, "AssemblerClass", MANIFEST_ATTRIBUTE_MAIN_CLASS, "MainClass"));
        Optional<String> entry = JarManifestParser.findEntryClass(jarFile);
        Assert.assertTrue(entry.isPresent());
        Assert.assertThat(entry.get(), Is.is(CoreMatchers.equalTo("AssemblerClass")));
    }

    @Test
    public void testFindEntryClassWithTestJobJar() throws IOException {
        File jarFile = TestJob.getTestJobJar();
        Optional<String> entryClass = JarManifestParser.findEntryClass(jarFile);
        Assert.assertTrue(entryClass.isPresent());
        Assert.assertThat(entryClass.get(), Is.is(CoreMatchers.equalTo(TestJob.class.getCanonicalName())));
    }

    @Test(expected = NoSuchElementException.class)
    public void testFindOnlyEntryClassEmptyArgument() throws IOException {
        JarManifestParser.findOnlyEntryClass(Collections.emptyList());
    }

    @Test(expected = NoSuchElementException.class)
    public void testFindOnlyEntryClassSingleJarWithNoManifest() throws IOException {
        File jarWithNoManifest = createJarFileWithManifest(ImmutableMap.of());
        JarManifestParser.findOnlyEntryClass(ImmutableList.of(jarWithNoManifest));
    }

    @Test
    public void testFindOnlyEntryClassSingleJar() throws IOException {
        File jarFile = TestJob.getTestJobJar();
        JarFileWithEntryClass jarFileWithEntryClass = JarManifestParser.findOnlyEntryClass(ImmutableList.of(jarFile));
        Assert.assertThat(jarFileWithEntryClass.getEntryClass(), Is.is(CoreMatchers.equalTo(TestJob.class.getCanonicalName())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindOnlyEntryClassMultipleJarsWithMultipleManifestEntries() throws IOException {
        File jarFile = TestJob.getTestJobJar();
        JarManifestParser.findOnlyEntryClass(ImmutableList.of(jarFile, jarFile, jarFile));
    }

    @Test
    public void testFindOnlyEntryClassMultipleJarsWithSingleManifestEntry() throws IOException {
        File jarWithNoManifest = createJarFileWithManifest(ImmutableMap.of());
        File jarFile = TestJob.getTestJobJar();
        JarFileWithEntryClass jarFileWithEntryClass = JarManifestParser.findOnlyEntryClass(ImmutableList.of(jarWithNoManifest, jarFile));
        Assert.assertThat(jarFileWithEntryClass.getEntryClass(), Is.is(CoreMatchers.equalTo(TestJob.class.getCanonicalName())));
    }
}

