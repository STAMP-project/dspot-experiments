/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;


import RuleViolationComparator.INSTANCE;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.sourceforge.pmd.lang.rule.MockRule;
import org.junit.Assert;
import org.junit.Test;


public class RuleViolationComparatorTest {
    @Test
    public void testComparator() {
        Rule rule1 = new MockRule("name1", "desc", "msg", "rulesetname1");
        Rule rule2 = new MockRule("name2", "desc", "msg", "rulesetname2");
        // RuleViolations created in pre-sorted order
        RuleViolation[] expectedOrder = new RuleViolation[12];
        int index = 0;
        // Different begin line
        expectedOrder[(index++)] = createJavaRuleViolation(rule1, "file1", 10, "desc1", 0, 20, 80);
        expectedOrder[(index++)] = createJavaRuleViolation(rule1, "file1", 20, "desc1", 0, 20, 80);
        // Different description
        expectedOrder[(index++)] = createJavaRuleViolation(rule1, "file2", 10, "desc1", 0, 20, 80);
        expectedOrder[(index++)] = createJavaRuleViolation(rule1, "file2", 10, "desc2", 0, 20, 80);
        // Different begin column
        expectedOrder[(index++)] = createJavaRuleViolation(rule1, "file3", 10, "desc1", 0, 20, 80);
        expectedOrder[(index++)] = createJavaRuleViolation(rule1, "file3", 10, "desc1", 10, 20, 80);
        // Different end line
        expectedOrder[(index++)] = createJavaRuleViolation(rule1, "file4", 10, "desc1", 0, 20, 80);
        expectedOrder[(index++)] = createJavaRuleViolation(rule1, "file4", 10, "desc1", 0, 30, 80);
        // Different end column
        expectedOrder[(index++)] = createJavaRuleViolation(rule1, "file5", 10, "desc1", 0, 20, 80);
        expectedOrder[(index++)] = createJavaRuleViolation(rule1, "file5", 10, "desc1", 0, 20, 90);
        // Different rule name
        expectedOrder[(index++)] = createJavaRuleViolation(rule1, "file6", 10, "desc1", 0, 20, 80);
        expectedOrder[(index++)] = createJavaRuleViolation(rule2, "file6", 10, "desc1", 0, 20, 80);
        // Randomize
        List<RuleViolation> ruleViolations = new java.util.ArrayList(Arrays.asList(expectedOrder));
        long seed = System.nanoTime();
        Random random = new Random(seed);
        Collections.shuffle(ruleViolations, random);
        // Sort
        Collections.sort(ruleViolations, INSTANCE);
        // Check
        int count = 0;
        for (int i = 0; i < (expectedOrder.length); i++) {
            count++;
            Assert.assertSame(((("Wrong RuleViolation " + i) + ", usind seed: ") + seed), expectedOrder[i], ruleViolations.get(i));
        }
        Assert.assertEquals("Missing assertion for every RuleViolation", expectedOrder.length, count);
    }
}

