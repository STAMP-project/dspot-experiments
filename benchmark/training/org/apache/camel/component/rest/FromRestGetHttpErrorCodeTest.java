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
package org.apache.camel.component.rest;


import Exchange.CONTENT_TYPE;
import Exchange.HTTP_RESPONSE_CODE;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.junit.Assert;
import org.junit.Test;


public class FromRestGetHttpErrorCodeTest extends ContextTestSupport {
    @Test
    public void testFromRestModel() throws Exception {
        String out = template.requestBody("seda:get-say-bye", "I was here", String.class);
        Assert.assertEquals("Bye World", out);
        Exchange reply = template.request("seda:get-say-bye", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("Kaboom");
            }
        });
        Assert.assertNotNull(reply);
        Assert.assertEquals(404, reply.getOut().getHeader(HTTP_RESPONSE_CODE));
        Assert.assertEquals("text/plain", reply.getOut().getHeader(CONTENT_TYPE));
    }
}

