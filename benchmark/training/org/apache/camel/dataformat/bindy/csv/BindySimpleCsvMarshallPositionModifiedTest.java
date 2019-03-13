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


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.CommonBindyTest;
import org.apache.camel.dataformat.bindy.model.simple.oneclassdifferentposition.Order;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration
public class BindySimpleCsvMarshallPositionModifiedTest extends CommonBindyTest {
    private List<Map<String, Object>> models = new ArrayList<>();

    private String expected;

    @Test
    @DirtiesContext
    public void testReverseMessage() throws Exception {
        expected = "08-01-2009,EUR,400.25,Share,BUY,BE12345678,ISIN,Knightley,Keira,B2,1\r\n";
        result.expectedBodiesReceived(expected);
        template.sendBody(generateModel());
        result.assertIsSatisfied();
    }

    public static class ContextConfig extends RouteBuilder {
        public void configure() {
            BindyCsvDataFormat csvBindyDataFormat = new BindyCsvDataFormat(Order.class);
            csvBindyDataFormat.setLocale("en");
            // default should errors go to mock:error
            errorHandler(deadLetterChannel(CommonBindyTest.URI_MOCK_ERROR).redeliveryDelay(0));
            onException(Exception.class).maximumRedeliveries(0).handled(true);
            from(CommonBindyTest.URI_DIRECT_START).marshal(csvBindyDataFormat).to(CommonBindyTest.URI_MOCK_RESULT);
        }
    }
}

