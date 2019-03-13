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
package org.apache.nifi.jms.processors;


import ConsumeJMS.DESTINATION;
import ConsumeJMS.DESTINATION_TYPE;
import ConsumeJMS.JMS_SOURCE_DESTINATION_NAME;
import ConsumeJMS.QUEUE;
import PublishJMS.CF_SERVICE;
import PublishJMS.REL_SUCCESS;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.jms.cf.JMSConnectionFactoryProviderDefinition;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;


public class ConsumeJMSIT {
    @Test
    public void validateSuccessfulConsumeAndTransferToSuccess() throws Exception {
        final String destinationName = "cooQueue";
        JmsTemplate jmsTemplate = CommonTest.buildJmsTemplateForDestination(false);
        try {
            JMSPublisher sender = new JMSPublisher(((CachingConnectionFactory) (jmsTemplate.getConnectionFactory())), jmsTemplate, Mockito.mock(ComponentLog.class));
            final Map<String, String> senderAttributes = new HashMap<>();
            senderAttributes.put("filename", "message.txt");
            senderAttributes.put("attribute_from_sender", "some value");
            sender.publish(destinationName, "Hey dude!".getBytes(), senderAttributes);
            TestRunner runner = TestRunners.newTestRunner(new ConsumeJMS());
            JMSConnectionFactoryProviderDefinition cs = Mockito.mock(JMSConnectionFactoryProviderDefinition.class);
            Mockito.when(cs.getIdentifier()).thenReturn("cfProvider");
            Mockito.when(cs.getConnectionFactory()).thenReturn(jmsTemplate.getConnectionFactory());
            runner.addControllerService("cfProvider", cs);
            runner.enableControllerService(cs);
            runner.setProperty(CF_SERVICE, "cfProvider");
            runner.setProperty(DESTINATION, destinationName);
            runner.setProperty(DESTINATION_TYPE, QUEUE);
            runner.run(1, false);
            // 
            final MockFlowFile successFF = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
            Assert.assertNotNull(successFF);
            successFF.assertAttributeExists(JmsHeaders.DESTINATION);
            successFF.assertAttributeEquals(JmsHeaders.DESTINATION, destinationName);
            successFF.assertAttributeExists("filename");
            successFF.assertAttributeEquals("filename", "message.txt");
            successFF.assertAttributeExists("attribute_from_sender");
            successFF.assertAttributeEquals("attribute_from_sender", "some value");
            successFF.assertContentEquals("Hey dude!".getBytes());
            String sourceDestination = successFF.getAttribute(JMS_SOURCE_DESTINATION_NAME);
            Assert.assertNotNull(sourceDestination);
        } finally {
            destroy();
        }
    }
}

