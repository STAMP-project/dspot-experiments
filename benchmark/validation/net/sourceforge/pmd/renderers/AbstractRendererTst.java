/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;


import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.ReportTest;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleWithProperties;
import net.sourceforge.pmd.lang.ast.DummyNode;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractRendererTst {
    @Test(expected = NullPointerException.class)
    public void testNullPassedIn() throws Exception {
        getRenderer().renderFileReport(null);
    }

    @Test
    public void testRuleWithProperties() throws Exception {
        DummyNode node = AbstractRendererTst.createNode(1);
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename(getSourceCodeFilename());
        Report report = new Report();
        RuleWithProperties theRule = new RuleWithProperties();
        theRule.setProperty(RuleWithProperties.STRING_PROPERTY_DESCRIPTOR, "the string value\nsecond line with \"quotes\"");
        report.addRuleViolation(new net.sourceforge.pmd.lang.rule.ParametricRuleViolation<net.sourceforge.pmd.lang.ast.Node>(theRule, ctx, node, "blah"));
        String rendered = ReportTest.render(getRenderer(), report);
        Assert.assertEquals(filter(getExpectedWithProperties()), filter(rendered));
    }

    @Test
    public void testRenderer() throws Exception {
        Report rep = reportOneViolation();
        String actual = ReportTest.render(getRenderer(), rep);
        Assert.assertEquals(filter(getExpected()), filter(actual));
    }

    @Test
    public void testRendererEmpty() throws Exception {
        Report rep = new Report();
        String actual = ReportTest.render(getRenderer(), rep);
        Assert.assertEquals(filter(getExpectedEmpty()), filter(actual));
    }

    @Test
    public void testRendererMultiple() throws Exception {
        Report rep = reportTwoViolations();
        String actual = ReportTest.render(getRenderer(), rep);
        Assert.assertEquals(filter(getExpectedMultiple()), filter(actual));
    }

    @Test
    public void testError() throws Exception {
        Report rep = new Report();
        Report.ProcessingError err = new Report.ProcessingError(new RuntimeException("Error"), "file");
        rep.addError(err);
        String actual = ReportTest.render(getRenderer(), rep);
        Assert.assertEquals(filter(getExpectedError(err)), filter(actual));
    }

    @Test
    public void testErrorWithoutMessage() throws Exception {
        Report rep = new Report();
        Report.ProcessingError err = new Report.ProcessingError(new NullPointerException(), "file");
        rep.addError(err);
        String actual = ReportTest.render(getRenderer(), rep);
        Assert.assertEquals(filter(getExpectedErrorWithoutMessage(err)), filter(actual));
    }

    @Test
    public void testConfigError() throws Exception {
        Report rep = new Report();
        Report.ConfigurationError err = new Report.ConfigurationError(new FooRule(), "a configuration error");
        rep.addConfigError(err);
        String actual = ReportTest.render(getRenderer(), rep);
        Assert.assertEquals(filter(getExpectedError(err)), filter(actual));
    }
}

