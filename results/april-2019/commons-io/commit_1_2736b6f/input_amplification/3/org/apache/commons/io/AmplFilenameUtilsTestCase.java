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
    public void testGetPrefix_add171null43298_literalMutationString54812() throws Exception {
        String o_testGetPrefix_add171__1 = FilenameUtils.getPrefix(null);
        Assert.assertNull(o_testGetPrefix_add171__1);
        String o_testGetPrefix_add171__2 = FilenameUtils.getPrefix(":");
        Assert.assertNull(o_testGetPrefix_add171__2);
        String o_testGetPrefix_add171__3 = FilenameUtils.getPrefix("1:\\a\\b\\c.txt");
        Assert.assertNull(o_testGetPrefix_add171__3);
        String o_testGetPrefix_add171__4 = FilenameUtils.getPrefix("1:");
        Assert.assertNull(o_testGetPrefix_add171__4);
        String o_testGetPrefix_add171__5 = FilenameUtils.getPrefix("1:a");
        Assert.assertNull(o_testGetPrefix_add171__5);
        String o_testGetPrefix_add171__6 = FilenameUtils.getPrefix("\\\\\\a\\b\\c.txt");
        Assert.assertNull(o_testGetPrefix_add171__6);
        String o_testGetPrefix_add171__7 = FilenameUtils.getPrefix("\\\\a");
        Assert.assertNull(o_testGetPrefix_add171__7);
        String o_testGetPrefix_add171__8 = FilenameUtils.getPrefix("");
        Assert.assertEquals("", o_testGetPrefix_add171__8);
        String o_testGetPrefix_add171__9 = FilenameUtils.getPrefix("\\");
        Assert.assertEquals("\\", o_testGetPrefix_add171__9);
        String o_testGetPrefix_add171__10 = FilenameUtils.getPrefix("C:");
        Assert.assertEquals("C:", o_testGetPrefix_add171__10);
        String o_testGetPrefix_add171__11 = FilenameUtils.getPrefix("C:\\");
        Assert.assertEquals("C:\\", o_testGetPrefix_add171__11);
        FilenameUtils.getPrefix("//server/");
        String o_testGetPrefix_add171__13 = FilenameUtils.getPrefix("~");
        Assert.assertEquals("~/", o_testGetPrefix_add171__13);
        String o_testGetPrefix_add171__14 = FilenameUtils.getPrefix("~/");
        Assert.assertEquals("~/", o_testGetPrefix_add171__14);
        String o_testGetPrefix_add171__15 = FilenameUtils.getPrefix("~user");
        Assert.assertEquals("~user/", o_testGetPrefix_add171__15);
        String o_testGetPrefix_add171__16 = FilenameUtils.getPrefix("~user/");
        Assert.assertEquals("~user/", o_testGetPrefix_add171__16);
        String o_testGetPrefix_add171__17 = FilenameUtils.getPrefix("a\\b\\c.txt");
        Assert.assertEquals("", o_testGetPrefix_add171__17);
        String o_testGetPrefix_add171__18 = FilenameUtils.getPrefix("\\a\\b\\c.txt");
        Assert.assertEquals("\\", o_testGetPrefix_add171__18);
        String o_testGetPrefix_add171__19 = FilenameUtils.getPrefix("\\a\\b\\c.txt");
        Assert.assertEquals("\\", o_testGetPrefix_add171__19);
        String o_testGetPrefix_add171__20 = FilenameUtils.getPrefix(null);
        Assert.assertNull(o_testGetPrefix_add171__20);
        String o_testGetPrefix_add171__21 = FilenameUtils.getPrefix("\\\\*erver\\a\\b\\c.txt");
        Assert.assertEquals("\\\\*erver\\", o_testGetPrefix_add171__21);
        String o_testGetPrefix_add171__22 = FilenameUtils.getPrefix("a/b/c.txt");
        Assert.assertEquals("", o_testGetPrefix_add171__22);
        FilenameUtils.getPrefix("/a/b/c.txt");
        String o_testGetPrefix_add171__24 = FilenameUtils.getPrefix("C:/a/b/c.txt");
        Assert.assertEquals("C:/", o_testGetPrefix_add171__24);
        FilenameUtils.getPrefix("//server/a/b/c.txt");
        String o_testGetPrefix_add171__26 = FilenameUtils.getPrefix("~/a/b/c.txt");
        Assert.assertEquals("~/", o_testGetPrefix_add171__26);
        String o_testGetPrefix_add171__27 = FilenameUtils.getPrefix("~user/a/b/c.txt");
        Assert.assertEquals("~user/", o_testGetPrefix_add171__27);
        String o_testGetPrefix_add171__28 = FilenameUtils.getPrefix("a\\b\\c.txt");
        Assert.assertEquals("", o_testGetPrefix_add171__28);
        String o_testGetPrefix_add171__29 = FilenameUtils.getPrefix("a\\b\\c.txt");
        Assert.assertEquals("", o_testGetPrefix_add171__29);
        String o_testGetPrefix_add171__30 = FilenameUtils.getPrefix("\\a\\b\\c.txt");
        Assert.assertEquals("\\", o_testGetPrefix_add171__30);
        String o_testGetPrefix_add171__31 = FilenameUtils.getPrefix("\\a\\b\\c.txt");
        Assert.assertEquals("\\", o_testGetPrefix_add171__31);
        String o_testGetPrefix_add171__32 = FilenameUtils.getPrefix("~\\a\\b\\c.txt");
        Assert.assertEquals("~\\", o_testGetPrefix_add171__32);
        String o_testGetPrefix_add171__33 = FilenameUtils.getPrefix("~user\\a\\b\\c.txt");
        Assert.assertEquals("~user\\", o_testGetPrefix_add171__33);
        Assert.assertNull(o_testGetPrefix_add171__1);
        Assert.assertNull(o_testGetPrefix_add171__2);
        Assert.assertNull(o_testGetPrefix_add171__3);
        Assert.assertNull(o_testGetPrefix_add171__4);
        Assert.assertNull(o_testGetPrefix_add171__5);
        Assert.assertNull(o_testGetPrefix_add171__6);
        Assert.assertNull(o_testGetPrefix_add171__7);
        Assert.assertEquals("", o_testGetPrefix_add171__8);
        Assert.assertEquals("\\", o_testGetPrefix_add171__9);
        Assert.assertEquals("C:", o_testGetPrefix_add171__10);
        Assert.assertEquals("C:\\", o_testGetPrefix_add171__11);
        Assert.assertEquals("~/", o_testGetPrefix_add171__13);
        Assert.assertEquals("~/", o_testGetPrefix_add171__14);
        Assert.assertEquals("~user/", o_testGetPrefix_add171__15);
        Assert.assertEquals("~user/", o_testGetPrefix_add171__16);
        Assert.assertEquals("", o_testGetPrefix_add171__17);
        Assert.assertEquals("\\", o_testGetPrefix_add171__18);
        Assert.assertEquals("\\", o_testGetPrefix_add171__19);
        Assert.assertNull(o_testGetPrefix_add171__20);
        Assert.assertEquals("\\\\*erver\\", o_testGetPrefix_add171__21);
        Assert.assertEquals("", o_testGetPrefix_add171__22);
        Assert.assertEquals("C:/", o_testGetPrefix_add171__24);
        Assert.assertEquals("~/", o_testGetPrefix_add171__26);
        Assert.assertEquals("~user/", o_testGetPrefix_add171__27);
        Assert.assertEquals("", o_testGetPrefix_add171__28);
        Assert.assertEquals("", o_testGetPrefix_add171__29);
        Assert.assertEquals("\\", o_testGetPrefix_add171__30);
        Assert.assertEquals("\\", o_testGetPrefix_add171__31);
        Assert.assertEquals("~\\", o_testGetPrefix_add171__32);
    }

    @Test(timeout = 10000)
    public void testGetPath_literalMutationString178353() throws Exception {
        String o_testGetPath_literalMutationString178353__1 = FilenameUtils.getPath(null);
        Assert.assertNull(o_testGetPath_literalMutationString178353__1);
        String o_testGetPath_literalMutationString178353__2 = FilenameUtils.getPath("noseperator.inthispath");
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__2);
        String o_testGetPath_literalMutationString178353__3 = FilenameUtils.getPath("/noseperator.inthispath");
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__3);
        String o_testGetPath_literalMutationString178353__4 = FilenameUtils.getPath("\\noseperator.inthispath");
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__4);
        String o_testGetPath_literalMutationString178353__5 = FilenameUtils.getPath("a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString178353__5);
        String o_testGetPath_literalMutationString178353__6 = FilenameUtils.getPath("a/b/c");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString178353__6);
        String o_testGetPath_literalMutationString178353__7 = FilenameUtils.getPath("a/b/c/");
        Assert.assertEquals("a/b/c/", o_testGetPath_literalMutationString178353__7);
        String o_testGetPath_literalMutationString178353__8 = FilenameUtils.getPath("a\\b\\c");
        Assert.assertEquals("a\\b\\", o_testGetPath_literalMutationString178353__8);
        String o_testGetPath_literalMutationString178353__9 = FilenameUtils.getPath(":");
        Assert.assertNull(o_testGetPath_literalMutationString178353__9);
        String o_testGetPath_literalMutationString178353__10 = FilenameUtils.getPath("1:/a/b/c.txt");
        Assert.assertNull(o_testGetPath_literalMutationString178353__10);
        String o_testGetPath_literalMutationString178353__11 = FilenameUtils.getPath("1:");
        Assert.assertNull(o_testGetPath_literalMutationString178353__11);
        String o_testGetPath_literalMutationString178353__12 = FilenameUtils.getPath("1:a");
        Assert.assertNull(o_testGetPath_literalMutationString178353__12);
        String o_testGetPath_literalMutationString178353__13 = FilenameUtils.getPath("///a/b/c.txt");
        Assert.assertNull(o_testGetPath_literalMutationString178353__13);
        String o_testGetPath_literalMutationString178353__14 = FilenameUtils.getPath("//a");
        Assert.assertNull(o_testGetPath_literalMutationString178353__14);
        String o_testGetPath_literalMutationString178353__15 = FilenameUtils.getPath("");
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__15);
        String o_testGetPath_literalMutationString178353__16 = FilenameUtils.getPath("C:");
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__16);
        String o_testGetPath_literalMutationString178353__17 = FilenameUtils.getPath("C:/");
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__17);
        String o_testGetPath_literalMutationString178353__18 = FilenameUtils.getPath("//serv&r/");
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__18);
        String o_testGetPath_literalMutationString178353__19 = FilenameUtils.getPath("~");
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__19);
        String o_testGetPath_literalMutationString178353__20 = FilenameUtils.getPath("~/");
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__20);
        String o_testGetPath_literalMutationString178353__21 = FilenameUtils.getPath("~user");
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__21);
        String o_testGetPath_literalMutationString178353__22 = FilenameUtils.getPath("~user/");
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__22);
        String o_testGetPath_literalMutationString178353__23 = FilenameUtils.getPath("a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString178353__23);
        String o_testGetPath_literalMutationString178353__24 = FilenameUtils.getPath("a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString178353__24);
        String o_testGetPath_literalMutationString178353__25 = FilenameUtils.getPath("/a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString178353__25);
        String o_testGetPath_literalMutationString178353__26 = FilenameUtils.getPath("C:a");
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__26);
        String o_testGetPath_literalMutationString178353__27 = FilenameUtils.getPath("C:a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString178353__27);
        String o_testGetPath_literalMutationString178353__28 = FilenameUtils.getPath("C:/a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString178353__28);
        String o_testGetPath_literalMutationString178353__29 = FilenameUtils.getPath("//server/a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString178353__29);
        String o_testGetPath_literalMutationString178353__30 = FilenameUtils.getPath("~/a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString178353__30);
        String o_testGetPath_literalMutationString178353__31 = FilenameUtils.getPath("~user/a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString178353__31);
        Assert.assertNull(o_testGetPath_literalMutationString178353__1);
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__2);
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__3);
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__4);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString178353__5);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString178353__6);
        Assert.assertEquals("a/b/c/", o_testGetPath_literalMutationString178353__7);
        Assert.assertEquals("a\\b\\", o_testGetPath_literalMutationString178353__8);
        Assert.assertNull(o_testGetPath_literalMutationString178353__9);
        Assert.assertNull(o_testGetPath_literalMutationString178353__10);
        Assert.assertNull(o_testGetPath_literalMutationString178353__11);
        Assert.assertNull(o_testGetPath_literalMutationString178353__12);
        Assert.assertNull(o_testGetPath_literalMutationString178353__13);
        Assert.assertNull(o_testGetPath_literalMutationString178353__14);
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__15);
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__16);
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__17);
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__18);
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__19);
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__20);
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__21);
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__22);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString178353__23);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString178353__24);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString178353__25);
        Assert.assertEquals("", o_testGetPath_literalMutationString178353__26);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString178353__27);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString178353__28);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString178353__29);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString178353__30);
    }

    @Test(timeout = 10000)
    public void testGetPathNoEndSeparator_literalMutationString421411() throws Exception {
        String o_testGetPathNoEndSeparator_literalMutationString421411__1 = FilenameUtils.getPath(null);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString421411__1);
        String o_testGetPathNoEndSeparator_literalMutationString421411__2 = FilenameUtils.getPath("noseperator.inthispath");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__2);
        String o_testGetPathNoEndSeparator_literalMutationString421411__3 = FilenameUtils.getPathNoEndSeparator("/noseperator.inthispath");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__3);
        String o_testGetPathNoEndSeparator_literalMutationString421411__4 = FilenameUtils.getPathNoEndSeparator("\\noseperator.inthispath");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__4);
        String o_testGetPathNoEndSeparator_literalMutationString421411__5 = FilenameUtils.getPathNoEndSeparator("a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString421411__5);
        String o_testGetPathNoEndSeparator_literalMutationString421411__6 = FilenameUtils.getPathNoEndSeparator("a/b/c");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString421411__6);
        String o_testGetPathNoEndSeparator_literalMutationString421411__7 = FilenameUtils.getPathNoEndSeparator("a/b/c/");
        Assert.assertEquals("a/b/c", o_testGetPathNoEndSeparator_literalMutationString421411__7);
        String o_testGetPathNoEndSeparator_literalMutationString421411__8 = FilenameUtils.getPathNoEndSeparator("a\\b\\c");
        Assert.assertEquals("a\\b", o_testGetPathNoEndSeparator_literalMutationString421411__8);
        String o_testGetPathNoEndSeparator_literalMutationString421411__9 = FilenameUtils.getPathNoEndSeparator(":");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString421411__9);
        String o_testGetPathNoEndSeparator_literalMutationString421411__10 = FilenameUtils.getPathNoEndSeparator("1:/a/b/c.txt");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString421411__10);
        String o_testGetPathNoEndSeparator_literalMutationString421411__11 = FilenameUtils.getPathNoEndSeparator("1:");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString421411__11);
        String o_testGetPathNoEndSeparator_literalMutationString421411__12 = FilenameUtils.getPathNoEndSeparator("1:a");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString421411__12);
        String o_testGetPathNoEndSeparator_literalMutationString421411__13 = FilenameUtils.getPathNoEndSeparator("///a/b/c.txt");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString421411__13);
        String o_testGetPathNoEndSeparator_literalMutationString421411__14 = FilenameUtils.getPathNoEndSeparator("//a");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString421411__14);
        String o_testGetPathNoEndSeparator_literalMutationString421411__15 = FilenameUtils.getPathNoEndSeparator("");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__15);
        String o_testGetPathNoEndSeparator_literalMutationString421411__16 = FilenameUtils.getPathNoEndSeparator("C:");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__16);
        String o_testGetPathNoEndSeparator_literalMutationString421411__17 = FilenameUtils.getPathNoEndSeparator("C:/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__17);
        String o_testGetPathNoEndSeparator_literalMutationString421411__18 = FilenameUtils.getPathNoEndSeparator("//serv+er/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__18);
        String o_testGetPathNoEndSeparator_literalMutationString421411__19 = FilenameUtils.getPathNoEndSeparator("~");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__19);
        String o_testGetPathNoEndSeparator_literalMutationString421411__20 = FilenameUtils.getPathNoEndSeparator("~/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__20);
        String o_testGetPathNoEndSeparator_literalMutationString421411__21 = FilenameUtils.getPathNoEndSeparator("~user");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__21);
        String o_testGetPathNoEndSeparator_literalMutationString421411__22 = FilenameUtils.getPathNoEndSeparator("~user/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__22);
        String o_testGetPathNoEndSeparator_literalMutationString421411__23 = FilenameUtils.getPathNoEndSeparator("a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString421411__23);
        String o_testGetPathNoEndSeparator_literalMutationString421411__24 = FilenameUtils.getPathNoEndSeparator("a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString421411__24);
        String o_testGetPathNoEndSeparator_literalMutationString421411__25 = FilenameUtils.getPathNoEndSeparator("/a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString421411__25);
        String o_testGetPathNoEndSeparator_literalMutationString421411__26 = FilenameUtils.getPathNoEndSeparator("C:a");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__26);
        String o_testGetPathNoEndSeparator_literalMutationString421411__27 = FilenameUtils.getPathNoEndSeparator("C:a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString421411__27);
        String o_testGetPathNoEndSeparator_literalMutationString421411__28 = FilenameUtils.getPathNoEndSeparator("C:/a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString421411__28);
        String o_testGetPathNoEndSeparator_literalMutationString421411__29 = FilenameUtils.getPathNoEndSeparator("//server/a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString421411__29);
        String o_testGetPathNoEndSeparator_literalMutationString421411__30 = FilenameUtils.getPathNoEndSeparator("~/a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString421411__30);
        String o_testGetPathNoEndSeparator_literalMutationString421411__31 = FilenameUtils.getPathNoEndSeparator("~user/a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString421411__31);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString421411__1);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__2);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__3);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__4);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString421411__5);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString421411__6);
        Assert.assertEquals("a/b/c", o_testGetPathNoEndSeparator_literalMutationString421411__7);
        Assert.assertEquals("a\\b", o_testGetPathNoEndSeparator_literalMutationString421411__8);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString421411__9);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString421411__10);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString421411__11);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString421411__12);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString421411__13);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString421411__14);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__15);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__16);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__17);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__18);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__19);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__20);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__21);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__22);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString421411__23);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString421411__24);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString421411__25);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString421411__26);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString421411__27);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString421411__28);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString421411__29);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString421411__30);
    }
}

