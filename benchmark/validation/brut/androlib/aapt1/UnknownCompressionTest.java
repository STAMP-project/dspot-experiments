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


import brut.androlib.BaseTest;
import brut.common.BrutException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Connor Tumbleson <connor.tumbleson@gmail.com>
 */
public class UnknownCompressionTest extends BaseTest {
    @Test
    public void pkmExtensionDeflatedTest() throws BrutException, IOException {
        Integer control = BaseTest.sTestOrigDir.getDirectory().getCompressionLevel("assets/bin/Data/test.pkm");
        Integer rebuilt = BaseTest.sTestNewDir.getDirectory().getCompressionLevel("assets/bin/Data/test.pkm");
        // Check that control = rebuilt (both deflated)
        // Add extra check for checking not equal to 0, just in case control gets broken
        Assert.assertEquals(control, rebuilt);
        Assert.assertNotSame(0, rebuilt);
    }

    @Test
    public void doubleExtensionStoredTest() throws BrutException, IOException {
        Integer control = BaseTest.sTestOrigDir.getDirectory().getCompressionLevel("assets/bin/Data/two.extension.file");
        Integer rebuilt = BaseTest.sTestNewDir.getDirectory().getCompressionLevel("assets/bin/Data/two.extension.file");
        // Check that control = rebuilt (both stored)
        // Add extra check for checking = 0 to enforce check for stored just in case control breaks
        Assert.assertEquals(control, rebuilt);
        Assert.assertEquals(new Integer(0), rebuilt);
    }

    @Test
    public void confirmJsonFileIsDeflatedTest() throws BrutException, IOException {
        Integer control = BaseTest.sTestOrigDir.getDirectory().getCompressionLevel("test.json");
        Integer rebuilt = BaseTest.sTestNewDir.getDirectory().getCompressionLevel("test.json");
        Assert.assertEquals(control, rebuilt);
        Assert.assertEquals(new Integer(8), rebuilt);
    }

    @Test
    public void confirmPngFileIsCorrectlyDeflatedTest() throws BrutException, IOException {
        Integer control = BaseTest.sTestOrigDir.getDirectory().getCompressionLevel("950x150.png");
        Integer rebuilt = BaseTest.sTestNewDir.getDirectory().getCompressionLevel("950x150.png");
        Assert.assertEquals(control, rebuilt);
        Assert.assertEquals(new Integer(8), rebuilt);
    }
}

