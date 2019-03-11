/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;


import RulePriority.HIGH;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.properties.StringProperty;
import net.sourceforge.pmd.properties.constraints.NumericConstraints;
import org.junit.Assert;
import org.junit.Test;


public class AbstractRuleTest {
    public static class MyRule extends AbstractRule {
        private static final StringProperty FOO_PROPERTY = new StringProperty("foo", "foo property", "x", 1.0F);

        private static final StringProperty XPATH_PROPERTY = new StringProperty("xpath", "xpath property", "", 2.0F);

        public MyRule() {
            definePropertyDescriptor(AbstractRuleTest.MyRule.FOO_PROPERTY);
            definePropertyDescriptor(AbstractRuleTest.MyRule.XPATH_PROPERTY);
            setName("MyRule");
            setMessage("my rule msg");
            setPriority(RulePriority.MEDIUM);
            setProperty(AbstractRuleTest.MyRule.FOO_PROPERTY, "value");
        }

        @Override
        public void apply(List<? extends Node> nodes, RuleContext ctx) {
        }
    }

    private static class MyOtherRule extends AbstractRule {
        private static final PropertyDescriptor FOO_PROPERTY = new StringProperty("foo", "foo property", "x", 1.0F);

        MyOtherRule() {
            definePropertyDescriptor(AbstractRuleTest.MyOtherRule.FOO_PROPERTY);
            setName("MyOtherRule");
            setMessage("my other rule");
            setPriority(RulePriority.MEDIUM);
            setProperty(AbstractRuleTest.MyOtherRule.FOO_PROPERTY, "value");
        }

        @Override
        public void apply(List<? extends Node> nodes, RuleContext ctx) {
        }
    }

    @Test
    public void testCreateRV() {
        AbstractRuleTest.MyRule r = new AbstractRuleTest.MyRule();
        setRuleSetName("foo");
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        DummyNode s = new DummyNode(1);
        testingOnlySetBeginColumn(5);
        testingOnlySetBeginLine(5);
        RuleViolation rv = new net.sourceforge.pmd.lang.rule.ParametricRuleViolation(r, ctx, s, getMessage());
        Assert.assertEquals("Line number mismatch!", 5, rv.getBeginLine());
        Assert.assertEquals("Filename mismatch!", "filename", rv.getFilename());
        Assert.assertEquals("Rule object mismatch!", r, rv.getRule());
        Assert.assertEquals("Rule msg mismatch!", "my rule msg", rv.getDescription());
        Assert.assertEquals("RuleSet name mismatch!", "foo", getRuleSetName());
    }

    @Test
    public void testCreateRV2() {
        AbstractRuleTest.MyRule r = new AbstractRuleTest.MyRule();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("filename");
        DummyNode s = new DummyNode(1);
        testingOnlySetBeginColumn(5);
        testingOnlySetBeginLine(5);
        RuleViolation rv = new net.sourceforge.pmd.lang.rule.ParametricRuleViolation(r, ctx, s, "specificdescription");
        Assert.assertEquals("Line number mismatch!", 5, rv.getBeginLine());
        Assert.assertEquals("Filename mismatch!", "filename", rv.getFilename());
        Assert.assertEquals("Rule object mismatch!", r, rv.getRule());
        Assert.assertEquals("Rule description mismatch!", "specificdescription", rv.getDescription());
    }

