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


import Account.AccountStatus;
import AmbryBlobStorageService.TTL_UPDATE_REJECTED_ALLOW_HEADER_VALUE;
import BlobId.BlobDataType;
import BlobId.BlobIdType;
import ClusterMapSnapshotConstants.TIMESTAMP_MS;
import Container.DEFAULT_PRIVATE_CONTAINER;
import Container.DEFAULT_PUBLIC_CONTAINER;
import Container.UNKNOWN_CONTAINER;
import GetOption.Include_All;
import GetOption.Include_Deleted_Blobs;
import GetOption.Include_Expired_Blobs;
import GetOption.None;
import GetReplicasHandler.REPLICAS_KEY;
import InMemAccountService.UNKNOWN_ACCOUNT;
import InMemoryRouter.OPERATION_THROW_EARLY_RUNTIME_EXCEPTION;
import MockClusterMap.DEFAULT_PARTITION_CLASS;
import Operations.ACCOUNTS;
import Operations.GET_CLUSTER_MAP_SNAPSHOT;
import Operations.GET_PEERS;
import Operations.GET_SIGNED_URL;
import Operations.UPDATE_TTL;
import ResponseStatus.MethodNotAllowed;
import ResponseStatus.Ok;
import RestMethod.DELETE;
import RestMethod.GET;
import RestMethod.HEAD;
import RestMethod.OPTIONS;
import RestMethod.POST;
import RestMethod.PUT;
import RestMethod.UNKNOWN;
import RestServiceErrorCode.AccessDenied;
import RestServiceErrorCode.BadRequest;
import RestServiceErrorCode.Deleted;
import RestServiceErrorCode.InternalServerError;
import RestServiceErrorCode.InvalidAccount;
import RestServiceErrorCode.InvalidArgs;
import RestServiceErrorCode.InvalidContainer;
import RestServiceErrorCode.MissingArgs;
import RestServiceErrorCode.NotAllowed;
import RestServiceErrorCode.NotFound;
import RestServiceErrorCode.ServiceUnavailable;
import RestUtils.Headers;
import RestUtils.Headers.ACCESS_CONTROL_ALLOW_METHODS;
import RestUtils.Headers.ACCESS_CONTROL_MAX_AGE;
import RestUtils.Headers.ALLOW;
import RestUtils.Headers.BLOB_ID;
import RestUtils.Headers.BLOB_SIZE;
import RestUtils.Headers.CONTENT_LENGTH;
import RestUtils.Headers.CONTENT_TYPE;
import RestUtils.Headers.DATE;
import RestUtils.Headers.RANGE;
import RestUtils.Headers.SERVICE_ID;
import RestUtils.Headers.SIGNED_URL;
import RestUtils.Headers.TARGET_ACCOUNT_ID;
import RestUtils.Headers.URL_TYPE;
import RestUtils.InternalKeys.SEND_TRACKING_INFO;
import RestUtils.InternalKeys.TARGET_ACCOUNT_KEY;
import RestUtils.InternalKeys.TARGET_CONTAINER_KEY;
import RestUtils.SubResource;
import RestUtils.TrackingHeaders;
import RestUtils.TrackingHeaders.DATACENTER_NAME;
import RestUtils.TrackingHeaders.FRONTEND_NAME;
import TestUtils.RANDOM;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.account.Account;
import com.github.ambry.account.AccountCollectionSerde;
import com.github.ambry.account.Container;
import com.github.ambry.account.InMemAccountService;
import com.github.ambry.account.InMemAccountServiceFactory;
import com.github.ambry.clustermap.ClusterMapUtils;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.clustermap.PartitionId;
import com.github.ambry.commons.BlobId;
import com.github.ambry.commons.ByteBufferReadableStreamChannel;
import com.github.ambry.commons.CommonTestUtils;
import com.github.ambry.config.FrontendConfig;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.messageformat.BlobProperties;
import com.github.ambry.rest.MockRestResponseChannel;
import com.github.ambry.rest.RestMethod;
import com.github.ambry.rest.RestRequest;
import com.github.ambry.rest.RestRequestMetricsTracker;
import com.github.ambry.rest.RestResponseChannel;
import com.github.ambry.rest.RestServiceException;
import com.github.ambry.rest.RestUtils;
import com.github.ambry.rest.RestUtilsTest;
import com.github.ambry.router.InMemoryRouter;
import com.github.ambry.router.PutBlobOptionsBuilder;
import com.github.ambry.router.ReadableStreamChannel;
import com.github.ambry.router.RouterErrorCode;
import com.github.ambry.utils.SystemTime;
import com.github.ambry.utils.TestUtils;
import com.github.ambry.utils.Utils;
import com.github.ambry.utils.UtilsTest;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import static GetPeersHandler.NAME_QUERY_PARAM;
import static GetPeersHandler.PORT_QUERY_PARAM;
import static Operations.GET_PEERS;
import static com.github.ambry.frontend.FrontendTestRouter.OpType.UpdateBlobTtl;


/**
 * Unit tests for {@link AmbryBlobStorageService}. Also tests {@link AccountAndContainerInjector}.
 */
public class AmbryBlobStorageServiceTest {
    private final Account refAccount;

    private final Properties configProps = new Properties();

    private final MetricRegistry metricRegistry = new MetricRegistry();

    private final FrontendMetrics frontendMetrics = new FrontendMetrics(metricRegistry);

    private final IdConverterFactory idConverterFactory;

    private final SecurityServiceFactory securityServiceFactory;

    private final FrontendTestResponseHandler responseHandler;

    private final InMemoryRouter router;

    private final MockClusterMap clusterMap;

    private final BlobId referenceBlobId;

    private final String referenceBlobIdStr;

    private final short blobIdVersion;

    private final UrlSigningService urlSigningService;

    private final IdSigningService idSigningService;

    private final String datacenterName = "Data-Center";

    private final String hostname = "localhost";

    private FrontendConfig frontendConfig;

    private VerifiableProperties verifiableProperties;

    private boolean shouldAllowServiceIdBasedPut = true;

    private AmbryBlobStorageService ambryBlobStorageService;

    private Container refContainer;

    private Container refDefaultPublicContainer;

    private Container refDefaultPrivateContainer;

    private InMemAccountService accountService = new InMemAccountServiceFactory(false, true).getAccountService();

    private AccountAndContainerInjector accountAndContainerInjector;

    private final String SECURE_PATH_PREFIX = "secure-path";

    private final int CONTENT_LENGTH = 1024;

