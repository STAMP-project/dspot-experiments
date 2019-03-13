/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.xml.rule;


import XmlLanguageModule.NAME;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.xml.XmlParserOptions;
import net.sourceforge.pmd.lang.xml.ast.XmlNode;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;


public class AbstractDomXmlRuleTest {
    @Test
    public void testVisit() throws Exception {
        String source = "<?xml version=\"1.0\"?><?mypi?><!DOCTYPE testDoc [<!ENTITY entity \"e\">]><!--Comment--><foo abc=\"abc\"><bar>TEXT</bar><![CDATA[cdata!]]>&gt;&entity;&lt;</foo>";
        XmlParserOptions parserOptions = new XmlParserOptions();
        parserOptions.setExpandEntityReferences(false);
        Parser parser = LanguageRegistry.getLanguage(NAME).getDefaultVersion().getLanguageVersionHandler().getParser(parserOptions);
        XmlNode xmlNode = ((XmlNode) (parser.parse(null, new StringReader(source))));
        List<XmlNode> nodes = new ArrayList<>();
        nodes.add(xmlNode);
        AbstractDomXmlRuleTest.MyRule rule = new AbstractDomXmlRuleTest.MyRule();
        rule.apply(nodes, null);
        List<Node> visited = rule.visitedNodes.get("Attr");
        Assert.assertEquals(1, visited.size());
        Assert.assertEquals("abc", visited.get(0).getLocalName());
        visited = rule.visitedNodes.get("CharacterData");
        Assert.assertEquals(1, visited.size());
        Assert.assertEquals("cdata!", ((CharacterData) (visited.get(0))).getData());
        visited = rule.visitedNodes.get("Comment");
        Assert.assertEquals("Comment", ((Comment) (visited.get(0))).getData());
        visited = rule.visitedNodes.get("Document");
        Assert.assertEquals(1, visited.size());
        visited = rule.visitedNodes.get("DocumentType");
        Assert.assertEquals("testDoc", ((DocumentType) (visited.get(0))).getName());
        visited = rule.visitedNodes.get("Element");
        Assert.assertEquals(2, visited.size());
        Assert.assertEquals("foo", visited.get(0).getLocalName());
        Assert.assertEquals("bar", visited.get(1).getLocalName());
        // TODO Figure out how to trigger this.
        // visited = rule.visitedNodes.get("Entity");
        // assertEquals(0, visited.size());
        visited = rule.visitedNodes.get("EntityReference");
        Assert.assertEquals(1, visited.size());
        Assert.assertEquals("entity", ((EntityReference) (visited.get(0))).getNodeName());
        // TODO Figure out how to trigger this.
        // visited = rule.visitedNodes.get("Notation");
        // assertEquals(0, visited.size());
        visited = rule.visitedNodes.get("ProcessingInstruction");
        Assert.assertEquals(1, visited.size());
        Assert.assertEquals("mypi", ((ProcessingInstruction) (visited.get(0))).getTarget());
        visited = rule.visitedNodes.get("Text");
        Assert.assertEquals(3, visited.size());
        Assert.assertEquals("TEXT", ((Text) (visited.get(0))).getData());
        Assert.assertEquals(">", ((Text) (visited.get(1))).getData());
        Assert.assertEquals("e<", ((Text) (visited.get(2))).getData());
    }

    @Test
    public void dtdIsNotLookedUp() {
        String source = "<!DOCTYPE struts-config PUBLIC " + ((" \"-//Apache Software Foundation//DTD Struts Configuration 1.1//EN \" " + " \"http://jakarta.inexistinghost.org/struts/dtds/struts-config_1_1.dtd\" >") + "<struts-config/>");
        XmlParserOptions parserOptions = new XmlParserOptions();
        parserOptions.setLookupDescriptorDoc(false);
        Parser parser = LanguageRegistry.getLanguage(NAME).getDefaultVersion().getLanguageVersionHandler().getParser(parserOptions);
        XmlNode xmlNode = ((XmlNode) (parser.parse(null, new StringReader(source))));
        // no exception should be thrown
        AbstractDomXmlRuleTest.MyRule rule = new AbstractDomXmlRuleTest.MyRule();
        List<XmlNode> nodes = new ArrayList<>();
        nodes.add(xmlNode);
        rule.apply(nodes, null);
        // first element is still parsed
        Assert.assertNotNull(rule.visitedNodes.get("Element"));
    }

