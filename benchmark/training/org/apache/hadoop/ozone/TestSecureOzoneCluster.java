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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone;


import AuthMethod.TOKEN;
import CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION;
import InterfaceAudience.Private;
import OMConfigKeys.DELEGATION_TOKEN_MAX_LIFETIME_KEY;
import OMConfigKeys.OZONE_OM_KERBEROS_PRINCIPAL_KEY;
import OzoneManager.LOG;
import ScmConfigKeys.HDDS_SCM_KERBEROS_KEYTAB_FILE_KEY;
import ScmConfigKeys.HDDS_SCM_KERBEROS_PRINCIPAL_KEY;
import Server.AUDITLOG;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.PrivilegedExceptionAction;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.util.concurrent.Callable;
import junit.framework.TestCase;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.SCMSecurityProtocol;
import org.apache.hadoop.hdds.scm.ScmInfo;
import org.apache.hadoop.hdds.scm.client.HddsClientUtils;
import org.apache.hadoop.hdds.scm.server.StorageContainerManager;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.ipc.Client;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.minikdc.MiniKdc;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.ozone.client.CertificateClientTestImpl;
import org.apache.hadoop.ozone.om.OMStorage;
import org.apache.hadoop.ozone.om.OzoneManager;
import org.apache.hadoop.ozone.om.exceptions.OMException;
import org.apache.hadoop.ozone.om.protocolPB.OzoneManagerProtocolClientSideTranslatorPB;
import org.apache.hadoop.ozone.om.protocolPB.OzoneManagerProtocolPB;
import org.apache.hadoop.ozone.security.OzoneTokenIdentifier;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.GenericTestUtils.LogCapturer;
import org.apache.hadoop.test.LambdaTestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


/**
 * Test class to for security enabled Ozone cluster.
 */
@InterfaceAudience.Private
public final class TestSecureOzoneCluster {
    private static final String TEST_USER = "testUgiUser@EXAMPLE.COM";

    private static final int CLIENT_TIMEOUT = 2 * 1000;

    private Logger logger = LoggerFactory.getLogger(TestSecureOzoneCluster.class);

    @Rule
    public Timeout timeout = new Timeout(80000);

    private MiniKdc miniKdc;

    private OzoneConfiguration conf;

    private File workDir;

    private static Properties securityProperties;

    private File scmKeytab;

    private File spnegoKeytab;

    private File omKeyTab;

    private File testUserKeytab;

    private String curUser;

    private String testUserPrincipal;

    private UserGroupInformation testKerberosUgi;

    private StorageContainerManager scm;

    private OzoneManager om;

    private String host;

    private static String clusterId;

    private static String scmId;

    private static String omId;

    private OzoneManagerProtocolClientSideTranslatorPB omClient;

    private KeyPair keyPair;

    private Path metaDirPath;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testSecureScmStartupSuccess() throws Exception {
        initSCM();
        scm = StorageContainerManager.createSCM(null, conf);
        // Reads the SCM Info from SCM instance
        ScmInfo scmInfo = scm.getClientProtocolServer().getScmInfo();
        Assert.assertEquals(TestSecureOzoneCluster.clusterId, scmInfo.getClusterId());
        Assert.assertEquals(TestSecureOzoneCluster.scmId, scmInfo.getScmId());
    }

    @Test
    public void testSCMSecurityProtocol() throws Exception {
        initSCM();
        scm = StorageContainerManager.createSCM(null, conf);
        // Reads the SCM Info from SCM instance
        try {
            scm.start();
            // Case 1: User with Kerberos credentials should succeed.
            UserGroupInformation ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(testUserPrincipal, testUserKeytab.getCanonicalPath());
            ugi.setAuthenticationMethod(AuthenticationMethod.KERBEROS);
            SCMSecurityProtocol scmSecurityProtocolClient = HddsClientUtils.getScmSecurityClient(conf, ugi);
            TestCase.assertNotNull(scmSecurityProtocolClient);
            String caCert = scmSecurityProtocolClient.getCACertificate();
            LambdaTestUtils.intercept(RemoteException.class, "Certificate not found", () -> scmSecurityProtocolClient.getCertificate("1"));
            TestCase.assertNotNull(caCert);
            // Case 2: User without Kerberos credentials should fail.
            ugi = UserGroupInformation.createRemoteUser("test");
            ugi.setAuthenticationMethod(TOKEN);
            SCMSecurityProtocol finalScmSecurityProtocolClient = HddsClientUtils.getScmSecurityClient(conf, ugi);
            LambdaTestUtils.intercept(IOException.class, ("Client cannot" + " authenticate via:[KERBEROS]"), () -> finalScmSecurityProtocolClient.getCACertificate());
            LambdaTestUtils.intercept(IOException.class, ("Client cannot" + " authenticate via:[KERBEROS]"), () -> finalScmSecurityProtocolClient.getCertificate("1"));
        } finally {
            if ((scm) != null) {
                scm.stop();
            }
        }
    }

