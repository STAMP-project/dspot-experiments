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


import java.util.Arrays;
import java.util.Collections;
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


public class DefaultToolchainManagerTest {
    // Mocks to inject into toolchainManager
    @Mock
    private Logger logger;

    @InjectMocks
    private DefaultToolchainManager toolchainManager;

    @Mock
    private ToolchainFactory toolchainFactory_basicType;

    @Mock
    private ToolchainFactory toolchainFactory_rareType;

    @Test
    public void testNoModels() {
        MavenSession session = Mockito.mock(MavenSession.class);
        MavenExecutionRequest executionRequest = new DefaultMavenExecutionRequest();
        Mockito.when(session.getRequest()).thenReturn(executionRequest);
        List<Toolchain> toolchains = toolchainManager.getToolchains(session, "unknown", null);
        Assert.assertEquals(0, toolchains.size());
    }

    @Test
    public void testModelNoFactory() {
        MavenSession session = Mockito.mock(MavenSession.class);
        MavenExecutionRequest executionRequest = new DefaultMavenExecutionRequest();
        Map<String, List<ToolchainModel>> toolchainModels = new HashMap<>();
        toolchainModels.put("unknown", Collections.singletonList(new ToolchainModel()));
        executionRequest.setToolchains(toolchainModels);
        Mockito.when(session.getRequest()).thenReturn(executionRequest);
        List<Toolchain> toolchains = toolchainManager.getToolchains(session, "unknown", null);
        Assert.assertEquals(0, toolchains.size());
        Mockito.verify(logger).error("Missing toolchain factory for type: unknown. Possibly caused by misconfigured project.");
    }

    @Test
    public void testModelAndFactory() {
        MavenSession session = Mockito.mock(MavenSession.class);
        MavenExecutionRequest executionRequest = new DefaultMavenExecutionRequest();
        Map<String, List<ToolchainModel>> toolchainModels = new HashMap<>();
        toolchainModels.put("basic", Arrays.asList(new ToolchainModel(), new ToolchainModel()));
        toolchainModels.put("rare", Collections.singletonList(new ToolchainModel()));
        executionRequest.setToolchains(toolchainModels);
        Mockito.when(session.getRequest()).thenReturn(executionRequest);
        List<Toolchain> toolchains = toolchainManager.getToolchains(session, "rare", null);
        Assert.assertEquals(1, toolchains.size());
    }

    @Test
    public void testModelsAndFactory() {
        MavenSession session = Mockito.mock(MavenSession.class);
        MavenExecutionRequest executionRequest = new DefaultMavenExecutionRequest();
        Map<String, List<ToolchainModel>> toolchainModels = new HashMap<>();
        toolchainModels.put("basic", Arrays.asList(new ToolchainModel(), new ToolchainModel()));
        toolchainModels.put("rare", Collections.singletonList(new ToolchainModel()));
        executionRequest.setToolchains(toolchainModels);
        Mockito.when(session.getRequest()).thenReturn(executionRequest);
        List<Toolchain> toolchains = toolchainManager.getToolchains(session, "basic", null);
        Assert.assertEquals(2, toolchains.size());
    }

    @Test
    public void testRequirements() throws Exception {
        MavenSession session = Mockito.mock(MavenSession.class);
        MavenExecutionRequest executionRequest = new DefaultMavenExecutionRequest();
        Map<String, List<ToolchainModel>> toolchainModels = new HashMap<>();
        toolchainModels.put("basic", Arrays.asList(new ToolchainModel(), new ToolchainModel()));
        toolchainModels.put("rare", Collections.singletonList(new ToolchainModel()));
        executionRequest.setToolchains(toolchainModels);
        Mockito.when(session.getRequest()).thenReturn(executionRequest);
        ToolchainPrivate basicPrivate = Mockito.mock(ToolchainPrivate.class);
        Mockito.when(basicPrivate.matchesRequirements(ArgumentMatchers.<String, String>anyMap())).thenReturn(false).thenReturn(true);
        Mockito.when(toolchainFactory_basicType.createToolchain(ArgumentMatchers.isA(ToolchainModel.class))).thenReturn(basicPrivate);
        List<Toolchain> toolchains = toolchainManager.getToolchains(session, "basic", Collections.singletonMap("key", "value"));
        Assert.assertEquals(1, toolchains.size());
    }
}

