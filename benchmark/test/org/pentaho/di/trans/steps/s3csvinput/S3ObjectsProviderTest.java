/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.s3csvinput;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class S3ObjectsProviderTest {
    private static final String BUCKET1_NAME = "Bucket1";

    private static final String BUCKET2_NAME = "Bucket2";

    private static final String BUCKET3_NAME = "Bucket3";

    private static final String UNKNOWN_BUCKET = "UnknownBucket";

    private static final Bucket BUCKET2 = new Bucket(S3ObjectsProviderTest.BUCKET2_NAME);

    private static final Bucket BUCKET3 = new Bucket(S3ObjectsProviderTest.BUCKET3_NAME);

    private static final Bucket BUCKET1 = new Bucket(S3ObjectsProviderTest.BUCKET1_NAME);

    private static final String[] TEST_USER_BUCKETS_NAMES = new String[]{ S3ObjectsProviderTest.BUCKET1_NAME, S3ObjectsProviderTest.BUCKET2_NAME, S3ObjectsProviderTest.BUCKET3_NAME };

    private static final String[] EXPECTED_BUCKETS_NAMES = S3ObjectsProviderTest.TEST_USER_BUCKETS_NAMES;

    private static ObjectListing bucket2Objects;

    private static ObjectListing bucket3Objects;

    private static S3Object testObject;

    private static AWSCredentials testUserCredentials;

    private S3ObjectsProvider provider;

    private AmazonS3 s3ClientMock;

    @Test
    public void testGetBucketsNames() throws Exception {
        String[] actual = provider.getBucketsNames();
        Assert.assertArrayEquals(S3ObjectsProviderTest.EXPECTED_BUCKETS_NAMES, actual);
    }

    @Test
    public void testGetBucketFound() throws Exception {
        Bucket actual = provider.getBucket(S3ObjectsProviderTest.BUCKET2_NAME);
        Assert.assertEquals(S3ObjectsProviderTest.BUCKET2_NAME, actual.getName());
    }

    @Test
    public void testGetBucketNotFound_ReturnsNull() throws Exception {
        Bucket actual = provider.getBucket(S3ObjectsProviderTest.UNKNOWN_BUCKET);
        Assert.assertNull(actual);
    }

    @Test
    public void testGetObjectsNamesInBucketWithObjects() throws Exception {
        List<String> actual = Arrays.asList(provider.getS3ObjectsNames(S3ObjectsProviderTest.BUCKET2_NAME));
        Assert.assertEquals(S3ObjectsProviderTest.bucket2Objects.getObjectSummaries().size(), actual.size());
        List<S3ObjectSummary> expectedList = S3ObjectsProviderTest.bucket2Objects.getObjectSummaries();
        expectedList.stream().forEach(( i) -> assertTrue(actual.contains(i.getKey())));
    }

    @Test
    public void testGetObjectsNamesInEmptyBucket() throws Exception {
        String[] actual = provider.getS3ObjectsNames(S3ObjectsProviderTest.BUCKET3_NAME);
        S3ObjectsProviderTest.logArray(actual);
        Assert.assertEquals(0, actual.length);
    }

    @Test
    public void testGetObjectsNamesNoSuchBucket_ThrowsExeption() {
        try {
            provider.getS3ObjectsNames(S3ObjectsProviderTest.UNKNOWN_BUCKET);
            Assert.fail((("The Exception: Unable to find bucket '" + (S3ObjectsProviderTest.UNKNOWN_BUCKET)) + "' should be thrown but it was not."));
        } catch (Exception e) {
            Assert.assertTrue(e.getLocalizedMessage().contains((("Unable to find bucket '" + (S3ObjectsProviderTest.UNKNOWN_BUCKET)) + "'")));
        }
    }

    @Test
    public void testGetS3ObjectInBucket() throws Exception {
        S3Object actual = provider.getS3Object(S3ObjectsProviderTest.BUCKET1, "tests3Object");
        Assert.assertNotNull(actual);
        Assert.assertEquals(S3ObjectsProviderTest.testObject, actual);
    }

    @Test
    public void testGetS3ObjectLenght() throws Exception {
        long actual = provider.getS3ObjectContentLenght(S3ObjectsProviderTest.BUCKET1, "test3Object");
        Assert.assertEquals(S3ObjectsProviderTest.testObject.getObjectMetadata().getContentLength(), actual);
    }
}

