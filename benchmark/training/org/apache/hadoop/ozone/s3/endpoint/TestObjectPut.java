/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hadoop.ozone.s3.endpoint;


import ReplicationType.RATIS;
import S3ErrorTable.INVALID_ARGUMENT;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.ozone.client.ObjectStore;
import org.apache.hadoop.ozone.client.OzoneClientStub;
import org.apache.hadoop.ozone.client.OzoneKeyDetails;
import org.apache.hadoop.ozone.client.io.OzoneInputStream;
import org.apache.hadoop.ozone.s3.exception.OS3Exception;
import org.apache.hadoop.ozone.s3.util.S3Consts;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test put object.
 */
public class TestObjectPut {
    public static final String CONTENT = "0123456789";

    private String userName = "ozone";

    private String bucketName = "b1";

    private String keyName = "key1";

    private String destBucket = "b2";

    private String destkey = "key2";

    private String nonexist = "nonexist";

    private OzoneClientStub clientStub;

    private ObjectStore objectStoreStub;

    private ObjectEndpoint objectEndpoint;

    @Test
    public void testPutObject() throws IOException, OS3Exception {
        // GIVEN
        HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        ByteArrayInputStream body = new ByteArrayInputStream(TestObjectPut.CONTENT.getBytes());
        objectEndpoint.setHeaders(headers);
        // WHEN
        Response response = objectEndpoint.put(bucketName, keyName, TestObjectPut.CONTENT.length(), 1, null, body);
        // THEN
        String volumeName = getObjectStore().getOzoneVolumeName(bucketName);
        OzoneInputStream ozoneInputStream = getObjectStore().getVolume(volumeName).getBucket(bucketName).readKey(keyName);
        String keyContent = IOUtils.toString(ozoneInputStream, Charset.forName("UTF-8"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(TestObjectPut.CONTENT, keyContent);
    }

    @Test
    public void testPutObjectWithSignedChunks() throws IOException, OS3Exception {
        // GIVEN
        HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        objectEndpoint.setHeaders(headers);
        String chunkedContent = "0a;chunk-signature=signature\r\n" + (("1234567890\r\n" + "05;chunk-signature=signature\r\n") + "abcde\r\n");
        Mockito.when(headers.getHeaderString("x-amz-content-sha256")).thenReturn("STREAMING-AWS4-HMAC-SHA256-PAYLOAD");
        // WHEN
        Response response = objectEndpoint.put(bucketName, keyName, chunkedContent.length(), 1, null, new ByteArrayInputStream(chunkedContent.getBytes()));
        // THEN
        String volumeName = getObjectStore().getOzoneVolumeName(bucketName);
        OzoneInputStream ozoneInputStream = getObjectStore().getVolume(volumeName).getBucket(bucketName).readKey(keyName);
        String keyContent = IOUtils.toString(ozoneInputStream, Charset.forName("UTF-8"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("1234567890abcde", keyContent);
    }

    @Test
    public void testCopyObject() throws IOException, OS3Exception {
        // Put object in to source bucket
        HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        ByteArrayInputStream body = new ByteArrayInputStream(TestObjectPut.CONTENT.getBytes());
        objectEndpoint.setHeaders(headers);
        keyName = "sourceKey";
        Response response = objectEndpoint.put(bucketName, keyName, TestObjectPut.CONTENT.length(), 1, null, body);
        String volumeName = getObjectStore().getOzoneVolumeName(bucketName);
        OzoneInputStream ozoneInputStream = getObjectStore().getVolume(volumeName).getBucket(bucketName).readKey(keyName);
        String keyContent = IOUtils.toString(ozoneInputStream, Charset.forName("UTF-8"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(TestObjectPut.CONTENT, keyContent);
        // Add copy header, and then call put
        Mockito.when(headers.getHeaderString(S3Consts.COPY_SOURCE_HEADER)).thenReturn((((bucketName) + "/") + (keyName)));
        response = objectEndpoint.put(destBucket, destkey, TestObjectPut.CONTENT.length(), 1, null, body);
        // Check destination key and response
        volumeName = getObjectStore().getOzoneVolumeName(destBucket);
        ozoneInputStream = getObjectStore().getVolume(volumeName).getBucket(destBucket).readKey(destkey);
        keyContent = IOUtils.toString(ozoneInputStream, Charset.forName("UTF-8"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(TestObjectPut.CONTENT, keyContent);
        // source and dest same
        try {
            objectEndpoint.put(bucketName, keyName, TestObjectPut.CONTENT.length(), 1, null, body);
            Assert.fail("test copy object failed");
        } catch (OS3Exception ex) {
            Assert.assertTrue(ex.getErrorMessage().contains(("This copy request is " + "illegal")));
        }
        // source bucket not found
        try {
            Mockito.when(headers.getHeaderString(S3Consts.COPY_SOURCE_HEADER)).thenReturn((((nonexist) + "/") + (keyName)));
            objectEndpoint.put(destBucket, destkey, TestObjectPut.CONTENT.length(), 1, null, body);
            Assert.fail("test copy object failed");
        } catch (OS3Exception ex) {
            Assert.assertTrue(ex.getCode().contains("NoSuchBucket"));
        }
        // dest bucket not found
        try {
            Mockito.when(headers.getHeaderString(S3Consts.COPY_SOURCE_HEADER)).thenReturn((((bucketName) + "/") + (keyName)));
            objectEndpoint.put(nonexist, destkey, TestObjectPut.CONTENT.length(), 1, null, body);
            Assert.fail("test copy object failed");
        } catch (OS3Exception ex) {
            Assert.assertTrue(ex.getCode().contains("NoSuchBucket"));
        }
        // Both source and dest bucket not found
        try {
            Mockito.when(headers.getHeaderString(S3Consts.COPY_SOURCE_HEADER)).thenReturn((((nonexist) + "/") + (keyName)));
            objectEndpoint.put(nonexist, destkey, TestObjectPut.CONTENT.length(), 1, null, body);
            Assert.fail("test copy object failed");
        } catch (OS3Exception ex) {
            Assert.assertTrue(ex.getCode().contains("NoSuchBucket"));
        }
        // source key not found
        try {
            Mockito.when(headers.getHeaderString(S3Consts.COPY_SOURCE_HEADER)).thenReturn((((bucketName) + "/") + (nonexist)));
            objectEndpoint.put("nonexistent", keyName, TestObjectPut.CONTENT.length(), 1, null, body);
            Assert.fail("test copy object failed");
        } catch (OS3Exception ex) {
            Assert.assertTrue(ex.getCode().contains("NoSuchBucket"));
        }
    }

    @Test
    public void testInvalidStorageType() throws IOException {
        HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        ByteArrayInputStream body = new ByteArrayInputStream(TestObjectPut.CONTENT.getBytes());
        objectEndpoint.setHeaders(headers);
        keyName = "sourceKey";
        Mockito.when(headers.getHeaderString(S3Consts.STORAGE_CLASS_HEADER)).thenReturn("random");
        try {
            Response response = objectEndpoint.put(bucketName, keyName, TestObjectPut.CONTENT.length(), 1, null, body);
            Assert.fail("testInvalidStorageType");
        } catch (OS3Exception ex) {
            Assert.assertEquals(INVALID_ARGUMENT.getErrorMessage(), ex.getErrorMessage());
            Assert.assertEquals("random", ex.getResource());
        }
    }

    @Test
    public void testEmptyStorageType() throws IOException, OS3Exception {
        HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        ByteArrayInputStream body = new ByteArrayInputStream(TestObjectPut.CONTENT.getBytes());
        objectEndpoint.setHeaders(headers);
        keyName = "sourceKey";
        Mockito.when(headers.getHeaderString(S3Consts.STORAGE_CLASS_HEADER)).thenReturn("");
        Response response = objectEndpoint.put(bucketName, keyName, TestObjectPut.CONTENT.length(), 1, null, body);
        String volumeName = getObjectStore().getOzoneVolumeName(bucketName);
        OzoneKeyDetails key = getObjectStore().getVolume(volumeName).getBucket(bucketName).getKey(keyName);
        // default type is set
        Assert.assertEquals(RATIS, key.getReplicationType());
    }
}

