/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.contrib.streaming.state;


import java.util.ArrayList;
import java.util.List;
import org.apache.flink.runtime.state.KeyGroupRange;
import org.apache.flink.runtime.state.KeyedStateHandle;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;


/**
 * Tests to guard {@link RocksDBIncrementalCheckpointUtils}.
 */
public class RocksDBIncrementalCheckpointUtilsTest extends TestLogger {
    @Rule
    public final TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void testClipDBWithKeyGroupRange() throws Exception {
        testClipDBWithKeyGroupRangeHelper(new KeyGroupRange(0, 1), new KeyGroupRange(0, 2), 1);
        testClipDBWithKeyGroupRangeHelper(new KeyGroupRange(0, 1), new KeyGroupRange(0, 1), 1);
        testClipDBWithKeyGroupRangeHelper(new KeyGroupRange(0, 1), new KeyGroupRange(1, 2), 1);
        testClipDBWithKeyGroupRangeHelper(new KeyGroupRange(0, 1), new KeyGroupRange(2, 4), 1);
        testClipDBWithKeyGroupRangeHelper(new KeyGroupRange(((Byte.MAX_VALUE) - 15), Byte.MAX_VALUE), new KeyGroupRange(((Byte.MAX_VALUE) - 10), Byte.MAX_VALUE), 1);
        testClipDBWithKeyGroupRangeHelper(new KeyGroupRange(((Short.MAX_VALUE) - 15), Short.MAX_VALUE), new KeyGroupRange(((Short.MAX_VALUE) - 10), Short.MAX_VALUE), 2);
        testClipDBWithKeyGroupRangeHelper(new KeyGroupRange(((Byte.MAX_VALUE) - 15), ((Byte.MAX_VALUE) - 1)), new KeyGroupRange(((Byte.MAX_VALUE) - 10), Byte.MAX_VALUE), 1);
        testClipDBWithKeyGroupRangeHelper(new KeyGroupRange(((Short.MAX_VALUE) - 15), ((Short.MAX_VALUE) - 1)), new KeyGroupRange(((Short.MAX_VALUE) - 10), Short.MAX_VALUE), 2);
    }

    @Test
    public void testChooseTheBestStateHandleForInitial() {
        List<KeyedStateHandle> keyedStateHandles = new ArrayList<>(3);
        KeyedStateHandle keyedStateHandle1 = Mockito.mock(KeyedStateHandle.class);
        Mockito.when(keyedStateHandle1.getKeyGroupRange()).thenReturn(new KeyGroupRange(0, 3));
        keyedStateHandles.add(keyedStateHandle1);
        KeyedStateHandle keyedStateHandle2 = Mockito.mock(KeyedStateHandle.class);
        Mockito.when(keyedStateHandle2.getKeyGroupRange()).thenReturn(new KeyGroupRange(4, 7));
        keyedStateHandles.add(keyedStateHandle2);
        KeyedStateHandle keyedStateHandle3 = Mockito.mock(KeyedStateHandle.class);
        Mockito.when(keyedStateHandle3.getKeyGroupRange()).thenReturn(new KeyGroupRange(8, 12));
        keyedStateHandles.add(keyedStateHandle3);
        // this should choose no one handle.
        Assert.assertNull(RocksDBIncrementalCheckpointUtils.chooseTheBestStateHandleForInitial(keyedStateHandles, new KeyGroupRange(3, 5)));
        // this should choose keyedStateHandle2, because keyedStateHandle2's key-group range satisfies the overlap fraction demand.
        Assert.assertEquals(keyedStateHandle2, RocksDBIncrementalCheckpointUtils.chooseTheBestStateHandleForInitial(keyedStateHandles, new KeyGroupRange(3, 6)));
        // both keyedStateHandle2 & keyedStateHandle3's key-group range satisfies the overlap fraction, but keyedStateHandle3's overlap fraction is better.
        Assert.assertEquals(keyedStateHandle3, RocksDBIncrementalCheckpointUtils.chooseTheBestStateHandleForInitial(keyedStateHandles, new KeyGroupRange(5, 12)));
        // both keyedStateHandle2 & keyedStateHandle3's key-group range are covered by [3, 12],
        // but this should choose the keyedStateHandle3, because keyedStateHandle3's key-group is bigger than keyedStateHandle2.
        Assert.assertEquals(keyedStateHandle3, RocksDBIncrementalCheckpointUtils.chooseTheBestStateHandleForInitial(keyedStateHandles, new KeyGroupRange(3, 12)));
    }
}

