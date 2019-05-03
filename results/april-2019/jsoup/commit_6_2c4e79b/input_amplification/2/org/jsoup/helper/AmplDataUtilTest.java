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
    public void supportsBOMinFiles_literalMutationString1402_failAssert0_literalMutationString16265_failAssert0() throws IOException {
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
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1402 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1402_failAssert0_literalMutationString16265 should have thrown FileNotFoundException");
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
    public void supportsBOMinFiles_literalMutationString1374_failAssert0_literalMutationString16530_failAssert0() throws IOException {
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
                in = ParseTest.getFile("KGbzYXg{Tac>znY3bgv!u{dzK$");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1374 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1374_failAssert0_literalMutationString16530 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1327_failAssert0null27028_failAssert0() throws IOException {
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
                doc.text().contains(null);
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1327 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1327_failAssert0null27028 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1327_failAssert0_add23357_failAssert0() throws IOException {
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
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1327 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1327_failAssert0_add23357 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1327_failAssert0_literalMutationString16712_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16BE");
                doc.text().contains("\uac00\uac01\uac02k\uac04\uac05");
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
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1327 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1327_failAssert0_literalMutationString16712 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1327_failAssert0_literalMutationString16768_failAssert0() throws IOException {
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
                doc.text().contains("2|=j|u");
                doc.text().contains("??????");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1327 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1327_failAssert0_literalMutationString16768 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1327_failAssert0() throws IOException {
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1327 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1326_failAssert0_literalMutationString18662_failAssert0() throws IOException {
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
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1326 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1326_failAssert0_literalMutationString18662 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1398_failAssert0() throws IOException {
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1398 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1374_failAssert0_add23271_failAssert0() throws IOException {
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
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1374 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1374_failAssert0_add23271 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1374_failAssert0() throws IOException {
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1374 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1327_failAssert0_add23360_failAssert0() throws IOException {
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
                Jsoup.parse(in, null, "http://example.com");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1327 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1327_failAssert0_add23360 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }
}

