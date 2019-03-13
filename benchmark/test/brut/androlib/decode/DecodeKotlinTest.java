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


import brut.androlib.BaseTest;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Connor Tumbleson <connor.tumbleson@gmail.com>
 */
public class DecodeKotlinTest extends BaseTest {
    @Test
    public void kotlinFolderExistsTest() {
        Assert.assertTrue(BaseTest.sTestNewDir.isDirectory());
        File testKotlinFolder = new File(BaseTest.sTestNewDir, "kotlin");
        Assert.assertTrue(testKotlinFolder.isDirectory());
    }

    @Test
    public void kotlinDecodeTest() throws IOException {
        File kotlinActivity = new File(BaseTest.sTestNewDir, "smali/org/example/kotlin/mixed/KotlinActivity.smali");
        Assert.assertTrue(FileUtils.readFileToString(kotlinActivity).contains("KotlinActivity.kt"));
    }
}

