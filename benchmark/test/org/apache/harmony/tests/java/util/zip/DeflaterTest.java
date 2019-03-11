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
package org.apache.harmony.tests.java.util.zip;


import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.Adler32;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import junit.framework.TestCase;
import tests.support.resource.Support_Resources;


public class DeflaterTest extends TestCase {
    class MyDeflater extends Deflater {
        MyDeflater() {
            super();
        }

        MyDeflater(int lvl) {
            super(lvl);
        }

        void myFinalize() {
            finalize();
        }

        int getDefCompression() {
            return Deflater.DEFAULT_COMPRESSION;
        }

        int getDefStrategy() {
            return Deflater.DEFAULT_STRATEGY;
        }

        int getHuffman() {
            return Deflater.HUFFMAN_ONLY;
        }

        int getFiltered() {
            return Deflater.FILTERED;
        }
    }

    /**
     * java.util.zip.Deflater#deflate(byte[])
     */
    public void test_deflate$B() {
        byte[] outPutBuf = new byte[50];
        byte[] byteArray = new byte[]{ 1, 3, 4, 7, 8 };
        byte[] outPutInf = new byte[50];
        int x = 0;
        Deflater defl = new Deflater();
        defl.setInput(byteArray);
        defl.finish();
        while (!(defl.finished())) {
            x += defl.deflate(outPutBuf);
        } 
        TestCase.assertEquals("Deflater at end of stream, should return 0", 0, defl.deflate(outPutBuf));
        int totalOut = defl.getTotalOut();
        int totalIn = defl.getTotalIn();
        TestCase.assertEquals(x, totalOut);
        TestCase.assertEquals(byteArray.length, totalIn);
        defl.end();
        Inflater infl = new Inflater();
        try {
            infl.setInput(outPutBuf);
            while (!(infl.finished())) {
                infl.inflate(outPutInf);
            } 
        } catch (DataFormatException e) {
            TestCase.fail("Invalid input to be decompressed");
        }
        TestCase.assertEquals(totalIn, infl.getTotalOut());
        TestCase.assertEquals(totalOut, infl.getTotalIn());
        for (int i = 0; i < (byteArray.length); i++) {
            TestCase.assertEquals(byteArray[i], outPutInf[i]);
        }
        TestCase.assertEquals("Final decompressed data contained more bytes than original", 0, outPutInf[byteArray.length]);
        infl.end();
    }

    /**
     * java.util.zip.Deflater#deflate(byte[], int, int)
     */
    public void test_deflate$BII() {
        byte[] outPutBuf = new byte[50];
        byte[] byteArray = new byte[]{ 5, 2, 3, 7, 8 };
        byte[] outPutInf = new byte[50];
        int offSet = 1;
        int length = (outPutBuf.length) - 1;
        int x = 0;
        Deflater defl = new Deflater();
        defl.setInput(byteArray);
        defl.finish();
        while (!(defl.finished())) {
            x += defl.deflate(outPutBuf, offSet, length);
        } 
        TestCase.assertEquals("Deflater at end of stream, should return 0", 0, defl.deflate(outPutBuf, offSet, length));
        int totalOut = defl.getTotalOut();
        int totalIn = defl.getTotalIn();
        TestCase.assertEquals(x, totalOut);
        TestCase.assertEquals(byteArray.length, totalIn);
        defl.end();
        Inflater infl = new Inflater();
        try {
            infl.setInput(outPutBuf, offSet, length);
            while (!(infl.finished())) {
                infl.inflate(outPutInf);
            } 
        } catch (DataFormatException e) {
            TestCase.fail("Invalid input to be decompressed");
        }
        TestCase.assertEquals(totalIn, infl.getTotalOut());
        TestCase.assertEquals(totalOut, infl.getTotalIn());
        for (int i = 0; i < (byteArray.length); i++) {
            TestCase.assertEquals(byteArray[i], outPutInf[i]);
        }
        TestCase.assertEquals("Final decompressed data contained more bytes than original", 0, outPutInf[byteArray.length]);
        infl.end();
        // Set of tests testing the boundaries of the offSet/length
        defl = new Deflater();
        outPutBuf = new byte[100];
        defl.setInput(byteArray);
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                offSet = (outPutBuf.length) + 1;
                length = outPutBuf.length;
            } else {
                offSet = 0;
                length = (outPutBuf.length) + 1;
            }
            try {
                defl.deflate(outPutBuf, offSet, length);
                TestCase.fail((("Test " + i) + ": ArrayIndexOutOfBoundsException not thrown"));
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
        defl.end();
    }

