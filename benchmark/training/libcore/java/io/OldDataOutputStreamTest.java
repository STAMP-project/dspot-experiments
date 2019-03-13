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


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import junit.framework.TestCase;
import tests.support.Support_OutputStream;


public class OldDataOutputStreamTest extends TestCase {
    private DataOutputStream os;

    private DataInputStream dis;

    private ByteArrayOutputStream bos;

    private Support_OutputStream sos;

    String unihw = "Hello World";

    private static final String testString = "Lorem ipsum dolor sit amet,\n" + ("consectetur adipisicing elit,\nsed do eiusmod tempor incididunt ut" + "labore et dolore magna aliqua.\n");

    private static final int testLength = OldDataOutputStreamTest.testString.length();

    public void test_flush() throws IOException {
        BufferedOutputStream buf = new BufferedOutputStream(bos);
        os = new DataOutputStream(buf);
        os.writeInt(9087589);
        TestCase.assertTrue("Test 1: Written data should not be available.", ((bos.toByteArray().length) == 0));
        os.flush();
        TestCase.assertTrue("Test 2: Written data should be available.", ((bos.toByteArray().length) > 0));
        os.close();
        openDataInputStream();
        int c = dis.readInt();
        TestCase.assertEquals("Test 3: Failed to flush correctly;", 9087589, c);
        dis.close();
        os = new DataOutputStream(sos);
        try {
            os.flush();
            TestCase.fail("Test 4: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    public void test_write$BII() throws IOException {
        int r;
        os.write(OldDataOutputStreamTest.testString.getBytes(), 5, ((OldDataOutputStreamTest.testLength) - 7));
        os.close();
        openDataInputStream();
        byte[] rbuf = new byte[OldDataOutputStreamTest.testLength];
        r = dis.read(rbuf, 0, OldDataOutputStreamTest.testLength);
        TestCase.assertEquals("Test 1: Incorrect number of bytes read;", ((OldDataOutputStreamTest.testLength) - 7), r);
        dis.close();
        TestCase.assertTrue("Test 2: Incorrect bytes written or read.", new String(rbuf, 0, r).equals(OldDataOutputStreamTest.testString.substring(5, ((OldDataOutputStreamTest.testLength) - 2))));
    }

    public void test_write$BII_Exception() throws IOException {
        byte[] nullByteArray = null;
        byte[] byteArray = new byte[10];
        try {
            os.write(nullByteArray, 0, 1);
            TestCase.fail("Test 1: NullPointerException expected.");
        } catch (NullPointerException e) {
            // Expected.
        }
        try {
            os.write(byteArray, (-1), 1);
            TestCase.fail("Test 2: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected.
        }
        try {
            os.write(byteArray, 0, (-1));
            TestCase.fail("Test 3: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected.
        }
        try {
            os.write(byteArray, 1, 10);
            TestCase.fail("Test 4: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected.
        }
    }

    public void test_writeI() throws IOException {
        os.write(42);
        os.close();
        openDataInputStream();
        TestCase.assertEquals("Test 1: Incorrect int written or read;", 42, dis.read());
        dis.close();
        os = new DataOutputStream(sos);
        try {
            os.write(42);
            TestCase.fail("Test 2: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    public void test_writeBytesLjava_lang_String() throws IOException {
        os.writeBytes(OldDataOutputStreamTest.testString);
        os.close();
        openDataInputStream();
        byte[] rbuf = new byte[OldDataOutputStreamTest.testLength];
        dis.read(rbuf, 0, OldDataOutputStreamTest.testLength);
        dis.close();
        TestCase.assertTrue("Test 1: Incorrect bytes written or read.", new String(rbuf, 0, OldDataOutputStreamTest.testLength).equals(OldDataOutputStreamTest.testString));
        os = new DataOutputStream(sos);
        try {
            os.writeBytes(OldDataOutputStreamTest.testString);
            TestCase.fail("Test 2: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    public void test_writeCharsLjava_lang_String() throws IOException {
        os.writeChars(unihw);
        os.close();
        openDataInputStream();
        char[] chars = new char[unihw.length()];
        int i;
        int a = (dis.available()) / 2;
        for (i = 0; i < a; i++)
            chars[i] = dis.readChar();

        TestCase.assertEquals("Test 1: Incorrect chars written or read;", unihw, new String(chars, 0, i));
        dis.close();
        os = new DataOutputStream(sos);
        try {
            os.writeChars(unihw);
            TestCase.fail("Test 2: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }
}

