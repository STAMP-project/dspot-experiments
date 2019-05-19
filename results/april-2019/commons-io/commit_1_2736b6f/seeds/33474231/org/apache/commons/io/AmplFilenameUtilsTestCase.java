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
    public void testGetPathNoEndSeparator_literalMutationString3471() throws Exception {
        String o_testGetPathNoEndSeparator_literalMutationString3471__1 = FilenameUtils.getPath(null);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3471__1);
        String o_testGetPathNoEndSeparator_literalMutationString3471__2 = FilenameUtils.getPath("noseperator.inthispath");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__2);
        String o_testGetPathNoEndSeparator_literalMutationString3471__3 = FilenameUtils.getPathNoEndSeparator("/noseperator.inthispath");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__3);
        String o_testGetPathNoEndSeparator_literalMutationString3471__4 = FilenameUtils.getPathNoEndSeparator("\\noseperator.inthispath");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__4);
        FilenameUtils.getPathNoEndSeparator("a/b/c.txt");
        FilenameUtils.getPathNoEndSeparator("a/b/c");
        FilenameUtils.getPathNoEndSeparator("a/b/c/");
        String o_testGetPathNoEndSeparator_literalMutationString3471__8 = FilenameUtils.getPathNoEndSeparator("a\\b\\c");
        Assert.assertEquals("a\\b", o_testGetPathNoEndSeparator_literalMutationString3471__8);
        String o_testGetPathNoEndSeparator_literalMutationString3471__9 = FilenameUtils.getPathNoEndSeparator(":");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3471__9);
        String o_testGetPathNoEndSeparator_literalMutationString3471__10 = FilenameUtils.getPathNoEndSeparator("1:/a/b/c.txt");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3471__10);
        String o_testGetPathNoEndSeparator_literalMutationString3471__11 = FilenameUtils.getPathNoEndSeparator("1:");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3471__11);
        String o_testGetPathNoEndSeparator_literalMutationString3471__12 = FilenameUtils.getPathNoEndSeparator("1:a");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3471__12);
        String o_testGetPathNoEndSeparator_literalMutationString3471__13 = FilenameUtils.getPathNoEndSeparator("///a/b/c.txt");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3471__13);
        String o_testGetPathNoEndSeparator_literalMutationString3471__14 = FilenameUtils.getPathNoEndSeparator("//a");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3471__14);
        String o_testGetPathNoEndSeparator_literalMutationString3471__15 = FilenameUtils.getPathNoEndSeparator("");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__15);
        String o_testGetPathNoEndSeparator_literalMutationString3471__16 = FilenameUtils.getPathNoEndSeparator("C:");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__16);
        String o_testGetPathNoEndSeparator_literalMutationString3471__17 = FilenameUtils.getPathNoEndSeparator("C:/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__17);
        String o_testGetPathNoEndSeparator_literalMutationString3471__18 = FilenameUtils.getPathNoEndSeparator("//(erver/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__18);
        String o_testGetPathNoEndSeparator_literalMutationString3471__19 = FilenameUtils.getPathNoEndSeparator("~");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__19);
        String o_testGetPathNoEndSeparator_literalMutationString3471__20 = FilenameUtils.getPathNoEndSeparator("~/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__20);
        String o_testGetPathNoEndSeparator_literalMutationString3471__21 = FilenameUtils.getPathNoEndSeparator("~user");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__21);
        String o_testGetPathNoEndSeparator_literalMutationString3471__22 = FilenameUtils.getPathNoEndSeparator("~user/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__22);
        FilenameUtils.getPathNoEndSeparator("a/b/c.txt");
        FilenameUtils.getPathNoEndSeparator("a/b/c.txt");
        FilenameUtils.getPathNoEndSeparator("/a/b/c.txt");
        String o_testGetPathNoEndSeparator_literalMutationString3471__26 = FilenameUtils.getPathNoEndSeparator("C:a");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__26);
        FilenameUtils.getPathNoEndSeparator("C:a/b/c.txt");
        FilenameUtils.getPathNoEndSeparator("C:/a/b/c.txt");
        FilenameUtils.getPathNoEndSeparator("//server/a/b/c.txt");
        FilenameUtils.getPathNoEndSeparator("~/a/b/c.txt");
        FilenameUtils.getPathNoEndSeparator("~user/a/b/c.txt");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3471__1);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__2);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__3);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__4);
        Assert.assertEquals("a\\b", o_testGetPathNoEndSeparator_literalMutationString3471__8);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3471__9);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3471__10);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3471__11);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3471__12);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3471__13);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3471__14);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__15);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__16);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__17);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__18);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__19);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__20);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__21);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__22);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3471__26);
    }
}

