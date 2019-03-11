/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex;


import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import org.junit.Assert;
import org.junit.Test;


public class DefaultRulesetTest {
    @Test
    public void loadDefaultRuleset() throws Exception {
        RuleSetFactory factory = new RuleSetFactory();
        RuleSet ruleset = factory.createRuleSet("rulesets/apex/ruleset.xml");
        Assert.assertNotNull(ruleset);
    }
}

