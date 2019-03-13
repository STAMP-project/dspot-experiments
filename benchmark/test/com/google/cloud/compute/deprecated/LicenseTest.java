/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.compute.deprecated;


import org.junit.Assert;
import org.junit.Test;


public class LicenseTest {
    private static final LicenseId LICENSE_ID = LicenseId.of("project", "license");

    private static final Boolean CHARGES_USE_FEE = true;

    private static final License LICENSE = new License(LicenseTest.LICENSE_ID, LicenseTest.CHARGES_USE_FEE);

    @Test
    public void testBuilder() {
        Assert.assertEquals(LicenseTest.LICENSE_ID, LicenseTest.LICENSE.getLicenseId());
        Assert.assertEquals(LicenseTest.CHARGES_USE_FEE, LicenseTest.LICENSE.chargesUseFee());
    }

    @Test
    public void testToAndFromPb() {
        License license = License.fromPb(LicenseTest.LICENSE.toPb());
        compareLicenses(LicenseTest.LICENSE, license);
        Assert.assertEquals(LicenseTest.LICENSE_ID.getProject(), license.getLicenseId().getProject());
        Assert.assertEquals(LicenseTest.LICENSE_ID.getLicense(), license.getLicenseId().getLicense());
    }
}

