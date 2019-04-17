package org.apache.commons.io;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.testtools.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


@SuppressWarnings({ "deprecation", "ResultOfMethodCallIgnored" })
public class AmplFileUtilsTestCase {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File getTestDirectory() {
        return temporaryFolder.getRoot();
    }

    private static final int TEST_DIRECTORY_SIZE = 0;

    private static final BigInteger TEST_DIRECTORY_SIZE_BI = BigInteger.ZERO;

    private static final BigInteger TEST_DIRECTORY_SIZE_GT_ZERO_BI = BigInteger.valueOf(100);

    private static final AmplFileUtilsTestCase.ListDirectoryWalker LIST_WALKER = new AmplFileUtilsTestCase.ListDirectoryWalker();

    private File testFile1;

    private File testFile2;

    private int testFile1Size;

    private int testFile2Size;

    @Before
    public void setUp() throws Exception {
        testFile1 = new File(getTestDirectory(), "file1-test.txt");
        testFile2 = new File(getTestDirectory(), "file1a-test.txt");
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
        FileUtils.deleteDirectory(getTestDirectory());
        getTestDirectory().mkdirs();
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

    private String getName() {
        return this.getClass().getSimpleName();
    }

    void openOutputStream_noParent(final boolean createFile) throws Exception {
        final File file = new File("test.txt");
        Assert.assertNull(file.getParentFile());
        try {
            if (createFile) {
                TestUtils.createLineBasedFile(file, new String[]{ "Hello" });
            }
            try (final FileOutputStream out = FileUtils.openOutputStream(file)) {
                out.write(0);
            }
            Assert.assertTrue(file.exists());
        } finally {
            if (!(file.delete())) {
                file.deleteOnExit();
            }
        }
    }

    @Test(timeout = 10000)
    public void testCopyURLToFile_literalMutationString43115_add43308_add44979() throws Exception {
        final File file = new File(getTestDirectory(), getName());
        file.deleteOnExit();
        final String resourceName = "";
        Assert.assertEquals("", resourceName);
        URL o_testCopyURLToFile_literalMutationString43115_add43308_add44979__9 = getClass().getResource(resourceName);
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/commons-io_parent/target/test-classes/org/apache/commons/io/", ((URL) (o_testCopyURLToFile_literalMutationString43115_add43308_add44979__9)).toString());
        Assert.assertEquals("", ((URL) (o_testCopyURLToFile_literalMutationString43115_add43308_add44979__9)).getAuthority());
        Assert.assertNull(((URL) (o_testCopyURLToFile_literalMutationString43115_add43308_add44979__9)).getQuery());
        Assert.assertEquals("file", ((URL) (o_testCopyURLToFile_literalMutationString43115_add43308_add44979__9)).getProtocol());
        Assert.assertEquals("", ((URL) (o_testCopyURLToFile_literalMutationString43115_add43308_add44979__9)).getHost());
        Assert.assertNull(((URL) (o_testCopyURLToFile_literalMutationString43115_add43308_add44979__9)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (o_testCopyURLToFile_literalMutationString43115_add43308_add44979__9)).getPort())));
        Assert.assertEquals(-1, ((int) (((URL) (o_testCopyURLToFile_literalMutationString43115_add43308_add44979__9)).getDefaultPort())));
        Assert.assertNull(((URL) (o_testCopyURLToFile_literalMutationString43115_add43308_add44979__9)).getRef());
        FileUtils.copyURLToFile(getClass().getResource(resourceName), file);
        FileUtils.copyURLToFile(getClass().getResource(resourceName), file);
        try (final FileInputStream fis = new FileInputStream(file)) {
            boolean o_testCopyURLToFile_literalMutationString43115__16 = IOUtils.contentEquals(getClass().getResourceAsStream(resourceName), fis);
        }

