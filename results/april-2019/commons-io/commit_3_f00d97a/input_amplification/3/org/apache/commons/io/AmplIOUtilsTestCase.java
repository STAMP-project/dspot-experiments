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
    public void testToByteArray_InputStream_Size_add1_failAssert0_add67_failAssert0_literalMutationString557_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        fin.available();
                        String String_3 = (("Wrong output size: our.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add67 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add67_failAssert0_literalMutationString557 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add63_failAssert0_add903_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        m_testFile.length();
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add63 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add63_failAssert0_add903 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString45_failAssert0_add984_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = ((" does not exist" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString45 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString45_failAssert0_add984 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add1_failAssert0_add63_failAssert0null1060_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(null, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add63 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add63_failAssert0null1060 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add3_add51_failAssert0_literalMutationString469_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                    IOUtils.toByteArray(fin, this.m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                    int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                    int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                    String String_2 = (("Wsrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_add51 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_add51_failAssert0_literalMutationString469 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0_add898_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        m_testFile.length();
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add66 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0_add898 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add64_failAssert0_add912_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        m_testFile.length();
                        IOUtils.toByteArray(fin, m_testFile.length());
                        m_testFile.length();
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add64 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add64_failAssert0_add912 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add63_failAssert0_literalMutationString536_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + " does not exist") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add63 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add63_failAssert0_literalMutationString536 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0_add896_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        m_testFile.length();
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add66 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0_add896 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_add1022_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "(") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_add1022 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0_literalMutationString547_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("FwF=EcyHGP}8hoa-J!Pzx|999)  +j" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add65 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0_literalMutationString547 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0_literalMutationString549_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrongoutput size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add65 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0_literalMutationString549 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0_add922_failAssert0() throws Exception {
        try {
            {
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
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0_add922 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0_add920_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        IOUtils.toByteArray(fin, m_testFile.length());
                        m_testFile.length();
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add65 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0_add920 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString44_failAssert0_literalMutationString708_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size:C out.length=" + (out.length)) + "") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString44 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString44_failAssert0_literalMutationString708 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0_literalMutationString526_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        m_testFile.length();
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + " does not exist") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add66 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0_literalMutationString526 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString44_failAssert0_add1006_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        fin.available();
                        String String_3 = (("Wrong output size:C out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString44 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString44_failAssert0_add1006 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString42_failAssert0_add1013_failAssert0() throws Exception {
        try {
            {
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
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString42_failAssert0_add1013 should have thrown IOException");
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
                String String_2 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_add51 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add67_failAssert0_literalMutationString562_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add67 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add67_failAssert0_literalMutationString562 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0_literalMutationString520_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        m_testFile.length();
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: ou.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add66 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0_literalMutationString520 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0null1065_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(null, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add65 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0null1065 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_add1020_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "(") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_add1020 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString43_failAssert0_literalMutationString723_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("" + (out.length)) + "") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString43 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString43_failAssert0_literalMutationString723 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString43_failAssert0_add1019_failAssert0() throws Exception {
        try {
            {
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
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString43_failAssert0_add1019 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0_add933_failAssert0() throws Exception {
        try {
            {
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
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add68 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0_add933 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0_add935_failAssert0() throws Exception {
        try {
            {
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
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0_add935 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add3_literalMutationString17_add785_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                String String_2 = ((" does not exist" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_literalMutationString17_add785 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString45_failAssert0_literalMutationString678_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = ((" do@s not exist" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString45 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString45_failAssert0_literalMutationString678 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add3_literalMutationString16_add781_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                String String_2 = (("" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_literalMutationString16_add781 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add67_failAssert0_add926_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        m_testFile.length();
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add67 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add67_failAssert0_add926 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add2_literalMutationString31_add834_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                long o_testToByteArray_InputStream_Size_add2__5 = this.m_testFile.length();
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add2__9 = fin.available();
                String String_4 = (("Wrong output size: out.length=" + (out.length)) + " does not exist") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add2_literalMutationString31_add834 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString42_failAssert0_literalMutationString719_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("*OdwpauR%h1,xavU[1Rvnj|}8wu]&8" + (out.length)) + "%") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString42 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString42_failAssert0_literalMutationString719 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add3_literalMutationString21_add793_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                String String_2 = (("Wrong output size: out.length=" + (out.length)) + "") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_literalMutationString21_add793 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_literalMutationString733_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + " does not exist") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_literalMutationString733 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add64_failAssert0_literalMutationString539_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        m_testFile.length();
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("sg2R8>3aX.)v8-E+,N[v<l2kvDr1Fo" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add64 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add64_failAssert0_literalMutationString539 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add3_literalMutationString19_add797_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                String String_2 = (("hscbCS@!x*zH_,y(q2 5[gpbL[{$QV" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_literalMutationString19_add797 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0_literalMutationString566_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = ((" does not exist" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add68 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0_literalMutationString566 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add3_add51_failAssert0_add865_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                    IOUtils.toByteArray(fin, this.m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                    int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                    int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                    String String_2 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_add51 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_add51_failAssert0_add865 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add67_failAssert0_add928_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        fin.available();
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add67 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add67_failAssert0_add928 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_literalMutationString730_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("juza;+kVD6&G)ynZ< gd.usM]Jt}g`" + (out.length)) + "(") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_literalMutationString730 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0_literalMutationString564_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong outpu size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add68 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0_literalMutationString564 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1549() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1549__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1549__10);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1552_add1753() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            exc.getMessage();
            boolean o_testToByteArray_InputStream_SizeIllegal_add1552_add1753__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1552_add1753__9);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1552__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1552_add1753__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_literalMutationNumber1539_add1773() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 2));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1539_add1773__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1539_add1773__9);
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1539__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1539_add1773__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_literalMutationNumber1539() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 2));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1539__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1539__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1549_add1742() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1549_add1742__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1549_add1742__10);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1549__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1549_add1742__10);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1550_add1736() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            long o_testToByteArray_InputStream_SizeIllegal_add1550__5 = this.m_testFile.length();
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1550_add1736__11 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1550_add1736__11);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1550__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1550_add1736__11);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1549_add1739_add3087() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            long o_testToByteArray_InputStream_SizeIllegal_add1549_add1739__5 = this.m_testFile.length();
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1549_add1739_add3087__13 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1549_add1739_add3087__13);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1549__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1549_add1739_add3087__13);
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
    public void testToByteArray_InputStream_SizeIllegal_literalMutationString1544() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationString1544__8 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationString1544__8);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1549_add1742_add3179() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1549_add1742__10 = exc.getMessage().startsWith("Unexpected readed size");
            boolean o_testToByteArray_InputStream_SizeIllegal_add1549_add1742_add3179__14 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1549_add1742_add3179__14);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1549__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1549_add1742_add3179__14);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1550() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            long o_testToByteArray_InputStream_SizeIllegal_add1550__5 = this.m_testFile.length();
            Assert.assertEquals(4097L, ((long) (o_testToByteArray_InputStream_SizeIllegal_add1550__5)));
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1550__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1550__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1551_add1748() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1551__8 = exc.getMessage().startsWith("Unexpected readed size");
            boolean o_testToByteArray_InputStream_SizeIllegal_add1551_add1748__12 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1551_add1748__12);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1551__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1551_add1748__12);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1551_add1746() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1551_add1746__8 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1551_add1746__8);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1551__8 = exc.getMessage().startsWith("Unexpected readed size");
            boolean o_testToByteArray_InputStream_SizeIllegal_add1551__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1551_add1746__8);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_literalMutationNumber1539_add1773_add3165() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 2));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1539_add1773__9 = exc.getMessage().startsWith("Unexpected readed size");
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1539_add1773_add3165__13 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1539_add1773_add3165__13);
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1539__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1539_add1773_add3165__13);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1552() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            exc.getMessage();
            boolean o_testToByteArray_InputStream_SizeIllegal_add1552__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1552__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_literalMutationString1544_add1781() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationString1544_add1781__8 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationString1544_add1781__8);
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationString1544__8 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationString1544_add1781__8);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1551() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1551__8 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1551__8);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1551__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1551__10);
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1551__8);
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

