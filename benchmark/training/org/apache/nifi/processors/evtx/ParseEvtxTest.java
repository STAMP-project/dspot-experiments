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
package org.apache.nifi.processors.evtx;


import CoreAttributes.FILENAME;
import ParseEvtx.EVTX_EXTENSION;
import ParseEvtx.FILE;
import ParseEvtx.GRANULARITY;
import ParseEvtx.PROPERTY_DESCRIPTORS;
import ParseEvtx.RECORD;
import ParseEvtx.RELATIONSHIPS;
import ParseEvtx.REL_BAD_CHUNK;
import ParseEvtx.REL_FAILURE;
import ParseEvtx.REL_ORIGINAL;
import ParseEvtx.REL_SUCCESS;
import ParseEvtx.XML_EXTENSION;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.io.OutputStreamCallback;
import org.apache.nifi.processors.evtx.parser.ChunkHeader;
import org.apache.nifi.processors.evtx.parser.FileHeader;
import org.apache.nifi.processors.evtx.parser.FileHeaderFactory;
import org.apache.nifi.processors.evtx.parser.MalformedChunkException;
import org.apache.nifi.processors.evtx.parser.Record;
import org.apache.nifi.processors.evtx.parser.bxml.RootNode;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.xml.sax.SAXException;


@RunWith(MockitoJUnitRunner.class)
public class ParseEvtxTest {
    public static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

    public static final String USER_DATA = "UserData";

    public static final String EVENT_DATA = "EventData";

    public static final Set DATA_TAGS = new HashSet<>(Arrays.asList(ParseEvtxTest.EVENT_DATA, ParseEvtxTest.USER_DATA));

    @Mock
    FileHeaderFactory fileHeaderFactory;

    @Mock
    MalformedChunkHandler malformedChunkHandler;

    @Mock
    RootNodeHandlerFactory rootNodeHandlerFactory;

    @Mock
    ResultProcessor resultProcessor;

    @Mock
    ComponentLog componentLog;

    @Mock
    InputStream in;

    @Mock
    OutputStream out;

    @Mock
    FileHeader fileHeader;

    ParseEvtx parseEvtx;

    @Test
    public void testGetNameFile() {
        String basename = "basename";
        Assert.assertEquals((basename + ".xml"), parseEvtx.getName(basename, null, null, XML_EXTENSION));
    }

    @Test
    public void testGetNameFileChunk() {
        String basename = "basename";
        Assert.assertEquals((basename + "-chunk1.xml"), parseEvtx.getName(basename, 1, null, XML_EXTENSION));
    }

    @Test
    public void testGetNameFileChunkRecord() {
        String basename = "basename";
        Assert.assertEquals((basename + "-chunk1-record2.xml"), parseEvtx.getName(basename, 1, 2, XML_EXTENSION));
    }

    @Test
    public void testGetBasenameEvtxExtension() {
        String basename = "basename";
        FlowFile flowFile = Mockito.mock(FlowFile.class);
        Mockito.when(flowFile.getAttribute(FILENAME.key())).thenReturn((basename + ".evtx"));
        Assert.assertEquals(basename, parseEvtx.getBasename(flowFile, componentLog));
        Mockito.verifyNoMoreInteractions(componentLog);
    }

    @Test
    public void testGetBasenameExtension() {
        String basename = "basename.wrongextension";
        FlowFile flowFile = Mockito.mock(FlowFile.class);
        ComponentLog componentLog = Mockito.mock(ComponentLog.class);
        Mockito.when(flowFile.getAttribute(FILENAME.key())).thenReturn(basename);
        Assert.assertEquals(basename, parseEvtx.getBasename(flowFile, componentLog));
        Mockito.verify(componentLog).warn(ArgumentMatchers.anyString(), ArgumentMatchers.isA(Object[].class));
    }

    @Test
    public void testGetRelationships() {
        Assert.assertEquals(RELATIONSHIPS, parseEvtx.getRelationships());
    }

    @Test
    public void testGetSupportedPropertyDescriptors() {
        Assert.assertEquals(PROPERTY_DESCRIPTORS, parseEvtx.getSupportedPropertyDescriptors());
    }

