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
package org.apache.beam.sdk.extensions.sorter;


import BufferedExternalSorter.Options;
import java.nio.file.Path;
import java.util.Arrays;
import org.apache.beam.sdk.values.KV;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link BufferedExternalSorter}.
 */
@RunWith(JUnit4.class)
public class BufferedExternalSorterTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static Path tmpLocation;

    @SuppressWarnings("unchecked")
    @Test
    public void testNoFallback() throws Exception {
        ExternalSorter mockExternalSorter = Mockito.mock(ExternalSorter.class);
        InMemorySorter mockInMemorySorter = Mockito.mock(InMemorySorter.class);
        BufferedExternalSorter testSorter = new BufferedExternalSorter(mockExternalSorter, mockInMemorySorter);
        KV<byte[], byte[]>[] kvs = new KV[]{ KV.of(new byte[]{ 0 }, new byte[]{  }), KV.of(new byte[]{ 0, 1 }, new byte[]{  }), KV.of(new byte[]{ 1 }, new byte[]{  }) };
        Mockito.when(mockInMemorySorter.addIfRoom(kvs[0])).thenReturn(true);
        Mockito.when(mockInMemorySorter.addIfRoom(kvs[1])).thenReturn(true);
        Mockito.when(mockInMemorySorter.addIfRoom(kvs[2])).thenReturn(true);
        Mockito.when(mockInMemorySorter.sort()).thenReturn(Arrays.asList(kvs[0], kvs[1], kvs[2]));
        testSorter.add(kvs[0]);
        testSorter.add(kvs[1]);
        testSorter.add(kvs[2]);
        Assert.assertEquals(Arrays.asList(kvs[0], kvs[1], kvs[2]), testSorter.sort());
        // Verify external sorter was never called
        Mockito.verify(mockExternalSorter, Mockito.never()).add(ArgumentMatchers.any(KV.class));
        Mockito.verify(mockExternalSorter, Mockito.never()).sort();
    }

    @Test
    public void testFallback() throws Exception {
        ExternalSorter mockExternalSorter = Mockito.mock(ExternalSorter.class);
        InMemorySorter mockInMemorySorter = Mockito.mock(InMemorySorter.class);
        BufferedExternalSorter testSorter = new BufferedExternalSorter(mockExternalSorter, mockInMemorySorter);
        @SuppressWarnings("unchecked")
        KV<byte[], byte[]>[] kvs = new KV[]{ KV.of(new byte[]{ 0 }, new byte[]{  }), KV.of(new byte[]{ 0, 1 }, new byte[]{  }), KV.of(new byte[]{ 1 }, new byte[]{  }) };
        Mockito.when(mockInMemorySorter.addIfRoom(kvs[0])).thenReturn(true);
        Mockito.when(mockInMemorySorter.addIfRoom(kvs[1])).thenReturn(true);
        Mockito.when(mockInMemorySorter.addIfRoom(kvs[2])).thenReturn(false);
        Mockito.when(mockInMemorySorter.sort()).thenReturn(Arrays.asList(kvs[0], kvs[1]));
        Mockito.when(mockExternalSorter.sort()).thenReturn(Arrays.asList(kvs[0], kvs[1], kvs[2]));
        testSorter.add(kvs[0]);
        testSorter.add(kvs[1]);
        testSorter.add(kvs[2]);
        Assert.assertEquals(Arrays.asList(kvs[0], kvs[1], kvs[2]), testSorter.sort());
        Mockito.verify(mockExternalSorter, Mockito.times(1)).add(kvs[0]);
        Mockito.verify(mockExternalSorter, Mockito.times(1)).add(kvs[1]);
        Mockito.verify(mockExternalSorter, Mockito.times(1)).add(kvs[2]);
    }

    @Test
    public void testEmpty() throws Exception {
        SorterTestUtils.testEmpty(BufferedExternalSorter.create(BufferedExternalSorter.options().withTempLocation(BufferedExternalSorterTest.tmpLocation.toString())));
    }

    @Test
    public void testSingleElement() throws Exception {
        SorterTestUtils.testSingleElement(BufferedExternalSorter.create(BufferedExternalSorter.options().withTempLocation(BufferedExternalSorterTest.tmpLocation.toString())));
    }

    @Test
    public void testEmptyKeyValueElement() throws Exception {
        SorterTestUtils.testEmptyKeyValueElement(BufferedExternalSorter.create(BufferedExternalSorter.options().withTempLocation(BufferedExternalSorterTest.tmpLocation.toString())));
    }

    @Test
    public void testMultipleIterations() throws Exception {
        SorterTestUtils.testMultipleIterations(BufferedExternalSorter.create(BufferedExternalSorter.options().withTempLocation(BufferedExternalSorterTest.tmpLocation.toString())));
    }

    @Test
    public void testManySortersFewRecords() throws Exception {
        SorterTestUtils.testRandom(() -> BufferedExternalSorter.create(BufferedExternalSorter.options().withTempLocation(BufferedExternalSorterTest.tmpLocation.toString())), 1000000, 10);
    }

    @Test
    public void testOneSorterManyRecords() throws Exception {
        SorterTestUtils.testRandom(() -> BufferedExternalSorter.create(BufferedExternalSorter.options().withTempLocation(BufferedExternalSorterTest.tmpLocation.toString())), 1, 1000000);
    }

    @Test
    public void testAddAfterSort() throws Exception {
        SorterTestUtils.testAddAfterSort(BufferedExternalSorter.create(BufferedExternalSorter.options().withTempLocation(BufferedExternalSorterTest.tmpLocation.toString())), thrown);
        Assert.fail();
    }

    @Test
    public void testSortTwice() throws Exception {
        SorterTestUtils.testSortTwice(BufferedExternalSorter.create(BufferedExternalSorter.options().withTempLocation(BufferedExternalSorterTest.tmpLocation.toString())), thrown);
        Assert.fail();
    }

    @Test
    public void testNegativeMemory() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("memoryMB must be greater than zero");
        BufferedExternalSorter.Options options = BufferedExternalSorter.options().withTempLocation(BufferedExternalSorterTest.tmpLocation.toString());
        options.withMemoryMB((-1));
    }

    @Test
    public void testZeroMemory() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("memoryMB must be greater than zero");
        BufferedExternalSorter.Options options = BufferedExternalSorter.options();
        options.withMemoryMB(0);
    }

    @Test
    public void testMemoryTooLarge() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("memoryMB must be less than 2048");
        BufferedExternalSorter.Options options = BufferedExternalSorter.options();
        options.withMemoryMB(2048);
    }
}

