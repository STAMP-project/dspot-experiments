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
    public void convertsGoogle_literalMutationString2363_failAssert0_add6514_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                wDoc.getChildNodes().item(0);
                Node htmlEl = wDoc.getChildNodes().item(0);
                htmlEl.getNamespaceURI();
                htmlEl.getLocalName();
                htmlEl.getNodeName();
                String out = w3c.asString(wDoc);
                out.contains("ipod");
                org.junit.Assert.fail("convertsGoogle_literalMutationString2363 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogle_literalMutationString2363_failAssert0_add6514 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationString2363_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogle_literalMutationString2363 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationString2363_failAssert0_literalMutationString3282_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "}TF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                Node htmlEl = wDoc.getChildNodes().item(0);
                htmlEl.getNamespaceURI();
                htmlEl.getLocalName();
                htmlEl.getNodeName();
                String out = w3c.asString(wDoc);
                out.contains("ipod");
                org.junit.Assert.fail("convertsGoogle_literalMutationString2363 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogle_literalMutationString2363_failAssert0_literalMutationString3282 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationString2380_literalMutationString2597_failAssert0() throws IOException {
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
            boolean o_convertsGoogle_literalMutationString2380__17 = out.contains("  ");
            org.junit.Assert.fail("convertsGoogle_literalMutationString2380_literalMutationString2597 should have thrown FileNotFoundException");
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
    public void convertsGoogleLocation_literalMutationString1_failAssert0_add1765_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_add1765 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString264_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString264 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_add1762_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_add1762 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_add13_literalMutationString113_failAssert0() throws IOException {
        try {
            ParseTest.getFile("/htmltests/google-ipod.html");
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF8");
            W3CDom w3c = new W3CDom();
            org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
            String out = w3c.asString(wDoc);
            doc.location();
            wDoc.getDocumentURI();
            org.junit.Assert.fail("convertsGoogleLocation_add13_literalMutationString113 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_add1763_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                String out = w3c.asString(wDoc);
                doc.location();
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_add1763 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_add1761_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                w3c.fromJsoup(doc);
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_add1761 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString268_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "J$%X");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString268 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString3_failAssert0_literalMutationString318_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString3 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString3_failAssert0_literalMutationString318 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0null1905_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0null1905 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString2_failAssert0_literalMutationString197_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString2 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString2_failAssert0_literalMutationString197 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocationnull23_literalMutationString84_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF8");
            W3CDom w3c = new W3CDom();
            org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
            String out = w3c.asString(null);
            doc.location();
            wDoc.getDocumentURI();
            org.junit.Assert.fail("convertsGoogleLocationnull23_literalMutationString84 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_add15_literalMutationString156_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF8");
            W3CDom w3c = new W3CDom();
            org.w3c.dom.Document o_convertsGoogleLocation_add15__7 = w3c.fromJsoup(doc);
            org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
            String out = w3c.asString(wDoc);
            doc.location();
            wDoc.getDocumentURI();
            org.junit.Assert.fail("convertsGoogleLocation_add15_literalMutationString156 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0null1903_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, null);
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0null1903 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void namespacePreservation_literalMutationString7297_failAssert0() throws IOException {
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
            org.junit.Assert.fail("namespacePreservation_literalMutationString7297 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }
}

