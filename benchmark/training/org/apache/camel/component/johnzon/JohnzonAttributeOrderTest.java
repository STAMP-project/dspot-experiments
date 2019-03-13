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
package org.apache.camel.component.johnzon;


import java.math.BigDecimal;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class JohnzonAttributeOrderTest extends CamelTestSupport {
    final String expectedJson = "{\"bool\":true,\"bg\":123.123,\"doubleNumber\":123.123,\"intNumber\":123,\"floatNumber\":123.0,\"longNumber\":123}";

    @Test
    public void testMarshalAndUnmarshalMap() throws Exception {
        NumberPojo nc = new NumberPojo();
        nc.setBg(new BigDecimal("123.123"));
        nc.setDoubleNumber(123.123);
        nc.setBool(true);
        nc.setFloatNumber(123);
        nc.setLongNumber(123L);
        nc.setIntNumber(123);
        MockEndpoint mock = getMockEndpoint("mock:reverse");
        mock.expectedMessageCount(1);
        mock.message(0).body().isInstanceOf(NumberPojo.class);
        mock.message(0).body().isEqualTo(nc);
        Object marshalled = template.requestBody("direct:in", nc);
        String marshalledAsString = context.getTypeConverter().convertTo(String.class, marshalled);
        assertEquals(expectedJson, marshalledAsString);
        template.sendBody("direct:back", marshalled);
        mock.assertIsSatisfied();
    }
}

