/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.domain.materials.dependency;


import com.thoughtworks.go.domain.materials.MaterialAgent;
import com.thoughtworks.go.domain.materials.MaterialAgentFactory;
import com.thoughtworks.go.remote.AgentIdentifier;
import com.thoughtworks.go.util.command.ProcessOutputStreamConsumer;
import java.io.File;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class DependencyMaterialAgentTest {
    @Test
    public void shouldBeCreatedByAgentFactory() {
        MaterialAgentFactory factory = new MaterialAgentFactory(ProcessOutputStreamConsumer.inMemoryConsumer(), new File("blah"), new AgentIdentifier("", "", ""), null);
        MaterialAgent createdAgent = factory.createAgent(materialRevision("pipeline-name", 1, "pipeline-label", "stage-name", 1));
        Assert.assertThat(createdAgent, Matchers.is(MaterialAgent.NO_OP));
    }
}

