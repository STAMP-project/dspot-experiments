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
package org.apache.hadoop.mapred;


import CommonConfigurationKeys.HADOOP_SECURITY_AUTHENTICATION;
import FinalApplicationStatus.SUCCEEDED;
import MRJobConfig.AM_NODE_LABEL_EXP;
import MRJobConfig.DEFAULT_TASK_PROFILE_PARAMS;
import MRJobConfig.JOB_NODE_LABEL_EXP;
import MRJobConfig.MR_AM_ADMIN_COMMAND_OPTS;
import MRJobConfig.MR_AM_COMMAND_OPTS;
import MRJobConfig.MR_AM_CPU_VCORES;
import MRJobConfig.MR_AM_HARD_KILL_TIMEOUT_MS;
import MRJobConfig.MR_AM_PROFILE;
import MRJobConfig.MR_AM_VMEM_MB;
import MRJobConfig.PRIORITY;
import MRJobConfig.RESOURCE_TYPE_ALTERNATIVE_NAME_MEMORY;
import MRJobConfig.RESOURCE_TYPE_NAME_MEMORY;
import RMDelegationTokenIdentifier.KIND_NAME;
import ResourceRequest.ANY;
import YarnApplicationState.FAILED;
import YarnApplicationState.FINISHED;
import YarnConfiguration.RM_PRINCIPAL;
import com.google.common.collect.ImmutableList;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.JobPriority;
import org.apache.hadoop.mapreduce.JobStatus.State;
import org.apache.hadoop.mapreduce.MRJobConfig;
import org.apache.hadoop.mapreduce.v2.api.MRClientProtocol;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetDelegationTokenRequest;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetDelegationTokenResponse;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterMetricsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterMetricsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueInfoRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueInfoResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueUserAclsInfoRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueUserAclsInfoResponse;
import org.apache.hadoop.yarn.api.protocolrecords.KillApplicationRequest;
import org.apache.hadoop.yarn.api.protocolrecords.KillApplicationResponse;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.ResourceInformation;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.api.records.YarnClusterMetrics;
import org.apache.hadoop.yarn.client.api.impl.YarnClientImpl;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.security.client.RMDelegationTokenIdentifier;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.util.Records;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.log4j.Logger.getLogger;


/**
 * Test YarnRunner and make sure the client side plugin works
 * fine
 */
public class TestYARNRunner {
    private static final Logger LOG = LoggerFactory.getLogger(TestYARNRunner.class);

