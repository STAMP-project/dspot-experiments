package org.jsoup.helper;


import java.io.File;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.integration.ParseTest;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.org.jsoup.nodes.Document;


public class W3CDomTest {
    @Test
    public void simpleConversion() {
        String html = "<html><head><title>W3c</title></head><body><p class='one' id=12>Text</p><!-- comment --><invalid>What<script>alert('!')";
        org.jsoup.nodes.Document doc = Jsoup.parse(html);
        W3CDom w3c = new W3CDom();
        Document wDoc = w3c.fromJsoup(doc);
        String out = TextUtil.stripNewlines(w3c.asString(wDoc));
        String expected = TextUtil.stripNewlines(("<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><title>W3c</title>" + ("</head><body><p class=\"one\" id=\"12\">Text</p><!-- comment --><invalid>What<script>alert(\'!\')</script>" + "</invalid></body></html>")));
        Assert.assertEquals(expected, out);
    }

    @Test
    public void convertsGoogle() throws IOException {
        File in = ParseTest.getFile("/htmltests/google-ipod.html");
        org.jsoup.nodes.Document doc = Jsoup.parse(in, "UTF8");
        W3CDom w3c = new W3CDom();
        Document wDoc = w3c.fromJsoup(doc);
        Node htmlEl = wDoc.getChildNodes().item(0);
        Assert.assertEquals(null, htmlEl.getNamespaceURI());
        Assert.assertEquals("html", htmlEl.getLocalName());
        Assert.assertEquals("html", htmlEl.getNodeName());
        String out = w3c.asString(wDoc);
        Assert.assertTrue(out.contains("ipod"));
    }

    @Test
    public void convertsGoogleLocation() throws IOException {
        File in = ParseTest.getFile("/htmltests/google-ipod.html");
        org.jsoup.nodes.Document doc = Jsoup.parse(in, "UTF8");
        W3CDom w3c = new W3CDom();
        Document wDoc = w3c.fromJsoup(doc);
        String out = w3c.asString(wDoc);
        Assert.assertEquals(doc.location(), wDoc.getDocumentURI());
    }

    @Test
    public void namespacePreservation() throws IOException {
        File in = ParseTest.getFile("/htmltests/namespaces.xhtml");
        org.jsoup.nodes.Document jsoupDoc;
        jsoupDoc = Jsoup.parse(in, "UTF-8");
        Document doc;
        org.jsoup.helper.W3CDom jDom = new org.jsoup.helper.W3CDom();
        doc = jDom.fromJsoup(jsoupDoc);
        Node htmlEl = doc.getChildNodes().item(0);
        Assert.assertEquals("http://www.w3.org/1999/xhtml", htmlEl.getNamespaceURI());
        Assert.assertEquals("html", htmlEl.getLocalName());
        Assert.assertEquals("html", htmlEl.getNodeName());
        // inherits default namespace
        Node head = htmlEl.getFirstChild();
        Assert.assertEquals("http://www.w3.org/1999/xhtml", head.getNamespaceURI());
        Assert.assertEquals("head", head.getLocalName());
        Assert.assertEquals("head", head.getNodeName());
        Node epubTitle = htmlEl.getChildNodes().item(2).getChildNodes().item(3);
        Assert.assertEquals("Check", epubTitle.getTextContent());
        Assert.assertEquals("http://www.idpf.org/2007/ops", epubTitle.getNamespaceURI());
        Assert.assertEquals("title", epubTitle.getLocalName());
        Assert.assertEquals("epub:title", epubTitle.getNodeName());
        Node xSection = epubTitle.getNextSibling().getNextSibling();
        Assert.assertEquals("urn:test", xSection.getNamespaceURI());
        Assert.assertEquals("section", xSection.getLocalName());
        Assert.assertEquals("x:section", xSection.getNodeName());
        // https://github.com/jhy/jsoup/issues/977
        // does not keep last set namespace
        Node svg = xSection.getNextSibling().getNextSibling();
        Assert.assertEquals("http://www.w3.org/2000/svg", svg.getNamespaceURI());
        Assert.assertEquals("svg", svg.getLocalName());
        Assert.assertEquals("svg", svg.getNodeName());
        Node path = svg.getChildNodes().item(1);
        Assert.assertEquals("http://www.w3.org/2000/svg", path.getNamespaceURI());
        Assert.assertEquals("path", path.getLocalName());
        Assert.assertEquals("path", path.getNodeName());
        Node clip = path.getChildNodes().item(1);
        Assert.assertEquals("http://example.com/clip", clip.getNamespaceURI());
        Assert.assertEquals("clip", clip.getLocalName());
        Assert.assertEquals("clip", clip.getNodeName());
        Assert.assertEquals("456", clip.getTextContent());
        Node picture = svg.getNextSibling().getNextSibling();
        Assert.assertEquals("http://www.w3.org/1999/xhtml", picture.getNamespaceURI());
        Assert.assertEquals("picture", picture.getLocalName());
        Assert.assertEquals("picture", picture.getNodeName());
        Node img = picture.getFirstChild();
        Assert.assertEquals("http://www.w3.org/1999/xhtml", img.getNamespaceURI());
        Assert.assertEquals("img", img.getLocalName());
        Assert.assertEquals("img", img.getNodeName());
    }

    @Test
    public void handlesInvalidAttributeNames() {
        String html = "<html><head></head><body style=\"color: red\" \" name\"></body></html>";
        org.jsoup.nodes.Document jsoupDoc;
        jsoupDoc = Jsoup.parse(html);
        Element body = jsoupDoc.select("body").first();
        Assert.assertTrue(body.hasAttr("\""));// actually an attribute with key '"'. Correct per HTML5 spec, but w3c xml dom doesn't dig it

        Assert.assertTrue(body.hasAttr("name\""));
        Document w3Doc = new W3CDom().fromJsoup(jsoupDoc);
    }

    @Test
    public void treatsUndeclaredNamespaceAsLocalName() {
        String html = "<fb:like>One</fb:like>";
        org.jsoup.nodes.Document doc = Jsoup.parse(html);
        Document w3Doc = new W3CDom().fromJsoup(doc);
        Node htmlEl = w3Doc.getFirstChild();
        Assert.assertNull(htmlEl.getNamespaceURI());
        Assert.assertEquals("html", htmlEl.getLocalName());
        Assert.assertEquals("html", htmlEl.getNodeName());
        Node fb = htmlEl.getFirstChild().getNextSibling().getFirstChild();
        Assert.assertNull(fb.getNamespaceURI());
        Assert.assertEquals("like", fb.getLocalName());
        Assert.assertEquals("fb:like", fb.getNodeName());
    }
}

