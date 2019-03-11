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
package com.thoughtworks.go.domain;


import com.thoughtworks.go.config.materials.PluggableSCMMaterial;
import com.thoughtworks.go.config.materials.mercurial.HgMaterial;
import com.thoughtworks.go.helper.MaterialsMother;
import com.thoughtworks.go.util.json.JsonHelper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class MaterialInstanceTest {
    @Test
    public void shouldGenerateUniqueFingerprintOnCreation() throws Exception {
        MaterialInstance one = new HgMaterial("url", null).createMaterialInstance();
        MaterialInstance two = new HgMaterial("otherurl", null).createMaterialInstance();
        Assert.assertThat(one.getFingerprint(), Matchers.not(Matchers.nullValue()));
        Assert.assertThat(one.getFingerprint(), Matchers.not(Matchers.is(two.getFingerprint())));
    }

    @Test
    public void shouldSerializeAndUnserializeAllAttributes() throws IOException, ClassNotFoundException {
        HgMaterial m = MaterialsMother.hgMaterial("url");
        MaterialInstance materialInstance = m.createMaterialInstance();
        materialInstance.setId(10);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(materialInstance);
        ObjectInputStream inputStream1 = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        MaterialInstance unserializedMaterial = ((MaterialInstance) (inputStream1.readObject()));
        Assert.assertThat(unserializedMaterial, Matchers.is(materialInstance));
        Assert.assertThat(unserializedMaterial.getId(), Matchers.is(10L));
        Assert.assertThat(unserializedMaterial, Matchers.is(materialInstance));
    }

    @Test
    public void shouldAnswerRequiresUpdate() {
        PluggableSCMMaterial material = MaterialsMother.pluggableSCMMaterial();
        MaterialInstance materialInstance = material.createMaterialInstance();
        // null
        materialInstance.setAdditionalData(null);
        Assert.assertThat(materialInstance.requiresUpdate(null), Matchers.is(false));
        Assert.assertThat(materialInstance.requiresUpdate(new HashMap()), Matchers.is(false));
        // empty
        materialInstance.setAdditionalData(JsonHelper.toJsonString(new HashMap<String, String>()));
        Assert.assertThat(materialInstance.requiresUpdate(null), Matchers.is(false));
        Assert.assertThat(materialInstance.requiresUpdate(new HashMap()), Matchers.is(false));
        // with data
        Map<String, String> data = new HashMap<>();
        data.put("k1", "v1");
        data.put("k2", "v2");
        materialInstance.setAdditionalData(JsonHelper.toJsonString(data));
        Assert.assertThat(materialInstance.requiresUpdate(null), Matchers.is(true));
        Assert.assertThat(materialInstance.requiresUpdate(new HashMap()), Matchers.is(true));
        Assert.assertThat(materialInstance.requiresUpdate(data), Matchers.is(false));
        // missing key-value
        Map<String, String> dataWithMissingKey = new HashMap<>(data);
        dataWithMissingKey.remove("k1");
        Assert.assertThat(materialInstance.requiresUpdate(dataWithMissingKey), Matchers.is(true));
        // extra key-value
        Map<String, String> dataWithExtraKey = new HashMap<>(data);
        dataWithExtraKey.put("k3", "v3");
        Assert.assertThat(materialInstance.requiresUpdate(dataWithExtraKey), Matchers.is(true));
    }
}

