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


import JobState.RUNNING;
import JobStatus.State.PREP;
import JobStatus.State.SUCCEEDED;
import MRConfig.FRAMEWORK_NAME;
import MRConfig.YARN_FRAMEWORK_NAME;
import MRJobConfig.DEFAULT_MR_CLIENT_MAX_RETRIES;
import MRJobConfig.JOB_AM_ACCESS_DISABLED;
import MRJobConfig.MR_CLIENT_MAX_RETRIES;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.InetSocketAddress;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.JobStatus;
import org.apache.hadoop.mapreduce.TypeConverter;
import org.apache.hadoop.mapreduce.v2.api.MRClientProtocol;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetJobReportRequest;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetJobReportResponse;
import org.apache.hadoop.mapreduce.v2.api.records.Counters;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.util.MRBuilderUtils;
import org.apache.hadoop.security.authorize.AuthorizationException;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for ClientServiceDelegate.java
 */
@RunWith(Parameterized.class)
public class TestClientServiceDelegate {
    private JobID oldJobId = JobID.forName("job_1315895242400_2");

    private JobId jobId = TypeConverter.toYarn(oldJobId);

    private boolean isAMReachableFromClient;

    public TestClientServiceDelegate(boolean isAMReachableFromClient) {
        this.isAMReachableFromClient = isAMReachableFromClient;
    }

    @Test
    public void testUnknownAppInRM() throws Exception {
        MRClientProtocol historyServerProxy = Mockito.mock(MRClientProtocol.class);
        Mockito.when(historyServerProxy.getJobReport(getJobReportRequest())).thenReturn(getJobReportResponse());
        ClientServiceDelegate clientServiceDelegate = getClientServiceDelegate(historyServerProxy, getRMDelegate());
        JobStatus jobStatus = clientServiceDelegate.getJobStatus(oldJobId);
        Assert.assertNotNull(jobStatus);
    }

