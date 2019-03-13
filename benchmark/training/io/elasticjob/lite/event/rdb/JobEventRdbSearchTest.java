/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */
package io.elasticjob.lite.event.rdb;


import io.elasticjob.lite.event.type.JobExecutionEvent;
import io.elasticjob.lite.event.type.JobStatusTraceEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class JobEventRdbSearchTest {
    private static JobEventRdbStorage storage;

    private static JobEventRdbSearch repository;

    @Test
    public void assertFindJobExecutionEventsWithPageSizeAndNumber() {
        JobEventRdbSearch.Result<JobExecutionEvent> result = JobEventRdbSearchTest.repository.findJobExecutionEvents(new JobEventRdbSearch.Condition(10, 1, null, null, null, null, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(10));
        result = JobEventRdbSearchTest.repository.findJobExecutionEvents(new JobEventRdbSearch.Condition(50, 1, null, null, null, null, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(50));
        result = JobEventRdbSearchTest.repository.findJobExecutionEvents(new JobEventRdbSearch.Condition(100, 5, null, null, null, null, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(100));
        result = JobEventRdbSearchTest.repository.findJobExecutionEvents(new JobEventRdbSearch.Condition(100, 6, null, null, null, null, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(0));
    }

    @Test
    public void assertFindJobExecutionEventsWithErrorPageSizeAndNumber() {
        JobEventRdbSearch.Result<JobExecutionEvent> result = JobEventRdbSearchTest.repository.findJobExecutionEvents(new JobEventRdbSearch.Condition((-1), (-1), null, null, null, null, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(10));
    }

    @Test
    public void assertFindJobExecutionEventsWithSort() {
        JobEventRdbSearch.Result<JobExecutionEvent> result = JobEventRdbSearchTest.repository.findJobExecutionEvents(new JobEventRdbSearch.Condition(10, 1, "jobName", "ASC", null, null, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(10));
        Assert.assertThat(result.getRows().get(0).getJobName(), CoreMatchers.is("test_job_1"));
        result = JobEventRdbSearchTest.repository.findJobExecutionEvents(new JobEventRdbSearch.Condition(10, 1, "jobName", "DESC", null, null, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(10));
        Assert.assertThat(result.getRows().get(0).getJobName(), CoreMatchers.is("test_job_99"));
    }

    @Test
    public void assertFindJobExecutionEventsWithErrorSort() {
        JobEventRdbSearch.Result<JobExecutionEvent> result = JobEventRdbSearchTest.repository.findJobExecutionEvents(new JobEventRdbSearch.Condition(10, 1, "jobName", "ERROR_SORT", null, null, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(10));
        Assert.assertThat(result.getRows().get(0).getJobName(), CoreMatchers.is("test_job_1"));
        result = JobEventRdbSearchTest.repository.findJobExecutionEvents(new JobEventRdbSearch.Condition(10, 1, "notExistField", "ASC", null, null, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(10));
    }

    @Test
    public void assertFindJobExecutionEventsWithTime() {
        Date now = new Date();
        Date tenMinutesBefore = new Date(((now.getTime()) - ((10 * 60) * 1000)));
        JobEventRdbSearch.Result<JobExecutionEvent> result = JobEventRdbSearchTest.repository.findJobExecutionEvents(new JobEventRdbSearch.Condition(10, 1, null, null, tenMinutesBefore, null, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(10));
        result = JobEventRdbSearchTest.repository.findJobExecutionEvents(new JobEventRdbSearch.Condition(10, 1, null, null, now, null, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(0));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(0));
        result = JobEventRdbSearchTest.repository.findJobExecutionEvents(new JobEventRdbSearch.Condition(10, 1, null, null, null, tenMinutesBefore, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(0));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(0));
        result = JobEventRdbSearchTest.repository.findJobExecutionEvents(new JobEventRdbSearch.Condition(10, 1, null, null, null, now, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(10));
        result = JobEventRdbSearchTest.repository.findJobExecutionEvents(new JobEventRdbSearch.Condition(10, 1, null, null, tenMinutesBefore, now, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(10));
    }

    @Test
    public void assertFindJobExecutionEventsWithFields() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("isSuccess", "1");
        JobEventRdbSearch.Result<JobExecutionEvent> result = JobEventRdbSearchTest.repository.findJobExecutionEvents(new JobEventRdbSearch.Condition(10, 1, null, null, null, null, fields));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(250));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(10));
        fields.put("isSuccess", null);
        fields.put("jobName", "test_job_1");
        result = JobEventRdbSearchTest.repository.findJobExecutionEvents(new JobEventRdbSearch.Condition(10, 1, null, null, null, null, fields));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(1));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(1));
    }

    @Test
    public void assertFindJobExecutionEventsWithErrorFields() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("notExistField", "some value");
        JobEventRdbSearch.Result<JobExecutionEvent> result = JobEventRdbSearchTest.repository.findJobExecutionEvents(new JobEventRdbSearch.Condition(10, 1, null, null, null, null, fields));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(10));
    }

    @Test
    public void assertFindJobStatusTraceEventsWithPageSizeAndNumber() {
        JobEventRdbSearch.Result<JobStatusTraceEvent> result = JobEventRdbSearchTest.repository.findJobStatusTraceEvents(new JobEventRdbSearch.Condition(10, 1, null, null, null, null, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(10));
        result = JobEventRdbSearchTest.repository.findJobStatusTraceEvents(new JobEventRdbSearch.Condition(50, 1, null, null, null, null, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(50));
        result = JobEventRdbSearchTest.repository.findJobStatusTraceEvents(new JobEventRdbSearch.Condition(100, 5, null, null, null, null, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(100));
        result = JobEventRdbSearchTest.repository.findJobStatusTraceEvents(new JobEventRdbSearch.Condition(100, 6, null, null, null, null, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(0));
    }

    @Test
    public void assertFindJobStatusTraceEventsWithErrorPageSizeAndNumber() {
        JobEventRdbSearch.Result<JobStatusTraceEvent> result = JobEventRdbSearchTest.repository.findJobStatusTraceEvents(new JobEventRdbSearch.Condition((-1), (-1), null, null, null, null, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(10));
    }

    @Test
    public void assertFindJobStatusTraceEventsWithSort() {
        JobEventRdbSearch.Result<JobStatusTraceEvent> result = JobEventRdbSearchTest.repository.findJobStatusTraceEvents(new JobEventRdbSearch.Condition(10, 1, "jobName", "ASC", null, null, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(10));
        Assert.assertThat(result.getRows().get(0).getJobName(), CoreMatchers.is("test_job_1"));
        result = JobEventRdbSearchTest.repository.findJobStatusTraceEvents(new JobEventRdbSearch.Condition(10, 1, "jobName", "DESC", null, null, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(10));
        Assert.assertThat(result.getRows().get(0).getJobName(), CoreMatchers.is("test_job_99"));
    }

    @Test
    public void assertFindJobStatusTraceEventsWithErrorSort() {
        JobEventRdbSearch.Result<JobStatusTraceEvent> result = JobEventRdbSearchTest.repository.findJobStatusTraceEvents(new JobEventRdbSearch.Condition(10, 1, "jobName", "ERROR_SORT", null, null, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(10));
        Assert.assertThat(result.getRows().get(0).getJobName(), CoreMatchers.is("test_job_1"));
        result = JobEventRdbSearchTest.repository.findJobStatusTraceEvents(new JobEventRdbSearch.Condition(10, 1, "notExistField", "ASC", null, null, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(10));
    }

    @Test
    public void assertFindJobStatusTraceEventsWithTime() {
        Date now = new Date();
        Date tenMinutesBefore = new Date(((now.getTime()) - ((10 * 60) * 1000)));
        JobEventRdbSearch.Result<JobStatusTraceEvent> result = JobEventRdbSearchTest.repository.findJobStatusTraceEvents(new JobEventRdbSearch.Condition(10, 1, null, null, tenMinutesBefore, null, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(10));
        result = JobEventRdbSearchTest.repository.findJobStatusTraceEvents(new JobEventRdbSearch.Condition(10, 1, null, null, now, null, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(0));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(0));
        result = JobEventRdbSearchTest.repository.findJobStatusTraceEvents(new JobEventRdbSearch.Condition(10, 1, null, null, null, tenMinutesBefore, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(0));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(0));
        result = JobEventRdbSearchTest.repository.findJobStatusTraceEvents(new JobEventRdbSearch.Condition(10, 1, null, null, null, now, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(10));
        result = JobEventRdbSearchTest.repository.findJobStatusTraceEvents(new JobEventRdbSearch.Condition(10, 1, null, null, tenMinutesBefore, now, null));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(10));
    }

    @Test
    public void assertFindJobStatusTraceEventsWithFields() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("jobName", "test_job_1");
        JobEventRdbSearch.Result<JobStatusTraceEvent> result = JobEventRdbSearchTest.repository.findJobStatusTraceEvents(new JobEventRdbSearch.Condition(10, 1, null, null, null, null, fields));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(1));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(1));
    }

    @Test
    public void assertFindJobStatusTraceEventsWithErrorFields() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("notExistField", "some value");
        JobEventRdbSearch.Result<JobStatusTraceEvent> result = JobEventRdbSearchTest.repository.findJobStatusTraceEvents(new JobEventRdbSearch.Condition(10, 1, null, null, null, null, fields));
        Assert.assertThat(result.getTotal(), CoreMatchers.is(500));
        Assert.assertThat(result.getRows().size(), CoreMatchers.is(10));
    }
}

