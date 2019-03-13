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
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.cdi.Uri;
import org.apache.camel.cdi.bean.FirstCamelContextPropertyInjectBean;
import org.apache.camel.cdi.bean.PropertyInjectBean;
import org.apache.camel.cdi.bean.SecondCamelContextPropertyInjectBean;
import org.apache.camel.component.mock.MockEndpoint;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class MultiContextPropertyInjectTest {
    @Inject
    private CamelContext defaultCamelContext;

    @Inject
    @Uri("direct:in")
    private ProducerTemplate defaultInbound;

    @Inject
    @Uri("mock:out")
    private MockEndpoint defaultOutbound;

    @Inject
    @ContextName("first")
    private CamelContext firstCamelContext;

    @Inject
    @ContextName("first")
    @Uri("direct:in")
    private ProducerTemplate firstInbound;

    @Inject
    @ContextName("first")
    @Uri("mock:out")
    private MockEndpoint firstOutbound;

    @Inject
    @ContextName("second")
    private CamelContext secondCamelContext;

    @Inject
    @ContextName("second")
    @Uri("direct:in")
    private ProducerTemplate secondInbound;

    @Inject
    @ContextName("second")
    @Uri("mock:out")
    private MockEndpoint secondOutbound;

    @Test
    @InSequence(1)
    public void configureCamelContexts() throws Exception {
        defaultCamelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:in").bean(PropertyInjectBean.class).to("mock:out");
            }
        });
        firstCamelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:in").bean(FirstCamelContextPropertyInjectBean.class).to("mock:out");
            }
        });
        secondCamelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:in").bean(SecondCamelContextPropertyInjectBean.class).to("mock:out");
            }
        });
        secondCamelContext.getRouteController().startAllRoutes();
    }

    @Test
    @InSequence(2)
    public void sendMessageToDefaultCamelContextInbound() throws InterruptedException {
        defaultOutbound.expectedMessageCount(1);
        defaultOutbound.expectedBodiesReceived("test");
        defaultOutbound.expectedHeaderReceived("header", "default");
        defaultInbound.sendBody("test");
        assertIsSatisfied(2L, TimeUnit.SECONDS, defaultOutbound);
    }

    @Test
    @InSequence(4)
    public void sendMessageToFirstCamelContextInbound() throws InterruptedException {
        firstOutbound.expectedMessageCount(1);
        firstOutbound.expectedBodiesReceived("test");
        firstOutbound.expectedHeaderReceived("header", "default");
        firstInbound.sendBody("test");
        assertIsSatisfied(2L, TimeUnit.SECONDS, firstOutbound);
    }

    @Test
    @InSequence(6)
    public void sendMessageToSecondCamelContextInbound() throws InterruptedException {
        secondOutbound.expectedMessageCount(1);
        secondOutbound.expectedBodiesReceived("test");
        secondOutbound.expectedHeaderReceived("header", "default");
        secondInbound.sendBody("test");
        assertIsSatisfied(2L, TimeUnit.SECONDS, secondOutbound);
    }
}

