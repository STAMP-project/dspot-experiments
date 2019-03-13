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


import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.cdi.ImportResource;
import org.apache.camel.cdi.Uri;
import org.apache.camel.cdi.mock.DummyRestConsumerFactory;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.rest.RestDefinition;
import org.hamcrest.Matchers;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
@ImportResource({ "imported-context-restContext.xml", "imported-context-restContextRef.xml" })
public class XmlRestContextTest {
    @Produces
    private static DummyRestConsumerFactory rest = new DummyRestConsumerFactory();

    @Inject
    @Uri("seda:get-inbound")
    private ProducerTemplate inbound;

    @Inject
    @Uri("mock:outbound")
    private MockEndpoint outbound;

    @Inject
    @Named("rest")
    private List<RestDefinition> rests;

    @Test
    public void verifyRestContext() {
        Assert.assertThat("Rest context is incorrect!", rests, Matchers.hasSize(1));
        RestDefinition rest = rests.get(0);
        Assert.assertThat("Rest path is incorrect!", rest.getPath(), Matchers.is(Matchers.equalTo("/inbound")));
    }

    @Test
    public void sendMessageToInbound() throws InterruptedException {
        outbound.expectedMessageCount(1);
        outbound.expectedBodiesReceived("Response to request");
        inbound.sendBody("request");
        assertIsSatisfied(2L, TimeUnit.SECONDS, outbound);
    }
}

