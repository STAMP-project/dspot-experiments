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
    public void supportsBOMinFiles_add186() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add186__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add186__6);
        boolean o_supportsBOMinFiles_add186__8 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add186__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add186__14 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add186__14);
        boolean o_supportsBOMinFiles_add186__16 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add186__16);
        boolean o_supportsBOMinFiles_add186__18 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add186__18);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add186__24 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add186__24);
        boolean o_supportsBOMinFiles_add186__26 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add186__26);
        boolean o_supportsBOMinFiles_add186__28 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add186__28);
        boolean o_supportsBOMinFiles_add186__30 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add186__30);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        Document o_supportsBOMinFiles_add186__34 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add186__34)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add186__34)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add186__34)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32LE\"> \n  <title>UTF-32LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add186__34)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add186__34)).hasParent());
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add186__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add186__37);
        boolean o_supportsBOMinFiles_add186__39 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add186__39);
        boolean o_supportsBOMinFiles_add186__41 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add186__41);
        boolean o_supportsBOMinFiles_add186__43 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add186__43);
        boolean o_supportsBOMinFiles_add186__45 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add186__45);
        Assert.assertTrue(o_supportsBOMinFiles_add186__6);
        Assert.assertTrue(o_supportsBOMinFiles_add186__8);
        Assert.assertTrue(o_supportsBOMinFiles_add186__14);
        Assert.assertTrue(o_supportsBOMinFiles_add186__16);
        Assert.assertTrue(o_supportsBOMinFiles_add186__18);
        Assert.assertTrue(o_supportsBOMinFiles_add186__24);
        Assert.assertTrue(o_supportsBOMinFiles_add186__26);
        Assert.assertTrue(o_supportsBOMinFiles_add186__28);
        Assert.assertTrue(o_supportsBOMinFiles_add186__30);
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add186__34)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add186__34)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add186__34)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32LE\"> \n  <title>UTF-32LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add186__34)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add186__34)).hasParent());
        Assert.assertTrue(o_supportsBOMinFiles_add186__37);
        Assert.assertTrue(o_supportsBOMinFiles_add186__39);
        Assert.assertTrue(o_supportsBOMinFiles_add186__41);
        Assert.assertTrue(o_supportsBOMinFiles_add186__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add178() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add178__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add178__6);
        boolean o_supportsBOMinFiles_add178__8 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add178__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add178__14 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add178__14);
        String o_supportsBOMinFiles_add178__16 = doc.text();
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add178__16);
        boolean o_supportsBOMinFiles_add178__17 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add178__17);
        boolean o_supportsBOMinFiles_add178__19 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add178__19);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add178__25 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add178__25);
        boolean o_supportsBOMinFiles_add178__27 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add178__27);
        boolean o_supportsBOMinFiles_add178__29 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add178__29);
        boolean o_supportsBOMinFiles_add178__31 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add178__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add178__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add178__37);
        boolean o_supportsBOMinFiles_add178__39 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add178__39);
        boolean o_supportsBOMinFiles_add178__41 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add178__41);
        boolean o_supportsBOMinFiles_add178__43 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add178__43);
        boolean o_supportsBOMinFiles_add178__45 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add178__45);
        Assert.assertTrue(o_supportsBOMinFiles_add178__6);
        Assert.assertTrue(o_supportsBOMinFiles_add178__8);
        Assert.assertTrue(o_supportsBOMinFiles_add178__14);
        Assert.assertEquals("UTF-16LE Encoded Korean Page with BOM UTF-16LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add178__16);
        Assert.assertTrue(o_supportsBOMinFiles_add178__17);
        Assert.assertTrue(o_supportsBOMinFiles_add178__19);
        Assert.assertTrue(o_supportsBOMinFiles_add178__25);
        Assert.assertTrue(o_supportsBOMinFiles_add178__27);
        Assert.assertTrue(o_supportsBOMinFiles_add178__29);
        Assert.assertTrue(o_supportsBOMinFiles_add178__31);
        Assert.assertTrue(o_supportsBOMinFiles_add178__37);
        Assert.assertTrue(o_supportsBOMinFiles_add178__39);
        Assert.assertTrue(o_supportsBOMinFiles_add178__41);
        Assert.assertTrue(o_supportsBOMinFiles_add178__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add168() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document o_supportsBOMinFiles_add168__4 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add168__4)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add168__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add168__4)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-16BE\"> \n  <title>UTF-16BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-16BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add168__4)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add168__4)).hasParent());
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add168__7 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add168__7);
        boolean o_supportsBOMinFiles_add168__9 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add168__9);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add168__15 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add168__15);
        boolean o_supportsBOMinFiles_add168__17 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add168__17);
        boolean o_supportsBOMinFiles_add168__19 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add168__19);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add168__25 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add168__25);
        boolean o_supportsBOMinFiles_add168__27 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add168__27);
        boolean o_supportsBOMinFiles_add168__29 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add168__29);
        boolean o_supportsBOMinFiles_add168__31 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add168__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add168__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add168__37);
        boolean o_supportsBOMinFiles_add168__39 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add168__39);
        boolean o_supportsBOMinFiles_add168__41 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add168__41);
        boolean o_supportsBOMinFiles_add168__43 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add168__43);
        boolean o_supportsBOMinFiles_add168__45 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add168__45);
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add168__4)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add168__4)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add168__4)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-16BE\"> \n  <title>UTF-16BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-16BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add168__4)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add168__4)).hasParent());
        Assert.assertTrue(o_supportsBOMinFiles_add168__7);
        Assert.assertTrue(o_supportsBOMinFiles_add168__9);
        Assert.assertTrue(o_supportsBOMinFiles_add168__15);
        Assert.assertTrue(o_supportsBOMinFiles_add168__17);
        Assert.assertTrue(o_supportsBOMinFiles_add168__19);
        Assert.assertTrue(o_supportsBOMinFiles_add168__25);
        Assert.assertTrue(o_supportsBOMinFiles_add168__27);
        Assert.assertTrue(o_supportsBOMinFiles_add168__29);
        Assert.assertTrue(o_supportsBOMinFiles_add168__31);
        Assert.assertTrue(o_supportsBOMinFiles_add168__37);
        Assert.assertTrue(o_supportsBOMinFiles_add168__39);
        Assert.assertTrue(o_supportsBOMinFiles_add168__41);
        Assert.assertTrue(o_supportsBOMinFiles_add168__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add190() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add190__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add190__6);
        boolean o_supportsBOMinFiles_add190__8 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add190__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add190__14 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add190__14);
        boolean o_supportsBOMinFiles_add190__16 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add190__16);
        boolean o_supportsBOMinFiles_add190__18 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add190__18);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add190__24 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add190__24);
        boolean o_supportsBOMinFiles_add190__26 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add190__26);
        boolean o_supportsBOMinFiles_add190__28 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add190__28);
        boolean o_supportsBOMinFiles_add190__30 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add190__30);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add190__36 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add190__36);
        String o_supportsBOMinFiles_add190__38 = doc.text();
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add190__38);
        boolean o_supportsBOMinFiles_add190__39 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add190__39);
        boolean o_supportsBOMinFiles_add190__41 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add190__41);
        boolean o_supportsBOMinFiles_add190__43 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add190__43);
        boolean o_supportsBOMinFiles_add190__45 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add190__45);
        Assert.assertTrue(o_supportsBOMinFiles_add190__6);
        Assert.assertTrue(o_supportsBOMinFiles_add190__8);
        Assert.assertTrue(o_supportsBOMinFiles_add190__14);
        Assert.assertTrue(o_supportsBOMinFiles_add190__16);
        Assert.assertTrue(o_supportsBOMinFiles_add190__18);
        Assert.assertTrue(o_supportsBOMinFiles_add190__24);
        Assert.assertTrue(o_supportsBOMinFiles_add190__26);
        Assert.assertTrue(o_supportsBOMinFiles_add190__28);
        Assert.assertTrue(o_supportsBOMinFiles_add190__30);
        Assert.assertTrue(o_supportsBOMinFiles_add190__36);
        Assert.assertEquals("UTF-32LE Encoded Korean Page with BOM UTF-32LE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add190__38);
        Assert.assertTrue(o_supportsBOMinFiles_add190__39);
        Assert.assertTrue(o_supportsBOMinFiles_add190__41);
        Assert.assertTrue(o_supportsBOMinFiles_add190__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add180() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add180__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add180__6);
        boolean o_supportsBOMinFiles_add180__8 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add180__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add180__14 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add180__14);
        boolean o_supportsBOMinFiles_add180__16 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add180__16);
        boolean o_supportsBOMinFiles_add180__18 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add180__18);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        Document o_supportsBOMinFiles_add180__22 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add180__22)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add180__22)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add180__22)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32BE\"> \n  <title>UTF-32BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add180__22)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add180__22)).hasParent());
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add180__25 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add180__25);
        boolean o_supportsBOMinFiles_add180__27 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add180__27);
        boolean o_supportsBOMinFiles_add180__29 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add180__29);
        boolean o_supportsBOMinFiles_add180__31 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add180__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add180__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add180__37);
        boolean o_supportsBOMinFiles_add180__39 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add180__39);
        boolean o_supportsBOMinFiles_add180__41 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add180__41);
        boolean o_supportsBOMinFiles_add180__43 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add180__43);
        boolean o_supportsBOMinFiles_add180__45 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add180__45);
        Assert.assertTrue(o_supportsBOMinFiles_add180__6);
        Assert.assertTrue(o_supportsBOMinFiles_add180__8);
        Assert.assertTrue(o_supportsBOMinFiles_add180__14);
        Assert.assertTrue(o_supportsBOMinFiles_add180__16);
        Assert.assertTrue(o_supportsBOMinFiles_add180__18);
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add180__22)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add180__22)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add180__22)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-32BE\"> \n  <title>UTF-32BE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-32BE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add180__22)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add180__22)).hasParent());
        Assert.assertTrue(o_supportsBOMinFiles_add180__25);
        Assert.assertTrue(o_supportsBOMinFiles_add180__27);
        Assert.assertTrue(o_supportsBOMinFiles_add180__29);
        Assert.assertTrue(o_supportsBOMinFiles_add180__31);
        Assert.assertTrue(o_supportsBOMinFiles_add180__37);
        Assert.assertTrue(o_supportsBOMinFiles_add180__39);
        Assert.assertTrue(o_supportsBOMinFiles_add180__41);
        Assert.assertTrue(o_supportsBOMinFiles_add180__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add172() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add172__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add172__6);
        String o_supportsBOMinFiles_add172__8 = doc.text();
        Assert.assertEquals("UTF-16BE Encoded Korean Page with BOM UTF-16BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add172__8);
        boolean o_supportsBOMinFiles_add172__9 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add172__9);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add172__15 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add172__15);
        boolean o_supportsBOMinFiles_add172__17 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add172__17);
        boolean o_supportsBOMinFiles_add172__19 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add172__19);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add172__25 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add172__25);
        boolean o_supportsBOMinFiles_add172__27 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add172__27);
        boolean o_supportsBOMinFiles_add172__29 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add172__29);
        boolean o_supportsBOMinFiles_add172__31 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add172__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add172__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add172__37);
        boolean o_supportsBOMinFiles_add172__39 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add172__39);
        boolean o_supportsBOMinFiles_add172__41 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add172__41);
        boolean o_supportsBOMinFiles_add172__43 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add172__43);
        boolean o_supportsBOMinFiles_add172__45 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add172__45);
        Assert.assertTrue(o_supportsBOMinFiles_add172__6);
        Assert.assertEquals("UTF-16BE Encoded Korean Page with BOM UTF-16BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add172__8);
        Assert.assertTrue(o_supportsBOMinFiles_add172__9);
        Assert.assertTrue(o_supportsBOMinFiles_add172__15);
        Assert.assertTrue(o_supportsBOMinFiles_add172__17);
        Assert.assertTrue(o_supportsBOMinFiles_add172__19);
        Assert.assertTrue(o_supportsBOMinFiles_add172__25);
        Assert.assertTrue(o_supportsBOMinFiles_add172__27);
        Assert.assertTrue(o_supportsBOMinFiles_add172__29);
        Assert.assertTrue(o_supportsBOMinFiles_add172__31);
        Assert.assertTrue(o_supportsBOMinFiles_add172__37);
        Assert.assertTrue(o_supportsBOMinFiles_add172__39);
        Assert.assertTrue(o_supportsBOMinFiles_add172__41);
        Assert.assertTrue(o_supportsBOMinFiles_add172__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString120_failAssert0() throws IOException {
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString120 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_literalMutationString71_failAssert0() throws IOException {
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
            org.junit.Assert.fail("supportsBOMinFiles_literalMutationString71 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add174() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add174__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add174__6);
        boolean o_supportsBOMinFiles_add174__8 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add174__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        Document o_supportsBOMinFiles_add174__12 = Jsoup.parse(in, null, "http://example.com");
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add174__12)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add174__12)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add174__12)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-16LE\"> \n  <title>UTF-16LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-16LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add174__12)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add174__12)).hasParent());
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add174__15 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add174__15);
        boolean o_supportsBOMinFiles_add174__17 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add174__17);
        boolean o_supportsBOMinFiles_add174__19 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add174__19);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add174__25 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add174__25);
        boolean o_supportsBOMinFiles_add174__27 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add174__27);
        boolean o_supportsBOMinFiles_add174__29 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add174__29);
        boolean o_supportsBOMinFiles_add174__31 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add174__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add174__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add174__37);
        boolean o_supportsBOMinFiles_add174__39 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add174__39);
        boolean o_supportsBOMinFiles_add174__41 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add174__41);
        boolean o_supportsBOMinFiles_add174__43 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add174__43);
        boolean o_supportsBOMinFiles_add174__45 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add174__45);
        Assert.assertTrue(o_supportsBOMinFiles_add174__6);
        Assert.assertTrue(o_supportsBOMinFiles_add174__8);
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add174__12)).isBlock());
        Assert.assertFalse(((Collection) (((Document) (o_supportsBOMinFiles_add174__12)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (o_supportsBOMinFiles_add174__12)).hasText());
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n<html>\n <head> \n  <meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; CHARSET=UTF-16LE\"> \n  <title>UTF-16LE Encoded Korean Page with BOM</title> \n </head> \n <body> \n  <h1>UTF-16LE Encoded Korean Page with BOM</h1> \n  <p>\ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. </p>\n  <p>Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. </p>\n  <pre>\n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53\n</pre>   \n </body>\n</html>", ((Document) (o_supportsBOMinFiles_add174__12)).toString());
        Assert.assertFalse(((Document) (o_supportsBOMinFiles_add174__12)).hasParent());
        Assert.assertTrue(o_supportsBOMinFiles_add174__15);
        Assert.assertTrue(o_supportsBOMinFiles_add174__17);
        Assert.assertTrue(o_supportsBOMinFiles_add174__19);
        Assert.assertTrue(o_supportsBOMinFiles_add174__25);
        Assert.assertTrue(o_supportsBOMinFiles_add174__27);
        Assert.assertTrue(o_supportsBOMinFiles_add174__29);
        Assert.assertTrue(o_supportsBOMinFiles_add174__31);
        Assert.assertTrue(o_supportsBOMinFiles_add174__37);
        Assert.assertTrue(o_supportsBOMinFiles_add174__39);
        Assert.assertTrue(o_supportsBOMinFiles_add174__41);
        Assert.assertTrue(o_supportsBOMinFiles_add174__43);
    }

    @Test(timeout = 10000)
    public void supportsBOMinFiles_add184() throws IOException {
        File in = ParseTest.getFile("/bomtests/bom_utf16be.html");
        Document doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add184__6 = doc.title().contains("UTF-16BE");
        Assert.assertTrue(o_supportsBOMinFiles_add184__6);
        boolean o_supportsBOMinFiles_add184__8 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add184__8);
        in = ParseTest.getFile("/bomtests/bom_utf16le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add184__14 = doc.title().contains("UTF-16LE");
        Assert.assertTrue(o_supportsBOMinFiles_add184__14);
        boolean o_supportsBOMinFiles_add184__16 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add184__16);
        boolean o_supportsBOMinFiles_add184__18 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add184__18);
        in = ParseTest.getFile("/bomtests/bom_utf32be.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add184__24 = doc.title().contains("UTF-32BE");
        Assert.assertTrue(o_supportsBOMinFiles_add184__24);
        String o_supportsBOMinFiles_add184__26 = doc.text();
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add184__26);
        boolean o_supportsBOMinFiles_add184__27 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add184__27);
        boolean o_supportsBOMinFiles_add184__29 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add184__29);
        boolean o_supportsBOMinFiles_add184__31 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add184__31);
        in = ParseTest.getFile("/bomtests/bom_utf32le.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        boolean o_supportsBOMinFiles_add184__37 = doc.title().contains("UTF-32LE");
        Assert.assertTrue(o_supportsBOMinFiles_add184__37);
        boolean o_supportsBOMinFiles_add184__39 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add184__39);
        boolean o_supportsBOMinFiles_add184__41 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add184__41);
        boolean o_supportsBOMinFiles_add184__43 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add184__43);
        boolean o_supportsBOMinFiles_add184__45 = doc.text().contains("가각갂갃간갅");
        Assert.assertTrue(o_supportsBOMinFiles_add184__45);
        Assert.assertTrue(o_supportsBOMinFiles_add184__6);
        Assert.assertTrue(o_supportsBOMinFiles_add184__8);
        Assert.assertTrue(o_supportsBOMinFiles_add184__14);
        Assert.assertTrue(o_supportsBOMinFiles_add184__16);
        Assert.assertTrue(o_supportsBOMinFiles_add184__18);
        Assert.assertTrue(o_supportsBOMinFiles_add184__24);
        Assert.assertEquals("UTF-32BE Encoded Korean Page with BOM UTF-32BE Encoded Korean Page with BOM \ub2e4\uc74c\uc740 \ud604\ub300 \ud55c\uad6d\uc5b4\uc758 \ud55c\uae00 \uc74c\uc808\uc744 \uac00\ub098\ub2e4 \uc21c\uc73c\ub85c \ubc30\uc5f4\ud55c \uac83\uc774\ub2e4. \uc5ec\ub7ec\ubd84\uc774 Unicode 2.0/KS C 5700\uc5d0\uc11c \uc815\uc758\ud55c \ubaa8\ub4e0 \ud55c\uae00 \uc74c\uc808\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc774\ub4e4 \ud55c\uae00\uc744 \ubaa8\ub450 \ubcfc \uc218 \uc788\uc744 \uac83\uc774\uc9c0\ub9cc, \uadf8\ub807\uc9c0 \uc54a\uace0 KS C 5601\uc5d0\uc11c \uc815\uc758\ud55c 2350\uc790\ub9cc\uc744 \ud3ec\ud568\ud55c \ud3f0\ud2b8\ub97c \uc4f0\uace0 \uc788\ub2e4\uba74 \uc0c1\ub2f9\uc218\uc758 \uae00\uc790\ub97c \ubcfc \uc218 \uc5c6\uc744 \uac83\uc774\ub2e4. Enumerated below are the first tens of Hangul syllables(for modern Korean) listed in Unicode 2.0(or later) and ISO-10646. If you use fonts with only a subset of 11,172 syllables, you\'ll find about four fifths of letters are represented as question mark. \n\uac00\uac01\uac02\uac03\uac04\uac05\uac06\uac07\uac08\uac09\uac0a\uac0b\uac0c\uac0d\uac0e\uac0f\uac10\uac11\uac12\uac13\uac14\uac15\uac16\uac17\uac18\uac19\uac1a\uac1b\n\uac1c\uac1d\uac1e\uac1f\uac20\uac21\uac22\uac23\uac24\uac25\uac26\uac27\uac28\uac29\uac2a\uac2b\uac2c\uac2d\uac2e\uac2f\uac30\uac31\uac32\uac33\uac34\uac35\uac36\uac37\n\uac38\uac39\uac3a\uac3b\uac3c\uac3d\uac3e\uac3f\uac40\uac41\uac42\uac43\uac44\uac45\uac46\uac47\uac48\uac49\uac4a\uac4b\uac4c\uac4d\uac4e\uac4f\uac50\uac51\uac52\uac53", o_supportsBOMinFiles_add184__26);
        Assert.assertTrue(o_supportsBOMinFiles_add184__27);
        Assert.assertTrue(o_supportsBOMinFiles_add184__29);
        Assert.assertTrue(o_supportsBOMinFiles_add184__31);
        Assert.assertTrue(o_supportsBOMinFiles_add184__37);
        Assert.assertTrue(o_supportsBOMinFiles_add184__39);
        Assert.assertTrue(o_supportsBOMinFiles_add184__41);
        Assert.assertTrue(o_supportsBOMinFiles_add184__43);
    }
}

