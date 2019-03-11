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
package org.apache.nifi.remote.protocol.socket;


import RequestType.RECEIVE_FLOWFILES;
import RequestType.SEND_FLOWFILES;
import ResponseCode.BAD_CHECKSUM;
import ResponseCode.CONFIRM_TRANSACTION;
import ResponseCode.CONTINUE_TRANSACTION;
import ResponseCode.FINISH_TRANSACTION;
import ResponseCode.MORE_DATA;
import ResponseCode.NO_MORE_DATA;
import ResponseCode.TRANSACTION_FINISHED;
import ResponseCode.TRANSACTION_FINISHED_BUT_DESTINATION_FULL;
import Transaction.TransactionState.TRANSACTION_STARTED;
import TransferDirection.RECEIVE;
import TransferDirection.SEND;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.nifi.remote.codec.FlowFileCodec;
import org.apache.nifi.remote.codec.StandardFlowFileCodec;
import org.apache.nifi.remote.protocol.DataPacket;
import org.apache.nifi.remote.protocol.RequestType;
import org.apache.nifi.remote.protocol.Response;
import org.apache.nifi.remote.protocol.SiteToSiteTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestSocketClientTransaction {
    private Logger logger = LoggerFactory.getLogger(TestSocketClientTransaction.class);

    private FlowFileCodec codec = new StandardFlowFileCodec();

    @Test
    public void testReceiveZeroFlowFile() throws IOException {
        ByteArrayOutputStream serverResponseBos = new ByteArrayOutputStream();
        DataOutputStream serverResponse = new DataOutputStream(serverResponseBos);
        NO_MORE_DATA.writeResponse(serverResponse);
        ByteArrayInputStream bis = new ByteArrayInputStream(serverResponseBos.toByteArray());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        SocketClientTransaction transaction = getClientTransaction(bis, bos, RECEIVE);
        SiteToSiteTestUtils.execReceiveZeroFlowFile(transaction);
        // Verify what client has sent.
        DataInputStream sentByClient = new DataInputStream(new ByteArrayInputStream(bos.toByteArray()));
        Assert.assertEquals(RECEIVE_FLOWFILES, RequestType.readRequestType(sentByClient));
        Assert.assertEquals((-1), sentByClient.read());
    }

    @Test
    public void testReceiveOneFlowFile() throws IOException {
        ByteArrayOutputStream serverResponseBos = new ByteArrayOutputStream();
        DataOutputStream serverResponse = new DataOutputStream(serverResponseBos);
        MORE_DATA.writeResponse(serverResponse);
        codec.encode(SiteToSiteTestUtils.createDataPacket("contents on server 1"), serverResponse);
        FINISH_TRANSACTION.writeResponse(serverResponse);
        CONFIRM_TRANSACTION.writeResponse(serverResponse, "Checksum has been verified at server.");
        ByteArrayInputStream bis = new ByteArrayInputStream(serverResponseBos.toByteArray());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        SocketClientTransaction transaction = getClientTransaction(bis, bos, RECEIVE);
        SiteToSiteTestUtils.execReceiveOneFlowFile(transaction);
        // Verify what client has sent.
        DataInputStream sentByClient = new DataInputStream(new ByteArrayInputStream(bos.toByteArray()));
        Assert.assertEquals(RECEIVE_FLOWFILES, RequestType.readRequestType(sentByClient));
        Response confirmResponse = Response.read(sentByClient);
        Assert.assertEquals(CONFIRM_TRANSACTION, confirmResponse.getCode());
        Assert.assertEquals("Checksum should be calculated at client", "3680976076", confirmResponse.getMessage());
        Response completeResponse = Response.read(sentByClient);
        Assert.assertEquals(TRANSACTION_FINISHED, completeResponse.getCode());
        Assert.assertEquals((-1), sentByClient.read());
    }

    @Test
    public void testReceiveTwoFlowFiles() throws IOException {
        ByteArrayOutputStream serverResponseBos = new ByteArrayOutputStream();
        DataOutputStream serverResponse = new DataOutputStream(serverResponseBos);
        MORE_DATA.writeResponse(serverResponse);
        codec.encode(SiteToSiteTestUtils.createDataPacket("contents on server 1"), serverResponse);
        CONTINUE_TRANSACTION.writeResponse(serverResponse);
        codec.encode(SiteToSiteTestUtils.createDataPacket("contents on server 2"), serverResponse);
        FINISH_TRANSACTION.writeResponse(serverResponse);
        CONFIRM_TRANSACTION.writeResponse(serverResponse, "Checksum has been verified at server.");
        ByteArrayInputStream bis = new ByteArrayInputStream(serverResponseBos.toByteArray());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        SocketClientTransaction transaction = getClientTransaction(bis, bos, RECEIVE);
        Assert.assertEquals(TRANSACTION_STARTED, transaction.getState());
        SiteToSiteTestUtils.execReceiveTwoFlowFiles(transaction);
        // Verify what client has sent.
        DataInputStream sentByClient = new DataInputStream(new ByteArrayInputStream(bos.toByteArray()));
        Assert.assertEquals(RECEIVE_FLOWFILES, RequestType.readRequestType(sentByClient));
        Response confirmResponse = Response.read(sentByClient);
        Assert.assertEquals(CONFIRM_TRANSACTION, confirmResponse.getCode());
        Assert.assertEquals("Checksum should be calculated at client", "2969091230", confirmResponse.getMessage());
        Response completeResponse = Response.read(sentByClient);
        Assert.assertEquals(TRANSACTION_FINISHED, completeResponse.getCode());
        Assert.assertEquals((-1), sentByClient.read());
    }

    @Test
    public void testReceiveWithInvalidChecksum() throws IOException {
        ByteArrayOutputStream serverResponseBos = new ByteArrayOutputStream();
        DataOutputStream serverResponse = new DataOutputStream(serverResponseBos);
        MORE_DATA.writeResponse(serverResponse);
        codec.encode(SiteToSiteTestUtils.createDataPacket("contents on server 1"), serverResponse);
        CONTINUE_TRANSACTION.writeResponse(serverResponse);
        codec.encode(SiteToSiteTestUtils.createDataPacket("contents on server 2"), serverResponse);
        FINISH_TRANSACTION.writeResponse(serverResponse);
        BAD_CHECKSUM.writeResponse(serverResponse);
        ByteArrayInputStream bis = new ByteArrayInputStream(serverResponseBos.toByteArray());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        SocketClientTransaction transaction = getClientTransaction(bis, bos, RECEIVE);
        SiteToSiteTestUtils.execReceiveWithInvalidChecksum(transaction);
        // Verify what client has sent.
        DataInputStream sentByClient = new DataInputStream(new ByteArrayInputStream(bos.toByteArray()));
        Assert.assertEquals(RECEIVE_FLOWFILES, RequestType.readRequestType(sentByClient));
        Response confirmResponse = Response.read(sentByClient);
        Assert.assertEquals(CONFIRM_TRANSACTION, confirmResponse.getCode());
        Assert.assertEquals("Checksum should be calculated at client", "2969091230", confirmResponse.getMessage());
        Assert.assertEquals((-1), sentByClient.read());
    }

    @Test
    public void testSendZeroFlowFile() throws IOException {
        ByteArrayOutputStream serverResponseBos = new ByteArrayOutputStream();
        ByteArrayInputStream bis = new ByteArrayInputStream(serverResponseBos.toByteArray());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        SocketClientTransaction transaction = getClientTransaction(bis, bos, SEND);
        SiteToSiteTestUtils.execSendZeroFlowFile(transaction);
        // Verify what client has sent.
        DataInputStream sentByClient = new DataInputStream(new ByteArrayInputStream(bos.toByteArray()));
        Assert.assertEquals(SEND_FLOWFILES, RequestType.readRequestType(sentByClient));
        Assert.assertEquals((-1), sentByClient.read());
    }

    @Test
    public void testSendOneFlowFile() throws IOException {
        ByteArrayOutputStream serverResponseBos = new ByteArrayOutputStream();
        DataOutputStream serverResponse = new DataOutputStream(serverResponseBos);
        CONFIRM_TRANSACTION.writeResponse(serverResponse, "2946083981");
        TRANSACTION_FINISHED.writeResponse(serverResponse);
        ByteArrayInputStream bis = new ByteArrayInputStream(serverResponseBos.toByteArray());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        SocketClientTransaction transaction = getClientTransaction(bis, bos, SEND);
        SiteToSiteTestUtils.execSendOneFlowFile(transaction);
        // Verify what client has sent.
        DataInputStream sentByClient = new DataInputStream(new ByteArrayInputStream(bos.toByteArray()));
        Assert.assertEquals(SEND_FLOWFILES, RequestType.readRequestType(sentByClient));
        DataPacket packetByClient = codec.decode(sentByClient);
        Assert.assertEquals("contents on client 1", SiteToSiteTestUtils.readContents(packetByClient));
        Response endOfDataResponse = Response.read(sentByClient);
        Assert.assertEquals(FINISH_TRANSACTION, endOfDataResponse.getCode());
        Response confirmResponse = Response.read(sentByClient);
        Assert.assertEquals(CONFIRM_TRANSACTION, confirmResponse.getCode());
        Assert.assertEquals((-1), sentByClient.read());
    }

    @Test
    public void testSendTwoFlowFiles() throws IOException {
        ByteArrayOutputStream serverResponseBos = new ByteArrayOutputStream();
        DataOutputStream serverResponse = new DataOutputStream(serverResponseBos);
        CONFIRM_TRANSACTION.writeResponse(serverResponse, "3359812065");
        TRANSACTION_FINISHED.writeResponse(serverResponse);
        ByteArrayInputStream bis = new ByteArrayInputStream(serverResponseBos.toByteArray());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        SocketClientTransaction transaction = getClientTransaction(bis, bos, SEND);
        SiteToSiteTestUtils.execSendTwoFlowFiles(transaction);
        // Verify what client has sent.
        DataInputStream sentByClient = new DataInputStream(new ByteArrayInputStream(bos.toByteArray()));
        Assert.assertEquals(SEND_FLOWFILES, RequestType.readRequestType(sentByClient));
        DataPacket packetByClient = codec.decode(sentByClient);
        Assert.assertEquals("contents on client 1", SiteToSiteTestUtils.readContents(packetByClient));
        Response continueDataResponse = Response.read(sentByClient);
        Assert.assertEquals(CONTINUE_TRANSACTION, continueDataResponse.getCode());
        packetByClient = codec.decode(sentByClient);
        Assert.assertEquals("contents on client 2", SiteToSiteTestUtils.readContents(packetByClient));
        Response endOfDataResponse = Response.read(sentByClient);
        Assert.assertEquals(FINISH_TRANSACTION, endOfDataResponse.getCode());
        Response confirmResponse = Response.read(sentByClient);
        Assert.assertEquals(CONFIRM_TRANSACTION, confirmResponse.getCode());
        Assert.assertEquals((-1), sentByClient.read());
    }

    @Test
    public void testSendWithInvalidChecksum() throws IOException {
        ByteArrayOutputStream serverResponseBos = new ByteArrayOutputStream();
        DataOutputStream serverResponse = new DataOutputStream(serverResponseBos);
        CONFIRM_TRANSACTION.writeResponse(serverResponse, "Different checksum");
        ByteArrayInputStream bis = new ByteArrayInputStream(serverResponseBos.toByteArray());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        SocketClientTransaction transaction = getClientTransaction(bis, bos, SEND);
        SiteToSiteTestUtils.execSendWithInvalidChecksum(transaction);
        // Verify what client has sent.
        DataInputStream sentByClient = new DataInputStream(new ByteArrayInputStream(bos.toByteArray()));
        Assert.assertEquals(SEND_FLOWFILES, RequestType.readRequestType(sentByClient));
        DataPacket packetByClient = codec.decode(sentByClient);
        Assert.assertEquals("contents on client 1", SiteToSiteTestUtils.readContents(packetByClient));
        Response continueDataResponse = Response.read(sentByClient);
        Assert.assertEquals(CONTINUE_TRANSACTION, continueDataResponse.getCode());
        packetByClient = codec.decode(sentByClient);
        Assert.assertEquals("contents on client 2", SiteToSiteTestUtils.readContents(packetByClient));
        Response endOfDataResponse = Response.read(sentByClient);
        Assert.assertEquals(FINISH_TRANSACTION, endOfDataResponse.getCode());
        Response confirmResponse = Response.read(sentByClient);
        Assert.assertEquals(BAD_CHECKSUM, confirmResponse.getCode());
        Assert.assertEquals((-1), sentByClient.read());
    }

    @Test
    public void testSendButDestinationFull() throws IOException {
        ByteArrayOutputStream serverResponseBos = new ByteArrayOutputStream();
        DataOutputStream serverResponse = new DataOutputStream(serverResponseBos);
        CONFIRM_TRANSACTION.writeResponse(serverResponse, "3359812065");
        TRANSACTION_FINISHED_BUT_DESTINATION_FULL.writeResponse(serverResponse);
        ByteArrayInputStream bis = new ByteArrayInputStream(serverResponseBos.toByteArray());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        SocketClientTransaction transaction = getClientTransaction(bis, bos, SEND);
        SiteToSiteTestUtils.execSendButDestinationFull(transaction);
        // Verify what client has sent.
        DataInputStream sentByClient = new DataInputStream(new ByteArrayInputStream(bos.toByteArray()));
        Assert.assertEquals(SEND_FLOWFILES, RequestType.readRequestType(sentByClient));
        DataPacket packetByClient = codec.decode(sentByClient);
        Assert.assertEquals("contents on client 1", SiteToSiteTestUtils.readContents(packetByClient));
        Response continueDataResponse = Response.read(sentByClient);
        Assert.assertEquals(CONTINUE_TRANSACTION, continueDataResponse.getCode());
        packetByClient = codec.decode(sentByClient);
        Assert.assertEquals("contents on client 2", SiteToSiteTestUtils.readContents(packetByClient));
        Response endOfDataResponse = Response.read(sentByClient);
        Assert.assertEquals(FINISH_TRANSACTION, endOfDataResponse.getCode());
        Response confirmResponse = Response.read(sentByClient);
        Assert.assertEquals(CONFIRM_TRANSACTION, confirmResponse.getCode());
        Assert.assertEquals((-1), sentByClient.read());
    }
}

