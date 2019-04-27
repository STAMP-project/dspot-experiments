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
        try (final BufferedOutputStream output3 = new BufferedOutputStream(new FileOutputStream(testFile1))) {
            TestUtils.generateTestData(output3, ((long) (testFile1Size)));
        }
        if (!(testFile2.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile2)) + " as the parent directory does not exist"));
        }
        try (final BufferedOutputStream output2 = new BufferedOutputStream(new FileOutputStream(testFile2))) {
            TestUtils.generateTestData(output2, ((long) (testFile2Size)));
        }
        FileUtils.deleteDirectory(FileBasedTestCase.getTestDirectory());
        FileBasedTestCase.getTestDirectory();
        if (!(testFile1.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile1)) + " as the parent directory does not exist"));
        }
        try (final BufferedOutputStream output1 = new BufferedOutputStream(new FileOutputStream(testFile1))) {
            TestUtils.generateTestData(output1, ((long) (testFile1Size)));
        }
        if (!(testFile2.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + (testFile2)) + " as the parent directory does not exist"));
        }
        try (final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(testFile2))) {
            TestUtils.generateTestData(output, ((long) (testFile2Size)));
        }
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(FileBasedTestCase.getTestDirectory());
    }

    @Test(timeout = 10000)
    public void testGetPrefixLength_literalMutationString165681() throws Exception {
        int o_testGetPrefixLength_literalMutationString165681__1 = FilenameUtils.getPrefixLength(null);
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString165681__1)));
        int o_testGetPrefixLength_literalMutationString165681__2 = FilenameUtils.getPrefixLength(":");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString165681__2)));
        int o_testGetPrefixLength_literalMutationString165681__3 = FilenameUtils.getPrefixLength("1:\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString165681__3)));
        int o_testGetPrefixLength_literalMutationString165681__4 = FilenameUtils.getPrefixLength("1:");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString165681__4)));
        int o_testGetPrefixLength_literalMutationString165681__5 = FilenameUtils.getPrefixLength("1:a");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString165681__5)));
        int o_testGetPrefixLength_literalMutationString165681__6 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString165681__6)));
        int o_testGetPrefixLength_literalMutationString165681__7 = FilenameUtils.getPrefixLength("\\\\a");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString165681__7)));
        int o_testGetPrefixLength_literalMutationString165681__8 = FilenameUtils.getPrefixLength("");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString165681__8)));
        int o_testGetPrefixLength_literalMutationString165681__9 = FilenameUtils.getPrefixLength("\\");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString165681__9)));
        int o_testGetPrefixLength_literalMutationString165681__10 = FilenameUtils.getPrefixLength("C:");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString165681__10)));
        int o_testGetPrefixLength_literalMutationString165681__11 = FilenameUtils.getPrefixLength("/:\\");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString165681__11)));
        int o_testGetPrefixLength_literalMutationString165681__12 = FilenameUtils.getPrefixLength("//server/");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString165681__12)));
        int o_testGetPrefixLength_literalMutationString165681__13 = FilenameUtils.getPrefixLength("~");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString165681__13)));
        int o_testGetPrefixLength_literalMutationString165681__14 = FilenameUtils.getPrefixLength("~/");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString165681__14)));
        int o_testGetPrefixLength_literalMutationString165681__15 = FilenameUtils.getPrefixLength("~user");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString165681__15)));
        int o_testGetPrefixLength_literalMutationString165681__16 = FilenameUtils.getPrefixLength("~user/");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString165681__16)));
        int o_testGetPrefixLength_literalMutationString165681__17 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString165681__17)));
        int o_testGetPrefixLength_literalMutationString165681__18 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString165681__18)));
        int o_testGetPrefixLength_literalMutationString165681__19 = FilenameUtils.getPrefixLength("C:a\\b\\c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString165681__19)));
        int o_testGetPrefixLength_literalMutationString165681__20 = FilenameUtils.getPrefixLength("C:\\a\\b\\c.txt");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString165681__20)));
        int o_testGetPrefixLength_literalMutationString165681__21 = FilenameUtils.getPrefixLength("\\\\server\\a\\b\\c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString165681__21)));
        int o_testGetPrefixLength_literalMutationString165681__22 = FilenameUtils.getPrefixLength("a/b/c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString165681__22)));
        int o_testGetPrefixLength_literalMutationString165681__23 = FilenameUtils.getPrefixLength("/a/b/c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString165681__23)));
        int o_testGetPrefixLength_literalMutationString165681__24 = FilenameUtils.getPrefixLength("C:/a/b/c.txt");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString165681__24)));
        int o_testGetPrefixLength_literalMutationString165681__25 = FilenameUtils.getPrefixLength("//server/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString165681__25)));
        int o_testGetPrefixLength_literalMutationString165681__26 = FilenameUtils.getPrefixLength("~/a/b/c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString165681__26)));
        int o_testGetPrefixLength_literalMutationString165681__27 = FilenameUtils.getPrefixLength("~user/a/b/c.txt");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString165681__27)));
        int o_testGetPrefixLength_literalMutationString165681__28 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString165681__28)));
        int o_testGetPrefixLength_literalMutationString165681__29 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString165681__29)));
        int o_testGetPrefixLength_literalMutationString165681__30 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString165681__30)));
        int o_testGetPrefixLength_literalMutationString165681__31 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString165681__31)));
        int o_testGetPrefixLength_literalMutationString165681__32 = FilenameUtils.getPrefixLength("~\\a\\b\\c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString165681__32)));
        int o_testGetPrefixLength_literalMutationString165681__33 = FilenameUtils.getPrefixLength("~user\\a\\b\\c.txt");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString165681__33)));
        int o_testGetPrefixLength_literalMutationString165681__34 = FilenameUtils.getPrefixLength("//server/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString165681__34)));
        int o_testGetPrefixLength_literalMutationString165681__35 = FilenameUtils.getPrefixLength("//server/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString165681__35)));
        int o_testGetPrefixLength_literalMutationString165681__36 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString165681__36)));
        int o_testGetPrefixLength_literalMutationString165681__37 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString165681__37)));
        int o_testGetPrefixLength_literalMutationString165681__38 = FilenameUtils.getPrefixLength("///a/b/c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString165681__38)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString165681__1)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString165681__2)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString165681__3)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString165681__4)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString165681__5)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString165681__6)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString165681__7)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString165681__8)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString165681__9)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString165681__10)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString165681__11)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString165681__12)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString165681__13)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString165681__14)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString165681__15)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString165681__16)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString165681__17)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString165681__18)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString165681__19)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString165681__20)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString165681__21)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString165681__22)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString165681__23)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString165681__24)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString165681__25)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString165681__26)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString165681__27)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString165681__28)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString165681__29)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString165681__30)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString165681__31)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString165681__32)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString165681__33)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString165681__34)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString165681__35)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString165681__36)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString165681__37)));
    }
}

