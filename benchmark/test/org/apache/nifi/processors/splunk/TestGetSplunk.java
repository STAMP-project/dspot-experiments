/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.splunk;


import GetSplunk.ATOM_VALUE;
import GetSplunk.EARLIEST_TIME;
import GetSplunk.EARLIEST_TIME_ATTR;
import GetSplunk.INDEX_TIME_VALUE;
import GetSplunk.LATEST_TIME;
import GetSplunk.LATEST_TIME_ATTR;
import GetSplunk.LATEST_TIME_KEY;
import GetSplunk.MANAGED_BEGINNING_VALUE;
import GetSplunk.MANAGED_CURRENT_VALUE;
import GetSplunk.OUTPUT_MODE;
import GetSplunk.PASSWORD;
import GetSplunk.QUERY;
import GetSplunk.QUERY_ATTR;
import GetSplunk.REL_SUCCESS;
import GetSplunk.TIME_FIELD_STRATEGY;
import GetSplunk.TIME_RANGE_STRATEGY;
import GetSplunk.TIME_ZONE;
import GetSplunk.USERNAME;
import JobExportArgs.OutputMode;
import JobExportArgs.SearchMode.NORMAL;
import ProvenanceEventType.RECEIVE;
import Scope.CLUSTER;
import com.splunk.JobExportArgs;
import com.splunk.Service;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.apache.nifi.components.state.StateMap;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static GetSplunk.DATE_TIME_FORMAT;


public class TestGetSplunk {
    private Service service;

    private TestGetSplunk.TestableGetSplunk proc;

    private TestRunner runner;

    @Test
    public void testCustomValidation() {
        final String query = "search tcp:7879";
        final String providedEarliest = "-1h";
        final String providedLatest = "now";
        final String outputMode = ATOM_VALUE.getValue();
        runner.setProperty(QUERY, query);
        runner.setProperty(EARLIEST_TIME, providedEarliest);
        runner.setProperty(LATEST_TIME, providedLatest);
        runner.setProperty(OUTPUT_MODE, outputMode);
        runner.assertValid();
        runner.setProperty(USERNAME, "user1");
        runner.assertNotValid();
        runner.setProperty(PASSWORD, "password");
        runner.assertValid();
    }

