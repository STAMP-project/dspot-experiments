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


import GlobalWindow.INSTANCE;
import com.google.api.services.dataflow.model.SideInputInfo;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.beam.runners.core.SideInputReader;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link LazilyInitializedSideInputReader}.
 */
@RunWith(JUnit4.class)
public class LazilyInitializedSideInputReaderTest {
    private static final String TEST_TAG = "test_tag";

    @Mock
    private SideInputReader mockSideInputReader;

    @Mock
    private PCollectionView mockPCollectionView;

    @Test
    public void testLazyInitialization() {
        final AtomicInteger wasCalled = new AtomicInteger();
        SideInputReader lazilyInitializedSideInputReader = new LazilyInitializedSideInputReader(ImmutableList.of(new SideInputInfo().setTag(LazilyInitializedSideInputReaderTest.TEST_TAG)), () -> {
            wasCalled.incrementAndGet();
            return mockSideInputReader;
        });
        // Ensure that after construction we have not been initialized yet.
        Assert.assertEquals(0, wasCalled.get());
        // Ensure that after checking some basic tag information we have not been initialized yet.
        Assert.assertFalse(lazilyInitializedSideInputReader.isEmpty());
        Assert.assertEquals(0, wasCalled.get());
        Mockito.when(mockPCollectionView.getTagInternal()).thenReturn(new TupleTag(LazilyInitializedSideInputReaderTest.TEST_TAG));
        Assert.assertTrue(lazilyInitializedSideInputReader.contains(mockPCollectionView));
        Assert.assertEquals(0, wasCalled.get());
        // Ensure that we were constructed only once, and provided the expected parameters and returned
        // the expected result.
        Mockito.when(mockSideInputReader.get(ArgumentMatchers.any(PCollectionView.class), ArgumentMatchers.any(BoundedWindow.class))).thenReturn(42).thenReturn(43);
        Assert.assertEquals(42, lazilyInitializedSideInputReader.get(mockPCollectionView, INSTANCE));
        Assert.assertEquals(1, wasCalled.get());
        Assert.assertEquals(43, lazilyInitializedSideInputReader.get(mockPCollectionView, INSTANCE));
        Assert.assertEquals(1, wasCalled.get());
    }
}

