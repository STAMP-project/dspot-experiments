/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.aws.s3;


import CoreAttributes.ABSOLUTE_PATH;
import CoreAttributes.FILENAME;
import CoreAttributes.MIME_TYPE;
import CoreAttributes.PATH;
import FetchS3Object.ACCESS_KEY;
import FetchS3Object.AWS_CREDENTIALS_PROVIDER_SERVICE;
import FetchS3Object.BUCKET;
import FetchS3Object.CREDENTIALS_FILE;
import FetchS3Object.ENDPOINT_OVERRIDE;
import FetchS3Object.KEY;
import FetchS3Object.PROXY_HOST;
import FetchS3Object.PROXY_HOST_PORT;
import FetchS3Object.PROXY_PASSWORD;
import FetchS3Object.PROXY_USERNAME;
import FetchS3Object.REGION;
import FetchS3Object.REL_FAILURE;
import FetchS3Object.REL_SUCCESS;
import FetchS3Object.SECRET_KEY;
import FetchS3Object.SIGNER_OVERRIDE;
import FetchS3Object.SSL_CONTEXT_SERVICE;
import FetchS3Object.TIMEOUT;
import FetchS3Object.VERSION_ID;
import ProxyConfigurationService.PROXY_CONFIGURATION_SERVICE;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.StringInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestFetchS3Object {
    private TestRunner runner = null;

    private FetchS3Object mockFetchS3Object = null;

    private AmazonS3Client actualS3Client = null;

    private AmazonS3Client mockS3Client = null;

    @Test
    public void testGetObject() throws IOException {
        runner.setProperty(REGION, "us-east-1");
        runner.setProperty(BUCKET, "request-bucket");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "request-key");
        runner.enqueue(new byte[0], attrs);
        S3Object s3ObjectResponse = new S3Object();
        s3ObjectResponse.setBucketName("response-bucket-name");
        s3ObjectResponse.setKey("response-key");
        s3ObjectResponse.setObjectContent(new StringInputStream("Some Content"));
        ObjectMetadata metadata = Mockito.spy(ObjectMetadata.class);
        metadata.setContentDisposition("key/path/to/file.txt");
        metadata.setContentType("text/plain");
        metadata.setContentMD5("testMD5hash");
        Date expiration = new Date();
        metadata.setExpirationTime(expiration);
        metadata.setExpirationTimeRuleId("testExpirationRuleId");
        Map<String, String> userMetadata = new HashMap<>();
        userMetadata.put("userKey1", "userValue1");
        userMetadata.put("userKey2", "userValue2");
        metadata.setUserMetadata(userMetadata);
        metadata.setSSEAlgorithm("testAlgorithm");
        Mockito.when(metadata.getETag()).thenReturn("test-etag");
        s3ObjectResponse.setObjectMetadata(metadata);
        Mockito.when(mockS3Client.getObject(Mockito.any())).thenReturn(s3ObjectResponse);
        runner.run(1);
        ArgumentCaptor<GetObjectRequest> captureRequest = ArgumentCaptor.forClass(GetObjectRequest.class);
        Mockito.verify(mockS3Client, Mockito.times(1)).getObject(captureRequest.capture());
        GetObjectRequest request = captureRequest.getValue();
        Assert.assertEquals("request-bucket", request.getBucketName());
        Assert.assertEquals("request-key", request.getKey());
        Assert.assertNull(request.getVersionId());
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final List<MockFlowFile> ffs = runner.getFlowFilesForRelationship(REL_SUCCESS);
        MockFlowFile ff = ffs.get(0);
        ff.assertAttributeEquals("s3.bucket", "response-bucket-name");
        ff.assertAttributeEquals(FILENAME.key(), "file.txt");
        ff.assertAttributeEquals(PATH.key(), "key/path/to");
        ff.assertAttributeEquals(ABSOLUTE_PATH.key(), "key/path/to/file.txt");
        ff.assertAttributeEquals(MIME_TYPE.key(), "text/plain");
        ff.assertAttributeEquals("hash.value", "testMD5hash");
        ff.assertAttributeEquals("hash.algorithm", "MD5");
        ff.assertAttributeEquals("s3.etag", "test-etag");
        ff.assertAttributeEquals("s3.expirationTime", String.valueOf(expiration.getTime()));
        ff.assertAttributeEquals("s3.expirationTimeRuleId", "testExpirationRuleId");
        ff.assertAttributeEquals("userKey1", "userValue1");
        ff.assertAttributeEquals("userKey2", "userValue2");
        ff.assertAttributeEquals("s3.sseAlgorithm", "testAlgorithm");
        ff.assertContentEquals("Some Content");
    }

    @Test
    public void testGetObjectVersion() throws IOException {
        runner.setProperty(REGION, "us-east-1");
        runner.setProperty(BUCKET, "request-bucket");
        runner.setProperty(VERSION_ID, "${s3.version}");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "request-key");
        attrs.put("s3.version", "request-version");
        runner.enqueue(new byte[0], attrs);
        S3Object s3ObjectResponse = new S3Object();
        s3ObjectResponse.setBucketName("response-bucket-name");
        s3ObjectResponse.setObjectContent(new StringInputStream("Some Content"));
        ObjectMetadata metadata = Mockito.spy(ObjectMetadata.class);
        metadata.setContentDisposition("key/path/to/file.txt");
        Mockito.when(metadata.getVersionId()).thenReturn("response-version");
        s3ObjectResponse.setObjectMetadata(metadata);
        Mockito.when(mockS3Client.getObject(Mockito.any())).thenReturn(s3ObjectResponse);
        runner.run(1);
        ArgumentCaptor<GetObjectRequest> captureRequest = ArgumentCaptor.forClass(GetObjectRequest.class);
        Mockito.verify(mockS3Client, Mockito.times(1)).getObject(captureRequest.capture());
        GetObjectRequest request = captureRequest.getValue();
        Assert.assertEquals("request-bucket", request.getBucketName());
        Assert.assertEquals("request-key", request.getKey());
        Assert.assertEquals("request-version", request.getVersionId());
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final List<MockFlowFile> ffs = runner.getFlowFilesForRelationship(REL_SUCCESS);
        MockFlowFile ff = ffs.get(0);
        ff.assertAttributeEquals("s3.bucket", "response-bucket-name");
        ff.assertAttributeEquals(FILENAME.key(), "file.txt");
        ff.assertAttributeEquals(PATH.key(), "key/path/to");
        ff.assertAttributeEquals(ABSOLUTE_PATH.key(), "key/path/to/file.txt");
        ff.assertAttributeEquals("s3.version", "response-version");
        ff.assertContentEquals("Some Content");
    }

    @Test
    public void testGetObjectExceptionGoesToFailure() throws IOException {
        runner.setProperty(REGION, "us-east-1");
        runner.setProperty(BUCKET, "request-bucket");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "request-key");
        runner.enqueue(new byte[0], attrs);
        Mockito.doThrow(new AmazonS3Exception("NoSuchBucket")).when(mockS3Client).getObject(Mockito.any());
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testGetPropertyDescriptors() throws Exception {
        FetchS3Object processor = new FetchS3Object();
        List<PropertyDescriptor> pd = processor.getSupportedPropertyDescriptors();
        Assert.assertEquals("size should be eq", 17, pd.size());
        Assert.assertTrue(pd.contains(ACCESS_KEY));
        Assert.assertTrue(pd.contains(AWS_CREDENTIALS_PROVIDER_SERVICE));
        Assert.assertTrue(pd.contains(BUCKET));
        Assert.assertTrue(pd.contains(CREDENTIALS_FILE));
        Assert.assertTrue(pd.contains(ENDPOINT_OVERRIDE));
        Assert.assertTrue(pd.contains(KEY));
        Assert.assertTrue(pd.contains(REGION));
        Assert.assertTrue(pd.contains(SECRET_KEY));
        Assert.assertTrue(pd.contains(SIGNER_OVERRIDE));
        Assert.assertTrue(pd.contains(SSL_CONTEXT_SERVICE));
        Assert.assertTrue(pd.contains(TIMEOUT));
        Assert.assertTrue(pd.contains(VERSION_ID));
        Assert.assertTrue(pd.contains(PROXY_CONFIGURATION_SERVICE));
        Assert.assertTrue(pd.contains(PROXY_HOST));
        Assert.assertTrue(pd.contains(PROXY_HOST_PORT));
        Assert.assertTrue(pd.contains(PROXY_USERNAME));
        Assert.assertTrue(pd.contains(PROXY_PASSWORD));
    }
}

