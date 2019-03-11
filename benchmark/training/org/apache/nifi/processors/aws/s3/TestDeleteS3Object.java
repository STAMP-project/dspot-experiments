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


import DeleteS3Object.BUCKET;
import DeleteS3Object.REGION;
import DeleteS3Object.REL_FAILURE;
import DeleteS3Object.REL_SUCCESS;
import DeleteS3Object.VERSION_ID;
import ProxyConfigurationService.PROXY_CONFIGURATION_SERVICE;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteVersionRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestDeleteS3Object {
    private TestRunner runner = null;

    private DeleteS3Object mockDeleteS3Object = null;

    private AmazonS3Client actualS3Client = null;

    private AmazonS3Client mockS3Client = null;

    @Test
    public void testDeleteObjectSimple() throws IOException {
        runner.setProperty(REGION, "us-west-2");
        runner.setProperty(BUCKET, "test-bucket");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "delete-key");
        runner.enqueue(new byte[0], attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        ArgumentCaptor<DeleteObjectRequest> captureRequest = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        Mockito.verify(mockS3Client, Mockito.times(1)).deleteObject(captureRequest.capture());
        DeleteObjectRequest request = captureRequest.getValue();
        Assert.assertEquals("test-bucket", request.getBucketName());
        Assert.assertEquals("delete-key", request.getKey());
        Mockito.verify(mockS3Client, Mockito.never()).deleteVersion(Mockito.any(DeleteVersionRequest.class));
    }

    @Test
    public void testDeleteObjectS3Exception() {
        runner.setProperty(REGION, "us-west-2");
        runner.setProperty(BUCKET, "test-bucket");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "delete-key");
        runner.enqueue(new byte[0], attrs);
        Mockito.doThrow(new AmazonS3Exception("NoSuchBucket")).when(mockS3Client).deleteObject(Mockito.any());
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        ArgumentCaptor<DeleteObjectRequest> captureRequest = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        Mockito.verify(mockS3Client, Mockito.never()).deleteVersion(Mockito.any(DeleteVersionRequest.class));
    }

    @Test
    public void testDeleteVersionSimple() {
        runner.setProperty(REGION, "us-west-2");
        runner.setProperty(BUCKET, "test-bucket");
        runner.setProperty(VERSION_ID, "test-version");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "test-key");
        runner.enqueue(new byte[0], attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        ArgumentCaptor<DeleteVersionRequest> captureRequest = ArgumentCaptor.forClass(DeleteVersionRequest.class);
        Mockito.verify(mockS3Client, Mockito.times(1)).deleteVersion(captureRequest.capture());
        DeleteVersionRequest request = captureRequest.getValue();
        Assert.assertEquals("test-bucket", request.getBucketName());
        Assert.assertEquals("test-key", request.getKey());
        Assert.assertEquals("test-version", request.getVersionId());
        Mockito.verify(mockS3Client, Mockito.never()).deleteObject(Mockito.any(DeleteObjectRequest.class));
    }

    @Test
    public void testDeleteVersionFromExpressions() {
        runner.setProperty(REGION, "us-west-2");
        runner.setProperty(BUCKET, "${s3.bucket}");
        runner.setProperty(VERSION_ID, "${s3.version}");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "test-key");
        attrs.put("s3.bucket", "test-bucket");
        attrs.put("s3.version", "test-version");
        runner.enqueue(new byte[0], attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        ArgumentCaptor<DeleteVersionRequest> captureRequest = ArgumentCaptor.forClass(DeleteVersionRequest.class);
        Mockito.verify(mockS3Client, Mockito.times(1)).deleteVersion(captureRequest.capture());
        DeleteVersionRequest request = captureRequest.getValue();
        Assert.assertEquals("test-bucket", request.getBucketName());
        Assert.assertEquals("test-key", request.getKey());
        Assert.assertEquals("test-version", request.getVersionId());
        Mockito.verify(mockS3Client, Mockito.never()).deleteObject(Mockito.any(DeleteObjectRequest.class));
    }

    @Test
    public void testGetPropertyDescriptors() throws Exception {
        DeleteS3Object processor = new DeleteS3Object();
        List<PropertyDescriptor> pd = processor.getSupportedPropertyDescriptors();
        Assert.assertEquals("size should be eq", 23, pd.size());
        Assert.assertTrue(pd.contains(processor.ACCESS_KEY));
        Assert.assertTrue(pd.contains(processor.AWS_CREDENTIALS_PROVIDER_SERVICE));
        Assert.assertTrue(pd.contains(processor.BUCKET));
        Assert.assertTrue(pd.contains(processor.CREDENTIALS_FILE));
        Assert.assertTrue(pd.contains(processor.ENDPOINT_OVERRIDE));
        Assert.assertTrue(pd.contains(processor.FULL_CONTROL_USER_LIST));
        Assert.assertTrue(pd.contains(processor.KEY));
        Assert.assertTrue(pd.contains(processor.OWNER));
        Assert.assertTrue(pd.contains(processor.READ_ACL_LIST));
        Assert.assertTrue(pd.contains(processor.READ_USER_LIST));
        Assert.assertTrue(pd.contains(processor.REGION));
        Assert.assertTrue(pd.contains(processor.SECRET_KEY));
        Assert.assertTrue(pd.contains(processor.SIGNER_OVERRIDE));
        Assert.assertTrue(pd.contains(processor.SSL_CONTEXT_SERVICE));
        Assert.assertTrue(pd.contains(processor.TIMEOUT));
        Assert.assertTrue(pd.contains(processor.VERSION_ID));
        Assert.assertTrue(pd.contains(processor.WRITE_ACL_LIST));
        Assert.assertTrue(pd.contains(processor.WRITE_USER_LIST));
        Assert.assertTrue(pd.contains(PROXY_CONFIGURATION_SERVICE));
        Assert.assertTrue(pd.contains(processor.PROXY_HOST));
        Assert.assertTrue(pd.contains(processor.PROXY_HOST_PORT));
        Assert.assertTrue(pd.contains(processor.PROXY_USERNAME));
        Assert.assertTrue(pd.contains(processor.PROXY_PASSWORD));
    }
}

