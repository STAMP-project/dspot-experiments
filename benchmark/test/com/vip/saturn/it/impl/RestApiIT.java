package com.vip.saturn.it.impl;


import com.google.gson.Gson;
import com.vip.saturn.it.base.AbstractSaturnIT;
import com.vip.saturn.it.utils.HttpClientUtils;
import java.io.IOException;
import java.util.Map;
import org.assertj.core.util.Maps;
import org.junit.Assert;
import org.junit.Test;


public class RestApiIT extends AbstractSaturnIT {
    private final Gson gson = new Gson();

    private static String CONSOLE_HOST_URL;

    private static String BASE_URL;

    private static final String PATH_SEPARATOR = "/";

    @Test
    public void testCreateAndQueryJobSuccessfully() throws Exception {
        // create
        String jobName = "testCreateAndQueryJobSuccessfully";
        RestApiIT.JobEntity jobEntity = constructJobEntity(jobName);
        HttpClientUtils.ResponseEntity responseEntity = HttpClientUtils.sendPostRequestJson(RestApiIT.BASE_URL, jobEntity.toJSON());
        Assert.assertEquals(201, responseEntity.getStatusCode());
        // query
        responseEntity = HttpClientUtils.sendGetRequestJson((((RestApiIT.BASE_URL) + "/") + jobName));
        Assert.assertEquals(200, responseEntity.getStatusCode());
        RestApiIT.JobEntity responseJobEntity = gson.fromJson(responseEntity.getEntity(), RestApiIT.JobEntity.class);
        // assert for details
        Assert.assertEquals(jobName, responseJobEntity.getJobName());
        Assert.assertEquals(("this is a description of " + jobName), responseJobEntity.getDescription());
        Assert.assertEquals("0 */1 * * * ?", responseJobEntity.getJobConfig().get("cron"));
        Assert.assertEquals("SHELL_JOB", responseJobEntity.getJobConfig().get("jobType"));
        Assert.assertEquals(2.0, responseJobEntity.getJobConfig().get("shardingTotalCount"));
        Assert.assertEquals("0=echo 0;sleep $SLEEP_SECS,1=echo 1", responseJobEntity.getJobConfig().get("shardingItemParameters"));
        removeJob(jobName);
    }

    @Test
    public void testCreateJobFailAsJobAlreadyExisted() throws Exception {
        String jobName = "testCreateJobFailAsJobAlreadyExisted";
        RestApiIT.JobEntity jobEntity = constructJobEntity(jobName);
        HttpClientUtils.ResponseEntity responseEntity = HttpClientUtils.sendPostRequestJson(RestApiIT.BASE_URL, jobEntity.toJSON());
        Assert.assertEquals(201, responseEntity.getStatusCode());
        responseEntity = HttpClientUtils.sendPostRequestJson(RestApiIT.BASE_URL, jobEntity.toJSON());
        Assert.assertEquals(400, responseEntity.getStatusCode());
        Map<String, String> responseMap = gson.fromJson(responseEntity.getEntity(), Map.class);
        Assert.assertEquals((("???(" + jobName) + ")????"), responseMap.get("message"));
        removeJob(jobName);
    }

    @Test
    public void testCreateJobFailAsNamespaceNotExisted() throws Exception {
        String jobName = "testCreateJobFailAsNamespaceNotExisted";
        RestApiIT.JobEntity jobEntity = constructJobEntity(jobName);
        HttpClientUtils.ResponseEntity responseEntity = HttpClientUtils.sendPostRequestJson(((RestApiIT.CONSOLE_HOST_URL) + "/rest/v1/unknown/jobs"), jobEntity.toJSON());
        Assert.assertEquals(404, responseEntity.getStatusCode());
        Map<String, String> responseMap = gson.fromJson(responseEntity.getEntity(), Map.class);
        Assert.assertEquals("The namespace {unknown} does not exists.", responseMap.get("message"));
    }

    @Test
    public void testCreateJobFailAsCronIsNotFilled() throws Exception {
        String jobName = "testCreateJobFailAsCronIsNotFilled";
        RestApiIT.JobEntity jobEntity = constructJobEntity(jobName);
        jobEntity.getJobConfig().remove("cron");
        HttpClientUtils.ResponseEntity responseEntity = HttpClientUtils.sendPostRequestJson(RestApiIT.BASE_URL, jobEntity.toJSON());
        Assert.assertEquals(400, responseEntity.getStatusCode());
        Map<String, String> responseMap = gson.fromJson(responseEntity.getEntity(), Map.class);
        Assert.assertEquals("??cron???cron?????", responseMap.get("message"));
    }

