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
/**
 *
 *
 * @author Vladimir N. Molotkov
 * @version $Revision$
 */
package org.apache.harmony.security.tests.java.security;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.MDGoldenData;


/**
 * Tests for fields and methods of class <code>DigestInputStream</code>
 */
public class DigestInputStreamTest extends TestCase {
    /**
     * Message digest algorithm name used during testing
     */
    private static final String[] algorithmName = new String[]{ "SHA-1", "SHA", "SHA1", "SHA-256", "SHA-384", "SHA-512", "MD5" };

    /**
     * Chunk size for read(byte, off, len) tests
     */
    private static final int CHUNK_SIZE = 32;

    /**
     * Test message for digest computations
     */
    private static final byte[] myMessage = MDGoldenData.getMessage();

    /**
     * The length of test message
     */
    private static final int MY_MESSAGE_LEN = DigestInputStreamTest.myMessage.length;

    // 
    // Tests
    // 
    /**
     * Test #1 for <code>DigestInputStream</code> constructor<br>
     *
     * Assertion: creates new <code>DigestInputStream</code> instance
     * using valid parameters (both non <code>null</code>)
     *
     * @throws NoSuchAlgorithmException
     * 		
     */
    public final void testDigestInputStream01() {
        for (int i = 0; i < (DigestInputStreamTest.algorithmName.length); i++) {
            try {
                MessageDigest md = MessageDigest.getInstance(DigestInputStreamTest.algorithmName[i]);
                InputStream is = new ByteArrayInputStream(DigestInputStreamTest.myMessage);
                InputStream dis = new DigestInputStream(is, md);
                TestCase.assertTrue((dis instanceof DigestInputStream));
                return;
            } catch (NoSuchAlgorithmException e) {
                // allowed failure
            }
        }
        TestCase.fail(((getName()) + ": no MessageDigest algorithms available - test not performed"));
    }

    /**
     * Test #2 for <code>DigestInputStream</code> constructor<br>
     *
     * Assertion: creates new <code>DigestInputStream</code> instance
     * using valid parameters (both <code>null</code>)
     */
    public final void testDigestInputStream02() {
        InputStream dis = new DigestInputStream(null, null);
        TestCase.assertTrue((dis instanceof DigestInputStream));
    }

    /**
     * Test #1 for <code>read()</code> method<br>
     *
     * Assertion: returns the byte read<br>
     * Assertion: updates associated digest<br>
     */
    public final void testRead01() throws IOException {
        for (int ii = 0; ii < (DigestInputStreamTest.algorithmName.length); ii++) {
            try {
                MessageDigest md = MessageDigest.getInstance(DigestInputStreamTest.algorithmName[ii]);
                InputStream is = new ByteArrayInputStream(DigestInputStreamTest.myMessage);
                DigestInputStream dis = new DigestInputStream(is, md);
                for (int i = 0; i < (DigestInputStreamTest.MY_MESSAGE_LEN); i++) {
                    // check that read() returns valid values
                    TestCase.assertTrue("retval", (((byte) (dis.read())) == (DigestInputStreamTest.myMessage[i])));
                }
                // check that associated digest has been updated properly
                TestCase.assertTrue("update", Arrays.equals(dis.getMessageDigest().digest(), MDGoldenData.getDigest(DigestInputStreamTest.algorithmName[ii])));
                return;
            } catch (NoSuchAlgorithmException e) {
                // allowed failure
            }
        }
        TestCase.fail(((getName()) + ": no MessageDigest algorithms available - test not performed"));
    }

    /**
     * Test #2 for <code>read()</code> method<br>
     *
     * Assertion: returns -1 if EOS had been
     * reached but not read before method call<br>
     *
     * Assertion: must not update digest if EOS had been
     * reached but not read before method call<br>
     */
    public final void testRead02() throws IOException {
        for (int ii = 0; ii < (DigestInputStreamTest.algorithmName.length); ii++) {
            try {
                MessageDigest md = MessageDigest.getInstance(DigestInputStreamTest.algorithmName[ii]);
                InputStream is = new ByteArrayInputStream(DigestInputStreamTest.myMessage);
                DigestInputStream dis = new DigestInputStream(is, md);
                for (int i = 0; i < (DigestInputStreamTest.MY_MESSAGE_LEN); i++) {
                    dis.read();
                }
                // check that subsequent read() calls return -1 (eos)
                TestCase.assertEquals("retval1", (-1), dis.read());
                TestCase.assertEquals("retval2", (-1), dis.read());
                TestCase.assertEquals("retval3", (-1), dis.read());
                // check that 3 previous read() calls did not update digest
                TestCase.assertTrue("update", Arrays.equals(dis.getMessageDigest().digest(), MDGoldenData.getDigest(DigestInputStreamTest.algorithmName[ii])));
                return;
            } catch (NoSuchAlgorithmException e) {
                // allowed failure
            }
        }
        TestCase.fail(((getName()) + ": no MessageDigest algorithms available - test not performed"));
    }

