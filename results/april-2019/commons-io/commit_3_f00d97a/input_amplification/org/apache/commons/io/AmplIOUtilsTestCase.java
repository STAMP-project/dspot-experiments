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
    public void testToByteArray_InputStream_Size_add1_failAssert0_add64_failAssert0_literalMutationString699_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        m_testFile.length();
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add64 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add64_failAssert0_literalMutationString699 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0_add987_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0_add987 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString44_failAssert0_literalMutationString564_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString44_failAssert0_literalMutationString564 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
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
    public void testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0_literalMutationString726_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: out.@ength=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add68 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0_literalMutationString726 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add3_add53_add807_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add3_add53__8 = fin.available();
                int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                String String_2 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_add53_add807 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString42_failAssert0_literalMutationString569_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("*OdwpauR%h-,xavU[1Rvnj|}8wu]&8" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString42 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString42_failAssert0_literalMutationString569 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add3_add51_failAssert0_add949_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                    IOUtils.toByteArray(fin, this.m_testFile.length());
                    IOUtils.toByteArray(fin, this.m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                    int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                    int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                    String String_2 = (("Wrong output size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_add51 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_add51_failAssert0_add949 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add3_add51_failAssert0_literalMutationString631_failAssert0() throws Exception {
        try {
            {
                try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                    IOUtils.toByteArray(fin, this.m_testFile.length());
                    final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                    int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                    int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                    String String_2 = (("Wrong output @size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_add51 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_add51_failAssert0_literalMutationString631 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add2_literalMutationString30_add796_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                long o_testToByteArray_InputStream_Size_add2__5 = this.m_testFile.length();
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add2__9 = fin.available();
                String String_4 = (("Wrong output size: out.length=" + (out.length)) + "") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add2_literalMutationString30_add796 should have thrown IOException");
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
                        String String_3 = (("Wrong utput size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
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
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_literalMutationString588_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_literalMutationString588 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_literalMutationString584_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_literalMutationString584 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString45_failAssert0_add886_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString45_failAssert0_add886 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add2_literalMutationString24_add780_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                long o_testToByteArray_InputStream_Size_add2__5 = this.m_testFile.length();
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add2__9 = fin.available();
                String String_4 = (("" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add2_literalMutationString24_add780 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add63_failAssert0_add992_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add63 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add63_failAssert0_add992 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0_literalMutationString732_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add68 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0_literalMutationString732 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add67_failAssert0_add1018_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add67_failAssert0_add1018 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString42_failAssert0_add913_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        fin.available();
                        String String_3 = (("*OdwpauR%h1,xavU[1Rvnj|}8wu]&8" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString42 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString42_failAssert0_add913 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString45_failAssert0_literalMutationString533_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("1023 KB" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString45 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString45_failAssert0_literalMutationString533 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString43_failAssert0_literalMutationString578_failAssert0() throws Exception {
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
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString43 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString43_failAssert0_literalMutationString578 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_add926_failAssert0() throws Exception {
        try {
            {
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
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_add926 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add3_literalMutationString22_add747_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                String String_2 = (("Wrong output size: out.length=" + (out.length)) + " does not exist") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_literalMutationString22_add747 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0_add1005_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0_add1005 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0_add1025_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add68_failAssert0_add1025 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString44_failAssert0_add904_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        m_testFile.length();
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size:C out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString44 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString44_failAssert0_add904 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add3_literalMutationString15_add743_failAssert0() throws Exception {
        try {
            try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
                IOUtils.toByteArray(fin, this.m_testFile.length());
                final byte[] out = IOUtils.toByteArray(fin, this.m_testFile.length());
                int o_testToByteArray_InputStream_Size_add3__8 = fin.available();
                int o_testToByteArray_InputStream_Size_add3__9 = fin.available();
                String String_2 = (("Wrong outpu size: out.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add3_literalMutationString15_add743 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add1_failAssert0_add63_failAssert0null1078_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add63_failAssert0null1078 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0_literalMutationString713_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: out:.length=" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add65 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0_literalMutationString713 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString43_failAssert0_add918_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        m_testFile.length();
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString43 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString43_failAssert0_add918 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0_literalMutationString714_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        IOUtils.toByteArray(fin, m_testFile.length());
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add65 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add65_failAssert0_literalMutationString714 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add1_failAssert0_add64_failAssert0_add1002_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add64_failAssert0_add1002 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_add921_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_literalMutationString50_failAssert0_add921 should have thrown IOException");
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
    public void testToByteArray_InputStream_Size_add1_failAssert0_add67_failAssert0_literalMutationString723_failAssert0() throws Exception {
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
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add67_failAssert0_literalMutationString723 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0_literalMutationString682_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        m_testFile.length();
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = ((" does not exist" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add66 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0_literalMutationString682 should have thrown IOException");
        } catch (IOException expected) {
            Assert.assertEquals("Unexpected readed size. current: 0, excepted: 4097", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0_literalMutationString681_failAssert0() throws Exception {
        try {
            {
                {
                    try (final FileInputStream fin = new FileInputStream(m_testFile)) {
                        IOUtils.toByteArray(fin, m_testFile.length());
                        m_testFile.length();
                        final byte[] out = IOUtils.toByteArray(fin, m_testFile.length());
                        fin.available();
                        String String_3 = (("" + (out.length)) + "!=") + (AmplIOUtilsTestCase.FILE_SIZE);
                    }
                    org.junit.Assert.fail("testToByteArray_InputStream_Size_add1 should have thrown IOException");
                }
                org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add66 should have thrown IOException");
            }
            org.junit.Assert.fail("testToByteArray_InputStream_Size_add1_failAssert0_add66_failAssert0_literalMutationString681 should have thrown IOException");
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
                        String String_3 = (("Wrong output size: out.length=" + (out.length)) + "*") + (AmplIOUtilsTestCase.FILE_SIZE);
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
    public void testToByteArray_InputStream_SizeIllegal_literalMutationNumber1535() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 2));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1535__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1535__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1548() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            exc.getMessage();
            boolean o_testToByteArray_InputStream_SizeIllegal_add1548__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1548__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1547_add1777_add3201() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1547_add1777_add3201__8 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1547_add1777_add3201__8);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1547__8 = exc.getMessage().startsWith("Unexpected readed size");
            boolean o_testToByteArray_InputStream_SizeIllegal_add1547_add1777__12 = exc.getMessage().startsWith("Unexpected readed size");
            boolean o_testToByteArray_InputStream_SizeIllegal_add1547__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1547_add1777_add3201__8);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1545_add1771_add3170() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1545_add1771__10 = exc.getMessage().startsWith("Unexpected readed size");
            boolean o_testToByteArray_InputStream_SizeIllegal_add1545_add1771_add3170__14 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1545_add1771_add3170__14);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1545__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1545_add1771_add3170__14);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1545() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1545__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1545__10);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1547() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1547__8 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1547__8);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1547__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1547__10);
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1547__8);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1546() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            long o_testToByteArray_InputStream_SizeIllegal_add1546__5 = this.m_testFile.length();
            Assert.assertEquals(4097L, ((long) (o_testToByteArray_InputStream_SizeIllegal_add1546__5)));
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1546__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1546__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1546_add1764_add3121() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            long o_testToByteArray_InputStream_SizeIllegal_add1546__5 = this.m_testFile.length();
            long o_testToByteArray_InputStream_SizeIllegal_add1546_add1764__8 = this.m_testFile.length();
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1546_add1764_add3121__14 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1546_add1764_add3121__14);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1546__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1546_add1764_add3121__14);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1548_add1760_add3149() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            exc.getMessage();
            boolean o_testToByteArray_InputStream_SizeIllegal_add1548_add1760_add3149__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1548_add1760_add3149__9);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1548_add1760__9 = exc.getMessage().startsWith("Unexpected readed size");
            boolean o_testToByteArray_InputStream_SizeIllegal_add1548__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1548_add1760_add3149__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_literalMutationNumber1535_add1743() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 2));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1535_add1743__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1535_add1743__9);
            boolean o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1535__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_literalMutationNumber1535_add1743__9);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1546_add1765() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            long o_testToByteArray_InputStream_SizeIllegal_add1546__5 = this.m_testFile.length();
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1546_add1765__11 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1546_add1765__11);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1546__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1546_add1765__11);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1548_add1760() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            exc.getMessage();
            boolean o_testToByteArray_InputStream_SizeIllegal_add1548_add1760__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1548_add1760__9);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1548__9 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1548_add1760__9);
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
    public void testToByteArray_InputStream_SizeIllegal_add1547_add1777() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1547__8 = exc.getMessage().startsWith("Unexpected readed size");
            boolean o_testToByteArray_InputStream_SizeIllegal_add1547_add1777__12 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1547_add1777__12);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1547__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1547_add1777__12);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1545_add1771() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1545_add1771__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1545_add1771__10);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1545__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1545_add1771__10);
        }
    }

    @Test(timeout = 10000)
    public void testToByteArray_InputStream_SizeIllegal_add1547_add1775() throws Exception {
        try (final FileInputStream fin = new FileInputStream(this.m_testFile)) {
            IOUtils.toByteArray(fin, ((this.m_testFile.length()) + 1));
        } catch (final IOException exc) {
            boolean o_testToByteArray_InputStream_SizeIllegal_add1547_add1775__8 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1547_add1775__8);
            boolean o_testToByteArray_InputStream_SizeIllegal_add1547__8 = exc.getMessage().startsWith("Unexpected readed size");
            boolean o_testToByteArray_InputStream_SizeIllegal_add1547__10 = exc.getMessage().startsWith("Unexpected readed size");
            Assert.assertTrue(o_testToByteArray_InputStream_SizeIllegal_add1547_add1775__8);
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

