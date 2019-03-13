/**
 * Copyright 2011-2019 Amazon Technologies, Inc.
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
package com.amazonaws.services.dynamodbv2.datamodeling;


import Region.AP_Sydney;
import Region.EU_Frankfurt;
import Region.US_GovCloud;
import Region.US_Standard;
import org.junit.Assert;
import org.junit.Test;


public class S3LinkTest {
    private DynamoDBMapper mapper;

    @Test(expected = IllegalArgumentException.class)
    public void nullKey() {
        mapper.createS3Link("bucket", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullBucketName() {
        mapper.createS3Link(null, "key");
    }

    @Test
    public void testToJson() {
        S3Link testLink = mapper.createS3Link("bucket", "key");
        String json = testLink.toJson();
        Assert.assertEquals(json, "{\"s3\":{\"bucket\":\"bucket\",\"key\":\"key\",\"region\":null}}", json);
        testLink = mapper.createS3Link("bucket", "testKey");
        json = testLink.toJson();
        Assert.assertEquals(json, "{\"s3\":{\"bucket\":\"bucket\",\"key\":\"testKey\",\"region\":null}}", json);
        testLink = mapper.createS3Link(AP_Sydney, "bucket", "testKey");
        json = testLink.toJson();
        Assert.assertEquals(json, "{\"s3\":{\"bucket\":\"bucket\",\"key\":\"testKey\",\"region\":\"ap-southeast-2\"}}", json);
        testLink = mapper.createS3Link(AP_Sydney, "test-bucket", "testKey");
        json = testLink.toJson();
        Assert.assertEquals(json, "{\"s3\":{\"bucket\":\"test-bucket\",\"key\":\"testKey\",\"region\":\"ap-southeast-2\"}}", json);
        testLink = mapper.createS3Link(AP_Sydney, "test-bucket", "test/key/with/slashes");
        json = testLink.toJson();
        Assert.assertEquals(json, "{\"s3\":{\"bucket\":\"test-bucket\",\"key\":\"test/key/with/slashes\",\"region\":\"ap-southeast-2\"}}", json);
        testLink = mapper.createS3Link("test-bucket", "test/key/with/slashes");
        json = testLink.toJson();
        Assert.assertEquals(json, "{\"s3\":{\"bucket\":\"test-bucket\",\"key\":\"test/key/with/slashes\",\"region\":null}}", json);
        testLink = mapper.createS3Link(AP_Sydney, "test-bucket", "test/key/with/slashes");
        json = testLink.toJson();
        Assert.assertEquals(json, "{\"s3\":{\"bucket\":\"test-bucket\",\"key\":\"test/key/with/slashes\",\"region\":\"ap-southeast-2\"}}", json);
    }

    @Test
    public void testFromJson() {
        String json = "{\"s3\":{\"region\":\"ap-southeast-2\",\"bucket\":\"test-bucket\",\"key\":\"testKey\"}}";
        S3Link s3link = S3Link.fromJson(mapper.getS3ClientCache(), json);
        Assert.assertEquals("test-bucket", s3link.getBucketName());
        Assert.assertEquals("ap-southeast-2", s3link.getRegion());
        Assert.assertEquals("testKey", s3link.getKey());
    }

    @Test
    public void testDefaultRegion() {
        S3Link testLink1 = mapper.createS3Link("bucket", "key");
        String json = testLink1.toJson();
        // Default to US_STANDARD if not specified
        Assert.assertEquals(json, "{\"s3\":{\"bucket\":\"bucket\",\"key\":\"key\",\"region\":null}}", json);
        // Default region changed to GovCloud
        testLink1 = mapper.createS3Link(US_GovCloud, "bucket", "key");
        json = testLink1.toJson();
        Assert.assertEquals(json, "{\"s3\":{\"bucket\":\"bucket\",\"key\":\"key\",\"region\":\"us-gov-west-1\"}}", json);
    }

    @Test
    public void testGetRegion_ReturnsUsEast1_WhenS3LinkCreated_WithNullRegion() {
        S3Link s3Link = mapper.createS3Link("bucket", "key");
        Assert.assertEquals("us-east-1", s3Link.getS3Region().toAWSRegion().getName());
        Assert.assertEquals("us-east-1", s3Link.getRegion());
    }

    @Test
    public void testGetRegion_ReturnsUsEast1_WhenS3LinkCreated_WithUsStandardRegion() {
        S3Link s3Link = mapper.createS3Link(US_Standard, "bucket", "key");
        Assert.assertEquals("us-east-1", s3Link.getS3Region().toAWSRegion().getName());
        Assert.assertEquals("us-east-1", s3Link.getRegion());
    }

    @Test
    public void testGetRegion_ReturnsUsEast1_WhenS3LinkCreated_WithUsEast1Region() {
        S3Link s3Link = mapper.createS3Link("us-east-1", "bucket", "key");
        Assert.assertEquals("us-east-1", s3Link.getS3Region().toAWSRegion().getName());
        Assert.assertEquals("us-east-1", s3Link.getRegion());
    }

    @Test
    public void testGetRegion_WithNonUsStandardRegion() {
        S3Link s3Link = mapper.createS3Link(EU_Frankfurt, "bucket", "key");
        Assert.assertEquals(EU_Frankfurt, s3Link.getS3Region());
        Assert.assertEquals(EU_Frankfurt.toAWSRegion().getName(), s3Link.getRegion());
    }
}

