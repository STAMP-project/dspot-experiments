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
package org.apache.activemq.ra;


import ActiveMQEndpointWorker.ON_MESSAGE_METHOD;
import java.lang.reflect.Method;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.resource.ResourceException;
import javax.resource.spi.endpoint.MessageEndpoint;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JMock.class)
public class MessageEndpointProxyTest {
    private MessageEndpoint mockEndpoint;

    private MessageEndpointProxyTest.EndpointAndListener mockEndpointAndListener;

    private Message stubMessage;

    private MessageEndpointProxy endpointProxy;

    private Mockery context;

    @Test(timeout = 60000)
    public void testInvalidConstruction() {
        try {
            new MessageEndpointProxy(mockEndpoint);
            Assert.fail("An exception should have been thrown");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
    }

    @Test(timeout = 60000)
    public void testSuccessfulCallSequence() throws Exception {
        setupBeforeDeliverySuccessful();
        setupOnMessageSuccessful();
        setupAfterDeliverySuccessful();
        doBeforeDeliveryExpectSuccess();
        doOnMessageExpectSuccess();
        doAfterDeliveryExpectSuccess();
    }

    @Test(timeout = 60000)
    public void testBeforeDeliveryFailure() throws Exception {
        context.checking(new Expectations() {
            {
                oneOf(mockEndpointAndListener).beforeDelivery(with(any(Method.class)));
                will(throwException(new ResourceException()));
            }
        });
        context.checking(new Expectations() {
            {
                never(mockEndpointAndListener).onMessage(null);
                never(mockEndpointAndListener).afterDelivery();
            }
        });
        setupExpectRelease();
        try {
            endpointProxy.beforeDelivery(ON_MESSAGE_METHOD);
            Assert.fail("An exception should have been thrown");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        doOnMessageExpectInvalidMessageEndpointException();
        doAfterDeliveryExpectInvalidMessageEndpointException();
        doFullyDeadCheck();
    }

    @Test(timeout = 60000)
    public void testOnMessageFailure() throws Exception {
        setupBeforeDeliverySuccessful();
        context.checking(new Expectations() {
            {
                oneOf(mockEndpointAndListener).onMessage(with(same(stubMessage)));
                will(throwException(new RuntimeException()));
            }
        });
        setupAfterDeliverySuccessful();
        doBeforeDeliveryExpectSuccess();
        try {
            endpointProxy.onMessage(stubMessage);
            Assert.fail("An exception should have been thrown");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        doAfterDeliveryExpectSuccess();
    }

    @Test(timeout = 60000)
    public void testAfterDeliveryFailure() throws Exception {
        setupBeforeDeliverySuccessful();
        setupOnMessageSuccessful();
        context.checking(new Expectations() {
            {
                oneOf(mockEndpointAndListener).afterDelivery();
                will(throwException(new ResourceException()));
            }
        });
        setupExpectRelease();
        doBeforeDeliveryExpectSuccess();
        doOnMessageExpectSuccess();
        try {
            endpointProxy.afterDelivery();
            Assert.fail("An exception should have been thrown");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        doFullyDeadCheck();
    }

    private interface EndpointAndListener extends MessageListener , MessageEndpoint {}
}

