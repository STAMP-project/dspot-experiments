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
package org.apache.geode.cache.query.internal.index;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.apache.geode.cache.query.MultithreadedTester;
import org.junit.Assert;
import org.junit.Test;


public class IndexElemArrayJUnitTest {
    private IndexElemArray list;

    @Test
    public void testFunctionality() throws Exception {
        boundaryCondition();
        add();
        clearAndAdd();
        removeFirst();
        clearAndAdd();
        removeLast();
        clearAndAdd();
        remove();
        clearAndAdd();
        iterate();
        clearAndAdd();
    }

    @Test
    public void overflowFromAddThrowsException() {
        // no exception should be thrown by this loop
        for (int i = 1; i < 256; i++) {
            list.add(i);
        }
        try {
            list.add(256);
            Assert.fail("list should have thrown an exception when full");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    @Test(expected = IllegalStateException.class)
    public void overflowFromAddAllThrowsException() {
        Object[] array = new Object[256];
        Arrays.fill(array, new Object());
        List<Object> data = Arrays.asList(array);
        list.addAll(data);
    }

    @Test
    public void sizeAfterOverflowFromAddIsCorrect() {
        for (int i = 1; i < 256; i++) {
            list.add(i);
        }
        try {
            list.add(256);
        } catch (IllegalStateException e) {
            assertThat(list.size()).isEqualTo(255);
        }
    }

    @Test
    public void sizeAfterOverflowFromAddAllIsCorrect() {
        for (int i = 1; i < 256; i++) {
            list.add(i);
        }
        try {
            list.addAll(Collections.singleton(new Object()));
        } catch (IllegalStateException e) {
            assertThat(list.size()).isEqualTo(255);
        }
    }

    @Test
    public void listCanBeIteratedOverFullRange() {
        for (int i = 1; i < 256; i++) {
            list.add(i);
        }
        for (int i = 1; i < 256; i++) {
            assertThat(list.get((i - 1))).isEqualTo(i);
        }
    }

    /**
     * This tests concurrent modification of IndexElemArray and to make sure elementData and size are
     * updated atomically. Ticket# GEODE-106.
     */
    @Test
    public void testFunctionalityUsingMultiThread() throws Exception {
        Collection<Callable> callables = new ConcurrentLinkedQueue<>();
        IntStream.range(0, 1000).parallel().forEach(( i) -> {
            callables.add(() -> {
                if ((i % 3) == 0) {
                    return add(Integer.valueOf(new Random().nextInt(4)));
                } else
                    if ((i % 3) == 1) {
                        return remove(Integer.valueOf(new Random().nextInt(4)));
                    } else {
                        return iterateList();
                    }

            });
        });
        Collection<Object> results = MultithreadedTester.runMultithreaded(callables);
        results.forEach(( result) -> {
            // There should not be any Exception here.
            // E.g. ArrayIndexOutOfBoundsException when multiple threads are acting.
            Assert.assertTrue(((result.getClass().getName()) + " was not an expected result"), (result instanceof Integer));
        });
    }
}

