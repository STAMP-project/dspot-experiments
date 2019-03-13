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


import TagS3Object.APPEND_TAG;
import TagS3Object.BUCKET;
import TagS3Object.CREDENTIALS_FILE;
import TagS3Object.REGION;
import TagS3Object.REL_SUCCESS;
import TagS3Object.TAG_KEY;
import TagS3Object.TAG_VALUE;
import com.amazonaws.services.s3.model.GetObjectTaggingRequest;
import com.amazonaws.services.s3.model.GetObjectTaggingResult;
import com.amazonaws.services.s3.model.Tag;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


/**
 * Provides integration level testing with actual AWS S3 resources for {@link TagS3Object} and requires additional
 * configuration and resources to work.
 */
public class ITTagS3Object extends AbstractS3IT {
    @Test
    public void testSimpleTag() throws Exception {
        String objectKey = "test-file";
        String tagKey = "nifi-key";
        String tagValue = "nifi-val";
        // put file in s3
        putTestFile(objectKey, getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME));
        // Set up processor
        final TestRunner runner = TestRunners.newTestRunner(new TagS3Object());
        runner.setProperty(TagS3Object.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(TagS3Object.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        runner.setProperty(TAG_KEY, tagKey);
        runner.setProperty(TAG_VALUE, tagValue);
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", objectKey);
        runner.enqueue(new byte[0], attrs);
        // tag file
        runner.run(1);
        // Verify processor succeeds
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        // Verify tag exists on S3 object
        GetObjectTaggingResult res = AbstractS3IT.client.getObjectTagging(new GetObjectTaggingRequest(AbstractS3IT.BUCKET_NAME, objectKey));
        Assert.assertTrue("Expected tag not found on S3 object", res.getTagSet().contains(new Tag(tagKey, tagValue)));
    }

    @Test
    public void testAppendTag() throws Exception {
        String objectKey = "test-file";
        String tagKey = "nifi-key";
        String tagValue = "nifi-val";
        Tag existingTag = new Tag("oldkey", "oldvalue");
        // put file in s3
        putFileWithObjectTag(objectKey, getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME), Arrays.asList(existingTag));
        // Set up processor
        final TestRunner runner = TestRunners.newTestRunner(new TagS3Object());
        runner.setProperty(TagS3Object.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(TagS3Object.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        runner.setProperty(TAG_KEY, tagKey);
        runner.setProperty(TAG_VALUE, tagValue);
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", objectKey);
        runner.enqueue(new byte[0], attrs);
        // tag file
        runner.run(1);
        // Verify processor succeeds
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        // Verify new tag and existing exist on S3 object
        GetObjectTaggingResult res = AbstractS3IT.client.getObjectTagging(new GetObjectTaggingRequest(AbstractS3IT.BUCKET_NAME, objectKey));
        Assert.assertTrue("Expected new tag not found on S3 object", res.getTagSet().contains(new Tag(tagKey, tagValue)));
        Assert.assertTrue("Expected existing tag not found on S3 object", res.getTagSet().contains(existingTag));
    }

    @Test
    public void testReplaceTags() throws Exception {
        String objectKey = "test-file";
        String tagKey = "nifi-key";
        String tagValue = "nifi-val";
        Tag existingTag = new Tag("s3.tag.oldkey", "oldvalue");
        // put file in s3
        putFileWithObjectTag(objectKey, getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME), Arrays.asList(existingTag));
        // Set up processor
        final TestRunner runner = TestRunners.newTestRunner(new TagS3Object());
        runner.setProperty(TagS3Object.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(TagS3Object.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        runner.setProperty(TAG_KEY, tagKey);
        runner.setProperty(TAG_VALUE, tagValue);
        runner.setProperty(APPEND_TAG, "false");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", objectKey);
        attrs.put(("s3.tag." + (existingTag.getKey())), existingTag.getValue());
        runner.enqueue(new byte[0], attrs);
        // tag file
        runner.run(1);
        // Verify processor succeeds
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        // Verify flowfile attributes match s3 tags
        MockFlowFile flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFiles.assertAttributeNotExists(existingTag.getKey());
        flowFiles.assertAttributeEquals(("s3.tag." + tagKey), tagValue);
        // Verify new tag exists on S3 object and prior tag removed
        GetObjectTaggingResult res = AbstractS3IT.client.getObjectTagging(new GetObjectTaggingRequest(AbstractS3IT.BUCKET_NAME, objectKey));
        Assert.assertTrue("Expected new tag not found on S3 object", res.getTagSet().contains(new Tag(tagKey, tagValue)));
        Assert.assertFalse("Existing tag not replaced on S3 object", res.getTagSet().contains(existingTag));
    }
}

