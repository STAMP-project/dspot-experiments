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
package org.apache.nifi.processors.aws.sqs;


import DeleteSQS.CREDENTIALS_FILE;
import DeleteSQS.QUEUE_URL;
import DeleteSQS.REGION;
import DeleteSQS.REL_SUCCESS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageResult;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("For local testing only - interacts with S3 so the credentials file must be configured and all necessary queues created")
public class ITDeleteSQS {
    private final String CREDENTIALS_FILE = (System.getProperty("user.home")) + "/aws-credentials.properties";

    private final String TEST_QUEUE_URL = "https://sqs.us-west-2.amazonaws.com/123456789012/nifi-test-queue";

    private final String TEST_REGION = "us-west-2";

    AmazonSQSClient sqsClient = null;

    @Test
    public void testSimpleDelete() throws IOException {
        // Setup - put one message in queue
        SendMessageResult sendMessageResult = sqsClient.sendMessage(TEST_QUEUE_URL, "Test message");
        Assert.assertEquals(200, sendMessageResult.getSdkHttpMetadata().getHttpStatusCode());
        // Setup - receive message to get receipt handle
        ReceiveMessageResult receiveMessageResult = sqsClient.receiveMessage(TEST_QUEUE_URL);
        Assert.assertEquals(200, receiveMessageResult.getSdkHttpMetadata().getHttpStatusCode());
        Message deleteMessage = receiveMessageResult.getMessages().get(0);
        String receiptHandle = deleteMessage.getReceiptHandle();
        // Test - delete message with DeleteSQS
        final TestRunner runner = TestRunners.newTestRunner(new DeleteSQS());
        runner.setProperty(DeleteSQS.CREDENTIALS_FILE, CREDENTIALS_FILE);
        runner.setProperty(QUEUE_URL, TEST_QUEUE_URL);
        runner.setProperty(REGION, TEST_REGION);
        final Map<String, String> ffAttributes = new HashMap<>();
        ffAttributes.put("filename", "1.txt");
        ffAttributes.put("sqs.receipt.handle", receiptHandle);
        runner.enqueue("TestMessageBody", ffAttributes);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }
}

