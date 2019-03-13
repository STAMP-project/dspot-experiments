/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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


import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.SdkClientException;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;


public class SystemPropertiesCredentialsProviderTest {
    private final SystemPropertiesCredentialsProvider provider = new SystemPropertiesCredentialsProvider();

    @Test
    public void accessKeyAndSecretPropertiesSet_ReturnsBasicCredentials() {
        System.setProperty(SDKGlobalConfiguration.ACCESS_KEY_SYSTEM_PROPERTY, "akid-value");
        System.setProperty(SDKGlobalConfiguration.SECRET_KEY_SYSTEM_PROPERTY, "skid-value");
        final AWSCredentials credentials = provider.getCredentials();
        Assert.assertThat(credentials, Matchers.not(IsInstanceOf.instanceOf(AWSSessionCredentials.class)));
        Assert.assertEquals("akid-value", credentials.getAWSAccessKeyId());
        Assert.assertEquals("skid-value", credentials.getAWSSecretKey());
    }

    @Test
    public void sessionTokenSet_ReturnsSessionCredentials() {
        System.setProperty(SDKGlobalConfiguration.ACCESS_KEY_SYSTEM_PROPERTY, "akid-value");
        System.setProperty(SDKGlobalConfiguration.SECRET_KEY_SYSTEM_PROPERTY, "skid-value");
        System.setProperty(SDKGlobalConfiguration.SESSION_TOKEN_SYSTEM_PROPERTY, "session-value");
        final AWSCredentials credentials = provider.getCredentials();
        Assert.assertThat(credentials, IsInstanceOf.instanceOf(AWSSessionCredentials.class));
        Assert.assertEquals("akid-value", credentials.getAWSAccessKeyId());
        Assert.assertEquals("skid-value", credentials.getAWSSecretKey());
        Assert.assertEquals("session-value", getSessionToken());
    }

    @Test(expected = SdkClientException.class)
    public void noPropertiesSet_ProviderThrowsException() {
        provider.getCredentials();
    }
}

