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
package org.apache.camel.dataformat.bindy.fix;


import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.CommonBindyTest;
import org.apache.camel.dataformat.bindy.kvp.BindyKeyValuePairDataFormat;
import org.apache.camel.dataformat.bindy.model.fix.sorted.body.Order;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration
public class BindySimpleKeyValuePairSortedMarshallTest extends CommonBindyTest {
    private static final Logger LOG = LoggerFactory.getLogger(BindySimpleKeyValuePairSortedMarshallTest.class);

    @Test
    @DirtiesContext
    public void testMarshallMessage() {
        String message = "8=FIX 4.1\u00019=20\u000135=0\u000134=1\u000149=INVMGR\u000156=BRKR\u00011=BE.CHM.001\u000122=4\u000111=CHM0001-01\u000148=BE0001245678\u000154=1\u000158=this is a camel - bindy test\u000110=220\u0001\r\n";
        result.expectedBodiesReceived(message);
        template.sendBody(generateModel());
        try {
            result.assertIsSatisfied();
        } catch (InterruptedException e) {
            BindySimpleKeyValuePairSortedMarshallTest.LOG.error("Unit test error : ", e);
        }
    }

    public static class ContextConfig extends RouteBuilder {
        BindyKeyValuePairDataFormat kvpBindyDataFormat = new BindyKeyValuePairDataFormat(Order.class);

        public void configure() {
            from(CommonBindyTest.URI_DIRECT_START).marshal(kvpBindyDataFormat).to(CommonBindyTest.URI_MOCK_RESULT);
        }
    }
}

