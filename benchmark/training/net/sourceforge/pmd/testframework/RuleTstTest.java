/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.testframework;


import java.util.Arrays;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.test.lang.ast.DummyNode;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class RuleTstTest {
    private LanguageVersion dummyLanguage = LanguageRegistry.findLanguageByTerseName("dummy").getDefaultVersion();

    private Rule rule = Mockito.mock(Rule.class);

    private RuleTst ruleTester = new RuleTst() {};

    @Test
    public void shouldCallStartAndEnd() {
        Report report = new Report();
        Mockito.when(rule.getLanguage()).thenReturn(dummyLanguage.getLanguage());
        Mockito.when(rule.getName()).thenReturn("test rule");
        ruleTester.runTestFromString("the code", rule, report, dummyLanguage, false);
        Mockito.verify(rule).start(ArgumentMatchers.any(RuleContext.class));
        Mockito.verify(rule).end(ArgumentMatchers.any(RuleContext.class));
        Mockito.verify(rule, Mockito.times(5)).getLanguage();
        Mockito.verify(rule).isDfa();
        Mockito.verify(rule).isTypeResolution();
        Mockito.verify(rule).isMultifile();
        Mockito.verify(rule, Mockito.times(2)).isRuleChain();
        Mockito.verify(rule).getMinimumLanguageVersion();
        Mockito.verify(rule).getMaximumLanguageVersion();
        Mockito.verify(rule).apply(ArgumentMatchers.anyList(), ArgumentMatchers.any(RuleContext.class));
        Mockito.verify(rule, Mockito.times(4)).getName();
        Mockito.verify(rule).getPropertiesByPropertyDescriptor();
        Mockito.verifyNoMoreInteractions(rule);
    }

    @Test
    public void shouldAssertLinenumbersSorted() {
        Mockito.when(rule.getLanguage()).thenReturn(dummyLanguage.getLanguage());
        Mockito.when(rule.getName()).thenReturn("test rule");
        Mockito.doAnswer(new Answer<Void>() {
            private RuleViolation createViolation(RuleContext context, int beginLine, String message) {
                DummyNode node = new DummyNode(1);
                node.testingOnlySetBeginLine(beginLine);
                node.testingOnlySetBeginColumn(1);
                ParametricRuleViolation<Node> violation = new ParametricRuleViolation<Node>(rule, context, node, message);
                return violation;
            }

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                RuleContext context = getArgumentAt(1, RuleContext.class);
                // the violations are reported out of order
                context.getReport().addRuleViolation(createViolation(context, 15, "first reported violation"));
                context.getReport().addRuleViolation(createViolation(context, 5, "second reported violation"));
                return null;
            }
        }).when(rule).apply(Mockito.anyList(), Mockito.any(RuleContext.class));
        TestDescriptor testDescriptor = new TestDescriptor("the code", "sample test", 2, rule, dummyLanguage);
        testDescriptor.setReinitializeRule(false);
        testDescriptor.setExpectedLineNumbers(Arrays.asList(5, 15));
        try {
            ruleTester.runTest(testDescriptor);
            // there should be no assertion failures
            // expected line numbers and actual line numbers match
        } catch (AssertionError assertionError) {
            Assert.fail(assertionError.toString());
        }
    }
}