    @Test
    public void xsdIsNotLookedUp() {
        String source = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> " + (((("<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" " + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ") + "xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.inexisting.com/xml/ns/javaee/web-app_2_5.xsd\" ") + "version=\"2.5\">") + "</web-app>");
        XmlParserOptions parserOptions = new XmlParserOptions();
        Parser parser = LanguageRegistry.getLanguage(NAME).getDefaultVersion().getLanguageVersionHandler().getParser(parserOptions);
        XmlNode xmlNode = ((XmlNode) (parser.parse(null, new StringReader(source))));
        // no exception should be thrown
        // first element is still parsed
        AbstractDomXmlRuleTest.MyRule rule = new AbstractDomXmlRuleTest.MyRule();
        List<XmlNode> nodes = new ArrayList<>();
        nodes.add(xmlNode);
        rule.apply(nodes, null);
        Assert.assertNotNull(rule.visitedNodes.get("Element"));
    }

    private static class MyRule extends AbstractDomXmlRule {
        final Map<String, List<Node>> visitedNodes = new HashMap<>();

        MyRule() {
        }

        private void visit(String key, Node node) {
            List<Node> nodes = visitedNodes.get(key);
            if (nodes == null) {
                nodes = new ArrayList<>();
                visitedNodes.put(key, nodes);
            }
            nodes.add(node);
        }

        @Override
        public void apply(List<? extends net.sourceforge.pmd.lang.ast.Node> nodes, RuleContext ctx) {
            super.apply(nodes, ctx);
        }

        @Override
        protected void visit(XmlNode node, Attr attr, RuleContext ctx) {
            visit("Attr", attr);
            super.visit(node, attr, ctx);
        }

        @Override
        protected void visit(XmlNode node, CharacterData characterData, RuleContext ctx) {
            visit("CharacterData", characterData);
            super.visit(node, characterData, ctx);
        }

        @Override
        protected void visit(XmlNode node, Comment comment, RuleContext ctx) {
            visit("Comment", comment);
            super.visit(node, comment, ctx);
        }

        @Override
        protected void visit(XmlNode node, Document document, RuleContext ctx) {
            visit("Document", document);
            super.visit(node, document, ctx);
        }

        @Override
        protected void visit(XmlNode node, DocumentType documentType, RuleContext ctx) {
            visit("DocumentType", documentType);
            super.visit(node, documentType, ctx);
        }

        @Override
        protected void visit(XmlNode node, Element element, RuleContext ctx) {
            visit("Element", element);
            super.visit(node, element, ctx);
        }

        @Override
        protected void visit(XmlNode node, Entity entity, RuleContext ctx) {
            visit("Entity", entity);
            super.visit(node, entity, ctx);
        }

        @Override
        protected void visit(XmlNode node, EntityReference entityReference, RuleContext ctx) {
            visit("EntityReference", entityReference);
            super.visit(node, entityReference, ctx);
        }

        @Override
        protected void visit(XmlNode node, Notation notation, RuleContext ctx) {
            visit("Notation", notation);
            super.visit(node, notation, ctx);
        }

        @Override
        protected void visit(XmlNode node, ProcessingInstruction processingInstruction, RuleContext ctx) {
            visit("ProcessingInstruction", processingInstruction);
            super.visit(node, processingInstruction, ctx);
        }

        @Override
        protected void visit(XmlNode node, Text text, RuleContext ctx) {
            visit("Text", text);
            super.visit(node, text, ctx);
        }
    }
}