    @Test
    public void testQueryJobFailAsJobIsNotFound() throws IOException {
        HttpClientUtils.ResponseEntity responseEntity = HttpClientUtils.sendGetRequestJson(((RestApiIT.BASE_URL) + "/not_existed"));
        Assert.assertEquals(404, responseEntity.getStatusCode());
        Map<String, String> responseMap = gson.fromJson(responseEntity.getEntity(), Map.class);
        Assert.assertEquals("The job {not_existed} does not exists.", responseMap.get("message"));
    }

    @Test
    public void testDeleteJobSuccessful() throws Exception {
        String jobName = "testDeleteJobSuccessful";
        RestApiIT.JobEntity jobEntity = constructJobEntity(jobName);
        createJob(jobEntity);
        Thread.sleep((((2 * 60) * 1000L) + 1));
        HttpClientUtils.ResponseEntity responseEntity = HttpClientUtils.sendDeleteResponseJson((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName));
        Assert.assertEquals(204, responseEntity.getStatusCode());
    }

    @Test
    public void testDeleteJobFailAsJobIsCreatedIn2Minutes() throws IOException {
        AbstractSaturnIT.resetJOB_CAN_BE_DELETE_TIME_LIMIT();
        try {
            String jobName = "testDeleteJobFailAsJobIsCreatedIn2Minutes";
            // create a job
            RestApiIT.JobEntity jobEntity = constructJobEntity(jobName);
            HttpClientUtils.ResponseEntity responseEntity = HttpClientUtils.sendPostRequestJson(RestApiIT.BASE_URL, jobEntity.toJSON());
            Assert.assertEquals(201, responseEntity.getStatusCode());
            // and delete it
            responseEntity = HttpClientUtils.sendDeleteResponseJson((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName));
            Assert.assertEquals(400, responseEntity.getStatusCode());
            Map<String, String> responseMap = gson.fromJson(responseEntity.getEntity(), Map.class);
            Assert.assertEquals((("???????(" + jobName) + ")?????????????????2??"), responseMap.get("message"));
        } finally {
            AbstractSaturnIT.setJOB_CAN_BE_DELETE_TIME_LIMIT();
        }
    }

    @Test
    public void testEnableAndDisableJobSuccessfully() throws Exception {
        // create
        String jobName = "testEnableAndDisableJobSuccessfully";
        RestApiIT.JobEntity jobEntity = constructJobEntity(jobName);
        HttpClientUtils.ResponseEntity responseEntity = HttpClientUtils.sendPostRequestJson(RestApiIT.BASE_URL, jobEntity.toJSON());
        Assert.assertEquals(201, responseEntity.getStatusCode());
        // sleep for 10 seconds
        Thread.sleep(10100L);
        // enable
        responseEntity = HttpClientUtils.sendPostRequestJson(((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName) + "/enable"), jobEntity.toJSON());
        Assert.assertEquals(200, responseEntity.getStatusCode());
        // query for status
        responseEntity = HttpClientUtils.sendGetRequestJson((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName));
        Assert.assertEquals(200, responseEntity.getStatusCode());
        RestApiIT.JobEntity responseJobEntity = gson.fromJson(responseEntity.getEntity(), RestApiIT.JobEntity.class);
        Assert.assertEquals("READY", responseJobEntity.getRunningStatus());
        // enable again
        responseEntity = HttpClientUtils.sendPostRequestJson(((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName) + "/enable"), jobEntity.toJSON());
        Assert.assertEquals(201, responseEntity.getStatusCode());
        // query for status
        responseEntity = HttpClientUtils.sendGetRequestJson((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName));
        Assert.assertEquals(200, responseEntity.getStatusCode());
        responseJobEntity = gson.fromJson(responseEntity.getEntity(), RestApiIT.JobEntity.class);
        Assert.assertEquals("READY", responseJobEntity.getRunningStatus());
        // sleep for 3 seconds
        Thread.sleep(3010L);
        // disable
        responseEntity = HttpClientUtils.sendPostRequestJson(((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName) + "/disable"), jobEntity.toJSON());
        Assert.assertEquals(200, responseEntity.getStatusCode());
        // query for status
        responseEntity = HttpClientUtils.sendGetRequestJson((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName));
        Assert.assertEquals(200, responseEntity.getStatusCode());
        responseJobEntity = gson.fromJson(responseEntity.getEntity(), RestApiIT.JobEntity.class);
        Assert.assertEquals("STOPPED", responseJobEntity.getRunningStatus());
        // disable again
        responseEntity = HttpClientUtils.sendPostRequestJson(((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName) + "/disable"), jobEntity.toJSON());
        Assert.assertEquals(201, responseEntity.getStatusCode());
        // query for status
        responseEntity = HttpClientUtils.sendGetRequestJson((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName));
        Assert.assertEquals(200, responseEntity.getStatusCode());
        responseJobEntity = gson.fromJson(responseEntity.getEntity(), RestApiIT.JobEntity.class);
        Assert.assertEquals("STOPPED", responseJobEntity.getRunningStatus());
        removeJob(jobName);
    }

