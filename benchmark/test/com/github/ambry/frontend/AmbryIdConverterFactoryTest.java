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


import RestMethod.GET;
import RestMethod.POST;
import RestServiceErrorCode.InternalServerError;
import RestServiceErrorCode.ServiceUnavailable;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.rest.MockRestRequest;
import com.github.ambry.rest.RestServiceErrorCode;
import com.github.ambry.router.Callback;
import com.github.ambry.utils.UtilsTest;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link AmbryIdConverterFactory}.
 */
public class AmbryIdConverterFactoryTest {
    /**
     * Tests the instantiation and use of the {@link IdConverter} instance returned through the
     * {@link AmbryIdConverterFactory}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void ambryIdConverterTest() throws Exception {
        // dud properties. server should pick up defaults
        Properties properties = new Properties();
        VerifiableProperties verifiableProperties = new VerifiableProperties(properties);
        IdSigningService idSigningService = Mockito.mock(IdSigningService.class);
        AmbryIdConverterFactory ambryIdConverterFactory = new AmbryIdConverterFactory(verifiableProperties, new MetricRegistry(), idSigningService);
        IdConverter idConverter = ambryIdConverterFactory.getIdConverter();
        Assert.assertNotNull("No IdConverter returned", idConverter);
        String input = UtilsTest.getRandomString(10);
        String inputWithLeadingSlash = "/" + input;
        // GET
        // without leading slash
        Mockito.reset(idSigningService);
        testConversion(idConverter, GET, null, input, input);
        Mockito.verify(idSigningService, Mockito.never()).parseSignedId(ArgumentMatchers.any());
        // with leading slash
        Mockito.reset(idSigningService);
        testConversion(idConverter, GET, null, input, inputWithLeadingSlash);
        Mockito.verify(idSigningService, Mockito.never()).parseSignedId(ArgumentMatchers.any());
        // with signed ID input.
        String idFromParsedSignedId = "parsedId" + input;
        Mockito.reset(idSigningService);
        Mockito.when(idSigningService.isIdSigned(ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(idSigningService.parseSignedId(ArgumentMatchers.any())).thenReturn(new com.github.ambry.utils.Pair(idFromParsedSignedId, Collections.emptyMap()));
        testConversion(idConverter, GET, null, idFromParsedSignedId, inputWithLeadingSlash);
        Mockito.verify(idSigningService).parseSignedId(input);
        // test signed id parsing exception
        Mockito.reset(idSigningService);
        Mockito.when(idSigningService.isIdSigned(ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(idSigningService.parseSignedId(ArgumentMatchers.any())).thenThrow(new com.github.ambry.rest.RestServiceException("expected", RestServiceErrorCode.InternalServerError));
        testConversionFailure(idConverter, new MockRestRequest(MockRestRequest.DUMMY_DATA, null), input, InternalServerError);
        Mockito.verify(idSigningService).parseSignedId(input);
        // POST
        // without leading slash (there will be no leading slashes returned from the Router)
        Mockito.reset(idSigningService);
        testConversion(idConverter, POST, null, inputWithLeadingSlash, input);
        Mockito.verify(idSigningService, Mockito.never()).getSignedId(ArgumentMatchers.any(), ArgumentMatchers.any());
        // with signed id metadata set, requires signed ID.
        String signedId = "signedId/" + input;
        Mockito.reset(idSigningService);
        Mockito.when(idSigningService.getSignedId(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(signedId);
        Map<String, String> signedIdMetadata = Collections.singletonMap("test-key", "test-value");
        testConversion(idConverter, POST, signedIdMetadata, ("/" + signedId), input);
        Mockito.verify(idSigningService).getSignedId(input, signedIdMetadata);
        idConverter.close();
        testConversionFailure(idConverter, new MockRestRequest(MockRestRequest.DUMMY_DATA, null), input, ServiceUnavailable);
    }

    /**
     * Callback implementation for testing {@link IdConverter#convert(RestRequest, String, Callback)}.
     */
    private static class IdConversionCallback implements Callback<String> {
        protected String result = null;

        protected Exception exception = null;

        @Override
        public void onCompletion(String result, Exception exception) {
            this.result = result;
            this.exception = exception;
        }

        /**
         * Resets the state of this callback.
         */
        protected void reset() {
            result = null;
            exception = null;
        }
    }
}

