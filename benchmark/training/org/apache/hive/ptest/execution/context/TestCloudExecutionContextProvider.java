/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.ptest.execution.context;


import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import junit.framework.Assert;
import org.apache.hive.ptest.execution.MockSSHCommandExecutor;
import org.apache.hive.ptest.execution.conf.Host;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestCloudExecutionContextProvider {
    private static final Logger LOG = LoggerFactory.getLogger(TestCloudExecutionContextProvider.class);

    private static final String PRIVATE_KEY = "mykey";

    private static final String USER = "user";

    private static final String[] SLAVE_DIRS = new String[]{ "/tmp/hive-ptest" };

    private static final int NUM_NODES = 2;

    @Rule
    public TemporaryFolder baseDir = new TemporaryFolder();

    private String dataDir;

    private CloudComputeService cloudComputeService;

    private MockSSHCommandExecutor sshCommandExecutor;

    private String workingDir;

    private Template template;

    private NodeMetadata node1;

    private NodeMetadata node2;

    private NodeMetadata node3;

    private RunNodesException runNodesException;

    @Test
    public void testRetry() throws Exception {
        Mockito.when(cloudComputeService.createNodes(ArgumentMatchers.anyInt())).then(new Answer<Set<NodeMetadata>>() {
            int count = 0;

            @Override
            public Set<NodeMetadata> answer(InvocationOnMock invocation) throws Throwable {
                if (((count)++) == 0) {
                    throw runNodesException;
                }
                return Collections.singleton(node3);
            }
        });
        CloudExecutionContextProvider provider = new CloudExecutionContextProvider(dataDir, TestCloudExecutionContextProvider.NUM_NODES, cloudComputeService, sshCommandExecutor, workingDir, TestCloudExecutionContextProvider.PRIVATE_KEY, TestCloudExecutionContextProvider.USER, TestCloudExecutionContextProvider.SLAVE_DIRS, 1, 0, 1);
        ExecutionContext executionContext = provider.createExecutionContext();
        Set<String> hosts = Sets.newHashSet();
        for (Host host : executionContext.getHosts()) {
            hosts.add(host.getName());
        }
        Assert.assertEquals(Sets.newHashSet("1.1.1.1", "1.1.1.3"), hosts);
    }
}

