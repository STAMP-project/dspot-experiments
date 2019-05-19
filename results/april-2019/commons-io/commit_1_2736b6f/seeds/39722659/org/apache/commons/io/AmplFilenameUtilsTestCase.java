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
    public void testGetPrefixLength_literalMutationString3993() throws Exception {
        int o_testGetPrefixLength_literalMutationString3993__1 = FilenameUtils.getPrefixLength(null);
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3993__1)));
        int o_testGetPrefixLength_literalMutationString3993__2 = FilenameUtils.getPrefixLength(":");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3993__2)));
        int o_testGetPrefixLength_literalMutationString3993__3 = FilenameUtils.getPrefixLength("1:\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3993__3)));
        int o_testGetPrefixLength_literalMutationString3993__4 = FilenameUtils.getPrefixLength("1:");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3993__4)));
        int o_testGetPrefixLength_literalMutationString3993__5 = FilenameUtils.getPrefixLength("1:a");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3993__5)));
        int o_testGetPrefixLength_literalMutationString3993__6 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3993__6)));
        int o_testGetPrefixLength_literalMutationString3993__7 = FilenameUtils.getPrefixLength("\\\\a");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3993__7)));
        int o_testGetPrefixLength_literalMutationString3993__8 = FilenameUtils.getPrefixLength("");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3993__8)));
        int o_testGetPrefixLength_literalMutationString3993__9 = FilenameUtils.getPrefixLength("\\");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3993__9)));
        int o_testGetPrefixLength_literalMutationString3993__10 = FilenameUtils.getPrefixLength("C:");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3993__10)));
        int o_testGetPrefixLength_literalMutationString3993__11 = FilenameUtils.getPrefixLength("C:\\");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString3993__11)));
        int o_testGetPrefixLength_literalMutationString3993__12 = FilenameUtils.getPrefixLength("//server/");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3993__12)));
        int o_testGetPrefixLength_literalMutationString3993__13 = FilenameUtils.getPrefixLength("~");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3993__13)));
        int o_testGetPrefixLength_literalMutationString3993__14 = FilenameUtils.getPrefixLength("~/");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3993__14)));
        int o_testGetPrefixLength_literalMutationString3993__15 = FilenameUtils.getPrefixLength("~user");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3993__15)));
        int o_testGetPrefixLength_literalMutationString3993__16 = FilenameUtils.getPrefixLength("~user/");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3993__16)));
        int o_testGetPrefixLength_literalMutationString3993__17 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3993__17)));
        int o_testGetPrefixLength_literalMutationString3993__18 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3993__18)));
        int o_testGetPrefixLength_literalMutationString3993__19 = FilenameUtils.getPrefixLength("C:a\\b\\c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3993__19)));
        int o_testGetPrefixLength_literalMutationString3993__20 = FilenameUtils.getPrefixLength("C:\\a\\b\\c.txt");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString3993__20)));
        int o_testGetPrefixLength_literalMutationString3993__21 = FilenameUtils.getPrefixLength("\\\\ser]ver\\a\\b\\c.txt");
        Assert.assertEquals(10, ((int) (o_testGetPrefixLength_literalMutationString3993__21)));
        int o_testGetPrefixLength_literalMutationString3993__22 = FilenameUtils.getPrefixLength("a/b/c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3993__22)));
        int o_testGetPrefixLength_literalMutationString3993__23 = FilenameUtils.getPrefixLength("/a/b/c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3993__23)));
        int o_testGetPrefixLength_literalMutationString3993__24 = FilenameUtils.getPrefixLength("C:/a/b/c.txt");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString3993__24)));
        int o_testGetPrefixLength_literalMutationString3993__25 = FilenameUtils.getPrefixLength("//server/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3993__25)));
        int o_testGetPrefixLength_literalMutationString3993__26 = FilenameUtils.getPrefixLength("~/a/b/c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3993__26)));
        int o_testGetPrefixLength_literalMutationString3993__27 = FilenameUtils.getPrefixLength("~user/a/b/c.txt");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3993__27)));
        int o_testGetPrefixLength_literalMutationString3993__28 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3993__28)));
        int o_testGetPrefixLength_literalMutationString3993__29 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3993__29)));
        int o_testGetPrefixLength_literalMutationString3993__30 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3993__30)));
        int o_testGetPrefixLength_literalMutationString3993__31 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3993__31)));
        int o_testGetPrefixLength_literalMutationString3993__32 = FilenameUtils.getPrefixLength("~\\a\\b\\c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3993__32)));
        int o_testGetPrefixLength_literalMutationString3993__33 = FilenameUtils.getPrefixLength("~user\\a\\b\\c.txt");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3993__33)));
        int o_testGetPrefixLength_literalMutationString3993__34 = FilenameUtils.getPrefixLength("//server/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3993__34)));
        int o_testGetPrefixLength_literalMutationString3993__35 = FilenameUtils.getPrefixLength("//server/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3993__35)));
        int o_testGetPrefixLength_literalMutationString3993__36 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3993__36)));
        int o_testGetPrefixLength_literalMutationString3993__37 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3993__37)));
        int o_testGetPrefixLength_literalMutationString3993__38 = FilenameUtils.getPrefixLength("///a/b/c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3993__38)));
        int o_testGetPrefixLength_literalMutationString3993__39 = FilenameUtils.getPrefixLength("/:foo");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3993__39)));
        int o_testGetPrefixLength_literalMutationString3993__40 = FilenameUtils.getPrefixLength("/:/");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3993__40)));
        int o_testGetPrefixLength_literalMutationString3993__41 = FilenameUtils.getPrefixLength("/:::::::.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3993__41)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3993__1)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3993__2)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3993__3)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3993__4)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3993__5)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3993__6)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3993__7)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3993__8)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3993__9)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3993__10)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString3993__11)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3993__12)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3993__13)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3993__14)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3993__15)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3993__16)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3993__17)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3993__18)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3993__19)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString3993__20)));
        Assert.assertEquals(10, ((int) (o_testGetPrefixLength_literalMutationString3993__21)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3993__22)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3993__23)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString3993__24)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3993__25)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3993__26)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3993__27)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3993__28)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3993__29)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3993__30)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3993__31)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3993__32)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3993__33)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3993__34)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3993__35)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3993__36)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3993__37)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3993__38)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3993__39)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3993__40)));
    }
}

