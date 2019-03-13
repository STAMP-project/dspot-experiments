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


import DeleteS3Object.REL_FAILURE;
import ProxyConfigurationService.PROXY_CONFIGURATION_SERVICE;
import TagS3Object.ACCESS_KEY;
import TagS3Object.APPEND_TAG;
import TagS3Object.AWS_CREDENTIALS_PROVIDER_SERVICE;
import TagS3Object.BUCKET;
import TagS3Object.CREDENTIALS_FILE;
import TagS3Object.ENDPOINT_OVERRIDE;
import TagS3Object.KEY;
import TagS3Object.PROXY_HOST;
import TagS3Object.PROXY_HOST_PORT;
import TagS3Object.PROXY_PASSWORD;
import TagS3Object.PROXY_USERNAME;
import TagS3Object.REGION;
import TagS3Object.REL_SUCCESS;
import TagS3Object.SECRET_KEY;
import TagS3Object.SIGNER_OVERRIDE;
import TagS3Object.SSL_CONTEXT_SERVICE;
import TagS3Object.TAG_KEY;
import TagS3Object.TAG_VALUE;
import TagS3Object.TIMEOUT;
import TagS3Object.VERSION_ID;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.Tag;
import java.io.IOException;
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


public class TestTagS3Object {
    private TestRunner runner = null;

    private TagS3Object mockTagS3Object = null;

    private AmazonS3Client actualS3Client = null;

    private AmazonS3Client mockS3Client = null;

    @Test
    public void testTagObjectSimple() throws IOException {
        final String tagKey = "k";
        final String tagVal = "v";
        runner.setProperty(REGION, "us-west-2");
        runner.setProperty(BUCKET, "test-bucket");
        runner.setProperty(TAG_KEY, tagKey);
        runner.setProperty(TAG_VALUE, tagVal);
        runner.setProperty(APPEND_TAG, "false");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "object-key");
        runner.enqueue(new byte[0], attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        ArgumentCaptor<SetObjectTaggingRequest> captureRequest = ArgumentCaptor.forClass(SetObjectTaggingRequest.class);
        Mockito.verify(mockS3Client, Mockito.times(1)).setObjectTagging(captureRequest.capture());
        SetObjectTaggingRequest request = captureRequest.getValue();
        Assert.assertEquals("test-bucket", request.getBucketName());
        Assert.assertEquals("object-key", request.getKey());
        Assert.assertNull("test-version", request.getVersionId());
        Assert.assertTrue("Expected tag not found in request", request.getTagging().getTagSet().contains(new Tag(tagKey, tagVal)));
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(ListS3.REL_SUCCESS);
        MockFlowFile ff0 = flowFiles.get(0);
        ff0.assertAttributeEquals(("s3.tag." + tagKey), tagVal);
    }

    @Test
    public void testTagObjectVersion() throws IOException {
        final String tagKey = "k";
        final String tagVal = "v";
        runner.setProperty(REGION, "us-west-2");
        runner.setProperty(BUCKET, "test-bucket");
        runner.setProperty(VERSION_ID, "test-version");
        runner.setProperty(TAG_KEY, tagKey);
        runner.setProperty(TAG_VALUE, tagVal);
        runner.setProperty(APPEND_TAG, "false");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "object-key");
        runner.enqueue(new byte[0], attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        ArgumentCaptor<SetObjectTaggingRequest> captureRequest = ArgumentCaptor.forClass(SetObjectTaggingRequest.class);
        Mockito.verify(mockS3Client, Mockito.times(1)).setObjectTagging(captureRequest.capture());
        SetObjectTaggingRequest request = captureRequest.getValue();
        Assert.assertEquals("test-bucket", request.getBucketName());
        Assert.assertEquals("object-key", request.getKey());
        Assert.assertEquals("test-version", request.getVersionId());
        Assert.assertTrue("Expected tag not found in request", request.getTagging().getTagSet().contains(new Tag(tagKey, tagVal)));
    }

