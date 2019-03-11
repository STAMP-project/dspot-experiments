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


import CoreAttributes.FILENAME;
import ProxyConfigurationService.PROXY_CONFIGURATION_SERVICE;
import PutS3Object.ACCESS_KEY;
import PutS3Object.AWS_CREDENTIALS_PROVIDER_SERVICE;
import PutS3Object.BUCKET;
import PutS3Object.CANNED_ACL;
import PutS3Object.CREDENTIALS_FILE;
import PutS3Object.ENDPOINT_OVERRIDE;
import PutS3Object.EXPIRATION_RULE_ID;
import PutS3Object.FULL_CONTROL_USER_LIST;
import PutS3Object.KEY;
import PutS3Object.OBJECT_TAGS_PREFIX;
import PutS3Object.OWNER;
import PutS3Object.PROXY_HOST;
import PutS3Object.PROXY_HOST_PORT;
import PutS3Object.PROXY_PASSWORD;
import PutS3Object.PROXY_USERNAME;
import PutS3Object.READ_ACL_LIST;
import PutS3Object.READ_USER_LIST;
import PutS3Object.REGION;
import PutS3Object.REL_FAILURE;
import PutS3Object.REL_SUCCESS;
import PutS3Object.REMOVE_TAG_PREFIX;
import PutS3Object.S3_ETAG_ATTR_KEY;
import PutS3Object.S3_VERSION_ATTR_KEY;
import PutS3Object.SECRET_KEY;
import PutS3Object.SERVER_SIDE_ENCRYPTION;
import PutS3Object.SIGNER_OVERRIDE;
import PutS3Object.SSL_CONTEXT_SERVICE;
import PutS3Object.STORAGE_CLASS;
import PutS3Object.TIMEOUT;
import PutS3Object.WRITE_ACL_LIST;
import PutS3Object.WRITE_USER_LIST;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ListMultipartUploadsRequest;
import com.amazonaws.services.s3.model.MultipartUploadListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.Tag;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.components.AllowableValue;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestPutS3Object {
    private TestRunner runner = null;

    private PutS3Object mockPutS3Object = null;

    private AmazonS3Client actualS3Client = null;

    private AmazonS3Client mockS3Client = null;

    @Test
    public void testPutSinglePart() {
        runner.setProperty("x-custom-prop", "hello");
        testBase();
        ArgumentCaptor<PutObjectRequest> captureRequest = ArgumentCaptor.forClass(PutObjectRequest.class);
        Mockito.verify(mockS3Client, Mockito.times(1)).putObject(captureRequest.capture());
        PutObjectRequest request = captureRequest.getValue();
        Assert.assertEquals("test-bucket", request.getBucketName());
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        MockFlowFile ff0 = flowFiles.get(0);
        ff0.assertAttributeEquals(FILENAME.key(), "testfile.txt");
        ff0.assertAttributeEquals(S3_ETAG_ATTR_KEY, "test-etag");
        ff0.assertAttributeEquals(S3_VERSION_ATTR_KEY, "test-version");
    }

    @Test
    public void testPutSinglePartException() {
        runner.setProperty(REGION, "ap-northeast-1");
        runner.setProperty(BUCKET, "test-bucket");
        final Map<String, String> ffAttributes = new HashMap<>();
        ffAttributes.put("filename", "testfile.txt");
        runner.enqueue("Test Content", ffAttributes);
        MultipartUploadListing uploadListing = new MultipartUploadListing();
        Mockito.when(mockS3Client.listMultipartUploads(Mockito.any(ListMultipartUploadsRequest.class))).thenReturn(uploadListing);
        Mockito.when(mockS3Client.putObject(Mockito.any(PutObjectRequest.class))).thenThrow(new AmazonS3Exception("TestFail"));
        runner.assertValid();
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testSignerOverrideOptions() {
        final AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();
        final ClientConfiguration config = new ClientConfiguration();
        final PutS3Object processor = new PutS3Object();
        final TestRunner runner = TestRunners.newTestRunner(processor);
        final List<AllowableValue> allowableSignerValues = SIGNER_OVERRIDE.getAllowableValues();
        final String defaultSignerValue = SIGNER_OVERRIDE.getDefaultValue();
        for (AllowableValue allowableSignerValue : allowableSignerValues) {
            String signerType = allowableSignerValue.getValue();
            if (!(signerType.equals(defaultSignerValue))) {
                runner.setProperty(SIGNER_OVERRIDE, signerType);
                ProcessContext context = runner.getProcessContext();
                try {
                    AmazonS3Client s3Client = processor.createClient(context, credentialsProvider, config);
                } catch (IllegalArgumentException argEx) {
                    Assert.fail(argEx.getMessage());
                }
            }
        }
    }

    @Test
    public void testObjectTags() {
        runner.setProperty(OBJECT_TAGS_PREFIX, "tagS3");
        runner.setProperty(REMOVE_TAG_PREFIX, "false");
        testBase();
        ArgumentCaptor<PutObjectRequest> captureRequest = ArgumentCaptor.forClass(PutObjectRequest.class);
        Mockito.verify(mockS3Client, Mockito.times(1)).putObject(captureRequest.capture());
        PutObjectRequest request = captureRequest.getValue();
        List<Tag> tagSet = request.getTagging().getTagSet();
        Assert.assertEquals(1, tagSet.size());
        Assert.assertEquals("tagS3PII", tagSet.get(0).getKey());
        Assert.assertEquals("true", tagSet.get(0).getValue());
    }

    @Test
    public void testGetPropertyDescriptors() throws Exception {
        PutS3Object processor = new PutS3Object();
        List<PropertyDescriptor> pd = processor.getSupportedPropertyDescriptors();
        Assert.assertEquals("size should be eq", 33, pd.size());
        Assert.assertTrue(pd.contains(ACCESS_KEY));
        Assert.assertTrue(pd.contains(AWS_CREDENTIALS_PROVIDER_SERVICE));
        Assert.assertTrue(pd.contains(BUCKET));
        Assert.assertTrue(pd.contains(CANNED_ACL));
        Assert.assertTrue(pd.contains(CREDENTIALS_FILE));
        Assert.assertTrue(pd.contains(ENDPOINT_OVERRIDE));
        Assert.assertTrue(pd.contains(FULL_CONTROL_USER_LIST));
        Assert.assertTrue(pd.contains(KEY));
        Assert.assertTrue(pd.contains(OWNER));
        Assert.assertTrue(pd.contains(READ_ACL_LIST));
        Assert.assertTrue(pd.contains(READ_USER_LIST));
        Assert.assertTrue(pd.contains(REGION));
        Assert.assertTrue(pd.contains(SECRET_KEY));
        Assert.assertTrue(pd.contains(SIGNER_OVERRIDE));
        Assert.assertTrue(pd.contains(SSL_CONTEXT_SERVICE));
        Assert.assertTrue(pd.contains(TIMEOUT));
        Assert.assertTrue(pd.contains(EXPIRATION_RULE_ID));
        Assert.assertTrue(pd.contains(STORAGE_CLASS));
        Assert.assertTrue(pd.contains(WRITE_ACL_LIST));
        Assert.assertTrue(pd.contains(WRITE_USER_LIST));
        Assert.assertTrue(pd.contains(SERVER_SIDE_ENCRYPTION));
        Assert.assertTrue(pd.contains(PROXY_CONFIGURATION_SERVICE));
        Assert.assertTrue(pd.contains(PROXY_HOST));
        Assert.assertTrue(pd.contains(PROXY_HOST_PORT));
        Assert.assertTrue(pd.contains(PROXY_USERNAME));
        Assert.assertTrue(pd.contains(PROXY_PASSWORD));
        Assert.assertTrue(pd.contains(OBJECT_TAGS_PREFIX));
        Assert.assertTrue(pd.contains(REMOVE_TAG_PREFIX));
    }
}

