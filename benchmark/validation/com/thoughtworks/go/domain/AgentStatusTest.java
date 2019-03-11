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
package com.thoughtworks.go.domain;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static AgentStatus.Building;
import static AgentStatus.Cancelled;
import static AgentStatus.Disabled;
import static AgentStatus.Idle;
import static AgentStatus.LostContact;
import static AgentStatus.Missing;
import static AgentStatus.Pending;


public class AgentStatusTest {
    @Test
    public void shouldCompareStatusAsExpected() {
        AgentStatus[] statusInOrder = new AgentStatus[]{ Pending, LostContact, Missing, Building, Cancelled, Idle, Disabled };
        AgentStatus previous = null;
        for (AgentStatus status : statusInOrder) {
            if (previous != null) {
                Assert.assertThat(previous.compareTo(status), Matchers.is(org.hamcrest.Matchers.lessThan(0)));
                Assert.assertThat(status.compareTo(previous), Matchers.is(Matchers.greaterThan(0)));
            }
            previous = status;
        }
    }
}

