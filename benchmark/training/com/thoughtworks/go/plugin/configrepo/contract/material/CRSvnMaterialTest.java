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


import CRSvnMaterial.TYPE_NAME;
import com.google.gson.JsonObject;
import com.thoughtworks.go.plugin.configrepo.contract.CRBaseTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CRSvnMaterialTest extends CRBaseTest<CRSvnMaterial> {
    private final CRSvnMaterial simpleSvn;

    private final CRSvnMaterial simpleSvnAuth;

    private final CRSvnMaterial customSvn;

    private final CRSvnMaterial invalidNoUrl;

    private final CRSvnMaterial invalidPasswordAndEncyptedPasswordSet;

    public CRSvnMaterialTest() {
        simpleSvn = new CRSvnMaterial();
        simpleSvn.setUrl("http://mypublicrepo");
        simpleSvnAuth = new CRSvnMaterial();
        simpleSvnAuth.setUrl("http://myprivaterepo");
        simpleSvnAuth.setUserName("john");
        simpleSvnAuth.setPassword("pa$sw0rd");
        customSvn = new CRSvnMaterial("svnMaterial1", "destDir1", false, "http://svn", "user1", "pass1", true, false, "tools", "lib");
        invalidNoUrl = new CRSvnMaterial();
        invalidPasswordAndEncyptedPasswordSet = new CRSvnMaterial();
        invalidPasswordAndEncyptedPasswordSet.setUrl("http://myprivaterepo");
        invalidPasswordAndEncyptedPasswordSet.setPassword("pa$sw0rd");
        invalidPasswordAndEncyptedPasswordSet.setEncryptedPassword("26t=$j64");
    }

    @Test
    public void shouldAppendTypeFieldWhenSerializingMaterials() {
        CRMaterial value = customSvn;
        JsonObject jsonObject = ((JsonObject) (gson.toJsonTree(value)));
        Assert.assertThat(jsonObject.get("type").getAsString(), Matchers.is(TYPE_NAME));
    }

    @Test
    public void shouldHandlePolymorphismWhenDeserializing() {
        CRMaterial value = customSvn;
        String json = gson.toJson(value);
        CRSvnMaterial deserializedValue = ((CRSvnMaterial) (gson.fromJson(json, CRMaterial.class)));
        Assert.assertThat("Deserialized value should equal to value before serialization", deserializedValue, Matchers.is(value));
    }
}

