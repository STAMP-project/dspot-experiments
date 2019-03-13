/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.controller.service;


import java.util.concurrent.ExecutionException;
import org.apache.nifi.bundle.Bundle;
import org.apache.nifi.components.state.StateManager;
import org.apache.nifi.components.state.StateManagerProvider;
import org.apache.nifi.controller.FlowController;
import org.apache.nifi.controller.scheduling.StandardProcessScheduler;
import org.apache.nifi.engine.FlowEngine;
import org.apache.nifi.nar.ExtensionDiscoveringManager;
import org.apache.nifi.registry.VariableRegistry;
import org.apache.nifi.util.NiFiProperties;
import org.junit.Test;
import org.mockito.Mockito;


public class StandardControllerServiceProviderIT {
    private static Bundle systemBundle;

    private static NiFiProperties niFiProperties;

    private static ExtensionDiscoveringManager extensionManager;

    private static VariableRegistry variableRegistry = VariableRegistry.ENVIRONMENT_SYSTEM_REGISTRY;

    private static StateManagerProvider stateManagerProvider = new StateManagerProvider() {
        @Override
        public StateManager getStateManager(final String componentId) {
            return Mockito.mock(StateManager.class);
        }

        @Override
        public void shutdown() {
        }

        @Override
        public void enableClusterProvider() {
        }

        @Override
        public void disableClusterProvider() {
        }

        @Override
        public void onComponentRemoved(final String componentId) {
        }
    };

    /**
     * We run the same test 1000 times and prior to bug fix (see NIFI-1143) it
     * would fail on some iteration. For more details please see
     * {@link PropertyDescriptor}.isDependentServiceEnableable() as well as
     * https://issues.apache.org/jira/browse/NIFI-1143
     */
    @Test(timeout = 120000)
    public void testConcurrencyWithEnablingReferencingServicesGraph() throws InterruptedException, ExecutionException {
        final StandardProcessScheduler scheduler = new StandardProcessScheduler(new FlowEngine(1, "Unit Test", true), Mockito.mock(FlowController.class), null, StandardControllerServiceProviderIT.stateManagerProvider, StandardControllerServiceProviderIT.niFiProperties);
        for (int i = 0; i < 5000; i++) {
            testEnableReferencingServicesGraph(scheduler);
        }
    }
}

