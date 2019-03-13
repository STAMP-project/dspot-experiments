/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.docs;


import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Test;


public class RuleDocGeneratorTest {
    private MockedFileWriter writer = new MockedFileWriter();

    private Path root;

    @Test
    public void testSingleRuleset() throws IOException, RuleSetNotFoundException {
        RuleDocGenerator generator = new RuleDocGenerator(writer, root);
        RuleSetFactory rsf = new RuleSetFactory();
        RuleSet ruleset = rsf.createRuleSet("rulesets/ruledoctest/sample.xml");
        generator.generate(Arrays.asList(ruleset).iterator(), Arrays.asList("rulesets/ruledoctest/sample-deprecated.xml", "rulesets/ruledoctest/other-ruleset.xml"));
        Assert.assertEquals(3, writer.getData().size());
        MockedFileWriter.FileEntry languageIndex = writer.getData().get(0);
        Assert.assertTrue(FilenameUtils.normalize(languageIndex.getFilename(), true).endsWith("docs/pages/pmd/rules/java.md"));
        Assert.assertEquals(RuleDocGeneratorTest.loadResource("/expected/java.md"), languageIndex.getContent());
        MockedFileWriter.FileEntry ruleSetIndex = writer.getData().get(1);
        Assert.assertTrue(FilenameUtils.normalize(ruleSetIndex.getFilename(), true).endsWith("docs/pages/pmd/rules/java/sample.md"));
        Assert.assertEquals(RuleDocGeneratorTest.loadResource("/expected/sample.md"), ruleSetIndex.getContent());
        MockedFileWriter.FileEntry sidebar = writer.getData().get(2);
        Assert.assertTrue(FilenameUtils.normalize(sidebar.getFilename(), true).endsWith("docs/_data/sidebars/pmd_sidebar.yml"));
        Assert.assertEquals(RuleDocGeneratorTest.loadResource("/expected/pmd_sidebar.yml"), sidebar.getContent());
    }
}

