/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.frontend;


import BlobId.BlobDataType;
import BlobId.BlobIdType;
import ClusterMapSnapshotConstants.TIMESTAMP_MS;
import Container.DEFAULT_PRIVATE_CONTAINER_ID;
import Container.DEFAULT_PUBLIC_CONTAINER_ID;
import GetOption.None;
import GetReplicasHandler.REPLICAS_KEY;
import HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS;
import HttpHeaderNames.ACCESS_CONTROL_MAX_AGE;
import HttpHeaderNames.DATE;
import HttpMethod.GET;
import HttpMethod.OPTIONS;
import HttpMethod.POST;
import HttpResponseStatus.INTERNAL_SERVER_ERROR;
import HttpResponseStatus.OK;
import HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE;
import InMemAccountService.UNKNOWN_ACCOUNT;
import Operations.GET_SIGNED_URL;
import RestUtils.Headers;
import RestUtils.Headers.BLOB_ID;
import RestUtils.Headers.BLOB_SIZE;
import RestUtils.Headers.SIGNED_URL;
import RestUtils.Headers.URL_TYPE;
import RestUtils.SubResource;
import SSLFactory.Mode.CLIENT;
import com.github.ambry.account.Account;
import com.github.ambry.account.Container;
import com.github.ambry.account.InMemAccountService;
import com.github.ambry.account.InMemAccountServiceFactory;
import com.github.ambry.clustermap.ClusterMapUtils;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.clustermap.PartitionId;
import com.github.ambry.commons.BlobId;
import com.github.ambry.commons.CommonTestUtils;
import com.github.ambry.commons.TestSSLUtils;
import com.github.ambry.config.FrontendConfig;
import com.github.ambry.config.NettyConfig;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.rest.NettyClient;
import com.github.ambry.rest.NettyClient.ResponseParts;
import com.github.ambry.rest.RestServer;
import com.github.ambry.rest.RestTestUtils;
import com.github.ambry.utils.Pair;
import com.github.ambry.utils.TestUtils;
import com.github.ambry.utils.UtilsTest;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static Operations.GET_CLUSTER_MAP_SNAPSHOT;


/**
 * Integration tests for Ambry frontend.
 */
@RunWith(Parameterized.class)
public class FrontendIntegrationTest {
    private static final int PLAINTEXT_SERVER_PORT = 1174;

    private static final int SSL_SERVER_PORT = 1175;

    private static final int MAX_MULTIPART_POST_SIZE_BYTES = (10 * 10) * 1024;

    private static final MockClusterMap CLUSTER_MAP;

    private static final VerifiableProperties FRONTEND_VERIFIABLE_PROPS;

    private static final VerifiableProperties SSL_CLIENT_VERIFIABLE_PROPS;

    private static final FrontendConfig FRONTEND_CONFIG;

    private static final InMemAccountService ACCOUNT_SERVICE = new InMemAccountServiceFactory(false, true).getAccountService();

    private static final String DATA_CENTER_NAME = "Datacenter-Name";

    private static final String HOST_NAME = "localhost";

