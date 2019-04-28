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
    public void testGetPath_literalMutationString1751() throws Exception {
        String o_testGetPath_literalMutationString1751__1 = FilenameUtils.getPath(null);
        Assert.assertNull(o_testGetPath_literalMutationString1751__1);
        String o_testGetPath_literalMutationString1751__2 = FilenameUtils.getPath("noseperator.inthispath");
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__2);
        String o_testGetPath_literalMutationString1751__3 = FilenameUtils.getPath("/noseperator.inthispath");
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__3);
        String o_testGetPath_literalMutationString1751__4 = FilenameUtils.getPath("\\noseperator.inthispath");
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__4);
        String o_testGetPath_literalMutationString1751__5 = FilenameUtils.getPath("a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1751__5);
        String o_testGetPath_literalMutationString1751__6 = FilenameUtils.getPath("a/b/c");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1751__6);
        String o_testGetPath_literalMutationString1751__7 = FilenameUtils.getPath("a/b/c/");
        Assert.assertEquals("a/b/c/", o_testGetPath_literalMutationString1751__7);
        String o_testGetPath_literalMutationString1751__8 = FilenameUtils.getPath("a\\b\\c");
        Assert.assertEquals("a\\b\\", o_testGetPath_literalMutationString1751__8);
        String o_testGetPath_literalMutationString1751__9 = FilenameUtils.getPath(":");
        Assert.assertNull(o_testGetPath_literalMutationString1751__9);
        String o_testGetPath_literalMutationString1751__10 = FilenameUtils.getPath("1:/a/b/c.txt");
        Assert.assertNull(o_testGetPath_literalMutationString1751__10);
        String o_testGetPath_literalMutationString1751__11 = FilenameUtils.getPath("1:");
        Assert.assertNull(o_testGetPath_literalMutationString1751__11);
        String o_testGetPath_literalMutationString1751__12 = FilenameUtils.getPath("1:a");
        Assert.assertNull(o_testGetPath_literalMutationString1751__12);
        String o_testGetPath_literalMutationString1751__13 = FilenameUtils.getPath("///a/b/c.txt");
        Assert.assertNull(o_testGetPath_literalMutationString1751__13);
        String o_testGetPath_literalMutationString1751__14 = FilenameUtils.getPath("//a");
        Assert.assertNull(o_testGetPath_literalMutationString1751__14);
        String o_testGetPath_literalMutationString1751__15 = FilenameUtils.getPath("");
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__15);
        String o_testGetPath_literalMutationString1751__16 = FilenameUtils.getPath("C:");
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__16);
        String o_testGetPath_literalMutationString1751__17 = FilenameUtils.getPath("C:/");
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__17);
        String o_testGetPath_literalMutationString1751__18 = FilenameUtils.getPath("//server/");
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__18);
        String o_testGetPath_literalMutationString1751__19 = FilenameUtils.getPath("~");
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__19);
        String o_testGetPath_literalMutationString1751__20 = FilenameUtils.getPath("~/");
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__20);
        String o_testGetPath_literalMutationString1751__21 = FilenameUtils.getPath("~user");
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__21);
        String o_testGetPath_literalMutationString1751__22 = FilenameUtils.getPath("~user/");
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__22);
        String o_testGetPath_literalMutationString1751__23 = FilenameUtils.getPath("a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1751__23);
        String o_testGetPath_literalMutationString1751__24 = FilenameUtils.getPath("a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1751__24);
        String o_testGetPath_literalMutationString1751__25 = FilenameUtils.getPath("/a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1751__25);
        String o_testGetPath_literalMutationString1751__26 = FilenameUtils.getPath("C:a");
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__26);
        String o_testGetPath_literalMutationString1751__27 = FilenameUtils.getPath("C:a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1751__27);
        String o_testGetPath_literalMutationString1751__28 = FilenameUtils.getPath("C:/a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1751__28);
        String o_testGetPath_literalMutationString1751__29 = FilenameUtils.getPath("//%server/a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1751__29);
        String o_testGetPath_literalMutationString1751__30 = FilenameUtils.getPath("~/a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1751__30);
        String o_testGetPath_literalMutationString1751__31 = FilenameUtils.getPath("~user/a/b/c.txt");
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1751__31);
        Assert.assertNull(o_testGetPath_literalMutationString1751__1);
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__2);
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__3);
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__4);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1751__5);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1751__6);
        Assert.assertEquals("a/b/c/", o_testGetPath_literalMutationString1751__7);
        Assert.assertEquals("a\\b\\", o_testGetPath_literalMutationString1751__8);
        Assert.assertNull(o_testGetPath_literalMutationString1751__9);
        Assert.assertNull(o_testGetPath_literalMutationString1751__10);
        Assert.assertNull(o_testGetPath_literalMutationString1751__11);
        Assert.assertNull(o_testGetPath_literalMutationString1751__12);
        Assert.assertNull(o_testGetPath_literalMutationString1751__13);
        Assert.assertNull(o_testGetPath_literalMutationString1751__14);
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__15);
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__16);
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__17);
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__18);
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__19);
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__20);
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__21);
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__22);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1751__23);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1751__24);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1751__25);
        Assert.assertEquals("", o_testGetPath_literalMutationString1751__26);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1751__27);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1751__28);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1751__29);
        Assert.assertEquals("a/b/", o_testGetPath_literalMutationString1751__30);
    }

    @Test(timeout = 10000)
    public void testGetPathNoEndSeparator_literalMutationString4936() throws Exception {
        String o_testGetPathNoEndSeparator_literalMutationString4936__1 = FilenameUtils.getPath(null);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString4936__1);
        String o_testGetPathNoEndSeparator_literalMutationString4936__2 = FilenameUtils.getPath("noseperator.inthispath");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__2);
        String o_testGetPathNoEndSeparator_literalMutationString4936__3 = FilenameUtils.getPathNoEndSeparator("/noseperator.inthispath");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__3);
        String o_testGetPathNoEndSeparator_literalMutationString4936__4 = FilenameUtils.getPathNoEndSeparator("\\noseperator.inthispath");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__4);
        String o_testGetPathNoEndSeparator_literalMutationString4936__5 = FilenameUtils.getPathNoEndSeparator("a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString4936__5);
        String o_testGetPathNoEndSeparator_literalMutationString4936__6 = FilenameUtils.getPathNoEndSeparator("a/b/c");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString4936__6);
        String o_testGetPathNoEndSeparator_literalMutationString4936__7 = FilenameUtils.getPathNoEndSeparator("a/b/c/");
        Assert.assertEquals("a/b/c", o_testGetPathNoEndSeparator_literalMutationString4936__7);
        String o_testGetPathNoEndSeparator_literalMutationString4936__8 = FilenameUtils.getPathNoEndSeparator("a\\b\\c");
        Assert.assertEquals("a\\b", o_testGetPathNoEndSeparator_literalMutationString4936__8);
        String o_testGetPathNoEndSeparator_literalMutationString4936__9 = FilenameUtils.getPathNoEndSeparator(":");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString4936__9);
        String o_testGetPathNoEndSeparator_literalMutationString4936__10 = FilenameUtils.getPathNoEndSeparator("1:/a/b/c.txt");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString4936__10);
        String o_testGetPathNoEndSeparator_literalMutationString4936__11 = FilenameUtils.getPathNoEndSeparator("1:");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString4936__11);
        String o_testGetPathNoEndSeparator_literalMutationString4936__12 = FilenameUtils.getPathNoEndSeparator("1:a");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString4936__12);
        String o_testGetPathNoEndSeparator_literalMutationString4936__13 = FilenameUtils.getPathNoEndSeparator("//^/a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString4936__13);
        String o_testGetPathNoEndSeparator_literalMutationString4936__14 = FilenameUtils.getPathNoEndSeparator("//a");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString4936__14);
        String o_testGetPathNoEndSeparator_literalMutationString4936__15 = FilenameUtils.getPathNoEndSeparator("");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__15);
        String o_testGetPathNoEndSeparator_literalMutationString4936__16 = FilenameUtils.getPathNoEndSeparator("C:");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__16);
        String o_testGetPathNoEndSeparator_literalMutationString4936__17 = FilenameUtils.getPathNoEndSeparator("C:/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__17);
        String o_testGetPathNoEndSeparator_literalMutationString4936__18 = FilenameUtils.getPathNoEndSeparator("//server/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__18);
        String o_testGetPathNoEndSeparator_literalMutationString4936__19 = FilenameUtils.getPathNoEndSeparator("~");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__19);
        String o_testGetPathNoEndSeparator_literalMutationString4936__20 = FilenameUtils.getPathNoEndSeparator("~/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__20);
        String o_testGetPathNoEndSeparator_literalMutationString4936__21 = FilenameUtils.getPathNoEndSeparator("~user");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__21);
        String o_testGetPathNoEndSeparator_literalMutationString4936__22 = FilenameUtils.getPathNoEndSeparator("~user/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__22);
        String o_testGetPathNoEndSeparator_literalMutationString4936__23 = FilenameUtils.getPathNoEndSeparator("a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString4936__23);
        String o_testGetPathNoEndSeparator_literalMutationString4936__24 = FilenameUtils.getPathNoEndSeparator("a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString4936__24);
        String o_testGetPathNoEndSeparator_literalMutationString4936__25 = FilenameUtils.getPathNoEndSeparator("/a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString4936__25);
        String o_testGetPathNoEndSeparator_literalMutationString4936__26 = FilenameUtils.getPathNoEndSeparator("C:a");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__26);
        String o_testGetPathNoEndSeparator_literalMutationString4936__27 = FilenameUtils.getPathNoEndSeparator("C:a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString4936__27);
        String o_testGetPathNoEndSeparator_literalMutationString4936__28 = FilenameUtils.getPathNoEndSeparator("C:/a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString4936__28);
        String o_testGetPathNoEndSeparator_literalMutationString4936__29 = FilenameUtils.getPathNoEndSeparator("//server/a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString4936__29);
        String o_testGetPathNoEndSeparator_literalMutationString4936__30 = FilenameUtils.getPathNoEndSeparator("~/a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString4936__30);
        String o_testGetPathNoEndSeparator_literalMutationString4936__31 = FilenameUtils.getPathNoEndSeparator("~user/a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString4936__31);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString4936__1);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__2);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__3);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__4);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString4936__5);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString4936__6);
        Assert.assertEquals("a/b/c", o_testGetPathNoEndSeparator_literalMutationString4936__7);
        Assert.assertEquals("a\\b", o_testGetPathNoEndSeparator_literalMutationString4936__8);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString4936__9);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString4936__10);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString4936__11);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString4936__12);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString4936__13);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString4936__14);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__15);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__16);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__17);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__18);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__19);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__20);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__21);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__22);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString4936__23);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString4936__24);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString4936__25);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString4936__26);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString4936__27);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString4936__28);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString4936__29);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString4936__30);
    }

    @Test(timeout = 10000)
    public void testGetPathNoEndSeparator_literalMutationString5013() throws Exception {
        String o_testGetPathNoEndSeparator_literalMutationString5013__1 = FilenameUtils.getPath(null);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString5013__1);
        String o_testGetPathNoEndSeparator_literalMutationString5013__2 = FilenameUtils.getPath("noseperator.inthispath");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__2);
        String o_testGetPathNoEndSeparator_literalMutationString5013__3 = FilenameUtils.getPathNoEndSeparator("/noseperator.inthispath");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__3);
        String o_testGetPathNoEndSeparator_literalMutationString5013__4 = FilenameUtils.getPathNoEndSeparator("\\noseperator.inthispath");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__4);
        String o_testGetPathNoEndSeparator_literalMutationString5013__5 = FilenameUtils.getPathNoEndSeparator("a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString5013__5);
        String o_testGetPathNoEndSeparator_literalMutationString5013__6 = FilenameUtils.getPathNoEndSeparator("a/b/c");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString5013__6);
        String o_testGetPathNoEndSeparator_literalMutationString5013__7 = FilenameUtils.getPathNoEndSeparator("a/b/c/");
        Assert.assertEquals("a/b/c", o_testGetPathNoEndSeparator_literalMutationString5013__7);
        String o_testGetPathNoEndSeparator_literalMutationString5013__8 = FilenameUtils.getPathNoEndSeparator("a\\b\\c");
        Assert.assertEquals("a\\b", o_testGetPathNoEndSeparator_literalMutationString5013__8);
        String o_testGetPathNoEndSeparator_literalMutationString5013__9 = FilenameUtils.getPathNoEndSeparator(":");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString5013__9);
        String o_testGetPathNoEndSeparator_literalMutationString5013__10 = FilenameUtils.getPathNoEndSeparator("1:/a/b/c.txt");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString5013__10);
        String o_testGetPathNoEndSeparator_literalMutationString5013__11 = FilenameUtils.getPathNoEndSeparator("1:");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString5013__11);
        String o_testGetPathNoEndSeparator_literalMutationString5013__12 = FilenameUtils.getPathNoEndSeparator("1:a");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString5013__12);
        String o_testGetPathNoEndSeparator_literalMutationString5013__13 = FilenameUtils.getPathNoEndSeparator("///a/b/c.txt");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString5013__13);
        String o_testGetPathNoEndSeparator_literalMutationString5013__14 = FilenameUtils.getPathNoEndSeparator("//a");
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString5013__14);
        String o_testGetPathNoEndSeparator_literalMutationString5013__15 = FilenameUtils.getPathNoEndSeparator("");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__15);
        String o_testGetPathNoEndSeparator_literalMutationString5013__16 = FilenameUtils.getPathNoEndSeparator("C:");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__16);
        String o_testGetPathNoEndSeparator_literalMutationString5013__17 = FilenameUtils.getPathNoEndSeparator("C:/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__17);
        String o_testGetPathNoEndSeparator_literalMutationString5013__18 = FilenameUtils.getPathNoEndSeparator("//server/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__18);
        String o_testGetPathNoEndSeparator_literalMutationString5013__19 = FilenameUtils.getPathNoEndSeparator("~");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__19);
        String o_testGetPathNoEndSeparator_literalMutationString5013__20 = FilenameUtils.getPathNoEndSeparator("~/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__20);
        String o_testGetPathNoEndSeparator_literalMutationString5013__21 = FilenameUtils.getPathNoEndSeparator("~user");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__21);
        String o_testGetPathNoEndSeparator_literalMutationString5013__22 = FilenameUtils.getPathNoEndSeparator("~user/");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__22);
        String o_testGetPathNoEndSeparator_literalMutationString5013__23 = FilenameUtils.getPathNoEndSeparator("a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString5013__23);
        String o_testGetPathNoEndSeparator_literalMutationString5013__24 = FilenameUtils.getPathNoEndSeparator("a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString5013__24);
        String o_testGetPathNoEndSeparator_literalMutationString5013__25 = FilenameUtils.getPathNoEndSeparator("/a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString5013__25);
        String o_testGetPathNoEndSeparator_literalMutationString5013__26 = FilenameUtils.getPathNoEndSeparator("C:a");
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__26);
        String o_testGetPathNoEndSeparator_literalMutationString5013__27 = FilenameUtils.getPathNoEndSeparator("C:a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString5013__27);
        String o_testGetPathNoEndSeparator_literalMutationString5013__28 = FilenameUtils.getPathNoEndSeparator("C:/a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString5013__28);
        String o_testGetPathNoEndSeparator_literalMutationString5013__29 = FilenameUtils.getPathNoEndSeparator("//#erver/a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString5013__29);
        String o_testGetPathNoEndSeparator_literalMutationString5013__30 = FilenameUtils.getPathNoEndSeparator("~/a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString5013__30);
        String o_testGetPathNoEndSeparator_literalMutationString5013__31 = FilenameUtils.getPathNoEndSeparator("~user/a/b/c.txt");
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString5013__31);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString5013__1);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__2);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__3);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__4);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString5013__5);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString5013__6);
        Assert.assertEquals("a/b/c", o_testGetPathNoEndSeparator_literalMutationString5013__7);
        Assert.assertEquals("a\\b", o_testGetPathNoEndSeparator_literalMutationString5013__8);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString5013__9);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString5013__10);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString5013__11);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString5013__12);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString5013__13);
        Assert.assertNull(o_testGetPathNoEndSeparator_literalMutationString5013__14);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__15);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__16);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__17);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__18);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__19);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__20);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__21);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__22);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString5013__23);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString5013__24);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString5013__25);
        Assert.assertEquals("", o_testGetPathNoEndSeparator_literalMutationString5013__26);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString5013__27);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString5013__28);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString5013__29);
        Assert.assertEquals("a/b", o_testGetPathNoEndSeparator_literalMutationString5013__30);
    }
}