    /**
     * java.util.zip.Deflater#end()
     */
    public void test_end() {
        byte[] byteArray = new byte[]{ 5, 2, 3, 7, 8 };
        byte[] outPutBuf = new byte[100];
        Deflater defl = new Deflater();
        defl.setInput(byteArray);
        defl.finish();
        while (!(defl.finished())) {
            defl.deflate(outPutBuf);
        } 
        defl.end();
        helper_end_test(defl, "end");
    }

    /**
     * java.util.zip.Deflater#finalize()
     */
    public void test_finalize() {
        DeflaterTest.MyDeflater mdefl = new DeflaterTest.MyDeflater();
        mdefl.myFinalize();
        System.gc();
        helper_end_test(mdefl, "finalize");
    }

    /**
     * java.util.zip.Deflater#finish()
     */
    public void test_finish() throws Exception {
        // This test already here, its the same as test_deflate()
        byte[] byteArray = new byte[]{ 5, 2, 3, 7, 8 };
        byte[] outPutBuf = new byte[100];
        byte[] outPutInf = new byte[100];
        int x = 0;
        Deflater defl = new Deflater();
        defl.setInput(byteArray);
        defl.finish();
        // needsInput should never return true after finish() is called
        if (System.getProperty("java.vendor").startsWith("IBM")) {
            TestCase.assertFalse("needsInput() should return false after finish() is called", defl.needsInput());
        }
        while (!(defl.finished())) {
            x += defl.deflate(outPutBuf);
        } 
        int totalOut = defl.getTotalOut();
        int totalIn = defl.getTotalIn();
        TestCase.assertEquals(x, totalOut);
        TestCase.assertEquals(byteArray.length, totalIn);
        defl.end();
        Inflater infl = new Inflater();
        infl.setInput(outPutBuf);
        while (!(infl.finished())) {
            infl.inflate(outPutInf);
        } 
        TestCase.assertEquals(totalIn, infl.getTotalOut());
        TestCase.assertEquals(totalOut, infl.getTotalIn());
        for (int i = 0; i < (byteArray.length); i++) {
            TestCase.assertEquals(byteArray[i], outPutInf[i]);
        }
        TestCase.assertEquals("Final decompressed data contained more bytes than original", 0, outPutInf[byteArray.length]);
        infl.end();
    }

    /**
     * java.util.zip.Deflater#finished()
     */
    public void test_finished() {
        byte[] byteArray = new byte[]{ 5, 2, 3, 7, 8 };
        byte[] outPutBuf = new byte[100];
        Deflater defl = new Deflater();
        TestCase.assertTrue("Test 1: Deflater should not be finished.", (!(defl.finished())));
        defl.setInput(byteArray);
        TestCase.assertTrue("Test 2: Deflater should not be finished.", (!(defl.finished())));
        defl.finish();
        TestCase.assertTrue("Test 3: Deflater should not be finished.", (!(defl.finished())));
        while (!(defl.finished())) {
            defl.deflate(outPutBuf);
        } 
        TestCase.assertTrue("Test 4: Deflater should be finished.", defl.finished());
        defl.end();
        TestCase.assertTrue("Test 5: Deflater should be finished.", defl.finished());
    }

    /**
     * java.util.zip.Deflater#getAdler()
     */
    public void test_getAdler() {
        byte[] byteArray = new byte[]{ 'a', 'b', 'c', 1, 2, 3 };
        byte[] outPutBuf = new byte[100];
        Deflater defl = new Deflater();
        // getting the checkSum value using the Adler
        defl.setInput(byteArray);
        defl.finish();
        while (!(defl.finished())) {
            defl.deflate(outPutBuf);
        } 
        long checkSumD = defl.getAdler();
        defl.end();
        // getting the checkSum value through the Adler32 class
        Adler32 adl = new Adler32();
        adl.update(byteArray);
        long checkSumR = adl.getValue();
        TestCase.assertEquals("The checksum value returned by getAdler() is not the same as the checksum returned by creating the adler32 instance", checkSumD, checkSumR);
    }

