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
import brut.androlib.TestUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;


/**
 *
 *
 * @author Connor Tumbleson <connor.tumbleson@gmail.com>
 */
public class DebugTagRetainedTest extends BaseTest {
    @Test
    public void buildAndDecodeTest() {
        Assert.assertTrue(BaseTest.sTestNewDir.isDirectory());
    }

    @Test
    public void DebugIsTruePriorToBeingFalseTest() throws IOException, SAXException {
        String apk = "issue1235-new";
        String expected = TestUtils.replaceNewlines(("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>" + (("<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\" android:compileSdkVersion=\"23\" " + "android:compileSdkVersionCodename=\"6.0-2438415\" package=\"com.ibotpeaches.issue1235\" platformBuildVersionCode=\"20\" ") + "platformBuildVersionName=\"4.4W.2-1537038\">    <application android:debuggable=\"true\"/></manifest>")));
        byte[] encoded = Files.readAllBytes(Paths.get((((((BaseTest.sTmpDir) + (File.separator)) + apk) + (File.separator)) + "AndroidManifest.xml")));
        String obtained = TestUtils.replaceNewlines(new String(encoded));
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setCompareUnmatched(false);
        assertXMLEqual(expected, obtained);
    }
}

