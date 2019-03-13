package org.drools.compiler.integrationtests.incrementalcompilation;


import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.test.testcategory.TurtleTestCategory;


@Category(TurtleTestCategory.class)
public abstract class AbstractAddRemoveGenerated2RulesTest {
    private final String rule1;

    private final String rule2;

    public AbstractAddRemoveGenerated2RulesTest(final ConstraintsPair constraintsPair) {
        final String rule1 = ((((((((((("package " + (TestUtil.RULES_PACKAGE_NAME)) + ";") + "global java.util.List list\n") + "rule ") + (TestUtil.RULE1_NAME)) + " \n") + " when \n ${constraints} ") + "then\n") + " list.add('") + (TestUtil.RULE1_NAME)) + "\'); \n") + "end\n";
        final String rule2 = ((((((((((("package " + (TestUtil.RULES_PACKAGE_NAME)) + ";") + "global java.util.List list\n") + "rule ") + (TestUtil.RULE2_NAME)) + " \n") + " when \n ${constraints} ") + "then\n") + " list.add('") + (TestUtil.RULE2_NAME)) + "\'); \n") + "end\n";
        this.rule1 = rule1.replace("${constraints}", constraintsPair.getConstraints1());
        this.rule2 = rule2.replace("${constraints}", constraintsPair.getConstraints2());
    }

    // ///////////////////////// TESTS //////////////////////////////////
    @Test(timeout = 10000)
    public void testInsertFactsFireRulesRemoveRules() {
        AddRemoveTestCases.insertFactsFireRulesRemoveRules1(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsFireRulesRemoveRules2(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsFireRulesRemoveRules3(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
    }

    @Test(timeout = 10000)
    public void testInsertFactsFireRulesRemoveRulesRevertedRules() {
        AddRemoveTestCases.insertFactsFireRulesRemoveRules1(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsFireRulesRemoveRules2(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsFireRulesRemoveRules3(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
    }

    @Test(timeout = 10000)
    public void testFireRulesInsertFactsFireRulesRemoveRules() {
        AddRemoveTestCases.fireRulesInsertFactsFireRulesRemoveRules1(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
        AddRemoveTestCases.fireRulesInsertFactsFireRulesRemoveRules2(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
        AddRemoveTestCases.fireRulesInsertFactsFireRulesRemoveRules3(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
    }

    @Test(timeout = 10000)
    public void testFireRulesInsertFactsFireRulesRemoveRulesRevertedRules() {
        AddRemoveTestCases.fireRulesInsertFactsFireRulesRemoveRules1(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
        AddRemoveTestCases.fireRulesInsertFactsFireRulesRemoveRules2(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
        AddRemoveTestCases.fireRulesInsertFactsFireRulesRemoveRules3(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
    }

    @Test(timeout = 10000)
    public void testInsertFactsRemoveRulesFireRulesRemoveRules() {
        AddRemoveTestCases.insertFactsRemoveRulesFireRulesRemoveRules1(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsRemoveRulesFireRulesRemoveRules2(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsRemoveRulesFireRulesRemoveRules3(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
    }

    @Test(timeout = 10000)
    public void testInsertFactsRemoveRulesFireRulesRemoveRulesRevertedRules() {
        AddRemoveTestCases.insertFactsRemoveRulesFireRulesRemoveRules1(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsRemoveRulesFireRulesRemoveRules2(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsRemoveRulesFireRulesRemoveRules3(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
    }

    @Test(timeout = 10000)
    public void testInsertFactsFireRulesRemoveRulesReinsertRules() {
        AddRemoveTestCases.insertFactsFireRulesRemoveRulesReinsertRules1(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsFireRulesRemoveRulesReinsertRules2(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsFireRulesRemoveRulesReinsertRules3(rule1, rule2, TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, null, getFacts());
    }

    @Test(timeout = 10000)
    public void testInsertFactsFireRulesRemoveRulesReinsertRulesRevertedRules() {
        AddRemoveTestCases.insertFactsFireRulesRemoveRulesReinsertRules1(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsFireRulesRemoveRulesReinsertRules2(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
        AddRemoveTestCases.insertFactsFireRulesRemoveRulesReinsertRules3(rule2, rule1, TestUtil.RULE2_NAME, TestUtil.RULE1_NAME, null, getFacts());
    }
}

