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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class LicenseIdTest {
    private static final String PROJECT = "project";

    private static final String LICENSE = "license";

    private static final String URL = "https://www.googleapis.com/compute/v1/projects/project/global/licenses/license";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testOf() {
        LicenseId licenseId = LicenseId.of(LicenseIdTest.PROJECT, LicenseIdTest.LICENSE);
        Assert.assertEquals(LicenseIdTest.PROJECT, licenseId.getProject());
        Assert.assertEquals(LicenseIdTest.LICENSE, licenseId.getLicense());
        Assert.assertEquals(LicenseIdTest.URL, licenseId.getSelfLink());
        licenseId = LicenseId.of(LicenseIdTest.LICENSE);
        Assert.assertNull(licenseId.getProject());
        Assert.assertEquals(LicenseIdTest.LICENSE, licenseId.getLicense());
    }

    @Test
    public void testToAndFromUrl() {
        LicenseId licenseId = LicenseId.of(LicenseIdTest.PROJECT, LicenseIdTest.LICENSE);
        compareLicenseId(licenseId, LicenseId.fromUrl(licenseId.getSelfLink()));
    }

    @Test
    public void testSetProjectId() {
        LicenseId licenseId = LicenseId.of(LicenseIdTest.PROJECT, LicenseIdTest.LICENSE);
        Assert.assertSame(licenseId, licenseId.setProjectId(LicenseIdTest.PROJECT));
        compareLicenseId(licenseId, LicenseId.of(LicenseIdTest.LICENSE).setProjectId(LicenseIdTest.PROJECT));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("notMatchingUrl is not a valid license URL");
        LicenseId.fromUrl("notMatchingUrl");
    }

    @Test
    public void testMatchesUrl() {
        Assert.assertTrue(LicenseId.matchesUrl(LicenseId.of(LicenseIdTest.PROJECT, LicenseIdTest.LICENSE).getSelfLink()));
        Assert.assertFalse(LicenseId.matchesUrl("notMatchingUrl"));
    }
}

