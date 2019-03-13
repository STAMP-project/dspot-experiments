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
import org.apache.camel.cdi.bean.ManualCamelRoute;
import org.apache.camel.cdi.qualifier.Manual;
import org.apache.camel.component.mock.MockEndpoint;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class ManualCamelContextTest {
    @Inject
    @Uri("direct:start")
    private ProducerTemplate inbound;

    @Inject
    @Uri("mock:result")
    private MockEndpoint outbound;

    @Inject
    @Uri("direct:manual")
    private ProducerTemplate manual;

    @Inject
    @Uri("mock:manual")
    private MockEndpoint mock;

    @Inject
    @Manual
    private ManualCamelRoute builder;

    @Test
    @InSequence(3)
    public void sendMessageToInbound() throws InterruptedException {
        outbound.expectedMessageCount(1);
        outbound.expectedBodiesReceived("test");
        inbound.sendBody("test");
        assertIsSatisfied(2L, TimeUnit.SECONDS, outbound);
    }

    @Test
    @InSequence(4)
    public void sendMessageToManual() throws InterruptedException {
        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived("manual");
        manual.sendBody("manual");
        assertIsSatisfied(2L, TimeUnit.SECONDS, mock);
    }
}

