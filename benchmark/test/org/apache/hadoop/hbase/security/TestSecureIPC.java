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
package org.apache.hadoop.hbase.security;


import AuthenticationMethod.KERBEROS;
import AuthenticationMethod.SIMPLE;
import RpcServer.FALLBACK_TO_INSECURE_CLIENT_AUTH;
import TestProtos.EchoRequestProto;
import User.HBASE_SECURITY_CONF_KEY;
import java.io.File;
import javax.security.sasl.SaslException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.shaded.ipc.protobuf.generated.TestRpcServiceProtos.TestProtobufRpcProto.BlockingInterface;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.minikdc.MiniKdc;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hbase.thirdparty.com.google.protobuf.ServiceException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Category({ SecurityTests.class, MediumTests.class })
public class TestSecureIPC {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSecureIPC.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final File KEYTAB_FILE = new File(getDataTestDir("keytab").toUri().getPath());

    private static MiniKdc KDC;

    private static String HOST = "localhost";

    private static String PRINCIPAL;

    String krbKeytab;

    String krbPrincipal;

    UserGroupInformation ugi;

    Configuration clientConf;

    Configuration serverConf;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Parameterized.Parameter(0)
    public String rpcClientImpl;

    @Parameterized.Parameter(1)
    public String rpcServerImpl;

    @Test
    public void testRpcCallWithEnabledKerberosSaslAuth() throws Exception {
        UserGroupInformation ugi2 = UserGroupInformation.getCurrentUser();
        // check that the login user is okay:
        Assert.assertSame(ugi2, ugi);
        Assert.assertEquals(KERBEROS, ugi.getAuthenticationMethod());
        Assert.assertEquals(krbPrincipal, ugi.getUserName());
        callRpcService(User.create(ugi2));
    }

    @Test
    public void testRpcFallbackToSimpleAuth() throws Exception {
        String clientUsername = "testuser";
        UserGroupInformation clientUgi = UserGroupInformation.createUserForTesting(clientUsername, new String[]{ clientUsername });
        // check that the client user is insecure
        Assert.assertNotSame(ugi, clientUgi);
        Assert.assertEquals(SIMPLE, clientUgi.getAuthenticationMethod());
        Assert.assertEquals(clientUsername, clientUgi.getUserName());
        clientConf.set(HBASE_SECURITY_CONF_KEY, "simple");
        serverConf.setBoolean(FALLBACK_TO_INSECURE_CLIENT_AUTH, true);
        callRpcService(User.create(clientUgi));
    }

    /**
     * Test various combinations of Server and Client qops.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSaslWithCommonQop() throws Exception {
        setRpcProtection("privacy,authentication", "authentication");
        callRpcService(User.create(ugi));
        setRpcProtection("authentication", "privacy,authentication");
        callRpcService(User.create(ugi));
        setRpcProtection("integrity,authentication", "privacy,authentication");
        callRpcService(User.create(ugi));
        setRpcProtection("integrity,authentication", "integrity,authentication");
        callRpcService(User.create(ugi));
        setRpcProtection("privacy,authentication", "privacy,authentication");
        callRpcService(User.create(ugi));
    }

    @Test
    public void testSaslNoCommonQop() throws Exception {
        exception.expect(SaslException.class);
        exception.expectMessage("No common protection layer between client and server");
        setRpcProtection("integrity", "privacy");
        callRpcService(User.create(ugi));
    }

    /**
     * Test sasl encryption with Crypto AES.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSaslWithCryptoAES() throws Exception {
        setRpcProtection("privacy", "privacy");
        setCryptoAES("true", "true");
        callRpcService(User.create(ugi));
    }

    /**
     * Test various combinations of Server and Client configuration for Crypto AES.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDifferentConfWithCryptoAES() throws Exception {
        setRpcProtection("privacy", "privacy");
        setCryptoAES("false", "true");
        callRpcService(User.create(ugi));
        setCryptoAES("true", "false");
        try {
            callRpcService(User.create(ugi));
            Assert.fail("The exception should be thrown out for the rpc timeout.");
        } catch (Exception e) {
            // ignore the expected exception
        }
    }

    public static class TestThread extends Thread {
        private final BlockingInterface stub;

        public TestThread(BlockingInterface stub) {
            this.stub = stub;
        }

        @Override
        public void run() {
            try {
                int[] messageSize = new int[]{ 100, 1000, 10000 };
                for (int i = 0; i < (messageSize.length); i++) {
                    String input = RandomStringUtils.random(messageSize[i]);
                    String result = stub.echo(null, EchoRequestProto.newBuilder().setMessage(input).build()).getMessage();
                    Assert.assertEquals(input, result);
                }
            } catch (org.apache.hbase e) {
                throw new RuntimeException(e);
            }
        }
    }
}

