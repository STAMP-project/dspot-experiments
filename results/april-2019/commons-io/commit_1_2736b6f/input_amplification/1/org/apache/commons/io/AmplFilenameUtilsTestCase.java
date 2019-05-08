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
    public void testGetPath_literalMutationString1154() throws Exception {
        String o_testGetPath_literalMutationString1154__1 = FilenameUtils.getPath(null);
        Assert.assertNull(o_testGetPath_literalMutationString1154__1);
        String o_testGetPath_literalMutationString1154__2 = FilenameUtils.getPath("noseperator.inthispath");
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__2);
        String o_testGetPath_literalMutationString1154__3 = FilenameUtils.getPath("/noseperator.inthispath");
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__3);
        String o_testGetPath_literalMutationString1154__4 = FilenameUtils.getPath("\\noseperator.inthispath");
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__4);
        String o_testGetPath_literalMutationString1154__5 = FilenameUtils.getPath("a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1154__5);
        String o_testGetPath_literalMutationString1154__6 = FilenameUtils.getPath("a/b/c");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1154__6);
        String o_testGetPath_literalMutationString1154__7 = FilenameUtils.getPath("a/b/c/");
        Assert.assertEquals("a/b/c/", o_testGetPath_literalMutationString1154__7);
        String o_testGetPath_literalMutationString1154__8 = FilenameUtils.getPath("a\\b\\c");
        Assert.assertEquals("a\\b\\", o_testGetPath_literalMutationString1154__8);
        String o_testGetPath_literalMutationString1154__9 = FilenameUtils.getPath(":");
        Assert.assertNull(o_testGetPath_literalMutationString1154__9);
        String o_testGetPath_literalMutationString1154__10 = FilenameUtils.getPath("1:/a/b/c.txt");
        Assert.assertNull(o_testGetPath_literalMutationString1154__10);
        String o_testGetPath_literalMutationString1154__11 = FilenameUtils.getPath("1:");
        Assert.assertNull(o_testGetPath_literalMutationString1154__11);
        String o_testGetPath_literalMutationString1154__12 = FilenameUtils.getPath("1:a");
        Assert.assertNull(o_testGetPath_literalMutationString1154__12);
        String o_testGetPath_literalMutationString1154__13 = FilenameUtils.getPath("///a/b/c.txt");
        Assert.assertNull(o_testGetPath_literalMutationString1154__13);
        String o_testGetPath_literalMutationString1154__14 = FilenameUtils.getPath("//a");
        Assert.assertNull(o_testGetPath_literalMutationString1154__14);
        String o_testGetPath_literalMutationString1154__15 = FilenameUtils.getPath("");
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__15);
        String o_testGetPath_literalMutationString1154__16 = FilenameUtils.getPath("C:");
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__16);
        String o_testGetPath_literalMutationString1154__17 = FilenameUtils.getPath("C:/");
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__17);
        String o_testGetPath_literalMutationString1154__18 = FilenameUtils.getPath("//server/");
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__18);
        String o_testGetPath_literalMutationString1154__19 = FilenameUtils.getPath("~");
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__19);
        String o_testGetPath_literalMutationString1154__20 = FilenameUtils.getPath("~/");
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__20);
        String o_testGetPath_literalMutationString1154__21 = FilenameUtils.getPath("~user");
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__21);
        String o_testGetPath_literalMutationString1154__22 = FilenameUtils.getPath("~user/");
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__22);
        String o_testGetPath_literalMutationString1154__23 = FilenameUtils.getPath("a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1154__23);
        String o_testGetPath_literalMutationString1154__24 = FilenameUtils.getPath("a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1154__24);
        String o_testGetPath_literalMutationString1154__25 = FilenameUtils.getPath("/a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1154__25);
        String o_testGetPath_literalMutationString1154__26 = FilenameUtils.getPath("C:a");
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__26);
        String o_testGetPath_literalMutationString1154__27 = FilenameUtils.getPath("C:a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1154__27);
        String o_testGetPath_literalMutationString1154__28 = FilenameUtils.getPath("C:/a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1154__28);
        String o_testGetPath_literalMutationString1154__29 = FilenameUtils.getPath("//%server/a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1154__29);
        String o_testGetPath_literalMutationString1154__30 = FilenameUtils.getPath("~/a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1154__30);
        String o_testGetPath_literalMutationString1154__31 = FilenameUtils.getPath("~user/a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1154__31);
        Assert.assertNull(o_testGetPath_literalMutationString1154__1);
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__2);
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__3);
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__4);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1154__5);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1154__6);
        Assert.assertEquals("a/b/c/", o_testGetPath_literalMutationString1154__7);
        Assert.assertEquals("a\\b\\", o_testGetPath_literalMutationString1154__8);
        Assert.assertNull(o_testGetPath_literalMutationString1154__9);
        Assert.assertNull(o_testGetPath_literalMutationString1154__10);
        Assert.assertNull(o_testGetPath_literalMutationString1154__11);
        Assert.assertNull(o_testGetPath_literalMutationString1154__12);
        Assert.assertNull(o_testGetPath_literalMutationString1154__13);
        Assert.assertNull(o_testGetPath_literalMutationString1154__14);
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__15);
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__16);
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__17);
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__18);
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__19);
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__20);
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__21);
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__22);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1154__23);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1154__24);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1154__25);
        Assert.assertEquals("", o_testGetPath_literalMutationString1154__26);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1154__27);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1154__28);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1154__29);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1154__30);
    }
}

