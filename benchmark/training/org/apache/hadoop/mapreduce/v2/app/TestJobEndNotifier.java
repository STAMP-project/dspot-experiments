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
package org.apache.hadoop.mapreduce.v2.app;


import JobState.RUNNING;
import JobStateInternal.REBOOT;
import MRJobConfig.MR_JOB_END_NOTIFICATION_MAX_ATTEMPTS;
import MRJobConfig.MR_JOB_END_NOTIFICATION_MAX_RETRY_INTERVAL;
import MRJobConfig.MR_JOB_END_NOTIFICATION_URL;
import MRJobConfig.MR_JOB_END_RETRY_ATTEMPTS;
import MRJobConfig.MR_JOB_END_RETRY_INTERVAL;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.HttpServer2;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.v2.api.records.JobReport;
import org.apache.hadoop.mapreduce.v2.app.client.ClientService;
import org.apache.hadoop.mapreduce.v2.app.job.event.JobEventType;
import org.apache.hadoop.mapreduce.v2.app.job.impl.JobImpl;
import org.apache.hadoop.mapreduce.v2.app.rm.ContainerAllocator;
import org.apache.hadoop.mapreduce.v2.app.rm.ContainerAllocatorEvent;
import org.apache.hadoop.mapreduce.v2.app.rm.RMCommunicator;
import org.apache.hadoop.mapreduce.v2.app.rm.RMHeartbeatHandler;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests job end notification
 */
@SuppressWarnings("unchecked")
public class TestJobEndNotifier extends JobEndNotifier {
    /**
     * Test that setting parameters has the desired effect
     */
    @Test
    public void checkConfiguration() {
        Configuration conf = new Configuration();
        testNumRetries(conf);
        testWaitInterval(conf);
        testTimeout(conf);
        testProxyConfiguration(conf);
    }

    protected int notificationCount = 0;

    // Check retries happen as intended
    @Test
    public void testNotifyRetries() throws InterruptedException {
        JobConf conf = new JobConf();
        conf.set(MR_JOB_END_RETRY_ATTEMPTS, "0");
        conf.set(MR_JOB_END_NOTIFICATION_MAX_ATTEMPTS, "1");
        conf.set(MR_JOB_END_NOTIFICATION_URL, "http://nonexistent");
        conf.set(MR_JOB_END_RETRY_INTERVAL, "5000");
        conf.set(MR_JOB_END_NOTIFICATION_MAX_RETRY_INTERVAL, "5000");
        JobReport jobReport = Mockito.mock(JobReport.class);
        long startTime = System.currentTimeMillis();
        this.notificationCount = 0;
        this.setConf(conf);
        this.notify(jobReport);
        long endTime = System.currentTimeMillis();
        Assert.assertEquals(("Only 1 try was expected but was : " + (this.notificationCount)), 1, this.notificationCount);
        Assert.assertTrue(("Should have taken more than 5 seconds it took " + (endTime - startTime)), ((endTime - startTime) > 5000));
        conf.set(MR_JOB_END_NOTIFICATION_MAX_ATTEMPTS, "3");
        conf.set(MR_JOB_END_RETRY_ATTEMPTS, "3");
        conf.set(MR_JOB_END_RETRY_INTERVAL, "3000");
        conf.set(MR_JOB_END_NOTIFICATION_MAX_RETRY_INTERVAL, "3000");
        startTime = System.currentTimeMillis();
        this.notificationCount = 0;
        this.setConf(conf);
        this.notify(jobReport);
        endTime = System.currentTimeMillis();
        Assert.assertEquals(("Only 3 retries were expected but was : " + (this.notificationCount)), 3, this.notificationCount);
        Assert.assertTrue(("Should have taken more than 9 seconds it took " + (endTime - startTime)), ((endTime - startTime) > 9000));
    }

    @Test
    public void testNotificationOnLastRetryNormalShutdown() throws Exception {
        testNotificationOnLastRetry(false);
    }

    @Test
    public void testNotificationOnLastRetryShutdownWithRuntimeException() throws Exception {
        testNotificationOnLastRetry(true);
    }

