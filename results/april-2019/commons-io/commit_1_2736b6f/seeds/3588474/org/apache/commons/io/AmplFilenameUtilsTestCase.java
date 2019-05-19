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
    public void testGetPrefixLength_literalMutationString3994() throws Exception {
        int o_testGetPrefixLength_literalMutationString3994__1 = FilenameUtils.getPrefixLength(null);
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3994__1)));
        int o_testGetPrefixLength_literalMutationString3994__2 = FilenameUtils.getPrefixLength(":");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3994__2)));
        int o_testGetPrefixLength_literalMutationString3994__3 = FilenameUtils.getPrefixLength("1:\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3994__3)));
        int o_testGetPrefixLength_literalMutationString3994__4 = FilenameUtils.getPrefixLength("1:");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3994__4)));
        int o_testGetPrefixLength_literalMutationString3994__5 = FilenameUtils.getPrefixLength("1:a");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3994__5)));
        int o_testGetPrefixLength_literalMutationString3994__6 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3994__6)));
        int o_testGetPrefixLength_literalMutationString3994__7 = FilenameUtils.getPrefixLength("\\\\a");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3994__7)));
        int o_testGetPrefixLength_literalMutationString3994__8 = FilenameUtils.getPrefixLength("");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3994__8)));
        int o_testGetPrefixLength_literalMutationString3994__9 = FilenameUtils.getPrefixLength("\\");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3994__9)));
        int o_testGetPrefixLength_literalMutationString3994__10 = FilenameUtils.getPrefixLength("C:");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3994__10)));
        int o_testGetPrefixLength_literalMutationString3994__11 = FilenameUtils.getPrefixLength("C:\\");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString3994__11)));
        int o_testGetPrefixLength_literalMutationString3994__12 = FilenameUtils.getPrefixLength("//server/");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3994__12)));
        int o_testGetPrefixLength_literalMutationString3994__13 = FilenameUtils.getPrefixLength("~");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3994__13)));
        int o_testGetPrefixLength_literalMutationString3994__14 = FilenameUtils.getPrefixLength("~/");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3994__14)));
        int o_testGetPrefixLength_literalMutationString3994__15 = FilenameUtils.getPrefixLength("~user");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3994__15)));
        int o_testGetPrefixLength_literalMutationString3994__16 = FilenameUtils.getPrefixLength("~user/");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3994__16)));
        int o_testGetPrefixLength_literalMutationString3994__17 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3994__17)));
        int o_testGetPrefixLength_literalMutationString3994__18 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3994__18)));
        int o_testGetPrefixLength_literalMutationString3994__19 = FilenameUtils.getPrefixLength("C:a\\b\\c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3994__19)));
        int o_testGetPrefixLength_literalMutationString3994__20 = FilenameUtils.getPrefixLength("C:\\a\\b\\c.txt");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString3994__20)));
        int o_testGetPrefixLength_literalMutationString3994__21 = FilenameUtils.getPrefixLength("\\\\se^rver\\a\\b\\c.txt");
        Assert.assertEquals(10, ((int) (o_testGetPrefixLength_literalMutationString3994__21)));
        int o_testGetPrefixLength_literalMutationString3994__22 = FilenameUtils.getPrefixLength("a/b/c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3994__22)));
        int o_testGetPrefixLength_literalMutationString3994__23 = FilenameUtils.getPrefixLength("/a/b/c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3994__23)));
        int o_testGetPrefixLength_literalMutationString3994__24 = FilenameUtils.getPrefixLength("C:/a/b/c.txt");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString3994__24)));
        int o_testGetPrefixLength_literalMutationString3994__25 = FilenameUtils.getPrefixLength("//server/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3994__25)));
        int o_testGetPrefixLength_literalMutationString3994__26 = FilenameUtils.getPrefixLength("~/a/b/c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3994__26)));
        int o_testGetPrefixLength_literalMutationString3994__27 = FilenameUtils.getPrefixLength("~user/a/b/c.txt");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3994__27)));
        int o_testGetPrefixLength_literalMutationString3994__28 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3994__28)));
        int o_testGetPrefixLength_literalMutationString3994__29 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3994__29)));
        int o_testGetPrefixLength_literalMutationString3994__30 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3994__30)));
        int o_testGetPrefixLength_literalMutationString3994__31 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3994__31)));
        int o_testGetPrefixLength_literalMutationString3994__32 = FilenameUtils.getPrefixLength("~\\a\\b\\c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3994__32)));
        int o_testGetPrefixLength_literalMutationString3994__33 = FilenameUtils.getPrefixLength("~user\\a\\b\\c.txt");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3994__33)));
        int o_testGetPrefixLength_literalMutationString3994__34 = FilenameUtils.getPrefixLength("//server/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3994__34)));
        int o_testGetPrefixLength_literalMutationString3994__35 = FilenameUtils.getPrefixLength("//server/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3994__35)));
        int o_testGetPrefixLength_literalMutationString3994__36 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3994__36)));
        int o_testGetPrefixLength_literalMutationString3994__37 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3994__37)));
        int o_testGetPrefixLength_literalMutationString3994__38 = FilenameUtils.getPrefixLength("///a/b/c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3994__38)));
        int o_testGetPrefixLength_literalMutationString3994__39 = FilenameUtils.getPrefixLength("/:foo");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3994__39)));
        int o_testGetPrefixLength_literalMutationString3994__40 = FilenameUtils.getPrefixLength("/:/");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3994__40)));
        int o_testGetPrefixLength_literalMutationString3994__41 = FilenameUtils.getPrefixLength("/:::::::.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3994__41)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3994__1)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3994__2)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3994__3)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3994__4)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3994__5)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3994__6)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3994__7)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3994__8)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3994__9)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3994__10)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString3994__11)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3994__12)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3994__13)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3994__14)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3994__15)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3994__16)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3994__17)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3994__18)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3994__19)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString3994__20)));
        Assert.assertEquals(10, ((int) (o_testGetPrefixLength_literalMutationString3994__21)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3994__22)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3994__23)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString3994__24)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3994__25)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3994__26)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3994__27)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3994__28)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3994__29)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3994__30)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3994__31)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3994__32)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3994__33)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3994__34)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3994__35)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3994__36)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3994__37)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3994__38)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3994__39)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3994__40)));
    }

    @Test(timeout = 10000)
    public void testGetPrefixLength_literalMutationString4061() throws Exception {
        int o_testGetPrefixLength_literalMutationString4061__1 = FilenameUtils.getPrefixLength(null);
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4061__1)));
        int o_testGetPrefixLength_literalMutationString4061__2 = FilenameUtils.getPrefixLength(":");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4061__2)));
        int o_testGetPrefixLength_literalMutationString4061__3 = FilenameUtils.getPrefixLength("1:\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4061__3)));
        int o_testGetPrefixLength_literalMutationString4061__4 = FilenameUtils.getPrefixLength("1:");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4061__4)));
        int o_testGetPrefixLength_literalMutationString4061__5 = FilenameUtils.getPrefixLength("1:a");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4061__5)));
        int o_testGetPrefixLength_literalMutationString4061__6 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4061__6)));
        int o_testGetPrefixLength_literalMutationString4061__7 = FilenameUtils.getPrefixLength("\\\\a");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4061__7)));
        int o_testGetPrefixLength_literalMutationString4061__8 = FilenameUtils.getPrefixLength("");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4061__8)));
        int o_testGetPrefixLength_literalMutationString4061__9 = FilenameUtils.getPrefixLength("\\");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4061__9)));
        int o_testGetPrefixLength_literalMutationString4061__10 = FilenameUtils.getPrefixLength("C:");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4061__10)));
        int o_testGetPrefixLength_literalMutationString4061__11 = FilenameUtils.getPrefixLength("C:\\");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString4061__11)));
        int o_testGetPrefixLength_literalMutationString4061__12 = FilenameUtils.getPrefixLength("//server/");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString4061__12)));
        int o_testGetPrefixLength_literalMutationString4061__13 = FilenameUtils.getPrefixLength("~");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4061__13)));
        int o_testGetPrefixLength_literalMutationString4061__14 = FilenameUtils.getPrefixLength("~/");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4061__14)));
        int o_testGetPrefixLength_literalMutationString4061__15 = FilenameUtils.getPrefixLength("~user");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4061__15)));
        int o_testGetPrefixLength_literalMutationString4061__16 = FilenameUtils.getPrefixLength("~user/");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4061__16)));
        int o_testGetPrefixLength_literalMutationString4061__17 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4061__17)));
        int o_testGetPrefixLength_literalMutationString4061__18 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4061__18)));
        int o_testGetPrefixLength_literalMutationString4061__19 = FilenameUtils.getPrefixLength("C:a\\b\\c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4061__19)));
        int o_testGetPrefixLength_literalMutationString4061__20 = FilenameUtils.getPrefixLength("C:\\a\\b\\c.txt");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString4061__20)));
        int o_testGetPrefixLength_literalMutationString4061__21 = FilenameUtils.getPrefixLength("\\\\server\\a\\b\\c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString4061__21)));
        int o_testGetPrefixLength_literalMutationString4061__22 = FilenameUtils.getPrefixLength("a/b/c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4061__22)));
        int o_testGetPrefixLength_literalMutationString4061__23 = FilenameUtils.getPrefixLength("/a/b/c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4061__23)));
        int o_testGetPrefixLength_literalMutationString4061__24 = FilenameUtils.getPrefixLength("C:/a/b/c.txt");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString4061__24)));
        int o_testGetPrefixLength_literalMutationString4061__25 = FilenameUtils.getPrefixLength("//server/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString4061__25)));
        int o_testGetPrefixLength_literalMutationString4061__26 = FilenameUtils.getPrefixLength("~/a/b/c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4061__26)));
        int o_testGetPrefixLength_literalMutationString4061__27 = FilenameUtils.getPrefixLength("~user/a/b/c.txt");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4061__27)));
        int o_testGetPrefixLength_literalMutationString4061__28 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4061__28)));
        int o_testGetPrefixLength_literalMutationString4061__29 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4061__29)));
        int o_testGetPrefixLength_literalMutationString4061__30 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4061__30)));
        int o_testGetPrefixLength_literalMutationString4061__31 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4061__31)));
        int o_testGetPrefixLength_literalMutationString4061__32 = FilenameUtils.getPrefixLength("~\\a\\b\\c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4061__32)));
        int o_testGetPrefixLength_literalMutationString4061__33 = FilenameUtils.getPrefixLength("~user\\a\\b\\c.txt");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4061__33)));
        int o_testGetPrefixLength_literalMutationString4061__34 = FilenameUtils.getPrefixLength("//s_erver/a/b/c.txt");
        Assert.assertEquals(10, ((int) (o_testGetPrefixLength_literalMutationString4061__34)));
        int o_testGetPrefixLength_literalMutationString4061__35 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4061__35)));
        int o_testGetPrefixLength_literalMutationString4061__36 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4061__36)));
        int o_testGetPrefixLength_literalMutationString4061__37 = FilenameUtils.getPrefixLength("///a/b/c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4061__37)));
        int o_testGetPrefixLength_literalMutationString4061__38 = FilenameUtils.getPrefixLength("/:foo");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4061__38)));
        int o_testGetPrefixLength_literalMutationString4061__39 = FilenameUtils.getPrefixLength("/:/");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4061__39)));
        int o_testGetPrefixLength_literalMutationString4061__40 = FilenameUtils.getPrefixLength("/:::::::.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4061__40)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4061__1)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4061__2)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4061__3)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4061__4)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4061__5)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4061__6)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4061__7)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4061__8)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4061__9)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4061__10)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString4061__11)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString4061__12)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4061__13)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4061__14)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4061__15)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4061__16)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4061__17)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4061__18)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4061__19)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString4061__20)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString4061__21)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4061__22)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4061__23)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString4061__24)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString4061__25)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4061__26)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4061__27)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4061__28)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString4061__29)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4061__30)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4061__31)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString4061__32)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString4061__33)));
        Assert.assertEquals(10, ((int) (o_testGetPrefixLength_literalMutationString4061__34)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4061__35)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4061__36)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString4061__37)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4061__38)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString4061__39)));
    }
}

