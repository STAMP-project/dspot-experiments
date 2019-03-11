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


import Container.DEFAULT_PUBLIC_CONTAINER_ID;
import Container.UNKNOWN_CONTAINER_ID;
import InMemAccountService.UNKNOWN_ACCOUNT;
import Operations.GET_SIGNED_URL;
import RestMethod.GET;
import RestMethod.HEAD;
import RestMethod.OPTIONS;
import RestMethod.POST;
import RestMethod.PUT;
import RestServiceErrorCode.BadRequest;
import RestServiceErrorCode.InternalServerError;
import RestServiceErrorCode.ServiceUnavailable;
import RestServiceErrorCode.TooManyRequests;
import RestServiceErrorCode.Unauthorized;
import RestUtils.Headers;
import RestUtils.InternalKeys;
import RestUtils.InternalKeys.KEEP_ALIVE_ON_ERROR_HINT;
import RestUtils.InternalKeys.SEND_TRACKING_INFO;
import RestUtils.SubResource;
import RestUtils.SubResource.UserMetadata;
import TestUtils.RANDOM;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.account.Account;
import com.github.ambry.account.Container;
import com.github.ambry.account.InMemAccountService;
import com.github.ambry.account.InMemAccountServiceFactory;
import com.github.ambry.config.FrontendConfig;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.messageformat.BlobInfo;
import com.github.ambry.rest.MockRestResponseChannel;
import com.github.ambry.rest.ResponseStatus;
import com.github.ambry.rest.RestMethod;
import com.github.ambry.rest.RestRequest;
import com.github.ambry.rest.RestResponseChannel;
import com.github.ambry.rest.RestServiceErrorCode;
import com.github.ambry.rest.RestServiceException;
import com.github.ambry.rest.RestUtils;
import com.github.ambry.router.Callback;
import com.github.ambry.utils.TestUtils;
import com.github.ambry.utils.Utils;
import com.github.ambry.utils.UtilsTest;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests {@link AmbrySecurityService}
 */
public class AmbrySecurityServiceTest {
    private static final FrontendConfig FRONTEND_CONFIG = new FrontendConfig(new VerifiableProperties(new Properties()));

    private static final String SERVICE_ID = "AmbrySecurityService";

    private static final String OWNER_ID = AmbrySecurityServiceTest.SERVICE_ID;

    private static final InMemAccountService ACCOUNT_SERVICE = new InMemAccountServiceFactory(false, true).getAccountService();

    private static final QuotaManager quotaManager = new QuotaManager(AmbrySecurityServiceTest.FRONTEND_CONFIG);

    private static final Account REF_ACCOUNT;

    private static final Container REF_CONTAINER;

    private static final Map<String, Object> USER_METADATA = new HashMap<>();

    private static final BlobInfo DEFAULT_INFO;

    private static final BlobInfo UNKNOWN_INFO = new BlobInfo(new com.github.ambry.messageformat.BlobProperties(100, AmbrySecurityServiceTest.SERVICE_ID, AmbrySecurityServiceTest.OWNER_ID, "image/gif", false, Utils.Infinite_Time, Account.UNKNOWN_ACCOUNT_ID, Container.UNKNOWN_CONTAINER_ID, false, null), new byte[0]);

    private static final BlobInfo UNKNOWN_INFO_ENC = new BlobInfo(new com.github.ambry.messageformat.BlobProperties(100, AmbrySecurityServiceTest.SERVICE_ID, AmbrySecurityServiceTest.OWNER_ID, "image/gif", false, Utils.Infinite_Time, Account.UNKNOWN_ACCOUNT_ID, Container.UNKNOWN_CONTAINER_ID, true, null), new byte[0]);

    private static final FrontendTestUrlSigningServiceFactory URL_SIGNING_SERVICE_FACTORY = new FrontendTestUrlSigningServiceFactory();