    @Test
    public void testProcessFileGranularity() throws IOException, XMLStreamException, MalformedChunkException {
        String basename = "basename";
        int chunkNum = 5;
        int offset = 10001;
        byte[] badChunk = new byte[]{ 8 };
        RootNodeHandler rootNodeHandler = Mockito.mock(RootNodeHandler.class);
        Mockito.when(rootNodeHandlerFactory.create(out)).thenReturn(rootNodeHandler);
        ChunkHeader chunkHeader1 = Mockito.mock(ChunkHeader.class);
        ChunkHeader chunkHeader2 = Mockito.mock(ChunkHeader.class);
        Record record1 = Mockito.mock(Record.class);
        Record record2 = Mockito.mock(Record.class);
        Record record3 = Mockito.mock(Record.class);
        RootNode rootNode1 = Mockito.mock(RootNode.class);
        RootNode rootNode2 = Mockito.mock(RootNode.class);
        RootNode rootNode3 = Mockito.mock(RootNode.class);
        ProcessSession session = Mockito.mock(ProcessSession.class);
        FlowFile flowFile = Mockito.mock(FlowFile.class);
        AtomicReference<Exception> reference = new AtomicReference<>();
        MalformedChunkException malformedChunkException = new MalformedChunkException("Test", null, offset, chunkNum, badChunk);
        Mockito.when(record1.getRootNode()).thenReturn(rootNode1);
        Mockito.when(record2.getRootNode()).thenReturn(rootNode2);
        Mockito.when(record3.getRootNode()).thenReturn(rootNode3);
        Mockito.when(fileHeader.hasNext()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        Mockito.when(fileHeader.next()).thenThrow(malformedChunkException).thenReturn(chunkHeader1).thenReturn(chunkHeader2).thenReturn(null);
        Mockito.when(chunkHeader1.hasNext()).thenReturn(true).thenReturn(false);
        Mockito.when(chunkHeader1.next()).thenReturn(record1).thenReturn(null);
        Mockito.when(chunkHeader2.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
        Mockito.when(chunkHeader2.next()).thenReturn(record2).thenReturn(record3).thenReturn(null);
        parseEvtx.processFileGranularity(session, componentLog, flowFile, basename, reference, in, out);
        Mockito.verify(malformedChunkHandler).handle(flowFile, session, parseEvtx.getName(basename, chunkNum, null, EVTX_EXTENSION), badChunk);
        Mockito.verify(rootNodeHandler).handle(rootNode1);
        Mockito.verify(rootNodeHandler).handle(rootNode2);
        Mockito.verify(rootNodeHandler).handle(rootNode3);
        Mockito.verify(rootNodeHandler).close();
    }

    @Test
    public void testProcessChunkGranularity() throws IOException, XMLStreamException, MalformedChunkException {
        String basename = "basename";
        int chunkNum = 5;
        int offset = 10001;
        byte[] badChunk = new byte[]{ 8 };
        RootNodeHandler rootNodeHandler1 = Mockito.mock(RootNodeHandler.class);
        RootNodeHandler rootNodeHandler2 = Mockito.mock(RootNodeHandler.class);
        OutputStream out2 = Mockito.mock(OutputStream.class);
        Mockito.when(rootNodeHandlerFactory.create(out)).thenReturn(rootNodeHandler1);
        Mockito.when(rootNodeHandlerFactory.create(out2)).thenReturn(rootNodeHandler2);
        ChunkHeader chunkHeader1 = Mockito.mock(ChunkHeader.class);
        ChunkHeader chunkHeader2 = Mockito.mock(ChunkHeader.class);
        Record record1 = Mockito.mock(Record.class);
        Record record2 = Mockito.mock(Record.class);
        Record record3 = Mockito.mock(Record.class);
        RootNode rootNode1 = Mockito.mock(RootNode.class);
        RootNode rootNode2 = Mockito.mock(RootNode.class);
        RootNode rootNode3 = Mockito.mock(RootNode.class);
        ProcessSession session = Mockito.mock(ProcessSession.class);
        FlowFile flowFile = Mockito.mock(FlowFile.class);
        FlowFile created1 = Mockito.mock(FlowFile.class);
        FlowFile updated1 = Mockito.mock(FlowFile.class);
        FlowFile created2 = Mockito.mock(FlowFile.class);
        FlowFile updated2 = Mockito.mock(FlowFile.class);
        MalformedChunkException malformedChunkException = new MalformedChunkException("Test", null, offset, chunkNum, badChunk);
        Mockito.when(session.create(flowFile)).thenReturn(created1).thenReturn(created2).thenReturn(null);
        Mockito.when(session.write(ArgumentMatchers.eq(created1), ArgumentMatchers.any(OutputStreamCallback.class))).thenAnswer(( invocation) -> {
            ((OutputStreamCallback) (invocation.getArguments()[1])).process(out);
            return updated1;
        });
        Mockito.when(session.write(ArgumentMatchers.eq(created2), ArgumentMatchers.any(OutputStreamCallback.class))).thenAnswer(( invocation) -> {
            ((OutputStreamCallback) (invocation.getArguments()[1])).process(out2);
            return updated2;
        });
        Mockito.when(record1.getRootNode()).thenReturn(rootNode1);
        Mockito.when(record2.getRootNode()).thenReturn(rootNode2);
        Mockito.when(record3.getRootNode()).thenReturn(rootNode3);
        Mockito.when(fileHeader.hasNext()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        Mockito.when(fileHeader.next()).thenThrow(malformedChunkException).thenReturn(chunkHeader1).thenReturn(chunkHeader2).thenReturn(null);
        Mockito.when(chunkHeader1.hasNext()).thenReturn(true).thenReturn(false);
        Mockito.when(chunkHeader1.next()).thenReturn(record1).thenReturn(null);
        Mockito.when(chunkHeader2.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
        Mockito.when(chunkHeader2.next()).thenReturn(record2).thenReturn(record3).thenReturn(null);
        parseEvtx.processChunkGranularity(session, componentLog, flowFile, basename, in);
        Mockito.verify(malformedChunkHandler).handle(flowFile, session, parseEvtx.getName(basename, chunkNum, null, EVTX_EXTENSION), badChunk);
        Mockito.verify(rootNodeHandler1).handle(rootNode1);
        Mockito.verify(rootNodeHandler1).close();
        Mockito.verify(rootNodeHandler2).handle(rootNode2);
        Mockito.verify(rootNodeHandler2).handle(rootNode3);
        Mockito.verify(rootNodeHandler2).close();
    }

    @Test
    public void testProcess1RecordGranularity() throws IOException, XMLStreamException, MalformedChunkException {
        String basename = "basename";
        int chunkNum = 5;
        int offset = 10001;
        byte[] badChunk = new byte[]{ 8 };
        RootNodeHandler rootNodeHandler1 = Mockito.mock(RootNodeHandler.class);
        RootNodeHandler rootNodeHandler2 = Mockito.mock(RootNodeHandler.class);
        RootNodeHandler rootNodeHandler3 = Mockito.mock(RootNodeHandler.class);
        OutputStream out2 = Mockito.mock(OutputStream.class);
        OutputStream out3 = Mockito.mock(OutputStream.class);
        Mockito.when(rootNodeHandlerFactory.create(out)).thenReturn(rootNodeHandler1);
        Mockito.when(rootNodeHandlerFactory.create(out2)).thenReturn(rootNodeHandler2);
        Mockito.when(rootNodeHandlerFactory.create(out3)).thenReturn(rootNodeHandler3);
        ChunkHeader chunkHeader1 = Mockito.mock(ChunkHeader.class);
        ChunkHeader chunkHeader2 = Mockito.mock(ChunkHeader.class);
        Record record1 = Mockito.mock(Record.class);
        Record record2 = Mockito.mock(Record.class);
        Record record3 = Mockito.mock(Record.class);
        RootNode rootNode1 = Mockito.mock(RootNode.class);
        RootNode rootNode2 = Mockito.mock(RootNode.class);
        RootNode rootNode3 = Mockito.mock(RootNode.class);
        ProcessSession session = Mockito.mock(ProcessSession.class);
        FlowFile flowFile = Mockito.mock(FlowFile.class);
        FlowFile created1 = Mockito.mock(FlowFile.class);
        FlowFile updated1 = Mockito.mock(FlowFile.class);
        FlowFile created2 = Mockito.mock(FlowFile.class);
        FlowFile updated2 = Mockito.mock(FlowFile.class);
        FlowFile created3 = Mockito.mock(FlowFile.class);
        FlowFile updated3 = Mockito.mock(FlowFile.class);
        MalformedChunkException malformedChunkException = new MalformedChunkException("Test", null, offset, chunkNum, badChunk);
        Mockito.when(session.create(flowFile)).thenReturn(created1).thenReturn(created2).thenReturn(created3).thenReturn(null);
        Mockito.when(session.write(ArgumentMatchers.eq(created1), ArgumentMatchers.any(OutputStreamCallback.class))).thenAnswer(( invocation) -> {
            ((OutputStreamCallback) (invocation.getArguments()[1])).process(out);
            return updated1;
        });
        Mockito.when(session.write(ArgumentMatchers.eq(created2), ArgumentMatchers.any(OutputStreamCallback.class))).thenAnswer(( invocation) -> {
            ((OutputStreamCallback) (invocation.getArguments()[1])).process(out2);
            return updated2;
        });
        Mockito.when(session.write(ArgumentMatchers.eq(created3), ArgumentMatchers.any(OutputStreamCallback.class))).thenAnswer(( invocation) -> {
            ((OutputStreamCallback) (invocation.getArguments()[1])).process(out3);
            return updated3;
        });
        Mockito.when(record1.getRootNode()).thenReturn(rootNode1);
        Mockito.when(record2.getRootNode()).thenReturn(rootNode2);
        Mockito.when(record3.getRootNode()).thenReturn(rootNode3);
        Mockito.when(fileHeader.hasNext()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        Mockito.when(fileHeader.next()).thenThrow(malformedChunkException).thenReturn(chunkHeader1).thenReturn(chunkHeader2).thenReturn(null);
        Mockito.when(chunkHeader1.hasNext()).thenReturn(true).thenReturn(false);
        Mockito.when(chunkHeader1.next()).thenReturn(record1).thenReturn(null);
        Mockito.when(chunkHeader2.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
        Mockito.when(chunkHeader2.next()).thenReturn(record2).thenReturn(record3).thenReturn(null);
        parseEvtx.processRecordGranularity(session, componentLog, flowFile, basename, in);
        Mockito.verify(malformedChunkHandler).handle(flowFile, session, parseEvtx.getName(basename, chunkNum, null, EVTX_EXTENSION), badChunk);
        Mockito.verify(rootNodeHandler1).handle(rootNode1);
        Mockito.verify(rootNodeHandler1).close();
        Mockito.verify(rootNodeHandler2).handle(rootNode2);
        Mockito.verify(rootNodeHandler2).close();
        Mockito.verify(rootNodeHandler3).handle(rootNode3);
        Mockito.verify(rootNodeHandler3).close();
    }

    @Test
    public void fileGranularityLifecycleTest() throws IOException, ParserConfigurationException, SAXException {
        String baseName = "testFileName";
        String name = baseName + ".evtx";
        TestRunner testRunner = TestRunners.newTestRunner(ParseEvtx.class);
        testRunner.setProperty(GRANULARITY, FILE);
        Map<String, String> attributes = new HashMap<>();
        attributes.put(FILENAME.key(), name);
        testRunner.enqueue(this.getClass().getClassLoader().getResourceAsStream("application-logs.evtx"), attributes);
        testRunner.run();
        List<MockFlowFile> originalFlowFiles = testRunner.getFlowFilesForRelationship(REL_ORIGINAL);
        Assert.assertEquals(1, originalFlowFiles.size());
        MockFlowFile originalFlowFile = originalFlowFiles.get(0);
        originalFlowFile.assertAttributeEquals(FILENAME.key(), name);
        originalFlowFile.assertContentEquals(this.getClass().getClassLoader().getResourceAsStream("application-logs.evtx"));
        // We expect the same bad chunks no matter the granularity
        List<MockFlowFile> badChunkFlowFiles = testRunner.getFlowFilesForRelationship(REL_BAD_CHUNK);
        Assert.assertEquals(2, badChunkFlowFiles.size());
        badChunkFlowFiles.get(0).assertAttributeEquals(FILENAME.key(), parseEvtx.getName(baseName, 1, null, EVTX_EXTENSION));
        badChunkFlowFiles.get(1).assertAttributeEquals(FILENAME.key(), parseEvtx.getName(baseName, 2, null, EVTX_EXTENSION));
        List<MockFlowFile> failureFlowFiles = testRunner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(1, failureFlowFiles.size());
        validateFlowFiles(failureFlowFiles);
        // We expect the same number of records to come out no matter the granularity
        Assert.assertEquals(960, validateFlowFiles(failureFlowFiles));
        // Whole file fails if there is a failure parsing
        List<MockFlowFile> successFlowFiles = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(0, successFlowFiles.size());
    }

    @Test
    public void chunkGranularityLifecycleTest() throws IOException, ParserConfigurationException, SAXException {
        String baseName = "testFileName";
        String name = baseName + ".evtx";
        TestRunner testRunner = TestRunners.newTestRunner(ParseEvtx.class);
        Map<String, String> attributes = new HashMap<>();
        attributes.put(FILENAME.key(), name);
        testRunner.enqueue(this.getClass().getClassLoader().getResourceAsStream("application-logs.evtx"), attributes);
        testRunner.run();
        List<MockFlowFile> originalFlowFiles = testRunner.getFlowFilesForRelationship(REL_ORIGINAL);
        Assert.assertEquals(1, originalFlowFiles.size());
        MockFlowFile originalFlowFile = originalFlowFiles.get(0);
        originalFlowFile.assertAttributeEquals(FILENAME.key(), name);
        originalFlowFile.assertContentEquals(this.getClass().getClassLoader().getResourceAsStream("application-logs.evtx"));
        // We expect the same bad chunks no matter the granularity
        List<MockFlowFile> badChunkFlowFiles = testRunner.getFlowFilesForRelationship(REL_BAD_CHUNK);
        Assert.assertEquals(2, badChunkFlowFiles.size());
        badChunkFlowFiles.get(0).assertAttributeEquals(FILENAME.key(), parseEvtx.getName(baseName, 1, null, EVTX_EXTENSION));
        badChunkFlowFiles.get(1).assertAttributeEquals(FILENAME.key(), parseEvtx.getName(baseName, 2, null, EVTX_EXTENSION));
        List<MockFlowFile> failureFlowFiles = testRunner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(1, failureFlowFiles.size());
        List<MockFlowFile> successFlowFiles = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(8, successFlowFiles.size());
        // We expect the same number of records to come out no matter the granularity
        Assert.assertEquals(960, ((validateFlowFiles(successFlowFiles)) + (validateFlowFiles(failureFlowFiles))));
    }

    @Test
    public void recordGranularityLifecycleTest() throws IOException, ParserConfigurationException, SAXException {
        String baseName = "testFileName";
        String name = baseName + ".evtx";
        TestRunner testRunner = TestRunners.newTestRunner(ParseEvtx.class);
        testRunner.setProperty(GRANULARITY, RECORD);
        Map<String, String> attributes = new HashMap<>();
        attributes.put(FILENAME.key(), name);
        testRunner.enqueue(this.getClass().getClassLoader().getResourceAsStream("application-logs.evtx"), attributes);
        testRunner.run();
        List<MockFlowFile> originalFlowFiles = testRunner.getFlowFilesForRelationship(REL_ORIGINAL);
        Assert.assertEquals(1, originalFlowFiles.size());
        MockFlowFile originalFlowFile = originalFlowFiles.get(0);
        originalFlowFile.assertAttributeEquals(FILENAME.key(), name);
        originalFlowFile.assertContentEquals(this.getClass().getClassLoader().getResourceAsStream("application-logs.evtx"));
        // We expect the same bad chunks no matter the granularity
        List<MockFlowFile> badChunkFlowFiles = testRunner.getFlowFilesForRelationship(REL_BAD_CHUNK);
        Assert.assertEquals(2, badChunkFlowFiles.size());
        badChunkFlowFiles.get(0).assertAttributeEquals(FILENAME.key(), parseEvtx.getName(baseName, 1, null, EVTX_EXTENSION));
        badChunkFlowFiles.get(1).assertAttributeEquals(FILENAME.key(), parseEvtx.getName(baseName, 2, null, EVTX_EXTENSION));
        List<MockFlowFile> failureFlowFiles = testRunner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(0, failureFlowFiles.size());
        // Whole file fails if there is a failure parsing
        List<MockFlowFile> successFlowFiles = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(960, successFlowFiles.size());
        // We expect the same number of records to come out no matter the granularity
        Assert.assertEquals(960, validateFlowFiles(successFlowFiles));
    }
}

