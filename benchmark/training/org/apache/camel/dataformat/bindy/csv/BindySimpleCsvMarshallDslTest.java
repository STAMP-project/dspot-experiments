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


import BindyType.Csv;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.dataformat.bindy.model.simple.oneclass.Order;
import org.apache.camel.model.dataformat.BindyDataFormat;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


@ContextConfiguration
public class BindySimpleCsvMarshallDslTest extends AbstractJUnit4SpringContextTests {
    private List<Map<String, Object>> models = new ArrayList<>();

    private String result = "1,B2,Keira,Knightley,ISIN,XX23456789,BUY,Share,450.45,EUR,14-01-2009,17-05-2010 23:21:59\r\n";

    @Produce(uri = "direct:start")
    private ProducerTemplate template;

    @EndpointInject(uri = "mock:result")
    private MockEndpoint resultEndpoint;

    @Test
    public void testMarshallMessage() throws Exception {
        resultEndpoint.expectedBodiesReceived(result);
        template.sendBody(generateModel());
        resultEndpoint.assertIsSatisfied();
    }

    public static class ContextConfig extends RouteBuilder {
        public void configure() {
            BindyDataFormat bindy = new BindyDataFormat();
            bindy.setLocale("en");
            bindy.setClassType(Order.class);
            bindy.setType(Csv);
            from("direct:start").marshal(bindy).to("mock:result");
        }
    }
}

