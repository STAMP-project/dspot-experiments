/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigquery;


import org.junit.Assert;
import org.junit.Test;


public class JobIdTest {
    private static final JobId JOB = JobId.of("job");

    private static final JobId JOB_COMPLETE = JobId.of("project", "job");

    @Test
    public void testOf() {
        Assert.assertEquals(null, JobIdTest.JOB.getProject());
        Assert.assertEquals("job", JobIdTest.JOB.getJob());
        Assert.assertEquals("project", JobIdTest.JOB_COMPLETE.getProject());
        Assert.assertEquals("job", JobIdTest.JOB_COMPLETE.getJob());
    }

    @Test
    public void testEquals() {
        compareJobs(JobIdTest.JOB, JobId.of("job"));
        compareJobs(JobIdTest.JOB_COMPLETE, JobId.of("project", "job"));
    }

    @Test
    public void testToPbAndFromPb() {
        compareJobs(JobIdTest.JOB, JobId.fromPb(JobIdTest.JOB.toPb()));
        compareJobs(JobIdTest.JOB_COMPLETE, JobId.fromPb(JobIdTest.JOB_COMPLETE.toPb()));
    }

    @Test
    public void testSetProjectId() {
        Assert.assertEquals(JobIdTest.JOB_COMPLETE, JobIdTest.JOB.setProjectId("project"));
    }
}

