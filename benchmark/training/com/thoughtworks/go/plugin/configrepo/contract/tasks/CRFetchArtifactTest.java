/**
 * Copyright 2018 ThoughtWorks, Inc.
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


import CRAbstractFetchTask.ArtifactOrigin.gocd;
import CRRunIf.passed;
import com.thoughtworks.go.plugin.configrepo.contract.CRBaseTest;
import junit.framework.TestCase;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CRFetchArtifactTest extends CRBaseTest<CRFetchArtifactTask> {
    private final CRFetchArtifactTask fetch;

    private final CRFetchArtifactTask fetchFromPipe;

    private final CRFetchArtifactTask fetchToDest;

    private final CRFetchArtifactTask invalidFetchNoSource;

    private final CRFetchArtifactTask invalidFetchNoJob;

    private final CRFetchArtifactTask invalidFetchNoStage;

    public CRFetchArtifactTest() {
        fetch = new CRFetchArtifactTask("build", "buildjob", "bin");
        fetchFromPipe = new CRFetchArtifactTask("build", "buildjob", "bin");
        fetchFromPipe.setPipelineName("pipeline1");
        fetchToDest = new CRFetchArtifactTask("build", "buildjob", "bin");
        fetchToDest.setDestination("lib");
        invalidFetchNoSource = new CRFetchArtifactTask("build", "buildjob", null);
        invalidFetchNoJob = new CRFetchArtifactTask("build", null, "bin");
        invalidFetchNoStage = new CRFetchArtifactTask(null, "buildjob", "bin");
    }

    @Test
    public void shouldHandlePolymorphismWhenDeserializingFetchTask() {
        CRTask value = fetch;
        String json = gson.toJson(value);
        CRFetchArtifactTask deserializedValue = ((CRFetchArtifactTask) (gson.fromJson(json, CRTask.class)));
        Assert.assertThat("Deserialized value should equal to value before serialization", deserializedValue, Matchers.is(value));
    }

    @Test
    public void shouldDeserializeWhenDestinationIsNull() {
        String json = "{\n" + ((((((("              \"type\" : \"fetch\",\n" + "              \"pipeline\" : \"pip\",\n") + "              \"stage\" : \"build1\",\n") + "              \"job\" : \"build\",\n") + "              \"source\" : \"bin\",\n") + "              \"run_if\" : \"passed\",\n") + "              \"artifact_origin\" : \"gocd\"\n") + "            }");
        CRFetchArtifactTask deserializedValue = ((CRFetchArtifactTask) (gson.fromJson(json, CRTask.class)));
        Assert.assertThat(deserializedValue.getPipelineName(), Matchers.is("pip"));
        Assert.assertThat(deserializedValue.getJob(), Matchers.is("build"));
        Assert.assertThat(deserializedValue.getStage(), Matchers.is("build1"));
        Assert.assertThat(deserializedValue.getSource(), Matchers.is("bin"));
        Assert.assertThat(deserializedValue.getRunIf(), Matchers.is(passed));
        TestCase.assertNull(deserializedValue.getDestination());
        Assert.assertThat(deserializedValue.sourceIsDirectory(), Matchers.is(true));
        Assert.assertThat(deserializedValue.getArtifactOrigin(), Matchers.is(gocd));
    }

    @Test
    public void shouldDeserializeWhenArtifactOriginIsNull() {
        String json = "{\n" + (((((("              \"type\" : \"fetch\",\n" + "              \"pipeline\" : \"pip\",\n") + "              \"stage\" : \"build1\",\n") + "              \"job\" : \"build\",\n") + "              \"source\" : \"bin\",\n") + "              \"run_if\" : \"passed\"\n") + "            }");
        CRFetchArtifactTask deserializedValue = ((CRFetchArtifactTask) (gson.fromJson(json, CRTask.class)));
        Assert.assertThat(deserializedValue.getPipelineName(), Matchers.is("pip"));
        Assert.assertThat(deserializedValue.getJob(), Matchers.is("build"));
        Assert.assertThat(deserializedValue.getStage(), Matchers.is("build1"));
        Assert.assertThat(deserializedValue.getSource(), Matchers.is("bin"));
        Assert.assertThat(deserializedValue.getRunIf(), Matchers.is(passed));
        TestCase.assertNull(deserializedValue.getDestination());
        Assert.assertThat(deserializedValue.sourceIsDirectory(), Matchers.is(true));
        Assert.assertThat(deserializedValue.getArtifactOrigin(), Matchers.is(gocd));
    }
}

