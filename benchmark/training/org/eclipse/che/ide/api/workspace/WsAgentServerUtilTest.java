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
package org.eclipse.che.ide.api.workspace;


import java.util.Optional;
import org.eclipse.che.ide.QueryParameters;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.workspace.model.MachineImpl;
import org.eclipse.che.ide.api.workspace.model.RuntimeImpl;
import org.eclipse.che.ide.api.workspace.model.ServerImpl;
import org.eclipse.che.ide.api.workspace.model.WorkspaceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for the {@link WsAgentServerUtil}.
 */
@RunWith(MockitoJUnitRunner.class)
public class WsAgentServerUtilTest {
    static final String REF_PREFIX = "dev-";

    @Mock
    AppContext appContext;

    @Mock
    QueryParameters queryParameters;

    @Mock
    RuntimeImpl runtime;

    @Mock
    WorkspaceImpl workspace;

    @Mock
    MachineImpl machine;

    @Mock
    ServerImpl serverWsAgentHTTP;

    @Mock
    ServerImpl serverWsAgentWebSocket;

    @InjectMocks
    WsAgentServerUtil util;

    @Test
    public void shouldReturnWsAgentServerMachine() throws Exception {
        mockRuntime();
        Optional<MachineImpl> machineOpt = util.getWsAgentServerMachine();
        Assert.assertTrue(machineOpt.isPresent());
        Assert.assertEquals(machine, machineOpt.get());
    }

    @Test
    public void shouldReturnServerByRef() throws Exception {
        mockRuntime();
        Optional<ServerImpl> serverOpt = util.getServerByRef(SERVER_WS_AGENT_HTTP_REFERENCE);
        Assert.assertTrue(serverOpt.isPresent());
        Assert.assertEquals(serverWsAgentHTTP, serverOpt.get());
    }

    @Test
    public void shouldNotReturnServerByWrongRef() throws Exception {
        mockRuntime();
        Optional<ServerImpl> serverOpt = util.getServerByRef("wrong-ref");
        Assert.assertFalse(serverOpt.isPresent());
    }

    @Test
    public void shouldReturnWsAgentHttpServerReferenceWithPrefix() throws Exception {
        Mockito.when(queryParameters.getByName(WsAgentServerUtil.WSAGENT_SERVER_REF_PREFIX_PARAM)).thenReturn(WsAgentServerUtilTest.REF_PREFIX);
        String serverRef = util.getWsAgentHttpServerReference();
        Mockito.verify(queryParameters).getByName(WsAgentServerUtil.WSAGENT_SERVER_REF_PREFIX_PARAM);
        Assert.assertEquals(((WsAgentServerUtilTest.REF_PREFIX) + (SERVER_WS_AGENT_HTTP_REFERENCE)), serverRef);
    }

    @Test
    public void shouldReturnWsAgentHttpServerReferenceWithoutPrefix() throws Exception {
        Mockito.when(queryParameters.getByName(WsAgentServerUtil.WSAGENT_SERVER_REF_PREFIX_PARAM)).thenReturn("");
        String serverRef = util.getWsAgentHttpServerReference();
        Mockito.verify(queryParameters).getByName(WsAgentServerUtil.WSAGENT_SERVER_REF_PREFIX_PARAM);
        Assert.assertEquals(SERVER_WS_AGENT_HTTP_REFERENCE, serverRef);
    }

    @Test
    public void shouldReturnWsAgentWebSocketServerReferenceWithPrefix() throws Exception {
        Mockito.when(queryParameters.getByName(WsAgentServerUtil.WSAGENT_SERVER_REF_PREFIX_PARAM)).thenReturn(WsAgentServerUtilTest.REF_PREFIX);
        String serverRef = util.getWsAgentWebSocketServerReference();
        Mockito.verify(queryParameters).getByName(WsAgentServerUtil.WSAGENT_SERVER_REF_PREFIX_PARAM);
        Assert.assertEquals(((WsAgentServerUtilTest.REF_PREFIX) + (SERVER_WS_AGENT_WEBSOCKET_REFERENCE)), serverRef);
    }

    @Test
    public void shouldReturnWsAgentWebSocketServerReferenceWithoutPrefix() throws Exception {
        Mockito.when(queryParameters.getByName(WsAgentServerUtil.WSAGENT_SERVER_REF_PREFIX_PARAM)).thenReturn("");
        String serverRef = util.getWsAgentWebSocketServerReference();
        Mockito.verify(queryParameters).getByName(WsAgentServerUtil.WSAGENT_SERVER_REF_PREFIX_PARAM);
        Assert.assertEquals(SERVER_WS_AGENT_WEBSOCKET_REFERENCE, serverRef);
    }

    @Test
    public void shouldReturnWorkspaceRuntime() throws Exception {
        mockRuntime();
        Optional<RuntimeImpl> runtimeOpt = util.getWorkspaceRuntime();
        Assert.assertTrue(runtimeOpt.isPresent());
        Assert.assertEquals(runtime, runtimeOpt.get());
    }
}

