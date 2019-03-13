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


import brut.androlib.ApkDecoder;
import brut.androlib.BaseTest;
import brut.common.BrutException;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class AndResGuardTest extends BaseTest {
    @Test
    public void checkifAndResDecodeRemapsRFolder() throws BrutException, IOException {
        String apk = "issue1170.apk";
        // decode issue1170.apk
        ApkDecoder apkDecoder = new ApkDecoder(new File((((BaseTest.sTmpDir) + (File.separator)) + apk)));
        BaseTest.sTestOrigDir = new brut.directory.ExtFile(((((BaseTest.sTmpDir) + (File.separator)) + apk) + ".out"));
        apkDecoder.setOutDir(new File(((((BaseTest.sTmpDir) + (File.separator)) + apk) + ".out")));
        apkDecoder.decode();
        File aPng = new File(BaseTest.sTestOrigDir, "res/mipmap-hdpi-v4/a.png");
        Assert.assertTrue(aPng.isFile());
    }
}

