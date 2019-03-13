/**
 * Copyright 2016-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.amazonaws.regions;


import Regions.AP_NORTHEAST_1;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for partition based region implementation and endpoint guessing.
 */
public class PartitionsRegionsTest {
    private static final String KeyManagementService = "kms";

    private static final String IAM = "iam";

    @Test
    public void region_name_matches_regionregex_guesses_regionalized_endpoint() {
        final String regionName = "ap-newregion-1";
        Region partitionUsWest = RegionUtils.getRegion(regionName);
        Assert.assertFalse(partitionUsWest.isServiceSupported(PartitionsRegionsTest.KeyManagementService));
        Assert.assertFalse(partitionUsWest.hasHttpEndpoint(PartitionsRegionsTest.KeyManagementService));
        Assert.assertFalse(partitionUsWest.hasHttpsEndpoint(PartitionsRegionsTest.KeyManagementService));
        Assert.assertNotNull(partitionUsWest.getServiceEndpoint(PartitionsRegionsTest.KeyManagementService));
    }

    @Test
    public void region_name_matches_regionregex_guesses_partition_wide_endpoint() {
        final String regionName = "ap-newregion-1";
        Region partitionUsWest = RegionUtils.getRegion(regionName);
        Assert.assertTrue(partitionUsWest.isServiceSupported(PartitionsRegionsTest.IAM));
        Assert.assertFalse(partitionUsWest.hasHttpEndpoint(PartitionsRegionsTest.IAM));
        Assert.assertTrue(partitionUsWest.hasHttpsEndpoint(PartitionsRegionsTest.IAM));
        Assert.assertNotNull(partitionUsWest.getServiceEndpoint(PartitionsRegionsTest.IAM));
    }

    @Test
    public void endpoint_information_for_completely_new_service_returns_null() {
        final Region region = RegionUtils.getRegion(AP_NORTHEAST_1.getName());
        final String serviceName = "unknown-service";
        Assert.assertNull(region.getServiceEndpoint(serviceName));
        Assert.assertFalse(region.hasHttpEndpoint(serviceName));
        Assert.assertFalse(region.hasHttpsEndpoint(serviceName));
        Assert.assertFalse(region.isServiceSupported(serviceName));
    }

    @Test
    public void region_name_not_matches_regionregex_returns_defaultPartition() {
        final String regionName = "ap-new-region-1";
        Assert.assertEquals("aws", RegionUtils.getRegion(regionName).getPartition());
    }

    @Test
    public void s3_external_endpoint_resolution() {
        final String regionName = "s3-external-1";
        Assert.assertNotNull(RegionUtils.getRegion(regionName));
    }
}

