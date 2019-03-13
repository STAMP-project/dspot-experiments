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
package org.apache.camel.component.vm;


import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.ResolveEndpointFailedException;
import org.apache.camel.TestSupport;
import org.apache.camel.util.SedaConstants;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class SameVmQueueSizeAndNoSizeTest extends ContextTestSupport {
    @Test
    public void testSameQueue() throws Exception {
        for (int i = 0; i < 100; i++) {
            template.sendBody("vm:foo", ("" + i));
        }
        try {
            template.sendBody("vm:foo", "Should be full now");
            Assert.fail("Should fail");
        } catch (CamelExecutionException e) {
            IllegalStateException ise = TestSupport.assertIsInstanceOf(IllegalStateException.class, e.getCause());
            if (!(TestSupport.isJavaVendor("ibm"))) {
                Assert.assertEquals("Queue full", ise.getMessage());
            }
        }
    }

    @Test
    public void testSameQueueDifferentSize() throws Exception {
        try {
            template.sendBody("vm:foo?size=200", "Should fail");
            Assert.fail("Should fail");
        } catch (ResolveEndpointFailedException e) {
            IllegalArgumentException ise = TestSupport.assertIsInstanceOf(IllegalArgumentException.class, e.getCause());
            Assert.assertEquals("Cannot use existing queue vm://foo as the existing queue size 100 does not match given queue size 200", ise.getMessage());
        }
    }

    @Test
    public void testSameQueueDifferentSizeBar() throws Exception {
        try {
            template.sendBody("vm:bar?size=200", "Should fail");
            Assert.fail("Should fail");
        } catch (ResolveEndpointFailedException e) {
            IllegalArgumentException ise = TestSupport.assertIsInstanceOf(IllegalArgumentException.class, e.getCause());
            Assert.assertEquals((("Cannot use existing queue vm://bar as the existing queue size " + (SedaConstants.QUEUE_SIZE)) + " does not match given queue size 200"), ise.getMessage());
        }
    }
}

