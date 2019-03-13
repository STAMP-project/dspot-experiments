/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.ide.processes.runtime;


import com.google.gwtmockito.GwtMockitoTestRunner;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.workspace.model.EnvironmentImpl;
import org.eclipse.che.ide.api.workspace.model.MachineConfigImpl;
import org.eclipse.che.ide.api.workspace.model.MachineImpl;
import org.eclipse.che.ide.api.workspace.model.RuntimeImpl;
import org.eclipse.che.ide.api.workspace.model.ServerConfigImpl;
import org.eclipse.che.ide.api.workspace.model.ServerImpl;
import org.eclipse.che.ide.api.workspace.model.WorkspaceConfigImpl;
import org.eclipse.che.ide.api.workspace.model.WorkspaceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for the {@link ContextBasedRuntimeInfoProvider}.
 *
 * @author Vlad Zhukovskyi
 * @since 5.18.0
 */
@RunWith(GwtMockitoTestRunner.class)
public class ContextBasedRuntimeInfoProviderTest {
    private static final String DEV_MACHINE = "dev-machine";

    private static final String DEFAULT_ENV = "default";

    private static final String CONFIG_REF = "web";

    private static final String CONFIG_PORT = "666";

    private static final String CONFIG_PROTOCOL = "http";

    private static final String CONFIG_URL = "http://example.com/";

    private static final String RUNTIME_URL = "http://example.org/";

    private static final String RUNTIME_REF = "wsagent";

    @Mock
    private AppContext appContext;

    private ContextBasedRuntimeInfoProvider provider;

    @Test
    public void testShouldReturnRuntimeInfoList() throws Exception {
        WorkspaceImpl workspace = Mockito.mock(WorkspaceImpl.class);
        WorkspaceConfigImpl workspaceConfig = Mockito.mock(WorkspaceConfigImpl.class);
        EnvironmentImpl environment = Mockito.mock(EnvironmentImpl.class);
        MachineConfigImpl machineConfig = Mockito.mock(MachineConfigImpl.class);
        Mockito.when(appContext.getWorkspace()).thenReturn(workspace);
        Mockito.when(workspace.getConfig()).thenReturn(workspaceConfig);
        Mockito.when(workspaceConfig.getDefaultEnv()).thenReturn(ContextBasedRuntimeInfoProviderTest.DEFAULT_ENV);
        Mockito.when(workspaceConfig.getEnvironments()).thenReturn(Collections.singletonMap(ContextBasedRuntimeInfoProviderTest.DEFAULT_ENV, environment));
        Mockito.when(environment.getMachines()).thenReturn(Collections.singletonMap(ContextBasedRuntimeInfoProviderTest.DEV_MACHINE, machineConfig));
        RuntimeImpl runtime = Mockito.mock(RuntimeImpl.class);
        MachineImpl runtimeDevMachine = Mockito.mock(MachineImpl.class);
        Mockito.when(workspace.getRuntime()).thenReturn(runtime);
        Mockito.when(runtime.getMachineByName(ArgumentMatchers.eq(ContextBasedRuntimeInfoProviderTest.DEV_MACHINE))).thenReturn(Optional.of(runtimeDevMachine));
        ServerConfigImpl serverConfig1 = Mockito.mock(ServerConfigImpl.class);
        Mockito.when(serverConfig1.getPort()).thenReturn(ContextBasedRuntimeInfoProviderTest.CONFIG_PORT);
        Mockito.when(serverConfig1.getProtocol()).thenReturn(ContextBasedRuntimeInfoProviderTest.CONFIG_PROTOCOL);
        Mockito.when(machineConfig.getServers()).thenReturn(Collections.singletonMap(ContextBasedRuntimeInfoProviderTest.CONFIG_REF, serverConfig1));
        ServerImpl runtimeServer1 = Mockito.mock(ServerImpl.class);
        Mockito.when(runtimeServer1.getUrl()).thenReturn(ContextBasedRuntimeInfoProviderTest.CONFIG_URL);
        ServerImpl runtimeServer2 = Mockito.mock(ServerImpl.class);
        Mockito.when(runtimeServer2.getUrl()).thenReturn(ContextBasedRuntimeInfoProviderTest.RUNTIME_URL);
        Map<String, ServerImpl> runtimeServers = new HashMap<>();
        runtimeServers.put(ContextBasedRuntimeInfoProviderTest.CONFIG_REF, runtimeServer1);
        runtimeServers.put(ContextBasedRuntimeInfoProviderTest.RUNTIME_REF, runtimeServer2);
        Mockito.when(runtimeDevMachine.getServers()).thenReturn(runtimeServers);
        List<RuntimeInfo> expectedList = provider.get(ContextBasedRuntimeInfoProviderTest.DEV_MACHINE);
        Assert.assertEquals(expectedList.size(), 2);
        RuntimeInfo expectedRuntime1 = new RuntimeInfo("web", "666", "http", "http://example.com/");
        RuntimeInfo expectedRuntime2 = new RuntimeInfo("wsagent", null, "http", "http://example.org/");
        Assert.assertTrue(expectedList.contains(expectedRuntime1));
        Assert.assertTrue(expectedList.contains(expectedRuntime2));
    }

    @Test(expected = NullPointerException.class)
    public void testShouldCatchNullPointerExceptionWhenMachineNameIsNull() throws Exception {
        provider.get(null);
    }

    @Test
    public void testShouldReturnEmptyListWhenWorkspaceIsNotSet() throws Exception {
        List<RuntimeInfo> expectedList = provider.get(ContextBasedRuntimeInfoProviderTest.DEV_MACHINE);
        Assert.assertTrue(expectedList.isEmpty());
    }
}

