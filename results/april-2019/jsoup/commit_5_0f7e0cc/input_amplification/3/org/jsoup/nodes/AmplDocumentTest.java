package org.jsoup.nodes;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.integration.ParseTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static org.jsoup.nodes.Document.OutputSettings.Syntax.xml;


public class AmplDocumentTest {
    private static final String charsetUtf8 = "UTF-8";

    private static final String charsetIso8859 = "ISO-8859-1";

    @Test(timeout = 10000)
    public void testLocation_literalMutationString17979_literalMutationString18596_literalMutationString22256_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("");
            Document doc = Jsoup.parse(in, "UTF8", "http://www.yahoo.co.jp/index.html");
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
            doc = Jsoup.parse(in, null, "ha1#62$sRQ.sKRA05g{aIExCh}S)[,;$a)tC=X<S^>i|EL&,gVt:y]{Ou{T29f");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_literalMutationString17979_literalMutationString18596_literalMutationString22256 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString17970_failAssert0_add20259_failAssert0() throws IOException {
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
                org.junit.Assert.fail("testLocation_literalMutationString17970 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString17970_failAssert0_add20259 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString17989_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("");
            doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_literalMutationString17989 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString17970_failAssert0_add20259_failAssert0_literalMutationString23663_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/inde2.html");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                    doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    doc.location();
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString17970 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString17970_failAssert0_add20259 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString17970_failAssert0_add20259_failAssert0_literalMutationString23663 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString17996_literalMutationString18467_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("");
            doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/0_7/26/business/global/26bp.html?hp");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_literalMutationString17996_literalMutationString18467 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString17970_failAssert0_literalMutationString19176_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, "UTF-8", " Hello\nthere \u00a0  ");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString17970 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString17970_failAssert0_literalMutationString19176 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString17989_failAssert0_add20225_failAssert0() throws IOException {
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
                org.junit.Assert.fail("testLocation_literalMutationString17989 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString17989_failAssert0_add20225 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString17970_failAssert0null20576_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("");
                Document doc = Jsoup.parse(in, null, "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString17970 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString17970_failAssert0null20576 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_add18002_literalMutationString18137_literalMutationString22121_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "");
            String o_testLocation_add18002__6 = doc.location();
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
            doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_add18002_literalMutationString18137_literalMutationString22121 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString17989_failAssert0_add20223_failAssert0() throws IOException {
        try {
            {
                File in = new ParseTest().getFile("/htmltests/yahoo-jp.html");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                String location = doc.location();
                String baseUri = doc.baseUri();
                in = new ParseTest().getFile("");
                doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                doc.location();
                location = doc.location();
                baseUri = doc.baseUri();
                org.junit.Assert.fail("testLocation_literalMutationString17989 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString17989_failAssert0_add20223 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString17970_failAssert0_add20259_failAssert0_add28685_failAssert0() throws IOException {
        try {
            {
                {
                    File in = new ParseTest().getFile("");
                    Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                    Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
                    String location = doc.location();
                    String baseUri = doc.baseUri();
                    in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
                    doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
                    doc.location();
                    location = doc.location();
                    baseUri = doc.baseUri();
                    org.junit.Assert.fail("testLocation_literalMutationString17970 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testLocation_literalMutationString17970_failAssert0_add20259 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString17970_failAssert0_add20259_failAssert0_add28685 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString17993_failAssert0_literalMutationString19211_failAssert0() throws IOException {
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
                org.junit.Assert.fail("testLocation_literalMutationString17993 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testLocation_literalMutationString17993_failAssert0_literalMutationString19211 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testLocation_literalMutationString17970_failAssert0() throws IOException {
        try {
            File in = new ParseTest().getFile("");
            Document doc = Jsoup.parse(in, "UTF-8", "http://www.yahoo.co.jp/index.html");
            String location = doc.location();
            String baseUri = doc.baseUri();
            in = new ParseTest().getFile("/htmltests/nyt-article-1.html");
            doc = Jsoup.parse(in, null, "http://www.nytimes.com/2010/07/26/business/global/26bp.html?hp");
            location = doc.location();
            baseUri = doc.baseUri();
            org.junit.Assert.fail("testLocation_literalMutationString17970 should have thrown FileNotFoundException");
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
}