    @Test
    public void testTagObjectAppendToExistingTags() throws IOException {
        // set up existing tags on S3 object
        Tag currentTag = new Tag("ck", "cv");
        mockGetExistingTags(currentTag);
        final String tagKey = "nk";
        final String tagVal = "nv";
        runner.setProperty(REGION, "us-west-2");
        runner.setProperty(BUCKET, "test-bucket");
        runner.setProperty(TAG_KEY, tagKey);
        runner.setProperty(TAG_VALUE, tagVal);
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "object-key");
        attrs.put(("s3.tag." + (currentTag.getKey())), currentTag.getValue());
        runner.enqueue(new byte[0], attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        ArgumentCaptor<SetObjectTaggingRequest> captureRequest = ArgumentCaptor.forClass(SetObjectTaggingRequest.class);
        Mockito.verify(mockS3Client, Mockito.times(1)).setObjectTagging(captureRequest.capture());
        SetObjectTaggingRequest request = captureRequest.getValue();
        Assert.assertEquals("test-bucket", request.getBucketName());
        Assert.assertEquals("object-key", request.getKey());
        Assert.assertTrue("New tag not found in request", request.getTagging().getTagSet().contains(new Tag(tagKey, tagVal)));
        Assert.assertTrue("Existing tag not found in request", request.getTagging().getTagSet().contains(currentTag));
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(ListS3.REL_SUCCESS);
        MockFlowFile ff0 = flowFiles.get(0);
        ff0.assertAttributeEquals(("s3.tag." + tagKey), tagVal);
        ff0.assertAttributeEquals(("s3.tag." + (currentTag.getKey())), currentTag.getValue());
    }

    @Test
    public void testTagObjectAppendUpdatesExistingTagValue() throws IOException {
        // set up existing tags on S3 object
        Tag currentTag1 = new Tag("ck", "cv");
        Tag currentTag2 = new Tag("nk", "ov");
        mockGetExistingTags(currentTag1, currentTag2);
        final String tagKey = "nk";
        final String tagVal = "nv";
        runner.setProperty(REGION, "us-west-2");
        runner.setProperty(BUCKET, "test-bucket");
        runner.setProperty(TAG_KEY, tagKey);
        runner.setProperty(TAG_VALUE, tagVal);
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "object-key");
        runner.enqueue(new byte[0], attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        ArgumentCaptor<SetObjectTaggingRequest> captureRequest = ArgumentCaptor.forClass(SetObjectTaggingRequest.class);
        Mockito.verify(mockS3Client, Mockito.times(1)).setObjectTagging(captureRequest.capture());
        SetObjectTaggingRequest request = captureRequest.getValue();
        Assert.assertEquals("test-bucket", request.getBucketName());
        Assert.assertEquals("object-key", request.getKey());
        Assert.assertTrue("New tag not found in request", request.getTagging().getTagSet().contains(new Tag(tagKey, tagVal)));
        Assert.assertTrue("Existing tag not found in request", request.getTagging().getTagSet().contains(currentTag1));
        Assert.assertFalse("Existing tag should be excluded from request", request.getTagging().getTagSet().contains(currentTag2));
    }