    @Test
    public void testSecureScmStartupFailure() throws Exception {
        initSCM();
        conf.set(HDDS_SCM_KERBEROS_KEYTAB_FILE_KEY, "");
        conf.set(HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        LambdaTestUtils.intercept(IOException.class, "Running in secure mode, but config doesn't have a keytab", () -> {
            StorageContainerManager.createSCM(null, conf);
        });
        conf.set(HDDS_SCM_KERBEROS_PRINCIPAL_KEY, "scm/_HOST@EXAMPLE.com");
        conf.set(HDDS_SCM_KERBEROS_KEYTAB_FILE_KEY, "/etc/security/keytabs/scm.keytab");
        testCommonKerberosFailures(() -> StorageContainerManager.createSCM(null, conf));
    }

    /**
     * Tests the secure om Initialization Failure.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testSecureOMInitializationFailure() throws Exception {
        initSCM();
        // Create a secure SCM instance as om client will connect to it
        scm = StorageContainerManager.createSCM(null, conf);
        setupOm(conf);
        conf.set(OZONE_OM_KERBEROS_PRINCIPAL_KEY, "non-existent-user@EXAMPLE.com");
        testCommonKerberosFailures(() -> OzoneManager.createOm(null, conf));
    }

    /**
     * Tests the secure om Initialization success.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testSecureOmInitializationSuccess() throws Exception {
        initSCM();
        // Create a secure SCM instance as om client will connect to it
        scm = StorageContainerManager.createSCM(null, conf);
        LogCapturer logs = LogCapturer.captureLogs(LOG);
        GenericTestUtils.setLogLevel(LOG, Level.INFO);
        setupOm(conf);
        try {
            om.start();
        } catch (Exception ex) {
            // Expects timeout failure from scmClient in om but om user login via
            // kerberos should succeed.
            Assert.assertTrue(logs.getOutput().contains(("Ozone Manager login" + " successful")));
        }
    }

    /**
     * Performs following tests for delegation token.
     * 1. Get valid delegation token
     * 2. Test successful token renewal.
     * 3. Client can authenticate using token.
     * 4. Delegation token renewal without Kerberos auth fails.
     * 5. Test success of token cancellation.
     * 5. Test failure of token cancellation.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDelegationToken() throws Exception {
        // Capture logs for assertions
        LogCapturer logs = LogCapturer.captureLogs(AUDITLOG);
        LogCapturer omLogs = LogCapturer.captureLogs(OzoneManager.getLogger());
        GenericTestUtils.setLogLevel(LoggerFactory.getLogger(Server.class.getName()), Level.INFO);
        // Setup secure OM for start
        setupOm(conf);
        long omVersion = RPC.getProtocolVersion(OzoneManagerProtocolPB.class);
        try {
            // Start OM
            om.setCertClient(new CertificateClientTestImpl(conf));
            om.start();
            UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
            String username = ugi.getUserName();
            // Get first OM client which will authenticate via Kerberos
            omClient = new OzoneManagerProtocolClientSideTranslatorPB(RPC.getProxy(OzoneManagerProtocolPB.class, omVersion, OmUtils.getOmAddress(conf), ugi, conf, NetUtils.getDefaultSocketFactory(conf), TestSecureOzoneCluster.CLIENT_TIMEOUT), RandomStringUtils.randomAscii(5));
            // Assert if auth was successful via Kerberos
            Assert.assertFalse(logs.getOutput().contains((("Auth successful for " + username) + " (auth:KERBEROS)")));
            // Case 1: Test successful delegation token.
            Token<OzoneTokenIdentifier> token = omClient.getDelegationToken(new Text("om"));
            // Case 2: Test successful token renewal.
            long renewalTime = omClient.renewDelegationToken(token);
            Assert.assertTrue((renewalTime > 0));
            // Check if token is of right kind and renewer is running om instance
            Assert.assertEquals(token.getKind().toString(), "OzoneToken");
            Assert.assertEquals(token.getService().toString(), OmUtils.getOmRpcAddress(conf));
            omClient.close();
            // Create a remote ugi and set its authentication method to Token
            UserGroupInformation testUser = UserGroupInformation.createRemoteUser(TestSecureOzoneCluster.TEST_USER);
            testUser.addToken(token);
            testUser.setAuthenticationMethod(TOKEN);
            UserGroupInformation.setLoginUser(testUser);
            // Get Om client, this time authentication should happen via Token
            testUser.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    omClient = new OzoneManagerProtocolClientSideTranslatorPB(RPC.getProxy(OzoneManagerProtocolPB.class, omVersion, OmUtils.getOmAddress(conf), testUser, conf, NetUtils.getDefaultSocketFactory(conf), TestSecureOzoneCluster.CLIENT_TIMEOUT), RandomStringUtils.randomAscii(5));
                    return null;
                }
            });
            // Case 3: Test Client can authenticate using token.
            Assert.assertFalse(logs.getOutput().contains((("Auth successful for " + username) + " (auth:TOKEN)")));
            OzoneTestUtils.expectOmException(VOLUME_NOT_FOUND, () -> omClient.deleteVolume("vol1"));
            Assert.assertTrue(logs.getOutput().contains((("Auth successful for " + username) + " (auth:TOKEN)")));
            // Case 4: Test failure of token renewal.
            // Call to renewDelegationToken will fail but it will confirm that
            // initial connection via DT succeeded
            omLogs.clearOutput();
            LambdaTestUtils.intercept(OMException.class, "INVALID_AUTH_METHOD", () -> {
                try {
                    omClient.renewDelegationToken(token);
                } catch ( ex) {
                    Assert.assertTrue(ex.getResult().equals(INVALID_AUTH_METHOD));
                    throw ex;
                }
            });
            Assert.assertTrue(logs.getOutput().contains((("Auth successful for " + username) + " (auth:TOKEN)")));
            omLogs.clearOutput();
            // testUser.setAuthenticationMethod(AuthMethod.KERBEROS);
            UserGroupInformation.setLoginUser(ugi);
            omClient = new OzoneManagerProtocolClientSideTranslatorPB(RPC.getProxy(OzoneManagerProtocolPB.class, omVersion, OmUtils.getOmAddress(conf), ugi, conf, NetUtils.getDefaultSocketFactory(conf), Client.getRpcTimeout(conf)), RandomStringUtils.randomAscii(5));
            // Case 5: Test success of token cancellation.
            omClient.cancelDelegationToken(token);
            omClient.close();
            // Wait for client to timeout
            Thread.sleep(TestSecureOzoneCluster.CLIENT_TIMEOUT);
            Assert.assertFalse(logs.getOutput().contains("Auth failed for"));
            // Case 6: Test failure of token cancellation.
            // Get Om client, this time authentication using Token will fail as
            // token is not in cache anymore.
            omClient = new OzoneManagerProtocolClientSideTranslatorPB(RPC.getProxy(OzoneManagerProtocolPB.class, omVersion, OmUtils.getOmAddress(conf), testUser, conf, NetUtils.getDefaultSocketFactory(conf), Client.getRpcTimeout(conf)), RandomStringUtils.randomAscii(5));
            LambdaTestUtils.intercept(OMException.class, ("Cancel delegation " + "token failed"), () -> {
                try {
                    omClient.cancelDelegationToken(token);
                } catch ( ex) {
                    Assert.assertTrue(ex.getResult().equals(TOKEN_ERROR_OTHER));
                    throw ex;
                }
            });
            Assert.assertTrue(logs.getOutput().contains("Auth failed for"));
        } finally {
            om.stop();
            om.join();
        }
    }

    /**
     * Tests delegation token renewal.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDelegationTokenRenewal() throws Exception {
        GenericTestUtils.setLogLevel(LoggerFactory.getLogger(Server.class.getName()), Level.INFO);
        LogCapturer omLogs = LogCapturer.captureLogs(OzoneManager.getLogger());
        // Setup secure OM for start.
        OzoneConfiguration newConf = new OzoneConfiguration(conf);
        newConf.setLong(DELEGATION_TOKEN_MAX_LIFETIME_KEY, 500);
        setupOm(newConf);
        long omVersion = RPC.getProtocolVersion(OzoneManagerProtocolPB.class);
        OzoneManager.setTestSecureOmFlag(true);
        // Start OM
        try {
            om.setCertClient(new CertificateClientTestImpl(conf));
            om.start();
            UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
            // Get first OM client which will authenticate via Kerberos
            omClient = new OzoneManagerProtocolClientSideTranslatorPB(RPC.getProxy(OzoneManagerProtocolPB.class, omVersion, OmUtils.getOmAddress(conf), ugi, conf, NetUtils.getDefaultSocketFactory(conf), TestSecureOzoneCluster.CLIENT_TIMEOUT), RandomStringUtils.randomAscii(5));
            // Since client is already connected get a delegation token
            Token<OzoneTokenIdentifier> token = omClient.getDelegationToken(new Text("om"));
            // Check if token is of right kind and renewer is running om instance
            Assert.assertEquals(token.getKind().toString(), "OzoneToken");
            Assert.assertEquals(token.getService().toString(), OmUtils.getOmRpcAddress(conf));
            // Renew delegation token
            long expiryTime = omClient.renewDelegationToken(token);
            Assert.assertTrue((expiryTime > 0));
            omLogs.clearOutput();
            // Test failure of delegation renewal
            // 1. When token maxExpiryTime exceeds
            Thread.sleep(500);
            LambdaTestUtils.intercept(OMException.class, "TOKEN_EXPIRED", () -> {
                try {
                    omClient.renewDelegationToken(token);
                } catch ( ex) {
                    Assert.assertTrue(ex.getResult().equals(TOKEN_EXPIRED));
                    throw ex;
                }
            });
            omLogs.clearOutput();
            // 2. When renewer doesn't match (implicitly covers when renewer is
            // null or empty )
            Token token2 = omClient.getDelegationToken(new Text("randomService"));
            LambdaTestUtils.intercept(OMException.class, "Delegation token renewal failed", () -> omClient.renewDelegationToken(token2));
            Assert.assertTrue(omLogs.getOutput().contains((" with non-matching " + "renewer randomService")));
            omLogs.clearOutput();
            // 3. Test tampered token
            OzoneTokenIdentifier tokenId = OzoneTokenIdentifier.readProtoBuf(token.getIdentifier());
            tokenId.setRenewer(new Text("om"));
            tokenId.setMaxDate(((System.currentTimeMillis()) * 2));
            Token<OzoneTokenIdentifier> tamperedToken = new Token(tokenId.getBytes(), token2.getPassword(), token2.getKind(), token2.getService());
            LambdaTestUtils.intercept(OMException.class, "Delegation token renewal failed", () -> omClient.renewDelegationToken(tamperedToken));
            Assert.assertTrue(omLogs.getOutput().contains(("can't be found in " + "cache")));
            omLogs.clearOutput();
        } finally {
            om.stop();
            om.join();
        }
    }

    /**
     * Test functionality to get SCM signed certificate for OM.
     */
    @Test
    public void testSecureOmInitSuccess() throws Exception {
        LogCapturer omLogs = LogCapturer.captureLogs(OzoneManager.getLogger());
        omLogs.clearOutput();
        initSCM();
        try {
            scm = StorageContainerManager.createSCM(null, conf);
            scm.start();
            OMStorage omStore = new OMStorage(conf);
            initializeOmStorage(omStore);
            OzoneManager.setTestSecureOmFlag(true);
            om = OzoneManager.createOm(null, conf);
            Assert.assertNotNull(om.getCertificateClient());
            Assert.assertNotNull(om.getCertificateClient().getPublicKey());
            Assert.assertNotNull(om.getCertificateClient().getPrivateKey());
            Assert.assertNotNull(om.getCertificateClient().getCertificate());
            Assert.assertTrue(omLogs.getOutput().contains("Init response: GETCERT"));
            Assert.assertTrue(omLogs.getOutput().contains(("Successfully stored " + "SCM signed certificate")));
            X509Certificate certificate = om.getCertificateClient().getCertificate();
            validateCertificate(certificate);
        } finally {
            if ((scm) != null) {
                scm.stop();
            }
            if ((om) != null) {
                om.stop();
            }
        }
    }
}