    @Test
    public void testEnabledJobFailAsCreationLessThan10() throws Exception {
        // create
        String jobName = "testEnabledJobFailAsCreationLessThan10";
        RestApiIT.JobEntity jobEntity = constructJobEntity(jobName);
        createJob(jobEntity);
        Thread.sleep(6001L);
        // enabled job
        HttpClientUtils.ResponseEntity responseEntity = HttpClientUtils.sendPostRequestJson(((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName) + "/enable"), jobEntity.toJSON());
        Assert.assertEquals(403, responseEntity.getStatusCode());
        Assert.assertEquals("Cannot enable the job until 10 seconds after job creation!", gson.fromJson(responseEntity.getEntity(), Map.class).get("message"));
        removeJob(jobName);
    }

    @Test
    public void testEnabledAndDisabledJobFailAsIntervalLessThan3() throws Exception {
        // create
        String jobName = "testEnabledAndDisabledJobFailAsIntervalLessThan3";
        RestApiIT.JobEntity jobEntity = constructJobEntity(jobName);
        createJob(jobEntity);
        Thread.sleep(10001L);
        // enabled job
        HttpClientUtils.ResponseEntity responseEntity = HttpClientUtils.sendPostRequestJson(((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName) + "/enable"), jobEntity.toJSON());
        Assert.assertEquals(200, responseEntity.getStatusCode());
        // disabled job less than 3 seconds
        responseEntity = HttpClientUtils.sendPostRequestJson(((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName) + "/disable"), jobEntity.toJSON());
        Assert.assertEquals(403, responseEntity.getStatusCode());
        Assert.assertEquals("The update interval time cannot less than 3 seconds", gson.fromJson(responseEntity.getEntity(), Map.class).get("message"));
        disableJob(jobName);
        Thread.sleep(1000);
        removeJob(jobName);
    }

    @Test
    public void testDisableAndEnabledJobFailAsIntervalLessThan3() throws Exception {
        // create
        String jobName = "testDisableAndEnabledJobFailAsIntervalLessThan3";
        RestApiIT.JobEntity jobEntity = constructJobEntity(jobName);
        createJob(jobEntity);
        Thread.sleep(10001L);
        // enabled job
        HttpClientUtils.ResponseEntity responseEntity = HttpClientUtils.sendPostRequestJson(((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName) + "/enable"), jobEntity.toJSON());
        Assert.assertEquals(200, responseEntity.getStatusCode());
        Thread.sleep(3001L);
        // disabled job after 3 seconds
        responseEntity = HttpClientUtils.sendPostRequestJson(((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName) + "/disable"), jobEntity.toJSON());
        Assert.assertEquals(200, responseEntity.getStatusCode());
        // enabled less than 3 seconds
        responseEntity = HttpClientUtils.sendPostRequestJson(((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName) + "/enable"), jobEntity.toJSON());
        Assert.assertEquals(403, responseEntity.getStatusCode());
        Assert.assertEquals("The update interval time cannot less than 3 seconds", gson.fromJson(responseEntity.getEntity(), Map.class).get("message"));
        removeJob(jobName);
    }

    @Test
    public void testRunJobSuccessful() throws Exception {
        AbstractSaturnIT.startExecutorList(1);
        String jobName = "testRunJobSuccessful";
        RestApiIT.JobEntity jobEntity = constructJobEntity(jobName);
        createJob(jobEntity);
        Thread.sleep(10001L);
        HttpClientUtils.ResponseEntity responseEntity = HttpClientUtils.sendPostRequestJson(((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName) + "/enable"), jobEntity.toJSON());
        Assert.assertEquals(200, responseEntity.getStatusCode());
        responseEntity = HttpClientUtils.sendPostRequestJson(((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName) + "/run"), "");
        Assert.assertEquals(204, responseEntity.getStatusCode());
        disableJob(jobName);
        Thread.sleep(1000);
        removeJob(jobName);
    }

