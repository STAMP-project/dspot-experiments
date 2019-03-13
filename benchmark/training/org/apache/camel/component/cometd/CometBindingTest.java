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
package org.apache.camel.component.cometd;


import CometdBinding.COMETD_SUBSCRIPTION_HEADER_NAME;
import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.BayeuxServerImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CometBindingTest {
    private static final Object FOO = new Object();

    private static final Long THIRTY_FOUR = Long.valueOf(34L);

    private static final Double TWO_POINT_ONE = Double.valueOf(2.1);

    private static final Integer EIGHT = new Integer(8);

    private static final String HELLO = "hello";

    private static final String FOO_ATTR_NAME = "foo";

    private static final String LONG_ATTR_NAME = "long";

    private static final String DOUBLE_ATTR_NAME = "double";

    private static final String INTEGER_ATTR_NAME = "integer";

    private static final String STRING_ATTR_NAME = "string";

    private static final String BOOLEAN_ATT_NAME = "boolean";

    private CometdBinding testObj;

    @Mock
    private BayeuxServerImpl bayeux;

    @Mock
    private ServerSession remote;

    @Mock
    private ServerMessage cometdMessage;

    private final CamelContext camelContext = new DefaultCamelContext();

    @Test
    public void testBindingTransfersSessionAttributtes() {
        // setup
        testObj = new CometdBinding(bayeux, true);
        // act
        Message result = testObj.createCamelMessage(camelContext, remote, cometdMessage, null);
        // assert
        Assert.assertEquals(6, result.getHeaders().size());
        Assert.assertEquals(CometBindingTest.HELLO, result.getHeader(CometBindingTest.STRING_ATTR_NAME));
        Assert.assertEquals(CometBindingTest.EIGHT, result.getHeader(CometBindingTest.INTEGER_ATTR_NAME));
        Assert.assertEquals(CometBindingTest.THIRTY_FOUR, result.getHeader(CometBindingTest.LONG_ATTR_NAME));
        Assert.assertEquals(CometBindingTest.TWO_POINT_ONE, result.getHeader(CometBindingTest.DOUBLE_ATTR_NAME));
        Assert.assertEquals(null, result.getHeader(CometBindingTest.FOO_ATTR_NAME));
        Assert.assertTrue(((Boolean) (result.getHeader(CometBindingTest.BOOLEAN_ATT_NAME))));
    }

    @Test
    public void testBindingHonorsFlagForSessionAttributtes() {
        // act
        Message result = testObj.createCamelMessage(camelContext, remote, cometdMessage, null);
        // assert
        Assert.assertEquals(1, result.getHeaders().size());
        Assert.assertEquals(null, result.getHeader(CometBindingTest.STRING_ATTR_NAME));
        Assert.assertEquals(null, result.getHeader(CometBindingTest.INTEGER_ATTR_NAME));
        Assert.assertEquals(null, result.getHeader(CometBindingTest.LONG_ATTR_NAME));
        Assert.assertEquals(null, result.getHeader(CometBindingTest.FOO_ATTR_NAME));
        Assert.assertEquals(null, result.getHeader(CometBindingTest.DOUBLE_ATTR_NAME));
        Assert.assertEquals(null, result.getHeader(CometBindingTest.BOOLEAN_ATT_NAME));
    }

    @Test
    public void testSubscriptionHeadersPassed() {
        // setup
        String expectedSubscriptionInfo = "subscriptionInfo";
        Mockito.when(cometdMessage.get(COMETD_SUBSCRIPTION_HEADER_NAME)).thenReturn(expectedSubscriptionInfo);
        // act
        Message result = testObj.createCamelMessage(camelContext, remote, cometdMessage, null);
        // assert
        Assert.assertEquals(2, result.getHeaders().size());
        Assert.assertEquals(expectedSubscriptionInfo, result.getHeader(COMETD_SUBSCRIPTION_HEADER_NAME));
    }
}

