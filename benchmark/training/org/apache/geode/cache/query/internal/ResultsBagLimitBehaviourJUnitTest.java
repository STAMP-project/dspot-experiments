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
package org.apache.geode.cache.query.internal;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.geode.cache.query.types.ObjectType;
import org.apache.geode.cache.query.types.StructType;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test ResultsBag Limit behaviour
 *
 * TODO: Test for null behaviour in various functions
 */
public class ResultsBagLimitBehaviourJUnitTest {
    @Test
    public void testAsListAndAsSetMethod() {
        ResultsBag bag = getBagObject(String.class);
        bag.add(wrap("one", bag.getCollectionType().getElementType()));
        bag.add(wrap("two", bag.getCollectionType().getElementType()));
        bag.add(wrap("three", bag.getCollectionType().getElementType()));
        bag.add(wrap("four", bag.getCollectionType().getElementType()));
        Assert.assertEquals(4, bag.size());
        bag.applyLimit(2);
        List list = bag.asList();
        Assert.assertEquals(2, list.size());
        Set set = bag.asSet();
        Assert.assertEquals(2, set.size());
    }

    @Test
    public void testOccurrence() {
        ResultsBag bag = getBagObject(String.class);
        bag.add(wrap(null, bag.getCollectionType().getElementType()));
        bag.add(wrap(null, bag.getCollectionType().getElementType()));
        bag.add(wrap("one", bag.getCollectionType().getElementType()));
        bag.add(wrap("two", bag.getCollectionType().getElementType()));
        bag.add(wrap("two", bag.getCollectionType().getElementType()));
        bag.add(wrap("three", bag.getCollectionType().getElementType()));
        bag.add(wrap("three", bag.getCollectionType().getElementType()));
        bag.add(wrap("three", bag.getCollectionType().getElementType()));
        bag.add(wrap("four", bag.getCollectionType().getElementType()));
        bag.add(wrap("four", bag.getCollectionType().getElementType()));
        bag.add(wrap("four", bag.getCollectionType().getElementType()));
        bag.add(wrap("four", bag.getCollectionType().getElementType()));
        Assert.assertEquals(12, bag.size());
        bag.applyLimit(6);
        int total = bag.occurrences(wrap(null, bag.getCollectionType().getElementType()));
        total += bag.occurrences(wrap("one", bag.getCollectionType().getElementType()));
        total += bag.occurrences(wrap("two", bag.getCollectionType().getElementType()));
        total += bag.occurrences(wrap("three", bag.getCollectionType().getElementType()));
        total += bag.occurrences(wrap("four", bag.getCollectionType().getElementType()));
        Assert.assertEquals(6, total);
    }

    @Test
    public void testIteratorType() {
        ResultsBag bag = getBagObject(String.class);
        bag.add(wrap("one", bag.getCollectionType().getElementType()));
        bag.add(wrap("two", bag.getCollectionType().getElementType()));
        bag.add(wrap("two", bag.getCollectionType().getElementType()));
        bag.add(wrap("three", bag.getCollectionType().getElementType()));
        bag.add(wrap("three", bag.getCollectionType().getElementType()));
        Assert.assertTrue(((bag.iterator()) instanceof Bag.BagIterator));
        bag.applyLimit(6);
        Assert.assertTrue(((bag.iterator()) instanceof Bag.BagIterator));
        bag = getBagObject(String.class);
        bag.add(wrap("one", bag.getCollectionType().getElementType()));
        bag.add(wrap("two", bag.getCollectionType().getElementType()));
        bag.add(wrap("two", bag.getCollectionType().getElementType()));
        bag.add(wrap("three", bag.getCollectionType().getElementType()));
        bag.add(wrap("three", bag.getCollectionType().getElementType()));
        bag.add(wrap("three", bag.getCollectionType().getElementType()));
        bag.add(wrap("four", bag.getCollectionType().getElementType()));
        bag.add(wrap("four", bag.getCollectionType().getElementType()));
        bag.add(wrap("four", bag.getCollectionType().getElementType()));
        bag.add(wrap("four", bag.getCollectionType().getElementType()));
        bag.applyLimit(6);
        if (!(bag instanceof StructBag)) {
            Assert.assertTrue(((bag.iterator()) instanceof Bag.LimitBagIterator));
        }
    }

