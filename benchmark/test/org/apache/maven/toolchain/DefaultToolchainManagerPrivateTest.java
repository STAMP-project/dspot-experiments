/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.toolchain;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.toolchain.model.ToolchainModel;
import org.codehaus.plexus.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


public class DefaultToolchainManagerPrivateTest {
    // Mocks to inject into toolchainManager
    @Mock
    private Logger logger;

    @InjectMocks
    private DefaultToolchainManagerPrivate toolchainManager;

    @Mock
    private ToolchainFactory toolchainFactory_basicType;

    @Mock
    private ToolchainFactory toolchainFactory_rareType;

    @Test
    public void testToolchainsForAvailableType() throws Exception {
        // prepare
        MavenSession session = Mockito.mock(MavenSession.class);
        MavenExecutionRequest req = new DefaultMavenExecutionRequest();
        Mockito.when(session.getRequest()).thenReturn(req);
        ToolchainPrivate basicToolchain = Mockito.mock(ToolchainPrivate.class);
        Mockito.when(toolchainFactory_basicType.createDefaultToolchain()).thenReturn(basicToolchain);
        ToolchainPrivate rareToolchain = Mockito.mock(ToolchainPrivate.class);
        Mockito.when(toolchainFactory_rareType.createDefaultToolchain()).thenReturn(rareToolchain);
        // execute
        ToolchainPrivate[] toolchains = toolchainManager.getToolchainsForType("basic", session);
        // verify
        Mockito.verify(logger, Mockito.never()).error(ArgumentMatchers.anyString());
        Assert.assertEquals(1, toolchains.length);
    }

    @Test
    public void testToolchainsForUnknownType() throws Exception {
        // prepare
        MavenSession session = Mockito.mock(MavenSession.class);
        MavenExecutionRequest req = new DefaultMavenExecutionRequest();
        Mockito.when(session.getRequest()).thenReturn(req);
        ToolchainPrivate basicToolchain = Mockito.mock(ToolchainPrivate.class);
        Mockito.when(toolchainFactory_basicType.createDefaultToolchain()).thenReturn(basicToolchain);
        ToolchainPrivate rareToolchain = Mockito.mock(ToolchainPrivate.class);
        Mockito.when(toolchainFactory_rareType.createDefaultToolchain()).thenReturn(rareToolchain);
        // execute
        ToolchainPrivate[] toolchains = toolchainManager.getToolchainsForType("unknown", session);
        // verify
        Mockito.verify(logger).error("Missing toolchain factory for type: unknown. Possibly caused by misconfigured project.");
        Assert.assertEquals(0, toolchains.length);
    }

    @Test
    public void testToolchainsForConfiguredType() throws Exception {
        // prepare
        MavenSession session = Mockito.mock(MavenSession.class);
        MavenExecutionRequest req = new DefaultMavenExecutionRequest();
        Mockito.when(session.getRequest()).thenReturn(req);
        Map<String, List<ToolchainModel>> groupedToolchains = new HashMap<>();
        req.setToolchains(groupedToolchains);
        List<ToolchainModel> basicToolchains = new ArrayList<>();
        ToolchainModel basicToolchainModel = new ToolchainModel();
        basicToolchainModel.setType("basic");
        basicToolchains.add(basicToolchainModel);
        basicToolchains.add(basicToolchainModel);
        groupedToolchains.put("basic", basicToolchains);
        List<ToolchainModel> rareToolchains = new ArrayList<>();
        ToolchainModel rareToolchainModel = new ToolchainModel();
        rareToolchainModel.setType("rare");
        rareToolchains.add(rareToolchainModel);
        groupedToolchains.put("rare", rareToolchains);
        // execute
        ToolchainPrivate[] toolchains = toolchainManager.getToolchainsForType("basic", session);
        // verify
        Mockito.verify(logger, Mockito.never()).error(ArgumentMatchers.anyString());
        Assert.assertEquals(2, toolchains.length);
    }

    @Test
    public void testMisconfiguredToolchain() throws Exception {
        // prepare
        MavenSession session = Mockito.mock(MavenSession.class);
        MavenExecutionRequest req = new DefaultMavenExecutionRequest();
        Mockito.when(session.getRequest()).thenReturn(req);
        // execute
        ToolchainPrivate[] basics = toolchainManager.getToolchainsForType("basic", session);
        // verify
        Assert.assertEquals(0, basics.length);
    }
}