    @Test
    public void testGetWithProvidedTime() {
        final String query = "search tcp:7879";
        final String providedEarliest = "-1h";
        final String providedLatest = "now";
        final String outputMode = ATOM_VALUE.getValue();
        runner.setProperty(QUERY, query);
        runner.setProperty(EARLIEST_TIME, providedEarliest);
        runner.setProperty(LATEST_TIME, providedLatest);
        runner.setProperty(OUTPUT_MODE, outputMode);
        final JobExportArgs expectedArgs = new JobExportArgs();
        expectedArgs.setSearchMode(NORMAL);
        expectedArgs.setEarliestTime(providedEarliest);
        expectedArgs.setLatestTime(providedLatest);
        expectedArgs.setOutputMode(OutputMode.valueOf(outputMode));
        final String resultContent = "fake results";
        final ByteArrayInputStream input = new ByteArrayInputStream(resultContent.getBytes(StandardCharsets.UTF_8));
        Mockito.when(service.export(ArgumentMatchers.eq(query), ArgumentMatchers.argThat(new TestGetSplunk.JobExportArgsMatcher(expectedArgs)))).thenReturn(input);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final List<MockFlowFile> mockFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, mockFlowFiles.size());
        final MockFlowFile mockFlowFile = mockFlowFiles.get(0);
        mockFlowFile.assertContentEquals(resultContent);
        mockFlowFile.assertAttributeEquals(QUERY_ATTR, query);
        mockFlowFile.assertAttributeEquals(EARLIEST_TIME_ATTR, providedEarliest);
        mockFlowFile.assertAttributeEquals(LATEST_TIME_ATTR, providedLatest);
        Assert.assertEquals(1, proc.count);
        final List<ProvenanceEventRecord> events = runner.getProvenanceEvents();
        Assert.assertEquals(1, events.size());
        Assert.assertEquals(RECEIVE, events.get(0).getEventType());
        Assert.assertEquals("https://localhost:8089", events.get(0).getTransitUri());
    }

    @Test
    public void testMultipleIterationsWithoutShuttingDown() {
        final String query = "search tcp:7879";
        final String providedEarliest = "-1h";
        final String providedLatest = "now";
        final String outputMode = ATOM_VALUE.getValue();
        runner.setProperty(QUERY, query);
        runner.setProperty(EARLIEST_TIME, providedEarliest);
        runner.setProperty(LATEST_TIME, providedLatest);
        runner.setProperty(OUTPUT_MODE, outputMode);
        final JobExportArgs expectedArgs = new JobExportArgs();
        expectedArgs.setSearchMode(NORMAL);
        expectedArgs.setEarliestTime(providedEarliest);
        expectedArgs.setLatestTime(providedLatest);
        expectedArgs.setOutputMode(OutputMode.valueOf(outputMode));
        final String resultContent = "fake results";
        final ByteArrayInputStream input = new ByteArrayInputStream(resultContent.getBytes(StandardCharsets.UTF_8));
        Mockito.when(service.export(ArgumentMatchers.eq(query), ArgumentMatchers.argThat(new TestGetSplunk.JobExportArgsMatcher(expectedArgs)))).thenReturn(input);
        final int iterations = 3;
        runner.run(iterations, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, iterations);
        Assert.assertEquals(1, proc.count);
    }

    @Test
    public void testGetWithManagedFromBeginning() throws ParseException {
        final String query = "search tcp:7879";
        final String outputMode = ATOM_VALUE.getValue();
        runner.setProperty(QUERY, query);
        runner.setProperty(OUTPUT_MODE, outputMode);
        runner.setProperty(TIME_RANGE_STRATEGY, MANAGED_BEGINNING_VALUE.getValue());
        final String resultContent = "fake results";
        final ByteArrayInputStream input = new ByteArrayInputStream(resultContent.getBytes(StandardCharsets.UTF_8));
        Mockito.when(service.export(ArgumentMatchers.eq(query), ArgumentMatchers.any(JobExportArgs.class))).thenReturn(input);
        // run once and don't shut down
        runner.run(1, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        // capture what the args were on last run
        final ArgumentCaptor<JobExportArgs> capture1 = ArgumentCaptor.forClass(JobExportArgs.class);
        Mockito.verify(service, Mockito.times(1)).export(ArgumentMatchers.eq(query), capture1.capture());
        // first execution with no previous state and "managed from beginning" should have a latest time and no earliest time
        final JobExportArgs actualArgs1 = capture1.getValue();
        Assert.assertNotNull(actualArgs1);
        Assert.assertNull(actualArgs1.get("earliest_time"));
        Assert.assertNotNull(actualArgs1.get("latest_time"));
        // save the latest time from the first run which should be earliest time of next run
        final String lastLatest = ((String) (actualArgs1.get("latest_time")));
        final SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        final Date lastLatestDate = format.parse(lastLatest);
        final String expectedLatest = format.format(new Date(((lastLatestDate.getTime()) + 1)));
        // run again
        runner.run(1, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        final ArgumentCaptor<JobExportArgs> capture2 = ArgumentCaptor.forClass(JobExportArgs.class);
        Mockito.verify(service, Mockito.times(2)).export(ArgumentMatchers.eq(query), capture2.capture());
        // second execution the earliest time should be the previous latest_time
        final JobExportArgs actualArgs2 = capture2.getValue();
        Assert.assertNotNull(actualArgs2);
        Assert.assertEquals(expectedLatest, actualArgs2.get("earliest_time"));
        Assert.assertNotNull(actualArgs2.get("latest_time"));
    }

    @Test
    public void testGetWithManagedFromBeginningWithDifferentTimeZone() throws ParseException {
        final String query = "search tcp:7879";
        final String outputMode = ATOM_VALUE.getValue();
        final TimeZone timeZone = TimeZone.getTimeZone("PST");
        runner.setProperty(QUERY, query);
        runner.setProperty(OUTPUT_MODE, outputMode);
        runner.setProperty(TIME_RANGE_STRATEGY, MANAGED_BEGINNING_VALUE.getValue());
        runner.setProperty(TIME_ZONE, timeZone.getID());
        final String resultContent = "fake results";
        final ByteArrayInputStream input = new ByteArrayInputStream(resultContent.getBytes(StandardCharsets.UTF_8));
        Mockito.when(service.export(ArgumentMatchers.eq(query), ArgumentMatchers.any(JobExportArgs.class))).thenReturn(input);
        // run once and don't shut down
        runner.run(1, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        // capture what the args were on last run
        final ArgumentCaptor<JobExportArgs> capture1 = ArgumentCaptor.forClass(JobExportArgs.class);
        Mockito.verify(service, Mockito.times(1)).export(ArgumentMatchers.eq(query), capture1.capture());
        // first execution with no previous state and "managed from beginning" should have a latest time and no earliest time
        final JobExportArgs actualArgs1 = capture1.getValue();
        Assert.assertNotNull(actualArgs1);
        Assert.assertNull(actualArgs1.get("earliest_time"));
        Assert.assertNotNull(actualArgs1.get("latest_time"));
        // save the latest time from the first run which should be earliest time of next run
        final String lastLatest = ((String) (actualArgs1.get("latest_time")));
        final SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
        format.setTimeZone(timeZone);
        final Date lastLatestDate = format.parse(lastLatest);
        final String expectedLatest = format.format(new Date(((lastLatestDate.getTime()) + 1)));
        // run again
        runner.run(1, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        final ArgumentCaptor<JobExportArgs> capture2 = ArgumentCaptor.forClass(JobExportArgs.class);
        Mockito.verify(service, Mockito.times(2)).export(ArgumentMatchers.eq(query), capture2.capture());
        // second execution the earliest time should be the previous latest_time
        final JobExportArgs actualArgs2 = capture2.getValue();
        Assert.assertNotNull(actualArgs2);
        Assert.assertEquals(expectedLatest, actualArgs2.get("earliest_time"));
        Assert.assertNotNull(actualArgs2.get("latest_time"));
    }

    @Test
    public void testGetWithManagedFromBeginningWithShutdown() throws ParseException {
        final String query = "search tcp:7879";
        final String outputMode = ATOM_VALUE.getValue();
        runner.setProperty(QUERY, query);
        runner.setProperty(OUTPUT_MODE, outputMode);
        runner.setProperty(TIME_RANGE_STRATEGY, MANAGED_BEGINNING_VALUE.getValue());
        final String resultContent = "fake results";
        final ByteArrayInputStream input = new ByteArrayInputStream(resultContent.getBytes(StandardCharsets.UTF_8));
        Mockito.when(service.export(ArgumentMatchers.eq(query), ArgumentMatchers.any(JobExportArgs.class))).thenReturn(input);
        // run once and shut down
        runner.run(1, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        // capture what the args were on last run
        final ArgumentCaptor<JobExportArgs> capture1 = ArgumentCaptor.forClass(JobExportArgs.class);
        Mockito.verify(service, Mockito.times(1)).export(ArgumentMatchers.eq(query), capture1.capture());
        // first execution with no previous state and "managed from beginning" should have a latest time and no earliest time
        final JobExportArgs actualArgs1 = capture1.getValue();
        Assert.assertNotNull(actualArgs1);
        Assert.assertNull(actualArgs1.get("earliest_time"));
        Assert.assertNotNull(actualArgs1.get("latest_time"));
        // save the latest time from the first run which should be earliest time of next run
        final String lastLatest = ((String) (actualArgs1.get("latest_time")));
        final SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        final Date lastLatestDate = format.parse(lastLatest);
        final String expectedLatest = format.format(new Date(((lastLatestDate.getTime()) + 1)));
        // run again
        runner.run(1, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        final ArgumentCaptor<JobExportArgs> capture2 = ArgumentCaptor.forClass(JobExportArgs.class);
        Mockito.verify(service, Mockito.times(2)).export(ArgumentMatchers.eq(query), capture2.capture());
        // second execution the earliest time should be the previous latest_time
        final JobExportArgs actualArgs2 = capture2.getValue();
        Assert.assertNotNull(actualArgs2);
        Assert.assertEquals(expectedLatest, actualArgs2.get("earliest_time"));
        Assert.assertNotNull(actualArgs2.get("latest_time"));
    }

    @Test
    public void testGetWithManagedFromCurrentUsingEventTime() throws IOException, ParseException {
        final String query = "search tcp:7879";
        final String outputMode = ATOM_VALUE.getValue();
        runner.setProperty(QUERY, query);
        runner.setProperty(OUTPUT_MODE, outputMode);
        runner.setProperty(TIME_RANGE_STRATEGY, MANAGED_CURRENT_VALUE.getValue());
        final String resultContent = "fake results";
        final ByteArrayInputStream input = new ByteArrayInputStream(resultContent.getBytes(StandardCharsets.UTF_8));
        Mockito.when(service.export(ArgumentMatchers.eq(query), ArgumentMatchers.any(JobExportArgs.class))).thenReturn(input);
        // run once and don't shut down, shouldn't produce any results first time
        runner.run(1, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        // capture what the args were on last run
        Mockito.verify(service, Mockito.times(0)).export(ArgumentMatchers.eq(query), ArgumentMatchers.any(JobExportArgs.class));
        final StateMap state = runner.getStateManager().getState(CLUSTER);
        Assert.assertNotNull(state);
        Assert.assertTrue(((state.getVersion()) > 0));
        // save the latest time from the first run which should be earliest time of next run
        final String lastLatest = state.get(LATEST_TIME_KEY);
        final SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        final Date lastLatestDate = format.parse(lastLatest);
        final String expectedLatest = format.format(new Date(((lastLatestDate.getTime()) + 1)));
        // run again
        runner.run(1, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final ArgumentCaptor<JobExportArgs> capture = ArgumentCaptor.forClass(JobExportArgs.class);
        Mockito.verify(service, Mockito.times(1)).export(ArgumentMatchers.eq(query), capture.capture());
        // second execution the earliest time should be the previous latest_time
        final JobExportArgs actualArgs = capture.getValue();
        Assert.assertNotNull(actualArgs);
        Assert.assertEquals(expectedLatest, actualArgs.get("earliest_time"));
        Assert.assertNotNull(actualArgs.get("latest_time"));
    }

    @Test
    public void testGetWithManagedFromCurrentUsingIndexTime() throws IOException, ParseException {
        final String query = "search tcp:7879";
        final String outputMode = ATOM_VALUE.getValue();
        runner.setProperty(QUERY, query);
        runner.setProperty(OUTPUT_MODE, outputMode);
        runner.setProperty(TIME_RANGE_STRATEGY, MANAGED_CURRENT_VALUE.getValue());
        runner.setProperty(TIME_FIELD_STRATEGY, INDEX_TIME_VALUE.getValue());
        final String resultContent = "fake results";
        final ByteArrayInputStream input = new ByteArrayInputStream(resultContent.getBytes(StandardCharsets.UTF_8));
        Mockito.when(service.export(ArgumentMatchers.eq(query), ArgumentMatchers.any(JobExportArgs.class))).thenReturn(input);
        // run once and don't shut down, shouldn't produce any results first time
        runner.run(1, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        // capture what the args were on last run
        Mockito.verify(service, Mockito.times(0)).export(ArgumentMatchers.eq(query), ArgumentMatchers.any(JobExportArgs.class));
        final StateMap state = runner.getStateManager().getState(CLUSTER);
        Assert.assertNotNull(state);
        Assert.assertTrue(((state.getVersion()) > 0));
        // save the latest time from the first run which should be earliest time of next run
        final String lastLatest = state.get(LATEST_TIME_KEY);
        final SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        final Date lastLatestDate = format.parse(lastLatest);
        final String expectedLatest = format.format(new Date(((lastLatestDate.getTime()) + 1)));
        // run again
        runner.run(1, false);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final ArgumentCaptor<JobExportArgs> capture = ArgumentCaptor.forClass(JobExportArgs.class);
        Mockito.verify(service, Mockito.times(1)).export(ArgumentMatchers.eq(query), capture.capture());
        // second execution the earliest time should be the previous latest_time
        final JobExportArgs actualArgs = capture.getValue();
        Assert.assertNotNull(actualArgs);
        Assert.assertEquals(expectedLatest, actualArgs.get("index_earliest"));
        Assert.assertNotNull(actualArgs.get("index_latest"));
    }

    /**
     * Testable implementation of GetSplunk to return a Mock Splunk Service.
     */
    private static class TestableGetSplunk extends GetSplunk {
        int count;

        Service mockService;

        public TestableGetSplunk(Service mockService) {
            this.mockService = mockService;
        }

        @Override
        protected Service createSplunkService(ProcessContext context) {
            (count)++;
            return mockService;
        }
    }

    /**
     * Custom args matcher for JobExportArgs.
     */
    private static class JobExportArgsMatcher extends ArgumentMatcher<JobExportArgs> {
        private JobExportArgs expected;

        public JobExportArgsMatcher(JobExportArgs expected) {
            this.expected = expected;
        }

        @Override
        public boolean matches(Object o) {
            if (o == null) {
                return false;
            }
            if (!(o instanceof JobExportArgs)) {
                return false;
            }
            JobExportArgs other = ((JobExportArgs) (o));
            return expected.equals(other);
        }
    }
}