    /**
     * Sets up the {@link AmbryBlobStorageService} instance before a test.
     *
     * @throws InstantiationException
     * 		
     * @throws IOException
     * 		
     */
    public AmbryBlobStorageServiceTest() throws Exception {
        RestRequestMetricsTracker.setDefaults(metricRegistry);
        configProps.setProperty("frontend.allow.service.id.based.post.request", String.valueOf(shouldAllowServiceIdBasedPut));
        configProps.setProperty("frontend.secure.path.prefix", SECURE_PATH_PREFIX);
        configProps.setProperty("frontend.path.prefixes.to.remove", "/media");
        verifiableProperties = new VerifiableProperties(configProps);
        clusterMap = new MockClusterMap();
        clusterMap.setPermanentMetricRegistry(metricRegistry);
        frontendConfig = new FrontendConfig(verifiableProperties);
        accountAndContainerInjector = new AccountAndContainerInjector(accountService, frontendMetrics, frontendConfig);
        String endpoint = "http://localhost:1174";
        urlSigningService = new AmbryUrlSigningService(endpoint, endpoint, frontendConfig.urlSignerDefaultUrlTtlSecs, frontendConfig.urlSignerDefaultMaxUploadSizeBytes, frontendConfig.urlSignerMaxUrlTtlSecs, frontendConfig.chunkUploadInitialChunkTtlSecs, ((4 * 1024) * 1024), SystemTime.getInstance());
        idSigningService = new AmbryIdSigningService();
        idConverterFactory = new AmbryIdConverterFactory(verifiableProperties, metricRegistry, idSigningService);
        securityServiceFactory = new AmbrySecurityServiceFactory(verifiableProperties, clusterMap, null, urlSigningService, idSigningService, accountAndContainerInjector);
        accountService.clear();
        accountService.updateAccounts(Collections.singleton(UNKNOWN_ACCOUNT));
        refAccount = accountService.createAndAddRandomAccount();
        for (Container container : refAccount.getAllContainers()) {
            if ((container.getId()) == (Container.DEFAULT_PUBLIC_CONTAINER_ID)) {
                refDefaultPublicContainer = container;
            } else
                if ((container.getId()) == (Container.DEFAULT_PRIVATE_CONTAINER_ID)) {
                    refDefaultPrivateContainer = container;
                } else {
                    refContainer = container;
                }

        }
        blobIdVersion = CommonTestUtils.getCurrentBlobIdVersion();
        router = new InMemoryRouter(verifiableProperties, clusterMap);
        responseHandler = new FrontendTestResponseHandler();
        referenceBlobId = new BlobId(blobIdVersion, BlobIdType.NATIVE, ClusterMapUtils.UNKNOWN_DATACENTER_ID, Account.UNKNOWN_ACCOUNT_ID, Container.UNKNOWN_CONTAINER_ID, clusterMap.getWritablePartitionIds(DEFAULT_PARTITION_CLASS).get(0), false, BlobDataType.DATACHUNK);
        referenceBlobIdStr = referenceBlobId.getID();
        ambryBlobStorageService = getAmbryBlobStorageService();
        responseHandler.start();
        ambryBlobStorageService.start();
    }

    /**
     * Tests basic startup and shutdown functionality (no exceptions).
     *
     * @throws InstantiationException
     * 		
     */
    @Test
    public void startShutDownTest() throws InstantiationException {
        ambryBlobStorageService.start();
        ambryBlobStorageService.shutdown();
    }

    /**
     * Tests for {@link AmbryBlobStorageService#shutdown()} when {@link AmbryBlobStorageService#start()} has not been
     * called previously.
     * <p/>
     * This test is for  cases where {@link AmbryBlobStorageService#start()} has failed and
     * {@link AmbryBlobStorageService#shutdown()} needs to be run.
     */
    @Test
    public void shutdownWithoutStartTest() {
        AmbryBlobStorageService ambryBlobStorageService = getAmbryBlobStorageService();
        ambryBlobStorageService.shutdown();
    }

    /**
     * This tests for exceptions thrown when an {@link AmbryBlobStorageService} instance is used without calling
     * {@link AmbryBlobStorageService#start()} first.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void useServiceWithoutStartTest() throws Exception {
        ambryBlobStorageService = getAmbryBlobStorageService();
        // not fine to use without start.
        for (RestMethod method : RestMethod.values()) {
            if (method.equals(UNKNOWN)) {
                continue;
            }
            verifyOperationFailure(AmbryBlobStorageServiceTest.createRestRequest(method, "/", null, null), ServiceUnavailable);
        }
    }

    /**
     * Checks for reactions of all methods in {@link AmbryBlobStorageService} to null arguments.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void nullInputsForFunctionsTest() throws Exception {
        doNullInputsForFunctionsTest("handleGet");
        doNullInputsForFunctionsTest("handlePost");
        doNullInputsForFunctionsTest("handleDelete");
        doNullInputsForFunctionsTest("handleHead");
        doNullInputsForFunctionsTest("handleOptions");
        doNullInputsForFunctionsTest("handlePut");
    }

    /**
     * Checks reactions of all methods in {@link AmbryBlobStorageService} to a {@link Router} that throws
     * {@link RuntimeException}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void runtimeExceptionRouterTest() throws Exception {
        // set InMemoryRouter up to throw RuntimeException
        Properties properties = new Properties();
        properties.setProperty(OPERATION_THROW_EARLY_RUNTIME_EXCEPTION, "true");
        router.setVerifiableProperties(new VerifiableProperties(properties));
        doRuntimeExceptionRouterTest(GET);
        doRuntimeExceptionRouterTest(POST);
        doRuntimeExceptionRouterTest(DELETE);
        doRuntimeExceptionRouterTest(HEAD);
        // PUT is tested in the individual handlers
    }

    /**
     * Checks reactions of PUT methods in {@link AmbryBlobStorageService} when there are bad request parameters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void putFailureTest() throws Exception {
        // unrecognized operation
        RestRequest restRequest = AmbryBlobStorageServiceTest.createRestRequest(PUT, "/non-existent-op", null, null);
        verifyOperationFailure(restRequest, BadRequest);
    }

    /**
     * Checks reactions of all methods in {@link AmbryBlobStorageService} to bad {@link RestResponseHandler} and
     * {@link RestRequest} implementations.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void badResponseHandlerAndRestRequestTest() throws Exception {
        // What happens inside AmbryBlobStorageService during this test?
        // 1. Since the RestRequest throws errors, AmbryBlobStorageService will attempt to submit response with exception
        // to FrontendTestResponseHandler.
        // 2. The submission will fail because FrontendTestResponseHandler has been shutdown.
        // 3. AmbryBlobStorageService will directly complete the request over the RestResponseChannel with the *original*
        // exception.
        // 4. It will then try to release resources but closing the RestRequest will also throw an exception. This exception
        // is swallowed.
        // What the test is looking for -> No exceptions thrown when the handle is run and the original exception arrives
        // safely.
        responseHandler.shutdown();
        for (String methodName : new String[]{ "handleGet", "handlePost", "handleHead", "handleDelete", "handleOptions", "handlePut" }) {
            Method method = AmbryBlobStorageService.class.getDeclaredMethod(methodName, RestRequest.class, RestResponseChannel.class);
            responseHandler.reset();
            RestRequest restRequest = new BadRestRequest();
            MockRestResponseChannel restResponseChannel = new MockRestResponseChannel();
            method.invoke(ambryBlobStorageService, restRequest, restResponseChannel);
            Exception e = restResponseChannel.getException();
            Assert.assertTrue("Unexpected exception", ((e instanceof IllegalStateException) || (e instanceof NullPointerException)));
        }
    }

    /**
     * Tests
     * {@link AmbryBlobStorageService#submitResponse(RestRequest, RestResponseChannel, ReadableStreamChannel, Exception)}.
     *
     * @throws JSONException
     * 		
     * @throws UnsupportedEncodingException
     * 		
     * @throws URISyntaxException
     * 		
     */
    @Test
    public void submitResponseTest() throws UnsupportedEncodingException, URISyntaxException, JSONException {
        String exceptionMsg = UtilsTest.getRandomString(10);
        responseHandler.shutdown();
        // handleResponse of FrontendTestResponseHandler throws exception because it has been shutdown.
        try {
            // there is an exception already.
            RestRequest restRequest = AmbryBlobStorageServiceTest.createRestRequest(GET, "/", null, null);
            Assert.assertTrue("RestRequest channel is not open", restRequest.isOpen());
            MockRestResponseChannel restResponseChannel = new MockRestResponseChannel();
            ambryBlobStorageService.submitResponse(restRequest, restResponseChannel, null, new RuntimeException(exceptionMsg));
            Assert.assertEquals("Unexpected exception message", exceptionMsg, restResponseChannel.getException().getMessage());
            // there is no exception and exception thrown when the response is submitted.
            restRequest = AmbryBlobStorageServiceTest.createRestRequest(GET, "/", null, null);
            Assert.assertTrue("RestRequest channel is not open", restRequest.isOpen());
            restResponseChannel = new MockRestResponseChannel();
            ReadableStreamChannel response = new ByteBufferReadableStreamChannel(ByteBuffer.allocate(0));
            Assert.assertTrue("Response channel is not open", response.isOpen());
            ambryBlobStorageService.submitResponse(restRequest, restResponseChannel, response, null);
            Assert.assertNotNull("There is no cause of failure", restResponseChannel.getException());
            // resources should have been cleaned up.
            Assert.assertFalse("Response channel is not cleaned up", response.isOpen());
        } finally {
            responseHandler.start();
        }
        // verify tracking infos are attached accordingly.
        RestRequest restRequest;
        MockRestResponseChannel restResponseChannel;
        for (String header : TrackingHeaders.TRACKING_HEADERS) {
            restRequest = AmbryBlobStorageServiceTest.createRestRequest(GET, "/", null, null);
            restResponseChannel = new MockRestResponseChannel();
            ambryBlobStorageService.submitResponse(restRequest, restResponseChannel, null, null);
            Assert.assertTrue("Response header should not contain tracking info", ((restResponseChannel.getHeader(header)) == null));
        }
        restRequest = AmbryBlobStorageServiceTest.createRestRequest(GET, "/", null, null);
        restRequest.setArg(SEND_TRACKING_INFO, new Boolean(true));
        restResponseChannel = new MockRestResponseChannel();
        ambryBlobStorageService.submitResponse(restRequest, restResponseChannel, null, null);
        Assert.assertEquals("Unexpected or missing tracking info", datacenterName, restResponseChannel.getHeader(DATACENTER_NAME));
        Assert.assertEquals("Unexpected or missing tracking info", hostname, restResponseChannel.getHeader(FRONTEND_NAME));
    }

