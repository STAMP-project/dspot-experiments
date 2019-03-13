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
package org.apache.camel.component.hipchat;


import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.hamcrest.core.Is;
import org.junit.Test;


public class HipchatXmlDefinedComponentProducerTest extends CamelTestSupport {
    @EndpointInject(uri = "hipchat:https:foobar.com:443?authToken=abc123")
    protected Endpoint endpoint;

    @Test
    public void shouldConfigureEndpointCorrectlyViaXml() throws Exception {
        assertIsInstanceOf(HipchatEndpoint.class, endpoint);
        HipchatEndpoint hipchatEndpoint = ((HipchatEndpoint) (endpoint));
        HipchatConfiguration configuration = hipchatEndpoint.getConfiguration();
        assertThat(configuration.getAuthToken(), Is.is("abc123"));
        assertThat(configuration.getHost(), Is.is("foobar.com"));
        assertThat(configuration.getProtocol(), Is.is("https"));
        assertThat(configuration.getPort(), Is.is(443));
    }
}

