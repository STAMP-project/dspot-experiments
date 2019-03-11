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
package org.apache.nifi.web.api;


import HttpHeaders.LOCATION_HEADER_NAME;
import ResponseCode.BAD_CHECKSUM;
import ResponseCode.CONFIRM_TRANSACTION;
import ResponseCode.CONTINUE_TRANSACTION;
import ResponseCode.PORT_NOT_IN_VALID_STATE;
import ResponseCode.PROPERTIES_OK;
import ResponseCode.UNAUTHORIZED;
import ResponseCode.UNKNOWN_PORT;
import java.io.InputStream;
import java.lang.reflect.Field;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import org.apache.nifi.remote.HttpRemoteSiteListener;
import org.apache.nifi.remote.Peer;
import org.apache.nifi.remote.RootGroupPort;
import org.apache.nifi.remote.protocol.ResponseCode;
import org.apache.nifi.remote.protocol.http.HttpFlowFileServerProtocol;
import org.apache.nifi.util.NiFiProperties;
import org.apache.nifi.web.api.entity.TransactionResultEntity;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestDataTransferResource {
    @Test
    public void testCreateTransactionPortNotFound() throws Exception {
        final HttpServletRequest req = createCommonHttpServletRequest();
        final DataTransferResource resource = getDataTransferResource();
        final HttpFlowFileServerProtocol serverProtocol = resource.getHttpFlowFileServerProtocol(null);
        Mockito.doThrow(new org.apache.nifi.remote.exception.HandshakeException(ResponseCode.UNKNOWN_PORT, "Not found.")).when(serverProtocol).handshake(ArgumentMatchers.any());
        final ServletContext context = null;
        final UriInfo uriInfo = null;
        final InputStream inputStream = null;
        final Response response = resource.createPortTransaction("input-ports", "port-id", req, context, uriInfo, inputStream);
        TransactionResultEntity resultEntity = ((TransactionResultEntity) (response.getEntity()));
        Assert.assertEquals(404, response.getStatus());
        Assert.assertEquals(UNKNOWN_PORT.getCode(), resultEntity.getResponseCode());
    }

    @Test
    public void testCreateTransactionPortNotInValidState() throws Exception {
        final HttpServletRequest req = createCommonHttpServletRequest();
        final DataTransferResource resource = getDataTransferResource();
        final HttpFlowFileServerProtocol serverProtocol = resource.getHttpFlowFileServerProtocol(null);
        Mockito.doThrow(new org.apache.nifi.remote.exception.HandshakeException(ResponseCode.PORT_NOT_IN_VALID_STATE, "Not in valid state.")).when(serverProtocol).handshake(ArgumentMatchers.any());
        final ServletContext context = null;
        final UriInfo uriInfo = null;
        final InputStream inputStream = null;
        final Response response = resource.createPortTransaction("input-ports", "port-id", req, context, uriInfo, inputStream);
        TransactionResultEntity resultEntity = ((TransactionResultEntity) (response.getEntity()));
        Assert.assertEquals(503, response.getStatus());
        Assert.assertEquals(PORT_NOT_IN_VALID_STATE.getCode(), resultEntity.getResponseCode());
    }

    @Test
    public void testCreateTransactionUnauthorized() throws Exception {
        final HttpServletRequest req = createCommonHttpServletRequest();
        final DataTransferResource resource = getDataTransferResource();
        final HttpFlowFileServerProtocol serverProtocol = resource.getHttpFlowFileServerProtocol(null);
        Mockito.doThrow(new org.apache.nifi.remote.exception.HandshakeException(ResponseCode.UNAUTHORIZED, "Unauthorized.")).when(serverProtocol).handshake(ArgumentMatchers.any());
        final ServletContext context = null;
        final UriInfo uriInfo = null;
        final InputStream inputStream = null;
        final Response response = resource.createPortTransaction("input-ports", "port-id", req, context, uriInfo, inputStream);
        TransactionResultEntity resultEntity = ((TransactionResultEntity) (response.getEntity()));
        Assert.assertEquals(401, response.getStatus());
        Assert.assertEquals(UNAUTHORIZED.getCode(), resultEntity.getResponseCode());
    }

    @Test
    public void testCreateTransaction() throws Exception {
        final HttpServletRequest req = createCommonHttpServletRequest();
        final DataTransferResource resource = getDataTransferResource();
        final String locationUriStr = "http://localhost:8080/nifi-api/data-transfer/input-ports/port-id/transactions/transaction-id";
        final ServletContext context = null;
        final UriInfo uriInfo = mockUriInfo(locationUriStr);
        final Field uriInfoField = resource.getClass().getSuperclass().getSuperclass().getDeclaredField("uriInfo");
        uriInfoField.setAccessible(true);
        uriInfoField.set(resource, uriInfo);
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final Field httpServletRequestField = resource.getClass().getSuperclass().getSuperclass().getDeclaredField("httpServletRequest");
        httpServletRequestField.setAccessible(true);
        httpServletRequestField.set(resource, request);
        final InputStream inputStream = null;
        final Response response = resource.createPortTransaction("input-ports", "port-id", req, context, uriInfo, inputStream);
        TransactionResultEntity resultEntity = ((TransactionResultEntity) (response.getEntity()));
        Assert.assertEquals(201, response.getStatus());
        Assert.assertEquals(PROPERTIES_OK.getCode(), resultEntity.getResponseCode());
        Assert.assertEquals(locationUriStr, response.getMetadata().getFirst(LOCATION_HEADER_NAME).toString());
    }

    @Test
    public void testCreateTransactionThroughReverseProxy() throws Exception {
        final HttpServletRequest req = createCommonHttpServletRequest();
        final DataTransferResource resource = getDataTransferResource();
        final String locationUriStr = "https://nifi2.example.com:443/nifi-api/data-transfer/input-ports/port-id/transactions/transaction-id";
        final ServletContext context = null;
        final UriInfo uriInfo = mockUriInfo(locationUriStr);
        final Field uriInfoField = resource.getClass().getSuperclass().getSuperclass().getDeclaredField("uriInfo");
        uriInfoField.setAccessible(true);
        uriInfoField.set(resource, uriInfo);
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader(ApplicationResource.PROXY_SCHEME_HTTP_HEADER)).thenReturn("https");
        Mockito.when(request.getHeader(ApplicationResource.PROXY_HOST_HTTP_HEADER)).thenReturn("nifi2.example.com");
        Mockito.when(request.getHeader(ApplicationResource.PROXY_PORT_HTTP_HEADER)).thenReturn("443");
        final Field httpServletRequestField = resource.getClass().getSuperclass().getSuperclass().getDeclaredField("httpServletRequest");
        httpServletRequestField.setAccessible(true);
        httpServletRequestField.set(resource, request);
        final InputStream inputStream = null;
        final Response response = resource.createPortTransaction("input-ports", "port-id", req, context, uriInfo, inputStream);
        TransactionResultEntity resultEntity = ((TransactionResultEntity) (response.getEntity()));
        Assert.assertEquals(201, response.getStatus());
        Assert.assertEquals(PROPERTIES_OK.getCode(), resultEntity.getResponseCode());
        Assert.assertEquals(locationUriStr, response.getMetadata().getFirst(LOCATION_HEADER_NAME).toString());
    }

    @Test
    public void testExtendTransaction() throws Exception {
        final HttpServletRequest req = createCommonHttpServletRequest();
        final DataTransferResource resource = getDataTransferResource();
        final String locationUriStr = "http://localhost:8080/nifi-api/data-transfer/input-ports/port-id/transactions/transaction-id";
        final ServletContext context = null;
        final HttpServletResponse res = null;
        final UriInfo uriInfo = mockUriInfo(locationUriStr);
        final InputStream inputStream = null;
        final HttpRemoteSiteListener transactionManager = HttpRemoteSiteListener.getInstance(NiFiProperties.createBasicNiFiProperties(null, null));
        final String transactionId = transactionManager.createTransaction();
        final Response response = resource.extendPortTransactionTTL("input-ports", "port-id", transactionId, req, res, context, uriInfo, inputStream);
        transactionManager.cancelTransaction(transactionId);
        TransactionResultEntity resultEntity = ((TransactionResultEntity) (response.getEntity()));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(CONTINUE_TRANSACTION.getCode(), resultEntity.getResponseCode());
    }

    @Test
    public void testReceiveFlowFiles() throws Exception {
        final HttpServletRequest req = createCommonHttpServletRequest();
        final DataTransferResource resource = getDataTransferResource();
        final HttpFlowFileServerProtocol serverProtocol = resource.getHttpFlowFileServerProtocol(null);
        final RootGroupPort port = Mockito.mock(RootGroupPort.class);
        Mockito.doReturn(port).when(serverProtocol).getPort();
        Mockito.doAnswer(( invocation) -> {
            Peer peer = ((Peer) (invocation.getArguments()[0]));
            setChecksum("server-checksum");
            return 7;
        }).when(port).receiveFlowFiles(ArgumentMatchers.any(Peer.class), ArgumentMatchers.any());
        final ServletContext context = null;
        final InputStream inputStream = null;
        final HttpRemoteSiteListener transactionManager = HttpRemoteSiteListener.getInstance(NiFiProperties.createBasicNiFiProperties(null, null));
        final String transactionId = transactionManager.createTransaction();
        final Response response = resource.receiveFlowFiles("port-id", transactionId, req, context, inputStream);
        transactionManager.cancelTransaction(transactionId);
        final Object entity = response.getEntity();
        Assert.assertEquals(202, response.getStatus());
        Assert.assertEquals("server-checksum", entity);
    }

    @Test
    public void testReceiveZeroFlowFiles() throws Exception {
        final HttpServletRequest req = createCommonHttpServletRequest();
        final DataTransferResource resource = getDataTransferResource();
        final HttpFlowFileServerProtocol serverProtocol = resource.getHttpFlowFileServerProtocol(null);
        final RootGroupPort port = Mockito.mock(RootGroupPort.class);
        Mockito.doReturn(port).when(serverProtocol).getPort();
        Mockito.doAnswer(( invocation) -> 0).when(port).receiveFlowFiles(ArgumentMatchers.any(Peer.class), ArgumentMatchers.any());
        final ServletContext context = null;
        final InputStream inputStream = null;
        final HttpRemoteSiteListener transactionManager = HttpRemoteSiteListener.getInstance(NiFiProperties.createBasicNiFiProperties(null, null));
        final String transactionId = transactionManager.createTransaction();
        final Response response = resource.receiveFlowFiles("port-id", transactionId, req, context, inputStream);
        transactionManager.cancelTransaction(transactionId);
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void testCommitInputPortTransaction() throws Exception {
        final HttpServletRequest req = createCommonHttpServletRequest();
        final DataTransferResource resource = getDataTransferResource();
        final ServletContext context = null;
        final InputStream inputStream = null;
        final HttpRemoteSiteListener transactionManager = HttpRemoteSiteListener.getInstance(NiFiProperties.createBasicNiFiProperties(null, null));
        final String transactionId = transactionManager.createTransaction();
        final Response response = resource.commitInputPortTransaction(CONFIRM_TRANSACTION.getCode(), "port-id", transactionId, req, context, inputStream);
        transactionManager.cancelTransaction(transactionId);
        TransactionResultEntity resultEntity = ((TransactionResultEntity) (response.getEntity()));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(CONFIRM_TRANSACTION.getCode(), resultEntity.getResponseCode());
    }

    @Test
    public void testTransferFlowFiles() throws Exception {
        final HttpServletRequest req = createCommonHttpServletRequest();
        final DataTransferResource resource = getDataTransferResource();
        final ServletContext context = null;
        final HttpServletResponse res = null;
        final InputStream inputStream = null;
        final HttpRemoteSiteListener transactionManager = HttpRemoteSiteListener.getInstance(NiFiProperties.createBasicNiFiProperties(null, null));
        final String transactionId = transactionManager.createTransaction();
        final Response response = resource.transferFlowFiles("port-id", transactionId, req, res, context, inputStream);
        transactionManager.cancelTransaction(transactionId);
        final Object entity = response.getEntity();
        Assert.assertEquals(202, response.getStatus());
        Assert.assertTrue((entity instanceof StreamingOutput));
    }

    @Test
    public void testCommitOutputPortTransaction() throws Exception {
        final HttpServletRequest req = createCommonHttpServletRequest();
        final DataTransferResource resource = getDataTransferResource();
        final ServletContext context = null;
        final InputStream inputStream = null;
        final HttpRemoteSiteListener transactionManager = HttpRemoteSiteListener.getInstance(NiFiProperties.createBasicNiFiProperties(null, null));
        final String transactionId = transactionManager.createTransaction();
        final Response response = resource.commitOutputPortTransaction(CONFIRM_TRANSACTION.getCode(), "client-checksum", "port-id", transactionId, req, context, inputStream);
        transactionManager.cancelTransaction(transactionId);
        TransactionResultEntity resultEntity = ((TransactionResultEntity) (response.getEntity()));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(CONFIRM_TRANSACTION.getCode(), resultEntity.getResponseCode());
    }

    @Test
    public void testCommitOutputPortTransactionBadChecksum() throws Exception {
        final HttpServletRequest req = createCommonHttpServletRequest();
        final DataTransferResource resource = getDataTransferResource();
        final HttpFlowFileServerProtocol serverProtocol = resource.getHttpFlowFileServerProtocol(null);
        Mockito.doThrow(new org.apache.nifi.remote.exception.HandshakeException(ResponseCode.BAD_CHECKSUM, "Bad checksum.")).when(serverProtocol).commitTransferTransaction(ArgumentMatchers.any(), ArgumentMatchers.any());
        final ServletContext context = null;
        final InputStream inputStream = null;
        final HttpRemoteSiteListener transactionManager = HttpRemoteSiteListener.getInstance(NiFiProperties.createBasicNiFiProperties(null, null));
        final String transactionId = transactionManager.createTransaction();
        final Response response = resource.commitOutputPortTransaction(CONFIRM_TRANSACTION.getCode(), "client-checksum", "port-id", transactionId, req, context, inputStream);
        transactionManager.cancelTransaction(transactionId);
        TransactionResultEntity resultEntity = ((TransactionResultEntity) (response.getEntity()));
        Assert.assertEquals(400, response.getStatus());
        Assert.assertEquals(BAD_CHECKSUM.getCode(), resultEntity.getResponseCode());
    }
}

