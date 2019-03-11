/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule;


import JavaLanguageModule.NAME;
import XPathRuleQuery.XPATH_1_0;
import XPathRuleQuery.XPATH_1_0_COMPATIBILITY;
import XPathRuleQuery.XPATH_2_0;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.JaxenXPathRuleQuery;
import net.sourceforge.pmd.lang.rule.xpath.SaxonXPathRuleQuery;
import net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery;
import net.sourceforge.pmd.properties.StringMultiProperty;
import net.sourceforge.pmd.properties.StringProperty;
import net.sourceforge.pmd.testframework.RuleTst;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author daniels
 */
public class XPathRuleTest extends RuleTst {
    XPathRule rule;

    @Test
    public void testPluginname() throws Exception {
        rule.setXPath("//VariableDeclaratorId[string-length(@Image) < 3]");
        rule.setMessage("{0}");
        Report report = XPathRuleTest.getReportForTestString(rule, XPathRuleTest.TEST1);
        RuleViolation rv = report.iterator().next();
        Assert.assertEquals("a", rv.getDescription());
    }

    @Test
    public void testXPathMultiProperty() throws Exception {
        rule.setXPath("//VariableDeclaratorId[@Image=$forbiddenNames]");
        rule.setMessage("Avoid vars");
        rule.setVersion(XPATH_2_0);
        StringMultiProperty varDescriptor = StringMultiProperty.named("forbiddenNames").desc("Forbidden names").defaultValues("forbid1", "forbid2").delim('$').build();
        rule.definePropertyDescriptor(varDescriptor);
        Report report = XPathRuleTest.getReportForTestString(rule, XPathRuleTest.TEST3);
        Iterator<RuleViolation> rv = report.iterator();
        int i = 0;
        for (; rv.hasNext(); ++i) {
            rv.next();
        }
        Assert.assertEquals(2, i);
    }

    @Test
    public void testVariables() throws Exception {
        rule.setXPath("//VariableDeclaratorId[@Image=$var]");
        rule.setMessage("Avoid vars");
        StringProperty varDescriptor = new StringProperty("var", "Test var", null, 1.0F);
        rule.definePropertyDescriptor(varDescriptor);
        rule.setProperty(varDescriptor, "fiddle");
        Report report = XPathRuleTest.getReportForTestString(rule, XPathRuleTest.TEST2);
        RuleViolation rv = report.iterator().next();
        Assert.assertEquals(3, rv.getBeginLine());
    }

    /**
     * Test for problem reported in bug #1219 PrimarySuffix/@Image does not work
     * in some cases in xpath 2.0
     *
     * @throws Exception
     * 		any error
     */
    @Test
    public void testImageOfPrimarySuffix() throws Exception {
        final String SUFFIX = "import java.io.File;\n" + ((((("\n" + "public class TestSuffix {\n") + "    public static void main(String args[]) {\n") + "        new File(\"subdirectory\").list();\n") + "    }\n") + "}");
        LanguageVersion language = LanguageRegistry.getLanguage(NAME).getDefaultVersion();
        ParserOptions parserOptions = language.getLanguageVersionHandler().getDefaultParserOptions();
        Parser parser = language.getLanguageVersionHandler().getParser(parserOptions);
        ASTCompilationUnit cu = ((ASTCompilationUnit) (parser.parse("test", new StringReader(SUFFIX))));
        RuleContext ruleContext = new RuleContext();
        ruleContext.setLanguageVersion(language);
        String xpath = "//PrimarySuffix[@Image='list']";
        // XPATH version 1.0
        XPathRuleQuery xpathRuleQuery = new JaxenXPathRuleQuery();
        xpathRuleQuery.setXPath(xpath);
        xpathRuleQuery.setProperties(new HashMap<net.sourceforge.pmd.properties.PropertyDescriptor<?>, Object>());
        xpathRuleQuery.setVersion(XPATH_1_0);
        List<Node> nodes = xpathRuleQuery.evaluate(cu, ruleContext);
        Assert.assertEquals(1, nodes.size());
        // XPATH version 1.0 Compatibility
        xpathRuleQuery = new SaxonXPathRuleQuery();
        xpathRuleQuery.setXPath(xpath);
        xpathRuleQuery.setProperties(new HashMap<net.sourceforge.pmd.properties.PropertyDescriptor<?>, Object>());
        xpathRuleQuery.setVersion(XPATH_1_0_COMPATIBILITY);
        nodes = xpathRuleQuery.evaluate(cu, ruleContext);
        Assert.assertEquals(1, nodes.size());
        // XPATH version 2.0
        xpathRuleQuery = new SaxonXPathRuleQuery();
        xpathRuleQuery.setXPath(xpath);
        xpathRuleQuery.setProperties(new HashMap<net.sourceforge.pmd.properties.PropertyDescriptor<?>, Object>());
        xpathRuleQuery.setVersion(XPATH_2_0);
        nodes = xpathRuleQuery.evaluate(cu, ruleContext);
        Assert.assertEquals(1, nodes.size());
    }

