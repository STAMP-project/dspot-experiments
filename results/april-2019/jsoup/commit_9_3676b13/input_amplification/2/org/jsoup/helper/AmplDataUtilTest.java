package org.jsoup.helper;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
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
    public void supportsBOMinFiles_add1339_literalMutationString4800() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1339__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add1339__8 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1339__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_add1339__16 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_add1339__18 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1339__24 = doc.title().contains("UTF-32BE");
        String o_supportsBOMinFiles_add1339__26 = doc.text();
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1339__26);
        boolean o_supportsBOMinFiles_add1339__27 = doc.text().contains("");
        boolean o_supportsBOMinFiles_add1339__29 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_add1339__31 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1339__37 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_add1339__39 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_add1339__41 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_add1339__43 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_add1339__45 = doc.text().contains("가각갂갃간갅");
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1339__26);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1274_failAssert0_literalMutationString12714_failAssert0() throws IOException {
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
                in = ParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32BE");
                doc.text().contains("");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32LE");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1274 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1274_failAssert0_literalMutationString12714 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1298_failAssert0_literalMutationString13251_failAssert0() throws IOException {
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
                doc.text().contains("");
                in = ParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32LE");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1298 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1298_failAssert0_literalMutationString13251 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1274_failAssert0() throws IOException {
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
            in = ParseTest.getFile("");
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1274 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1274_failAssert0_add17069_failAssert0() throws IOException {
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
                in = ParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32BE");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                doc.text();
                doc.text().contains("가각갂갃간갅");
                in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32LE");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1274 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1274_failAssert0_add17069 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add1339() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1339__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1339__6);
        boolean o_supportsBOMinFiles_add1339__8 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1339__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1339__14 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1339__14);
        boolean o_supportsBOMinFiles_add1339__16 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1339__16);
        boolean o_supportsBOMinFiles_add1339__18 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1339__18);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1339__24 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1339__24);
        String o_supportsBOMinFiles_add1339__26 = doc.text();
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1339__26);
        boolean o_supportsBOMinFiles_add1339__27 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1339__27);
        boolean o_supportsBOMinFiles_add1339__29 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1339__29);
        boolean o_supportsBOMinFiles_add1339__31 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1339__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1339__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1339__37);
        boolean o_supportsBOMinFiles_add1339__39 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1339__39);
        boolean o_supportsBOMinFiles_add1339__41 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1339__41);
        boolean o_supportsBOMinFiles_add1339__43 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1339__43);
        boolean o_supportsBOMinFiles_add1339__45 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1339__45);
        Assert.assertTrue(o_supportsBOMinFiles_add1339__6);
        Assert.assertTrue(o_supportsBOMinFiles_add1339__8);
        Assert.assertTrue(o_supportsBOMinFiles_add1339__14);
        Assert.assertTrue(o_supportsBOMinFiles_add1339__16);
        Assert.assertTrue(o_supportsBOMinFiles_add1339__18);
        Assert.assertTrue(o_supportsBOMinFiles_add1339__24);
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1339__26);
        Assert.assertTrue(o_supportsBOMinFiles_add1339__27);
        Assert.assertTrue(o_supportsBOMinFiles_add1339__29);
        Assert.assertTrue(o_supportsBOMinFiles_add1339__31);
        Assert.assertTrue(o_supportsBOMinFiles_add1339__37);
        Assert.assertTrue(o_supportsBOMinFiles_add1339__39);
        Assert.assertTrue(o_supportsBOMinFiles_add1339__41);
        Assert.assertTrue(o_supportsBOMinFiles_add1339__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add1323() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document o_supportsBOMinFiles_add1323__4 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add1323__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add1323__4)).hasText());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1323__4)).isBlock());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-16BE\"> \n  <title>UTF-16BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-16BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add1323__4)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1323__4)).hasParent());
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1323__7 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1323__7);
        boolean o_supportsBOMinFiles_add1323__9 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1323__9);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1323__15 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1323__15);
        boolean o_supportsBOMinFiles_add1323__17 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1323__17);
        boolean o_supportsBOMinFiles_add1323__19 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1323__19);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1323__25 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1323__25);
        boolean o_supportsBOMinFiles_add1323__27 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1323__27);
        boolean o_supportsBOMinFiles_add1323__29 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1323__29);
        boolean o_supportsBOMinFiles_add1323__31 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1323__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1323__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1323__37);
        boolean o_supportsBOMinFiles_add1323__39 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1323__39);
        boolean o_supportsBOMinFiles_add1323__41 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1323__41);
        boolean o_supportsBOMinFiles_add1323__43 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1323__43);
        boolean o_supportsBOMinFiles_add1323__45 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1323__45);
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add1323__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add1323__4)).hasText());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1323__4)).isBlock());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-16BE\"> \n  <title>UTF-16BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-16BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add1323__4)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1323__4)).hasParent());
        Assert.assertTrue(o_supportsBOMinFiles_add1323__7);
        Assert.assertTrue(o_supportsBOMinFiles_add1323__9);
        Assert.assertTrue(o_supportsBOMinFiles_add1323__15);
        Assert.assertTrue(o_supportsBOMinFiles_add1323__17);
        Assert.assertTrue(o_supportsBOMinFiles_add1323__19);
        Assert.assertTrue(o_supportsBOMinFiles_add1323__25);
        Assert.assertTrue(o_supportsBOMinFiles_add1323__27);
        Assert.assertTrue(o_supportsBOMinFiles_add1323__29);
        Assert.assertTrue(o_supportsBOMinFiles_add1323__31);
        Assert.assertTrue(o_supportsBOMinFiles_add1323__37);
        Assert.assertTrue(o_supportsBOMinFiles_add1323__39);
        Assert.assertTrue(o_supportsBOMinFiles_add1323__41);
        Assert.assertTrue(o_supportsBOMinFiles_add1323__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add1345() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1345__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1345__6);
        boolean o_supportsBOMinFiles_add1345__8 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1345__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1345__14 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1345__14);
        boolean o_supportsBOMinFiles_add1345__16 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1345__16);
        boolean o_supportsBOMinFiles_add1345__18 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1345__18);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1345__24 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1345__24);
        boolean o_supportsBOMinFiles_add1345__26 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1345__26);
        boolean o_supportsBOMinFiles_add1345__28 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1345__28);
        boolean o_supportsBOMinFiles_add1345__30 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1345__30);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1345__36 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1345__36);
        String o_supportsBOMinFiles_add1345__38 = doc.text();
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1345__38);
        boolean o_supportsBOMinFiles_add1345__39 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1345__39);
        boolean o_supportsBOMinFiles_add1345__41 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1345__41);
        boolean o_supportsBOMinFiles_add1345__43 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1345__43);
        boolean o_supportsBOMinFiles_add1345__45 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1345__45);
        Assert.assertTrue(o_supportsBOMinFiles_add1345__6);
        Assert.assertTrue(o_supportsBOMinFiles_add1345__8);
        Assert.assertTrue(o_supportsBOMinFiles_add1345__14);
        Assert.assertTrue(o_supportsBOMinFiles_add1345__16);
        Assert.assertTrue(o_supportsBOMinFiles_add1345__18);
        Assert.assertTrue(o_supportsBOMinFiles_add1345__24);
        Assert.assertTrue(o_supportsBOMinFiles_add1345__26);
        Assert.assertTrue(o_supportsBOMinFiles_add1345__28);
        Assert.assertTrue(o_supportsBOMinFiles_add1345__30);
        Assert.assertTrue(o_supportsBOMinFiles_add1345__36);
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1345__38);
        Assert.assertTrue(o_supportsBOMinFiles_add1345__39);
        Assert.assertTrue(o_supportsBOMinFiles_add1345__41);
        Assert.assertTrue(o_supportsBOMinFiles_add1345__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1298_failAssert0_add17210_failAssert0() throws IOException {
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1298_failAssert0_add17210 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add1341() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1341__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1341__6);
        boolean o_supportsBOMinFiles_add1341__8 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1341__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1341__14 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1341__14);
        boolean o_supportsBOMinFiles_add1341__16 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1341__16);
        boolean o_supportsBOMinFiles_add1341__18 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1341__18);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1341__24 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1341__24);
        boolean o_supportsBOMinFiles_add1341__26 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1341__26);
        boolean o_supportsBOMinFiles_add1341__28 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1341__28);
        boolean o_supportsBOMinFiles_add1341__30 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1341__30);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        Document o_supportsBOMinFiles_add1341__34 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add1341__34)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add1341__34)).hasText());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1341__34)).isBlock());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32LE\"> \n  <title>UTF-32LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add1341__34)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1341__34)).hasParent());
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1341__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1341__37);
        boolean o_supportsBOMinFiles_add1341__39 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1341__39);
        boolean o_supportsBOMinFiles_add1341__41 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1341__41);
        boolean o_supportsBOMinFiles_add1341__43 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1341__43);
        boolean o_supportsBOMinFiles_add1341__45 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1341__45);
        Assert.assertTrue(o_supportsBOMinFiles_add1341__6);
        Assert.assertTrue(o_supportsBOMinFiles_add1341__8);
        Assert.assertTrue(o_supportsBOMinFiles_add1341__14);
        Assert.assertTrue(o_supportsBOMinFiles_add1341__16);
        Assert.assertTrue(o_supportsBOMinFiles_add1341__18);
        Assert.assertTrue(o_supportsBOMinFiles_add1341__24);
        Assert.assertTrue(o_supportsBOMinFiles_add1341__26);
        Assert.assertTrue(o_supportsBOMinFiles_add1341__28);
        Assert.assertTrue(o_supportsBOMinFiles_add1341__30);
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add1341__34)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add1341__34)).hasText());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1341__34)).isBlock());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32LE\"> \n  <title>UTF-32LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add1341__34)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1341__34)).hasParent());
        Assert.assertTrue(o_supportsBOMinFiles_add1341__37);
        Assert.assertTrue(o_supportsBOMinFiles_add1341__39);
        Assert.assertTrue(o_supportsBOMinFiles_add1341__41);
        Assert.assertTrue(o_supportsBOMinFiles_add1341__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add1345_literalMutationString5966() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "httsp://example.com");
        boolean o_supportsBOMinFiles_add1345__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add1345__8 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1345__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_add1345__16 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_add1345__18 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1345__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_add1345__26 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_add1345__28 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_add1345__30 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1345__36 = doc.title().contains("UTF-32LE");
        String o_supportsBOMinFiles_add1345__38 = doc.text();
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1345__38);
        boolean o_supportsBOMinFiles_add1345__39 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_add1345__41 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_add1345__43 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_add1345__45 = doc.text().contains("가각갂갃간갅");
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1345__38);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1311_add14174() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1311__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString1311__8 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1311__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString1311__16 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_literalMutationString1311__18 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1311__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString1311__26 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_literalMutationString1311__28 = doc.text().contains("가각갂갃간갅");
        String o_supportsBOMinFiles_literalMutationString1311_add14174__46 = doc.text();
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString1311_add14174__46);
        boolean o_supportsBOMinFiles_literalMutationString1311__30 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1311__36 = doc.title().contains("");
        boolean o_supportsBOMinFiles_literalMutationString1311__38 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_literalMutationString1311__40 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_literalMutationString1311__42 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_literalMutationString1311__44 = doc.text().contains("가각갂갃간갅");
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString1311_add14174__46);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add1345_add15193() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1345__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add1345__8 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1345__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_add1345__16 = doc.text().contains("가각갂갃간갅");
        String o_supportsBOMinFiles_add1345_add15193__26 = doc.text();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1345_add15193__26);
        boolean o_supportsBOMinFiles_add1345__18 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1345__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_add1345__26 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_add1345__28 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_add1345__30 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1345__36 = doc.title().contains("UTF-32LE");
        String o_supportsBOMinFiles_add1345__38 = doc.text();
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1345__38);
        boolean o_supportsBOMinFiles_add1345__39 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_add1345__41 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_add1345__43 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_add1345__45 = doc.text().contains("가각갂갃간갅");
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1345_add15193__26);
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1345__38);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add1338_add15010() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1338__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add1338__8 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1338__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_add1338__16 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_add1338__18 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1338__24 = doc.title().contains("UTF-32BE");
        String o_supportsBOMinFiles_add1338_add15010__38 = doc.text();
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1338_add15010__38);
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
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1338_add15010__38);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1284_add15744() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1284__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString1284__8 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1284__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString1284__16 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_literalMutationString1284__18 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.zom");
        boolean o_supportsBOMinFiles_literalMutationString1284__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString1284__26 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_literalMutationString1284__28 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_literalMutationString1284__30 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        Document o_supportsBOMinFiles_literalMutationString1284_add15744__52 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString1284_add15744__52)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_literalMutationString1284_add15744__52)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_literalMutationString1284_add15744__52)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32LE\"> \n  <title>UTF-32LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_literalMutationString1284_add15744__52)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString1284_add15744__52)).hasParent());
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1284__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString1284__38 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_literalMutationString1284__40 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_literalMutationString1284__42 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_literalMutationString1284__44 = doc.text().contains("가각갂갃간갅");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString1284_add15744__52)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_literalMutationString1284_add15744__52)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_literalMutationString1284_add15744__52)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32LE\"> \n  <title>UTF-32LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_literalMutationString1284_add15744__52)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString1284_add15744__52)).hasParent());
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add1339_add14815() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1339__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add1339__8 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1339_add14815__18 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1339_add14815__18);
        boolean o_supportsBOMinFiles_add1339__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_add1339__16 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_add1339__18 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1339__24 = doc.title().contains("UTF-32BE");
        String o_supportsBOMinFiles_add1339__26 = doc.text();
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1339__26);
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
        Assert.assertTrue(o_supportsBOMinFiles_add1339_add14815__18);
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1339__26);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add1327() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1327__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1327__6);
        String o_supportsBOMinFiles_add1327__8 = doc.text();
        Assert.assertEquals("UTF-16BE Encoded Korean Page with BOM UTF-16BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1327__8);
        boolean o_supportsBOMinFiles_add1327__9 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1327__9);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1327__15 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1327__15);
        boolean o_supportsBOMinFiles_add1327__17 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1327__17);
        boolean o_supportsBOMinFiles_add1327__19 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1327__19);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1327__25 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1327__25);
        boolean o_supportsBOMinFiles_add1327__27 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1327__27);
        boolean o_supportsBOMinFiles_add1327__29 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1327__29);
        boolean o_supportsBOMinFiles_add1327__31 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1327__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1327__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1327__37);
        boolean o_supportsBOMinFiles_add1327__39 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1327__39);
        boolean o_supportsBOMinFiles_add1327__41 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1327__41);
        boolean o_supportsBOMinFiles_add1327__43 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1327__43);
        boolean o_supportsBOMinFiles_add1327__45 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1327__45);
        Assert.assertTrue(o_supportsBOMinFiles_add1327__6);
        Assert.assertEquals("UTF-16BE Encoded Korean Page with BOM UTF-16BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1327__8);
        Assert.assertTrue(o_supportsBOMinFiles_add1327__9);
        Assert.assertTrue(o_supportsBOMinFiles_add1327__15);
        Assert.assertTrue(o_supportsBOMinFiles_add1327__17);
        Assert.assertTrue(o_supportsBOMinFiles_add1327__19);
        Assert.assertTrue(o_supportsBOMinFiles_add1327__25);
        Assert.assertTrue(o_supportsBOMinFiles_add1327__27);
        Assert.assertTrue(o_supportsBOMinFiles_add1327__29);
        Assert.assertTrue(o_supportsBOMinFiles_add1327__31);
        Assert.assertTrue(o_supportsBOMinFiles_add1327__37);
        Assert.assertTrue(o_supportsBOMinFiles_add1327__39);
        Assert.assertTrue(o_supportsBOMinFiles_add1327__41);
        Assert.assertTrue(o_supportsBOMinFiles_add1327__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add1329() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1329__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1329__6);
        boolean o_supportsBOMinFiles_add1329__8 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1329__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        Document o_supportsBOMinFiles_add1329__12 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add1329__12)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add1329__12)).hasText());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1329__12)).isBlock());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-16LE\"> \n  <title>UTF-16LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-16LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add1329__12)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1329__12)).hasParent());
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1329__15 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1329__15);
        boolean o_supportsBOMinFiles_add1329__17 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1329__17);
        boolean o_supportsBOMinFiles_add1329__19 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1329__19);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1329__25 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1329__25);
        boolean o_supportsBOMinFiles_add1329__27 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1329__27);
        boolean o_supportsBOMinFiles_add1329__29 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1329__29);
        boolean o_supportsBOMinFiles_add1329__31 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1329__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1329__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1329__37);
        boolean o_supportsBOMinFiles_add1329__39 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1329__39);
        boolean o_supportsBOMinFiles_add1329__41 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1329__41);
        boolean o_supportsBOMinFiles_add1329__43 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1329__43);
        boolean o_supportsBOMinFiles_add1329__45 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1329__45);
        Assert.assertTrue(o_supportsBOMinFiles_add1329__6);
        Assert.assertTrue(o_supportsBOMinFiles_add1329__8);
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add1329__12)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add1329__12)).hasText());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1329__12)).isBlock());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-16LE\"> \n  <title>UTF-16LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-16LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add1329__12)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1329__12)).hasParent());
        Assert.assertTrue(o_supportsBOMinFiles_add1329__15);
        Assert.assertTrue(o_supportsBOMinFiles_add1329__17);
        Assert.assertTrue(o_supportsBOMinFiles_add1329__19);
        Assert.assertTrue(o_supportsBOMinFiles_add1329__25);
        Assert.assertTrue(o_supportsBOMinFiles_add1329__27);
        Assert.assertTrue(o_supportsBOMinFiles_add1329__29);
        Assert.assertTrue(o_supportsBOMinFiles_add1329__31);
        Assert.assertTrue(o_supportsBOMinFiles_add1329__37);
        Assert.assertTrue(o_supportsBOMinFiles_add1329__39);
        Assert.assertTrue(o_supportsBOMinFiles_add1329__41);
        Assert.assertTrue(o_supportsBOMinFiles_add1329__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1234_add14220() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "  ");
        boolean o_supportsBOMinFiles_literalMutationString1234__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString1234__8 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1234__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString1234__16 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_literalMutationString1234__18 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1234__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString1234__26 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_literalMutationString1234__28 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_literalMutationString1234__30 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1234__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString1234__38 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_literalMutationString1234__40 = doc.text().contains("가각갂갃간갅");
        String o_supportsBOMinFiles_literalMutationString1234_add14220__66 = doc.text();
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString1234_add14220__66);
        boolean o_supportsBOMinFiles_literalMutationString1234__42 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_literalMutationString1234__44 = doc.text().contains("가각갂갃간갅");
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString1234_add14220__66);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add1335() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1335__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1335__6);
        boolean o_supportsBOMinFiles_add1335__8 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1335__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1335__14 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1335__14);
        boolean o_supportsBOMinFiles_add1335__16 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1335__16);
        boolean o_supportsBOMinFiles_add1335__18 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1335__18);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        Document o_supportsBOMinFiles_add1335__22 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add1335__22)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add1335__22)).hasText());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1335__22)).isBlock());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32BE\"> \n  <title>UTF-32BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add1335__22)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1335__22)).hasParent());
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1335__25 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1335__25);
        boolean o_supportsBOMinFiles_add1335__27 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1335__27);
        boolean o_supportsBOMinFiles_add1335__29 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1335__29);
        boolean o_supportsBOMinFiles_add1335__31 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1335__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1335__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1335__37);
        boolean o_supportsBOMinFiles_add1335__39 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1335__39);
        boolean o_supportsBOMinFiles_add1335__41 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1335__41);
        boolean o_supportsBOMinFiles_add1335__43 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1335__43);
        boolean o_supportsBOMinFiles_add1335__45 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1335__45);
        Assert.assertTrue(o_supportsBOMinFiles_add1335__6);
        Assert.assertTrue(o_supportsBOMinFiles_add1335__8);
        Assert.assertTrue(o_supportsBOMinFiles_add1335__14);
        Assert.assertTrue(o_supportsBOMinFiles_add1335__16);
        Assert.assertTrue(o_supportsBOMinFiles_add1335__18);
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add1335__22)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add1335__22)).hasText());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1335__22)).isBlock());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32BE\"> \n  <title>UTF-32BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add1335__22)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1335__22)).hasParent());
        Assert.assertTrue(o_supportsBOMinFiles_add1335__25);
        Assert.assertTrue(o_supportsBOMinFiles_add1335__27);
        Assert.assertTrue(o_supportsBOMinFiles_add1335__29);
        Assert.assertTrue(o_supportsBOMinFiles_add1335__31);
        Assert.assertTrue(o_supportsBOMinFiles_add1335__37);
        Assert.assertTrue(o_supportsBOMinFiles_add1335__39);
        Assert.assertTrue(o_supportsBOMinFiles_add1335__41);
        Assert.assertTrue(o_supportsBOMinFiles_add1335__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1250_failAssert0() throws IOException {
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1250 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1274_failAssert0null19630_failAssert0() throws IOException {
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
                in = ParseTest.getFile("");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32BE");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                in = ParseTest.getFile(null);
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32LE");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                doc.text().contains("가각갂갃간갅");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1274 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1274_failAssert0null19630 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add1333() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1333__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1333__6);
        boolean o_supportsBOMinFiles_add1333__8 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1333__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1333__14 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1333__14);
        String o_supportsBOMinFiles_add1333__16 = doc.text();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1333__16);
        boolean o_supportsBOMinFiles_add1333__17 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1333__17);
        boolean o_supportsBOMinFiles_add1333__19 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1333__19);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1333__25 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1333__25);
        boolean o_supportsBOMinFiles_add1333__27 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1333__27);
        boolean o_supportsBOMinFiles_add1333__29 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1333__29);
        boolean o_supportsBOMinFiles_add1333__31 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1333__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1333__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1333__37);
        boolean o_supportsBOMinFiles_add1333__39 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1333__39);
        boolean o_supportsBOMinFiles_add1333__41 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1333__41);
        boolean o_supportsBOMinFiles_add1333__43 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1333__43);
        boolean o_supportsBOMinFiles_add1333__45 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add1333__45);
        Assert.assertTrue(o_supportsBOMinFiles_add1333__6);
        Assert.assertTrue(o_supportsBOMinFiles_add1333__8);
        Assert.assertTrue(o_supportsBOMinFiles_add1333__14);
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1333__16);
        Assert.assertTrue(o_supportsBOMinFiles_add1333__17);
        Assert.assertTrue(o_supportsBOMinFiles_add1333__19);
        Assert.assertTrue(o_supportsBOMinFiles_add1333__25);
        Assert.assertTrue(o_supportsBOMinFiles_add1333__27);
        Assert.assertTrue(o_supportsBOMinFiles_add1333__29);
        Assert.assertTrue(o_supportsBOMinFiles_add1333__31);
        Assert.assertTrue(o_supportsBOMinFiles_add1333__37);
        Assert.assertTrue(o_supportsBOMinFiles_add1333__39);
        Assert.assertTrue(o_supportsBOMinFiles_add1333__41);
        Assert.assertTrue(o_supportsBOMinFiles_add1333__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1240_add14506() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1240__6 = doc.title().contains("UTF-x16BE");
        boolean o_supportsBOMinFiles_literalMutationString1240__8 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        Document o_supportsBOMinFiles_literalMutationString1240_add14506__16 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString1240_add14506__16)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_literalMutationString1240_add14506__16)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_literalMutationString1240_add14506__16)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-16LE\"> \n  <title>UTF-16LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-16LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_literalMutationString1240_add14506__16)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString1240_add14506__16)).hasParent());
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1240__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString1240__16 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_literalMutationString1240__18 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1240__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString1240__26 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_literalMutationString1240__28 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_literalMutationString1240__30 = doc.text().contains("가각갂갃간갅");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1240__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString1240__38 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_literalMutationString1240__40 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_literalMutationString1240__42 = doc.text().contains("가각갂갃간갅");
        boolean o_supportsBOMinFiles_literalMutationString1240__44 = doc.text().contains("가각갂갃간갅");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString1240_add14506__16)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_literalMutationString1240_add14506__16)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_literalMutationString1240_add14506__16)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-16LE\"> \n  <title>UTF-16LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-16LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_literalMutationString1240_add14506__16)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString1240_add14506__16)).hasParent());
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1298_failAssert0_add17206_failAssert0() throws IOException {
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1298_failAssert0_add17206 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }
}

