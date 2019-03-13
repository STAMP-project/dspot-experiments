/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;


import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.stat.Metric;
import org.junit.Assert;
import org.junit.Test;


public class ReportTest implements ThreadSafeReportListener {
    private boolean violationSemaphore;

    private boolean metricSemaphore;

    @Test
    public void testMetric0() {
        Report r = new Report();
        Assert.assertFalse("Default report shouldn't contain metrics", r.hasMetrics());
    }

    @Test
    public void testMetric1() {
        Report r = new Report();
        Assert.assertFalse("Default report shouldn't contain metrics", r.hasMetrics());
        r.addMetric(new Metric("m1", 0, 0.0, 1.0, 2.0, 3.0, 4.0));
        Assert.assertTrue("Expected metrics weren't there", r.hasMetrics());
        Iterator<Metric> ms = r.metrics();
        Assert.assertTrue("Should have some metrics in there now", ms.hasNext());
        Object o = ms.next();
        Assert.assertTrue(("Expected Metric, got " + (o.getClass())), (o instanceof Metric));
        Metric m = ((Metric) (o));
        Assert.assertEquals("metric name mismatch", "m1", m.getMetricName());
        Assert.assertEquals("wrong low value", 1.0, m.getLowValue(), 0.05);
        Assert.assertEquals("wrong high value", 2.0, m.getHighValue(), 0.05);
        Assert.assertEquals("wrong avg value", 3.0, m.getAverage(), 0.05);
        Assert.assertEquals("wrong std dev value", 4.0, m.getStandardDeviation(), 0.05);
    }

    // Files are grouped together now.
    @Test
    public void testSortedReportFile() throws IOException {
        Report r = new Report();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("foo");
        Node s = ReportTest.getNode(10, 5);
        Rule rule1 = new MockRule("name", "desc", "msg", "rulesetname");
        r.addRuleViolation(new net.sourceforge.pmd.lang.rule.ParametricRuleViolation(rule1, ctx, s, rule1.getMessage()));
        ctx.setSourceCodeFilename("bar");
        Node s1 = ReportTest.getNode(10, 5);
        Rule rule2 = new MockRule("name", "desc", "msg", "rulesetname");
        r.addRuleViolation(new net.sourceforge.pmd.lang.rule.ParametricRuleViolation(rule2, ctx, s1, rule2.getMessage()));
        Renderer rend = new XMLRenderer();
        String result = ReportTest.render(rend, r);
        Assert.assertTrue("sort order wrong", ((result.indexOf("bar")) < (result.indexOf("foo"))));
    }

    @Test
    public void testSortedReportLine() throws IOException {
        Report r = new Report();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("foo1");// same file!!

        Node node1 = ReportTest.getNode(20, 5);// line 20: after rule2 violation

        Rule rule1 = new MockRule("rule1", "rule1", "msg", "rulesetname");
        r.addRuleViolation(new net.sourceforge.pmd.lang.rule.ParametricRuleViolation(rule1, ctx, node1, rule1.getMessage()));
        ctx.setSourceCodeFilename("foo1");// same file!!

        Node node2 = ReportTest.getNode(10, 5);// line 10: before rule1 violation

        Rule rule2 = new MockRule("rule2", "rule2", "msg", "rulesetname");
        r.addRuleViolation(new net.sourceforge.pmd.lang.rule.ParametricRuleViolation(rule2, ctx, node2, rule2.getMessage()));
        Renderer rend = new XMLRenderer();
        String result = ReportTest.render(rend, r);
        Assert.assertTrue("sort order wrong", ((result.indexOf("rule2")) < (result.indexOf("rule1"))));
    }

    @Test
    public void testListener() {
        Report rpt = new Report();
        rpt.addListener(this);
        violationSemaphore = false;
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("file");
        Node s = ReportTest.getNode(5, 5);
        Rule rule1 = new MockRule("name", "desc", "msg", "rulesetname");
        rpt.addRuleViolation(new net.sourceforge.pmd.lang.rule.ParametricRuleViolation(rule1, ctx, s, rule1.getMessage()));
        Assert.assertTrue(violationSemaphore);
        metricSemaphore = false;
        rpt.addMetric(new Metric("test", 0, 0.0, 0.0, 0.0, 0.0, 0.0));
        Assert.assertTrue("no metric", metricSemaphore);
    }

    @Test
    public void testSummary() {
        Report r = new Report();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("foo1");
        Node s = ReportTest.getNode(5, 5);
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        r.addRuleViolation(new net.sourceforge.pmd.lang.rule.ParametricRuleViolation(rule, ctx, s, rule.getMessage()));
        ctx.setSourceCodeFilename("foo2");
        Rule mr = new MockRule("rule1", "rule1", "msg", "rulesetname");
        Node s1 = ReportTest.getNode(20, 5);
        Node s2 = ReportTest.getNode(30, 5);
        r.addRuleViolation(new net.sourceforge.pmd.lang.rule.ParametricRuleViolation(mr, ctx, s1, mr.getMessage()));
        r.addRuleViolation(new net.sourceforge.pmd.lang.rule.ParametricRuleViolation(mr, ctx, s2, mr.getMessage()));
        Map<String, Integer> summary = r.getSummary();
        Assert.assertEquals(summary.keySet().size(), 2);
        Assert.assertTrue(summary.values().contains(Integer.valueOf(1)));
        Assert.assertTrue(summary.values().contains(Integer.valueOf(2)));
    }

    @Test
    public void testTreeIterator() {
        Report r = new Report();
        RuleContext ctx = new RuleContext();
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        Node node1 = ReportTest.getNode(5, 5, true);
        r.addRuleViolation(new net.sourceforge.pmd.lang.rule.ParametricRuleViolation(rule, ctx, node1, rule.getMessage()));
        Node node2 = ReportTest.getNode(5, 6, true);
        r.addRuleViolation(new net.sourceforge.pmd.lang.rule.ParametricRuleViolation(rule, ctx, node2, rule.getMessage()));
        Iterator<RuleViolation> violations = r.iterator();
        int violationCount = 0;
        while (violations.hasNext()) {
            violations.next();
            violationCount++;
        } 
        Assert.assertEquals(2, violationCount);
        Iterator<RuleViolation> treeIterator = r.treeIterator();
        int treeCount = 0;
        while (treeIterator.hasNext()) {
            treeIterator.next();
            treeCount++;
        } 
        Assert.assertEquals(2, treeCount);
    }
}

