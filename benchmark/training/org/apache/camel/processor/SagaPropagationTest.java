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
package org.apache.camel.processor;


import java.util.List;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;


public class SagaPropagationTest extends ContextTestSupport {
    private List<String> sagaIds;

    @Test
    public void testPropagationRequired() throws Exception {
        context.createFluentProducerTemplate().to("direct:required").request();
        TestSupport.assertListSize(sagaIds, 3);
        assertUniqueNonNullSagaIds(1);
    }

    @Test
    public void testPropagationRequiresNew() throws Exception {
        context.createFluentProducerTemplate().to("direct:requiresNew").request();
        TestSupport.assertListSize(sagaIds, 3);
        assertUniqueNonNullSagaIds(3);
    }

    @Test
    public void testPropagationNotSupported() throws Exception {
        context.createFluentProducerTemplate().to("direct:notSupported").request();
        TestSupport.assertListSize(sagaIds, 4);
        assertNonNullSagaIds(1);
    }

    @Test
    public void testPropagationSupports() throws Exception {
        context.createFluentProducerTemplate().to("direct:supports").request();
        TestSupport.assertListSize(sagaIds, 2);
        assertNonNullSagaIds(1);
    }

    @Test
    public void testPropagationMandatory() throws Exception {
        try {
            context.createFluentProducerTemplate().to("direct:mandatory").request();
            Assert.fail("Exception not thrown");
        } catch (CamelExecutionException ex) {
            // fine
        }
    }

    @Test
    public void testPropagationNever() throws Exception {
        try {
            context.createFluentProducerTemplate().to("direct:never").request();
            Assert.fail("Exception not thrown");
        } catch (CamelExecutionException ex) {
            // fine
        }
    }
}

