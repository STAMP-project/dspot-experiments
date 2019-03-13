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


import CRRunIf.passed;
import com.thoughtworks.go.plugin.configrepo.contract.CRBaseTest;
import com.thoughtworks.go.plugin.configrepo.contract.CRConfigurationProperty;
import junit.framework.TestCase;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CRFetchPluggableArtifactTaskTest extends CRBaseTest<CRFetchPluggableArtifactTask> {
    private final CRFetchPluggableArtifactTask fetch;

    private final CRFetchPluggableArtifactTask fetchFromPipe;

    private final CRFetchPluggableArtifactTask invalidFetchNoStoreId;

    private final CRFetchPluggableArtifactTask invalidFetchNoJob;

    private final CRFetchPluggableArtifactTask invalidFetchNoStage;

    private final CRFetchPluggableArtifactTask invalidFetchWithDuplicateProperties;

    public CRFetchPluggableArtifactTaskTest() {
        CRConfigurationProperty crConfigurationProperty = new CRConfigurationProperty("k1", "v1", null);
        fetch = new CRFetchPluggableArtifactTask("build", "buildjob", "storeId", crConfigurationProperty);
        fetchFromPipe = new CRFetchPluggableArtifactTask("build", "buildjob", "storeId", crConfigurationProperty);
        fetchFromPipe.setPipelineName("pipeline1");
        invalidFetchNoStoreId = new CRFetchPluggableArtifactTask("build", "buildjob", null);
        invalidFetchNoJob = new CRFetchPluggableArtifactTask("build", null, "storeId");
        invalidFetchNoStage = new CRFetchPluggableArtifactTask(null, "buildjob", "storeId");
        invalidFetchWithDuplicateProperties = new CRFetchPluggableArtifactTask("build", "buildjob", "storeId", crConfigurationProperty, crConfigurationProperty);
    }

    @Test
    public void shouldDeserializeWhenConfigurationIsNull() {
        String json = "{\n" + ((((((("              \"type\" : \"fetch\",\n" + "              \"pipeline\" : \"pip\",\n") + "              \"stage\" : \"build1\",\n") + "              \"job\" : \"build\",\n") + "              \"artifact_id\" : \"s3\",\n") + "              \"run_if\" : \"passed\",\n") + "              \"artifact_origin\" : \"external\"\n") + "            }");
        CRFetchPluggableArtifactTask deserializedValue = ((CRFetchPluggableArtifactTask) (gson.fromJson(json, CRTask.class)));
        Assert.assertThat(deserializedValue.getPipelineName(), Matchers.is("pip"));
        Assert.assertThat(deserializedValue.getJob(), Matchers.is("build"));
        Assert.assertThat(deserializedValue.getStage(), Matchers.is("build1"));
        Assert.assertThat(deserializedValue.getArtifactId(), Matchers.is("s3"));
        Assert.assertThat(deserializedValue.getRunIf(), Matchers.is(passed));
        TestCase.assertNull(deserializedValue.getConfiguration());
    }
}