        Assert.assertEquals("", resourceName);
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/commons-io_parent/target/test-classes/org/apache/commons/io/", ((URL) (o_testCopyURLToFile_literalMutationString43115_add43308_add44979__9)).toString());
        Assert.assertEquals("", ((URL) (o_testCopyURLToFile_literalMutationString43115_add43308_add44979__9)).getAuthority());
        Assert.assertNull(((URL) (o_testCopyURLToFile_literalMutationString43115_add43308_add44979__9)).getQuery());
        Assert.assertEquals("file", ((URL) (o_testCopyURLToFile_literalMutationString43115_add43308_add44979__9)).getProtocol());
        Assert.assertEquals("", ((URL) (o_testCopyURLToFile_literalMutationString43115_add43308_add44979__9)).getHost());
        Assert.assertNull(((URL) (o_testCopyURLToFile_literalMutationString43115_add43308_add44979__9)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (o_testCopyURLToFile_literalMutationString43115_add43308_add44979__9)).getPort())));
        Assert.assertEquals(-1, ((int) (((URL) (o_testCopyURLToFile_literalMutationString43115_add43308_add44979__9)).getDefaultPort())));
        Assert.assertNull(((URL) (o_testCopyURLToFile_literalMutationString43115_add43308_add44979__9)).getRef());
    }

    @Test(timeout = 10000)
    public void testCopyURLToFile_add43122_literalMutationString43236_add44837() throws Exception {
        final File file = new File(getTestDirectory(), getName());
        file.deleteOnExit();
        file.deleteOnExit();
        final String resourceName = "";
        Assert.assertEquals("", resourceName);
        URL o_testCopyURLToFile_add43122_literalMutationString43236_add44837__10 = getClass().getResource(resourceName);
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/commons-io_parent/target/test-classes/org/apache/commons/io/", ((URL) (o_testCopyURLToFile_add43122_literalMutationString43236_add44837__10)).toString());
        Assert.assertEquals("", ((URL) (o_testCopyURLToFile_add43122_literalMutationString43236_add44837__10)).getAuthority());
        Assert.assertNull(((URL) (o_testCopyURLToFile_add43122_literalMutationString43236_add44837__10)).getQuery());
        Assert.assertEquals("file", ((URL) (o_testCopyURLToFile_add43122_literalMutationString43236_add44837__10)).getProtocol());
        Assert.assertEquals("", ((URL) (o_testCopyURLToFile_add43122_literalMutationString43236_add44837__10)).getHost());
        Assert.assertNull(((URL) (o_testCopyURLToFile_add43122_literalMutationString43236_add44837__10)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (o_testCopyURLToFile_add43122_literalMutationString43236_add44837__10)).getPort())));
        Assert.assertEquals(-1, ((int) (((URL) (o_testCopyURLToFile_add43122_literalMutationString43236_add44837__10)).getDefaultPort())));
        Assert.assertNull(((URL) (o_testCopyURLToFile_add43122_literalMutationString43236_add44837__10)).getRef());
        FileUtils.copyURLToFile(getClass().getResource(resourceName), file);
        try (final FileInputStream fis = new FileInputStream(file)) {
            boolean o_testCopyURLToFile_add43122__17 = IOUtils.contentEquals(getClass().getResourceAsStream(resourceName), fis);
        }

        Assert.assertEquals("", resourceName);
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/commons-io_parent/target/test-classes/org/apache/commons/io/", ((URL) (o_testCopyURLToFile_add43122_literalMutationString43236_add44837__10)).toString());
        Assert.assertEquals("", ((URL) (o_testCopyURLToFile_add43122_literalMutationString43236_add44837__10)).getAuthority());
        Assert.assertNull(((URL) (o_testCopyURLToFile_add43122_literalMutationString43236_add44837__10)).getQuery());
        Assert.assertEquals("file", ((URL) (o_testCopyURLToFile_add43122_literalMutationString43236_add44837__10)).getProtocol());
        Assert.assertEquals("", ((URL) (o_testCopyURLToFile_add43122_literalMutationString43236_add44837__10)).getHost());
        Assert.assertNull(((URL) (o_testCopyURLToFile_add43122_literalMutationString43236_add44837__10)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (o_testCopyURLToFile_add43122_literalMutationString43236_add44837__10)).getPort())));
        Assert.assertEquals(-1, ((int) (((URL) (o_testCopyURLToFile_add43122_literalMutationString43236_add44837__10)).getDefaultPort())));
        Assert.assertNull(((URL) (o_testCopyURLToFile_add43122_literalMutationString43236_add44837__10)).getRef());
    }

    @Test(timeout = 10000)
    public void testCopyURLToFileWithTimeout_literalMutationNumber36481_literalMutationString36702_add41139() throws Exception {
        final File file = new File(getTestDirectory(), "testCopyURLToFileWithTimeout");
        file.deleteOnExit();
        final String resourceName = "";
        Assert.assertEquals("", resourceName);
        URL o_testCopyURLToFileWithTimeout_literalMutationNumber36481_literalMutationString36702_add41139__8 = getClass().getResource(resourceName);
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/commons-io_parent/target/test-classes/org/apache/commons/io/", ((URL) (o_testCopyURLToFileWithTimeout_literalMutationNumber36481_literalMutationString36702_add41139__8)).toString());
        Assert.assertEquals("", ((URL) (o_testCopyURLToFileWithTimeout_literalMutationNumber36481_literalMutationString36702_add41139__8)).getAuthority());
        Assert.assertNull(((URL) (o_testCopyURLToFileWithTimeout_literalMutationNumber36481_literalMutationString36702_add41139__8)).getQuery());
        Assert.assertEquals("file", ((URL) (o_testCopyURLToFileWithTimeout_literalMutationNumber36481_literalMutationString36702_add41139__8)).getProtocol());
        Assert.assertEquals("", ((URL) (o_testCopyURLToFileWithTimeout_literalMutationNumber36481_literalMutationString36702_add41139__8)).getHost());
        Assert.assertNull(((URL) (o_testCopyURLToFileWithTimeout_literalMutationNumber36481_literalMutationString36702_add41139__8)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (o_testCopyURLToFileWithTimeout_literalMutationNumber36481_literalMutationString36702_add41139__8)).getPort())));
        Assert.assertEquals(-1, ((int) (((URL) (o_testCopyURLToFileWithTimeout_literalMutationNumber36481_literalMutationString36702_add41139__8)).getDefaultPort())));
        Assert.assertNull(((URL) (o_testCopyURLToFileWithTimeout_literalMutationNumber36481_literalMutationString36702_add41139__8)).getRef());
        FileUtils.copyURLToFile(getClass().getResource(resourceName), file, 500, 0);
        try (final FileInputStream fis = new FileInputStream(file)) {
            boolean o_testCopyURLToFileWithTimeout_literalMutationNumber36481__16 = IOUtils.contentEquals(getClass().getResourceAsStream(resourceName), fis);
        }

        Assert.assertEquals("", resourceName);
        Assert.assertEquals("file:/tmp/dspot-experiments/dataset/april-2019/commons-io_parent/target/test-classes/org/apache/commons/io/", ((URL) (o_testCopyURLToFileWithTimeout_literalMutationNumber36481_literalMutationString36702_add41139__8)).toString());
        Assert.assertEquals("", ((URL) (o_testCopyURLToFileWithTimeout_literalMutationNumber36481_literalMutationString36702_add41139__8)).getAuthority());
        Assert.assertNull(((URL) (o_testCopyURLToFileWithTimeout_literalMutationNumber36481_literalMutationString36702_add41139__8)).getQuery());
        Assert.assertEquals("file", ((URL) (o_testCopyURLToFileWithTimeout_literalMutationNumber36481_literalMutationString36702_add41139__8)).getProtocol());
        Assert.assertEquals("", ((URL) (o_testCopyURLToFileWithTimeout_literalMutationNumber36481_literalMutationString36702_add41139__8)).getHost());
        Assert.assertNull(((URL) (o_testCopyURLToFileWithTimeout_literalMutationNumber36481_literalMutationString36702_add41139__8)).getUserInfo());
        Assert.assertEquals(-1, ((int) (((URL) (o_testCopyURLToFileWithTimeout_literalMutationNumber36481_literalMutationString36702_add41139__8)).getPort())));
        Assert.assertEquals(-1, ((int) (((URL) (o_testCopyURLToFileWithTimeout_literalMutationNumber36481_literalMutationString36702_add41139__8)).getDefaultPort())));
        Assert.assertNull(((URL) (o_testCopyURLToFileWithTimeout_literalMutationNumber36481_literalMutationString36702_add41139__8)).getRef());
    }

    private void createCircularSymLink(final File file) throws IOException {
        if (!(FilenameUtils.isSystemWindows())) {
            Runtime.getRuntime().exec((((("ln -s " + file) + "/.. ") + file) + "/cycle"));
        } else {
            try {
                Runtime.getRuntime().exec((((("mklink /D " + file) + "/cycle") + file) + "/.. "));
            } catch (final IOException ioe) {


            }
        }
    }

    @Test
    @Ignore
    public void testCopyFileLarge() throws Exception {
        final File largeFile = new File(getTestDirectory(), "large.txt");
        final File destination = new File(getTestDirectory(), "copylarge.txt");
        System.out.println(("START:   " + (new Date())));
        if (!(largeFile.getParentFile().exists())) {
            throw new IOException((("Cannot create file " + largeFile) + " as the parent directory does not exist"));
        }
        final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(largeFile));
        try {
            TestUtils.generateTestData(output, FileUtils.ONE_GB);
        } finally {
            IOUtils.closeQuietly(output);
        }
        System.out.println(("CREATED: " + (new Date())));
        FileUtils.copyFile(largeFile, destination);
        System.out.println(("COPIED:  " + (new Date())));
        Assert.assertTrue("Check Exist", destination.exists());
        Assert.assertEquals("Check Full copy", largeFile.length(), destination.length());
    }

    private void createFilesForTestCopyDirectory(final File grandParentDir, final File parentDir, final File childDir) throws Exception {
        final File childDir2 = new File(parentDir, "child2");
        final File grandChildDir = new File(childDir, "grandChild");
        final File grandChild2Dir = new File(childDir2, "grandChild2");
        final File file1 = new File(grandParentDir, "file1.txt");
        final File file2 = new File(parentDir, "file2.txt");
        final File file3 = new File(childDir, "file3.txt");
        final File file4 = new File(childDir2, "file4.txt");
        final File file5 = new File(grandChildDir, "file5.txt");
        final File file6 = new File(grandChild2Dir, "file6.txt");
        FileUtils.deleteDirectory(grandParentDir);
        grandChildDir.mkdirs();
        grandChild2Dir.mkdirs();
        FileUtils.writeStringToFile(file1, "File 1 in grandparent", "UTF8");
        FileUtils.writeStringToFile(file2, "File 2 in parent", "UTF8");
        FileUtils.writeStringToFile(file3, "File 3 in child", "UTF8");
        FileUtils.writeStringToFile(file4, "File 4 in child2", "UTF8");
        FileUtils.writeStringToFile(file5, "File 5 in grandChild", "UTF8");
        FileUtils.writeStringToFile(file6, "File 6 in grandChild2", "UTF8");
    }

    private static class ShorterFile extends File {
        private static final long serialVersionUID = 1L;

        public ShorterFile(final String pathname) {
            super(pathname);
        }

        @Override
        public long length() {
            return (super.length()) - 1;
        }
    }

    static class ListDirectoryWalker extends DirectoryWalker<File> {
        ListDirectoryWalker() {
            super();
        }

        List<File> list(final File startDirectory) throws IOException {
            final ArrayList<File> files = new ArrayList<>();
            walk(startDirectory, files);
            return files;
        }

        @Override
        protected void handleDirectoryStart(final File directory, final int depth, final Collection<File> results) throws IOException {
            if (depth > 0) {
                results.add(directory);
            }
        }

        @Override
        protected void handleFile(final File file, final int depth, final Collection<File> results) throws IOException {
            results.add(file);
        }
    }
}

