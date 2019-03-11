/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.io.network.api.reader;


import EndOfPartitionEvent.INSTANCE;
import java.io.IOException;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputView;
import org.apache.flink.runtime.event.TaskEvent;
import org.apache.flink.runtime.io.network.api.EndOfSuperstepEvent;
import org.apache.flink.runtime.io.network.partition.consumer.InputGate;
import org.apache.flink.runtime.util.event.EventListener;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;


/**
 * Tests for the event handling behaviour.
 */
public class AbstractReaderTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testTaskEvent() throws Exception {
        final AbstractReader reader = new AbstractReaderTest.MockReader(createInputGate(1));
        final EventListener<TaskEvent> listener1 = Mockito.mock(EventListener.class);
        final EventListener<TaskEvent> listener2 = Mockito.mock(EventListener.class);
        final EventListener<TaskEvent> listener3 = Mockito.mock(EventListener.class);
        reader.registerTaskEventListener(listener1, AbstractReaderTest.TestTaskEvent1.class);
        reader.registerTaskEventListener(listener2, AbstractReaderTest.TestTaskEvent2.class);
        reader.registerTaskEventListener(listener3, TaskEvent.class);
        reader.handleEvent(new AbstractReaderTest.TestTaskEvent1());// for listener1 only

        reader.handleEvent(new AbstractReaderTest.TestTaskEvent2());// for listener2 only

        Mockito.verify(listener1, Mockito.times(1)).onEvent(Matchers.any(TaskEvent.class));
        Mockito.verify(listener2, Mockito.times(1)).onEvent(Matchers.any(TaskEvent.class));
        Mockito.verify(listener3, Mockito.times(0)).onEvent(Matchers.any(TaskEvent.class));
    }

    @Test
    public void testEndOfPartitionEvent() throws Exception {
        final AbstractReader reader = new AbstractReaderTest.MockReader(createInputGate(1));
        Assert.assertTrue(reader.handleEvent(INSTANCE));
    }

    /**
     * Ensure that all end of superstep event related methods throw an Exception when used with a
     * non-iterative reader.
     */
    @Test
    public void testExceptionsNonIterativeReader() throws Exception {
        final AbstractReader reader = new AbstractReaderTest.MockReader(createInputGate(4));
        // Non-iterative reader cannot reach end of superstep
        Assert.assertFalse(reader.hasReachedEndOfSuperstep());
        try {
            reader.startNextSuperstep();
            Assert.fail("Did not throw expected exception when starting next superstep with non-iterative reader.");
        } catch (Throwable t) {
            // All good, expected exception.
        }
        try {
            reader.handleEvent(EndOfSuperstepEvent.INSTANCE);
            Assert.fail("Did not throw expected exception when handling end of superstep event with non-iterative reader.");
        } catch (Throwable t) {
            // All good, expected exception.
        }
    }

    @Test
    public void testEndOfSuperstepEventLogic() throws IOException {
        final int numberOfInputChannels = 4;
        final AbstractReader reader = new AbstractReaderTest.MockReader(createInputGate(numberOfInputChannels));
        reader.setIterativeReader();
        try {
            // The first superstep does not need not to be explicitly started
            reader.startNextSuperstep();
            Assert.fail("Did not throw expected exception when starting next superstep before receiving all end of superstep events.");
        } catch (Throwable t) {
            // All good, expected exception.
        }
        EndOfSuperstepEvent eos = EndOfSuperstepEvent.INSTANCE;
        // One end of superstep event for each input channel. The superstep finishes with the last
        // received event.
        for (int i = 0; i < (numberOfInputChannels - 1); i++) {
            Assert.assertFalse(reader.handleEvent(eos));
            Assert.assertFalse(reader.hasReachedEndOfSuperstep());
        }
        Assert.assertTrue(reader.handleEvent(eos));
        Assert.assertTrue(reader.hasReachedEndOfSuperstep());
        try {
            // Verify exception, when receiving too many end of superstep events.
            reader.handleEvent(eos);
            Assert.fail("Did not throw expected exception when receiving too many end of superstep events.");
        } catch (Throwable t) {
            // All good, expected exception.
        }
        // Start next superstep.
        reader.startNextSuperstep();
        Assert.assertFalse(reader.hasReachedEndOfSuperstep());
    }

    // ------------------------------------------------------------------------
    private static class TestTaskEvent1 extends TaskEvent {
        @Override
        public void write(DataOutputView out) throws IOException {
        }

        @Override
        public void read(DataInputView in) throws IOException {
        }
    }

    private static class TestTaskEvent2 extends TaskEvent {
        @Override
        public void write(DataOutputView out) throws IOException {
        }

        @Override
        public void read(DataInputView in) throws IOException {
        }
    }

    private static class MockReader extends AbstractReader {
        protected MockReader(InputGate inputGate) {
            super(inputGate);
        }
    }
}

