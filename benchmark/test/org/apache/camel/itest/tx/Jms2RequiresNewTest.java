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
package org.apache.camel.itest.tx;


import DirtiesContext.ClassMode;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.itest.ITestSupport;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


/**
 * Unit test will look for the spring .xml file with the same class name
 * but postfixed with -config.xml as filename.
 * <p/>
 * We use Spring Testing for unit test, eg we extend AbstractJUnit4SpringContextTests
 * that is a Spring class.
 */
@ContextConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class Jms2RequiresNewTest extends AbstractJUnit4SpringContextTests {
    private static final int PORT3 = ITestSupport.getPort3();

    @Autowired
    private CamelContext camelContext;

    @EndpointInject(uri = "mock:result1")
    private MockEndpoint result1;

    @EndpointInject(uri = "mock:result2")
    private MockEndpoint result2;

    @EndpointInject(uri = "mock:dlq")
    private MockEndpoint dlq;

    @EndpointInject(uri = "direct:start")
    private ProducerTemplate start;

    @Test
    public void testSendThrowingException() throws Exception {
        result1.expectedMessageCount(0);
        result2.expectedMessageCount(1);
        dlq.expectedMessageCount(1);
        start.sendBody("Single ticket to Neverland please!");
        result2.assertIsSatisfied();
        dlq.assertIsSatisfied();
        result1.assertIsSatisfied();
    }

    @Test
    public void testSend() throws Exception {
        result1.expectedMessageCount(1);
        result2.expectedMessageCount(1);
        dlq.expectedMessageCount(0);
        start.sendBody("Piotr Klimczak");
        result1.assertIsSatisfied();
        result2.assertIsSatisfied();
        dlq.assertIsSatisfied();
    }
}