    @Test
    public void testRunJobFailAsStatusNotReady() throws Exception {
        String jobName = "testRunJobFailAsStatusNotReady";
        RestApiIT.JobEntity jobEntity = constructJobEntity(jobName);
        createJob(jobEntity);
        HttpClientUtils.ResponseEntity responseEntity = HttpClientUtils.sendPostRequestJson(((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName) + "/run"), "");
        Assert.assertEquals(400, responseEntity.getStatusCode());
        Assert.assertEquals("job's status is not {READY}", gson.fromJson(responseEntity.getEntity(), Map.class).get("message"));
        removeJob(jobName);
    }

    @Test
    public void testRunJobFailAsNoExecutor() throws Exception {
        String jobName = "testRunJobFailAsNoExecutor";
        RestApiIT.JobEntity jobEntity = constructJobEntity(jobName);
        createJob(jobEntity);
        Thread.sleep(10001L);
        HttpClientUtils.ResponseEntity responseEntity = HttpClientUtils.sendPostRequestJson(((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName) + "/enable"), jobEntity.toJSON());
        Assert.assertEquals(200, responseEntity.getStatusCode());
        responseEntity = HttpClientUtils.sendPostRequestJson(((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName) + "/run"), "");
        Assert.assertEquals(400, responseEntity.getStatusCode());
        Assert.assertEquals("no executor found for this job", gson.fromJson(responseEntity.getEntity(), Map.class).get("message"));
        disableJob(jobName);
        Thread.sleep(1000);
        removeJob(jobName);
    }

    @Test
    public void testStopJobFailAsStatusIsReady() throws Exception {
        String jobName = "testStopJobFailAsStatusIsReady";
        RestApiIT.JobEntity jobEntity = constructJobEntity(jobName);
        createJob(jobEntity);
        Thread.sleep(10001L);
        HttpClientUtils.ResponseEntity responseEntity = HttpClientUtils.sendPostRequestJson(((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName) + "/enable"), jobEntity.toJSON());
        Assert.assertEquals(200, responseEntity.getStatusCode());
        responseEntity = HttpClientUtils.sendPostRequestJson(((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName) + "/stop"), "");
        Assert.assertEquals(400, responseEntity.getStatusCode());
        Assert.assertEquals("job cannot be stopped while its status is READY or RUNNING", gson.fromJson(responseEntity.getEntity(), Map.class).get("message"));
        disableJob(jobName);
        Thread.sleep(1000);
        removeJob(jobName);
    }

    @Test
    public void testRaiseExecutorRestartAlarmSuccessfully() throws IOException {
        Map<String, Object> requestBody = Maps.newHashMap();
        requestBody.put("executorName", "exec_1");
        requestBody.put("level", "Critical");
        requestBody.put("title", "Executor_Restart");
        requestBody.put("name", "Saturn Event");
        HttpClientUtils.ResponseEntity responseEntity = HttpClientUtils.sendPostRequestJson(((RestApiIT.CONSOLE_HOST_URL) + "/rest/v1/it-saturn/alarms/raise"), gson.toJson(requestBody));
        Assert.assertEquals(201, responseEntity.getStatusCode());
    }

    @Test
    public void testUpdateCronSuccessfully() throws Exception {
        String jobName = "testUpdateCronSuccessfully";
        // create
        RestApiIT.JobEntity jobEntity = constructJobEntity(jobName);
        HttpClientUtils.ResponseEntity responseEntity = HttpClientUtils.sendPostRequestJson(RestApiIT.BASE_URL, jobEntity.toJSON());
        Assert.assertEquals(201, responseEntity.getStatusCode());
        // sleep for a while ...
        Thread.sleep(3010L);
        // update cron
        Map<String, Object> requestBody = Maps.newHashMap();
        requestBody.put("cron", "0 0/11 * * * ?");
        responseEntity = HttpClientUtils.sendPutRequestJson(((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName) + "/cron"), gson.toJson(requestBody));
        System.out.println(responseEntity.getEntity());
        Assert.assertEquals(200, responseEntity.getStatusCode());
        // query again
        responseEntity = HttpClientUtils.sendGetRequestJson((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName));
        Assert.assertEquals(200, responseEntity.getStatusCode());
        RestApiIT.JobEntity responseJobEntity = gson.fromJson(responseEntity.getEntity(), RestApiIT.JobEntity.class);
        Assert.assertEquals("0 0/11 * * * ?", responseJobEntity.getJobConfig().get("cron"));
        removeJob(jobName);
    }

