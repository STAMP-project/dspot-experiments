/**
 * Copyright 2016 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.netflix.genie.web.controllers;


import ContentType.TEXT;
import HttpHeaders.LOCATION;
import HttpStatus.ACCEPTED;
import HttpStatus.NOT_FOUND;
import HttpStatus.OK;
import HttpStatus.PRECONDITION_FAILED;
import JobStatus.FAILED;
import JobStatus.KILLED;
import JobStatusMessages.JOB_FAILED;
import JobStatusMessages.JOB_KILLED_BY_USER;
import MediaType.ALL_VALUE;
import MediaType.APPLICATION_JSON_UTF8_VALUE;
import MediaType.APPLICATION_JSON_VALUE;
import MediaType.TEXT_PLAIN_VALUE;
import MediaTypes.HAL_JSON_UTF8_VALUE;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.netflix.genie.common.dto.ClusterCriteria;
import com.netflix.genie.common.dto.JobRequest;
import com.netflix.genie.common.dto.JobStatusMessages;
import com.netflix.genie.common.internal.util.GenieHostInfo;
import com.netflix.genie.common.util.GenieObjectMapper;
import com.netflix.genie.web.properties.FileCacheProperties;
import com.netflix.genie.web.properties.JobsLocationsProperties;
import io.restassured.RestAssured;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation;
import org.springframework.restdocs.restassured3.RestDocumentationFilter;


/**
 * Integration tests for Jobs REST API.
 *
 * @author amsharma
 * @author tgianos
 * @since 3.0.0
 */
@Slf4j
public class JobRestControllerIntegrationTests extends RestControllerIntegrationTestsBase {
    private static final long SLEEP_TIME = 1000L;

    private static final String SCHEDULER_JOB_NAME_KEY = "schedulerJobName";

    private static final String SCHEDULER_RUN_ID_KEY = "schedulerRunId";

    private static final String COMMAND_ARGS_PATH = "commandArgs";

    private static final String STATUS_MESSAGE_PATH = "statusMsg";

    private static final String CLUSTER_NAME_PATH = "clusterName";

    private static final String COMMAND_NAME_PATH = "commandName";

    private static final String ARCHIVE_LOCATION_PATH = "archiveLocation";

    private static final String STARTED_PATH = "started";

    private static final String FINISHED_PATH = "finished";

    private static final String CLUSTER_CRITERIAS_PATH = "clusterCriterias";

    private static final String COMMAND_CRITERIA_PATH = "commandCriteria";

    private static final String GROUP_PATH = "group";

    private static final String DISABLE_LOG_ARCHIVAL_PATH = "disableLogArchival";

    private static final String EMAIL_PATH = "email";

    private static final String CPU_PATH = "cpu";

    private static final String MEMORY_PATH = "memory";

    private static final String APPLICATIONS_PATH = "applications";

    private static final String HOST_NAME_PATH = "hostName";

    private static final String PROCESS_ID_PATH = "processId";

    private static final String CHECK_DELAY_PATH = "checkDelay";

    private static final String EXIT_CODE_PATH = "exitCode";

    private static final String CLIENT_HOST_PATH = "clientHost";

    private static final String USER_AGENT_PATH = "userAgent";

    private static final String NUM_ATTACHMENTS_PATH = "numAttachments";

    private static final String TOTAL_SIZE_ATTACHMENTS_PATH = "totalSizeOfAttachments";

    private static final String STD_OUT_SIZE_PATH = "stdOutSize";

    private static final String STD_ERR_SIZE_PATH = "stdErrSize";

    private static final String JOBS_LIST_PATH = (RestControllerIntegrationTestsBase.EMBEDDED_PATH) + ".jobSearchResultList";

    private static final String JOB_COMMAND_LINK_PATH = "_links.command.href";

    private static final String JOB_CLUSTER_LINK_PATH = "_links.cluster.href";

    private static final String JOB_APPLICATIONS_LINK_PATH = "_links.applications.href";

    private static final long CHECK_DELAY = 500L;

    private static final String BASE_DIR = "com/netflix/genie/web/controllers/JobRestControllerIntegrationTests/";

    private static final String FILE_DELIMITER = "/";