    @Test
    public void testTagObjectReplacesExistingTags() throws IOException {
        // set up existing tags on S3 object
        Tag currentTag = new Tag("ck", "cv");
        mockGetExistingTags(currentTag);
        final String tagKey = "nk";
        final String tagVal = "nv";
        runner.setProperty(REGION, "us-west-2");
        runner.setProperty(BUCKET, "test-bucket");
        runner.setProperty(TAG_KEY, tagKey);
        runner.setProperty(TAG_VALUE, tagVal);
        runner.setProperty(APPEND_TAG, "false");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "object-key");
        attrs.put(("s3.tag." + (currentTag.getKey())), currentTag.getValue());
        runner.enqueue(new byte[0], attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        ArgumentCaptor<SetObjectTaggingRequest> captureRequest = ArgumentCaptor.forClass(SetObjectTaggingRequest.class);
        Mockito.verify(mockS3Client, Mockito.times(1)).setObjectTagging(captureRequest.capture());
        SetObjectTaggingRequest request = captureRequest.getValue();
        Assert.assertEquals("test-bucket", request.getBucketName());
        Assert.assertEquals("object-key", request.getKey());
        Assert.assertTrue("New tag not found in request", request.getTagging().getTagSet().contains(new Tag(tagKey, tagVal)));
        Assert.assertFalse("Existing tag should be excluded from request", request.getTagging().getTagSet().contains(currentTag));
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(ListS3.REL_SUCCESS);
        MockFlowFile ff0 = flowFiles.get(0);
        ff0.assertAttributeEquals(("s3.tag." + tagKey), tagVal);
        ff0.assertAttributeNotExists(("s3.tag." + (currentTag.getKey())));
    }

    @Test
    public void testTagObjectS3Exception() {
        // set up existing tags on S3 object
        Tag currentTag = new Tag("ck", "cv");
        mockGetExistingTags(currentTag);
        final String tagKey = "nk";
        final String tagVal = "nv";
        runner.setProperty(REGION, "us-west-2");
        runner.setProperty(BUCKET, "test-bucket");
        runner.setProperty(TAG_KEY, tagKey);
        runner.setProperty(TAG_VALUE, tagVal);
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "delete-key");
        runner.enqueue(new byte[0], attrs);
        Mockito.doThrow(new AmazonS3Exception("TagFailure")).when(mockS3Client).setObjectTagging(Mockito.any());
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        ArgumentCaptor<SetObjectTaggingRequest> captureRequest = ArgumentCaptor.forClass(SetObjectTaggingRequest.class);
    }

    @Test
    public void testGetPropertyDescriptors() throws Exception {
        TagS3Object processor = new TagS3Object();
        List<PropertyDescriptor> pd = processor.getSupportedPropertyDescriptors();
        Assert.assertEquals("size should be eq", 20, pd.size());
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
        Assert.assertTrue(pd.contains(PROXY_CONFIGURATION_SERVICE));
        Assert.assertTrue(pd.contains(PROXY_HOST));
        Assert.assertTrue(pd.contains(PROXY_HOST_PORT));
        Assert.assertTrue(pd.contains(PROXY_USERNAME));
        Assert.assertTrue(pd.contains(PROXY_PASSWORD));
        Assert.assertTrue(pd.contains(TAG_KEY));
        Assert.assertTrue(pd.contains(TAG_VALUE));
        Assert.assertTrue(pd.contains(APPEND_TAG));
        Assert.assertTrue(pd.contains(VERSION_ID));
    }

    @Test
    public void testBucketEvaluatedAsBlank() {
        runner.setProperty(REGION, "us-west-2");
        runner.setProperty(BUCKET, "${not.existant.attribute}");
        runner.setProperty(TAG_KEY, "key");
        runner.setProperty(TAG_VALUE, "val");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "delete-key");
        runner.enqueue(new byte[0], attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testTagKeyEvaluatedAsBlank() {
        runner.setProperty(REGION, "us-west-2");
        runner.setProperty(BUCKET, "test-bucket");
        runner.setProperty(TAG_KEY, "${not.existant.attribute}");
        runner.setProperty(TAG_VALUE, "val");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "delete-key");
        runner.enqueue(new byte[0], attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testTagValEvaluatedAsBlank() {
        runner.setProperty(REGION, "us-west-2");
        runner.setProperty(BUCKET, "test-bucket");
        runner.setProperty(TAG_KEY, "tagKey");
        runner.setProperty(TAG_VALUE, "${not.existant.attribute}");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "delete-key");
        runner.enqueue(new byte[0], attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }
}

