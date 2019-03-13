/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex;


import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import org.junit.Assert;
import org.junit.Test;


public class DefaultRulesetTest {
    private RuleSetFactory factory = new RuleSetFactory();

    @Test
    public void loadDefaultRuleset() throws Exception {
        RuleSet ruleset = factory.createRuleSet("rulesets/apex/ruleset.xml");
        Assert.assertNotNull(ruleset);
    }

    @Test
    public void loadQuickstartRuleset() throws Exception {
        RuleSet ruleset = factory.createRuleSet("rulesets/apex/quickstart.xml");
        Assert.assertNotNull(ruleset);
    }
}

