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
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.camel.CamelException;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.cdi.ImportResource;
import org.apache.camel.cdi.Uri;
import org.apache.camel.cdi.rule.LogEventMatcher;
import org.apache.camel.cdi.rule.LogEventVerifier;
import org.apache.camel.component.mock.MockEndpoint;
import org.hamcrest.Matchers;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
@ImportResource("imported-context.xml")
public class XmlErrorHandlerPolicyTest {
    @ClassRule
    public static TestRule verifier = new LogEventVerifier() {
        @Override
        protected void verify() {
            Assert.assertThat("Log messages not found!", getEvents(), Matchers.containsInRelativeOrder(LogEventMatcher.logEvent().withLevel("INFO").withMessage(Matchers.containsString("Camel CDI is starting Camel context [test]")), LogEventMatcher.logEvent().withLevel("WARN").withMessage(Matchers.matchesPattern(("Failed delivery for \\(MessageId: .+\\). " + ("On delivery attempt: 3 " + "caught: org.apache.camel.CamelException: failure message!")))), LogEventMatcher.logEvent().withLevel("ERROR").withMessage(Matchers.matchesPattern(("(?s)Failed delivery for \\(MessageId: .+\\). " + ("Exhausted after delivery attempt: 4 " + "caught: org.apache.camel.CamelException: failure message!.*")))), LogEventMatcher.logEvent().withLevel("INFO").withMessage(Matchers.containsString("Camel CDI is stopping Camel context [test]"))));
        }
    };

    @Named
    @Produces
    private Exception failure = new CamelException("failure message!");

    @Inject
    @Uri("direct:inbound")
    private ProducerTemplate inbound;

    @Inject
    @Uri("mock:outbound")
    private MockEndpoint outbound;

    @Test
    public void sendMessageToInbound() throws InterruptedException {
        outbound.expectedMessageCount(1);
        outbound.expectedBodiesReceived("Response to message");
        inbound.sendBody("message");
        assertIsSatisfied(2L, TimeUnit.SECONDS, outbound);
    }

    @Test
    public void sendExceptionToInbound() {
        try {
            inbound.sendBody("exception");
        } catch (Exception exception) {
            Assert.assertThat("Exception is incorrect!", exception, Matchers.is(Matchers.instanceOf(CamelExecutionException.class)));
            Assert.assertThat("Exception cause is incorrect!", exception.getCause(), Matchers.is(Matchers.instanceOf(CamelException.class)));
            Assert.assertThat("Exception message is incorrect!", exception.getCause().getMessage(), Matchers.is(Matchers.equalTo("failure message!")));
            return;
        }
        Assert.fail("No exception thrown!");
    }
}

