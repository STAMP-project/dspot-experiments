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
package libcore.java.io;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamConstants;
import java.io.OutputStream;
import java.io.Serializable;
import junit.framework.TestCase;
import tests.support.Support_ASimpleOutputStream;
import tests.support.Support_OutputStream;


public class OldObjectOutputStreamTest extends TestCase implements Serializable {
    static final long serialVersionUID = 1L;

    File f;

    public class SerializableTestHelper implements Serializable {
        public String aField1;

        public String aField2;

        SerializableTestHelper(String s, String t) {
            aField1 = s;
            aField2 = t;
        }

        private void readObject(ObjectInputStream ois) throws IOException {
            // note aField2 is not read
            try {
                ObjectInputStream.GetField fields = ois.readFields();
                aField1 = ((String) (fields.get("aField1", "Zap")));
            } catch (Exception e) {
            }
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

        public String getText2() {
            return aField2;
        }
    }

    private static class BasicObjectOutputStream extends ObjectOutputStream {
        public boolean writeStreamHeaderCalled;

        public BasicObjectOutputStream() throws IOException, SecurityException {
            super();
            writeStreamHeaderCalled = false;
        }

        public BasicObjectOutputStream(OutputStream output) throws IOException {
            super(output);
        }

        public void drain() throws IOException {
            super.drain();
        }

        public boolean enableReplaceObject(boolean enable) throws SecurityException {
            return super.enableReplaceObject(enable);
        }

        public void writeObjectOverride(Object object) throws IOException {
            super.writeObjectOverride(object);
        }

        public void writeStreamHeader() throws IOException {
            super.writeStreamHeader();
            writeStreamHeaderCalled = true;
        }
    }

    private static class NoFlushTestOutputStream extends ByteArrayOutputStream {
        public boolean flushCalled;

        public NoFlushTestOutputStream() {
            super();
            flushCalled = false;
        }

        public void flush() throws IOException {
            super.flush();
            flushCalled = true;
        }
    }

    protected static final String MODE_XLOAD = "xload";

    protected static final String MODE_XDUMP = "xdump";

    static final String FOO = "foo";

    static final String MSG_WITE_FAILED = "Failed to write: ";

    private static final boolean DEBUG = false;

    protected static boolean xload = false;

    protected static boolean xdump = false;

    protected static String xFileName = null;

    protected ObjectInputStream ois;

    protected ObjectOutputStream oos;

    protected ObjectOutputStream oos_ioe;

    protected Support_OutputStream sos;

    protected ByteArrayOutputStream bao;

    static final int INIT_INT_VALUE = 7;

    static final String INIT_STR_VALUE = "a string that is blortz";

