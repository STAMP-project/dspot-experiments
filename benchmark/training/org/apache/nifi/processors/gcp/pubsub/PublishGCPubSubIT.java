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
package org.apache.nifi.processors.gcp.pubsub;


import PublishGCPubSub.BATCH_SIZE;
import PublishGCPubSub.GCP_CREDENTIALS_PROVIDER_SERVICE;
import PublishGCPubSub.PROJECT_ID;
import PublishGCPubSub.REL_SUCCESS;
import PublishGCPubSub.TOPIC_NAME;
import org.apache.nifi.reporting.InitializationException;
import org.junit.Test;


public class PublishGCPubSubIT extends AbstractGCPubSubIT {
    @Test
    public void testSimplePublish() throws InitializationException {
        final String topic = "my-topic";
        AbstractGCPubSubIT.runner = setCredentialsCS(AbstractGCPubSubIT.runner);
        AbstractGCPubSubIT.runner.setProperty(PublishGCPubSub.PROJECT_ID, AbstractGCPubSubIT.PROJECT_ID);
        AbstractGCPubSubIT.runner.setProperty(TOPIC_NAME, topic);
        AbstractGCPubSubIT.runner.setProperty(GCP_CREDENTIALS_PROVIDER_SERVICE, AbstractGCPubSubIT.CONTROLLER_SERVICE);
        AbstractGCPubSubIT.runner.setProperty(BATCH_SIZE, "1");
        AbstractGCPubSubIT.runner.assertValid();
        AbstractGCPubSubIT.runner.enqueue("Testing simple publish");
        AbstractGCPubSubIT.runner.run();
        AbstractGCPubSubIT.runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        AbstractGCPubSubIT.runner.assertAllFlowFilesContainAttribute(PubSubAttributes.MESSAGE_ID_ATTRIBUTE);
        AbstractGCPubSubIT.runner.assertAllFlowFilesContainAttribute(PubSubAttributes.TOPIC_NAME_ATTRIBUTE);
    }

    @Test
    public void testPublishWithFormattedTopicName() throws InitializationException {
        final String topic = "projects/my-gcm-client/topics/my-topic";
        AbstractGCPubSubIT.runner = setCredentialsCS(AbstractGCPubSubIT.runner);
        AbstractGCPubSubIT.runner.setProperty(PublishGCPubSub.PROJECT_ID, AbstractGCPubSubIT.PROJECT_ID);
        AbstractGCPubSubIT.runner.setProperty(TOPIC_NAME, topic);
        AbstractGCPubSubIT.runner.setProperty(GCP_CREDENTIALS_PROVIDER_SERVICE, AbstractGCPubSubIT.CONTROLLER_SERVICE);
        AbstractGCPubSubIT.runner.setProperty(BATCH_SIZE, "1");
        AbstractGCPubSubIT.runner.assertValid();
        AbstractGCPubSubIT.runner.enqueue("Testing publish with formatted topic name");
        AbstractGCPubSubIT.runner.run();
        AbstractGCPubSubIT.runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        AbstractGCPubSubIT.runner.assertAllFlowFilesContainAttribute(PubSubAttributes.MESSAGE_ID_ATTRIBUTE);
        AbstractGCPubSubIT.runner.assertAllFlowFilesContainAttribute(PubSubAttributes.TOPIC_NAME_ATTRIBUTE);
    }
}

