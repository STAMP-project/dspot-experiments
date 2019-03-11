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
package org.apache.hadoop.yarn.server.timelineservice.security;


import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Callable;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.minikdc.MiniKdc;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authentication.KerberosTestUtils;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.security.client.TimelineDelegationTokenIdentifier;
import org.apache.hadoop.yarn.server.api.CollectorNodemanagerProtocol;
import org.apache.hadoop.yarn.server.api.protocolrecords.GetTimelineCollectorContextRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.GetTimelineCollectorContextResponse;
import org.apache.hadoop.yarn.server.timelineservice.collector.AppLevelTimelineCollector;
import org.apache.hadoop.yarn.server.timelineservice.collector.NodeTimelineCollectorManager;
import org.apache.hadoop.yarn.server.timelineservice.collector.PerNodeTimelineCollectorsAuxService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests timeline authentication filter based security for timeline service v2.
 */
@RunWith(Parameterized.class)
public class TestTimelineAuthFilterForV2 {
    private static final String FOO_USER = "foo";

    private static final String HTTP_USER = "HTTP";

    private static final File TEST_ROOT_DIR = new File(System.getProperty("test.build.dir", (("target" + (File.separator)) + "test-dir")), UUID.randomUUID().toString());

    private static final String BASEDIR = ((System.getProperty("test.build.dir", "target/test-dir")) + "/") + (TestTimelineAuthFilterForV2.class.getSimpleName());

    private static File httpSpnegoKeytabFile = new File(KerberosTestUtils.getKeytabFile());

    private static String httpSpnegoPrincipal = KerberosTestUtils.getServerPrincipal();

    private static MiniKdc testMiniKDC;

    private static String keystoresDir;

    private static String sslConfDir;

    private static Configuration conf;

    private static UserGroupInformation nonKerberosUser;

    static {
        try {
            TestTimelineAuthFilterForV2.nonKerberosUser = UserGroupInformation.getCurrentUser();
        } catch (IOException e) {
        }
    }

    // Indicates whether HTTPS or HTTP access.
    private boolean withSsl;

    // Indicates whether Kerberos based login is used or token based access is
    // done.
    private boolean withKerberosLogin;

    private NodeTimelineCollectorManager collectorManager;

    private PerNodeTimelineCollectorsAuxService auxService;

    public TestTimelineAuthFilterForV2(boolean withSsl, boolean withKerberosLogin) {
        this.withSsl = withSsl;
        this.withKerberosLogin = withKerberosLogin;
    }

