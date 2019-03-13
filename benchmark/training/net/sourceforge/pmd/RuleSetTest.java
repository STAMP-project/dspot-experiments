/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.RuleSet.RuleSetBuilder;
import net.sourceforge.pmd.lang.Dummy2LanguageModule;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.util.CollectionUtil;
import org.junit.Assert;
import org.junit.Test;


public class RuleSetTest {
    @Test(expected = NullPointerException.class)
    public void testRuleSetRequiresName() {
        new RuleSetBuilder(new Random().nextLong()).withName(null);
    }

    @Test(expected = NullPointerException.class)
    public void testRuleSetRequiresDescription() {
        new RuleSetBuilder(new Random().nextLong()).withName("some name").withDescription(null);
    }

    @Test(expected = NullPointerException.class)
    public void testRuleSetRequiresName2() {
        new RuleSetBuilder(new Random().nextLong()).build();
    }

    @Test
    public void testNoDFA() {
        MockRule mock = new MockRule("name", "desc", "msg", "rulesetname");
        mock.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        RuleSet rs = new RuleSetFactory().createSingleRuleRuleSet(mock);
        Assert.assertFalse(rs.usesDFA(LanguageRegistry.getLanguage(DummyLanguageModule.NAME)));
    }

    @Test
    public void testIncludesRuleWithDFA() {
        MockRule mock = new MockRule("name", "desc", "msg", "rulesetname");
        mock.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        mock.setDfa(true);
        RuleSet rs = new RuleSetFactory().createSingleRuleRuleSet(mock);
        Assert.assertTrue(rs.usesDFA(LanguageRegistry.getLanguage(DummyLanguageModule.NAME)));
    }

    @Test
    public void testAccessors() {
        RuleSet rs = new RuleSetBuilder(new Random().nextLong()).withFileName("baz").withName("foo").withDescription("bar").build();
        Assert.assertEquals("file name mismatch", "baz", rs.getFileName());
        Assert.assertEquals("name mismatch", "foo", rs.getName());
        Assert.assertEquals("description mismatch", "bar", rs.getDescription());
    }

    @Test
    public void testGetRuleByName() {
        MockRule mock = new MockRule("name", "desc", "msg", "rulesetname");
        RuleSet rs = new RuleSetFactory().createSingleRuleRuleSet(mock);
        Assert.assertEquals("unable to fetch rule by name", mock, rs.getRuleByName("name"));
    }

    @Test
    public void testGetRuleByName2() {
        MockRule mock = new MockRule("name", "desc", "msg", "rulesetname");
        RuleSet rs = new RuleSetFactory().createSingleRuleRuleSet(mock);
        Assert.assertNull("the rule FooRule must not be found!", rs.getRuleByName("FooRule"));
    }

    @Test
    public void testRuleList() {
        MockRule rule = new MockRule("name", "desc", "msg", "rulesetname");
        RuleSet ruleset = new RuleSetFactory().createSingleRuleRuleSet(rule);
        Assert.assertEquals("Size of RuleSet isn't one.", 1, ruleset.size());
        Collection<Rule> rules = ruleset.getRules();
        Iterator<Rule> i = rules.iterator();
        Assert.assertTrue("Empty Set", i.hasNext());
        Assert.assertEquals("Returned set of wrong size.", 1, rules.size());
        Assert.assertEquals("Rule isn't in ruleset.", rule, i.next());
    }

    @Test
    public void testAddRuleSet() {
        RuleSet set1 = createRuleSetBuilder("ruleset1").addRule(new MockRule("name", "desc", "msg", "rulesetname")).build();
        RuleSet set2 = createRuleSetBuilder("ruleset2").addRule(new MockRule("name2", "desc", "msg", "rulesetname")).addRuleSet(set1).build();
        Assert.assertEquals("ruleset size wrong", 2, set2.size());
    }