    /**
     * Test #3 for <code>read()</code> method<br>
     * Test #1 for <code>on(boolean)</code> method<br>
     *
     * Assertion: <code>read()</code> must not update digest if it is off<br>
     * Assertion: <code>on(boolean)</code> turns digest functionality on
     * (if <code>true</code> passed as a parameter) or off (if <code>false</code>
     *  passed)
     */
    public final void testRead03() throws IOException {
        for (int ii = 0; ii < (DigestInputStreamTest.algorithmName.length); ii++) {
            try {
                MessageDigest md = MessageDigest.getInstance(DigestInputStreamTest.algorithmName[ii]);
                InputStream is = new ByteArrayInputStream(DigestInputStreamTest.myMessage);
                DigestInputStream dis = new DigestInputStream(is, md);
                // turn digest off
                dis.on(false);
                for (int i = 0; i < (DigestInputStreamTest.MY_MESSAGE_LEN); i++) {
                    dis.read();
                }
                // check that digest value has not been updated by read()
                TestCase.assertTrue(Arrays.equals(dis.getMessageDigest().digest(), MDGoldenData.getDigest(((DigestInputStreamTest.algorithmName[ii]) + "_NU"))));
                return;
            } catch (NoSuchAlgorithmException e) {
                // allowed failure
            }
        }
        TestCase.fail(((getName()) + ": no MessageDigest algorithms available - test not performed"));
    }

    /**
     * Test #4 for <code>read()</code> method<br>
     *
     * Assertion: broken <code>DigestInputStream</code>instance:
     * <code>InputStream</code> not set. <code>read()</code> must
     * not work
     */
    public final void testRead04() throws IOException {
        for (int ii = 0; ii < (DigestInputStreamTest.algorithmName.length); ii++) {
            try {
                MessageDigest md = MessageDigest.getInstance(DigestInputStreamTest.algorithmName[ii]);
                DigestInputStream dis = new DigestInputStream(null, md);
                // must result in an exception
                try {
                    for (int i = 0; i < (DigestInputStreamTest.MY_MESSAGE_LEN); i++) {
                        dis.read();
                    }
                } catch (Exception e) {
                    // Expected.
                    return;
                }
                TestCase.fail("InputStream not set. read() must not work");
            } catch (NoSuchAlgorithmException e) {
                // allowed failure
            }
        }
        TestCase.fail(((getName()) + ": no MessageDigest algorithms available - test not performed"));
    }

    /**
     * Test #5 for <code>read()</code> method<br>
     *
     * Assertion: broken <code>DigestInputStream</code>instance:
     * associated <code>MessageDigest</code> not set.
     * <code>read()</code> must not work when digest
     * functionality is on
     */
    public final void testRead05() {
        InputStream is = new ByteArrayInputStream(DigestInputStreamTest.myMessage);
        DigestInputStream dis = new DigestInputStream(is, null);
        // must result in an exception
        try {
            for (int i = 0; i < (DigestInputStreamTest.MY_MESSAGE_LEN); i++) {
                dis.read();
            }
            TestCase.fail("read() must not work when digest functionality is on");
        } catch (Exception e) {
            // Expected.
        }
    }

    /**
     * Test #6 for <code>read()</code> method<br>
     * Test #2 for <code>on(boolean)</code> method<br>
     *
     * Assertion: broken <code>DigestInputStream</code>instance:
     * associated <code>MessageDigest</code> not set.
     * <code>read()</code> must work when digest
     * functionality is off
     */
    public final void testRead06() throws IOException {
        InputStream is = new ByteArrayInputStream(DigestInputStreamTest.myMessage);
        // construct object without digest
        DigestInputStream dis = new DigestInputStream(is, null);
        // set digest functionality to off
        dis.on(false);
        // the following must pass without any exception
        for (int i = 0; i < (DigestInputStreamTest.MY_MESSAGE_LEN); i++) {
            TestCase.assertTrue((((byte) (dis.read())) == (DigestInputStreamTest.myMessage[i])));
        }
    }

