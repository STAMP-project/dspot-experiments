/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;


import JavaLanguageModule.NAME;
import java.io.StringReader;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.testframework.RuleTst;
import org.junit.Assert;
import org.junit.Test;

import static PMD.EOL;


public class ExcludeLinesTest extends RuleTst {
    private Rule rule;

    @Test
    public void testAcceptance() {
        runTest(new net.sourceforge.pmd.testframework.TestDescriptor(ExcludeLinesTest.TEST1, "NOPMD should work", 0, rule));
        runTest(new net.sourceforge.pmd.testframework.TestDescriptor(ExcludeLinesTest.TEST2, "Should fail without exclude marker", 1, rule));
    }

    @Test
    public void testAlternateMarker() throws Exception {
        PMD p = new PMD();
        p.getConfiguration().setSuppressMarker("FOOBAR");
        RuleContext ctx = new RuleContext();
        Report r = new Report();
        ctx.setReport(r);
        ctx.setSourceCodeFilename("n/a");
        ctx.setLanguageVersion(LanguageRegistry.getLanguage(NAME).getDefaultVersion());
        RuleSet rules = new RuleSetFactory().createSingleRuleRuleSet(rule);
        p.getSourceCodeProcessor().processSourceCode(new StringReader(ExcludeLinesTest.TEST3), new RuleSets(rules), ctx);
        Assert.assertTrue(r.isEmpty());
        Assert.assertEquals(r.getSuppressedRuleViolations().size(), 1);
    }

    private static final String TEST1 = ((((((("public class Foo {" + (EOL)) + " void foo() {") + (EOL)) + "  int x; //NOPMD ") + (EOL)) + " } ") + (EOL)) + "}";

    private static final String TEST2 = ((((((("public class Foo {" + (EOL)) + " void foo() {") + (EOL)) + "  int x;") + (EOL)) + " } ") + (EOL)) + "}";

    private static final String TEST3 = ((((((("public class Foo {" + (EOL)) + " void foo() {") + (EOL)) + "  int x; // FOOBAR") + (EOL)) + " } ") + (EOL)) + "}";
}

