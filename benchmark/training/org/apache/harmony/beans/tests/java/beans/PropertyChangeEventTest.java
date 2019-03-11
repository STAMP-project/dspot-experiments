/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.beans.tests.java.beans;


import java.beans.PropertyChangeEvent;
import java.io.Serializable;
import junit.framework.TestCase;
import org.apache.harmony.testframework.serialization.SerializationTest;
import org.apache.harmony.testframework.serialization.SerializationTest.SerializableAssert;


/**
 * Test class java.beans.PropertyChangeEvent.
 */
public class PropertyChangeEventTest extends TestCase {
    /* Test the constructor with normal parameters. */
    public void testConstructor_Normal() {
        Object src = new Object();
        Object oldValue = new Object();
        Object newValue = new Object();
        PropertyChangeEvent event = new PropertyChangeEvent(src, "myPropName", oldValue, newValue);
        TestCase.assertSame(src, event.getSource());
        TestCase.assertEquals("myPropName", event.getPropertyName());
        TestCase.assertSame(oldValue, event.getOldValue());
        TestCase.assertSame(newValue, event.getNewValue());
        TestCase.assertNull(event.getPropagationId());
    }

    /* Test the constructor with null parameters except the source parameter. */
    public void testConstructor_Null() {
        Object src = new Object();
        PropertyChangeEvent event = new PropertyChangeEvent(src, null, null, null);
        TestCase.assertSame(src, event.getSource());
        TestCase.assertNull(event.getPropertyName());
        TestCase.assertSame(null, event.getOldValue());
        TestCase.assertSame(null, event.getNewValue());
        TestCase.assertNull(event.getPropagationId());
    }

    /* Test the constructor with null properties but non-null old and new
    values.
     */
    public void testConstructor_NullProperty() {
        Object src = new Object();
        Object oldValue = new Object();
        Object newValue = new Object();
        PropertyChangeEvent event = new PropertyChangeEvent(src, null, oldValue, newValue);
        TestCase.assertSame(src, event.getSource());
        TestCase.assertNull(event.getPropertyName());
        TestCase.assertSame(oldValue, event.getOldValue());
        TestCase.assertSame(newValue, event.getNewValue());
        TestCase.assertNull(event.getPropagationId());
    }

    /* Test the constructor with null source parameter. */
    public void testConstructor_NullSrc() {
        try {
            new PropertyChangeEvent(null, "prop", new Object(), new Object());
            TestCase.fail("Should throw IllegalArgumentException!");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    /* Test the method setPropagationId() with a normal value. */
    public void testSetPropagationId_Normal() {
        Object src = new Object();
        Object oldValue = new Object();
        Object newValue = new Object();
        PropertyChangeEvent event = new PropertyChangeEvent(src, "myPropName", oldValue, newValue);
        TestCase.assertNull(event.getPropagationId());
        Object pid = new Object();
        event.setPropagationId(pid);
        TestCase.assertSame(src, event.getSource());
        TestCase.assertEquals("myPropName", event.getPropertyName());
        TestCase.assertSame(oldValue, event.getOldValue());
        TestCase.assertSame(newValue, event.getNewValue());
        TestCase.assertSame(pid, event.getPropagationId());
    }

    /* Test the method setPropagationId() with a null value. */
    public void testSetPropagationId_Null() {
        Object src = new Object();
        Object oldValue = new Object();
        Object newValue = new Object();
        PropertyChangeEvent event = new PropertyChangeEvent(src, "myPropName", oldValue, newValue);
        TestCase.assertNull(event.getPropagationId());
        // set null when already null
        event.setPropagationId(null);
        TestCase.assertNull(event.getPropagationId());
        // set a non-null value
        Object pid = new Object();
        event.setPropagationId(pid);
        TestCase.assertSame(src, event.getSource());
        TestCase.assertEquals("myPropName", event.getPropertyName());
        TestCase.assertSame(oldValue, event.getOldValue());
        TestCase.assertSame(newValue, event.getNewValue());
        TestCase.assertSame(pid, event.getPropagationId());
        // reset to null
        event.setPropagationId(null);
        TestCase.assertNull(event.getPropagationId());
    }

    // comparator for PropertyChangeEvent objects
    public static final SerializableAssert comparator = new SerializableAssert() {
        public void assertDeserialized(Serializable initial, Serializable deserialized) {
            PropertyChangeEvent initEv = ((PropertyChangeEvent) (initial));
            PropertyChangeEvent desrEv = ((PropertyChangeEvent) (deserialized));
            TestCase.assertEquals("NewValue", initEv.getNewValue(), desrEv.getNewValue());
            TestCase.assertEquals("OldValue", initEv.getOldValue(), desrEv.getOldValue());
            TestCase.assertEquals("PropagationId", initEv.getPropagationId(), desrEv.getPropagationId());
            TestCase.assertEquals("PropertyName", initEv.getPropertyName(), desrEv.getPropertyName());
        }
    };

    /**
     *
     *
     * @unknown serialization/deserialization.
     */
    public void testSerializationSelf() throws Exception {
        SerializationTest.verifySelf(new PropertyChangeEvent(new Object(), "myPropName", "oldValue", "newValue"), PropertyChangeEventTest.comparator);
    }

    /**
     *
     *
     * @unknown serialization/deserialization compatibility with RI.
     */
    public void testSerializationCompatibility() throws Exception {
        SerializationTest.verifyGolden(this, new PropertyChangeEvent(new Object(), "myPropName", "oldValue", "newValue"), PropertyChangeEventTest.comparator);
    }
}

