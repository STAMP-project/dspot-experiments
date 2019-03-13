/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.metrics.xpath;


import java.util.Iterator;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.java.xpath.MetricFunction;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 *
 *
 * @author Cl?ment Fournier
 * @since 6.0.0
 */
public class XPathMetricFunctionTest {
    private static final String VIOLATION_MESSAGE = "violation";

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void testWellFormedClassMetricRule() throws PMDException {
        net.sourceforge.pmd.Rule rule = makeXpathRuleFromXPath("//ClassOrInterfaceDeclaration[metric('NCSS') > 0]");
        final String code = "class Foo { Foo() {} void bar() {}}";
        Iterator<RuleViolation> violations = getViolations(rule, code);
        Assert.assertTrue(violations.hasNext());
    }

    @Test
    public void testWellFormedOperationMetricRule() throws PMDException {
        net.sourceforge.pmd.Rule rule = makeXpathRuleFromXPath("//ConstructorDeclaration[metric('CYCLO') > 1]");
        final String code = "class Goo { Goo() {if(true){}} }";
        Iterator<RuleViolation> violations = getViolations(rule, code);
        Assert.assertTrue(violations.hasNext());
    }

    @Test
    public void testBadCase() throws PMDException {
        net.sourceforge.pmd.Rule rule = makeXpathRuleFromXPath("//ConstructorDeclaration[metric('cYclo') > 1]");
        final String code = "class Hoo { Hoo() {if(true){}} }";
        Iterator<RuleViolation> violations = getViolations(rule, code);
        Assert.assertTrue(violations.hasNext());
    }

    @Test
    public void testNonexistentMetric() throws Exception {
        testWithExpectedException("//ConstructorDeclaration[metric('FOOBAR') > 1]", "class Joo { Joo() {if(true){}} }", IllegalArgumentException.class, MetricFunction.badOperationMetricKeyMessage());
    }

    @Test
    public void testWrongNodeTypeGeneric() throws Exception {
        testWithExpectedException("//IfStatement[metric('NCSS') > 1]", "class Koo { Koo() {if(true){}} }", IllegalStateException.class, MetricFunction.genericBadNodeMessage());
    }

    @Test
    public void testWrongMetricKeyForTypeDeclaration() throws Exception {
        testWithExpectedException("//EnumDeclaration[metric('CYCLO') > 1]", "enum Loo { FOO; }", IllegalArgumentException.class, MetricFunction.badClassMetricKeyMessage());
    }

    @Test
    public void testWrongMetricKeyForOperationDeclaration() throws Exception {
        testWithExpectedException("//MethodDeclaration[metric('WMC') > 1]", "class Moo { void foo() {if(true){}} }", IllegalArgumentException.class, MetricFunction.badOperationMetricKeyMessage());
    }
}

