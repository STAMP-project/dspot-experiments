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
package org.apache.camel.cdi.test;


import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.cdi.Uri;
import org.apache.camel.cdi.bean.DefaultCamelContextBean;
import org.apache.camel.component.mock.MockEndpoint;
import org.hamcrest.Matchers;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class MockEndpointTest {
    @Inject
    private DefaultCamelContextBean defaultCamelContext;

    @Inject
    @Uri("direct:start")
    private ProducerTemplate defaultInbound;

    @Inject
    @Uri("mock:result")
    private MockEndpoint defaultOutbound;

    @Test
    public void verifyCamelContext() {
        Assert.assertThat(getName(), Matchers.is(Matchers.equalTo("camel-cdi")));
        Assert.assertThat(getName(), Matchers.is(Matchers.equalTo(getName())));
    }

    @Test
    public void sendMessageToInbound() throws InterruptedException {
        defaultOutbound.expectedMessageCount(1);
        defaultOutbound.expectedBodiesReceived("test");
        defaultOutbound.expectedHeaderReceived("foo", "bar");
        defaultInbound.sendBodyAndHeader("test", "foo", "bar");
        assertIsSatisfied(2L, TimeUnit.SECONDS, defaultOutbound);
    }
}