    /**
     * Test #1 for <code>read(byte[],int,int)</code> method<br>
     *
     * Assertion: returns the number of bytes read<br>
     *
     * Assertion: put bytes read into specified array at specified offset<br>
     *
     * Assertion: updates associated digest<br>
     */
    public final void testReadbyteArrayintint01() throws IOException {
        for (int ii = 0; ii < (DigestInputStreamTest.algorithmName.length); ii++) {
            try {
                MessageDigest md = MessageDigest.getInstance(DigestInputStreamTest.algorithmName[ii]);
                InputStream is = new ByteArrayInputStream(DigestInputStreamTest.myMessage);
                DigestInputStream dis = new DigestInputStream(is, md);
                byte[] bArray = new byte[DigestInputStreamTest.MY_MESSAGE_LEN];
                // check that read(byte[],int,int) returns valid value
                TestCase.assertTrue("retval", ((dis.read(bArray, 0, bArray.length)) == (DigestInputStreamTest.MY_MESSAGE_LEN)));
                // check that bArray has been filled properly
                TestCase.assertTrue("bArray", Arrays.equals(DigestInputStreamTest.myMessage, bArray));
                // check that associated digest has been updated properly
                TestCase.assertTrue("update", Arrays.equals(dis.getMessageDigest().digest(), MDGoldenData.getDigest(DigestInputStreamTest.algorithmName[ii])));
                return;
            } catch (NoSuchAlgorithmException e) {
                // allowed failure
            }
        }
        TestCase.fail(((getName()) + ": no MessageDigest algorithms available - test not performed"));
    }

    /**
     * Test #2 for <code>read(byte[],int,int)</code> method<br>
     *
     * Assertion: returns the number of bytes read<br>
     *
     * Assertion: put bytes read into specified array at specified offset<br>
     *
     * Assertion: updates associated digest<br>
     */
    public final void testReadbyteArrayintint02() throws IOException {
        // check precondition
        TestCase.assertEquals(0, ((DigestInputStreamTest.MY_MESSAGE_LEN) % (DigestInputStreamTest.CHUNK_SIZE)));
        for (int ii = 0; ii < (DigestInputStreamTest.algorithmName.length); ii++) {
            try {
                MessageDigest md = MessageDigest.getInstance(DigestInputStreamTest.algorithmName[ii]);
                InputStream is = new ByteArrayInputStream(DigestInputStreamTest.myMessage);
                DigestInputStream dis = new DigestInputStream(is, md);
                byte[] bArray = new byte[DigestInputStreamTest.MY_MESSAGE_LEN];
                for (int i = 0; i < ((DigestInputStreamTest.MY_MESSAGE_LEN) / (DigestInputStreamTest.CHUNK_SIZE)); i++) {
                    // check that read(byte[],int,int) returns valid value
                    TestCase.assertTrue("retval", ((dis.read(bArray, (i * (DigestInputStreamTest.CHUNK_SIZE)), DigestInputStreamTest.CHUNK_SIZE)) == (DigestInputStreamTest.CHUNK_SIZE)));
                }
                // check that bArray has been filled properly
                TestCase.assertTrue("bArray", Arrays.equals(DigestInputStreamTest.myMessage, bArray));
                // check that associated digest has been updated properly
                TestCase.assertTrue("update", Arrays.equals(dis.getMessageDigest().digest(), MDGoldenData.getDigest(DigestInputStreamTest.algorithmName[ii])));
                return;
            } catch (NoSuchAlgorithmException e) {
                // allowed failure
            }
        }
        TestCase.fail(((getName()) + ": no MessageDigest algorithms available - test not performed"));
    }

