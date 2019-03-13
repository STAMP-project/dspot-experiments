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
package org.apache.camel.processor.routingslip;


import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.FailedToCreateProducerException;
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;


public class RoutingSlipCreateProducerFailedTest extends ContextTestSupport {
    @Test
    public void testRoutingSlipCreateProducerFailed() throws Exception {
        // no inflight
        Assert.assertEquals(0, context.getInflightRepository().size());
        template.sendBodyAndHeader("direct:start", "Hello World", "foo", "log:foo");
        // no inflight
        Assert.assertEquals(0, context.getInflightRepository().size());
        // those 2 options not allowed together
        try {
            template.sendBodyAndHeader("direct:start", "Hello World", "foo", "file://target/data/test?fileExist=Append&tempPrefix=hello");
            Assert.fail("Should fail");
        } catch (CamelExecutionException e) {
            TestSupport.assertIsInstanceOf(FailedToCreateProducerException.class, e.getCause());
        }
        // no inflight
        Assert.assertEquals(0, context.getInflightRepository().size());
    }
}