    @Test
    public void testContains() {
        ResultsBag bag = getBagObject(Integer.class);
        bag.add(wrap(new Integer(1), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(2), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(3), bag.getCollectionType().getElementType()));
        bag.add(wrap(null, bag.getCollectionType().getElementType()));
        bag.add(wrap(null, bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(4), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(5), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(6), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(7), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(8), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(9), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(10), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(11), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(12), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(13), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(14), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(15), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(16), bag.getCollectionType().getElementType()));
        bag.applyLimit(6);
        List temp = bag.asList();
        Assert.assertEquals(6, bag.size());
        for (int i = 1; i < 17; ++i) {
            Integer intg = new Integer(i);
            Assert.assertTrue(((temp.contains(wrap(intg, bag.getCollectionType().getElementType()))) == (bag.contains(wrap(intg, bag.getCollectionType().getElementType())))));
        }
        Assert.assertTrue(((temp.contains(wrap(null, bag.getCollectionType().getElementType()))) == (bag.contains(wrap(null, bag.getCollectionType().getElementType())))));
    }

    @Test
    public void testAddExceptionIfLimitApplied() {
        ResultsBag bag = getBagObject(String.class);
        bag.add(wrap("one", bag.getCollectionType().getElementType()));
        bag.add(wrap("two", bag.getCollectionType().getElementType()));
        bag.add(wrap("two", bag.getCollectionType().getElementType()));
        bag.applyLimit(6);
        try {
            bag.add(wrap("four", bag.getCollectionType().getElementType()));
            Assert.fail("Addition to bag with limit applied should have failed");
        } catch (UnsupportedOperationException soe) {
            // Expected exception
        }
    }

    // Internal method AddAndGetOccurrence used for iter evaluating
    // only up till the limit
    @Test
    public void testAddAndGetOccurrence() {
        ResultsBag bag = getBagObject(String.class);
        bag = getBagObject(String.class);
        ObjectType elementType = bag.getCollectionType().getElementType();
        Assert.assertEquals(1, bag.addAndGetOccurence((elementType instanceof StructType ? getFieldValues() : wrap("one", elementType))));
        bag.add(wrap("two", elementType));
        Assert.assertEquals(2, bag.addAndGetOccurence((elementType instanceof StructType ? getFieldValues() : wrap("two", bag.getCollectionType().getElementType()))));
        bag.add(wrap("three", bag.getCollectionType().getElementType()));
        bag.add(wrap("three", bag.getCollectionType().getElementType()));
        Assert.assertEquals(3, bag.addAndGetOccurence((elementType instanceof StructType ? getFieldValues() : wrap("three", elementType))));
        bag.add(wrap(null, bag.getCollectionType().getElementType()));
        bag.add(wrap(null, bag.getCollectionType().getElementType()));
        Assert.assertEquals(3, bag.addAndGetOccurence((elementType instanceof StructType ? getFieldValues() : wrap(null, elementType))));
    }

    @Test
    public void testSizeWithLimitApplied() {
        ResultsBag bag = getBagObject(String.class);
        bag.add(wrap("two", bag.getCollectionType().getElementType()));
        bag.add(wrap("three", bag.getCollectionType().getElementType()));
        bag.add(wrap("three", bag.getCollectionType().getElementType()));
        bag.add(wrap("three", bag.getCollectionType().getElementType()));
        bag.add(wrap("three", bag.getCollectionType().getElementType()));
        Assert.assertEquals(5, bag.size());
        // Limit less than actual size
        bag.applyLimit(3);
        Assert.assertEquals(3, bag.size());
        bag = getBagObject(String.class);
        bag.add(wrap("two", bag.getCollectionType().getElementType()));
        bag.add(wrap("three", bag.getCollectionType().getElementType()));
        bag.applyLimit(7);
        Assert.assertEquals(2, bag.size());
    }