    @Test
    public void testRemoteExceptionFromHistoryServer() throws Exception {
        MRClientProtocol historyServerProxy = Mockito.mock(MRClientProtocol.class);
        Mockito.when(historyServerProxy.getJobReport(getJobReportRequest())).thenThrow(new IOException("Job ID doesnot Exist"));
        ResourceMgrDelegate rm = Mockito.mock(ResourceMgrDelegate.class);
        Mockito.when(rm.getApplicationReport(TypeConverter.toYarn(oldJobId).getAppId())).thenReturn(null);
        ClientServiceDelegate clientServiceDelegate = getClientServiceDelegate(historyServerProxy, rm);
        try {
            clientServiceDelegate.getJobStatus(oldJobId);
            Assert.fail("Invoke should throw exception after retries.");
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("Job ID doesnot Exist"));
        }
    }

    @Test
    public void testRetriesOnConnectionFailure() throws Exception {
        MRClientProtocol historyServerProxy = Mockito.mock(MRClientProtocol.class);
        Mockito.when(historyServerProxy.getJobReport(getJobReportRequest())).thenThrow(new RuntimeException("1")).thenThrow(new RuntimeException("2")).thenReturn(getJobReportResponse());
        ResourceMgrDelegate rm = Mockito.mock(ResourceMgrDelegate.class);
        Mockito.when(rm.getApplicationReport(TypeConverter.toYarn(oldJobId).getAppId())).thenReturn(null);
        ClientServiceDelegate clientServiceDelegate = getClientServiceDelegate(historyServerProxy, rm);
        JobStatus jobStatus = clientServiceDelegate.getJobStatus(oldJobId);
        Assert.assertNotNull(jobStatus);
        Mockito.verify(historyServerProxy, Mockito.times(3)).getJobReport(ArgumentMatchers.any(GetJobReportRequest.class));
    }

    @Test
    public void testRetriesOnAMConnectionFailures() throws Exception {
        if (!(isAMReachableFromClient)) {
            return;
        }
        ResourceMgrDelegate rm = Mockito.mock(ResourceMgrDelegate.class);
        Mockito.when(rm.getApplicationReport(TypeConverter.toYarn(oldJobId).getAppId())).thenReturn(getRunningApplicationReport("am1", 78));
        // throw exception in 1st, 2nd, 3rd and 4th call of getJobReport, and
        // succeed in the 5th call.
        final MRClientProtocol amProxy = Mockito.mock(MRClientProtocol.class);
        Mockito.when(amProxy.getJobReport(ArgumentMatchers.any(GetJobReportRequest.class))).thenThrow(new RuntimeException("11")).thenThrow(new RuntimeException("22")).thenThrow(new RuntimeException("33")).thenThrow(new RuntimeException("44")).thenReturn(getJobReportResponse());
        Configuration conf = new YarnConfiguration();
        conf.set(FRAMEWORK_NAME, YARN_FRAMEWORK_NAME);
        conf.setBoolean(JOB_AM_ACCESS_DISABLED, (!(isAMReachableFromClient)));
        ClientServiceDelegate clientServiceDelegate = new ClientServiceDelegate(conf, rm, oldJobId, null) {
            @Override
            MRClientProtocol instantiateAMProxy(final InetSocketAddress serviceAddr) throws IOException {
                super.instantiateAMProxy(serviceAddr);
                return amProxy;
            }
        };
        JobStatus jobStatus = clientServiceDelegate.getJobStatus(oldJobId);
        Assert.assertNotNull(jobStatus);
        // assert maxClientRetry is not decremented.
        Assert.assertEquals(conf.getInt(MR_CLIENT_MAX_RETRIES, DEFAULT_MR_CLIENT_MAX_RETRIES), clientServiceDelegate.getMaxClientRetry());
        Mockito.verify(amProxy, Mockito.times(5)).getJobReport(ArgumentMatchers.any(GetJobReportRequest.class));
    }

    @Test
    public void testNoRetryOnAMAuthorizationException() throws Exception {
        if (!(isAMReachableFromClient)) {
            return;
        }
        ResourceMgrDelegate rm = Mockito.mock(ResourceMgrDelegate.class);
        Mockito.when(rm.getApplicationReport(TypeConverter.toYarn(oldJobId).getAppId())).thenReturn(getRunningApplicationReport("am1", 78));
        // throw authorization exception on first invocation
        final MRClientProtocol amProxy = Mockito.mock(MRClientProtocol.class);
        Mockito.when(amProxy.getJobReport(ArgumentMatchers.any(GetJobReportRequest.class))).thenThrow(new AuthorizationException("Denied"));
        Configuration conf = new YarnConfiguration();
        conf.set(FRAMEWORK_NAME, YARN_FRAMEWORK_NAME);
        conf.setBoolean(JOB_AM_ACCESS_DISABLED, (!(isAMReachableFromClient)));
        ClientServiceDelegate clientServiceDelegate = new ClientServiceDelegate(conf, rm, oldJobId, null) {
            @Override
            MRClientProtocol instantiateAMProxy(final InetSocketAddress serviceAddr) throws IOException {
                super.instantiateAMProxy(serviceAddr);
                return amProxy;
            }
        };
        try {
            clientServiceDelegate.getJobStatus(oldJobId);
            Assert.fail("Exception should be thrown upon AuthorizationException");
        } catch (IOException e) {
            Assert.assertEquals(((AuthorizationException.class.getName()) + ": Denied"), e.getMessage());
        }
        // assert maxClientRetry is not decremented.
        Assert.assertEquals(conf.getInt(MR_CLIENT_MAX_RETRIES, DEFAULT_MR_CLIENT_MAX_RETRIES), clientServiceDelegate.getMaxClientRetry());
        Mockito.verify(amProxy, Mockito.times(1)).getJobReport(ArgumentMatchers.any(GetJobReportRequest.class));
    }

    @Test
    public void testHistoryServerNotConfigured() throws Exception {
        // RM doesn't have app report and job History Server is not configured
        ClientServiceDelegate clientServiceDelegate = getClientServiceDelegate(null, getRMDelegate());
        JobStatus jobStatus = clientServiceDelegate.getJobStatus(oldJobId);
        Assert.assertEquals("N/A", jobStatus.getUsername());
        Assert.assertEquals(PREP, jobStatus.getState());
        // RM has app report and job History Server is not configured
        ResourceMgrDelegate rm = Mockito.mock(ResourceMgrDelegate.class);
        ApplicationReport applicationReport = getFinishedApplicationReport();
        Mockito.when(rm.getApplicationReport(jobId.getAppId())).thenReturn(applicationReport);
        clientServiceDelegate = getClientServiceDelegate(null, rm);
        jobStatus = clientServiceDelegate.getJobStatus(oldJobId);
        Assert.assertEquals(applicationReport.getUser(), jobStatus.getUsername());
        Assert.assertEquals(SUCCEEDED, jobStatus.getState());
    }

    @Test
    public void testJobReportFromHistoryServer() throws Exception {
        MRClientProtocol historyServerProxy = Mockito.mock(MRClientProtocol.class);
        Mockito.when(historyServerProxy.getJobReport(getJobReportRequest())).thenReturn(getJobReportResponseFromHistoryServer());
        ResourceMgrDelegate rm = Mockito.mock(ResourceMgrDelegate.class);
        Mockito.when(rm.getApplicationReport(TypeConverter.toYarn(oldJobId).getAppId())).thenReturn(null);
        ClientServiceDelegate clientServiceDelegate = getClientServiceDelegate(historyServerProxy, rm);
        JobStatus jobStatus = clientServiceDelegate.getJobStatus(oldJobId);
        Assert.assertNotNull(jobStatus);
        Assert.assertEquals("TestJobFilePath", jobStatus.getJobFile());
        Assert.assertEquals("http://TestTrackingUrl", jobStatus.getTrackingUrl());
        Assert.assertEquals(1.0F, jobStatus.getMapProgress(), 0.0F);
        Assert.assertEquals(1.0F, jobStatus.getReduceProgress(), 0.0F);
    }

    @Test
    public void testCountersFromHistoryServer() throws Exception {
        MRClientProtocol historyServerProxy = Mockito.mock(MRClientProtocol.class);
        Mockito.when(historyServerProxy.getCounters(getCountersRequest())).thenReturn(getCountersResponseFromHistoryServer());
        ResourceMgrDelegate rm = Mockito.mock(ResourceMgrDelegate.class);
        Mockito.when(rm.getApplicationReport(TypeConverter.toYarn(oldJobId).getAppId())).thenReturn(null);
        ClientServiceDelegate clientServiceDelegate = getClientServiceDelegate(historyServerProxy, rm);
        Counters counters = TypeConverter.toYarn(clientServiceDelegate.getJobCounters(oldJobId));
        Assert.assertNotNull(counters);
        Assert.assertEquals(1001, counters.getCounterGroup("dummyCounters").getCounter("dummyCounter").getValue());
    }

    @Test
    public void testReconnectOnAMRestart() throws IOException {
        // test not applicable when AM not reachable
        // as instantiateAMProxy is not called at all
        if (!(isAMReachableFromClient)) {
            return;
        }
        MRClientProtocol historyServerProxy = Mockito.mock(MRClientProtocol.class);
        // RM returns AM1 url, null, null and AM2 url on invocations.
        // Nulls simulate the time when AM2 is in the process of restarting.
        ResourceMgrDelegate rmDelegate = Mockito.mock(ResourceMgrDelegate.class);
        try {
            Mockito.when(rmDelegate.getApplicationReport(jobId.getAppId())).thenReturn(getRunningApplicationReport("am1", 78)).thenReturn(getRunningApplicationReport(null, 0)).thenReturn(getRunningApplicationReport(null, 0)).thenReturn(getRunningApplicationReport("am2", 90));
        } catch (YarnException e) {
            throw new IOException(e);
        }
        GetJobReportResponse jobReportResponse1 = Mockito.mock(GetJobReportResponse.class);
        Mockito.when(jobReportResponse1.getJobReport()).thenReturn(MRBuilderUtils.newJobReport(jobId, "jobName-firstGen", "user", RUNNING, 0, 0, 0, 0, 0, 0, 0, "anything", null, false, ""));
        // First AM returns a report with jobName firstGen and simulates AM shutdown
        // on second invocation.
        MRClientProtocol firstGenAMProxy = Mockito.mock(MRClientProtocol.class);
        Mockito.when(firstGenAMProxy.getJobReport(ArgumentMatchers.any(GetJobReportRequest.class))).thenReturn(jobReportResponse1).thenThrow(new RuntimeException("AM is down!"));
        GetJobReportResponse jobReportResponse2 = Mockito.mock(GetJobReportResponse.class);
        Mockito.when(jobReportResponse2.getJobReport()).thenReturn(MRBuilderUtils.newJobReport(jobId, "jobName-secondGen", "user", RUNNING, 0, 0, 0, 0, 0, 0, 0, "anything", null, false, ""));
        // Second AM generation returns a report with jobName secondGen
        MRClientProtocol secondGenAMProxy = Mockito.mock(MRClientProtocol.class);
        Mockito.when(secondGenAMProxy.getJobReport(ArgumentMatchers.any(GetJobReportRequest.class))).thenReturn(jobReportResponse2);
        ClientServiceDelegate clientServiceDelegate = Mockito.spy(getClientServiceDelegate(historyServerProxy, rmDelegate));
        // First time, connection should be to AM1, then to AM2. Further requests
        // should use the same proxy to AM2 and so instantiateProxy shouldn't be
        // called.
        Mockito.doReturn(firstGenAMProxy).doReturn(secondGenAMProxy).when(clientServiceDelegate).instantiateAMProxy(ArgumentMatchers.any(InetSocketAddress.class));
        JobStatus jobStatus = clientServiceDelegate.getJobStatus(oldJobId);
        Assert.assertNotNull(jobStatus);
        Assert.assertEquals("jobName-firstGen", jobStatus.getJobName());
        jobStatus = clientServiceDelegate.getJobStatus(oldJobId);
        Assert.assertNotNull(jobStatus);
        Assert.assertEquals("jobName-secondGen", jobStatus.getJobName());
        jobStatus = clientServiceDelegate.getJobStatus(oldJobId);
        Assert.assertNotNull(jobStatus);
        Assert.assertEquals("jobName-secondGen", jobStatus.getJobName());
        Mockito.verify(clientServiceDelegate, Mockito.times(2)).instantiateAMProxy(ArgumentMatchers.any(InetSocketAddress.class));
    }

    @Test
    public void testAMAccessDisabled() throws IOException {
        // test only applicable when AM not reachable
        if (isAMReachableFromClient) {
            return;
        }
        MRClientProtocol historyServerProxy = Mockito.mock(MRClientProtocol.class);
        Mockito.when(historyServerProxy.getJobReport(getJobReportRequest())).thenReturn(getJobReportResponseFromHistoryServer());
        ResourceMgrDelegate rmDelegate = Mockito.mock(ResourceMgrDelegate.class);
        try {
            Mockito.when(rmDelegate.getApplicationReport(jobId.getAppId())).thenReturn(getRunningApplicationReport("am1", 78)).thenReturn(getRunningApplicationReport("am1", 78)).thenReturn(getRunningApplicationReport("am1", 78)).thenReturn(getFinishedApplicationReport());
        } catch (YarnException e) {
            throw new IOException(e);
        }
        ClientServiceDelegate clientServiceDelegate = Mockito.spy(getClientServiceDelegate(historyServerProxy, rmDelegate));
        JobStatus jobStatus = clientServiceDelegate.getJobStatus(oldJobId);
        Assert.assertNotNull(jobStatus);
        Assert.assertEquals("N/A", jobStatus.getJobName());
        Mockito.verify(clientServiceDelegate, Mockito.times(0)).instantiateAMProxy(ArgumentMatchers.any(InetSocketAddress.class));
        // Should not reach AM even for second and third times too.
        jobStatus = clientServiceDelegate.getJobStatus(oldJobId);
        Assert.assertNotNull(jobStatus);
        Assert.assertEquals("N/A", jobStatus.getJobName());
        Mockito.verify(clientServiceDelegate, Mockito.times(0)).instantiateAMProxy(ArgumentMatchers.any(InetSocketAddress.class));
        jobStatus = clientServiceDelegate.getJobStatus(oldJobId);
        Assert.assertNotNull(jobStatus);
        Assert.assertEquals("N/A", jobStatus.getJobName());
        Mockito.verify(clientServiceDelegate, Mockito.times(0)).instantiateAMProxy(ArgumentMatchers.any(InetSocketAddress.class));
        // The third time around, app is completed, so should go to JHS
        JobStatus jobStatus1 = clientServiceDelegate.getJobStatus(oldJobId);
        Assert.assertNotNull(jobStatus1);
        Assert.assertEquals("TestJobFilePath", jobStatus1.getJobFile());
        Assert.assertEquals("http://TestTrackingUrl", jobStatus1.getTrackingUrl());
        Assert.assertEquals(1.0F, jobStatus1.getMapProgress(), 0.0F);
        Assert.assertEquals(1.0F, jobStatus1.getReduceProgress(), 0.0F);
        Mockito.verify(clientServiceDelegate, Mockito.times(0)).instantiateAMProxy(ArgumentMatchers.any(InetSocketAddress.class));
    }

    @Test
    public void testRMDownForJobStatusBeforeGetAMReport() throws IOException {
        Configuration conf = new YarnConfiguration();
        testRMDownForJobStatusBeforeGetAMReport(conf, DEFAULT_MR_CLIENT_MAX_RETRIES);
    }

    @Test
    public void testRMDownForJobStatusBeforeGetAMReportWithRetryTimes() throws IOException {
        Configuration conf = new YarnConfiguration();
        conf.setInt(MR_CLIENT_MAX_RETRIES, 2);
        testRMDownForJobStatusBeforeGetAMReport(conf, conf.getInt(MR_CLIENT_MAX_RETRIES, DEFAULT_MR_CLIENT_MAX_RETRIES));
    }

    @Test
    public void testRMDownRestoreForJobStatusBeforeGetAMReport() throws IOException {
        Configuration conf = new YarnConfiguration();
        conf.setInt(MR_CLIENT_MAX_RETRIES, 3);
        conf.set(FRAMEWORK_NAME, YARN_FRAMEWORK_NAME);
        conf.setBoolean(JOB_AM_ACCESS_DISABLED, (!(isAMReachableFromClient)));
        MRClientProtocol historyServerProxy = Mockito.mock(MRClientProtocol.class);
        Mockito.when(historyServerProxy.getJobReport(ArgumentMatchers.any(GetJobReportRequest.class))).thenReturn(getJobReportResponse());
        ResourceMgrDelegate rmDelegate = Mockito.mock(ResourceMgrDelegate.class);
        try {
            Mockito.when(rmDelegate.getApplicationReport(jobId.getAppId())).thenThrow(new UndeclaredThrowableException(new IOException("Connection refuced1"))).thenThrow(new UndeclaredThrowableException(new IOException("Connection refuced2"))).thenReturn(getFinishedApplicationReport());
            ClientServiceDelegate clientServiceDelegate = new ClientServiceDelegate(conf, rmDelegate, oldJobId, historyServerProxy);
            JobStatus jobStatus = clientServiceDelegate.getJobStatus(oldJobId);
            Mockito.verify(rmDelegate, Mockito.times(3)).getApplicationReport(ArgumentMatchers.any(ApplicationId.class));
            Assert.assertNotNull(jobStatus);
        } catch (YarnException e) {
            throw new IOException(e);
        }
    }
}

