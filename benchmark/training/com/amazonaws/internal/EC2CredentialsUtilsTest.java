/**
 * Copyright 2011-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package com.amazonaws.internal;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.retry.internal.CredentialsEndpointRetryParameters;
import com.amazonaws.retry.internal.CredentialsEndpointRetryPolicy;
import com.amazonaws.util.VersionInfoUtils;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import utils.http.SocketUtils;


@RunWith(MockitoJUnitRunner.class)
public class EC2CredentialsUtilsTest {
    private static final String USER_AGENT = String.format("aws-sdk-java/%s", VersionInfoUtils.getVersion());

    @ClassRule
    public static WireMockRule mockServer = new WireMockRule(0);

    private static final String CREDENTIALS_PATH = "/dummy/credentials/path";

    private static final String SUCCESS_BODY = "{\"AccessKeyId\":\"ACCESS_KEY_ID\",\"SecretAccessKey\":\"SECRET_ACCESS_KEY\"," + "\"Token\":\"TOKEN_TOKEN_TOKEN\",\"Expiration\":\"3000-05-03T04:55:54Z\"}";

    private static URI endpoint;

    private static EC2CredentialsUtilsTest.CustomRetryPolicy customRetryPolicy;

    private static EC2CredentialsUtils ec2CredentialsUtils;

    @Mock
    private ConnectionUtils mockConnection;

    /**
     * When a connection to end host cannot be opened, throws {@link IOException}.
     */
    @Test(expected = IOException.class)
    public void readResourceThrowsIOExceptionWhenNoConnection() throws IOException, URISyntaxException {
        int port = 0;
        try {
            port = SocketUtils.getUnusedPort();
        } catch (IOException ioexception) {
            Assert.fail("Unable to find an unused port");
        }
        EC2CredentialsUtilsTest.ec2CredentialsUtils.readResource(new URI(("http://localhost:" + port)));
    }

    /**
     * When server returns with status code 200,
     * the test successfully returns the body from the response.
     */
    @Test
    public void readResouceReturnsResponseBodyFor200Response() throws IOException {
        generateStub(200, EC2CredentialsUtilsTest.SUCCESS_BODY);
        Assert.assertEquals(EC2CredentialsUtilsTest.SUCCESS_BODY, EC2CredentialsUtilsTest.ec2CredentialsUtils.readResource(EC2CredentialsUtilsTest.endpoint));
    }

    /**
     * When server returns with 404 status code,
     * the test should throw AmazonClientException.
     */
    @Test
    public void readResouceReturnsAceFor404ErrorResponse() throws Exception {
        try {
            EC2CredentialsUtilsTest.ec2CredentialsUtils.readResource(new URI((("http://localhost:" + (EC2CredentialsUtilsTest.mockServer.port())) + "/dummyPath")));
            Assert.fail("Expected AmazonClientException");
        } catch (AmazonClientException ace) {
            Assert.assertTrue(ace.getMessage().contains("The requested metadata is not found at"));
        }
    }

    /**
     * When server returns a status code other than 200 and 404,
     * the test should throw AmazonServiceException. The request
     * is not retried.
     */
    @Test
    public void readResouceReturnsAseFor5xxResponse() throws IOException {
        generateStub(500, "{\"code\":\"500 Internal Server Error\",\"message\":\"ERROR_MESSAGE\"}");
        try {
            EC2CredentialsUtilsTest.ec2CredentialsUtils.readResource(EC2CredentialsUtilsTest.endpoint);
            Assert.fail("Expected AmazonServiceException");
        } catch (AmazonServiceException ase) {
            Assert.assertEquals(500, ase.getStatusCode());
            Assert.assertEquals("500 Internal Server Error", ase.getErrorCode());
            Assert.assertEquals("ERROR_MESSAGE", ase.getErrorMessage());
        }
    }

    /**
     * When server returns a status code other than 200 and 404
     * and error body message is not in Json format,
     * the test throws AmazonServiceException.
     */
    @Test
    public void readResouceNonJsonErrorBody() throws IOException {
        generateStub(500, "Non Json error body");
        try {
            EC2CredentialsUtilsTest.ec2CredentialsUtils.readResource(EC2CredentialsUtilsTest.endpoint);
            Assert.fail("Expected AmazonServiceException");
        } catch (AmazonServiceException ase) {
            Assert.assertEquals(500, ase.getStatusCode());
            Assert.assertNotNull(ase.getErrorMessage());
        }
    }

    /**
     * When readResource is called with default retry policy and IOException occurs,
     * the request is not retried.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void readResouceWithDefaultRetryPolicy_DoesNotRetry_ForIoException() throws IOException {
        Mockito.when(mockConnection.connectToEndpoint(ArgumentMatchers.eq(EC2CredentialsUtilsTest.endpoint), ArgumentMatchers.any(Map.class))).thenThrow(new IOException());
        try {
            new EC2CredentialsUtils(mockConnection).readResource(EC2CredentialsUtilsTest.endpoint);
            Assert.fail("Expected an IOexception");
        } catch (IOException exception) {
            Mockito.verify(mockConnection, Mockito.times(1)).connectToEndpoint(ArgumentMatchers.eq(EC2CredentialsUtilsTest.endpoint), ArgumentMatchers.any(Map.class));
        }
    }

    /**
     * When readResource is called with custom retry policy and IOException occurs,
     * the request is retried and the number of retries is equal to the value
     * returned by getMaxRetries method of the custom retry policy.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void readResouceWithCustomRetryPolicy_DoesRetry_ForIoException() throws IOException {
        Mockito.when(mockConnection.connectToEndpoint(ArgumentMatchers.eq(EC2CredentialsUtilsTest.endpoint), ArgumentMatchers.any(Map.class))).thenThrow(new IOException());
        try {
            new EC2CredentialsUtils(mockConnection).readResource(EC2CredentialsUtilsTest.endpoint, EC2CredentialsUtilsTest.customRetryPolicy, null);
            Assert.fail("Expected an IOexception");
        } catch (IOException exception) {
            Mockito.verify(mockConnection, Mockito.times(((EC2CredentialsUtilsTest.CustomRetryPolicy.MAX_RETRIES) + 1))).connectToEndpoint(ArgumentMatchers.eq(EC2CredentialsUtilsTest.endpoint), ArgumentMatchers.any(Map.class));
        }
    }

    /**
     * When readResource is called with custom retry policy
     * and the exception is not an IOException,
     * then the request is not retried.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void readResouceWithCustomRetryPolicy_DoesNotRetry_ForNonIoException() throws IOException {
        generateStub(500, "Non Json error body");
        Mockito.when(mockConnection.connectToEndpoint(ArgumentMatchers.eq(EC2CredentialsUtilsTest.endpoint), ArgumentMatchers.any(Map.class))).thenCallRealMethod();
        try {
            new EC2CredentialsUtils(mockConnection).readResource(EC2CredentialsUtilsTest.endpoint, EC2CredentialsUtilsTest.customRetryPolicy, new HashMap<String, String>());
            Assert.fail("Expected an AmazonServiceException");
        } catch (AmazonServiceException ase) {
            Mockito.verify(mockConnection, Mockito.times(1)).connectToEndpoint(ArgumentMatchers.eq(EC2CredentialsUtilsTest.endpoint), ArgumentMatchers.any(Map.class));
        }
    }

    /**
     * When readResource is called,
     * the SDK User-Agent should be added.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void readResouce_AddsSDKUserAgent() throws IOException {
        Mockito.when(mockConnection.connectToEndpoint(ArgumentMatchers.eq(EC2CredentialsUtilsTest.endpoint), ArgumentMatchers.any(Map.class))).thenThrow(new IOException());
        try {
            new EC2CredentialsUtils(mockConnection).readResource(EC2CredentialsUtilsTest.endpoint);
            Assert.fail("Expected an IOexception");
        } catch (IOException exception) {
            Matcher<Map<? extends String, ? extends String>> expectedHeaders = Matchers.hasEntry("User-Agent", EC2CredentialsUtilsTest.USER_AGENT);
            Mockito.verify(mockConnection, Mockito.times(1)).connectToEndpoint(ArgumentMatchers.eq(EC2CredentialsUtilsTest.endpoint), ((Map<String, String>) (ArgumentMatchers.argThat(expectedHeaders))));
        }
    }

    /**
     * When readResource is called with custom retry policy,
     * the SDK User-Agent should be added.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void readResouceWithCustomRetryPolicy_AddsSDKUserAgent() throws IOException {
        Mockito.when(mockConnection.connectToEndpoint(ArgumentMatchers.eq(EC2CredentialsUtilsTest.endpoint), ArgumentMatchers.any(Map.class))).thenThrow(new IOException());
        try {
            new EC2CredentialsUtils(mockConnection).readResource(EC2CredentialsUtilsTest.endpoint, EC2CredentialsUtilsTest.customRetryPolicy, null);
            Assert.fail("Expected an IOexception");
        } catch (IOException exception) {
            Matcher<Map<? extends String, ? extends String>> expectedHeaders = Matchers.hasEntry("User-Agent", EC2CredentialsUtilsTest.USER_AGENT);
            Mockito.verify(mockConnection, Mockito.times(((EC2CredentialsUtilsTest.CustomRetryPolicy.MAX_RETRIES) + 1))).connectToEndpoint(ArgumentMatchers.eq(EC2CredentialsUtilsTest.endpoint), ((Map<String, String>) (ArgumentMatchers.argThat(expectedHeaders))));
        }
    }

    /**
     * When readResource is called with custom retry policy
     * and additional headers, the SDK User-Agent should be
     * added.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void readResouceWithCustomRetryPolicyAndHeaders_AddsSDKUserAgent() throws IOException {
        Mockito.when(mockConnection.connectToEndpoint(ArgumentMatchers.eq(EC2CredentialsUtilsTest.endpoint), ArgumentMatchers.any(Map.class))).thenThrow(new IOException());
        try {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Foo", "Bar");
            readResource(EC2CredentialsUtilsTest.endpoint, EC2CredentialsUtilsTest.customRetryPolicy, headers);
            Assert.fail("Expected an IOexception");
        } catch (IOException exception) {
            Matcher<Map<? extends String, ? extends String>> expectedHeaders = CoreMatchers.allOf(Matchers.hasEntry("User-Agent", EC2CredentialsUtilsTest.USER_AGENT), Matchers.hasEntry("Foo", "Bar"));
            Mockito.verify(mockConnection, Mockito.times(((EC2CredentialsUtilsTest.CustomRetryPolicy.MAX_RETRIES) + 1))).connectToEndpoint(ArgumentMatchers.eq(EC2CredentialsUtilsTest.endpoint), ((Map<String, String>) (ArgumentMatchers.argThat(expectedHeaders))));
        }
    }

    /**
     * Retry policy that retries only if a request fails with an IOException.
     */
    private static class CustomRetryPolicy implements CredentialsEndpointRetryPolicy {
        private static final int MAX_RETRIES = 3;

        @Override
        public boolean shouldRetry(int retriesAttempted, CredentialsEndpointRetryParameters retryParams) {
            if (retriesAttempted >= (EC2CredentialsUtilsTest.CustomRetryPolicy.MAX_RETRIES)) {
                return false;
            }
            if (((retryParams.getException()) != null) && ((retryParams.getException()) instanceof IOException)) {
                return true;
            }
            return false;
        }
    }
}

