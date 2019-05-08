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
    public void testToByteArray_InputStream_Size_add3_add51_failAssert0_add843_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                    IOUtils.toByteArray(fin, this.m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                    int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                    int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                    String String_1 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_add51 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_add51_failAssert0_add843 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
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
                String String_1 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_add51 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString40_failAssert0_literalMutationString714_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_2 = (("Wrong output size: out.length=" + (out.length)) + " does nFot exist") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString40 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString40_failAssert0_literalMutationString714 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add59_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_2 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add59 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add3_add51_failAssert0_literalMutationString430_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                    IOUtils.toByteArray(fin, this.m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                    int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                    int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                    String String_1 = (("Wrong output size: out.$length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_add51 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_add51_failAssert0_literalMutationString430 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add62_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    m_testFile.length();
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_2 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add62 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString41_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_2 = (("Wrong output size: out.length=" + (out.length)) + ")") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString41 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString40_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_2 = (("Wrong output size: out.length=" + (out.length)) + " does not exist") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString40 should have thrown IOException");
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
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    fin.available();
                    String String_2 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add63 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add61_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_2 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add61 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add64_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_2 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add64 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add60_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    m_testFile.length();
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_2 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add60 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString34_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_2 = (("OKS@Rl&{ha!&Bcvg[?i!rb0/|]6^FT" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString34 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString35_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_2 = ((" does not exist" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString35 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString33_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_2 = (("" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString33 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add2_add58_add756_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                long o_testToByteArray_InputStream_Size_add2__5 = this.m_testFile.length();
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add2_add58__11 = fin.available();
                int o_testToByteArray_InputStream_Size_add2__9 = fin.available();
                String String_3 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add2_add58_add756 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString40_failAssert0_add1009_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        m_testFile.length();
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_2 = (("Wrong output size: out.length=" + (out.length)) + " does not exist") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString40 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString40_failAssert0_add1009 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add3_literalMutationString18_add765_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                String String_1 = (("Wrong outpu} size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_literalMutationString18_add765 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add3_literalMutationString16_add781_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                String String_1 = (("" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_literalMutationString16_add781 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString40_failAssert0_add1008_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_2 = (("Wrong output size: out.length=" + (out.length)) + " does not exist") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString40 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString40_failAssert0_add1008 should have thrown IOException");
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
                String String_2 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString38_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_2 = (("Wrong ouput size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString38 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString39_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_2 = (("Wrong output size: out.length=" + (out.length)) + "") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString39 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString37_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_2 = (("Wrong output size: out.lengtOh=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString37 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString36_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                    IOUtils.toByteArray(fin, m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                    fin.available();
                    String String_2 = (("Wrowg output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString36 should have thrown IOException");
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
                String String_3 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add2_add56 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString40_failAssert0_literalMutationString712_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_2 = (("Wrong output size: out.length=" + (out.length)) + " does notgexist") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString40 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString40_failAssert0_literalMutationString712 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add3_literalMutationString15_add773_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                String String_1 = (("Wrong outpu size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_literalMutationString15_add773 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1334_add1525() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            long o_testToByteArray_InputStream_SizeIllegal_add1334__5 = this.m_testFile.length();
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1334_add1525__11 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1334_add1525__11);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1334__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1334_add1525__11);
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
    public void testToByteArray_InputStream_SizeIllegal_literalMutationNumber1323() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 2));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1323__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1323__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1335_add1535() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1335_add1535__8 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1335_add1535__8);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1335__8 = exc.getMessage().startsWith("Unexpected readed size");
            boolean o_testToByteArray_InputStream_SizeIllegal_add1335__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1335_add1535__8);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1336_add1520() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            exc.getMessage();
            boolean o_testToByteArray_InputStream_SizeIllegal_add1336_add1520__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1336_add1520__9);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1336__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1336_add1520__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1336() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            exc.getMessage();
            boolean o_testToByteArray_InputStream_SizeIllegal_add1336__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1336__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1335() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1335__8 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1335__8);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1335__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1335__10);
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1335__8);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_literalMutationNumber1323_add1553() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 2));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1323_add1553__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1323_add1553__9);
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1323__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1323_add1553__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1335_add1535_add2469() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1335_add1535__8 = exc.getMessage().startsWith("Unexpected readed size");
            boolean o_testToByteArray_InputStream_SizeIllegal_add1335__8 = exc.getMessage().startsWith("Unexpected readed size");
            boolean o_testToByteArray_InputStream_SizeIllegal_add1335_add1535_add2469__16 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1335_add1535_add2469__16);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1335__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1335_add1535_add2469__16);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1334() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            long o_testToByteArray_InputStream_SizeIllegal_add1334__5 = this.m_testFile.length();
            Assert.assertEquals(4097L, ((long) (o_testToByteArray_InputStream_SizeIllegal_add1334__5)));
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1334__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1334__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1333() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1333__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1333__10);
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

