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
package org.apache.camel.dataformat.bindy.csv;


import java.util.Locale;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.dataformat.bindy.model.padding.Unity;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class BindyPatternLocaleTest extends CamelTestSupport {
    private Locale origLocale;

    @Test
    public void testMarshalling1() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:marshal");
        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived("050,010\r\n");
        Unity unity = new Unity();
        unity.setMandant(50.0F);
        unity.setReceiver(10.0F);
        template.sendBody("direct:marshal", unity);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testMarshalling2() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:marshal");
        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived("080,001\r\n");
        Unity unity = new Unity();
        unity.setMandant(80.0F);
        unity.setReceiver(1.0F);
        template.sendBody("direct:marshal", unity);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testMarshalling3() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:marshal");
        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived("000,000\r\n");
        Unity unity = new Unity();
        unity.setMandant(0.0F);
        unity.setReceiver(0.0F);
        template.sendBody("direct:marshal", unity);
        assertMockEndpointsSatisfied();
    }
}

