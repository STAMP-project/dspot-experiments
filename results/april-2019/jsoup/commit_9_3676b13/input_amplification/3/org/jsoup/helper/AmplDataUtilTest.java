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
    public void supportsBOMinFiles_literalMutationString7213_failAssert0_add29838_failAssert0_add55767_failAssert0() throws IOException {
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
                    doc.text();
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
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7213 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7213_failAssert0_add29838 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7213_failAssert0_add29838_failAssert0_add55767 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7285_add26822_remove57217() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__7 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add7285__9 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__15 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_add7285__17 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__19 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__25 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_add7285__27 = doc.text().contains("??????");
        String o_supportsBOMinFiles_add7285_add26822__43 = doc.text();
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7285_add26822__43);
        boolean o_supportsBOMinFiles_add7285__29 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__31 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__37 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_add7285__39 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__41 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__43 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__45 = doc.text().contains("??????");
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7285_add26822__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7286_add26679_literalMutationString38449() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document o_supportsBOMinFiles_add7286__4 = Jsoup.parse(in, null, "http://example.com");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__7 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add7286__9 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__15 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_add7286__17 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__19 = doc.text().contains("  ");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__25 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_add7286__27 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__29 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__31 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__37 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_add7286__39 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__41 = doc.text().contains("??????");
        String o_supportsBOMinFiles_add7286_add26679__69 = doc.text();
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7286_add26679__69);
        boolean o_supportsBOMinFiles_add7286__43 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__45 = doc.text().contains("??????");
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7286_add26679__69);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7285_add26822_literalMutationString35158() throws IOException {
        ParseTest.getFile("/bomtests/bom_utf16be.html");
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__7 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add7285__9 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__15 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_add7285__17 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__19 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__25 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_add7285__27 = doc.text().contains("??????");
        String o_supportsBOMinFiles_add7285_add26822__43 = doc.text();
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7285_add26822__43);
        boolean o_supportsBOMinFiles_add7285__29 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__31 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__37 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_add7285__39 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__41 = doc.text().contains("  ");
        boolean o_supportsBOMinFiles_add7285__43 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__45 = doc.text().contains("??????");
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7285_add26822__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7285_add26822() throws IOException {
        ParseTest.getFile("/bomtests/bom_utf16be.html");
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__7 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add7285__9 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__15 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_add7285__17 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__19 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__25 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_add7285__27 = doc.text().contains("??????");
        String o_supportsBOMinFiles_add7285_add26822__43 = doc.text();
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7285_add26822__43);
        boolean o_supportsBOMinFiles_add7285__29 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__31 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__37 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_add7285__39 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__41 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__43 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__45 = doc.text().contains("??????");
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7285_add26822__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7286_add26679() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document o_supportsBOMinFiles_add7286__4 = Jsoup.parse(in, null, "http://example.com");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__7 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add7286__9 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__15 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_add7286__17 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__19 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__25 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_add7286__27 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__29 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__31 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__37 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_add7286__39 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__41 = doc.text().contains("??????");
        String o_supportsBOMinFiles_add7286_add26679__69 = doc.text();
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7286_add26679__69);
        boolean o_supportsBOMinFiles_add7286__43 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__45 = doc.text().contains("??????");
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7286_add26679__69);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7286() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document o_supportsBOMinFiles_add7286__4 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7286__4)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add7286__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add7286__4)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-16BE\"> \n  <title>UTF-16BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-16BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add7286__4)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7286__4)).hasParent());
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__7 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add7286__7);
        boolean o_supportsBOMinFiles_add7286__9 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7286__9);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__15 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add7286__15);
        boolean o_supportsBOMinFiles_add7286__17 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7286__17);
        boolean o_supportsBOMinFiles_add7286__19 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7286__19);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__25 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add7286__25);
        boolean o_supportsBOMinFiles_add7286__27 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7286__27);
        boolean o_supportsBOMinFiles_add7286__29 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7286__29);
        boolean o_supportsBOMinFiles_add7286__31 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7286__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add7286__37);
        boolean o_supportsBOMinFiles_add7286__39 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7286__39);
        boolean o_supportsBOMinFiles_add7286__41 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7286__41);
        boolean o_supportsBOMinFiles_add7286__43 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7286__43);
        boolean o_supportsBOMinFiles_add7286__45 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7286__45);
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7286__4)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add7286__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add7286__4)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-16BE\"> \n  <title>UTF-16BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-16BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add7286__4)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7286__4)).hasParent());
        Assert.assertTrue(o_supportsBOMinFiles_add7286__7);
        Assert.assertFalse(o_supportsBOMinFiles_add7286__9);
        Assert.assertTrue(o_supportsBOMinFiles_add7286__15);
        Assert.assertFalse(o_supportsBOMinFiles_add7286__17);
        Assert.assertFalse(o_supportsBOMinFiles_add7286__19);
        Assert.assertTrue(o_supportsBOMinFiles_add7286__25);
        Assert.assertFalse(o_supportsBOMinFiles_add7286__27);
        Assert.assertFalse(o_supportsBOMinFiles_add7286__29);
        Assert.assertFalse(o_supportsBOMinFiles_add7286__31);
        Assert.assertTrue(o_supportsBOMinFiles_add7286__37);
        Assert.assertFalse(o_supportsBOMinFiles_add7286__39);
        Assert.assertFalse(o_supportsBOMinFiles_add7286__41);
        Assert.assertFalse(o_supportsBOMinFiles_add7286__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7237_failAssert0_add28794_failAssert0() throws IOException {
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
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7237 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7237_failAssert0_add28794 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7213_failAssert0_add29838_failAssert0_literalMutationString46924_failAssert0() throws IOException {
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
                    doc.text();
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    in = ParseTest.getFile("/bomtests/bom_utf32be.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("Cy99_<j.");
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
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7213 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7213_failAssert0_add29838 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7213_failAssert0_add29838_failAssert0_literalMutationString46924 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7294_add27010() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7294__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add7294__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        String o_supportsBOMinFiles_add7294__14 = doc.title();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM", o_supportsBOMinFiles_add7294__14);
        boolean o_supportsBOMinFiles_add7294__15 = doc.title().contains("UTF-16LE");
        String o_supportsBOMinFiles_add7294_add27010__25 = doc.text();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7294_add27010__25);
        boolean o_supportsBOMinFiles_add7294__17 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7294__19 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7294__25 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_add7294__27 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7294__29 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7294__31 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7294__37 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_add7294__39 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7294__41 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7294__43 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7294__45 = doc.text().contains("??????");
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM", o_supportsBOMinFiles_add7294__14);
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7294_add27010__25);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7213_failAssert0() throws IOException {
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7213 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7254_add27894() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7254__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString7254__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7254__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString7254__16 = doc.text().contains("??????");
        String o_supportsBOMinFiles_literalMutationString7254_add27894__26 = doc.text();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7254_add27894__26);
        boolean o_supportsBOMinFiles_literalMutationString7254__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7254__24 = doc.title().contains("UTF-E32BE");
        boolean o_supportsBOMinFiles_literalMutationString7254__26 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7254__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7254__30 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7254__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString7254__38 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7254__40 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7254__42 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7254__44 = doc.text().contains("??????");
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7254_add27894__26);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7215_failAssert0_add29614_failAssert0_literalMutationString48438_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                    Document doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16BE");
                    doc.text().contains("??????");
                    in = ParseTest.getFile("");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title();
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
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7215 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7215_failAssert0_add29614 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7215_failAssert0_add29614_failAssert0_literalMutationString48438 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7283_add25300_literalMutationString36720() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7283__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString7283__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "/wP(X-#M&jh}g<:vX0");
        boolean o_supportsBOMinFiles_literalMutationString7283__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString7283__16 = doc.text().contains("??????");
        String o_supportsBOMinFiles_literalMutationString7283_add25300__26 = doc.text();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7283_add25300__26);
        boolean o_supportsBOMinFiles_literalMutationString7283__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7283__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString7283__26 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7283__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7283__30 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7283__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString7283__38 = doc.text().contains("\uac00K\uac01\uac02\uac03\uac04\uac05");
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7283_add25300__26);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7294_add27010_add52972() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7294__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add7294__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        String o_supportsBOMinFiles_add7294__14 = doc.title();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM", o_supportsBOMinFiles_add7294__14);
        boolean o_supportsBOMinFiles_add7294__15 = doc.title().contains("UTF-16LE");
        String o_supportsBOMinFiles_add7294_add27010__25 = doc.text();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7294_add27010__25);
        boolean o_supportsBOMinFiles_add7294__17 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7294__19 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7294__25 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_add7294__27 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7294__29 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7294__31 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7294__37 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_add7294_add27010_add52972__64 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7294_add27010_add52972__64);
        boolean o_supportsBOMinFiles_add7294__39 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7294__41 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7294__43 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7294__45 = doc.text().contains("??????");
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM", o_supportsBOMinFiles_add7294__14);
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7294_add27010__25);
        Assert.assertFalse(o_supportsBOMinFiles_add7294_add27010_add52972__64);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7237_failAssert0_add28794_failAssert0null60175_failAssert0() throws IOException {
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
                    in = ParseTest.getFile("");
                    doc = Jsoup.parse(in, null, null);
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
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7237 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7237_failAssert0_add28794 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7237_failAssert0_add28794_failAssert0null60175 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7237_failAssert0() throws IOException {
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7237 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7213_failAssert0_add29838_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                Document doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-16BE");
                doc.text().contains("??????");
                in = ParseTest.getFile("");
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
                in = ParseTest.getFile("/bomtests/bom_utf32le.html");
                doc = Jsoup.parse(in, null, "http://example.com");
                doc.title().contains("UTF-32LE");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7213 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7213_failAssert0_add29838 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7286_add26680_add52295() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document o_supportsBOMinFiles_add7286__4 = Jsoup.parse(in, null, "http://example.com");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__7 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add7286__9 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__15 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_add7286__17 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__19 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__25 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_add7286__27 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__29 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__31 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__37 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_add7286__39 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__41 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__43 = doc.text().contains("??????");
        String o_supportsBOMinFiles_add7286_add26680_add52295__73 = doc.text();
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7286_add26680_add52295__73);
        boolean o_supportsBOMinFiles_add7286_add26680__73 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__45 = doc.text().contains("??????");
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7286_add26680_add52295__73);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7292() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7292__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add7292__6);
        boolean o_supportsBOMinFiles_add7292__8 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7292__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        Document o_supportsBOMinFiles_add7292__12 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7292__12)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add7292__12)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add7292__12)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-16LE\"> \n  <title>UTF-16LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-16LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add7292__12)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7292__12)).hasParent());
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7292__15 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add7292__15);
        boolean o_supportsBOMinFiles_add7292__17 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7292__17);
        boolean o_supportsBOMinFiles_add7292__19 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7292__19);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7292__25 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add7292__25);
        boolean o_supportsBOMinFiles_add7292__27 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7292__27);
        boolean o_supportsBOMinFiles_add7292__29 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7292__29);
        boolean o_supportsBOMinFiles_add7292__31 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7292__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7292__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add7292__37);
        boolean o_supportsBOMinFiles_add7292__39 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7292__39);
        boolean o_supportsBOMinFiles_add7292__41 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7292__41);
        boolean o_supportsBOMinFiles_add7292__43 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7292__43);
        boolean o_supportsBOMinFiles_add7292__45 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7292__45);
        Assert.assertTrue(o_supportsBOMinFiles_add7292__6);
        Assert.assertFalse(o_supportsBOMinFiles_add7292__8);
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7292__12)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add7292__12)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add7292__12)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-16LE\"> \n  <title>UTF-16LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-16LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add7292__12)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7292__12)).hasParent());
        Assert.assertTrue(o_supportsBOMinFiles_add7292__15);
        Assert.assertFalse(o_supportsBOMinFiles_add7292__17);
        Assert.assertFalse(o_supportsBOMinFiles_add7292__19);
        Assert.assertTrue(o_supportsBOMinFiles_add7292__25);
        Assert.assertFalse(o_supportsBOMinFiles_add7292__27);
        Assert.assertFalse(o_supportsBOMinFiles_add7292__29);
        Assert.assertFalse(o_supportsBOMinFiles_add7292__31);
        Assert.assertTrue(o_supportsBOMinFiles_add7292__37);
        Assert.assertFalse(o_supportsBOMinFiles_add7292__39);
        Assert.assertFalse(o_supportsBOMinFiles_add7292__41);
        Assert.assertFalse(o_supportsBOMinFiles_add7292__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7290() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7290__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add7290__6);
        String o_supportsBOMinFiles_add7290__8 = doc.text();
        Assert.assertEquals("UTF-16BE Encoded Korean Page with BOM UTF-16BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7290__8);
        boolean o_supportsBOMinFiles_add7290__9 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7290__9);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7290__15 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add7290__15);
        boolean o_supportsBOMinFiles_add7290__17 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7290__17);
        boolean o_supportsBOMinFiles_add7290__19 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7290__19);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7290__25 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add7290__25);
        boolean o_supportsBOMinFiles_add7290__27 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7290__27);
        boolean o_supportsBOMinFiles_add7290__29 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7290__29);
        boolean o_supportsBOMinFiles_add7290__31 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7290__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7290__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add7290__37);
        boolean o_supportsBOMinFiles_add7290__39 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7290__39);
        boolean o_supportsBOMinFiles_add7290__41 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7290__41);
        boolean o_supportsBOMinFiles_add7290__43 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7290__43);
        boolean o_supportsBOMinFiles_add7290__45 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7290__45);
        Assert.assertTrue(o_supportsBOMinFiles_add7290__6);
        Assert.assertEquals("UTF-16BE Encoded Korean Page with BOM UTF-16BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7290__8);
        Assert.assertFalse(o_supportsBOMinFiles_add7290__9);
        Assert.assertTrue(o_supportsBOMinFiles_add7290__15);
        Assert.assertFalse(o_supportsBOMinFiles_add7290__17);
        Assert.assertFalse(o_supportsBOMinFiles_add7290__19);
        Assert.assertTrue(o_supportsBOMinFiles_add7290__25);
        Assert.assertFalse(o_supportsBOMinFiles_add7290__27);
        Assert.assertFalse(o_supportsBOMinFiles_add7290__29);
        Assert.assertFalse(o_supportsBOMinFiles_add7290__31);
        Assert.assertTrue(o_supportsBOMinFiles_add7290__37);
        Assert.assertFalse(o_supportsBOMinFiles_add7290__39);
        Assert.assertFalse(o_supportsBOMinFiles_add7290__41);
        Assert.assertFalse(o_supportsBOMinFiles_add7290__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7283_add25300() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7283__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString7283__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7283__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString7283__16 = doc.text().contains("??????");
        String o_supportsBOMinFiles_literalMutationString7283_add25300__26 = doc.text();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7283_add25300__26);
        boolean o_supportsBOMinFiles_literalMutationString7283__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7283__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString7283__26 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7283__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7283__30 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7283__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString7283__38 = doc.text().contains("\uac00K\uac01\uac02\uac03\uac04\uac05");
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7283_add25300__26);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7285_add26822_literalMutationString35121() throws IOException {
        ParseTest.getFile("/bomtests/bom_utf16be.html");
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__7 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add7285__9 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__15 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_add7285__17 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__19 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__25 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_add7285__27 = doc.text().contains("??????");
        String o_supportsBOMinFiles_add7285_add26822__43 = doc.text();
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7285_add26822__43);
        boolean o_supportsBOMinFiles_add7285__29 = doc.text().contains("");
        boolean o_supportsBOMinFiles_add7285__31 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__37 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_add7285__39 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__41 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__43 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__45 = doc.text().contains("??????");
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7285_add26822__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7281_add26174() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7281__6 = doc.title().contains("UTF-16BE");
        String o_supportsBOMinFiles_literalMutationString7281_add26174__10 = doc.text();
        Assert.assertEquals("UTF-16BE Encoded Korean Page with BOM UTF-16BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7281_add26174__10);
        boolean o_supportsBOMinFiles_literalMutationString7281__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7281__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString7281__16 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7281__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7281__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString7281__26 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7281__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7281__30 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7281__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString7281__38 = doc.text().contains("\uac00\uac01\uac02\uac04\uac05");
        Assert.assertEquals("UTF-16BE Encoded Korean Page with BOM UTF-16BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7281_add26174__10);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7256_add25512() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7256__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString7256__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7256__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString7256__16 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7256__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7256__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString7256__26 = doc.text().contains("  ");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        Document o_supportsBOMinFiles_literalMutationString7256_add25512__44 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString7256_add25512__44)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_literalMutationString7256_add25512__44)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_literalMutationString7256_add25512__44)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32LE\"> \n  <title>UTF-32LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_literalMutationString7256_add25512__44)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString7256_add25512__44)).hasParent());
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7256__32 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString7256__34 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7256__36 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7256__38 = doc.text().contains("??????");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString7256_add25512__44)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_literalMutationString7256_add25512__44)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_literalMutationString7256_add25512__44)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32LE\"> \n  <title>UTF-32LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_literalMutationString7256_add25512__44)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString7256_add25512__44)).hasParent());
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7287_add27105() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7287__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add7287__8 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add7287__10 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7287__16 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_add7287__18 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7287__20 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7287__26 = doc.title().contains("UTF-32BE");
        String o_supportsBOMinFiles_add7287_add27105__42 = doc.text();
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7287_add27105__42);
        boolean o_supportsBOMinFiles_add7287__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7287__30 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7287__32 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7287__38 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_add7287__40 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7287__42 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7287__44 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7287__46 = doc.text().contains("??????");
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7287_add27105__42);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7237_failAssert0_literalMutationString21274_failAssert0() throws IOException {
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
                doc.text().contains("\uac00\uac01>\uac03\uac04\uac05");
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7237 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7237_failAssert0_literalMutationString21274 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7258_add27430() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7258__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString7258__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7258__14 = doc.title().contains("UTF-16LE");
        String o_supportsBOMinFiles_literalMutationString7258_add27430__22 = doc.text();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7258_add27430__22);
        boolean o_supportsBOMinFiles_literalMutationString7258__16 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7258__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7258__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString7258__26 = doc.text().contains("\uac00\uac02\uac03\uac04\uac05");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7258__32 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString7258__34 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7258__36 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7258__38 = doc.text().contains("??????");
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7258_add27430__22);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7298() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7298__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add7298__6);
        boolean o_supportsBOMinFiles_add7298__8 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7298__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7298__14 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add7298__14);
        boolean o_supportsBOMinFiles_add7298__16 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7298__16);
        boolean o_supportsBOMinFiles_add7298__18 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7298__18);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        Document o_supportsBOMinFiles_add7298__22 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7298__22)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add7298__22)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add7298__22)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32BE\"> \n  <title>UTF-32BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add7298__22)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7298__22)).hasParent());
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7298__25 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add7298__25);
        boolean o_supportsBOMinFiles_add7298__27 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7298__27);
        boolean o_supportsBOMinFiles_add7298__29 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7298__29);
        boolean o_supportsBOMinFiles_add7298__31 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7298__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7298__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add7298__37);
        boolean o_supportsBOMinFiles_add7298__39 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7298__39);
        boolean o_supportsBOMinFiles_add7298__41 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7298__41);
        boolean o_supportsBOMinFiles_add7298__43 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7298__43);
        boolean o_supportsBOMinFiles_add7298__45 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7298__45);
        Assert.assertTrue(o_supportsBOMinFiles_add7298__6);
        Assert.assertFalse(o_supportsBOMinFiles_add7298__8);
        Assert.assertTrue(o_supportsBOMinFiles_add7298__14);
        Assert.assertFalse(o_supportsBOMinFiles_add7298__16);
        Assert.assertFalse(o_supportsBOMinFiles_add7298__18);
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7298__22)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add7298__22)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add7298__22)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32BE\"> \n  <title>UTF-32BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add7298__22)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7298__22)).hasParent());
        Assert.assertTrue(o_supportsBOMinFiles_add7298__25);
        Assert.assertFalse(o_supportsBOMinFiles_add7298__27);
        Assert.assertFalse(o_supportsBOMinFiles_add7298__29);
        Assert.assertFalse(o_supportsBOMinFiles_add7298__31);
        Assert.assertTrue(o_supportsBOMinFiles_add7298__37);
        Assert.assertFalse(o_supportsBOMinFiles_add7298__39);
        Assert.assertFalse(o_supportsBOMinFiles_add7298__41);
        Assert.assertFalse(o_supportsBOMinFiles_add7298__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7213_failAssert0null33333_failAssert0() throws IOException {
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
                doc.text().contains(null);
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7213 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7213_failAssert0null33333 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7296() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7296__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add7296__6);
        boolean o_supportsBOMinFiles_add7296__8 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7296__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7296__14 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add7296__14);
        String o_supportsBOMinFiles_add7296__16 = doc.text();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7296__16);
        boolean o_supportsBOMinFiles_add7296__17 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7296__17);
        boolean o_supportsBOMinFiles_add7296__19 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7296__19);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7296__25 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add7296__25);
        boolean o_supportsBOMinFiles_add7296__27 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7296__27);
        boolean o_supportsBOMinFiles_add7296__29 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7296__29);
        boolean o_supportsBOMinFiles_add7296__31 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7296__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7296__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add7296__37);
        boolean o_supportsBOMinFiles_add7296__39 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7296__39);
        boolean o_supportsBOMinFiles_add7296__41 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7296__41);
        boolean o_supportsBOMinFiles_add7296__43 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7296__43);
        boolean o_supportsBOMinFiles_add7296__45 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7296__45);
        Assert.assertTrue(o_supportsBOMinFiles_add7296__6);
        Assert.assertFalse(o_supportsBOMinFiles_add7296__8);
        Assert.assertTrue(o_supportsBOMinFiles_add7296__14);
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7296__16);
        Assert.assertFalse(o_supportsBOMinFiles_add7296__17);
        Assert.assertFalse(o_supportsBOMinFiles_add7296__19);
        Assert.assertTrue(o_supportsBOMinFiles_add7296__25);
        Assert.assertFalse(o_supportsBOMinFiles_add7296__27);
        Assert.assertFalse(o_supportsBOMinFiles_add7296__29);
        Assert.assertFalse(o_supportsBOMinFiles_add7296__31);
        Assert.assertTrue(o_supportsBOMinFiles_add7296__37);
        Assert.assertFalse(o_supportsBOMinFiles_add7296__39);
        Assert.assertFalse(o_supportsBOMinFiles_add7296__41);
        Assert.assertFalse(o_supportsBOMinFiles_add7296__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7267_add27644() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7267__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString7267__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7267__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString7267__16 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7267__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7267__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString7267__26 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7267__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7267__30 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "");
        boolean o_supportsBOMinFiles_literalMutationString7267__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString7267__38 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7267__40 = doc.text().contains("??????");
        String o_supportsBOMinFiles_literalMutationString7267_add27644__66 = doc.text();
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7267_add27644__66);
        boolean o_supportsBOMinFiles_literalMutationString7267__42 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7267__44 = doc.text().contains("??????");
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7267_add27644__66);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7286_add26679_literalMutationString38489() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document o_supportsBOMinFiles_add7286__4 = Jsoup.parse(in, null, "http://example.com");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__7 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add7286__9 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__15 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_add7286__17 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__19 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__25 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_add7286__27 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__29 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__31 = doc.text().contains("\uac00\uac01R\uac02\uac03\uac04\uac05");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__37 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_add7286__39 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__41 = doc.text().contains("??????");
        String o_supportsBOMinFiles_add7286_add26679__69 = doc.text();
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7286_add26679__69);
        boolean o_supportsBOMinFiles_add7286__43 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__45 = doc.text().contains("??????");
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7286_add26679__69);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7267_add27646() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7267__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString7267__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7267__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString7267__16 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7267__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7267__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString7267__26 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7267__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7267__30 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "");
        boolean o_supportsBOMinFiles_literalMutationString7267__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString7267__38 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7267__40 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7267__42 = doc.text().contains("??????");
        String o_supportsBOMinFiles_literalMutationString7267_add27646__70 = doc.text();
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7267_add27646__70);
        boolean o_supportsBOMinFiles_literalMutationString7267__44 = doc.text().contains("??????");
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7267_add27646__70);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7251_add26040_add52522() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7251__6 = doc.title().contains("UTF-16BE");
        String o_supportsBOMinFiles_literalMutationString7251_add26040_add52522__10 = doc.text();
        Assert.assertEquals("UTF-16BE Encoded Korean Page with BOM UTF-16BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7251_add26040_add52522__10);
        boolean o_supportsBOMinFiles_literalMutationString7251__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        String o_supportsBOMinFiles_literalMutationString7251_add26040__18 = doc.title();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM", o_supportsBOMinFiles_literalMutationString7251_add26040__18);
        boolean o_supportsBOMinFiles_literalMutationString7251__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString7251__16 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7251__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7251__24 = doc.title().contains("UTF-3,BE");
        boolean o_supportsBOMinFiles_literalMutationString7251__26 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7251__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7251__30 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7251__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString7251__38 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7251__40 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7251__42 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7251__44 = doc.text().contains("??????");
        Assert.assertEquals("UTF-16BE Encoded Korean Page with BOM UTF-16BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7251_add26040_add52522__10);
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM", o_supportsBOMinFiles_literalMutationString7251_add26040__18);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7280_add27490() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7280__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString7280__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7280__14 = doc.title().contains("UTF-16LE");
        String o_supportsBOMinFiles_literalMutationString7280_add27490__22 = doc.text();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7280_add27490__22);
        boolean o_supportsBOMinFiles_literalMutationString7280__16 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7280__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7280__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString7280__26 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7280__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7280__30 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7280__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString7280__38 = doc.text().contains("  ");
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7280_add27490__22);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7285_add26822_add52322() throws IOException {
        ParseTest.getFile("/bomtests/bom_utf16be.html");
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__7 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add7285__9 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__15 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_add7285__17 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__19 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__25 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_add7285__27 = doc.text().contains("??????");
        String o_supportsBOMinFiles_add7285_add26822__43 = doc.text();
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7285_add26822__43);
        boolean o_supportsBOMinFiles_add7285__29 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285_add26822_add52322__50 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7285_add26822_add52322__50);
        boolean o_supportsBOMinFiles_add7285__31 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__37 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_add7285__39 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__41 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__43 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__45 = doc.text().contains("??????");
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7285_add26822__43);
        Assert.assertFalse(o_supportsBOMinFiles_add7285_add26822_add52322__50);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7249_literalMutationString12390_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
            Document doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString7249__6 = doc.title().contains("UTF-16BE");
            boolean o_supportsBOMinFiles_literalMutationString7249__8 = doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf16le.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString7249__14 = doc.title().contains("UTF-16LE");
            boolean o_supportsBOMinFiles_literalMutationString7249__16 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7249__18 = doc.text().contains("??????");
            in = ParseTest.getFile("/bomtests/bom_utf32be.html");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString7249__24 = doc.title().contains("");
            boolean o_supportsBOMinFiles_literalMutationString7249__26 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7249__28 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7249__30 = doc.text().contains("??????");
            in = ParseTest.getFile("");
            doc = Jsoup.parse(in, null, "http://example.com");
            boolean o_supportsBOMinFiles_literalMutationString7249__36 = doc.title().contains("UTF-32LE");
            boolean o_supportsBOMinFiles_literalMutationString7249__38 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7249__40 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7249__42 = doc.text().contains("??????");
            boolean o_supportsBOMinFiles_literalMutationString7249__44 = doc.text().contains("??????");
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7249_literalMutationString12390 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7213_failAssert0_literalMutationString24905_failAssert0() throws IOException {
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
                doc.text().contains("L\uac01\uac02\uac03\uac04\uac05");
                doc.text().contains("??????");
                doc.text().contains("??????");
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7213 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7213_failAssert0_literalMutationString24905 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7286_add26680_add52275() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document o_supportsBOMinFiles_add7286__4 = Jsoup.parse(in, null, "http://example.com");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__7 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add7286__9 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__15 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_add7286__17 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__19 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        Document o_supportsBOMinFiles_add7286_add26680_add52275__35 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7286_add26680_add52275__35)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add7286_add26680_add52275__35)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add7286_add26680_add52275__35)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32BE\"> \n  <title>UTF-32BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add7286_add26680_add52275__35)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7286_add26680_add52275__35)).hasParent());
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__25 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_add7286__27 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__29 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__31 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__37 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_add7286__39 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__41 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__43 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286_add26680__73 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__45 = doc.text().contains("??????");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7286_add26680_add52275__35)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add7286_add26680_add52275__35)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add7286_add26680_add52275__35)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32BE\"> \n  <title>UTF-32BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add7286_add26680_add52275__35)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7286_add26680_add52275__35)).hasParent());
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7294_add27010_literalMutationString37429() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7294__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add7294__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        String o_supportsBOMinFiles_add7294__14 = doc.title();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM", o_supportsBOMinFiles_add7294__14);
        boolean o_supportsBOMinFiles_add7294__15 = doc.title().contains("UTF-16LE");
        String o_supportsBOMinFiles_add7294_add27010__25 = doc.text();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7294_add27010__25);
        boolean o_supportsBOMinFiles_add7294__17 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7294__19 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7294__25 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_add7294__27 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7294__29 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7294__31 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7294__37 = doc.title().contains("]TF-32LE");
        boolean o_supportsBOMinFiles_add7294__39 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7294__41 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7294__43 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7294__45 = doc.text().contains("??????");
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM", o_supportsBOMinFiles_add7294__14);
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7294_add27010__25);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7285_add26822_add52318() throws IOException {
        ParseTest.getFile("/bomtests/bom_utf16be.html");
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__7 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add7285__9 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__15 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_add7285__17 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__19 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__25 = doc.title().contains("UTF-32BE");
        String o_supportsBOMinFiles_add7285_add26822_add52318__39 = doc.text();
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7285_add26822_add52318__39);
        boolean o_supportsBOMinFiles_add7285__27 = doc.text().contains("??????");
        String o_supportsBOMinFiles_add7285_add26822__43 = doc.text();
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7285_add26822__43);
        boolean o_supportsBOMinFiles_add7285__29 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__31 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7285__37 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_add7285__39 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__41 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__43 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7285__45 = doc.text().contains("??????");
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7285_add26822_add52318__39);
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7285_add26822__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7283_add25300_add52772() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7283__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString7283__8 = doc.text().contains("??????");
        ParseTest.getFile("/bomtests/bom_utf16le.html");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7283__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString7283__16 = doc.text().contains("??????");
        String o_supportsBOMinFiles_literalMutationString7283_add25300__26 = doc.text();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7283_add25300__26);
        boolean o_supportsBOMinFiles_literalMutationString7283__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7283__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString7283__26 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7283__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7283__30 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7283__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString7283__38 = doc.text().contains("\uac00K\uac01\uac02\uac03\uac04\uac05");
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7283_add25300__26);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7237_failAssert0_add28794_failAssert0_add56231_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
                    Document doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16BE");
                    doc.text().contains("??????");
                    in = ParseTest.getFile("/bomtests/bom_utf16le.html");
                    Jsoup.parse(in, null, "http://example.com");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-16LE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    in = ParseTest.getFile("");
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
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7237 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7237_failAssert0_add28794 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7237_failAssert0_add28794_failAssert0_add56231 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7189_failAssert0() throws IOException {
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7189 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7288_add26563() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        String o_supportsBOMinFiles_add7288__6 = doc.title();
        Assert.assertEquals("UTF-16BE Encoded Korean Page with BOM", o_supportsBOMinFiles_add7288__6);
        boolean o_supportsBOMinFiles_add7288__7 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add7288__9 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7288__15 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_add7288__17 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7288__19 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7288__25 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_add7288__27 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7288__29 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7288__31 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7288__37 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_add7288__39 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7288__41 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7288__43 = doc.text().contains("??????");
        String o_supportsBOMinFiles_add7288_add26563__73 = doc.text();
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7288_add26563__73);
        boolean o_supportsBOMinFiles_add7288__45 = doc.text().contains("??????");
        Assert.assertEquals("UTF-16BE Encoded Korean Page with BOM", o_supportsBOMinFiles_add7288__6);
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7288_add26563__73);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7261_failAssert0() throws IOException {
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7261 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7237_failAssert0_add28794_failAssert0_literalMutationString48642_failAssert0() throws IOException {
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
                    in = ParseTest.getFile("");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-32BE");
                    doc.title().contains("UTF-32BE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    in = ParseTest.getFile("/bomtests/bom_tf32le.html");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-32LE");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    doc.text().contains("??????");
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7237 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7237_failAssert0_add28794 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7237_failAssert0_add28794_failAssert0_literalMutationString48642 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7269_add27720() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7269__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString7269__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        Document o_supportsBOMinFiles_literalMutationString7269_add27720__16 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString7269_add27720__16)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_literalMutationString7269_add27720__16)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_literalMutationString7269_add27720__16)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-16LE\"> \n  <title>UTF-16LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-16LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_literalMutationString7269_add27720__16)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString7269_add27720__16)).hasParent());
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7269__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString7269__16 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7269__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7269__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString7269__26 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7269__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7269__30 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://examplencom");
        boolean o_supportsBOMinFiles_literalMutationString7269__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString7269__38 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7269__40 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7269__42 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7269__44 = doc.text().contains("??????");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString7269_add27720__16)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_literalMutationString7269_add27720__16)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_literalMutationString7269_add27720__16)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-16LE\"> \n  <title>UTF-16LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-16LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_literalMutationString7269_add27720__16)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString7269_add27720__16)).hasParent());
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7260_add26250() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7260__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString7260__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7260__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString7260__16 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7260__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        Document o_supportsBOMinFiles_literalMutationString7260_add26250__32 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString7260_add26250__32)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_literalMutationString7260_add26250__32)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_literalMutationString7260_add26250__32)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32BE\"> \n  <title>UTF-32BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_literalMutationString7260_add26250__32)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString7260_add26250__32)).hasParent());
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7260__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString7260__26 = doc.text().contains("U\uac01\uac02\uac03\uac04\uac05");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7260__32 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString7260__34 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7260__36 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7260__38 = doc.text().contains("??????");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString7260_add26250__32)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_literalMutationString7260_add26250__32)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_literalMutationString7260_add26250__32)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32BE\"> \n  <title>UTF-32BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_literalMutationString7260_add26250__32)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString7260_add26250__32)).hasParent());
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7267_add27622() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7267__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString7267__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7267__14 = doc.title().contains("UTF-16LE");
        String o_supportsBOMinFiles_literalMutationString7267_add27622__22 = doc.text();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7267_add27622__22);
        boolean o_supportsBOMinFiles_literalMutationString7267__16 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7267__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7267__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString7267__26 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7267__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7267__30 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "");
        boolean o_supportsBOMinFiles_literalMutationString7267__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString7267__38 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7267__40 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7267__42 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7267__44 = doc.text().contains("??????");
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7267_add27622__22);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7286_add26679_add53293() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document o_supportsBOMinFiles_add7286__4 = Jsoup.parse(in, null, "http://example.com");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__7 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add7286__9 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__15 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_add7286__17 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__19 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__25 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_add7286__27 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__29 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__31 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__37 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_add7286_add26679_add53293__61 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7286_add26679_add53293__61);
        boolean o_supportsBOMinFiles_add7286__39 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__41 = doc.text().contains("??????");
        String o_supportsBOMinFiles_add7286_add26679__69 = doc.text();
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7286_add26679__69);
        boolean o_supportsBOMinFiles_add7286__43 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__45 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7286_add26679_add53293__61);
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7286_add26679__69);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7286_add26679_add53297() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document o_supportsBOMinFiles_add7286__4 = Jsoup.parse(in, null, "http://example.com");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__7 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add7286__9 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__15 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_add7286__17 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__19 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__25 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_add7286__27 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__29 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__31 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7286__37 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_add7286__39 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__41 = doc.text().contains("??????");
        String o_supportsBOMinFiles_add7286_add26679_add53297__69 = doc.text();
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7286_add26679_add53297__69);
        String o_supportsBOMinFiles_add7286_add26679__69 = doc.text();
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7286_add26679__69);
        boolean o_supportsBOMinFiles_add7286__43 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7286__45 = doc.text().contains("??????");
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7286_add26679_add53297__69);
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7286_add26679__69);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7259_add25134() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7259__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString7259__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7259__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString7259__16 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7259__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        Document o_supportsBOMinFiles_literalMutationString7259_add25134__32 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString7259_add25134__32)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_literalMutationString7259_add25134__32)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_literalMutationString7259_add25134__32)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32BE\"> \n  <title>UTF-32BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_literalMutationString7259_add25134__32)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString7259_add25134__32)).hasParent());
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7259__24 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_literalMutationString7259__26 = doc.text().contains("86?Kd&");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7259__32 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString7259__34 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7259__36 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7259__38 = doc.text().contains("??????");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString7259_add25134__32)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_literalMutationString7259_add25134__32)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_literalMutationString7259_add25134__32)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32BE\"> \n  <title>UTF-32BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_literalMutationString7259_add25134__32)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_literalMutationString7259_add25134__32)).hasParent());
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7308() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7308__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add7308__6);
        boolean o_supportsBOMinFiles_add7308__8 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7308__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7308__14 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add7308__14);
        boolean o_supportsBOMinFiles_add7308__16 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7308__16);
        boolean o_supportsBOMinFiles_add7308__18 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7308__18);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7308__24 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add7308__24);
        boolean o_supportsBOMinFiles_add7308__26 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7308__26);
        boolean o_supportsBOMinFiles_add7308__28 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7308__28);
        boolean o_supportsBOMinFiles_add7308__30 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7308__30);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7308__36 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add7308__36);
        String o_supportsBOMinFiles_add7308__38 = doc.text();
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7308__38);
        boolean o_supportsBOMinFiles_add7308__39 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7308__39);
        boolean o_supportsBOMinFiles_add7308__41 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7308__41);
        boolean o_supportsBOMinFiles_add7308__43 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7308__43);
        boolean o_supportsBOMinFiles_add7308__45 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7308__45);
        Assert.assertTrue(o_supportsBOMinFiles_add7308__6);
        Assert.assertFalse(o_supportsBOMinFiles_add7308__8);
        Assert.assertTrue(o_supportsBOMinFiles_add7308__14);
        Assert.assertFalse(o_supportsBOMinFiles_add7308__16);
        Assert.assertFalse(o_supportsBOMinFiles_add7308__18);
        Assert.assertTrue(o_supportsBOMinFiles_add7308__24);
        Assert.assertFalse(o_supportsBOMinFiles_add7308__26);
        Assert.assertFalse(o_supportsBOMinFiles_add7308__28);
        Assert.assertFalse(o_supportsBOMinFiles_add7308__30);
        Assert.assertTrue(o_supportsBOMinFiles_add7308__36);
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7308__38);
        Assert.assertFalse(o_supportsBOMinFiles_add7308__39);
        Assert.assertFalse(o_supportsBOMinFiles_add7308__41);
        Assert.assertFalse(o_supportsBOMinFiles_add7308__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7237_failAssert0_add28794_failAssert0_add56245_failAssert0() throws IOException {
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
                    in = ParseTest.getFile("");
                    doc = Jsoup.parse(in, null, "http://example.com");
                    doc.title().contains("UTF-32BE");
                    doc.title().contains("UTF-32BE");
                    doc.text();
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
                    org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7237 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7237_failAssert0_add28794 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString7237_failAssert0_add28794_failAssert0_add56245 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7304() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7304__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add7304__6);
        boolean o_supportsBOMinFiles_add7304__8 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7304__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7304__14 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add7304__14);
        boolean o_supportsBOMinFiles_add7304__16 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7304__16);
        boolean o_supportsBOMinFiles_add7304__18 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7304__18);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7304__24 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add7304__24);
        boolean o_supportsBOMinFiles_add7304__26 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7304__26);
        boolean o_supportsBOMinFiles_add7304__28 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7304__28);
        boolean o_supportsBOMinFiles_add7304__30 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7304__30);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        Document o_supportsBOMinFiles_add7304__34 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7304__34)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add7304__34)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add7304__34)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32LE\"> \n  <title>UTF-32LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add7304__34)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7304__34)).hasParent());
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7304__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add7304__37);
        boolean o_supportsBOMinFiles_add7304__39 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7304__39);
        boolean o_supportsBOMinFiles_add7304__41 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7304__41);
        boolean o_supportsBOMinFiles_add7304__43 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7304__43);
        boolean o_supportsBOMinFiles_add7304__45 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7304__45);
        Assert.assertTrue(o_supportsBOMinFiles_add7304__6);
        Assert.assertFalse(o_supportsBOMinFiles_add7304__8);
        Assert.assertTrue(o_supportsBOMinFiles_add7304__14);
        Assert.assertFalse(o_supportsBOMinFiles_add7304__16);
        Assert.assertFalse(o_supportsBOMinFiles_add7304__18);
        Assert.assertTrue(o_supportsBOMinFiles_add7304__24);
        Assert.assertFalse(o_supportsBOMinFiles_add7304__26);
        Assert.assertFalse(o_supportsBOMinFiles_add7304__28);
        Assert.assertFalse(o_supportsBOMinFiles_add7304__30);
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7304__34)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add7304__34)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add7304__34)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32LE\"> \n  <title>UTF-32LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add7304__34)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7304__34)).hasParent());
        Assert.assertTrue(o_supportsBOMinFiles_add7304__37);
        Assert.assertFalse(o_supportsBOMinFiles_add7304__39);
        Assert.assertFalse(o_supportsBOMinFiles_add7304__41);
        Assert.assertFalse(o_supportsBOMinFiles_add7304__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7302() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7302__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add7302__6);
        boolean o_supportsBOMinFiles_add7302__8 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7302__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7302__14 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add7302__14);
        boolean o_supportsBOMinFiles_add7302__16 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7302__16);
        boolean o_supportsBOMinFiles_add7302__18 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7302__18);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7302__24 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add7302__24);
        String o_supportsBOMinFiles_add7302__26 = doc.text();
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7302__26);
        boolean o_supportsBOMinFiles_add7302__27 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7302__27);
        boolean o_supportsBOMinFiles_add7302__29 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7302__29);
        boolean o_supportsBOMinFiles_add7302__31 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7302__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7302__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add7302__37);
        boolean o_supportsBOMinFiles_add7302__39 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7302__39);
        boolean o_supportsBOMinFiles_add7302__41 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7302__41);
        boolean o_supportsBOMinFiles_add7302__43 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7302__43);
        boolean o_supportsBOMinFiles_add7302__45 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_add7302__45);
        Assert.assertTrue(o_supportsBOMinFiles_add7302__6);
        Assert.assertFalse(o_supportsBOMinFiles_add7302__8);
        Assert.assertTrue(o_supportsBOMinFiles_add7302__14);
        Assert.assertFalse(o_supportsBOMinFiles_add7302__16);
        Assert.assertFalse(o_supportsBOMinFiles_add7302__18);
        Assert.assertTrue(o_supportsBOMinFiles_add7302__24);
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7302__26);
        Assert.assertFalse(o_supportsBOMinFiles_add7302__27);
        Assert.assertFalse(o_supportsBOMinFiles_add7302__29);
        Assert.assertFalse(o_supportsBOMinFiles_add7302__31);
        Assert.assertTrue(o_supportsBOMinFiles_add7302__37);
        Assert.assertFalse(o_supportsBOMinFiles_add7302__39);
        Assert.assertFalse(o_supportsBOMinFiles_add7302__41);
        Assert.assertFalse(o_supportsBOMinFiles_add7302__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7287_add27091() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7287__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add7287__8 = doc.title().contains("UTF-16BE");
        String o_supportsBOMinFiles_add7287_add27091__14 = doc.text();
        Assert.assertEquals("UTF-16BE Encoded Korean Page with BOM UTF-16BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7287_add27091__14);
        boolean o_supportsBOMinFiles_add7287__10 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7287__16 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_add7287__18 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7287__20 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7287__26 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_add7287__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7287__30 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7287__32 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7287__38 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_add7287__40 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7287__42 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7287__44 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7287__46 = doc.text().contains("??????");
        Assert.assertEquals("UTF-16BE Encoded Korean Page with BOM UTF-16BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add7287_add27091__14);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add7288_add26543() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        String o_supportsBOMinFiles_add7288__6 = doc.title();
        Assert.assertEquals("UTF-16BE Encoded Korean Page with BOM", o_supportsBOMinFiles_add7288__6);
        boolean o_supportsBOMinFiles_add7288__7 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_add7288__9 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7288__15 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_add7288__17 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7288__19 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        Document o_supportsBOMinFiles_add7288_add26543__35 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7288_add26543__35)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add7288_add26543__35)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add7288_add26543__35)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32BE\"> \n  <title>UTF-32BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add7288_add26543__35)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7288_add26543__35)).hasParent());
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7288__25 = doc.title().contains("UTF-32BE");
        boolean o_supportsBOMinFiles_add7288__27 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7288__29 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7288__31 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add7288__37 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_add7288__39 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7288__41 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7288__43 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_add7288__45 = doc.text().contains("??????");
        Assert.assertEquals("UTF-16BE Encoded Korean Page with BOM", o_supportsBOMinFiles_add7288__6);
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7288_add26543__35)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add7288_add26543__35)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add7288_add26543__35)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32BE\"> \n  <title>UTF-32BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add7288_add26543__35)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add7288_add26543__35)).hasParent());
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7254_add27894_add52564() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7254__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString7254__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7254__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString7254_add27894_add52564__22 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_literalMutationString7254_add27894_add52564__22);
        boolean o_supportsBOMinFiles_literalMutationString7254__16 = doc.text().contains("??????");
        String o_supportsBOMinFiles_literalMutationString7254_add27894__26 = doc.text();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7254_add27894__26);
        boolean o_supportsBOMinFiles_literalMutationString7254__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7254__24 = doc.title().contains("UTF-E32BE");
        boolean o_supportsBOMinFiles_literalMutationString7254__26 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7254__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7254__30 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7254__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString7254__38 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7254__40 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7254__42 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7254__44 = doc.text().contains("??????");
        Assert.assertFalse(o_supportsBOMinFiles_literalMutationString7254_add27894_add52564__22);
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7254_add27894__26);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString7250_add27414() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7250__6 = doc.title().contains("UTF-16BE");
        boolean o_supportsBOMinFiles_literalMutationString7250__8 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7250__14 = doc.title().contains("UTF-16LE");
        boolean o_supportsBOMinFiles_literalMutationString7250__16 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7250__18 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7250__24 = doc.title().contains("  ");
        boolean o_supportsBOMinFiles_literalMutationString7250__26 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7250__28 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7250__30 = doc.text().contains("??????");
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_literalMutationString7250__36 = doc.title().contains("UTF-32LE");
        boolean o_supportsBOMinFiles_literalMutationString7250__38 = doc.text().contains("??????");
        String o_supportsBOMinFiles_literalMutationString7250_add27414__62 = doc.text();
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7250_add27414__62);
        boolean o_supportsBOMinFiles_literalMutationString7250__40 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7250__42 = doc.text().contains("??????");
        boolean o_supportsBOMinFiles_literalMutationString7250__44 = doc.text().contains("??????");
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_literalMutationString7250_add27414__62);
    }
}

