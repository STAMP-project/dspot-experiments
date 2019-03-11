/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache;


import Operation.CONTAINS_KEY;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class LocalDataSetTest {
    @Test
    public void verifyThatIsEmptyIsTrueWhenEntryCountReturnsZero() {
        PartitionedRegion pr = Mockito.mock(PartitionedRegion.class);
        Mockito.when(pr.isEmpty()).thenReturn(false);
        Mockito.when(pr.entryCount(ArgumentMatchers.any())).thenReturn(0);
        LocalDataSet lds = new LocalDataSet(pr, Collections.emptySet());
        Assert.assertTrue(lds.isEmpty());
    }

    @Test
    public void verifyThatIsEmptyIsFalseWhenEntryCountReturnsNonZero() {
        PartitionedRegion pr = Mockito.mock(PartitionedRegion.class);
        Mockito.when(pr.isEmpty()).thenReturn(true);
        Mockito.when(pr.entryCount(ArgumentMatchers.any())).thenReturn(1);
        LocalDataSet lds = new LocalDataSet(pr, Collections.emptySet());
        Assert.assertFalse(lds.isEmpty());
    }

    @Test
    public void verifyThatGetCallbackArgIsCorrectlyPassedToGetHashKey() {
        PartitionedRegion pr = Mockito.mock(PartitionedRegion.class);
        Mockito.when(pr.getTotalNumberOfBuckets()).thenReturn(33);
        LocalDataSet lds = new LocalDataSet(pr, Collections.emptySet());
        LocalDataSet spy = Mockito.spy(lds);
        Object key = "key";
        Object callbackArg = "callbackArg";
        spy.get(key, callbackArg);
        Mockito.verify(spy).getHashKey(CONTAINS_KEY, key, null, callbackArg);
    }
}

