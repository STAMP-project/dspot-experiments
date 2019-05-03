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
    public void convertsGoogle_literalMutationString7226_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogle_literalMutationString7226 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationString7226_failAssert0_add11364_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Jsoup.parse(in, "UTF8");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                Node htmlEl = wDoc.getChildNodes().item(0);
                htmlEl.getNamespaceURI();
                htmlEl.getLocalName();
                htmlEl.getNodeName();
                String out = w3c.asString(wDoc);
                out.contains("ipod");
                org.junit.Assert.fail("convertsGoogle_literalMutationString7226 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogle_literalMutationString7226_failAssert0_add11364 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationString7226_failAssert0_add11366_failAssert0() throws IOException {
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
                org.junit.Assert.fail("convertsGoogle_literalMutationString7226 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogle_literalMutationString7226_failAssert0_add11366 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationString7234_failAssert0_literalMutationString8140_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "  ");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                Node htmlEl = wDoc.getChildNodes().item(0);
                htmlEl.getNamespaceURI();
                htmlEl.getLocalName();
                htmlEl.getNodeName();
                String out = w3c.asString(wDoc);
                out.contains("ipod");
                org.junit.Assert.fail("convertsGoogle_literalMutationString7234 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("convertsGoogle_literalMutationString7234_failAssert0_literalMutationString8140 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationString7226_failAssert0_literalMutationNumber8131_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                Node htmlEl = wDoc.getChildNodes().item(-1);
                htmlEl.getNamespaceURI();
                htmlEl.getLocalName();
                htmlEl.getNodeName();
                String out = w3c.asString(wDoc);
                out.contains("ipod");
                org.junit.Assert.fail("convertsGoogle_literalMutationString7226 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogle_literalMutationString7226_failAssert0_literalMutationNumber8131 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationNumber7241_literalMutationString7787_failAssert0() throws IOException {
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
            boolean o_convertsGoogle_literalMutationNumber7241__18 = out.contains("ipod");
            org.junit.Assert.fail("convertsGoogle_literalMutationNumber7241_literalMutationString7787 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationString7231_failAssert0_literalMutationString8039_failAssert0() throws IOException {
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
                out.contains("ipod");
                org.junit.Assert.fail("convertsGoogle_literalMutationString7231 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("convertsGoogle_literalMutationString7231_failAssert0_literalMutationString8039 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationString7226_failAssert0_literalMutationString8126_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UITF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                Node htmlEl = wDoc.getChildNodes().item(0);
                htmlEl.getNamespaceURI();
                htmlEl.getLocalName();
                htmlEl.getNodeName();
                String out = w3c.asString(wDoc);
                out.contains("ipod");
                org.junit.Assert.fail("convertsGoogle_literalMutationString7226 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogle_literalMutationString7226_failAssert0_literalMutationString8126 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogle_literalMutationString7226_failAssert0null11666_failAssert0() throws IOException {
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
                org.junit.Assert.fail("convertsGoogle_literalMutationString7226 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogle_literalMutationString7226_failAssert0null11666 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0null1919_failAssert0null6785_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF8");
                    W3CDom w3c = new W3CDom();
                    org.w3c.dom.Document wDoc = w3c.fromJsoup(null);
                    String out = w3c.asString(null);
                    doc.location();
                    wDoc.getDocumentURI();
                    org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0null1919 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0null1919_failAssert0null6785 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString10_failAssert0_literalMutationString197_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, " TF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString10 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString10_failAssert0_literalMutationString197 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString2_failAssert0_literalMutationString242_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString2_failAssert0_literalMutationString242 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString2_failAssert0_literalMutationString242_failAssert0_add5172_failAssert0() throws IOException {
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
                    wDoc.getDocumentURI();
                    org.junit.Assert.fail("convertsGoogleLocation_literalMutationString2 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString2_failAssert0_literalMutationString242 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString2_failAssert0_literalMutationString242_failAssert0_add5172 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString7_failAssert0_literalMutationString277_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString7 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString7_failAssert0_literalMutationString277 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString5_failAssert0_literalMutationString218_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString5_failAssert0_literalMutationString218 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString300_failAssert0null6799_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "  ");
                    W3CDom w3c = new W3CDom();
                    org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                    String out = w3c.asString(null);
                    doc.location();
                    wDoc.getDocumentURI();
                    org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString300 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString300_failAssert0null6799 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0null1919_failAssert0_literalMutationString4359_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "");
                    W3CDom w3c = new W3CDom();
                    org.w3c.dom.Document wDoc = w3c.fromJsoup(null);
                    String out = w3c.asString(wDoc);
                    doc.location();
                    wDoc.getDocumentURI();
                    org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0null1919 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0null1919_failAssert0_literalMutationString4359 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString5_failAssert0_literalMutationString218_failAssert0_add5442_failAssert0() throws IOException {
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
                    org.junit.Assert.fail("convertsGoogleLocation_literalMutationString5 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString5_failAssert0_literalMutationString218 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString5_failAssert0_literalMutationString218_failAssert0_add5442 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString300_failAssert0_add5746_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Jsoup.parse(in, "  ");
                    Document doc = Jsoup.parse(in, "  ");
                    W3CDom w3c = new W3CDom();
                    org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                    String out = w3c.asString(wDoc);
                    doc.location();
                    wDoc.getDocumentURI();
                    org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString300 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString300_failAssert0_add5746 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString2_failAssert0_literalMutationString242_failAssert0null6407_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    W3CDom w3c = new W3CDom();
                    org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                    String out = w3c.asString(wDoc);
                    doc.location();
                    wDoc.getDocumentURI();
                    org.junit.Assert.fail("convertsGoogleLocation_literalMutationString2 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString2_failAssert0_literalMutationString242 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString2_failAssert0_literalMutationString242_failAssert0null6407 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString10_failAssert0_literalMutationString197_failAssert0null6609_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, null);
                    W3CDom w3c = new W3CDom();
                    org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                    String out = w3c.asString(wDoc);
                    doc.location();
                    wDoc.getDocumentURI();
                    org.junit.Assert.fail("convertsGoogleLocation_literalMutationString10 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString10_failAssert0_literalMutationString197 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString10_failAssert0_literalMutationString197_failAssert0null6609 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocationnull21_literalMutationString173_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, null);
            W3CDom w3c = new W3CDom();
            org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
            String out = w3c.asString(wDoc);
            doc.location();
            wDoc.getDocumentURI();
            org.junit.Assert.fail("convertsGoogleLocationnull21_literalMutationString173 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString300_failAssert0_literalMutationString4385_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "Accept-Encoding");
                    W3CDom w3c = new W3CDom();
                    org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                    String out = w3c.asString(wDoc);
                    doc.location();
                    wDoc.getDocumentURI();
                    org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString300 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString300_failAssert0_literalMutationString4385 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString5_failAssert0_literalMutationString218_failAssert0_literalMutationString3938_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "LS*)");
                    W3CDom w3c = new W3CDom();
                    org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                    String out = w3c.asString(wDoc);
                    doc.location();
                    wDoc.getDocumentURI();
                    org.junit.Assert.fail("convertsGoogleLocation_literalMutationString5 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString5_failAssert0_literalMutationString218 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString5_failAssert0_literalMutationString218_failAssert0_literalMutationString3938 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString300_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "  ");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString300 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString8_failAssert0_literalMutationString209_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "  ");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString8 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString8_failAssert0_literalMutationString209 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString10_failAssert0_literalMutationString197_failAssert0_literalMutationString3971_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, " F8");
                    W3CDom w3c = new W3CDom();
                    org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                    String out = w3c.asString(wDoc);
                    doc.location();
                    wDoc.getDocumentURI();
                    org.junit.Assert.fail("convertsGoogleLocation_literalMutationString10 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString10_failAssert0_literalMutationString197 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString10_failAssert0_literalMutationString197_failAssert0_literalMutationString3971 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString4_failAssert0_literalMutationString240_failAssert0_literalMutationString3592_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UT8");
                    W3CDom w3c = new W3CDom();
                    org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                    String out = w3c.asString(wDoc);
                    doc.location();
                    wDoc.getDocumentURI();
                    org.junit.Assert.fail("convertsGoogleLocation_literalMutationString4 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString4_failAssert0_literalMutationString240 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString4_failAssert0_literalMutationString240_failAssert0_literalMutationString3592 should have thrown FileNotFoundException");
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
    public void convertsGoogleLocation_literalMutationString1_failAssert0null1919_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0null1919 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString12_failAssert0_add1795_failAssert0_literalMutationString3114_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Jsoup.parse(in, "UT{F8");
                    Document doc = Jsoup.parse(in, "UT{F8");
                    W3CDom w3c = new W3CDom();
                    org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                    String out = w3c.asString(wDoc);
                    doc.location();
                    wDoc.getDocumentURI();
                    org.junit.Assert.fail("convertsGoogleLocation_literalMutationString12 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString12_failAssert0_add1795 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString12_failAssert0_add1795_failAssert0_literalMutationString3114 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString9_failAssert0_literalMutationString263_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString9 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString9_failAssert0_literalMutationString263 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString10_failAssert0_literalMutationString197_failAssert0_literalMutationString3966_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, " TF8");
                    W3CDom w3c = new W3CDom();
                    org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                    String out = w3c.asString(wDoc);
                    doc.location();
                    wDoc.getDocumentURI();
                    org.junit.Assert.fail("convertsGoogleLocation_literalMutationString10 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString10_failAssert0_literalMutationString197 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString10_failAssert0_literalMutationString197_failAssert0_literalMutationString3966 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_add14_literalMutationString113_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document o_convertsGoogleLocation_add14__3 = Jsoup.parse(in, "UTF8");
            Document doc = Jsoup.parse(in, "UTF8");
            W3CDom w3c = new W3CDom();
            org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
            String out = w3c.asString(wDoc);
            doc.location();
            wDoc.getDocumentURI();
            org.junit.Assert.fail("convertsGoogleLocation_add14_literalMutationString113 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_add16_literalMutationString89_failAssert0() throws IOException {
        try {
            File in = ParseTest.getFile("");
            Document doc = Jsoup.parse(in, "UTF8");
            W3CDom w3c = new W3CDom();
            org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
            String o_convertsGoogleLocation_add16__9 = w3c.asString(wDoc);
            String out = w3c.asString(wDoc);
            doc.location();
            wDoc.getDocumentURI();
            org.junit.Assert.fail("convertsGoogleLocation_add16_literalMutationString89 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString296_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString296 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0null1918_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0null1918 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_add1783_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_add1783 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString4_failAssert0_literalMutationString230_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString4 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString4_failAssert0_literalMutationString230 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_add1784_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_add1784 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_add1782_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_add1782 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_add1785_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_add1785 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString3_failAssert0_literalMutationString305_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString3_failAssert0_literalMutationString305 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString12_failAssert0_add1794_failAssert0_literalMutationString4066_failAssert0() throws IOException {
        try {
            {
                {
                    ParseTest.getFile("/htmltests/google-ipod.html");
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UT{F8");
                    W3CDom w3c = new W3CDom();
                    org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                    String out = w3c.asString(wDoc);
                    doc.location();
                    wDoc.getDocumentURI();
                    org.junit.Assert.fail("convertsGoogleLocation_literalMutationString12 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString12_failAssert0_add1794 should have thrown UnsupportedEncodingException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString12_failAssert0_add1794_failAssert0_literalMutationString4066 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0null1919_failAssert0_add5727_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF8");
                    W3CDom w3c = new W3CDom();
                    org.w3c.dom.Document wDoc = w3c.fromJsoup(null);
                    w3c.asString(wDoc);
                    String out = w3c.asString(wDoc);
                    doc.location();
                    wDoc.getDocumentURI();
                    org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0null1919 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0null1919_failAssert0_add5727 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString299_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0_literalMutationString299 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString2_failAssert0_literalMutationString242_failAssert0_add5169_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "UTF8");
                    W3CDom w3c = new W3CDom();
                    w3c.fromJsoup(doc);
                    org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                    String out = w3c.asString(wDoc);
                    doc.location();
                    wDoc.getDocumentURI();
                    org.junit.Assert.fail("convertsGoogleLocation_literalMutationString2 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString2_failAssert0_literalMutationString242 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString2_failAssert0_literalMutationString242_failAssert0_add5169 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString2_failAssert0_literalMutationString242_failAssert0_literalMutationString3563_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "/TF8");
                    W3CDom w3c = new W3CDom();
                    org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                    String out = w3c.asString(wDoc);
                    doc.location();
                    wDoc.getDocumentURI();
                    org.junit.Assert.fail("convertsGoogleLocation_literalMutationString2 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString2_failAssert0_literalMutationString242 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString2_failAssert0_literalMutationString242_failAssert0_literalMutationString3563 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString1_failAssert0null1920_failAssert0() throws IOException {
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
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString1_failAssert0null1920 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString10_failAssert0_literalMutationString197_failAssert0_add5461_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, " TF8");
                    W3CDom w3c = new W3CDom();
                    org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                    String out = w3c.asString(wDoc);
                    doc.location();
                    doc.location();
                    wDoc.getDocumentURI();
                    org.junit.Assert.fail("convertsGoogleLocation_literalMutationString10 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString10_failAssert0_literalMutationString197 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString10_failAssert0_literalMutationString197_failAssert0_add5461 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString10_failAssert0_literalMutationString197_failAssert0_add5462_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, " TF8");
                    W3CDom w3c = new W3CDom();
                    org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                    String out = w3c.asString(wDoc);
                    doc.location();
                    wDoc.getDocumentURI();
                    wDoc.getDocumentURI();
                    org.junit.Assert.fail("convertsGoogleLocation_literalMutationString10 should have thrown UnsupportedEncodingException");
                }
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString10_failAssert0_literalMutationString197 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString10_failAssert0_literalMutationString197_failAssert0_add5462 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocationnull22_failAssert0_literalMutationString181_failAssert0() throws IOException {
        try {
            {
                File in = ParseTest.getFile("");
                Document doc = Jsoup.parse(in, "UTF8");
                W3CDom w3c = new W3CDom();
                org.w3c.dom.Document wDoc = w3c.fromJsoup(null);
                String out = w3c.asString(wDoc);
                doc.location();
                wDoc.getDocumentURI();
                org.junit.Assert.fail("convertsGoogleLocationnull22 should have thrown IllegalArgumentException");
            }
            org.junit.Assert.fail("convertsGoogleLocationnull22_failAssert0_literalMutationString181 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString2_failAssert0_literalMutationString242_failAssert0_literalMutationString3566_failAssert0() throws IOException {
        try {
            {
                {
                    File in = ParseTest.getFile("");
                    Document doc = Jsoup.parse(in, "k*0_");
                    W3CDom w3c = new W3CDom();
                    org.w3c.dom.Document wDoc = w3c.fromJsoup(doc);
                    String out = w3c.asString(wDoc);
                    doc.location();
                    wDoc.getDocumentURI();
                    org.junit.Assert.fail("convertsGoogleLocation_literalMutationString2 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString2_failAssert0_literalMutationString242 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString2_failAssert0_literalMutationString242_failAssert0_literalMutationString3566 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void convertsGoogleLocation_literalMutationString5_failAssert0_literalMutationString218_failAssert0null6596_failAssert0() throws IOException {
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
                    org.junit.Assert.fail("convertsGoogleLocation_literalMutationString5 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("convertsGoogleLocation_literalMutationString5_failAssert0_literalMutationString218 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("convertsGoogleLocation_literalMutationString5_failAssert0_literalMutationString218_failAssert0null6596 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void namespacePreservation_literalMutationString28213_failAssert0() throws IOException {
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
            org.junit.Assert.fail("namespacePreservation_literalMutationString28213 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }
}

