/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.nd4j.linalg.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.collection.CompactHeapStringList;
import org.nd4j.linalg.factory.Nd4jBackend;


public class TestCollections extends BaseNd4jTest {
    public TestCollections(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testCompactHeapStringList() {
        int[] reallocSizeBytes = new int[]{ 1024, 1048576 };
        int[] intReallocSizeBytes = new int[]{ 1024, 1048576 };
        int numElementsToTest = 10000;
        int minLength = 1;
        int maxLength = 1048;
        Random r = new Random(12345);
        List<String> compare = new ArrayList<>(numElementsToTest);
        for (int i = 0; i < numElementsToTest; i++) {
            int thisLength = minLength + (r.nextInt(maxLength));
            char[] c = new char[thisLength];
            for (int j = 0; j < (c.length); j++) {
                c[j] = ((char) (r.nextInt(65536)));
            }
            String s = new String(c);
            compare.add(s);
        }
        for (int rb : reallocSizeBytes) {
            for (int irb : intReallocSizeBytes) {
                // System.out.println(rb + "\t" + irb);
                List<String> list = new CompactHeapStringList(rb, irb);
                Assert.assertTrue(list.isEmpty());
                Assert.assertEquals(0, list.size());
                for (int i = 0; i < numElementsToTest; i++) {
                    String s = compare.get(i);
                    list.add(s);
                    Assert.assertEquals((i + 1), list.size());
                    String s2 = list.get(i);
                    Assert.assertEquals(s, s2);
                }
                Assert.assertEquals(numElementsToTest, list.size());
                Assert.assertEquals(list, compare);
                Assert.assertEquals(compare, list);
                Assert.assertEquals(compare, Arrays.asList(list.toArray()));
                for (int i = 0; i < numElementsToTest; i++) {
                    Assert.assertEquals(i, list.indexOf(compare.get(i)));
                }
                Iterator<String> iter = list.iterator();
                int count = 0;
                while (iter.hasNext()) {
                    String s = iter.next();
                    Assert.assertEquals(s, compare.get((count++)));
                } 
                Assert.assertEquals(numElementsToTest, count);
            }
        }
    }
}