    /**
     * java.util.zip.Deflater#getTotalIn()
     */
    public void test_getTotalIn() {
        byte[] outPutBuf = new byte[5];
        byte[] byteArray = new byte[]{ 1, 3, 4, 7, 8 };
        Deflater defl = new Deflater();
        defl.setInput(byteArray);
        defl.finish();
        while (!(defl.finished())) {
            defl.deflate(outPutBuf);
        } 
        TestCase.assertEquals(byteArray.length, defl.getTotalIn());
        defl.end();
        defl = new Deflater();
        int offSet = 2;
        int length = 3;
        outPutBuf = new byte[5];
        defl.setInput(byteArray, offSet, length);
        defl.finish();
        while (!(defl.finished())) {
            defl.deflate(outPutBuf);
        } 
        TestCase.assertEquals(length, defl.getTotalIn());
        defl.end();
    }

    /**
     * java.util.zip.Deflater#getTotalOut()
     */
    public void test_getTotalOut() {
        // the getTotalOut should equal the sum of value returned by deflate()
        byte[] outPutBuf = new byte[5];
        byte[] byteArray = new byte[]{ 5, 2, 3, 7, 8 };
        int x = 0;
        Deflater defl = new Deflater();
        defl.setInput(byteArray);
        defl.finish();
        while (!(defl.finished())) {
            x += defl.deflate(outPutBuf);
        } 
        TestCase.assertEquals(x, defl.getTotalOut());
        defl.end();
        x = 0;
        int offSet = 2;
        int length = 3;
        defl = new Deflater();
        outPutBuf = new byte[5];
        defl.setInput(byteArray, offSet, length);
        defl.finish();
        while (!(defl.finished())) {
            x += defl.deflate(outPutBuf);
        } 
        TestCase.assertEquals(x, defl.getTotalOut());
    }

    /**
     * java.util.zip.Deflater#needsInput()
     */
    public void test_needsInput() {
        Deflater defl = new Deflater();
        TestCase.assertTrue("needsInput give the wrong boolean value as a result of no input buffer", defl.needsInput());
        byte[] byteArray = new byte[]{ 1, 2, 3 };
        defl.setInput(byteArray);
        TestCase.assertFalse("needsInput give wrong boolean value as a result of a full input buffer", defl.needsInput());
        byte[] outPutBuf = new byte[50];
        while (!(defl.needsInput())) {
            defl.deflate(outPutBuf);
        } 
        byte[] emptyByteArray = new byte[0];
        defl.setInput(emptyByteArray);
        TestCase.assertTrue("needsInput give wrong boolean value as a result of an empty input buffer", defl.needsInput());
        defl.setInput(byteArray);
        defl.finish();
        while (!(defl.finished())) {
            defl.deflate(outPutBuf);
        } 
        // needsInput should NOT return true after finish() has been
        // called.
        if (System.getProperty("java.vendor").startsWith("IBM")) {
            TestCase.assertFalse("needsInput gave wrong boolean value as a result of finish() being called", defl.needsInput());
        }
        defl.end();
    }

    /**
     * java.util.zip.Deflater#reset()
     */
    public void test_reset() {
        byte[] outPutBuf = new byte[100];
        byte[] outPutInf = new byte[100];
        byte[] curArray = new byte[5];
        byte[] byteArray = new byte[]{ 1, 3, 4, 7, 8 };
        byte[] byteArray2 = new byte[]{ 8, 7, 4, 3, 1 };
        int x = 0;
        int orgValue = 0;
        Deflater defl = new Deflater();
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                curArray = byteArray;
            } else
                if (i == 1) {
                    curArray = byteArray2;
                } else {
                    defl.reset();
                }

