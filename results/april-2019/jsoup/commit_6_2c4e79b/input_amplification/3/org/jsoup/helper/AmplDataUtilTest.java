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
    public void supportsBOMinFiles_literalMutationString4060_failAssert0_add19882_failAssert0null34187_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                    Document doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16BE");
                    doc.text().contains("가각갂갃간갅");
                    in = ParseTest.getFile("");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16LE");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    in = ParseTest.getFile(null);
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title();
                    doc.title().contains("UTF-32BE");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-32LE");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString4060 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString4060_failAssert0_add19882 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString4060_failAssert0_add19882_failAssert0null34187 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString4108_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
            Document doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-16BE");
            doc.text().contains("가각갂갃간갅");
            in = ParseTest.getFile("/bomtests/bom_utf16le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-16LE");
            doc.text().contains("가각갂갃간갅");
            doc.text().contains("가각갂갃간갅");
            in = ParseTest.getFile("/bomtests/bom_utf32be.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-32BE");
            doc.text().contains("가각갂갃간갅");
            doc.text().contains("가각갂갃간갅");
            doc.text().contains("가각갂갃간갅");
            in = ParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-32LE");
            doc.text().contains("가각갂갃간갅");
            doc.text().contains("가각갂갃간갅");
            doc.text().contains("가각갂갃간갅");
            doc.text().contains("가각갂갃간갅");
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString4108 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString4112_failAssert0_add20029_failAssert0_literalMutationString27081_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                    Document doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16BE");
                    doc.text().contains("가각갂갃간갅");
                    in = ParseTest.getFile("/bomtests/bom_utf16le.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16LE");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    in = ParseTest.getFile("");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-32BE");
                    doc.title().contains("UTF-32BE");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    in = ParseTest.getFile("/bomtests/bom_utf32le.hml");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-32LE");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString4112 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString4112_failAssert0_add20029 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString4112_failAssert0_add20029_failAssert0_literalMutationString27081 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString4060_failAssert0_add19882_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16BE");
                doc.text().contains("가각갂갃간갅");
                in = ParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16LE");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                in = ParseTest.getFile("/bomtests/bom_utf32be.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title();
                doc.title().contains("UTF-32BE");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32LE");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString4060 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString4060_failAssert0_add19882 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString4060_failAssert0_add19882_failAssert0_literalMutationString27636_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                    Document doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16BE");
                    doc.text().contains("가각갂갃간갅");
                    in = ParseTest.getFile("");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16LE");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    in = ParseTest.getFile("/bomtests/bom_utf32be.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title();
                    doc.title().contains("UTF-32BE");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    in = ParseTest.getFile("=gJ;3 &I/A8t!bJq7}t0un/bpH");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-32LE");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString4060 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString4060_failAssert0_add19882 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString4060_failAssert0_add19882_failAssert0_literalMutationString27636 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString4060_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
            Document doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-16BE");
            doc.text().contains("가각갂갃간갅");
            in = ParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-16LE");
            doc.text().contains("가각갂갃간갅");
            doc.text().contains("가각갂갃간갅");
            in = ParseTest.getFile("/bomtests/bom_utf32be.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-32BE");
            doc.text().contains("가각갂갃간갅");
            doc.text().contains("가각갂갃간갅");
            doc.text().contains("가각갂갃간갅");
            in = ParseTest.getFile("/bomtests/bom_utf32le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-32LE");
            doc.text().contains("가각갂갃간갅");
            doc.text().contains("가각갂갃간갅");
            doc.text().contains("가각갂갃간갅");
            doc.text().contains("가각갂갃간갅");
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString4060 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString4060_failAssert0_literalMutationString15821_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16BE");
                doc.text().contains("가각갂갃간갅");
                in = ParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16LE");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                in = ParseTest.getFile("/bomtests/om_utf32be.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32BE");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32LE");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString4060 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString4060_failAssert0_literalMutationString15821 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString4036_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-16BE");
            doc.text().contains("가각갂갃간갅");
            in = ParseTest.getFile("/bomtests/bom_utf16le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-16LE");
            doc.text().contains("가각갂갃간갅");
            doc.text().contains("가각갂갃간갅");
            in = ParseTest.getFile("/bomtests/bom_utf32be.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-32BE");
            doc.text().contains("가각갂갃간갅");
            doc.text().contains("가각갂갃간갅");
            doc.text().contains("가각갂갃간갅");
            in = ParseTest.getFile("/bomtests/bom_utf32le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            doc.title().contains("UTF-32LE");
            doc.text().contains("가각갂갃간갅");
            doc.text().contains("가각갂갃간갅");
            doc.text().contains("가각갂갃간갅");
            doc.text().contains("가각갂갃간갅");
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString4036 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString4060_failAssert0_add19882_failAssert0_add32261_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                    Document doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16BE");
                    doc.text().contains("가각갂갃간갅");
                    in = ParseTest.getFile("");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16LE");
                    doc.text().contains("가각갂갃간갅");
                    doc.text();
                    doc.text().contains("가각갂갃간갅");
                    in = ParseTest.getFile("/bomtests/bom_utf32be.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title();
                    doc.title().contains("UTF-32BE");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-32LE");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    doc.text().contains("가각갂갃간갅");
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString4060 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString4060_failAssert0_add19882 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString4060_failAssert0_add19882_failAssert0_add32261 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }
}

