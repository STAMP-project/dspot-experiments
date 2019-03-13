/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.wsdl.rule;


import WsdlLanguageModule.NAME;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.xml.XmlParserOptions;
import net.sourceforge.pmd.lang.xml.ast.XmlNode;
import org.junit.Assert;
import org.junit.Test;


public class AbstractWsdlRuleTest {
    @Test
    public void testVisit() throws Exception {
        String source = "<?xml version=\"1.0\"?><foo abc=\"abc\"><bar/></foo>";
        XmlParserOptions parserOptions = new XmlParserOptions();
        Parser parser = LanguageRegistry.getLanguage(NAME).getDefaultVersion().getLanguageVersionHandler().getParser(parserOptions);
        XmlNode xmlNode = ((XmlNode) (parser.parse(null, new StringReader(source))));
        List<XmlNode> nodes = new ArrayList<>();
        nodes.add(xmlNode);
        AbstractWsdlRuleTest.MyRule rule = new AbstractWsdlRuleTest.MyRule();
        rule.apply(nodes, null);
        Assert.assertEquals(3, rule.visitedNodes.size());
        Assert.assertEquals("document", rule.visitedNodes.get(0).toString());
        Assert.assertEquals("foo", rule.visitedNodes.get(1).toString());
        Assert.assertEquals("bar", rule.visitedNodes.get(2).toString());
    }

    private static class MyRule extends AbstractWsdlRule {
        final List<XmlNode> visitedNodes = new ArrayList<>();

        MyRule() {
        }

        @Override
        public void apply(List<? extends Node> nodes, RuleContext ctx) {
            visitedNodes.clear();
            super.apply(nodes, ctx);
        }

        @Override
        protected void visit(XmlNode node, RuleContext ctx) {
            visitedNodes.add(node);
            super.visit(node, ctx);
        }
    }
}

