package com.hankcs.hanlp.corpus.io;


import com.hankcs.hanlp.utility.ByteUtil;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import junit.framework.TestCase;


// /**
// * ???-Xms512m -Xmx512m -Xmn256m???<br>
// *     java.lang.OutOfMemoryError: GC overhead limit exceeded
// * @throws Exception
// */
// public void testLoadByteArray() throws Exception
// {
// ByteArray byteArray = ByteArray.createByteArray(HanLP.Config.MaxEntModelPath + Predefine.BIN_EXT);
// MaxEntModel.create(byteArray);
// }
// 
// /**
// * ???-Xms512m -Xmx512m -Xmn256m???
// * @throws Exception
// */
// public void testLoadByteArrayStream() throws Exception
// {
// ByteArray byteArray = ByteArrayFileStream.createByteArrayFileStream(HanLP.Config.MaxEntModelPath + Predefine.BIN_EXT);
// MaxEntModel.create(byteArray);
// }
// 
// public void testBenchmark() throws Exception
// {
// long start;
// 
// ByteArray byteArray = ByteArray.createByteArray(HanLP.Config.MaxEntModelPath + Predefine.BIN_EXT);
// MaxEntModel.create(byteArray);
// 
// byteArray = ByteArrayFileStream.createByteArrayFileStream(HanLP.Config.MaxEntModelPath + Predefine.BIN_EXT);
// MaxEntModel.create(byteArray);
// 
// start = System.currentTimeMillis();
// byteArray = ByteArray.createByteArray(HanLP.Config.MaxEntModelPath + Predefine.BIN_EXT);
// MaxEntModel.create(byteArray);
// System.out.printf("ByteArray: %d ms\n", (System.currentTimeMillis() - start));
// 
// start = System.currentTimeMillis();
// byteArray = ByteArrayFileStream.createByteArrayFileStream(HanLP.Config.MaxEntModelPath + Predefine.BIN_EXT);
// MaxEntModel.create(byteArray);
// System.out.printf("ByteArrayStream: %d ms\n", (System.currentTimeMillis() - start));
// 
// //        ByteArray: 2626 ms
// //        ByteArrayStream: 4165 ms
// }
public class ByteArrayTest extends TestCase {
    static String DATA_TEST_OUT_BIN;

    private File tempFile;

    public void testReadDouble() throws Exception {
        DataOutputStream out = new DataOutputStream(new FileOutputStream(ByteArrayTest.DATA_TEST_OUT_BIN));
        double d = 0.123456789;
        out.writeDouble(d);
        int i = 3389;
        out.writeInt(i);
        ByteArray byteArray = ByteArray.createByteArray(ByteArrayTest.DATA_TEST_OUT_BIN);
        TestCase.assertEquals(d, byteArray.nextDouble());
        TestCase.assertEquals(i, byteArray.nextInt());
    }

    public void testReadUTF() throws Exception {
        DataOutputStream out = new DataOutputStream(new FileOutputStream(ByteArrayTest.DATA_TEST_OUT_BIN));
        String utf = "hankcs??123";
        out.writeUTF(utf);
        ByteArray byteArray = ByteArray.createByteArray(ByteArrayTest.DATA_TEST_OUT_BIN);
        TestCase.assertEquals(utf, byteArray.nextUTF());
    }

    public void testReadUnsignedShort() throws Exception {
        DataOutputStream out = new DataOutputStream(new FileOutputStream(ByteArrayTest.DATA_TEST_OUT_BIN));
        int utflen = 123;
        out.writeByte(((byte) ((utflen >>> 8) & 255)));
        out.writeByte(((byte) ((utflen >>> 0) & 255)));
        ByteArray byteArray = ByteArray.createByteArray(ByteArrayTest.DATA_TEST_OUT_BIN);
        TestCase.assertEquals(utflen, byteArray.nextUnsignedShort());
    }

    public void testConvertCharToInt() throws Exception {
        // for (int i = 0; i < Integer.MAX_VALUE; ++i)
        for (int i = 0; i < 1024; ++i) {
            int n = i;
            char[] twoChar = ByteUtil.convertIntToTwoChar(n);
            TestCase.assertEquals(n, ByteUtil.convertTwoCharToInt(twoChar[0], twoChar[1]));
        }
    }

    public void testNextBoolean() throws Exception {
        DataOutputStream out = new DataOutputStream(new FileOutputStream(tempFile));
        out.writeBoolean(true);
        out.writeBoolean(false);
        ByteArray byteArray = ByteArray.createByteArray(tempFile.getAbsolutePath());
        TestCase.assertNotNull(byteArray);
        TestCase.assertEquals(byteArray.nextBoolean(), true);
        TestCase.assertEquals(byteArray.nextBoolean(), false);
        tempFile.deleteOnExit();
    }

    public void testWriteAndRead() throws Exception {
        DataOutputStream out = new DataOutputStream(new FileOutputStream(ByteArrayTest.DATA_TEST_OUT_BIN));
        out.writeChar('H');
        out.writeChar('e');
        out.writeChar('l');
        out.writeChar('l');
        out.writeChar('o');
        out.close();
        ByteArray byteArray = ByteArray.createByteArray(ByteArrayTest.DATA_TEST_OUT_BIN);
        while (byteArray.hasMore()) {
            byteArray.nextChar();
            // System.out.println(byteArray.nextChar());
        } 
    }

    public void testWriteBigFile() throws Exception {
        DataOutputStream out = new DataOutputStream(new FileOutputStream(ByteArrayTest.DATA_TEST_OUT_BIN));
        for (int i = 0; i < 10000; i++) {
            out.writeInt(i);
        }
        out.close();
    }

    public void testStream() throws Exception {
        ByteArray byteArray = ByteArrayFileStream.createByteArrayFileStream(ByteArrayTest.DATA_TEST_OUT_BIN);
        while (byteArray.hasMore()) {
            System.out.println(byteArray.nextInt());
        } 
    }
}

