/**
 * Copyright 2017 LinkedIn Corp. All rights reserved.
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


import FrontendConfig.CHUNK_UPLOAD_INITIAL_CHUNK_TTL_SECS_KEY;
import FrontendConfig.URL_SIGNER_ENDPOINTS;
import RestMethod.GET;
import RestMethod.POST;
import RestServiceErrorCode.InternalServerError;
import RestUtils.Headers.URL_TYPE;
import com.github.ambry.commons.CommonTestUtils;
import com.github.ambry.rest.RestRequest;
import com.github.ambry.utils.MockTime;
import com.github.ambry.utils.Time;
import java.util.Properties;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import static AmbryUrlSigningService.AMBRY_PARAMETERS_PREFIX;


/**
 * Tests for {@link AmbryUrlSigningService}.
 */
public class AmbryUrlSigningServiceTest {
    private static final String UPLOAD_ENDPOINT = "http://uploadUrl:15158";

    private static final String DOWNLOAD_ENDPOINT = "http://downloadUrl:15158";

    private static final long DEFAULT_URL_TTL_SECS = 5 * 60;

    private static final long DEFAULT_MAX_UPLOAD_SIZE = (100 * 1024) * 1024;

    private static final long MAX_URL_TTL_SECS = 60 * 60;

    private static final long CHUNK_UPLOAD_INITIAL_CHUNK_TTL_SECS = (24 * 1024) * 1024;

    private static final long CHUNK_UPLOAD_MAX_CHUNK_SIZE = (4 * 1024) * 1024;

    private static final String RANDOM_AMBRY_HEADER = (AMBRY_PARAMETERS_PREFIX) + "random";

    /**
     * Tests for {@link AmbryUrlSigningServiceFactory}.
     */
    @Test
    public void factoryTest() {
        Properties properties = new Properties();
        JSONObject jsonObject = new JSONObject().put("POST", AmbryUrlSigningServiceTest.UPLOAD_ENDPOINT).put("GET", AmbryUrlSigningServiceTest.DOWNLOAD_ENDPOINT);
        properties.setProperty(URL_SIGNER_ENDPOINTS, jsonObject.toString());
        properties.setProperty("frontend.url.signer.default.url.ttl.secs", Long.toString(AmbryUrlSigningServiceTest.DEFAULT_URL_TTL_SECS));
        properties.setProperty("frontend.url.signer.default.max.upload.size.bytes", Long.toString(AmbryUrlSigningServiceTest.DEFAULT_MAX_UPLOAD_SIZE));
        properties.setProperty("frontend.url.signer.max.url.ttl.secs", Long.toString(AmbryUrlSigningServiceTest.MAX_URL_TTL_SECS));
        properties.setProperty(CHUNK_UPLOAD_INITIAL_CHUNK_TTL_SECS_KEY, Long.toString(AmbryUrlSigningServiceTest.CHUNK_UPLOAD_INITIAL_CHUNK_TTL_SECS));
        CommonTestUtils.populateRequiredRouterProps(properties);
        properties.setProperty("router.max.put.chunk.size.bytes", Long.toString(AmbryUrlSigningServiceTest.CHUNK_UPLOAD_MAX_CHUNK_SIZE));
        UrlSigningService signer = getUrlSigningService();
        Assert.assertNotNull("UrlSigningService is null", signer);
        Assert.assertTrue("UrlSigningService is AmbryUrlSigningService", (signer instanceof AmbryUrlSigningService));
        Assert.assertTrue(getUploadEndpoint().contains(AmbryUrlSigningServiceTest.UPLOAD_ENDPOINT));
        Assert.assertTrue(getDownloadEndpoint().contains(AmbryUrlSigningServiceTest.DOWNLOAD_ENDPOINT));
    }

    /**
     * Tests for {@link AmbryUrlSigningServiceFactory}.
     */
    @Test
    public void factoryTestBadJson() {
        Properties properties = new Properties();
        CommonTestUtils.populateRequiredRouterProps(properties);
        // Missing GET
        JSONObject jsonObject = new JSONObject().put("POST", AmbryUrlSigningServiceTest.UPLOAD_ENDPOINT);
        properties.setProperty(URL_SIGNER_ENDPOINTS, jsonObject.toString());
        try {
            new AmbryUrlSigningServiceFactory(new com.github.ambry.config.VerifiableProperties(properties), new com.codahale.metrics.MetricRegistry()).getUrlSigningService();
            Assert.fail("Expected exception");
        } catch (IllegalStateException ex) {
        }
        // Missing POST
        jsonObject = new JSONObject().put("GET", AmbryUrlSigningServiceTest.DOWNLOAD_ENDPOINT);
        properties.setProperty(URL_SIGNER_ENDPOINTS, jsonObject.toString());
        try {
            new AmbryUrlSigningServiceFactory(new com.github.ambry.config.VerifiableProperties(properties), new com.codahale.metrics.MetricRegistry()).getUrlSigningService();
            Assert.fail("Expected exception");
        } catch (IllegalStateException ex) {
        }
        // Gibberish
        properties.setProperty(URL_SIGNER_ENDPOINTS, "[Garbage string &%#123");
        try {
            new AmbryUrlSigningServiceFactory(new com.github.ambry.config.VerifiableProperties(properties), new com.codahale.metrics.MetricRegistry()).getUrlSigningService();
            Assert.fail("Expected exception");
        } catch (IllegalStateException ex) {
        }
    }

    /**
     * Tests that generate and verify signed URLs.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void signAndVerifyTest() throws Exception {
        Time time = new MockTime();
        AmbryUrlSigningService signer = getUrlSignerWithDefaults(time);
        doSignAndVerifyTest(signer, POST, time);
        doSignAndVerifyTest(signer, GET, time);
        signFailuresTest();
    }

    /**
     * Tests for some failure scenarios in verification.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void notSignedRequestFailuresTest() throws Exception {
        // positive test done in signAndVerifyTest()
        AmbryUrlSigningService signer = getUrlSignerWithDefaults(new MockTime());
        RestRequest request = getRequestFromUrl(GET, "/");
        Assert.assertFalse("Request should not be declared signed", signer.isRequestSigned(request));
        ensureVerificationFailure(signer, request, InternalServerError);
        request.setArg(URL_TYPE, POST.name());
        Assert.assertFalse("Request should not be declared signed", signer.isRequestSigned(request));
        ensureVerificationFailure(signer, request, InternalServerError);
    }
}

