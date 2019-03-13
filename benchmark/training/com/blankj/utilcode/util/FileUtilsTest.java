package com.blankj.utilcode.util;


import FileUtils.OnReplaceListener;
import java.io.File;
import java.io.FileFilter;
import org.junit.Assert;
import org.junit.Test;


/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/08/19
 *     desc  : test FileUtils
 * </pre>
 */
public class FileUtilsTest extends BaseTest {
    private FileFilter mFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.getName().endsWith("8.txt");
        }
    };

    private OnReplaceListener mListener = new FileUtils.OnReplaceListener() {
        @Override
        public boolean onReplace() {
            return true;
        }
    };

    @Test
    public void getFileByPath() {
        Assert.assertNull(FileUtils.getFileByPath(" "));
        Assert.assertNotNull(FileUtils.getFileByPath(TestConfig.PATH_FILE));
    }

    @Test
    public void isFileExists() {
        Assert.assertTrue(FileUtils.isFileExists(((TestConfig.PATH_FILE) + "UTF8.txt")));
        Assert.assertFalse(FileUtils.isFileExists(((TestConfig.PATH_FILE) + "UTF8")));
    }

    @Test
    public void rename() {
        Assert.assertTrue(FileUtils.rename(((TestConfig.PATH_FILE) + "GBK.txt"), "GBK1.txt"));
        Assert.assertTrue(FileUtils.rename(((TestConfig.PATH_FILE) + "GBK1.txt"), "GBK.txt"));
    }

    @Test
    public void isDir() {
        Assert.assertFalse(FileUtils.isDir(((TestConfig.PATH_FILE) + "UTF8.txt")));
        Assert.assertTrue(FileUtils.isDir(TestConfig.PATH_FILE));
    }

    @Test
    public void isFile() {
        Assert.assertTrue(FileUtils.isFile(((TestConfig.PATH_FILE) + "UTF8.txt")));
        Assert.assertFalse(FileUtils.isFile(TestConfig.PATH_FILE));
    }

    @Test
    public void createOrExistsDir() {
        Assert.assertTrue(FileUtils.createOrExistsDir(((TestConfig.PATH_FILE) + "new Dir")));
        Assert.assertTrue(FileUtils.createOrExistsDir(TestConfig.PATH_FILE));
        Assert.assertTrue(FileUtils.deleteDir(((TestConfig.PATH_FILE) + "new Dir")));
    }

    @Test
    public void createOrExistsFile() {
        Assert.assertTrue(FileUtils.createOrExistsFile(((TestConfig.PATH_FILE) + "new File")));
        Assert.assertFalse(FileUtils.createOrExistsFile(TestConfig.PATH_FILE));
        Assert.assertTrue(FileUtils.deleteFile(((TestConfig.PATH_FILE) + "new File")));
    }

    @Test
    public void createFileByDeleteOldFile() {
        Assert.assertTrue(FileUtils.createFileByDeleteOldFile(((TestConfig.PATH_FILE) + "new File")));
        Assert.assertFalse(FileUtils.createFileByDeleteOldFile(TestConfig.PATH_FILE));
        Assert.assertTrue(FileUtils.deleteFile(((TestConfig.PATH_FILE) + "new File")));
    }

    @Test
    public void copyDir() {
        Assert.assertFalse(FileUtils.copyDir(TestConfig.PATH_FILE, TestConfig.PATH_FILE, mListener));
        Assert.assertFalse(FileUtils.copyDir(TestConfig.PATH_FILE, ((TestConfig.PATH_FILE) + "new Dir"), mListener));
        Assert.assertTrue(FileUtils.copyDir(TestConfig.PATH_FILE, TestConfig.PATH_TEMP, mListener));
        Assert.assertTrue(FileUtils.deleteDir(TestConfig.PATH_TEMP));
    }

    @Test
    public void copyFile() {
        Assert.assertFalse(FileUtils.copyFile(((TestConfig.PATH_FILE) + "GBK.txt"), ((TestConfig.PATH_FILE) + "GBK.txt"), mListener));
        Assert.assertTrue(FileUtils.copyFile(((TestConfig.PATH_FILE) + "GBK.txt"), ((((TestConfig.PATH_FILE) + "new Dir") + (TestConfig.FILE_SEP)) + "GBK.txt"), mListener));
        Assert.assertTrue(FileUtils.copyFile(((TestConfig.PATH_FILE) + "GBK.txt"), ((TestConfig.PATH_TEMP) + "GBK.txt"), mListener));
        Assert.assertTrue(FileUtils.deleteDir(((TestConfig.PATH_FILE) + "new Dir")));
        Assert.assertTrue(FileUtils.deleteDir(TestConfig.PATH_TEMP));
    }

    @Test
    public void moveDir() {
        Assert.assertFalse(FileUtils.moveDir(TestConfig.PATH_FILE, TestConfig.PATH_FILE, mListener));
        Assert.assertFalse(FileUtils.moveDir(TestConfig.PATH_FILE, ((TestConfig.PATH_FILE) + "new Dir"), mListener));
        Assert.assertTrue(FileUtils.moveDir(TestConfig.PATH_FILE, TestConfig.PATH_TEMP, mListener));
        Assert.assertTrue(FileUtils.moveDir(TestConfig.PATH_TEMP, TestConfig.PATH_FILE, mListener));
    }

    @Test
    public void moveFile() {
        Assert.assertFalse(FileUtils.moveFile(((TestConfig.PATH_FILE) + "GBK.txt"), ((TestConfig.PATH_FILE) + "GBK.txt"), mListener));
        Assert.assertTrue(FileUtils.moveFile(((TestConfig.PATH_FILE) + "GBK.txt"), ((TestConfig.PATH_TEMP) + "GBK.txt"), mListener));
        Assert.assertTrue(FileUtils.moveFile(((TestConfig.PATH_TEMP) + "GBK.txt"), ((TestConfig.PATH_FILE) + "GBK.txt"), mListener));
        FileUtils.deleteDir(TestConfig.PATH_TEMP);
    }

    @Test
    public void listFilesInDir() {
        System.out.println(FileUtils.listFilesInDir(TestConfig.PATH_FILE, false).toString());
        System.out.println(FileUtils.listFilesInDir(TestConfig.PATH_FILE, true).toString());
    }

    @Test
    public void listFilesInDirWithFilter() {
        System.out.println(FileUtils.listFilesInDirWithFilter(TestConfig.PATH_FILE, mFilter, false).toString());
        System.out.println(FileUtils.listFilesInDirWithFilter(TestConfig.PATH_FILE, mFilter, true).toString());
    }

    @Test
    public void getFileLastModified() {
        System.out.println(TimeUtils.millis2String(FileUtils.getFileLastModified(TestConfig.PATH_FILE)));
    }

    @Test
    public void getFileCharsetSimple() {
        Assert.assertEquals("GBK", FileUtils.getFileCharsetSimple(((TestConfig.PATH_FILE) + "GBK.txt")));
        Assert.assertEquals("Unicode", FileUtils.getFileCharsetSimple(((TestConfig.PATH_FILE) + "Unicode.txt")));
        Assert.assertEquals("UTF-8", FileUtils.getFileCharsetSimple(((TestConfig.PATH_FILE) + "UTF8.txt")));
        Assert.assertEquals("UTF-16BE", FileUtils.getFileCharsetSimple(((TestConfig.PATH_FILE) + "UTF16BE.txt")));
    }

    @Test
    public void getFileLines() {
        Assert.assertEquals(7, FileUtils.getFileLines(((TestConfig.PATH_FILE) + "UTF8.txt")));
    }

    @Test
    public void getDirSize() {
        System.out.println(FileUtils.getDirSize(TestConfig.PATH_FILE));
    }

    @Test
    public void getFileSize() {
        System.out.println(FileUtils.getFileSize(((TestConfig.PATH_FILE) + "UTF8.txt")));
    }

    @Test
    public void getDirLength() {
        System.out.println(FileUtils.getDirLength(TestConfig.PATH_FILE));
    }

    @Test
    public void getFileLength() {
        System.out.println(FileUtils.getFileLength(((TestConfig.PATH_FILE) + "UTF8.txt")));
        // System.out.println(FileUtils.getFileLength("https://raw.githubusercontent.com/Blankj/AndroidUtilCode/85bc44d1c8adb31027870ea4cb7a931700c80cad/LICENSE"));
    }

    @Test
    public void getFileMD5ToString() {
        Assert.assertEquals("249D3E76851DCC56C945994DE9DAC406", FileUtils.getFileMD5ToString(((TestConfig.PATH_FILE) + "UTF8.txt")));
    }

    @Test
    public void getDirName() {
        Assert.assertEquals(TestConfig.PATH_FILE, FileUtils.getDirName(new File(((TestConfig.PATH_FILE) + "UTF8.txt"))));
        Assert.assertEquals(TestConfig.PATH_FILE, FileUtils.getDirName(((TestConfig.PATH_FILE) + "UTF8.txt")));
    }

    @Test
    public void getFileName() {
        Assert.assertEquals("UTF8.txt", FileUtils.getFileName(((TestConfig.PATH_FILE) + "UTF8.txt")));
        Assert.assertEquals("UTF8.txt", FileUtils.getFileName(new File(((TestConfig.PATH_FILE) + "UTF8.txt"))));
    }

    @Test
    public void getFileNameNoExtension() {
        Assert.assertEquals("UTF8", FileUtils.getFileNameNoExtension(((TestConfig.PATH_FILE) + "UTF8.txt")));
        Assert.assertEquals("UTF8", FileUtils.getFileNameNoExtension(new File(((TestConfig.PATH_FILE) + "UTF8.txt"))));
    }

    @Test
    public void getFileExtension() {
        Assert.assertEquals("txt", FileUtils.getFileExtension(new File(((TestConfig.PATH_FILE) + "UTF8.txt"))));
        Assert.assertEquals("txt", FileUtils.getFileExtension(((TestConfig.PATH_FILE) + "UTF8.txt")));
    }
}

