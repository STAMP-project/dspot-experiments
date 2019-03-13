/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;


import XPathRule.XPATH_DESCRIPTOR;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportTest;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.rule.XPathRule;
import org.junit.Assert;
import org.junit.Test;


public class CodeClimateRendererTest extends AbstractRendererTst {
    @Test
    public void testXPathRule() throws Exception {
        DummyNode node = AbstractRendererTst.createNode(1);
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename(getSourceCodeFilename());
        Report report = new Report();
        XPathRule theRule = new XPathRule();
        theRule.setProperty(XPATH_DESCRIPTOR, "//dummyNode");
        // Setup as FooRule
        theRule.setDescription("desc");
        theRule.setName("Foo");
        report.addRuleViolation(new net.sourceforge.pmd.lang.rule.ParametricRuleViolation<net.sourceforge.pmd.lang.ast.Node>(theRule, ctx, node, "blah"));
        String rendered = ReportTest.render(getRenderer(), report);
        // Output should be the exact same as for non xpath rules
        Assert.assertEquals(filter(getExpected()), filter(rendered));
    }
}

