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
package com.thoughtworks.go.plugin.configrepo.contract.material;


import CRPackageMaterial.TYPE_NAME;
import com.google.gson.JsonObject;
import com.thoughtworks.go.plugin.configrepo.contract.CRBaseTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CRPackageMaterialTest extends CRBaseTest<CRPackageMaterial> {
    private CRPackageMaterial packageMaterial = new CRPackageMaterial("apt-package-plugin-id");

    private CRPackageMaterial namedPackageMaterial = new CRPackageMaterial("myapt", "apt-repo-id");

    private CRPackageMaterial invalidPackageMaterialNoId = new CRPackageMaterial();

    @Test
    public void shouldAppendTypeFieldWhenSerializingMaterials() {
        CRMaterial value = packageMaterial;
        JsonObject jsonObject = ((JsonObject) (gson.toJsonTree(value)));
        Assert.assertThat(jsonObject.get("type").getAsString(), Matchers.is(TYPE_NAME));
    }

    @Test
    public void shouldHandlePolymorphismWhenDeserializing() {
        CRMaterial value = packageMaterial;
        String json = gson.toJson(value);
        CRPackageMaterial deserializedValue = ((CRPackageMaterial) (gson.fromJson(json, CRMaterial.class)));
        Assert.assertThat("Deserialized value should equal to value before serialization", deserializedValue, Matchers.is(value));
    }
}

