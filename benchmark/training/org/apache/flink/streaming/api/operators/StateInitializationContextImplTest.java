/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.api.operators;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import org.apache.flink.core.fs.CloseableRegistry;
import org.apache.flink.core.fs.FSDataInputStream;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataInputViewStreamWrapper;
import org.apache.flink.runtime.state.KeyGroupStatePartitionStreamProvider;
import org.apache.flink.runtime.state.StateInitializationContextImpl;
import org.apache.flink.runtime.state.StatePartitionStreamProvider;
import org.apache.flink.runtime.state.memory.ByteStreamStateHandle;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link StateInitializationContextImpl}.
 */
public class StateInitializationContextImplTest {
    static final int NUM_HANDLES = 10;

    private StateInitializationContextImpl initializationContext;

    private CloseableRegistry closableRegistry;

    private int writtenKeyGroups;

    private Set<Integer> writtenOperatorStates;

    @Test
    public void getOperatorStateStreams() throws Exception {
        int i = 0;
        int s = 0;
        for (StatePartitionStreamProvider streamProvider : initializationContext.getRawOperatorStateInputs()) {
            if (0 == (i % 4)) {
                ++i;
            }
            Assert.assertNotNull(streamProvider);
            try (InputStream is = streamProvider.getStream()) {
                DataInputView div = new DataInputViewStreamWrapper(is);
                int val = div.readInt();
                Assert.assertEquals(((i * (StateInitializationContextImplTest.NUM_HANDLES)) + s), val);
            }
            ++s;
            if (s == (i % 4)) {
                s = 0;
                ++i;
            }
        }
    }

    @Test
    public void getKeyedStateStreams() throws Exception {
        int readKeyGroupCount = 0;
        for (KeyGroupStatePartitionStreamProvider stateStreamProvider : initializationContext.getRawKeyedStateInputs()) {
            Assert.assertNotNull(stateStreamProvider);
            try (InputStream is = stateStreamProvider.getStream()) {
                DataInputView div = new DataInputViewStreamWrapper(is);
                int val = div.readInt();
                ++readKeyGroupCount;
                Assert.assertEquals(stateStreamProvider.getKeyGroupId(), val);
            }
        }
        Assert.assertEquals(writtenKeyGroups, readKeyGroupCount);
    }

    @Test
    public void getOperatorStateStore() throws Exception {
        Set<Integer> readStatesCount = new HashSet<>();
        for (StatePartitionStreamProvider statePartitionStreamProvider : initializationContext.getRawOperatorStateInputs()) {
            Assert.assertNotNull(statePartitionStreamProvider);
            try (InputStream is = statePartitionStreamProvider.getStream()) {
                DataInputView div = new DataInputViewStreamWrapper(is);
                Assert.assertTrue(readStatesCount.add(div.readInt()));
            }
        }
        Assert.assertEquals(writtenOperatorStates, readStatesCount);
    }

    @Test
    public void close() throws Exception {
        int count = 0;
        int stopCount = (StateInitializationContextImplTest.NUM_HANDLES) / 2;
        boolean isClosed = false;
        try {
            for (KeyGroupStatePartitionStreamProvider stateStreamProvider : initializationContext.getRawKeyedStateInputs()) {
                Assert.assertNotNull(stateStreamProvider);
                if (count == stopCount) {
                    closableRegistry.close();
                    isClosed = true;
                }
                try (InputStream is = stateStreamProvider.getStream()) {
                    DataInputView div = new DataInputViewStreamWrapper(is);
                    try {
                        int val = div.readInt();
                        Assert.assertEquals(stateStreamProvider.getKeyGroupId(), val);
                        if (isClosed) {
                            Assert.fail("Close was ignored: stream");
                        }
                        ++count;
                    } catch (IOException ioex) {
                        if (!isClosed) {
                            throw ioex;
                        }
                    }
                }
            }
            Assert.fail("Close was ignored: registry");
        } catch (IOException iex) {
            Assert.assertTrue(isClosed);
            Assert.assertEquals(stopCount, count);
        }
    }

    static final class ByteStateHandleCloseChecking extends ByteStreamStateHandle {
        private static final long serialVersionUID = -6201941296931334140L;

        public ByteStateHandleCloseChecking(String handleName, byte[] data) {
            super(handleName, data);
        }

        @Override
        public FSDataInputStream openInputStream() throws IOException {
            final FSDataInputStream original = super.openInputStream();
            return new FSDataInputStream() {
                private boolean closed = false;

                @Override
                public void seek(long desired) throws IOException {
                    original.seek(desired);
                }

                @Override
                public long getPos() throws IOException {
                    return original.getPos();
                }

                @Override
                public int read() throws IOException {
                    if (closed) {
                        throw new IOException("Stream closed");
                    }
                    return original.read();
                }

                @Override
                public void close() throws IOException {
                    original.close();
                    this.closed = true;
                }
            };
        }
    }
}