    /**
     * Tests releasing of resources if response submission fails.
     *
     * @throws JSONException
     * 		
     * @throws UnsupportedEncodingException
     * 		
     * @throws URISyntaxException
     * 		
     */
    @Test
    public void releaseResourcesTest() throws UnsupportedEncodingException, URISyntaxException, JSONException {
        responseHandler.shutdown();
        // handleResponse of FrontendTestResponseHandler throws exception because it has been shutdown.
        try {
            RestRequest restRequest = AmbryBlobStorageServiceTest.createRestRequest(GET, "/", null, null);
            MockRestResponseChannel restResponseChannel = new MockRestResponseChannel();
            ReadableStreamChannel channel = new ByteBufferReadableStreamChannel(ByteBuffer.allocate(0));
            Assert.assertTrue("RestRequest channel not open", restRequest.isOpen());
            Assert.assertTrue("ReadableStreamChannel not open", channel.isOpen());
            ambryBlobStorageService.submitResponse(restRequest, restResponseChannel, channel, null);
            Assert.assertFalse("ReadableStreamChannel is still open", channel.isOpen());
            // null ReadableStreamChannel
            restRequest = AmbryBlobStorageServiceTest.createRestRequest(GET, "/", null, null);
            restResponseChannel = new MockRestResponseChannel();
            Assert.assertTrue("RestRequest channel not open", restRequest.isOpen());
            ambryBlobStorageService.submitResponse(restRequest, restResponseChannel, null, null);
            // bad RestRequest (close() throws IOException)
            channel = new ByteBufferReadableStreamChannel(ByteBuffer.allocate(0));
            restResponseChannel = new MockRestResponseChannel();
            Assert.assertTrue("ReadableStreamChannel not open", channel.isOpen());
            ambryBlobStorageService.submitResponse(new BadRestRequest(), restResponseChannel, channel, null);
            // bad ReadableStreamChannel (close() throws IOException)
            restRequest = AmbryBlobStorageServiceTest.createRestRequest(GET, "/", null, null);
            restResponseChannel = new MockRestResponseChannel();
            Assert.assertTrue("RestRequest channel not open", restRequest.isOpen());
            ambryBlobStorageService.submitResponse(restRequest, restResponseChannel, new BadRSC(), null);
        } finally {
            responseHandler.start();
        }
    }

    /**
     * Tests blob POST, GET, HEAD, TTL update and DELETE operations.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postGetHeadUpdateDeleteTest() throws Exception {
        // add another account
        accountService.createAndAddRandomAccount();
        // valid account and container names passed as part of POST
        for (Account testAccount : accountService.getAllAccounts()) {
            if ((testAccount.getId()) != (Account.UNKNOWN_ACCOUNT_ID)) {
                for (Container container : testAccount.getAllContainers()) {
                    doPostGetHeadUpdateDeleteTest(testAccount, container, testAccount.getName(), (!(container.isCacheable())), testAccount, container);
                    doConditionalUpdateAndDeleteTest(testAccount, container, testAccount.getName());
                }
            }
        }
        // valid account and container names but only serviceId passed as part of POST
        doPostGetHeadUpdateDeleteTest(null, null, refAccount.getName(), false, refAccount, refDefaultPublicContainer);
        doPostGetHeadUpdateDeleteTest(null, null, refAccount.getName(), true, refAccount, refDefaultPrivateContainer);
        // unrecognized serviceId
        doPostGetHeadUpdateDeleteTest(null, null, "unknown_service_id", false, UNKNOWN_ACCOUNT, DEFAULT_PUBLIC_CONTAINER);
        doPostGetHeadUpdateDeleteTest(null, null, "unknown_service_id", true, UNKNOWN_ACCOUNT, DEFAULT_PRIVATE_CONTAINER);
    }

    /**
     * Tests injecting target {@link Account} and {@link Container} for PUT requests. The {@link AccountService} is
     * prepopulated with a reference account and {@link InMemAccountService#UNKNOWN_ACCOUNT}. The expected behavior should be:
     *
     * <pre>
     *   accountHeader    containerHeader   serviceIdHeader     expected Error      injected account      injected container
     *    null             null              "someServiceId"     null                UNKNOWN_ACCOUNT       UNKNOWN_CONTAINER
     *    null             nonExistName      "someServiceId"     MissingArgs         null                  null
     *    null             C#UNKOWN          "someServiceId"     InvalidContainer    null                  null
     *    null             realCntName       "someServiceId"     MissingArgs         null                  null
     *    A#UNKNOWN        null              "someServiceId"     InvalidAccount      null                  null
     *    A#UNKNOWN        nonExistName      "someServiceId"     InvalidAccount      null                  null
     *    A#UNKNOWN        C#UNKOWN          "someServiceId"     InvalidAccount      null                  null
     *    A#UNKNOWN        realCntName       "someServiceId"     InvalidAccount      null                  null
     *    realAcctName     null              "someServiceId"     MissingArgs         null                  null
     *    realAcctName     nonExistName      "someServiceId"     InvalidContainer    null                  null
     *    realAcctName     C#UNKOWN          "someServiceId"     InvalidContainer    null                  null
     *    realAcctName     realCntName       "someServiceId"     null                realAccount           realContainer
     *    nonExistName     null              "someServiceId"     MissingArgs         null                  null
     *    nonExistName     nonExistName      "someServiceId"     InvalidAccount      null                  null
     *    nonExistName     C#UNKOWN          "someServiceId"     InvalidAccount      null                  null
     *    nonExistName     realCntName       "someServiceId"     InvalidAccount      null                  null
     *    null             null              A#UNKNOWN           InvalidAccount      null                  null
     *    null             nonExistName      A#UNKNOWN           InvalidAccount      null                  null
     *    null             C#UNKOWN          A#UNKNOWN           InvalidAccount      null                  null
     *    null             realCntName       A#UNKNOWN           InvalidAccount      null                  null
     *    null             null              realAcctName        InvalidContainer    null                  null     Note: The account does not have the two default containers for legacy public and private blobs.
     *    null             nonExistName      realAcctName        MissingArgs         null                  null
     *    null             C#UNKOWN          realAcctName        InvalidContainer    null                  null
     *    null             realCntName       realAcctName        MissingArgs         null                  null
     *    null             null              realAcctName        null                realAccount           default pub/private ctn     Note: The account has the two default containers for legacy public and private blobs.
     * </pre>
     *
     * @throws Exception
     * 		
     */
    @Test
    public void injectionAccountAndContainerForPostTest() throws Exception {
        injectAccountAndContainerForPostAndVerify(refDefaultPrivateContainer, true);
        injectAccountAndContainerForPostAndVerify(refDefaultPrivateContainer, false);
        injectAccountAndContainerForPostAndVerify(refDefaultPublicContainer, true);
        injectAccountAndContainerForPostAndVerify(refDefaultPublicContainer, false);
    }

