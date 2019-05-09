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
    public void supportsBOMinFiles_literalMutationString1298_failAssert0() throws IOException {
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1298 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1298_failAssert0_add17114_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16BE");
                doc.text().contains("가각갂갃간갅");
                in = ParseTest.getFile("/bomtests/bom_utf16le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title();
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
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1298 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1298_failAssert0_add17114 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1252_failAssert0() throws IOException {
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1252 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add1339_literalMutationString2467_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
            Document doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_add1339__6 = doc.title().contains("UTF-16BE");
            boolean o_supportsBOMinFiles_add1339__8 = doc.text().contains("가각갂갃간갅");
            in = ParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_add1339__14 = doc.title().contains("UTF-16LE");
            boolean o_supportsBOMinFiles_add1339__16 = doc.text().contains("가각갂갃간갅");
            boolean o_supportsBOMinFiles_add1339__18 = doc.text().contains("가각갂갃간갅");
            in = ParseTest.getFile("/bomtests/bom_utf32be.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_add1339__24 = doc.title().contains("UTF-32BE");
            String o_supportsBOMinFiles_add1339__26 = doc.text();
            boolean o_supportsBOMinFiles_add1339__27 = doc.text().contains("가각갂갃간갅");
            boolean o_supportsBOMinFiles_add1339__29 = doc.text().contains("가각갂갃간갅");
            boolean o_supportsBOMinFiles_add1339__31 = doc.text().contains("가각갂갃간갅");
            in = ParseTest.getFile("/bomtests/bom_utf32le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_add1339__37 = doc.title().contains("UTF-32LE");
            boolean o_supportsBOMinFiles_add1339__39 = doc.text().contains("가각갂갃간갅");
            boolean o_supportsBOMinFiles_add1339__41 = doc.text().contains("가각갂갃간갅");
            boolean o_supportsBOMinFiles_add1339__43 = doc.text().contains("가각갂갃간갅");
            boolean o_supportsBOMinFiles_add1339__45 = doc.text().contains("가각갂갃간갅");
            org.junit.Assert.fail("supportsBOMinFiles_add1339_literalMutationString2467 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add1338_literalMutationString3439_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
            Document doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_add1338__6 = doc.title().contains("UTF-16BE");
            boolean o_supportsBOMinFiles_add1338__8 = doc.text().contains("가각갂갃간갅");
            in = ParseTest.getFile("/bomtests/bom_utf16le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_add1338__14 = doc.title().contains("UTF-16LE");
            boolean o_supportsBOMinFiles_add1338__16 = doc.text().contains("가각갂갃간갅");
            boolean o_supportsBOMinFiles_add1338__18 = doc.text().contains("가각갂갃간갅");
            in = ParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_add1338__24 = doc.title().contains("UTF-32BE");
            boolean o_supportsBOMinFiles_add1338__26 = doc.text().contains("가각갂갃간갅");
            boolean o_supportsBOMinFiles_add1338__28 = doc.text().contains("가각갂갃간갅");
            boolean o_supportsBOMinFiles_add1338__30 = doc.text().contains("가각갂갃간갅");
            boolean o_supportsBOMinFiles_add1338__32 = doc.text().contains("가각갂갃간갅");
            in = ParseTest.getFile("/bomtests/bom_utf32le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_add1338__38 = doc.title().contains("UTF-32LE");
            boolean o_supportsBOMinFiles_add1338__40 = doc.text().contains("가각갂갃간갅");
            boolean o_supportsBOMinFiles_add1338__42 = doc.text().contains("가각갂갃간갅");
            boolean o_supportsBOMinFiles_add1338__44 = doc.text().contains("가각갂갃간갅");
            boolean o_supportsBOMinFiles_add1338__46 = doc.text().contains("가각갂갃간갅");
            org.junit.Assert.fail("supportsBOMinFiles_add1338_literalMutationString3439 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1252_failAssert0_literalMutationString13309_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16BE");
                doc.text().contains("가각갂갃간갅");
                in = ParseTest.getFile("");
                doc = Jsoup.parse(in, null, "htt;p://example.com");
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
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1252 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1252_failAssert0_literalMutationString13309 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1298_failAssert0_literalMutationString12964_failAssert0() throws IOException {
        try {
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
                in = ParseTest.getFile("/bomtests/bom_utf32be.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32BE");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("");
                doc.text().contains("가각갂갃간갅");
                in = ParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32LE");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1298 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1298_failAssert0_literalMutationString12964 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1252_failAssert0_add17230_failAssert0() throws IOException {
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
                ParseTest.getFile("/bomtests/bom_utf32be.html");
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
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1252 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1252_failAssert0_add17230 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1298_failAssert0_literalMutationString12917_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16BE");
                doc.text().contains("가각갂갃간갅");
                in = ParseTest.getFile("/bomtests/bom_utf16le.html");
                doc = Jsoup.parse(in, null, "  ");
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
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1298 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1298_failAssert0_literalMutationString12917 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1298_failAssert0null19667_failAssert0() throws IOException {
        try {
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
                in = ParseTest.getFile("/bomtests/bom_utf32be.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32BE");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                in = ParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains(null);
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1298 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1298_failAssert0null19667 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1298_failAssert0_add17141_failAssert0() throws IOException {
        try {
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
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1298 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1298_failAssert0_add17141 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }
}

