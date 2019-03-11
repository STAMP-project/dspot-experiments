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
package com.thoughtworks.go.plugin.configrepo.contract.tasks;


import CRPluggableTask.TYPE_NAME;
import com.google.gson.JsonObject;
import com.thoughtworks.go.plugin.configrepo.contract.CRBaseTest;
import com.thoughtworks.go.plugin.configrepo.contract.CRConfigurationProperty;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CRPluggableTaskTest extends CRBaseTest<CRPluggableTask> {
    private final CRPluggableTask curl;

    private final CRPluggableTask example;

    private final CRPluggableTask invalidNoPlugin;

    private final CRPluggableTask invalidDuplicatedKeys;

    public CRPluggableTaskTest() {
        curl = new CRPluggableTask("curl.task.plugin", "1", new CRConfigurationProperty("Url", "http://www.google.com"), new CRConfigurationProperty("SecureConnection", "no"), new CRConfigurationProperty("RequestType", "no"));
        example = new CRPluggableTask("example.task.plugin", "1");
        invalidNoPlugin = new CRPluggableTask();
        invalidDuplicatedKeys = new CRPluggableTask("curl.task.plugin", "1", new CRConfigurationProperty("Url", "http://www.google.com"), new CRConfigurationProperty("Url", "http://www.gg.com"));
    }

    @Test
    public void shouldAppendTypeFieldWhenSerializingTask() {
        CRTask value = curl;
        JsonObject jsonObject = ((JsonObject) (gson.toJsonTree(value)));
        Assert.assertThat(jsonObject.get("type").getAsString(), Matchers.is(TYPE_NAME));
    }

    @Test
    public void shouldHandlePolymorphismWhenDeserializingTask() {
        CRTask value = curl;
        String json = gson.toJson(value);
        CRPluggableTask deserializedValue = ((CRPluggableTask) (gson.fromJson(json, CRTask.class)));
        Assert.assertThat("Deserialized value should equal to value before serialization", deserializedValue, Matchers.is(value));
    }
}

