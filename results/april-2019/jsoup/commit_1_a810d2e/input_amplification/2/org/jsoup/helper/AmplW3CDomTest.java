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
    public void convertsGoogle_literalMutationString7893_failAssert0_add11884_failAssert0() throws IOException {
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
                w3c.asString(wDoc);
                String out = w3c.asString(wDoc);
                out.contains("ipod");
                org.junit.Assert.fail("convertsGoogle_literalMutationString7893 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogle_literalMutationString7893_failAssert0_add11884 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationString7893_failAssert0_literalMutationString8498_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTRF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                Node htmlEl = wDoc.getChildNodes().item(0);
                htmlEl.getNamespaceURI();
                htmlEl.getLocalName();
                htmlEl.getNodeName();
                String out = w3c.asString(wDoc);
                out.contains("ipod");
                org.junit.Assert.fail("convertsGoogle_literalMutationString7893 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogle_literalMutationString7893_failAssert0_literalMutationString8498 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationNumber7907_literalMutationString8026_failAssert0() throws IOException {
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
            boolean o_convertsGoogle_literalMutationNumber7907__18 = out.contains("ipod");
            org.junit.Assert.fail("convertsGoogle_literalMutationNumber7907_literalMutationString8026 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationString7893_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogle_literalMutationString7893 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationString7893_failAssert0null12250_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(null);
                Node htmlEl = wDoc.getChildNodes().item(0);
                htmlEl.getNamespaceURI();
                htmlEl.getLocalName();
                htmlEl.getNodeName();
                String out = w3c.asString(wDoc);
                out.contains("ipod");
                org.junit.Assert.fail("convertsGoogle_literalMutationString7893 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogle_literalMutationString7893_failAssert0null12250 should have thrown FileNotFoundException");
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
    public void convertsGoogleLocation_literalMutationString1_failAssert0_add1769_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                w3c.asString(wDoc);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_add1769 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_add18_literalMutationString108_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF8");
            W3CDom w3c = new W3CDom();
            org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
            String out = w3c.asString(wDoc);
            wDoc.getDocumentURI();
            doc.location();
            wDoc.getDocumentURI();
            org.junit.Assert.fail("convertsGoogleLocation_add18_literalMutationString108 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_add1772_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_add1772 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString284_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString284 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString5_failAssert0_literalMutationString179_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString5 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString5_failAssert0_literalMutationString179 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_add13_literalMutationString155_failAssert0() throws IOException {
        try {
            ParseTest.getFile("/htmltests/google-ipod.html");
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF8");
            W3CDom w3c = new W3CDom();
            org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
            String out = w3c.asString(wDoc);
            doc.location();
            wDoc.getDocumentURI();
            org.junit.Assert.fail("convertsGoogleLocation_add13_literalMutationString155 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString281_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString281 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void namespacePreservation_literalMutationString12576_failAssert0() throws IOException {
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
            org.junit.Assert.fail("namespacePreservation_literalMutationString12576 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }
}

