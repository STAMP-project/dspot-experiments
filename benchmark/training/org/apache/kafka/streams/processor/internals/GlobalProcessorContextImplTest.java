/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.processor.internals;


import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.processor.To;
import org.apache.kafka.streams.state.KeyValueStore;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;


public class GlobalProcessorContextImplTest {
    private static final String GLOBAL_STORE_NAME = "global-store";

    private static final String UNKNOWN_STORE = "unknown-store";

    private static final String CHILD_PROCESSOR = "child";

    private GlobalProcessorContextImpl globalContext;

    private ProcessorNode child;

    private ProcessorRecordContext recordContext;

    @Test
    public void shouldReturnGlobalOrNullStore() {
        MatcherAssert.assertThat(globalContext.getStateStore(GlobalProcessorContextImplTest.GLOBAL_STORE_NAME), new IsInstanceOf(KeyValueStore.class));
        Assert.assertNull(globalContext.getStateStore(GlobalProcessorContextImplTest.UNKNOWN_STORE));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldForwardToSingleChild() {
        child.process(null, null);
        expectLastCall();
        replay(child, recordContext);
        globalContext.forward(null, null);
        verify(child, recordContext);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailToForwardUsingToParameter() {
        globalContext.forward(null, null, To.all());
    }

    // need to test deprecated code until removed
    @SuppressWarnings("deprecation")
    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotSupportForwardingViaChildIndex() {
        globalContext.forward(null, null, 0);
    }

    // need to test deprecated code until removed
    @SuppressWarnings("deprecation")
    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotSupportForwardingViaChildName() {
        globalContext.forward(null, null, "processorName");
    }

    @Test
    public void shouldNotFailOnNoOpCommit() {
        globalContext.commit();
    }

    @SuppressWarnings("deprecation")
    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotAllowToSchedulePunctuationsUsingDeprecatedApi() {
        globalContext.schedule(0L, null, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotAllowToSchedulePunctuations() {
        globalContext.schedule(null, null, null);
    }

    @Test
    public void shouldNotAllowInit() {
        final StateStore store = globalContext.getStateStore(GlobalProcessorContextImplTest.GLOBAL_STORE_NAME);
        try {
            store.init(null, null);
            Assert.fail("Should have thrown UnsupportedOperationException.");
        } catch (final UnsupportedOperationException expected) {
        }
    }

    @Test
    public void shouldNotAllowClose() {
        final StateStore store = globalContext.getStateStore(GlobalProcessorContextImplTest.GLOBAL_STORE_NAME);
        try {
            store.close();
            Assert.fail("Should have thrown UnsupportedOperationException.");
        } catch (final UnsupportedOperationException expected) {
        }
    }
}

