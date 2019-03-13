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
package org.apache.kafka.streams.kstream.internals;


import java.util.Collections;
import java.util.Set;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.internals.AbstractProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.test.InternalMockProcessorContext;
import org.junit.Assert;
import org.junit.Test;


public class KTableReduceTest {
    @Test
    public void shouldAddAndSubtract() {
        final AbstractProcessorContext context = new InternalMockProcessorContext();
        final Processor<String, Change<Set<String>>> reduceProcessor = new KTableReduce<String, Set<String>>("myStore", this::unionNotNullArgs, this::differenceNotNullArgs).get();
        final KeyValueStore<String, Set<String>> myStore = new org.apache.kafka.test.GenericInMemoryKeyValueStore("myStore");
        context.register(myStore, null);
        reduceProcessor.init(context);
        context.setCurrentNode(new org.apache.kafka.streams.processor.internals.ProcessorNode("reduce", reduceProcessor, Collections.singleton("myStore")));
        reduceProcessor.process("A", new Change(Collections.singleton("a"), null));
        Assert.assertEquals(Collections.singleton("a"), myStore.get("A"));
        reduceProcessor.process("A", new Change(Collections.singleton("b"), Collections.singleton("a")));
        Assert.assertEquals(Collections.singleton("b"), myStore.get("A"));
        reduceProcessor.process("A", new Change(null, Collections.singleton("b")));
        Assert.assertEquals(Collections.emptySet(), myStore.get("A"));
    }
}

