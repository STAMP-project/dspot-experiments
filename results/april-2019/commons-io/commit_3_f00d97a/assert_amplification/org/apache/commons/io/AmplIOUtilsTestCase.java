package org.apache.commons.io;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import org.apache.commons.io.testtools.FileBasedTestCase;
import org.apache.commons.io.testtools.TestUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("deprecation")
public class AmplIOUtilsTestCase extends FileBasedTestCase {
    private static final int FILE_SIZE = (1024 * 4) + 1;

    private static final boolean WINDOWS = (File.separatorChar) == '\\';

    private char[] carr = null;

    private byte[] iarr = null;

    private File m_testFile;

    private void assertEqualContent(final byte[] b0, final byte[] b1) {
        Assert.assertTrue("Content not equal according to java.util.Arrays#equals()", Arrays.equals(b0, b1));
    }

    @Before
    public void setUp() {
        try {
            FileBasedTestCase.getTestDirectory().mkdirs();
            m_testFile = new File(FileBasedTestCase.getTestDirectory(), "file2-test.txt");
            if (!(m_testFile.getParentFile().exists())) {
                throw new IOException((("Cannot create file " + (m_testFile)) + " as the parent directory does not exist"));
            }
            final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(m_testFile));
            try {
                TestUtils.generateTestData(output, ((long) (AmplIOUtilsTestCase.FILE_SIZE)));
            } finally {
                IOUtils.closeQuietly(output);
            }
        } catch (final IOException ioe) {
            throw new RuntimeException(("Can't run this test because the environment could not be built: " + (ioe.getMessage())));
        }
        iarr = new byte[200];
        Arrays.fill(iarr, ((byte) (-1)));
        for (int i = 0; i < 80; i++) {
            iarr[i] = ((byte) (i));
        }
        carr = new char[200];
        Arrays.fill(carr, ((char) (-1)));
        for (int i = 0; i < 80; i++) {
            carr[i] = ((char) (i));
        }
    }

    @After
    public void tearDown() {
        carr = null;
        iarr = null;
        try {
            FileUtils.deleteDirectory(FileBasedTestCase.getTestDirectory());
        } catch (final IOException e) {
            throw new RuntimeException(((("Could not clear up " + (FileBasedTestCase.getTestDirectory())) + ": ") + e));
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal__8 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal__8);
        }
    }

    private void testToString_URI(final String encoding) throws Exception {
        final URI uri = m_testFile.toURI();
        final String out = IOUtils.toString(uri, encoding);
        Assert.assertNotNull(out);
        Assert.assertEquals("Wrong output size", AmplIOUtilsTestCase.FILE_SIZE, out.length());
    }

    private void testToString_URL(final String encoding) throws Exception {
        final URL url = m_testFile.toURI().toURL();
        final String out = IOUtils.toString(url, encoding);
        Assert.assertNotNull(out);
        Assert.assertEquals("Wrong output size", AmplIOUtilsTestCase.FILE_SIZE, out.length());
    }
}

