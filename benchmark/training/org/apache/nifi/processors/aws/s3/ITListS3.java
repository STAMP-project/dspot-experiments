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


import ListS3.AWS_CREDENTIALS_PROVIDER_SERVICE;
import ListS3.BUCKET;
import ListS3.CREDENTIALS_FILE;
import ListS3.DELIMITER;
import ListS3.PREFIX;
import ListS3.REGION;
import ListS3.REL_SUCCESS;
import ListS3.USE_VERSIONS;
import ListS3.WRITE_OBJECT_TAGS;
import com.amazonaws.services.s3.model.Tag;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.nifi.processors.aws.credentials.provider.service.AWSCredentialsProviderControllerService;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Test;


/**
 * Provides integration level testing with actual AWS S3 resources for {@link ListS3} and requires additional configuration and resources to work.
 */
public class ITListS3 extends AbstractS3IT {
    @Test
    public void testSimpleList() throws IOException {
        putTestFile("a", getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME));
        putTestFile("b/c", getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME));
        putTestFile("d/e", getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME));
        final TestRunner runner = TestRunners.newTestRunner(new ListS3());
        runner.setProperty(ListS3.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(ListS3.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 3);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        flowFiles.get(0).assertAttributeEquals("filename", "a");
        flowFiles.get(1).assertAttributeEquals("filename", "b/c");
        flowFiles.get(2).assertAttributeEquals("filename", "d/e");
    }

    @Test
    public void testSimpleListUsingCredentialsProviderService() throws Throwable {
        putTestFile("a", getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME));
        putTestFile("b/c", getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME));
        putTestFile("d/e", getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME));
        final TestRunner runner = TestRunners.newTestRunner(new ListS3());
        final AWSCredentialsProviderControllerService serviceImpl = new AWSCredentialsProviderControllerService();
        runner.addControllerService("awsCredentialsProvider", serviceImpl);
        runner.setProperty(serviceImpl, AbstractAWSProcessor.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.enableControllerService(serviceImpl);
        runner.assertValid(serviceImpl);
        runner.setProperty(AWS_CREDENTIALS_PROVIDER_SERVICE, "awsCredentialsProvider");
        runner.setProperty(ListS3.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 3);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        flowFiles.get(0).assertAttributeEquals("filename", "a");
        flowFiles.get(1).assertAttributeEquals("filename", "b/c");
        flowFiles.get(2).assertAttributeEquals("filename", "d/e");
    }

    @Test
    public void testSimpleListWithDelimiter() throws Throwable {
        putTestFile("a", getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME));
        putTestFile("b/c", getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME));
        putTestFile("d/e", getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME));
        final TestRunner runner = TestRunners.newTestRunner(new ListS3());
        runner.setProperty(ListS3.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(ListS3.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        runner.setProperty(DELIMITER, "/");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        flowFiles.get(0).assertAttributeEquals("filename", "a");
    }

    @Test
    public void testSimpleListWithPrefix() throws Throwable {
        putTestFile("a", getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME));
        putTestFile("b/c", getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME));
        putTestFile("d/e", getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME));
        final TestRunner runner = TestRunners.newTestRunner(new ListS3());
        runner.setProperty(ListS3.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(ListS3.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        runner.setProperty(PREFIX, "b/");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        flowFiles.get(0).assertAttributeEquals("filename", "b/c");
    }

    @Test
    public void testSimpleListWithPrefixAndVersions() throws Throwable {
        putTestFile("a", getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME));
        putTestFile("b/c", getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME));
        putTestFile("d/e", getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME));
        final TestRunner runner = TestRunners.newTestRunner(new ListS3());
        runner.setProperty(ListS3.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(ListS3.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        runner.setProperty(PREFIX, "b/");
        runner.setProperty(USE_VERSIONS, "true");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        flowFiles.get(0).assertAttributeEquals("filename", "b/c");
    }

    @Test
    public void testObjectTagsWritten() {
        List<Tag> objectTags = new ArrayList<>();
        objectTags.add(new Tag("dummytag1", "dummyvalue1"));
        objectTags.add(new Tag("dummytag2", "dummyvalue2"));
        putFileWithObjectTag("b/fileWithTag", getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME), objectTags);
        final TestRunner runner = TestRunners.newTestRunner(new ListS3());
        runner.setProperty(ListS3.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(PREFIX, "b/");
        runner.setProperty(ListS3.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        runner.setProperty(WRITE_OBJECT_TAGS, "true");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFiles.assertAttributeEquals("filename", "b/fileWithTag");
        flowFiles.assertAttributeExists("s3.tag.dummytag1");
        flowFiles.assertAttributeExists("s3.tag.dummytag2");
        flowFiles.assertAttributeEquals("s3.tag.dummytag1", "dummyvalue1");
        flowFiles.assertAttributeEquals("s3.tag.dummytag2", "dummyvalue2");
    }
}

