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


import JobStatus.SUCCEEDED;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.HttpServer2;
import org.junit.Assert;
import org.junit.Test;


public class TestJobEndNotifier {
    HttpServer2 server;

    URL baseUrl;

    @SuppressWarnings("serial")
    public static class JobEndServlet extends HttpServlet {
        public static volatile int calledTimes = 0;

        public static URI requestUri;

        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            InputStreamReader in = new InputStreamReader(request.getInputStream());
            PrintStream out = new PrintStream(response.getOutputStream());
            (TestJobEndNotifier.JobEndServlet.calledTimes)++;
            try {
                TestJobEndNotifier.JobEndServlet.requestUri = new URI(null, null, request.getRequestURI(), request.getQueryString(), null);
            } catch (URISyntaxException e) {
            }
            in.close();
            out.close();
        }
    }

    // Servlet that delays requests for a long time
    @SuppressWarnings("serial")
    public static class DelayServlet extends HttpServlet {
        public static volatile int calledTimes = 0;

        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            boolean timedOut = false;
            (TestJobEndNotifier.DelayServlet.calledTimes)++;
            try {
                // Sleep for a long time
                Thread.sleep(1000000);
            } catch (InterruptedException e) {
                timedOut = true;
            }
            Assert.assertTrue("DelayServlet should be interrupted", timedOut);
        }
    }

    // Servlet that fails all requests into it
    @SuppressWarnings("serial")
    public static class FailServlet extends HttpServlet {
        public static volatile int calledTimes = 0;

        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            (TestJobEndNotifier.FailServlet.calledTimes)++;
            throw new IOException("I am failing!");
        }
    }

    /**
     * Basic validation for localRunnerNotification.
     */
    @Test
    public void testLocalJobRunnerUriSubstitution() throws InterruptedException {
        JobStatus jobStatus = TestJobEndNotifier.createTestJobStatus("job_20130313155005308_0001", SUCCEEDED);
        JobConf jobConf = TestJobEndNotifier.createTestJobConf(new Configuration(), 0, ((baseUrl) + "jobend?jobid=$jobId&status=$jobStatus"));
        JobEndNotifier.localRunnerNotification(jobConf, jobStatus);
        // No need to wait for the notification to go thru since calls are
        // synchronous
        // Validate params
        Assert.assertEquals(1, TestJobEndNotifier.JobEndServlet.calledTimes);
        Assert.assertEquals("jobid=job_20130313155005308_0001&status=SUCCEEDED", TestJobEndNotifier.JobEndServlet.requestUri.getQuery());
    }

    /**
     * Validate job.end.retry.attempts for the localJobRunner.
     */
    @Test
    public void testLocalJobRunnerRetryCount() throws InterruptedException {
        int retryAttempts = 3;
        JobStatus jobStatus = TestJobEndNotifier.createTestJobStatus("job_20130313155005308_0001", SUCCEEDED);
        JobConf jobConf = TestJobEndNotifier.createTestJobConf(new Configuration(), retryAttempts, ((baseUrl) + "fail"));
        JobEndNotifier.localRunnerNotification(jobConf, jobStatus);
        // Validate params
        Assert.assertEquals((retryAttempts + 1), TestJobEndNotifier.FailServlet.calledTimes);
    }

    /**
     * Validate that the notification times out after reaching
     * mapreduce.job.end-notification.timeout.
     */
    @Test
    public void testNotificationTimeout() throws InterruptedException {
        Configuration conf = new Configuration();
        // Reduce the timeout to 1 second
        conf.setInt("mapreduce.job.end-notification.timeout", 1000);
        JobStatus jobStatus = TestJobEndNotifier.createTestJobStatus("job_20130313155005308_0001", SUCCEEDED);
        JobConf jobConf = TestJobEndNotifier.createTestJobConf(conf, 0, ((baseUrl) + "delay"));
        long startTime = System.currentTimeMillis();
        JobEndNotifier.localRunnerNotification(jobConf, jobStatus);
        long elapsedTime = (System.currentTimeMillis()) - startTime;
        // Validate params
        Assert.assertEquals(1, TestJobEndNotifier.DelayServlet.calledTimes);
        // Make sure we timed out with time slightly above 1 second
        // (default timeout is in terms of minutes, so we'll catch the problem)
        Assert.assertTrue((elapsedTime < 2000));
    }
}

