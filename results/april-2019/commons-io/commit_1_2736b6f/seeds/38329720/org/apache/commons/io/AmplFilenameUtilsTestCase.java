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
    public void testGetPrefixLength_literalMutationString3945() throws Exception {
        int o_testGetPrefixLength_literalMutationString3945__1 = FilenameUtils.getPrefixLength(null);
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3945__1)));
        int o_testGetPrefixLength_literalMutationString3945__2 = FilenameUtils.getPrefixLength(":");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3945__2)));
        int o_testGetPrefixLength_literalMutationString3945__3 = FilenameUtils.getPrefixLength("1:\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3945__3)));
        int o_testGetPrefixLength_literalMutationString3945__4 = FilenameUtils.getPrefixLength("1:");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3945__4)));
        int o_testGetPrefixLength_literalMutationString3945__5 = FilenameUtils.getPrefixLength("1:a");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3945__5)));
        int o_testGetPrefixLength_literalMutationString3945__6 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3945__6)));
        int o_testGetPrefixLength_literalMutationString3945__7 = FilenameUtils.getPrefixLength("\\\\a");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3945__7)));
        int o_testGetPrefixLength_literalMutationString3945__8 = FilenameUtils.getPrefixLength("");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3945__8)));
        int o_testGetPrefixLength_literalMutationString3945__9 = FilenameUtils.getPrefixLength("\\");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3945__9)));
        int o_testGetPrefixLength_literalMutationString3945__10 = FilenameUtils.getPrefixLength("C:");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3945__10)));
        int o_testGetPrefixLength_literalMutationString3945__11 = FilenameUtils.getPrefixLength("C:\\");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString3945__11)));
        int o_testGetPrefixLength_literalMutationString3945__12 = FilenameUtils.getPrefixLength("//s[erver/");
        Assert.assertEquals(10, ((int) (o_testGetPrefixLength_literalMutationString3945__12)));
        int o_testGetPrefixLength_literalMutationString3945__13 = FilenameUtils.getPrefixLength("~");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3945__13)));
        int o_testGetPrefixLength_literalMutationString3945__14 = FilenameUtils.getPrefixLength("~/");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3945__14)));
        int o_testGetPrefixLength_literalMutationString3945__15 = FilenameUtils.getPrefixLength("~user");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3945__15)));
        int o_testGetPrefixLength_literalMutationString3945__16 = FilenameUtils.getPrefixLength("~user/");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3945__16)));
        int o_testGetPrefixLength_literalMutationString3945__17 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3945__17)));
        int o_testGetPrefixLength_literalMutationString3945__18 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3945__18)));
        int o_testGetPrefixLength_literalMutationString3945__19 = FilenameUtils.getPrefixLength("C:a\\b\\c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3945__19)));
        int o_testGetPrefixLength_literalMutationString3945__20 = FilenameUtils.getPrefixLength("C:\\a\\b\\c.txt");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString3945__20)));
        int o_testGetPrefixLength_literalMutationString3945__21 = FilenameUtils.getPrefixLength("\\\\server\\a\\b\\c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3945__21)));
        int o_testGetPrefixLength_literalMutationString3945__22 = FilenameUtils.getPrefixLength("a/b/c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3945__22)));
        int o_testGetPrefixLength_literalMutationString3945__23 = FilenameUtils.getPrefixLength("/a/b/c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3945__23)));
        int o_testGetPrefixLength_literalMutationString3945__24 = FilenameUtils.getPrefixLength("C:/a/b/c.txt");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString3945__24)));
        int o_testGetPrefixLength_literalMutationString3945__25 = FilenameUtils.getPrefixLength("//server/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3945__25)));
        int o_testGetPrefixLength_literalMutationString3945__26 = FilenameUtils.getPrefixLength("~/a/b/c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3945__26)));
        int o_testGetPrefixLength_literalMutationString3945__27 = FilenameUtils.getPrefixLength("~user/a/b/c.txt");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3945__27)));
        int o_testGetPrefixLength_literalMutationString3945__28 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3945__28)));
        int o_testGetPrefixLength_literalMutationString3945__29 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3945__29)));
        int o_testGetPrefixLength_literalMutationString3945__30 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3945__30)));
        int o_testGetPrefixLength_literalMutationString3945__31 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3945__31)));
        int o_testGetPrefixLength_literalMutationString3945__32 = FilenameUtils.getPrefixLength("~\\a\\b\\c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3945__32)));
        int o_testGetPrefixLength_literalMutationString3945__33 = FilenameUtils.getPrefixLength("~user\\a\\b\\c.txt");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3945__33)));
        int o_testGetPrefixLength_literalMutationString3945__34 = FilenameUtils.getPrefixLength("//server/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3945__34)));
        int o_testGetPrefixLength_literalMutationString3945__35 = FilenameUtils.getPrefixLength("//server/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3945__35)));
        int o_testGetPrefixLength_literalMutationString3945__36 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3945__36)));
        int o_testGetPrefixLength_literalMutationString3945__37 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3945__37)));
        int o_testGetPrefixLength_literalMutationString3945__38 = FilenameUtils.getPrefixLength("///a/b/c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3945__38)));
        int o_testGetPrefixLength_literalMutationString3945__39 = FilenameUtils.getPrefixLength("/:foo");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3945__39)));
        int o_testGetPrefixLength_literalMutationString3945__40 = FilenameUtils.getPrefixLength("/:/");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3945__40)));
        int o_testGetPrefixLength_literalMutationString3945__41 = FilenameUtils.getPrefixLength("/:::::::.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3945__41)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3945__1)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3945__2)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3945__3)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3945__4)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3945__5)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3945__6)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3945__7)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3945__8)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3945__9)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3945__10)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString3945__11)));
        Assert.assertEquals(10, ((int) (o_testGetPrefixLength_literalMutationString3945__12)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3945__13)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3945__14)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3945__15)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3945__16)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3945__17)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3945__18)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3945__19)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString3945__20)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3945__21)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3945__22)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3945__23)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString3945__24)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3945__25)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3945__26)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3945__27)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3945__28)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString3945__29)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3945__30)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3945__31)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString3945__32)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString3945__33)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3945__34)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString3945__35)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3945__36)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3945__37)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString3945__38)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3945__39)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString3945__40)));
    }
}