    private static final String LOCALHOST_CLUSTER_TAG = "localhost";

    private static final String BASH_COMMAND_TAG = "bash";

    private static final String JOB_NAME = "List * ... Directories bash job";

    private static final String JOB_USER = "genie";

    private static final String JOB_VERSION = "1.0";

    private static final String JOB_DESCRIPTION = "Genie 3 Test Job";

    private static final String JOB_STATUS_MSG = JobStatusMessages.JOB_FINISHED_SUCCESSFULLY;

    private static final String APP1_ID = "app1";

    private static final String APP1_NAME = "Application 1";

    private static final String APP1_USER = "genie";

    private static final String APP1_VERSION = "1.0";

    private static final String APP2_ID = "app2";

    private static final String APP2_NAME = "Application 2";

    private static final String CMD1_ID = "cmd1";

    private static final String CMD1_NAME = "Unix Bash command";

    private static final String CMD1_USER = "genie";

    private static final String CMD1_VERSION = "1.0";

    private static final String CMD1_EXECUTABLE = "/bin/bash";

    private static final String CMD1_TAGS = ((((((JobRestControllerIntegrationTests.BASH_COMMAND_TAG) + ",") + "genie.id:") + (JobRestControllerIntegrationTests.CMD1_ID)) + ",") + "genie.name:") + (JobRestControllerIntegrationTests.CMD1_NAME);

    private static final String CLUSTER1_ID = "cluster1";

    private static final String CLUSTER1_NAME = "Local laptop";

    private static final String CLUSTER1_USER = "genie";

    private static final String CLUSTER1_VERSION = "1.0";

    private static final String CLUSTER1_TAGS = ((((("genie.id:" + (JobRestControllerIntegrationTests.CLUSTER1_ID)) + ",") + "genie.name:") + (JobRestControllerIntegrationTests.CLUSTER1_NAME)) + ",") + (JobRestControllerIntegrationTests.LOCALHOST_CLUSTER_TAG);

    // This file is not UTF-8 encoded. It is uploaded to test server behavior
    // related to charset headers
    private static final String GB18030_TXT = "GB18030.txt";

    /**
     * A temporary directory to use that will be cleaned up automatically at the end of testing.
     */
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ResourceLoader resourceLoader;

    private JsonNode metadata;

    private String schedulerJobName;

    private String schedulerRunId;

    @Autowired
    private GenieHostInfo genieHostInfo;

    @Autowired
    private Resource jobDirResource;

    @Autowired
    private FileCacheProperties fileCacheProperties;

    @Autowired
    private JobsLocationsProperties jobsLocationsProperties;

    /**
     * Test the job submit method for success.
     *
     * @throws Exception
     * 		If there is a problem.
     */
    @Test
    public void testSubmitJobMethodSuccess() throws Exception {
        this.submitAndCheckJob(1, true);
    }

    /**
     * Test the job submit method for success twice to validate the file cache use.
     *
     * @throws Exception
     * 		If there is a problem.
     */
    @Test
    public void testSubmitJobMethodTwiceSuccess() throws Exception {
        submitAndCheckJob(2, true);
        cleanup();
        setup();
        submitAndCheckJob(3, false);
    }

