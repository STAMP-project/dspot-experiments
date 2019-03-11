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
package org.apache.nifi.remote.protocol.http;


import ResponseCode.BAD_CHECKSUM;
import ResponseCode.CANCEL_TRANSACTION;
import ResponseCode.TRANSACTION_FINISHED;
import ResponseCode.TRANSACTION_FINISHED_BUT_DESTINATION_FULL;
import TransferDirection.RECEIVE;
import TransferDirection.SEND;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.nifi.remote.Peer;
import org.apache.nifi.remote.codec.FlowFileCodec;
import org.apache.nifi.remote.codec.StandardFlowFileCodec;
import org.apache.nifi.remote.io.http.HttpCommunicationsSession;
import org.apache.nifi.remote.protocol.CommunicationsSession;
import org.apache.nifi.remote.protocol.DataPacket;
import org.apache.nifi.remote.protocol.ResponseCode;
import org.apache.nifi.remote.protocol.SiteToSiteTestUtils;
import org.apache.nifi.remote.util.SiteToSiteRestApiClient;
import org.apache.nifi.web.api.entity.TransactionResultEntity;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestHttpClientTransaction {
    private Logger logger = LoggerFactory.getLogger(TestHttpClientTransaction.class);

    private FlowFileCodec codec = new StandardFlowFileCodec();

    @Test
    public void testReceiveZeroFlowFile() throws IOException {
        SiteToSiteRestApiClient apiClient = Mockito.mock(SiteToSiteRestApiClient.class);
        final String transactionUrl = "http://www.example.com/data-transfer/input-ports/portId/transactions/transactionId";
        Mockito.doReturn(false).when(apiClient).openConnectionForReceive(ArgumentMatchers.eq(transactionUrl), ArgumentMatchers.any(Peer.class));
        ByteArrayInputStream serverResponse = new ByteArrayInputStream(new byte[0]);
        ByteArrayOutputStream clientRequest = new ByteArrayOutputStream();
        HttpClientTransaction transaction = getClientTransaction(serverResponse, clientRequest, apiClient, RECEIVE, transactionUrl);
        SiteToSiteTestUtils.execReceiveZeroFlowFile(transaction);
        Assert.assertEquals("Client sends nothing as payload to receive flow files.", 0, clientRequest.toByteArray().length);
    }

    @Test
    public void testReceiveOneFlowFile() throws IOException {
        SiteToSiteRestApiClient apiClient = Mockito.mock(SiteToSiteRestApiClient.class);
        final String transactionUrl = "http://www.example.com/data-transfer/input-ports/portId/transactions/transactionId";
        Mockito.doReturn(true).when(apiClient).openConnectionForReceive(ArgumentMatchers.eq(transactionUrl), ArgumentMatchers.any(Peer.class));
        TransactionResultEntity resultEntity = new TransactionResultEntity();
        resultEntity.setResponseCode(ResponseCode.CONFIRM_TRANSACTION.getCode());
        Mockito.doReturn(resultEntity).when(apiClient).commitReceivingFlowFiles(ArgumentMatchers.eq(transactionUrl), ArgumentMatchers.eq(ResponseCode.CONFIRM_TRANSACTION), ArgumentMatchers.eq("3680976076"));
        ByteArrayOutputStream serverResponseBos = new ByteArrayOutputStream();
        codec.encode(SiteToSiteTestUtils.createDataPacket("contents on server 1"), serverResponseBos);
        ByteArrayInputStream serverResponse = new ByteArrayInputStream(serverResponseBos.toByteArray());
        ByteArrayOutputStream clientRequest = new ByteArrayOutputStream();
        HttpClientTransaction transaction = getClientTransaction(serverResponse, clientRequest, apiClient, RECEIVE, transactionUrl);
        SiteToSiteTestUtils.execReceiveOneFlowFile(transaction);
        Assert.assertEquals("Client sends nothing as payload to receive flow files.", 0, clientRequest.toByteArray().length);
        Mockito.verify(apiClient).commitReceivingFlowFiles(transactionUrl, ResponseCode.CONFIRM_TRANSACTION, "3680976076");
    }

    @Test
    public void testReceiveTwoFlowFiles() throws IOException {
        SiteToSiteRestApiClient apiClient = Mockito.mock(SiteToSiteRestApiClient.class);
        final String transactionUrl = "http://www.example.com/data-transfer/input-ports/portId/transactions/transactionId";
        Mockito.doReturn(true).when(apiClient).openConnectionForReceive(ArgumentMatchers.eq(transactionUrl), ArgumentMatchers.any(Peer.class));
        TransactionResultEntity resultEntity = new TransactionResultEntity();
        resultEntity.setResponseCode(ResponseCode.CONFIRM_TRANSACTION.getCode());
        Mockito.doReturn(resultEntity).when(apiClient).commitReceivingFlowFiles(ArgumentMatchers.eq(transactionUrl), ArgumentMatchers.eq(ResponseCode.CONFIRM_TRANSACTION), ArgumentMatchers.eq("2969091230"));
        ByteArrayOutputStream serverResponseBos = new ByteArrayOutputStream();
        codec.encode(SiteToSiteTestUtils.createDataPacket("contents on server 1"), serverResponseBos);
        codec.encode(SiteToSiteTestUtils.createDataPacket("contents on server 2"), serverResponseBos);
        ByteArrayInputStream serverResponse = new ByteArrayInputStream(serverResponseBos.toByteArray());
        ByteArrayOutputStream clientRequest = new ByteArrayOutputStream();
        HttpClientTransaction transaction = getClientTransaction(serverResponse, clientRequest, apiClient, RECEIVE, transactionUrl);
        SiteToSiteTestUtils.execReceiveTwoFlowFiles(transaction);
        Assert.assertEquals("Client sends nothing as payload to receive flow files.", 0, clientRequest.toByteArray().length);
        Mockito.verify(apiClient).commitReceivingFlowFiles(transactionUrl, ResponseCode.CONFIRM_TRANSACTION, "2969091230");
    }

    @Test
    public void testReceiveWithInvalidChecksum() throws IOException {
        SiteToSiteRestApiClient apiClient = Mockito.mock(SiteToSiteRestApiClient.class);
        final String transactionUrl = "http://www.example.com/data-transfer/input-ports/portId/transactions/transactionId";
        Mockito.doReturn(true).when(apiClient).openConnectionForReceive(ArgumentMatchers.eq(transactionUrl), ArgumentMatchers.any(Peer.class));
        // The checksum is correct, but here we simulate as if it's wrong, BAD_CHECKSUM.
        TransactionResultEntity resultEntity = new TransactionResultEntity();
        resultEntity.setResponseCode(BAD_CHECKSUM.getCode());
        Mockito.doReturn(resultEntity).when(apiClient).commitReceivingFlowFiles(ArgumentMatchers.eq(transactionUrl), ArgumentMatchers.eq(ResponseCode.CONFIRM_TRANSACTION), ArgumentMatchers.eq("2969091230"));
        ByteArrayOutputStream serverResponseBos = new ByteArrayOutputStream();
        codec.encode(SiteToSiteTestUtils.createDataPacket("contents on server 1"), serverResponseBos);
        codec.encode(SiteToSiteTestUtils.createDataPacket("contents on server 2"), serverResponseBos);
        ByteArrayInputStream serverResponse = new ByteArrayInputStream(serverResponseBos.toByteArray());
        ByteArrayOutputStream clientRequest = new ByteArrayOutputStream();
        HttpClientTransaction transaction = getClientTransaction(serverResponse, clientRequest, apiClient, RECEIVE, transactionUrl);
        SiteToSiteTestUtils.execReceiveWithInvalidChecksum(transaction);
        Assert.assertEquals("Client sends nothing as payload to receive flow files.", 0, clientRequest.toByteArray().length);
        Mockito.verify(apiClient).commitReceivingFlowFiles(transactionUrl, ResponseCode.CONFIRM_TRANSACTION, "2969091230");
    }

    @Test
    public void testSendZeroFlowFile() throws IOException {
        SiteToSiteRestApiClient apiClient = Mockito.mock(SiteToSiteRestApiClient.class);
        final String transactionUrl = "http://www.example.com/data-transfer/input-ports/portId/transactions/transactionId";
        Mockito.doNothing().when(apiClient).openConnectionForSend(ArgumentMatchers.eq(transactionUrl), ArgumentMatchers.any(Peer.class));
        ByteArrayOutputStream serverResponseBos = new ByteArrayOutputStream();
        ByteArrayInputStream serverResponse = new ByteArrayInputStream(serverResponseBos.toByteArray());
        ByteArrayOutputStream clientRequest = new ByteArrayOutputStream();
        HttpClientTransaction transaction = getClientTransaction(serverResponse, clientRequest, apiClient, SEND, transactionUrl);
        SiteToSiteTestUtils.execSendZeroFlowFile(transaction);
        Assert.assertEquals("Client didn't send anything", 0, clientRequest.toByteArray().length);
    }

    @Test
    public void testSendOneFlowFile() throws IOException {
        SiteToSiteRestApiClient apiClient = Mockito.mock(SiteToSiteRestApiClient.class);
        final String transactionUrl = "http://www.example.com/data-transfer/input-ports/portId/transactions/transactionId";
        Mockito.doNothing().when(apiClient).openConnectionForSend(ArgumentMatchers.eq(transactionUrl), ArgumentMatchers.any(Peer.class));
        // Emulate that server returns correct checksum.
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                HttpCommunicationsSession commSession = ((HttpCommunicationsSession) (invocation.getArguments()[0]));
                commSession.setChecksum("2946083981");
                return null;
            }
        }).when(apiClient).finishTransferFlowFiles(ArgumentMatchers.any(CommunicationsSession.class));
        TransactionResultEntity resultEntity = new TransactionResultEntity();
        resultEntity.setResponseCode(TRANSACTION_FINISHED.getCode());
        Mockito.doReturn(resultEntity).when(apiClient).commitTransferFlowFiles(ArgumentMatchers.eq(transactionUrl), ArgumentMatchers.eq(ResponseCode.CONFIRM_TRANSACTION));
        ByteArrayOutputStream serverResponseBos = new ByteArrayOutputStream();
        ByteArrayInputStream serverResponse = new ByteArrayInputStream(serverResponseBos.toByteArray());
        ByteArrayOutputStream clientRequest = new ByteArrayOutputStream();
        HttpClientTransaction transaction = getClientTransaction(serverResponse, clientRequest, apiClient, SEND, transactionUrl);
        SiteToSiteTestUtils.execSendOneFlowFile(transaction);
        InputStream sentByClient = new ByteArrayInputStream(clientRequest.toByteArray());
        DataPacket packetByClient = codec.decode(sentByClient);
        Assert.assertEquals("contents on client 1", SiteToSiteTestUtils.readContents(packetByClient));
        Assert.assertEquals((-1), sentByClient.read());
        Mockito.verify(apiClient).commitTransferFlowFiles(transactionUrl, ResponseCode.CONFIRM_TRANSACTION);
    }

    @Test
    public void testSendTwoFlowFiles() throws IOException {
        SiteToSiteRestApiClient apiClient = Mockito.mock(SiteToSiteRestApiClient.class);
        final String transactionUrl = "http://www.example.com/data-transfer/input-ports/portId/transactions/transactionId";
        Mockito.doNothing().when(apiClient).openConnectionForSend(ArgumentMatchers.eq("portId"), ArgumentMatchers.any(Peer.class));
        // Emulate that server returns correct checksum.
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                HttpCommunicationsSession commSession = ((HttpCommunicationsSession) (invocation.getArguments()[0]));
                commSession.setChecksum("3359812065");
                return null;
            }
        }).when(apiClient).finishTransferFlowFiles(ArgumentMatchers.any(CommunicationsSession.class));
        TransactionResultEntity resultEntity = new TransactionResultEntity();
        resultEntity.setResponseCode(TRANSACTION_FINISHED.getCode());
        Mockito.doReturn(resultEntity).when(apiClient).commitTransferFlowFiles(ArgumentMatchers.eq(transactionUrl), ArgumentMatchers.eq(ResponseCode.CONFIRM_TRANSACTION));
        ByteArrayOutputStream serverResponseBos = new ByteArrayOutputStream();
        ByteArrayInputStream serverResponse = new ByteArrayInputStream(serverResponseBos.toByteArray());
        ByteArrayOutputStream clientRequest = new ByteArrayOutputStream();
        HttpClientTransaction transaction = getClientTransaction(serverResponse, clientRequest, apiClient, SEND, transactionUrl);
        SiteToSiteTestUtils.execSendTwoFlowFiles(transaction);
        InputStream sentByClient = new ByteArrayInputStream(clientRequest.toByteArray());
        DataPacket packetByClient = codec.decode(sentByClient);
        Assert.assertEquals("contents on client 1", SiteToSiteTestUtils.readContents(packetByClient));
        packetByClient = codec.decode(sentByClient);
        Assert.assertEquals("contents on client 2", SiteToSiteTestUtils.readContents(packetByClient));
        Assert.assertEquals((-1), sentByClient.read());
        Mockito.verify(apiClient).commitTransferFlowFiles(transactionUrl, ResponseCode.CONFIRM_TRANSACTION);
    }

    @Test
    public void testSendWithInvalidChecksum() throws IOException {
        SiteToSiteRestApiClient apiClient = Mockito.mock(SiteToSiteRestApiClient.class);
        final String transactionUrl = "http://www.example.com/data-transfer/input-ports/portId/transactions/transactionId";
        Mockito.doNothing().when(apiClient).openConnectionForSend(ArgumentMatchers.eq(transactionUrl), ArgumentMatchers.any(Peer.class));
        // Emulate that server returns incorrect checksum.
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                HttpCommunicationsSession commSession = ((HttpCommunicationsSession) (invocation.getArguments()[0]));
                commSession.setChecksum("Different checksum");
                return null;
            }
        }).when(apiClient).finishTransferFlowFiles(ArgumentMatchers.any(CommunicationsSession.class));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                TransactionResultEntity serverResult = new TransactionResultEntity();
                serverResult.setResponseCode(CANCEL_TRANSACTION.getCode());
                return serverResult;
            }
        }).when(apiClient).commitTransferFlowFiles(ArgumentMatchers.eq(transactionUrl), ArgumentMatchers.eq(BAD_CHECKSUM));
        ByteArrayOutputStream serverResponseBos = new ByteArrayOutputStream();
        ByteArrayInputStream serverResponse = new ByteArrayInputStream(serverResponseBos.toByteArray());
        ByteArrayOutputStream clientRequest = new ByteArrayOutputStream();
        HttpClientTransaction transaction = getClientTransaction(serverResponse, clientRequest, apiClient, SEND, transactionUrl);
        SiteToSiteTestUtils.execSendWithInvalidChecksum(transaction);
        InputStream sentByClient = new ByteArrayInputStream(clientRequest.toByteArray());
        DataPacket packetByClient = codec.decode(sentByClient);
        Assert.assertEquals("contents on client 1", SiteToSiteTestUtils.readContents(packetByClient));
        packetByClient = codec.decode(sentByClient);
        Assert.assertEquals("contents on client 2", SiteToSiteTestUtils.readContents(packetByClient));
        Assert.assertEquals((-1), sentByClient.read());
        Mockito.verify(apiClient).commitTransferFlowFiles(transactionUrl, BAD_CHECKSUM);
    }

    @Test
    public void testSendButDestinationFull() throws IOException {
        SiteToSiteRestApiClient apiClient = Mockito.mock(SiteToSiteRestApiClient.class);
        final String transactionUrl = "http://www.example.com/data-transfer/input-ports/portId/transactions/transactionId";
        Mockito.doNothing().when(apiClient).openConnectionForSend(ArgumentMatchers.eq("portId"), ArgumentMatchers.any(Peer.class));
        // Emulate that server returns correct checksum.
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                HttpCommunicationsSession commSession = ((HttpCommunicationsSession) (invocation.getArguments()[0]));
                commSession.setChecksum("3359812065");
                return null;
            }
        }).when(apiClient).finishTransferFlowFiles(ArgumentMatchers.any(CommunicationsSession.class));
        TransactionResultEntity resultEntity = new TransactionResultEntity();
        resultEntity.setResponseCode(TRANSACTION_FINISHED_BUT_DESTINATION_FULL.getCode());
        Mockito.doReturn(resultEntity).when(apiClient).commitTransferFlowFiles(ArgumentMatchers.eq(transactionUrl), ArgumentMatchers.eq(ResponseCode.CONFIRM_TRANSACTION));
        ByteArrayOutputStream serverResponseBos = new ByteArrayOutputStream();
        ByteArrayInputStream serverResponse = new ByteArrayInputStream(serverResponseBos.toByteArray());
        ByteArrayOutputStream clientRequest = new ByteArrayOutputStream();
        HttpClientTransaction transaction = getClientTransaction(serverResponse, clientRequest, apiClient, SEND, transactionUrl);
        SiteToSiteTestUtils.execSendButDestinationFull(transaction);
        InputStream sentByClient = new ByteArrayInputStream(clientRequest.toByteArray());
        DataPacket packetByClient = codec.decode(sentByClient);
        Assert.assertEquals("contents on client 1", SiteToSiteTestUtils.readContents(packetByClient));
        packetByClient = codec.decode(sentByClient);
        Assert.assertEquals("contents on client 2", SiteToSiteTestUtils.readContents(packetByClient));
        Assert.assertEquals((-1), sentByClient.read());
        Mockito.verify(apiClient).commitTransferFlowFiles(transactionUrl, ResponseCode.CONFIRM_TRANSACTION);
    }
}

