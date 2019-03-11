/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.client.rest;


import Constants.SECOND_MS;
import ServiceConstants.CANCEL;
import ServiceConstants.GET_STATUS;
import ServiceConstants.LIST;
import ServiceConstants.SERVICE_NAME;
import ServiceConstants.SERVICE_VERSION;
import Status.CANCELED;
import Status.COMPLETED;
import alluxio.Constants;
import alluxio.job.JobConfig;
import alluxio.job.wire.JobInfo;
import alluxio.master.LocalAlluxioJobCluster;
import alluxio.master.job.JobMaster;
import alluxio.util.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.ws.rs.HttpMethod;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link JobMasterClientRestServiceHandler}.
 */
public final class JobMasterClientRestApiTest extends RestApiTest {
    private static final Map<String, String> NO_PARAMS = Maps.newHashMap();

    private LocalAlluxioJobCluster mJobCluster;

    private JobMaster mJobMaster;

    @Test
    public void serviceName() throws Exception {
        new TestCase(mHostname, mPort, getEndpoint(SERVICE_NAME), JobMasterClientRestApiTest.NO_PARAMS, HttpMethod.GET, Constants.JOB_MASTER_CLIENT_SERVICE_NAME).run();
    }

    @Test
    public void serviceVersion() throws Exception {
        new TestCase(mHostname, mPort, getEndpoint(SERVICE_VERSION), JobMasterClientRestApiTest.NO_PARAMS, HttpMethod.GET, Constants.JOB_MASTER_CLIENT_SERVICE_VERSION).run();
    }

    @Test
    public void run() throws Exception {
        final long jobId = startJob(new alluxio.job.SleepJobConfig(Constants.SECOND_MS));
        Assert.assertEquals(1, mJobMaster.list().size());
        waitForStatus(jobId, COMPLETED);
    }

    @Test
    public void cancel() throws Exception {
        long jobId = startJob(new alluxio.job.SleepJobConfig((10 * (Constants.SECOND_MS))));
        // Sleep to make sure the run request and the cancel request are separated by a job worker
        // heartbeat. If not, job service will not handle that case correctly.
        CommonUtils.sleepMs(SECOND_MS);
        Map<String, String> params = Maps.newHashMap();
        params.put("jobId", Long.toString(jobId));
        new TestCase(mHostname, mPort, getEndpoint(CANCEL), params, HttpMethod.POST, null).run();
        waitForStatus(jobId, CANCELED);
    }

    @Test
    public void list() throws Exception {
        List<Long> empty = Lists.newArrayList();
        new TestCase(mHostname, mPort, getEndpoint(LIST), JobMasterClientRestApiTest.NO_PARAMS, HttpMethod.GET, empty).run();
    }

    @Test
    public void getStatus() throws Exception {
        JobConfig config = new alluxio.job.SleepJobConfig(Constants.SECOND_MS);
        final long jobId = startJob(config);
        Map<String, String> params = Maps.newHashMap();
        params.put("jobId", Long.toString(jobId));
        TestCaseOptions options = TestCaseOptions.defaults().setPrettyPrint(true);
        String result = new TestCase(mHostname, mPort, getEndpoint(GET_STATUS), params, HttpMethod.GET, null, options).call();
        JobInfo jobInfo = new ObjectMapper().readValue(result, JobInfo.class);
        Assert.assertEquals(jobId, jobInfo.getJobId());
        Assert.assertEquals(1, jobInfo.getTaskInfoList().size());
    }
}

