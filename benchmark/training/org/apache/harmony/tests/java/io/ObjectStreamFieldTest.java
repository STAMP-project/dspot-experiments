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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.ObjectStreamField;
import java.io.Serializable;
import junit.framework.TestCase;


public class ObjectStreamFieldTest extends TestCase {
    static class DummyClass implements Serializable {
        private static final long serialVersionUID = 999999999999998L;

        long bam = 999L;

        int ham = 9999;

        int sam = 8888;

        Object hola = new Object();

        public static long getUID() {
            return ObjectStreamFieldTest.DummyClass.serialVersionUID;
        }
    }

    ObjectStreamClass osc;

    ObjectStreamField hamField;

    ObjectStreamField samField;

    ObjectStreamField bamField;

    ObjectStreamField holaField;

    /**
     * java.io.ObjectStreamField#ObjectStreamField(java.lang.String,
     * java.lang.Class)
     */
    public void test_ConstructorLjava_lang_StringLjava_lang_Class() {
        TestCase.assertTrue("Used to test", true);
    }

    public void test_equalsLjava_lang_Object() {
        // Regression test for HARMONY-4273
        TestCase.assertTrue(samField.equals(samField));
        TestCase.assertFalse(samField.equals(hamField));
        TestCase.assertFalse(samField.equals("fish"));
        TestCase.assertFalse(samField.equals(null));
    }

    /**
     * java.io.ObjectStreamField#compareTo(java.lang.Object)
     */
    public void test_compareToLjava_lang_Object() {
        TestCase.assertTrue("Object compared to int did not return > 0", ((holaField.compareTo(hamField)) > 0));
        TestCase.assertEquals("Int compared to itself did not return 0", 0, hamField.compareTo(hamField));
        TestCase.assertTrue("(Int)ham compared to (Int)sam did not return < 0", ((hamField.compareTo(samField)) < 0));
    }

    /**
     * java.io.ObjectStreamField#getName()
     */
    public void test_getName() {
        TestCase.assertEquals("Field did not return correct name", "hola", holaField.getName());
    }

    /**
     * java.io.ObjectStreamField#getOffset()
     */
    public void test_getOffset() {
        ObjectStreamField[] osfArray;
        osfArray = osc.getFields();
        TestCase.assertTrue("getOffset did not return reasonable values", ((osfArray[0].getOffset()) != (osfArray[1].getOffset())));
        TestCase.assertEquals("getOffset for osfArray[0].getOffset() did not return 0", 0, osfArray[0].getOffset());
        TestCase.assertEquals("osfArray[1].getOffset() did not return	8", 8, osfArray[1].getOffset());
        TestCase.assertEquals("osfArray[2].getOffset() did not return 12", 12, osfArray[2].getOffset());
    }

    /**
     * java.io.ObjectStreamField#getType()
     */
    public void test_getType() {
        TestCase.assertTrue("getType on an Object field did not answer Object", holaField.getType().equals(Object.class));
    }

    /**
     * java.io.ObjectStreamField#getTypeCode()
     */
    public void test_getTypeCode() {
        TestCase.assertEquals("getTypeCode on an Object field did not answer 'L'", 'L', holaField.getTypeCode());
        TestCase.assertEquals("getTypeCode on a long field did not answer 'J'", 'J', bamField.getTypeCode());
    }

    /**
     * java.io.ObjectStreamField#getTypeString()
     */
    public void test_getTypeString() {
        TestCase.assertTrue(("getTypeString returned: " + (holaField.getTypeString())), ((holaField.getTypeString().indexOf("Object")) >= 0));
        TestCase.assertNull("Primitive types' strings should be null", hamField.getTypeString());
        ObjectStreamField osf = new ObjectStreamField("s", String.class, true);
        TestCase.assertTrue(((osf.getTypeString()) == "Ljava/lang/String;"));
    }

    /**
     * java.io.ObjectStreamField#toString()
     */
    public void test_toString() {
        TestCase.assertTrue(("toString on a long returned: " + (bamField.toString())), ((bamField.toString().indexOf("bam")) >= 0));
    }

    /**
     * java.io.ObjectStreamField#getType()
     */
    public void test_getType_Deserialized() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(new SerializableObject());
        oos.close();
        baos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        SerializableObject obj = ((SerializableObject) (ois.readObject()));
        ObjectStreamClass oc = obj.getObjectStreamClass();
        ObjectStreamField field = oc.getField("i");
        TestCase.assertEquals(Object.class, field.getType());
    }

    /**
     * java.io.ObjectStreamField#getType()
     */
    public void test_getType_MockObjectInputStream() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(new SerializableObject());
        oos.close();
        baos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        MockObjectInputStream ois = new MockObjectInputStream(bais);
        ois.readObject();
        ObjectStreamClass oc = ois.getObjectStreamClass();
        ObjectStreamField field = oc.getField("i");
        TestCase.assertEquals(Object.class, field.getType());
    }

    public void test_isUnshared() throws Exception {
        SerializableObject2 obj = new SerializableObject2();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        baos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        SerializableObject2 newObj = ((SerializableObject2) (ois.readObject()));
        ObjectInputStream.GetField getField = newObj.getGetField();
        ObjectStreamClass objectStreamClass = getField.getObjectStreamClass();
        TestCase.assertTrue(objectStreamClass.getField("i").isUnshared());
        TestCase.assertFalse(objectStreamClass.getField("d").isUnshared());
        TestCase.assertTrue(objectStreamClass.getField("s").isUnshared());
        TestCase.assertEquals(1000, getField.get("i", null));
        TestCase.assertEquals(SerializableObject2.today, getField.get("d", null));
        TestCase.assertEquals("Richard", getField.get("s", null));
        TestCase.assertTrue(((objectStreamClass.getField("s").getTypeString()) == "Ljava/lang/String;"));
        TestCase.assertEquals(0, objectStreamClass.getField("d").getOffset());
        TestCase.assertEquals(1, objectStreamClass.getField("i").getOffset());
        TestCase.assertEquals(2, objectStreamClass.getField("s").getOffset());
    }

    /**
     * Write/serialize and read/de-serialize an object with primitive field
     */
    public void test_ObjectWithPrimitiveField() throws IOException, ClassNotFoundException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final MyObjectOutputStream oos = new MyObjectOutputStream(baos);
        oos.writeObject(new MockClass());
        final byte[] bytes = baos.toByteArray();
        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        final MyObjectInputStream ois = new MyObjectInputStream(bais);
        // NullPointerException is thrown by the readObject call below.
        ois.readObject();
    }
}

