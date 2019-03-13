/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.docs;


import FlowStyle.BLOCK;
import LineBreak.WIN;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Test;
import org.yaml.snakeyaml.DumperOptions;


public class SidebarGeneratorTest {
    private MockedFileWriter writer = new MockedFileWriter();

    @Test
    public void testSidebar() throws IOException {
        Map<Language, List<RuleSet>> rulesets = new HashMap<>();
        RuleSet ruleSet1 = new RuleSetFactory().createNewRuleSet("test", "test", "bestpractices.xml", Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        RuleSet ruleSet2 = new RuleSetFactory().createNewRuleSet("test2", "test", "codestyle.xml", Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        rulesets.put(LanguageRegistry.findLanguageByTerseName("java"), Arrays.asList(ruleSet1, ruleSet2));
        rulesets.put(LanguageRegistry.findLanguageByTerseName("ecmascript"), Arrays.asList(ruleSet1));
        SidebarGenerator generator = new SidebarGenerator(writer, FileSystems.getDefault().getPath(".."));
        List<Map<String, Object>> result = generator.generateRuleReferenceSection(rulesets);
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(BLOCK);
        if (SystemUtils.IS_OS_WINDOWS) {
            options.setLineBreak(WIN);
        }
        String yaml = dump(result);
        String expected = MockedFileWriter.normalizeLineSeparators(IOUtils.toString(SidebarGeneratorTest.class.getResourceAsStream("sidebar.yml"), StandardCharsets.UTF_8));
        Assert.assertEquals(expected, yaml);
    }
}

