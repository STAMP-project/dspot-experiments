package org.jsoup.helper;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.integration.ParseTest;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;


public class AmplW3CDomTest {
    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationString2152_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF8");
            W3CDom w3c = new W3CDom();
            org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
            Node htmlEl = wDoc.getChildNodes().item(0);
            htmlEl.getNamespaceURI();
            htmlEl.getLocalName();
            htmlEl.getNodeName();
            String out = w3c.asString(wDoc);
            out.contains("ipod");
            org.junit.Assert.fail("convertsGoogle_literalMutationString2152 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationString2152_failAssert0_add6241_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                Node htmlEl = wDoc.getChildNodes().item(0);
                htmlEl.getNamespaceURI();
                htmlEl.getLocalName();
                htmlEl.getNodeName();
                htmlEl.getNodeName();
                String out = w3c.asString(wDoc);
                out.contains("ipod");
                org.junit.Assert.fail("convertsGoogle_literalMutationString2152 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogle_literalMutationString2152_failAssert0_add6241 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationNumber2164_failAssert0_literalMutationString2915_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                Node htmlEl = wDoc.getChildNodes().item(1);
                htmlEl.getNamespaceURI();
                htmlEl.getLocalName();
                htmlEl.getNodeName();
                String out = w3c.asString(wDoc);
                out.contains("ipod");
                org.junit.Assert.fail("convertsGoogle_literalMutationNumber2164 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("convertsGoogle_literalMutationNumber2164_failAssert0_literalMutationString2915 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGooglenull2189_failAssert0_literalMutationString2757_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                Node htmlEl = wDoc.getChildNodes().item(0);
                htmlEl.getNamespaceURI();
                htmlEl.getLocalName();
                htmlEl.getNodeName();
                String out = w3c.asString(wDoc);
                out.contains(null);
                org.junit.Assert.fail("convertsGooglenull2189 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("convertsGooglenull2189_failAssert0_literalMutationString2757 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationString2152_failAssert0_literalMutationString2943_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                Node htmlEl = wDoc.getChildNodes().item(0);
                htmlEl.getNamespaceURI();
                htmlEl.getLocalName();
                htmlEl.getNodeName();
                String out = w3c.asString(wDoc);
                out.contains("ipod");
                org.junit.Assert.fail("convertsGoogle_literalMutationString2152 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogle_literalMutationString2152_failAssert0_literalMutationString2943 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationString2152_failAssert0null6562_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                Node htmlEl = wDoc.getChildNodes().item(0);
                htmlEl.getNamespaceURI();
                htmlEl.getLocalName();
                htmlEl.getNodeName();
                String out = w3c.asString(wDoc);
                out.contains(null);
                org.junit.Assert.fail("convertsGoogle_literalMutationString2152 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogle_literalMutationString2152_failAssert0null6562 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString248_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString248 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF8");
            W3CDom w3c = new W3CDom();
            org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
            String out = w3c.asString(wDoc);
            doc.location();
            wDoc.getDocumentURI();
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString12_failAssert0_literalMutationString233_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UiTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString12 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString12_failAssert0_literalMutationString233 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocationnull23_literalMutationString161_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF8");
            W3CDom w3c = new W3CDom();
            org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
            String out = w3c.asString(null);
            doc.location();
            wDoc.getDocumentURI();
            org.junit.Assert.fail("convertsGoogleLocationnull23_literalMutationString161 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0null1895_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                String out = w3c.asString(null);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0null1895 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0null1894_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(null);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0null1894 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_add1750_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_add1750 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString11_failAssert0_literalMutationString197_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UdF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString11 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString11_failAssert0_literalMutationString197 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString245_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString245 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_add14_literalMutationString89_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document o_convertsGoogleLocation_add14__3 = Jsoup.parse(in, "UTF8");
            Document doc = Jsoup.parse(in, "UTF8");
            W3CDom w3c = new W3CDom();
            org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
            String out = w3c.asString(wDoc);
            doc.location();
            wDoc.getDocumentURI();
            org.junit.Assert.fail("convertsGoogleLocation_add14_literalMutationString89 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_add1751_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_add1751 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void namespacePreservation_literalMutationString6841_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document jsoupDoc;
            jsoupDoc = Jsoup.parse(in, "UTF-8");
            org.w3c.dom.Document doc;
            W3CDom jDom = new W3CDom();
            doc = jDom.fromJsoup(jsoupDoc);
            Node htmlEl = doc.getChildNodes().item(0);
            htmlEl.getNamespaceURI();
            htmlEl.getLocalName();
            htmlEl.getNodeName();
            Node head = htmlEl.getFirstChild();
            head.getNamespaceURI();
            head.getLocalName();
            head.getNodeName();
            Node epubTitle = htmlEl.getChildNodes().item(2).getChildNodes().item(3);
            epubTitle.getTextContent();
            epubTitle.getNamespaceURI();
            epubTitle.getLocalName();
            epubTitle.getNodeName();
            Node xSection = epubTitle.getNextSibling().getNextSibling();
            xSection.getNamespaceURI();
            xSection.getLocalName();
            xSection.getNodeName();
            Node svg = xSection.getNextSibling().getNextSibling();
            svg.getNamespaceURI();
            svg.getLocalName();
            svg.getNodeName();
            Node path = svg.getChildNodes().item(1);
            path.getNamespaceURI();
            path.getLocalName();
            path.getNodeName();
            Node clip = path.getChildNodes().item(1);
            clip.getNamespaceURI();
            clip.getLocalName();
            clip.getNodeName();
            clip.getTextContent();
            Node picture = svg.getNextSibling().getNextSibling();
            picture.getNamespaceURI();
            picture.getLocalName();
            picture.getNodeName();
            Node img = picture.getFirstChild();
            img.getNamespaceURI();
            img.getLocalName();
            img.getNodeName();
            org.junit.Assert.fail("namespacePreservation_literalMutationString6841 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }
}