    /**
     * Test #3 for <code>read(byte[],int,int)</code> method<br>
     *
     * Assertion: returns the number of bytes read<br>
     *
     * Assertion: put bytes read into specified array at specified offset<br>
     *
     * Assertion: updates associated digest<br>
     */
    public final void testReadbyteArrayintint03() throws IOException {
        // check precondition
        TestCase.assertTrue((((DigestInputStreamTest.MY_MESSAGE_LEN) % ((DigestInputStreamTest.CHUNK_SIZE) + 1)) != 0));
        for (int ii = 0; ii < (DigestInputStreamTest.algorithmName.length); ii++) {
            try {
                MessageDigest md = MessageDigest.getInstance(DigestInputStreamTest.algorithmName[ii]);
                InputStream is = new ByteArrayInputStream(DigestInputStreamTest.myMessage);
                DigestInputStream dis = new DigestInputStream(is, md);
                byte[] bArray = new byte[DigestInputStreamTest.MY_MESSAGE_LEN];
                for (int i = 0; i < ((DigestInputStreamTest.MY_MESSAGE_LEN) / ((DigestInputStreamTest.CHUNK_SIZE) + 1)); i++) {
                    // check that read(byte[],int,int) returns valid value
                    TestCase.assertTrue("retval1", ((dis.read(bArray, (i * ((DigestInputStreamTest.CHUNK_SIZE) + 1)), ((DigestInputStreamTest.CHUNK_SIZE) + 1))) == ((DigestInputStreamTest.CHUNK_SIZE) + 1)));
                }
                // check that last call returns right
                // number of remaining bytes
                TestCase.assertTrue("retval2", ((dis.read(bArray, (((DigestInputStreamTest.MY_MESSAGE_LEN) / ((DigestInputStreamTest.CHUNK_SIZE) + 1)) * ((DigestInputStreamTest.CHUNK_SIZE) + 1)), ((DigestInputStreamTest.MY_MESSAGE_LEN) % ((DigestInputStreamTest.CHUNK_SIZE) + 1)))) == ((DigestInputStreamTest.MY_MESSAGE_LEN) % ((DigestInputStreamTest.CHUNK_SIZE) + 1))));
                // check that bArray has been filled properly
                TestCase.assertTrue("bArray", Arrays.equals(DigestInputStreamTest.myMessage, bArray));
                // check that associated digest has been updated properly
                TestCase.assertTrue("update", Arrays.equals(dis.getMessageDigest().digest(), MDGoldenData.getDigest(DigestInputStreamTest.algorithmName[ii])));
                return;
            } catch (NoSuchAlgorithmException e) {
                // allowed failure
            }
        }
        TestCase.fail(((getName()) + ": no MessageDigest algorithms available - test not performed"));
    }

    /**
     * Test #4 for <code>read(byte[],int,int)</code> method<br>
     *
     * Assertion: returns the number of bytes read<br>
     *
     * Assertion: updates associated digest<br>
     */
    public final void testReadbyteArrayintint04() throws IOException {
        for (int ii = 0; ii < (DigestInputStreamTest.algorithmName.length); ii++) {
            try {
                MessageDigest md = MessageDigest.getInstance(DigestInputStreamTest.algorithmName[ii]);
                InputStream is = new ByteArrayInputStream(DigestInputStreamTest.myMessage);
                DigestInputStream dis = new DigestInputStream(is, md);
                byte[] bArray = new byte[DigestInputStreamTest.MY_MESSAGE_LEN];
                // read all but EOS
                dis.read(bArray, 0, bArray.length);
                // check that subsequent read(byte[],int,int) calls return -1 (EOS)
                TestCase.assertEquals("retval1", (-1), dis.read(bArray, 0, 1));
                TestCase.assertEquals("retval2", (-1), dis.read(bArray, 0, bArray.length));
                TestCase.assertEquals("retval3", (-1), dis.read(bArray, 0, 1));
                // check that 3 previous read() calls did not update digest
                TestCase.assertTrue("update", Arrays.equals(dis.getMessageDigest().digest(), MDGoldenData.getDigest(DigestInputStreamTest.algorithmName[ii])));
                return;
            } catch (NoSuchAlgorithmException e) {
                // allowed failure
            }
        }
        TestCase.fail(((getName()) + ": no MessageDigest algorithms available - test not performed"));
    }

    /**
     * Test #5 for <code>read(byte[],int,int)</code> method<br>
     *
     * Assertion: returns the number of bytes read<br>
     *
     * Assertion: put bytes read into specified array at specified offset<br>
     *
     * Assertion: does not update associated digest if
     * digest functionality is off<br>
     */
    public final void testReadbyteArrayintint05() throws IOException {
        // check precondition
        TestCase.assertEquals(0, ((DigestInputStreamTest.MY_MESSAGE_LEN) % (DigestInputStreamTest.CHUNK_SIZE)));
        for (int ii = 0; ii < (DigestInputStreamTest.algorithmName.length); ii++) {
            try {
                MessageDigest md = MessageDigest.getInstance(DigestInputStreamTest.algorithmName[ii]);
                InputStream is = new ByteArrayInputStream(DigestInputStreamTest.myMessage);
                DigestInputStream dis = new DigestInputStream(is, md);
                byte[] bArray = new byte[DigestInputStreamTest.MY_MESSAGE_LEN];
                // turn digest off
                dis.on(false);
                for (int i = 0; i < ((DigestInputStreamTest.MY_MESSAGE_LEN) / (DigestInputStreamTest.CHUNK_SIZE)); i++) {
                    dis.read(bArray, (i * (DigestInputStreamTest.CHUNK_SIZE)), DigestInputStreamTest.CHUNK_SIZE);
                }
                // check that digest has not been updated
                TestCase.assertTrue(Arrays.equals(dis.getMessageDigest().digest(), MDGoldenData.getDigest(((DigestInputStreamTest.algorithmName[ii]) + "_NU"))));
                return;
            } catch (NoSuchAlgorithmException e) {
                // allowed failure
            }
        }
        TestCase.fail(((getName()) + ": no MessageDigest algorithms available - test not performed"));
    }

