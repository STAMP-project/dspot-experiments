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
package org.apache.camel.component.nagios;


import com.googlecode.jsendnsca.MessagePayload;
import com.googlecode.jsendnsca.NagiosPassiveCheckSender;
import com.googlecode.jsendnsca.PassiveCheckSender;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class NagiosEventNotifierTest extends CamelTestSupport {
    protected boolean canRun;

    @Mock
    private PassiveCheckSender nagiosPassiveCheckSender = Mockito.mock(NagiosPassiveCheckSender.class);

    @Test
    public void testNagiosEventNotifierOk() throws Exception {
        if (!(canRun)) {
            return;
        }
        getMockEndpoint("mock:ok").expectedMessageCount(1);
        template.sendBody("direct:ok", "Hello World");
        assertMockEndpointsSatisfied();
        context.stop();
        Mockito.verify(nagiosPassiveCheckSender, Mockito.atLeast(11)).send(ArgumentMatchers.any(MessagePayload.class));
    }

    @Test
    public void testNagiosEventNotifierError() throws Exception {
        if (!(canRun)) {
            return;
        }
        try {
            template.sendBody("direct:fail", "Bye World");
            fail("Should have thrown an exception");
        } catch (Exception e) {
            // ignore
        }
        context.stop();
        Mockito.verify(nagiosPassiveCheckSender, Mockito.atLeast(9)).send(ArgumentMatchers.any(MessagePayload.class));
    }
}

