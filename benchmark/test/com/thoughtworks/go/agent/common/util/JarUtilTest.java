/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.agent.common.util;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class JarUtilTest {
    private static final String PATH_WITH_HASHES = "#hashes#in#path/";

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldGetManifestKey() throws Exception {
        String manifestKey = JarUtil.getManifestKey(new File(((JarUtilTest.PATH_WITH_HASHES) + "test-agent.jar")), "Go-Agent-Bootstrap-Class");
        Assert.assertThat(manifestKey, Matchers.is("com.thoughtworks.go.HelloWorldStreamWriter"));
    }

    @Test
    public void shouldExtractJars() throws Exception {
        File sourceFile = new File(((JarUtilTest.PATH_WITH_HASHES) + "test-agent.jar"));
        File outputTmpDir = temporaryFolder.newFolder();
        Set<File> files = new HashSet(JarUtil.extractFilesInLibDirAndReturnFiles(sourceFile, ( jarEntry) -> jarEntry.getName().endsWith(".class"), outputTmpDir));
        Set<File> actualFiles = Files.list(outputTmpDir.toPath()).map(Path::toFile).collect(Collectors.toSet());
        Assert.assertEquals(files, actualFiles);
        Assert.assertEquals(files.size(), 2);
        Set<String> fileNames = files.stream().map(File::getName).collect(Collectors.toSet());
        Assert.assertEquals(fileNames, new HashSet<>(Arrays.asList("ArgPrintingMain.class", "HelloWorldStreamWriter.class")));
    }
}