    /**
     * Test for <code>getMessageDigest()</code> method<br>
     *
     * Assertion: returns associated message digest<br>
     */
    public final void testGetMessageDigest() {
        for (int ii = 0; ii < (DigestInputStreamTest.algorithmName.length); ii++) {
            try {
                MessageDigest md = MessageDigest.getInstance(DigestInputStreamTest.algorithmName[ii]);
                DigestInputStream dis = new DigestInputStream(null, md);
                TestCase.assertTrue(((dis.getMessageDigest()) == md));
                return;
            } catch (NoSuchAlgorithmException e) {
                // allowed failure
            }
        }
        TestCase.fail(((getName()) + ": no MessageDigest algorithms available - test not performed"));
    }

    /**
     * Test for <code>setMessageDigest()</code> method<br>
     *
     * Assertion: set associated message digest<br>
     */
    public final void testSetMessageDigest() {
        for (int ii = 0; ii < (DigestInputStreamTest.algorithmName.length); ii++) {
            try {
                DigestInputStream dis = new DigestInputStream(null, null);
                MessageDigest md = MessageDigest.getInstance(DigestInputStreamTest.algorithmName[ii]);
                dis.setMessageDigest(md);
                TestCase.assertTrue(((dis.getMessageDigest()) == md));
                return;
            } catch (NoSuchAlgorithmException e) {
                // allowed failure
            }
        }
        TestCase.fail(((getName()) + ": no MessageDigest algorithms available - test not performed"));
    }

    /**
     * Test for <code>on()</code> method<br>
     * Assertion: turns digest functionality on or off
     */
    public final void testOn() throws IOException {
        for (int ii = 0; ii < (DigestInputStreamTest.algorithmName.length); ii++) {
            try {
                MessageDigest md = MessageDigest.getInstance(DigestInputStreamTest.algorithmName[ii]);
                InputStream is = new ByteArrayInputStream(DigestInputStreamTest.myMessage);
                DigestInputStream dis = new DigestInputStream(is, md);
                // turn digest off
                dis.on(false);
                for (int i = 0; i < ((DigestInputStreamTest.MY_MESSAGE_LEN) - 1); i++) {
                    dis.read();
                }
                // turn digest on
                dis.on(true);
                // read remaining byte
                dis.read();
                byte[] digest = dis.getMessageDigest().digest();
                // check that digest value has been
                // updated by the last read() call
                TestCase.assertFalse(((Arrays.equals(digest, MDGoldenData.getDigest(DigestInputStreamTest.algorithmName[ii]))) || (Arrays.equals(digest, MDGoldenData.getDigest(((DigestInputStreamTest.algorithmName[ii]) + "_NU"))))));
                return;
            } catch (NoSuchAlgorithmException e) {
                // allowed failure
            }
        }
        TestCase.fail(((getName()) + ": no MessageDigest algorithms available - test not performed"));
    }

    /**
     * Test for <code>toString()</code> method<br>
     * Assertion: returns <code>String</code> representation of this object
     */
    public final void testToString() {
        for (int ii = 0; ii < (DigestInputStreamTest.algorithmName.length); ii++) {
            try {
                MessageDigest md = MessageDigest.getInstance(DigestInputStreamTest.algorithmName[ii]);
                InputStream is = new ByteArrayInputStream(DigestInputStreamTest.myMessage);
                DigestInputStream dis = new DigestInputStream(is, md);
                TestCase.assertNotNull(dis.toString());
                return;
            } catch (NoSuchAlgorithmException e) {
                // allowed failure
            }
        }
        TestCase.fail(((getName()) + ": no MessageDigest algorithms available - test not performed"));
    }
}

