/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2019 the AndroidAnnotations project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.internal.helper;


import AndroidManifestFinder.OPTION_INSTANT_FEATURE;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.androidannotations.AndroidAnnotationsEnvironment;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;


@RunWith(Parameterized.class)
public class AndroidManifestFinderTest {
    private static final String GRADLE_GEN_FOLDER = "build/generated/source/apt/debug";

    private static final String GRADLE_FLAVOR_GEN_FOLDER = "build/generated/source/apt/flavor/debug";

    private static final String GRADLE_KOTLIN_GEN_FOLDER = "build/generated/source/kapt/debug";

    private static final String GRADLE_KOTLIN_FLAVOR_GEN_FOLDER = "build/generated/source/kapt/flavorDebug";

    private static final String MAVEN_GEN_FOLDER = "target/generated-sources/annotations";

    private static final String ECLIPSE_GEN_FOLDER = ".apt_generated";

    private final String genFolderPath;

    private final String manifestFolderPath;

    private final boolean shouldFind;

    private Path tempDirectory;

    public AndroidManifestFinderTest(String genFolderPath, String manifestFolderPath, boolean shouldFind) {
        this.genFolderPath = genFolderPath;
        this.manifestFolderPath = manifestFolderPath;
        this.shouldFind = shouldFind;
    }

    @Test
    public void testFindManifestInKnownPathsStartingFromGenFolder() throws IOException {
        AndroidAnnotationsEnvironment mockEnvironment = Mockito.mock(AndroidAnnotationsEnvironment.class);
        Mockito.when(mockEnvironment.getOptionBooleanValue(OPTION_INSTANT_FEATURE)).thenReturn(false);
        AndroidManifestFinder finder = new AndroidManifestFinder(mockEnvironment);
        tempDirectory = Files.createTempDirectory("AA");
        File genFolder = createGenFolder(genFolderPath);
        File manifestFolder = new File(tempDirectory.toString(), manifestFolderPath);
        createFolder(manifestFolder);
        File expectedManifest = new File(manifestFolder, "AndroidManifest.xml");
        createFile(expectedManifest);
        File foundManifest = finder.findManifestInKnownPathsStartingFromGenFolder(genFolder.getAbsolutePath());
        Assert.assertEquals(shouldFind, Objects.equals(expectedManifest, foundManifest));
    }
}

