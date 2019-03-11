/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.harmony.tests.java.io;


import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamClass;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.Proxy;
import junit.framework.TestCase;


public class ObjectStreamClassTest extends TestCase {
    static class DummyClass implements Serializable {
        private static final long serialVersionUID = 999999999999999L;

        long bam = 999L;

        int ham = 9999;

        public static long getUID() {
            return ObjectStreamClassTest.DummyClass.serialVersionUID;
        }
    }

    /**
     * java.io.ObjectStreamClass#forClass()
     */
    public void test_forClass() {
        // Need to test during serialization to be sure an instance is
        // returned
        ObjectStreamClass osc = ObjectStreamClass.lookup(ObjectStreamClassTest.DummyClass.class);
        TestCase.assertEquals(("forClass returned an object: " + (osc.forClass())), ObjectStreamClassTest.DummyClass.class, osc.forClass());
    }

    /**
     * java.io.ObjectStreamClass#getField(java.lang.String)
     */
    public void test_getFieldLjava_lang_String() {
        ObjectStreamClass osc = ObjectStreamClass.lookup(ObjectStreamClassTest.DummyClass.class);
        TestCase.assertEquals("getField did not return correct field", 'J', osc.getField("bam").getTypeCode());
        TestCase.assertNull("getField did not null for non-existent field", osc.getField("wham"));
    }

    /**
     * java.io.ObjectStreamClass#getFields()
     */
    public void test_getFields() {
        ObjectStreamClass osc = ObjectStreamClass.lookup(ObjectStreamClassTest.DummyClass.class);
        ObjectStreamField[] osfArray = osc.getFields();
        TestCase.assertTrue(("Array of fields should be of length 2 but is instead of length: " + (osfArray.length)), ((osfArray.length) == 2));
    }

    /**
     * java.io.ObjectStreamClass#getName()
     */
    public void test_getName() {
        ObjectStreamClass osc = ObjectStreamClass.lookup(ObjectStreamClassTest.DummyClass.class);
        TestCase.assertEquals(("getName returned incorrect name: " + (osc.getName())), "org.apache.harmony.tests.java.io.ObjectStreamClassTest$DummyClass", osc.getName());
    }

    /**
     * java.io.ObjectStreamClass#getSerialVersionUID()
     */
    public void test_getSerialVersionUID() {
        ObjectStreamClass osc = ObjectStreamClass.lookup(ObjectStreamClassTest.DummyClass.class);
        TestCase.assertTrue(((("getSerialversionUID returned incorrect uid: " + (osc.getSerialVersionUID())) + " instead of ") + (ObjectStreamClassTest.DummyClass.getUID())), ((osc.getSerialVersionUID()) == (ObjectStreamClassTest.DummyClass.getUID())));
    }

    static class SyntheticTest implements Serializable {
        private int i;

        private class X implements Serializable {
            public int get() {
                return i;
            }
        }

        public ObjectStreamClassTest.SyntheticTest.X foo() {
            return new ObjectStreamClassTest.SyntheticTest.X();
        }
    }

    /**
     * java.io.ObjectStreamClass#lookup(java.lang.Class)
     */
    public void test_lookupLjava_lang_Class() {
        ObjectStreamClass osc = ObjectStreamClass.lookup(ObjectStreamClassTest.DummyClass.class);
        TestCase.assertEquals(("lookup returned wrong class: " + (osc.getName())), "org.apache.harmony.tests.java.io.ObjectStreamClassTest$DummyClass", osc.getName());
    }

    /**
     * java.io.ObjectStreamClass#toString()
     */
    public void test_toString() {
        ObjectStreamClass osc = ObjectStreamClass.lookup(ObjectStreamClassTest.DummyClass.class);
        String oscString = osc.toString();
        // The previous test was more specific than the spec so it was replaced
        // with the test below
        TestCase.assertTrue(("toString returned incorrect string: " + (osc.toString())), (((oscString.indexOf("serialVersionUID")) >= 0) && ((oscString.indexOf("999999999999999L")) >= 0)));
    }

    public void testSerialization() {
        ObjectStreamClass osc = ObjectStreamClass.lookup(ObjectStreamClass.class);
        TestCase.assertEquals(0, osc.getFields().length);
    }

    public void test_specialTypes() {
        Class<?> proxyClass = Proxy.getProxyClass(this.getClass().getClassLoader(), new Class[]{ Runnable.class });
        ObjectStreamClass proxyStreamClass = ObjectStreamClass.lookup(proxyClass);
        TestCase.assertEquals("Proxy classes should have zero serialVersionUID", 0, proxyStreamClass.getSerialVersionUID());
        ObjectStreamField[] proxyFields = proxyStreamClass.getFields();
        TestCase.assertEquals("Proxy classes should have no serialized fields", 0, proxyFields.length);
        ObjectStreamClass enumStreamClass = ObjectStreamClass.lookup(Thread.State.class);
        TestCase.assertEquals("Enum classes should have zero serialVersionUID", 0, enumStreamClass.getSerialVersionUID());
        ObjectStreamField[] enumFields = enumStreamClass.getFields();
        TestCase.assertEquals("Enum classes should have no serialized fields", 0, enumFields.length);
    }

    /**
     *
     *
     * @since 1.6
     */
    static class NonSerialzableClass {
        private static final long serialVersionUID = 1L;

        public static long getUID() {
            return ObjectStreamClassTest.NonSerialzableClass.serialVersionUID;
        }
    }

    /**
     *
     *
     * @since 1.6
     */
    static class ExternalizableClass implements Externalizable {
        private static final long serialVersionUID = -4285635779249689129L;

        public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
            throw new ClassNotFoundException();
        }

        public void writeExternal(ObjectOutput output) throws IOException {
            throw new IOException();
        }
    }

    /**
     * java.io.ObjectStreamClass#lookupAny(java.lang.Class)
     *
     * @since 1.6
     */
    public void test_lookupAnyLjava_lang_Class() {
        // Test for method java.io.ObjectStreamClass
        // java.io.ObjectStreamClass.lookupAny(java.lang.Class)
        ObjectStreamClass osc = ObjectStreamClass.lookupAny(ObjectStreamClassTest.DummyClass.class);
        TestCase.assertEquals(("lookup returned wrong class: " + (osc.getName())), "org.apache.harmony.tests.java.io.ObjectStreamClassTest$DummyClass", osc.getName());
        osc = ObjectStreamClass.lookupAny(ObjectStreamClassTest.NonSerialzableClass.class);
        TestCase.assertEquals(("lookup returned wrong class: " + (osc.getName())), "org.apache.harmony.tests.java.io.ObjectStreamClassTest$NonSerialzableClass", osc.getName());
        osc = ObjectStreamClass.lookupAny(ObjectStreamClassTest.ExternalizableClass.class);
        TestCase.assertEquals(("lookup returned wrong class: " + (osc.getName())), "org.apache.harmony.tests.java.io.ObjectStreamClassTest$ExternalizableClass", osc.getName());
        osc = ObjectStreamClass.lookup(ObjectStreamClassTest.NonSerialzableClass.class);
        TestCase.assertNull(osc);
    }
}

