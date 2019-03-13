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
package org.apache.camel.component.restlet;


import Exchange.HTTP_QUERY;
import Exchange.HTTP_RESPONSE_CODE;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Test;


public class RestletQueryTest extends RestletTestSupport {
    private static final String QUERY_STRING = "foo=bar&test=123";

    class SetUserProcessor implements Processor {
        public void process(Exchange exchange) throws Exception {
            assertEquals(RestletQueryTest.QUERY_STRING, exchange.getIn().getHeader(HTTP_QUERY, String.class));
        }
    }

    @Test
    public void testPostBody() throws Exception {
        HttpResponse response = doExecute(new HttpGet(((("http://localhost:" + (RestletTestSupport.portNum)) + "/users/homer?") + (RestletQueryTest.QUERY_STRING))));
        RestletTestSupport.assertHttpResponse(response, 204, "text/plain");
    }

    @Test
    public void testGetBodyByRestletProducer() throws Exception {
        Exchange ex = template.request("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(HTTP_QUERY, RestletQueryTest.QUERY_STRING);
                exchange.getIn().setHeader("username", "homer");
            }
        });
        assertEquals(204, ex.getOut().getHeader(HTTP_RESPONSE_CODE));
    }
}

