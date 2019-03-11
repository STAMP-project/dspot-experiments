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


import ApkDecoder.DECODE_ASSETS_FULL;
import ApkDecoder.DECODE_ASSETS_NONE;
import brut.androlib.ApkDecoder;
import brut.androlib.BaseTest;
import brut.common.BrutException;
import java.io.File;
import java.io.IOException;
import org.junit.Test;


public class SkipAssetTest extends BaseTest {
    @Test
    public void checkIfEnablingSkipAssetWorks() throws BrutException, IOException {
        String apk = "issue1605.apk";
        // decode issue1605.apk
        ApkDecoder apkDecoder = new ApkDecoder(new File((((BaseTest.sTmpDir) + (File.separator)) + apk)));
        BaseTest.sTestOrigDir = new brut.directory.ExtFile(((((BaseTest.sTmpDir) + (File.separator)) + apk) + ".out"));
        apkDecoder.setOutDir(BaseTest.sTestOrigDir);
        apkDecoder.setDecodeAssets(DECODE_ASSETS_NONE);
        apkDecoder.setForceDelete(true);
        apkDecoder.decode();
        checkFileDoesNotExist((("assets" + (File.separator)) + "kotlin.kotlin_builtins"));
        checkFileDoesNotExist((((("assets" + (File.separator)) + "ranges") + (File.separator)) + "ranges.kotlin_builtins"));
    }

    @Test
    public void checkControl() throws BrutException, IOException {
        String apk = "issue1605.apk";
        // decode issue1605.apk
        ApkDecoder apkDecoder = new ApkDecoder(new File((((BaseTest.sTmpDir) + (File.separator)) + apk)));
        BaseTest.sTestOrigDir = new brut.directory.ExtFile(((((BaseTest.sTmpDir) + (File.separator)) + apk) + ".out"));
        apkDecoder.setOutDir(BaseTest.sTestOrigDir);
        apkDecoder.setDecodeAssets(DECODE_ASSETS_FULL);
        apkDecoder.setForceDelete(true);
        apkDecoder.decode();
        checkFileDoesExist((("assets" + (File.separator)) + "kotlin.kotlin_builtins"));
        checkFileDoesExist((((("assets" + (File.separator)) + "ranges") + (File.separator)) + "ranges.kotlin_builtins"));
    }
}

