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


import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.NonWritableChannelException;
import junit.framework.TestCase;


public class OldRandomAccessFileTest extends TestCase {
    public String fileName;

    public boolean ufile = true;

    RandomAccessFile raf;

    File f;

    String unihw = "Hel\u0801lo W\u0081orld";

    static final String testString = "Lorem ipsum dolor sit amet,\n" + ("consectetur adipisicing elit,\nsed do eiusmod tempor incididunt ut" + "labore et dolore magna aliqua.\n");

    static final int testLength = OldRandomAccessFileTest.testString.length();

    /**
     * java.io.RandomAccessFile#RandomAccessFile(java.io.File,
     *        java.lang.String)
     */
    public void test_ConstructorLjava_io_FileLjava_lang_String() throws Exception {
        RandomAccessFile raf = null;
        File tmpFile = new File(fileName);
        try {
            raf = new RandomAccessFile(tmpFile, "r");
            TestCase.fail("Test 1: FileNotFoundException expected.");
        } catch (FileNotFoundException e) {
            // Expected.
        } catch (IllegalArgumentException e) {
            TestCase.fail(("Test 2: Unexpected IllegalArgumentException: " + (e.getMessage())));
        }
        tmpFile.createNewFile();
        try {
            // Checking the remaining valid mode parameters.
            try {
                raf = new RandomAccessFile(tmpFile, "rwd");
            } catch (IllegalArgumentException e) {
                TestCase.fail(("Test 3: Unexpected IllegalArgumentException: " + (e.getMessage())));
            }
            raf.close();
            try {
                raf = new RandomAccessFile(tmpFile, "rws");
            } catch (IllegalArgumentException e) {
                TestCase.fail(("Test 4: Unexpected IllegalArgumentException: " + (e.getMessage())));
            }
            raf.close();
            try {
                raf = new RandomAccessFile(tmpFile, "rw");
            } catch (IllegalArgumentException e) {
                TestCase.fail(("Test 5: Unexpected IllegalArgumentException: " + (e.getMessage())));
            }
            raf.close();
            // Checking an invalid mode parameter.
            try {
                raf = new RandomAccessFile(tmpFile, "i");
                TestCase.fail("Test 6: IllegalArgumentException expected.");
            } catch (IllegalArgumentException e) {
                // Expected.
            }
        } finally {
            if (raf != null)
                raf.close();

            tmpFile.delete();
        }
    }

    /**
     * java.io.RandomAccessFile#RandomAccessFile(java.lang.String,
     *        java.lang.String)
     */
    public void test_ConstructorLjava_lang_StringLjava_lang_String() throws IOException {
        RandomAccessFile raf = null;
        File tmpFile = new File(fileName);
        try {
            raf = new RandomAccessFile(fileName, "r");
            TestCase.fail("Test 1: FileNotFoundException expected.");
        } catch (FileNotFoundException e) {
            // Expected.
        } catch (IllegalArgumentException e) {
            TestCase.fail(("Test 2: Unexpected IllegalArgumentException: " + (e.getMessage())));
        }
        try {
            // Checking the remaining valid mode parameters.
            try {
                raf = new RandomAccessFile(fileName, "rwd");
            } catch (IllegalArgumentException e) {
                TestCase.fail(("Test 3: Unexpected IllegalArgumentException: " + (e.getMessage())));
            }
            raf.close();
            try {
                raf = new RandomAccessFile(fileName, "rws");
            } catch (IllegalArgumentException e) {
                TestCase.fail(("Test 4: Unexpected IllegalArgumentException: " + (e.getMessage())));
            }
            raf.close();
            try {
                raf = new RandomAccessFile(fileName, "rw");
            } catch (IllegalArgumentException e) {
                TestCase.fail(("Test 5: Unexpected IllegalArgumentException: " + (e.getMessage())));
            }
            raf.close();
            // Checking an invalid mode parameter.
            try {
                raf = new RandomAccessFile(fileName, "i");
                TestCase.fail("Test 6: IllegalArgumentException expected.");
            } catch (IllegalArgumentException e) {
                // Expected.
            }
            // Checking for NoWritableChannelException.
            raf = new RandomAccessFile(fileName, "r");
            FileChannel fcr = raf.getChannel();
            try {
                fcr.lock(0L, Long.MAX_VALUE, false);
                TestCase.fail("Test 7: NonWritableChannelException expected.");
            } catch (NonWritableChannelException e) {
                // Expected.
            }
        } finally {
            if (raf != null)
                raf.close();

            if (tmpFile.exists())
                tmpFile.delete();

        }
    }

