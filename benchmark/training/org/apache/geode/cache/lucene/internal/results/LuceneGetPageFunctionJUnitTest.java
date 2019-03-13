/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
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
package org.apache.geode.cache.lucene.internal.results;


import java.util.Collections;
import java.util.Set;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.internal.cache.EntrySnapshot;
import org.apache.geode.internal.cache.PartitionedRegion;
import org.apache.geode.internal.cache.RegionEntry;
import org.apache.geode.internal.cache.execute.InternalRegionFunctionContext;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ LuceneTest.class })
public class LuceneGetPageFunctionJUnitTest {
    @Test
    public void shouldReturnMapWithKeyAndValue() {
        PartitionedRegion region = Mockito.mock(PartitionedRegion.class);
        InternalRegionFunctionContext context = Mockito.mock(InternalRegionFunctionContext.class);
        Mockito.when(context.getDataSet()).thenReturn(region);
        ResultSender resultSender = Mockito.mock(ResultSender.class);
        Mockito.when(context.getResultSender()).thenReturn(resultSender);
        LuceneGetPageFunction function = new LuceneGetPageFunction();
        Mockito.when(context.getLocalDataSet(ArgumentMatchers.any())).thenReturn(region);
        final EntrySnapshot entry = Mockito.mock(EntrySnapshot.class);
        Mockito.when(region.getEntry(ArgumentMatchers.any())).thenReturn(entry);
        final RegionEntry regionEntry = Mockito.mock(RegionEntry.class);
        Mockito.when(entry.getRegionEntry()).thenReturn(regionEntry);
        Mockito.when(regionEntry.getValue(ArgumentMatchers.any())).thenReturn("value");
        Mockito.when(context.getFilter()).thenReturn(((Set) (Collections.singleton("key"))));
        function.execute(context);
        PageResults expectedResults = new PageResults();
        expectedResults.add(new PageEntry("key", "value"));
        Mockito.verify(resultSender).lastResult(ArgumentMatchers.eq(expectedResults));
    }
}

