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
package com.thoughtworks.go.plugin.access.elastic.v3;


import com.thoughtworks.go.plugin.access.elastic.models.AgentMetadata;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class AgentMetadataConverterV3Test {
    @Test
    public void fromDTO_shouldConvertToAgentMetadataFromAgentMetadataDTO() {
        final AgentMetadataDTO agentMetadataDTO = new AgentMetadataDTO("agent-id", "Idle", "Building", "Enabled");
        final AgentMetadata agentMetadata = new AgentMetadataConverterV3().fromDTO(agentMetadataDTO);
        Assert.assertThat(agentMetadata.elasticAgentId(), Matchers.is("agent-id"));
        Assert.assertThat(agentMetadata.agentState(), Matchers.is("Idle"));
        Assert.assertThat(agentMetadata.buildState(), Matchers.is("Building"));
        Assert.assertThat(agentMetadata.configState(), Matchers.is("Enabled"));
    }

    @Test
    public void fromDTO_shouldConvertToAgentMetadataDTOFromAgentMetadata() {
        final AgentMetadata agentMetadata = new AgentMetadata("agent-id", "Idle", "Building", "Enabled");
        final AgentMetadataDTO agentMetadataDTO = new AgentMetadataConverterV3().toDTO(agentMetadata);
        Assert.assertThat(agentMetadataDTO.elasticAgentId(), Matchers.is("agent-id"));
        Assert.assertThat(agentMetadataDTO.agentState(), Matchers.is("Idle"));
        Assert.assertThat(agentMetadataDTO.buildState(), Matchers.is("Building"));
        Assert.assertThat(agentMetadataDTO.configState(), Matchers.is("Enabled"));
    }
}

