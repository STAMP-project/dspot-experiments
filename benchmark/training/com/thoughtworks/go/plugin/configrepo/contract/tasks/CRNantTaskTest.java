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


import com.google.gson.JsonObject;
import com.thoughtworks.go.plugin.configrepo.contract.CRBaseTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CRNantTaskTest extends CRBaseTest<CRNantTask> {
    private final CRNantTask nantTask;

    private final CRNantTask nantCompileFileTask;

    private final CRNantTask nantCompileTask;

    private final CRNantTask nantWithDirTask;

    private final CRNantTask nantWithPath;

    public CRNantTaskTest() {
        nantTask = CRBuildTask.nant();
        nantCompileFileTask = CRBuildTask.nant("mybuild.xml", "compile");
        nantCompileTask = CRBuildTask.nant(null, "compile");
        nantWithDirTask = CRBuildTask.nant(null, "build", "src/tasks");
        nantWithPath = CRBuildTask.nant("mybuild.xml", "build", "src/tasks", "/path/to/nant");
    }

    @Test
    public void shouldAppendTypeFieldWhenSerializingNantTask() {
        CRTask value = nantWithPath;
        JsonObject jsonObject = ((JsonObject) (gson.toJsonTree(value)));
        Assert.assertThat(jsonObject.get("type").getAsString(), Matchers.is("nant"));
    }

    @Test
    public void shouldHandlePolymorphismWhenDeserializingNantTask() {
        CRTask value = nantTask;
        String json = gson.toJson(value);
        CRBuildTask deserializedValue = ((CRBuildTask) (gson.fromJson(json, CRTask.class)));
        Assert.assertThat("Deserialized value should equal to value before serialization", deserializedValue, Matchers.is(value));
    }
}

