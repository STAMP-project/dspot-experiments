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
    public void testGetPrefixLength_literalMutationString78537() throws Exception {
        int o_testGetPrefixLength_literalMutationString78537__1 = FilenameUtils.getPrefixLength(null);
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78537__1)));
        int o_testGetPrefixLength_literalMutationString78537__2 = FilenameUtils.getPrefixLength(":");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78537__2)));
        int o_testGetPrefixLength_literalMutationString78537__3 = FilenameUtils.getPrefixLength("1:\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78537__3)));
        int o_testGetPrefixLength_literalMutationString78537__4 = FilenameUtils.getPrefixLength("1:");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78537__4)));
        int o_testGetPrefixLength_literalMutationString78537__5 = FilenameUtils.getPrefixLength("1:a");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78537__5)));
        int o_testGetPrefixLength_literalMutationString78537__6 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78537__6)));
        int o_testGetPrefixLength_literalMutationString78537__7 = FilenameUtils.getPrefixLength("\\\\a");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78537__7)));
        int o_testGetPrefixLength_literalMutationString78537__8 = FilenameUtils.getPrefixLength("");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString78537__8)));
        int o_testGetPrefixLength_literalMutationString78537__9 = FilenameUtils.getPrefixLength("\\");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78537__9)));
        int o_testGetPrefixLength_literalMutationString78537__10 = FilenameUtils.getPrefixLength("C:");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78537__10)));
        int o_testGetPrefixLength_literalMutationString78537__11 = FilenameUtils.getPrefixLength("C:\\");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString78537__11)));
        int o_testGetPrefixLength_literalMutationString78537__12 = FilenameUtils.getPrefixLength("//serv{er/");
        Assert.assertEquals(10, ((int) (o_testGetPrefixLength_literalMutationString78537__12)));
        int o_testGetPrefixLength_literalMutationString78537__13 = FilenameUtils.getPrefixLength("~");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78537__13)));
        int o_testGetPrefixLength_literalMutationString78537__14 = FilenameUtils.getPrefixLength("~/");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78537__14)));
        int o_testGetPrefixLength_literalMutationString78537__15 = FilenameUtils.getPrefixLength("~user");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString78537__15)));
        int o_testGetPrefixLength_literalMutationString78537__16 = FilenameUtils.getPrefixLength("~user/");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString78537__16)));
        int o_testGetPrefixLength_literalMutationString78537__17 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString78537__17)));
        int o_testGetPrefixLength_literalMutationString78537__18 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78537__18)));
        int o_testGetPrefixLength_literalMutationString78537__19 = FilenameUtils.getPrefixLength("C:a\\b\\c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78537__19)));
        int o_testGetPrefixLength_literalMutationString78537__20 = FilenameUtils.getPrefixLength("C:\\a\\b\\c.txt");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString78537__20)));
        int o_testGetPrefixLength_literalMutationString78537__21 = FilenameUtils.getPrefixLength("\\\\server\\a\\b\\c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString78537__21)));
        int o_testGetPrefixLength_literalMutationString78537__22 = FilenameUtils.getPrefixLength("a/b/c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString78537__22)));
        int o_testGetPrefixLength_literalMutationString78537__23 = FilenameUtils.getPrefixLength("/a/b/c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78537__23)));
        int o_testGetPrefixLength_literalMutationString78537__24 = FilenameUtils.getPrefixLength("C:/a/b/c.txt");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString78537__24)));
        int o_testGetPrefixLength_literalMutationString78537__25 = FilenameUtils.getPrefixLength("//server/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString78537__25)));
        int o_testGetPrefixLength_literalMutationString78537__26 = FilenameUtils.getPrefixLength("~/a/b/c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78537__26)));
        int o_testGetPrefixLength_literalMutationString78537__27 = FilenameUtils.getPrefixLength("~user/a/b/c.txt");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString78537__27)));
        int o_testGetPrefixLength_literalMutationString78537__28 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString78537__28)));
        int o_testGetPrefixLength_literalMutationString78537__29 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString78537__29)));
        int o_testGetPrefixLength_literalMutationString78537__30 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78537__30)));
        int o_testGetPrefixLength_literalMutationString78537__31 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78537__31)));
        int o_testGetPrefixLength_literalMutationString78537__32 = FilenameUtils.getPrefixLength("~\\a\\b\\c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78537__32)));
        int o_testGetPrefixLength_literalMutationString78537__33 = FilenameUtils.getPrefixLength("~user\\a\\b\\c.txt");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString78537__33)));
        int o_testGetPrefixLength_literalMutationString78537__34 = FilenameUtils.getPrefixLength("//server/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString78537__34)));
        int o_testGetPrefixLength_literalMutationString78537__35 = FilenameUtils.getPrefixLength("//server/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString78537__35)));
        int o_testGetPrefixLength_literalMutationString78537__36 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78537__36)));
        int o_testGetPrefixLength_literalMutationString78537__37 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78537__37)));
        int o_testGetPrefixLength_literalMutationString78537__38 = FilenameUtils.getPrefixLength("///a/b/c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78537__38)));
        int o_testGetPrefixLength_literalMutationString78537__39 = FilenameUtils.getPrefixLength("/:foo");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78537__39)));
        int o_testGetPrefixLength_literalMutationString78537__40 = FilenameUtils.getPrefixLength("/:/");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78537__40)));
        int o_testGetPrefixLength_literalMutationString78537__41 = FilenameUtils.getPrefixLength("/:::::::.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78537__41)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78537__1)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78537__2)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78537__3)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78537__4)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78537__5)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78537__6)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78537__7)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString78537__8)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78537__9)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78537__10)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString78537__11)));
        Assert.assertEquals(10, ((int) (o_testGetPrefixLength_literalMutationString78537__12)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78537__13)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78537__14)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString78537__15)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString78537__16)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString78537__17)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78537__18)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78537__19)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString78537__20)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString78537__21)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString78537__22)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78537__23)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString78537__24)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString78537__25)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78537__26)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString78537__27)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString78537__28)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString78537__29)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78537__30)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78537__31)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78537__32)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString78537__33)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString78537__34)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString78537__35)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78537__36)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78537__37)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78537__38)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78537__39)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78537__40)));
    }

    @Test(timeout = 10000)
    public void testGetPrefixLength_literalMutationString78538() throws Exception {
        int o_testGetPrefixLength_literalMutationString78538__1 = FilenameUtils.getPrefixLength(null);
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78538__1)));
        int o_testGetPrefixLength_literalMutationString78538__2 = FilenameUtils.getPrefixLength(":");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78538__2)));
        int o_testGetPrefixLength_literalMutationString78538__3 = FilenameUtils.getPrefixLength("1:\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78538__3)));
        int o_testGetPrefixLength_literalMutationString78538__4 = FilenameUtils.getPrefixLength("1:");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78538__4)));
        int o_testGetPrefixLength_literalMutationString78538__5 = FilenameUtils.getPrefixLength("1:a");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78538__5)));
        int o_testGetPrefixLength_literalMutationString78538__6 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78538__6)));
        int o_testGetPrefixLength_literalMutationString78538__7 = FilenameUtils.getPrefixLength("\\\\a");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78538__7)));
        int o_testGetPrefixLength_literalMutationString78538__8 = FilenameUtils.getPrefixLength("");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString78538__8)));
        int o_testGetPrefixLength_literalMutationString78538__9 = FilenameUtils.getPrefixLength("\\");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78538__9)));
        int o_testGetPrefixLength_literalMutationString78538__10 = FilenameUtils.getPrefixLength("C:");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78538__10)));
        int o_testGetPrefixLength_literalMutationString78538__11 = FilenameUtils.getPrefixLength("C:\\");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString78538__11)));
        int o_testGetPrefixLength_literalMutationString78538__12 = FilenameUtils.getPrefixLength("//serv:r/");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString78538__12)));
        int o_testGetPrefixLength_literalMutationString78538__13 = FilenameUtils.getPrefixLength("~");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78538__13)));
        int o_testGetPrefixLength_literalMutationString78538__14 = FilenameUtils.getPrefixLength("~/");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78538__14)));
        int o_testGetPrefixLength_literalMutationString78538__15 = FilenameUtils.getPrefixLength("~user");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString78538__15)));
        int o_testGetPrefixLength_literalMutationString78538__16 = FilenameUtils.getPrefixLength("~user/");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString78538__16)));
        int o_testGetPrefixLength_literalMutationString78538__17 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString78538__17)));
        int o_testGetPrefixLength_literalMutationString78538__18 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78538__18)));
        int o_testGetPrefixLength_literalMutationString78538__19 = FilenameUtils.getPrefixLength("C:a\\b\\c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78538__19)));
        int o_testGetPrefixLength_literalMutationString78538__20 = FilenameUtils.getPrefixLength("C:\\a\\b\\c.txt");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString78538__20)));
        int o_testGetPrefixLength_literalMutationString78538__21 = FilenameUtils.getPrefixLength("\\\\server\\a\\b\\c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString78538__21)));
        int o_testGetPrefixLength_literalMutationString78538__22 = FilenameUtils.getPrefixLength("a/b/c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString78538__22)));
        int o_testGetPrefixLength_literalMutationString78538__23 = FilenameUtils.getPrefixLength("/a/b/c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78538__23)));
        int o_testGetPrefixLength_literalMutationString78538__24 = FilenameUtils.getPrefixLength("C:/a/b/c.txt");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString78538__24)));
        int o_testGetPrefixLength_literalMutationString78538__25 = FilenameUtils.getPrefixLength("//server/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString78538__25)));
        int o_testGetPrefixLength_literalMutationString78538__26 = FilenameUtils.getPrefixLength("~/a/b/c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78538__26)));
        int o_testGetPrefixLength_literalMutationString78538__27 = FilenameUtils.getPrefixLength("~user/a/b/c.txt");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString78538__27)));
        int o_testGetPrefixLength_literalMutationString78538__28 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString78538__28)));
        int o_testGetPrefixLength_literalMutationString78538__29 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString78538__29)));
        int o_testGetPrefixLength_literalMutationString78538__30 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78538__30)));
        int o_testGetPrefixLength_literalMutationString78538__31 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78538__31)));
        int o_testGetPrefixLength_literalMutationString78538__32 = FilenameUtils.getPrefixLength("~\\a\\b\\c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78538__32)));
        int o_testGetPrefixLength_literalMutationString78538__33 = FilenameUtils.getPrefixLength("~user\\a\\b\\c.txt");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString78538__33)));
        int o_testGetPrefixLength_literalMutationString78538__34 = FilenameUtils.getPrefixLength("//server/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString78538__34)));
        int o_testGetPrefixLength_literalMutationString78538__35 = FilenameUtils.getPrefixLength("//server/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString78538__35)));
        int o_testGetPrefixLength_literalMutationString78538__36 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78538__36)));
        int o_testGetPrefixLength_literalMutationString78538__37 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78538__37)));
        int o_testGetPrefixLength_literalMutationString78538__38 = FilenameUtils.getPrefixLength("///a/b/c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78538__38)));
        int o_testGetPrefixLength_literalMutationString78538__39 = FilenameUtils.getPrefixLength("/:foo");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78538__39)));
        int o_testGetPrefixLength_literalMutationString78538__40 = FilenameUtils.getPrefixLength("/:/");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78538__40)));
        int o_testGetPrefixLength_literalMutationString78538__41 = FilenameUtils.getPrefixLength("/:::::::.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78538__41)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78538__1)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78538__2)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78538__3)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78538__4)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78538__5)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78538__6)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78538__7)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString78538__8)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78538__9)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78538__10)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString78538__11)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString78538__12)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78538__13)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78538__14)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString78538__15)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString78538__16)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString78538__17)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78538__18)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78538__19)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString78538__20)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString78538__21)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString78538__22)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78538__23)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString78538__24)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString78538__25)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78538__26)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString78538__27)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString78538__28)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString78538__29)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78538__30)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78538__31)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString78538__32)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString78538__33)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString78538__34)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString78538__35)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78538__36)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78538__37)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString78538__38)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78538__39)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString78538__40)));
    }

    @Test(timeout = 10000)
    public void testGetPath_literalMutationString31519() throws Exception {
        String o_testGetPath_literalMutationString31519__1 = FilenameUtils.getPath(null);
        Assert.assertNull(o_testGetPath_literalMutationString31519__1);
        String o_testGetPath_literalMutationString31519__2 = FilenameUtils.getPath("noseperator.inthispath");
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__2);
        String o_testGetPath_literalMutationString31519__3 = FilenameUtils.getPath("/noseperator.inthispath");
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__3);
        String o_testGetPath_literalMutationString31519__4 = FilenameUtils.getPath("\\noseperator.inthispath");
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__4);
        String o_testGetPath_literalMutationString31519__5 = FilenameUtils.getPath("a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString31519__5);
        String o_testGetPath_literalMutationString31519__6 = FilenameUtils.getPath("a/b/c");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString31519__6);
        String o_testGetPath_literalMutationString31519__7 = FilenameUtils.getPath("a/b/c/");
        Assert.assertEquals("a/b/c/", o_testGetPath_literalMutationString31519__7);
        String o_testGetPath_literalMutationString31519__8 = FilenameUtils.getPath("a\\b\\c");
        Assert.assertEquals("a\\b\\", o_testGetPath_literalMutationString31519__8);
        String o_testGetPath_literalMutationString31519__9 = FilenameUtils.getPath(":");
        Assert.assertNull(o_testGetPath_literalMutationString31519__9);
        String o_testGetPath_literalMutationString31519__10 = FilenameUtils.getPath("1:/a/b/c.txt");
        Assert.assertNull(o_testGetPath_literalMutationString31519__10);
        String o_testGetPath_literalMutationString31519__11 = FilenameUtils.getPath("1:");
        Assert.assertNull(o_testGetPath_literalMutationString31519__11);
        String o_testGetPath_literalMutationString31519__12 = FilenameUtils.getPath("1:a");
        Assert.assertNull(o_testGetPath_literalMutationString31519__12);
        String o_testGetPath_literalMutationString31519__13 = FilenameUtils.getPath("///a/b/c.txt");
        Assert.assertNull(o_testGetPath_literalMutationString31519__13);
        String o_testGetPath_literalMutationString31519__14 = FilenameUtils.getPath("//a");
        Assert.assertNull(o_testGetPath_literalMutationString31519__14);
        String o_testGetPath_literalMutationString31519__15 = FilenameUtils.getPath("");
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__15);
        String o_testGetPath_literalMutationString31519__16 = FilenameUtils.getPath("C:");
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__16);
        String o_testGetPath_literalMutationString31519__17 = FilenameUtils.getPath("C:/");
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__17);
        String o_testGetPath_literalMutationString31519__18 = FilenameUtils.getPath("//serve`r/");
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__18);
        String o_testGetPath_literalMutationString31519__19 = FilenameUtils.getPath("~");
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__19);
        String o_testGetPath_literalMutationString31519__20 = FilenameUtils.getPath("~/");
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__20);
        String o_testGetPath_literalMutationString31519__21 = FilenameUtils.getPath("~user");
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__21);
        String o_testGetPath_literalMutationString31519__22 = FilenameUtils.getPath("~user/");
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__22);
        String o_testGetPath_literalMutationString31519__23 = FilenameUtils.getPath("a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString31519__23);
        String o_testGetPath_literalMutationString31519__24 = FilenameUtils.getPath("a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString31519__24);
        String o_testGetPath_literalMutationString31519__25 = FilenameUtils.getPath("/a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString31519__25);
        String o_testGetPath_literalMutationString31519__26 = FilenameUtils.getPath("C:a");
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__26);
        String o_testGetPath_literalMutationString31519__27 = FilenameUtils.getPath("C:a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString31519__27);
        String o_testGetPath_literalMutationString31519__28 = FilenameUtils.getPath("C:/a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString31519__28);
        String o_testGetPath_literalMutationString31519__29 = FilenameUtils.getPath("//server/a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString31519__29);
        String o_testGetPath_literalMutationString31519__30 = FilenameUtils.getPath("~/a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString31519__30);
        String o_testGetPath_literalMutationString31519__31 = FilenameUtils.getPath("~user/a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString31519__31);
        Assert.assertNull(o_testGetPath_literalMutationString31519__1);
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__2);
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__3);
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__4);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString31519__5);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString31519__6);
        Assert.assertEquals("a/b/c/", o_testGetPath_literalMutationString31519__7);
        Assert.assertEquals("a\\b\\", o_testGetPath_literalMutationString31519__8);
        Assert.assertNull(o_testGetPath_literalMutationString31519__9);
        Assert.assertNull(o_testGetPath_literalMutationString31519__10);
        Assert.assertNull(o_testGetPath_literalMutationString31519__11);
        Assert.assertNull(o_testGetPath_literalMutationString31519__12);
        Assert.assertNull(o_testGetPath_literalMutationString31519__13);
        Assert.assertNull(o_testGetPath_literalMutationString31519__14);
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__15);
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__16);
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__17);
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__18);
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__19);
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__20);
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__21);
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__22);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString31519__23);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString31519__24);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString31519__25);
        Assert.assertEquals("", o_testGetPath_literalMutationString31519__26);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString31519__27);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString31519__28);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString31519__29);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString31519__30);
    }

    @Test(timeout = 10000)
    public void testGetPathNoEndSeparator_literalMutationString65327() throws Exception {
        String o_testGetPathNoEndSeparator_literalMutationString65327__1 = FilenameUtils.getPath(null);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString65327__1);
        String o_testGetPathNoEndSeparator_literalMutationString65327__2 = FilenameUtils.getPath("noseperator.inthispath");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__2);
        String o_testGetPathNoEndSeparator_literalMutationString65327__3 = FilenameUtils.getPathNoEndSeparator("/noseperator.inthispath");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__3);
        String o_testGetPathNoEndSeparator_literalMutationString65327__4 = FilenameUtils.getPathNoEndSeparator("\\noseperator.inthispath");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__4);
        String o_testGetPathNoEndSeparator_literalMutationString65327__5 = FilenameUtils.getPathNoEndSeparator("a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString65327__5);
        String o_testGetPathNoEndSeparator_literalMutationString65327__6 = FilenameUtils.getPathNoEndSeparator("a/b/c");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString65327__6);
        String o_testGetPathNoEndSeparator_literalMutationString65327__7 = FilenameUtils.getPathNoEndSeparator("a/b/c/");
        Assert.assertEquals("a/b/c", o_testGetPathNoEndSeparator_literalMutationString65327__7);
        String o_testGetPathNoEndSeparator_literalMutationString65327__8 = FilenameUtils.getPathNoEndSeparator("a\\b\\c");
        Assert.assertEquals("a\\b", o_testGetPathNoEndSeparator_literalMutationString65327__8);
        String o_testGetPathNoEndSeparator_literalMutationString65327__9 = FilenameUtils.getPathNoEndSeparator(":");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString65327__9);
        String o_testGetPathNoEndSeparator_literalMutationString65327__10 = FilenameUtils.getPathNoEndSeparator("1:/a/b/c.txt");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString65327__10);
        String o_testGetPathNoEndSeparator_literalMutationString65327__11 = FilenameUtils.getPathNoEndSeparator("1:");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString65327__11);
        String o_testGetPathNoEndSeparator_literalMutationString65327__12 = FilenameUtils.getPathNoEndSeparator("1:a");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString65327__12);
        String o_testGetPathNoEndSeparator_literalMutationString65327__13 = FilenameUtils.getPathNoEndSeparator("//?a/b/c.txt");
        Assert.assertEquals("b", o_testGetPathNoEndSeparator_literalMutationString65327__13);
        String o_testGetPathNoEndSeparator_literalMutationString65327__14 = FilenameUtils.getPathNoEndSeparator("//a");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString65327__14);
        String o_testGetPathNoEndSeparator_literalMutationString65327__15 = FilenameUtils.getPathNoEndSeparator("");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__15);
        String o_testGetPathNoEndSeparator_literalMutationString65327__16 = FilenameUtils.getPathNoEndSeparator("C:");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__16);
        String o_testGetPathNoEndSeparator_literalMutationString65327__17 = FilenameUtils.getPathNoEndSeparator("C:/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__17);
        String o_testGetPathNoEndSeparator_literalMutationString65327__18 = FilenameUtils.getPathNoEndSeparator("//server/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__18);
        String o_testGetPathNoEndSeparator_literalMutationString65327__19 = FilenameUtils.getPathNoEndSeparator("~");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__19);
        String o_testGetPathNoEndSeparator_literalMutationString65327__20 = FilenameUtils.getPathNoEndSeparator("~/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__20);
        String o_testGetPathNoEndSeparator_literalMutationString65327__21 = FilenameUtils.getPathNoEndSeparator("~user");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__21);
        String o_testGetPathNoEndSeparator_literalMutationString65327__22 = FilenameUtils.getPathNoEndSeparator("~user/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__22);
        String o_testGetPathNoEndSeparator_literalMutationString65327__23 = FilenameUtils.getPathNoEndSeparator("a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString65327__23);
        String o_testGetPathNoEndSeparator_literalMutationString65327__24 = FilenameUtils.getPathNoEndSeparator("a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString65327__24);
        String o_testGetPathNoEndSeparator_literalMutationString65327__25 = FilenameUtils.getPathNoEndSeparator("/a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString65327__25);
        String o_testGetPathNoEndSeparator_literalMutationString65327__26 = FilenameUtils.getPathNoEndSeparator("C:a");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__26);
        String o_testGetPathNoEndSeparator_literalMutationString65327__27 = FilenameUtils.getPathNoEndSeparator("C:a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString65327__27);
        String o_testGetPathNoEndSeparator_literalMutationString65327__28 = FilenameUtils.getPathNoEndSeparator("C:/a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString65327__28);
        String o_testGetPathNoEndSeparator_literalMutationString65327__29 = FilenameUtils.getPathNoEndSeparator("//server/a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString65327__29);
        String o_testGetPathNoEndSeparator_literalMutationString65327__30 = FilenameUtils.getPathNoEndSeparator("~/a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString65327__30);
        String o_testGetPathNoEndSeparator_literalMutationString65327__31 = FilenameUtils.getPathNoEndSeparator("~user/a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString65327__31);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString65327__1);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__2);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__3);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__4);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString65327__5);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString65327__6);
        Assert.assertEquals("a/b/c", o_testGetPathNoEndSeparator_literalMutationString65327__7);
        Assert.assertEquals("a\\b", o_testGetPathNoEndSeparator_literalMutationString65327__8);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString65327__9);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString65327__10);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString65327__11);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString65327__12);
        Assert.assertEquals("b", o_testGetPathNoEndSeparator_literalMutationString65327__13);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString65327__14);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__15);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__16);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__17);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__18);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__19);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__20);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__21);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__22);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString65327__23);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString65327__24);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString65327__25);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString65327__26);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString65327__27);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString65327__28);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString65327__29);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString65327__30);
    }
}