    @Test
    public void testPutTimelineEntities() throws Exception {
        final String entityType = "dummy_type";
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        File entityTypeDir = new File((((((((((((((((((TestTimelineAuthFilterForV2.TEST_ROOT_DIR.getAbsolutePath()) + (File.separator)) + "entities") + (File.separator)) + (YarnConfiguration.DEFAULT_RM_CLUSTER_ID)) + (File.separator)) + (UserGroupInformation.getCurrentUser().getUserName())) + (File.separator)) + "test_flow_name") + (File.separator)) + "test_flow_version") + (File.separator)) + "1") + (File.separator)) + (appId.toString())) + (File.separator)) + entityType));
        try {
            if (withKerberosLogin) {
                KerberosTestUtils.doAs(((TestTimelineAuthFilterForV2.HTTP_USER) + "/localhost"), new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        publishAndVerifyEntity(appId, entityTypeDir, entityType, 1);
                        return null;
                    }
                });
            } else {
                Assert.assertTrue("Entities should have been published successfully.", publishWithRetries(appId, entityTypeDir, entityType, 1));
                AppLevelTimelineCollector collector = ((AppLevelTimelineCollector) (collectorManager.get(appId)));
                Token<TimelineDelegationTokenIdentifier> token = collector.getDelegationTokenForApp();
                Assert.assertNotNull(token);
                // Verify if token is renewed automatically and entities can still be
                // published.
                Thread.sleep(1000);
                // Entities should publish successfully after renewal.
                Assert.assertTrue("Entities should have been published successfully.", publishWithRetries(appId, entityTypeDir, entityType, 2));
                Assert.assertNotNull(collector);
                Mockito.verify(collectorManager.getTokenManagerService(), Mockito.atLeastOnce()).renewToken(ArgumentMatchers.eq(collector.getDelegationTokenForApp()), ArgumentMatchers.any(String.class));
                // Wait to ensure lifetime of token expires and ensure its regenerated
                // automatically.
                Thread.sleep(3000);
                for (int i = 0; i < 40; i++) {
                    if (!(token.equals(collector.getDelegationTokenForApp()))) {
                        break;
                    }
                    Thread.sleep(50);
                }
                Assert.assertNotEquals("Token should have been regenerated.", token, collector.getDelegationTokenForApp());
                Thread.sleep(1000);
                // Try publishing with the old token in UGI. Publishing should fail due
                // to invalid token.
                try {
                    publishAndVerifyEntity(appId, entityTypeDir, entityType, 2);
                    Assert.fail("Exception should have been thrown due to Invalid Token.");
                } catch (YarnException e) {
                    Assert.assertTrue("Exception thrown should have been due to Invalid Token.", e.getCause().getMessage().contains("InvalidToken"));
                }
                // Update the regenerated token in UGI and retry publishing entities.
                Token<TimelineDelegationTokenIdentifier> regeneratedToken = collector.getDelegationTokenForApp();
                regeneratedToken.setService(new org.apache.hadoop.io.Text(("localhost" + (regeneratedToken.getService().toString().substring(regeneratedToken.getService().toString().indexOf(":"))))));
                UserGroupInformation.getCurrentUser().addToken(regeneratedToken);
                Assert.assertTrue("Entities should have been published successfully.", publishWithRetries(appId, entityTypeDir, entityType, 2));
                // Token was generated twice, once when app collector was created and
                // later after token lifetime expiry.
                Mockito.verify(collectorManager.getTokenManagerService(), Mockito.times(2)).generateToken(ArgumentMatchers.any(UserGroupInformation.class), ArgumentMatchers.any(String.class));
                Assert.assertEquals(1, ((TestTimelineAuthFilterForV2.DummyNodeTimelineCollectorManager) (collectorManager)).getTokenExpiredCnt());
            }
            // Wait for async entity to be published.
            for (int i = 0; i < 50; i++) {
                if ((entityTypeDir.listFiles().length) == 2) {
                    break;
                }
                Thread.sleep(50);
            }
            Assert.assertEquals(2, entityTypeDir.listFiles().length);
            TestTimelineAuthFilterForV2.verifyEntity(entityTypeDir, "entity2", entityType);
            AppLevelTimelineCollector collector = ((AppLevelTimelineCollector) (collectorManager.get(appId)));
            Assert.assertNotNull(collector);
            auxService.removeApplication(appId);
            Mockito.verify(collectorManager.getTokenManagerService()).cancelToken(ArgumentMatchers.eq(collector.getDelegationTokenForApp()), ArgumentMatchers.any(String.class));
        } finally {
            FileUtils.deleteQuietly(entityTypeDir);
        }
    }

    private static class DummyNodeTimelineCollectorManager extends NodeTimelineCollectorManager {
        private volatile int tokenExpiredCnt = 0;

        DummyNodeTimelineCollectorManager() {
            super();
        }

        private int getTokenExpiredCnt() {
            return tokenExpiredCnt;
        }

        @Override
        protected TimelineV2DelegationTokenSecretManagerService createTokenManagerService() {
            return Mockito.spy(new TimelineV2DelegationTokenSecretManagerService() {
                @Override
                protected AbstractDelegationTokenSecretManager<TimelineDelegationTokenIdentifier> createTimelineDelegationTokenSecretManager(long secretKeyInterval, long tokenMaxLifetime, long tokenRenewInterval, long tokenRemovalScanInterval) {
                    return Mockito.spy(new TimelineV2DelegationTokenSecretManager(secretKeyInterval, tokenMaxLifetime, tokenRenewInterval, 2000L) {
                        @Override
                        protected void logExpireToken(TimelineDelegationTokenIdentifier ident) throws IOException {
                            (tokenExpiredCnt)++;
                        }
                    });
                }
            });
        }

        @Override
        protected CollectorNodemanagerProtocol getNMCollectorService() {
            CollectorNodemanagerProtocol protocol = Mockito.mock(CollectorNodemanagerProtocol.class);
            try {
                GetTimelineCollectorContextResponse response = GetTimelineCollectorContextResponse.newInstance(UserGroupInformation.getCurrentUser().getUserName(), "test_flow_name", "test_flow_version", 1L);
                Mockito.when(protocol.getTimelineCollectorContext(ArgumentMatchers.any(GetTimelineCollectorContextRequest.class))).thenReturn(response);
            } catch (YarnException | IOException e) {
                Assert.fail();
            }
            return protocol;
        }
    }
}