    @Test(expected = RuntimeException.class)
    public void testAddRuleSetByReferenceBad() {
        RuleSet set1 = createRuleSetBuilder("ruleset1").addRule(new MockRule("name", "desc", "msg", "rulesetname")).build();
        createRuleSetBuilder("ruleset2").addRule(new MockRule("name2", "desc", "msg", "rulesetname")).addRuleSetByReference(set1, false).build();
    }

    @Test
    public void testAddRuleSetByReferenceAllRule() {
        RuleSet set2 = createRuleSetBuilder("ruleset2").withFileName("foo").addRule(new MockRule("name", "desc", "msg", "rulesetname")).addRule(new MockRule("name2", "desc", "msg", "rulesetname")).build();
        RuleSet set1 = createRuleSetBuilder("ruleset1").addRuleSetByReference(set2, true).build();
        Assert.assertEquals("wrong rule size", 2, set1.getRules().size());
        for (Rule rule : set1.getRules()) {
            Assert.assertTrue("not a rule reference", (rule instanceof RuleReference));
            RuleReference ruleReference = ((RuleReference) (rule));
            Assert.assertEquals("wrong ruleset file name", "foo", ruleReference.getRuleSetReference().getRuleSetFileName());
            Assert.assertTrue("not all rule reference", ruleReference.getRuleSetReference().isAllRules());
        }
    }

    @Test
    public void testAddRuleSetByReferenceSingleRule() {
        RuleSet set2 = createRuleSetBuilder("ruleset2").withFileName("foo").addRule(new MockRule("name", "desc", "msg", "rulesetname")).addRule(new MockRule("name2", "desc", "msg", "rulesetname")).build();
        RuleSet set1 = createRuleSetBuilder("ruleset1").addRuleSetByReference(set2, false).build();
        Assert.assertEquals("wrong rule size", 2, set1.getRules().size());
        for (Rule rule : set1.getRules()) {
            Assert.assertTrue("not a rule reference", (rule instanceof RuleReference));
            RuleReference ruleReference = ((RuleReference) (rule));
            Assert.assertEquals("wrong ruleset file name", "foo", ruleReference.getRuleSetReference().getRuleSetFileName());
            Assert.assertFalse("should not be all rule reference", ruleReference.getRuleSetReference().isAllRules());
        }
    }

    @Test
    public void testApply0Rules() throws Exception {
        RuleSet ruleset = createRuleSetBuilder("ruleset").build();
        verifyRuleSet(ruleset, 0, new HashSet<RuleViolation>());
    }

    @Test
    public void testEquals1() {
        RuleSet s = createRuleSetBuilder("ruleset").build();
        Assert.assertFalse("A ruleset cannot be equals to null", s.equals(null));
    }

    @Test
    @SuppressWarnings("PMD.UseAssertEqualsInsteadOfAssertTrue")
    public void testEquals2() {
        RuleSet s = createRuleSetBuilder("ruleset").build();
        Assert.assertTrue("A rulset must be equals to itself", s.equals(s));
    }

    @Test
    public void testEquals3() {
        RuleSet s = new RuleSetBuilder(new Random().nextLong()).withName("basic rules").withDescription("desc").build();
        Assert.assertFalse("A ruleset cannot be equals to another kind of object", s.equals("basic rules"));
    }

    @Test
    public void testEquals4() {
        RuleSet s1 = createRuleSetBuilder("my ruleset").addRule(new MockRule("name", "desc", "msg", "rulesetname")).build();
        RuleSet s2 = createRuleSetBuilder("my ruleset").addRule(new MockRule("name", "desc", "msg", "rulesetname")).build();
        Assert.assertEquals("2 rulesets with same name and rules must be equals", s1, s2);
        Assert.assertEquals("Equals rulesets must have the same hashcode", s1.hashCode(), s2.hashCode());
    }

    @Test
    public void testEquals5() {
        RuleSet s1 = createRuleSetBuilder("my ruleset").addRule(new MockRule("name", "desc", "msg", "rulesetname")).build();
        RuleSet s2 = createRuleSetBuilder("my other ruleset").addRule(new MockRule("name", "desc", "msg", "rulesetname")).build();
        Assert.assertFalse("2 rulesets with different name but same rules must not be equals", s1.equals(s2));
    }

