package org.jsoup.nodes;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import org.jsoup.Jsoup;
import org.jsoup.integration.ParseTest;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static org.jsoup.nodes.Document.OutputSettings.Syntax.xml;


public class AmplDocumentTest {
    private static final String charsetUtf8 = "UTF-8";

    private static final String charsetIso8859 = "ISO-8859-1";

    @Test(timeout = 10000)
    public void testLocation_literalMutationString4095_failAssert0_add6227_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString4095 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString4095_failAssert0_add6227 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString4077_failAssert0_add6405_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                doc.location();
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString4077 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString4077_failAssert0_add6405 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString4104_literalMutationString4396_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("");
            doc = Jsoup.parse(in, null, "http://www.nytimes.com/2O010/07/26/business/global/26bp.html?hp");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_literalMutationString4104_literalMutationString4396 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString4095_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("");
            doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_literalMutationString4095 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString4095_failAssert0_add6222_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                doc.baseUri();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString4095 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString4095_failAssert0_add6222 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString4077_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
            doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_literalMutationString4077 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString4077_failAssert0null6710_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile(null);
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString4077 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString4077_failAssert0null6710 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString4077_failAssert0_literalMutationString5479_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString4077 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString4077_failAssert0_literalMutationString5479 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Ignore
    @Test
    public void testOverflowClone() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 100000; i++) {
            builder.insert(0, "<i>");
            builder.append("</i>");
        }
        Document doc = Jsoup.parse(builder.toString());
        doc.clone();
    }

    private Document createHtmlDocument(String charset) {
        final Document doc = Document.createShell("");
        doc.head().appendElement("meta").attr("charset", charset);
        doc.head().appendElement("meta").attr("name", "charset").attr("content", charset);
        return doc;
    }

    private Document createXmlDocument(String version, String charset, boolean addDecl) {
        final Document doc = new Document("");
        doc.appendElement("root").text("node");
        doc.outputSettings().syntax(xml);
        if (addDecl) {
            XmlDeclaration decl = new XmlDeclaration("xml", false);
            decl.attr("version", version);
            decl.attr("encoding", charset);
            doc.prependChild(decl);
        }
        return doc;
    }

    @Test(timeout = 10000)
    public void parseAndHtmlOnDifferentThreads_add22577_remove24724() throws InterruptedException {
        String html = "<p>Alrighty then it\'s not \ud83d\udca9. <span>Next</span></p>";
        Assert.assertEquals("<p>Alrighty then it\'s not \ud83d\udca9. <span>Next</span></p>", html);
        String asci = "<p>Alrighty then it's not &#x1f4a9;. <span>Next</span></p>";
        Assert.assertEquals("<p>Alrighty then it\'s not &#x1f4a9;. <span>Next</span></p>", asci);
        final Document doc = Jsoup.parse(html);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <p>Alrighty then it\'s not \ud83d\udca9. <span>Next</span></p>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        final String[] out = new String[1];
        final Elements p = doc.select("p");
        String o_parseAndHtmlOnDifferentThreads_add22577__9 = p.outerHtml();
        Assert.assertEquals("<p>Alrighty then it\'s not \ud83d\udca9. <span>Next</span></p>", o_parseAndHtmlOnDifferentThreads_add22577__9);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                out[0] = p.outerHtml();
                Assert.assertEquals("<p>Alrighty then it\'s not \ud83d\udca9. <span>Next</span></p>", out[0]);
                doc.outputSettings();
                doc.outputSettings().charset(StandardCharsets.US_ASCII);
                Assert.assertEquals("<p>Alrighty then it\'s not \ud83d\udca9. <span>Next</span></p>", out[0]);
            }
        });
        thread.start();
        String String_159 = out[0];
        Assert.assertNull(String_159);
        Charset o_parseAndHtmlOnDifferentThreads_add22577__25 = doc.outputSettings().charset();
        String o_parseAndHtmlOnDifferentThreads_add22577__27 = p.outerHtml();
        Assert.assertEquals("<p>Alrighty then it\'s not \ud83d\udca9. <span>Next</span></p>", o_parseAndHtmlOnDifferentThreads_add22577__27);
        Assert.assertEquals("<p>Alrighty then it\'s not \ud83d\udca9. <span>Next</span></p>", html);
        Assert.assertEquals("<p>Alrighty then it\'s not &#x1f4a9;. <span>Next</span></p>", asci);
        Assert.assertFalse(((Collection) (((Document) (doc)).getAllElements())).isEmpty());
        Assert.assertTrue(((Document) (doc)).hasText());
        Assert.assertFalse(((Document) (doc)).isBlock());
        Assert.assertEquals("<html>\n <head></head>\n <body>\n  <p>Alrighty then it\'s not &#x1f4a9;. <span>Next</span></p>\n </body>\n</html>", ((Document) (doc)).toString());
        Assert.assertFalse(((Document) (doc)).hasParent());
        Assert.assertEquals("<p>Alrighty then it\'s not \ud83d\udca9. <span>Next</span></p>", o_parseAndHtmlOnDifferentThreads_add22577__9);
        Assert.assertNull(String_159);
    }
}

