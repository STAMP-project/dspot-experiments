/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.??See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.??The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.??You may obtain a copy of the License at
 *
 * ???? http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone.container.ozoneimpl;


import DatanodeDetails.Port.Name.STANDALONE;
import HddsConfigKeys.HDDS_GRPC_MUTUAL_TLS_REQUIRED;
import OzoneConfigKeys.DFS_CONTAINER_IPC_PORT;
import OzoneConfigKeys.DFS_CONTAINER_IPC_RANDOM_PORT;
import java.util.UUID;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.scm.ScmConfigKeys;
import org.apache.hadoop.hdds.scm.TestUtils;
import org.apache.hadoop.hdds.scm.XceiverClientGrpc;
import org.apache.hadoop.hdds.scm.pipeline.Pipeline;
import org.apache.hadoop.hdds.security.x509.SecurityConfig;
import org.apache.hadoop.ozone.container.ContainerTestHelper;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests ozone containers via secure grpc/netty.
 */
@RunWith(Parameterized.class)
@Ignore("TODO:HDDS-1157")
public class TestOzoneContainerWithTLS {
    private static final Logger LOG = LoggerFactory.getLogger(TestOzoneContainerWithTLS.class);

    /**
     * Set the timeout for every test.
     */
    @Rule
    public Timeout testTimeout = new Timeout(300000);

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private OzoneConfiguration conf;

    private SecurityConfig secConfig;

    private Boolean requireMutualTls;

    public TestOzoneContainerWithTLS(Boolean requireMutualTls) {
        this.requireMutualTls = requireMutualTls;
    }

    @Test
    public void testCreateOzoneContainer() throws Exception {
        TestOzoneContainerWithTLS.LOG.info("testCreateOzoneContainer with Mutual TLS: {}", requireMutualTls);
        conf.setBoolean(HDDS_GRPC_MUTUAL_TLS_REQUIRED, requireMutualTls);
        long containerID = ContainerTestHelper.getTestContainerID();
        OzoneContainer container = null;
        System.out.println(System.getProperties().getProperty("java.library.path"));
        DatanodeDetails dn = TestUtils.randomDatanodeDetails();
        try {
            Pipeline pipeline = ContainerTestHelper.createSingleNodePipeline();
            conf.set(ScmConfigKeys.HDDS_DATANODE_DIR_KEY, tempFolder.getRoot().getPath());
            conf.setInt(DFS_CONTAINER_IPC_PORT, pipeline.getFirstNode().getPort(STANDALONE).getValue());
            conf.setBoolean(DFS_CONTAINER_IPC_RANDOM_PORT, false);
            container = new OzoneContainer(dn, conf, getContext(dn));
            // Setting scmId, as we start manually ozone container.
            container.getDispatcher().setScmId(UUID.randomUUID().toString());
            container.start();
            XceiverClientGrpc client = new XceiverClientGrpc(pipeline, conf);
            client.connect();
            TestOzoneContainerWithTLS.createContainerForTesting(client, containerID);
        } finally {
            if (container != null) {
                container.stop();
            }
        }
    }
}

