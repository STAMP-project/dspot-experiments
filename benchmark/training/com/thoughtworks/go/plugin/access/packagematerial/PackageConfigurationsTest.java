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
import PackageConfiguration.PART_OF_IDENTITY;
import PackageConfiguration.REQUIRED;
import Property.SECURE;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialProperty;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PackageConfigurationsTest {
    @Test
    public void shouldGetAllPackagesSortedByDisplayOrder() throws Exception {
        PackageConfiguration c1 = new PackageConfiguration("k1").with(DISPLAY_ORDER, 2);
        PackageConfiguration c2 = new PackageConfiguration("k2").with(DISPLAY_ORDER, 0);
        PackageConfiguration c3 = new PackageConfiguration("k3").with(DISPLAY_ORDER, 1);
        PackageConfigurations packageConfigurations = new PackageConfigurations();
        packageConfigurations.add(c1);
        packageConfigurations.add(c2);
        packageConfigurations.add(c3);
        Assert.assertThat(packageConfigurations.list().get(0), Matchers.is(c2));
        Assert.assertThat(packageConfigurations.list().get(1), Matchers.is(c3));
        Assert.assertThat(packageConfigurations.list().get(2), Matchers.is(c1));
    }

    @Test
    public void shouldConstructPackageConfigurationFromApiRepositoryConfiguration() throws Exception {
        RepositoryConfiguration configuration = new RepositoryConfiguration();
        configuration.add(new PackageMaterialProperty("k1", "v1").with(SECURE, Boolean.TRUE));
        PackageConfigurations packageConfigurations = new PackageConfigurations(configuration);
        Assert.assertThat(packageConfigurations.list().size(), Matchers.is(1));
        Assert.assertThat(packageConfigurations.list().get(0).getKey(), Matchers.is("k1"));
        Assert.assertThat(packageConfigurations.list().get(0).getValue(), Matchers.is("v1"));
        Assert.assertThat(packageConfigurations.list().get(0).getOption(REQUIRED), Matchers.is(true));
        Assert.assertThat(packageConfigurations.list().get(0).getOption(PART_OF_IDENTITY), Matchers.is(true));
        Assert.assertThat(packageConfigurations.list().get(0).getOption(PackageConfiguration.SECURE), Matchers.is(true));
        Assert.assertThat(packageConfigurations.list().get(0).getOption(DISPLAY_NAME), Matchers.is(""));
        Assert.assertThat(packageConfigurations.list().get(0).getOption(DISPLAY_ORDER), Matchers.is(0));
    }

    @Test
    public void shouldConstructPackageConfigurationFromApiPackageConfiguration() throws Exception {
        com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration configuration = new com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration();
        configuration.add(new PackageMaterialProperty("k1", "v1").with(SECURE, Boolean.TRUE));
        PackageConfigurations packageConfigurations = new PackageConfigurations(configuration);
        Assert.assertThat(packageConfigurations.list().size(), Matchers.is(1));
        Assert.assertThat(packageConfigurations.list().get(0).getKey(), Matchers.is("k1"));
        Assert.assertThat(packageConfigurations.list().get(0).getValue(), Matchers.is("v1"));
        Assert.assertThat(packageConfigurations.list().get(0).getOption(REQUIRED), Matchers.is(true));
        Assert.assertThat(packageConfigurations.list().get(0).getOption(PART_OF_IDENTITY), Matchers.is(true));
        Assert.assertThat(packageConfigurations.list().get(0).getOption(PackageConfiguration.SECURE), Matchers.is(true));
        Assert.assertThat(packageConfigurations.list().get(0).getOption(DISPLAY_NAME), Matchers.is(""));
        Assert.assertThat(packageConfigurations.list().get(0).getOption(DISPLAY_ORDER), Matchers.is(0));
    }
}

