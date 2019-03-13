/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.networking.nio;


import com.hazelcast.internal.networking.Channel;
import com.hazelcast.internal.networking.ChannelErrorHandler;
import com.hazelcast.logging.ILogger;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.ExpectedRuntimeException;
import com.hazelcast.test.HazelcastTestSupport;
import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests the NioThread. A lot of the internal are tested using a mock-selector. This gives us full
 * control on testing the edge cases which are extremely hard to realize with a real selector.
 */
public abstract class NioThreadAbstractTest extends HazelcastTestSupport {
    private ChannelErrorHandler errorHandler;

    private ILogger logger;

    private NioThreadAbstractTest.MockSelector selector;

    private NioPipeline handler;

    NioThread thread;

    @Test
    public void whenValidSelectionKey_thenHandlerCalled() {
        startThread();
        SelectionKey selectionKey = Mockito.mock(SelectionKey.class);
        selectionKey.attach(handler);
        Mockito.when(selectionKey.isValid()).thenReturn(true);
        selector.scheduleSelectAction(selectionKey);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Mockito.verify(handler).process();
            }
        });
        Assert.assertEquals(1, thread.getEventCount());
        assertStillRunning();
    }

    @Test
    public void whenInvalidSelectionKey_thenHandlerOnFailureCalledWithCancelledKeyException() {
        startThread();
        SelectionKey selectionKey = Mockito.mock(SelectionKey.class);
        selectionKey.attach(handler);
        Mockito.when(selectionKey.isValid()).thenReturn(false);
        selector.scheduleSelectAction(selectionKey);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Mockito.verify(handler).onError(ArgumentMatchers.isA(CancelledKeyException.class));
            }
        });
        assertStillRunning();
    }

    @Test
    public void whenHandlerThrowException_thenHandlerOnFailureCalledWithThatException() throws Exception {
        startThread();
        SelectionKey selectionKey = Mockito.mock(SelectionKey.class);
        selectionKey.attach(handler);
        Mockito.when(selectionKey.isValid()).thenReturn(true);
        Mockito.doThrow(new ExpectedRuntimeException()).when(handler).process();
        selector.scheduleSelectAction(selectionKey);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Mockito.verify(handler).onError(ArgumentMatchers.isA(ExpectedRuntimeException.class));
            }
        });
        assertStillRunning();
    }

    @Test
    public void whenSelectThrowsIOException_thenKeepRunning() {
        startThread();
        selector.scheduleSelectThrowsIOException();
        assertStillRunning();
    }

    @Test
    public void whenSelectThrowsOOME_thenThreadTerminates() {
        startThread();
        selector.scheduleSelectThrowsOOME();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse(thread.isAlive());
            }
        });
        Mockito.verify(errorHandler).onError(((Channel) (ArgumentMatchers.isNull())), ArgumentMatchers.any(OutOfMemoryError.class));
    }

    @Test
    public void testToString() {
        startThread();
        Assert.assertEquals(thread.getName(), thread.toString());
    }

    class MockSelector extends Selector {
        final BlockingQueue<NioThreadAbstractTest.SelectorAction> actionQueue = new LinkedBlockingQueue<NioThreadAbstractTest.SelectorAction>();

        Set<SelectionKey> pendingKeys;

        void scheduleSelectAction(SelectionKey selectionKey) {
            NioThreadAbstractTest.SelectorAction selectorAction = new NioThreadAbstractTest.SelectorAction();
            selectorAction.keys.add(selectionKey);
            actionQueue.add(selectorAction);
        }

        void scheduleSelectThrowsIOException() {
            NioThreadAbstractTest.SelectorAction selectorAction = new NioThreadAbstractTest.SelectorAction();
            selectorAction.selectThrowsIOException = true;
            actionQueue.add(selectorAction);
        }

        void scheduleSelectThrowsOOME() {
            NioThreadAbstractTest.SelectorAction selectorAction = new NioThreadAbstractTest.SelectorAction();
            selectorAction.selectThrowsOOME = true;
            actionQueue.add(selectorAction);
        }

        @Override
        public boolean isOpen() {
            return true;
        }

        @Override
        public SelectorProvider provider() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<SelectionKey> keys() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<SelectionKey> selectedKeys() {
            if ((pendingKeys) == null) {
                throw new IllegalArgumentException();
            }
            return pendingKeys;
        }

        @Override
        public int selectNow() throws IOException {
            return select(0);
        }

        @Override
        public int select(long timeout) throws IOException {
            try {
                NioThreadAbstractTest.SelectorAction action = actionQueue.poll(timeout, TimeUnit.MILLISECONDS);
                if (action == null) {
                    // there was a timeout.
                    return 0;
                }
                if (action.selectThrowsIOException) {
                    throw new IOException();
                }
                if (action.selectThrowsOOME) {
                    throw new OutOfMemoryError();
                }
                pendingKeys = action.keys;
                return pendingKeys.size();
            } catch (InterruptedException e) {
                // should not happen, so lets propagate it.
                throw new RuntimeException(e);
            }
        }

        @Override
        public int select() {
            // not needed for the time being.
            throw new UnsupportedOperationException();
        }

        @Override
        public Selector wakeup() {
            actionQueue.add(new NioThreadAbstractTest.SelectorAction());
            return this;
        }

        @Override
        public void close() {
        }
    }

    static class SelectorAction {
        final Set<SelectionKey> keys = new HashSet<SelectionKey>();

        boolean selectThrowsIOException;

        boolean selectThrowsOOME;
    }
}

