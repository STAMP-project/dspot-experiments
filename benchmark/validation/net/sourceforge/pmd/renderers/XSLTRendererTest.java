/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;


import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportTest;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.DummyNode;
import org.junit.Assert;
import org.junit.Test;


public class XSLTRendererTest {
    @Test
    public void testDefaultStylesheet() throws Exception {
        XSLTRenderer renderer = new XSLTRenderer();
        Report report = new Report();
        DummyNode node = new DummyNode(1);
        testingOnlySetBeginLine(1);
        testingOnlySetBeginColumn(1);
        RuleViolation rv = new net.sourceforge.pmd.lang.rule.ParametricRuleViolation<net.sourceforge.pmd.lang.ast.Node>(new FooRule(), new RuleContext(), node, "violation message");
        report.addRuleViolation(rv);
        String result = ReportTest.render(renderer, report);
        Assert.assertTrue(result.contains("violation message"));
    }
}

