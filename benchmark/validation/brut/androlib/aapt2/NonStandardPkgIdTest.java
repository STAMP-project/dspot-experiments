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
package brut.androlib.aapt2;


import brut.androlib.BaseTest;
import brut.androlib.res.data.ResTable;
import brut.common.BrutException;
import org.junit.Assert;
import org.junit.Test;


public class NonStandardPkgIdTest extends BaseTest {
    @Test
    public void buildAndDecodeTest() {
        Assert.assertTrue(BaseTest.sTestNewDir.isDirectory());
    }

    @Test
    public void valuesStringsTest() throws BrutException {
        compareValuesFiles("values/strings.xml");
    }

    @Test
    public void confirmManifestStructureTest() throws BrutException {
        compareXmlFiles("AndroidManifest.xml");
    }

    @Test
    public void confirmResourcesAreFromPkgId8() throws AndrolibException {
        Assert.assertEquals(128, NonStandardPkgIdTest.mResTable.getPackageId());
        Assert.assertEquals(128, NonStandardPkgIdTest.mResTable.getResSpec(-2147352576).getPackage().getId());
        Assert.assertEquals(128, NonStandardPkgIdTest.mResTable.getResSpec(-2147352575).getPackage().getId());
        Assert.assertEquals(128, NonStandardPkgIdTest.mResTable.getResSpec(-2147287040).getPackage().getId());
    }

    private static ResTable mResTable;
}