    @Test
    public void testRemove() {
        // Test when actual size in resultset is less than the limit
        ResultsBag bag = getBagObject(String.class);
        bag.add(wrap(null, bag.getCollectionType().getElementType()));
        bag.add(wrap(null, bag.getCollectionType().getElementType()));
        bag.add(wrap("one", bag.getCollectionType().getElementType()));
        bag.add(wrap("two", bag.getCollectionType().getElementType()));
        bag.add(wrap("two", bag.getCollectionType().getElementType()));
        bag.add(wrap("three", bag.getCollectionType().getElementType()));
        bag.add(wrap("three", bag.getCollectionType().getElementType()));
        bag.add(wrap("three", bag.getCollectionType().getElementType()));
        bag.add(wrap("four", bag.getCollectionType().getElementType()));
        bag.add(wrap("four", bag.getCollectionType().getElementType()));
        bag.add(wrap("four", bag.getCollectionType().getElementType()));
        bag.add(wrap("four", bag.getCollectionType().getElementType()));
        bag.applyLimit(15);
        Assert.assertEquals(12, bag.size());
        bag.remove(wrap(null, bag.getCollectionType().getElementType()));
        Assert.assertEquals(11, bag.size());
        Assert.assertEquals(1, bag.occurrences(wrap(null, bag.getCollectionType().getElementType())));
        bag.remove(wrap("three", bag.getCollectionType().getElementType()));
        Assert.assertEquals(10, bag.size());
        Assert.assertEquals(2, bag.occurrences(wrap("three", bag.getCollectionType().getElementType())));
        // Test when the actual size is more than the limit
        bag = getBagObject(Integer.class);
        bag.add(wrap(null, bag.getCollectionType().getElementType()));
        bag.add(wrap(null, bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(1), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(2), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(2), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(3), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(3), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(3), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(4), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(4), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(4), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(4), bag.getCollectionType().getElementType()));
        bag.applyLimit(8);
        List temp = bag.asList();
        int currSize = 8;
        Assert.assertEquals(currSize, 8);
        for (int i = 1; i < 5; ++i) {
            Integer intg = new Integer(i);
            if (temp.contains(wrap(intg, bag.getCollectionType().getElementType()))) {
                int occurrence = bag.occurrences(wrap(intg, bag.getCollectionType().getElementType()));
                Assert.assertTrue(bag.remove(wrap(intg, bag.getCollectionType().getElementType())));
                Assert.assertEquals((--occurrence), bag.occurrences(wrap(intg, bag.getCollectionType().getElementType())));
                --currSize;
                Assert.assertEquals(currSize, bag.size());
            } else {
                Assert.assertEquals(0, bag.occurrences(wrap(intg, bag.getCollectionType().getElementType())));
            }
        }
        if (temp.contains(wrap(null, bag.getCollectionType().getElementType()))) {
            int occurrence = bag.occurrences(wrap(null, bag.getCollectionType().getElementType()));
            Assert.assertTrue(bag.remove(wrap(null, bag.getCollectionType().getElementType())));
            Assert.assertEquals((--occurrence), bag.occurrences(wrap(null, bag.getCollectionType().getElementType())));
            --currSize;
            Assert.assertEquals(currSize, bag.size());
        }
        // Test null removal
        bag = getBagObject(Object.class);
        for (int i = 0; i < 20; ++i) {
            bag.add(wrap(null, bag.getCollectionType().getElementType()));
        }
        bag.applyLimit(4);
        for (int i = 0; i < 3; ++i) {
            bag.remove(wrap(null, bag.getCollectionType().getElementType()));
        }
        Assert.assertEquals(1, bag.size());
        Assert.assertTrue(bag.contains(wrap(null, bag.getCollectionType().getElementType())));
        Iterator itr = bag.iterator();
        Assert.assertEquals(wrap(null, bag.getCollectionType().getElementType()), itr.next());
        Assert.assertFalse(itr.hasNext());
        bag.remove(wrap(null, bag.getCollectionType().getElementType()));
        Assert.assertEquals(0, bag.size());
        Assert.assertFalse(bag.contains(wrap(null, bag.getCollectionType().getElementType())));
        itr = bag.iterator();
        Assert.assertFalse(itr.hasNext());
    }

