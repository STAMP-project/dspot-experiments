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
package org.apache.ambari.view.pig.test;


import JobResourceManager.RUN_STATE_FAILED;
import JobResourceManager.RUN_STATE_KILLED;
import JobResourceManager.RUN_STATE_PREP;
import JobResourceManager.RUN_STATE_RUNNING;
import JobResourceManager.RUN_STATE_SUCCEEDED;
import PigJob.PIG_JOB_STATE_COMPLETED;
import PigJob.PIG_JOB_STATE_FAILED;
import PigJob.PIG_JOB_STATE_KILLED;
import PigJob.PIG_JOB_STATE_RUNNING;
import PigJob.PIG_JOB_STATE_SUBMITTED;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.ambari.view.pig.BasePigTest;
import org.apache.ambari.view.pig.resources.jobs.JobService;
import org.apache.ambari.view.pig.resources.jobs.models.PigJob;
import org.apache.ambari.view.pig.templeton.client.TempletonApi;
import org.apache.ambari.view.pig.utils.BadRequestFormattedException;
import org.apache.ambari.view.pig.utils.NotFoundFormattedException;
import org.apache.ambari.view.pig.utils.ServiceFormattedException;
import org.apache.ambari.view.pig.utils.UserLocalObjects;
import org.apache.ambari.view.utils.hdfs.HdfsApi;
import org.apache.ambari.view.utils.hdfs.HdfsApiException;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.easymock.EasyMock;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class JobTest extends BasePigTest {
    private JobService jobService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSubmitJob() throws Exception {
        HdfsApi hdfsApi = createNiceMock(HdfsApi.class);
        hdfsApi.copy(eq("/tmp/script.pig"), startsWith("/tmp/.pigjobs/"));
        ByteArrayOutputStream do_stream = new ByteArrayOutputStream();
        FSDataOutputStream stream = new FSDataOutputStream(do_stream, null);
        expect(hdfsApi.create(anyString(), eq(true))).andReturn(stream);
        replay(hdfsApi);
        UserLocalObjects.setHdfsApi(hdfsApi, context);
        TempletonApi api = createNiceMock(TempletonApi.class);
        UserLocalObjects.setTempletonApi(api, context);
        TempletonApi.JobData data = api.new JobData();
        data.id = "job_1466418324742_0005";
        expect(api.runPigQuery(((File) (anyObject())), anyString(), eq("-useHCatalog"))).andReturn(data);
        replay(api);
        Response response = JobTest.doCreateJob("Test", "/tmp/script.pig", "-useHCatalog", jobService);
        Assert.assertEquals("-useHCatalog", do_stream.toString());
        Assert.assertEquals(201, response.getStatus());
        JSONObject obj = ((JSONObject) (response.getEntity()));
        Assert.assertTrue(obj.containsKey("job"));
        Assert.assertNotNull(getId());
        Assert.assertFalse(getId().isEmpty());
        Assert.assertTrue(startsWith("/tmp/.pigjobs/test"));
        PigJob job = ((PigJob) (obj.get("job")));
        Assert.assertEquals(PIG_JOB_STATE_SUBMITTED, job.getStatus());
        Assert.assertTrue(job.isInProgress());
    }

    @Test
    public void testListJobs() throws Exception {
        HdfsApi hdfsApi = createNiceMock(HdfsApi.class);
        hdfsApi.copy(eq("/tmp/script.pig"), startsWith("/tmp/.pigjobs/"));
        ByteArrayOutputStream do_stream = new ByteArrayOutputStream();
        FSDataOutputStream stream = new FSDataOutputStream(do_stream, null);
        expect(hdfsApi.create(anyString(), eq(true))).andReturn(stream).anyTimes();
        replay(hdfsApi);
        UserLocalObjects.setHdfsApi(hdfsApi, context);
        TempletonApi api = createNiceMock(TempletonApi.class);
        UserLocalObjects.setTempletonApi(api, context);
        TempletonApi.JobData data = api.new JobData();
        data.id = "job_1466418324742_0005";
        expect(api.runPigQuery(((File) (anyObject())), anyString(), ((String) (isNull())))).andReturn(data).anyTimes();
        replay(api);
        Response response = JobTest.doCreateJob("Test", "/tmp/script.pig", null, null, "x42", jobService);
        Assert.assertEquals(201, response.getStatus());
        response = JobTest.doCreateJob("Test", "/tmp/script.pig", null, null, "x42", jobService);
        Assert.assertEquals(201, response.getStatus());
        response = JobTest.doCreateJob("Test", "/tmp/script.pig", null, null, "100", jobService);
        Assert.assertEquals(201, response.getStatus());
        response = jobService.getJobList("x42");
        Assert.assertEquals(200, response.getStatus());
        JSONObject obj = ((JSONObject) (response.getEntity()));
        Assert.assertTrue(obj.containsKey("jobs"));
        Assert.assertEquals(2, ((List) (obj.get("jobs"))).size());
        response = jobService.getJobList(null);
        Assert.assertEquals(200, response.getStatus());
        obj = ((JSONObject) (response.getEntity()));
        Assert.assertTrue(obj.containsKey("jobs"));
        Assert.assertTrue(((((List) (obj.get("jobs"))).size()) > 2));
    }

    @Test
    public void testSubmitJobUsernameProvided() throws Exception {
        HdfsApi hdfsApi = createNiceMock(HdfsApi.class);
        hdfsApi.copy(eq("/tmp/script.pig"), startsWith("/tmp/.pigjobs/"));
        ByteArrayOutputStream do_stream = new ByteArrayOutputStream();
        FSDataOutputStream stream = new FSDataOutputStream(do_stream, null);
        expect(hdfsApi.create(anyString(), eq(true))).andReturn(stream);
        replay(hdfsApi);
        UserLocalObjects.setHdfsApi(hdfsApi, context);
        TempletonApi api = createNiceMock(TempletonApi.class);
        UserLocalObjects.setTempletonApi(api, context);
        TempletonApi.JobData data = api.new JobData();
        data.id = "job_1466418324742_0005";
        expect(api.runPigQuery(((File) (anyObject())), anyString(), eq("-useHCatalog"))).andReturn(data);
        replay(api);
        properties.put("dataworker.username", "luke");
        Response response = JobTest.doCreateJob("Test", "/tmp/script.pig", "-useHCatalog", jobService);
        JSONObject obj = ((JSONObject) (response.getEntity()));
        Assert.assertTrue(obj.containsKey("job"));
        Assert.assertTrue(startsWith("/tmp/.pigjobs/test"));
    }

    @Test
    public void testSubmitJobNoArguments() throws Exception {
        HdfsApi hdfsApi = createNiceMock(HdfsApi.class);
        hdfsApi.copy(eq("/tmp/script.pig"), startsWith("/tmp/.pigjobs/"));
        ByteArrayOutputStream do_stream = new ByteArrayOutputStream();
        FSDataOutputStream stream = new FSDataOutputStream(do_stream, null);
        expect(hdfsApi.create(anyString(), eq(true))).andReturn(stream);
        replay(hdfsApi);
        UserLocalObjects.setHdfsApi(hdfsApi, context);
        TempletonApi api = createNiceMock(TempletonApi.class);
        UserLocalObjects.setTempletonApi(api, context);
        TempletonApi.JobData data = api.new JobData();
        data.id = "job_1466418324742_0005";
        expect(api.runPigQuery(((File) (anyObject())), anyString(), ((String) (isNull())))).andReturn(data);
        replay(api);
        Response response = JobTest.doCreateJob("Test", "/tmp/script.pig", null, jobService);
        Assert.assertEquals("", do_stream.toString());
        Assert.assertEquals(201, response.getStatus());
        JSONObject obj = ((JSONObject) (response.getEntity()));
        Assert.assertTrue(obj.containsKey("job"));
        Assert.assertNotNull(getId());
        Assert.assertFalse(getId().isEmpty());
        Assert.assertTrue(startsWith("/tmp/.pigjobs/test"));
        PigJob job = ((PigJob) (obj.get("job")));
        Assert.assertEquals(PIG_JOB_STATE_SUBMITTED, job.getStatus());
        Assert.assertTrue(job.isInProgress());
    }

    @Test
    public void testSubmitJobNoFile() throws Exception {
        HdfsApi hdfsApi = createNiceMock(HdfsApi.class);
        hdfsApi.copy(eq("/tmp/script.pig"), startsWith("/tmp/.pigjobs/"));
        ByteArrayOutputStream do_stream = new ByteArrayOutputStream();
        FSDataOutputStream stream = new FSDataOutputStream(do_stream, null);
        expect(hdfsApi.create(anyString(), eq(true))).andReturn(stream);
        replay(hdfsApi);
        UserLocalObjects.setHdfsApi(hdfsApi, context);
        TempletonApi api = createNiceMock(TempletonApi.class);
        UserLocalObjects.setTempletonApi(api, context);
        TempletonApi.JobData data = api.new JobData();
        expect(api.runPigQuery(((File) (anyObject())), anyString(), eq("-useHCatalog"))).andReturn(data);
        replay(api);
        thrown.expect(ServiceFormattedException.class);
        JobTest.doCreateJob("Test", null, "-useHCatalog", jobService);
    }

    @Test
    public void testSubmitJobForcedContent() throws Exception {
        HdfsApi hdfsApi = createNiceMock(HdfsApi.class);
        ByteArrayOutputStream baScriptStream = new ByteArrayOutputStream();
        ByteArrayOutputStream baTempletonArgsStream = new ByteArrayOutputStream();
        FSDataOutputStream scriptStream = new FSDataOutputStream(baScriptStream, null);
        FSDataOutputStream templetonArgsStream = new FSDataOutputStream(baTempletonArgsStream, null);
        expect(hdfsApi.create(endsWith("script.pig"), eq(true))).andReturn(scriptStream);
        expect(hdfsApi.create(endsWith("params"), eq(true))).andReturn(templetonArgsStream);
        replay(hdfsApi);
        UserLocalObjects.setHdfsApi(hdfsApi, context);
        TempletonApi api = createNiceMock(TempletonApi.class);
        UserLocalObjects.setTempletonApi(api, context);
        TempletonApi.JobData data = api.new JobData();
        data.id = "job_1466418324742_0005";
        expect(api.runPigQuery(((File) (anyObject())), anyString(), eq("-useHCatalog"))).andReturn(data);
        replay(api);
        Response response = JobTest.doCreateJob("Test", null, "-useHCatalog", "pwd", null, jobService);// with forcedContent

        Assert.assertEquals(201, response.getStatus());
        Assert.assertEquals("-useHCatalog", baTempletonArgsStream.toString());
        Assert.assertEquals("pwd", baScriptStream.toString());
    }

    @Test
    public void testSubmitJobNoTitle() throws Exception {
        HdfsApi hdfsApi = createNiceMock(HdfsApi.class);
        hdfsApi.copy(eq("/tmp/script.pig"), startsWith("/tmp/.pigjobs/"));
        ByteArrayOutputStream do_stream = new ByteArrayOutputStream();
        FSDataOutputStream stream = new FSDataOutputStream(do_stream, null);
        expect(hdfsApi.create(anyString(), eq(true))).andReturn(stream);
        replay(hdfsApi);
        UserLocalObjects.setHdfsApi(hdfsApi, context);
        TempletonApi api = createNiceMock(TempletonApi.class);
        UserLocalObjects.setTempletonApi(api, context);
        TempletonApi.JobData data = api.new JobData();
        expect(api.runPigQuery(((File) (anyObject())), anyString(), eq("-useHCatalog"))).andReturn(data);
        replay(api);
        thrown.expect(BadRequestFormattedException.class);
        JobTest.doCreateJob(null, "/tmp/1.pig", "-useHCatalog", jobService);
    }

    @Test
    public void testSubmitJobFailed() throws Exception {
        HdfsApi hdfsApi = createNiceMock(HdfsApi.class);
        hdfsApi.copy(eq("/tmp/script.pig"), startsWith("/tmp/.pigjobs/"));
        EasyMock.expectLastCall().andThrow(new HdfsApiException("Copy failed"));
        ByteArrayOutputStream do_stream = new ByteArrayOutputStream();
        FSDataOutputStream stream = new FSDataOutputStream(do_stream, null);
        expect(hdfsApi.create(anyString(), eq(true))).andReturn(stream);
        replay(hdfsApi);
        UserLocalObjects.setHdfsApi(hdfsApi, context);
        TempletonApi api = createNiceMock(TempletonApi.class);
        UserLocalObjects.setTempletonApi(api, context);
        TempletonApi.JobData data = api.new JobData();
        expect(api.runPigQuery(((File) (anyObject())), anyString(), eq("-useHCatalog"))).andReturn(data);
        replay(api);
        thrown.expect(ServiceFormattedException.class);
        JobTest.doCreateJob("Test", "/tmp/script.pig", "-useHCatalog", jobService);
    }

    @Test
    public void testSubmitJobTempletonError() throws Exception {
        HdfsApi hdfsApi = createNiceMock(HdfsApi.class);
        hdfsApi.copy(eq("/tmp/script.pig"), startsWith("/tmp/.pigjobs/"));
        ByteArrayOutputStream do_stream = new ByteArrayOutputStream();
        FSDataOutputStream stream = new FSDataOutputStream(do_stream, null);
        expect(hdfsApi.create(anyString(), eq(true))).andReturn(stream);
        replay(hdfsApi);
        UserLocalObjects.setHdfsApi(hdfsApi, context);
        TempletonApi api = createNiceMock(TempletonApi.class);
        UserLocalObjects.setTempletonApi(api, context);
        TempletonApi.JobData data = api.new JobData();
        // Templeton returns 500 e.g.
        expect(api.runPigQuery(((File) (anyObject())), anyString(), eq("-useHCatalog"))).andThrow(new IOException());
        replay(api);
        thrown.expect(ServiceFormattedException.class);
        JobTest.doCreateJob("Test", "/tmp/script.pig", "-useHCatalog", jobService);
    }

    @Test
    public void testKillJobNoRemove() throws Exception {
        HdfsApi hdfsApi = createNiceMock(HdfsApi.class);
        hdfsApi.copy(eq("/tmp/script.pig"), startsWith("/tmp/.pigjobs/"));
        ByteArrayOutputStream do_stream = new ByteArrayOutputStream();
        FSDataOutputStream stream = new FSDataOutputStream(do_stream, null);
        expect(hdfsApi.create(anyString(), eq(true))).andReturn(stream);
        replay(hdfsApi);
        UserLocalObjects.setHdfsApi(hdfsApi, context);
        TempletonApi api = createStrictMock(TempletonApi.class);
        UserLocalObjects.setTempletonApi(api, context);
        TempletonApi.JobData data = api.new JobData();
        data.id = "job_id_##";
        expect(api.runPigQuery(((File) (anyObject())), anyString(), eq("-useHCatalog"))).andReturn(data);
        replay(api);
        Response response = JobTest.doCreateJob("Test", "/tmp/script.pig", "-useHCatalog", jobService);
        Assert.assertEquals(201, response.getStatus());
        reset(api);
        api.killJob(eq("job_id_##"));
        expect(api.checkJob(anyString())).andReturn(api.new JobInfo()).anyTimes();
        replay(api);
        JSONObject obj = ((JSONObject) (response.getEntity()));
        PigJob job = ((PigJob) (obj.get("job")));
        response = jobService.killJob(job.getId(), null);
        Assert.assertEquals(204, response.getStatus());
        response = jobService.getJob(job.getId());// it should still be present in DB

        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void testKillJobWithRemove() throws Exception {
        HdfsApi hdfsApi = createNiceMock(HdfsApi.class);
        hdfsApi.copy(eq("/tmp/script.pig"), startsWith("/tmp/.pigjobs/"));
        ByteArrayOutputStream do_stream = new ByteArrayOutputStream();
        FSDataOutputStream stream = new FSDataOutputStream(do_stream, null);
        expect(hdfsApi.create(anyString(), eq(true))).andReturn(stream);
        replay(hdfsApi);
        UserLocalObjects.setHdfsApi(hdfsApi, context);
        TempletonApi api = createStrictMock(TempletonApi.class);
        UserLocalObjects.setTempletonApi(api, context);
        TempletonApi.JobData data = api.new JobData();
        data.id = "job_id_##";
        expect(api.runPigQuery(((File) (anyObject())), anyString(), eq("-useHCatalog"))).andReturn(data);
        replay(api);
        Response response = JobTest.doCreateJob("Test", "/tmp/script.pig", "-useHCatalog", jobService);
        Assert.assertEquals(201, response.getStatus());
        reset(api);
        api.killJob(eq("job_id_##"));
        expect(api.checkJob(anyString())).andReturn(api.new JobInfo()).anyTimes();
        replay(api);
        JSONObject obj = ((JSONObject) (response.getEntity()));
        PigJob job = ((PigJob) (obj.get("job")));
        response = jobService.killJob(job.getId(), "true");
        Assert.assertEquals(204, response.getStatus());
        thrown.expect(NotFoundFormattedException.class);// it should not be present in DB

        jobService.getJob(job.getId());
    }

    @Test
    public void testJobStatusFlow() throws Exception {
        HdfsApi hdfsApi = createNiceMock(HdfsApi.class);
        hdfsApi.copy(eq("/tmp/script.pig"), startsWith("/tmp/.pigjobs/"));
        ByteArrayOutputStream do_stream = new ByteArrayOutputStream();
        FSDataOutputStream stream = new FSDataOutputStream(do_stream, null);
        expect(hdfsApi.create(anyString(), eq(true))).andReturn(stream);
        replay(hdfsApi);
        UserLocalObjects.setHdfsApi(hdfsApi, context);
        TempletonApi api = createNiceMock(TempletonApi.class);
        UserLocalObjects.setTempletonApi(api, context);
        TempletonApi.JobData data = api.new JobData();
        data.id = "job_id_#";
        expect(api.runPigQuery(((File) (anyObject())), anyString(), eq("-useHCatalog"))).andReturn(data);
        replay(api);
        Response response = JobTest.doCreateJob("Test", "/tmp/script.pig", "-useHCatalog", jobService);
        Assert.assertEquals("-useHCatalog", do_stream.toString());
        Assert.assertEquals(201, response.getStatus());
        PigJob job = ((PigJob) (get("job")));
        Assert.assertEquals(PIG_JOB_STATE_SUBMITTED, job.getStatus());
        Assert.assertTrue(job.isInProgress());
        // Retrieve status:
        // SUBMITTED
        reset(api);
        TempletonApi.JobInfo info = api.new JobInfo();
        expect(api.checkJob(eq("job_id_#"))).andReturn(info);
        replay(api);
        response = jobService.getJob(job.getId());
        Assert.assertEquals(200, response.getStatus());
        job = ((PigJob) (get("job")));
        Assert.assertEquals(PIG_JOB_STATE_SUBMITTED, job.getStatus());
        // RUNNING
        reset(api);
        info = api.new JobInfo();
        info.status = new HashMap<String, Object>();
        info.status.put("runState", ((double) (RUN_STATE_RUNNING)));
        info.percentComplete = "30% complete";
        expect(api.checkJob(eq("job_id_#"))).andReturn(info);
        replay(api);
        response = jobService.getJob(job.getId());
        Assert.assertEquals(200, response.getStatus());
        job = ((PigJob) (get("job")));
        Assert.assertEquals(PIG_JOB_STATE_RUNNING, job.getStatus());
        Assert.assertTrue(job.isInProgress());
        Assert.assertEquals(30, ((Object) (job.getPercentComplete())));
        // SUCCEED
        reset(api);
        info = api.new JobInfo();
        info.status = new HashMap<String, Object>();
        info.status.put("runState", ((double) (RUN_STATE_SUCCEEDED)));
        expect(api.checkJob(eq("job_id_#"))).andReturn(info);
        replay(api);
        response = jobService.getJob(job.getId());
        Assert.assertEquals(200, response.getStatus());
        job = ((PigJob) (get("job")));
        Assert.assertEquals(PIG_JOB_STATE_COMPLETED, job.getStatus());
        Assert.assertFalse(job.isInProgress());
        Assert.assertNull(job.getPercentComplete());
        // PREP
        reset(api);
        info = api.new JobInfo();
        info.status = new HashMap<String, Object>();
        info.status.put("runState", ((double) (RUN_STATE_PREP)));
        expect(api.checkJob(eq("job_id_#"))).andReturn(info);
        replay(api);
        response = jobService.getJob(job.getId());
        Assert.assertEquals(200, response.getStatus());
        job = ((PigJob) (get("job")));
        Assert.assertEquals(PIG_JOB_STATE_RUNNING, job.getStatus());
        // FAILED
        reset(api);
        info = api.new JobInfo();
        info.status = new HashMap<String, Object>();
        info.status.put("runState", ((double) (RUN_STATE_FAILED)));
        expect(api.checkJob(eq("job_id_#"))).andReturn(info);
        replay(api);
        response = jobService.getJob(job.getId());
        Assert.assertEquals(200, response.getStatus());
        job = ((PigJob) (get("job")));
        Assert.assertEquals(PIG_JOB_STATE_FAILED, job.getStatus());
        Assert.assertFalse(job.isInProgress());
        // KILLED
        reset(api);
        info = api.new JobInfo();
        info.status = new HashMap<String, Object>();
        info.status.put("runState", ((double) (RUN_STATE_KILLED)));
        expect(api.checkJob(eq("job_id_#"))).andReturn(info);
        replay(api);
        response = jobService.getJob(job.getId());
        Assert.assertEquals(200, response.getStatus());
        job = ((PigJob) (get("job")));
        Assert.assertEquals(PIG_JOB_STATE_KILLED, job.getStatus());
        Assert.assertFalse(job.isInProgress());
    }
}