    /**
     * Tests injecting target {@link Account} and {@link Container} for GET/HEAD/DELETE blobId string in {@link BlobId#BLOB_ID_V2}.
     * The {@link AccountService} is prepopulated with a reference account and {@link InMemAccountService#UNKNOWN_ACCOUNT}. The expected
     * behavior should be:
     *
     * <pre>
     *   AId in blobId    CId in blobId     expected Error      injected account      injected container
     *    realAId           realCId          NotFound            refAccount            refContainer       This can succeed if the blob exists in backend.
     *    realAId           UNKNOWN          InvalidContainer    null                  null
     *    realAId           nonExistCId      InvalidContainer    null                  null
     *    UNKNOWN           realCId          InvalidContainer    null                  null
     *    UNKNOWN           UNKNOWN          NotFound            UNKNOWN               UNKNOWN            This can succeed if the blob exists in backend.
     *    UNKNOWN           nonExistCId      InvalidContainer    null                  null
     *    nonExistAId       realCId          InvalidAccount      null                  null
     *    nonExistAId       UNKNOWN          InvalidAccount      null                  null
     *    nonExistAId       nonExistCId      InvalidAccount      null                  null
     * </pre>
     *
     * @throws Exception
     * 		
     */
    @Test
    public void injectionAccountAndContainerForGetHeadDeleteBlobIdTest() throws Exception {
        List<Short> blobIdVersions = Arrays.stream(BlobId.getAllValidVersions()).filter(( version) -> version >= BlobId.BLOB_ID_V2).collect(Collectors.toList());
        for (short version : blobIdVersions) {
            populateAccountService();
            // aid=refAId, cid=refCId
            String blobId = getID();
            verifyAccountAndContainerFromBlobId(blobId, refAccount, refContainer, NotFound);
            // aid=refAId, cid=unknownCId
            blobId = getID();
            verifyAccountAndContainerFromBlobId(blobId, null, null, InvalidContainer);
            // aid=refAId, cid=nonExistCId
            blobId = getID();
            verifyAccountAndContainerFromBlobId(blobId, null, null, InvalidContainer);
            // aid=unknownAId, cid=refCId
            blobId = getID();
            verifyAccountAndContainerFromBlobId(blobId, null, null, InvalidContainer);
            // aid=unknownAId, cid=unknownCId
            blobId = getID();
            verifyAccountAndContainerFromBlobId(blobId, UNKNOWN_ACCOUNT, UNKNOWN_CONTAINER, NotFound);
            // aid=unknownAId, cid=nonExistCId
            blobId = getID();
            verifyAccountAndContainerFromBlobId(blobId, null, null, InvalidContainer);
            // aid=nonExistAId, cid=refCId
            blobId = getID();
            verifyAccountAndContainerFromBlobId(blobId, null, null, InvalidAccount);
            // aid=nonExistAId, cid=unknownCId
            blobId = getID();
            verifyAccountAndContainerFromBlobId(blobId, null, null, InvalidAccount);
            // aid=nonExistAId, cid=nonExistCId
            blobId = getID();
            verifyAccountAndContainerFromBlobId(blobId, null, null, InvalidAccount);
        }
    }

    /**
     * Tests injecting target {@link Account} and {@link Container} for GET/HEAD/DELETE blobId string in {@link BlobId#BLOB_ID_V1}.
     * The {@link AccountService} is prepopulated with a reference account and {@link InMemAccountService#UNKNOWN_ACCOUNT}. The expected
     * behavior should be:
     * <pre>
     *   AId in blobId    CId in blobId     expected Error      injected account      injected container
     *    UNKNOWN           UNKNOWN          NotFound            UNKNOWN               UNKNOWN            This can succeed if the blob exists in backend.
     * </pre>
     *
     * @throws Exception
     * 		
     */
    @Test
    public void injectionAccountAndContainerForGetHeadDeleteBlobIdV1Test() throws Exception {
        populateAccountService();
        // it does not matter what AID and CID are supplied when constructing blobId in v1.
        // expect unknown account and container for v1 blob IDs that went through request processing only.
        String blobId = getID();
        verifyAccountAndContainerFromBlobId(blobId, UNKNOWN_ACCOUNT, UNKNOWN_CONTAINER, NotFound);
        // test response path account injection for V1 blob IDs
        // public blob with service ID that does not correspond to a valid account
        verifyResponsePathAccountAndContainerInjection(((refAccount.getName()) + "extra"), false, UNKNOWN_ACCOUNT, DEFAULT_PUBLIC_CONTAINER);
        // private blob with service ID that does not correspond to a valid account
        verifyResponsePathAccountAndContainerInjection(((refAccount.getName()) + "extra"), true, UNKNOWN_ACCOUNT, DEFAULT_PRIVATE_CONTAINER);
        // public blob with service ID that corresponds to a valid account
        verifyResponsePathAccountAndContainerInjection(refAccount.getName(), false, refAccount, refDefaultPublicContainer);
        // private blob with service ID that corresponds to a valid account
        verifyResponsePathAccountAndContainerInjection(refAccount.getName(), true, refAccount, refDefaultPrivateContainer);
    }

