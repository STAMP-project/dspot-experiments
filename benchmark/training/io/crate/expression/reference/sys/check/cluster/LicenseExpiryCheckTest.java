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
package io.crate.expression.reference.sys.check.cluster;


import LicenseExpiryNotification.MODERATE;
import LicenseExpiryNotification.SEVERE;
import SysCheck.Severity.HIGH;
import SysCheck.Severity.MEDIUM;
import io.crate.license.DecryptedLicenseData;
import io.crate.license.LicenseService;
import io.crate.test.integration.CrateUnitTest;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.common.settings.Settings;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;


public class LicenseExpiryCheckTest extends CrateUnitTest {
    private LicenseService licenseService;

    private LicenseExpiryCheck expirationCheck;

    @Test
    public void testSysCheckMetadata() {
        assertThat(expirationCheck.id(), Matchers.is(6));
    }

    @Test
    public void testValidLicense() {
        DecryptedLicenseData thirtyDaysLicense = new DecryptedLicenseData(((System.currentTimeMillis()) + (TimeUnit.DAYS.toMillis(30))), "test");
        Mockito.when(licenseService.currentLicense()).thenReturn(thirtyDaysLicense);
        Mockito.when(licenseService.getLicenseExpiryNotification(thirtyDaysLicense)).thenReturn(null);
        assertThat(expirationCheck.validate(), Matchers.is(true));
    }

    @Test
    public void testLessThanFifteenDaysToExpiryTriggersMediumCheck() {
        DecryptedLicenseData sevenDaysLicense = new DecryptedLicenseData(((System.currentTimeMillis()) + (TimeUnit.DAYS.toMillis(7))), "test");
        Mockito.when(licenseService.currentLicense()).thenReturn(sevenDaysLicense);
        Mockito.when(licenseService.getLicenseExpiryNotification(sevenDaysLicense)).thenReturn(MODERATE);
        assertThat(expirationCheck.validate(), Matchers.is(false));
        assertThat(expirationCheck.severity(), Matchers.is(MEDIUM));
    }

    @Test
    public void testLessThanOneDayToExpiryTriggersSevereCheck() {
        DecryptedLicenseData sevenDaysLicense = new DecryptedLicenseData(((System.currentTimeMillis()) + (TimeUnit.MINUTES.toMillis(15))), "test");
        Mockito.when(licenseService.currentLicense()).thenReturn(sevenDaysLicense);
        Mockito.when(licenseService.getLicenseExpiryNotification(sevenDaysLicense)).thenReturn(SEVERE);
        assertThat(expirationCheck.validate(), Matchers.is(false));
        assertThat(expirationCheck.severity(), Matchers.is(HIGH));
    }

    @Test
    public void testCheckIsAlwaysValidWhenEnterpriseIsDisabled() {
        Settings settings = Settings.builder().put("license.enterprise", false).build();
        LicenseExpiryCheck expiryCheckNoEnterprise = new LicenseExpiryCheck(settings, Mockito.mock(LicenseService.class));
        assertThat(expiryCheckNoEnterprise.validate(), Matchers.is(true));
    }
}

