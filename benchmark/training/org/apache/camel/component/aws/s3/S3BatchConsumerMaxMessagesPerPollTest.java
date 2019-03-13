/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.aws.s3;


import Exchange.BATCH_COMPLETE;
import Exchange.BATCH_INDEX;
import Exchange.BATCH_SIZE;
import org.apache.camel.BindToRegistry;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class S3BatchConsumerMaxMessagesPerPollTest extends CamelTestSupport {
    @BindToRegistry("amazonS3Client")
    AmazonS3ClientMock clientMock = new AmazonS3ClientMock();

    @EndpointInject(uri = "mock:result")
    private MockEndpoint mock;

    @Test
    public void receiveBatch() throws Exception {
        mock.expectedMessageCount(20);
        assertMockEndpointsSatisfied();
        mock.message(0).exchangeProperty(BATCH_INDEX).isEqualTo(1);
        mock.message(1).exchangeProperty(BATCH_INDEX).isEqualTo(2);
        mock.message(2).exchangeProperty(BATCH_INDEX).isEqualTo(3);
        mock.message(3).exchangeProperty(BATCH_INDEX).isEqualTo(4);
        mock.message(4).exchangeProperty(BATCH_INDEX).isEqualTo(5);
        mock.message(5).exchangeProperty(BATCH_INDEX).isEqualTo(6);
        mock.message(6).exchangeProperty(BATCH_INDEX).isEqualTo(7);
        mock.message(7).exchangeProperty(BATCH_INDEX).isEqualTo(8);
        mock.message(8).exchangeProperty(BATCH_INDEX).isEqualTo(9);
        mock.message(9).exchangeProperty(BATCH_INDEX).isEqualTo(10);
        mock.message(10).exchangeProperty(BATCH_INDEX).isEqualTo(11);
        mock.message(11).exchangeProperty(BATCH_INDEX).isEqualTo(12);
        mock.message(12).exchangeProperty(BATCH_INDEX).isEqualTo(13);
        mock.message(13).exchangeProperty(BATCH_INDEX).isEqualTo(14);
        mock.message(14).exchangeProperty(BATCH_INDEX).isEqualTo(15);
        mock.message(15).exchangeProperty(BATCH_INDEX).isEqualTo(16);
        mock.message(16).exchangeProperty(BATCH_INDEX).isEqualTo(17);
        mock.message(17).exchangeProperty(BATCH_INDEX).isEqualTo(18);
        mock.message(18).exchangeProperty(BATCH_INDEX).isEqualTo(19);
        mock.message(19).exchangeProperty(BATCH_INDEX).isEqualTo(20);
        mock.message(0).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(1).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(2).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(3).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(4).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(5).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(6).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(7).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(8).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(9).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(10).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(11).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(12).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(13).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(14).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(15).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(16).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(17).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(18).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(19).exchangeProperty(BATCH_COMPLETE).isEqualTo(true);
        mock.expectedPropertyReceived(BATCH_SIZE, 20);
    }
}