    @Test
    public void testRuleWithVariableInMessage() {
        AbstractRuleTest.MyRule r = new AbstractRuleTest.MyRule();
        r.definePropertyDescriptor(PropertyFactory.intProperty("testInt").desc("description").require(NumericConstraints.inRange(0, 100)).defaultValue(10).build());
        setMessage("Message ${packageName} ${className} ${methodName} ${variableName} ${testInt} ${noSuchProperty}");
        RuleContext ctx = new RuleContext();
        ctx.setLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion());
        ctx.setReport(new Report());
        ctx.setSourceCodeFilename("filename");
        DummyNode s = new DummyNode(1);
        testingOnlySetBeginColumn(5);
        testingOnlySetBeginLine(5);
        setImage("TestImage");
        r.addViolation(ctx, s);
        RuleViolation rv = ctx.getReport().getViolationTree().iterator().next();
        Assert.assertEquals("Message foo    10 ${noSuchProperty}", rv.getDescription());
    }

    @Test
    public void testRuleSuppress() {
        AbstractRuleTest.MyRule r = new AbstractRuleTest.MyRule();
        RuleContext ctx = new RuleContext();
        Map<Integer, String> m = new HashMap<>();
        m.put(Integer.valueOf(5), "");
        ctx.setReport(new Report());
        ctx.getReport().suppress(m);
        ctx.setSourceCodeFilename("filename");
        DummyNode n = new DummyNode(1);
        testingOnlySetBeginColumn(5);
        testingOnlySetBeginLine(5);
        RuleViolation rv = new net.sourceforge.pmd.lang.rule.ParametricRuleViolation(r, ctx, n, "specificdescription");
        ctx.getReport().addRuleViolation(rv);
        Assert.assertTrue(ctx.getReport().isEmpty());
    }

    @Test
    public void testEquals1() {
        AbstractRuleTest.MyRule r = new AbstractRuleTest.MyRule();
        Assert.assertFalse("A rule is never equals to null!", r.equals(null));
    }

    @Test
    public void testEquals2() {
        AbstractRuleTest.MyRule r = new AbstractRuleTest.MyRule();
        Assert.assertEquals("A rule must be equals to itself", r, r);
    }

    @Test
    public void testEquals3() {
        AbstractRuleTest.MyRule r1 = new AbstractRuleTest.MyRule();
        AbstractRuleTest.MyRule r2 = new AbstractRuleTest.MyRule();
        Assert.assertEquals("Two instances of the same rule are equal", r1, r2);
        Assert.assertEquals("Hashcode for two instances of the same rule must be equal", r1.hashCode(), r2.hashCode());
    }

    @Test
    public void testEquals4() {
        AbstractRuleTest.MyRule myRule = new AbstractRuleTest.MyRule();
        Assert.assertFalse("A rule cannot be equal to an object of another class", myRule.equals("MyRule"));
    }

    @Test
    public void testEquals5() {
        AbstractRuleTest.MyRule myRule = new AbstractRuleTest.MyRule();
        AbstractRuleTest.MyOtherRule myOtherRule = new AbstractRuleTest.MyOtherRule();
        Assert.assertFalse("Two rules from different classes cannot be equal", myRule.equals(myOtherRule));
    }

    @Test
    public void testEquals6() {
        AbstractRuleTest.MyRule r1 = new AbstractRuleTest.MyRule();
        AbstractRuleTest.MyRule r2 = new AbstractRuleTest.MyRule();
        setName("MyRule2");
        Assert.assertFalse("Rules with different names cannot be equal", r1.equals(r2));
    }

    @Test
    public void testEquals7() {
        AbstractRuleTest.MyRule r1 = new AbstractRuleTest.MyRule();
        AbstractRuleTest.MyRule r2 = new AbstractRuleTest.MyRule();
        r2.setPriority(HIGH);
        Assert.assertFalse("Rules with different priority levels cannot be equal", r1.equals(r2));
    }

    @Test
    public void testEquals8() {
        AbstractRuleTest.MyRule r1 = new AbstractRuleTest.MyRule();
        r1.setProperty(AbstractRuleTest.MyRule.XPATH_PROPERTY, "something");
        AbstractRuleTest.MyRule r2 = new AbstractRuleTest.MyRule();
        r2.setProperty(AbstractRuleTest.MyRule.XPATH_PROPERTY, "something else");
        Assert.assertFalse("Rules with different properties values cannot be equal", r1.equals(r2));
    }

    @Test
    public void testEquals9() {
        AbstractRuleTest.MyRule r1 = new AbstractRuleTest.MyRule();
        AbstractRuleTest.MyRule r2 = new AbstractRuleTest.MyRule();
        r2.setProperty(AbstractRuleTest.MyRule.XPATH_PROPERTY, "something else");
        Assert.assertFalse("Rules with different properties cannot be equal", r1.equals(r2));
    }

    @Test
    public void testEquals10() {
        AbstractRuleTest.MyRule r1 = new AbstractRuleTest.MyRule();
        AbstractRuleTest.MyRule r2 = new AbstractRuleTest.MyRule();
        setMessage("another message");
        Assert.assertEquals("Rules with different messages are still equal", r1, r2);
        Assert.assertEquals("Rules that are equal must have the an equal hashcode", r1.hashCode(), r2.hashCode());
    }

    @Test
    public void testDeepCopyRule() {
        AbstractRuleTest.MyRule r1 = new AbstractRuleTest.MyRule();
        AbstractRuleTest.MyRule r2 = ((AbstractRuleTest.MyRule) (deepCopy()));
        Assert.assertEquals(getDescription(), getDescription());
        Assert.assertEquals(getExamples(), getExamples());
        Assert.assertEquals(getExternalInfoUrl(), getExternalInfoUrl());
        Assert.assertEquals(getLanguage(), getLanguage());
        Assert.assertEquals(getMaximumLanguageVersion(), getMaximumLanguageVersion());
        Assert.assertEquals(getMessage(), getMessage());
        Assert.assertEquals(getMinimumLanguageVersion(), getMinimumLanguageVersion());
        Assert.assertEquals(getName(), getName());
        Assert.assertEquals(getPriority(), getPriority());
        Assert.assertEquals(getPropertyDescriptors(), getPropertyDescriptors());
        Assert.assertEquals(getRuleChainVisits(), getRuleChainVisits());
        Assert.assertEquals(getRuleClass(), getRuleClass());
        Assert.assertEquals(getRuleSetName(), getRuleSetName());
        Assert.assertEquals(getSince(), getSince());
    }
}

