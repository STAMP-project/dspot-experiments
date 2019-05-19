package org.apache.commons.io;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.io.testtools.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class AmplFilenameUtilsTestCase {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final String SEP = "" + (File.separatorChar);

    private static final boolean WINDOWS = (File.separatorChar) == '\\';

    private File testFile1;

    private File testFile2;

    private int testFile1Size;

    private int testFile2Size;

    @Before
    public void setUp() throws Exception {
        testFile1 = temporaryFolder.newFile("file1-test.txt");
        testFile2 = temporaryFolder.newFile("file1a-test.txt");
        testFile1Size = ((int) (testFile1.length()));
        testFile2Size = ((int) (testFile2.length()));
        if (!(testFile1.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile1)) + " as the parent directory does not exist"));
        }
        try (final BufferedOutputStream output3 = new BufferedOutputStream(new FileOutputStream(testFile1))) {
            TestUtils.generateTestData(output3, testFile1Size);
        }
        if (!(testFile2.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile2)) + " as the parent directory does not exist"));
        }
        try (final BufferedOutputStream output2 = new BufferedOutputStream(new FileOutputStream(testFile2))) {
            TestUtils.generateTestData(output2, testFile2Size);
        }
        if (!(testFile1.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile1)) + " as the parent directory does not exist"));
        }
        try (final BufferedOutputStream output1 = new BufferedOutputStream(new FileOutputStream(testFile1))) {
            TestUtils.generateTestData(output1, testFile1Size);
        }
        if (!(testFile2.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile2)) + " as the parent directory does not exist"));
        }
        try (final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(testFile2))) {
            TestUtils.generateTestData(output, testFile2Size);
        }
    }

    @Test(timeout = 10000)
    public void testGetPath_literalMutationString1100() throws Exception {
        String o_testGetPath_literalMutationString1100__1 = FilenameUtils.getPath(null);
        Assert.assertNull(o_testGetPath_literalMutationString1100__1);
        String o_testGetPath_literalMutationString1100__2 = FilenameUtils.getPath("noseperator.inthispath");
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__2);
        String o_testGetPath_literalMutationString1100__3 = FilenameUtils.getPath("/noseperator.inthispath");
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__3);
        String o_testGetPath_literalMutationString1100__4 = FilenameUtils.getPath("\\noseperator.inthispath");
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__4);
        FilenameUtils.getPath("a/b/c.txt");
        FilenameUtils.getPath("a/b/c");
        FilenameUtils.getPath("a/b/c/");
        String o_testGetPath_literalMutationString1100__8 = FilenameUtils.getPath("a\\b\\c");
        Assert.assertEquals("a\\b\\", o_testGetPath_literalMutationString1100__8);
        String o_testGetPath_literalMutationString1100__9 = FilenameUtils.getPath(":");
        Assert.assertNull(o_testGetPath_literalMutationString1100__9);
        String o_testGetPath_literalMutationString1100__10 = FilenameUtils.getPath("1:/a/b/c.txt");
        Assert.assertNull(o_testGetPath_literalMutationString1100__10);
        String o_testGetPath_literalMutationString1100__11 = FilenameUtils.getPath("1:");
        Assert.assertNull(o_testGetPath_literalMutationString1100__11);
        String o_testGetPath_literalMutationString1100__12 = FilenameUtils.getPath("1:a");
        Assert.assertNull(o_testGetPath_literalMutationString1100__12);
        String o_testGetPath_literalMutationString1100__13 = FilenameUtils.getPath("///a/b/c.txt");
        Assert.assertNull(o_testGetPath_literalMutationString1100__13);
        String o_testGetPath_literalMutationString1100__14 = FilenameUtils.getPath("//a");
        Assert.assertNull(o_testGetPath_literalMutationString1100__14);
        String o_testGetPath_literalMutationString1100__15 = FilenameUtils.getPath("");
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__15);
        String o_testGetPath_literalMutationString1100__16 = FilenameUtils.getPath("C:");
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__16);
        String o_testGetPath_literalMutationString1100__17 = FilenameUtils.getPath("C:/");
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__17);
        String o_testGetPath_literalMutationString1100__18 = FilenameUtils.getPath("//s,erver/");
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__18);
        String o_testGetPath_literalMutationString1100__19 = FilenameUtils.getPath("~");
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__19);
        String o_testGetPath_literalMutationString1100__20 = FilenameUtils.getPath("~/");
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__20);
        String o_testGetPath_literalMutationString1100__21 = FilenameUtils.getPath("~user");
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__21);
        String o_testGetPath_literalMutationString1100__22 = FilenameUtils.getPath("~user/");
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__22);
        FilenameUtils.getPath("a/b/c.txt");
        FilenameUtils.getPath("a/b/c.txt");
        FilenameUtils.getPath("/a/b/c.txt");
        String o_testGetPath_literalMutationString1100__26 = FilenameUtils.getPath("C:a");
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__26);
        FilenameUtils.getPath("C:a/b/c.txt");
        FilenameUtils.getPath("C:/a/b/c.txt");
        FilenameUtils.getPath("//server/a/b/c.txt");
        FilenameUtils.getPath("~/a/b/c.txt");
        FilenameUtils.getPath("~user/a/b/c.txt");
        Assert.assertNull(o_testGetPath_literalMutationString1100__1);
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__2);
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__3);
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__4);
        Assert.assertEquals("a\\b\\", o_testGetPath_literalMutationString1100__8);
        Assert.assertNull(o_testGetPath_literalMutationString1100__9);
        Assert.assertNull(o_testGetPath_literalMutationString1100__10);
        Assert.assertNull(o_testGetPath_literalMutationString1100__11);
        Assert.assertNull(o_testGetPath_literalMutationString1100__12);
        Assert.assertNull(o_testGetPath_literalMutationString1100__13);
        Assert.assertNull(o_testGetPath_literalMutationString1100__14);
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__15);
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__16);
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__17);
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__18);
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__19);
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__20);
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__21);
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__22);
        Assert.assertEquals("", o_testGetPath_literalMutationString1100__26);
    }
}

