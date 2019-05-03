package org.jsoup.helper;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import org.jsoup.Jsoup;
import org.jsoup.integration.ParseTest;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;


public class AmplDataUtilTest {
    private InputStream stream(String data) {
        return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
    }

    private InputStream stream(String data, String charset) {
        try {
            return new ByteArrayInputStream(data.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            Assert.fail();
        }
        return null;
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1375_failAssert0_add23152_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16BE");
                doc.title().contains("UTF-16BE");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf16le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                in = ParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32BE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1375 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1375_failAssert0_add23152 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1326_failAssert0_add23548_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16BE");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf16le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf32be.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32BE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1326 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1326_failAssert0_add23548 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1326_failAssert0null27147_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16BE");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf16le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf32be.html");
                doc = Jsoup.parse(in, null, null);
                doc.title().contains("UTF-32BE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1326 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1326_failAssert0null27147 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1326_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-16BE");
            doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf16le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-16LE");
            doc.text().contains("??????");
            doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf32be.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-32BE");
            doc.text().contains("??????");
            doc.text().contains("??????");
            doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf32le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-32LE");
            doc.text().contains("??????");
            doc.text().contains("??????");
            doc.text().contains("??????");
            doc.text().contains("??????");
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1326 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1326_failAssert0_add23538_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16BE");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf16le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf32be.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32BE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1326 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1326_failAssert0_add23538 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1375_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
            Document doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-16BE");
            doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf16le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-16LE");
            doc.text().contains("??????");
            doc.text().contains("??????");
            in = ParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-32BE");
            doc.text().contains("??????");
            doc.text().contains("??????");
            doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf32le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-32LE");
            doc.text().contains("??????");
            doc.text().contains("??????");
            doc.text().contains("??????");
            doc.text().contains("??????");
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1375 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1375_failAssert0_literalMutationString16155_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16BE");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf16le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                in = ParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32BE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32LE");
                doc.text().contains("\uac00\uac01\uac02\uac03\uac05");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1375 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1375_failAssert0_literalMutationString16155 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1350_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
            Document doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-16BE");
            doc.text().contains("??????");
            in = ParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-16LE");
            doc.text().contains("??????");
            doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf32be.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-32BE");
            doc.text().contains("??????");
            doc.text().contains("??????");
            doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf32le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-32LE");
            doc.text().contains("??????");
            doc.text().contains("??????");
            doc.text().contains("??????");
            doc.text().contains("??????");
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1350 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1326_failAssert0_literalMutationString17359_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "?@!pos*7@4I|0zxW=h");
                doc.title().contains("UTF-16BE");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf16le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf32be.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32BE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1326 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1326_failAssert0_literalMutationString17359 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1375_failAssert0null26895_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16BE");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf16le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                in = ParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32BE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains(null);
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1375 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1375_failAssert0null26895 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1402_failAssert0_literalMutationString16304_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16BE");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf16le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf32be.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32BE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bomP_utf32le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1402 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1402_failAssert0_literalMutationString16304 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1379_failAssert0_literalMutationString18587_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16BE");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf16le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                in = ParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32BE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1379 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1379_failAssert0_literalMutationString18587 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1399_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
            Document doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-16BE");
            doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf16le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-16LE");
            doc.text().contains("??????");
            doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf32be.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-32BE");
            doc.text().contains("??????");
            doc.text().contains("??????");
            doc.text().contains("??????");
            in = ParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-32LE");
            doc.text().contains("??????");
            doc.text().contains("??????");
            doc.text().contains("??????");
            doc.text().contains("??????");
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1399 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1326_failAssert0_literalMutationString17466_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16BE");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf16le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf32be.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32BE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32LE");
                doc.text().contains("??????");
                doc.text().contains("  ");
                doc.text().contains("??????");
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1326 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1326_failAssert0_literalMutationString17466 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }
}