    /**
     * Test to make sure we can submit a job with attachments.
     *
     * @throws Exception
     * 		on any error
     */
    @Test
    public void canSubmitJobWithAttachments() throws Exception {
        final List<String> commandArgs = Lists.newArrayList("-c", "'echo hello world'");
        final List<ClusterCriteria> clusterCriteriaList = Lists.newArrayList(new ClusterCriteria(Sets.newHashSet(JobRestControllerIntegrationTests.LOCALHOST_CLUSTER_TAG)));
        final String setUpFile = this.resourceLoader.getResource(((((JobRestControllerIntegrationTests.BASE_DIR) + "job") + (JobRestControllerIntegrationTests.FILE_DELIMITER)) + "jobsetupfile")).getFile().getAbsolutePath();
        final File attachment1File = this.resourceLoader.getResource(((JobRestControllerIntegrationTests.BASE_DIR) + "job/query.sql")).getFile();
        final MockMultipartFile attachment1;
        try (InputStream is = new FileInputStream(attachment1File)) {
            attachment1 = new MockMultipartFile("attachment", attachment1File.getName(), MediaType.APPLICATION_OCTET_STREAM_VALUE, is);
        }
        final File attachment2File = this.resourceLoader.getResource(((JobRestControllerIntegrationTests.BASE_DIR) + "job/query2.sql")).getFile();
        final MockMultipartFile attachment2;
        try (InputStream is = new FileInputStream(attachment2File)) {
            attachment2 = new MockMultipartFile("attachment", attachment2File.getName(), MediaType.APPLICATION_OCTET_STREAM_VALUE, is);
        }
        final Set<String> commandCriteria = Sets.newHashSet(JobRestControllerIntegrationTests.BASH_COMMAND_TAG);
        final JobRequest jobRequest = withCommandArgs(commandArgs).withDisableLogArchival(true).withSetupFile(setUpFile).withDescription(JobRestControllerIntegrationTests.JOB_DESCRIPTION).build();
        this.waitForDone(this.submitJob(4, jobRequest, Lists.newArrayList(attachment1, attachment2)));
    }