    private static final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);

    // prefix before <LOG_DIR>/profile.out
    private static final String PROFILE_PARAMS = DEFAULT_TASK_PROFILE_PARAMS.substring(0, DEFAULT_TASK_PROFILE_PARAMS.lastIndexOf("%"));

    private static final String CUSTOM_RESOURCE_NAME = "a-custom-resource";

    private static class TestAppender extends AppenderSkeleton {
        private final List<LoggingEvent> logEvents = new CopyOnWriteArrayList<>();

        @Override
        public boolean requiresLayout() {
            return false;
        }

        @Override
        public void close() {
        }

        @Override
        protected void append(LoggingEvent arg0) {
            logEvents.add(arg0);
        }

        private List<LoggingEvent> getLogEvents() {
            return logEvents;
        }
    }

    private YARNRunner yarnRunner;

    private ResourceMgrDelegate resourceMgrDelegate;

    private YarnConfiguration conf;

    private ClientCache clientCache;

    private ApplicationId appId;

    private JobID jobId;

    private File testWorkDir = new File("target", TestYARNRunner.class.getName());

    private ApplicationSubmissionContext submissionContext;

    private ClientServiceDelegate clientDelegate;

    private static final String failString = "Rejected job";

    @Test(timeout = 20000)
    public void testJobKill() throws Exception {
        clientDelegate = Mockito.mock(ClientServiceDelegate.class);
        Mockito.when(clientDelegate.getJobStatus(ArgumentMatchers.any(JobID.class))).thenReturn(new org.apache.hadoop.mapreduce.JobStatus(jobId, 0.0F, 0.0F, 0.0F, 0.0F, State.PREP, JobPriority.HIGH, "tmp", "tmp", "tmp", "tmp"));
        Mockito.when(clientDelegate.killJob(ArgumentMatchers.any(JobID.class))).thenReturn(true);
        Mockito.doAnswer(new Answer<ClientServiceDelegate>() {
            @Override
            public ClientServiceDelegate answer(InvocationOnMock invocation) throws Throwable {
                return clientDelegate;
            }
        }).when(clientCache).getClient(ArgumentMatchers.any(JobID.class));
        yarnRunner.killJob(jobId);
        Mockito.verify(resourceMgrDelegate).killApplication(appId);
        Mockito.when(clientDelegate.getJobStatus(ArgumentMatchers.any(JobID.class))).thenReturn(new org.apache.hadoop.mapreduce.JobStatus(jobId, 0.0F, 0.0F, 0.0F, 0.0F, State.RUNNING, JobPriority.HIGH, "tmp", "tmp", "tmp", "tmp"));
        yarnRunner.killJob(jobId);
        Mockito.verify(clientDelegate).killJob(jobId);
        Mockito.when(clientDelegate.getJobStatus(ArgumentMatchers.any(JobID.class))).thenReturn(null);
        Mockito.when(resourceMgrDelegate.getApplicationReport(ArgumentMatchers.any(ApplicationId.class))).thenReturn(ApplicationReport.newInstance(appId, null, "tmp", "tmp", "tmp", "tmp", 0, null, FINISHED, "tmp", "tmp", 0L, 0L, 0L, SUCCEEDED, null, null, 0.0F, "tmp", null));
        yarnRunner.killJob(jobId);
        Mockito.verify(clientDelegate).killJob(jobId);
    }

    @Test(timeout = 60000)
    public void testJobKillTimeout() throws Exception {
        long timeToWaitBeforeHardKill = 10000 + (MRJobConfig.DEFAULT_MR_AM_HARD_KILL_TIMEOUT_MS);
        conf.setLong(MR_AM_HARD_KILL_TIMEOUT_MS, timeToWaitBeforeHardKill);
        clientDelegate = Mockito.mock(ClientServiceDelegate.class);
        Mockito.doAnswer(new Answer<ClientServiceDelegate>() {
            @Override
            public ClientServiceDelegate answer(InvocationOnMock invocation) throws Throwable {
                return clientDelegate;
            }
        }).when(clientCache).getClient(ArgumentMatchers.any(JobID.class));
        Mockito.when(clientDelegate.getJobStatus(ArgumentMatchers.any(JobID.class))).thenReturn(new org.apache.hadoop.mapreduce.JobStatus(jobId, 0.0F, 0.0F, 0.0F, 0.0F, State.RUNNING, JobPriority.HIGH, "tmp", "tmp", "tmp", "tmp"));
        long startTimeMillis = System.currentTimeMillis();
        yarnRunner.killJob(jobId);
        Assert.assertTrue((("killJob should have waited at least " + timeToWaitBeforeHardKill) + " ms."), (((System.currentTimeMillis()) - startTimeMillis) >= timeToWaitBeforeHardKill));
    }

    @Test(timeout = 20000)
    public void testJobSubmissionFailure() throws Exception {
        Mockito.when(resourceMgrDelegate.submitApplication(ArgumentMatchers.any(ApplicationSubmissionContext.class))).thenReturn(appId);
        ApplicationReport report = Mockito.mock(ApplicationReport.class);
        Mockito.when(report.getApplicationId()).thenReturn(appId);
        Mockito.when(report.getDiagnostics()).thenReturn(TestYARNRunner.failString);
        Mockito.when(report.getYarnApplicationState()).thenReturn(FAILED);
        Mockito.when(resourceMgrDelegate.getApplicationReport(appId)).thenReturn(report);
        Credentials credentials = new Credentials();
        File jobxml = new File(testWorkDir, "job.xml");
        OutputStream out = new FileOutputStream(jobxml);
        conf.writeXml(out);
        out.close();
        try {
            yarnRunner.submitJob(jobId, testWorkDir.getAbsolutePath().toString(), credentials);
        } catch (IOException io) {
            TestYARNRunner.LOG.info("Logging exception:", io);
            Assert.assertTrue(io.getLocalizedMessage().contains(TestYARNRunner.failString));
        }
    }

    @Test(timeout = 20000)
    public void testResourceMgrDelegate() throws Exception {
        /* we not want a mock of resource mgr delegate */
        final ApplicationClientProtocol clientRMProtocol = Mockito.mock(ApplicationClientProtocol.class);
        ResourceMgrDelegate delegate = new ResourceMgrDelegate(conf) {
            @Override
            protected void serviceStart() throws Exception {
                Assert.assertTrue(((this.client) instanceof YarnClientImpl));
                ((YarnClientImpl) (this.client)).setRMClient(clientRMProtocol);
            }
        };
        /* make sure kill calls finish application master */
        Mockito.when(clientRMProtocol.forceKillApplication(ArgumentMatchers.any(KillApplicationRequest.class))).thenReturn(KillApplicationResponse.newInstance(true));
        delegate.killApplication(appId);
        Mockito.verify(clientRMProtocol).forceKillApplication(ArgumentMatchers.any(KillApplicationRequest.class));
        /* make sure getalljobs calls get all applications */
        Mockito.when(clientRMProtocol.getApplications(ArgumentMatchers.any(GetApplicationsRequest.class))).thenReturn(TestYARNRunner.recordFactory.newRecordInstance(GetApplicationsResponse.class));
        delegate.getAllJobs();
        Mockito.verify(clientRMProtocol).getApplications(ArgumentMatchers.any(GetApplicationsRequest.class));
        /* make sure getapplication report is called */
        Mockito.when(clientRMProtocol.getApplicationReport(ArgumentMatchers.any(GetApplicationReportRequest.class))).thenReturn(TestYARNRunner.recordFactory.newRecordInstance(GetApplicationReportResponse.class));
        delegate.getApplicationReport(appId);
        Mockito.verify(clientRMProtocol).getApplicationReport(ArgumentMatchers.any(GetApplicationReportRequest.class));
        /* make sure metrics is called */
        GetClusterMetricsResponse clusterMetricsResponse = TestYARNRunner.recordFactory.newRecordInstance(GetClusterMetricsResponse.class);
        clusterMetricsResponse.setClusterMetrics(TestYARNRunner.recordFactory.newRecordInstance(YarnClusterMetrics.class));
        Mockito.when(clientRMProtocol.getClusterMetrics(ArgumentMatchers.any(GetClusterMetricsRequest.class))).thenReturn(clusterMetricsResponse);
        delegate.getClusterMetrics();
        Mockito.verify(clientRMProtocol).getClusterMetrics(ArgumentMatchers.any(GetClusterMetricsRequest.class));
        Mockito.when(clientRMProtocol.getClusterNodes(ArgumentMatchers.any(GetClusterNodesRequest.class))).thenReturn(TestYARNRunner.recordFactory.newRecordInstance(GetClusterNodesResponse.class));
        delegate.getActiveTrackers();
        Mockito.verify(clientRMProtocol).getClusterNodes(ArgumentMatchers.any(GetClusterNodesRequest.class));
        GetNewApplicationResponse newAppResponse = TestYARNRunner.recordFactory.newRecordInstance(GetNewApplicationResponse.class);
        newAppResponse.setApplicationId(appId);
        Mockito.when(clientRMProtocol.getNewApplication(ArgumentMatchers.any(GetNewApplicationRequest.class))).thenReturn(newAppResponse);
        delegate.getNewJobID();
        Mockito.verify(clientRMProtocol).getNewApplication(ArgumentMatchers.any(GetNewApplicationRequest.class));
        GetQueueInfoResponse queueInfoResponse = TestYARNRunner.recordFactory.newRecordInstance(GetQueueInfoResponse.class);
        queueInfoResponse.setQueueInfo(TestYARNRunner.recordFactory.newRecordInstance(QueueInfo.class));
        Mockito.when(clientRMProtocol.getQueueInfo(ArgumentMatchers.any(GetQueueInfoRequest.class))).thenReturn(queueInfoResponse);
        delegate.getQueues();
        Mockito.verify(clientRMProtocol).getQueueInfo(ArgumentMatchers.any(GetQueueInfoRequest.class));
        GetQueueUserAclsInfoResponse aclResponse = TestYARNRunner.recordFactory.newRecordInstance(GetQueueUserAclsInfoResponse.class);
        Mockito.when(clientRMProtocol.getQueueUserAcls(ArgumentMatchers.any(GetQueueUserAclsInfoRequest.class))).thenReturn(aclResponse);
        delegate.getQueueAclsForCurrentUser();
        Mockito.verify(clientRMProtocol).getQueueUserAcls(ArgumentMatchers.any(GetQueueUserAclsInfoRequest.class));
    }

    @Test(timeout = 20000)
    public void testGetHSDelegationToken() throws Exception {
        try {
            Configuration conf = new Configuration();
            // Setup mock service
            InetSocketAddress mockRmAddress = new InetSocketAddress("localhost", 4444);
            Text rmTokenSevice = SecurityUtil.buildTokenService(mockRmAddress);
            InetSocketAddress mockHsAddress = new InetSocketAddress("localhost", 9200);
            Text hsTokenSevice = SecurityUtil.buildTokenService(mockHsAddress);
            // Setup mock rm token
            RMDelegationTokenIdentifier tokenIdentifier = new RMDelegationTokenIdentifier(new Text("owner"), new Text("renewer"), new Text("real"));
            Token<RMDelegationTokenIdentifier> token = new Token<RMDelegationTokenIdentifier>(new byte[0], new byte[0], tokenIdentifier.getKind(), rmTokenSevice);
            token.setKind(KIND_NAME);
            // Setup mock history token
            org.apache.hadoop.yarn.api.records.Token historyToken = org.apache.hadoop.yarn.api.records.Token.newInstance(new byte[0], MRDelegationTokenIdentifier.KIND_NAME.toString(), new byte[0], hsTokenSevice.toString());
            GetDelegationTokenResponse getDtResponse = Records.newRecord(GetDelegationTokenResponse.class);
            getDtResponse.setDelegationToken(historyToken);
            // mock services
            MRClientProtocol mockHsProxy = Mockito.mock(MRClientProtocol.class);
            Mockito.doReturn(mockHsAddress).when(mockHsProxy).getConnectAddress();
            Mockito.doReturn(getDtResponse).when(mockHsProxy).getDelegationToken(ArgumentMatchers.any(GetDelegationTokenRequest.class));
            ResourceMgrDelegate rmDelegate = Mockito.mock(ResourceMgrDelegate.class);
            Mockito.doReturn(rmTokenSevice).when(rmDelegate).getRMDelegationTokenService();
            ClientCache clientCache = Mockito.mock(ClientCache.class);
            Mockito.doReturn(mockHsProxy).when(clientCache).getInitializedHSProxy();
            Credentials creds = new Credentials();
            YARNRunner yarnRunner = new YARNRunner(conf, rmDelegate, clientCache);
            // No HS token if no RM token
            yarnRunner.addHistoryToken(creds);
            Mockito.verify(mockHsProxy, Mockito.times(0)).getDelegationToken(ArgumentMatchers.any(GetDelegationTokenRequest.class));
            // No HS token if RM token, but secirity disabled.
            creds.addToken(new Text("rmdt"), token);
            yarnRunner.addHistoryToken(creds);
            Mockito.verify(mockHsProxy, Mockito.times(0)).getDelegationToken(ArgumentMatchers.any(GetDelegationTokenRequest.class));
            conf.set(HADOOP_SECURITY_AUTHENTICATION, "kerberos");
            UserGroupInformation.setConfiguration(conf);
            creds = new Credentials();
            // No HS token if no RM token, security enabled
            yarnRunner.addHistoryToken(creds);
            Mockito.verify(mockHsProxy, Mockito.times(0)).getDelegationToken(ArgumentMatchers.any(GetDelegationTokenRequest.class));
            // HS token if RM token present, security enabled
            creds.addToken(new Text("rmdt"), token);
            yarnRunner.addHistoryToken(creds);
            Mockito.verify(mockHsProxy, Mockito.times(1)).getDelegationToken(ArgumentMatchers.any(GetDelegationTokenRequest.class));
            // No additional call to get HS token if RM and HS token present
            yarnRunner.addHistoryToken(creds);
            Mockito.verify(mockHsProxy, Mockito.times(1)).getDelegationToken(ArgumentMatchers.any(GetDelegationTokenRequest.class));
        } finally {
            // Back to defaults.
            UserGroupInformation.setConfiguration(new Configuration());
        }
    }

    @Test(timeout = 20000)
    public void testHistoryServerToken() throws Exception {
        // Set the master principal in the config
        conf.set(RM_PRINCIPAL, "foo@LOCAL");
        final String masterPrincipal = Master.getMasterPrincipal(conf);
        final MRClientProtocol hsProxy = Mockito.mock(MRClientProtocol.class);
        Mockito.when(hsProxy.getDelegationToken(ArgumentMatchers.any(GetDelegationTokenRequest.class))).thenAnswer(new Answer<GetDelegationTokenResponse>() {
            public GetDelegationTokenResponse answer(InvocationOnMock invocation) {
                GetDelegationTokenRequest request = ((GetDelegationTokenRequest) (invocation.getArguments()[0]));
                // check that the renewer matches the cluster's RM principal
                Assert.assertEquals(masterPrincipal, request.getRenewer());
                org.apache.hadoop.yarn.api.records.Token token = TestYARNRunner.recordFactory.newRecordInstance(Token.class);
                // none of these fields matter for the sake of the test
                token.setKind("");
                token.setService("");
                token.setIdentifier(ByteBuffer.allocate(0));
                token.setPassword(ByteBuffer.allocate(0));
                GetDelegationTokenResponse tokenResponse = TestYARNRunner.recordFactory.newRecordInstance(GetDelegationTokenResponse.class);
                tokenResponse.setDelegationToken(token);
                return tokenResponse;
            }
        });
        UserGroupInformation.createRemoteUser("someone").doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                yarnRunner = new YARNRunner(conf, null, null);
                yarnRunner.getDelegationTokenFromHS(hsProxy);
                Mockito.verify(hsProxy).getDelegationToken(ArgumentMatchers.any(GetDelegationTokenRequest.class));
                return null;
            }
        });
    }

    @Test(timeout = 20000)
    public void testAMAdminCommandOpts() throws Exception {
        JobConf jobConf = new JobConf();
        jobConf.set(MR_AM_ADMIN_COMMAND_OPTS, "-Djava.net.preferIPv4Stack=true");
        jobConf.set(MR_AM_COMMAND_OPTS, "-Xmx1024m");
        YARNRunner yarnRunner = new YARNRunner(jobConf);
        ApplicationSubmissionContext submissionContext = buildSubmitContext(yarnRunner, jobConf);
        ContainerLaunchContext containerSpec = submissionContext.getAMContainerSpec();
        List<String> commands = containerSpec.getCommands();
        int index = 0;
        int adminIndex = 0;
        int adminPos = -1;
        int userIndex = 0;
        int userPos = -1;
        int tmpDirPos = -1;
        for (String command : commands) {
            if (command != null) {
                Assert.assertFalse("Profiler should be disabled by default", command.contains(TestYARNRunner.PROFILE_PARAMS));
                adminPos = command.indexOf("-Djava.net.preferIPv4Stack=true");
                if (adminPos >= 0)
                    adminIndex = index;

                userPos = command.indexOf("-Xmx1024m");
                if (userPos >= 0)
                    userIndex = index;

                tmpDirPos = command.indexOf("-Djava.io.tmpdir=");
            }
            index++;
        }
        // Check java.io.tmpdir opts are set in the commands
        Assert.assertTrue("java.io.tmpdir is not set for AM", (tmpDirPos > 0));
        // Check both admin java opts and user java opts are in the commands
        Assert.assertTrue("AM admin command opts not in the commands.", (adminPos > 0));
        Assert.assertTrue("AM user command opts not in the commands.", (userPos > 0));
        // Check the admin java opts is before user java opts in the commands
        if (adminIndex == userIndex) {
            Assert.assertTrue("AM admin command opts is after user command opts.", (adminPos < userPos));
        } else {
            Assert.assertTrue("AM admin command opts is after user command opts.", (adminIndex < userIndex));
        }
    }

    @Test(timeout = 20000)
    public void testWarnCommandOpts() throws Exception {
        org.apache.log4j.Logger logger = getLogger(YARNRunner.class);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Layout layout = new SimpleLayout();
        Appender appender = new WriterAppender(layout, bout);
        logger.addAppender(appender);
        JobConf jobConf = new JobConf();
        jobConf.set(MR_AM_ADMIN_COMMAND_OPTS, "-Djava.net.preferIPv4Stack=true -Djava.library.path=foo");
        jobConf.set(MR_AM_COMMAND_OPTS, "-Xmx1024m -Djava.library.path=bar");
        YARNRunner yarnRunner = new YARNRunner(jobConf);
        @SuppressWarnings("unused")
        ApplicationSubmissionContext submissionContext = buildSubmitContext(yarnRunner, jobConf);
        String logMsg = bout.toString();
        Assert.assertTrue(logMsg.contains(("WARN - Usage of -Djava.library.path in " + ((("yarn.app.mapreduce.am.admin-command-opts can cause programs to no " + "longer function if hadoop native libraries are used. These values ") + "should be set as part of the LD_LIBRARY_PATH in the app master JVM ") + "env using yarn.app.mapreduce.am.admin.user.env config settings."))));
        Assert.assertTrue(logMsg.contains(("WARN - Usage of -Djava.library.path in " + ((("yarn.app.mapreduce.am.command-opts can cause programs to no longer " + "function if hadoop native libraries are used. These values should ") + "be set as part of the LD_LIBRARY_PATH in the app master JVM env ") + "using yarn.app.mapreduce.am.env config settings."))));
    }

    @Test(timeout = 20000)
    public void testAMProfiler() throws Exception {
        JobConf jobConf = new JobConf();
        jobConf.setBoolean(MR_AM_PROFILE, true);
        YARNRunner yarnRunner = new YARNRunner(jobConf);
        ApplicationSubmissionContext submissionContext = buildSubmitContext(yarnRunner, jobConf);
        ContainerLaunchContext containerSpec = submissionContext.getAMContainerSpec();
        List<String> commands = containerSpec.getCommands();
        for (String command : commands) {
            if (command != null) {
                if (command.contains(TestYARNRunner.PROFILE_PARAMS)) {
                    return;
                }
            }
        }
        throw new IllegalStateException("Profiler opts not found!");
    }

    @Test
    public void testNodeLabelExp() throws Exception {
        JobConf jobConf = new JobConf();
        jobConf.set(JOB_NODE_LABEL_EXP, "GPU");
        jobConf.set(AM_NODE_LABEL_EXP, "highMem");
        YARNRunner yarnRunner = new YARNRunner(jobConf);
        ApplicationSubmissionContext appSubCtx = buildSubmitContext(yarnRunner, jobConf);
        Assert.assertEquals(appSubCtx.getNodeLabelExpression(), "GPU");
        Assert.assertEquals(appSubCtx.getAMContainerResourceRequests().get(0).getNodeLabelExpression(), "highMem");
    }

    @Test
    public void testResourceRequestLocalityAny() throws Exception {
        ResourceRequest amAnyResourceRequest = createResourceRequest(ANY, true);
        verifyResourceRequestLocality(null, null, amAnyResourceRequest);
        verifyResourceRequestLocality(null, "label1", amAnyResourceRequest);
    }

    @Test
    public void testResourceRequestLocalityRack() throws Exception {
        ResourceRequest amAnyResourceRequest = createResourceRequest(ANY, false);
        ResourceRequest amRackResourceRequest = createResourceRequest("/rack1", true);
        verifyResourceRequestLocality("/rack1", null, amAnyResourceRequest, amRackResourceRequest);
        verifyResourceRequestLocality("/rack1", "label1", amAnyResourceRequest, amRackResourceRequest);
    }

    @Test
    public void testResourceRequestLocalityNode() throws Exception {
        ResourceRequest amAnyResourceRequest = createResourceRequest(ANY, false);
        ResourceRequest amRackResourceRequest = createResourceRequest("/rack1", false);
        ResourceRequest amNodeResourceRequest = createResourceRequest("node1", true);
        verifyResourceRequestLocality("/rack1/node1", null, amAnyResourceRequest, amRackResourceRequest, amNodeResourceRequest);
        verifyResourceRequestLocality("/rack1/node1", "label1", amAnyResourceRequest, amRackResourceRequest, amNodeResourceRequest);
    }

    @Test
    public void testResourceRequestLocalityNodeDefaultRack() throws Exception {
        ResourceRequest amAnyResourceRequest = createResourceRequest(ANY, false);
        ResourceRequest amRackResourceRequest = createResourceRequest("/default-rack", false);
        ResourceRequest amNodeResourceRequest = createResourceRequest("node1", true);
        verifyResourceRequestLocality("node1", null, amAnyResourceRequest, amRackResourceRequest, amNodeResourceRequest);
        verifyResourceRequestLocality("node1", "label1", amAnyResourceRequest, amRackResourceRequest, amNodeResourceRequest);
    }

    @Test
    public void testResourceRequestLocalityMultipleNodes() throws Exception {
        ResourceRequest amAnyResourceRequest = createResourceRequest(ANY, false);
        ResourceRequest amRackResourceRequest = createResourceRequest("/rack1", false);
        ResourceRequest amNodeResourceRequest = createResourceRequest("node1", true);
        ResourceRequest amNode2ResourceRequest = createResourceRequest("node2", true);
        verifyResourceRequestLocality("/rack1/node1,/rack1/node2", null, amAnyResourceRequest, amRackResourceRequest, amNodeResourceRequest, amNode2ResourceRequest);
        verifyResourceRequestLocality("/rack1/node1,/rack1/node2", "label1", amAnyResourceRequest, amRackResourceRequest, amNodeResourceRequest, amNode2ResourceRequest);
    }

    @Test
    public void testResourceRequestLocalityMultipleNodesDifferentRack() throws Exception {
        ResourceRequest amAnyResourceRequest = createResourceRequest(ANY, false);
        ResourceRequest amRackResourceRequest = createResourceRequest("/rack1", false);
        ResourceRequest amNodeResourceRequest = createResourceRequest("node1", true);
        ResourceRequest amRack2ResourceRequest = createResourceRequest("/rack2", false);
        ResourceRequest amNode2ResourceRequest = createResourceRequest("node2", true);
        verifyResourceRequestLocality("/rack1/node1,/rack2/node2", null, amAnyResourceRequest, amRackResourceRequest, amNodeResourceRequest, amRack2ResourceRequest, amNode2ResourceRequest);
        verifyResourceRequestLocality("/rack1/node1,/rack2/node2", "label1", amAnyResourceRequest, amRackResourceRequest, amNodeResourceRequest, amRack2ResourceRequest, amNode2ResourceRequest);
    }

    @Test
    public void testResourceRequestLocalityMultipleNodesDefaultRack() throws Exception {
        ResourceRequest amAnyResourceRequest = createResourceRequest(ANY, false);
        ResourceRequest amRackResourceRequest = createResourceRequest("/rack1", false);
        ResourceRequest amNodeResourceRequest = createResourceRequest("node1", true);
        ResourceRequest amRack2ResourceRequest = createResourceRequest("/default-rack", false);
        ResourceRequest amNode2ResourceRequest = createResourceRequest("node2", true);
        verifyResourceRequestLocality("/rack1/node1,node2", null, amAnyResourceRequest, amRackResourceRequest, amNodeResourceRequest, amRack2ResourceRequest, amNode2ResourceRequest);
        verifyResourceRequestLocality("/rack1/node1,node2", "label1", amAnyResourceRequest, amRackResourceRequest, amNodeResourceRequest, amRack2ResourceRequest, amNode2ResourceRequest);
    }

    @Test
    public void testResourceRequestLocalityInvalid() throws Exception {
        try {
            verifyResourceRequestLocality("rack/node1", null, new ResourceRequest[]{  });
            Assert.fail("Should have failed due to invalid resource but did not");
        } catch (IOException ioe) {
            Assert.assertTrue(ioe.getMessage().contains("Invalid resource name"));
        }
        try {
            verifyResourceRequestLocality("/rack/node1/blah", null, new ResourceRequest[]{  });
            Assert.fail("Should have failed due to invalid resource but did not");
        } catch (IOException ioe) {
            Assert.assertTrue(ioe.getMessage().contains("Invalid resource name"));
        }
    }

    @Test
    public void testAMStandardEnvWithDefaultLibPath() throws Exception {
        testAMStandardEnv(false, false);
    }

    @Test
    public void testAMStandardEnvWithCustomLibPath() throws Exception {
        testAMStandardEnv(true, false);
    }

    @Test
    public void testAMStandardEnvWithCustomLibPathWithSeparateEnvProps() throws Exception {
        testAMStandardEnv(true, true);
    }

    @Test
    public void testJobPriority() throws Exception {
        JobConf jobConf = new JobConf();
        jobConf.set(PRIORITY, "LOW");
        YARNRunner yarnRunner = new YARNRunner(jobConf);
        ApplicationSubmissionContext appSubCtx = buildSubmitContext(yarnRunner, jobConf);
        // 2 corresponds to LOW
        Assert.assertEquals(appSubCtx.getPriority(), Priority.newInstance(2));
        // Set an integer explicitly
        jobConf.set(PRIORITY, "12");
        yarnRunner = new YARNRunner(jobConf);
        appSubCtx = buildSubmitContext(yarnRunner, jobConf);
        // Verify whether 12 is set to submission context
        Assert.assertEquals(appSubCtx.getPriority(), Priority.newInstance(12));
    }

    // Test configs that match regex expression should be set in
    // containerLaunchContext
    @Test
    public void testSendJobConf() throws IOException {
        JobConf jobConf = new JobConf();
        jobConf.set("dfs.nameservices", "mycluster1,mycluster2");
        jobConf.set("dfs.namenode.rpc-address.mycluster2.nn1", "123.0.0.1");
        jobConf.set("dfs.namenode.rpc-address.mycluster2.nn2", "123.0.0.2");
        jobConf.set("dfs.ha.namenodes.mycluster2", "nn1,nn2");
        jobConf.set("dfs.client.failover.proxy.provider.mycluster2", "provider");
        jobConf.set("hadoop.tmp.dir", "testconfdir");
        jobConf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        jobConf.set("mapreduce.job.send-token-conf", ("dfs.nameservices|^dfs.namenode.rpc-address.*$|^dfs.ha.namenodes.*$" + ("|^dfs.client.failover.proxy.provider.*$" + "|dfs.namenode.kerberos.principal")));
        UserGroupInformation.setConfiguration(jobConf);
        YARNRunner yarnRunner = new YARNRunner(jobConf);
        ApplicationSubmissionContext submissionContext = buildSubmitContext(yarnRunner, jobConf);
        Configuration confSent = BuilderUtils.parseTokensConf(submissionContext);
        // configs that match regex should be included
        Assert.assertEquals("123.0.0.1", confSent.get("dfs.namenode.rpc-address.mycluster2.nn1"));
        Assert.assertEquals("123.0.0.2", confSent.get("dfs.namenode.rpc-address.mycluster2.nn2"));
        // configs that aren't matching regex should not be included
        Assert.assertTrue((((confSent.get("hadoop.tmp.dir")) == null) || (!(confSent.get("hadoop.tmp.dir").equals("testconfdir")))));
        UserGroupInformation.reset();
    }

    @Test
    public void testCustomAMRMResourceType() throws Exception {
        initResourceTypes();
        JobConf jobConf = new JobConf();
        jobConf.setInt(((MRJobConfig.MR_AM_RESOURCE_PREFIX) + (TestYARNRunner.CUSTOM_RESOURCE_NAME)), 5);
        jobConf.setInt(MR_AM_CPU_VCORES, 3);
        yarnRunner = new YARNRunner(jobConf);
        submissionContext = buildSubmitContext(yarnRunner, jobConf);
        List<ResourceRequest> resourceRequests = submissionContext.getAMContainerResourceRequests();
        Assert.assertEquals(1, resourceRequests.size());
        ResourceRequest resourceRequest = resourceRequests.get(0);
        ResourceInformation resourceInformation = resourceRequest.getCapability().getResourceInformation(TestYARNRunner.CUSTOM_RESOURCE_NAME);
        Assert.assertEquals("Expecting the default unit (G)", "G", resourceInformation.getUnits());
        Assert.assertEquals(5L, resourceInformation.getValue());
        Assert.assertEquals(3, resourceRequest.getCapability().getVirtualCores());
    }

    @Test
    public void testAMRMemoryRequest() throws Exception {
        for (String memoryName : ImmutableList.of(RESOURCE_TYPE_NAME_MEMORY, RESOURCE_TYPE_ALTERNATIVE_NAME_MEMORY)) {
            JobConf jobConf = new JobConf();
            jobConf.set(((MRJobConfig.MR_AM_RESOURCE_PREFIX) + memoryName), "3 Gi");
            yarnRunner = new YARNRunner(jobConf);
            submissionContext = buildSubmitContext(yarnRunner, jobConf);
            List<ResourceRequest> resourceRequests = submissionContext.getAMContainerResourceRequests();
            Assert.assertEquals(1, resourceRequests.size());
            ResourceRequest resourceRequest = resourceRequests.get(0);
            long memorySize = resourceRequest.getCapability().getMemorySize();
            Assert.assertEquals(3072, memorySize);
        }
    }

    @Test
    public void testAMRMemoryRequestOverriding() throws Exception {
        for (String memoryName : ImmutableList.of(RESOURCE_TYPE_NAME_MEMORY, RESOURCE_TYPE_ALTERNATIVE_NAME_MEMORY)) {
            TestYARNRunner.TestAppender testAppender = new TestYARNRunner.TestAppender();
            org.apache.log4j.Logger logger = getLogger(YARNRunner.class);
            logger.addAppender(testAppender);
            try {
                JobConf jobConf = new JobConf();
                jobConf.set(((MRJobConfig.MR_AM_RESOURCE_PREFIX) + memoryName), "3 Gi");
                jobConf.setInt(MR_AM_VMEM_MB, 2048);
                yarnRunner = new YARNRunner(jobConf);
                submissionContext = buildSubmitContext(yarnRunner, jobConf);
                List<ResourceRequest> resourceRequests = submissionContext.getAMContainerResourceRequests();
                Assert.assertEquals(1, resourceRequests.size());
                ResourceRequest resourceRequest = resourceRequests.get(0);
                long memorySize = resourceRequest.getCapability().getMemorySize();
                Assert.assertEquals(3072, memorySize);
                Assert.assertTrue(testAppender.getLogEvents().stream().anyMatch(( e) -> ((e.getLevel()) == (Level.WARN)) && (((((("Configuration " + "yarn.app.mapreduce.am.resource.") + memoryName) + "=3Gi is ") + "overriding the yarn.app.mapreduce.am.resource.mb=2048 ") + "configuration").equals(e.getMessage()))));
            } finally {
                logger.removeAppender(testAppender);
            }
        }
    }
}

