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


import java.io.File;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for the PropertiesFileCredentialsProvider.
 */
public class PropertiesFileCredentialsProviderIntegrationTest {
    private static String fileName = "propertiesCredentials";

    private static String data = "accessKey=testKey\nsecretKey=secretKey";

    private static File file = null;

    /**
     * Tests that the credentials provider loads credentials appropriately
     */
    @Test
    public void testPropertiesCredentialsMethod() throws Exception {
        PropertiesFileCredentialsProvider provider = new PropertiesFileCredentialsProvider(PropertiesFileCredentialsProviderIntegrationTest.file.getAbsolutePath());
        Assert.assertNotNull(provider.getCredentials());
        Assert.assertEquals(provider.getCredentials().getAWSAccessKeyId(), "testKey");
        Assert.assertEquals(provider.getCredentials().getAWSSecretKey(), "secretKey");
    }
}