    private final SecurityService securityService = new AmbrySecurityService(AmbrySecurityServiceTest.FRONTEND_CONFIG, new FrontendMetrics(new MetricRegistry()), AmbrySecurityServiceTest.URL_SIGNING_SERVICE_FACTORY.getUrlSigningService(), AmbrySecurityServiceTest.quotaManager);

    static {
        try {
            AmbrySecurityServiceTest.ACCOUNT_SERVICE.clear();
            REF_ACCOUNT = AmbrySecurityServiceTest.ACCOUNT_SERVICE.createAndAddRandomAccount();
            REF_CONTAINER = AmbrySecurityServiceTest.REF_ACCOUNT.getContainerById(DEFAULT_PUBLIC_CONTAINER_ID);
            AmbrySecurityServiceTest.USER_METADATA.put(((Headers.USER_META_DATA_HEADER_PREFIX) + (UtilsTest.getRandomString(9))), UtilsTest.getRandomString(9));
            AmbrySecurityServiceTest.USER_METADATA.put(((Headers.USER_META_DATA_HEADER_PREFIX) + (UtilsTest.getRandomString(10))), UtilsTest.getRandomString(10));
            AmbrySecurityServiceTest.USER_METADATA.put(((Headers.USER_META_DATA_HEADER_PREFIX) + (UtilsTest.getRandomString(11))), UtilsTest.getRandomString(11));
            DEFAULT_INFO = new BlobInfo(new com.github.ambry.messageformat.BlobProperties(((Utils.getRandomLong(RANDOM, 1000)) + 100), AmbrySecurityServiceTest.SERVICE_ID, AmbrySecurityServiceTest.OWNER_ID, "image/gif", false, Utils.Infinite_Time, AmbrySecurityServiceTest.REF_ACCOUNT.getId(), AmbrySecurityServiceTest.REF_CONTAINER.getId(), false, null), RestUtils.buildUserMetadata(AmbrySecurityServiceTest.USER_METADATA));
            AmbrySecurityServiceTest.ACCOUNT_SERVICE.updateAccounts(Collections.singletonList(UNKNOWN_ACCOUNT));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Tests for {@link AmbrySecurityService#preProcessRequest(RestRequest, Callback)}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void preProcessRequestTest() throws Exception {
        RestMethod[] methods = new RestMethod[]{ RestMethod.POST, RestMethod.GET, RestMethod.DELETE, RestMethod.HEAD, RestMethod.OPTIONS, RestMethod.PUT };
        for (RestMethod restMethod : methods) {
            // add a header that is prohibited
            JSONObject headers = new JSONObject();
            headers.put(KEEP_ALIVE_ON_ERROR_HINT, true);
            RestRequest restRequest = createRestRequest(restMethod, "/", headers);
            try {
                securityService.preProcessRequest(restRequest).get(1, TimeUnit.SECONDS);
                Assert.fail(("Should have failed because the request contains a prohibited header: " + (InternalKeys.KEEP_ALIVE_ON_ERROR_HINT)));
            } catch (ExecutionException e) {
                RestServiceException rse = ((RestServiceException) (Utils.getRootCause(e)));
                Assert.assertEquals("Should be a bad request", BadRequest, rse.getErrorCode());
            }
        }
        // verify request args regarding to tracking is set accordingly
        RestRequest restRequest = createRestRequest(GET, "/", null);
        securityService.preProcessRequest(restRequest).get();
        Assert.assertTrue("The arg with key: ambry-internal-keys-send-tracking-info should be set to true", ((Boolean) (restRequest.getArgs().get(SEND_TRACKING_INFO))));
        Properties properties = new Properties();
        properties.setProperty("frontend.attach.tracking.info", "false");
        FrontendConfig frontendConfig = new FrontendConfig(new VerifiableProperties(properties));
        SecurityService securityServiceWithTrackingDisabled = new AmbrySecurityService(frontendConfig, new FrontendMetrics(new MetricRegistry()), AmbrySecurityServiceTest.URL_SIGNING_SERVICE_FACTORY.getUrlSigningService(), AmbrySecurityServiceTest.quotaManager);
        restRequest = createRestRequest(GET, "/", null);
        securityServiceWithTrackingDisabled.preProcessRequest(restRequest);
        Assert.assertFalse("The arg with key: ambry-internal-keys-send-tracking-info should be set to false", ((Boolean) (restRequest.getArgs().get(SEND_TRACKING_INFO))));
    }

    /**
     * Tests {@link AmbrySecurityService#processRequest(RestRequest, Callback)} for common as well as uncommon cases
     *
     * @throws Exception
     * 		
     */
    @Test
    public void processRequestTest() throws Exception {
        // rest request being null
        TestUtils.assertException(IllegalArgumentException.class, () -> securityService.preProcessRequest(null).get(), null);
        TestUtils.assertException(IllegalArgumentException.class, () -> securityService.processRequest(null).get(), null);
        TestUtils.assertException(IllegalArgumentException.class, () -> securityService.postProcessRequest(null).get(), null);
        // without callbacks
        RestMethod[] methods = new RestMethod[]{ RestMethod.POST, RestMethod.GET, RestMethod.DELETE, RestMethod.HEAD, RestMethod.OPTIONS, RestMethod.PUT };
        for (RestMethod restMethod : methods) {
            RestRequest restRequest = createRestRequest(restMethod, "/", null);
            securityService.preProcessRequest(restRequest).get();
            securityService.processRequest(restRequest).get();
            securityService.postProcessRequest(restRequest).get();
        }
        // with GET sub resources
        for (RestUtils.SubResource subResource : SubResource.values()) {
            RestRequest restRequest = createRestRequest(GET, ("/sampleId/" + subResource), null);
            Account account = InMemAccountService.UNKNOWN_ACCOUNT;
            insertAccountAndContainer(restRequest, account, account.getContainerById(UNKNOWN_CONTAINER_ID));
            securityService.preProcessRequest(restRequest).get();
            securityService.processRequest(restRequest).get();
            securityService.postProcessRequest(restRequest).get();
        }
        // with UrlSigningService denying the request
        AmbrySecurityServiceTest.URL_SIGNING_SERVICE_FACTORY.isRequestSigned = true;
        AmbrySecurityServiceTest.URL_SIGNING_SERVICE_FACTORY.verifySignedRequestException = new RestServiceException("Msg", RestServiceErrorCode.Unauthorized);
        testExceptionCasesProcessRequest(createRestRequest(GET, "/", null), Unauthorized, false);
        AmbrySecurityServiceTest.URL_SIGNING_SERVICE_FACTORY.isRequestSigned = false;
        // security service closed
        securityService.close();
        for (RestMethod restMethod : methods) {
            testExceptionCasesProcessRequest(createRestRequest(restMethod, "/", null), ServiceUnavailable, true);
        }
    }

    /**
     * {@link AmbrySecurityService#postProcessRequest(RestRequest, Callback)})} should throw RestServiceException if rate
     * is more than expected. RestServiceErrorCode.TooManyRequests is expected in this case.
     */
    @Test
    public void postProcessQuotaManagerTest() throws Exception {
        QuotaManager quotaManager = Mockito.mock(QuotaManager.class);
        AmbrySecurityService ambrySecurityService = new AmbrySecurityService(new FrontendConfig(new VerifiableProperties(new Properties())), new FrontendMetrics(new MetricRegistry()), AmbrySecurityServiceTest.URL_SIGNING_SERVICE_FACTORY.getUrlSigningService(), quotaManager);
        // Everything should be good.
        Mockito.when(quotaManager.shouldThrottle(ArgumentMatchers.any())).thenReturn(false);
        for (int i = 0; i < 100; i++) {
            for (RestMethod restMethod : RestMethod.values()) {
                RestRequest restRequest = createRestRequest(restMethod, "/", null);
                ambrySecurityService.postProcessRequest(restRequest).get();
            }
        }
        // Requests should be denied.
        Mockito.when(quotaManager.shouldThrottle(ArgumentMatchers.any())).thenReturn(true);
        for (RestMethod restMethod : RestMethod.values()) {
            RestRequest restRequest = createRestRequest(restMethod, "/", null);
            try {
                ambrySecurityService.postProcessRequest(restRequest).get();
                Assert.fail("Should have failed.");
            } catch (Exception e) {
                Assert.assertEquals("Exception should be TooManyRequests", TooManyRequests, getErrorCode());
            }
        }
    }

    /**
     * Tests {@link AmbrySecurityService#processResponse(RestRequest, RestResponseChannel, BlobInfo, Callback)}  for
     * common as well as uncommon cases
     *
     * @throws Exception
     * 		
     */
    @Test
    public void processResponseTest() throws Exception {
        RestRequest restRequest = createRestRequest(GET, "/", null);
        // rest request being null
        TestUtils.assertException(IllegalArgumentException.class, () -> securityService.processResponse(null, new MockRestResponseChannel(), DEFAULT_INFO).get(), null);
        // restResponseChannel being null
        TestUtils.assertException(IllegalArgumentException.class, () -> securityService.processResponse(restRequest, null, DEFAULT_INFO).get(), null);
        // blob info being null
        TestUtils.assertException(IllegalArgumentException.class, () -> securityService.processResponse(restRequest, new MockRestResponseChannel(), null).get(), null);
        // for unsupported methods
        RestMethod[] methods = new RestMethod[]{ RestMethod.DELETE };
        for (RestMethod restMethod : methods) {
            testExceptionCasesProcessResponse(restMethod, new MockRestResponseChannel(), AmbrySecurityServiceTest.DEFAULT_INFO, InternalServerError);
        }
        // OPTIONS (should be no errors)
        securityService.processResponse(createRestRequest(OPTIONS, "/", null), new MockRestResponseChannel(), null).get();
        // PUT (should be no errors)
        securityService.processResponse(createRestRequest(PUT, "/", null), new MockRestResponseChannel(), null).get();
        // GET signed URL (should be no errors)
        securityService.processResponse(createRestRequest(GET, GET_SIGNED_URL, null), new MockRestResponseChannel(), null).get();
        // HEAD
        // normal
        testHeadBlobWithVariousRanges(AmbrySecurityServiceTest.DEFAULT_INFO);
        // unknown account
        testHeadBlobWithVariousRanges(AmbrySecurityServiceTest.UNKNOWN_INFO);
        // encrypted unknown account
        testHeadBlobWithVariousRanges(AmbrySecurityServiceTest.UNKNOWN_INFO_ENC);
        // with no owner id
        BlobInfo blobInfo = new BlobInfo(new com.github.ambry.messageformat.BlobProperties(100, AmbrySecurityServiceTest.SERVICE_ID, null, "image/gif", false, Utils.Infinite_Time, AmbrySecurityServiceTest.REF_ACCOUNT.getId(), AmbrySecurityServiceTest.REF_CONTAINER.getId(), false, null), new byte[0]);
        testHeadBlobWithVariousRanges(blobInfo);
        // with no content type
        blobInfo = new BlobInfo(new com.github.ambry.messageformat.BlobProperties(100, AmbrySecurityServiceTest.SERVICE_ID, AmbrySecurityServiceTest.OWNER_ID, null, false, Utils.Infinite_Time, AmbrySecurityServiceTest.REF_ACCOUNT.getId(), AmbrySecurityServiceTest.REF_CONTAINER.getId(), false, null), new byte[0]);
        testHeadBlobWithVariousRanges(blobInfo);
        // with a TTL
        blobInfo = new BlobInfo(new com.github.ambry.messageformat.BlobProperties(100, AmbrySecurityServiceTest.SERVICE_ID, AmbrySecurityServiceTest.OWNER_ID, "image/gif", false, 10000, AmbrySecurityServiceTest.REF_ACCOUNT.getId(), AmbrySecurityServiceTest.REF_CONTAINER.getId(), false, null), new byte[0]);
        testHeadBlobWithVariousRanges(blobInfo);
        // GET BlobInfo
        testGetSubResource(AmbrySecurityServiceTest.DEFAULT_INFO, RestUtils.SubResource.BlobInfo);
        testGetSubResource(AmbrySecurityServiceTest.UNKNOWN_INFO, RestUtils.SubResource.BlobInfo);
        testGetSubResource(AmbrySecurityServiceTest.UNKNOWN_INFO, RestUtils.SubResource.BlobInfo);
        testGetSubResource(AmbrySecurityServiceTest.UNKNOWN_INFO_ENC, RestUtils.SubResource.BlobInfo);
        // GET UserMetadata
        testGetSubResource(AmbrySecurityServiceTest.DEFAULT_INFO, UserMetadata);
        byte[] usermetadata = TestUtils.getRandomBytes(10);
        testGetSubResource(new BlobInfo(AmbrySecurityServiceTest.DEFAULT_INFO.getBlobProperties(), usermetadata), UserMetadata);
        // POST
        testPostBlob();
        // GET Blob
        // less than chunk threshold size
        blobInfo = new BlobInfo(new com.github.ambry.messageformat.BlobProperties(((AmbrySecurityServiceTest.FRONTEND_CONFIG.chunkedGetResponseThresholdInBytes) - 1), AmbrySecurityServiceTest.SERVICE_ID, AmbrySecurityServiceTest.OWNER_ID, "image/gif", false, 10000, Account.UNKNOWN_ACCOUNT_ID, Container.UNKNOWN_CONTAINER_ID, false, null), new byte[0]);
        testGetBlobWithVariousRanges(blobInfo);
        // == chunk threshold size
        blobInfo = new BlobInfo(new com.github.ambry.messageformat.BlobProperties(AmbrySecurityServiceTest.FRONTEND_CONFIG.chunkedGetResponseThresholdInBytes, AmbrySecurityServiceTest.SERVICE_ID, AmbrySecurityServiceTest.OWNER_ID, "image/gif", false, 10000, Account.UNKNOWN_ACCOUNT_ID, Container.UNKNOWN_CONTAINER_ID, false, null), new byte[0]);
        testGetBlobWithVariousRanges(blobInfo);
        // more than chunk threshold size
        blobInfo = new BlobInfo(new com.github.ambry.messageformat.BlobProperties(((AmbrySecurityServiceTest.FRONTEND_CONFIG.chunkedGetResponseThresholdInBytes) * 2), AmbrySecurityServiceTest.SERVICE_ID, AmbrySecurityServiceTest.OWNER_ID, "image/gif", false, 10000, Account.UNKNOWN_ACCOUNT_ID, Container.UNKNOWN_CONTAINER_ID, false, null), new byte[0]);
        testGetBlobWithVariousRanges(blobInfo);
        // Get blob with content type null
        blobInfo = new BlobInfo(new com.github.ambry.messageformat.BlobProperties(100, AmbrySecurityServiceTest.SERVICE_ID, AmbrySecurityServiceTest.OWNER_ID, null, true, 10000, Account.UNKNOWN_ACCOUNT_ID, Container.UNKNOWN_CONTAINER_ID, false, null), new byte[0]);
        testGetBlobWithVariousRanges(blobInfo);
        // Get blob in a non-cacheable container. AmbrySecurityService should not care about the isPrivate setting.
        blobInfo = new BlobInfo(new com.github.ambry.messageformat.BlobProperties(100, AmbrySecurityServiceTest.SERVICE_ID, AmbrySecurityServiceTest.OWNER_ID, "image/gif", false, Utils.Infinite_Time, Account.UNKNOWN_ACCOUNT_ID, Container.DEFAULT_PRIVATE_CONTAINER_ID, false, null), new byte[0]);
        testGetBlobWithVariousRanges(blobInfo);
        // Get blob in a cacheable container. AmbrySecurityService should not care about the isPrivate setting.
        blobInfo = new BlobInfo(new com.github.ambry.messageformat.BlobProperties(100, AmbrySecurityServiceTest.SERVICE_ID, AmbrySecurityServiceTest.OWNER_ID, "image/gif", true, Utils.Infinite_Time, Account.UNKNOWN_ACCOUNT_ID, Container.DEFAULT_PUBLIC_CONTAINER_ID, false, null), new byte[0]);
        testGetBlobWithVariousRanges(blobInfo);
        // not modified response
        // > creation time (in secs).
        testGetNotModifiedBlob(blobInfo, ((blobInfo.getBlobProperties().getCreationTimeInMs()) + 1000));
        // == creation time
        testGetNotModifiedBlob(blobInfo, blobInfo.getBlobProperties().getCreationTimeInMs());
        // < creation time (in secs)
        testGetNotModifiedBlob(blobInfo, ((blobInfo.getBlobProperties().getCreationTimeInMs()) - 1000));
        // Get blob for a public blob with content type as "text/html"
        blobInfo = new BlobInfo(new com.github.ambry.messageformat.BlobProperties(100, AmbrySecurityServiceTest.SERVICE_ID, AmbrySecurityServiceTest.OWNER_ID, "text/html", true, 10000, Account.UNKNOWN_ACCOUNT_ID, Container.UNKNOWN_CONTAINER_ID, false, null), new byte[0]);
        testGetBlobWithVariousRanges(blobInfo);
        // not modified response
        // > creation time (in secs).
        testGetNotModifiedBlob(AmbrySecurityServiceTest.DEFAULT_INFO, ((AmbrySecurityServiceTest.DEFAULT_INFO.getBlobProperties().getCreationTimeInMs()) + 1000));
        // == creation time
        testGetNotModifiedBlob(AmbrySecurityServiceTest.DEFAULT_INFO, AmbrySecurityServiceTest.DEFAULT_INFO.getBlobProperties().getCreationTimeInMs());
        // < creation time (in secs)
        testGetNotModifiedBlob(AmbrySecurityServiceTest.DEFAULT_INFO, ((AmbrySecurityServiceTest.DEFAULT_INFO.getBlobProperties().getCreationTimeInMs()) - 1000));
        // bad rest response channel
        testExceptionCasesProcessResponse(HEAD, new AmbrySecurityServiceTest.BadRestResponseChannel(), blobInfo, InternalServerError);
        testExceptionCasesProcessResponse(GET, new AmbrySecurityServiceTest.BadRestResponseChannel(), blobInfo, InternalServerError);
        testExceptionCasesProcessResponse(POST, new AmbrySecurityServiceTest.BadRestResponseChannel(), blobInfo, InternalServerError);
        // security service closed
        securityService.close();
        methods = new RestMethod[]{ RestMethod.POST, RestMethod.GET, RestMethod.DELETE, RestMethod.HEAD };
        for (RestMethod restMethod : methods) {
            testExceptionCasesProcessResponse(restMethod, new MockRestResponseChannel(), blobInfo, ServiceUnavailable);
        }
    }

    /**
     * A bad implementation of {@link RestResponseChannel}. Just throws exceptions.
     */
    class BadRestResponseChannel implements RestResponseChannel {
        @Override
        public Future<Long> write(ByteBuffer src, Callback<Long> callback) {
            return null;
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public void onResponseComplete(Exception exception) {
        }

        @Override
        public void setStatus(ResponseStatus status) throws RestServiceException {
            throw new RestServiceException("Not Implemented", RestServiceErrorCode.InternalServerError);
        }

        @Override
        public ResponseStatus getStatus() {
            throw new IllegalStateException("Not implemented");
        }

        @Override
        public void setHeader(String headerName, Object headerValue) throws RestServiceException {
            throw new RestServiceException("Not Implemented", RestServiceErrorCode.InternalServerError);
        }

        @Override
        public Object getHeader(String headerName) {
            throw new IllegalStateException("Not implemented");
        }
    }
}

