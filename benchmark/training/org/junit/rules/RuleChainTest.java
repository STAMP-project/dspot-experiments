package org.junit.rules;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;


public class RuleChainTest {
    private static final List<String> LOG = new ArrayList<String>();

    private static class LoggingRule extends TestWatcher {
        private final String label;

        public LoggingRule(String label) {
            this.label = label;
        }

        @Override
        protected void starting(Description description) {
            RuleChainTest.LOG.add(("starting " + (label)));
        }

        @Override
        protected void finished(Description description) {
            RuleChainTest.LOG.add(("finished " + (label)));
        }
    }

    public static class UseRuleChain {
        @Rule
        public final RuleChain chain = RuleChain.outerRule(new RuleChainTest.LoggingRule("outer rule")).around(new RuleChainTest.LoggingRule("middle rule")).around(new RuleChainTest.LoggingRule("inner rule"));

        @Test
        public void example() {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void executeRulesInCorrectOrder() throws Exception {
        PrintableResult.testResult(RuleChainTest.UseRuleChain.class);
        List<String> expectedLog = Arrays.asList("starting outer rule", "starting middle rule", "starting inner rule", "finished inner rule", "finished middle rule", "finished outer rule");
        Assert.assertEquals(expectedLog, RuleChainTest.LOG);
    }

    @Test
    public void aroundShouldNotAllowNullRules() {
        RuleChain chain = RuleChain.emptyRuleChain();
        try {
            chain.around(null);
            Assert.fail("around() should not allow null rules");
        } catch (NullPointerException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.equalTo("The enclosed rule must not be null"));
        }
    }

    public static class RuleChainWithNullRules {
        @Rule
        public final RuleChain chain = RuleChain.outerRule(new RuleChainTest.LoggingRule("outer rule")).around(null);

        @Test
        public void example() {
        }
    }

    @Test
    public void whenRuleChainHasNullRuleTheStacktraceShouldPointToIt() {
        Result result = JUnitCore.runClasses(RuleChainTest.RuleChainWithNullRules.class);
        MatcherAssert.assertThat(result.getFailures().size(), CoreMatchers.equalTo(1));
        String stacktrace = getStacktrace(result.getFailures().get(0).getException());
        MatcherAssert.assertThat(stacktrace, CoreMatchers.containsString("\tat org.junit.rules.RuleChainTest$RuleChainWithNullRules.<init>(RuleChainTest.java:"));
    }
}

