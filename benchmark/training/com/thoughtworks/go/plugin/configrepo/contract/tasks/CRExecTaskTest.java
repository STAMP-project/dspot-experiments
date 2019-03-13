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


import CRExecTask.TYPE_NAME;
import CRRunIf.failed;
import com.google.gson.JsonObject;
import com.thoughtworks.go.plugin.configrepo.contract.CRBaseTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static CRRunIf.any;


public class CRExecTaskTest extends CRBaseTest<CRExecTask> {
    private final CRExecTask simpleExecWithArgs;

    private final CRExecTask execInDir;

    private final CRExecTask simpleExecRunIf;

    private final CRExecTask customExec;

    private final CRExecTask invalidNoCommand;

    private CRExecTask simpleExec;

    public CRExecTaskTest() {
        simpleExec = new CRExecTask("/usr/local/bin/ruby");
        simpleExecWithArgs = new CRExecTask("/usr/local/bin/ruby");
        simpleExecWithArgs.addArgument("backup.rb");
        simpleExecRunIf = new CRExecTask("/usr/local/bin/ruby");
        simpleExecRunIf.setRunIf(failed);
        customExec = new CRExecTask("rake", "dir", 120L, any, simpleExec, "-f", "Rakefile.rb");
        execInDir = new CRExecTask("/usr/local/bin/rake");
        execInDir.setWorkingDirectory("myProjectDir");
        invalidNoCommand = new CRExecTask();
    }

    @Test
    public void shouldAppendTypeFieldWhenSerializingTasks() {
        CRTask value = simpleExecWithArgs;
        JsonObject jsonObject = ((JsonObject) (gson.toJsonTree(value)));
        Assert.assertThat(jsonObject.get("type").getAsString(), Matchers.is(TYPE_NAME));
    }

    @Test
    public void shouldHandlePolymorphismWhenDeserializing() {
        CRTask value = simpleExecWithArgs;
        String json = gson.toJson(value);
        CRExecTask deserializedValue = ((CRExecTask) (gson.fromJson(json, CRTask.class)));
        Assert.assertThat("Deserialized value should equal to value before serialization", deserializedValue, Matchers.is(value));
    }
}

