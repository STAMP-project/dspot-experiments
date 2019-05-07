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
    public void convertsGoogle_add28408_literalMutationString28803_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF8");
            W3CDom w3c = new W3CDom();
            org.w3c.dom.Document o_convertsGoogle_add28408__7 = w3c.fromJsoup(doc);
            org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
            Node htmlEl = wDoc.getChildNodes().item(0);
            htmlEl.getNamespaceURI();
            htmlEl.getLocalName();
            htmlEl.getNodeName();
            String out = w3c.asString(wDoc);
            boolean o_convertsGoogle_add28408__18 = out.contains("ipod");
            org.junit.Assert.fail("convertsGoogle_add28408_literalMutationString28803 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_add28412_literalMutationString28851_failAssert0_literalMutationString34203_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UT8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                Node htmlEl = wDoc.getChildNodes().item(0);
                htmlEl.getNamespaceURI();
                htmlEl.getLocalName();
                htmlEl.getLocalName();
                htmlEl.getNodeName();
                String out = w3c.asString(wDoc);
                boolean o_convertsGoogle_add28412__18 = out.contains("ipod");
                org.junit.Assert.fail("convertsGoogle_add28412_literalMutationString28851 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogle_add28412_literalMutationString28851_failAssert0_literalMutationString34203 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_add28412_literalMutationString28851_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF8");
            W3CDom w3c = new W3CDom();
            org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
            Node htmlEl = wDoc.getChildNodes().item(0);
            htmlEl.getNamespaceURI();
            htmlEl.getLocalName();
            htmlEl.getLocalName();
            htmlEl.getNodeName();
            String out = w3c.asString(wDoc);
            boolean o_convertsGoogle_add28412__18 = out.contains("ipod");
            org.junit.Assert.fail("convertsGoogle_add28412_literalMutationString28851 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationString28388_failAssert0_literalMutationNumber29108_failAssert0_literalMutationString34576_failAssert0() throws IOException {
        try {
            {
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
                    org.junit.Assert.fail("convertsGoogle_literalMutationString28388 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("convertsGoogle_literalMutationString28388_failAssert0_literalMutationNumber29108 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("convertsGoogle_literalMutationString28388_failAssert0_literalMutationNumber29108_failAssert0_literalMutationString34576 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_add28412_literalMutationString28851_failAssert0null39350_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(null);
                Node htmlEl = wDoc.getChildNodes().item(0);
                htmlEl.getNamespaceURI();
                htmlEl.getLocalName();
                htmlEl.getLocalName();
                htmlEl.getNodeName();
                String out = w3c.asString(wDoc);
                boolean o_convertsGoogle_add28412__18 = out.contains("ipod");
                org.junit.Assert.fail("convertsGoogle_add28412_literalMutationString28851 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogle_add28412_literalMutationString28851_failAssert0null39350 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationString28384_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogle_literalMutationString28384 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationString28387_failAssert0_add32383_failAssert0_literalMutationString34970_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF8");
                    W3CDom w3c = new W3CDom();
                    org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                    Node htmlEl = wDoc.getChildNodes().item(0);
                    htmlEl.getNamespaceURI();
                    htmlEl.getNamespaceURI();
                    htmlEl.getLocalName();
                    htmlEl.getNodeName();
                    String out = w3c.asString(wDoc);
                    out.contains("ipod");
                    org.junit.Assert.fail("convertsGoogle_literalMutationString28387 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("convertsGoogle_literalMutationString28387_failAssert0_add32383 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("convertsGoogle_literalMutationString28387_failAssert0_add32383_failAssert0_literalMutationString34970 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_add28412_literalMutationString28851_failAssert0_add38408_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                Node htmlEl = wDoc.getChildNodes().item(0);
                htmlEl.getNamespaceURI();
                htmlEl.getLocalName();
                htmlEl.getLocalName();
                htmlEl.getLocalName();
                htmlEl.getNodeName();
                String out = w3c.asString(wDoc);
                boolean o_convertsGoogle_add28412__18 = out.contains("ipod");
                org.junit.Assert.fail("convertsGoogle_add28412_literalMutationString28851 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogle_add28412_literalMutationString28851_failAssert0_add38408 should have thrown FileNotFoundException");
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
    public void convertsGoogleLocation_literalMutationString1_failAssert0_add1772_failAssert0_literalMutationString2816_failAssert0() throws IOException {
        try {
            {
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
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_add1772_failAssert0_literalMutationString2816 should have thrown FileNotFoundException");
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
    public void convertsGoogleLocation_add13_literalMutationString155_failAssert0null5387_failAssert0() throws IOException {
        try {
            {
                ParseTest.getFile("/htmltests/google-ipod.html");
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(null);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_add13_literalMutationString155 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_add13_literalMutationString155_failAssert0null5387 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_add1772_failAssert0null5467_failAssert0() throws IOException {
        try {
            {
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
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_add1772 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_add1772_failAssert0null5467 should have thrown FileNotFoundException");
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
    public void convertsGoogleLocation_add13_literalMutationString155_failAssert0_add4741_failAssert0() throws IOException {
        try {
            {
                ParseTest.getFile("/htmltests/google-ipod.html");
                ParseTest.getFile("/htmltests/google-ipod.html");
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_add13_literalMutationString155 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_add13_literalMutationString155_failAssert0_add4741 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_add1772_failAssert0_add4876_failAssert0() throws IOException {
        try {
            {
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
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_add1772_failAssert0_add4876 should have thrown FileNotFoundException");
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
    public void convertsGoogleLocationnull23_add339_literalMutationString2204_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF8");
            W3CDom w3c = new W3CDom();
            org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
            String out = w3c.asString(null);
            doc.location();
            doc.location();
            wDoc.getDocumentURI();
            org.junit.Assert.fail("convertsGoogleLocationnull23_add339_literalMutationString2204 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void namespacePreservation_literalMutationString39816_failAssert0() throws IOException {
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
            org.junit.Assert.fail("namespacePreservation_literalMutationString39816 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }
}

