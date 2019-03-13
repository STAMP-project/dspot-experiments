/**
 * Copyright 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.zxing.qrcode.decoder;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sean Owen
 */
public final class VersionTestCase extends Assert {
    @Test(expected = IllegalArgumentException.class)
    public void testBadVersion() {
        Version.getVersionForNumber(0);
    }

    @Test
    public void testVersionForNumber() {
        for (int i = 1; i <= 40; i++) {
            VersionTestCase.checkVersion(Version.getVersionForNumber(i), i, ((4 * i) + 17));
        }
    }

    @Test
    public void testGetProvisionalVersionForDimension() throws Exception {
        for (int i = 1; i <= 40; i++) {
            Assert.assertEquals(i, Version.getProvisionalVersionForDimension(((4 * i) + 17)).getVersionNumber());
        }
    }

    @Test
    public void testDecodeVersionInformation() {
        // Spot check
        VersionTestCase.doTestVersion(7, 31892);
        VersionTestCase.doTestVersion(12, 51042);
        VersionTestCase.doTestVersion(17, 70749);
        VersionTestCase.doTestVersion(22, 92361);
        VersionTestCase.doTestVersion(27, 110734);
        VersionTestCase.doTestVersion(32, 133589);
    }
}

