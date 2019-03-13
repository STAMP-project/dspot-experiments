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


import android.content.Context;
import android.content.pm.ApplicationInfo;
import java.io.File;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;


public class ApkLibraryInstallerTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void installsCorrectly() throws IOException {
        final Context context = Mockito.mock(Context.class);
        final ApplicationInfo appInfo = Mockito.mock(ApplicationInfo.class);
        final ReLinkerInstance instance = Mockito.mock(ReLinkerInstance.class);
        final ApkLibraryInstaller installer = new ApkLibraryInstaller();
        final File destination = tempFolder.newFile("test");
        final String[] abis = new String[]{ "x86" };
        Mockito.when(context.getApplicationInfo()).thenReturn(appInfo);
        appInfo.sourceDir = getClass().getResource("/fake.apk").getFile();
        installer.installLibrary(context, abis, "libtest.so", destination, instance);
        Mockito.verify(context).getApplicationInfo();
        MatcherAssert.assertThat(fileToString(destination), Is.is("works!"));
    }
}