    /**
     * java.io.RandomAccessFile#close()
     */
    public void test_close() {
        // Test for method void java.io.RandomAccessFile.close()
        try {
            RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
            raf.close();
            raf.write("Test".getBytes(), 0, 4);
            TestCase.fail("Failed to close file properly.");
        } catch (IOException e) {
        }
    }

    /**
     * java.io.RandomAccessFile#getChannel()
     */
    public void test_getChannel() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        FileChannel fc = raf.getChannel();
        // Indirect test: If the file's file pointer moves then the position
        // in the channel has to move accordingly.
        TestCase.assertTrue("Test 1: Channel position expected to be 0.", ((fc.position()) == 0));
        raf.write(OldRandomAccessFileTest.testString.getBytes());
        TestCase.assertEquals("Test 2: Unexpected channel position.", OldRandomAccessFileTest.testLength, fc.position());
        TestCase.assertTrue("Test 3: Channel position is not equal to file pointer.", ((fc.position()) == (raf.getFilePointer())));
        raf.close();
    }

    /**
     * java.io.RandomAccessFile#getFD()
     */
    public void test_getFD() throws IOException {
        // Test for method java.io.FileDescriptor
        // java.io.RandomAccessFile.getFD()
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        TestCase.assertTrue("Test 1: Returned invalid fd.", raf.getFD().valid());
        raf.close();
        TestCase.assertFalse("Test 2: Returned valid fd after close", raf.getFD().valid());
    }

    /**
     * java.io.RandomAccessFile#getFilePointer()
     */
    public void test_getFilePointer() throws IOException {
        // Test for method long java.io.RandomAccessFile.getFilePointer()
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.write(OldRandomAccessFileTest.testString.getBytes(), 0, OldRandomAccessFileTest.testLength);
        TestCase.assertEquals("Test 1: Incorrect filePointer returned. ", OldRandomAccessFileTest.testLength, raf.getFilePointer());
        raf.close();
        try {
            raf.getFilePointer();
            TestCase.fail("Test 2: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * java.io.RandomAccessFile#length()
     */
    public void test_length() throws IOException {
        // Test for method long java.io.RandomAccessFile.length()
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.write(OldRandomAccessFileTest.testString.getBytes());
        TestCase.assertEquals("Test 1: Incorrect length returned. ", OldRandomAccessFileTest.testLength, raf.length());
        raf.close();
        try {
            raf.length();
            TestCase.fail("Test 2: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * java.io.RandomAccessFile#read()
     */
    public void test_read_write() throws IOException {
        int i;
        byte[] testBuf = OldRandomAccessFileTest.testString.getBytes();
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        for (i = 0; i < (OldRandomAccessFileTest.testString.length()); i++) {
            try {
                raf.write(testBuf[i]);
            } catch (Exception e) {
                TestCase.fail(("Test 1: Unexpected exception while writing: " + (e.getMessage())));
            }
        }
        raf.seek(0);
        for (i = 0; i < (OldRandomAccessFileTest.testString.length()); i++) {
            TestCase.assertEquals(String.format("Test 2: Incorrect value written or read at index %d; ", i), testBuf[i], raf.read());
        }
        TestCase.assertTrue("Test 3: End of file indicator (-1) expected.", ((raf.read()) == (-1)));
        raf.close();
        try {
            raf.write(42);
            TestCase.fail("Test 4: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
        try {
            raf.read();
            TestCase.fail("Test 5: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * java.io.RandomAccessFile#read(byte[])
     */
    public void test_read$B() throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(OldRandomAccessFileTest.testString.getBytes(), 0, OldRandomAccessFileTest.testLength);
        fos.close();
        RandomAccessFile raf = new RandomAccessFile(fileName, "r");
        byte[] rbuf = new byte[(OldRandomAccessFileTest.testLength) + 10];
        int bytesRead = raf.read(rbuf);
        TestCase.assertEquals("Test 1: Incorrect number of bytes read. ", OldRandomAccessFileTest.testLength, bytesRead);
        TestCase.assertEquals("Test 2: Incorrect bytes read. ", OldRandomAccessFileTest.testString, new String(rbuf, 0, OldRandomAccessFileTest.testLength));
        bytesRead = raf.read(rbuf);
        TestCase.assertTrue("Test 3: EOF (-1) expected. ", (bytesRead == (-1)));
        raf.close();
        try {
            bytesRead = raf.read(rbuf);
            TestCase.fail("Test 4: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * java.io.RandomAccessFile#read(byte[], int, int)
     */
    public void test_read$BII() throws IOException {
        int bytesRead;
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        byte[] rbuf = new byte[4000];
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(OldRandomAccessFileTest.testString.getBytes(), 0, OldRandomAccessFileTest.testLength);
        fos.close();
        // Read half of the file contents.
        bytesRead = raf.read(rbuf, 10, ((OldRandomAccessFileTest.testLength) / 2));
        TestCase.assertEquals("Test 1: Incorrect number of bytes read. ", ((OldRandomAccessFileTest.testLength) / 2), bytesRead);
        TestCase.assertEquals("Test 2: Incorrect bytes read. ", OldRandomAccessFileTest.testString.substring(0, ((OldRandomAccessFileTest.testLength) / 2)), new String(rbuf, 10, ((OldRandomAccessFileTest.testLength) / 2)));
        // Read the rest of the file contents.
        bytesRead = raf.read(rbuf, 0, OldRandomAccessFileTest.testLength);
        TestCase.assertEquals("Test 3: Incorrect number of bytes read. ", ((OldRandomAccessFileTest.testLength) - ((OldRandomAccessFileTest.testLength) / 2)), bytesRead);
        TestCase.assertEquals("Test 4: Incorrect bytes read. ", OldRandomAccessFileTest.testString.substring(((OldRandomAccessFileTest.testLength) / 2), (((OldRandomAccessFileTest.testLength) / 2) + bytesRead)), new String(rbuf, 0, bytesRead));
        // Try to read even more.
        bytesRead = raf.read(rbuf, 0, 1);
        TestCase.assertTrue("Test 5: EOF (-1) expected. ", (bytesRead == (-1)));
        // Illegal parameter value tests.
        try {
            raf.read(rbuf, (-1), 1);
            TestCase.fail("Test 6: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected.
        }
        try {
            raf.read(rbuf, 0, (-1));
            TestCase.fail("Test 7: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected.
        }
        try {
            raf.read(rbuf, 2000, 2001);
            TestCase.fail("Test 8: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected.
        }
        // IOException test.
        raf.close();
        try {
            bytesRead = raf.read(rbuf, 0, 1);
            TestCase.fail("Test 9: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * java.io.RandomAccessFile#readBoolean()
     * java.io.RandomAccessFile#writeBoolean(boolean)
     */
    public void test_read_writeBoolean() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.writeBoolean(true);
        raf.writeBoolean(false);
        raf.seek(0);
        TestCase.assertEquals("Test 1: Incorrect value written or read;", true, raf.readBoolean());
        TestCase.assertEquals("Test 2: Incorrect value written or read;", false, raf.readBoolean());
        try {
            raf.readBoolean();
            TestCase.fail("Test 3: EOFException expected.");
        } catch (EOFException e) {
            // Expected.
        }
        raf.close();
        try {
            raf.writeBoolean(false);
            TestCase.fail("Test 4: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
        try {
            raf.readBoolean();
            TestCase.fail("Test 5: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * java.io.RandomAccessFile#readByte()
     * java.io.RandomAccessFile#writeByte(byte)
     */
    public void test_read_writeByte() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.writeByte(Byte.MIN_VALUE);
        raf.writeByte(11);
        raf.writeByte(Byte.MAX_VALUE);
        raf.writeByte(((Byte.MIN_VALUE) - 1));
        raf.writeByte(((Byte.MAX_VALUE) + 1));
        raf.seek(0);
        TestCase.assertEquals("Test 1: Incorrect value written or read;", Byte.MIN_VALUE, raf.readByte());
        TestCase.assertEquals("Test 2: Incorrect value written or read;", 11, raf.readByte());
        TestCase.assertEquals("Test 3: Incorrect value written or read;", Byte.MAX_VALUE, raf.readByte());
        TestCase.assertEquals("Test 4: Incorrect value written or read;", 127, raf.readByte());
        TestCase.assertEquals("Test 5: Incorrect value written or read;", (-128), raf.readByte());
        try {
            raf.readByte();
            TestCase.fail("Test 6: EOFException expected.");
        } catch (EOFException e) {
            // Expected.
        }
        raf.close();
        try {
            raf.writeByte(13);
            TestCase.fail("Test 7: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
        try {
            raf.readByte();
            TestCase.fail("Test 8: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * java.io.RandomAccessFile#readChar()
     * java.io.RandomAccessFile#writeChar(char)
     */
    public void test_read_writeChar() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.writeChar(Character.MIN_VALUE);
        raf.writeChar('T');
        raf.writeChar(Character.MAX_VALUE);
        raf.writeChar(((Character.MIN_VALUE) - 1));
        raf.writeChar(((Character.MAX_VALUE) + 1));
        raf.seek(0);
        TestCase.assertEquals("Test 1: Incorrect value written or read;", Character.MIN_VALUE, raf.readChar());
        TestCase.assertEquals("Test 2: Incorrect value written or read;", 'T', raf.readChar());
        TestCase.assertEquals("Test 3: Incorrect value written or read;", Character.MAX_VALUE, raf.readChar());
        TestCase.assertEquals("Test 4: Incorrect value written or read;", 65535, raf.readChar());
        TestCase.assertEquals("Test 5: Incorrect value written or read;", 0, raf.readChar());
        try {
            raf.readChar();
            TestCase.fail("Test 6: EOFException expected.");
        } catch (EOFException e) {
            // Expected.
        }
        raf.close();
        try {
            raf.writeChar('E');
            TestCase.fail("Test 7: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
        try {
            raf.readChar();
            TestCase.fail("Test 8: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * java.io.RandomAccessFile#readDouble()
     * java.io.RandomAccessFile#writeDouble(double)
     */
    public void test_read_writeDouble() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.writeDouble(Double.MAX_VALUE);
        raf.writeDouble(424242.4242);
        raf.seek(0);
        TestCase.assertEquals("Test 1: Incorrect value written or read;", Double.MAX_VALUE, raf.readDouble());
        TestCase.assertEquals("Test 2: Incorrect value written or read;", 424242.4242, raf.readDouble());
        try {
            raf.readDouble();
            TestCase.fail("Test 3: EOFException expected.");
        } catch (EOFException e) {
            // Expected.
        }
        raf.close();
        try {
            raf.writeDouble(Double.MIN_VALUE);
            TestCase.fail("Test 4: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
        try {
            raf.readDouble();
            TestCase.fail("Test 5: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * java.io.RandomAccessFile#readFloat()
     * java.io.RandomAccessFile#writeFloat(double)
     */
    public void test_read_writeFloat() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.writeFloat(Float.MAX_VALUE);
        raf.writeFloat(555.55F);
        raf.seek(0);
        TestCase.assertEquals("Test 1: Incorrect value written or read. ", Float.MAX_VALUE, raf.readFloat());
        TestCase.assertEquals("Test 2: Incorrect value written or read. ", 555.55F, raf.readFloat());
        try {
            raf.readFloat();
            TestCase.fail("Test 3: EOFException expected.");
        } catch (EOFException e) {
            // Expected.
        }
        raf.close();
        try {
            raf.writeFloat(Float.MIN_VALUE);
            TestCase.fail("Test 4: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
        try {
            raf.readFloat();
            TestCase.fail("Test 5: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * java.io.RandomAccessFile#readInt()
     * java.io.RandomAccessFile#writeInt(char)
     */
    public void test_read_writeInt() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.writeInt(Integer.MIN_VALUE);
        raf.writeInt('T');
        raf.writeInt(Integer.MAX_VALUE);
        raf.writeInt(((Integer.MIN_VALUE) - 1));
        raf.writeInt(((Integer.MAX_VALUE) + 1));
        raf.seek(0);
        TestCase.assertEquals("Test 1: Incorrect value written or read;", Integer.MIN_VALUE, raf.readInt());
        TestCase.assertEquals("Test 2: Incorrect value written or read;", 'T', raf.readInt());
        TestCase.assertEquals("Test 3: Incorrect value written or read;", Integer.MAX_VALUE, raf.readInt());
        TestCase.assertEquals("Test 4: Incorrect value written or read;", 2147483647, raf.readInt());
        TestCase.assertEquals("Test 5: Incorrect value written or read;", -2147483648, raf.readInt());
        try {
            raf.readInt();
            TestCase.fail("Test 6: EOFException expected.");
        } catch (EOFException e) {
            // Expected.
        }
        raf.close();
        try {
            raf.writeInt('E');
            TestCase.fail("Test 7: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
        try {
            raf.readInt();
            TestCase.fail("Test 8: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * java.io.RandomAccessFile#readLong()
     * java.io.RandomAccessFile#writeLong(char)
     */
    public void test_read_writeLong() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.writeLong(Long.MIN_VALUE);
        raf.writeLong('T');
        raf.writeLong(Long.MAX_VALUE);
        raf.writeLong(((Long.MIN_VALUE) - 1));
        raf.writeLong(((Long.MAX_VALUE) + 1));
        raf.seek(0);
        TestCase.assertEquals("Test 1: Incorrect value written or read;", Long.MIN_VALUE, raf.readLong());
        TestCase.assertEquals("Test 2: Incorrect value written or read;", 'T', raf.readLong());
        TestCase.assertEquals("Test 3: Incorrect value written or read;", Long.MAX_VALUE, raf.readLong());
        TestCase.assertEquals("Test 4: Incorrect value written or read;", 9223372036854775807L, raf.readLong());
        TestCase.assertEquals("Test 5: Incorrect value written or read;", -9223372036854775808L, raf.readLong());
        try {
            raf.readLong();
            TestCase.fail("Test 6: EOFException expected.");
        } catch (EOFException e) {
            // Expected.
        }
        raf.close();
        try {
            raf.writeLong('E');
            TestCase.fail("Test 7: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
        try {
            raf.readLong();
            TestCase.fail("Test 8: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * java.io.RandomAccessFile#readShort()
     * java.io.RandomAccessFile#writeShort(short)
     */
    public void test_read_writeShort() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.writeShort(Short.MIN_VALUE);
        raf.writeShort('T');
        raf.writeShort(Short.MAX_VALUE);
        raf.writeShort(((Short.MIN_VALUE) - 1));
        raf.writeShort(((Short.MAX_VALUE) + 1));
        raf.seek(0);
        TestCase.assertEquals("Test 1: Incorrect value written or read;", Short.MIN_VALUE, raf.readShort());
        TestCase.assertEquals("Test 2: Incorrect value written or read;", 'T', raf.readShort());
        TestCase.assertEquals("Test 3: Incorrect value written or read;", Short.MAX_VALUE, raf.readShort());
        TestCase.assertEquals("Test 4: Incorrect value written or read;", 32767, raf.readShort());
        TestCase.assertEquals("Test 5: Incorrect value written or read;", ((short) (32768)), raf.readShort());
        try {
            raf.readShort();
            TestCase.fail("Test 6: EOFException expected.");
        } catch (EOFException e) {
            // Expected.
        }
        raf.close();
        try {
            raf.writeShort('E');
            TestCase.fail("Test 7: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
        try {
            raf.readShort();
            TestCase.fail("Test 8: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * java.io.RandomAccessFile#readUTF()
     * java.io.RandomAccessFile#writeShort(char)
     */
    public void test_read_writeUTF() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.writeUTF(unihw);
        raf.seek(0);
        TestCase.assertEquals("Test 1: Incorrect UTF string written or read;", unihw, raf.readUTF());
        try {
            raf.readUTF();
            TestCase.fail("Test 2: EOFException expected.");
        } catch (EOFException e) {
            // Expected.
        }
        raf.close();
        try {
            raf.writeUTF("Already closed.");
            TestCase.fail("Test 3: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
        try {
            raf.readUTF();
            TestCase.fail("Test 4: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * java.io.RandomAccessFile#writeBytes(java.lang.String)
     * java.io.RandomAccessFile#readFully(byte[])
     */
    public void test_readFully$B_writeBytesLjava_lang_String() throws IOException {
        byte[] buf = new byte[OldRandomAccessFileTest.testLength];
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.writeBytes(OldRandomAccessFileTest.testString);
        raf.seek(0);
        try {
            raf.readFully(null);
            TestCase.fail("Test 1: NullPointerException expected.");
        } catch (NullPointerException e) {
            // Expected.
        }
        raf.readFully(buf);
        TestCase.assertEquals("Test 2: Incorrect bytes written or read;", OldRandomAccessFileTest.testString, new String(buf));
        try {
            raf.readFully(buf);
            TestCase.fail("Test 3: EOFException expected.");
        } catch (EOFException e) {
            // Expected.
        }
        raf.close();
        try {
            raf.writeBytes("Already closed.");
            TestCase.fail("Test 4: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
        try {
            raf.readFully(buf);
            TestCase.fail("Test 5: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * java.io.RandomAccessFile#writeBytes(java.lang.String)
     * java.io.RandomAccessFile#readFully(byte[], int, int)
     */
    public void test_readFully$BII() throws IOException {
        byte[] buf = new byte[OldRandomAccessFileTest.testLength];
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.writeBytes(OldRandomAccessFileTest.testString);
        raf.seek(0);
        try {
            raf.readFully(null);
            TestCase.fail("Test 1: NullPointerException expected.");
        } catch (NullPointerException e) {
            // Expected.
        }
        raf.readFully(buf, 5, ((OldRandomAccessFileTest.testLength) - 10));
        for (int i = 0; i < 5; i++) {
            TestCase.assertEquals("Test 2: Incorrect bytes read;", 0, buf[i]);
        }
        TestCase.assertEquals("Test 3: Incorrect bytes written or read;", OldRandomAccessFileTest.testString.substring(0, ((OldRandomAccessFileTest.testLength) - 10)), new String(buf, 5, ((OldRandomAccessFileTest.testLength) - 10)));
        // Reading past the end of the file.
        try {
            raf.readFully(buf, 3, ((OldRandomAccessFileTest.testLength) - 6));
            TestCase.fail("Test 4: EOFException expected.");
        } catch (EOFException e) {
            // Expected.
        }
        // Passing invalid arguments.
        try {
            raf.readFully(buf, (-1), 1);
            TestCase.fail("Test 5: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected.
        }
        try {
            raf.readFully(buf, 0, (-1));
            TestCase.fail("Test 6: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected.
        }
        try {
            raf.readFully(buf, 2, OldRandomAccessFileTest.testLength);
            TestCase.fail("Test 7: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException e) {
            // Expected.
        }
        // Reading from a closed file.
        raf.close();
        try {
            raf.readFully(buf);
            TestCase.fail("Test 8: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * java.io.RandomAccessFile#readUnsignedByte()
     */
    public void test_readUnsignedByte() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.writeByte((-1));
        raf.seek(0);
        TestCase.assertEquals("Test 1: Incorrect value written or read;", 255, raf.readUnsignedByte());
        try {
            raf.readUnsignedByte();
            TestCase.fail("Test 2: EOFException expected.");
        } catch (EOFException e) {
            // Expected.
        }
        raf.close();
        try {
            raf.readUnsignedByte();
            TestCase.fail("Test 3: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * java.io.RandomAccessFile#readUnsignedShort()
     */
    public void test_readUnsignedShort() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.writeShort((-1));
        raf.seek(0);
        TestCase.assertEquals("Test 1: Incorrect value written or read;", 65535, raf.readUnsignedShort());
        try {
            raf.readUnsignedShort();
            TestCase.fail("Test 2: EOFException expected.");
        } catch (EOFException e) {
            // Expected.
        }
        raf.close();
        try {
            raf.readUnsignedShort();
            TestCase.fail("Test 3: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * java.io.RandomAccessFile#readLine()
     */
    public void test_readLine() throws IOException {
        // Test for method java.lang.String java.io.RandomAccessFile.readLine()
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        String s = "Goodbye\nCruel\nWorld\n";
        raf.write(s.getBytes(), 0, s.length());
        raf.seek(0);
        TestCase.assertEquals("Test 1: Incorrect line read;", "Goodbye", raf.readLine());
        TestCase.assertEquals("Test 2: Incorrect line read;", "Cruel", raf.readLine());
        TestCase.assertEquals("Test 3: Incorrect line read;", "World", raf.readLine());
        TestCase.assertNull("Test 4: Incorrect line read; null expected.", raf.readLine());
        raf.close();
        try {
            raf.readLine();
            TestCase.fail("Test 5: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * java.io.RandomAccessFile#seek(long)
     */
    public void test_seekJ() throws IOException {
        // Test for method void java.io.RandomAccessFile.seek(long)
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        try {
            raf.seek((-1));
            TestCase.fail("Test 1: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
        raf.write(OldRandomAccessFileTest.testString.getBytes(), 0, OldRandomAccessFileTest.testLength);
        raf.seek(12);
        TestCase.assertEquals("Test 3: Seek failed to set file pointer.", 12, raf.getFilePointer());
        raf.close();
        try {
            raf.seek(1);
            TestCase.fail("Test 4: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * java.io.RandomAccessFile#skipBytes(int)
     */
    public void test_skipBytesI() throws IOException {
        byte[] buf = new byte[5];
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.writeBytes("HelloWorld");
        raf.seek(0);
        TestCase.assertTrue("Test 1: Nothing should be skipped if parameter is less than zero", ((raf.skipBytes((-1))) == 0));
        TestCase.assertEquals("Test 4: Incorrect number of bytes skipped; ", 5, raf.skipBytes(5));
        raf.readFully(buf);
        TestCase.assertEquals("Test 3: Failed to skip bytes.", "World", new String(buf, 0, 5));
        raf.seek(0);
        TestCase.assertEquals("Test 4: Incorrect number of bytes skipped; ", 10, raf.skipBytes(20));
        raf.close();
        try {
            raf.skipBytes(1);
            TestCase.fail("Test 5: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * java.io.RandomAccessFile#skipBytes(int)
     */
    public void test_setLengthJ() throws IOException {
        int bytesRead;
        long truncLength = ((long) ((OldRandomAccessFileTest.testLength) * 0.75));
        byte[] rbuf = new byte[(OldRandomAccessFileTest.testLength) + 10];
        // Setup the test file.
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.write(OldRandomAccessFileTest.testString.getBytes());
        TestCase.assertEquals("Test 1: Incorrect file length;", OldRandomAccessFileTest.testLength, raf.length());
        // Truncate the file.
        raf.setLength(truncLength);
        TestCase.assertTrue("Test 2: File pointer not moved to the end of the truncated file.", ((raf.getFilePointer()) == truncLength));
        raf.close();
        raf = new RandomAccessFile(fileName, "rw");
        TestCase.assertEquals("Test 3: Incorrect file length;", truncLength, raf.length());
        bytesRead = raf.read(rbuf);
        TestCase.assertEquals("Test 4: Incorrect number of bytes read;", truncLength, bytesRead);
        TestCase.assertEquals("Test 5: Incorrect bytes read. ", OldRandomAccessFileTest.testString.substring(0, bytesRead), new String(rbuf, 0, bytesRead));
        // Expand the file.
        raf.setLength(((OldRandomAccessFileTest.testLength) + 2));
        TestCase.assertTrue("Test 6: File pointer incorrectly moved.", ((raf.getFilePointer()) == truncLength));
        TestCase.assertEquals("Test 7: Incorrect file length;", ((OldRandomAccessFileTest.testLength) + 2), raf.length());
        // Exception testing.
        try {
            raf.setLength((-1));
            TestCase.fail("Test 9: IllegalArgumentException expected.");
        } catch (IOException expected) {
        } catch (IllegalArgumentException expected) {
        }
        raf.close();
        try {
            raf.setLength(truncLength);
            TestCase.fail("Test 10: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * java.io.RandomAccessFile#write(byte[])
     */
    public void test_write$B() throws IOException {
        byte[] rbuf = new byte[4000];
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        byte[] nullByteArray = null;
        try {
            raf.write(nullByteArray);
            TestCase.fail("Test 1: NullPointerException expected.");
        } catch (NullPointerException e) {
            // Expected.
        }
        try {
            raf.write(OldRandomAccessFileTest.testString.getBytes());
        } catch (Exception e) {
            TestCase.fail(("Test 2: Unexpected exception: " + (e.getMessage())));
        }
        raf.close();
        try {
            raf.write(new byte[0]);
        } catch (IOException e) {
            TestCase.fail(("Test 3: Unexpected IOException: " + (e.getMessage())));
        }
        try {
            raf.write(OldRandomAccessFileTest.testString.getBytes());
            TestCase.fail("Test 4: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
        FileInputStream fis = new FileInputStream(fileName);
        fis.read(rbuf, 0, OldRandomAccessFileTest.testLength);
        TestCase.assertEquals("Incorrect bytes written", OldRandomAccessFileTest.testString, new String(rbuf, 0, OldRandomAccessFileTest.testLength));
    }

    /**
     * java.io.RandomAccessFile#write(byte[], int, int)
     */
    public void test_write$BII() throws Exception {
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        byte[] rbuf = new byte[4000];
        byte[] testBuf = null;
        int bytesRead;
        try {
            raf.write(testBuf, 1, 1);
            TestCase.fail("Test 1: NullPointerException expected.");
        } catch (NullPointerException e) {
            // Expected.
        }
        testBuf = OldRandomAccessFileTest.testString.getBytes();
        try {
            raf.write(testBuf, (-1), 10);
            TestCase.fail("Test 2: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException expected) {
        }
        try {
            raf.write(testBuf, 0, (-1));
            TestCase.fail("Test 3: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException expected) {
        }
        try {
            raf.write(testBuf, 5, OldRandomAccessFileTest.testLength);
            TestCase.fail("Test 4: IndexOutOfBoundsException expected.");
        } catch (IndexOutOfBoundsException expected) {
        }
        // Positive test: The following write should not fail.
        try {
            raf.write(testBuf, 3, ((OldRandomAccessFileTest.testLength) - 5));
        } catch (Exception e) {
            TestCase.fail(("Test 5: Unexpected exception: " + (e.getMessage())));
        }
        raf.close();
        // Writing nothing to a closed file should not fail either.
        try {
            raf.write(new byte[0]);
        } catch (IOException e) {
            TestCase.fail(("Test 6: Unexpected IOException: " + (e.getMessage())));
        }
        // Writing something to a closed file should fail.
        try {
            raf.write(OldRandomAccessFileTest.testString.getBytes());
            TestCase.fail("Test 7: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
        FileInputStream fis = new FileInputStream(fileName);
        bytesRead = fis.read(rbuf, 0, OldRandomAccessFileTest.testLength);
        TestCase.assertEquals("Test 8: Incorrect number of bytes written or read;", ((OldRandomAccessFileTest.testLength) - 5), bytesRead);
        TestCase.assertEquals("Test 9: Incorrect bytes written or read; ", OldRandomAccessFileTest.testString.substring(3, ((OldRandomAccessFileTest.testLength) - 2)), new String(rbuf, 0, bytesRead));
    }

    /**
     * java.io.RandomAccessFile#writeChars(java.lang.String)
     */
    public void test_writeCharsLjava_lang_String() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.writeChars(unihw);
        char[] hchars = new char[unihw.length()];
        unihw.getChars(0, unihw.length(), hchars, 0);
        raf.seek(0);
        for (int i = 0; i < (hchars.length); i++)
            TestCase.assertEquals((("Test 1: Incorrect character written or read at index " + i) + ";"), hchars[i], raf.readChar());

        raf.close();
        try {
            raf.writeChars("Already closed.");
            TestCase.fail("Test 2: IOException expected.");
        } catch (IOException e) {
            // Expected.
        }
    }
}

