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
    public void supportsBOMinFiles_literalMutationString7178_failAssert0_literalMutationString23568_failAssert0_add60594_failAssert0() throws IOException {
        try {
            {
                {
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
                    doc.text().contains("??????");
                    in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-32LE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    doc.text().contains("\uac00\uac01\uac02\uac03\uac05");
                    doc.text().contains("??????");
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_literalMutationString23568 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_literalMutationString23568_failAssert0_add60594 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7153_failAssert0null33250_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains(null);
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
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153_failAssert0null33250 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7153_failAssert0_add29774_failAssert0() throws IOException {
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
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153_failAssert0_add29774 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7178_failAssert0null33030_failAssert0() throws IOException {
        try {
            {
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
                doc.text().contains(null);
                in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0null33030 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7178_failAssert0_literalMutationString23568_failAssert0() throws IOException {
        try {
            {
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
                doc.text().contains("\uac00\uac01\uac02\uac03\uac05");
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_literalMutationString23568 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7226_failAssert0_add29277_failAssert0_add56824_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                    Document doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16BE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    in = ParseTest.getFile("/bomtests/bom_utf16le.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16LE");
                    doc.text();
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
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7226 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7226_failAssert0_add29277 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7226_failAssert0_add29277_failAssert0_add56824 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7226_failAssert0() throws IOException {
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7226 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7153_failAssert0() throws IOException {
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7178_failAssert0_literalMutationString23568_failAssert0_literalMutationString53665_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                    Document doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16BE");
                    doc.text().contains("??????");
                    in = ParseTest.getFile("");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16LE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    in = ParseTest.getFile("/bomtests/bom_utf32>e.html");
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
                    doc.text().contains("\uac00\uac01\uac02\uac03\uac05");
                    doc.text().contains("??????");
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_literalMutationString23568 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_literalMutationString23568_failAssert0_literalMutationString53665 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7178_failAssert0_add29435_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16BE");
                doc.text().contains("??????");
                in = ParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                ParseTest.getFile("/bomtests/bom_utf32be.html");
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
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_add29435 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7153_failAssert0null33250_failAssert0_add58507_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains(null);
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
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153_failAssert0null33250 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153_failAssert0null33250_failAssert0_add58507 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7153_failAssert0null33250_failAssert0_add58498_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains(null);
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
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153_failAssert0null33250 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153_failAssert0null33250_failAssert0_add58498 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7178_failAssert0_literalMutationString23568_failAssert0null64890_failAssert0() throws IOException {
        try {
            {
                {
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
                    doc.text().contains(null);
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-32LE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    doc.text().contains("\uac00\uac01\uac02\uac03\uac05");
                    doc.text().contains("??????");
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_literalMutationString23568 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_literalMutationString23568_failAssert0null64890 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7153_failAssert0null33250_failAssert0null63448_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains(null);
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
                    doc.text().contains(null);
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153_failAssert0null33250 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153_failAssert0null33250_failAssert0null63448 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7178_failAssert0_add29435_failAssert0_literalMutationString39265_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                    Document doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16BE");
                    doc.text().contains("??????");
                    in = ParseTest.getFile("");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16LE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    ParseTest.getFile("/bomtests/bom_utf32be.html");
                    in = ParseTest.getFile("/bomtests/bom_utf32be.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-32BE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                    doc = Jsoup.parse(in, null, "http:{/example.com");
                    doc.title().contains("UTF-32LE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_add29435 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_add29435_failAssert0_literalMutationString39265 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7178_failAssert0_add29435_failAssert0_add56366_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                    Document doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16BE");
                    doc.text().contains("??????");
                    in = ParseTest.getFile("");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16LE");
                    doc.title().contains("UTF-16LE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    ParseTest.getFile("/bomtests/bom_utf32be.html");
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
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_add29435 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_add29435_failAssert0_add56366 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7226_failAssert0_literalMutationString23050_failAssert0() throws IOException {
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
                doc.text().contains("\uac00\uac01\uac02\uac03\uac05");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7226 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7226_failAssert0_literalMutationString23050 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7178_failAssert0_add29435_failAssert0_literalMutationString39224_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                    Document doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16BE");
                    doc.text().contains("??????");
                    in = ParseTest.getFile("");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16LE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    ParseTest.getFile("/bomtests/bom_utf32be.html");
                    in = ParseTest.getFile("/bomtesss/bom_utf32be.html");
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
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_add29435 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_add29435_failAssert0_literalMutationString39224 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7178_failAssert0() throws IOException {
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7178_failAssert0_add29435_failAssert0_add56387_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                    Document doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16BE");
                    doc.text().contains("??????");
                    in = ParseTest.getFile("");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16LE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    ParseTest.getFile("/bomtests/bom_utf32be.html");
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
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_add29435 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_add29435_failAssert0_add56387 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7201_failAssert0() throws IOException {
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7201 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7226_failAssert0_literalMutationString23050_failAssert0_literalMutationString49866_failAssert0() throws IOException {
        try {
            {
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
                    doc.text().contains("\uac00\uac01\uac024\uac04\uac05");
                    in = ParseTest.getFile("");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-32LE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    doc.text().contains("\uac00\uac01\uac02\uac03\uac05");
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7226 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7226_failAssert0_literalMutationString23050 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7226_failAssert0_literalMutationString23050_failAssert0_literalMutationString49866 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7178_failAssert0_literalMutationString23509_failAssert0() throws IOException {
        try {
            {
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
                doc = Jsoup.parse(in, null, "410$p#d?1(Qr!.$uV0");
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
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_literalMutationString23509 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7226_failAssert0_add29277_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16BE");
                doc.text().contains("??????");
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
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7226 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7226_failAssert0_add29277 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7178_failAssert0_add29435_failAssert0null61995_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                    Document doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16BE");
                    doc.text().contains("??????");
                    in = ParseTest.getFile("");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16LE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    ParseTest.getFile(null);
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
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_add29435 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_add29435_failAssert0null61995 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7201_failAssert0_literalMutationString24488_failAssert0() throws IOException {
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
                doc.text().contains("E1,(]I");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7201 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7201_failAssert0_literalMutationString24488 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7178_failAssert0_add29435_failAssert0null62005_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                    Document doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16BE");
                    doc.text().contains("??????");
                    in = ParseTest.getFile("");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16LE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    ParseTest.getFile("/bomtests/bom_utf32be.html");
                    in = ParseTest.getFile("/bomtests/bom_utf32be.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-32BE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                    doc = Jsoup.parse(in, null, null);
                    doc.title().contains("UTF-32LE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_add29435 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_add29435_failAssert0null62005 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7201_failAssert0null33208_failAssert0() throws IOException {
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
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7201 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7201_failAssert0null33208 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7201_failAssert0_add29690_failAssert0() throws IOException {
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
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7201 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7201_failAssert0_add29690 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7178_failAssert0_add29425_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16BE");
                doc.text().contains("??????");
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
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_add29425 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7153_failAssert0_literalMutationString24696_failAssert0() throws IOException {
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
                doc.text().contains("qjMCP,");
                doc.text().contains("??????");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153_failAssert0_literalMutationString24696 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7168_literalMutationString17566_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
            Document doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString7168__6 = doc.title().contains("UTF-1i6BE");
            boolean o_supportsBOMinFiles_literalMutationString7168__8 = doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf16le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString7168__14 = doc.title().contains("UTF-16LE");
            boolean o_supportsBOMinFiles_literalMutationString7168__16 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7168__18 = doc.text().contains("??????");
            in = ParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString7168__24 = doc.title().contains("UTF-32BE");
            boolean o_supportsBOMinFiles_literalMutationString7168__26 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7168__28 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7168__30 = doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf32le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString7168__36 = doc.title().contains("UTF-32LE");
            boolean o_supportsBOMinFiles_literalMutationString7168__38 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7168__40 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7168__42 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7168__44 = doc.text().contains("??????");
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7168_literalMutationString17566 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7153_failAssert0null33250_failAssert0_literalMutationString46476_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains(null);
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
                    doc.text().contains("u\uac01\uac02\uac03\uac04\uac05");
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153_failAssert0null33250 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153_failAssert0null33250_failAssert0_literalMutationString46476 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7222_literalMutationString11325_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
            Document doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString7222__6 = doc.title().contains("UTF-16BE");
            boolean o_supportsBOMinFiles_literalMutationString7222__8 = doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf16le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString7222__14 = doc.title().contains("UTF-16LE");
            boolean o_supportsBOMinFiles_literalMutationString7222__16 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7222__18 = doc.text().contains("??????");
            in = ParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString7222__24 = doc.title().contains("UTF-32BE");
            boolean o_supportsBOMinFiles_literalMutationString7222__26 = doc.text().contains("vn}+zt");
            in = ParseTest.getFile("/bomtests/bom_utf32le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString7222__32 = doc.title().contains("UTF-32LE");
            boolean o_supportsBOMinFiles_literalMutationString7222__34 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7222__36 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7222__38 = doc.text().contains("??????");
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7222_literalMutationString11325 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7153_failAssert0null33250_failAssert0_literalMutationString46443_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains(null);
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
                    doc = Jsoup.parse(in, null, "htt://example.com");
                    doc.title().contains("UTF-32LE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153_failAssert0null33250 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153_failAssert0null33250_failAssert0_literalMutationString46443 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7166_add26279_literalMutationString35557_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
            Document doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString7166__6 = doc.title().contains("  ");
            boolean o_supportsBOMinFiles_literalMutationString7166__8 = doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf16le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString7166__14 = doc.title().contains("UTF-16LE");
            boolean o_supportsBOMinFiles_literalMutationString7166__16 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7166__18 = doc.text().contains("??????");
            in = ParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString7166__24 = doc.title().contains("UTF-32BE");
            boolean o_supportsBOMinFiles_literalMutationString7166__26 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7166__28 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7166__30 = doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf32le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString7166__36 = doc.title().contains("UTF-32LE");
            boolean o_supportsBOMinFiles_literalMutationString7166__38 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7166_add26279__62 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7166__40 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7166__42 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7166__44 = doc.text().contains("??????");
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7166_add26279_literalMutationString35557 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7178_failAssert0_literalMutationString23568_failAssert0_add60602_failAssert0() throws IOException {
        try {
            {
                {
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
                    doc.title().contains("UTF-32LE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    doc.text().contains("\uac00\uac01\uac02\uac03\uac05");
                    doc.text().contains("??????");
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_literalMutationString23568 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_literalMutationString23568_failAssert0_add60602 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7226_failAssert0_add29277_failAssert0_literalMutationString40809_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                    Document doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16BE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    in = ParseTest.getFile("/bomtests/bom_utf16le.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16LE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    in = ParseTest.getFile("/bomtests/bom_utf32be.html");
                    doc = Jsoup.parse(in, null, "o`fpIGLnzOK!x4EoDj");
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
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7226 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7226_failAssert0_add29277 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7226_failAssert0_add29277_failAssert0_literalMutationString40809 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7153_failAssert0null33250_failAssert0null63432_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains(null);
                    doc.text().contains("??????");
                    in = ParseTest.getFile("/bomtests/bom_utf16le.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16LE");
                    doc.text().contains(null);
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
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153_failAssert0null33250 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7153_failAssert0null33250_failAssert0null63432 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7178_failAssert0_literalMutationString23568_failAssert0_literalMutationString53713_failAssert0() throws IOException {
        try {
            {
                {
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
                    doc.title().contains("UT-32LE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    doc.text().contains("\uac00\uac01\uac02\uac03\uac05");
                    doc.text().contains("??????");
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_literalMutationString23568 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7178_failAssert0_literalMutationString23568_failAssert0_literalMutationString53713 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7272_literalMutationString8235_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
            Document doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_add7272__6 = doc.title().contains("UTF-16BE");
            boolean o_supportsBOMinFiles_add7272__8 = doc.text().contains("??????");
            in = ParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_add7272__14 = doc.title().contains("UTF-16LE");
            boolean o_supportsBOMinFiles_add7272__16 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_add7272__18 = doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf32be.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_add7272__24 = doc.title().contains("UTF-32BE");
            boolean o_supportsBOMinFiles_add7272__26 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_add7272__28 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_add7272__30 = doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf32le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_add7272__36 = doc.title().contains("UTF-32LE");
            String o_supportsBOMinFiles_add7272__38 = doc.text();
            boolean o_supportsBOMinFiles_add7272__39 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_add7272__41 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_add7272__43 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_add7272__45 = doc.text().contains("??????");
            org.junit.Assert.fail("supportsBOMinFiles_add7272_literalMutationString8235 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7164_add28304_literalMutationString35290_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
            Document doc = Jsoup.parse(in, null, ",1hhRM,/WOWr^zWFuV");
            boolean o_supportsBOMinFiles_literalMutationString7164__6 = doc.title().contains("UTF-16BE");
            boolean o_supportsBOMinFiles_literalMutationString7164__8 = doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf16le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString7164__14 = doc.title().contains("UTF-16LE");
            boolean o_supportsBOMinFiles_literalMutationString7164__16 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7164__18 = doc.text().contains("??????");
            in = ParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString7164__24 = doc.title().contains("UTF-32BE");
            boolean o_supportsBOMinFiles_literalMutationString7164__26 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7164__28 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7164__30 = doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf32le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString7164__36 = doc.title().contains("UTF-32LE");
            boolean o_supportsBOMinFiles_literalMutationString7164__38 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7164__40 = doc.text().contains("??????");
            String o_supportsBOMinFiles_literalMutationString7164_add28304__66 = doc.text();
            boolean o_supportsBOMinFiles_literalMutationString7164__42 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7164__44 = doc.text().contains("??????");
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7164_add28304_literalMutationString35290 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }
}

