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
    public void supportsBOMinFiles_literalMutationString3854_failAssert0_literalMutationString14731_failAssert0() throws IOException {
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
                in = ParseTest.getFile("}z0QT1FrQ*J]VTjK3(IP^Svhr;");
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
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString3854 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString3854_failAssert0_literalMutationString14731 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString3828_failAssert0() throws IOException {
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString3828 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsUTF8BOM_literalMutationString2756_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://example.com");
            doc.head().select("title").text();
            org.junit.Assert.fail("supportsUTF8BOM_literalMutationString2756 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsUTF8BOM_literalMutationString2756_failAssert0_literalMutationString3284_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.head().select("tYtle").text();
                org.junit.Assert.fail("supportsUTF8BOM_literalMutationString2756 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsUTF8BOM_literalMutationString2756_failAssert0_literalMutationString3284 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsUTF8BOM_add2775_literalMutationString2898_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document o_supportsUTF8BOM_add2775__3 = Jsoup.parse(in, null, "http://example.com");
            Document doc = Jsoup.parse(in, null, "http://example.com");
            String o_supportsUTF8BOM_add2775__6 = doc.head().select("title").text();
            org.junit.Assert.fail("supportsUTF8BOM_add2775_literalMutationString2898 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsUTF8BOM_literalMutationString2756_failAssert0_add3474_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.head();
                doc.head().select("title").text();
                org.junit.Assert.fail("supportsUTF8BOM_literalMutationString2756 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsUTF8BOM_literalMutationString2756_failAssert0_add3474 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }
}

