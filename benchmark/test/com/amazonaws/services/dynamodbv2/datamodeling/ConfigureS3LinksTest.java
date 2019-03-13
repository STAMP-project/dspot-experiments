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


import Region.AP_Singapore;
import org.junit.Assert;
import org.junit.Test;


public class ConfigureS3LinksTest {
    private S3ClientCache s3cc;

    @DynamoDBTable(tableName = "nonexisting-test-tablename")
    public static class CorrectTestClass {
        private String hk;

        private S3Link s3;

        public CorrectTestClass() {
        }

        @DynamoDBHashKey
        public String getHk() {
            return hk;
        }

        public void setHk(String hk) {
            this.hk = hk;
        }

        public S3Link getS3() {
            return s3;
        }

        public void setS3(S3Link s3) {
            this.s3 = s3;
        }
    }

    @Test
    public void testS3LinkWithStringRegion() {
        ConfigureS3LinksTest.CorrectTestClass obj = new ConfigureS3LinksTest.CorrectTestClass();
        S3Link s3 = new S3Link(s3cc, "ap-southeast-1", "nonexisting-test-bucketname2", "key");
        obj.setS3(s3);
        Assert.assertNotNull(obj.getS3());
        Assert.assertEquals("nonexisting-test-bucketname2", obj.getS3().getBucketName());
        Assert.assertSame(AP_Singapore, obj.getS3().getS3Region());
        Assert.assertSame("ap-southeast-1", obj.getS3().getRegion());
    }

    @DynamoDBTable(tableName = "nonexisting-test-tablename")
    public static class ManyS3LinksTestClass {
        private String hk;

        private S3Link s3_1;

        private S3Link s3_2;

        private S3Link s3_3;

        private S3Link s3_4;

        private S3Link s3_5;

        private S3Link s3_6;

        public ManyS3LinksTestClass() {
        }

        @DynamoDBHashKey
        public String getHk() {
            return hk;
        }

        public void setHk(String hk) {
            this.hk = hk;
        }

        public S3Link getS3_1() {
            return s3_1;
        }

        public void setS3_1(S3Link s3_1) {
            this.s3_1 = s3_1;
        }

        public S3Link getS3_2() {
            return s3_2;
        }

        public void setS3_2(S3Link s3_2) {
            this.s3_2 = s3_2;
        }

        public S3Link getS3_3() {
            return s3_3;
        }

        public void setS3_3(S3Link s3_3) {
            this.s3_3 = s3_3;
        }

        public S3Link getS3_4() {
            return s3_4;
        }

        public void setS3_4(S3Link s3_4) {
            this.s3_4 = s3_4;
        }

        public S3Link getS3_5() {
            return s3_5;
        }

        public void setS3_5(S3Link s3_5) {
            this.s3_5 = s3_5;
        }

        public S3Link getS3_6() {
            return s3_6;
        }

        public void setS3_6(S3Link s3_6) {
            this.s3_6 = s3_6;
        }
    }

    @Test
    public void testManyS3LinksClass() {
        ConfigureS3LinksTest.ManyS3LinksTestClass obj = new ConfigureS3LinksTest.ManyS3LinksTestClass();
        Assert.assertNull(obj.getS3_1());
    }
}