    /**
     * Test the job submit method for incorrect cluster resolved.
     *
     * @throws Exception
     * 		If there is a problem.
     */
    @Test
    public void testSubmitJobMethodMissingCluster() throws Exception {
        Assume.assumeTrue(SystemUtils.IS_OS_UNIX);
        final List<String> commandArgs = Lists.newArrayList("-c", "'echo hello world'");
        final List<ClusterCriteria> clusterCriteriaList = new ArrayList<>();
        final Set<String> clusterTags = Sets.newHashSet("undefined");
        final ClusterCriteria clusterCriteria = new ClusterCriteria(clusterTags);
        clusterCriteriaList.add(clusterCriteria);
        final String jobId = UUID.randomUUID().toString();
        final Set<String> commandCriteria = Sets.newHashSet(JobRestControllerIntegrationTests.BASH_COMMAND_TAG);
        final JobRequest jobRequest = withCommandArgs(commandArgs).withDisableLogArchival(true).build();
        RestAssured.given(getRequestSpecification()).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(jobRequest)).when().port(this.port).post(RestControllerIntegrationTestsBase.JOBS_API).then().statusCode(Matchers.is(PRECONDITION_FAILED.value()));
        Assert.assertThat(this.getStatus(jobId), Matchers.is("{\"status\":\"FAILED\"}"));
    }

    /**
     * Test the job submit method for incorrect cluster criteria.
     *
     * @throws Exception
     * 		If there is a problem.
     */
    @Test
    public void testSubmitJobMethodInvalidClusterCriteria() throws Exception {
        Assume.assumeTrue(SystemUtils.IS_OS_UNIX);
        final List<String> commandArgs = Lists.newArrayList("-c", "'echo hello world'");
        final List<ClusterCriteria> clusterCriteriaList = Lists.newArrayList(new ClusterCriteria(Sets.newHashSet(" ", "", null)));
        final String jobId = UUID.randomUUID().toString();
        final Set<String> commandCriteria = Sets.newHashSet("bash");
        final JobRequest jobRequest = withCommandArgs(commandArgs).withDisableLogArchival(true).build();
        RestAssured.given(getRequestSpecification()).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(jobRequest)).when().port(this.port).post(RestControllerIntegrationTestsBase.JOBS_API).then().statusCode(Matchers.is(PRECONDITION_FAILED.value()));
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(((RestControllerIntegrationTestsBase.JOBS_API) + "/{id}/status"), jobId).then().statusCode(Matchers.is(NOT_FOUND.value()));
    }

    /**
     * Test the job submit method for incorrect cluster criteria.
     *
     * @throws Exception
     * 		If there is a problem.
     */
    @Test
    public void testSubmitJobMethodInvalidCommandCriteria() throws Exception {
        Assume.assumeTrue(SystemUtils.IS_OS_UNIX);
        final List<String> commandArgs = Lists.newArrayList("-c", "'echo hello world'");
        final List<ClusterCriteria> clusterCriteriaList = Lists.newArrayList(new ClusterCriteria(Sets.newHashSet("ok")));
        final String jobId = UUID.randomUUID().toString();
        final Set<String> commandCriteria = Sets.newHashSet(" ", "", null);
        final JobRequest jobRequest = withCommandArgs(commandArgs).withDisableLogArchival(true).build();
        RestAssured.given(getRequestSpecification()).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(jobRequest)).when().port(this.port).post(RestControllerIntegrationTestsBase.JOBS_API).then().statusCode(Matchers.is(PRECONDITION_FAILED.value()));
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(((RestControllerIntegrationTestsBase.JOBS_API) + "/{id}/status"), jobId).then().statusCode(Matchers.is(NOT_FOUND.value()));
    }

    /**
     * Test the job submit method for incorrect command resolved.
     *
     * @throws Exception
     * 		If there is a problem.
     */
    @Test
    public void testSubmitJobMethodMissingCommand() throws Exception {
        Assume.assumeTrue(SystemUtils.IS_OS_UNIX);
        final List<String> commandArgs = Lists.newArrayList("-c", "'echo hello world'");
        final List<ClusterCriteria> clusterCriteriaList = new ArrayList<>();
        final Set<String> clusterTags = Sets.newHashSet(JobRestControllerIntegrationTests.LOCALHOST_CLUSTER_TAG);
        final ClusterCriteria clusterCriteria = new ClusterCriteria(clusterTags);
        clusterCriteriaList.add(clusterCriteria);
        final String jobId = UUID.randomUUID().toString();
        final Set<String> commandCriteria = Sets.newHashSet("undefined");
        final JobRequest jobRequest = withCommandArgs(commandArgs).withDisableLogArchival(true).build();
        RestAssured.given(getRequestSpecification()).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(jobRequest)).when().port(this.port).post(RestControllerIntegrationTestsBase.JOBS_API).then().statusCode(Matchers.is(PRECONDITION_FAILED.value()));
        Assert.assertThat(this.getStatus(jobId), Matchers.is("{\"status\":\"FAILED\"}"));
    }

    /**
     * Test the job submit method for when the job is killed by sending a DELETE HTTP call.
     *
     * @throws Exception
     * 		If there is a problem.
     */
    @Test
    public void testSubmitJobMethodKill() throws Exception {
        Assume.assumeTrue(SystemUtils.IS_OS_UNIX);
        final List<String> commandArgs = Lists.newArrayList("-c", "'sleep 60'");
        final List<ClusterCriteria> clusterCriteriaList = new ArrayList<>();
        final Set<String> clusterTags = Sets.newHashSet(JobRestControllerIntegrationTests.LOCALHOST_CLUSTER_TAG);
        final ClusterCriteria clusterCriteria = new ClusterCriteria(clusterTags);
        clusterCriteriaList.add(clusterCriteria);
        final Set<String> commandCriteria = Sets.newHashSet(JobRestControllerIntegrationTests.BASH_COMMAND_TAG);
        final JobRequest jobRequest = withCommandArgs(commandArgs).withDisableLogArchival(true).build();
        final String jobId = this.getIdFromLocation(RestAssured.given(getRequestSpecification()).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(jobRequest)).when().port(this.port).post(RestControllerIntegrationTestsBase.JOBS_API).then().statusCode(Matchers.is(ACCEPTED.value())).header(LOCATION, Matchers.notNullValue()).extract().header(LOCATION));
        this.waitForRunning(jobId);
        // Make sure we can get output for a running job
        RestAssured.given(getRequestSpecification()).accept(APPLICATION_JSON_VALUE).when().port(this.port).get(((RestControllerIntegrationTestsBase.JOBS_API) + "/{id}/output/{filePath}"), jobId, "").then().statusCode(Matchers.is(OK.value())).contentType(Matchers.equalToIgnoringCase(APPLICATION_JSON_UTF8_VALUE)).body("parent", Matchers.isEmptyOrNullString()).body("directories[0].name", Matchers.is("genie/")).body("files[0].name", Matchers.is("run")).body("files[1].name", Matchers.is("stderr")).body("files[2].name", Matchers.is("stdout"));
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(((RestControllerIntegrationTestsBase.JOBS_API) + "/{id}/output/{filePath}"), jobId, "stdout").then().statusCode(Matchers.is(OK.value())).contentType(Matchers.containsString(TEXT.toString()));
        // Let it run for a couple of seconds
        Thread.sleep(2000);
        // Send a kill request to the job.
        final RestDocumentationFilter killResultFilter = RestAssuredRestDocumentation.document("{class-name}/killJob/", Snippets.ID_PATH_PARAM);
        RestAssured.given(getRequestSpecification()).filter(killResultFilter).when().port(this.port).delete(((RestControllerIntegrationTestsBase.JOBS_API) + "/{id}"), jobId).then().statusCode(Matchers.is(ACCEPTED.value()));
        this.waitForDone(jobId);
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(((RestControllerIntegrationTestsBase.JOBS_API) + "/{id}"), jobId).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.is(HAL_JSON_UTF8_VALUE)).body(RestControllerIntegrationTestsBase.ID_PATH, Matchers.is(jobId)).body(RestControllerIntegrationTestsBase.STATUS_PATH, Matchers.is(KILLED.toString())).body(JobRestControllerIntegrationTests.STATUS_MESSAGE_PATH, Matchers.is(JOB_KILLED_BY_USER));
        // Kill the job again to make sure it doesn't cause a problem.
        RestAssured.given(getRequestSpecification()).filter(killResultFilter).when().port(this.port).delete(((RestControllerIntegrationTestsBase.JOBS_API) + "/{id}"), jobId).then().statusCode(Matchers.is(ACCEPTED.value()));
    }

    /**
     * Test the job submit method for when the job is killed as it times out.
     *
     * @throws Exception
     * 		If there is a problem.
     */
    @Test
    public void testSubmitJobMethodKillOnTimeout() throws Exception {
        Assume.assumeTrue(SystemUtils.IS_OS_UNIX);
        final List<String> commandArgs = Lists.newArrayList("-c", "'sleep 60'");
        final List<ClusterCriteria> clusterCriteriaList = new ArrayList<>();
        final Set<String> clusterTags = Sets.newHashSet(JobRestControllerIntegrationTests.LOCALHOST_CLUSTER_TAG);
        final ClusterCriteria clusterCriteria = new ClusterCriteria(clusterTags);
        clusterCriteriaList.add(clusterCriteria);
        final Set<String> commandCriteria = Sets.newHashSet(JobRestControllerIntegrationTests.BASH_COMMAND_TAG);
        final JobRequest jobRequest = withCommandArgs(commandArgs).withTimeout(5).withDisableLogArchival(true).build();
        final String id = this.getIdFromLocation(RestAssured.given(getRequestSpecification()).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(jobRequest)).when().port(this.port).post(RestControllerIntegrationTestsBase.JOBS_API).then().statusCode(Matchers.is(ACCEPTED.value())).header(LOCATION, Matchers.notNullValue()).extract().header(LOCATION));
        this.waitForDone(id);
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(((RestControllerIntegrationTestsBase.JOBS_API) + "/{id}"), id).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.is(HAL_JSON_UTF8_VALUE)).body(RestControllerIntegrationTestsBase.ID_PATH, Matchers.is(id)).body(RestControllerIntegrationTestsBase.STATUS_PATH, Matchers.is(KILLED.toString())).body(JobRestControllerIntegrationTests.STATUS_MESSAGE_PATH, Matchers.is("Job exceeded timeout."));
    }

    /**
     * Test the job submit method for when the job fails.
     *
     * @throws Exception
     * 		If there is a problem.
     */
    @Test
    public void testSubmitJobMethodFailure() throws Exception {
        Assume.assumeTrue(SystemUtils.IS_OS_UNIX);
        final List<String> commandArgs = Lists.newArrayList("-c", "'exit 1'");
        final List<ClusterCriteria> clusterCriteriaList = new ArrayList<>();
        final Set<String> clusterTags = Sets.newHashSet(JobRestControllerIntegrationTests.LOCALHOST_CLUSTER_TAG);
        final ClusterCriteria clusterCriteria = new ClusterCriteria(clusterTags);
        clusterCriteriaList.add(clusterCriteria);
        final Set<String> commandCriteria = Sets.newHashSet(JobRestControllerIntegrationTests.BASH_COMMAND_TAG);
        final JobRequest jobRequest = withCommandArgs(commandArgs).withDisableLogArchival(true).build();
        final String id = this.getIdFromLocation(RestAssured.given(getRequestSpecification()).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(jobRequest)).when().port(this.port).post(RestControllerIntegrationTestsBase.JOBS_API).then().statusCode(Matchers.is(ACCEPTED.value())).header(LOCATION, Matchers.notNullValue()).extract().header(LOCATION));
        this.waitForDone(id);
        Assert.assertEquals(this.getStatus(id), "{\"status\":\"FAILED\"}");
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(((RestControllerIntegrationTestsBase.JOBS_API) + "/{id}"), id).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.is(HAL_JSON_UTF8_VALUE)).body(RestControllerIntegrationTestsBase.ID_PATH, Matchers.is(id)).body(RestControllerIntegrationTestsBase.STATUS_PATH, Matchers.is(FAILED.toString())).body(JobRestControllerIntegrationTests.STATUS_MESSAGE_PATH, Matchers.is(JOB_FAILED));
    }

    /**
     * Test the response content types to ensure UTF-8.
     *
     * @throws Exception
     * 		If there is a problem.
     */
    @Test
    public void testResponseContentType() throws Exception {
        Assume.assumeTrue(SystemUtils.IS_OS_UNIX);
        final List<String> commandArgs = Lists.newArrayList("-c", "'echo hello'");
        final JobRequest jobRequest = withCommandArgs(commandArgs).build();
        final String jobId = this.getIdFromLocation(RestAssured.given(getRequestSpecification()).contentType(APPLICATION_JSON_VALUE).body(GenieObjectMapper.getMapper().writeValueAsBytes(jobRequest)).when().port(this.port).post(RestControllerIntegrationTestsBase.JOBS_API).then().statusCode(Matchers.is(ACCEPTED.value())).header(LOCATION, Matchers.notNullValue()).extract().header(LOCATION));
        this.waitForDone(jobId);
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(((((RestControllerIntegrationTestsBase.JOBS_API) + "/") + jobId) + "/output/genie/logs/env.log")).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.containsString(TEXT_PLAIN_VALUE)).contentType(Matchers.containsString("UTF-8"));
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(((((RestControllerIntegrationTestsBase.JOBS_API) + "/") + jobId) + "/output/genie/logs/genie.log")).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.containsString(TEXT_PLAIN_VALUE)).contentType(Matchers.containsString("UTF-8"));
        RestAssured.given(getRequestSpecification()).when().port(this.port).get(((((RestControllerIntegrationTestsBase.JOBS_API) + "/") + jobId) + "/output/genie/genie.done")).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.containsString(TEXT_PLAIN_VALUE)).contentType(Matchers.containsString("UTF-8"));
        RestAssured.given(getRequestSpecification()).accept(ALL_VALUE).when().port(this.port).get(((((RestControllerIntegrationTestsBase.JOBS_API) + "/") + jobId) + "/output/stdout")).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.containsString(TEXT_PLAIN_VALUE)).contentType(Matchers.containsString("UTF-8"));
        RestAssured.given(getRequestSpecification()).accept(ALL_VALUE).when().port(this.port).get(((((RestControllerIntegrationTestsBase.JOBS_API) + "/") + jobId) + "/output/stderr")).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.containsString(TEXT_PLAIN_VALUE)).contentType(Matchers.containsString("UTF-8"));
        // Verify the file is served as UTF-8 even if it's not
        RestAssured.given(getRequestSpecification()).accept(ALL_VALUE).when().port(this.port).get((((((((RestControllerIntegrationTestsBase.JOBS_API) + "/") + jobId) + "/output/genie/command/") + (JobRestControllerIntegrationTests.CMD1_ID)) + "/config/") + (JobRestControllerIntegrationTests.GB18030_TXT))).then().statusCode(Matchers.is(OK.value())).contentType(Matchers.containsString(TEXT_PLAIN_VALUE)).contentType(Matchers.containsString("UTF-8"));
    }
}

