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
package org.apache.harmony.luni.tests.java.io;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.NotActiveException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import junit.framework.TestCase;
import org.apache.harmony.luni.tests.pkg2.TestClass;
import org.apache.harmony.testframework.serialization.SerializationTest;
import org.apache.harmony.testframework.serialization.SerializationTest.SerializableAssert;


@SuppressWarnings("serial")
public class ObjectInputStreamTest extends TestCase implements Serializable {
    ObjectInputStream ois;

    ObjectOutputStream oos;

    ByteArrayOutputStream bao;

    public class SerializableTestHelper implements Serializable {
        public String aField1;

        public String aField2;

        SerializableTestHelper() {
            aField1 = null;
            aField2 = null;
        }

        SerializableTestHelper(String s, String t) {
            aField1 = s;
            aField2 = t;
        }

        private void readObject(ObjectInputStream ois) throws Exception {
            // note aField2 is not read
            ObjectInputStream.GetField fields = ois.readFields();
            aField1 = ((String) (fields.get("aField1", "Zap")));
        }

        private void writeObject(ObjectOutputStream oos) throws IOException {
            // note aField2 is not written
            ObjectOutputStream.PutField fields = oos.putFields();
            fields.put("aField1", aField1);
            oos.writeFields();
        }

        public String getText1() {
            return aField1;
        }

        public void setText1(String s) {
            aField1 = s;
        }

        public String getText2() {
            return aField2;
        }

        public void setText2(String s) {
            aField2 = s;
        }
    }

    public static class A1 implements Serializable {
        private static final long serialVersionUID = 5942584913446079661L;

        ObjectInputStreamTest.B1 b1 = new ObjectInputStreamTest.B1();

        ObjectInputStreamTest.B1 b2 = b1;

        Vector v = new Vector();
    }

    public static class B1 implements Serializable {
        int i = 5;

        Hashtable h = new Hashtable();
    }

    /**
     * java.io.ObjectInputStream#readObject()
     */
    public void test_readObjectMissingClasses() throws Exception {
        SerializationTest.verifySelf(new ObjectInputStreamTest.A1(), new SerializableAssert() {
            public void assertDeserialized(Serializable initial, Serializable deserialized) {
                TestCase.assertEquals(5, ((ObjectInputStreamTest.A1) (deserialized)).b1.i);
            }
        });
    }

