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


import org.apache.camel.Exchange;
import org.hamcrest.CoreMatchers;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("Does not run well on CI due test uses JMX mbeans")
public class RestRestletApiTest extends RestletTestSupport {
    @Test
    public void testApi() throws Exception {
        Exchange exchange = template.request((("http://localhost:" + (RestletTestSupport.portNum)) + "/docs"), null);
        assertThat(exchange.getOut().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class), CoreMatchers.is(200));
        String body = exchange.getOut().getBody(String.class);
        log.info("Received body: ", body);
        assertThat(body, CoreMatchers.containsString("\"version\" : \"1.2.3\""));
        assertThat(body, CoreMatchers.containsString("\"title\" : \"The hello rest thing\""));
        assertThat(body, CoreMatchers.containsString("\"/bye/{name}\""));
        assertThat(body, CoreMatchers.containsString("\"/hello/{name}\""));
        assertThat(body, CoreMatchers.containsString("\"summary\" : \"To update the greeting message\""));
    }
}

