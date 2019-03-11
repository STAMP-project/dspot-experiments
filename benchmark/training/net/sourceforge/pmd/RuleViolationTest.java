/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;


import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.rule.MockRule;
import org.junit.Assert;
import org.junit.Test;

import static RuleViolationComparator.INSTANCE;


public class RuleViolationTest {
    @Test
    public void testConstructor1() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        DummyNode s = new DummyNode(1);
        testingOnlySetBeginLine(2);
        testingOnlySetBeginColumn(1);
        RuleViolation r = new net.sourceforge.pmd.lang.rule.ParametricRuleViolation<net.sourceforge.pmd.lang.ast.Node>(rule, ctx, s, rule.getMessage());
        Assert.assertEquals("object mismatch", rule, r.getRule());
        Assert.assertEquals("line number is wrong", 2, r.getBeginLine());
        Assert.assertEquals("filename is wrong", "filename", r.getFilename());
    }

    @Test
    public void testConstructor2() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        DummyNode s = new DummyNode(1);
        testingOnlySetBeginLine(2);
        testingOnlySetBeginColumn(1);
        RuleViolation r = new net.sourceforge.pmd.lang.rule.ParametricRuleViolation<net.sourceforge.pmd.lang.ast.Node>(rule, ctx, s, "description");
        Assert.assertEquals("object mismatch", rule, r.getRule());
        Assert.assertEquals("line number is wrong", 2, r.getBeginLine());
        Assert.assertEquals("filename is wrong", "filename", r.getFilename());
        Assert.assertEquals("description is wrong", "description", r.getDescription());
    }

    @Test
    public void testComparatorWithDifferentFilenames() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleViolationComparator comp = INSTANCE;
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename1");
        DummyNode s = new DummyNode(1);
        testingOnlySetBeginLine(10);
        testingOnlySetBeginColumn(1);
        RuleViolation r1 = new net.sourceforge.pmd.lang.rule.ParametricRuleViolation<net.sourceforge.pmd.lang.ast.Node>(rule, ctx, s, "description");
        ctx.setSourceCodeFilename("filename2");
        DummyNode s1 = new DummyNode(1);
        testingOnlySetBeginLine(10);
        testingOnlySetBeginColumn(1);
        RuleViolation r2 = new net.sourceforge.pmd.lang.rule.ParametricRuleViolation<net.sourceforge.pmd.lang.ast.Node>(rule, ctx, s1, "description");
        Assert.assertEquals((-1), comp.compare(r1, r2));
        Assert.assertEquals(1, comp.compare(r2, r1));
    }

    @Test
    public void testComparatorWithSameFileDifferentLines() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleViolationComparator comp = INSTANCE;
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        DummyNode s = new DummyNode(1);
        testingOnlySetBeginLine(10);
        testingOnlySetBeginColumn(1);
        DummyNode s1 = new DummyNode(1);
        testingOnlySetBeginLine(20);
        testingOnlySetBeginColumn(1);
        RuleViolation r1 = new net.sourceforge.pmd.lang.rule.ParametricRuleViolation<net.sourceforge.pmd.lang.ast.Node>(rule, ctx, s, "description");
        RuleViolation r2 = new net.sourceforge.pmd.lang.rule.ParametricRuleViolation<net.sourceforge.pmd.lang.ast.Node>(rule, ctx, s1, "description");
        Assert.assertTrue(((comp.compare(r1, r2)) < 0));
        Assert.assertTrue(((comp.compare(r2, r1)) > 0));
    }
}

