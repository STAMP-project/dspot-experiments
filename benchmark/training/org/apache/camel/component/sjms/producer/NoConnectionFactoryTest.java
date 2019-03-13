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
package org.apache.camel.component.sjms.producer;


import org.apache.camel.CamelContext;
import org.apache.camel.FailedToCreateProducerException;
import org.apache.camel.FailedToCreateRouteException;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A unit test to ensure getting a meaningful error message
 * when neither of ConnectionResource nor ConnectionFactory is configured.
 */
public class NoConnectionFactoryTest {
    private static final Logger LOG = LoggerFactory.getLogger(NoConnectionFactoryTest.class);

    @Test
    public void testConsumerInOnly() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(createConsumerInOnlyRouteBuilder());
        try {
            context.start();
        } catch (Throwable t) {
            Assert.assertEquals(IllegalArgumentException.class, t.getClass());
            NoConnectionFactoryTest.LOG.info("Expected exception was thrown", t);
            return;
        }
        Assert.fail("No exception was thrown");
    }

    @Test
    public void testConsumerInOut() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(createConsumerInOutRouteBuilder());
        try {
            context.start();
        } catch (Throwable t) {
            Assert.assertEquals(IllegalArgumentException.class, t.getClass());
            NoConnectionFactoryTest.LOG.info("Expected exception was thrown", t);
            return;
        }
        Assert.fail("No exception was thrown");
    }

    @Test
    public void testProducerInOnly() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(createProducerInOnlyRouteBuilder());
        try {
            context.start();
        } catch (Throwable t) {
            Assert.assertEquals(FailedToCreateRouteException.class, t.getClass());
            Assert.assertEquals(FailedToCreateProducerException.class, t.getCause().getClass());
            Assert.assertEquals(IllegalArgumentException.class, t.getCause().getCause().getClass());
            NoConnectionFactoryTest.LOG.info("Expected exception was thrown", t);
            return;
        }
        Assert.fail("No exception was thrown");
    }

    @Test
    public void testProducerInOut() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(createProducerInOutRouteBuilder());
        try {
            context.start();
        } catch (Throwable t) {
            Assert.assertEquals(FailedToCreateRouteException.class, t.getClass());
            Assert.assertEquals(FailedToCreateProducerException.class, t.getCause().getClass());
            Assert.assertEquals(IllegalArgumentException.class, t.getCause().getCause().getClass());
            NoConnectionFactoryTest.LOG.info("Expected exception was thrown", t);
            return;
        }
        Assert.fail("No exception was thrown");
    }
}

