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


public class BuildTaskTest extends CRBaseTest<CRBuildTask> {
    private final CRBuildTask rakeTask;

    private final CRBuildTask rakeCompileTask;

    private final CRBuildTask rakeCompileFileTask;

    private final CRBuildTask invalidTaskNoType;

    private final CRBuildTask rakeWithDirTask;

    private final CRBuildTask antTask;

    private final CRBuildTask antCompileFileTask;

    private final CRBuildTask antCompileTask;

    private final CRBuildTask antWithDirTask;

    public BuildTaskTest() {
        rakeTask = CRBuildTask.rake();
        rakeCompileFileTask = CRBuildTask.rake("Rakefile.rb", "compile");
        rakeCompileTask = CRBuildTask.rake(null, "compile");
        rakeWithDirTask = CRBuildTask.rake(null, "build", "src/tasks");
        antTask = CRBuildTask.ant();
        antCompileFileTask = CRBuildTask.ant("mybuild.xml", "compile");
        antCompileTask = CRBuildTask.ant(null, "compile");
        antWithDirTask = CRBuildTask.ant(null, "build", "src/tasks");
        invalidTaskNoType = new CRBuildTask(null, null, null, null);
    }

    @Test
    public void shouldAppendTypeFieldWhenSerializingAntTask() {
        CRTask value = antTask;
        JsonObject jsonObject = ((JsonObject) (gson.toJsonTree(value)));
        Assert.assertThat(jsonObject.get("type").getAsString(), Matchers.is("ant"));
    }

    @Test
    public void shouldAppendTypeFieldWhenSerializingRakeTask() {
        CRTask value = rakeTask;
        JsonObject jsonObject = ((JsonObject) (gson.toJsonTree(value)));
        Assert.assertThat(jsonObject.get("type").getAsString(), Matchers.is("rake"));
    }

    @Test
    public void shouldHandlePolymorphismWhenDeserializingAntTask() {
        CRTask value = antTask;
        String json = gson.toJson(value);
        CRBuildTask deserializedValue = ((CRBuildTask) (gson.fromJson(json, CRTask.class)));
        Assert.assertThat("Deserialized value should equal to value before serialization", deserializedValue, Matchers.is(value));
    }

    @Test
    public void shouldHandlePolymorphismWhenDeserializingRakeTask() {
        CRTask value = rakeTask;
        String json = gson.toJson(value);
        CRBuildTask deserializedValue = ((CRBuildTask) (gson.fromJson(json, CRTask.class)));
        Assert.assertThat("Deserialized value should equal to value before serialization", deserializedValue, Matchers.is(value));
    }
}

