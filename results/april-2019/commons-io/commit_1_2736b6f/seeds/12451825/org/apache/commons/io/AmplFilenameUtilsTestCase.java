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
    public void testGetPrefixLength_literalMutationString4056() throws Exception {
        int o_testGetPrefixLength_literalMutationString4056__1 = FilenameUtils.getPrefixLength(null);
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4056__1)));
        int o_testGetPrefixLength_literalMutationString4056__2 = FilenameUtils.getPrefixLength(":");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4056__2)));
        int o_testGetPrefixLength_literalMutationString4056__3 = FilenameUtils.getPrefixLength("1:\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4056__3)));
        int o_testGetPrefixLength_literalMutationString4056__4 = FilenameUtils.getPrefixLength("1:");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4056__4)));
        int o_testGetPrefixLength_literalMutationString4056__5 = FilenameUtils.getPrefixLength("1:a");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4056__5)));
        int o_testGetPrefixLength_literalMutationString4056__6 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4056__6)));
        int o_testGetPrefixLength_literalMutationString4056__7 = FilenameUtils.getPrefixLength("\\\\a");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4056__7)));
        int o_testGetPrefixLength_literalMutationString4056__8 = FilenameUtils.getPrefixLength("");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4056__8)));
        int o_testGetPrefixLength_literalMutationString4056__9 = FilenameUtils.getPrefixLength("\\");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4056__9)));
        int o_testGetPrefixLength_literalMutationString4056__10 = FilenameUtils.getPrefixLength("C:");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4056__10)));
        int o_testGetPrefixLength_literalMutationString4056__11 = FilenameUtils.getPrefixLength("C:\\");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString4056__11)));
        int o_testGetPrefixLength_literalMutationString4056__12 = FilenameUtils.getPrefixLength("//server/");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString4056__12)));
        int o_testGetPrefixLength_literalMutationString4056__13 = FilenameUtils.getPrefixLength("~");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4056__13)));
        int o_testGetPrefixLength_literalMutationString4056__14 = FilenameUtils.getPrefixLength("~/");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4056__14)));
        int o_testGetPrefixLength_literalMutationString4056__15 = FilenameUtils.getPrefixLength("~user");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4056__15)));
        int o_testGetPrefixLength_literalMutationString4056__16 = FilenameUtils.getPrefixLength("~user/");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4056__16)));
        int o_testGetPrefixLength_literalMutationString4056__17 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4056__17)));
        int o_testGetPrefixLength_literalMutationString4056__18 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4056__18)));
        int o_testGetPrefixLength_literalMutationString4056__19 = FilenameUtils.getPrefixLength("C:a\\b\\c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4056__19)));
        int o_testGetPrefixLength_literalMutationString4056__20 = FilenameUtils.getPrefixLength("C:\\a\\b\\c.txt");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString4056__20)));
        int o_testGetPrefixLength_literalMutationString4056__21 = FilenameUtils.getPrefixLength("\\\\server\\a\\b\\c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString4056__21)));
        int o_testGetPrefixLength_literalMutationString4056__22 = FilenameUtils.getPrefixLength("a/b/c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4056__22)));
        int o_testGetPrefixLength_literalMutationString4056__23 = FilenameUtils.getPrefixLength("/a/b/c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4056__23)));
        int o_testGetPrefixLength_literalMutationString4056__24 = FilenameUtils.getPrefixLength("C:/a/b/c.txt");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString4056__24)));
        int o_testGetPrefixLength_literalMutationString4056__25 = FilenameUtils.getPrefixLength("//server/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString4056__25)));
        int o_testGetPrefixLength_literalMutationString4056__26 = FilenameUtils.getPrefixLength("~/a/b/c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4056__26)));
        int o_testGetPrefixLength_literalMutationString4056__27 = FilenameUtils.getPrefixLength("~user/a/b/c.txt");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4056__27)));
        int o_testGetPrefixLength_literalMutationString4056__28 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4056__28)));
        int o_testGetPrefixLength_literalMutationString4056__29 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4056__29)));
        int o_testGetPrefixLength_literalMutationString4056__30 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4056__30)));
        int o_testGetPrefixLength_literalMutationString4056__31 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4056__31)));
        int o_testGetPrefixLength_literalMutationString4056__32 = FilenameUtils.getPrefixLength("~\\a\\b\\c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4056__32)));
        int o_testGetPrefixLength_literalMutationString4056__33 = FilenameUtils.getPrefixLength("~user\\a\\b\\c.txt");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4056__33)));
        int o_testGetPrefixLength_literalMutationString4056__34 = FilenameUtils.getPrefixLength("//serve!/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString4056__34)));
        int o_testGetPrefixLength_literalMutationString4056__35 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4056__35)));
        int o_testGetPrefixLength_literalMutationString4056__36 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4056__36)));
        int o_testGetPrefixLength_literalMutationString4056__37 = FilenameUtils.getPrefixLength("///a/b/c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4056__37)));
        int o_testGetPrefixLength_literalMutationString4056__38 = FilenameUtils.getPrefixLength("/:foo");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4056__38)));
        int o_testGetPrefixLength_literalMutationString4056__39 = FilenameUtils.getPrefixLength("/:/");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4056__39)));
        int o_testGetPrefixLength_literalMutationString4056__40 = FilenameUtils.getPrefixLength("/:::::::.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4056__40)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4056__1)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4056__2)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4056__3)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4056__4)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4056__5)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4056__6)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4056__7)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4056__8)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4056__9)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4056__10)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString4056__11)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString4056__12)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4056__13)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4056__14)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4056__15)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4056__16)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4056__17)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4056__18)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4056__19)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString4056__20)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString4056__21)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4056__22)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4056__23)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString4056__24)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString4056__25)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4056__26)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4056__27)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4056__28)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4056__29)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4056__30)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4056__31)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4056__32)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4056__33)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString4056__34)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4056__35)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4056__36)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4056__37)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4056__38)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4056__39)));
    }

    @Test(timeout = 10000)
    public void testGetPrefixLength_literalMutationString4014() throws Exception {
        int o_testGetPrefixLength_literalMutationString4014__1 = FilenameUtils.getPrefixLength(null);
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4014__1)));
        int o_testGetPrefixLength_literalMutationString4014__2 = FilenameUtils.getPrefixLength(":");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4014__2)));
        int o_testGetPrefixLength_literalMutationString4014__3 = FilenameUtils.getPrefixLength("1:\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4014__3)));
        int o_testGetPrefixLength_literalMutationString4014__4 = FilenameUtils.getPrefixLength("1:");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4014__4)));
        int o_testGetPrefixLength_literalMutationString4014__5 = FilenameUtils.getPrefixLength("1:a");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4014__5)));
        int o_testGetPrefixLength_literalMutationString4014__6 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4014__6)));
        int o_testGetPrefixLength_literalMutationString4014__7 = FilenameUtils.getPrefixLength("\\\\a");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4014__7)));
        int o_testGetPrefixLength_literalMutationString4014__8 = FilenameUtils.getPrefixLength("");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4014__8)));
        int o_testGetPrefixLength_literalMutationString4014__9 = FilenameUtils.getPrefixLength("\\");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4014__9)));
        int o_testGetPrefixLength_literalMutationString4014__10 = FilenameUtils.getPrefixLength("C:");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4014__10)));
        int o_testGetPrefixLength_literalMutationString4014__11 = FilenameUtils.getPrefixLength("C:\\");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString4014__11)));
        int o_testGetPrefixLength_literalMutationString4014__12 = FilenameUtils.getPrefixLength("//server/");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString4014__12)));
        int o_testGetPrefixLength_literalMutationString4014__13 = FilenameUtils.getPrefixLength("~");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4014__13)));
        int o_testGetPrefixLength_literalMutationString4014__14 = FilenameUtils.getPrefixLength("~/");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4014__14)));
        int o_testGetPrefixLength_literalMutationString4014__15 = FilenameUtils.getPrefixLength("~user");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4014__15)));
        int o_testGetPrefixLength_literalMutationString4014__16 = FilenameUtils.getPrefixLength("~user/");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4014__16)));
        int o_testGetPrefixLength_literalMutationString4014__17 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4014__17)));
        int o_testGetPrefixLength_literalMutationString4014__18 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4014__18)));
        int o_testGetPrefixLength_literalMutationString4014__19 = FilenameUtils.getPrefixLength("C:a\\b\\c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4014__19)));
        int o_testGetPrefixLength_literalMutationString4014__20 = FilenameUtils.getPrefixLength("C:\\a\\b\\c.txt");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString4014__20)));
        int o_testGetPrefixLength_literalMutationString4014__21 = FilenameUtils.getPrefixLength("\\\\server\\a\\b\\c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString4014__21)));
        int o_testGetPrefixLength_literalMutationString4014__22 = FilenameUtils.getPrefixLength("a/b/c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4014__22)));
        int o_testGetPrefixLength_literalMutationString4014__23 = FilenameUtils.getPrefixLength("/a/b/c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4014__23)));
        int o_testGetPrefixLength_literalMutationString4014__24 = FilenameUtils.getPrefixLength("C:/a/b/c.txt");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString4014__24)));
        int o_testGetPrefixLength_literalMutationString4014__25 = FilenameUtils.getPrefixLength("//ser]er/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString4014__25)));
        int o_testGetPrefixLength_literalMutationString4014__26 = FilenameUtils.getPrefixLength("~/a/b/c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4014__26)));
        int o_testGetPrefixLength_literalMutationString4014__27 = FilenameUtils.getPrefixLength("~user/a/b/c.txt");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4014__27)));
        int o_testGetPrefixLength_literalMutationString4014__28 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4014__28)));
        int o_testGetPrefixLength_literalMutationString4014__29 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4014__29)));
        int o_testGetPrefixLength_literalMutationString4014__30 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4014__30)));
        int o_testGetPrefixLength_literalMutationString4014__31 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4014__31)));
        int o_testGetPrefixLength_literalMutationString4014__32 = FilenameUtils.getPrefixLength("~\\a\\b\\c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4014__32)));
        int o_testGetPrefixLength_literalMutationString4014__33 = FilenameUtils.getPrefixLength("~user\\a\\b\\c.txt");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4014__33)));
        int o_testGetPrefixLength_literalMutationString4014__34 = FilenameUtils.getPrefixLength("//server/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString4014__34)));
        int o_testGetPrefixLength_literalMutationString4014__35 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4014__35)));
        int o_testGetPrefixLength_literalMutationString4014__36 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4014__36)));
        int o_testGetPrefixLength_literalMutationString4014__37 = FilenameUtils.getPrefixLength("///a/b/c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4014__37)));
        int o_testGetPrefixLength_literalMutationString4014__38 = FilenameUtils.getPrefixLength("/:foo");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4014__38)));
        int o_testGetPrefixLength_literalMutationString4014__39 = FilenameUtils.getPrefixLength("/:/");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4014__39)));
        int o_testGetPrefixLength_literalMutationString4014__40 = FilenameUtils.getPrefixLength("/:::::::.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4014__40)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4014__1)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4014__2)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4014__3)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4014__4)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4014__5)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4014__6)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4014__7)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4014__8)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4014__9)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4014__10)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString4014__11)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString4014__12)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4014__13)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4014__14)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4014__15)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4014__16)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4014__17)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4014__18)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4014__19)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString4014__20)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString4014__21)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4014__22)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4014__23)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString4014__24)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString4014__25)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4014__26)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4014__27)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4014__28)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4014__29)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4014__30)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4014__31)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4014__32)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4014__33)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString4014__34)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4014__35)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4014__36)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4014__37)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4014__38)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4014__39)));
    }
}