    static {
        try {
            CLUSTER_MAP = new MockClusterMap();
            File trustStoreFile = File.createTempFile("truststore", ".jks");
            trustStoreFile.deleteOnExit();
            FRONTEND_VERIFIABLE_PROPS = FrontendIntegrationTest.buildFrontendVProps(trustStoreFile);
            SSL_CLIENT_VERIFIABLE_PROPS = TestSSLUtils.createSslProps("", CLIENT, trustStoreFile, "client");
            FRONTEND_CONFIG = new FrontendConfig(FrontendIntegrationTest.FRONTEND_VERIFIABLE_PROPS);
            FrontendIntegrationTest.ACCOUNT_SERVICE.clear();
            FrontendIntegrationTest.ACCOUNT_SERVICE.updateAccounts(Collections.singletonList(UNKNOWN_ACCOUNT));
        } catch (IOException | GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private static RestServer ambryRestServer = null;

    private static NettyClient plaintextNettyClient = null;

    private static NettyClient sslNettyClient = null;

    private final NettyClient nettyClient;

    /**
     *
     *
     * @param useSSL
     * 		{@code true} if SSL should be tested.
     */
    public FrontendIntegrationTest(boolean useSSL) {
        nettyClient = (useSSL) ? FrontendIntegrationTest.sslNettyClient : FrontendIntegrationTest.plaintextNettyClient;
    }

    /**
     * Tests blob POST, GET, HEAD, TTL update and DELETE operations.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postGetHeadUpdateDeleteTest() throws Exception {
        // add some accounts
        Account refAccount = FrontendIntegrationTest.ACCOUNT_SERVICE.createAndAddRandomAccount();
        Container publicContainer = refAccount.getContainerById(DEFAULT_PUBLIC_CONTAINER_ID);
        Container privateContainer = refAccount.getContainerById(DEFAULT_PRIVATE_CONTAINER_ID);
        int refContentSize = (FrontendIntegrationTest.FRONTEND_CONFIG.chunkedGetResponseThresholdInBytes) * 3;
        // with valid account and containers
        for (int i = 0; i < 2; i++) {
            Account account = FrontendIntegrationTest.ACCOUNT_SERVICE.createAndAddRandomAccount();
            for (Container container : account.getAllContainers()) {
                doPostGetHeadUpdateDeleteTest(refContentSize, account, container, account.getName(), (!(container.isCacheable())), account.getName(), container.getName(), false);
            }
        }
        // valid account and container names but only serviceId passed as part of POST
        doPostGetHeadUpdateDeleteTest(refContentSize, null, null, refAccount.getName(), false, refAccount.getName(), publicContainer.getName(), false);
        doPostGetHeadUpdateDeleteTest(refContentSize, null, null, refAccount.getName(), true, refAccount.getName(), privateContainer.getName(), false);
        // unrecognized serviceId
        doPostGetHeadUpdateDeleteTest(refContentSize, null, null, "unknown_service_id", false, null, null, false);
        doPostGetHeadUpdateDeleteTest(refContentSize, null, null, "unknown_service_id", true, null, null, false);
        // different sizes
        for (int contentSize : new int[]{ 0, (FrontendIntegrationTest.FRONTEND_CONFIG.chunkedGetResponseThresholdInBytes) - 1, FrontendIntegrationTest.FRONTEND_CONFIG.chunkedGetResponseThresholdInBytes, refContentSize }) {
            doPostGetHeadUpdateDeleteTest(contentSize, refAccount, publicContainer, refAccount.getName(), (!(publicContainer.isCacheable())), refAccount.getName(), publicContainer.getName(), false);
        }
    }

    /**
     * Tests multipart POST and verifies it via GET operations.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void multipartPostGetHeadUpdateDeleteTest() throws Exception {
        Account refAccount = FrontendIntegrationTest.ACCOUNT_SERVICE.createAndAddRandomAccount();
        Container refContainer = refAccount.getContainerById(DEFAULT_PUBLIC_CONTAINER_ID);
        doPostGetHeadUpdateDeleteTest(0, refAccount, refContainer, refAccount.getName(), (!(refContainer.isCacheable())), refAccount.getName(), refContainer.getName(), true);
        doPostGetHeadUpdateDeleteTest(((FrontendIntegrationTest.FRONTEND_CONFIG.chunkedGetResponseThresholdInBytes) * 3), refAccount, refContainer, refAccount.getName(), (!(refContainer.isCacheable())), refAccount.getName(), refContainer.getName(), true);
        // failure case
        // size of content being POSTed is higher than what is allowed via multipart/form-data
        long maxAllowedSizeBytes = new NettyConfig(FrontendIntegrationTest.FRONTEND_VERIFIABLE_PROPS).nettyMultipartPostMaxSizeBytes;
        ByteBuffer content = ByteBuffer.wrap(TestUtils.getRandomBytes((((int) (maxAllowedSizeBytes)) + 1)));
        HttpHeaders headers = new DefaultHttpHeaders();
        setAmbryHeadersForPut(headers, TTL_SECS, (!(refContainer.isCacheable())), refAccount.getName(), "application/octet-stream", null, refAccount.getName(), refContainer.getName());
        HttpRequest httpRequest = RestTestUtils.createRequest(POST, "/", headers);
        HttpPostRequestEncoder encoder = createEncoder(httpRequest, content, ByteBuffer.allocate(0));
        ResponseParts responseParts = nettyClient.sendRequest(encoder.finalizeRequest(), encoder, null).get();
        HttpResponse response = getHttpResponse(responseParts);
        Assert.assertEquals("Unexpected response status", REQUEST_ENTITY_TOO_LARGE, response.status());
        Assert.assertTrue("No Date header", ((response.headers().getTimeMillis(DATE, (-1))) != (-1)));
        Assert.assertFalse("Channel should not be active", HttpUtil.isKeepAlive(response));
    }

    /* Tests health check request
    @throws ExecutionException
    @throws InterruptedException
    @throws IOException
     */
    @Test
    public void healthCheckRequestTest() throws IOException, InterruptedException, ExecutionException {
        FullHttpRequest httpRequest = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/healthCheck", Unpooled.buffer(0));
        ResponseParts responseParts = nettyClient.sendRequest(httpRequest, null, null).get();
        HttpResponse response = getHttpResponse(responseParts);
        Assert.assertEquals("Unexpected response status", OK, response.status());
        final String expectedResponseBody = "GOOD";
        ByteBuffer content = getContent(responseParts.queue, expectedResponseBody.length());
        Assert.assertEquals("GET content does not match original content", expectedResponseBody, new String(content.array()));
    }

    /**
     * Tests {@link RestUtils.SubResource#Replicas} requests
     * <p/>
     * For each {@link PartitionId} in the {@link ClusterMap}, a {@link BlobId} is created. The replica list returned from
     * server is checked for equality against a locally obtained replica list.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getReplicasTest() throws Exception {
        List<? extends PartitionId> partitionIds = FrontendIntegrationTest.CLUSTER_MAP.getWritablePartitionIds(null);
        for (PartitionId partitionId : partitionIds) {
            String originalReplicaStr = partitionId.getReplicaIds().toString().replace(", ", ",");
            BlobId blobId = new BlobId(CommonTestUtils.getCurrentBlobIdVersion(), BlobIdType.NATIVE, ClusterMapUtils.UNKNOWN_DATACENTER_ID, Account.UNKNOWN_ACCOUNT_ID, Container.UNKNOWN_CONTAINER_ID, partitionId, false, BlobDataType.DATACHUNK);
            FullHttpRequest httpRequest = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, (((blobId.getID()) + "/") + (SubResource.Replicas)), Unpooled.buffer(0));
            ResponseParts responseParts = nettyClient.sendRequest(httpRequest, null, null).get();
            HttpResponse response = getHttpResponse(responseParts);
            Assert.assertEquals("Unexpected response status", OK, response.status());
            verifyTrackingHeaders(response);
            ByteBuffer content = getContent(responseParts.queue, HttpUtil.getContentLength(response));
            JSONObject responseJson = new JSONObject(new String(content.array()));
            String returnedReplicasStr = responseJson.get(REPLICAS_KEY).toString().replace("\"", "");
            Assert.assertEquals("Replica IDs returned for the BlobId do no match with the replicas IDs of partition", originalReplicaStr, returnedReplicasStr);
        }
    }

    /**
     * Tests the handling of {@link Operations#GET_CLUSTER_MAP_SNAPSHOT} requests.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getClusterMapSnapshotTest() throws Exception {
        FullHttpRequest httpRequest = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, GET_CLUSTER_MAP_SNAPSHOT, Unpooled.buffer(0));
        ResponseParts responseParts = nettyClient.sendRequest(httpRequest, null, null).get();
        HttpResponse response = getHttpResponse(responseParts);
        Assert.assertEquals("Unexpected response status", OK, response.status());
        verifyTrackingHeaders(response);
        ByteBuffer content = getContent(responseParts.queue, HttpUtil.getContentLength(response));
        JSONObject expected = FrontendIntegrationTest.CLUSTER_MAP.getSnapshot();
        JSONObject actual = new JSONObject(new String(content.array()));
        // remove timestamps because they may differ
        expected.remove(TIMESTAMP_MS);
        actual.remove(TIMESTAMP_MS);
        Assert.assertEquals("Snapshot does not match expected", expected.toString(), actual.toString());
        // test a failure to ensure that it goes through the exception path
        String msg = UtilsTest.getRandomString(10);
        FrontendIntegrationTest.CLUSTER_MAP.setExceptionOnSnapshot(new RuntimeException(msg));
        httpRequest = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, GET_CLUSTER_MAP_SNAPSHOT, Unpooled.buffer(0));
        responseParts = nettyClient.sendRequest(httpRequest, null, null).get();
        response = getHttpResponse(responseParts);
        Assert.assertEquals("Unexpected response status", INTERNAL_SERVER_ERROR, response.status());
        verifyTrackingHeaders(response);
        assertNoContent(responseParts.queue, 1);
        FrontendIntegrationTest.CLUSTER_MAP.setExceptionOnSnapshot(null);
    }

    /**
     * Tests the handling of {@link Operations#GET_SIGNED_URL} requests.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getAndUseSignedUrlTest() throws Exception {
        Account account = FrontendIntegrationTest.ACCOUNT_SERVICE.createAndAddRandomAccount();
        Container container = account.getContainerById(DEFAULT_PRIVATE_CONTAINER_ID);
        // setup
        ByteBuffer content = ByteBuffer.wrap(TestUtils.getRandomBytes(10));
        String serviceId = "getAndUseSignedUrlTest";
        String contentType = "application/octet-stream";
        String ownerId = "getAndUseSignedUrlTest";
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(URL_TYPE, RestMethod.POST.name());
        setAmbryHeadersForPut(headers, TTL_SECS, (!(container.isCacheable())), serviceId, contentType, ownerId, account.getName(), container.getName());
        headers.add(((Headers.USER_META_DATA_HEADER_PREFIX) + "key1"), "value1");
        headers.add(((Headers.USER_META_DATA_HEADER_PREFIX) + "key2"), "value2");
        // POST
        // Get signed URL
        FullHttpRequest httpRequest = buildRequest(GET, GET_SIGNED_URL, headers, null);
        ResponseParts responseParts = nettyClient.sendRequest(httpRequest, null, null).get();
        HttpResponse response = getHttpResponse(responseParts);
        verifyTrackingHeaders(response);
        Assert.assertNotNull("There should be a response from the server", response);
        Assert.assertEquals("Unexpected response status", OK, response.status());
        String signedPostUrl = response.headers().get(SIGNED_URL);
        Assert.assertNotNull("Did not get a signed POST URL", signedPostUrl);
        assertNoContent(responseParts.queue, 1);
        // Use signed URL to POST
        URI uri = new URI(signedPostUrl);
        httpRequest = buildRequest(POST, (((uri.getPath()) + "?") + (uri.getQuery())), null, content);
        responseParts = nettyClient.sendRequest(httpRequest, null, null).get();
        String blobId = verifyPostAndReturnBlobId(responseParts);
        // verify POST
        headers.add(BLOB_SIZE, content.capacity());
        getBlobAndVerify(blobId, null, None, headers, (!(container.isCacheable())), content, account.getName(), container.getName());
        getBlobInfoAndVerify(blobId, None, headers, (!(container.isCacheable())), account.getName(), container.getName(), null);
        // GET
        // Get signed URL
        HttpHeaders getHeaders = new DefaultHttpHeaders();
        getHeaders.add(URL_TYPE, RestMethod.GET.name());
        blobId = (blobId.startsWith("/")) ? blobId.substring(1) : blobId;
        getHeaders.add(BLOB_ID, blobId);
        httpRequest = buildRequest(GET, GET_SIGNED_URL, getHeaders, null);
        responseParts = nettyClient.sendRequest(httpRequest, null, null).get();
        response = getHttpResponse(responseParts);
        Assert.assertEquals("Unexpected response status", OK, response.status());
        verifyTrackingHeaders(response);
        String signedGetUrl = response.headers().get(SIGNED_URL);
        Assert.assertNotNull("Did not get a signed GET URL", signedGetUrl);
        assertNoContent(responseParts.queue, 1);
        // Use URL to GET blob
        uri = new URI(signedGetUrl);
        httpRequest = buildRequest(GET, (((uri.getPath()) + "?") + (uri.getQuery())), null, null);
        responseParts = nettyClient.sendRequest(httpRequest, null, null).get();
        verifyGetBlobResponse(responseParts, null, headers, (!(container.isCacheable())), content, account.getName(), container.getName());
    }

    /**
     * Test the stitched (multipart) upload flow. This includes generating signed chunk upload URLs, uploading chunks to
     * that URL, calling the /stitch API to create a stitched blob, and performing get/head/ttlUpdate/delete operations on
     * the stitched blob.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void stitchedUploadTest() throws Exception {
        Account account = FrontendIntegrationTest.ACCOUNT_SERVICE.createAndAddRandomAccount();
        Container container = account.getContainerById(DEFAULT_PRIVATE_CONTAINER_ID);
        Pair<List<String>, byte[]> idsAndContent = uploadDataChunksAndVerify(account, container, 50, 50, 50, 50, 17);
        stitchBlobAndVerify(account, container, idsAndContent.getFirst(), idsAndContent.getSecond());
        idsAndContent = uploadDataChunksAndVerify(account, container, 167);
        stitchBlobAndVerify(account, container, idsAndContent.getFirst(), idsAndContent.getSecond());
    }

    /**
     * Tests for handling of {@link HttpMethod#OPTIONS}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void optionsTest() throws Exception {
        FullHttpRequest httpRequest = buildRequest(OPTIONS, "", null, null);
        ResponseParts responseParts = nettyClient.sendRequest(httpRequest, null, null).get();
        HttpResponse response = getHttpResponse(responseParts);
        Assert.assertEquals("Unexpected response status", OK, response.status());
        Assert.assertTrue("No Date header", ((response.headers().getTimeMillis(DATE, (-1))) != (-1)));
        Assert.assertEquals("Content-Length is not 0", 0, HttpUtil.getContentLength(response));
        Assert.assertEquals(("Unexpected value for " + (HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS)), FrontendIntegrationTest.FRONTEND_CONFIG.optionsAllowMethods, response.headers().get(ACCESS_CONTROL_ALLOW_METHODS));
        Assert.assertEquals(("Unexpected value for " + (HttpHeaderNames.ACCESS_CONTROL_MAX_AGE)), FrontendIntegrationTest.FRONTEND_CONFIG.optionsValiditySeconds, Long.parseLong(response.headers().get(ACCESS_CONTROL_MAX_AGE)));
        verifyTrackingHeaders(response);
    }

    /**
     * Tests for the account get/update API.
     */
    @Test
    public void accountApiTest() throws Exception {
        getAccountsAndVerify();
        // update and add accounts
        Map<Short, Account> accountsById = FrontendIntegrationTest.ACCOUNT_SERVICE.getAllAccounts().stream().collect(Collectors.toMap(Account::getId, Function.identity()));
        Account editedAccount = accountsById.values().stream().findAny().get();
        Container editedContainer = editedAccount.getAllContainers().stream().findAny().get();
        editedContainer = setDescription("new description abcdefgh").build();
        editedAccount = new com.github.ambry.account.AccountBuilder(editedAccount).addOrUpdateContainer(editedContainer).build();
        updateAccountsAndVerify(editedAccount, FrontendIntegrationTest.ACCOUNT_SERVICE.generateRandomAccount());
        getAccountsAndVerify();
    }
}

