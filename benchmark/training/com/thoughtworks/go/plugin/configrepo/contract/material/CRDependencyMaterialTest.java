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


import CRDependencyMaterial.TYPE_NAME;
import com.google.gson.JsonObject;
import com.thoughtworks.go.plugin.configrepo.contract.CRBaseTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CRDependencyMaterialTest extends CRBaseTest<CRDependencyMaterial> {
    private final CRDependencyMaterial namedDependsOnPipeline;

    private final CRDependencyMaterial invalidNoPipeline;

    private final CRDependencyMaterial invalidNoStage;

    private CRDependencyMaterial dependsOnPipeline;

    public CRDependencyMaterialTest() {
        namedDependsOnPipeline = new CRDependencyMaterial("pipe2", "pipeline2", "build");
        dependsOnPipeline = new CRDependencyMaterial("pipeline2", "build");
        invalidNoPipeline = new CRDependencyMaterial();
        invalidNoPipeline.setStageName("build");
        invalidNoStage = new CRDependencyMaterial();
        invalidNoStage.setPipelineName("pipeline1");
    }

    @Test
    public void shouldAppendTypeFieldWhenSerializingMaterials() {
        CRMaterial value = dependsOnPipeline;
        JsonObject jsonObject = ((JsonObject) (gson.toJsonTree(value)));
        Assert.assertThat(jsonObject.get("type").getAsString(), Matchers.is(TYPE_NAME));
    }

    @Test
    public void shouldHandlePolymorphismWhenDeserializing() {
        CRMaterial value = dependsOnPipeline;
        String json = gson.toJson(value);
        CRDependencyMaterial deserializedValue = ((CRDependencyMaterial) (gson.fromJson(json, CRMaterial.class)));
        Assert.assertThat("Deserialized value should equal to value before serialization", deserializedValue, Matchers.is(value));
    }
}

