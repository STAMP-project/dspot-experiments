/**
 * Copyright (c) 2016. Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.internal.config;


import InternalConfig.DEFAULT_CONFIG_RESOURCE_ABSOLUTE_PATH;
import Regions.US_EAST_1;
import com.amazonaws.regions.Regions;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class InternalConfigTest {
    private static InternalConfig config = null;

    private static final String DEFAULT_SIGNER_TYPE = "AWS4SignerType";

    private static final String S3V4_SIGNER_TYPE = "AWSS3V4SignerType";

    private static final String NEW_REGION = "newregion";

    private static final Map<String, String> serviceSignerMap = new HashMap<String, String>();

    /**
     * This test case tests signers for all AWS regions mentioned in the
     * <code>Regions</code>
     */
    @Test
    public void testServiceSpecificSigners() {
        Set<Map.Entry<String, String>> entrySet = InternalConfigTest.serviceSignerMap.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            testAllRegions(entry.getKey(), entry.getValue());
        }
    }

    /**
     * This test case tests the Amazon S3 specific signers.
     */
    @Test
    public void testS3Signers() {
        final String serviceName = "s3";
        assertSignerType(InternalConfigTest.S3V4_SIGNER_TYPE, serviceName, null);
        for (Regions region : Regions.values()) {
            assertSignerType(InternalConfigTest.S3V4_SIGNER_TYPE, serviceName, region.getName());
        }
        assertSignerType(InternalConfigTest.S3V4_SIGNER_TYPE, serviceName, InternalConfigTest.NEW_REGION);
    }

    @Test
    public void testSDBSigners() {
        assertSignerType("QueryStringSignerType", "sdb", "us-east-1");
        assertSignerType("QueryStringSignerType", "sdb", "us-west-1");
        assertSignerType("QueryStringSignerType", "sdb", "us-west-2");
        assertSignerType("QueryStringSignerType", "sdb", "ap-northeast-1");
        assertSignerType("QueryStringSignerType", "sdb", "ap-southeast-1");
        assertSignerType("QueryStringSignerType", "sdb", "ap-southeast-2");
        assertSignerType("QueryStringSignerType", "sdb", "sa-east-1");
        assertSignerType("QueryStringSignerType", "sdb", "eu-west-1");
    }

    /**
     * This test case tests the Import/Export specific signers.
     */
    @Test
    public void testImportExportSigners() {
        assertSignerType("QueryStringSignerType", "importexport", null);
    }

    /**
     * This test case tests the Simple Email Service specific signers.
     */
    @Test
    public void testSimpleEmailServiceSigners() {
        assertSignerType(InternalConfigTest.DEFAULT_SIGNER_TYPE, "email", "us-east-1");
        assertSignerType(InternalConfigTest.DEFAULT_SIGNER_TYPE, "email", "us-west-1");
        assertSignerType(InternalConfigTest.DEFAULT_SIGNER_TYPE, "email", "us-west-2");
        assertSignerType(InternalConfigTest.DEFAULT_SIGNER_TYPE, "email", InternalConfigTest.NEW_REGION);
    }

    /**
     * This test cases tests the default signers for any new regions added to
     * any AWS service.
     */
    @Test
    public void testNewRegions() {
        assertSignerType(InternalConfigTest.DEFAULT_SIGNER_TYPE, "dynamodb", InternalConfigTest.NEW_REGION);
        assertSignerType(InternalConfigTest.S3V4_SIGNER_TYPE, "s3", InternalConfigTest.NEW_REGION);
        assertSignerType(InternalConfigTest.DEFAULT_SIGNER_TYPE, "email", InternalConfigTest.NEW_REGION);
        assertSignerType(InternalConfigTest.DEFAULT_SIGNER_TYPE, "ec2", InternalConfigTest.NEW_REGION);
        assertSignerType(InternalConfigTest.DEFAULT_SIGNER_TYPE, "sdb", InternalConfigTest.NEW_REGION);
    }

    /**
     * This test case tests the Cognito specific signers.
     */
    @Test
    public void testCognitoAssertions() {
        assertSignerType("AWS4SignerType", "cognito-identity", null);
        assertSignerType("AWS4SignerType", "cognito-identity", US_EAST_1.getName());
        assertSignerType("AWS4SignerType", "cognito-sync", null);
        assertSignerType("AWS4SignerType", "cognito-sync", US_EAST_1.getName());
    }

    @Test
    public void loadFromFile() throws Exception {
        loadFrom(DEFAULT_CONFIG_RESOURCE_ABSOLUTE_PATH);
    }

    @Test
    public void load() throws Exception {
        InternalConfig config = InternalConfig.load();
        Assert.assertNotNull(config);
        config.dump();
    }
}