    @Test
    public void testAbsentNotificationOnNotLastRetryUnregistrationFailure() throws Exception {
        HttpServer2 server = TestJobEndNotifier.startHttpServer();
        MRApp app = Mockito.spy(new TestJobEndNotifier.MRAppWithCustomContainerAllocator(2, 2, false, this.getClass().getName(), true, 1, false));
        sysexit();
        JobConf conf = new JobConf();
        conf.set(JobContext.MR_JOB_END_NOTIFICATION_URL, ((TestJobEndNotifier.JobEndServlet.baseUrl) + "jobend?jobid=$jobId&status=$jobStatus"));
        JobImpl job = ((JobImpl) (app.submit(conf)));
        app.waitForState(job, RUNNING);
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.JobEvent(getJobId(), JobEventType.JOB_AM_REBOOT));
        app.waitForInternalState(job, REBOOT);
        // Now shutdown.
        // Unregistration fails: isLastAMRetry is recalculated, this is not
        shutDownJob();
        // Not the last AM attempt. So user should that the job is still running.
        app.waitForState(job, RUNNING);
        Assert.assertFalse(isLastAMRetry());
        Assert.assertEquals(0, TestJobEndNotifier.JobEndServlet.calledTimes);
        Assert.assertNull(TestJobEndNotifier.JobEndServlet.requestUri);
        Assert.assertNull(TestJobEndNotifier.JobEndServlet.foundJobState);
        server.stop();
    }

    @Test
    public void testNotificationOnLastRetryUnregistrationFailure() throws Exception {
        HttpServer2 server = TestJobEndNotifier.startHttpServer();
        MRApp app = Mockito.spy(new TestJobEndNotifier.MRAppWithCustomContainerAllocator(2, 2, false, this.getClass().getName(), true, 2, false));
        // Currently, we will have isLastRetry always equals to false at beginning
        // of MRAppMaster, except staging area exists or commit already started at
        // the beginning.
        // Now manually set isLastRetry to true and this should reset to false when
        // unregister failed.
        app.isLastAMRetry = true;
        sysexit();
        JobConf conf = new JobConf();
        conf.set(JobContext.MR_JOB_END_NOTIFICATION_URL, ((TestJobEndNotifier.JobEndServlet.baseUrl) + "jobend?jobid=$jobId&status=$jobStatus"));
        JobImpl job = ((JobImpl) (app.submit(conf)));
        app.waitForState(job, RUNNING);
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.JobEvent(getJobId(), JobEventType.JOB_AM_REBOOT));
        app.waitForInternalState(job, REBOOT);
        // Now shutdown. User should see FAILED state.
        // Unregistration fails: isLastAMRetry is recalculated, this is
        // /reboot will stop service internally, we don't need to shutdown twice
        waitForServiceToStop(10000);
        Assert.assertFalse(isLastAMRetry());
        // Since it's not last retry, JobEndServlet didn't called
        Assert.assertEquals(0, TestJobEndNotifier.JobEndServlet.calledTimes);
        Assert.assertNull(TestJobEndNotifier.JobEndServlet.requestUri);
        Assert.assertNull(TestJobEndNotifier.JobEndServlet.foundJobState);
        server.stop();
    }

    @SuppressWarnings("serial")
    public static class JobEndServlet extends HttpServlet {
        public static volatile int calledTimes = 0;

        public static URI requestUri;

        public static String baseUrl;

        public static String foundJobState;

        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            InputStreamReader in = new InputStreamReader(request.getInputStream());
            PrintStream out = new PrintStream(response.getOutputStream());
            (TestJobEndNotifier.JobEndServlet.calledTimes)++;
            try {
                TestJobEndNotifier.JobEndServlet.requestUri = new URI(null, null, request.getRequestURI(), request.getQueryString(), null);
                TestJobEndNotifier.JobEndServlet.foundJobState = request.getParameter("status");
            } catch (URISyntaxException e) {
            }
            in.close();
            out.close();
        }
    }

    private class MRAppWithCustomContainerAllocator extends MRApp {
        private boolean crushUnregistration;

        public MRAppWithCustomContainerAllocator(int maps, int reduces, boolean autoComplete, String testName, boolean cleanOnStart, int startCount, boolean crushUnregistration) {
            super(maps, reduces, autoComplete, testName, cleanOnStart, startCount, false);
            this.crushUnregistration = crushUnregistration;
        }

        @Override
        protected ContainerAllocator createContainerAllocator(ClientService clientService, AppContext context) {
            context = Mockito.spy(context);
            Mockito.when(context.getEventHandler()).thenReturn(null);
            Mockito.when(context.getApplicationID()).thenReturn(null);
            return new TestJobEndNotifier.MRAppWithCustomContainerAllocator.CustomContainerAllocator(this, context);
        }

        private class CustomContainerAllocator extends RMCommunicator implements ContainerAllocator , RMHeartbeatHandler {
            private TestJobEndNotifier.MRAppWithCustomContainerAllocator app;

            private MRApp.MRAppContainerAllocator allocator = new MRApp.MRAppContainerAllocator();

            public CustomContainerAllocator(TestJobEndNotifier.MRAppWithCustomContainerAllocator app, AppContext context) {
                super(null, context);
                this.app = app;
            }

            @Override
            public void serviceInit(Configuration conf) {
            }

            @Override
            public void serviceStart() {
            }

            @Override
            public void serviceStop() {
                unregister();
            }

            @Override
            protected void doUnregistration() throws IOException, InterruptedException, YarnException {
                if (crushUnregistration) {
                    app.successfullyUnregistered.set(true);
                } else {
                    throw new YarnException("test exception");
                }
            }

            @Override
            public void handle(ContainerAllocatorEvent event) {
                allocator.handle(event);
            }

            @Override
            public long getLastHeartbeatTime() {
                return allocator.getLastHeartbeatTime();
            }

            @Override
            public void runOnNextHeartbeat(Runnable callback) {
                allocator.runOnNextHeartbeat(callback);
            }

            @Override
            protected void heartbeat() throws Exception {
            }
        }
    }
}

