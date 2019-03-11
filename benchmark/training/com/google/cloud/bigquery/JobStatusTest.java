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


import JobStatus.State;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class JobStatusTest {
    private static final State STATE = State.DONE;

    private static final BigQueryError ERROR = new BigQueryError("reason", "location", "message", "debugInfo");

    private static final List<BigQueryError> ALL_ERRORS = ImmutableList.of(new BigQueryError("reason1", "location1", "message1", "debugInfo1"), new BigQueryError("reason2", "location2", "message2", "debugInfo2"));

    private static final JobStatus JOB_STATUS = new JobStatus(JobStatusTest.STATE, JobStatusTest.ERROR, JobStatusTest.ALL_ERRORS);

    private static final JobStatus JOB_STATUS_INCOMPLETE1 = new JobStatus(JobStatusTest.STATE, JobStatusTest.ERROR, null);

    private static final JobStatus JOB_STATUS_INCOMPLETE2 = new JobStatus(JobStatusTest.STATE, null, null);

    @Test
    public void testConstructor() {
        Assert.assertEquals(JobStatusTest.STATE, JobStatusTest.JOB_STATUS.getState());
        Assert.assertEquals(JobStatusTest.ERROR, JobStatusTest.JOB_STATUS.getError());
        Assert.assertEquals(JobStatusTest.ALL_ERRORS, JobStatusTest.JOB_STATUS.getExecutionErrors());
        Assert.assertEquals(JobStatusTest.STATE, JobStatusTest.JOB_STATUS_INCOMPLETE1.getState());
        Assert.assertEquals(JobStatusTest.ERROR, JobStatusTest.JOB_STATUS_INCOMPLETE1.getError());
        Assert.assertEquals(null, JobStatusTest.JOB_STATUS_INCOMPLETE1.getExecutionErrors());
        Assert.assertEquals(JobStatusTest.STATE, JobStatusTest.JOB_STATUS_INCOMPLETE2.getState());
        Assert.assertEquals(null, JobStatusTest.JOB_STATUS_INCOMPLETE2.getError());
        Assert.assertEquals(null, JobStatusTest.JOB_STATUS_INCOMPLETE2.getExecutionErrors());
    }

    @Test
    public void testToPbAndFromPb() {
        compareStatus(JobStatusTest.JOB_STATUS, JobStatus.fromPb(JobStatusTest.JOB_STATUS.toPb()));
        compareStatus(JobStatusTest.JOB_STATUS_INCOMPLETE1, JobStatus.fromPb(JobStatusTest.JOB_STATUS_INCOMPLETE1.toPb()));
        compareStatus(JobStatusTest.JOB_STATUS_INCOMPLETE2, JobStatus.fromPb(JobStatusTest.JOB_STATUS_INCOMPLETE2.toPb()));
    }
}

