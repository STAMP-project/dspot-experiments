/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.plugin.access.packagematerial;


import PackageConfiguration.DISPLAY_NAME;
import PackageConfiguration.DISPLAY_ORDER;
import PackageConfiguration.REQUIRED;
import PackageConfiguration.SECURE;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PackageConfigurationTest {
    @Test
    public void shouldGetOptionIfAvailable() {
        PackageConfiguration packageConfiguration = new PackageConfiguration("key");
        packageConfiguration.with(REQUIRED, true);
        Assert.assertThat(packageConfiguration.hasOption(REQUIRED), Matchers.is(true));
        Assert.assertThat(packageConfiguration.hasOption(SECURE), Matchers.is(false));
    }

    @Test
    public void shouldGetOptionValue() {
        PackageConfiguration packageConfiguration = new PackageConfiguration("key");
        packageConfiguration.with(DISPLAY_NAME, "some display name");
        packageConfiguration.with(DISPLAY_ORDER, 3);
        Assert.assertThat(packageConfiguration.getOption(DISPLAY_NAME), Matchers.is("some display name"));
        Assert.assertThat(packageConfiguration.getOption(DISPLAY_ORDER), Matchers.is(3));
    }

    @Test
    public void shouldSortPackageConfigurationByDisplayOrder() throws Exception {
        PackageConfiguration p1 = new PackageConfiguration("k1").with(DISPLAY_ORDER, 1);
        PackageConfiguration p2 = new PackageConfiguration("k2").with(DISPLAY_ORDER, 3);
        Assert.assertThat(p2.compareTo(p1), Matchers.is(2));
    }
}

