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
package org.apache.hadoop.mapreduce.security;


import AuthenticationMethod.KERBEROS;
import CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION;
import JHAdminConfig.MR_HISTORY_PRINCIPAL;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.v2.api.MRClientProtocol;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetJobReportRequest;
import org.apache.hadoop.mapreduce.v2.hs.HistoryClientService;
import org.apache.hadoop.mapreduce.v2.hs.HistoryServerStateStoreService;
import org.apache.hadoop.mapreduce.v2.hs.JHSDelegationTokenSecretManager;
import org.apache.hadoop.mapreduce.v2.hs.JobHistoryServer;
import org.apache.hadoop.mapreduce.v2.util.MRBuilderUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.Records;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestJHSSecurity {
    private static final Logger LOG = LoggerFactory.getLogger(TestJHSSecurity.class);

    @Test
    public void testDelegationToken() throws IOException, InterruptedException {
        org.apache.log4j.Logger rootLogger = LogManager.getRootLogger();
        rootLogger.setLevel(Level.DEBUG);
        final YarnConfiguration conf = new YarnConfiguration(new JobConf());
        // Just a random principle
        conf.set(MR_HISTORY_PRINCIPAL, "RandomOrc/localhost@apache.org");
        conf.set(HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        UserGroupInformation.setConfiguration(conf);
        final long initialInterval = 10000L;
        final long maxLifetime = 20000L;
        final long renewInterval = 10000L;
        JobHistoryServer jobHistoryServer = null;
        MRClientProtocol clientUsingDT = null;
        long tokenFetchTime;
        try {
            jobHistoryServer = new JobHistoryServer() {
                protected void doSecureLogin(Configuration conf) throws IOException {
                    // no keytab based login
                }

                @Override
                protected JHSDelegationTokenSecretManager createJHSSecretManager(Configuration conf, HistoryServerStateStoreService store) {
                    return new JHSDelegationTokenSecretManager(initialInterval, maxLifetime, renewInterval, 3600000, store);
                }

                @Override
                protected HistoryClientService createHistoryClientService() {
                    return new HistoryClientService(historyContext, this.jhsDTSecretManager) {
                        @Override
                        protected void initializeWebApp(Configuration conf) {
                            // Don't need it, skip.;
                        }
                    };
                }
            };
            // final JobHistoryServer jobHistoryServer = jhServer;
            jobHistoryServer.init(conf);
            jobHistoryServer.start();
            final MRClientProtocol hsService = jobHistoryServer.getClientService().getClientHandler();
            // Fake the authentication-method
            UserGroupInformation loggedInUser = UserGroupInformation.createRemoteUser("testrenewer@APACHE.ORG");
            Assert.assertEquals("testrenewer", loggedInUser.getShortUserName());
            // Default realm is APACHE.ORG
            loggedInUser.setAuthenticationMethod(KERBEROS);
            Token token = getDelegationToken(loggedInUser, hsService, loggedInUser.getShortUserName());
            tokenFetchTime = System.currentTimeMillis();
            TestJHSSecurity.LOG.info(("Got delegation token at: " + tokenFetchTime));
            // Now try talking to JHS using the delegation token
            clientUsingDT = getMRClientProtocol(token, jobHistoryServer.getClientService().getBindAddress(), "TheDarkLord", conf);
            GetJobReportRequest jobReportRequest = Records.newRecord(GetJobReportRequest.class);
            jobReportRequest.setJobId(MRBuilderUtils.newJobId(123456, 1, 1));
            try {
                clientUsingDT.getJobReport(jobReportRequest);
            } catch (IOException e) {
                Assert.assertEquals("Unknown job job_123456_0001", e.getMessage());
            }
            // Renew after 50% of token age.
            while ((System.currentTimeMillis()) < (tokenFetchTime + (initialInterval / 2))) {
                Thread.sleep(500L);
            } 
            long nextExpTime = renewDelegationToken(loggedInUser, hsService, token);
            long renewalTime = System.currentTimeMillis();
            TestJHSSecurity.LOG.info(((("Renewed token at: " + renewalTime) + ", NextExpiryTime: ") + nextExpTime));
            // Wait for first expiry, but before renewed expiry.
            while (((System.currentTimeMillis()) > (tokenFetchTime + initialInterval)) && ((System.currentTimeMillis()) < nextExpTime)) {
                Thread.sleep(500L);
            } 
            Thread.sleep(50L);
            // Valid token because of renewal.
            try {
                clientUsingDT.getJobReport(jobReportRequest);
            } catch (IOException e) {
                Assert.assertEquals("Unknown job job_123456_0001", e.getMessage());
            }
            // Wait for expiry.
            while ((System.currentTimeMillis()) < (renewalTime + renewInterval)) {
                Thread.sleep(500L);
            } 
            Thread.sleep(50L);
            TestJHSSecurity.LOG.info((("At time: " + (System.currentTimeMillis())) + ", token should be invalid"));
            // Token should have expired.
            try {
                clientUsingDT.getJobReport(jobReportRequest);
                Assert.fail("Should not have succeeded with an expired token");
            } catch (IOException e) {
                Assert.assertTrue(e.getCause().getMessage().contains("is expired"));
            }
            // Test cancellation
            // Stop the existing proxy, start another.
            if (clientUsingDT != null) {
                // RPC.stopProxy(clientUsingDT);
                clientUsingDT = null;
            }
            token = getDelegationToken(loggedInUser, hsService, loggedInUser.getShortUserName());
            tokenFetchTime = System.currentTimeMillis();
            TestJHSSecurity.LOG.info(("Got delegation token at: " + tokenFetchTime));
            // Now try talking to HSService using the delegation token
            clientUsingDT = getMRClientProtocol(token, jobHistoryServer.getClientService().getBindAddress(), "loginuser2", conf);
            try {
                clientUsingDT.getJobReport(jobReportRequest);
            } catch (IOException e) {
                Assert.fail(("Unexpected exception" + e));
            }
            cancelDelegationToken(loggedInUser, hsService, token);
            // Testing the token with different renewer to cancel the token
            Token tokenWithDifferentRenewer = getDelegationToken(loggedInUser, hsService, "yarn");
            cancelDelegationToken(loggedInUser, hsService, tokenWithDifferentRenewer);
            if (clientUsingDT != null) {
                // RPC.stopProxy(clientUsingDT);
                clientUsingDT = null;
            }
            // Creating a new connection.
            clientUsingDT = getMRClientProtocol(token, jobHistoryServer.getClientService().getBindAddress(), "loginuser2", conf);
            TestJHSSecurity.LOG.info(("Cancelled delegation token at: " + (System.currentTimeMillis())));
            // Verify cancellation worked.
            try {
                clientUsingDT.getJobReport(jobReportRequest);
                Assert.fail("Should not have succeeded with a cancelled delegation token");
            } catch (IOException e) {
            }
        } finally {
            jobHistoryServer.stop();
        }
    }
}

