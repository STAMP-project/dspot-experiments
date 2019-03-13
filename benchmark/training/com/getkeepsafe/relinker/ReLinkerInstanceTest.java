/**
 * Copyright 2015 - 2016 KeepSafe Software, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.getkeepsafe.relinker;


import ReLinker.LibraryInstaller;
import ReLinker.LibraryLoader;
import android.content.Context;
import java.io.File;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class ReLinkerInstanceTest {
    private static final String TEST_LIB = "mylib";

    private static final String TEST_LIB_MAPPED = "libmylib.so";

    private static final String TEST_DIR = "lib";

    @Mock
    Context context;

    @Mock
    LibraryLoader testLoader;

    @Mock
    LibraryInstaller testInstaller;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private File libDir;

    @Test
    public void getsCorrectWorkaroundDirectory() {
        final ReLinkerInstance instance = new ReLinkerInstance(testLoader, testInstaller);
        MatcherAssert.assertThat(instance.getWorkaroundLibDir(context), Is.is(libDir));
    }

    @Test
    public void getsCorrectWorkaroundFile() {
        final ReLinkerInstance instance = new ReLinkerInstance(testLoader, testInstaller);
        final String libName = testLoader.mapLibraryName(ReLinkerInstanceTest.TEST_LIB);
        final File libFile = new File(libDir, libName);
        MatcherAssert.assertThat(instance.getWorkaroundLibFile(context, ReLinkerInstanceTest.TEST_LIB, null), Is.is(libFile));
        final File versionedLibFile = new File(libDir, (libName + ".2.0"));
        MatcherAssert.assertThat(instance.getWorkaroundLibFile(context, ReLinkerInstanceTest.TEST_LIB, "2.0"), Is.is(versionedLibFile));
    }

    @Test
    public void cleansupOldLibraryFiles() throws IOException {
        final ReLinkerInstance instance = new ReLinkerInstance(testLoader, testInstaller);
        final String mappedName = testLoader.mapLibraryName(ReLinkerInstanceTest.TEST_LIB);
        tempFolder.newFile(mappedName);
        tempFolder.newFile((mappedName + ".2.0"));
        tempFolder.newFile((mappedName + ".3.4"));
        tempFolder.newFile((mappedName + ".4.0"));
        MatcherAssert.assertThat(libDir.listFiles().length, Is.is(4));
        instance.cleanupOldLibFiles(context, ReLinkerInstanceTest.TEST_LIB, "4.0");
        MatcherAssert.assertThat(libDir.listFiles().length, Is.is(1));
        tempFolder.newFile(mappedName);
        tempFolder.newFile((mappedName + ".2.0"));
        tempFolder.newFile((mappedName + ".3.4"));
        MatcherAssert.assertThat(libDir.listFiles().length, Is.is(4));
        instance.cleanupOldLibFiles(context, ReLinkerInstanceTest.TEST_LIB, null);
        MatcherAssert.assertThat(libDir.listFiles().length, Is.is(1));
        tempFolder.newFile((mappedName + ".2.0"));
        tempFolder.newFile((mappedName + ".3.4"));
        tempFolder.newFile((mappedName + ".4.0"));
        MatcherAssert.assertThat(libDir.listFiles().length, Is.is(4));
        instance.force().cleanupOldLibFiles(context, ReLinkerInstanceTest.TEST_LIB, "4.0");
        MatcherAssert.assertThat(libDir.listFiles().length, Is.is(0));
    }

    @Test
    public void loadsLibraryNormally() {
        final ReLinkerInstance instance = new ReLinkerInstance(testLoader, testInstaller);
        instance.loadLibrary(context, ReLinkerInstanceTest.TEST_LIB);
    }

    @Test
    public void relinksLibrary() {
        final ReLinkerInstance instance = new ReLinkerInstance(testLoader, testInstaller);
        final File workaroundFile = new File(libDir.getAbsolutePath(), ReLinkerInstanceTest.TEST_LIB_MAPPED);
        final String[] abis = new String[]{ "x86" };
        Mockito.doThrow(new UnsatisfiedLinkError("boo")).when(testLoader).loadLibrary(ArgumentMatchers.anyString());
        Mockito.when(testLoader.supportedAbis()).thenReturn(abis);
        instance.loadLibrary(context, ReLinkerInstanceTest.TEST_LIB);
        Mockito.verify(testLoader).loadLibrary(ReLinkerInstanceTest.TEST_LIB);
        Mockito.verify(testLoader).loadPath(workaroundFile.getAbsolutePath());
        Mockito.verify(testLoader).supportedAbis();
        Mockito.verify(testInstaller).installLibrary(context, abis, ReLinkerInstanceTest.TEST_LIB_MAPPED, workaroundFile, instance);
        instance.force().loadLibrary(context, ReLinkerInstanceTest.TEST_LIB);
        Mockito.verify(testLoader, Mockito.times(2)).loadLibrary(ReLinkerInstanceTest.TEST_LIB);
        Mockito.verify(testLoader, Mockito.times(2)).loadPath(workaroundFile.getAbsolutePath());
        Mockito.verify(testLoader, Mockito.times(2)).supportedAbis();
        Mockito.verify(testInstaller, Mockito.times(2)).installLibrary(context, abis, ReLinkerInstanceTest.TEST_LIB_MAPPED, workaroundFile, instance);
    }
}

