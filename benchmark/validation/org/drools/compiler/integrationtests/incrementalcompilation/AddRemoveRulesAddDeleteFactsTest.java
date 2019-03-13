package org.drools.compiler.integrationtests.incrementalcompilation;


import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.test.testcategory.TurtleTestCategory;


@Category(TurtleTestCategory.class)
@RunWith(Parameterized.class)
public class AddRemoveRulesAddDeleteFactsTest {
    private final StringPermutation rulesPermutation;

    public AddRemoveRulesAddDeleteFactsTest(final StringPermutation rulesPermutation) {
        this.rulesPermutation = rulesPermutation;
    }

    @Test
    public void testAddRemoveRulesAddRemoveFacts() {
        final KieSession kieSession = TestUtil.buildSessionInSteps(AddRemoveRulesAddDeleteFactsTest.getRules());
        try {
            final List<String> resultsList = new ArrayList<>();
            kieSession.setGlobal("list", resultsList);
            final List<FactHandle> insertedFacts = TestUtil.insertFacts(kieSession, getFacts());
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).containsOnly(TestUtil.RULE1_NAME, TestUtil.RULE2_NAME, TestUtil.RULE3_NAME);
            resultsList.clear();
            TestUtil.removeRules(kieSession, TestUtil.RULES_PACKAGE_NAME, rulesPermutation.getPermutation());
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
            TestUtil.removeFacts(kieSession, insertedFacts);
            kieSession.fireAllRules();
            Assertions.assertThat(resultsList).isEmpty();
        } finally {
            kieSession.dispose();
        }
    }
}

