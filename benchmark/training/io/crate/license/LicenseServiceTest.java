/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.license;


import LicenseExpiryNotification.EXPIRED;
import LicenseExpiryNotification.MODERATE;
import LicenseExpiryNotification.SEVERE;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


public class LicenseServiceTest extends CrateDummyClusterServiceUnitTest {
    private LicenseService licenseService;

    @Test
    public void testGetLicenseDataForTrialLicenseKeyProduceValidValues() throws IOException {
        LicenseKey key = TrialLicense.createLicenseKey(LicenseKey.VERSION, new DecryptedLicenseData(Long.MAX_VALUE, "test"));
        DecryptedLicenseData licenseData = LicenseService.licenseData(LicenseKey.decodeLicense(key));
        assertThat(licenseData.expiryDateInMs(), Matchers.is(Long.MAX_VALUE));
        assertThat(licenseData.issuedTo(), Matchers.is("test"));
    }

    @Test
    public void testGetLicenseDataForEnterpriseLicenseKeyProduceValidValues() throws IOException {
        LicenseKey key = LicenseServiceTest.createEnterpriseLicenseKey(LicenseKey.VERSION, new DecryptedLicenseData(Long.MAX_VALUE, "test"));
        DecryptedLicenseData licenseData = LicenseService.licenseData(LicenseKey.decodeLicense(key));
        assertThat(licenseData.expiryDateInMs(), Matchers.is(Long.MAX_VALUE));
        assertThat(licenseData.issuedTo(), Matchers.is("test"));
    }

    @Test
    public void testVerifyValidTrialLicense() {
        LicenseKey licenseKey = TrialLicense.createLicenseKey(LicenseKey.VERSION, new DecryptedLicenseData(Long.MAX_VALUE, "test"));
        assertThat(LicenseService.verifyLicense(licenseKey), Is.is(true));
    }

    @Test
    public void testVerifyExpiredTrialLicense() {
        LicenseKey expiredLicense = TrialLicense.createLicenseKey(LicenseKey.VERSION, new DecryptedLicenseData(((System.currentTimeMillis()) - (TimeUnit.HOURS.toMillis(5))), "test"));
        assertThat(LicenseService.verifyLicense(expiredLicense), Is.is(false));
    }

    @Test
    public void testVerifyValidEnterpriseLicense() {
        LicenseKey key = LicenseServiceTest.createEnterpriseLicenseKey(LicenseKey.VERSION, new DecryptedLicenseData(Long.MAX_VALUE, "test"));
        assertThat(LicenseService.verifyLicense(key), Is.is(true));
    }

    @Test
    public void testLicenseNotificationIsExpiredForExpiredLicense() {
        DecryptedLicenseData expiredLicense = new DecryptedLicenseData(((System.currentTimeMillis()) - (TimeUnit.HOURS.toMillis(5))), "test");
        assertThat(licenseService.getLicenseExpiryNotification(expiredLicense), Is.is(EXPIRED));
    }

    @Test
    public void testLicenseNotificationIsSevereForLicenseThatExpiresWithin1Day() {
        DecryptedLicenseData licenseData = new DecryptedLicenseData(((System.currentTimeMillis()) + (TimeUnit.HOURS.toMillis(5))), "test");
        assertThat(licenseService.getLicenseExpiryNotification(licenseData), Is.is(SEVERE));
    }

    @Test
    public void testLicenseNotificationIsModerateForLicenseThatExpiresWithin15Days() {
        DecryptedLicenseData licenseData = new DecryptedLicenseData(((System.currentTimeMillis()) + (TimeUnit.DAYS.toMillis(13))), "test");
        assertThat(licenseService.getLicenseExpiryNotification(licenseData), Is.is(MODERATE));
    }

    @Test
    public void testLicenseNotificationIsNullForLicenseWithMoreThan15DaysLeft() {
        DecryptedLicenseData licenseData = new DecryptedLicenseData(((System.currentTimeMillis()) + (TimeUnit.DAYS.toMillis(30))), "test");
        assertThat(licenseService.getLicenseExpiryNotification(licenseData), Is.is(Matchers.nullValue()));
    }

    @Test
    public void testVerifyTamperedEnterpriseLicenseThrowsException() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Decryption error");
        LicenseKey tamperedKey = new LicenseKey("AAAAAQAAAAEAAAEAU2d2c5fhWCGYEOlcedLoffev+ymwGM/yZKtMK50NYsTEFMz+Gun2YC7oRMy5rARb1gJbZQNRKBP/G2/e2QiS0ncvw/LChBLbTKD2m3PR1Efi9vl7GNgcz3pBbtDs/+/BDvTpOyyJxsvE9h+wUOnb9uppSK/kAx0/VIcXfezRSqFLOz7yH5F+w0rvXKYIuWZpfDpGmNl1gsBp6Pb+dPzA2rS8ty/+riaC5viT7gmdq+HJzAx28M1IYatq3IqwWpyG5HzciMSiiLVEAg7D1yj/QXoP39N3Ehceoh+Q9JCPndHDA0F54UZVGsMAddVkBO1kUf50sVXndwRJB9MUXVZQdQ==");
        LicenseService.verifyLicense(tamperedKey);
    }

    @Test
    public void testVerifyExpiredEnterpriseLicense() {
        LicenseKey key = LicenseServiceTest.createEnterpriseLicenseKey(LicenseKey.VERSION, new DecryptedLicenseData(((System.currentTimeMillis()) - (TimeUnit.HOURS.toMillis(5))), "test"));
        assertThat(LicenseService.verifyLicense(key), Is.is(false));
    }
}

