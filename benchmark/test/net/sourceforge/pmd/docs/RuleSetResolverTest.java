/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.docs;


import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Test;


public class RuleSetResolverTest {
    private static List<String> excludedRulesets = new ArrayList<>();

    static {
        RuleSetResolverTest.excludedRulesets.add(FilenameUtils.normalize("pmd-test/src/main/resources/rulesets/dummy/basic.xml"));
    }

    @Test
    public void resolveAllRulesets() {
        Path basePath = FileSystems.getDefault().getPath(".").resolve("..").toAbsolutePath().normalize();
        List<String> additionalRulesets = GenerateRuleDocsCmd.findAdditionalRulesets(basePath);
        filterRuleSets(additionalRulesets);
        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        for (String filename : additionalRulesets) {
            try {
                ruleSetFactory.createRuleSet(filename);
            } catch (RuntimeException | RuleSetNotFoundException e) {
                Assert.fail(((("Couldn't load ruleset " + filename) + ": ") + (getMessage())));
            }
        }
    }
}

