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
    public void testGetPrefixLength_literalMutationString133123() throws Exception {
        int o_testGetPrefixLength_literalMutationString133123__1 = FilenameUtils.getPrefixLength(null);
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString133123__1)));
        int o_testGetPrefixLength_literalMutationString133123__2 = FilenameUtils.getPrefixLength(":");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString133123__2)));
        int o_testGetPrefixLength_literalMutationString133123__3 = FilenameUtils.getPrefixLength("1:\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString133123__3)));
        int o_testGetPrefixLength_literalMutationString133123__4 = FilenameUtils.getPrefixLength("1:");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString133123__4)));
        int o_testGetPrefixLength_literalMutationString133123__5 = FilenameUtils.getPrefixLength("1:a");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString133123__5)));
        int o_testGetPrefixLength_literalMutationString133123__6 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString133123__6)));
        int o_testGetPrefixLength_literalMutationString133123__7 = FilenameUtils.getPrefixLength("\\\\a");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString133123__7)));
        int o_testGetPrefixLength_literalMutationString133123__8 = FilenameUtils.getPrefixLength("");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString133123__8)));
        int o_testGetPrefixLength_literalMutationString133123__9 = FilenameUtils.getPrefixLength("\\");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString133123__9)));
        int o_testGetPrefixLength_literalMutationString133123__10 = FilenameUtils.getPrefixLength("C:");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString133123__10)));
        int o_testGetPrefixLength_literalMutationString133123__11 = FilenameUtils.getPrefixLength("C:\\");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString133123__11)));
        int o_testGetPrefixLength_literalMutationString133123__12 = FilenameUtils.getPrefixLength("//server/");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString133123__12)));
        int o_testGetPrefixLength_literalMutationString133123__13 = FilenameUtils.getPrefixLength("~");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString133123__13)));
        int o_testGetPrefixLength_literalMutationString133123__14 = FilenameUtils.getPrefixLength("~/");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString133123__14)));
        int o_testGetPrefixLength_literalMutationString133123__15 = FilenameUtils.getPrefixLength("~user");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString133123__15)));
        int o_testGetPrefixLength_literalMutationString133123__16 = FilenameUtils.getPrefixLength("~user/");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString133123__16)));
        int o_testGetPrefixLength_literalMutationString133123__17 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString133123__17)));
        int o_testGetPrefixLength_literalMutationString133123__18 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString133123__18)));
        int o_testGetPrefixLength_literalMutationString133123__19 = FilenameUtils.getPrefixLength("C:a\\b\\c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString133123__19)));
        int o_testGetPrefixLength_literalMutationString133123__20 = FilenameUtils.getPrefixLength("C:\\a\\b\\c.txt");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString133123__20)));
        int o_testGetPrefixLength_literalMutationString133123__21 = FilenameUtils.getPrefixLength("\\\\server\\a\\b\\c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString133123__21)));
        int o_testGetPrefixLength_literalMutationString133123__22 = FilenameUtils.getPrefixLength("a/b/c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString133123__22)));
        int o_testGetPrefixLength_literalMutationString133123__23 = FilenameUtils.getPrefixLength("/a/b/c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString133123__23)));
        int o_testGetPrefixLength_literalMutationString133123__24 = FilenameUtils.getPrefixLength("C:/a/b/c.txt");
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString133123__24)));
        int o_testGetPrefixLength_literalMutationString133123__25 = FilenameUtils.getPrefixLength("//serv=er/a/b/c.txt");
        Assert.assertEquals(10, ((int) (o_testGetPrefixLength_literalMutationString133123__25)));
        int o_testGetPrefixLength_literalMutationString133123__26 = FilenameUtils.getPrefixLength("~/a/b/c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString133123__26)));
        int o_testGetPrefixLength_literalMutationString133123__27 = FilenameUtils.getPrefixLength("~user/a/b/c.txt");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString133123__27)));
        int o_testGetPrefixLength_literalMutationString133123__28 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString133123__28)));
        int o_testGetPrefixLength_literalMutationString133123__29 = FilenameUtils.getPrefixLength("a\\b\\c.txt");
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString133123__29)));
        int o_testGetPrefixLength_literalMutationString133123__30 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString133123__30)));
        int o_testGetPrefixLength_literalMutationString133123__31 = FilenameUtils.getPrefixLength("\\a\\b\\c.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString133123__31)));
        int o_testGetPrefixLength_literalMutationString133123__32 = FilenameUtils.getPrefixLength("~\\a\\b\\c.txt");
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString133123__32)));
        int o_testGetPrefixLength_literalMutationString133123__33 = FilenameUtils.getPrefixLength("~user\\a\\b\\c.txt");
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString133123__33)));
        int o_testGetPrefixLength_literalMutationString133123__34 = FilenameUtils.getPrefixLength("//server/a/b/c.txt");
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString133123__34)));
        int o_testGetPrefixLength_literalMutationString133123__35 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString133123__35)));
        int o_testGetPrefixLength_literalMutationString133123__36 = FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString133123__36)));
        int o_testGetPrefixLength_literalMutationString133123__37 = FilenameUtils.getPrefixLength("///a/b/c.txt");
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString133123__37)));
        int o_testGetPrefixLength_literalMutationString133123__38 = FilenameUtils.getPrefixLength("/:foo");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString133123__38)));
        int o_testGetPrefixLength_literalMutationString133123__39 = FilenameUtils.getPrefixLength("/:/");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString133123__39)));
        int o_testGetPrefixLength_literalMutationString133123__40 = FilenameUtils.getPrefixLength("/:::::::.txt");
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString133123__40)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString133123__1)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString133123__2)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString133123__3)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString133123__4)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString133123__5)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString133123__6)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString133123__7)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString133123__8)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString133123__9)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString133123__10)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString133123__11)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString133123__12)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString133123__13)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString133123__14)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString133123__15)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString133123__16)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString133123__17)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString133123__18)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString133123__19)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString133123__20)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString133123__21)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString133123__22)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString133123__23)));
        Assert.assertEquals(3, ((int) (o_testGetPrefixLength_literalMutationString133123__24)));
        Assert.assertEquals(10, ((int) (o_testGetPrefixLength_literalMutationString133123__25)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString133123__26)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString133123__27)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString133123__28)));
        Assert.assertEquals(0, ((int) (o_testGetPrefixLength_literalMutationString133123__29)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString133123__30)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString133123__31)));
        Assert.assertEquals(2, ((int) (o_testGetPrefixLength_literalMutationString133123__32)));
        Assert.assertEquals(6, ((int) (o_testGetPrefixLength_literalMutationString133123__33)));
        Assert.assertEquals(9, ((int) (o_testGetPrefixLength_literalMutationString133123__34)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString133123__35)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString133123__36)));
        Assert.assertEquals(-1, ((int) (o_testGetPrefixLength_literalMutationString133123__37)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString133123__38)));
        Assert.assertEquals(1, ((int) (o_testGetPrefixLength_literalMutationString133123__39)));
    }

    @Test(timeout = 10000)
    public void testGetPath_literalMutationString47537() throws Exception {
        String o_testGetPath_literalMutationString47537__1 = FilenameUtils.getPath(null);
        Assert.assertNull(o_testGetPath_literalMutationString47537__1);
        String o_testGetPath_literalMutationString47537__2 = FilenameUtils.getPath("noseperator.inthispath");
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__2);
        String o_testGetPath_literalMutationString47537__3 = FilenameUtils.getPath("/noseperator.inthispath");
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__3);
        String o_testGetPath_literalMutationString47537__4 = FilenameUtils.getPath("\\noseperator.inthispath");
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__4);
        FilenameUtils.getPath("a/b/c.txt");
        FilenameUtils.getPath("a/b/c.txt");
        FilenameUtils.getPath("a/b/c.txt");
        FilenameUtils.getPath("a/b/c");
        FilenameUtils.getPath("a/b/c/");
        String o_testGetPath_literalMutationString47537__8 = FilenameUtils.getPath("a\\b\\c");
        Assert.assertEquals("a\\b\\", o_testGetPath_literalMutationString47537__8);
        String o_testGetPath_literalMutationString47537__9 = FilenameUtils.getPath(":");
        Assert.assertNull(o_testGetPath_literalMutationString47537__9);
        String o_testGetPath_literalMutationString47537__10 = FilenameUtils.getPath("1:/a/b/c.txt");
        Assert.assertNull(o_testGetPath_literalMutationString47537__10);
        String o_testGetPath_literalMutationString47537__11 = FilenameUtils.getPath("1:");
        Assert.assertNull(o_testGetPath_literalMutationString47537__11);
        String o_testGetPath_literalMutationString47537__12 = FilenameUtils.getPath("1:a");
        Assert.assertNull(o_testGetPath_literalMutationString47537__12);
        String o_testGetPath_literalMutationString47537__13 = FilenameUtils.getPath("///a/b/c.txt");
        Assert.assertNull(o_testGetPath_literalMutationString47537__13);
        String o_testGetPath_literalMutationString47537__14 = FilenameUtils.getPath("//a");
        Assert.assertNull(o_testGetPath_literalMutationString47537__14);
        String o_testGetPath_literalMutationString47537__15 = FilenameUtils.getPath("");
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__15);
        String o_testGetPath_literalMutationString47537__16 = FilenameUtils.getPath("C:");
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__16);
        String o_testGetPath_literalMutationString47537__17 = FilenameUtils.getPath("C:/");
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__17);
        String o_testGetPath_literalMutationString47537__18 = FilenameUtils.getPath("//serve]r/");
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__18);
        String o_testGetPath_literalMutationString47537__19 = FilenameUtils.getPath("~");
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__19);
        String o_testGetPath_literalMutationString47537__20 = FilenameUtils.getPath("~/");
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__20);
        String o_testGetPath_literalMutationString47537__21 = FilenameUtils.getPath("~user");
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__21);
        String o_testGetPath_literalMutationString47537__22 = FilenameUtils.getPath("~user/");
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__22);
        FilenameUtils.getPath("/a/b/c.txt");
        String o_testGetPath_literalMutationString47537__26 = FilenameUtils.getPath("C:a");
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__26);
        FilenameUtils.getPath("C:a/b/c.txt");
        FilenameUtils.getPath("C:/a/b/c.txt");
        FilenameUtils.getPath("//server/a/b/c.txt");
        FilenameUtils.getPath("~/a/b/c.txt");
        FilenameUtils.getPath("~user/a/b/c.txt");
        Assert.assertNull(o_testGetPath_literalMutationString47537__1);
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__2);
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__3);
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__4);
        Assert.assertEquals("a\\b\\", o_testGetPath_literalMutationString47537__8);
        Assert.assertNull(o_testGetPath_literalMutationString47537__9);
        Assert.assertNull(o_testGetPath_literalMutationString47537__10);
        Assert.assertNull(o_testGetPath_literalMutationString47537__11);
        Assert.assertNull(o_testGetPath_literalMutationString47537__12);
        Assert.assertNull(o_testGetPath_literalMutationString47537__13);
        Assert.assertNull(o_testGetPath_literalMutationString47537__14);
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__15);
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__16);
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__17);
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__18);
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__19);
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__20);
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__21);
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__22);
        Assert.assertEquals("", o_testGetPath_literalMutationString47537__26);
    }
}

