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
package org.apache.flink.queryablestate.client.state;


import java.util.List;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the {@link ImmutableListState}.
 */
public class ImmutableListStateTest {
    private final ListStateDescriptor<Long> listStateDesc = new ListStateDescriptor("test", BasicTypeInfo.LONG_TYPE_INFO);

    private ListState<Long> listState;

    @Test(expected = UnsupportedOperationException.class)
    public void testUpdate() throws Exception {
        List<Long> list = getStateContents();
        Assert.assertEquals(1L, list.size());
        long element = list.get(0);
        Assert.assertEquals(42L, element);
        listState.add(54L);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testClear() throws Exception {
        List<Long> list = getStateContents();
        Assert.assertEquals(1L, list.size());
        long element = list.get(0);
        Assert.assertEquals(42L, element);
        listState.clear();
    }
}

