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
package brut.androlib.decode;


import ApkDecoder.DECODE_RESOURCES_FULL;
import ApkDecoder.DECODE_RESOURCES_NONE;
import ApkDecoder.FORCE_DECODE_MANIFEST_FULL;
import ApkDecoder.FORCE_DECODE_MANIFEST_NONE;
import brut.androlib.BaseTest;
import brut.androlib.TestUtils;
import brut.common.BrutException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class ForceManifestDecodeNoResourcesTest extends BaseTest {
    private byte[] xmlHeader = new byte[]{ 60// <
    , 63// ?
    , 120// x
    , 109// m
    , 108// l
    , 32// (empty)
     };

    @Test
    public void checkIfForceManifestWithNoResourcesWorks() throws BrutException, IOException {
        String apk = "issue1680.apk";
        String output = (((BaseTest.sTmpDir) + (File.separator)) + apk) + ".out";
        // decode issue1680.apk
        decodeFile((((BaseTest.sTmpDir) + (File.separator)) + apk), DECODE_RESOURCES_NONE, FORCE_DECODE_MANIFEST_FULL, output);
        // lets probe filetype of manifest, we should detect XML
        File manifestFile = new File(((output + (File.separator)) + "AndroidManifest.xml"));
        byte[] magic = TestUtils.readHeaderOfFile(manifestFile, 6);
        Assert.assertTrue(Arrays.equals(this.xmlHeader, magic));
        // confirm resources.arsc still exists, as its raw
        File resourcesArsc = new File(((output + (File.separator)) + "resources.arsc"));
        Assert.assertTrue(resourcesArsc.isFile());
    }

    @Test
    public void checkIfForceManifestWorksWithNoChangeToResources() throws BrutException, IOException {
        String apk = "issue1680.apk";
        String output = (((BaseTest.sTmpDir) + (File.separator)) + apk) + ".out";
        // decode issue1680.apk
        decodeFile((((BaseTest.sTmpDir) + (File.separator)) + apk), DECODE_RESOURCES_FULL, FORCE_DECODE_MANIFEST_FULL, output);
        // lets probe filetype of manifest, we should detect XML
        File manifestFile = new File(((output + (File.separator)) + "AndroidManifest.xml"));
        byte[] magic = TestUtils.readHeaderOfFile(manifestFile, 6);
        Assert.assertTrue(Arrays.equals(this.xmlHeader, magic));
        // confirm resources.arsc does not exist
        File resourcesArsc = new File(((output + (File.separator)) + "resources.arsc"));
        Assert.assertFalse(resourcesArsc.isFile());
    }

    @Test
    public void checkForceManifestToFalseWithResourcesEnabledIsIgnored() throws BrutException, IOException {
        String apk = "issue1680.apk";
        String output = (((BaseTest.sTmpDir) + (File.separator)) + apk) + ".out";
        // decode issue1680.apk
        decodeFile((((BaseTest.sTmpDir) + (File.separator)) + apk), DECODE_RESOURCES_FULL, FORCE_DECODE_MANIFEST_NONE, output);
        // lets probe filetype of manifest, we should detect XML
        File manifestFile = new File(((output + (File.separator)) + "AndroidManifest.xml"));
        byte[] magic = TestUtils.readHeaderOfFile(manifestFile, 6);
        Assert.assertTrue(Arrays.equals(this.xmlHeader, magic));
        // confirm resources.arsc does not exist
        File resourcesArsc = new File(((output + (File.separator)) + "resources.arsc"));
        Assert.assertFalse(resourcesArsc.isFile());
    }

    @Test
    public void checkBothManifestAndResourcesSetToNone() throws BrutException, IOException {
        String apk = "issue1680.apk";
        String output = (((BaseTest.sTmpDir) + (File.separator)) + apk) + ".out";
        // decode issue1680.apk
        decodeFile((((BaseTest.sTmpDir) + (File.separator)) + apk), DECODE_RESOURCES_NONE, FORCE_DECODE_MANIFEST_NONE, output);
        // lets probe filetype of manifest, we should not detect XML
        File manifestFile = new File(((output + (File.separator)) + "AndroidManifest.xml"));
        byte[] magic = TestUtils.readHeaderOfFile(manifestFile, 6);
        Assert.assertFalse(Arrays.equals(this.xmlHeader, magic));
        // confirm resources.arsc exists
        File resourcesArsc = new File(((output + (File.separator)) + "resources.arsc"));
        Assert.assertTrue(resourcesArsc.isFile());
    }
}

