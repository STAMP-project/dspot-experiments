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
package com.thoughtworks.go.plugin.api.material.packagerepository;


import Property.DISPLAY_NAME;
import Property.DISPLAY_ORDER;
import Property.PART_OF_IDENTITY;
import Property.REQUIRED;
import Property.SECURE;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PackageMaterialPropertyTest {
    @Test
    public void validatePackagePropertyDefaults() throws Exception {
        PackageMaterialProperty packageMaterialProperty = new PackageMaterialProperty("Test-Property");
        Assert.assertThat(packageMaterialProperty.getOptions().size(), Matchers.is(5));
        Assert.assertThat(packageMaterialProperty.getOption(REQUIRED), Matchers.is(true));
        Assert.assertThat(packageMaterialProperty.getOption(PART_OF_IDENTITY), Matchers.is(true));
        Assert.assertThat(packageMaterialProperty.getOption(SECURE), Matchers.is(false));
        Assert.assertThat(packageMaterialProperty.getOption(DISPLAY_NAME), Matchers.is(""));
        Assert.assertThat(packageMaterialProperty.getOption(DISPLAY_ORDER), Matchers.is(0));
        packageMaterialProperty = new PackageMaterialProperty("Test-Property", "Dummy Value");
        Assert.assertThat(packageMaterialProperty.getOptions().size(), Matchers.is(5));
        Assert.assertThat(packageMaterialProperty.getOption(REQUIRED), Matchers.is(true));
        Assert.assertThat(packageMaterialProperty.getOption(PART_OF_IDENTITY), Matchers.is(true));
        Assert.assertThat(packageMaterialProperty.getOption(SECURE), Matchers.is(false));
        Assert.assertThat(packageMaterialProperty.getOption(DISPLAY_NAME), Matchers.is(""));
        Assert.assertThat(packageMaterialProperty.getOption(DISPLAY_ORDER), Matchers.is(0));
    }
}