    @Test
    public void testEquals6() {
        RuleSet s1 = createRuleSetBuilder("my ruleset").addRule(new MockRule("name", "desc", "msg", "rulesetname")).build();
        RuleSet s2 = createRuleSetBuilder("my ruleset").addRule(new MockRule("other rule", "desc", "msg", "rulesetname")).build();
        Assert.assertFalse("2 rulesets with same name but different rules must not be equals", s1.equals(s2));
    }

    @Test
    public void testLanguageApplies() {
        Rule rule = new MockRule();
        rule.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        Assert.assertFalse("Different languages should not apply", RuleSet.applies(rule, LanguageRegistry.getLanguage(Dummy2LanguageModule.NAME).getDefaultVersion()));
        rule.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        Assert.assertTrue("Same language with no min/max should apply", RuleSet.applies(rule, LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.5")));
        rule.setMinimumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.5"));
        Assert.assertTrue("Same language with valid min only should apply", RuleSet.applies(rule, LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.5")));
        rule.setMaximumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.6"));
        Assert.assertTrue("Same language with valid min and max should apply", RuleSet.applies(rule, LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.5")));
        Assert.assertFalse("Same language with outside range of min/max should not apply", RuleSet.applies(rule, LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.4")));
        Assert.assertFalse("Same language with outside range of min/max should not apply", RuleSet.applies(rule, LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.7")));
    }

    @Test
    public void testAddExcludePattern() {
        RuleSet ruleSet = createRuleSetBuilder("ruleset1").addExcludePattern("*").build();
        Assert.assertNotNull("Exclude patterns", ruleSet.getExcludePatterns());
        Assert.assertEquals("Invalid number of patterns", 1, ruleSet.getExcludePatterns().size());
        RuleSet ruleSet2 = // try to create a duplicate
        createRuleSetBuilder("ruleset2").addExcludePattern("*").addExcludePattern("*").build();
        Assert.assertEquals("Invalid number of patterns", 1, ruleSet2.getExcludePatterns().size());
        Assert.assertEquals("Exclude pattern", "*", ruleSet2.getExcludePatterns().get(0));
        Assert.assertNotNull("Include patterns", ruleSet2.getIncludePatterns());
        Assert.assertEquals("Invalid number of include patterns", 0, ruleSet2.getIncludePatterns().size());
    }

    @Test
    public void testAddExcludePatterns() {
        RuleSet ruleSet = createRuleSetBuilder("ruleset1").addExcludePattern("*").addExcludePattern(".*").build();
        RuleSet ruleSet2 = createRuleSetBuilder("ruleset2").addExcludePatterns(ruleSet.getExcludePatterns()).build();
        Assert.assertNotNull("Exclude patterns", ruleSet2.getExcludePatterns());
        Assert.assertEquals("Invalid number of patterns", 2, ruleSet2.getExcludePatterns().size());
        RuleSet ruleSet3 = // try to create a duplicate
        createRuleSetBuilder("ruleset3").addExcludePattern("*").addExcludePattern(".*").addExcludePattern(".*").build();
        Assert.assertEquals("Invalid number of patterns", 2, ruleSet3.getExcludePatterns().size());
        Assert.assertEquals("Exclude pattern", "*", ruleSet3.getExcludePatterns().get(0));
        Assert.assertEquals("Exclude pattern", ".*", ruleSet3.getExcludePatterns().get(1));
        Assert.assertNotNull("Include patterns", ruleSet3.getIncludePatterns());
        Assert.assertEquals("Invalid number of include patterns", 0, ruleSet3.getIncludePatterns().size());
    }

    @Test
    public void testSetExcludePatterns() {
        List<String> excludePatterns = new ArrayList<>();
        excludePatterns.add("*");
        excludePatterns.add(".*");
        RuleSet ruleSet = createRuleSetBuilder("ruleset").setExcludePatterns(excludePatterns).build();
        Assert.assertNotNull("Exclude patterns", ruleSet.getExcludePatterns());
        Assert.assertEquals("Invalid number of exclude patterns", 2, ruleSet.getExcludePatterns().size());
        Assert.assertEquals("Exclude pattern", "*", ruleSet.getExcludePatterns().get(0));
        Assert.assertEquals("Exclude pattern", ".*", ruleSet.getExcludePatterns().get(1));
        Assert.assertNotNull("Include patterns", ruleSet.getIncludePatterns());
        Assert.assertEquals("Invalid number of include patterns", 0, ruleSet.getIncludePatterns().size());
    }

    @Test
    public void testAddIncludePattern() {
        RuleSet ruleSet = createRuleSetBuilder("ruleset").addIncludePattern("*").build();
        Assert.assertNotNull("Include patterns", ruleSet.getIncludePatterns());
        Assert.assertEquals("Invalid number of patterns", 1, ruleSet.getIncludePatterns().size());
        Assert.assertEquals("Include pattern", "*", ruleSet.getIncludePatterns().get(0));
        Assert.assertNotNull("Exclude patterns", ruleSet.getExcludePatterns());
        Assert.assertEquals("Invalid number of exclude patterns", 0, ruleSet.getExcludePatterns().size());
    }

    @Test
    public void testAddIncludePatterns() {
        RuleSet ruleSet = createRuleSetBuilder("ruleset1").addIncludePattern("*").addIncludePattern(".*").build();
        RuleSet ruleSet2 = createRuleSetBuilder("ruleset1").addIncludePatterns(ruleSet.getIncludePatterns()).build();
        Assert.assertNotNull("Include patterns", ruleSet2.getIncludePatterns());
        Assert.assertEquals("Invalid number of patterns", 2, ruleSet2.getIncludePatterns().size());
        Assert.assertEquals("Include pattern", "*", ruleSet2.getIncludePatterns().get(0));
        Assert.assertEquals("Include pattern", ".*", ruleSet2.getIncludePatterns().get(1));
        Assert.assertNotNull("Exclude patterns", ruleSet.getExcludePatterns());
        Assert.assertEquals("Invalid number of exclude patterns", 0, ruleSet.getExcludePatterns().size());
    }

    @Test
    public void testSetIncludePatterns() {
        List<String> includePatterns = new ArrayList<>();
        includePatterns.add("*");
        includePatterns.add(".*");
        RuleSet ruleSet = createRuleSetBuilder("ruleset").setIncludePatterns(includePatterns).build();
        Assert.assertNotNull("Include patterns", ruleSet.getIncludePatterns());
        Assert.assertEquals("Invalid number of include patterns", 2, ruleSet.getIncludePatterns().size());
        Assert.assertEquals("Include pattern", "*", ruleSet.getIncludePatterns().get(0));
        Assert.assertEquals("Include pattern", ".*", ruleSet.getIncludePatterns().get(1));
        Assert.assertNotNull("Exclude patterns", ruleSet.getExcludePatterns());
        Assert.assertEquals("Invalid number of exclude patterns", 0, ruleSet.getExcludePatterns().size());
    }

    @Test
    public void testIncludeExcludeApplies() {
        File file = new File("C:\\myworkspace\\project\\some\\random\\package\\RandomClass.java");
        RuleSet ruleSet = createRuleSetBuilder("ruleset").build();
        Assert.assertTrue("No patterns", ruleSet.applies(file));
        ruleSet = createRuleSetBuilder("ruleset").addExcludePattern("nomatch").build();
        Assert.assertTrue("Non-matching exclude", ruleSet.applies(file));
        ruleSet = createRuleSetBuilder("ruleset").addExcludePattern("nomatch").addExcludePattern(".*/package/.*").build();
        Assert.assertFalse("Matching exclude", ruleSet.applies(file));
        ruleSet = createRuleSetBuilder("ruleset").addExcludePattern("nomatch").addExcludePattern(".*/package/.*").addIncludePattern(".*/randomX/.*").build();
        Assert.assertFalse("Non-matching include", ruleSet.applies(file));
        ruleSet = createRuleSetBuilder("ruleset").addExcludePattern("nomatch").addExcludePattern(".*/package/.*").addIncludePattern(".*/randomX/.*").addIncludePattern(".*/random/.*").build();
        Assert.assertTrue("Matching include", ruleSet.applies(file));
    }

    @Test
    public void testIncludeExcludeMultipleRuleSetWithRuleChainApplies() throws PMDException {
        File file = new File("C:\\myworkspace\\project\\some\\random\\package\\RandomClass.java");
        Rule rule = new FooRule();
        rule.setName("FooRule1");
        rule.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        rule.addRuleChainVisit("dummyNode");
        Assert.assertTrue("RuleChain rule", rule.isRuleChain());
        RuleSet ruleSet1 = createRuleSetBuilder("RuleSet1").addRule(rule).build();
        RuleSet ruleSet2 = createRuleSetBuilder("RuleSet2").addRule(rule).build();
        RuleSets ruleSets = new RuleSets();
        ruleSets.addRuleSet(ruleSet1);
        ruleSets.addRuleSet(ruleSet2);
        // Two violations
        PMD p = new PMD();
        RuleContext ctx = new RuleContext();
        Report r = new Report();
        ctx.setReport(r);
        ctx.setSourceCodeFilename(file.getName());
        ctx.setSourceCodeFile(file);
        ctx.setLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion());
        ruleSets.apply(makeCompilationUnits(), ctx, LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        Assert.assertEquals("Violations", 2, r.size());
        // One violation
        ruleSet1 = createRuleSetBuilder("RuleSet1").addExcludePattern(".*/package/.*").addRule(rule).build();
        ruleSets = new RuleSets();
        ruleSets.addRuleSet(ruleSet1);
        ruleSets.addRuleSet(ruleSet2);
        r = new Report();
        ctx.setReport(r);
        ruleSets.apply(makeCompilationUnits(), ctx, LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        Assert.assertEquals("Violations", 1, r.size());
    }

    @Test
    public void copyConstructorDeepCopies() {
        Rule rule = new FooRule();
        rule.setName("FooRule1");
        rule.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        rule.addRuleChainVisit("dummyNode");
        RuleSet ruleSet1 = createRuleSetBuilder("RuleSet1").addRule(rule).build();
        RuleSet ruleSet2 = new RuleSet(ruleSet1);
        Assert.assertEquals(ruleSet1, ruleSet2);
        Assert.assertNotSame(ruleSet1, ruleSet2);
        Assert.assertEquals(rule, ruleSet2.getRuleByName("FooRule1"));
        Assert.assertNotSame(rule, ruleSet2.getRuleByName("FooRule1"));
    }

    @Test
    public void ruleExceptionShouldBeReported() {
        RuleSet ruleset = createRuleSetBuilder("ruleExceptionShouldBeReported").addRule(new MockRule() {
            @Override
            public void apply(List<? extends Node> nodes, RuleContext ctx) {
                throw new RuntimeException("Test exception while applying rule");
            }
        }).build();
        RuleContext context = new RuleContext();
        context.setReport(new Report());
        context.setLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion());
        context.setSourceCodeFilename(((RuleSetTest.class.getName()) + ".ruleExceptionShouldBeReported"));
        context.setIgnoreExceptions(true);// the default

        ruleset.apply(makeCompilationUnits(), context);
        Assert.assertTrue("Report should have processing errors", context.getReport().hasErrors());
        List<ProcessingError> errors = CollectionUtil.toList(context.getReport().errors());
        Assert.assertEquals("Errors expected", 1, errors.size());
        Assert.assertEquals("Wrong error message", "RuntimeException: Test exception while applying rule", errors.get(0).getMsg());
        Assert.assertTrue("Should be a RuntimeException", ((errors.get(0).getError()) instanceof RuntimeException));
    }

    @Test(expected = RuntimeException.class)
    public void ruleExceptionShouldBeThrownIfNotIgnored() {
        RuleSet ruleset = createRuleSetBuilder("ruleExceptionShouldBeReported").addRule(new MockRule() {
            @Override
            public void apply(List<? extends Node> nodes, RuleContext ctx) {
                throw new RuntimeException("Test exception while applying rule");
            }
        }).build();
        RuleContext context = new RuleContext();
        context.setReport(new Report());
        context.setLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion());
        context.setSourceCodeFilename(((RuleSetTest.class.getName()) + ".ruleExceptionShouldBeThrownIfNotIgnored"));
        context.setIgnoreExceptions(false);
        ruleset.apply(makeCompilationUnits(), context);
    }

    @Test
    public void ruleExceptionShouldNotStopProcessingFile() {
        RuleSet ruleset = createRuleSetBuilder("ruleExceptionShouldBeReported").addRule(new MockRule() {
            @Override
            public void apply(List<? extends Node> nodes, RuleContext ctx) {
                throw new RuntimeException("Test exception while applying rule");
            }
        }).addRule(new MockRule() {
            @Override
            public void apply(List<? extends Node> nodes, RuleContext ctx) {
                for (Node node : nodes) {
                    addViolationWithMessage(ctx, node, "Test violation of the second rule in the ruleset");
                }
            }
        }).build();
        RuleContext context = new RuleContext();
        context.setReport(new Report());
        context.setLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion());
        context.setSourceCodeFilename(((RuleSetTest.class.getName()) + ".ruleExceptionShouldBeReported"));
        context.setIgnoreExceptions(true);// the default

        ruleset.apply(makeCompilationUnits(), context);
        Assert.assertTrue("Report should have processing errors", context.getReport().hasErrors());
        List<ProcessingError> errors = CollectionUtil.toList(context.getReport().errors());
        Assert.assertEquals("Errors expected", 1, errors.size());
        Assert.assertEquals("Wrong error message", "RuntimeException: Test exception while applying rule", errors.get(0).getMsg());
        Assert.assertTrue("Should be a RuntimeException", ((errors.get(0).getError()) instanceof RuntimeException));
        Assert.assertEquals("There should be a violation", 1, context.getReport().size());
    }

    @Test
    public void ruleExceptionShouldNotStopProcessingFileWithRuleChain() {
        RuleSet ruleset = createRuleSetBuilder("ruleExceptionShouldBeReported").addRule(new MockRule() {
            {
                addRuleChainVisit("dummyNode");
            }

            @Override
            public void apply(List<? extends Node> nodes, RuleContext ctx) {
                throw new RuntimeException("Test exception while applying rule");
            }
        }).addRule(new MockRule() {
            {
                addRuleChainVisit("dummyNode");
            }

            @Override
            public void apply(List<? extends Node> nodes, RuleContext ctx) {
                for (Node node : nodes) {
                    addViolationWithMessage(ctx, node, "Test violation of the second rule in the ruleset");
                }
            }
        }).build();
        RuleContext context = new RuleContext();
        context.setReport(new Report());
        context.setLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion());
        context.setSourceCodeFilename(((RuleSetTest.class.getName()) + ".ruleExceptionShouldBeReported"));
        context.setIgnoreExceptions(true);// the default

        RuleSets rulesets = new RuleSets(ruleset);
        rulesets.apply(makeCompilationUnits(), context, LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        Assert.assertTrue("Report should have processing errors", context.getReport().hasErrors());
        List<ProcessingError> errors = CollectionUtil.toList(context.getReport().errors());
        Assert.assertEquals("Errors expected", 1, errors.size());
        Assert.assertEquals("Wrong error message", "RuntimeException: Test exception while applying rule", errors.get(0).getMsg());
        Assert.assertTrue("Should be a RuntimeException", ((errors.get(0).getError()) instanceof RuntimeException));
        Assert.assertEquals("There should be a violation", 1, context.getReport().size());
    }
}

