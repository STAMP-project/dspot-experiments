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
package com.amazonaws.services.dynamodbv2.datamodeling;


import Region.US_Standard;
import S3Link.ID;
import com.amazonaws.util.json.Jackson;
import org.junit.Assert;
import org.junit.Test;


public class S3LinkIDTest {
    @Test
    public void testToFromJson() {
        String region = "ap-northeast-1";
        S3Link.ID id = new S3Link.ID(region, "bucket", "key");
        String json = id.toJson();
        S3Link.ID twin = Jackson.fromJsonString(json, ID.class);
        Assert.assertEquals("bucket", twin.getBucket());
        Assert.assertEquals("key", twin.getKey());
        Assert.assertEquals(region, twin.getRegionId());
    }

    @Test
    public void testDefaultRegion() {
        S3Link.ID id = new S3Link.ID("bucketname", "keyname");
        Assert.assertEquals(US_Standard.getFirstRegionId(), id.getRegionId());
        String json = id.toJson();
        S3Link.ID twin = Jackson.fromJsonString(json, ID.class);
        Assert.assertEquals("bucketname", twin.getBucket());
        Assert.assertEquals("keyname", twin.getKey());
        Assert.assertEquals(US_Standard.getFirstRegionId(), twin.getRegionId());
    }
}

