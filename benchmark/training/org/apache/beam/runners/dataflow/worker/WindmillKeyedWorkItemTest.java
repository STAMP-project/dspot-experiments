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
package org.apache.beam.runners.dataflow.worker;


import GlobalWindow.Coder.INSTANCE;
import TimeDomain.EVENT_TIME;
import TimeDomain.PROCESSING_TIME;
import Windmill.Timer.Type.REALTIME;
import Windmill.Timer.Type.WATERMARK;
import Windmill.TimerBundle;
import Windmill.WorkItem.Builder;
import java.util.Collection;
import org.apache.beam.runners.core.KeyedWorkItem;
import org.apache.beam.runners.core.StateNamespace;
import org.apache.beam.runners.core.StateNamespaces;
import org.apache.beam.runners.dataflow.worker.WindmillKeyedWorkItem.FakeKeyedWorkItemCoder;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.CollectionCoder;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.testing.CoderProperties;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.ByteString;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;


/**
 * Tests for {@link WindmillKeyedWorkItem}.
 */
@RunWith(JUnit4.class)
public class WindmillKeyedWorkItemTest {
    private static final String STATE_FAMILY = "state";

    private static final String KEY = "key";

    private static final ByteString SERIALIZED_KEY = ByteString.copyFromUtf8(WindmillKeyedWorkItemTest.KEY);

    private static final Coder<IntervalWindow> WINDOW_CODER = IntervalWindow.getCoder();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final Coder<Collection<? extends BoundedWindow>> WINDOWS_CODER = ((Coder) (CollectionCoder.of(WindmillKeyedWorkItemTest.WINDOW_CODER)));

    private static final Coder<String> VALUE_CODER = StringUtf8Coder.of();

    private static final IntervalWindow WINDOW_1 = new IntervalWindow(new Instant(0), new Instant(10));

    private static final StateNamespace STATE_NAMESPACE_1 = StateNamespaces.window(WindmillKeyedWorkItemTest.WINDOW_CODER, WindmillKeyedWorkItemTest.WINDOW_1);

    private static final IntervalWindow WINDOW_2 = new IntervalWindow(new Instant(10), new Instant(20));

    private static final StateNamespace STATE_NAMESPACE_2 = StateNamespaces.window(WindmillKeyedWorkItemTest.WINDOW_CODER, WindmillKeyedWorkItemTest.WINDOW_2);

    @Mock
    private StreamingModeExecutionContext mockContext;

    @Test
    public void testElementIteration() throws Exception {
        Windmill.WorkItem.Builder workItem = Windmill.WorkItem.newBuilder().setKey(WindmillKeyedWorkItemTest.SERIALIZED_KEY).setWorkToken(17);
        Windmill.InputMessageBundle.Builder chunk1 = workItem.addMessageBundlesBuilder();
        chunk1.setSourceComputationId("computation");
        addElement(chunk1, 5, "hello", WindmillKeyedWorkItemTest.WINDOW_1, paneInfo(0));
        addElement(chunk1, 7, "world", WindmillKeyedWorkItemTest.WINDOW_2, paneInfo(2));
        Windmill.InputMessageBundle.Builder chunk2 = workItem.addMessageBundlesBuilder();
        chunk2.setSourceComputationId("computation");
        addElement(chunk2, 6, "earth", WindmillKeyedWorkItemTest.WINDOW_1, paneInfo(1));
        KeyedWorkItem<String, String> keyedWorkItem = new WindmillKeyedWorkItem(WindmillKeyedWorkItemTest.KEY, workItem.build(), WindmillKeyedWorkItemTest.WINDOW_CODER, WindmillKeyedWorkItemTest.WINDOWS_CODER, WindmillKeyedWorkItemTest.VALUE_CODER);
        Assert.assertThat(keyedWorkItem.elementsIterable(), Matchers.contains(WindowedValue.of("hello", new Instant(5), WindmillKeyedWorkItemTest.WINDOW_1, paneInfo(0)), WindowedValue.of("world", new Instant(7), WindmillKeyedWorkItemTest.WINDOW_2, paneInfo(2)), WindowedValue.of("earth", new Instant(6), WindmillKeyedWorkItemTest.WINDOW_1, paneInfo(1))));
    }

    /**
     * Make sure that event time timers are processed before other timers.
     */
    @Test
    public void testTimerOrdering() throws Exception {
        Windmill.WorkItem workItem = Windmill.WorkItem.newBuilder().setKey(WindmillKeyedWorkItemTest.SERIALIZED_KEY).setWorkToken(17).setTimers(TimerBundle.newBuilder().addTimers(WindmillKeyedWorkItemTest.makeSerializedTimer(WindmillKeyedWorkItemTest.STATE_NAMESPACE_1, 0, REALTIME)).addTimers(WindmillKeyedWorkItemTest.makeSerializedTimer(WindmillKeyedWorkItemTest.STATE_NAMESPACE_1, 1, WATERMARK)).addTimers(WindmillKeyedWorkItemTest.makeSerializedTimer(WindmillKeyedWorkItemTest.STATE_NAMESPACE_1, 2, REALTIME)).addTimers(WindmillKeyedWorkItemTest.makeSerializedTimer(WindmillKeyedWorkItemTest.STATE_NAMESPACE_2, 3, WATERMARK)).build()).build();
        KeyedWorkItem<String, String> keyedWorkItem = new WindmillKeyedWorkItem(WindmillKeyedWorkItemTest.KEY, workItem, WindmillKeyedWorkItemTest.WINDOW_CODER, WindmillKeyedWorkItemTest.WINDOWS_CODER, WindmillKeyedWorkItemTest.VALUE_CODER);
        Assert.assertThat(keyedWorkItem.timersIterable(), Matchers.contains(WindmillKeyedWorkItemTest.makeTimer(WindmillKeyedWorkItemTest.STATE_NAMESPACE_1, 1, EVENT_TIME), WindmillKeyedWorkItemTest.makeTimer(WindmillKeyedWorkItemTest.STATE_NAMESPACE_2, 3, EVENT_TIME), WindmillKeyedWorkItemTest.makeTimer(WindmillKeyedWorkItemTest.STATE_NAMESPACE_1, 0, PROCESSING_TIME), WindmillKeyedWorkItemTest.makeTimer(WindmillKeyedWorkItemTest.STATE_NAMESPACE_1, 2, PROCESSING_TIME)));
    }

    @Test
    public void testCoderIsSerializableWithWellKnownCoderType() {
        CoderProperties.coderSerializable(FakeKeyedWorkItemCoder.of(KvCoder.of(INSTANCE, INSTANCE)));
    }
}

