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
package org.apache.camel.component.cxf;


import Client.REQUEST_CONTEXT;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.cxf.helpers.CastUtils;
import org.junit.Assert;
import org.junit.Test;


// We use context to change the producer's endpoint address here
public class CxfProducerContextTest extends CxfProducerTest {
    // *** This class extends CxfProducerTest, so see that class for other tests
    // run by this code
    private static final String TEST_KEY = "sendSimpleMessage-test";

    private static final String TEST_VALUE = "exchange property value should get passed through request context";

    @Test
    public void testExchangePropertyPropagation() throws Exception {
        Exchange exchange = sendSimpleMessage();
        // No direct access to native CXF Message but we can verify the
        // request context from the Camel exchange
        Assert.assertNotNull(exchange);
        Map<String, Object> requestContext = CastUtils.cast(((Map<?, ?>) (exchange.getProperty(REQUEST_CONTEXT))));
        Assert.assertNotNull(requestContext);
        String actualValue = ((String) (requestContext.get(CxfProducerContextTest.TEST_KEY)));
        Assert.assertEquals("exchange property should get propagated to the request context", CxfProducerContextTest.TEST_VALUE, actualValue);
    }
}

