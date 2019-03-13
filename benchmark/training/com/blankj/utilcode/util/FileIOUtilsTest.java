package com.blankj.utilcode.util;


import java.io.FileInputStream;
import org.junit.Assert;
import org.junit.Test;


/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2017/05/24
 *     desc  : test FileIOUtils
 * </pre>
 */
public class FileIOUtilsTest extends BaseTest {
    @Test
    public void writeFileFromIS() throws Exception {
        Assert.assertTrue(FileIOUtils.writeFileFromIS(((TestConfig.PATH_TEMP) + "UTF8.txt"), new FileInputStream(((TestConfig.PATH_FILE) + "UTF8.txt")), false));
        Assert.assertTrue(FileIOUtils.writeFileFromIS(((TestConfig.PATH_TEMP) + "UTF8.txt"), new FileInputStream(((TestConfig.PATH_FILE) + "UTF8.txt")), true));
    }

    @Test
    public void writeFileFromBytes() {
        // String p = path + "test.txt";
        // String p1 = path + "copy.zip";
        // byte[] data = new byte[(1 << 20) * 100];
        // long st, end;
        // FileUtils.deleteFile(p);
        // 
        // st = System.currentTimeMillis();
        // for (int i = 0; i < 100; i++) {
        // FileIOUtils.writeFileFromBytesByStream(p, data, true);
        // }
        // end = System.currentTimeMillis();
        // System.out.println(end - st);
        // FileUtils.deleteFile(p);
        // 
        // st = System.currentTimeMillis();
        // for (int i = 0; i < 100; i++) {
        // FileIOUtils.writeFileFromBytesByChannel(p, data, true);
        // }
        // end = System.currentTimeMillis();
        // System.out.println(end - st);
        // FileUtils.deleteFile(p);
        // 
        // st = System.currentTimeMillis();
        // for (int i = 0; i < 100; i++) {
        // FileIOUtils.writeFileFromBytesByMap(p, data, true, false);
        // }
        // end = System.currentTimeMillis();
        // System.out.println(end - st);
        // FileUtils.deleteFile(p);
    }

    @Test
    public void readFile2Bytes() {
        // String p = path + "test.app.zip";
        // long st, end;
        // st = System.currentTimeMillis();
        // FileIOUtils.readFile2BytesByStream(p);
        // end = System.currentTimeMillis();
        // System.out.println(end - st);
        // st = System.currentTimeMillis();
        // FileIOUtils.readFile2BytesByChannel(p);
        // end = System.currentTimeMillis();
        // System.out.println(end - st);
        // st = System.currentTimeMillis();
        // FileIOUtils.readFile2BytesByMap(p);
        // end = System.currentTimeMillis();
        // System.out.println(end - st);
    }
}

