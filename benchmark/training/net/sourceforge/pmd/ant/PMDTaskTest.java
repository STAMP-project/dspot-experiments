/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ant;


import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildFileRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class PMDTaskTest {
    @Rule
    public final BuildFileRule buildRule = new BuildFileRule();

    @Test
    public void testFormatterWithNoToFileAttribute() {
        try {
            buildRule.executeTarget("testFormatterWithNoToFileAttribute");
            Assert.fail("This should throw an exception");
        } catch (BuildException ex) {
            Assert.assertEquals("toFile or toConsole needs to be specified in Formatter", ex.getMessage());
        }
    }

    @Test
    public void testNoRuleSets() {
        try {
            buildRule.executeTarget("testNoRuleSets");
            Assert.fail("This should throw an exception");
        } catch (BuildException ex) {
            Assert.assertEquals("No rulesets specified", ex.getMessage());
        }
    }

    @Test
    public void testBasic() {
        buildRule.executeTarget("testBasic");
    }

    @Test
    public void testInvalidLanguageVersion() {
        try {
            buildRule.executeTarget("testInvalidLanguageVersion");
            Assert.assertEquals("The following language is not supported:<sourceLanguage name=\"java\" version=\"42\" />.", buildRule.getLog());
            Assert.fail("This should throw an exception");
        } catch (BuildException ex) {
            Assert.assertEquals("The following language is not supported:<sourceLanguage name=\"java\" version=\"42\" />.", ex.getMessage());
        }
    }
}

