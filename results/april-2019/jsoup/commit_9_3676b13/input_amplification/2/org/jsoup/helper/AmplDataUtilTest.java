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
    public void supportsBOMinFiles_literalMutationString1340_literalMutationString10410_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
            Document doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString1340__6 = doc.title().contains("UTl-16BE");
            boolean o_supportsBOMinFiles_literalMutationString1340__8 = doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf16le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString1340__14 = doc.title().contains("UTF-16LE");
            boolean o_supportsBOMinFiles_literalMutationString1340__16 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString1340__18 = doc.text().contains("??????");
            in = ParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString1340__24 = doc.title().contains("UTF-32BE");
            boolean o_supportsBOMinFiles_literalMutationString1340__26 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString1340__28 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString1340__30 = doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf32le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString1340__36 = doc.title().contains("UTF-32LE");
            boolean o_supportsBOMinFiles_literalMutationString1340__38 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString1340__40 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString1340__42 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString1340__44 = doc.text().contains("??????");
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1340_literalMutationString10410 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1374_failAssert0_add23898_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16BE");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf16le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16LE");
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
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1374_failAssert0_add23898 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1326_failAssert0_literalMutationString15334_failAssert0() throws IOException {
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
                in = ParseTest.getFile("  ");
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1326_failAssert0_literalMutationString15334 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1326_failAssert0null26740_failAssert0() throws IOException {
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
                doc = Jsoup.parse(in, null, null);
                doc.title().contains("UTF-32LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1326 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1326_failAssert0null26740 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1335_add19349() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://examplecom");
        boolean o_supportsBOMinFiles_literalMutationString1335__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString1335__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1335__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString1335__16 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1335__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1335__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString1335__26 = doc.text().contains("??????");
        String o_supportsBOMinFiles_literalMutationString1335_add19349__42 = doc.text();
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString1335_add19349__42);
        boolean o_supportsBOMinFiles_literalMutationString1335__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1335__30 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1335__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString1335__38 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1335__40 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1335__42 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1335__44 = doc.text().contains("??????");
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString1335_add19349__42);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1397_add21777() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1397__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString1397__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1397__14 = doc.title().contains("UTF-16LE");
        String o_supportsBOMinFiles_literalMutationString1397_add21777__22 = doc.text();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString1397_add21777__22);
        boolean o_supportsBOMinFiles_literalMutationString1397__16 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1397__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1397__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString1397__26 = doc.text().contains("#\uac01\uac02\uac03\uac04\uac05");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1397__32 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString1397__34 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1397__36 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1397__38 = doc.text().contains("??????");
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString1397_add21777__22);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1374_failAssert0_literalMutationString18726_failAssert0() throws IOException {
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
                doc = Jsoup.parse(in, null, "zttp://example.com");
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1374_failAssert0_literalMutationString18726 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add1439() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1439__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1439__6);
        boolean o_supportsBOMinFiles_add1439__8 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1439__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1439__14 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1439__14);
        boolean o_supportsBOMinFiles_add1439__16 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1439__16);
        boolean o_supportsBOMinFiles_add1439__18 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1439__18);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1439__24 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1439__24);
        String o_supportsBOMinFiles_add1439__26 = doc.text();
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1439__26);
        boolean o_supportsBOMinFiles_add1439__27 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1439__27);
        boolean o_supportsBOMinFiles_add1439__29 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1439__29);
        boolean o_supportsBOMinFiles_add1439__31 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1439__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1439__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1439__37);
        boolean o_supportsBOMinFiles_add1439__39 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1439__39);
        boolean o_supportsBOMinFiles_add1439__41 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1439__41);
        boolean o_supportsBOMinFiles_add1439__43 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1439__43);
        boolean o_supportsBOMinFiles_add1439__45 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1439__45);
        Assert.assertTrue(o_supportsBOMinFiles_add1439__6);
        Assert.assertFalse(o_supportsBOMinFiles_add1439__8);
        Assert.assertTrue(o_supportsBOMinFiles_add1439__14);
        Assert.assertFalse(o_supportsBOMinFiles_add1439__16);
        Assert.assertFalse(o_supportsBOMinFiles_add1439__18);
        Assert.assertTrue(o_supportsBOMinFiles_add1439__24);
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1439__26);
        Assert.assertFalse(o_supportsBOMinFiles_add1439__27);
        Assert.assertFalse(o_supportsBOMinFiles_add1439__29);
        Assert.assertFalse(o_supportsBOMinFiles_add1439__31);
        Assert.assertTrue(o_supportsBOMinFiles_add1439__37);
        Assert.assertFalse(o_supportsBOMinFiles_add1439__39);
        Assert.assertFalse(o_supportsBOMinFiles_add1439__41);
        Assert.assertFalse(o_supportsBOMinFiles_add1439__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1332_add19443() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "");
        boolean o_supportsBOMinFiles_literalMutationString1332__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString1332__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1332__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString1332__16 = doc.text().contains("??????");
        String o_supportsBOMinFiles_literalMutationString1332_add19443__26 = doc.text();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString1332_add19443__26);
        boolean o_supportsBOMinFiles_literalMutationString1332__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1332__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString1332__26 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1332__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1332__30 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1332__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString1332__38 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1332__40 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1332__42 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1332__44 = doc.text().contains("??????");
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString1332_add19443__26);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1326_failAssert0_add22923_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16BE");
                doc.text().contains("??????");
                in = ParseTest.getFile("/bomtests/bom_utf16le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16LE");
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1326_failAssert0_add22923 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add1423() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document o_supportsBOMinFiles_add1423__4 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1423__4)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add1423__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add1423__4)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-16BE\"> \n  <title>UTF-16BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-16BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add1423__4)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1423__4)).hasParent());
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1423__7 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1423__7);
        boolean o_supportsBOMinFiles_add1423__9 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1423__9);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1423__15 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1423__15);
        boolean o_supportsBOMinFiles_add1423__17 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1423__17);
        boolean o_supportsBOMinFiles_add1423__19 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1423__19);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1423__25 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1423__25);
        boolean o_supportsBOMinFiles_add1423__27 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1423__27);
        boolean o_supportsBOMinFiles_add1423__29 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1423__29);
        boolean o_supportsBOMinFiles_add1423__31 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1423__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1423__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1423__37);
        boolean o_supportsBOMinFiles_add1423__39 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1423__39);
        boolean o_supportsBOMinFiles_add1423__41 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1423__41);
        boolean o_supportsBOMinFiles_add1423__43 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1423__43);
        boolean o_supportsBOMinFiles_add1423__45 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1423__45);
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1423__4)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add1423__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add1423__4)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-16BE\"> \n  <title>UTF-16BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-16BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add1423__4)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1423__4)).hasParent());
        Assert.assertTrue(o_supportsBOMinFiles_add1423__7);
        Assert.assertFalse(o_supportsBOMinFiles_add1423__9);
        Assert.assertTrue(o_supportsBOMinFiles_add1423__15);
        Assert.assertFalse(o_supportsBOMinFiles_add1423__17);
        Assert.assertFalse(o_supportsBOMinFiles_add1423__19);
        Assert.assertTrue(o_supportsBOMinFiles_add1423__25);
        Assert.assertFalse(o_supportsBOMinFiles_add1423__27);
        Assert.assertFalse(o_supportsBOMinFiles_add1423__29);
        Assert.assertFalse(o_supportsBOMinFiles_add1423__31);
        Assert.assertTrue(o_supportsBOMinFiles_add1423__37);
        Assert.assertFalse(o_supportsBOMinFiles_add1423__39);
        Assert.assertFalse(o_supportsBOMinFiles_add1423__41);
        Assert.assertFalse(o_supportsBOMinFiles_add1423__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add1445() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1445__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1445__6);
        boolean o_supportsBOMinFiles_add1445__8 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1445__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1445__14 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1445__14);
        boolean o_supportsBOMinFiles_add1445__16 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1445__16);
        boolean o_supportsBOMinFiles_add1445__18 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1445__18);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1445__24 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1445__24);
        boolean o_supportsBOMinFiles_add1445__26 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1445__26);
        boolean o_supportsBOMinFiles_add1445__28 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1445__28);
        boolean o_supportsBOMinFiles_add1445__30 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1445__30);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1445__36 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1445__36);
        String o_supportsBOMinFiles_add1445__38 = doc.text();
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1445__38);
        boolean o_supportsBOMinFiles_add1445__39 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1445__39);
        boolean o_supportsBOMinFiles_add1445__41 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1445__41);
        boolean o_supportsBOMinFiles_add1445__43 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1445__43);
        boolean o_supportsBOMinFiles_add1445__45 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1445__45);
        Assert.assertTrue(o_supportsBOMinFiles_add1445__6);
        Assert.assertFalse(o_supportsBOMinFiles_add1445__8);
        Assert.assertTrue(o_supportsBOMinFiles_add1445__14);
        Assert.assertFalse(o_supportsBOMinFiles_add1445__16);
        Assert.assertFalse(o_supportsBOMinFiles_add1445__18);
        Assert.assertTrue(o_supportsBOMinFiles_add1445__24);
        Assert.assertFalse(o_supportsBOMinFiles_add1445__26);
        Assert.assertFalse(o_supportsBOMinFiles_add1445__28);
        Assert.assertFalse(o_supportsBOMinFiles_add1445__30);
        Assert.assertTrue(o_supportsBOMinFiles_add1445__36);
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1445__38);
        Assert.assertFalse(o_supportsBOMinFiles_add1445__39);
        Assert.assertFalse(o_supportsBOMinFiles_add1445__41);
        Assert.assertFalse(o_supportsBOMinFiles_add1445__43);
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
    public void supportsBOMinFiles_add1441() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1441__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1441__6);
        boolean o_supportsBOMinFiles_add1441__8 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1441__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1441__14 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1441__14);
        boolean o_supportsBOMinFiles_add1441__16 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1441__16);
        boolean o_supportsBOMinFiles_add1441__18 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1441__18);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1441__24 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1441__24);
        boolean o_supportsBOMinFiles_add1441__26 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1441__26);
        boolean o_supportsBOMinFiles_add1441__28 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1441__28);
        boolean o_supportsBOMinFiles_add1441__30 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1441__30);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        Document o_supportsBOMinFiles_add1441__34 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1441__34)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add1441__34)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add1441__34)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32LE\"> \n  <title>UTF-32LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add1441__34)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1441__34)).hasParent());
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1441__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1441__37);
        boolean o_supportsBOMinFiles_add1441__39 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1441__39);
        boolean o_supportsBOMinFiles_add1441__41 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1441__41);
        boolean o_supportsBOMinFiles_add1441__43 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1441__43);
        boolean o_supportsBOMinFiles_add1441__45 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1441__45);
        Assert.assertTrue(o_supportsBOMinFiles_add1441__6);
        Assert.assertFalse(o_supportsBOMinFiles_add1441__8);
        Assert.assertTrue(o_supportsBOMinFiles_add1441__14);
        Assert.assertFalse(o_supportsBOMinFiles_add1441__16);
        Assert.assertFalse(o_supportsBOMinFiles_add1441__18);
        Assert.assertTrue(o_supportsBOMinFiles_add1441__24);
        Assert.assertFalse(o_supportsBOMinFiles_add1441__26);
        Assert.assertFalse(o_supportsBOMinFiles_add1441__28);
        Assert.assertFalse(o_supportsBOMinFiles_add1441__30);
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1441__34)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add1441__34)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add1441__34)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32LE\"> \n  <title>UTF-32LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add1441__34)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1441__34)).hasParent());
        Assert.assertTrue(o_supportsBOMinFiles_add1441__37);
        Assert.assertFalse(o_supportsBOMinFiles_add1441__39);
        Assert.assertFalse(o_supportsBOMinFiles_add1441__41);
        Assert.assertFalse(o_supportsBOMinFiles_add1441__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1397_literalMutationString11132_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
            Document doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString1397__6 = doc.title().contains("UTF-16BE");
            boolean o_supportsBOMinFiles_literalMutationString1397__8 = doc.text().contains("??????");
            in = ParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString1397__14 = doc.title().contains("UTF-16LE");
            boolean o_supportsBOMinFiles_literalMutationString1397__16 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString1397__18 = doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf32be.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString1397__24 = doc.title().contains("UTF-32BE");
            boolean o_supportsBOMinFiles_literalMutationString1397__26 = doc.text().contains("#\uac01\uac02\uac03\uac04\uac05");
            in = ParseTest.getFile("/bomtests/bom_utf32le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString1397__32 = doc.title().contains("UTF-32LE");
            boolean o_supportsBOMinFiles_literalMutationString1397__34 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString1397__36 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString1397__38 = doc.text().contains("??????");
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1397_literalMutationString11132 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1326_failAssert0null26735_failAssert0() throws IOException {
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
                doc.text().contains(null);
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1326_failAssert0null26735 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1389_add20293() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1389__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString1389__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1389__14 = doc.title().contains("UTF-16LE");
        String o_supportsBOMinFiles_literalMutationString1389_add20293__22 = doc.text();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString1389_add20293__22);
        boolean o_supportsBOMinFiles_literalMutationString1389__16 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1389__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1389__24 = doc.title().contains("UF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString1389__26 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1389__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1389__30 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1389__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString1389__38 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1389__40 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1389__42 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1389__44 = doc.text().contains("??????");
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString1389_add20293__22);
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
    public void supportsBOMinFiles_literalMutationString1361_add21537() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1361__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString1361__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, ".NvTw>p,HRp{Bm[FVK");
        boolean o_supportsBOMinFiles_literalMutationString1361__14 = doc.title().contains("UTF-16LE");
        String o_supportsBOMinFiles_literalMutationString1361_add21537__22 = doc.text();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString1361_add21537__22);
        boolean o_supportsBOMinFiles_literalMutationString1361__16 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1361__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1361__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString1361__26 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1361__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1361__30 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1361__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString1361__38 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1361__40 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1361__42 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1361__44 = doc.text().contains("??????");
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString1361_add21537__22);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1326_failAssert0_add22931_failAssert0() throws IOException {
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1326_failAssert0_add22931 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1374_failAssert0null27417_failAssert0() throws IOException {
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
                doc.text().contains(null);
                doc.text().contains("??????");
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1374 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1374_failAssert0null27417 should have thrown FileNotFoundException");
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
    public void supportsBOMinFiles_add1427() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1427__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1427__6);
        String o_supportsBOMinFiles_add1427__8 = doc.text();
        Assert.assertEquals("UTF-16BE Encoded Korean Page with BOM UTF-16BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1427__8);
        boolean o_supportsBOMinFiles_add1427__9 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1427__9);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1427__15 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1427__15);
        boolean o_supportsBOMinFiles_add1427__17 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1427__17);
        boolean o_supportsBOMinFiles_add1427__19 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1427__19);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1427__25 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1427__25);
        boolean o_supportsBOMinFiles_add1427__27 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1427__27);
        boolean o_supportsBOMinFiles_add1427__29 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1427__29);
        boolean o_supportsBOMinFiles_add1427__31 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1427__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1427__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1427__37);
        boolean o_supportsBOMinFiles_add1427__39 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1427__39);
        boolean o_supportsBOMinFiles_add1427__41 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1427__41);
        boolean o_supportsBOMinFiles_add1427__43 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1427__43);
        boolean o_supportsBOMinFiles_add1427__45 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1427__45);
        Assert.assertTrue(o_supportsBOMinFiles_add1427__6);
        Assert.assertEquals("UTF-16BE Encoded Korean Page with BOM UTF-16BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1427__8);
        Assert.assertFalse(o_supportsBOMinFiles_add1427__9);
        Assert.assertTrue(o_supportsBOMinFiles_add1427__15);
        Assert.assertFalse(o_supportsBOMinFiles_add1427__17);
        Assert.assertFalse(o_supportsBOMinFiles_add1427__19);
        Assert.assertTrue(o_supportsBOMinFiles_add1427__25);
        Assert.assertFalse(o_supportsBOMinFiles_add1427__27);
        Assert.assertFalse(o_supportsBOMinFiles_add1427__29);
        Assert.assertFalse(o_supportsBOMinFiles_add1427__31);
        Assert.assertTrue(o_supportsBOMinFiles_add1427__37);
        Assert.assertFalse(o_supportsBOMinFiles_add1427__39);
        Assert.assertFalse(o_supportsBOMinFiles_add1427__41);
        Assert.assertFalse(o_supportsBOMinFiles_add1427__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add1429() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1429__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1429__6);
        boolean o_supportsBOMinFiles_add1429__8 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1429__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        Document o_supportsBOMinFiles_add1429__12 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1429__12)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add1429__12)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add1429__12)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-16LE\"> \n  <title>UTF-16LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-16LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add1429__12)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1429__12)).hasParent());
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1429__15 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1429__15);
        boolean o_supportsBOMinFiles_add1429__17 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1429__17);
        boolean o_supportsBOMinFiles_add1429__19 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1429__19);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1429__25 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1429__25);
        boolean o_supportsBOMinFiles_add1429__27 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1429__27);
        boolean o_supportsBOMinFiles_add1429__29 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1429__29);
        boolean o_supportsBOMinFiles_add1429__31 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1429__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1429__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1429__37);
        boolean o_supportsBOMinFiles_add1429__39 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1429__39);
        boolean o_supportsBOMinFiles_add1429__41 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1429__41);
        boolean o_supportsBOMinFiles_add1429__43 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1429__43);
        boolean o_supportsBOMinFiles_add1429__45 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1429__45);
        Assert.assertTrue(o_supportsBOMinFiles_add1429__6);
        Assert.assertFalse(o_supportsBOMinFiles_add1429__8);
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1429__12)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add1429__12)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add1429__12)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-16LE\"> \n  <title>UTF-16LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-16LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add1429__12)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1429__12)).hasParent());
        Assert.assertTrue(o_supportsBOMinFiles_add1429__15);
        Assert.assertFalse(o_supportsBOMinFiles_add1429__17);
        Assert.assertFalse(o_supportsBOMinFiles_add1429__19);
        Assert.assertTrue(o_supportsBOMinFiles_add1429__25);
        Assert.assertFalse(o_supportsBOMinFiles_add1429__27);
        Assert.assertFalse(o_supportsBOMinFiles_add1429__29);
        Assert.assertFalse(o_supportsBOMinFiles_add1429__31);
        Assert.assertTrue(o_supportsBOMinFiles_add1429__37);
        Assert.assertFalse(o_supportsBOMinFiles_add1429__39);
        Assert.assertFalse(o_supportsBOMinFiles_add1429__41);
        Assert.assertFalse(o_supportsBOMinFiles_add1429__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1326_failAssert0_literalMutationString15293_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16E");
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1326_failAssert0_literalMutationString15293 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add1433() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1433__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1433__6);
        boolean o_supportsBOMinFiles_add1433__8 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1433__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1433__14 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1433__14);
        String o_supportsBOMinFiles_add1433__16 = doc.text();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1433__16);
        boolean o_supportsBOMinFiles_add1433__17 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1433__17);
        boolean o_supportsBOMinFiles_add1433__19 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1433__19);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1433__25 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1433__25);
        boolean o_supportsBOMinFiles_add1433__27 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1433__27);
        boolean o_supportsBOMinFiles_add1433__29 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1433__29);
        boolean o_supportsBOMinFiles_add1433__31 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1433__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1433__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1433__37);
        boolean o_supportsBOMinFiles_add1433__39 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1433__39);
        boolean o_supportsBOMinFiles_add1433__41 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1433__41);
        boolean o_supportsBOMinFiles_add1433__43 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1433__43);
        boolean o_supportsBOMinFiles_add1433__45 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1433__45);
        Assert.assertTrue(o_supportsBOMinFiles_add1433__6);
        Assert.assertFalse(o_supportsBOMinFiles_add1433__8);
        Assert.assertTrue(o_supportsBOMinFiles_add1433__14);
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add1433__16);
        Assert.assertFalse(o_supportsBOMinFiles_add1433__17);
        Assert.assertFalse(o_supportsBOMinFiles_add1433__19);
        Assert.assertTrue(o_supportsBOMinFiles_add1433__25);
        Assert.assertFalse(o_supportsBOMinFiles_add1433__27);
        Assert.assertFalse(o_supportsBOMinFiles_add1433__29);
        Assert.assertFalse(o_supportsBOMinFiles_add1433__31);
        Assert.assertTrue(o_supportsBOMinFiles_add1433__37);
        Assert.assertFalse(o_supportsBOMinFiles_add1433__39);
        Assert.assertFalse(o_supportsBOMinFiles_add1433__41);
        Assert.assertFalse(o_supportsBOMinFiles_add1433__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add1435() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1435__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1435__6);
        boolean o_supportsBOMinFiles_add1435__8 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1435__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1435__14 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1435__14);
        boolean o_supportsBOMinFiles_add1435__16 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1435__16);
        boolean o_supportsBOMinFiles_add1435__18 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1435__18);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        Document o_supportsBOMinFiles_add1435__22 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1435__22)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add1435__22)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add1435__22)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32BE\"> \n  <title>UTF-32BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add1435__22)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1435__22)).hasParent());
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1435__25 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add1435__25);
        boolean o_supportsBOMinFiles_add1435__27 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1435__27);
        boolean o_supportsBOMinFiles_add1435__29 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1435__29);
        boolean o_supportsBOMinFiles_add1435__31 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1435__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add1435__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add1435__37);
        boolean o_supportsBOMinFiles_add1435__39 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1435__39);
        boolean o_supportsBOMinFiles_add1435__41 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1435__41);
        boolean o_supportsBOMinFiles_add1435__43 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1435__43);
        boolean o_supportsBOMinFiles_add1435__45 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add1435__45);
        Assert.assertTrue(o_supportsBOMinFiles_add1435__6);
        Assert.assertFalse(o_supportsBOMinFiles_add1435__8);
        Assert.assertTrue(o_supportsBOMinFiles_add1435__14);
        Assert.assertFalse(o_supportsBOMinFiles_add1435__16);
        Assert.assertFalse(o_supportsBOMinFiles_add1435__18);
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1435__22)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add1435__22)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add1435__22)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32BE\"> \n  <title>UTF-32BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add1435__22)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add1435__22)).hasParent());
        Assert.assertTrue(o_supportsBOMinFiles_add1435__25);
        Assert.assertFalse(o_supportsBOMinFiles_add1435__27);
        Assert.assertFalse(o_supportsBOMinFiles_add1435__29);
        Assert.assertFalse(o_supportsBOMinFiles_add1435__31);
        Assert.assertTrue(o_supportsBOMinFiles_add1435__37);
        Assert.assertFalse(o_supportsBOMinFiles_add1435__39);
        Assert.assertFalse(o_supportsBOMinFiles_add1435__41);
        Assert.assertFalse(o_supportsBOMinFiles_add1435__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1337_add21725() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "*DQFO[$)D)#a:U_8Vz");
        boolean o_supportsBOMinFiles_literalMutationString1337__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString1337__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1337__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString1337__16 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1337__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1337__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString1337__26 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1337__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1337__30 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1337__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString1337__38 = doc.text().contains("??????");
        String o_supportsBOMinFiles_literalMutationString1337_add21725__62 = doc.text();
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString1337_add21725__62);
        boolean o_supportsBOMinFiles_literalMutationString1337__40 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1337__42 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1337__44 = doc.text().contains("??????");
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString1337_add21725__62);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1340_add21587() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1340__6 = doc.title().contains("UTl-16BE");
        boolean o_supportsBOMinFiles_literalMutationString1340__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1340__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString1340__16 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1340__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1340__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString1340__26 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1340__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1340__30 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        Document o_supportsBOMinFiles_literalMutationString1340_add21587__52 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString1340_add21587__52)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_literalMutationString1340_add21587__52)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_literalMutationString1340_add21587__52)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32LE\"> \n  <title>UTF-32LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_literalMutationString1340_add21587__52)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString1340_add21587__52)).hasParent());
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1340__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString1340__38 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1340__40 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1340__42 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1340__44 = doc.text().contains("??????");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString1340_add21587__52)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_literalMutationString1340_add21587__52)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_literalMutationString1340_add21587__52)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32LE\"> \n  <title>UTF-32LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_literalMutationString1340_add21587__52)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString1340_add21587__52)).hasParent());
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1388_add19383() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1388__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString1388__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1388__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString1388__16 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1388__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1388__24 = doc.title().contains("UOF-32BE");
        String o_supportsBOMinFiles_literalMutationString1388_add19383__38 = doc.text();
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString1388_add19383__38);
        boolean o_supportsBOMinFiles_literalMutationString1388__26 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1388__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1388__30 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1388__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString1388__38 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1388__40 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1388__42 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1388__44 = doc.text().contains("??????");
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString1388_add19383__38);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1336_add21663() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://exampJe.com");
        boolean o_supportsBOMinFiles_literalMutationString1336__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString1336__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1336__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString1336__16 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1336__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1336__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString1336__26 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1336__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1336__30 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1336__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString1336__38 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1336__40 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1336__42 = doc.text().contains("??????");
        String o_supportsBOMinFiles_literalMutationString1336_add21663__70 = doc.text();
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString1336_add21663__70);
        boolean o_supportsBOMinFiles_literalMutationString1336__44 = doc.text().contains("??????");
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString1336_add21663__70);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1392_add22133() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1392__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString1392__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1392__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString1392__16 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1392__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1392__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString1392__26 = doc.text().contains("");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString1392__32 = doc.title().contains("UTF-32LE");
        String o_supportsBOMinFiles_literalMutationString1392_add22133__50 = doc.text();
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString1392_add22133__50);
        boolean o_supportsBOMinFiles_literalMutationString1392__34 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1392__36 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString1392__38 = doc.text().contains("??????");
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString1392_add22133__50);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString1394_literalMutationString6488_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString1394__6 = doc.title().contains("UTF-16BE");
            boolean o_supportsBOMinFiles_literalMutationString1394__8 = doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf16le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString1394__14 = doc.title().contains("UTF-16LE");
            boolean o_supportsBOMinFiles_literalMutationString1394__16 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString1394__18 = doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf32be.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString1394__24 = doc.title().contains("UTF-32BE");
            boolean o_supportsBOMinFiles_literalMutationString1394__26 = doc.text().contains("\uac00\uac01\uac02\uac04\uac05");
            in = ParseTest.getFile("/bomtests/bom_utf32le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString1394__32 = doc.title().contains("UTF-32LE");
            boolean o_supportsBOMinFiles_literalMutationString1394__34 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString1394__36 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString1394__38 = doc.text().contains("??????");
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString1394_literalMutationString6488 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }
}