    /**
     * Tests a corner case when {@link Account} inquired from {@link AccountService} has a name that does not match the
     * target account name set by the request.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void accountNameMismatchTest() throws Exception {
        accountService = new InMemAccountServiceFactory(true, false).getAccountService();
        accountAndContainerInjector = new AccountAndContainerInjector(accountService, frontendMetrics, frontendConfig);
        ambryBlobStorageService = getAmbryBlobStorageService();
        ambryBlobStorageService.start();
        postBlobAndVerifyWithAccountAndContainer(refAccount.getName(), refContainer.getName(), "serviceId", (!(refContainer.isCacheable())), null, null, InternalServerError);
    }

    /**
     * Tests how metadata that has not been POSTed in the form of headers is returned.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void oldStyleUserMetadataTest() throws Exception {
        ByteBuffer content = ByteBuffer.allocate(0);
        BlobProperties blobProperties = new BlobProperties(0, "userMetadataTestOldStyleServiceID", Account.UNKNOWN_ACCOUNT_ID, Container.UNKNOWN_CONTAINER_ID, false);
        byte[] usermetadata = TestUtils.getRandomBytes(25);
        String blobId = router.putBlob(blobProperties, usermetadata, new ByteBufferReadableStreamChannel(content), new PutBlobOptionsBuilder().build()).get();
        RestUtils[] subResources = new SubResource[]{ SubResource.UserMetadata, SubResource.BlobInfo };
        for (RestUtils.SubResource subResource : subResources) {
            RestRequest restRequest = AmbryBlobStorageServiceTest.createRestRequest(GET, ((blobId + "/") + subResource), null, null);
            MockRestResponseChannel restResponseChannel = new MockRestResponseChannel();
            doOperation(restRequest, restResponseChannel);
            Assert.assertEquals(("Unexpected response status for " + subResource), Ok, restResponseChannel.getStatus());
            Assert.assertEquals(("Unexpected Content-Type for " + subResource), "application/octet-stream", restResponseChannel.getHeader(CONTENT_TYPE));
            Assert.assertEquals(("Unexpected Content-Length for " + subResource), usermetadata.length, Integer.parseInt(restResponseChannel.getHeader(RestUtils.Headers.CONTENT_LENGTH)));
            Assert.assertArrayEquals(("Unexpected user metadata for " + subResource), usermetadata, restResponseChannel.getResponseBody());
        }
    }

    /**
     * Tests for cases where the {@link IdConverter} misbehaves and throws {@link RuntimeException}.
     *
     * @throws InstantiationException
     * 		
     * @throws JSONException
     * 		
     */
    @Test
    public void misbehavingIdConverterTest() throws InstantiationException, JSONException {
        FrontendTestIdConverterFactory converterFactory = new FrontendTestIdConverterFactory();
        String exceptionMsg = UtilsTest.getRandomString(10);
        converterFactory.exceptionToThrow = new IllegalStateException(exceptionMsg);
        doIdConverterExceptionTest(converterFactory, exceptionMsg);
    }

    /**
     * Tests for cases where the {@link IdConverter} returns valid exceptions.
     *
     * @throws InstantiationException
     * 		
     * @throws JSONException
     * 		
     */
    @Test
    public void idConverterExceptionPipelineTest() throws InstantiationException, JSONException {
        FrontendTestIdConverterFactory converterFactory = new FrontendTestIdConverterFactory();
        String exceptionMsg = UtilsTest.getRandomString(10);
        converterFactory.exceptionToReturn = new IllegalStateException(exceptionMsg);
        doIdConverterExceptionTest(converterFactory, exceptionMsg);
    }

    /**
     * Tests for cases where the {@link SecurityService} misbehaves and throws {@link RuntimeException}.
     *
     * @throws InstantiationException
     * 		
     * @throws JSONException
     * 		
     */
    @Test
    public void misbehavingSecurityServiceTest() throws InstantiationException, JSONException {
        FrontendTestSecurityServiceFactory securityFactory = new FrontendTestSecurityServiceFactory();
        String exceptionMsg = UtilsTest.getRandomString(10);
        securityFactory.exceptionToThrow = new IllegalStateException(exceptionMsg);
        doSecurityServiceExceptionTest(securityFactory, exceptionMsg);
    }

    /**
     * Tests for cases where the {@link SecurityService} returns valid exceptions.
     *
     * @throws InstantiationException
     * 		
     * @throws JSONException
     * 		
     */
    @Test
    public void securityServiceExceptionPipelineTest() throws InstantiationException, JSONException {
        FrontendTestSecurityServiceFactory securityFactory = new FrontendTestSecurityServiceFactory();
        String exceptionMsg = UtilsTest.getRandomString(10);
        securityFactory.exceptionToReturn = new IllegalStateException(exceptionMsg);
        doSecurityServiceExceptionTest(securityFactory, exceptionMsg);
    }

    /**
     * Tests for cases where the {@link Router} misbehaves and throws {@link RuntimeException}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void misbehavingRouterTest() throws Exception {
        FrontendTestRouter testRouter = new FrontendTestRouter();
        String exceptionMsg = UtilsTest.getRandomString(10);
        testRouter.exceptionToThrow = new IllegalStateException(exceptionMsg);
        doRouterExceptionPipelineTest(testRouter, exceptionMsg);
    }

    /**
     * Tests for cases where the {@link Router} returns valid {@link RouterException}.
     *
     * @throws InstantiationException
     * 		
     * @throws JSONException
     * 		
     */
    @Test
    public void routerExceptionPipelineTest() throws Exception {
        FrontendTestRouter testRouter = new FrontendTestRouter();
        String exceptionMsg = UtilsTest.getRandomString(10);
        testRouter.exceptionToReturn = new com.github.ambry.router.RouterException(exceptionMsg, RouterErrorCode.UnexpectedInternalError);
        doRouterExceptionPipelineTest(testRouter, ((exceptionMsg + " Error: ") + (RouterErrorCode.UnexpectedInternalError)));
    }

