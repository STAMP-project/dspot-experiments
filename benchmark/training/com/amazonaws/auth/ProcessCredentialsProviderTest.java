/**
 * Copyright 2014-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.auth;


import java.util.concurrent.TimeUnit;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;


public class ProcessCredentialsProviderTest {
    private static String scriptLocation;

    @Test
    public void staticCredentialsCanBeLoaded() {
        AWSCredentials credentials = ProcessCredentialsProvider.builder().withCommand(((ProcessCredentialsProviderTest.scriptLocation) + " STATIC_CREDENTIALS")).build().getCredentials();
        Assert.assertFalse((credentials instanceof AWSSessionCredentials));
        Assert.assertEquals("accessKeyId", credentials.getAWSAccessKeyId());
        Assert.assertEquals("secretAccessKey", credentials.getAWSSecretKey());
    }

    @Test
    public void sessionCredentialsCanBeLoaded() {
        ProcessCredentialsProvider credentialsProvider = ProcessCredentialsProvider.builder().withCommand(((ProcessCredentialsProviderTest.scriptLocation) + " SESSION_CREDENTIALS")).withCredentialExpirationBuffer(0, TimeUnit.SECONDS).build();
        AWSCredentials credentials = credentialsProvider.getCredentials();
        Assert.assertTrue((credentials instanceof AWSSessionCredentials));
        AWSSessionCredentials sessionCredentials = ((AWSSessionCredentials) (credentials));
        Assert.assertEquals("accessKeyId", sessionCredentials.getAWSAccessKeyId());
        Assert.assertEquals("secretAccessKey", sessionCredentials.getAWSSecretKey());
        Assert.assertEquals("sessionToken", sessionCredentials.getSessionToken());
        Assert.assertTrue(DateTime.parse("2018-12-11T17:46:28Z").isEqual(credentialsProvider.getCredentialExpirationTime()));
    }

    @Test
    public void expirationBufferOverrideIsApplied() {
        ProcessCredentialsProvider credentialsProvider = ProcessCredentialsProvider.builder().withCommand(((ProcessCredentialsProviderTest.scriptLocation) + " SESSION_CREDENTIALS")).withCredentialExpirationBuffer(10, TimeUnit.SECONDS).build();
        credentialsProvider.getCredentials();
        Assert.assertTrue(DateTime.parse("2018-12-11T17:46:28Z").minusSeconds(10).isEqual(credentialsProvider.getCredentialExpirationTime()));
    }

    @Test(expected = IllegalStateException.class)
    public void processOutputLimitIsEnforced() {
        ProcessCredentialsProvider.builder().withCommand(((ProcessCredentialsProviderTest.scriptLocation) + " STATIC_CREDENTIALS")).withProcessOutputLimit(1).build().getCredentials();
    }
}

