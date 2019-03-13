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


import ConsumeGCPubSub.BATCH_SIZE;
import ConsumeGCPubSub.GCP_CREDENTIALS_PROVIDER_SERVICE;
import ConsumeGCPubSub.PROJECT_ID;
import ConsumeGCPubSub.REL_SUCCESS;
import ConsumeGCPubSub.SUBSCRIPTION;
import org.apache.nifi.reporting.InitializationException;
import org.junit.Test;


public class ConsumeGCPubSubIT extends AbstractGCPubSubIT {
    @Test
    public void testSimpleConsume() throws InitializationException {
        final String subscription = "my-sub";
        AbstractGCPubSubIT.runner.clearTransferState();
        AbstractGCPubSubIT.runner = setCredentialsCS(AbstractGCPubSubIT.runner);
        AbstractGCPubSubIT.runner.setProperty(ConsumeGCPubSub.PROJECT_ID, AbstractGCPubSubIT.PROJECT_ID);
        AbstractGCPubSubIT.runner.setProperty(GCP_CREDENTIALS_PROVIDER_SERVICE, AbstractGCPubSubIT.CONTROLLER_SERVICE);
        AbstractGCPubSubIT.runner.setProperty(SUBSCRIPTION, subscription);
        AbstractGCPubSubIT.runner.setProperty(BATCH_SIZE, "10");
        AbstractGCPubSubIT.runner.assertValid();
        AbstractGCPubSubIT.runner.run();
        AbstractGCPubSubIT.runner.assertAllFlowFilesTransferred(REL_SUCCESS, 10);
        AbstractGCPubSubIT.runner.assertAllFlowFilesContainAttribute(PubSubAttributes.ACK_ID_ATTRIBUTE);
        AbstractGCPubSubIT.runner.assertAllFlowFilesContainAttribute(PubSubAttributes.MSG_ATTRIBUTES_COUNT_ATTRIBUTE);
        AbstractGCPubSubIT.runner.assertAllFlowFilesContainAttribute(PubSubAttributes.MSG_PUBLISH_TIME_ATTRIBUTE);
    }

    @Test
    public void testConsumeWithBatchSize() throws InitializationException {
        final String subscription = "my-sub";
        AbstractGCPubSubIT.runner.clearTransferState();
        AbstractGCPubSubIT.runner = setCredentialsCS(AbstractGCPubSubIT.runner);
        AbstractGCPubSubIT.runner.setProperty(ConsumeGCPubSub.PROJECT_ID, AbstractGCPubSubIT.PROJECT_ID);
        AbstractGCPubSubIT.runner.setProperty(GCP_CREDENTIALS_PROVIDER_SERVICE, AbstractGCPubSubIT.CONTROLLER_SERVICE);
        AbstractGCPubSubIT.runner.setProperty(SUBSCRIPTION, subscription);
        AbstractGCPubSubIT.runner.setProperty(BATCH_SIZE, "2");
        AbstractGCPubSubIT.runner.assertValid();
        AbstractGCPubSubIT.runner.run();
        AbstractGCPubSubIT.runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        AbstractGCPubSubIT.runner.run();
        AbstractGCPubSubIT.runner.assertAllFlowFilesTransferred(REL_SUCCESS, 4);
        AbstractGCPubSubIT.runner.assertAllFlowFilesContainAttribute(PubSubAttributes.ACK_ID_ATTRIBUTE);
        AbstractGCPubSubIT.runner.assertAllFlowFilesContainAttribute(PubSubAttributes.MSG_ATTRIBUTES_COUNT_ATTRIBUTE);
        AbstractGCPubSubIT.runner.assertAllFlowFilesContainAttribute(PubSubAttributes.MSG_PUBLISH_TIME_ATTRIBUTE);
    }

    @Test
    public void testConsumeWithFormattedSubscriptionName() throws InitializationException {
        final String subscription = "projects/my-gcm-client/subscriptions/my-sub";
        AbstractGCPubSubIT.runner.clearTransferState();
        AbstractGCPubSubIT.runner = setCredentialsCS(AbstractGCPubSubIT.runner);
        AbstractGCPubSubIT.runner.setProperty(ConsumeGCPubSub.PROJECT_ID, AbstractGCPubSubIT.PROJECT_ID);
        AbstractGCPubSubIT.runner.setProperty(GCP_CREDENTIALS_PROVIDER_SERVICE, AbstractGCPubSubIT.CONTROLLER_SERVICE);
        AbstractGCPubSubIT.runner.setProperty(SUBSCRIPTION, subscription);
        AbstractGCPubSubIT.runner.setProperty(BATCH_SIZE, "2");
        AbstractGCPubSubIT.runner.assertValid();
        AbstractGCPubSubIT.runner.run();
        AbstractGCPubSubIT.runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        AbstractGCPubSubIT.runner.assertAllFlowFilesContainAttribute(PubSubAttributes.ACK_ID_ATTRIBUTE);
        AbstractGCPubSubIT.runner.assertAllFlowFilesContainAttribute(PubSubAttributes.MSG_ATTRIBUTES_COUNT_ATTRIBUTE);
        AbstractGCPubSubIT.runner.assertAllFlowFilesContainAttribute(PubSubAttributes.MSG_PUBLISH_TIME_ATTRIBUTE);
    }
}