    /**
     * Test that GET operations fail with the expected error code when a bad range header is provided.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void badRangeHeaderTest() throws Exception {
        JSONObject headers = new JSONObject();
        headers.put(RANGE, "adsfksakdfsdfkdaklf");
        verifyOperationFailure(AmbryBlobStorageServiceTest.createRestRequest(GET, "/", headers, null), InvalidArgs);
    }

    /**
     * Tests put requests with prohibited headers.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void badPutRequestWithProhibitedHeadersTest() throws Exception {
        putRequestWithProhibitedHeader(TARGET_ACCOUNT_KEY);
        putRequestWithProhibitedHeader(TARGET_CONTAINER_KEY);
    }

    /**
     * Test that the correct service ID is sent to the router on deletes.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void deleteServiceIdTest() throws Exception {
        FrontendTestRouter testRouter = new FrontendTestRouter();
        ambryBlobStorageService = new AmbryBlobStorageService(frontendConfig, frontendMetrics, responseHandler, testRouter, clusterMap, idConverterFactory, securityServiceFactory, urlSigningService, idSigningService, accountService, accountAndContainerInjector, datacenterName, hostname);
        ambryBlobStorageService.start();
        JSONObject headers = new JSONObject();
        String serviceId = "service-id";
        headers.put(SERVICE_ID, serviceId);
        doOperation(AmbryBlobStorageServiceTest.createRestRequest(DELETE, referenceBlobIdStr, headers, null), new MockRestResponseChannel());
        Assert.assertEquals(serviceId, testRouter.deleteServiceId);
        doOperation(AmbryBlobStorageServiceTest.createRestRequest(DELETE, referenceBlobIdStr, null, null), new MockRestResponseChannel());
        Assert.assertNull("Service ID should not have been set for this delete", testRouter.deleteServiceId);
    }

    /**
     * Tests the handling of {@link Operations#GET_PEERS} requests.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getPeersTest() throws Exception {
        ambryBlobStorageService.shutdown();
        TailoredPeersClusterMap clusterMap = new TailoredPeersClusterMap();
        ambryBlobStorageService = new AmbryBlobStorageService(frontendConfig, frontendMetrics, responseHandler, router, clusterMap, idConverterFactory, securityServiceFactory, urlSigningService, idSigningService, accountService, accountAndContainerInjector, datacenterName, hostname);
        ambryBlobStorageService.start();
        // test good requests
        for (String datanode : TailoredPeersClusterMap.DATANODE_NAMES) {
            String[] parts = datanode.split(":");
            String baseUri = ((((((((GET_PEERS) + "?") + (NAME_QUERY_PARAM)) + "=") + (parts[0])) + "&") + (PORT_QUERY_PARAM)) + "=") + (parts[1]);
            String[] uris = new String[]{ baseUri, "/" + baseUri };
            for (String uri : uris) {
                MockRestResponseChannel restResponseChannel = new MockRestResponseChannel();
                doOperation(AmbryBlobStorageServiceTest.createRestRequest(GET, uri, null, null), restResponseChannel);
                byte[] peerStrBytes = restResponseChannel.getResponseBody();
                Set<String> peersFromResponse = GetPeersHandlerTest.getPeersFromResponse(new JSONObject(new String(peerStrBytes)));
                Set<String> expectedPeers = clusterMap.getPeers(datanode);
                Assert.assertEquals(("Peer list returned does not match expected for " + datanode), expectedPeers, peersFromResponse);
            }
        }
        // test one bad request
        RestRequest restRequest = AmbryBlobStorageServiceTest.createRestRequest(GET, GET_PEERS, null, null);
        verifyOperationFailure(restRequest, MissingArgs);
    }

    /**
     * Tests {@link GetReplicasHandler#getReplicas(String, RestResponseChannel)}
     * <p/>
     * For each {@link PartitionId} in the {@link ClusterMap}, a {@link BlobId} is created. The replica list returned from
     * {@link GetReplicasHandler#getReplicas(String, RestResponseChannel)}is checked for equality against a locally
     * obtained replica list.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getReplicasTest() throws Exception {
        List<? extends PartitionId> partitionIds = clusterMap.getWritablePartitionIds(null);
        for (PartitionId partitionId : partitionIds) {
            String originalReplicaStr = partitionId.getReplicaIds().toString().replace(", ", ",");
            BlobId blobId = new BlobId(blobIdVersion, BlobIdType.NATIVE, ClusterMapUtils.UNKNOWN_DATACENTER_ID, Account.UNKNOWN_ACCOUNT_ID, Container.UNKNOWN_CONTAINER_ID, partitionId, false, BlobDataType.DATACHUNK);
            RestRequest restRequest = AmbryBlobStorageServiceTest.createRestRequest(GET, (((blobId.getID()) + "/") + (SubResource.Replicas)), null, null);
            MockRestResponseChannel restResponseChannel = new MockRestResponseChannel();
            doOperation(restRequest, restResponseChannel);
            JSONObject response = new JSONObject(new String(restResponseChannel.getResponseBody()));
            String returnedReplicasStr = response.get(REPLICAS_KEY).toString().replace("\"", "");
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
        RestRequest restRequest = AmbryBlobStorageServiceTest.createRestRequest(GET, GET_CLUSTER_MAP_SNAPSHOT, null, null);
        MockRestResponseChannel restResponseChannel = new MockRestResponseChannel();
        doOperation(restRequest, restResponseChannel);
        JSONObject expected = clusterMap.getSnapshot();
        JSONObject actual = new JSONObject(new String(restResponseChannel.getResponseBody()));
        // remove timestamps because they may differ
        expected.remove(TIMESTAMP_MS);
        actual.remove(TIMESTAMP_MS);
        Assert.assertEquals("Snapshot does not match expected", expected.toString(), actual.toString());
        // test a failure to ensure that it goes through the exception path
        String msg = UtilsTest.getRandomString(10);
        clusterMap.setExceptionOnSnapshot(new RuntimeException(msg));
        restRequest = AmbryBlobStorageServiceTest.createRestRequest(GET, GET_CLUSTER_MAP_SNAPSHOT, null, null);
        try {
            doOperation(restRequest, new MockRestResponseChannel());
            Assert.fail("Operation should have failed");
        } catch (RuntimeException e) {
            Assert.assertEquals("Exception not as expected", msg, e.getMessage());
        }
        clusterMap.setExceptionOnSnapshot(null);
    }

    /**
     * Tests the handling of {@link Operations#ACCOUNTS} get requests.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getAccountsTest() throws Exception {
        RestRequest restRequest = AmbryBlobStorageServiceTest.createRestRequest(GET, ACCOUNTS, null, null);
        MockRestResponseChannel restResponseChannel = new MockRestResponseChannel();
        doOperation(restRequest, restResponseChannel);
        Set<Account> expected = new java.util.HashSet(accountService.getAllAccounts());
        Set<Account> actual = new java.util.HashSet(AccountCollectionSerde.fromJson(new JSONObject(new String(restResponseChannel.getResponseBody()))));
        Assert.assertEquals("Unexpected GET /accounts response", expected, actual);
        // test an account not found case to ensure that it goes through the exception path
        restRequest = AmbryBlobStorageServiceTest.createRestRequest(GET, ACCOUNTS, new JSONObject().put(TARGET_ACCOUNT_ID, accountService.generateRandomAccount().getId()), null);
        try {
            doOperation(restRequest, new MockRestResponseChannel());
            Assert.fail("Operation should have failed");
        } catch (RestServiceException e) {
            Assert.assertEquals("Error code not as expected", NotFound, e.getErrorCode());
        }
    }

    /**
     * Tests the handling of {@link Operations#ACCOUNTS} post requests.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void postAccountsTest() throws Exception {
        Account accountToAdd = accountService.generateRandomAccount();
        List<ByteBuffer> body = new LinkedList<>();
        body.add(ByteBuffer.wrap(AccountCollectionSerde.toJson(Collections.singleton(accountToAdd)).toString().getBytes(StandardCharsets.UTF_8)));
        body.add(null);
        RestRequest restRequest = AmbryBlobStorageServiceTest.createRestRequest(POST, ACCOUNTS, null, body);
        MockRestResponseChannel restResponseChannel = new MockRestResponseChannel();
        doOperation(restRequest, restResponseChannel);
        Assert.assertEquals("Account not created correctly", accountToAdd, accountService.getAccountById(accountToAdd.getId()));
        // test an invalid request case to ensure that it goes through the exception path
        body = new LinkedList<>();
        body.add(ByteBuffer.wrap("abcdefghijk".toString().getBytes(StandardCharsets.UTF_8)));
        body.add(null);
        restRequest = AmbryBlobStorageServiceTest.createRestRequest(POST, ACCOUNTS, null, body);
        try {
            doOperation(restRequest, new MockRestResponseChannel());
            Assert.fail("Operation should have failed");
        } catch (RestServiceException e) {
            Assert.assertEquals("Error code not as expected", BadRequest, e.getErrorCode());
        }
    }

    /**
     * Tests reactions of the {@link GetReplicasHandler#getReplicas(String, RestResponseChannel)} operation to bad input -
     * specifically if we do not include required parameters.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getReplicasWithBadInputTest() throws Exception {
        // bad input - invalid blob id.
        RestRequest restRequest = AmbryBlobStorageServiceTest.createRestRequest(GET, ("12345/" + (SubResource.Replicas)), null, null);
        verifyOperationFailure(restRequest, BadRequest);
        // bad input - invalid blob id for this cluster map.
        String blobId = "AAEAAQAAAAAAAADFAAAAJDMyYWZiOTJmLTBkNDYtNDQyNS1iYzU0LWEwMWQ1Yzg3OTJkZQ.gif";
        restRequest = AmbryBlobStorageServiceTest.createRestRequest(GET, ((blobId + "/") + (SubResource.Replicas)), null, null);
        verifyOperationFailure(restRequest, BadRequest);
    }

    /**
     * Tests the handling of {@link Operations#GET_SIGNED_URL} requests.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getAndUseSignedUrlTest() throws Exception {
        // setup
        int contentLength = 10;
        ByteBuffer content = ByteBuffer.wrap(TestUtils.getRandomBytes(contentLength));
        long blobTtl = 7200;
        String serviceId = "getAndUseSignedUrlTest";
        String contentType = "application/octet-stream";
        String ownerId = "getAndUseSignedUrlTest";
        JSONObject headers = new JSONObject();
        headers.put(URL_TYPE, POST.name());
        AmbryBlobStorageServiceTest.setAmbryHeadersForPut(headers, blobTtl, (!(refContainer.isCacheable())), serviceId, contentType, ownerId, refAccount.getName(), refContainer.getName());
        Map<String, String> userMetadata = new HashMap<>();
        userMetadata.put(((Headers.USER_META_DATA_HEADER_PREFIX) + "key1"), "value1");
        userMetadata.put(((Headers.USER_META_DATA_HEADER_PREFIX) + "key2"), "value2");
        RestUtilsTest.setUserMetadataHeaders(headers, userMetadata);
        // POST
        // Get signed URL
        RestRequest getSignedUrlRequest = AmbryBlobStorageServiceTest.createRestRequest(GET, GET_SIGNED_URL, headers, null);
        MockRestResponseChannel restResponseChannel = new MockRestResponseChannel();
        doOperation(getSignedUrlRequest, restResponseChannel);
        Assert.assertEquals("Account not as expected", refAccount, getSignedUrlRequest.getArgs().get(TARGET_ACCOUNT_KEY));
        Assert.assertEquals("Container not as expected", refContainer, getSignedUrlRequest.getArgs().get(TARGET_CONTAINER_KEY));
        Assert.assertEquals("Unexpected response status", Ok, restResponseChannel.getStatus());
        String signedPostUrl = restResponseChannel.getHeader(SIGNED_URL);
        Assert.assertNotNull("Did not get a signed POST URL", signedPostUrl);
        // Use signed URL to POST
        List<ByteBuffer> contents = new LinkedList<>();
        contents.add(content);
        contents.add(null);
        RestRequest postSignedRequest = AmbryBlobStorageServiceTest.createRestRequest(POST, signedPostUrl, null, contents);
        restResponseChannel = new MockRestResponseChannel();
        doOperation(postSignedRequest, restResponseChannel);
        String blobId = verifyPostAndReturnBlobId(postSignedRequest, restResponseChannel, refAccount, refContainer);
        // verify POST
        headers.put(BLOB_SIZE, contentLength);
        getBlobAndVerify(blobId, null, null, headers, content, refAccount, refContainer);
        getBlobInfoAndVerify(blobId, null, headers, refAccount, refContainer);
        // GET
        // Get signed URL
        JSONObject getHeaders = new JSONObject();
        getHeaders.put(URL_TYPE, GET.name());
        blobId = (blobId.startsWith("/")) ? blobId.substring(1) : blobId;
        getHeaders.put(BLOB_ID, blobId);
        getSignedUrlRequest = AmbryBlobStorageServiceTest.createRestRequest(GET, GET_SIGNED_URL, getHeaders, null);
        restResponseChannel = new MockRestResponseChannel();
        doOperation(getSignedUrlRequest, restResponseChannel);
        Assert.assertEquals("Account not as expected", refAccount, getSignedUrlRequest.getArgs().get(TARGET_ACCOUNT_KEY));
        Assert.assertEquals("Container not as expected", refContainer, getSignedUrlRequest.getArgs().get(TARGET_CONTAINER_KEY));
        Assert.assertEquals("Unexpected response status", Ok, restResponseChannel.getStatus());
        String signedGetUrl = restResponseChannel.getHeader(SIGNED_URL);
        Assert.assertNotNull("Did not get a signed GET URL", signedGetUrl);
        // Use URL to GET blob
        RestRequest getSignedRequest = AmbryBlobStorageServiceTest.createRestRequest(GET, signedGetUrl, null, null);
        restResponseChannel = new MockRestResponseChannel();
        doOperation(getSignedRequest, restResponseChannel);
        verifyGetBlobResponse(getSignedRequest, restResponseChannel, null, headers, content, refAccount, refContainer);
        // one error scenario to exercise exception path
        verifyOperationFailure(AmbryBlobStorageServiceTest.createRestRequest(GET, GET_SIGNED_URL, null, null), MissingArgs);
    }

    /**
     * Tests for handling of {@link RestMethod#OPTIONS}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void optionsTest() throws Exception {
        RestRequest restRequest = AmbryBlobStorageServiceTest.createRestRequest(OPTIONS, "/", null, null);
        MockRestResponseChannel restResponseChannel = new MockRestResponseChannel();
        doOperation(restRequest, restResponseChannel);
        Assert.assertEquals("Unexpected response status", Ok, restResponseChannel.getStatus());
        Assert.assertTrue("No Date header", ((restResponseChannel.getHeader(DATE)) != null));
        Assert.assertEquals("Unexpected content length", 0, Long.parseLong(restResponseChannel.getHeader(RestUtils.Headers.CONTENT_LENGTH)));
        Assert.assertEquals(("Unexpected value for " + (Headers.ACCESS_CONTROL_ALLOW_METHODS)), frontendConfig.optionsAllowMethods, restResponseChannel.getHeader(ACCESS_CONTROL_ALLOW_METHODS));
        Assert.assertEquals(("Unexpected value for " + (Headers.ACCESS_CONTROL_MAX_AGE)), frontendConfig.optionsValiditySeconds, Long.parseLong(restResponseChannel.getHeader(ACCESS_CONTROL_MAX_AGE)));
    }

    /**
     * Tests the case when the TTL update is rejected
     *
     * @throws Exception
     * 		
     */
    @Test
    public void updateTtlRejectedTest() throws Exception {
        FrontendTestRouter testRouter = new FrontendTestRouter();
        String exceptionMsg = UtilsTest.getRandomString(10);
        testRouter.exceptionToReturn = new com.github.ambry.router.RouterException(exceptionMsg, RouterErrorCode.BlobUpdateNotAllowed);
        testRouter.exceptionOpType = UpdateBlobTtl;
        ambryBlobStorageService = new AmbryBlobStorageService(frontendConfig, frontendMetrics, responseHandler, testRouter, clusterMap, idConverterFactory, securityServiceFactory, urlSigningService, idSigningService, accountService, accountAndContainerInjector, datacenterName, hostname);
        ambryBlobStorageService.start();
        String blobId = getID();
        JSONObject headers = new JSONObject();
        setUpdateTtlHeaders(headers, blobId, "updateTtlRejectedTest");
        RestRequest restRequest = AmbryBlobStorageServiceTest.createRestRequest(PUT, UPDATE_TTL, headers, null);
        MockRestResponseChannel restResponseChannel = verifyOperationFailure(restRequest, NotAllowed);
        Assert.assertEquals("Unexpected response status", MethodNotAllowed, restResponseChannel.getStatus());
        Assert.assertEquals("Unexpected value for the 'allow' header", TTL_UPDATE_REJECTED_ALLOW_HEADER_VALUE, restResponseChannel.getHeader(ALLOW));
    }

