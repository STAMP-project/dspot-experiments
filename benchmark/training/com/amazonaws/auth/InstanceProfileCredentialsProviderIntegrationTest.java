/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights
 * Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is
 * distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either
 * express or implied. See the License for the specific language
 * governing
 * permissions and limitations under the License.
 */
package com.amazonaws.auth;


import com.amazonaws.AmazonClientException;
import com.amazonaws.util.LogCaptor;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for the InstanceProfileCredentialsProvider.
 */
public class InstanceProfileCredentialsProviderIntegrationTest extends LogCaptor.LogCaptorTestBase {
    private EC2MetadataServiceMock mockServer;

    @Test
    public void getInstance_ReturnsSameInstance() {
        Assert.assertEquals(InstanceProfileCredentialsProvider.getInstance(), InstanceProfileCredentialsProvider.getInstance());
    }

    /**
     * Tests that we correctly handle the metadata service returning credentials.
     */
    @Test
    public void testSessionCredentials() throws Exception {
        mockServer.setResponseFileName("sessionResponse");
        mockServer.setAvailableSecurityCredentials("aws-dr-tools-test");
        InstanceProfileCredentialsProvider credentialsProvider = new InstanceProfileCredentialsProvider();
        AWSSessionCredentials credentials = ((AWSSessionCredentials) (credentialsProvider.getCredentials()));
        Assert.assertEquals("ACCESS_KEY_ID", credentials.getAWSAccessKeyId());
        Assert.assertEquals("SECRET_ACCESS_KEY", credentials.getAWSSecretKey());
        Assert.assertEquals("TOKEN_TOKEN_TOKEN", credentials.getSessionToken());
    }

    /**
     * Tests that we correctly handle the metadata service returning credentials
     * when multiple instance profiles are available.
     */
    @Test
    public void testSessionCredentials_MultipleInstanceProfiles() throws Exception {
        mockServer.setResponseFileName("sessionResponse");
        mockServer.setAvailableSecurityCredentials("test-credentials");
        InstanceProfileCredentialsProvider credentialsProvider = new InstanceProfileCredentialsProvider();
        AWSSessionCredentials credentials = ((AWSSessionCredentials) (credentialsProvider.getCredentials()));
        Assert.assertEquals("ACCESS_KEY_ID", credentials.getAWSAccessKeyId());
        Assert.assertEquals("SECRET_ACCESS_KEY", credentials.getAWSSecretKey());
        Assert.assertEquals("TOKEN_TOKEN_TOKEN", credentials.getSessionToken());
    }

    /**
     * Tests that we correctly handle when no instance profiles are available
     * through the metadata service.
     */
    @Test
    public void testNoInstanceProfiles() throws Exception {
        mockServer.setResponseFileName("sessionResponse");
        mockServer.setAvailableSecurityCredentials("");
        InstanceProfileCredentialsProvider credentialsProvider = new InstanceProfileCredentialsProvider();
        try {
            credentialsProvider.getCredentials();
            Assert.fail("Expected an AmazonClientException, but wasn't thrown");
        } catch (AmazonClientException ace) {
            Assert.assertNotNull(ace.getMessage());
        }
    }

    @Test
    public void getCredentialsDisabled_shouldGetCredentialsAfterEnabled() throws Exception {
        InstanceProfileCredentialsProvider credentialsProvider = null;
        try {
            System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
            credentialsProvider = new InstanceProfileCredentialsProvider();
            credentialsProvider.getCredentials();
            Assert.fail("exception not thrown when ec2Metadata disabled");
        } catch (AmazonClientException ex) {
            // expected
        } finally {
            System.clearProperty("com.amazonaws.sdk.disableEc2Metadata");
        }
        mockServer.setResponseFileName("sessionResponse");
        mockServer.setAvailableSecurityCredentials("test-credentials");
        Assert.assertNotNull(credentialsProvider.getCredentials());
    }

    /**
     * Tests that we correctly handle when the metadata service credentials have
     * expired.
     */
    @Test
    public void testSessionCredentials_Expired() throws Exception {
        mockServer.setResponseFileName("sessionResponseExpired");
        mockServer.setAvailableSecurityCredentials("test-credentials");
        InstanceProfileCredentialsProvider credentialsProvider = new InstanceProfileCredentialsProvider();
        try {
            credentialsProvider.getCredentials();
            Assert.fail("Expected an AmazonClientException, but wasn't thrown");
        } catch (AmazonClientException ace) {
            Assert.assertNotNull(ace.getMessage());
        }
    }

    /**
     * Tests by initiating a refresh thread in parallel which refreshes the
     * credentials. Next call to credentials provider will result in refreshing
     * and getting new credentials.
     */
    @Test
    public void testMultipleThreadsLoadingAndRefreshingCredentials() throws Exception {
        mockServer.setResponseFileName("sessionResponse");
        mockServer.setAvailableSecurityCredentials("test-credentials");
        InstanceProfileCredentialsProvider credentialsProvider = new InstanceProfileCredentialsProvider();
        AWSSessionCredentials credentials = ((AWSSessionCredentials) (credentialsProvider.getCredentials()));
        Assert.assertNotNull(credentials);
        new InstanceProfileCredentialsProviderIntegrationTest.RefreshThread(credentialsProvider).join();
        AWSSessionCredentials newCredentials = ((AWSSessionCredentials) (credentialsProvider.getCredentials()));
        Assert.assertNotNull(newCredentials);
        Assert.assertNotSame(credentials, newCredentials);
    }

    @Test(expected = AmazonClientException.class)
    public void canBeConfiguredToOnlyRefreshCredentialsAfterFirstCallToGetCredentials() throws InterruptedException {
        mockServer.setResponseFileName("sessionResponseExpired");
        mockServer.setAvailableSecurityCredentials("test-credentials");
        InstanceProfileCredentialsProvider credentialsProvider = InstanceProfileCredentialsProvider.createAsyncRefreshingProvider(false);
        Thread.sleep(1000);
        // Hacky assert but we know that this mockServer will create an exception that will be logged, if there's no log entry
        // then there's no exception, which means that getCredentials didn't get called on the fetcher
        MatcherAssert.assertThat(loggedEvents(), CoreMatchers.is(empty()));
        credentialsProvider.getCredentials();
    }

    private class RefreshThread extends Thread {
        private InstanceProfileCredentialsProvider provider;

        public RefreshThread(InstanceProfileCredentialsProvider provider) {
            this.provider = provider;
            this.start();
        }

        @Override
        public void run() {
            this.provider.refresh();
        }
    }
}