    /**
     * java.io.ObjectInputStream#ObjectInputStream(java.io.InputStream)
     */
    public void test_ConstructorLjava_io_InputStream() throws IOException {
        oos.writeDouble(Double.MAX_VALUE);
        oos.close();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        ois.close();
        oos.close();
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(new byte[90]));
            TestCase.fail("StreamCorruptedException expected");
        } catch (StreamCorruptedException e) {
            // Expected
        }
    }

    /**
     * {@link java.io.ObjectInputStream#resolveProxyClass(String[])}
     */
    public void test_resolveProxyClass() throws IOException, ClassNotFoundException {
        oos.writeBytes("HelloWorld");
        oos.close();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        ObjectInputStreamTest.MockObjectInputStream mockIn = new ObjectInputStreamTest.MockObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        Class[] clazzs = new Class[]{ ObjectInputStream.class, Reader.class };
        for (int i = 0; i < (clazzs.length); i++) {
            Class clazz = clazzs[i];
            Class[] interfaceNames = clazz.getInterfaces();
            String[] interfaces = new String[interfaceNames.length];
            int index = 0;
            for (Class c : interfaceNames) {
                interfaces[index] = c.getName();
                index++;
            }
            Class<?> s = mockIn.resolveProxyClass(interfaces);
            if (Proxy.isProxyClass(s)) {
                Class[] implementedInterfaces = s.getInterfaces();
                for (index = 0; index < (implementedInterfaces.length); index++) {
                    TestCase.assertEquals(interfaceNames[index], implementedInterfaces[index]);
                }
            } else {
                TestCase.fail("Should return a proxy class that implements the interfaces named in a proxy class descriptor");
            }
        }
        mockIn.close();
    }

    class MockObjectInputStream extends ObjectInputStream {
        public MockObjectInputStream(InputStream input) throws IOException, StreamCorruptedException {
            super(input);
        }

        @Override
        public Class<?> resolveProxyClass(String[] interfaceNames) throws IOException, ClassNotFoundException {
            return super.resolveProxyClass(interfaceNames);
        }
    }

    /**
     * java.io.ObjectInputStream#available()
     */
    public void test_available() throws IOException {
        oos.writeBytes("HelloWorld");
        oos.close();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        TestCase.assertEquals("Read incorrect bytes", 10, ois.available());
        ois.close();
    }

    /**
     * java.io.ObjectInputStream#close()
     */
    public void test_close() throws IOException {
        oos.writeBytes("HelloWorld");
        oos.close();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        ois.close();
    }

    /**
     * java.io.ObjectInputStream#defaultReadObject()
     */
    public void test_defaultReadObject() throws Exception {
        // SM. This method may as well be private, as if called directly it
        // throws an exception.
        String s = "HelloWorld";
        oos.writeObject(s);
        oos.close();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        try {
            ois.defaultReadObject();
            TestCase.fail("NotActiveException expected");
        } catch (NotActiveException e) {
            // Desired behavior
        } finally {
            ois.close();
        }
    }

    /**
     * java.io.ObjectInputStream#read()
     */
    public void test_read() throws IOException {
        oos.write('T');
        oos.close();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        TestCase.assertEquals("Read incorrect byte value", 'T', ois.read());
        ois.close();
    }

    /**
     * java.io.ObjectInputStream#read(byte[], int, int)
     */
    public void test_read$BII() throws IOException {
        byte[] buf = new byte[10];
        oos.writeBytes("HelloWorld");
        oos.close();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        ois.read(buf, 0, 10);
        ois.close();
        TestCase.assertEquals("Read incorrect bytes", "HelloWorld", new String(buf, 0, 10, "UTF-8"));
    }

    /**
     * java.io.ObjectInputStream#readBoolean()
     */
    public void test_readBoolean() throws IOException {
        oos.writeBoolean(true);
        oos.close();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        TestCase.assertTrue("Read incorrect boolean value", ois.readBoolean());
        ois.close();
    }

    /**
     * java.io.ObjectInputStream#readByte()
     */
    public void test_readByte() throws IOException {
        oos.writeByte(127);
        oos.close();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        TestCase.assertEquals("Read incorrect byte value", 127, ois.readByte());
        ois.close();
    }

    /**
     * java.io.ObjectInputStream#readChar()
     */
    public void test_readChar() throws IOException {
        oos.writeChar('T');
        oos.close();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        TestCase.assertEquals("Read incorrect char value", 'T', ois.readChar());
        ois.close();
    }

    /**
     * java.io.ObjectInputStream#readDouble()
     */
    public void test_readDouble() throws IOException {
        oos.writeDouble(Double.MAX_VALUE);
        oos.close();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        TestCase.assertTrue("Read incorrect double value", ((ois.readDouble()) == (Double.MAX_VALUE)));
        ois.close();
    }

    /**
     * java.io.ObjectInputStream#readFields()
     */
    public void test_readFields() throws Exception {
        ObjectInputStreamTest.SerializableTestHelper sth;
        /* "SerializableTestHelper" is an object created for these tests with
        two fields (Strings) and simple implementations of readObject and
        writeObject which simply read and write the first field but not the
        second
         */
        oos.writeObject(new ObjectInputStreamTest.SerializableTestHelper("Gabba", "Jabba"));
        oos.flush();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        sth = ((ObjectInputStreamTest.SerializableTestHelper) (ois.readObject()));
        TestCase.assertEquals("readFields / writeFields failed--first field not set", "Gabba", sth.getText1());
        TestCase.assertNull("readFields / writeFields failed--second field should not have been set", sth.getText2());
    }

    /**
     * java.io.ObjectInputStream#readFloat()
     */
    public void test_readFloat() throws IOException {
        oos.writeFloat(Float.MAX_VALUE);
        oos.close();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        TestCase.assertTrue("Read incorrect float value", ((ois.readFloat()) == (Float.MAX_VALUE)));
        ois.close();
    }

    /**
     * java.io.ObjectInputStream#readFully(byte[])
     */
    public void test_readFully$B() throws IOException {
        byte[] buf = new byte[10];
        oos.writeBytes("HelloWorld");
        oos.close();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        ois.readFully(buf);
        ois.close();
        TestCase.assertEquals("Read incorrect bytes", "HelloWorld", new String(buf, 0, 10, "UTF-8"));
    }

    /**
     * java.io.ObjectInputStream#readFully(byte[], int, int)
     */
    public void test_readFully$BII() throws IOException {
        byte[] buf = new byte[10];
        oos.writeBytes("HelloWorld");
        oos.close();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        ois.readFully(buf, 0, 10);
        ois.close();
        TestCase.assertEquals("Read incorrect bytes", "HelloWorld", new String(buf, 0, 10, "UTF-8"));
    }

    /**
     * java.io.ObjectInputStream#readInt()
     */
    public void test_readInt() throws IOException {
        oos.writeInt(Integer.MAX_VALUE);
        oos.close();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        TestCase.assertTrue("Read incorrect int value", ((ois.readInt()) == (Integer.MAX_VALUE)));
        ois.close();
    }

    /**
     * java.io.ObjectInputStream#readLong()
     */
    public void test_readLong() throws IOException {
        oos.writeLong(Long.MAX_VALUE);
        oos.close();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        TestCase.assertTrue("Read incorrect long value", ((ois.readLong()) == (Long.MAX_VALUE)));
        ois.close();
    }

    /**
     * java.io.ObjectInputStream#readObject()
     */
    public void test_readObject() throws Exception {
        String s = "HelloWorld";
        oos.writeObject(s);
        oos.close();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        TestCase.assertEquals("Read incorrect Object value", s, ois.readObject());
        ois.close();
        // Regression for HARMONY-91
        // dynamically create serialization byte array for the next hierarchy:
        // - class A implements Serializable
        // - class C extends A
        byte[] cName = ObjectInputStreamTest.C.class.getName().getBytes("UTF-8");
        byte[] aName = ObjectInputStreamTest.A.class.getName().getBytes("UTF-8");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] begStream = new byte[]{ ((byte) (172)), ((byte) (237))// STREAM_MAGIC
        , ((byte) (0)), ((byte) (5))// STREAM_VERSION
        , ((byte) (115))// TC_OBJECT
        , ((byte) (114))// TC_CLASSDESC
        , ((byte) (0))// only first byte for C class name length
         };
        out.write(begStream, 0, begStream.length);
        out.write(cName.length);// second byte for C class name length

        out.write(cName, 0, cName.length);// C class name

        byte[] midStream = new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (33))// serialVersionUID = 33L
        , ((byte) (2))// flags
        , ((byte) (0)), ((byte) (0))// fields : none
        , ((byte) (120))// TC_ENDBLOCKDATA
        , ((byte) (114))// Super class for C: TC_CLASSDESC for A class
        , ((byte) (0))// only first byte for A class name length
         };
        out.write(midStream, 0, midStream.length);
        out.write(aName.length);// second byte for A class name length

        out.write(aName, 0, aName.length);// A class name

        byte[] endStream = new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (11))// serialVersionUID = 11L
        , ((byte) (2))// flags
        , ((byte) (0)), ((byte) (1))// fields
        , ((byte) (76))// field description: type L (object)
        , ((byte) (0)), ((byte) (4))// length
        , // field = 'name'
        ((byte) (110)), ((byte) (97)), ((byte) (109)), ((byte) (101)), ((byte) (116))// className1: TC_STRING
        , ((byte) (0)), ((byte) (18))// length
        , // 
        ((byte) (76)), ((byte) (106)), ((byte) (97)), ((byte) (118)), ((byte) (97)), ((byte) (47)), ((byte) (108)), ((byte) (97)), ((byte) (110)), ((byte) (103)), ((byte) (47)), ((byte) (83)), ((byte) (116)), ((byte) (114)), ((byte) (105)), ((byte) (110)), ((byte) (103)), ((byte) (59)), ((byte) (120))// TC_ENDBLOCKDATA
        , ((byte) (112))// NULL super class for A class
        , // classdata
        ((byte) (116))// TC_STRING
        , ((byte) (0)), ((byte) (4))// length
        , ((byte) (110)), ((byte) (97)), ((byte) (109)), ((byte) (101))// value
         };
        out.write(endStream, 0, endStream.length);
        out.flush();
        // read created serial. form
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
        Object o = ois.readObject();
        TestCase.assertEquals(ObjectInputStreamTest.C.class, o.getClass());
        // Regression for HARMONY-846
        TestCase.assertNull(new ObjectInputStream() {}.readObject());
    }

    /**
     * java.io.ObjectInputStream#readObjectOverride()
     */
    public void test_readObjectOverride() throws Exception {
        // Regression for HARMONY-846
        TestCase.assertNull(new ObjectInputStream() {
            @Override
            public Object readObjectOverride() throws IOException, ClassNotFoundException {
                return super.readObjectOverride();
            }
        }.readObjectOverride());
    }

    public static class A implements Serializable {
        private static final long serialVersionUID = 11L;

        public String name = "name";
    }

    public static class B extends ObjectInputStreamTest.A {}

    public static class C extends ObjectInputStreamTest.B {
        private static final long serialVersionUID = 33L;
    }

    /**
     * java.io.ObjectInputStream#readObject()
     */
    public void test_readObjectCorrupt() throws IOException, ClassNotFoundException {
        byte[] bytes = new byte[]{ 0, 0, 0, 100, 67, 72, ((byte) (253)), 113, 0, 0, 11, ((byte) (184)), 77, 101 };
        ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream in = new ObjectInputStream(bin);
            in.readObject();
            TestCase.fail("Unexpected read of corrupted stream");
        } catch (StreamCorruptedException e) {
            // Expected
        }
    }

    /**
     * java.io.ObjectInputStream#readShort()
     */
    public void test_readShort() throws IOException {
        oos.writeShort(Short.MAX_VALUE);
        oos.close();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        TestCase.assertTrue("Read incorrect short value", ((ois.readShort()) == (Short.MAX_VALUE)));
        ois.close();
    }

    /**
     * java.io.ObjectInputStream#readUnsignedByte()
     */
    public void test_readUnsignedByte() throws IOException {
        oos.writeByte((-1));
        oos.close();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        TestCase.assertEquals("Read incorrect unsignedByte value", 255, ois.readUnsignedByte());
        ois.close();
    }

    /**
     * java.io.ObjectInputStream#readUnsignedShort()
     */
    public void test_readUnsignedShort() throws IOException {
        oos.writeShort((-1));
        oos.close();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        TestCase.assertEquals("Read incorrect unsignedShort value", 65535, ois.readUnsignedShort());
        ois.close();
    }

    /**
     * java.io.ObjectInputStream#readUTF()
     */
    public void test_readUTF() throws IOException {
        oos.writeUTF("HelloWorld");
        oos.close();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        TestCase.assertEquals("Read incorrect utf value", "HelloWorld", ois.readUTF());
        ois.close();
    }

    /**
     * java.io.ObjectInputStream#skipBytes(int)
     */
    public void test_skipBytesI() throws IOException {
        byte[] buf = new byte[10];
        oos.writeBytes("HelloWorld");
        oos.close();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        ois.skipBytes(5);
        ois.read(buf, 0, 5);
        ois.close();
        TestCase.assertEquals("Skipped incorrect bytes", "World", new String(buf, 0, 5, "UTF-8"));
        // Regression for HARMONY-844
        try {
            new ObjectInputStream() {}.skipBytes(0);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
        }
    }

    // Regression Test for JIRA 2192
    public void test_readObject_withPrimitiveClass() throws Exception {
        File file = new File("test.ser");
        file.deleteOnExit();
        Test test = new Test();
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
        out.writeObject(test);
        out.close();
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
        Test another = ((Test) (in.readObject()));
        in.close();
        TestCase.assertEquals(test, another);
    }

    // Regression Test for JIRA-2249
    public static class ObjectOutputStreamWithWriteDesc extends ObjectOutputStream {
        public ObjectOutputStreamWithWriteDesc(OutputStream os) throws IOException {
            super(os);
        }

        @Override
        public void writeClassDescriptor(ObjectStreamClass desc) throws IOException {
        }
    }

    public static class ObjectIutputStreamWithReadDesc extends ObjectInputStream {
        private Class returnClass;

        public ObjectIutputStreamWithReadDesc(InputStream is, Class returnClass) throws IOException {
            super(is);
            this.returnClass = returnClass;
        }

        @Override
        public ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
            return ObjectStreamClass.lookup(returnClass);
        }
    }

    static class TestClassForSerialization implements Serializable {
        private static final long serialVersionUID = 1L;
    }

    public void test_ClassDescriptor() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectInputStreamTest.ObjectOutputStreamWithWriteDesc oos = new ObjectInputStreamTest.ObjectOutputStreamWithWriteDesc(baos);
        oos.writeObject(String.class);
        oos.close();
        Class cls = ObjectInputStreamTest.TestClassForSerialization.class;
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStreamTest.ObjectIutputStreamWithReadDesc ois = new ObjectInputStreamTest.ObjectIutputStreamWithReadDesc(bais, cls);
        Object obj = ois.readObject();
        ois.close();
        TestCase.assertEquals(cls, obj);
    }

    // Regression Test for JIRA-2340
    public static class ObjectOutputStreamWithWriteDesc1 extends ObjectOutputStream {
        public ObjectOutputStreamWithWriteDesc1(OutputStream os) throws IOException {
            super(os);
        }

        @Override
        public void writeClassDescriptor(ObjectStreamClass desc) throws IOException {
            super.writeClassDescriptor(desc);
        }
    }

    public static class ObjectIutputStreamWithReadDesc1 extends ObjectInputStream {
        public ObjectIutputStreamWithReadDesc1(InputStream is) throws IOException {
            super(is);
        }

        @Override
        public ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
            return super.readClassDescriptor();
        }
    }

    // Regression test for Harmony-1921
    public static class ObjectInputStreamWithResolve extends ObjectInputStream {
        public ObjectInputStreamWithResolve(InputStream in) throws IOException {
            super(in);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Class resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            if (desc.getName().equals("org.apache.harmony.luni.tests.pkg1.TestClass")) {
                return TestClass.class;
            }
            return super.resolveClass(desc);
        }
    }

    public void test_resolveClass() throws Exception {
        org.apache.harmony.luni.tests.pkg1.TestClass to1 = new org.apache.harmony.luni.tests.pkg1.TestClass();
        to1.i = 555;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(to1);
        oos.flush();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStreamTest.ObjectInputStreamWithResolve(bais);
        TestClass to2 = ((TestClass) (ois.readObject()));
        if ((to2.i) != (to1.i)) {
            TestCase.fail(((("Wrong object read. Expected val: " + (to1.i)) + ", got: ") + (to2.i)));
        }
    }

    static class ObjectInputStreamWithResolveObject extends ObjectInputStream {
        public static Integer intObj = Integer.valueOf(1000);

        public ObjectInputStreamWithResolveObject(InputStream in) throws IOException {
            super(in);
            enableResolveObject(true);
        }

        @Override
        protected Object resolveObject(Object obj) throws IOException {
            if (obj instanceof Integer) {
                obj = ObjectInputStreamTest.ObjectInputStreamWithResolveObject.intObj;
            }
            return super.resolveObject(obj);
        }
    }

    /**
     * java.io.ObjectInputStream#resolveObject(Object)
     */
    public void test_resolveObjectLjava_lang_Object() throws Exception {
        // Write an Integer object into memory
        Integer original = new Integer(10);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(original);
        oos.flush();
        oos.close();
        // Read the object from memory
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStreamTest.ObjectInputStreamWithResolveObject ois = new ObjectInputStreamTest.ObjectInputStreamWithResolveObject(bais);
        Integer actual = ((Integer) (ois.readObject()));
        ois.close();
        // object should be resolved from 10 to 1000
        TestCase.assertEquals(ObjectInputStreamTest.ObjectInputStreamWithResolveObject.intObj, actual);
    }

    public void test_readClassDescriptor() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectInputStreamTest.ObjectOutputStreamWithWriteDesc1 oos = new ObjectInputStreamTest.ObjectOutputStreamWithWriteDesc1(baos);
        ObjectStreamClass desc = ObjectStreamClass.lookup(ObjectInputStreamTest.TestClassForSerialization.class);
        oos.writeClassDescriptor(desc);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStreamTest.ObjectIutputStreamWithReadDesc1 ois = new ObjectInputStreamTest.ObjectIutputStreamWithReadDesc1(bais);
        Object obj = ois.readClassDescriptor();
        ois.close();
        TestCase.assertEquals(desc.getClass(), obj.getClass());
        // eof
        bais = new ByteArrayInputStream(bytes);
        ObjectInputStreamTest.ExceptionalBufferedInputStream bis = new ObjectInputStreamTest.ExceptionalBufferedInputStream(bais);
        ois = new ObjectInputStreamTest.ObjectIutputStreamWithReadDesc1(bis);
        bis.setEOF(true);
        try {
            obj = ois.readClassDescriptor();
        } catch (IOException e) {
            // e.printStackTrace();
        } finally {
            ois.close();
        }
        // throw exception
        bais = new ByteArrayInputStream(bytes);
        bis = new ObjectInputStreamTest.ExceptionalBufferedInputStream(bais);
        ois = new ObjectInputStreamTest.ObjectIutputStreamWithReadDesc1(bis);
        bis.setException(new IOException());
        try {
            obj = ois.readClassDescriptor();
        } catch (IOException e) {
            // e.printStackTrace();
        } finally {
            ois.close();
        }
        // corrupt
        bais = new ByteArrayInputStream(bytes);
        bis = new ObjectInputStreamTest.ExceptionalBufferedInputStream(bais);
        ois = new ObjectInputStreamTest.ObjectIutputStreamWithReadDesc1(bis);
        bis.setCorrupt(true);
        try {
            obj = ois.readClassDescriptor();
        } catch (IOException e) {
            // e.printStackTrace();
        } finally {
            ois.close();
        }
    }

    static class ExceptionalBufferedInputStream extends BufferedInputStream {
        private boolean eof = false;

        private IOException exception = null;

        private boolean corrupt = false;

        public ExceptionalBufferedInputStream(InputStream in) {
            super(in);
        }

        @Override
        public int read() throws IOException {
            if ((exception) != null) {
                throw exception;
            }
            if (eof) {
                return -1;
            }
            if (corrupt) {
                return 0;
            }
            return super.read();
        }

        public void setEOF(boolean eof) {
            this.eof = eof;
        }

        public void setException(IOException exception) {
            this.exception = exception;
        }

        public void setCorrupt(boolean corrupt) {
            this.corrupt = corrupt;
        }
    }

    public static class ObjectIutputStreamWithReadDesc2 extends ObjectInputStream {
        private Class returnClass;

        public ObjectIutputStreamWithReadDesc2(InputStream is, Class returnClass) throws IOException {
            super(is);
            this.returnClass = returnClass;
        }

        @Override
        public ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
            ObjectStreamClass osc = super.readClassDescriptor();
            if (osc.getName().equals(returnClass.getName())) {
                return ObjectStreamClass.lookup(returnClass);
            }
            return osc;
        }
    }

    /* Testing classDescriptor replacement with the value generated by
    ObjectStreamClass.lookup() method.
    Regression test for HARMONY-4638
     */
    public void test_readClassDescriptor_1() throws IOException, ClassNotFoundException {
        ObjectInputStreamTest.A a = new ObjectInputStreamTest.A();
        a.name = "It's a test";
        PipedOutputStream pout = new PipedOutputStream();
        PipedInputStream pin = new PipedInputStream(pout);
        ObjectOutputStream out = new ObjectOutputStream(pout);
        ObjectInputStream in = new ObjectInputStreamTest.ObjectIutputStreamWithReadDesc2(pin, ObjectInputStreamTest.A.class);
        // test single object
        out.writeObject(a);
        ObjectInputStreamTest.A a1 = ((ObjectInputStreamTest.A) (in.readObject()));
        TestCase.assertEquals("Single case: incorrectly read the field of A", a.name, a1.name);
        // test cyclic reference
        HashMap m = new HashMap();
        a = new ObjectInputStreamTest.A();
        a.name = "It's a test 0";
        a1 = new ObjectInputStreamTest.A();
        a1.name = "It's a test 1";
        m.put("0", a);
        m.put("1", a1);
        out.writeObject(m);
        HashMap m1 = ((HashMap) (in.readObject()));
        TestCase.assertEquals("Incorrectly read the field of A", a.name, ((ObjectInputStreamTest.A) (m1.get("0"))).name);
        TestCase.assertEquals("Incorrectly read the field of A1", a1.name, ((ObjectInputStreamTest.A) (m1.get("1"))).name);
    }

    public void test_registerValidation() throws Exception {
        // Regression Test for Harmony-2402
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        try {
            ois.registerValidation(null, 256);
            TestCase.fail("NotActiveException should be thrown");
        } catch (NotActiveException nae) {
            // expected
        }
        // Regression Test for Harmony-3916
        baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(new ObjectInputStreamTest.RegisterValidationClass());
        oos.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream fis = new ObjectInputStream(bais);
        // should not throw NotActiveException
        fis.readObject();
    }

    private static class RegisterValidationClass implements Serializable {
        @SuppressWarnings("unused")
        private ObjectInputStreamTest.A a = new ObjectInputStreamTest.A();

        private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
            stream.registerValidation(new ObjectInputStreamTest.MockObjectInputValidation(), 0);
        }
    }

    private static class MockObjectInputValidation implements ObjectInputValidation {
        public void validateObject() throws InvalidObjectException {
        }
    }

    // Regression Test for HARMONY-3726
    public void test_readObject_array() throws Exception {
        final String resourcePrefix = ObjectInputStreamTest.class.getPackage().getName().replace('.', '/');
        // ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("/temp/test_array_strings.ser"));
        // TestArray ta = new TestArray(new String[] { "AAA", "BBB" });
        // oos.writeObject(ta);
        // oos.close();
        // oos = new ObjectOutputStream(new FileOutputStream("/temp/test_array_integers.ser"));
        // ta = new TestArray(new Integer[] { 10, 20 });
        // oos.writeObject(ta);
        // oos.close();
        ObjectInputStream oin = new ObjectInputStream(this.getClass().getClassLoader().getResourceAsStream((("serialization/" + resourcePrefix) + "/test_array_strings.ser")));
        TestArray testArray = ((TestArray) (oin.readObject()));
        String[] strings = new String[]{ "AAA", "BBB" };
        TestCase.assertTrue(Arrays.equals(strings, testArray.array));
        oin = new ObjectInputStream(this.getClass().getClassLoader().getResourceAsStream((("serialization/" + resourcePrefix) + "/test_array_integers.ser")));
        testArray = ((TestArray) (oin.readObject()));
        Integer[] integers = new Integer[]{ 10, 20 };
        TestCase.assertTrue(Arrays.equals(integers, testArray.array));
    }

    public static class TestExtObject implements Externalizable {
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(10);
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            in.readInt();
        }
    }

    static class TestObjectOutputStream extends ObjectOutputStream {
        private ObjectStreamClass[] objs;

        private int pos = 0;

        public TestObjectOutputStream(OutputStream out, ObjectStreamClass[] objs) throws IOException {
            super(out);
            this.objs = objs;
        }

        @Override
        protected void writeClassDescriptor(ObjectStreamClass osc) throws IOException {
            objs[((pos)++)] = osc;
        }
    }

    static class TestObjectInputStream extends ObjectInputStream {
        private ObjectStreamClass[] objs;

        private int pos = 0;

        public TestObjectInputStream(InputStream in, ObjectStreamClass[] objs) throws IOException {
            super(in);
            this.objs = objs;
        }

        @Override
        protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
            return objs[((pos)++)];
        }
    }

    // Regression test for HARMONY-4996
    public void test_readObject_replacedClassDescriptor() throws Exception {
        ObjectStreamClass[] objs = new ObjectStreamClass[1000];
        PipedOutputStream pout = new PipedOutputStream();
        PipedInputStream pin = new PipedInputStream(pout);
        ObjectOutputStream oout = new ObjectInputStreamTest.TestObjectOutputStream(pout, objs);
        oout.writeObject(new ObjectInputStreamTest.TestExtObject());
        oout.writeObject("test");
        oout.close();
        ObjectInputStream oin = new ObjectInputStreamTest.TestObjectInputStream(pin, objs);
        oin.readObject();
        oin.readObject();
    }
}

