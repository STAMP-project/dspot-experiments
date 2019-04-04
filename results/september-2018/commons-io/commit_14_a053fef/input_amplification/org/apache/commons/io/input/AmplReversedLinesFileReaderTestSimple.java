package org.apache.commons.io.input;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;


public class AmplReversedLinesFileReaderTestSimple {
    private ReversedLinesFileReader reversedLinesFileReader;

    @After
    public void closeReader() {
        try {
            reversedLinesFileReader.close();
        } catch (final Exception e) {

        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5041() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529__3 = this.getClass().getResource("/test-file-empty.bin");
            URL o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5041__7 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5041__7)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5041__7)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5041__7)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5041__7)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5041__7)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5041__7)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5041__7)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5041__7)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5041__7)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5041__7)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5041__7)).getRef());
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add2890 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10908() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            URI o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10908__8 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10908__8)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10908__8)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10908__8)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10908__8)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10908__8)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10908__8)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10908__8)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10908__8)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10908__8)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10908__8)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10908__8)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10908__8)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10908__8)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10908__8)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10908__8)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10908__8)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10908__8)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10908__8)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10908__8)).getPort())));
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add8779 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5181() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5181__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5181__3)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5181__3)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5181__3)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5181__3)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5181__3)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5181__3)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5181__3)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5181__3)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5181__3)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5181__3)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5181__3)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5181__3)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5181__3)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5181__3)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5181__3)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5181__3)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5181__3)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5181__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5181__3)).getPort())));
            URI o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add2890 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add2888_failAssert124_add3514() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514__3)).getRef());
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add2888 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5183() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5183__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5183__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5183__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5183__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5183__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5183__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5183__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5183__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5183__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5183__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5183__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5183__3)).getRef());
            URI o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add2890 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add8779_failAssert335_add9313() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313__3)).getRef());
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add8779 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_literalMutationString8759_failAssert315() throws IOException, Exception, URISyntaxException {
        try {
            final File testFileEmpty = new File(this.getClass().getResource("").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationString8759 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11039() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11039__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11039__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11039__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11039__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11039__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11039__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11039__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11039__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11039__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11039__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11039__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11039__3)).getRef());
            URI o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add8776 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add2890_failAssert126_add3529() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529__3)).getRef());
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add2890 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_literalMutationString2875_failAssert111_literalMutationString3150_failAssert137() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEmpty = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
                org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationString2875 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationString2875_failAssert111_literalMutationString3150 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11038() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11038__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11038__3)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11038__3)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11038__3)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11038__3)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11038__3)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11038__3)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11038__3)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11038__3)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11038__3)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11038__3)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11038__3)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11038__3)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11038__3)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11038__3)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11038__3)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11038__3)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11038__3)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11038__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11038__3)).getPort())));
            URI o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add8776 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add2890_failAssert126_add3526() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3)).getRawUserInfo());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3)).getRawPath());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3)).getRawQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3)).getRawFragment());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3)).getSchemeSpecificPart());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3)).getPort())));
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add2890 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add8779_failAssert335_add9308() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3)).getRawUserInfo());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3)).getRawPath());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3)).getRawQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3)).getRawFragment());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3)).getRawAuthority());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3)).getPort())));
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add8779 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5037() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529__3 = this.getClass().getResource("/test-file-empty.bin");
            URI o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5037__7 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5037__7)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5037__7)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5037__7)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5037__7)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5037__7)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5037__7)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5037__7)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5037__7)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5037__7)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5037__7)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5037__7)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5037__7)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5037__7)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5037__7)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5037__7)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5037__7)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5037__7)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5037__7)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add5037__7)).getPort())));
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add2890 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10915() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10915__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10915__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10915__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10915__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10915__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10915__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10915__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10915__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10915__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10915__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10915__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10915__3)).getRef());
            URL o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323__3 = this.getClass().getResource("/test-file-empty.bin");
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add8777 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_literalMutationString2873_failAssert109_literalMutationString3410_failAssert139() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEmpty = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
                org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationString2873 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationString2873_failAssert109_literalMutationString3410 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4783() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514__3 = this.getClass().getResource("/test-file-empty.bin");
            URI o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4783__7 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4783__7)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4783__7)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4783__7)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4783__7)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4783__7)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4783__7)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4783__7)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4783__7)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4783__7)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4783__7)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4783__7)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4783__7)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4783__7)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4783__7)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4783__7)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4783__7)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4783__7)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4783__7)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4783__7)).getPort())));
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add2888 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4786() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514__3 = this.getClass().getResource("/test-file-empty.bin");
            URL o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4786__7 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4786__7)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4786__7)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4786__7)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4786__7)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4786__7)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4786__7)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4786__7)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4786__7)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4786__7)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4786__7)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4786__7)).getRef());
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add2888 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10798() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313__3 = this.getClass().getResource("/test-file-empty.bin");
            URL o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10798__7 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10798__7)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10798__7)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10798__7)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10798__7)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10798__7)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10798__7)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10798__7)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10798__7)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10798__7)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10798__7)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10798__7)).getRef());
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add8779 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10795() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313__3 = this.getClass().getResource("/test-file-empty.bin");
            URI o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10795__7 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10795__7)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10795__7)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10795__7)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10795__7)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10795__7)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10795__7)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10795__7)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10795__7)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10795__7)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10795__7)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10795__7)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10795__7)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10795__7)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10795__7)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10795__7)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10795__7)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10795__7)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10795__7)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10795__7)).getPort())));
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add8779 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add4979() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add4979__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add4979__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add4979__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add4979__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add4979__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add4979__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add4979__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add4979__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add4979__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add4979__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add4979__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529_add4979__3)).getRef());
            URL o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3529__3 = this.getClass().getResource("/test-file-empty.bin");
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add2890 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5236() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            URI o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5236__8 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5236__8)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5236__8)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5236__8)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5236__8)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5236__8)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5236__8)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5236__8)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5236__8)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5236__8)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5236__8)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5236__8)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5236__8)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5236__8)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5236__8)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5236__8)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5236__8)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5236__8)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5236__8)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5236__8)).getPort())));
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add2890 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5239() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            URL o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5239__8 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5239__8)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5239__8)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5239__8)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5239__8)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5239__8)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5239__8)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5239__8)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5239__8)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5239__8)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5239__8)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_add5239__8)).getRef());
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add2890 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10911() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            URL o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10911__8 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10911__8)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10911__8)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10911__8)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10911__8)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10911__8)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10911__8)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10911__8)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10911__8)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10911__8)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10911__8)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10911__8)).getRef());
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add8779 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_literalMutationString2874_failAssert110_literalMutationString3311_failAssert136() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEmpty = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
                org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationString2874 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationString2874_failAssert110_literalMutationString3311 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5046() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            URL o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5046__8 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5046__8)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5046__8)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5046__8)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5046__8)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5046__8)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5046__8)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5046__8)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5046__8)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5046__8)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5046__8)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5046__8)).getRef());
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add2887 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5040() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            URI o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5040__8 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5040__8)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5040__8)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5040__8)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5040__8)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5040__8)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5040__8)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5040__8)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5040__8)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5040__8)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5040__8)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5040__8)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5040__8)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5040__8)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5040__8)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5040__8)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5040__8)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5040__8)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5040__8)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add5040__8)).getPort())));
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add2887 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_literalMutationString10691_failAssert400() throws IOException, Exception, URISyntaxException {
        try {
            try {
                URI o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
                final File testFileEmpty = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
                new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
                org.junit.Assert.fail("testUnsupportedEncodingUTF16_add8779 should have thrown UnsupportedEncodingException");
            } catch (UnsupportedEncodingException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_literalMutationString10691 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_literalMutationString2870_failAssert106() throws IOException, Exception, URISyntaxException {
        try {
            final File testFileEmpty = new File(this.getClass().getResource("").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationString2870 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_literalMutationString8760_failAssert316_literalMutationString9340_failAssert344() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEmpty = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
                org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationString8760 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationString8760_failAssert316_literalMutationString9340 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10949() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323__3 = this.getClass().getResource("/test-file-empty.bin");
            URI o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10949__7 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10949__7)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10949__7)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10949__7)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10949__7)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10949__7)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10949__7)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10949__7)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10949__7)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10949__7)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10949__7)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10949__7)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10949__7)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10949__7)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10949__7)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10949__7)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10949__7)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10949__7)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10949__7)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10949__7)).getPort())));
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add8777 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_literalMutationString5105_failAssert190() throws IOException, Exception, URISyntaxException {
        try {
            try {
                URI o_testUnsupportedEncodingUTF16_add2890_failAssert126_add3526__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
                final File testFileEmpty = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
                new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
                org.junit.Assert.fail("testUnsupportedEncodingUTF16_add2890 should have thrown UnsupportedEncodingException");
            } catch (UnsupportedEncodingException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add2890_failAssert126_add3526_literalMutationString5105 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4926() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4926__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4926__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4926__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4926__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4926__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4926__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4926__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4926__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4926__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4926__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4926__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4926__3)).getRef());
            URI o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add2887 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4921() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4921__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4921__3)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4921__3)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4921__3)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4921__3)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4921__3)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4921__3)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4921__3)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4921__3)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4921__3)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4921__3)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4921__3)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4921__3)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4921__3)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4921__3)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4921__3)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4921__3)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4921__3)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4921__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_add4921__3)).getPort())));
            URI o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add2887 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_literalMutationString8763_failAssert319_literalMutationString9354_failAssert347() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEmpty = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
                org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationString8763 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationString8763_failAssert319_literalMutationString9354 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_literalMutationString2871_failAssert107_literalMutationString3367_failAssert140() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEmpty = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
                org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationString2871 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationString2871_failAssert107_literalMutationString3367 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4737() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4737__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4737__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4737__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4737__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4737__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4737__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4737__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4737__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4737__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4737__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4737__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514_add4737__3)).getRef());
            URL o_testUnsupportedEncodingUTF16_add2888_failAssert124_add3514__3 = this.getClass().getResource("/test-file-empty.bin");
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add2888 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_literalMutationString8762_failAssert318_literalMutationString9373_failAssert346() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEmpty = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
                org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationString8762 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationString8762_failAssert318_literalMutationString9373 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_literalMutationString2872_failAssert108_literalMutationString3133_failAssert138() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEmpty = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
                org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationString2872 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationString2872_failAssert108_literalMutationString3133 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10953() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323__3 = this.getClass().getResource("/test-file-empty.bin");
            URL o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10953__7 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10953__7)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10953__7)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10953__7)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10953__7)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10953__7)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10953__7)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10953__7)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10953__7)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10953__7)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10953__7)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323_add10953__7)).getRef());
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add8777 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11060() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            URI o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11060__8 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11060__8)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11060__8)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11060__8)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11060__8)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11060__8)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11060__8)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11060__8)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11060__8)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11060__8)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11060__8)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11060__8)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11060__8)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11060__8)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11060__8)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11060__8)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11060__8)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11060__8)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11060__8)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11060__8)).getPort())));
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add8776 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_literalMutationString10968_failAssert399() throws IOException, Exception, URISyntaxException {
        try {
            try {
                URI o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
                final File testFileEmpty = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
                new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
                org.junit.Assert.fail("testUnsupportedEncodingUTF16_add8776 should have thrown UnsupportedEncodingException");
            } catch (UnsupportedEncodingException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_literalMutationString10968 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11061() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            URL o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11061__8 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11061__8)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11061__8)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11061__8)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11061__8)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11061__8)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11061__8)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11061__8)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11061__8)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11061__8)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11061__8)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956_add11061__8)).getRef());
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add8776 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_literalMutationNumber2879_failAssert115_literalMutationString3179_failAssert141() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEmpty = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEmpty, 0, "UTF-16").close();
                org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationNumber2879 should have thrown ArithmeticException");
            } catch (ArithmeticException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationNumber2879_failAssert115_literalMutationString3179 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add8776_failAssert332_add8956() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3)).getRawUserInfo());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3)).getRawPath());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3)).getRawQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3)).getRawFragment());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3)).getRawAuthority());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add8776_failAssert332_add8956__3)).getPort())));
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add8776 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10841() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10841__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10841__3)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10841__3)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10841__3)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10841__3)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10841__3)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10841__3)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10841__3)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10841__3)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10841__3)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10841__3)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10841__3)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10841__3)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10841__3)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10841__3)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10841__3)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10841__3)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10841__3)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10841__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10841__3)).getPort())));
            URI o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add8779 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10844() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10844__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10844__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10844__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10844__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10844__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10844__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10844__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10844__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10844__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10844__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10844__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308_add10844__3)).getRef());
            URI o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9308__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add8779 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_literalMutationString8761_failAssert317_literalMutationString9289_failAssert348() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEmpty = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
                org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationString8761 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationString8761_failAssert317_literalMutationString9289 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add2887_failAssert123_add3301() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3)).getRawUserInfo());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3)).getRawPath());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3)).getRawQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3)).getRawFragment());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3)).getSchemeSpecificPart());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3)).getPort())));
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add2887 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_literalMutationString8764_failAssert320_literalMutationString9001_failAssert343() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEmpty = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
                org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationString8764 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationString8764_failAssert320_literalMutationString9001 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_literalMutationString4746_failAssert191() throws IOException, Exception, URISyntaxException {
        try {
            try {
                URI o_testUnsupportedEncodingUTF16_add2887_failAssert123_add3301__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
                final File testFileEmpty = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
                new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
                org.junit.Assert.fail("testUnsupportedEncodingUTF16_add2887 should have thrown UnsupportedEncodingException");
            } catch (UnsupportedEncodingException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add2887_failAssert123_add3301_literalMutationString4746 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add8777_failAssert333_add9323() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8777_failAssert333_add9323__3)).getRef());
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add8777 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10749() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10749__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10749__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10749__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10749__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10749__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10749__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10749__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10749__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10749__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10749__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10749__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313_add10749__3)).getRef());
            URL o_testUnsupportedEncodingUTF16_add8779_failAssert335_add9313__3 = this.getClass().getResource("/test-file-empty.bin");
            final File testFileEmpty = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            new ReversedLinesFileReader(testFileEmpty, 4096, "UTF-16").close();
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_add8779 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingUTF16_literalMutationNumber8768_failAssert324_literalMutationString9085_failAssert345() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEmpty = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEmpty, 0, "UTF-16").close();
                org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationNumber8768 should have thrown ArithmeticException");
            } catch (ArithmeticException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingUTF16_literalMutationNumber8768_failAssert324_literalMutationString9085 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add18_failAssert21_add491_literalMutationString1986_failAssert86() throws IOException, Exception, URISyntaxException {
        try {
            try {
                URI o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
                final File testFileEncodingBig5 = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
                new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
                org.junit.Assert.fail("testUnsupportedEncodingBig5_add18 should have thrown UnsupportedEncodingException");
            } catch (UnsupportedEncodingException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add18_failAssert21_add491_literalMutationString1986 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_literalMutationNumber5816_failAssert218_literalMutationString6132_failAssert239() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEncodingBig5 = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEncodingBig5, 0, "Big5").close();
                org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationNumber5816 should have thrown ArithmeticException");
            } catch (ArithmeticException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationNumber5816_failAssert218_literalMutationString6132 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add18_failAssert21_add491_add2264() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            URI o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2264__8 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2264__8)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2264__8)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2264__8)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2264__8)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2264__8)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2264__8)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2264__8)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2264__8)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2264__8)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2264__8)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2264__8)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2264__8)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2264__8)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2264__8)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2264__8)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2264__8)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2264__8)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2264__8)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2264__8)).getPort())));
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add18 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add21_failAssert24_add634_add1933() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1933__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1933__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1933__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1933__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1933__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1933__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1933__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1933__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1933__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1933__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1933__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1933__3)).getRef());
            URI o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add21 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add21_failAssert24_add634_literalMutationString1735_failAssert87() throws IOException, Exception, URISyntaxException {
        try {
            try {
                URI o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
                final File testFileEncodingBig5 = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
                new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
                org.junit.Assert.fail("testUnsupportedEncodingBig5_add21 should have thrown UnsupportedEncodingException");
            } catch (UnsupportedEncodingException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add21_failAssert24_add634_literalMutationString1735 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8117() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8117__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8117__3)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8117__3)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8117__3)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8117__3)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8117__3)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8117__3)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8117__3)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8117__3)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8117__3)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8117__3)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8117__3)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8117__3)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8117__3)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8117__3)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8117__3)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8117__3)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8117__3)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8117__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8117__3)).getPort())));
            URI o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add5827 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add19_failAssert22_add676_add2192() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingBig5_add19_failAssert22_add676__3 = this.getClass().getResource("/test-file-empty.bin");
            URL o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2192__7 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2192__7)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2192__7)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2192__7)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2192__7)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2192__7)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2192__7)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2192__7)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2192__7)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2192__7)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2192__7)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2192__7)).getRef());
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add19 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7721() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427__3 = this.getClass().getResource("/test-file-empty.bin");
            URI o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7721__7 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7721__7)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7721__7)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7721__7)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7721__7)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7721__7)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7721__7)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7721__7)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7721__7)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7721__7)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7721__7)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7721__7)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7721__7)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7721__7)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7721__7)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7721__7)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7721__7)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7721__7)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7721__7)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7721__7)).getPort())));
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add5827 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8222() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            URL o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8222__8 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8222__8)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8222__8)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8222__8)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8222__8)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8222__8)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8222__8)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8222__8)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8222__8)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8222__8)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8222__8)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8222__8)).getRef());
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add5824 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_literalMutationString5_failAssert8_literalMutationString348_failAssert36() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEncodingBig5 = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
                org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationString5 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationString5_failAssert8_literalMutationString348 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8221() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            URI o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8221__8 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8221__8)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8221__8)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8221__8)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8221__8)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8221__8)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8221__8)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8221__8)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8221__8)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8221__8)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8221__8)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8221__8)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8221__8)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8221__8)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8221__8)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8221__8)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8221__8)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8221__8)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8221__8)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8221__8)).getPort())));
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add5824 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7727() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427__3 = this.getClass().getResource("/test-file-empty.bin");
            URL o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7727__7 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7727__7)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7727__7)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7727__7)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7727__7)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7727__7)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7727__7)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7727__7)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7727__7)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7727__7)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7727__7)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7727__7)).getRef());
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add5827 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8011() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459__3 = this.getClass().getResource("/test-file-empty.bin");
            URI o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8011__7 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8011__7)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8011__7)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8011__7)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8011__7)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8011__7)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8011__7)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8011__7)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8011__7)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8011__7)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8011__7)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8011__7)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8011__7)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8011__7)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8011__7)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8011__7)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8011__7)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8011__7)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8011__7)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8011__7)).getPort())));
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add5825 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_literalMutationString4_failAssert7_literalMutationString418_failAssert35() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEncodingBig5 = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
                org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationString4 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationString4_failAssert7_literalMutationString418 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add5824_failAssert226_add6377() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3)).getRawUserInfo());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3)).getRawPath());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3)).getRawQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3)).getRawFragment());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3)).getSchemeSpecificPart());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3)).getPort())));
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add5824 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add21_failAssert24_add634() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3)).getRawUserInfo());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3)).getRawPath());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3)).getRawQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3)).getRawFragment());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3)).getSchemeSpecificPart());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3)).getPort())));
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add21 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add7960() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add7960__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add7960__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add7960__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add7960__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add7960__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add7960__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add7960__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add7960__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add7960__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add7960__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add7960__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add7960__3)).getRef());
            URL o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459__3 = this.getClass().getResource("/test-file-empty.bin");
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add5825 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add5827_failAssert229_add6423() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3)).getRawUserInfo());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3)).getRawPath());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3)).getRawQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3)).getRawFragment());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3)).getSchemeSpecificPart());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3)).getPort())));
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add5827 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8016() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459__3 = this.getClass().getResource("/test-file-empty.bin");
            URL o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8016__7 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8016__7)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8016__7)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8016__7)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8016__7)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8016__7)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8016__7)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8016__7)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8016__7)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8016__7)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8016__7)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459_add8016__7)).getRef());
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add5825 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_literalMutationString5809_failAssert211_literalMutationString5878_failAssert244() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEncodingBig5 = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
                org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationString5809 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationString5809_failAssert211_literalMutationString5878 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_literalMutationString5812_failAssert214_literalMutationString6057_failAssert242() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEncodingBig5 = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
                org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationString5812 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationString5812_failAssert214_literalMutationString6057 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add5827_failAssert229_add6427() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427__3)).getRef());
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add5827 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add19_failAssert22_add676_add2190() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingBig5_add19_failAssert22_add676__3 = this.getClass().getResource("/test-file-empty.bin");
            URI o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2190__7 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2190__7)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2190__7)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2190__7)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2190__7)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2190__7)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2190__7)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2190__7)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2190__7)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2190__7)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2190__7)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2190__7)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2190__7)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2190__7)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2190__7)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2190__7)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2190__7)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2190__7)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2190__7)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2190__7)).getPort())));
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add19 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add21_failAssert24_add638_add1676() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingBig5_add21_failAssert24_add638__3 = this.getClass().getResource("/test-file-empty.bin");
            URI o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1676__7 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1676__7)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1676__7)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1676__7)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1676__7)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1676__7)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1676__7)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1676__7)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1676__7)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1676__7)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1676__7)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1676__7)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1676__7)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1676__7)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1676__7)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1676__7)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1676__7)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1676__7)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1676__7)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1676__7)).getPort())));
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add21 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_literalMutationString6_failAssert9_literalMutationString269_failAssert37() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEncodingBig5 = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
                org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationString6 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationString6_failAssert9_literalMutationString269 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_literalMutationString5811_failAssert213_literalMutationString5969_failAssert243() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEncodingBig5 = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
                org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationString5811 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationString5811_failAssert213_literalMutationString5969 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_literalMutationString5808_failAssert210_literalMutationString6130_failAssert241() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEncodingBig5 = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
                org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationString5808 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationString5808_failAssert210_literalMutationString6130 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add5824_failAssert226_add6377_literalMutationString8171_failAssert296() throws IOException, Exception, URISyntaxException {
        try {
            try {
                URI o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
                final File testFileEncodingBig5 = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
                new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
                org.junit.Assert.fail("testUnsupportedEncodingBig5_add5824 should have thrown UnsupportedEncodingException");
            } catch (UnsupportedEncodingException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add5824_failAssert226_add6377_literalMutationString8171 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add21_failAssert24_add638() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingBig5_add21_failAssert24_add638__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638__3)).getRef());
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add21 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add5825_failAssert227_add6459() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5825_failAssert227_add6459__3)).getRef());
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add5825 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8200() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8200__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8200__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8200__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8200__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8200__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8200__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8200__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8200__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8200__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8200__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8200__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8200__3)).getRef());
            URI o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add5824 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add21_failAssert24_add634_add2021() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            URI o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2021__8 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2021__8)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2021__8)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2021__8)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2021__8)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2021__8)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2021__8)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2021__8)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2021__8)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2021__8)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2021__8)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2021__8)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2021__8)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2021__8)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2021__8)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2021__8)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2021__8)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2021__8)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2021__8)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2021__8)).getPort())));
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add21 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add21_failAssert24_add638_add1680() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingBig5_add21_failAssert24_add638__3 = this.getClass().getResource("/test-file-empty.bin");
            URL o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1680__7 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1680__7)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1680__7)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1680__7)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1680__7)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1680__7)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1680__7)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1680__7)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1680__7)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1680__7)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1680__7)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1680__7)).getRef());
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add21 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add21_failAssert24_add634_add2027() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            URL o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2027__8 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2027__8)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2027__8)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2027__8)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2027__8)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2027__8)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2027__8)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2027__8)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2027__8)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2027__8)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2027__8)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add2027__8)).getRef());
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add21 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add19_failAssert22_add676_add2162() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2162__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2162__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2162__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2162__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2162__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2162__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2162__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2162__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2162__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2162__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2162__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676_add2162__3)).getRef());
            URL o_testUnsupportedEncodingBig5_add19_failAssert22_add676__3 = this.getClass().getResource("/test-file-empty.bin");
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add19 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add19_failAssert22_add676() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingBig5_add19_failAssert22_add676__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add19_failAssert22_add676__3)).getRef());
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add19 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add21_failAssert24_add638_add1618() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1618__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1618__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1618__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1618__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1618__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1618__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1618__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1618__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1618__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1618__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1618__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add21_failAssert24_add638_add1618__3)).getRef());
            URL o_testUnsupportedEncodingBig5_add21_failAssert24_add638__3 = this.getClass().getResource("/test-file-empty.bin");
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add21 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8158() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            URI o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8158__8 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8158__8)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8158__8)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8158__8)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8158__8)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8158__8)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8158__8)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8158__8)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8158__8)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8158__8)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8158__8)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8158__8)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8158__8)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8158__8)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8158__8)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8158__8)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8158__8)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8158__8)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8158__8)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8158__8)).getPort())));
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add5827 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8159() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            URL o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8159__8 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8159__8)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8159__8)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8159__8)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8159__8)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8159__8)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8159__8)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8159__8)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8159__8)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8159__8)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8159__8)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8159__8)).getRef());
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add5827 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_literalMutationString5807_failAssert209() throws IOException, Exception, URISyntaxException {
        try {
            final File testFileEncodingBig5 = new File(this.getClass().getResource("").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationString5807 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_literalMutationString2_failAssert5() throws IOException, Exception, URISyntaxException {
        try {
            final File testFileEncodingBig5 = new File(this.getClass().getResource("").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationString2 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add18_failAssert21_add491_add2201() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2201__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2201__3)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2201__3)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2201__3)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2201__3)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2201__3)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2201__3)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2201__3)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2201__3)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2201__3)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2201__3)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2201__3)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2201__3)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2201__3)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2201__3)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2201__3)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2201__3)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2201__3)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2201__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2201__3)).getPort())));
            URI o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add18 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add18_failAssert21_add491_add2205() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2205__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2205__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2205__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2205__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2205__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2205__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2205__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2205__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2205__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2205__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2205__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2205__3)).getRef());
            URI o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add18 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_literalMutationString3_failAssert6_literalMutationString271_failAssert34() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEncodingBig5 = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
                org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationString3 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationString3_failAssert6_literalMutationString271 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add18_failAssert21_add491() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3)).getRawUserInfo());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3)).getRawPath());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3)).getRawQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3)).getRawFragment());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3)).getSchemeSpecificPart());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3)).getPort())));
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add18 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7659() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7659__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7659__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7659__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7659__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7659__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7659__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7659__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7659__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7659__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7659__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7659__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427_add7659__3)).getRef());
            URL o_testUnsupportedEncodingBig5_add5827_failAssert229_add6427__3 = this.getClass().getResource("/test-file-empty.bin");
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add5827 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8199() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8199__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8199__3)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8199__3)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8199__3)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8199__3)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8199__3)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8199__3)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8199__3)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8199__3)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8199__3)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8199__3)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8199__3)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8199__3)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8199__3)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8199__3)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8199__3)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8199__3)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8199__3)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8199__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377_add8199__3)).getPort())));
            URI o_testUnsupportedEncodingBig5_add5824_failAssert226_add6377__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add5824 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add21_failAssert24_add634_add1929() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1929__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1929__3)).getRawSchemeSpecificPart());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1929__3)).getSchemeSpecificPart());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1929__3)).getRawAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1929__3)).getRawFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1929__3)).getRawUserInfo());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1929__3)).getRawQuery());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1929__3)).getRawPath());
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1929__3)).toString());
            Assert.assertEquals(433662550, ((int) (((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1929__3)).hashCode())));
            Assert.assertTrue(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1929__3)).isAbsolute());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1929__3)).getPath());
            Assert.assertFalse(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1929__3)).isOpaque());
            Assert.assertEquals("file", ((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1929__3)).getScheme());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1929__3)).getAuthority());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1929__3)).getFragment());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1929__3)).getQuery());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1929__3)).getHost());
            Assert.assertNull(((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1929__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URI) (o_testUnsupportedEncodingBig5_add21_failAssert24_add634_add1929__3)).getPort())));
            URI o_testUnsupportedEncodingBig5_add21_failAssert24_add634__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add21 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_literalMutationNumber10_failAssert13_literalMutationString293_failAssert38() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEncodingBig5 = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEncodingBig5, 0, "Big5").close();
                org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationNumber10 should have thrown ArithmeticException");
            } catch (ArithmeticException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationNumber10_failAssert13_literalMutationString293 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add5827_failAssert229_add6423_literalMutationString7985_failAssert297() throws IOException, Exception, URISyntaxException {
        try {
            try {
                URI o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
                final File testFileEncodingBig5 = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
                new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
                org.junit.Assert.fail("testUnsupportedEncodingBig5_add5827 should have thrown UnsupportedEncodingException");
            } catch (UnsupportedEncodingException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add5827_failAssert229_add6423_literalMutationString7985 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_literalMutationString1_failAssert4_literalMutationString541_failAssert33() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEncodingBig5 = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
                org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationString1 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationString1_failAssert4_literalMutationString541 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8120() throws IOException, Exception, URISyntaxException {
        try {
            URL o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8120__3 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8120__3)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8120__3)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8120__3)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8120__3)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8120__3)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8120__3)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8120__3)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8120__3)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8120__3)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8120__3)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423_add8120__3)).getRef());
            URI o_testUnsupportedEncodingBig5_add5827_failAssert229_add6423__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add5827 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_add18_failAssert21_add491_add2267() throws IOException, Exception, URISyntaxException {
        try {
            URI o_testUnsupportedEncodingBig5_add18_failAssert21_add491__3 = this.getClass().getResource("/test-file-empty.bin").toURI();
            URL o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2267__8 = this.getClass().getResource("/test-file-empty.bin");
            Assert.assertEquals("file:/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2267__8)).toString());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2267__8)).getPath());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2267__8)).getAuthority());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2267__8)).getQuery());
            Assert.assertEquals("file", ((URL) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2267__8)).getProtocol());
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/test-file-empty.bin", ((URL) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2267__8)).getFile());
            Assert.assertEquals("", ((URL) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2267__8)).getHost());
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2267__8)).getUserInfo());
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2267__8)).getPort())));
            Assert.assertEquals(-1, ((int) (((URL) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2267__8)).getDefaultPort())));
            Assert.assertNull(((URL) (o_testUnsupportedEncodingBig5_add18_failAssert21_add491_add2267__8)).getRef());
            final File testFileEncodingBig5 = new File(this.getClass().getResource("/test-file-empty.bin").toURI());
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
            org.junit.Assert.fail("testUnsupportedEncodingBig5_add18 should have thrown UnsupportedEncodingException");
        } catch (UnsupportedEncodingException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testUnsupportedEncodingBig5_literalMutationString5810_failAssert212_literalMutationString6055_failAssert240() throws IOException, Exception, URISyntaxException {
        try {
            try {
                final File testFileEncodingBig5 = new File(this.getClass().getResource("").toURI());
                new ReversedLinesFileReader(testFileEncodingBig5, 4096, "Big5").close();
                org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationString5810 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testUnsupportedEncodingBig5_literalMutationString5810_failAssert212_literalMutationString6055 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected_1) {
            Assert.assertEquals("/tmp/icst-2019/september-2018/dataset/commons-io_parent/target/test-classes/org/apache/commons/io/input (Is a directory)", expected_1.getMessage());
        }
    }
}