    @Test
    public void testAddAllExceptionIfLimitApplied() {
        ResultsBag bag = getBagObject(Object.class);
        bag.applyLimit(6);
        try {
            bag.addAll(new HashSet());
            Assert.fail("addAll invocation to bag with limit applied should have failed");
        } catch (UnsupportedOperationException soe) {
            // Expected exception
        }
    }

    @Test
    public void testToDataFromData() throws Exception {
        // Test with limit specified & limit less than internal size
        ResultsBag toBag = getBagObject(String.class);
        toBag.add(wrap(null, toBag.getCollectionType().getElementType()));
        toBag.add(wrap(null, toBag.getCollectionType().getElementType()));
        toBag.add(wrap(null, toBag.getCollectionType().getElementType()));
        toBag.add(wrap("one", toBag.getCollectionType().getElementType()));
        toBag.add(wrap("two", toBag.getCollectionType().getElementType()));
        toBag.add(wrap("two", toBag.getCollectionType().getElementType()));
        toBag.add(wrap("three", toBag.getCollectionType().getElementType()));
        toBag.add(wrap("three", toBag.getCollectionType().getElementType()));
        toBag.add(wrap("three", toBag.getCollectionType().getElementType()));
        toBag.add(wrap("four", toBag.getCollectionType().getElementType()));
        toBag.add(wrap("four", toBag.getCollectionType().getElementType()));
        toBag.add(wrap("four", toBag.getCollectionType().getElementType()));
        toBag.add(wrap("four", toBag.getCollectionType().getElementType()));
        toBag.applyLimit(9);
        Assert.assertEquals(9, toBag.size());
        ByteArrayOutputStream baos = new ByteArrayOutputStream(10240);
        DataOutputStream dos = new DataOutputStream(baos);
        toBag.toData(dos);
        byte[] data = baos.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bis);
        // Create a From ResultBag
        ResultsBag fromBag = getBagObject(String.class);
        fromBag.fromData(dis);
        Assert.assertEquals(toBag.size(), fromBag.size());
        Assert.assertEquals(toBag.occurrences(wrap(null, toBag.getCollectionType().getElementType())), fromBag.occurrences(wrap(null, fromBag.getCollectionType().getElementType())));
        Assert.assertEquals(toBag.occurrences(wrap("one", toBag.getCollectionType().getElementType())), fromBag.occurrences(wrap("one", fromBag.getCollectionType().getElementType())));
        Assert.assertEquals(toBag.occurrences(wrap("two", toBag.getCollectionType().getElementType())), fromBag.occurrences(wrap("two", fromBag.getCollectionType().getElementType())));
        Assert.assertEquals(toBag.occurrences(wrap("three", toBag.getCollectionType().getElementType())), fromBag.occurrences(wrap("three", fromBag.getCollectionType().getElementType())));
        Assert.assertEquals(toBag.occurrences(wrap("four", toBag.getCollectionType().getElementType())), fromBag.occurrences(wrap("four", fromBag.getCollectionType().getElementType())));
        Assert.assertEquals(toBag.occurrences(wrap(null, toBag.getCollectionType().getElementType())), fromBag.occurrences(wrap(null, fromBag.getCollectionType().getElementType())));
        Assert.assertFalse(toBag.asList().retainAll(fromBag.asList()));
    }

    @Test
    public void testLimitResultsBagIterator_1() {
        ResultsBag bag = getBagObject(Integer.class);
        bag.add(wrap(null, bag.getCollectionType().getElementType()));
        bag.add(wrap(null, bag.getCollectionType().getElementType()));
        bag.add(wrap(null, bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(1), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(2), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(2), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(3), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(3), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(3), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(4), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(4), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(4), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(4), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(5), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(5), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(5), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(5), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(5), bag.getCollectionType().getElementType()));
        bag.applyLimit(8);
        Iterator itr = bag.iterator();
        if (!(bag instanceof StructBag)) {
            Assert.assertTrue(((bag.iterator()) instanceof Bag.LimitBagIterator));
        }
        List asList = bag.asList();
        // Check repetition
        int i = 0;
        while (itr.hasNext()) {
            Assert.assertEquals(itr.next(), asList.get((i++)));
        } 
        // Remvove first 5. This means after the completion of iteration the
        // size should be 3
        i = 0;
        itr = bag.iterator();
        while (i < 5) {
            itr.next();
            itr.remove();
            ++i;
        } 
        Assert.assertEquals(3, bag.size());
        i = 0;
        itr = bag.iterator();
        while (i < 3) {
            Assert.assertEquals(asList.get((i + 5)), itr.next());
            ++i;
        } 
    }

    @Test
    public void testLimitResultsBagIterator_2() {
        ResultsBag bag = getBagObject(Object.class);
        for (int i = 0; i < 20; ++i) {
            bag.add(wrap(null, bag.getCollectionType().getElementType()));
        }
        bag.applyLimit(8);
        Iterator itr = bag.iterator();
        if (!(bag instanceof StructBag)) {
            Assert.assertTrue(((bag.iterator()) instanceof Bag.LimitBagIterator));
        }
        List asList = bag.asList();
        // Check repetition
        int i = 0;
        while (itr.hasNext()) {
            Assert.assertEquals(itr.next(), asList.get((i++)));
        } 
        // Remvove first 5. This means after the completion of iteration the
        // size should be 3
        i = 0;
        itr = bag.iterator();
        while (i < 5) {
            itr.next();
            itr.remove();
            ++i;
        } 
        Assert.assertEquals(3, bag.size());
        i = 0;
        itr = bag.iterator();
        if (!(bag instanceof StructBag)) {
            Assert.assertTrue(((bag.iterator()) instanceof Bag.LimitBagIterator));
        }
        while (itr.hasNext()) {
            Assert.assertEquals(asList.get((i + 5)), itr.next());
            ++i;
        } 
        Assert.assertEquals(i, 3);
    }

    @Test
    public void testValidExceptionThrown() {
        ResultsBag bag = getBagObject(Integer.class);
        bag.add(wrap(new Integer(1), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(2), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(2), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(3), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(3), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(4), bag.getCollectionType().getElementType()));
        bag.applyLimit(3);
        Assert.assertEquals(3, bag.size());
        Iterator itr = bag.iterator();
        try {
            itr.remove();
            Assert.fail("should have thrown a IllegalStateException");
        } catch (IllegalStateException e) {
            // pass
        }
        for (int i = 0; i < 3; i++) {
            Object n = itr.next();
        }
        try {
            itr.next();
            Assert.fail("should have thrown a NoSuchElementException");
        } catch (NoSuchElementException e) {
            // pass
        }
        // test with removes
        itr = bag.iterator();
        for (int i = 0; i < 3; i++) {
            Object n = itr.next();
            itr.remove();
        }
        Assert.assertEquals(0, bag.size());
        try {
            itr.next();
            Assert.fail("should have thrown a NoSuchElementException");
        } catch (NoSuchElementException e) {
            // pass
        }
    }

    @Test
    public void testRemoveAll() {
        ResultsBag bag = getBagObject(Integer.class);
        // Add Integer & null Objects
        bag.add(wrap(new Integer(1), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(2), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(2), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(3), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(3), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(4), bag.getCollectionType().getElementType()));
        bag.add(wrap(null, bag.getCollectionType().getElementType()));
        bag.add(wrap(null, bag.getCollectionType().getElementType()));
        bag.applyLimit(4);
        List asList = bag.asList();
        asList.add(wrap(new Integer(13), bag.getCollectionType().getElementType()));
        Assert.assertEquals(4, bag.size());
        Assert.assertEquals(5, asList.size());
        // Remove all the elements from the list which match the
        // first element pf the list and also
        // Get the number of occurnce of first element of List, in the bag
        int occurrence = bag.occurrences(asList.get(0));
        // Now remove the this element from the list totally
        Object toRemove = asList.get(0);
        for (int i = 0; i < occurrence; ++i) {
            asList.remove(toRemove);
        }
        // So we have added one element in thje list which does not exist in the bag
        // and removeed one element from list which exists in the bag.
        bag.removeAll(asList);
        Assert.assertEquals(occurrence, bag.size());
        Iterator itr = bag.iterator();
        for (int i = 0; i < occurrence; ++i) {
            itr.next();
        }
        Assert.assertFalse(itr.hasNext());
    }

    @Test
    public void testRetainAll() {
        ResultsBag bag = getBagObject(Integer.class);
        // Add Integer & null Objects
        bag.add(wrap(new Integer(1), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(2), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(2), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(3), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(3), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(4), bag.getCollectionType().getElementType()));
        bag.add(wrap(null, bag.getCollectionType().getElementType()));
        bag.add(wrap(null, bag.getCollectionType().getElementType()));
        bag.applyLimit(4);
        List asList = bag.asList();
        // Asif :Just add an arbit data which is not contained in the bag
        asList.add(wrap(new Integer(13), bag.getCollectionType().getElementType()));
        bag.retainAll(asList);
        Assert.assertEquals(4, bag.size());
        Assert.assertEquals(5, asList.size());
        // Remove all the elements from the list which match the
        // first element pf the list and also
        // Get the number of occurnce of first element of List, in the bag
        int occurrence = bag.occurrences(asList.get(0));
        // Now remove the this element from the list totally
        Object toRemove = asList.get(0);
        for (int i = 0; i < occurrence; ++i) {
            asList.remove(toRemove);
        }
        // So we have added one element in thje list which does not exist in the bag
        // and removeed one element from list which exists in the bag.
        bag.retainAll(asList);
        Assert.assertEquals((4 - occurrence), bag.size());
        Iterator itr = bag.iterator();
        for (int i = 0; i < (4 - occurrence); ++i) {
            itr.next();
        }
        Assert.assertFalse(itr.hasNext());
    }

    @Test
    public void testContainAll() {
        ResultsBag bag = getBagObject(Integer.class);
        // Add Integer & null Objects
        bag.add(wrap(new Integer(1), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(2), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(2), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(3), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(3), bag.getCollectionType().getElementType()));
        bag.add(wrap(new Integer(4), bag.getCollectionType().getElementType()));
        bag.add(wrap(null, bag.getCollectionType().getElementType()));
        bag.add(wrap(null, bag.getCollectionType().getElementType()));
        bag.applyLimit(4);
        List asList = bag.asList();
        // Asif :Just add an arbit data which is not contained in the bag
        // asList.add(wrap(new
        // Integer(13),bag.getCollectionType().getElementType()));
        Assert.assertEquals(4, bag.size());
        // assertIndexDetailsEquals(5,asList.size());
        // Remove all the elements from the list which match the
        // first element pf the list
        int occurrence = bag.occurrences(asList.get(0));
        // Now remove the this element from the list totally
        Object toRemove = asList.get(0);
        for (int i = 0; i < occurrence; ++i) {
            asList.remove(toRemove);
        }
        Assert.assertTrue(bag.containsAll(asList));
        // Asif :Just add an arbit data which is not contained in the bag
        asList.add(wrap(new Integer(13), bag.getCollectionType().getElementType()));
        Assert.assertFalse(bag.containsAll(asList));
    }
}

