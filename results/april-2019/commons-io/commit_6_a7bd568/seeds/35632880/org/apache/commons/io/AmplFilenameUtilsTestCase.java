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
    public void testGetPath_literalMutationString1135() throws Exception {
        String o_testGetPath_literalMutationString1135__1 = FilenameUtils.getPath(null);
        Assert.assertNull(o_testGetPath_literalMutationString1135__1);
        String o_testGetPath_literalMutationString1135__2 = FilenameUtils.getPath("noseperator.inthispath");
        Assert.assertEquals("", o_testGetPath_literalMutationString1135__2);
        String o_testGetPath_literalMutationString1135__3 = FilenameUtils.getPath("/noseperator.inthispath");
        Assert.assertEquals("", o_testGetPath_literalMutationString1135__3);
        String o_testGetPath_literalMutationString1135__4 = FilenameUtils.getPath("\\noseperator.inthispath");
        Assert.assertEquals("", o_testGetPath_literalMutationString1135__4);
        FilenameUtils.getPath("a/b/c.txt");
        FilenameUtils.getPath("a/b/c");
        FilenameUtils.getPath("a/b/c/");
        String o_testGetPath_literalMutationString1135__8 = FilenameUtils.getPath("a\\b\\c");
        Assert.assertEquals("a\\b\\", o_testGetPath_literalMutationString1135__8);
        String o_testGetPath_literalMutationString1135__9 = FilenameUtils.getPath(":");
        Assert.assertNull(o_testGetPath_literalMutationString1135__9);
        String o_testGetPath_literalMutationString1135__10 = FilenameUtils.getPath("1:/a/b/c.txt");
        Assert.assertNull(o_testGetPath_literalMutationString1135__10);
        String o_testGetPath_literalMutationString1135__11 = FilenameUtils.getPath("1:");
        Assert.assertNull(o_testGetPath_literalMutationString1135__11);
        String o_testGetPath_literalMutationString1135__12 = FilenameUtils.getPath("1:a");
        Assert.assertNull(o_testGetPath_literalMutationString1135__12);
        String o_testGetPath_literalMutationString1135__13 = FilenameUtils.getPath("///a/b/c.txt");
        Assert.assertNull(o_testGetPath_literalMutationString1135__13);
        String o_testGetPath_literalMutationString1135__14 = FilenameUtils.getPath("//a");
        Assert.assertNull(o_testGetPath_literalMutationString1135__14);
        String o_testGetPath_literalMutationString1135__15 = FilenameUtils.getPath("");
        Assert.assertEquals("", o_testGetPath_literalMutationString1135__15);
        String o_testGetPath_literalMutationString1135__16 = FilenameUtils.getPath("C:");
        Assert.assertEquals("", o_testGetPath_literalMutationString1135__16);
        String o_testGetPath_literalMutationString1135__17 = FilenameUtils.getPath("C:/");
        Assert.assertEquals("", o_testGetPath_literalMutationString1135__17);
        String o_testGetPath_literalMutationString1135__18 = FilenameUtils.getPath("//server/");
        Assert.assertEquals("", o_testGetPath_literalMutationString1135__18);
        String o_testGetPath_literalMutationString1135__19 = FilenameUtils.getPath("~");
        Assert.assertEquals("", o_testGetPath_literalMutationString1135__19);
        String o_testGetPath_literalMutationString1135__20 = FilenameUtils.getPath("~/");
        Assert.assertEquals("", o_testGetPath_literalMutationString1135__20);
        String o_testGetPath_literalMutationString1135__21 = FilenameUtils.getPath("~user");
        Assert.assertEquals("", o_testGetPath_literalMutationString1135__21);
        String o_testGetPath_literalMutationString1135__22 = FilenameUtils.getPath("~user/");
        Assert.assertEquals("", o_testGetPath_literalMutationString1135__22);
        FilenameUtils.getPath("a/b/c.txt");
        FilenameUtils.getPath("a/b/c.txt");
        FilenameUtils.getPath("/a/b/c.txt");
        String o_testGetPath_literalMutationString1135__26 = FilenameUtils.getPath("/:a");
        Assert.assertNull(o_testGetPath_literalMutationString1135__26);
        FilenameUtils.getPath("C:a/b/c.txt");
        FilenameUtils.getPath("C:/a/b/c.txt");
        FilenameUtils.getPath("//server/a/b/c.txt");
        FilenameUtils.getPath("~/a/b/c.txt");
        FilenameUtils.getPath("~user/a/b/c.txt");
        Assert.assertNull(o_testGetPath_literalMutationString1135__1);
        Assert.assertEquals("", o_testGetPath_literalMutationString1135__2);
        Assert.assertEquals("", o_testGetPath_literalMutationString1135__3);
        Assert.assertEquals("", o_testGetPath_literalMutationString1135__4);
        Assert.assertEquals("a\\b\\", o_testGetPath_literalMutationString1135__8);
        Assert.assertNull(o_testGetPath_literalMutationString1135__9);
        Assert.assertNull(o_testGetPath_literalMutationString1135__10);
        Assert.assertNull(o_testGetPath_literalMutationString1135__11);
        Assert.assertNull(o_testGetPath_literalMutationString1135__12);
        Assert.assertNull(o_testGetPath_literalMutationString1135__13);
        Assert.assertNull(o_testGetPath_literalMutationString1135__14);
        Assert.assertEquals("", o_testGetPath_literalMutationString1135__15);
        Assert.assertEquals("", o_testGetPath_literalMutationString1135__16);
        Assert.assertEquals("", o_testGetPath_literalMutationString1135__17);
        Assert.assertEquals("", o_testGetPath_literalMutationString1135__18);
        Assert.assertEquals("", o_testGetPath_literalMutationString1135__19);
        Assert.assertEquals("", o_testGetPath_literalMutationString1135__20);
        Assert.assertEquals("", o_testGetPath_literalMutationString1135__21);
        Assert.assertEquals("", o_testGetPath_literalMutationString1135__22);
        Assert.assertNull(o_testGetPath_literalMutationString1135__26);
    }

    @Test(timeout = 10000)
    public void testGetPathNoEndSeparator_literalMutationString3475() throws Exception {
        String o_testGetPathNoEndSeparator_literalMutationString3475__1 = FilenameUtils.getPath(null);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3475__1);
        String o_testGetPathNoEndSeparator_literalMutationString3475__2 = FilenameUtils.getPath("noseperator.inthispath");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3475__2);
        String o_testGetPathNoEndSeparator_literalMutationString3475__3 = FilenameUtils.getPathNoEndSeparator("/noseperator.inthispath");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3475__3);
        String o_testGetPathNoEndSeparator_literalMutationString3475__4 = FilenameUtils.getPathNoEndSeparator("\\noseperator.inthispath");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3475__4);
        FilenameUtils.getPathNoEndSeparator("a/b/c.txt");
        FilenameUtils.getPathNoEndSeparator("a/b/c");
        FilenameUtils.getPathNoEndSeparator("a/b/c/");
        String o_testGetPathNoEndSeparator_literalMutationString3475__8 = FilenameUtils.getPathNoEndSeparator("a\\b\\c");
        Assert.assertEquals("a\\b", o_testGetPathNoEndSeparator_literalMutationString3475__8);
        String o_testGetPathNoEndSeparator_literalMutationString3475__9 = FilenameUtils.getPathNoEndSeparator(":");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3475__9);
        String o_testGetPathNoEndSeparator_literalMutationString3475__10 = FilenameUtils.getPathNoEndSeparator("1:/a/b/c.txt");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3475__10);
        String o_testGetPathNoEndSeparator_literalMutationString3475__11 = FilenameUtils.getPathNoEndSeparator("1:");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3475__11);
        String o_testGetPathNoEndSeparator_literalMutationString3475__12 = FilenameUtils.getPathNoEndSeparator("1:a");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3475__12);
        String o_testGetPathNoEndSeparator_literalMutationString3475__13 = FilenameUtils.getPathNoEndSeparator("///a/b/c.txt");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3475__13);
        String o_testGetPathNoEndSeparator_literalMutationString3475__14 = FilenameUtils.getPathNoEndSeparator("//a");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3475__14);
        String o_testGetPathNoEndSeparator_literalMutationString3475__15 = FilenameUtils.getPathNoEndSeparator("");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3475__15);
        String o_testGetPathNoEndSeparator_literalMutationString3475__16 = FilenameUtils.getPathNoEndSeparator("C:");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3475__16);
        String o_testGetPathNoEndSeparator_literalMutationString3475__17 = FilenameUtils.getPathNoEndSeparator("C:/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3475__17);
        String o_testGetPathNoEndSeparator_literalMutationString3475__18 = FilenameUtils.getPathNoEndSeparator("/:server/");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3475__18);
        String o_testGetPathNoEndSeparator_literalMutationString3475__19 = FilenameUtils.getPathNoEndSeparator("~");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3475__19);
        String o_testGetPathNoEndSeparator_literalMutationString3475__20 = FilenameUtils.getPathNoEndSeparator("~/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3475__20);
        String o_testGetPathNoEndSeparator_literalMutationString3475__21 = FilenameUtils.getPathNoEndSeparator("~user");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3475__21);
        String o_testGetPathNoEndSeparator_literalMutationString3475__22 = FilenameUtils.getPathNoEndSeparator("~user/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3475__22);
        FilenameUtils.getPathNoEndSeparator("a/b/c.txt");
        FilenameUtils.getPathNoEndSeparator("a/b/c.txt");
        FilenameUtils.getPathNoEndSeparator("/a/b/c.txt");
        String o_testGetPathNoEndSeparator_literalMutationString3475__26 = FilenameUtils.getPathNoEndSeparator("C:a");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3475__26);
        FilenameUtils.getPathNoEndSeparator("C:a/b/c.txt");
        FilenameUtils.getPathNoEndSeparator("C:/a/b/c.txt");
        FilenameUtils.getPathNoEndSeparator("//server/a/b/c.txt");
        FilenameUtils.getPathNoEndSeparator("~/a/b/c.txt");
        FilenameUtils.getPathNoEndSeparator("~user/a/b/c.txt");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3475__1);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3475__2);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3475__3);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3475__4);
        Assert.assertEquals("a\\b", o_testGetPathNoEndSeparator_literalMutationString3475__8);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3475__9);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3475__10);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3475__11);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3475__12);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3475__13);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3475__14);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3475__15);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3475__16);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3475__17);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString3475__18);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3475__19);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3475__20);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3475__21);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3475__22);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString3475__26);
    }
}