    /**
     * Following sibling check: See https://sourceforge.net/p/pmd/bugs/1209/
     *
     * @throws Exception
     * 		any error
     */
    @Test
    public void testFollowingSibling() throws Exception {
        final String SOURCE = "public class dummy {\n" + ((((("  public String toString() {\n" + "    String test = \"bad example\";\n") + "    test = \"a\";\n") + "    return test;\n") + "  }\n") + "}");
        LanguageVersion language = LanguageRegistry.getLanguage(NAME).getDefaultVersion();
        ParserOptions parserOptions = language.getLanguageVersionHandler().getDefaultParserOptions();
        Parser parser = language.getLanguageVersionHandler().getParser(parserOptions);
        ASTCompilationUnit cu = ((ASTCompilationUnit) (parser.parse("test", new StringReader(SOURCE))));
        RuleContext ruleContext = new RuleContext();
        ruleContext.setLanguageVersion(language);
        String xpath = "//Block/BlockStatement/following-sibling::BlockStatement";
        // XPATH version 1.0
        XPathRuleQuery xpathRuleQuery = new JaxenXPathRuleQuery();
        xpathRuleQuery.setXPath(xpath);
        xpathRuleQuery.setProperties(new HashMap<net.sourceforge.pmd.properties.PropertyDescriptor<?>, Object>());
        xpathRuleQuery.setVersion(XPATH_1_0);
        List<Node> nodes = xpathRuleQuery.evaluate(cu, ruleContext);
        Assert.assertEquals(2, nodes.size());
        Assert.assertEquals(4, nodes.get(0).getBeginLine());
        Assert.assertEquals(5, nodes.get(1).getBeginLine());
        // XPATH version 2.0
        xpathRuleQuery = new SaxonXPathRuleQuery();
        xpathRuleQuery.setXPath(xpath);
        xpathRuleQuery.setProperties(new HashMap<net.sourceforge.pmd.properties.PropertyDescriptor<?>, Object>());
        xpathRuleQuery.setVersion(XPATH_2_0);
        nodes = xpathRuleQuery.evaluate(cu, ruleContext);
        Assert.assertEquals(2, nodes.size());
        Assert.assertEquals(4, nodes.get(0).getBeginLine());
        Assert.assertEquals(5, nodes.get(1).getBeginLine());
    }

    private static final String TEST1 = ((("public class Foo {" + (PMD.EOL)) + " int a;") + (PMD.EOL)) + "}";

    private static final String TEST2 = ((((("public class Foo {" + (PMD.EOL)) + " int faddle;") + (PMD.EOL)) + " int fiddle;") + (PMD.EOL)) + "}";

    private static final String TEST3 = ((("public class Foo {" + (PMD.EOL)) + " int forbid1; int forbid2; int forbid1$forbid2;") + (PMD.EOL)) + "}";
}

