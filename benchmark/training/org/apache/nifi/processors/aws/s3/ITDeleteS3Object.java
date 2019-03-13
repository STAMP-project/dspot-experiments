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


import DeleteS3Object.AWS_CREDENTIALS_PROVIDER_SERVICE;
import DeleteS3Object.BUCKET;
import DeleteS3Object.CREDENTIALS_FILE;
import DeleteS3Object.KEY;
import DeleteS3Object.REGION;
import DeleteS3Object.REL_SUCCESS;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.processors.aws.credentials.provider.service.AWSCredentialsProviderControllerService;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Test;


/**
 * Provides integration level testing with actual AWS S3 resources for {@link DeleteS3Object} and requires additional configuration and resources to work.
 */
public class ITDeleteS3Object extends AbstractS3IT {
    @Test
    public void testSimpleDelete() throws IOException {
        // Prepares for this test
        putTestFile("delete-me", getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME));
        final TestRunner runner = TestRunners.newTestRunner(new DeleteS3Object());
        runner.setProperty(DeleteS3Object.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(DeleteS3Object.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "delete-me");
        runner.enqueue(new byte[0], attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testDeleteFolder() throws IOException {
        // Prepares for this test
        putTestFile("folder/delete-me", getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME));
        final TestRunner runner = TestRunners.newTestRunner(new DeleteS3Object());
        runner.setProperty(DeleteS3Object.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(DeleteS3Object.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "folder/delete-me");
        runner.enqueue(new byte[0], attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testDeleteFolderUsingCredentialsProviderService() throws Throwable {
        // Prepares for this test
        putTestFile("folder/delete-me", getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME));
        final TestRunner runner = TestRunners.newTestRunner(new DeleteS3Object());
        final AWSCredentialsProviderControllerService serviceImpl = new AWSCredentialsProviderControllerService();
        runner.addControllerService("awsCredentialsProvider", serviceImpl);
        runner.setProperty(serviceImpl, AbstractAWSProcessor.CREDENTIALS_FILE, ((System.getProperty("user.home")) + "/aws-credentials.properties"));
        runner.enableControllerService(serviceImpl);
        runner.assertValid(serviceImpl);
        runner.setProperty(AWS_CREDENTIALS_PROVIDER_SERVICE, "awsCredentialsProvider");
        runner.setProperty(DeleteS3Object.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "folder/delete-me");
        runner.enqueue(new byte[0], attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testDeleteFolderNoExpressionLanguage() throws IOException {
        // Prepares for this test
        putTestFile("folder/delete-me", getFileFromResourceName(AbstractS3IT.SAMPLE_FILE_RESOURCE_NAME));
        final TestRunner runner = TestRunners.newTestRunner(new DeleteS3Object());
        runner.setProperty(DeleteS3Object.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(DeleteS3Object.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        runner.setProperty(KEY, "folder/delete-me");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "a-different-name");
        runner.enqueue(new byte[0], attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testTryToDeleteNotExistingFile() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new DeleteS3Object());
        runner.setProperty(DeleteS3Object.CREDENTIALS_FILE, AbstractS3IT.CREDENTIALS_FILE);
        runner.setProperty(DeleteS3Object.REGION, AbstractS3IT.REGION);
        runner.setProperty(BUCKET, AbstractS3IT.BUCKET_NAME);
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "no-such-a-file");
        runner.enqueue(new byte[0], attrs);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }
}

