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
package org.apache.hadoop.mapreduce.v2.app.webapp;


import AMParams.ATTEMPT_STATE;
import AMParams.JOB_ID;
import AMParams.TASK_ID;
import AMParams.TASK_TYPE;
import AppController.COUNTER_GROUP;
import AppController.COUNTER_NAME;
import MimeType.TEXT;
import ResponseInfo.Item;
import java.io.IOException;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.mapreduce.JobACL;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.webapp.Controller.RequestContext;
import org.apache.hadoop.yarn.webapp.ResponseInfo;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestAppController {
    private AppControllerForTest appController;

    private RequestContext ctx;

    private Job job;

    private static final String taskId = "task_01_01_m_01";

    /**
     * test bad request should be status 400...
     */
    @Test
    public void testBadRequest() {
        String message = "test string";
        appController.badRequest(message);
        verifyExpectations(message);
    }

    @Test
    public void testBadRequestWithNullMessage() {
        // It should not throw NullPointerException
        badRequest(null);
        verifyExpectations(StringUtils.EMPTY);
    }

    /**
     * Test the method 'info'.
     */
    @Test
    public void testInfo() {
        info();
        Iterator<ResponseInfo.Item> iterator = appController.getResponseInfo().iterator();
        ResponseInfo.Item item = iterator.next();
        Assert.assertEquals("Application ID:", item.key);
        Assert.assertEquals("application_0_0000", item.value);
        item = iterator.next();
        Assert.assertEquals("Application Name:", item.key);
        Assert.assertEquals("AppName", item.value);
        item = iterator.next();
        Assert.assertEquals("User:", item.key);
        Assert.assertEquals("User", item.value);
        item = iterator.next();
        Assert.assertEquals("Started on:", item.key);
        item = iterator.next();
        Assert.assertEquals("Elasped: ", item.key);
    }

    /**
     * Test method 'job'. Should print message about error or set JobPage class for rendering
     */
    @Test
    public void testGetJob() {
        Mockito.when(job.checkAccess(ArgumentMatchers.any(UserGroupInformation.class), ArgumentMatchers.any(JobACL.class))).thenReturn(false);
        job();
        Mockito.verify(appController.response()).setContentType(TEXT);
        Assert.assertEquals("Access denied: User user does not have permission to view job job_01_01", appController.getData());
        Mockito.when(job.checkAccess(ArgumentMatchers.any(UserGroupInformation.class), ArgumentMatchers.any(JobACL.class))).thenReturn(true);
        appController.getProperty().remove(JOB_ID);
        job();
        Assert.assertEquals("Access denied: User user does not have permission to view job job_01_01Bad Request: Missing job ID", appController.getData());
        appController.getProperty().put(JOB_ID, "job_01_01");
        job();
        Assert.assertEquals(JobPage.class, appController.getClazz());
    }

    /**
     * Test method 'jobCounters'. Should print message about error or set CountersPage class for rendering
     */
    @Test
    public void testGetJobCounters() {
        Mockito.when(job.checkAccess(ArgumentMatchers.any(UserGroupInformation.class), ArgumentMatchers.any(JobACL.class))).thenReturn(false);
        jobCounters();
        Mockito.verify(appController.response()).setContentType(TEXT);
        Assert.assertEquals("Access denied: User user does not have permission to view job job_01_01", appController.getData());
        Mockito.when(job.checkAccess(ArgumentMatchers.any(UserGroupInformation.class), ArgumentMatchers.any(JobACL.class))).thenReturn(true);
        appController.getProperty().remove(JOB_ID);
        jobCounters();
        Assert.assertEquals("Access denied: User user does not have permission to view job job_01_01Bad Request: Missing job ID", appController.getData());
        appController.getProperty().put(JOB_ID, "job_01_01");
        jobCounters();
        Assert.assertEquals(CountersPage.class, appController.getClazz());
    }

    /**
     * Test method 'taskCounters'. Should print message about error or set CountersPage class for rendering
     */
    @Test
    public void testGetTaskCounters() {
        Mockito.when(job.checkAccess(ArgumentMatchers.any(UserGroupInformation.class), ArgumentMatchers.any(JobACL.class))).thenReturn(false);
        taskCounters();
        Mockito.verify(appController.response()).setContentType(TEXT);
        Assert.assertEquals("Access denied: User user does not have permission to view job job_01_01", appController.getData());
        Mockito.when(job.checkAccess(ArgumentMatchers.any(UserGroupInformation.class), ArgumentMatchers.any(JobACL.class))).thenReturn(true);
        appController.getProperty().remove(TASK_ID);
        taskCounters();
        Assert.assertEquals("Access denied: User user does not have permission to view job job_01_01missing task ID", appController.getData());
        appController.getProperty().put(TASK_ID, TestAppController.taskId);
        taskCounters();
        Assert.assertEquals(CountersPage.class, appController.getClazz());
    }

    /**
     * Test method 'singleJobCounter'. Should set SingleCounterPage class for rendering
     */
    @Test
    public void testGetSingleJobCounter() throws IOException {
        singleJobCounter();
        Assert.assertEquals(SingleCounterPage.class, appController.getClazz());
    }

    /**
     * Test method 'singleTaskCounter'. Should set SingleCounterPage class for rendering
     */
    @Test
    public void testGetSingleTaskCounter() throws IOException {
        singleTaskCounter();
        Assert.assertEquals(SingleCounterPage.class, appController.getClazz());
        Assert.assertNotNull(appController.getProperty().get(COUNTER_GROUP));
        Assert.assertNotNull(appController.getProperty().get(COUNTER_NAME));
    }

    /**
     * Test method 'tasks'. Should set TasksPage class for rendering
     */
    @Test
    public void testTasks() {
        tasks();
        Assert.assertEquals(TasksPage.class, appController.getClazz());
    }

    /**
     * Test method 'task'. Should set TaskPage class for rendering and information for title
     */
    @Test
    public void testTask() {
        task();
        Assert.assertEquals(("Attempts for " + (TestAppController.taskId)), appController.getProperty().get("title"));
        Assert.assertEquals(TaskPage.class, appController.getClazz());
    }

    /**
     * Test method 'conf'. Should set JobConfPage class for rendering
     */
    @Test
    public void testConfiguration() {
        conf();
        Assert.assertEquals(JobConfPage.class, appController.getClazz());
    }

    /**
     * Test downloadConf request handling.
     */
    @Test
    public void testDownloadConfiguration() {
        downloadConf();
        String jobConfXml = appController.getData();
        Assert.assertTrue("Error downloading the job configuration file.", (!(jobConfXml.contains("Error"))));
    }

    /**
     * Test method 'conf'. Should set AttemptsPage class for rendering or print information about error
     */
    @Test
    public void testAttempts() {
        appController.getProperty().remove(TASK_TYPE);
        Mockito.when(job.checkAccess(ArgumentMatchers.any(UserGroupInformation.class), ArgumentMatchers.any(JobACL.class))).thenReturn(false);
        attempts();
        Mockito.verify(appController.response()).setContentType(TEXT);
        Assert.assertEquals("Access denied: User user does not have permission to view job job_01_01", appController.getData());
        Mockito.when(job.checkAccess(ArgumentMatchers.any(UserGroupInformation.class), ArgumentMatchers.any(JobACL.class))).thenReturn(true);
        appController.getProperty().remove(TASK_ID);
        attempts();
        Assert.assertEquals("Access denied: User user does not have permission to view job job_01_01", appController.getData());
        appController.getProperty().put(TASK_ID, TestAppController.taskId);
        attempts();
        Assert.assertEquals("Bad request: missing task-type.", appController.getProperty().get("title"));
        appController.getProperty().put(TASK_TYPE, "m");
        attempts();
        Assert.assertEquals("Bad request: missing attempt-state.", appController.getProperty().get("title"));
        appController.getProperty().put(ATTEMPT_STATE, "State");
        attempts();
        Assert.assertEquals(AttemptsPage.class, appController.getClazz());
    }
}

