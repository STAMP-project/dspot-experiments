/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.utils;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * A unit test for ImplicitLinkedHashMultiSet.
 */
public class ImplicitLinkedHashMultiSetTest {
    @Rule
    public final Timeout globalTimeout = Timeout.millis(120000);

    @Test
    public void testNullForbidden() {
        ImplicitLinkedHashMultiSet<ImplicitLinkedHashSetTest.TestElement> multiSet = new ImplicitLinkedHashMultiSet();
        Assert.assertFalse(multiSet.add(null));
    }

    @Test
    public void testInsertDelete() {
        ImplicitLinkedHashMultiSet<ImplicitLinkedHashSetTest.TestElement> multiSet = new ImplicitLinkedHashMultiSet(100);
        ImplicitLinkedHashSetTest.TestElement e1 = new ImplicitLinkedHashSetTest.TestElement(1);
        ImplicitLinkedHashSetTest.TestElement e2 = new ImplicitLinkedHashSetTest.TestElement(1);
        ImplicitLinkedHashSetTest.TestElement e3 = new ImplicitLinkedHashSetTest.TestElement(2);
        multiSet.mustAdd(e1);
        multiSet.mustAdd(e2);
        multiSet.mustAdd(e3);
        Assert.assertFalse(multiSet.add(e3));
        Assert.assertEquals(3, multiSet.size());
        ImplicitLinkedHashMultiSetTest.expectExactTraversal(multiSet.findAll(e1).iterator(), e1, e2);
        ImplicitLinkedHashMultiSetTest.expectExactTraversal(multiSet.findAll(e3).iterator(), e3);
        multiSet.remove(e2);
        ImplicitLinkedHashMultiSetTest.expectExactTraversal(multiSet.findAll(e1).iterator(), e1);
        Assert.assertTrue(multiSet.contains(e2));
    }

    @Test
    public void testTraversal() {
        ImplicitLinkedHashMultiSet<ImplicitLinkedHashSetTest.TestElement> multiSet = new ImplicitLinkedHashMultiSet();
        ImplicitLinkedHashMultiSetTest.expectExactTraversal(multiSet.iterator());
        ImplicitLinkedHashSetTest.TestElement e1 = new ImplicitLinkedHashSetTest.TestElement(1);
        ImplicitLinkedHashSetTest.TestElement e2 = new ImplicitLinkedHashSetTest.TestElement(1);
        ImplicitLinkedHashSetTest.TestElement e3 = new ImplicitLinkedHashSetTest.TestElement(2);
        Assert.assertTrue(multiSet.add(e1));
        Assert.assertTrue(multiSet.add(e2));
        Assert.assertTrue(multiSet.add(e3));
        ImplicitLinkedHashMultiSetTest.expectExactTraversal(multiSet.iterator(), e1, e2, e3);
        Assert.assertTrue(multiSet.remove(e2));
        ImplicitLinkedHashMultiSetTest.expectExactTraversal(multiSet.iterator(), e1, e3);
        Assert.assertTrue(multiSet.remove(e1));
        ImplicitLinkedHashMultiSetTest.expectExactTraversal(multiSet.iterator(), e3);
    }

    @Test
    public void testEnlargement() {
        ImplicitLinkedHashMultiSet<ImplicitLinkedHashSetTest.TestElement> multiSet = new ImplicitLinkedHashMultiSet(5);
        Assert.assertEquals(11, multiSet.numSlots());
        ImplicitLinkedHashSetTest.TestElement[] testElements = new ImplicitLinkedHashSetTest.TestElement[]{ new ImplicitLinkedHashSetTest.TestElement(100), new ImplicitLinkedHashSetTest.TestElement(101), new ImplicitLinkedHashSetTest.TestElement(102), new ImplicitLinkedHashSetTest.TestElement(100), new ImplicitLinkedHashSetTest.TestElement(101), new ImplicitLinkedHashSetTest.TestElement(105) };
        for (int i = 0; i < (testElements.length); i++) {
            Assert.assertTrue(multiSet.add(testElements[i]));
        }
        for (int i = 0; i < (testElements.length); i++) {
            Assert.assertFalse(multiSet.add(testElements[i]));
        }
        Assert.assertEquals(23, multiSet.numSlots());
        Assert.assertEquals(testElements.length, multiSet.size());
        ImplicitLinkedHashMultiSetTest.expectExactTraversal(multiSet.iterator(), testElements);
        multiSet.remove(testElements[1]);
        Assert.assertEquals(23, multiSet.numSlots());
        Assert.assertEquals(5, multiSet.size());
        ImplicitLinkedHashMultiSetTest.expectExactTraversal(multiSet.iterator(), testElements[0], testElements[2], testElements[3], testElements[4], testElements[5]);
    }

    @Test
    public void testManyInsertsAndDeletes() {
        Random random = new Random(123);
        LinkedList<ImplicitLinkedHashSetTest.TestElement> existing = new LinkedList<>();
        ImplicitLinkedHashMultiSet<ImplicitLinkedHashSetTest.TestElement> multiSet = new ImplicitLinkedHashMultiSet();
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 4; j++) {
                ImplicitLinkedHashSetTest.TestElement testElement = new ImplicitLinkedHashSetTest.TestElement(random.nextInt());
                multiSet.mustAdd(testElement);
                existing.add(testElement);
            }
            int elementToRemove = random.nextInt(multiSet.size());
            Iterator<ImplicitLinkedHashSetTest.TestElement> iter1 = multiSet.iterator();
            Iterator<ImplicitLinkedHashSetTest.TestElement> iter2 = existing.iterator();
            for (int j = 0; j <= elementToRemove; j++) {
                iter1.next();
                iter2.next();
            }
            iter1.remove();
            iter2.remove();
            expectTraversal(multiSet.iterator(), existing.iterator());
        }
    }
}

