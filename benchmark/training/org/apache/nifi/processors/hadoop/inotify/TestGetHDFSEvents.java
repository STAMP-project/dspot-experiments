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
package org.apache.nifi.processors.hadoop.inotify;


import EventAttributes.EVENT_TYPE;
import GetHDFSEvents.EVENT_TYPES;
import GetHDFSEvents.HDFS_PATH_TO_WATCH;
import GetHDFSEvents.IGNORE_HIDDEN_FILES;
import GetHDFSEvents.NUMBER_OF_RETRIES_FOR_POLL;
import GetHDFSEvents.POLL_DURATION;
import GetHDFSEvents.REL_SUCCESS;
import Scope.CLUSTER;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DFSInotifyEventInputStream;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.client.HdfsAdmin;
import org.apache.hadoop.hdfs.inotify.Event;
import org.apache.hadoop.hdfs.inotify.EventBatch;
import org.apache.nifi.hadoop.KerberosProperties;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.NiFiProperties;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class TestGetHDFSEvents {
    NiFiProperties mockNiFiProperties;

    KerberosProperties kerberosProperties;

    DFSInotifyEventInputStream inotifyEventInputStream;

    HdfsAdmin hdfsAdmin;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void notSettingHdfsPathToWatchShouldThrowError() throws Exception {
        exception.expect(AssertionError.class);
        exception.expectMessage("'HDFS Path to Watch' is invalid because HDFS Path to Watch is required");
        GetHDFSEvents processor = new TestGetHDFSEvents.TestableGetHDFSEvents(kerberosProperties, hdfsAdmin);
        TestRunner runner = TestRunners.newTestRunner(processor);
        runner.setProperty(POLL_DURATION, "1 second");
        runner.run();
    }

    @Test
    public void onTriggerShouldProperlyHandleAnEmptyEventBatch() throws Exception {
        EventBatch eventBatch = Mockito.mock(EventBatch.class);
        Mockito.when(eventBatch.getEvents()).thenReturn(new Event[]{  });
        Mockito.when(inotifyEventInputStream.poll(1000000L, TimeUnit.MICROSECONDS)).thenReturn(eventBatch);
        Mockito.when(hdfsAdmin.getInotifyEventStream()).thenReturn(inotifyEventInputStream);
        Mockito.when(eventBatch.getTxid()).thenReturn(100L);
        GetHDFSEvents processor = new TestGetHDFSEvents.TestableGetHDFSEvents(kerberosProperties, hdfsAdmin);
        TestRunner runner = TestRunners.newTestRunner(processor);
        runner.setProperty(POLL_DURATION, "1 second");
        runner.setProperty(HDFS_PATH_TO_WATCH, "/some/path");
        runner.setProperty(NUMBER_OF_RETRIES_FOR_POLL, "5");
        runner.run();
        List<MockFlowFile> successfulFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(0, successfulFlowFiles.size());
        Mockito.verify(eventBatch).getTxid();
        Assert.assertEquals("100", runner.getProcessContext().getStateManager().getState(CLUSTER).get("last.tx.id"));
    }

    @Test
    public void onTriggerShouldProperlyHandleANullEventBatch() throws Exception {
        Mockito.when(inotifyEventInputStream.poll(1000000L, TimeUnit.MICROSECONDS)).thenReturn(null);
        Mockito.when(hdfsAdmin.getInotifyEventStream()).thenReturn(inotifyEventInputStream);
        GetHDFSEvents processor = new TestGetHDFSEvents.TestableGetHDFSEvents(kerberosProperties, hdfsAdmin);
        TestRunner runner = TestRunners.newTestRunner(processor);
        runner.setProperty(POLL_DURATION, "1 second");
        runner.setProperty(HDFS_PATH_TO_WATCH, "/some/path${now()}");
        runner.run();
        List<MockFlowFile> successfulFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(0, successfulFlowFiles.size());
        Assert.assertEquals("-1", runner.getProcessContext().getStateManager().getState(CLUSTER).get("last.tx.id"));
    }

    @Test
    public void makeSureHappyPathForProcessingEventsSendsFlowFilesToCorrectRelationship() throws Exception {
        Event[] events = getEvents();
        EventBatch eventBatch = Mockito.mock(EventBatch.class);
        Mockito.when(eventBatch.getEvents()).thenReturn(events);
        Mockito.when(inotifyEventInputStream.poll(1000000L, TimeUnit.MICROSECONDS)).thenReturn(eventBatch);
        Mockito.when(hdfsAdmin.getInotifyEventStream()).thenReturn(inotifyEventInputStream);
        Mockito.when(eventBatch.getTxid()).thenReturn(100L);
        GetHDFSEvents processor = new TestGetHDFSEvents.TestableGetHDFSEvents(kerberosProperties, hdfsAdmin);
        TestRunner runner = TestRunners.newTestRunner(processor);
        runner.setProperty(POLL_DURATION, "1 second");
        runner.setProperty(HDFS_PATH_TO_WATCH, "/some/path(/)?.*");
        runner.run();
        List<MockFlowFile> successfulFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(3, successfulFlowFiles.size());
        Mockito.verify(eventBatch).getTxid();
        Assert.assertEquals("100", runner.getProcessContext().getStateManager().getState(CLUSTER).get("last.tx.id"));
    }

    @Test
    public void onTriggerShouldOnlyProcessEventsWithSpecificPath() throws Exception {
        Event[] events = getEvents();
        EventBatch eventBatch = Mockito.mock(EventBatch.class);
        Mockito.when(eventBatch.getEvents()).thenReturn(events);
        Mockito.when(inotifyEventInputStream.poll(1000000L, TimeUnit.MICROSECONDS)).thenReturn(eventBatch);
        Mockito.when(hdfsAdmin.getInotifyEventStream()).thenReturn(inotifyEventInputStream);
        Mockito.when(eventBatch.getTxid()).thenReturn(100L);
        GetHDFSEvents processor = new TestGetHDFSEvents.TestableGetHDFSEvents(kerberosProperties, hdfsAdmin);
        TestRunner runner = TestRunners.newTestRunner(processor);
        runner.setProperty(HDFS_PATH_TO_WATCH, "/some/path/create(/)?");
        runner.run();
        List<MockFlowFile> successfulFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, successfulFlowFiles.size());
        Mockito.verify(eventBatch).getTxid();
        Assert.assertEquals("100", runner.getProcessContext().getStateManager().getState(CLUSTER).get("last.tx.id"));
    }

    @Test
    public void eventsProcessorShouldProperlyFilterEventTypes() throws Exception {
        Event[] events = getEvents();
        EventBatch eventBatch = Mockito.mock(EventBatch.class);
        Mockito.when(eventBatch.getEvents()).thenReturn(events);
        Mockito.when(inotifyEventInputStream.poll(1000000L, TimeUnit.MICROSECONDS)).thenReturn(eventBatch);
        Mockito.when(hdfsAdmin.getInotifyEventStream()).thenReturn(inotifyEventInputStream);
        Mockito.when(eventBatch.getTxid()).thenReturn(100L);
        GetHDFSEvents processor = new TestGetHDFSEvents.TestableGetHDFSEvents(kerberosProperties, hdfsAdmin);
        TestRunner runner = TestRunners.newTestRunner(processor);
        runner.setProperty(HDFS_PATH_TO_WATCH, "/some/path(/.*)?");
        runner.setProperty(EVENT_TYPES, "create, metadata");
        runner.run();
        List<MockFlowFile> successfulFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(2, successfulFlowFiles.size());
        List<String> expectedEventTypes = Arrays.asList("CREATE", "METADATA");
        for (MockFlowFile f : successfulFlowFiles) {
            String eventType = f.getAttribute(EVENT_TYPE);
            Assert.assertTrue(expectedEventTypes.contains(eventType));
        }
        Mockito.verify(eventBatch).getTxid();
        Assert.assertEquals("100", runner.getProcessContext().getStateManager().getState(CLUSTER).get("last.tx.id"));
    }

    @Test
    public void makeSureExpressionLanguageIsWorkingProperlyWithinTheHdfsPathToWatch() throws Exception {
        Event[] events = new Event[]{ new Event.CreateEvent.Builder().path("/some/path/1/2/3/t.txt").build(), new Event.CreateEvent.Builder().path("/some/path/1/2/4/t.txt").build(), new Event.CreateEvent.Builder().path("/some/path/1/2/3/.t.txt").build() };
        EventBatch eventBatch = Mockito.mock(EventBatch.class);
        Mockito.when(eventBatch.getEvents()).thenReturn(events);
        Mockito.when(inotifyEventInputStream.poll(1000000L, TimeUnit.MICROSECONDS)).thenReturn(eventBatch);
        Mockito.when(hdfsAdmin.getInotifyEventStream()).thenReturn(inotifyEventInputStream);
        Mockito.when(eventBatch.getTxid()).thenReturn(100L);
        GetHDFSEvents processor = new TestGetHDFSEvents.TestableGetHDFSEvents(kerberosProperties, hdfsAdmin);
        TestRunner runner = TestRunners.newTestRunner(processor);
        runner.setProperty(HDFS_PATH_TO_WATCH, "/some/path/${literal(1)}/${literal(2)}/${literal(3)}/.*.txt");
        runner.setProperty(EVENT_TYPES, "create");
        runner.setProperty(IGNORE_HIDDEN_FILES, "true");
        runner.run();
        List<MockFlowFile> successfulFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, successfulFlowFiles.size());
        for (MockFlowFile f : successfulFlowFiles) {
            String eventType = f.getAttribute(EVENT_TYPE);
            Assert.assertTrue(eventType.equals("CREATE"));
        }
        Mockito.verify(eventBatch).getTxid();
        Assert.assertEquals("100", runner.getProcessContext().getStateManager().getState(CLUSTER).get("last.tx.id"));
    }

    private class TestableGetHDFSEvents extends GetHDFSEvents {
        private final KerberosProperties testKerberosProperties;

        private final FileSystem fileSystem = new DistributedFileSystem();

        private final HdfsAdmin hdfsAdmin;

        TestableGetHDFSEvents(KerberosProperties testKerberosProperties, HdfsAdmin hdfsAdmin) {
            this.testKerberosProperties = testKerberosProperties;
            this.hdfsAdmin = hdfsAdmin;
        }

        @Override
        protected FileSystem getFileSystem() {
            return fileSystem;
        }

        @Override
        protected KerberosProperties getKerberosProperties(File kerberosConfigFile) {
            return testKerberosProperties;
        }

        @Override
        protected HdfsAdmin getHdfsAdmin() {
            return hdfsAdmin;
        }
    }
}