            defl.setInput(curArray);
            defl.finish();
            while (!(defl.finished())) {
                x += defl.deflate(outPutBuf);
            } 
            if (i == 0) {
                TestCase.assertEquals(x, defl.getTotalOut());
            } else
                if (i == 1) {
                    TestCase.assertEquals(x, orgValue);
                } else {
                    TestCase.assertEquals(x, (orgValue * 2));
                }

            if (i == 0) {
                orgValue = x;
            }
            try {
                Inflater infl = new Inflater();
                infl.setInput(outPutBuf);
                while (!(infl.finished())) {
                    infl.inflate(outPutInf);
                } 
                infl.end();
            } catch (DataFormatException e) {
                TestCase.fail((("Test " + i) + ": Invalid input to be decompressed"));
            }
            if (i == 1) {
                curArray = byteArray;
            }
            for (int j = 0; j < (curArray.length); j++) {
                TestCase.assertEquals(curArray[j], outPutInf[j]);
            }
            TestCase.assertEquals(0, outPutInf[curArray.length]);
        }
    }

    /**
     * java.util.zip.Deflater#setDictionary(byte[])
     */
    public void test_setDictionary$B() {
        // This test is very close to getAdler()
        byte[] dictionaryArray = new byte[]{ 'e', 'r', 't', 'a', 'b', 2, 3 };
        byte[] byteArray = new byte[]{ 4, 5, 3, 2, 'a', 'b', 6, 7, 8, 9, 0, 's', '3', 'w', 'r' };
        byte[] outPutBuf = new byte[100];
        Deflater defl = new Deflater();
        long deflAdler = defl.getAdler();
        TestCase.assertEquals("No dictionary set, no data deflated, getAdler should return 1", 1, deflAdler);
        defl.setDictionary(dictionaryArray);
        deflAdler = defl.getAdler();
        // getting the checkSum value through the Adler32 class
        Adler32 adl = new Adler32();
        adl.update(dictionaryArray);
        long realAdler = adl.getValue();
        TestCase.assertEquals(deflAdler, realAdler);
        defl.setInput(byteArray);
        defl.finish();
        while (!(defl.finished())) {
            defl.deflate(outPutBuf);
        } 
        deflAdler = defl.getAdler();
        adl = new Adler32();
        adl.update(byteArray);
        realAdler = adl.getValue();
        // Deflate is finished and there were bytes deflated that did not occur
        // in the dictionaryArray, therefore a new dictionary was automatically
        // set.
        TestCase.assertEquals(realAdler, deflAdler);
        defl.end();
    }

    /**
     * java.util.zip.Deflater#setDictionary(byte[], int, int)
     */
    public void test_setDictionary$BII() {
        // This test is very close to getAdler()
        byte[] dictionaryArray = new byte[]{ 'e', 'r', 't', 'a', 'b', 2, 3, 'o', 't' };
        byte[] byteArray = new byte[]{ 4, 5, 3, 2, 'a', 'b', 6, 7, 8, 9, 0, 's', '3', 'w', 'r', 't', 'u', 'i', 'o', 4, 5, 6, 7 };
        byte[] outPutBuf = new byte[500];
        int offSet = 4;
        int length = 5;
        Deflater defl = new Deflater();
        long deflAdler = defl.getAdler();
        TestCase.assertEquals("No dictionary set, no data deflated, getAdler should return 1", 1, deflAdler);
        defl.setDictionary(dictionaryArray, offSet, length);
        deflAdler = defl.getAdler();
        // getting the checkSum value through the Adler32 class
        Adler32 adl = new Adler32();
        adl.update(dictionaryArray, offSet, length);
        long realAdler = adl.getValue();
        TestCase.assertEquals(deflAdler, realAdler);
        defl.setInput(byteArray);
        while (!(defl.needsInput())) {
            defl.deflate(outPutBuf);
        } 
        deflAdler = defl.getAdler();
        adl = new Adler32();
        adl.update(byteArray);
        realAdler = adl.getValue();
        // Deflate is finished and there were bytes deflated that did not occur
        // in the dictionaryArray, therefore a new dictionary was automatically
        // set.
        TestCase.assertEquals(realAdler, deflAdler);
        defl.end();
        // boundary check
        defl = new Deflater();
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                offSet = 0;
                length = (dictionaryArray.length) + 1;
            } else {
                offSet = (dictionaryArray.length) + 1;
                length = 1;
            }
            try {
                defl.setDictionary(dictionaryArray, offSet, length);
                TestCase.fail(((((("Test " + i) + ": boundary check for setDictionary failed for offset ") + offSet) + " and length ") + length));
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
    }

    /**
     * java.util.zip.Deflater#setInput(byte[])
     */
    public void test_setInput$B() {
        byte[] byteArray = new byte[]{ 1, 2, 3 };
        byte[] outPutBuf = new byte[50];
        byte[] outPutInf = new byte[50];
        Deflater defl = new Deflater();
        defl.setInput(byteArray);
        TestCase.assertTrue("the array buffer in setInput() is empty", (!(defl.needsInput())));
        // The second setInput() should be ignored since needsInput() return
        // false
        defl.setInput(byteArray);
        defl.finish();
        while (!(defl.finished())) {
            defl.deflate(outPutBuf);
        } 
        defl.end();
        Inflater infl = new Inflater();
        try {
            infl.setInput(outPutBuf);
            while (!(infl.finished())) {
                infl.inflate(outPutInf);
            } 
        } catch (DataFormatException e) {
            TestCase.fail("Invalid input to be decompressed");
        }
        for (int i = 0; i < (byteArray.length); i++) {
            TestCase.assertEquals(byteArray[i], outPutInf[i]);
        }
        TestCase.assertEquals(byteArray.length, infl.getTotalOut());
        infl.end();
    }

    /**
     * java.util.zip.Deflater#setInput(byte[], int, int)
     */
    public void test_setInput$BII() throws Exception {
        byte[] byteArray = new byte[]{ 1, 2, 3, 4, 5 };
        byte[] outPutBuf = new byte[50];
        byte[] outPutInf = new byte[50];
        int offSet = 1;
        int length = 3;
        Deflater defl = new Deflater();
        defl.setInput(byteArray, offSet, length);
        TestCase.assertFalse("the array buffer in setInput() is empty", defl.needsInput());
        // The second setInput() should be ignored since needsInput() return
        // false
        defl.setInput(byteArray, offSet, length);
        defl.finish();
        while (!(defl.finished())) {
            defl.deflate(outPutBuf);
        } 
        defl.end();
        Inflater infl = new Inflater();
        infl.setInput(outPutBuf);
        while (!(infl.finished())) {
            infl.inflate(outPutInf);
        } 
        for (int i = 0; i < length; i++) {
            TestCase.assertEquals(byteArray[(i + offSet)], outPutInf[i]);
        }
        TestCase.assertEquals(length, infl.getTotalOut());
        infl.end();
        // boundary check
        defl = new Deflater();
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                offSet = 0;
                length = (byteArray.length) + 1;
            } else {
                offSet = (byteArray.length) + 1;
                length = 1;
            }
            try {
                defl.setInput(byteArray, offSet, length);
                TestCase.fail(((((("Test " + i) + ": boundary check for setInput failed for offset ") + offSet) + " and length ") + length));
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
    }

    /**
     * java.util.zip.Deflater#setLevel(int)
     */
    public void test_setLevelI() throws Exception {
        // Very similar to test_Constructor(int)
        byte[] byteArray = new byte[100];
        InputStream inFile = Support_Resources.getStream("hyts_checkInput.txt");
        inFile.read(byteArray);
        inFile.close();
        byte[] outPutBuf;
        int totalOut;
        for (int i = 0; i < 10; i++) {
            Deflater defl = new Deflater();
            defl.setLevel(i);
            outPutBuf = new byte[500];
            defl.setInput(byteArray);
            while (!(defl.needsInput())) {
                defl.deflate(outPutBuf);
            } 
            defl.finish();
            while (!(defl.finished())) {
                defl.deflate(outPutBuf);
            } 
            totalOut = defl.getTotalOut();
            defl.end();
            outPutBuf = new byte[500];
            defl = new Deflater(i);
            defl.setInput(byteArray);
            while (!(defl.needsInput())) {
                defl.deflate(outPutBuf);
            } 
            defl.finish();
            while (!(defl.finished())) {
                defl.deflate(outPutBuf);
            } 
            TestCase.assertEquals(totalOut, defl.getTotalOut());
            defl.end();
        }
        // testing boundaries
        try {
            Deflater boundDefl = new Deflater();
            // Level must be between 0-9
            boundDefl.setLevel((-2));
            TestCase.fail("IllegalArgumentException not thrown when setting level to a number < 0.");
        } catch (IllegalArgumentException e) {
        }
        try {
            Deflater boundDefl = new Deflater();
            boundDefl.setLevel(10);
            TestCase.fail("IllegalArgumentException not thrown when setting level to a number > 9.");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * java.util.zip.Deflater#setStrategy(int)
     */
    public void test_setStrategyI() throws Exception {
        byte[] byteArray = new byte[100];
        InputStream inFile = Support_Resources.getStream("hyts_checkInput.txt");
        inFile.read(byteArray);
        inFile.close();
        for (int i = 0; i < 3; i++) {
            byte[] outPutBuf = new byte[500];
            DeflaterTest.MyDeflater mdefl = new DeflaterTest.MyDeflater();
            if (i == 0) {
                mdefl.setStrategy(mdefl.getDefStrategy());
            } else
                if (i == 1) {
                    mdefl.setStrategy(mdefl.getHuffman());
                } else {
                    mdefl.setStrategy(mdefl.getFiltered());
                }

            mdefl.setInput(byteArray);
            while (!(mdefl.needsInput())) {
                mdefl.deflate(outPutBuf);
            } 
            mdefl.finish();
            while (!(mdefl.finished())) {
                mdefl.deflate(outPutBuf);
            } 
            if (i == 0) {
                // System.out.println(mdefl.getTotalOut());
                // ran JDK and found that getTotalOut() = 86 for this particular
                // file
                TestCase.assertEquals("getTotalOut() for the default strategy did not correspond with JDK", 86, mdefl.getTotalOut());
            } else
                if (i == 1) {
                    // System.out.println(mdefl.getTotalOut());
                    // ran JDK and found that getTotalOut() = 100 for this
                    // particular file
                    TestCase.assertEquals("getTotalOut() for the Huffman strategy did not correspond with JDK", 100, mdefl.getTotalOut());
                } else {
                    // System.out.println(mdefl.getTotalOut());
                    // ran JDK and found that totalOut = 93 for this particular file
                    TestCase.assertEquals("Total Out for the Filtered strategy did not correspond with JDK", 93, mdefl.getTotalOut());
                }

            mdefl.end();
        }
        // Attempting to setStrategy to an invalid value
        try {
            Deflater defl = new Deflater();
            defl.setStrategy((-412));
            TestCase.fail("IllegalArgumentException not thrown when setting strategy to an invalid value.");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * java.util.zip.Deflater#Deflater()
     */
    public void test_Constructor() throws Exception {
        byte[] byteArray = new byte[100];
        InputStream inFile = Support_Resources.getStream("hyts_checkInput.txt");
        inFile.read(byteArray);
        inFile.close();
        Deflater defl = new Deflater();
        byte[] outPutBuf = new byte[500];
        defl.setInput(byteArray);
        while (!(defl.needsInput())) {
            defl.deflate(outPutBuf);
        } 
        defl.finish();
        while (!(defl.finished())) {
            defl.deflate(outPutBuf);
        } 
        int totalOut = defl.getTotalOut();
        defl.end();
        // creating a Deflater using the DEFAULT_COMPRESSION as the int
        DeflaterTest.MyDeflater mdefl = new DeflaterTest.MyDeflater();
        mdefl = new DeflaterTest.MyDeflater(mdefl.getDefCompression());
        outPutBuf = new byte[500];
        mdefl.setInput(byteArray);
        while (!(mdefl.needsInput())) {
            mdefl.deflate(outPutBuf);
        } 
        mdefl.finish();
        while (!(mdefl.finished())) {
            mdefl.deflate(outPutBuf);
        } 
        TestCase.assertEquals(totalOut, mdefl.getTotalOut());
        mdefl.end();
    }

    /**
     * java.util.zip.Deflater#Deflater(int, boolean)
     */
    public void test_ConstructorIZ() throws Exception {
        byte[] byteArray = new byte[]{ 4, 5, 3, 2, 'a', 'b', 6, 7, 8, 9, 0, 's', '3', 'w', 'r' };
        Deflater defl = new Deflater();
        byte[] outPutBuf = new byte[500];
        defl.setLevel(2);
        defl.setInput(byteArray);
        while (!(defl.needsInput())) {
            defl.deflate(outPutBuf);
        } 
        defl.finish();
        while (!(defl.finished())) {
            defl.deflate(outPutBuf);
        } 
        int totalOut = defl.getTotalOut();
        defl.end();
        outPutBuf = new byte[500];
        defl = new Deflater(2, false);
        defl.setInput(byteArray);
        while (!(defl.needsInput())) {
            defl.deflate(outPutBuf);
        } 
        defl.finish();
        while (!(defl.finished())) {
            defl.deflate(outPutBuf);
        } 
        TestCase.assertEquals(totalOut, defl.getTotalOut());
        defl.end();
        outPutBuf = new byte[500];
        defl = new Deflater(2, true);
        defl.setInput(byteArray);
        while (!(defl.needsInput())) {
            defl.deflate(outPutBuf);
        } 
        defl.finish();
        while (!(defl.finished())) {
            defl.deflate(outPutBuf);
        } 
        TestCase.assertTrue("getTotalOut() should not be equal comparing two Deflaters with different header options.", ((defl.getTotalOut()) != totalOut));
        defl.end();
        byte[] outPutInf = new byte[500];
        Inflater infl = new Inflater(true);
        while (!(infl.finished())) {
            if (infl.needsInput()) {
                infl.setInput(outPutBuf);
            }
            infl.inflate(outPutInf);
        } 
        for (int i = 0; i < (byteArray.length); i++) {
            TestCase.assertEquals(byteArray[i], outPutInf[i]);
        }
        TestCase.assertEquals("final decompressed data contained more bytes than original - constructorIZ", 0, outPutInf[byteArray.length]);
        infl.end();
        infl = new Inflater(false);
        outPutInf = new byte[500];
        int r = 0;
        try {
            while (!(infl.finished())) {
                if (infl.needsInput()) {
                    infl.setInput(outPutBuf);
                }
                infl.inflate(outPutInf);
            } 
        } catch (DataFormatException e) {
            r = 1;
        }
        TestCase.assertEquals("header option did not correspond", 1, r);
        // testing boundaries
        try {
            Deflater boundDefl = new Deflater();
            // Level must be between 0-9
            boundDefl.setLevel((-2));
            TestCase.fail("IllegalArgumentException not thrown when setting level to a number < 0.");
        } catch (IllegalArgumentException e) {
        }
        try {
            Deflater boundDefl = new Deflater();
            boundDefl.setLevel(10);
            TestCase.fail("IllegalArgumentException not thrown when setting level to a number > 9.");
        } catch (IllegalArgumentException e) {
        }
        try {
            Deflater boundDefl = new Deflater((-2), true);
            TestCase.fail("IllegalArgumentException not thrown when passing level to a number < 0.");
        } catch (IllegalArgumentException e) {
        }
        try {
            Deflater boundDefl = new Deflater(10, true);
            TestCase.fail("IllegalArgumentException not thrown when passing level to a number > 9.");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * java.util.zip.Deflater#Deflater(int)
     */
    public void test_ConstructorI() throws Exception {
        byte[] byteArray = new byte[100];
        InputStream inFile = Support_Resources.getStream("hyts_checkInput.txt");
        inFile.read(byteArray);
        inFile.close();
        byte[] outPutBuf = new byte[500];
        Deflater defl = new Deflater(3);
        defl.setInput(byteArray);
        while (!(defl.needsInput())) {
            defl.deflate(outPutBuf);
        } 
        defl.finish();
        while (!(defl.finished())) {
            defl.deflate(outPutBuf);
        } 
        int totalOut = defl.getTotalOut();
        defl.end();
        // test to see if the compression ratio is the same as setting the level
        // on a deflater
        outPutBuf = new byte[500];
        defl = new Deflater();
        defl.setLevel(3);
        defl.setInput(byteArray);
        while (!(defl.needsInput())) {
            defl.deflate(outPutBuf);
        } 
        defl.finish();
        while (!(defl.finished())) {
            defl.deflate(outPutBuf);
        } 
        TestCase.assertEquals(totalOut, defl.getTotalOut());
        defl.end();
        // testing boundaries
        try {
            Deflater boundDefl = new Deflater();
            // Level must be between 0-9
            boundDefl.setLevel((-2));
            TestCase.fail("IllegalArgumentException not thrown when setting level to a number < 0.");
        } catch (IllegalArgumentException e) {
        }
        try {
            Deflater boundDefl = new Deflater();
            boundDefl.setLevel(10);
            TestCase.fail("IllegalArgumentException not thrown when setting level to a number > 9.");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * java.util.zip.Deflater()
     */
    public void test_needsDictionary() {
        Deflater inf = new Deflater();
        TestCase.assertEquals(0, inf.getTotalIn());
        TestCase.assertEquals(0, inf.getTotalOut());
        TestCase.assertEquals(0, inf.getBytesRead());
        TestCase.assertEquals(0, inf.getBytesWritten());
    }

    /**
     *
     *
     * @throws DataFormatException
     * 		
     * @throws UnsupportedEncodingException
    java.util.zip.Deflater#getBytesRead()
     * 		
     */
    public void test_getBytesRead() throws UnsupportedEncodingException, DataFormatException {
        // Regression test for HARMONY-158
        Deflater def = new Deflater();
        TestCase.assertEquals(0, def.getTotalIn());
        TestCase.assertEquals(0, def.getTotalOut());
        TestCase.assertEquals(0, def.getBytesRead());
        // Encode a String into bytes
        String inputString = "blahblahblah??";
        byte[] input = inputString.getBytes("UTF-8");
        // Compress the bytes
        byte[] output = new byte[100];
        def.setInput(input);
        def.finish();
        int compressedDataLength = def.deflate(output);
        TestCase.assertEquals(14, def.getTotalIn());
        TestCase.assertEquals(compressedDataLength, def.getTotalOut());
        TestCase.assertEquals(14, def.getBytesRead());
    }

    /**
     *
     *
     * @throws DataFormatException
     * 		
     * @throws UnsupportedEncodingException
    java.util.zip.Deflater#getBytesRead()
     * 		
     */
    public void test_getBytesWritten() throws UnsupportedEncodingException, DataFormatException {
        // Regression test for HARMONY-158
        Deflater def = new Deflater();
        TestCase.assertEquals(0, def.getTotalIn());
        TestCase.assertEquals(0, def.getTotalOut());
        TestCase.assertEquals(0, def.getBytesWritten());
        // Encode a String into bytes
        String inputString = "blahblahblah??";
        byte[] input = inputString.getBytes("UTF-8");
        // Compress the bytes
        byte[] output = new byte[100];
        def.setInput(input);
        def.finish();
        int compressedDataLength = def.deflate(output);
        TestCase.assertEquals(14, def.getTotalIn());
        TestCase.assertEquals(compressedDataLength, def.getTotalOut());
        TestCase.assertEquals(compressedDataLength, def.getBytesWritten());
    }

    // Regression Test for HARMONY-2481
    public void test_deflate_beforeSetInput() throws Exception {
        Deflater deflater = new Deflater();
        deflater.finish();
        byte[] buffer = new byte[1024];
        TestCase.assertEquals(8, deflater.deflate(buffer));
        byte[] expectedBytes = new byte[]{ 120, -100, 3, 0, 0, 0, 0, 1 };
        for (int i = 0; i < (expectedBytes.length); i++) {
            TestCase.assertEquals(expectedBytes[i], buffer[i]);
        }
    }
}

