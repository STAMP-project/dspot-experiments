/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights
 * Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is
 * distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either
 * express or implied. See the License for the specific language
 * governing
 * permissions and limitations under the License.
 */
package com.amazonaws.util;


import VersionInfoUtils.VERSION_INFO_FILE;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class VersionInfoUtilsTest {
    @Test
    public void getVersion() {
        Assert.assertNotNull(VersionInfoUtils.getVersion());
    }

    @Test
    public void OSGi() {
        Assert.assertNotNull(VersionInfoUtils.class.getResource(VERSION_INFO_FILE));
    }

    @Test
    public void userAgent() {
        String userAgent = VersionInfoUtils.userAgent();
        Assert.assertNotNull(userAgent);
    }

    @Test
    public void userAgentWithCustomVendorName() {
        // store the current value before setting it
        String vendorName = System.getProperty("java.vendor");
        System.setProperty("java.vendor", "foo");
        String userAgent = VersionInfoUtils.userAgent();
        // reset the value
        System.setProperty("java.vendor", vendorName);
        Assert.assertThat(userAgent, Matchers.containsString("vendor/foo"));
    }

    @Test
    public void userAgentWithJDKProvidedVendorName() {
        String userAgent = VersionInfoUtils.userAgent();
        String vendorName = System.getProperty("java.vendor");
        if (vendorName != null) {
            // Note: This mimics the behavior in VendorInfoUtils by replacing spaces
            // with underscores. If that logic changes, this test will break
            vendorName = vendorName.replace(" ", "_");
            Assert.assertThat(userAgent, Matchers.containsString(("vendor/" + vendorName)));
        } else {
            // Note: This assumes that VendorInfoUtils.UNKNOWN == "unknown"
            // If that value changes, this test will break
            Assert.assertThat(userAgent, Matchers.containsString("vendor/unknown"));
        }
    }

    @Test
    public void userAgentWithNullVendorName() {
        String vendorName = System.getProperty("java.vendor");
        System.clearProperty("java.vendor");
        String userAgent = VersionInfoUtils.userAgent();
        System.setProperty("java.vendor", vendorName);
        // Note: This assumes the value of VendorInfoUtils.UNKNOWN == "unknown"
        // If that value changes, this test will break
        Assert.assertThat(userAgent, Matchers.containsString("vendor/unknown"));
    }
}

