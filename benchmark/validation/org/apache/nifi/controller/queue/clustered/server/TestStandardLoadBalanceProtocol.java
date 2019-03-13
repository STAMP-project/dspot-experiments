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
package org.apache.nifi.controller.queue.clustered.server;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Checksum;
import org.apache.nifi.controller.FlowController;
import org.apache.nifi.controller.queue.LoadBalancedFlowFileQueue;
import org.apache.nifi.controller.repository.ContentRepository;
import org.apache.nifi.controller.repository.FlowFileRecord;
import org.apache.nifi.controller.repository.FlowFileRepository;
import org.apache.nifi.controller.repository.RepositoryRecord;
import org.apache.nifi.controller.repository.claim.ContentClaim;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.provenance.ProvenanceRepository;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestStandardLoadBalanceProtocol {
    private final LoadBalanceAuthorizer ALWAYS_AUTHORIZED = ( sslSocket) -> sslSocket == null ? null : "authorized.mydomain.com";

    private FlowFileRepository flowFileRepo;

    private ContentRepository contentRepo;

    private ProvenanceRepository provenanceRepo;

    private FlowController flowController;

    private LoadBalancedFlowFileQueue flowFileQueue;

    private List<RepositoryRecord> flowFileRepoUpdateRecords;

    private List<ProvenanceEventRecord> provRepoUpdateRecords;

    private List<FlowFileRecord> flowFileQueuePutRecords;

    private List<FlowFileRecord> flowFileQueueReceiveRecords;

    private ConcurrentMap<ContentClaim, byte[]> claimContents;

    @Test
    public void testSimpleFlowFileTransaction() throws IOException {
        final StandardLoadBalanceProtocol protocol = new StandardLoadBalanceProtocol(flowFileRepo, contentRepo, provenanceRepo, flowController, ALWAYS_AUTHORIZED);
        final PipedInputStream serverInput = new PipedInputStream();
        final PipedOutputStream serverContentSource = new PipedOutputStream();
        serverInput.connect(serverContentSource);
        final ByteArrayOutputStream serverOutput = new ByteArrayOutputStream();
        // Write connection ID
        final Checksum checksum = new CRC32();
        final OutputStream checkedOutput = new CheckedOutputStream(serverContentSource, checksum);
        final DataOutputStream dos = new DataOutputStream(checkedOutput);
        dos.writeUTF("unit-test-connection-id");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("a", "A");
        attributes.put("uuid", "unit-test-id");
        attributes.put("b", "B");
        dos.write(CHECK_SPACE);
        dos.write(MORE_FLOWFILES);
        writeAttributes(attributes, dos);
        writeContent("hello".getBytes(), dos);
        dos.write(NO_MORE_FLOWFILES);
        dos.writeLong(checksum.getValue());
        dos.write(COMPLETE_TRANSACTION);
        protocol.receiveFlowFiles(serverInput, serverOutput, "Unit Test", 1);
        final byte[] serverResponse = serverOutput.toByteArray();
        Assert.assertEquals(3, serverResponse.length);
        Assert.assertEquals(SPACE_AVAILABLE, serverResponse[0]);
        Assert.assertEquals(CONFIRM_CHECKSUM, serverResponse[1]);
        Assert.assertEquals(CONFIRM_COMPLETE_TRANSACTION, serverResponse[2]);
        Assert.assertEquals(1, claimContents.size());
        final byte[] firstFlowFileContent = claimContents.values().iterator().next();
        Assert.assertArrayEquals("hello".getBytes(), firstFlowFileContent);
        Mockito.verify(flowFileRepo, Mockito.times(1)).updateRepository(ArgumentMatchers.anyCollection());
        Mockito.verify(provenanceRepo, Mockito.times(1)).registerEvents(ArgumentMatchers.anyList());
        Mockito.verify(flowFileQueue, Mockito.times(0)).putAll(ArgumentMatchers.anyCollection());
        Mockito.verify(flowFileQueue, Mockito.times(1)).receiveFromPeer(ArgumentMatchers.anyCollection());
    }

    @Test
    public void testMultipleFlowFiles() throws IOException {
        final StandardLoadBalanceProtocol protocol = new StandardLoadBalanceProtocol(flowFileRepo, contentRepo, provenanceRepo, flowController, ALWAYS_AUTHORIZED);
        final PipedInputStream serverInput = new PipedInputStream();
        final PipedOutputStream serverContentSource = new PipedOutputStream();
        serverInput.connect(serverContentSource);
        final ByteArrayOutputStream serverOutput = new ByteArrayOutputStream();
        // Write connection ID
        final Checksum checksum = new CRC32();
        final OutputStream checkedOutput = new CheckedOutputStream(serverContentSource, checksum);
        final DataOutputStream dos = new DataOutputStream(checkedOutput);
        dos.writeUTF("unit-test-connection-id");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("a", "A");
        attributes.put("uuid", "unit-test-id");
        attributes.put("b", "B");
        // Send 4 FlowFiles.
        dos.write(CHECK_SPACE);
        dos.write(MORE_FLOWFILES);
        writeAttributes(attributes, dos);
        writeContent("hello".getBytes(), dos);
        dos.write(MORE_FLOWFILES);
        writeAttributes(Collections.singletonMap("uuid", "unit-test-id-2"), dos);
        writeContent(null, dos);
        dos.write(MORE_FLOWFILES);
        writeAttributes(Collections.singletonMap("uuid", "unit-test-id-3"), dos);
        writeContent("greetings".getBytes(), dos);
        dos.write(MORE_FLOWFILES);
        writeAttributes(Collections.singletonMap("uuid", "unit-test-id-4"), dos);
        writeContent(new byte[0], dos);
        dos.write(NO_MORE_FLOWFILES);
        dos.writeLong(checksum.getValue());
        dos.write(COMPLETE_TRANSACTION);
        protocol.receiveFlowFiles(serverInput, serverOutput, "Unit Test", 1);
        final byte[] serverResponse = serverOutput.toByteArray();
        Assert.assertEquals(3, serverResponse.length);
        Assert.assertEquals(SPACE_AVAILABLE, serverResponse[0]);
        Assert.assertEquals(CONFIRM_CHECKSUM, serverResponse[1]);
        Assert.assertEquals(CONFIRM_COMPLETE_TRANSACTION, serverResponse[2]);
        Assert.assertEquals(1, claimContents.size());
        final byte[] bytes = claimContents.values().iterator().next();
        Assert.assertTrue(((Arrays.equals("hellogreetings".getBytes(), bytes)) || (Arrays.equals("greetingshello".getBytes(), bytes))));
        Assert.assertEquals(4, flowFileRepoUpdateRecords.size());
        Assert.assertEquals(4, provRepoUpdateRecords.size());
        Assert.assertEquals(0, flowFileQueuePutRecords.size());
        Assert.assertEquals(4, flowFileQueueReceiveRecords.size());
        Assert.assertTrue(provRepoUpdateRecords.stream().allMatch(( event) -> (event.getEventType()) == ProvenanceEventType.RECEIVE));
    }

    @Test
    public void testMultipleFlowFilesWithoutCheckingSpace() throws IOException {
        final StandardLoadBalanceProtocol protocol = new StandardLoadBalanceProtocol(flowFileRepo, contentRepo, provenanceRepo, flowController, ALWAYS_AUTHORIZED);
        final PipedInputStream serverInput = new PipedInputStream();
        final PipedOutputStream serverContentSource = new PipedOutputStream();
        serverInput.connect(serverContentSource);
        final ByteArrayOutputStream serverOutput = new ByteArrayOutputStream();
        // Write connection ID
        final Checksum checksum = new CRC32();
        final OutputStream checkedOutput = new CheckedOutputStream(serverContentSource, checksum);
        final DataOutputStream dos = new DataOutputStream(checkedOutput);
        dos.writeUTF("unit-test-connection-id");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("a", "A");
        attributes.put("uuid", "unit-test-id");
        attributes.put("b", "B");
        // Send 4 FlowFiles.
        dos.write(SKIP_SPACE_CHECK);
        dos.write(MORE_FLOWFILES);
        writeAttributes(attributes, dos);
        writeContent("hello".getBytes(), dos);
        dos.write(MORE_FLOWFILES);
        writeAttributes(Collections.singletonMap("uuid", "unit-test-id-2"), dos);
        writeContent(null, dos);
        dos.write(MORE_FLOWFILES);
        writeAttributes(Collections.singletonMap("uuid", "unit-test-id-3"), dos);
        writeContent("greetings".getBytes(), dos);
        dos.write(MORE_FLOWFILES);
        writeAttributes(Collections.singletonMap("uuid", "unit-test-id-4"), dos);
        writeContent(new byte[0], dos);
        dos.write(NO_MORE_FLOWFILES);
        dos.writeLong(checksum.getValue());
        dos.write(COMPLETE_TRANSACTION);
        protocol.receiveFlowFiles(serverInput, serverOutput, "Unit Test", 1);
        final byte[] serverResponse = serverOutput.toByteArray();
        Assert.assertEquals(2, serverResponse.length);
        Assert.assertEquals(CONFIRM_CHECKSUM, serverResponse[0]);
        Assert.assertEquals(CONFIRM_COMPLETE_TRANSACTION, serverResponse[1]);
        Assert.assertEquals(1, claimContents.size());
        final byte[] bytes = claimContents.values().iterator().next();
        Assert.assertTrue(((Arrays.equals("hellogreetings".getBytes(), bytes)) || (Arrays.equals("greetingshello".getBytes(), bytes))));
        Assert.assertEquals(4, flowFileRepoUpdateRecords.size());
        Assert.assertEquals(4, provRepoUpdateRecords.size());
        Assert.assertEquals(0, flowFileQueuePutRecords.size());
        Assert.assertEquals(4, flowFileQueueReceiveRecords.size());
        Assert.assertTrue(provRepoUpdateRecords.stream().allMatch(( event) -> (event.getEventType()) == ProvenanceEventType.RECEIVE));
    }

    @Test
    public void testEofExceptionMultipleFlowFiles() throws IOException {
        final StandardLoadBalanceProtocol protocol = new StandardLoadBalanceProtocol(flowFileRepo, contentRepo, provenanceRepo, flowController, ALWAYS_AUTHORIZED);
        final PipedInputStream serverInput = new PipedInputStream();
        final PipedOutputStream serverContentSource = new PipedOutputStream();
        serverInput.connect(serverContentSource);
        final ByteArrayOutputStream serverOutput = new ByteArrayOutputStream();
        // Write connection ID
        final Checksum checksum = new CRC32();
        final OutputStream checkedOutput = new CheckedOutputStream(serverContentSource, checksum);
        final DataOutputStream dos = new DataOutputStream(checkedOutput);
        dos.writeUTF("unit-test-connection-id");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("a", "A");
        attributes.put("uuid", "unit-test-id");
        attributes.put("b", "B");
        // Send 4 FlowFiles.
        dos.write(CHECK_SPACE);
        dos.write(MORE_FLOWFILES);
        writeAttributes(attributes, dos);
        writeContent("hello".getBytes(), dos);
        dos.write(MORE_FLOWFILES);
        writeAttributes(Collections.singletonMap("uuid", "unit-test-id-2"), dos);
        writeContent(null, dos);
        dos.write(MORE_FLOWFILES);
        writeAttributes(Collections.singletonMap("uuid", "unit-test-id-3"), dos);
        writeContent("greetings".getBytes(), dos);
        dos.write(MORE_FLOWFILES);
        writeAttributes(Collections.singletonMap("uuid", "unit-test-id-4"), dos);
        writeContent(new byte[0], dos);
        dos.flush();
        dos.close();
        try {
            protocol.receiveFlowFiles(serverInput, serverOutput, "Unit Test", 1);
            Assert.fail("Expected EOFException but none was thrown");
        } catch (final EOFException eof) {
            // expected
        }
        final byte[] serverResponse = serverOutput.toByteArray();
        Assert.assertEquals(1, serverResponse.length);
        Assert.assertEquals(SPACE_AVAILABLE, serverResponse[0]);
        Assert.assertEquals(1, claimContents.size());
        Assert.assertArrayEquals("hellogreetings".getBytes(), claimContents.values().iterator().next());
        Assert.assertEquals(0, flowFileRepoUpdateRecords.size());
        Assert.assertEquals(0, provRepoUpdateRecords.size());
        Assert.assertEquals(0, flowFileQueuePutRecords.size());
    }

    @Test
    public void testBadChecksum() throws IOException {
        final StandardLoadBalanceProtocol protocol = new StandardLoadBalanceProtocol(flowFileRepo, contentRepo, provenanceRepo, flowController, ALWAYS_AUTHORIZED);
        final PipedInputStream serverInput = new PipedInputStream();
        final PipedOutputStream serverContentSource = new PipedOutputStream();
        serverInput.connect(serverContentSource);
        final ByteArrayOutputStream serverOutput = new ByteArrayOutputStream();
        // Write connection ID
        final Checksum checksum = new CRC32();
        final OutputStream checkedOutput = new CheckedOutputStream(serverContentSource, checksum);
        final DataOutputStream dos = new DataOutputStream(checkedOutput);
        dos.writeUTF("unit-test-connection-id");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("uuid", "unit-test-id");
        dos.write(CHECK_SPACE);
        dos.write(MORE_FLOWFILES);
        writeAttributes(attributes, dos);
        writeContent("hello".getBytes(), dos);
        dos.write(NO_MORE_FLOWFILES);
        dos.writeLong(1L);// Write bad checksum.

        dos.write(COMPLETE_TRANSACTION);
        try {
            protocol.receiveFlowFiles(serverInput, serverOutput, "Unit Test", 1);
            Assert.fail("Expected TransactionAbortedException but none was thrown");
        } catch (final TransactionAbortedException e) {
            // expected
        }
        final byte[] serverResponse = serverOutput.toByteArray();
        Assert.assertEquals(2, serverResponse.length);
        Assert.assertEquals(SPACE_AVAILABLE, serverResponse[0]);
        Assert.assertEquals(REJECT_CHECKSUM, serverResponse[1]);
        Assert.assertEquals(1, claimContents.size());
        final byte[] firstFlowFileContent = claimContents.values().iterator().next();
        Assert.assertArrayEquals("hello".getBytes(), firstFlowFileContent);
        Mockito.verify(flowFileRepo, Mockito.times(0)).updateRepository(ArgumentMatchers.anyCollection());
        Mockito.verify(provenanceRepo, Mockito.times(0)).registerEvents(ArgumentMatchers.anyList());
        Mockito.verify(flowFileQueue, Mockito.times(0)).putAll(ArgumentMatchers.anyCollection());
        Mockito.verify(contentRepo, Mockito.times(1)).incrementClaimaintCount(claimContents.keySet().iterator().next());
        Mockito.verify(contentRepo, Mockito.times(2)).decrementClaimantCount(claimContents.keySet().iterator().next());
        Mockito.verify(contentRepo, Mockito.times(1)).remove(claimContents.keySet().iterator().next());
    }

    @Test
    public void testEofWritingContent() throws IOException {
        final StandardLoadBalanceProtocol protocol = new StandardLoadBalanceProtocol(flowFileRepo, contentRepo, provenanceRepo, flowController, ALWAYS_AUTHORIZED);
        final PipedInputStream serverInput = new PipedInputStream();
        final PipedOutputStream serverContentSource = new PipedOutputStream();
        serverInput.connect(serverContentSource);
        final ByteArrayOutputStream serverOutput = new ByteArrayOutputStream();
        // Write connection ID
        final Checksum checksum = new CRC32();
        final OutputStream checkedOutput = new CheckedOutputStream(serverContentSource, checksum);
        final DataOutputStream dos = new DataOutputStream(checkedOutput);
        dos.writeUTF("unit-test-connection-id");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("uuid", "unit-test-id");
        dos.write(CHECK_SPACE);
        dos.write(MORE_FLOWFILES);
        writeAttributes(attributes, dos);
        // Indicate 45 byte data frame, then stop after 5 bytes.
        dos.write(DATA_FRAME_FOLLOWS);
        dos.writeShort(45);
        dos.write("hello".getBytes());
        dos.flush();
        dos.close();
        try {
            protocol.receiveFlowFiles(serverInput, serverOutput, "Unit Test", 1);
            Assert.fail("Expected EOFException but none was thrown");
        } catch (final EOFException e) {
            // expected
        }
        final byte[] serverResponse = serverOutput.toByteArray();
        Assert.assertEquals(1, serverResponse.length);
        Assert.assertEquals(SPACE_AVAILABLE, serverResponse[0]);
        Assert.assertEquals(1, claimContents.size());
        final byte[] firstFlowFileContent = claimContents.values().iterator().next();
        Assert.assertArrayEquals(new byte[0], firstFlowFileContent);
        Mockito.verify(flowFileRepo, Mockito.times(0)).updateRepository(ArgumentMatchers.anyCollection());
        Mockito.verify(provenanceRepo, Mockito.times(0)).registerEvents(ArgumentMatchers.anyList());
        Mockito.verify(flowFileQueue, Mockito.times(0)).putAll(ArgumentMatchers.anyCollection());
        Mockito.verify(contentRepo, Mockito.times(0)).incrementClaimaintCount(claimContents.keySet().iterator().next());
        Mockito.verify(contentRepo, Mockito.times(0)).decrementClaimantCount(claimContents.keySet().iterator().next());
        Mockito.verify(contentRepo, Mockito.times(1)).remove(claimContents.keySet().iterator().next());
    }

    @Test
    public void testAbortAfterChecksumConfirmation() throws IOException {
        final StandardLoadBalanceProtocol protocol = new StandardLoadBalanceProtocol(flowFileRepo, contentRepo, provenanceRepo, flowController, ALWAYS_AUTHORIZED);
        final PipedInputStream serverInput = new PipedInputStream();
        final PipedOutputStream serverContentSource = new PipedOutputStream();
        serverInput.connect(serverContentSource);
        final ByteArrayOutputStream serverOutput = new ByteArrayOutputStream();
        // Write connection ID
        final Checksum checksum = new CRC32();
        final OutputStream checkedOutput = new CheckedOutputStream(serverContentSource, checksum);
        final DataOutputStream dos = new DataOutputStream(checkedOutput);
        dos.writeUTF("unit-test-connection-id");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("uuid", "unit-test-id");
        dos.write(CHECK_SPACE);
        dos.write(MORE_FLOWFILES);
        writeAttributes(attributes, dos);
        writeContent("hello".getBytes(), dos);
        dos.write(NO_MORE_FLOWFILES);
        dos.writeLong(checksum.getValue());
        dos.write(ABORT_TRANSACTION);
        try {
            protocol.receiveFlowFiles(serverInput, serverOutput, "Unit Test", 1);
            Assert.fail("Expected TransactionAbortedException but none was thrown");
        } catch (final TransactionAbortedException e) {
            // expected
        }
        final byte[] serverResponse = serverOutput.toByteArray();
        Assert.assertEquals(2, serverResponse.length);
        Assert.assertEquals(SPACE_AVAILABLE, serverResponse[0]);
        Assert.assertEquals(CONFIRM_CHECKSUM, serverResponse[1]);
        Assert.assertEquals(1, claimContents.size());
        final byte[] firstFlowFileContent = claimContents.values().iterator().next();
        Assert.assertArrayEquals("hello".getBytes(), firstFlowFileContent);
        Mockito.verify(flowFileRepo, Mockito.times(0)).updateRepository(ArgumentMatchers.anyCollection());
        Mockito.verify(provenanceRepo, Mockito.times(0)).registerEvents(ArgumentMatchers.anyList());
        Mockito.verify(flowFileQueue, Mockito.times(0)).putAll(ArgumentMatchers.anyCollection());
        Mockito.verify(contentRepo, Mockito.times(1)).incrementClaimaintCount(claimContents.keySet().iterator().next());
        Mockito.verify(contentRepo, Mockito.times(2)).decrementClaimantCount(claimContents.keySet().iterator().next());
        Mockito.verify(contentRepo, Mockito.times(1)).remove(claimContents.keySet().iterator().next());
    }

    @Test
    public void testFlowFileNoContent() throws IOException {
        final StandardLoadBalanceProtocol protocol = new StandardLoadBalanceProtocol(flowFileRepo, contentRepo, provenanceRepo, flowController, ALWAYS_AUTHORIZED);
        final PipedInputStream serverInput = new PipedInputStream();
        final PipedOutputStream serverContentSource = new PipedOutputStream();
        serverInput.connect(serverContentSource);
        final ByteArrayOutputStream serverOutput = new ByteArrayOutputStream();
        // Write connection ID
        final Checksum checksum = new CRC32();
        final OutputStream checkedOutput = new CheckedOutputStream(serverContentSource, checksum);
        final DataOutputStream dos = new DataOutputStream(checkedOutput);
        dos.writeUTF("unit-test-connection-id");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("uuid", "unit-test-id");
        dos.write(CHECK_SPACE);
        dos.write(MORE_FLOWFILES);
        writeAttributes(attributes, dos);
        writeContent(null, dos);
        dos.write(NO_MORE_FLOWFILES);
        dos.writeLong(checksum.getValue());
        dos.write(COMPLETE_TRANSACTION);
        protocol.receiveFlowFiles(serverInput, serverOutput, "Unit Test", 1);
        final byte[] serverResponse = serverOutput.toByteArray();
        Assert.assertEquals(3, serverResponse.length);
        Assert.assertEquals(SPACE_AVAILABLE, serverResponse[0]);
        Assert.assertEquals(CONFIRM_CHECKSUM, serverResponse[1]);
        Assert.assertEquals(CONFIRM_COMPLETE_TRANSACTION, serverResponse[2]);
        Assert.assertEquals(1, claimContents.size());
        Assert.assertEquals(0, claimContents.values().iterator().next().length);
        Mockito.verify(flowFileRepo, Mockito.times(1)).updateRepository(ArgumentMatchers.anyCollection());
        Mockito.verify(provenanceRepo, Mockito.times(1)).registerEvents(ArgumentMatchers.anyList());
        Mockito.verify(flowFileQueue, Mockito.times(0)).putAll(ArgumentMatchers.anyCollection());
        Mockito.verify(flowFileQueue, Mockito.times(1)).receiveFromPeer(ArgumentMatchers.anyCollection());
    }
}

