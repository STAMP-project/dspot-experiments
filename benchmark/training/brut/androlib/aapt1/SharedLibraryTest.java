/**
 * Copyright (C) 2018 Ryszard Wi?niewski <brut.alll@gmail.com>
 *  Copyright (C) 2018 Connor Tumbleson <connor.tumbleson@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package brut.androlib.aapt1;


import brut.androlib.AndrolibException;
import brut.androlib.BaseTest;
import brut.common.BrutException;
import brut.directory.ExtFile;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class SharedLibraryTest extends BaseTest {
    @Test
    public void isFrameworkTaggingWorking() throws AndrolibException {
        String apkName = "library.apk";
        ApkOptions apkOptions = new ApkOptions();
        apkOptions.frameworkFolderLocation = BaseTest.sTmpDir.getAbsolutePath();
        apkOptions.frameworkTag = "building";
        installFramework(new File((((BaseTest.sTmpDir) + (File.separator)) + apkName)));
        Assert.assertTrue(fileExists("2-building.apk"));
    }

    @Test
    public void isFrameworkInstallingWorking() throws AndrolibException {
        String apkName = "library.apk";
        ApkOptions apkOptions = new ApkOptions();
        apkOptions.frameworkFolderLocation = BaseTest.sTmpDir.getAbsolutePath();
        installFramework(new File((((BaseTest.sTmpDir) + (File.separator)) + apkName)));
        Assert.assertTrue(fileExists("2.apk"));
    }

    @Test
    public void isSharedResourceDecodingAndRebuildingWorking() throws BrutException, IOException {
        String library = "library.apk";
        String client = "client.apk";
        // setup apkOptions
        ApkOptions apkOptions = new ApkOptions();
        apkOptions.frameworkFolderLocation = BaseTest.sTmpDir.getAbsolutePath();
        apkOptions.frameworkTag = "shared";
        // install library/framework
        installFramework(new File((((BaseTest.sTmpDir) + (File.separator)) + library)));
        Assert.assertTrue(fileExists("2-shared.apk"));
        // decode client.apk
        ApkDecoder apkDecoder = new ApkDecoder(new File((((BaseTest.sTmpDir) + (File.separator)) + client)));
        apkDecoder.setOutDir(new File(((((BaseTest.sTmpDir) + (File.separator)) + client) + ".out")));
        apkDecoder.setFrameworkDir(apkOptions.frameworkFolderLocation);
        apkDecoder.setFrameworkTag(apkOptions.frameworkTag);
        apkDecoder.decode();
        // decode library.apk
        ApkDecoder libraryDecoder = new ApkDecoder(new File((((BaseTest.sTmpDir) + (File.separator)) + library)));
        libraryDecoder.setOutDir(new File(((((BaseTest.sTmpDir) + (File.separator)) + library) + ".out")));
        libraryDecoder.setFrameworkDir(apkOptions.frameworkFolderLocation);
        libraryDecoder.setFrameworkTag(apkOptions.frameworkTag);
        libraryDecoder.decode();
        // build client.apk
        ExtFile clientApk = new ExtFile(BaseTest.sTmpDir, (client + ".out"));
        new Androlib(apkOptions).build(clientApk, null);
        Assert.assertTrue(fileExists((((((client + ".out") + (File.separator)) + "dist") + (File.separator)) + client)));
        // build library.apk (shared library)
        ExtFile libraryApk = new ExtFile(BaseTest.sTmpDir, (library + ".out"));
        new Androlib(apkOptions).build(libraryApk, null);
        Assert.assertTrue(fileExists((((((library + ".out") + (File.separator)) + "dist") + (File.separator)) + library)));
    }
}

