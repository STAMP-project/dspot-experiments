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
    public void testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0_add988_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0_add988 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0_add985_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        m_testFile.length();
                        m_testFile.length();
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add66 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0_add985 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString42_failAssert0_literalMutationString606_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("*OdwpauR%h1,xavU[1Rvnj|}]8wu]&8" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString42 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString42_failAssert0_literalMutationString606 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString45_failAssert0_add908_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        m_testFile.length();
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = ((" does not exist" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString45 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString45_failAssert0_add908 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add3_literalMutationString19_add767_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                String String_2 = (("hscbCS@!x*zH_,y(q2 5[gpbL[{$QV" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_literalMutationString19_add767 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add3_literalMutationString21_add763_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                String String_2 = (("Wrong output size: out.length=" + (out.length)) + "") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_literalMutationString21_add763 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add3_literalMutationString18_add735_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                String String_2 = (("Wrong outpu} size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_literalMutationString18_add735 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add1_failAssert0_add63_failAssert0_literalMutationString692_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output sze: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add63 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add63_failAssert0_literalMutationString692 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add1_failAssert0_add64_failAssert0_add999_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        m_testFile.length();
                        IOUtils.toByteArray(fin, m_testFile.length());
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add64 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add64_failAssert0_add999 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add64_failAssert0_literalMutationString706_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        m_testFile.length();
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + " does not exist") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add64 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add64_failAssert0_literalMutationString706 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add2_literalMutationString25_add784_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                long o_testToByteArray_InputStream_Size_add2__5 = this.m_testFile.length();
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add2__9 = fin.available();
                String String_4 = (("Wrong output size^: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add2_literalMutationString25_add784 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add3_add52_add813_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                long o_testToByteArray_InputStream_Size_add3_add52__5 = this.m_testFile.length();
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                String String_2 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_add52_add813 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0_literalMutationString733_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add68 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0_literalMutationString733 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString44_failAssert0_literalMutationString598_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong ouTtput size:C out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString44 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString44_failAssert0_literalMutationString598 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add67_failAssert0_literalMutationString717_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        fin.available();
                        String String_3 = (("" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add67 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add67_failAssert0_literalMutationString717 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString45_failAssert0_literalMutationString572_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("@BafG1>V9s7n4hm" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString45 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString45_failAssert0_literalMutationString572 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0null1083_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0null1083 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add67_failAssert0_add1015_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add67 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add67_failAssert0_add1015 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add67_failAssert0_add1019_failAssert0() throws Exception {
        try {
            {
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
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add67_failAssert0_add1019 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0_literalMutationString730_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("juza;+kVD6&G)ynZ< gd.usM]Jt}g`" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add68 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0_literalMutationString730 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString42_failAssert0_add936_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString42_failAssert0_add936 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0_add1006_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        m_testFile.length();
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
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0_add1006 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString44_failAssert0_add930_failAssert0() throws Exception {
        try {
            {
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
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString44_failAssert0_add930 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_literalMutationString620_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = ((" does not exist" + (out.length)) + "(") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_literalMutationString620 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0_literalMutationString689_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        m_testFile.length();
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add66 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0_literalMutationString689 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0_add1020_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add68 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0_add1020 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add63_failAssert0_add997_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add63 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add63_failAssert0_add997 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0_add1022_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add68 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0_add1022 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add3_add51_failAssert0_add954_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                    IOUtils.toByteArray(fin, this.m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                    int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                    fin.available();
                    int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                    String String_2 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_add51 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_add51_failAssert0_add954 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add3_add51_failAssert0_literalMutationString628_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                    IOUtils.toByteArray(fin, this.m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                    int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                    int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                    String String_2 = ((" does not exist" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_add51 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_add51_failAssert0_literalMutationString628 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add3_literalMutationString17_add755_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                String String_2 = ((" does not exist" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_literalMutationString17_add755 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString43_failAssert0_add939_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString43 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString43_failAssert0_add939 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0_literalMutationString715_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "#") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add65 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0_literalMutationString715 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0_add1007_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
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
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0_add1007 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0_literalMutationString686_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        m_testFile.length();
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong outp=t size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add66 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0_literalMutationString686 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString43_failAssert0_literalMutationString614_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = ((")" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString43 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString43_failAssert0_literalMutationString614 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add2_literalMutationString28_add800_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                long o_testToByteArray_InputStream_Size_add2__5 = this.m_testFile.length();
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add2__9 = fin.available();
                String String_4 = (("Wrong 6utput size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add2_literalMutationString28_add800 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_add943_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_add943 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_add947_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "(") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_add947 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0_literalMutationString709_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output sie: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add65 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0_literalMutationString709 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add3_literalMutationString16_add751_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                String String_2 = (("" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_literalMutationString16_add751 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add67_failAssert0_literalMutationString725_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "m") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add67 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add67_failAssert0_literalMutationString725 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_literalMutationString626_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "U") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_literalMutationString626 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_literalMutationNumber1536() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 2));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1536__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1536__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1549() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            exc.getMessage();
            boolean o_testToByteArray_InputStream_SizeIllegal_add1549__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1549__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1548() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1548__8 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1548__8);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1548__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1548__10);
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1548__8);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1547_add1761() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            long o_testToByteArray_InputStream_SizeIllegal_add1547__5 = this.m_testFile.length();
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1547_add1761__11 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1547_add1761__11);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1547__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1547_add1761__11);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_literalMutationNumber1536_add1748_add3196() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 2));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1536_add1748__9 = exc.getMessage().startsWith("Unexpected readed size");
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1536_add1748_add3196__13 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1536_add1748_add3196__13);
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1536__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1536_add1748_add3196__13);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_literalMutationNumber1536_add1748() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 2));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1536_add1748__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1536_add1748__9);
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1536__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1536_add1748__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1547() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            long o_testToByteArray_InputStream_SizeIllegal_add1547__5 = this.m_testFile.length();
            Assert.assertEquals(4097L, ((long) (o_testToByteArray_InputStream_SizeIllegal_add1547__5)));
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1547__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1547__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1549_add1778() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            exc.getMessage();
            boolean o_testToByteArray_InputStream_SizeIllegal_add1549_add1778__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1549_add1778__9);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1549__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1549_add1778__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1546() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1546__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1546__10);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_literalMutationString1541_add1756() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationString1541_add1756__8 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationString1541_add1756__8);
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationString1541__8 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationString1541_add1756__8);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1548_add1771() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1548_add1771__8 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1548_add1771__8);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1548__8 = exc.getMessage().startsWith("Unexpected readed size");
            boolean o_testToByteArray_InputStream_SizeIllegal_add1548__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1548_add1771__8);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1548_add1773() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1548__8 = exc.getMessage().startsWith("Unexpected readed size");
            boolean o_testToByteArray_InputStream_SizeIllegal_add1548_add1773__12 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1548_add1773__12);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1548__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1548_add1773__12);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1549_add1778_add3203() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            exc.getMessage();
            boolean o_testToByteArray_InputStream_SizeIllegal_add1549_add1778__9 = exc.getMessage().startsWith("Unexpected readed size");
            boolean o_testToByteArray_InputStream_SizeIllegal_add1549_add1778_add3203__13 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1549_add1778_add3203__13);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1549__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1549_add1778_add3203__13);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1547_add1761_add3217() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            long o_testToByteArray_InputStream_SizeIllegal_add1547__5 = this.m_testFile.length();
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1547_add1761__11 = exc.getMessage().startsWith("Unexpected readed size");
            boolean o_testToByteArray_InputStream_SizeIllegal_add1547_add1761_add3217__15 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1547_add1761_add3217__15);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1547__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1547_add1761_add3217__15);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1546_add1767() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1546_add1767__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1546_add1767__10);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1546__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1546_add1767__10);
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
    public void testToByteArray_InputStream_SizeIllegal_literalMutationString1541() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationString1541__8 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationString1541__8);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1548_add1770_add3210() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            long o_testToByteArray_InputStream_SizeIllegal_add1548_add1770__5 = this.m_testFile.length();
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1548__8 = exc.getMessage().startsWith("Unexpected readed size");
            boolean o_testToByteArray_InputStream_SizeIllegal_add1548_add1770_add3210__15 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1548_add1770_add3210__15);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1548__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1548_add1770_add3210__15);
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

