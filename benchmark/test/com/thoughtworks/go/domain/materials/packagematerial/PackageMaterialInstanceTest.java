/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.domain.materials.packagematerial;


import com.thoughtworks.go.config.materials.PackageMaterial;
import com.thoughtworks.go.domain.MaterialInstance;
import com.thoughtworks.go.domain.packagerepository.ConfigurationPropertyMother;
import com.thoughtworks.go.domain.packagerepository.PackageDefinition;
import com.thoughtworks.go.helper.MaterialsMother;
import com.thoughtworks.go.util.ReflectionUtil;
import com.thoughtworks.go.util.json.JsonHelper;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PackageMaterialInstanceTest {
    @Test
    public void shouldConvertMaterialInstanceToMaterial() {
        PackageMaterial material = MaterialsMother.packageMaterial();
        PackageDefinition packageDefinition = material.getPackageDefinition();
        PackageMaterialInstance materialInstance = new PackageMaterialInstance(JsonHelper.toJsonString(material), "flyweight");
        materialInstance.setId(1L);
        PackageMaterial constructedMaterial = ((PackageMaterial) (materialInstance.toOldMaterial(null, null, null)));
        Assert.assertThat(constructedMaterial.getPackageDefinition().getConfiguration(), Matchers.is(packageDefinition.getConfiguration()));
        Assert.assertThat(constructedMaterial.getPackageDefinition().getRepository().getPluginConfiguration().getId(), Matchers.is(packageDefinition.getRepository().getPluginConfiguration().getId()));
        Assert.assertThat(constructedMaterial.getPackageDefinition().getRepository().getConfiguration(), Matchers.is(packageDefinition.getRepository().getConfiguration()));
        Assert.assertThat(constructedMaterial.getId(), Matchers.is(1L));
    }

    @Test
    public void shouldTestEqualsBasedOnConfiguration() {
        PackageMaterial material = MaterialsMother.packageMaterial("repo-id", "repo-name", "pkg-id", "pkg-name", ConfigurationPropertyMother.create("key1", false, "value1"));
        MaterialInstance materialInstance = material.createMaterialInstance();
        MaterialInstance materialInstanceCopy = material.createMaterialInstance();
        material.getPackageDefinition().getConfiguration().add(ConfigurationPropertyMother.create("key2", false, "value2"));
        MaterialInstance newMaterialInstance = material.createMaterialInstance();
        Assert.assertThat(materialInstance, Matchers.is(materialInstanceCopy));
        Assert.assertThat(materialInstance, Matchers.is(Matchers.not(newMaterialInstance)));
    }

    @Test
    public void shouldCorrectlyCheckIfUpgradeIsNecessary() {
        PackageMaterial material = MaterialsMother.packageMaterial("repo-id", "repo-name", "pkg-id", "pkg-name", ConfigurationPropertyMother.create("key1", false, "value1"));
        PackageMaterialInstance materialInstance = ((PackageMaterialInstance) (material.createMaterialInstance()));
        materialInstance.setId(10L);
        PackageMaterialInstance materialInstanceCopy = ((PackageMaterialInstance) (material.createMaterialInstance()));
        material.getPackageDefinition().getConfiguration().add(ConfigurationPropertyMother.create("key2", false, "value2"));
        PackageMaterialInstance newMaterialInstance = ((PackageMaterialInstance) (material.createMaterialInstance()));
        Assert.assertThat(materialInstance.shouldUpgradeTo(materialInstanceCopy), Matchers.is(false));
        Assert.assertThat(materialInstance.shouldUpgradeTo(newMaterialInstance), Matchers.is(true));
    }

    @Test
    public void shouldCorrectlyCopyConfigurationValue() {
        PackageMaterialInstance packageMaterialInstance = ((PackageMaterialInstance) (MaterialsMother.packageMaterial().createMaterialInstance()));
        packageMaterialInstance.setId(10L);
        PackageMaterial latestMaterial = MaterialsMother.packageMaterial("repo-id", "name", "pkId", "name", ConfigurationPropertyMother.create("key1", false, "value1"));
        PackageMaterialInstance newPackageMaterialInstance = ((PackageMaterialInstance) (latestMaterial.createMaterialInstance()));
        packageMaterialInstance.upgradeTo(newPackageMaterialInstance);
        Assert.assertThat(packageMaterialInstance.getId(), Matchers.is(10L));
        Assert.assertThat(packageMaterialInstance.getConfiguration(), Matchers.is(newPackageMaterialInstance.getConfiguration()));
    }

    @Test
    public void shouldSetFingerprintWhenConvertingMaterialInstanceToMaterial() {
        String fingerprint = "fingerprint";
        PackageMaterial material = MaterialsMother.packageMaterial();
        PackageMaterialInstance materialInstance = new PackageMaterialInstance(JsonHelper.toJsonString(material), "flyweight");
        ReflectionUtil.setField(materialInstance, "fingerprint", fingerprint);
        materialInstance.setId(1L);
        PackageMaterial constructedMaterial = ((PackageMaterial) (materialInstance.toOldMaterial(null, null, null)));
        Assert.assertThat(constructedMaterial.getFingerprint(), Matchers.is(fingerprint));
    }
}