    /**
     * java.io.ObjectOutputStream#ObjectOutputStream(java.io.OutputStream)
     */
    public void test_ConstructorLjava_io_OutputStream() throws IOException {
        oos.close();
        oos = new ObjectOutputStream(new ByteArrayOutputStream());
        oos.close();
        try {
            oos = new ObjectOutputStream(null);
            TestCase.fail("Test 1: NullPointerException expected.");
        } catch (NullPointerException e) {
            // Expected.
        }
        Support_ASimpleOutputStream sos = new Support_ASimpleOutputStream(true);
        try {
            oos = new ObjectOutputStream(sos);
            TestCase.fail("Test 2: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    public void test_close() throws IOException {
        int outputSize = bao.size();
        // Writing of a primitive type should be buffered.
        oos.writeInt(42);
        TestCase.assertTrue("Test 1: Primitive data unexpectedly written to the target stream.", ((bao.size()) == outputSize));
        // Closing should write the buffered data to the target stream.
        oos.close();
        TestCase.assertTrue("Test 2: Primitive data has not been written to the the target stream.", ((bao.size()) > outputSize));
        try {
            oos_ioe.close();
            TestCase.fail("Test 3: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    public void test_drain() throws IOException {
        OldObjectOutputStreamTest.NoFlushTestOutputStream target = new OldObjectOutputStreamTest.NoFlushTestOutputStream();
        OldObjectOutputStreamTest.BasicObjectOutputStream boos = new OldObjectOutputStreamTest.BasicObjectOutputStream(target);
        int initialSize = target.size();
        boolean written = false;
        boos.writeBytes("Lorem ipsum");
        // If there is no buffer then the bytes have already been written.
        written = (target.size()) > initialSize;
        boos.drain();
        TestCase.assertTrue("Content has not been written to the target.", (written || ((target.size()) > initialSize)));
        TestCase.assertFalse("flush() has been called on the target.", target.flushCalled);
    }

    public void test_enableReplaceObjectB() throws IOException {
        // Start testing without a SecurityManager.
        OldObjectOutputStreamTest.BasicObjectOutputStream boos = new OldObjectOutputStreamTest.BasicObjectOutputStream();
        TestCase.assertFalse("Test 1: Object resolving must be disabled by default.", boos.enableReplaceObject(true));
        TestCase.assertTrue("Test 2: enableReplaceObject did not return the previous value.", boos.enableReplaceObject(false));
    }

    public void test_flush() throws Exception {
        // Test for method void java.io.ObjectOutputStream.flush()
        int size = bao.size();
        oos.writeByte(127);
        TestCase.assertTrue("Test 1: Data already flushed.", ((bao.size()) == size));
        oos.flush();
        TestCase.assertTrue("Test 2: Failed to flush data.", ((bao.size()) > size));
        try {
            oos_ioe.flush();
            TestCase.fail("Test 3: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    public void test_putFields() throws Exception {
        /* "SerializableTestHelper" is an object created for these tests with
        two fields (Strings) and simple implementations of readObject and
        writeObject which simply read and write the first field but not the
        second one.
         */
        OldObjectOutputStreamTest.SerializableTestHelper sth;
        try {
            oos.putFields();
            TestCase.fail("Test 1: NotActiveException expected.");
        } catch (NotActiveException e) {
            // Expected.
        }
        oos.writeObject(new OldObjectOutputStreamTest.SerializableTestHelper("Gabba", "Jabba"));
        oos.flush();
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        sth = ((OldObjectOutputStreamTest.SerializableTestHelper) (ois.readObject()));
        TestCase.assertEquals("Test 2: readFields or writeFields failed; first field not set.", "Gabba", sth.getText1());
        TestCase.assertNull("Test 3: readFields or writeFields failed; second field should not have been set.", sth.getText2());
    }

    public void test_reset() throws Exception {
        String o = "HelloWorld";
        sos = new Support_OutputStream(200);
        oos.close();
        oos = new ObjectOutputStream(sos);
        oos.writeObject(o);
        oos.writeObject(o);
        oos.reset();
        oos.writeObject(o);
        sos.setThrowsException(true);
        try {
            oos.reset();
            TestCase.fail("Test 1: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
        sos.setThrowsException(false);
        ois = new ObjectInputStream(new ByteArrayInputStream(sos.toByteArray()));
        TestCase.assertEquals("Test 2: Incorrect object read.", o, ois.readObject());
        TestCase.assertEquals("Test 3: Incorrect object read.", o, ois.readObject());
        TestCase.assertEquals("Test 4: Incorrect object read.", o, ois.readObject());
        ois.close();
    }

    public void test_write$BII() throws Exception {
        byte[] buf = new byte[10];
        ois = new ObjectInputStream(new ByteArrayInputStream(bao.toByteArray()));
        try {
            ois.read(buf, 0, (-1));
            TestCase.fail("IndexOutOfBoundsException not thrown");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            ois.read(buf, (-1), 1);
            TestCase.fail("IndexOutOfBoundsException not thrown");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            ois.read(buf, 10, 1);
            TestCase.fail("IndexOutOfBoundsException not thrown");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        ois.close();
    }

    public void test_writeObjectOverrideLjava_lang_Object() throws IOException {
        OldObjectOutputStreamTest.BasicObjectOutputStream boos = new OldObjectOutputStreamTest.BasicObjectOutputStream(new ByteArrayOutputStream());
        try {
            boos.writeObjectOverride(new Object());
            TestCase.fail("IOException expected.");
        } catch (IOException e) {
        } finally {
            boos.close();
        }
    }

    public void test_writeStreamHeader() throws IOException {
        OldObjectOutputStreamTest.BasicObjectOutputStream boos;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        short s;
        byte[] buffer;
        // Test 1: Make sure that writeStreamHeader() has been called.
        boos = new OldObjectOutputStreamTest.BasicObjectOutputStream(baos);
        try {
            TestCase.assertTrue("Test 1: writeStreamHeader() has not been called.", boos.writeStreamHeaderCalled);
            // Test 2: Check that at least four bytes have been written.
            buffer = baos.toByteArray();
            TestCase.assertTrue("Test 2: At least four bytes should have been written", ((buffer.length) >= 4));
            // Test 3: Check the magic number.
            s = buffer[0];
            s <<= 8;
            s += ((short) (buffer[1])) & 255;
            TestCase.assertEquals("Test 3: Invalid magic number written.", ObjectStreamConstants.STREAM_MAGIC, s);
            // Test 4: Check the stream version number.
            s = buffer[2];
            s <<= 8;
            s += ((short) (buffer[3])) & 255;
            TestCase.assertEquals("Invalid stream version number written.", ObjectStreamConstants.STREAM_VERSION, s);
        } finally {
            boos.close();
        }
    }
}