    /**
     * Tests the injection of {@link GetOption#Include_All} and {@link GetOption#Include_Deleted_Blobs} as the default
     * {@link GetOption}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void defaultGetDeletedTest() throws Exception {
        AmbryBlobStorageServiceTest.PostResults postResults = prepareAndPostBlob(1024, "defaultGetOptionsTest", TTL_SECS, "application/octet-stream", "defaultGetOptionsTest", refAccount, refContainer, null);
        // this also verifies that the blob is inaccessible
        deleteBlobAndVerify(postResults.blobId, postResults.headers, postResults.content, refAccount, refContainer, false);
        // now reload AmbryBlobStorageService with a new default get option (Include_Deleted and Include_All) and the blob
        // can be retrieved
        verifyGetWithDefaultOptions(postResults.blobId, postResults.headers, postResults.content, EnumSet.of(Include_Deleted_Blobs, Include_All));
        // won't work with default GetOption.None
        restartAmbryBlobStorageServiceWithDefaultGetOption(None);
        verifyOperationsAfterDelete(postResults.blobId, postResults.headers, postResults.content, refAccount, refContainer);
    }

    /**
     * Tests the injection of {@link GetOption#Include_All} and {@link GetOption#Include_Expired_Blobs} as the default
     * {@link GetOption}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void defaultGetExpiredTest() throws Exception {
        AmbryBlobStorageServiceTest.PostResults postResults = prepareAndPostBlob(1024, "defaultGetOptionsTest", 0, "application/octet-stream", "defaultGetOptionsTest", refAccount, refContainer, null);
        Thread.sleep(5);
        RestRequest restRequest = AmbryBlobStorageServiceTest.createRestRequest(GET, postResults.blobId, null, null);
        verifyOperationFailure(restRequest, Deleted);
        // now reload AmbryBlobStorageService with a new default get option (Include_Expired and Include_All) and the blob
        // can be retrieved
        verifyGetWithDefaultOptions(postResults.blobId, postResults.headers, postResults.content, EnumSet.of(Include_Expired_Blobs, Include_All));
        // won't work with default GetOption.None
        restartAmbryBlobStorageServiceWithDefaultGetOption(None);
        restRequest = AmbryBlobStorageServiceTest.createRestRequest(GET, postResults.blobId, null, null);
        verifyOperationFailure(restRequest, Deleted);
    }

    /**
     * Test that the secure path is validated if required by {@link Container}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void validateSecurePathTest() throws Exception {
        short refAccountId = Utils.getRandomShort(RANDOM);
        String refAccountName = UtilsTest.getRandomString(10);
        short[] refContainerIds = new short[]{ 2, 3 };
        String[] refContainerNames = new String[]{ "SecurePathValidation", "NoValidation" };
        Container signedPathRequiredContainer = setSecurePathRequired(true).build();
        Container noValidationContainer = setSecurePathRequired(false).build();
        Account account = new com.github.ambry.account.AccountBuilder(refAccountId, refAccountName, AccountStatus.ACTIVE).addOrUpdateContainer(signedPathRequiredContainer).addOrUpdateContainer(noValidationContainer).build();
        accountService.updateAccounts(Collections.singletonList(account));
        ByteBuffer content = ByteBuffer.wrap(TestUtils.getRandomBytes(CONTENT_LENGTH));
        String contentType = "application/octet-stream";
        String ownerId = "SecurePathValidationTest";
        JSONObject headers = new JSONObject();
        AmbryBlobStorageServiceTest.setAmbryHeadersForPut(headers, TTL_SECS, false, refAccountName, contentType, ownerId, refAccountName, refContainerNames[0]);
        Map<String, String> userMetadata = new HashMap<>();
        userMetadata.put(((Headers.USER_META_DATA_HEADER_PREFIX) + "key1"), "value1");
        RestUtilsTest.setUserMetadataHeaders(headers, userMetadata);
        String blobId = postBlobAndVerify(headers, content, account, signedPathRequiredContainer);
        headers.put(BLOB_SIZE, ((long) (CONTENT_LENGTH)));
        // test that secure path validation succeeded
        String testUri = ("/" + (frontendConfig.securePathPrefix)) + blobId;
        getBlobAndVerify(testUri, null, null, headers, content, account, signedPathRequiredContainer);
        // test that no secure path should fail (return AccessDenied)
        try {
            getBlobAndVerify(blobId, null, null, headers, content, account, signedPathRequiredContainer);
            Assert.fail("get blob should fail because secure path is missing");
        } catch (Exception e) {
            Assert.assertEquals("Mismatch in error code", AccessDenied, getErrorCode());
        }
        // test that secure path equals other prefix should fail (return AccessDenied)
        try {
            getBlobAndVerify(("/media" + blobId), null, null, headers, content, account, signedPathRequiredContainer);
            Assert.fail("get blob should fail because secure path equals other prefix and doesn't match expected one");
        } catch (Exception e) {
            Assert.assertEquals("Mismatch in error code", AccessDenied, getErrorCode());
        }
        // test that incorrect path should fail (return BadRequest)
        try {
            getBlobAndVerify(("/incorrect-path" + blobId), null, null, headers, content, account, signedPathRequiredContainer);
            Assert.fail("get blob should fail because secure path is incorrect");
        } catch (Exception e) {
            Assert.assertEquals("Mismatch in error code", BadRequest, getErrorCode());
        }
        // test container with no validation
        AmbryBlobStorageServiceTest.setAmbryHeadersForPut(headers, TTL_SECS, false, refAccountName, contentType, ownerId, refAccountName, refContainerNames[1]);
        content = ByteBuffer.wrap(TestUtils.getRandomBytes(CONTENT_LENGTH));
        blobId = postBlobAndVerify(headers, content, account, noValidationContainer);
        // test container with no validation should fail if there is invalid path in URI
        try {
            getBlobAndVerify(("/incorrect-path" + blobId), null, null, headers, content, account, noValidationContainer);
            Assert.fail("get blob should fail because there is invalid path in uri");
        } catch (Exception e) {
            Assert.assertEquals("Mismatch in error code", BadRequest, getErrorCode());
        }
        // test container with no validation should succeed if URI is correct
        getBlobAndVerify(blobId, null, null, headers, content, account, noValidationContainer);
    }

    /**
     * Results from a POST performed against {@link AmbryBlobStorageService}
     */
    private class PostResults {
        final String blobId;

        final JSONObject headers;

        final ByteBuffer content;

        PostResults(String blobId, JSONObject headers, ByteBuffer content) {
            this.blobId = blobId;
            this.headers = headers;
            this.content = content;
        }
    }
}

