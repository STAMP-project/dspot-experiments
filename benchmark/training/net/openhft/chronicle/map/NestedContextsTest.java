/**
 * Copyright 2012-2018 Chronicle Map Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.openhft.chronicle.map;


import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntConsumer;
import org.junit.Assert;
import org.junit.Test;


public class NestedContextsTest {
    @Test
    public void nestedContextsTest() throws InterruptedException, ExecutionException {
        HashSet<Integer> averageValue = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            averageValue.add(i);
        }
        ChronicleMap<Integer, Set<Integer>> graph = ChronicleMap.of(Integer.class, ((Class<Set<Integer>>) ((Class) (Set.class)))).entries(10).averageValue(averageValue).actualSegments(2).create();
        NestedContextsTest.addEdge(graph, 1, 2);
        NestedContextsTest.addEdge(graph, 2, 3);
        NestedContextsTest.addEdge(graph, 1, 3);
        Assert.assertEquals(ImmutableSet.of(2, 3), graph.get(1));
        Assert.assertEquals(ImmutableSet.of(1, 3), graph.get(2));
        Assert.assertEquals(ImmutableSet.of(1, 2), graph.get(3));
        NestedContextsTest.verifyGraphConsistent(graph);
        ForkJoinPool pool = new ForkJoinPool(8);
        try {
            pool.submit(() -> {
                ThreadLocalRandom.current().ints().limit(10000).parallel().forEach(( i) -> {
                    int sourceNode = Math.abs((i % 10));
                    int targetNode;
                    do {
                        targetNode = ThreadLocalRandom.current().nextInt(10);
                    } while (targetNode == sourceNode );
                    if ((i % 2) == 0) {
                        NestedContextsTest.addEdge(graph, sourceNode, targetNode);
                    } else {
                        NestedContextsTest.removeEdge(graph, sourceNode, targetNode);
                    }
                });
            }).get();
            NestedContextsTest.verifyGraphConsistent(graph);
        } finally {
            pool.shutdownNow();
        }
    }
}