    @Test
    public void testUpdateCronFailAsCronInvalid() throws Exception {
        String jobName = "testUpdateCronFailAsCronInvalid";
        // create
        RestApiIT.JobEntity jobEntity = constructJobEntity(jobName);
        HttpClientUtils.ResponseEntity responseEntity = HttpClientUtils.sendPostRequestJson(RestApiIT.BASE_URL, jobEntity.toJSON());
        Assert.assertEquals(201, responseEntity.getStatusCode());
        // sleep for a while ...
        Thread.sleep(3010L);
        // update cron
        Map<String, Object> requestBody = Maps.newHashMap();
        requestBody.put("cron", "abc");
        responseEntity = HttpClientUtils.sendPutRequestJson(((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName) + "/cron"), gson.toJson(requestBody));
        System.out.println(responseEntity.getEntity());
        Assert.assertEquals(400, responseEntity.getStatusCode());
        Assert.assertEquals("The cron expression is invalid: abc", gson.fromJson(responseEntity.getEntity(), Map.class).get("message"));
        removeJob(jobName);
    }

    @Test
    public void testUpdateCronFailAsJobNotExists() throws Exception {
        // update cron
        Map<String, Object> requestBody = Maps.newHashMap();
        requestBody.put("cron", "abc");
        HttpClientUtils.ResponseEntity responseEntity = HttpClientUtils.sendPutRequestJson(((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + "unknown") + "/cron"), gson.toJson(requestBody));
        System.out.println(responseEntity.getEntity());
        Assert.assertEquals(404, responseEntity.getStatusCode());
        Assert.assertEquals("The job {unknown} does not exists.", gson.fromJson(responseEntity.getEntity(), Map.class).get("message"));
    }

    @Test
    public void testUpdateJobSuccessfully() throws Exception {
        String jobName = "testUpdateJobSuccessfully";
        RestApiIT.JobEntity jobEntity = constructJobEntity(jobName);
        createJob(jobEntity);
        // ?? update
        HttpClientUtils.ResponseEntity responseEntity = HttpClientUtils.sendPostRequestJson((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName), jobEntity.toJSON());
        Assert.assertEquals(200, responseEntity.getStatusCode());
        removeJob(jobName);
    }

    @Test
    public void testUpdateJobFailAsStatusNotDisabled() throws Exception {
        String jobName = "testUpdateJobFailAsStatusNotDisabled";
        RestApiIT.JobEntity jobEntity = constructJobEntity(jobName);
        createJob(jobEntity);
        Thread.sleep(10001L);
        HttpClientUtils.ResponseEntity responseEntity = HttpClientUtils.sendPostRequestJson(((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName) + "/enable"), jobEntity.toJSON());
        Assert.assertEquals(200, responseEntity.getStatusCode());
        responseEntity = HttpClientUtils.sendPostRequestJson((((RestApiIT.BASE_URL) + (RestApiIT.PATH_SEPARATOR)) + jobName), jobEntity.toJSON());
        Assert.assertEquals(400, responseEntity.getStatusCode());
        Assert.assertEquals("job's status is not {STOPPED}", gson.fromJson(responseEntity.getEntity(), Map.class).get("message"));
        disableJob(jobName);
        Thread.sleep(1000);
        removeJob(jobName);
    }

    public class JobEntity {
        private final Gson gson = new Gson();

        private String jobName;

        private String description;

        private String runningStatus;

        private Map<String, Object> jobConfig = Maps.newHashMap();

        public JobEntity(String jobName) {
            this.jobName = jobName;
        }

        public void setConfig(String key, Object value) {
            jobConfig.put(key, value);
        }

        public Object getConfig(String key) {
            return jobConfig.get(key);
        }

        public String toJSON() {
            return gson.toJson(this);
        }

        public Gson getGson() {
            return gson;
        }

        public String getJobName() {
            return jobName;
        }

        public void setJobName(String jobName) {
            this.jobName = jobName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Map<String, Object> getJobConfig() {
            return jobConfig;
        }

        public void setJobConfig(Map<String, Object> jobConfig) {
            this.jobConfig = jobConfig;
        }

        public String getRunningStatus() {
            return runningStatus;
        }

        public void setRunningStatus(String runningStatus) {
            this.runningStatus = runningStatus;
        }
    }
}

