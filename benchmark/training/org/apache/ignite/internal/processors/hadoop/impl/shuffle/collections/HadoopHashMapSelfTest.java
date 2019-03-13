/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.hadoop.impl.shuffle.collections;


import HadoopMultimap.Adder;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.Random;
import org.apache.hadoop.io.IntWritable;
import org.apache.ignite.internal.processors.hadoop.HadoopTaskContext;
import org.apache.ignite.internal.processors.hadoop.shuffle.collections.HadoopHashMultimap;
import org.apache.ignite.internal.processors.hadoop.shuffle.collections.HadoopMultimap;
import org.apache.ignite.internal.util.offheap.unsafe.GridUnsafeMemory;
import org.apache.ignite.internal.util.typedef.X;
import org.junit.Test;


/**
 *
 */
public class HadoopHashMapSelfTest extends HadoopAbstractMapTest {
    /**
     * Test simple map.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMapSimple() throws Exception {
        GridUnsafeMemory mem = new GridUnsafeMemory(0);
        Random rnd = new Random();
        int mapSize = 16 << (rnd.nextInt(3));
        HadoopTaskContext taskCtx = new HadoopAbstractMapTest.TaskContext();
        final HadoopHashMultimap m = new HadoopHashMultimap(new HadoopAbstractMapTest.JobInfo(), mem, mapSize);
        HadoopMultimap.Adder a = m.startAdding(taskCtx);
        Multimap<Integer, Integer> mm = ArrayListMultimap.create();
        for (int i = 0, vals = (4 * mapSize) + (rnd.nextInt(25)); i < vals; i++) {
            int key = rnd.nextInt(mapSize);
            int val = rnd.nextInt();
            a.write(new IntWritable(key), new IntWritable(val));
            mm.put(key, val);
            X.println(((("k: " + key) + " v: ") + val));
            a.close();
            check(m, mm, taskCtx);
            a = m.startAdding(taskCtx);
        }
        // a.add(new IntWritable(10), new IntWritable(2));
        // mm.put(10, 2);
        // check(m, mm);
        a.close();
        X.println(("Alloc: " + (mem.allocatedSize())));
        m.close();
        assertEquals(0, mem.allocatedSize());
    }
}

