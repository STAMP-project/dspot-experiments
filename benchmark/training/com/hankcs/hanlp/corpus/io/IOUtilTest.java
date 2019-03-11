package com.hankcs.hanlp.corpus.io;


import IOUtil.LineIterator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Random;
import junit.framework.TestCase;


public class IOUtilTest extends TestCase {
    public void testReadBytesFromOtherInputStream() throws Exception {
        Random random = new Random(System.currentTimeMillis());
        byte[] originalData = new byte[1024 * 1024];// 1MB

        random.nextBytes(originalData);
        ByteArrayInputStream is = new ByteArrayInputStream(originalData) {
            @Override
            public synchronized int available() {
                int realAvailable = super.available();
                if (realAvailable > 0) {
                    return 2048;// ??????InputStream

                }
                return realAvailable;
            }
        };
        byte[] readData = IOUtil.readBytesFromOtherInputStream(is);
        TestCase.assertEquals(originalData.length, readData.length);
        for (int i = 0; i < (originalData.length); i++) {
            TestCase.assertEquals(originalData[i], readData[i]);
        }
    }

    public void testUTF8BOM() throws Exception {
        File tempFile = File.createTempFile("hanlp-", ".txt");
        tempFile.deleteOnExit();
        IOUtil.saveTxt(tempFile.getAbsolutePath(), "\ufeff\u7b2c1\u884c\n\u7b2c2\u884c");
        IOUtil.LineIterator lineIterator = new IOUtil.LineIterator(tempFile.getAbsolutePath());
        int i = 1;
        for (String line : lineIterator) {
            TestCase.assertEquals(String.format("?%d?", (i++)), line);
        }
    }
}

