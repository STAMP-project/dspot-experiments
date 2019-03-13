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
package io.crate.expression.reference.sys.cluster;


import io.crate.license.DecryptedLicenseData;
import io.crate.license.LicenseService;
import io.crate.test.integration.CrateUnitTest;
import java.util.Map;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.mockito.Mockito;


public class ClusterLicenseExpressionTest extends CrateUnitTest {
    private LicenseService licenseService;

    private ClusterLicenseExpression licenseExpression;

    @Test
    public void testExpressionValueReturnsNullForMissingLicense() {
        Mockito.when(licenseService.currentLicense()).thenReturn(null);
        assertThat(licenseExpression.value(), Is.is(IsNull.nullValue()));
    }

    @Test
    public void testChildValueReturnsNullForMissingLicense() {
        Mockito.when(licenseService.currentLicense()).thenReturn(null);
        assertThat(licenseExpression.getChild(ClusterLicenseExpression.EXPIRY_DATE).value(), Is.is(IsNull.nullValue()));
        assertThat(licenseExpression.getChild(ClusterLicenseExpression.ISSUED_TO).value(), Is.is(IsNull.nullValue()));
    }

    @Test
    public void testExpressionValueReturnsLicenseFields() {
        Mockito.when(licenseService.currentLicense()).thenReturn(new DecryptedLicenseData(3L, "test"));
        Map<String, Object> expressionValues = licenseExpression.value();
        assertThat(expressionValues.size(), Is.is(2));
        assertThat(expressionValues.get(ClusterLicenseExpression.EXPIRY_DATE), Is.is(3L));
        assertThat(expressionValues.get(ClusterLicenseExpression.ISSUED_TO), Is.is("test"));
    }

    @Test
    public void testGetChild() {
        Mockito.when(licenseService.currentLicense()).thenReturn(new DecryptedLicenseData(3L, "test"));
        assertThat(licenseExpression.getChild(ClusterLicenseExpression.EXPIRY_DATE).value(), Is.is(3L));
        assertThat(licenseExpression.getChild(ClusterLicenseExpression.ISSUED_TO).value(), Is.is("test"));
    }

    @Test
    public void testGetChildAfterValueCallReturnsUpdatedLicenseFields() {
        Mockito.when(licenseService.currentLicense()).thenReturn(new DecryptedLicenseData(3L, "test")).thenReturn(new DecryptedLicenseData(4L, "test2")).thenReturn(new DecryptedLicenseData(4L, "test2"));
        Map<String, Object> expressionValues = licenseExpression.value();
        assertThat(expressionValues.size(), Is.is(2));
        assertThat(expressionValues.get(ClusterLicenseExpression.EXPIRY_DATE), Is.is(3L));
        assertThat(expressionValues.get(ClusterLicenseExpression.ISSUED_TO), Is.is("test"));
        assertThat(licenseExpression.getChild(ClusterLicenseExpression.EXPIRY_DATE).value(), Is.is(4L));
        assertThat(licenseExpression.getChild(ClusterLicenseExpression.ISSUED_TO).value(), Is.is("test2"));
    }
}

