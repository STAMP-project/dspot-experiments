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
    public void testToByteArray_InputStream_Size_add3_add51_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                String String_2 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_add51 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_3 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add68 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    m_testFile.length();
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_3 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add66 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add67_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    fin.available();
                    String String_3 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add67 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_3 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add65 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_3 = (("Wrong output size: out.length=" + (out.length)) + "(") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add63_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_3 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add63 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add64_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    m_testFile.length();
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_3 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add64 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString43_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_3 = (("" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString43 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString42_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_3 = (("*OdwpauR%h1,xavU[1Rvnj|}8wu]&8" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString42 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString45_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_3 = ((" does not exist" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString45 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString44_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_3 = (("Wrong output size:C out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString44 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                IOUtils.toByteArray(fin, m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                fin.available();
                String String_3 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString49_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_3 = (("Wrong output size: out.length=" + (out.length)) + " does not exist") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString49 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString48_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_3 = (("Wrong output size: out.length=" + (out.length)) + "") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString48 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString47_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_3 = (("Wrong output size: outlength=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString47 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString46_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_3 = (("Wrong output size: out.len1th=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString46 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add2_add56_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                long o_testToByteArray_InputStream_Size_add2__5 = this.m_testFile.length();
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add2__9 = fin.available();
                String String_4 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add2_add56 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add223_add450() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add223_add450__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add223_add450__10);
            boolean o_testToByteArray_InputStream_SizeIllegal_add223__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add223_add450__10);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add224() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            long o_testToByteArray_InputStream_SizeIllegal_add224__5 = this.m_testFile.length();
            Assert.assertEquals(4097L, ((long) (o_testToByteArray_InputStream_SizeIllegal_add224__5)));
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add224__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add224__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add224_add455() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            long o_testToByteArray_InputStream_SizeIllegal_add224__5 = this.m_testFile.length();
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add224_add455__11 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add224_add455__11);
            boolean o_testToByteArray_InputStream_SizeIllegal_add224__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add224_add455__11);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add225() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add225__8 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add225__8);
            boolean o_testToByteArray_InputStream_SizeIllegal_add225__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add225__10);
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add225__8);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add223() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add223__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add223__10);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add226_add438() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            exc.getMessage();
            boolean o_testToByteArray_InputStream_SizeIllegal_add226_add438__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add226_add438__9);
            boolean o_testToByteArray_InputStream_SizeIllegal_add226__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add226_add438__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_literalMutationNumber213_add425() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 2));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber213_add425__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber213_add425__9);
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber213__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber213_add425__9);
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

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add226() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            exc.getMessage();
            boolean o_testToByteArray_InputStream_SizeIllegal_add226__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add226__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_literalMutationNumber213() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 2));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber213__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber213__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add225_add444() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add225__8 = exc.getMessage().startsWith("Unexpected readed size");
            boolean o_testToByteArray_InputStream_SizeIllegal_add225_add444__12 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add225_add444__12);
            boolean o_testToByteArray_InputStream_SizeIllegal_add225__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add225_add444__12);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add225_add442() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add225_add442__8 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add225_add442__8);
            boolean o_testToByteArray_InputStream_SizeIllegal_add225__8 = exc.getMessage().startsWith("Unexpected readed size");
            boolean o_testToByteArray_InputStream_SizeIllegal_add225__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add225_add442__8);
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

