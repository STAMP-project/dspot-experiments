/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.controller.utilities.state;


import State.INSTALLED;
import State.STARTED;
import org.apache.ambari.server.state.ServiceComponent;
import org.apache.ambari.server.state.State;
import org.junit.Assert;
import org.junit.Test;


public class HDFSServiceCalculatedStateTest extends GeneralServiceCalculatedStateTest {
    // Should be in INSTALLED state when all NNs for at least one NS is INSTALLED
    @Test
    public void testServiceState_STOPPED_WITH_TWO_NS() throws Exception {
        simulateNNFederation();
        ServiceComponent nnComponent = service.getServiceComponent("NAMENODE");
        updateServiceState(STARTED);
        nnComponent.getServiceComponentHost("h3").setState(INSTALLED);
        nnComponent.getServiceComponentHost("h4").setState(INSTALLED);
        State state = serviceCalculatedState.getState(clusterName, getServiceName());
        Assert.assertEquals(INSTALLED, state);
    }

    // Should be in STARTED state when at least one NN for each NS is STARTED
    @Test
    public void testServiceState_STARTED_WITH_TWO_NS() throws Exception {
        simulateNNFederation();
        ServiceComponent nnComponent = service.getServiceComponent("NAMENODE");
        updateServiceState(STARTED);
        nnComponent.getServiceComponentHost("h1").setState(INSTALLED);
        nnComponent.getServiceComponentHost("h4").setState(INSTALLED);
        State state = serviceCalculatedState.getState(clusterName, getServiceName());
        Assert.assertEquals(STARTED, state);
    }
}

