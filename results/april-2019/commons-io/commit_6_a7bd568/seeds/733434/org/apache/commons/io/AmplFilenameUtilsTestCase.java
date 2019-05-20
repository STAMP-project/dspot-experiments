package org.apache.commons.io;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.io.testtools.FileBasedTestCase;
import org.apache.commons.io.testtools.TestUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class AmplFilenameUtilsTestCase extends FileBasedTestCase {
    private static final String SEP = "" + (File.separatorChar);

    private static final boolean WINDOWS = (File.separatorChar) == '\\';

    private final File testFile1;

    private final File testFile2;

    private final int testFile1Size;

    private final int testFile2Size;

    public AmplFilenameUtilsTestCase() {
        testFile1 = new File(FileBasedTestCase.getTestDirectory(), "file1-test.txt");
        testFile2 = new File(FileBasedTestCase.getTestDirectory(), "file1a-test.txt");
        testFile1Size = ((int) (testFile1.length()));
        testFile2Size = ((int) (testFile2.length()));
    }

    @Before
    public void setUp() throws Exception {
        FileBasedTestCase.getTestDirectory();
        if (!(testFile1.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile1)) + " as the parent directory does not exist"));
        }
        final BufferedOutputStream output3 = new BufferedOutputStream(new FileOutputStream(testFile1));
        try {
            TestUtils.generateTestData(output3, ((long) (testFile1Size)));
        } finally {
            IOUtils.closeQuietly(output3);
        }
        if (!(testFile2.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile2)) + " as the parent directory does not exist"));
        }
        final BufferedOutputStream output2 = new BufferedOutputStream(new FileOutputStream(testFile2));
        try {
            TestUtils.generateTestData(output2, ((long) (testFile2Size)));
        } finally {
            IOUtils.closeQuietly(output2);
        }
        FileUtils.deleteDirectory(FileBasedTestCase.getTestDirectory());
        FileBasedTestCase.getTestDirectory();
        if (!(testFile1.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile1)) + " as the parent directory does not exist"));
        }
        final BufferedOutputStream output1 = new BufferedOutputStream(new FileOutputStream(testFile1));
        try {
            TestUtils.generateTestData(output1, ((long) (testFile1Size)));
        } finally {
            IOUtils.closeQuietly(output1);
        }
        if (!(testFile2.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile2)) + " as the parent directory does not exist"));
        }
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(testFile2));
        try {
            TestUtils.generateTestData(output, ((long) (testFile2Size)));
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(FileBasedTestCase.getTestDirectory());
    }

    @Test(timeout = 10000)
    public void testGetPathNoEndSeparator_literalMutationString3446() throws Exception {
        String o_testGetPathNoEndSeparator_literalMutationString3446__1 = FilenameUtils.getPath(null);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3446__1);
        String o_testGetPathNoEndSeparator_literalMutationString3446__2 = FilenameUtils.getPath("noseperator.inthispath");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__2);
        String o_testGetPathNoEndSeparator_literalMutationString3446__3 = FilenameUtils.getPathNoEndSeparator("/noseperator.inthispath");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__3);
        String o_testGetPathNoEndSeparator_literalMutationString3446__4 = FilenameUtils.getPathNoEndSeparator("\\noseperator.inthispath");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__4);
        FilenameUtils.getPathNoEndSeparator("a/b/c.txt");
        FilenameUtils.getPathNoEndSeparator("a/b/c");
        FilenameUtils.getPathNoEndSeparator("a/b/c/");
        String o_testGetPathNoEndSeparator_literalMutationString3446__8 = FilenameUtils.getPathNoEndSeparator("a\\b\\c");
        Assert.assertEquals("a\\b", o_testGetPathNoEndSeparator_literalMutationString3446__8);
        String o_testGetPathNoEndSeparator_literalMutationString3446__9 = FilenameUtils.getPathNoEndSeparator(":");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3446__9);
        String o_testGetPathNoEndSeparator_literalMutationString3446__10 = FilenameUtils.getPathNoEndSeparator("1:/a/b/c.txt");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3446__10);
        String o_testGetPathNoEndSeparator_literalMutationString3446__11 = FilenameUtils.getPathNoEndSeparator("1:");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3446__11);
        String o_testGetPathNoEndSeparator_literalMutationString3446__12 = FilenameUtils.getPathNoEndSeparator("/:a");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3446__12);
        String o_testGetPathNoEndSeparator_literalMutationString3446__13 = FilenameUtils.getPathNoEndSeparator("///a/b/c.txt");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3446__13);
        String o_testGetPathNoEndSeparator_literalMutationString3446__14 = FilenameUtils.getPathNoEndSeparator("//a");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3446__14);
        String o_testGetPathNoEndSeparator_literalMutationString3446__15 = FilenameUtils.getPathNoEndSeparator("");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__15);
        String o_testGetPathNoEndSeparator_literalMutationString3446__16 = FilenameUtils.getPathNoEndSeparator("C:");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__16);
        String o_testGetPathNoEndSeparator_literalMutationString3446__17 = FilenameUtils.getPathNoEndSeparator("C:/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__17);
        String o_testGetPathNoEndSeparator_literalMutationString3446__18 = FilenameUtils.getPathNoEndSeparator("//server/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__18);
        String o_testGetPathNoEndSeparator_literalMutationString3446__19 = FilenameUtils.getPathNoEndSeparator("~");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__19);
        String o_testGetPathNoEndSeparator_literalMutationString3446__20 = FilenameUtils.getPathNoEndSeparator("~/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__20);
        String o_testGetPathNoEndSeparator_literalMutationString3446__21 = FilenameUtils.getPathNoEndSeparator("~user");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__21);
        String o_testGetPathNoEndSeparator_literalMutationString3446__22 = FilenameUtils.getPathNoEndSeparator("~user/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__22);
        FilenameUtils.getPathNoEndSeparator("a/b/c.txt");
        FilenameUtils.getPathNoEndSeparator("a/b/c.txt");
        FilenameUtils.getPathNoEndSeparator("/a/b/c.txt");
        String o_testGetPathNoEndSeparator_literalMutationString3446__26 = FilenameUtils.getPathNoEndSeparator("C:a");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__26);
        FilenameUtils.getPathNoEndSeparator("C:a/b/c.txt");
        FilenameUtils.getPathNoEndSeparator("C:/a/b/c.txt");
        FilenameUtils.getPathNoEndSeparator("//server/a/b/c.txt");
        FilenameUtils.getPathNoEndSeparator("~/a/b/c.txt");
        FilenameUtils.getPathNoEndSeparator("~user/a/b/c.txt");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3446__1);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__2);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__3);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__4);
        Assert.assertEquals("a\\b", o_testGetPathNoEndSeparator_literalMutationString3446__8);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3446__9);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3446__10);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3446__11);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3446__12);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3446__13);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3446__14);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__15);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__16);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__17);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__18);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__19);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__20);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__21);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__22);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3446__26);
    }
}

