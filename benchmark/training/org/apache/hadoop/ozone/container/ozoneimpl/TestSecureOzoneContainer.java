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
import HddsConfigKeys.HDDS_BLOCK_TOKEN_ENABLED;
import HddsProtos.BlockTokenSecretProto.AccessModeProto;
import OzoneConfigKeys.DFS_CONTAINER_IPC_PORT;
import OzoneConfigKeys.DFS_CONTAINER_IPC_PORT_DEFAULT;
import OzoneConfigKeys.DFS_CONTAINER_IPC_RANDOM_PORT;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.PrivilegedAction;
import java.util.EnumSet;
import java.util.UUID;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.scm.ScmConfigKeys;
import org.apache.hadoop.hdds.scm.TestUtils;
import org.apache.hadoop.hdds.scm.XceiverClientGrpc;
import org.apache.hadoop.hdds.scm.pipeline.Pipeline;
import org.apache.hadoop.hdds.security.exception.SCMSecurityException;
import org.apache.hadoop.hdds.security.token.OzoneBlockTokenIdentifier;
import org.apache.hadoop.hdds.security.x509.SecurityConfig;
import org.apache.hadoop.ozone.container.ContainerTestHelper;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
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
public class TestSecureOzoneContainer {
    private static final Logger LOG = LoggerFactory.getLogger(TestSecureOzoneContainer.class);

    /**
     * Set the timeout for every test.
     */
    @Rule
    public Timeout testTimeout = new Timeout(300000);

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private OzoneConfiguration conf;

    private SecurityConfig secConfig;

    private Boolean requireBlockToken;

    private Boolean hasBlockToken;

    private Boolean blockTokeExpired;

    public TestSecureOzoneContainer(Boolean requireBlockToken, Boolean hasBlockToken, Boolean blockTokenExpired) {
        this.requireBlockToken = requireBlockToken;
        this.hasBlockToken = hasBlockToken;
        this.blockTokeExpired = blockTokenExpired;
    }

    @Test
    public void testCreateOzoneContainer() throws Exception {
        TestSecureOzoneContainer.LOG.info(("Test case: requireBlockToken: {} hasBlockToken: {} " + "blockTokenExpired: {}."), requireBlockToken, hasBlockToken, blockTokeExpired);
        conf.setBoolean(HDDS_BLOCK_TOKEN_ENABLED, requireBlockToken);
        long containerID = ContainerTestHelper.getTestContainerID();
        OzoneContainer container = null;
        System.out.println(System.getProperties().getProperty("java.library.path"));
        try {
            Pipeline pipeline = ContainerTestHelper.createSingleNodePipeline();
            conf.set(ScmConfigKeys.HDDS_DATANODE_DIR_KEY, tempFolder.getRoot().getPath());
            conf.setInt(DFS_CONTAINER_IPC_PORT, pipeline.getFirstNode().getPort(STANDALONE).getValue());
            conf.setBoolean(DFS_CONTAINER_IPC_RANDOM_PORT, false);
            DatanodeDetails dn = TestUtils.randomDatanodeDetails();
            container = new OzoneContainer(dn, conf, getContext(dn));
            // Setting scmId, as we start manually ozone container.
            container.getDispatcher().setScmId(UUID.randomUUID().toString());
            container.start();
            UserGroupInformation ugi = UserGroupInformation.createUserForTesting("user1", new String[]{ "usergroup" });
            long expiryDate = (blockTokeExpired) ? (Time.now()) - ((60 * 60) * 2) : (Time.now()) + ((60 * 60) * 24);
            OzoneBlockTokenIdentifier tokenId = new OzoneBlockTokenIdentifier("testUser", "cid:lud:bcsid", EnumSet.allOf(AccessModeProto.class), expiryDate, "1234", 128L);
            int port = dn.getPort(STANDALONE).getValue();
            if (port == 0) {
                port = secConfig.getConfiguration().getInt(DFS_CONTAINER_IPC_PORT, DFS_CONTAINER_IPC_PORT_DEFAULT);
            }
            InetSocketAddress addr = new InetSocketAddress(dn.getIpAddress(), port);
            Token<OzoneBlockTokenIdentifier> token = new Token(tokenId.getBytes(), new byte[50], tokenId.getKind(), SecurityUtil.buildTokenService(addr));
            if (hasBlockToken) {
                ugi.addToken(token);
            }
            ugi.doAs(new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    try {
                        XceiverClientGrpc client = new XceiverClientGrpc(pipeline, conf);
                        client.connect(token.encodeToUrlString());
                        if (hasBlockToken) {
                            TestSecureOzoneContainer.createContainerForTesting(client, containerID, token);
                        } else {
                            TestSecureOzoneContainer.createContainerForTesting(client, containerID, null);
                        }
                    } catch (Exception e) {
                        if (((requireBlockToken) && (hasBlockToken)) && (!(blockTokeExpired))) {
                            TestSecureOzoneContainer.LOG.error("Unexpected error. ", e);
                            Assert.fail(("Client with BlockToken should succeed when block token is" + " required."));
                        }
                        if (((requireBlockToken) && (hasBlockToken)) && (blockTokeExpired)) {
                            Assert.assertTrue("Receive expected exception", (e instanceof SCMSecurityException));
                        }
                        if ((requireBlockToken) && (!(hasBlockToken))) {
                            Assert.assertTrue("Receive expected exception", (e instanceof IOException));
                        }
                    }
                    return null;
                }
            });
        } finally {
            if (container != null) {
                container.stop();
            }
        }
    }
}

