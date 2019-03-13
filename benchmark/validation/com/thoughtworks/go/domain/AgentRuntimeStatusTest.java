/**
 * Copyright 2016 ThoughtWorks, Inc.
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


import AgentRuntimeStatus.Building;
import AgentRuntimeStatus.Cancelled;
import AgentRuntimeStatus.Idle;
import AgentRuntimeStatus.LostContact;
import AgentRuntimeStatus.Missing;
import AgentRuntimeStatus.Unknown;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static AgentRuntimeStatus.Cancelled;


public class AgentRuntimeStatusTest {
    @Test
    public void shouldConvertToBuildState() throws Exception {
        List<AgentRuntimeStatus> agentRuntimeStatuses = Arrays.asList(Idle, Building, Cancelled);
        for (AgentRuntimeStatus status : AgentRuntimeStatus.values()) {
            if (agentRuntimeStatuses.contains(status)) {
                Assert.assertEquals(status.buildState(), status);
            } else {
                Assert.assertEquals(status.buildState(), Unknown);
            }
        }
    }

    @Test
    public void shouldConvertToAgentState() throws Exception {
        List<AgentRuntimeStatus> agentRuntimeStatuses = Arrays.asList(Idle, Building, LostContact, Missing);
        for (AgentRuntimeStatus status : AgentRuntimeStatus.values()) {
            if (agentRuntimeStatuses.contains(status)) {
                Assert.assertEquals(status.agentState(), status);
            } else
                if (status == (Cancelled)) {
                    Assert.assertEquals(status.agentState(), Building);
                } else {
                    Assert.assertEquals(status.agentState(), Unknown);
                }

        }
    }
}

