package com.blankj.utilcode.util;


import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;


/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/09/10
 *     desc  : test ZipUtils
 * </pre>
 */
public class ZipUtilsTest extends BaseTest {
    private String zipFile = (TestConfig.PATH_TEMP) + "zipFile.zip";

    private String zipFiles = (TestConfig.PATH_TEMP) + "zipFiles.zip";

    @Test
    public void zipFiles() throws Exception {
        List<String> files = new ArrayList<>();
        files.add(((TestConfig.PATH_ZIP) + "test.txt"));
        files.add(TestConfig.PATH_ZIP);
        files.add(((TestConfig.PATH_ZIP) + "testDir"));
        TestCase.assertTrue(ZipUtils.zipFiles(files, zipFiles));
    }

    @Test
    public void unzipFile() throws Exception {
        System.out.println(ZipUtils.unzipFile(zipFile, TestConfig.PATH_TEMP));
    }

    @Test
    public void unzipFileByKeyword() throws Exception {
        System.out.println(ZipUtils.unzipFileByKeyword(zipFile, TestConfig.PATH_TEMP, null).toString());
    }

    @Test
    public void getFilesPath() throws Exception {
        System.out.println(ZipUtils.getFilesPath(zipFile));
    }

    @Test
    public void getComments() throws Exception {
        System.out.println(ZipUtils.getComments(zipFile));
    }
}

