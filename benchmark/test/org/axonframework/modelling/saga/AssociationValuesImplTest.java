/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.modelling.saga;


import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class AssociationValuesImplTest {
    private AssociationValuesImpl testSubject;

    private AssociationValue associationValue;

    @Test
    public void testAddAssociationValue() {
        testSubject.add(associationValue);
        Assert.assertEquals(1, testSubject.addedAssociations().size());
        Assert.assertTrue(testSubject.removedAssociations().isEmpty());
    }

    @Test
    public void testAddAssociationValue_AddedTwice() {
        testSubject.add(associationValue);
        testSubject.commit();
        testSubject.add(associationValue);
        Assert.assertTrue(testSubject.addedAssociations().isEmpty());
        Assert.assertTrue(testSubject.removedAssociations().isEmpty());
    }

    @Test
    public void testRemoveAssociationValue() {
        Assert.assertTrue(testSubject.add(associationValue));
        testSubject.commit();
        Assert.assertTrue(testSubject.remove(associationValue));
        Assert.assertTrue(testSubject.addedAssociations().isEmpty());
        Assert.assertEquals(1, testSubject.removedAssociations().size());
    }

    @Test
    public void testRemoveAssociationValue_NotInContainer() {
        testSubject.remove(associationValue);
        Assert.assertTrue(testSubject.addedAssociations().isEmpty());
        Assert.assertTrue(testSubject.removedAssociations().isEmpty());
    }

    @Test
    public void testAddAndRemoveEntry() {
        testSubject.add(associationValue);
        testSubject.remove(associationValue);
        Assert.assertTrue(testSubject.addedAssociations().isEmpty());
        Assert.assertTrue(testSubject.removedAssociations().isEmpty());
    }

    @Test
    public void testContains() {
        Assert.assertFalse(testSubject.contains(associationValue));
        testSubject.add(associationValue);
        Assert.assertTrue(testSubject.contains(associationValue));
        Assert.assertTrue(testSubject.contains(new AssociationValue("key", "value")));
        testSubject.remove(associationValue);
        Assert.assertFalse(testSubject.contains(associationValue));
    }

    @Test
    public void testAsSet() {
        testSubject.add(associationValue);
        int t = 0;
        for (AssociationValue actual : testSubject.asSet()) {
            Assert.assertSame(associationValue, actual);
            t++;
        }
        Assert.assertEquals(1, t);
    }

    @Test
    public void testIterator() {
        testSubject.add(associationValue);
        Iterator<AssociationValue> iterator = testSubject.iterator();
        Assert.assertSame(associationValue, iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }
}

