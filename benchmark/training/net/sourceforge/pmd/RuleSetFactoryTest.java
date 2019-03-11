/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;


import RulePriority.LOW;
import RulePriority.MEDIUM;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sourceforge.pmd.junit.JavaUtilLoggingRule;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.util.ResourceLoader;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static PMD.EOL;
import static RulePriority.HIGH;
import static RulePriority.LOW;
import static RulePriority.MEDIUM_HIGH;
import static RulePriority.MEDIUM_LOW;


public class RuleSetFactoryTest {
    @Rule
    public ExpectedException ex = ExpectedException.none();

    @Test
    public void testRuleSetFileName() throws RuleSetNotFoundException {
        RuleSet rs = loadRuleSet(RuleSetFactoryTest.EMPTY_RULESET);
        Assert.assertNull("RuleSet file name not expected", rs.getFileName());
        RuleSetFactory rsf = new RuleSetFactory();
        rs = rsf.createRuleSet("net/sourceforge/pmd/TestRuleset1.xml");
        Assert.assertEquals("wrong RuleSet file name", rs.getFileName(), "net/sourceforge/pmd/TestRuleset1.xml");
    }

    @Test
    public void testNoRuleSetFileName() throws RuleSetNotFoundException {
        RuleSet rs = loadRuleSet(RuleSetFactoryTest.EMPTY_RULESET);
        Assert.assertNull("RuleSet file name not expected", rs.getFileName());
    }

    @Test
    public void testRefs() throws Exception {
        RuleSetFactory rsf = new RuleSetFactory();
        RuleSet rs = rsf.createRuleSet("net/sourceforge/pmd/TestRuleset1.xml");
        Assert.assertNotNull(rs.getRuleByName("TestRuleRef"));
    }

    @Test
    public void testExtendedReferences() throws Exception {
        InputStream in = new ResourceLoader().loadClassPathResourceAsStream("net/sourceforge/pmd/rulesets/reference-ruleset.xml");
        Assert.assertNotNull("Test ruleset not found - can't continue with test!", in);
        in.close();
        RuleSetFactory rsf = new RuleSetFactory();
        RuleSets rs = rsf.createRuleSets("net/sourceforge/pmd/rulesets/reference-ruleset.xml");
        // added by referencing a complete ruleset (TestRuleset1.xml)
        Assert.assertNotNull(rs.getRuleByName("MockRule1"));
        Assert.assertNotNull(rs.getRuleByName("MockRule2"));
        Assert.assertNotNull(rs.getRuleByName("MockRule3"));
        Assert.assertNotNull(rs.getRuleByName("TestRuleRef"));
        // added by specific reference
        Assert.assertNotNull(rs.getRuleByName("TestRule"));
        // this is from TestRuleset2.xml, but not referenced
        Assert.assertNull(rs.getRuleByName("TestRule2Ruleset2"));
        Rule mockRule3 = rs.getRuleByName("MockRule3");
        Assert.assertEquals("Overridden message", mockRule3.getMessage());
        Assert.assertEquals(2, mockRule3.getPriority().getPriority());
        Rule mockRule2 = rs.getRuleByName("MockRule2");
        Assert.assertEquals("Just combine them!", mockRule2.getMessage());
        // assert that MockRule2 is only once added to the ruleset, so that it
        // really
        // overwrites the configuration inherited from TestRuleset1.xml
        Assert.assertEquals(1, countRule(rs, "MockRule2"));
        Rule mockRule1 = rs.getRuleByName("MockRule1");
        Assert.assertNotNull(mockRule1);
        PropertyDescriptor<?> prop = mockRule1.getPropertyDescriptor("testIntProperty");
        Object property = mockRule1.getProperty(prop);
        Assert.assertEquals("5", String.valueOf(property));
        // included from TestRuleset3.xml
        Assert.assertNotNull(rs.getRuleByName("Ruleset3Rule2"));
        // excluded from TestRuleset3.xml
        Assert.assertNull(rs.getRuleByName("Ruleset3Rule1"));
        // overridden to 5
        Rule ruleset4Rule1 = rs.getRuleByName("Ruleset4Rule1");
        Assert.assertNotNull(ruleset4Rule1);
        Assert.assertEquals(5, ruleset4Rule1.getPriority().getPriority());
        Assert.assertEquals(1, countRule(rs, "Ruleset4Rule1"));
        // priority overridden for whole TestRuleset4 group
        Rule ruleset4Rule2 = rs.getRuleByName("Ruleset4Rule2");
        Assert.assertNotNull(ruleset4Rule2);
        Assert.assertEquals(2, ruleset4Rule2.getPriority().getPriority());
    }

    @Test(expected = RuleSetNotFoundException.class)
    public void testRuleSetNotFound() throws RuleSetNotFoundException {
        RuleSetFactory rsf = new RuleSetFactory();
        rsf.createRuleSet("fooooo");
    }

    @Test
    public void testCreateEmptyRuleSet() throws RuleSetNotFoundException {
        RuleSet rs = loadRuleSet(RuleSetFactoryTest.EMPTY_RULESET);
        Assert.assertEquals("test", rs.getName());
        Assert.assertEquals(0, rs.size());
    }

    @Test
    public void testSingleRule() throws RuleSetNotFoundException {
        RuleSet rs = loadRuleSet(RuleSetFactoryTest.SINGLE_RULE);
        Assert.assertEquals(1, rs.size());
        Rule r = rs.getRules().iterator().next();
        Assert.assertEquals("MockRuleName", r.getName());
        Assert.assertEquals("net.sourceforge.pmd.lang.rule.MockRule", r.getRuleClass());
        Assert.assertEquals("avoid the mock rule", r.getMessage());
    }

    @Test
    public void testMultipleRules() throws RuleSetNotFoundException {
        RuleSet rs = loadRuleSet(RuleSetFactoryTest.MULTIPLE_RULES);
        Assert.assertEquals(2, rs.size());
        Set<String> expected = new HashSet<>();
        expected.add("MockRuleName1");
        expected.add("MockRuleName2");
        for (Rule rule : rs.getRules()) {
            Assert.assertTrue(expected.contains(rule.getName()));
        }
    }

    @Test
    public void testSingleRuleWithPriority() throws RuleSetNotFoundException {
        Assert.assertEquals(MEDIUM, loadFirstRule(RuleSetFactoryTest.PRIORITY).getPriority());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testProps() throws RuleSetNotFoundException {
        Rule r = loadFirstRule(RuleSetFactoryTest.PROPERTIES);
        Assert.assertEquals("bar", r.getProperty(((PropertyDescriptor<String>) (r.getPropertyDescriptor("fooString")))));
        Assert.assertEquals(new Integer(3), r.getProperty(((PropertyDescriptor<Integer>) (r.getPropertyDescriptor("fooInt")))));
        Assert.assertTrue(r.getProperty(((PropertyDescriptor<Boolean>) (r.getPropertyDescriptor("fooBoolean")))));
        Assert.assertEquals(3.0, r.getProperty(((PropertyDescriptor<Double>) (r.getPropertyDescriptor("fooDouble")))), 0.05);
        Assert.assertNull(r.getPropertyDescriptor("BuggleFish"));
        Assert.assertNotSame(r.getDescription().indexOf("testdesc2"), (-1));
    }

    @Test
    public void testStringMultiPropertyDefaultDelimiter() throws Exception {
        Rule r = loadFirstRule(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<ruleset name=\"the ruleset\">\n  <description>Desc</description>\n" + ((((("     <rule name=\"myRule\" message=\"Do not place to this package. Move to \n{0} package/s instead.\" \n" + "class=\"net.sourceforge.pmd.lang.rule.XPathRule\" language=\"dummy\">\n") + "         <description>Please move your class to the right folder(rest \nfolder)</description>\n") + "         <priority>2</priority>\n         <properties>\n             <property name=\"packageRegEx\"") + " value=\"com.aptsssss|com.abc\" \ntype=\"List[String]\" ") + "description=\"valid packages\"/>\n         </properties></rule></ruleset>")));
        PropertyDescriptor<List<String>> prop = ((PropertyDescriptor<List<String>>) (r.getPropertyDescriptor("packageRegEx")));
        List<String> values = r.getProperty(prop);
        Assert.assertEquals(Arrays.asList("com.aptsssss", "com.abc"), values);
    }

    @Test
    public void testStringMultiPropertyDelimiter() throws Exception {
        Rule r = loadFirstRule(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + (((((((((("<ruleset name=\"test\">\n " + " <description>ruleset desc</description>\n     ") + "<rule name=\"myRule\" message=\"Do not place to this package. Move to \n{0} package/s") + " instead.\" \n") + "class=\"net.sourceforge.pmd.lang.rule.XPathRule\" language=\"dummy\">\n") + "         <description>Please move your class to the right folder(rest \nfolder)</description>\n") + "         <priority>2</priority>\n         <properties>\n             <property name=\"packageRegEx\"") + " value=\"com.aptsssss,com.abc\" \ntype=\"List[String]\" delimiter=\",\" ") + "description=\"valid packages\"/>\n") + "         </properties></rule>") + "</ruleset>")));
        PropertyDescriptor<List<String>> prop = ((PropertyDescriptor<List<String>>) (r.getPropertyDescriptor("packageRegEx")));
        List<String> values = r.getProperty(prop);
        Assert.assertEquals(Arrays.asList("com.aptsssss", "com.abc"), values);
    }

    @Test
    public void testRuleSetWithDeprecatedRule() throws Exception {
        RuleSet rs = loadRuleSet(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ((("<ruleset name=\"ruleset\">\n" + "  <description>ruleset desc</description>\n") + "     <rule deprecated=\"true\" ref=\"rulesets/dummy/basic.xml/DummyBasicMockRule\"/>") + "</ruleset>")));
        Assert.assertEquals(1, rs.getRules().size());
        Rule rule = rs.getRuleByName("DummyBasicMockRule");
        Assert.assertNotNull(rule);
    }

    @Test
    public void testRuleSetWithDeprecatedButRenamedRule() throws Exception {
        RuleSet rs = loadRuleSet(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ((((((("<ruleset name=\"test\">\n" + "  <description>ruleset desc</description>\n") + "     <rule deprecated=\"true\" ref=\"NewName\" name=\"OldName\"/>") + "     <rule name=\"NewName\" message=\"m\" class=\"net.sourceforge.pmd.lang.rule.XPathRule\" language=\"dummy\">") + "         <description>d</description>\n") + "         <priority>2</priority>\n") + "     </rule>") + "</ruleset>")));
        Assert.assertEquals(1, rs.getRules().size());
        Rule rule = rs.getRuleByName("NewName");
        Assert.assertNotNull(rule);
        Assert.assertNull(rs.getRuleByName("OldName"));
    }

    @Test
    public void testRuleSetReferencesADeprecatedRenamedRule() throws Exception {
        RuleSet rs = loadRuleSet(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ((("<ruleset name=\"test\">\n" + "  <description>ruleset desc</description>\n") + "     <rule ref=\"rulesets/dummy/basic.xml/OldNameOfDummyBasicMockRule\"/>") + "</ruleset>")));
        Assert.assertEquals(1, rs.getRules().size());
        Rule rule = rs.getRuleByName("OldNameOfDummyBasicMockRule");
        Assert.assertNotNull(rule);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testXPath() throws RuleSetNotFoundException {
        Rule r = loadFirstRule(RuleSetFactoryTest.XPATH);
        PropertyDescriptor<String> xpathProperty = ((PropertyDescriptor<String>) (r.getPropertyDescriptor("xpath")));
        Assert.assertNotNull("xpath property descriptor", xpathProperty);
        Assert.assertNotSame(r.getProperty(xpathProperty).indexOf(" //Block "), (-1));
    }

    @Test
    public void testFacadesOffByDefault() throws RuleSetNotFoundException {
        Rule r = loadFirstRule(RuleSetFactoryTest.XPATH);
        Assert.assertFalse(r.isDfa());
    }

    @Test
    public void testDFAFlag() throws RuleSetNotFoundException {
        Assert.assertTrue(loadFirstRule(RuleSetFactoryTest.DFA).isDfa());
    }

    @Test
    public void testExternalReferenceOverride() throws RuleSetNotFoundException {
        Rule r = loadFirstRule(RuleSetFactoryTest.REF_OVERRIDE);
        Assert.assertEquals("TestNameOverride", r.getName());
        Assert.assertEquals("Test message override", r.getMessage());
        Assert.assertEquals("Test description override", r.getDescription());
        Assert.assertEquals("Test that both example are stored", 2, r.getExamples().size());
        Assert.assertEquals("Test example override", r.getExamples().get(1));
        Assert.assertEquals(MEDIUM, r.getPriority());
        PropertyDescriptor<?> test2Descriptor = r.getPropertyDescriptor("test2");
        Assert.assertNotNull("test2 descriptor", test2Descriptor);
        Assert.assertEquals("override2", r.getProperty(test2Descriptor));
        PropertyDescriptor<?> test3Descriptor = r.getPropertyDescriptor("test3");
        Assert.assertNotNull("test3 descriptor", test3Descriptor);
        Assert.assertEquals("override3", r.getProperty(test3Descriptor));
    }

    @Test
    public void testExternalReferenceOverrideNonExistent() throws RuleSetNotFoundException {
        ex.expect(IllegalArgumentException.class);
        ex.expectMessage("Cannot set non-existent property 'test4' on Rule TestNameOverride");
        loadFirstRule(RuleSetFactoryTest.REF_OVERRIDE_NONEXISTENT);
    }

    @Test
    public void testReferenceInternalToInternal() throws RuleSetNotFoundException {
        RuleSet ruleSet = loadRuleSet(RuleSetFactoryTest.REF_INTERNAL_TO_INTERNAL);
        Rule rule = ruleSet.getRuleByName("MockRuleName");
        Assert.assertNotNull("Could not find Rule MockRuleName", rule);
        Rule ruleRef = ruleSet.getRuleByName("MockRuleNameRef");
        Assert.assertNotNull("Could not find Rule MockRuleNameRef", ruleRef);
    }

    @Test
    public void testReferenceInternalToInternalChain() throws RuleSetNotFoundException {
        RuleSet ruleSet = loadRuleSet(RuleSetFactoryTest.REF_INTERNAL_TO_INTERNAL_CHAIN);
        Rule rule = ruleSet.getRuleByName("MockRuleName");
        Assert.assertNotNull("Could not find Rule MockRuleName", rule);
        Rule ruleRef = ruleSet.getRuleByName("MockRuleNameRef");
        Assert.assertNotNull("Could not find Rule MockRuleNameRef", ruleRef);
        Rule ruleRefRef = ruleSet.getRuleByName("MockRuleNameRefRef");
        Assert.assertNotNull("Could not find Rule MockRuleNameRefRef", ruleRefRef);
    }

    @Test
    public void testReferenceInternalToExternal() throws RuleSetNotFoundException {
        RuleSet ruleSet = loadRuleSet(RuleSetFactoryTest.REF_INTERNAL_TO_EXTERNAL);
        Rule rule = ruleSet.getRuleByName("ExternalRefRuleName");
        Assert.assertNotNull("Could not find Rule ExternalRefRuleName", rule);
        Rule ruleRef = ruleSet.getRuleByName("ExternalRefRuleNameRef");
        Assert.assertNotNull("Could not find Rule ExternalRefRuleNameRef", ruleRef);
    }

    @Test
    public void testReferenceInternalToExternalChain() throws RuleSetNotFoundException {
        RuleSet ruleSet = loadRuleSet(RuleSetFactoryTest.REF_INTERNAL_TO_EXTERNAL_CHAIN);
        Rule rule = ruleSet.getRuleByName("ExternalRefRuleName");
        Assert.assertNotNull("Could not find Rule ExternalRefRuleName", rule);
        Rule ruleRef = ruleSet.getRuleByName("ExternalRefRuleNameRef");
        Assert.assertNotNull("Could not find Rule ExternalRefRuleNameRef", ruleRef);
        Rule ruleRefRef = ruleSet.getRuleByName("ExternalRefRuleNameRefRef");
        Assert.assertNotNull("Could not find Rule ExternalRefRuleNameRefRef", ruleRefRef);
    }

    @Test
    public void testReferencePriority() throws RuleSetNotFoundException {
        ResourceLoader rl = new ResourceLoader();
        RuleSetFactory rsf = new RuleSetFactory(rl, LOW, false, true);
        RuleSet ruleSet = rsf.createRuleSet(RuleSetFactoryTest.createRuleSetReferenceId(RuleSetFactoryTest.REF_INTERNAL_TO_INTERNAL_CHAIN));
        Assert.assertEquals("Number of Rules", 3, ruleSet.getRules().size());
        Assert.assertNotNull(ruleSet.getRuleByName("MockRuleName"));
        Assert.assertNotNull(ruleSet.getRuleByName("MockRuleNameRef"));
        Assert.assertNotNull(ruleSet.getRuleByName("MockRuleNameRefRef"));
        rsf = new RuleSetFactory(rl, MEDIUM_HIGH, false, true);
        ruleSet = rsf.createRuleSet(RuleSetFactoryTest.createRuleSetReferenceId(RuleSetFactoryTest.REF_INTERNAL_TO_INTERNAL_CHAIN));
        Assert.assertEquals("Number of Rules", 2, ruleSet.getRules().size());
        Assert.assertNotNull(ruleSet.getRuleByName("MockRuleNameRef"));
        Assert.assertNotNull(ruleSet.getRuleByName("MockRuleNameRefRef"));
        rsf = new RuleSetFactory(rl, HIGH, false, true);
        ruleSet = rsf.createRuleSet(RuleSetFactoryTest.createRuleSetReferenceId(RuleSetFactoryTest.REF_INTERNAL_TO_INTERNAL_CHAIN));
        Assert.assertEquals("Number of Rules", 1, ruleSet.getRules().size());
        Assert.assertNotNull(ruleSet.getRuleByName("MockRuleNameRefRef"));
        rsf = new RuleSetFactory(rl, LOW, false, true);
        ruleSet = rsf.createRuleSet(RuleSetFactoryTest.createRuleSetReferenceId(RuleSetFactoryTest.REF_INTERNAL_TO_EXTERNAL_CHAIN));
        Assert.assertEquals("Number of Rules", 3, ruleSet.getRules().size());
        Assert.assertNotNull(ruleSet.getRuleByName("ExternalRefRuleName"));
        Assert.assertNotNull(ruleSet.getRuleByName("ExternalRefRuleNameRef"));
        Assert.assertNotNull(ruleSet.getRuleByName("ExternalRefRuleNameRefRef"));
        rsf = new RuleSetFactory(rl, MEDIUM_HIGH, false, true);
        ruleSet = rsf.createRuleSet(RuleSetFactoryTest.createRuleSetReferenceId(RuleSetFactoryTest.REF_INTERNAL_TO_EXTERNAL_CHAIN));
        Assert.assertEquals("Number of Rules", 2, ruleSet.getRules().size());
        Assert.assertNotNull(ruleSet.getRuleByName("ExternalRefRuleNameRef"));
        Assert.assertNotNull(ruleSet.getRuleByName("ExternalRefRuleNameRefRef"));
        rsf = new RuleSetFactory(rl, HIGH, false, true);
        ruleSet = rsf.createRuleSet(RuleSetFactoryTest.createRuleSetReferenceId(RuleSetFactoryTest.REF_INTERNAL_TO_EXTERNAL_CHAIN));
        Assert.assertEquals("Number of Rules", 1, ruleSet.getRules().size());
        Assert.assertNotNull(ruleSet.getRuleByName("ExternalRefRuleNameRefRef"));
    }

    @Test
    public void testOverridePriorityLoadWithMinimum() throws RuleSetNotFoundException {
        RuleSetFactory rsf = new RuleSetFactory(new ResourceLoader(), MEDIUM_LOW, true, true);
        RuleSet ruleset = rsf.createRuleSet("net/sourceforge/pmd/rulesets/ruleset-minimum-priority.xml");
        // only one rule should remain, since we filter out the other rules by minimum priority
        Assert.assertEquals("Number of Rules", 1, ruleset.getRules().size());
        // Priority is overridden and applied, rule is missing
        Assert.assertNull(ruleset.getRuleByName("DummyBasicMockRule"));
        // that's the remaining rule
        Assert.assertNotNull(ruleset.getRuleByName("SampleXPathRule"));
        // now, load with default minimum priority
        rsf = new RuleSetFactory();
        ruleset = rsf.createRuleSet("net/sourceforge/pmd/rulesets/ruleset-minimum-priority.xml");
        Assert.assertEquals("Number of Rules", 2, ruleset.getRules().size());
        Rule dummyBasicMockRule = ruleset.getRuleByName("DummyBasicMockRule");
        Assert.assertEquals("Wrong Priority", LOW, dummyBasicMockRule.getPriority());
    }

    @Test
    public void testExcludeWithMinimumPriority() throws RuleSetNotFoundException {
        RuleSetFactory rsf = new RuleSetFactory(new ResourceLoader(), HIGH, true, true);
        RuleSet ruleset = rsf.createRuleSet("net/sourceforge/pmd/rulesets/ruleset-minimum-priority-exclusion.xml");
        // no rules should be loaded
        Assert.assertEquals("Number of Rules", 0, ruleset.getRules().size());
        // now, load with default minimum priority
        rsf = new RuleSetFactory();
        ruleset = rsf.createRuleSet("net/sourceforge/pmd/rulesets/ruleset-minimum-priority-exclusion.xml");
        // only one rule, we have excluded one...
        Assert.assertEquals("Number of Rules", 1, ruleset.getRules().size());
        // rule is excluded
        Assert.assertNull(ruleset.getRuleByName("DummyBasicMockRule"));
        // that's the remaining rule
        Assert.assertNotNull(ruleset.getRuleByName("SampleXPathRule"));
    }

    @Test
    public void testOverrideMessage() throws RuleSetNotFoundException {
        Rule r = loadFirstRule(RuleSetFactoryTest.REF_OVERRIDE_ORIGINAL_NAME);
        Assert.assertEquals("TestMessageOverride", r.getMessage());
    }

    @Test
    public void testOverrideMessageOneElem() throws RuleSetNotFoundException {
        Rule r = loadFirstRule(RuleSetFactoryTest.REF_OVERRIDE_ORIGINAL_NAME_ONE_ELEM);
        Assert.assertEquals("TestMessageOverride", r.getMessage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectExternalRef() throws IllegalArgumentException, RuleSetNotFoundException {
        loadFirstRule(RuleSetFactoryTest.REF_MISPELLED_XREF);
    }

    @Test
    public void testSetPriority() throws RuleSetNotFoundException {
        ResourceLoader rl = new ResourceLoader();
        RuleSetFactory rsf = new RuleSetFactory(rl, MEDIUM_HIGH, false, true);
        Assert.assertEquals(0, rsf.createRuleSet(RuleSetFactoryTest.createRuleSetReferenceId(RuleSetFactoryTest.SINGLE_RULE)).size());
        rsf = new RuleSetFactory(rl, MEDIUM_LOW, false, true);
        Assert.assertEquals(1, rsf.createRuleSet(RuleSetFactoryTest.createRuleSetReferenceId(RuleSetFactoryTest.SINGLE_RULE)).size());
    }

    @Test
    public void testLanguage() throws RuleSetNotFoundException {
        Rule r = loadFirstRule(RuleSetFactoryTest.LANGUAGE);
        Assert.assertEquals(LanguageRegistry.getLanguage(DummyLanguageModule.NAME), r.getLanguage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectLanguage() throws RuleSetNotFoundException {
        loadFirstRule(RuleSetFactoryTest.INCORRECT_LANGUAGE);
    }

    @Test
    public void testMinimumLanugageVersion() throws RuleSetNotFoundException {
        Rule r = loadFirstRule(RuleSetFactoryTest.MINIMUM_LANGUAGE_VERSION);
        Assert.assertEquals(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.4"), r.getMinimumLanguageVersion());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectMinimumLanugageVersion() throws RuleSetNotFoundException {
        loadFirstRule(RuleSetFactoryTest.INCORRECT_MINIMUM_LANGUAGE_VERSION);
    }

    @Test
    public void testMaximumLanugageVersion() throws RuleSetNotFoundException {
        Rule r = loadFirstRule(RuleSetFactoryTest.MAXIMUM_LANGUAGE_VERSION);
        Assert.assertEquals(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.7"), r.getMaximumLanguageVersion());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectMaximumLanugageVersion() throws RuleSetNotFoundException {
        loadFirstRule(RuleSetFactoryTest.INCORRECT_MAXIMUM_LANGUAGE_VERSION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvertedMinimumMaximumLanugageVersions() throws RuleSetNotFoundException {
        loadFirstRule(RuleSetFactoryTest.INVERTED_MINIMUM_MAXIMUM_LANGUAGE_VERSIONS);
    }

    @Test
    public void testDirectDeprecatedRule() throws RuleSetNotFoundException {
        Rule r = loadFirstRule(RuleSetFactoryTest.DIRECT_DEPRECATED_RULE);
        Assert.assertNotNull("Direct Deprecated Rule", r);
        Assert.assertTrue(r.isDeprecated());
    }

    @Test
    public void testReferenceToDeprecatedRule() throws RuleSetNotFoundException {
        Rule r = loadFirstRule(RuleSetFactoryTest.REFERENCE_TO_DEPRECATED_RULE);
        Assert.assertNotNull("Reference to Deprecated Rule", r);
        Assert.assertTrue("Rule Reference", (r instanceof RuleReference));
        Assert.assertFalse("Not deprecated", r.isDeprecated());
        Assert.assertTrue("Original Rule Deprecated", getRule().isDeprecated());
        Assert.assertEquals("Rule name", r.getName(), RuleSetFactoryTest.DEPRECATED_RULE_NAME);
    }

    @Test
    public void testRuleSetReferenceWithDeprecatedRule() throws RuleSetNotFoundException {
        RuleSet ruleSet = loadRuleSet(RuleSetFactoryTest.REFERENCE_TO_RULESET_WITH_DEPRECATED_RULE);
        Assert.assertNotNull("RuleSet", ruleSet);
        Assert.assertFalse("RuleSet empty", ruleSet.getRules().isEmpty());
        // No deprecated Rules should be loaded when loading an entire RuleSet
        // by reference - unless it contains only deprecated rules - then all rules would be added
        Rule r = ruleSet.getRuleByName(RuleSetFactoryTest.DEPRECATED_RULE_NAME);
        Assert.assertNull("Deprecated Rule Reference", r);
        for (Rule rule : ruleSet.getRules()) {
            Assert.assertFalse("Rule not deprecated", rule.isDeprecated());
        }
    }

    @Test
    public void testDeprecatedRuleSetReference() throws RuleSetNotFoundException {
        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        RuleSet ruleSet = ruleSetFactory.createRuleSet("net/sourceforge/pmd/rulesets/ruleset-deprecated.xml");
        Assert.assertEquals(2, ruleSet.getRules().size());
    }

    @Test
    public void testExternalReferences() throws RuleSetNotFoundException {
        RuleSet rs = loadRuleSet(RuleSetFactoryTest.EXTERNAL_REFERENCE_RULE_SET);
        Assert.assertEquals(1, rs.size());
        Assert.assertEquals(MockRule.class.getName(), rs.getRuleByName("MockRule").getRuleClass());
    }

    @Test
    public void testIncludeExcludePatterns() throws RuleSetNotFoundException {
        RuleSet ruleSet = loadRuleSet(RuleSetFactoryTest.INCLUDE_EXCLUDE_RULESET);
        Assert.assertNotNull("Include patterns", ruleSet.getIncludePatterns());
        Assert.assertEquals("Include patterns size", 2, ruleSet.getIncludePatterns().size());
        Assert.assertEquals("Include pattern #1", "include1", ruleSet.getIncludePatterns().get(0));
        Assert.assertEquals("Include pattern #2", "include2", ruleSet.getIncludePatterns().get(1));
        Assert.assertNotNull("Exclude patterns", ruleSet.getExcludePatterns());
        Assert.assertEquals("Exclude patterns size", 3, ruleSet.getExcludePatterns().size());
        Assert.assertEquals("Exclude pattern #1", "exclude1", ruleSet.getExcludePatterns().get(0));
        Assert.assertEquals("Exclude pattern #2", "exclude2", ruleSet.getExcludePatterns().get(1));
        Assert.assertEquals("Exclude pattern #3", "exclude3", ruleSet.getExcludePatterns().get(2));
    }

    /**
     * Rule reference can't be resolved - ref is used instead of class and the
     * class is old (pmd 4.3 and not pmd 5).
     *
     * @throws Exception
     * 		any error
     */
    @Test(expected = RuleSetNotFoundException.class)
    public void testBug1202() throws Exception {
        RuleSetReferenceId ref = RuleSetFactoryTest.createRuleSetReferenceId(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + (((((((("<ruleset>\n" + "  <rule ref=\"net.sourceforge.pmd.rules.XPathRule\">\n") + "    <priority>1</priority>\n") + "    <properties>\n") + "      <property name=\"xpath\" value=\"//TypeDeclaration\" />\n") + "      <property name=\"message\" value=\"Foo\" />\n") + "    </properties>\n") + "  </rule>\n") + "</ruleset>\n")));
        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        ruleSetFactory.createRuleSet(ref);
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1225/
     *
     * @throws Exception
     * 		any error
     */
    @Test
    public void testEmptyRuleSetFile() throws Exception {
        RuleSetReferenceId ref = RuleSetFactoryTest.createRuleSetReferenceId(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + (((((((("\n" + "<ruleset name=\"Custom ruleset\" xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n") + "    xmlns:xsi=\"http:www.w3.org/2001/XMLSchema-instance\"\n") + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n") + "    <description>PMD Ruleset.</description>\n") + "\n") + "    <exclude-pattern>.*Test.*</exclude-pattern>\n") + "\n") + "</ruleset>\n")));
        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        RuleSet ruleset = ruleSetFactory.createRuleSet(ref);
        Assert.assertEquals(0, ruleset.getRules().size());
    }

    /**
     * See https://github.com/pmd/pmd/issues/782
     * Empty ruleset should be interpreted as deprecated.
     *
     * @throws Exception
     * 		any error
     */
    @Test
    public void testEmptyRuleSetReferencedShouldNotBeDeprecated() throws Exception {
        RuleSetReferenceId ref = RuleSetFactoryTest.createRuleSetReferenceId(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ((((((("\n" + "<ruleset name=\"Custom ruleset\" xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n") + "    xmlns:xsi=\"http:www.w3.org/2001/XMLSchema-instance\"\n") + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n") + "    <description>Ruleset which references a empty ruleset</description>\n") + "\n") + "    <rule ref=\"rulesets/dummy/empty-ruleset.xml\" />\n") + "</ruleset>\n")));
        RuleSetFactory ruleSetFactory = new RuleSetFactory(new ResourceLoader(), LOW, true, true);
        RuleSet ruleset = ruleSetFactory.createRuleSet(ref);
        Assert.assertEquals(0, ruleset.getRules().size());
        Assert.assertTrue(logging.getLog().isEmpty());
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1231/
     *
     * @throws Exception
     * 		any error
     */
    @Test(expected = IllegalArgumentException.class)
    public void testWrongRuleNameReferenced() throws Exception {
        RuleSetReferenceId ref = RuleSetFactoryTest.createRuleSetReferenceId(("<?xml version=\"1.0\"?>\n" + (((((("<ruleset name=\"Custom ruleset for tests\"\n" + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n") + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n") + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n") + "  <description>Custom ruleset for tests</description>\n") + "  <rule ref=\"net/sourceforge/pmd/TestRuleset1.xml/ThisRuleDoesNotExist\"/>\n") + "</ruleset>\n")));
        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        ruleSetFactory.createRuleSet(ref);
    }

    /**
     * Unit test for #1312 see https://sourceforge.net/p/pmd/bugs/1312/
     *
     * @throws Exception
     * 		any error
     */
    @Test
    public void testRuleReferenceWithNameOverridden() throws Exception {
        RuleSetReferenceId ref = RuleSetFactoryTest.createRuleSetReferenceId(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + (((((((((("<ruleset xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n" + "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n") + "         name=\"pmd-eclipse\"\n") + "         xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n") + "   <description>PMD Plugin preferences rule set</description>\n") + "\n") + "<rule name=\"OverriddenDummyBasicMockRule\"\n") + "    ref=\"rulesets/dummy/basic.xml/DummyBasicMockRule\">\n") + "</rule>\n") + "\n") + "</ruleset>")));
        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        RuleSet rs = ruleSetFactory.createRuleSet(ref);
        Rule r = rs.getRules().toArray(new Rule[1])[0];
        Assert.assertEquals("OverriddenDummyBasicMockRule", r.getName());
        RuleReference ruleRef = ((RuleReference) (r));
        Assert.assertEquals("DummyBasicMockRule", ruleRef.getRule().getName());
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1231/
     *
     * @throws Exception
     * 		any error
     */
    @Test(expected = IllegalArgumentException.class)
    public void testWrongRuleNameExcluded() throws Exception {
        RuleSetReferenceId ref = RuleSetFactoryTest.createRuleSetReferenceId(("<?xml version=\"1.0\"?>\n" + (((((((("<ruleset name=\"Custom ruleset for tests\"\n" + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n") + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n") + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n") + "  <description>Custom ruleset for tests</description>\n") + "  <rule ref=\"net/sourceforge/pmd/TestRuleset1.xml\">\n") + "    <exclude name=\"ThisRuleDoesNotExist\"/>\n") + "  </rule>\n") + "</ruleset>\n")));
        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        ruleSetFactory.createRuleSet(ref);
    }

    /**
     * This unit test manifests the current behavior - which might change in the
     * future. See #1537.
     *
     * Currently, if a ruleset is imported twice, the excludes of the first
     * import are ignored. Duplicated rules are silently ignored.
     *
     * @throws Exception
     * 		any error
     * @see <a href="https://sourceforge.net/p/pmd/bugs/1537/">#1537 Implement
    strict ruleset parsing</a>
     * @see <a href=
    "http://stackoverflow.com/questions/40299075/custom-pmd-ruleset-not-working">stackoverflow
    - custom ruleset not working</a>
     */
    @Test
    public void testExcludeAndImportTwice() throws Exception {
        RuleSetReferenceId ref1 = RuleSetFactoryTest.createRuleSetReferenceId(("<?xml version=\"1.0\"?>\n" + (((((((("<ruleset name=\"Custom ruleset for tests\"\n" + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n") + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n") + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n") + "  <description>Custom ruleset for tests</description>\n") + "  <rule ref=\"rulesets/dummy/basic.xml\">\n") + "    <exclude name=\"DummyBasicMockRule\"/>\n") + "  </rule>\n") + "</ruleset>\n")));
        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        RuleSet ruleset = ruleSetFactory.createRuleSet(ref1);
        Assert.assertNull(ruleset.getRuleByName("DummyBasicMockRule"));
        RuleSetReferenceId ref2 = RuleSetFactoryTest.createRuleSetReferenceId(("<?xml version=\"1.0\"?>\n" + ((((((((("<ruleset name=\"Custom ruleset for tests\"\n" + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n") + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n") + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n") + "  <description>Custom ruleset for tests</description>\n") + "  <rule ref=\"rulesets/dummy/basic.xml\">\n") + "    <exclude name=\"DummyBasicMockRule\"/>\n") + "  </rule>\n") + "  <rule ref=\"rulesets/dummy/basic.xml\"/>\n") + "</ruleset>\n")));
        RuleSetFactory ruleSetFactory2 = new RuleSetFactory();
        RuleSet ruleset2 = ruleSetFactory2.createRuleSet(ref2);
        Assert.assertNotNull(ruleset2.getRuleByName("DummyBasicMockRule"));
        RuleSetReferenceId ref3 = RuleSetFactoryTest.createRuleSetReferenceId(("<?xml version=\"1.0\"?>\n" + ((((((((("<ruleset name=\"Custom ruleset for tests\"\n" + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n") + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n") + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n") + "  <description>Custom ruleset for tests</description>\n") + "  <rule ref=\"rulesets/dummy/basic.xml\"/>\n") + "  <rule ref=\"rulesets/dummy/basic.xml\">\n") + "    <exclude name=\"DummyBasicMockRule\"/>\n") + "  </rule>\n") + "</ruleset>\n")));
        RuleSetFactory ruleSetFactory3 = new RuleSetFactory();
        RuleSet ruleset3 = ruleSetFactory3.createRuleSet(ref3);
        Assert.assertNotNull(ruleset3.getRuleByName("DummyBasicMockRule"));
    }

    @Rule
    public JavaUtilLoggingRule logging = new JavaUtilLoggingRule(RuleSetFactory.class.getName());

    @Test
    public void testMissingRuleSetNameIsWarning() throws Exception {
        RuleSetReferenceId ref = RuleSetFactoryTest.createRuleSetReferenceId(("<?xml version=\"1.0\"?>\n" + (((((("<ruleset \n" + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n") + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n") + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n") + "  <description>Custom ruleset for tests</description>\n") + "  <rule ref=\"rulesets/dummy/basic.xml\"/>\n") + "  </ruleset>\n")));
        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        ruleSetFactory.createRuleSet(ref);
        Assert.assertTrue(logging.getLog().contains("RuleSet name is missing."));
    }

    @Test
    public void testMissingRuleSetDescriptionIsWarning() throws Exception {
        RuleSetReferenceId ref = RuleSetFactoryTest.createRuleSetReferenceId(("<?xml version=\"1.0\"?>\n" + ((((("<ruleset name=\"then name\"\n" + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n") + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n") + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n") + "  <rule ref=\"rulesets/dummy/basic.xml\"/>\n") + "  </ruleset>\n")));
        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        ruleSetFactory.createRuleSet(ref);
        Assert.assertTrue(logging.getLog().contains("RuleSet description is missing."));
    }

    private static final String REF_OVERRIDE_ORIGINAL_NAME = ((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + " <description>testdesc</description>") + (EOL)) + " <rule ") + (EOL)) + "  ref=\"net/sourceforge/pmd/TestRuleset1.xml/MockRule1\" message=\"TestMessageOverride\"> ") + (EOL)) + " </rule>") + (EOL)) + "</ruleset>";

    private static final String REF_MISPELLED_XREF = ((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + " <description>testdesc</description>") + (EOL)) + " <rule ") + (EOL)) + "  ref=\"net/sourceforge/pmd/TestRuleset1.xml/FooMockRule1\"> ") + (EOL)) + " </rule>") + (EOL)) + "</ruleset>";

    private static final String REF_OVERRIDE_ORIGINAL_NAME_ONE_ELEM = ((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + " <description>testdesc</description>") + (EOL)) + " <rule ref=\"net/sourceforge/pmd/TestRuleset1.xml/MockRule1\" message=\"TestMessageOverride\"/> ") + (EOL)) + "</ruleset>";

    private static final String REF_OVERRIDE = ((((((((((((((((((((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + " <description>testdesc</description>") + (EOL)) + " <rule ") + (EOL)) + "  ref=\"net/sourceforge/pmd/TestRuleset1.xml/MockRule4\" ") + (EOL)) + "  name=\"TestNameOverride\" ") + (EOL)) + "  message=\"Test message override\"> ") + (EOL)) + "  <description>Test description override</description>") + (EOL)) + "  <example>Test example override</example>") + (EOL)) + "  <priority>3</priority>") + (EOL)) + "  <properties>") + (EOL)) + "   <property name=\"test2\" description=\"test2\" type=\"String\" value=\"override2\"/>") + (EOL)) + "   <property name=\"test3\" type=\"String\" description=\"test3\"><value>override3</value></property>") + // + PMD.EOL + "   <property name=\"test4\" description=\"test4\" type=\"String\" value=\"new property\"/>" // Nonsense
    (EOL)) + "  </properties>") + (EOL)) + " </rule>") + (EOL)) + "</ruleset>";

    private static final String REF_OVERRIDE_NONEXISTENT = ((((((((((((((((((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + " <description>testdesc</description>") + (EOL)) + " <rule ") + (EOL)) + "  ref=\"net/sourceforge/pmd/TestRuleset1.xml/MockRule4\" ") + (EOL)) + "  name=\"TestNameOverride\" ") + (EOL)) + "  message=\"Test message override\"> ") + (EOL)) + "  <description>Test description override</description>") + (EOL)) + "  <example>Test example override</example>") + (EOL)) + "  <priority>3</priority>") + (EOL)) + "  <properties>") + (EOL)) + "   <property name=\"test4\" description=\"test4\" type=\"String\" value=\"new property\"/>") + (EOL))// inexistent property
     + "  </properties>") + (EOL)) + " </rule>") + (EOL)) + "</ruleset>";

    private static final String REF_INTERNAL_TO_INTERNAL = (((((((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + " <description>testdesc</description>") + (EOL)) + "<rule ") + (EOL)) + "name=\"MockRuleName\" ") + (EOL)) + "message=\"avoid the mock rule\" ") + (EOL)) + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">") + (EOL)) + "</rule>") + " <rule ref=\"MockRuleName\" name=\"MockRuleNameRef\"/> ") + (EOL)) + "</ruleset>";

    private static final String REF_INTERNAL_TO_INTERNAL_CHAIN = (((((((((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + " <description>testdesc</description>") + (EOL)) + "<rule ") + (EOL)) + "name=\"MockRuleName\" ") + (EOL)) + "message=\"avoid the mock rule\" ") + (EOL)) + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">") + (EOL)) + "</rule>") + " <rule ref=\"MockRuleName\" name=\"MockRuleNameRef\"><priority>2</priority></rule> ") + (EOL)) + " <rule ref=\"MockRuleNameRef\" name=\"MockRuleNameRefRef\"><priority>1</priority></rule> ") + (EOL)) + "</ruleset>";

    private static final String REF_INTERNAL_TO_EXTERNAL = ((((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + " <description>testdesc</description>") + (EOL)) + "<rule ") + (EOL)) + "name=\"ExternalRefRuleName\" ") + (EOL)) + "ref=\"net/sourceforge/pmd/TestRuleset1.xml/MockRule1\"/>") + (EOL)) + " <rule ref=\"ExternalRefRuleName\" name=\"ExternalRefRuleNameRef\"/> ") + (EOL)) + "</ruleset>";

    private static final String REF_INTERNAL_TO_EXTERNAL_CHAIN = ((((((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + " <description>testdesc</description>") + (EOL)) + "<rule ") + (EOL)) + "name=\"ExternalRefRuleName\" ") + (EOL)) + "ref=\"net/sourceforge/pmd/TestRuleset2.xml/TestRule\"/>") + (EOL)) + " <rule ref=\"ExternalRefRuleName\" name=\"ExternalRefRuleNameRef\"><priority>2</priority></rule> ") + (EOL)) + " <rule ref=\"ExternalRefRuleNameRef\" name=\"ExternalRefRuleNameRefRef\"><priority>1</priority></rule> ") + (EOL)) + "</ruleset>";

    private static final String EMPTY_RULESET = ((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + "<description>testdesc</description>") + (EOL)) + "</ruleset>";

    private static final String SINGLE_RULE = (((((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + "<description>testdesc</description>") + (EOL)) + "<rule ") + (EOL)) + "name=\"MockRuleName\" ") + (EOL)) + "message=\"avoid the mock rule\" ") + (EOL)) + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">") + "<priority>3</priority>") + (EOL)) + "</rule></ruleset>";

    private static final String MULTIPLE_RULES = ((((((((((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + "<description>testdesc</description>") + (EOL)) + "<rule name=\"MockRuleName1\" ") + (EOL)) + "message=\"avoid the mock rule\" ") + (EOL)) + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">") + (EOL)) + "</rule>") + (EOL)) + "<rule name=\"MockRuleName2\" ") + (EOL)) + "message=\"avoid the mock rule\" ") + (EOL)) + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">") + (EOL)) + "</rule></ruleset>";

    private static final String PROPERTIES = ((((((((((((((((((((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + "<description>testdesc</description>") + (EOL)) + "<rule name=\"MockRuleName\" ") + (EOL)) + "message=\"avoid the mock rule\" ") + (EOL)) + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">") + (EOL)) + "<description>testdesc2</description>") + (EOL)) + "<properties>") + (EOL)) + "<property name=\"fooBoolean\" description=\"test\" type=\"Boolean\" value=\"true\" />") + (EOL)) + "<property name=\"fooChar\" description=\"test\" type=\"Character\" value=\"B\" />") + (EOL)) + "<property name=\"fooInt\" description=\"test\" type=\"Integer\" min=\"1\" max=\"10\" value=\"3\" />") + (EOL)) + "<property name=\"fooFloat\" description=\"test\" type=\"Float\" min=\"1.0\" max=\"1.0\" value=\"1.0\"  />") + (EOL)) + "<property name=\"fooDouble\" description=\"test\" type=\"Double\" min=\"1.0\" max=\"9.0\" value=\"3.0\"  />") + (EOL)) + "<property name=\"fooString\" description=\"test\" type=\"String\" value=\"bar\" />") + (EOL)) + "</properties>") + (EOL)) + "</rule></ruleset>";

    private static final String XPATH = ((((((((((((((((((((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + "<description>testdesc</description>") + (EOL)) + "<rule name=\"MockRuleName\" ") + (EOL)) + "message=\"avoid the mock rule\" ") + (EOL)) + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">") + "<priority>3</priority>") + (EOL)) + (EOL)) + "<description>testdesc2</description>") + (EOL)) + "<properties>") + (EOL)) + "<property name=\"xpath\" description=\"test\" type=\"String\">") + (EOL)) + "<value>") + (EOL)) + "<![CDATA[ //Block ]]>") + (EOL)) + "</value>") + (EOL)) + "</property>") + (EOL)) + "</properties>") + (EOL)) + "</rule></ruleset>";

    private static final String PRIORITY = (((((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + "<description>testdesc</description>") + (EOL)) + "<rule ") + (EOL)) + "name=\"MockRuleName\" ") + (EOL)) + "message=\"avoid the mock rule\" ") + (EOL)) + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">") + "<priority>3</priority>") + (EOL)) + "</rule></ruleset>";

    private static final String LANGUAGE = ((((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + "<description>testdesc</description>") + (EOL)) + "<rule ") + (EOL)) + "name=\"MockRuleName\" ") + (EOL)) + "message=\"avoid the mock rule\" ") + (EOL)) + "class=\"net.sourceforge.pmd.lang.rule.MockRule\" language=\"dummy\">") + (EOL)) + "</rule></ruleset>";

    private static final String INCORRECT_LANGUAGE = ((((((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + "<description>testdesc</description>") + (EOL)) + "<rule ") + (EOL)) + "name=\"MockRuleName\" ") + (EOL)) + "message=\"avoid the mock rule\" ") + (EOL)) + "class=\"net.sourceforge.pmd.lang.rule.MockRule\"") + (EOL)) + " language=\"bogus\">") + (EOL)) + "</rule></ruleset>";

    private static final String MINIMUM_LANGUAGE_VERSION = ((((((((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + "<description>testdesc</description>") + (EOL)) + "<rule ") + (EOL)) + "name=\"MockRuleName\" ") + (EOL)) + "message=\"avoid the mock rule\" ") + (EOL)) + "class=\"net.sourceforge.pmd.lang.rule.MockRule\"") + (EOL)) + " language=\"dummy\"") + (EOL)) + " minimumLanguageVersion=\"1.4\">") + (EOL)) + "</rule></ruleset>";

    private static final String INCORRECT_MINIMUM_LANGUAGE_VERSION = ((((((((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + "<description>testdesc</description>") + (EOL)) + "<rule ") + (EOL)) + "name=\"MockRuleName\" ") + (EOL)) + "message=\"avoid the mock rule\" ") + (EOL)) + "class=\"net.sourceforge.pmd.lang.rule.MockRule\"") + (EOL)) + " language=\"dummy\"") + (EOL)) + " minimumLanguageVersion=\"bogus\">") + (EOL)) + "</rule></ruleset>";

    private static final String MAXIMUM_LANGUAGE_VERSION = ((((((((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + "<description>testdesc</description>") + (EOL)) + "<rule ") + (EOL)) + "name=\"MockRuleName\" ") + (EOL)) + "message=\"avoid the mock rule\" ") + (EOL)) + "class=\"net.sourceforge.pmd.lang.rule.MockRule\"") + (EOL)) + " language=\"dummy\"") + (EOL)) + " maximumLanguageVersion=\"1.7\">") + (EOL)) + "</rule></ruleset>";

    private static final String INCORRECT_MAXIMUM_LANGUAGE_VERSION = ((((((((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + "<description>testdesc</description>") + (EOL)) + "<rule ") + (EOL)) + "name=\"MockRuleName\" ") + (EOL)) + "message=\"avoid the mock rule\" ") + (EOL)) + "class=\"net.sourceforge.pmd.lang.rule.MockRule\"") + (EOL)) + " language=\"dummy\"") + (EOL)) + " maximumLanguageVersion=\"bogus\">") + (EOL)) + "</rule></ruleset>";

    private static final String INVERTED_MINIMUM_MAXIMUM_LANGUAGE_VERSIONS = ((((((((((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + "<description>testdesc</description>") + (EOL)) + "<rule ") + (EOL)) + "name=\"MockRuleName\" ") + (EOL)) + "message=\"avoid the mock rule\" ") + (EOL)) + "class=\"net.sourceforge.pmd.lang.rule.MockRule\" ") + (EOL)) + "language=\"dummy\"") + (EOL)) + " minimumLanguageVersion=\"1.7\"") + (EOL)) + "maximumLanguageVersion=\"1.4\">") + (EOL)) + "</rule></ruleset>";

    private static final String DIRECT_DEPRECATED_RULE = ((((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + "<description>testdesc</description>") + (EOL)) + "<rule ") + (EOL)) + "name=\"MockRuleName\" ") + (EOL)) + "message=\"avoid the mock rule\" ") + (EOL)) + "class=\"net.sourceforge.pmd.lang.rule.MockRule\" deprecated=\"true\">") + (EOL)) + "</rule></ruleset>";

    // Note: Update this RuleSet name to a different RuleSet with deprecated
    // Rules when the Rules are finally removed.
    private static final String DEPRECATED_RULE_RULESET_NAME = "net/sourceforge/pmd/TestRuleset1.xml";

    // Note: Update this Rule name to a different deprecated Rule when the one
    // listed here is finally removed.
    private static final String DEPRECATED_RULE_NAME = "MockRule3";

    private static final String REFERENCE_TO_DEPRECATED_RULE = ((((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + "<description>testdesc</description>") + (EOL)) + "<rule ") + (EOL)) + "ref=\"") + (RuleSetFactoryTest.DEPRECATED_RULE_RULESET_NAME)) + "/") + (RuleSetFactoryTest.DEPRECATED_RULE_NAME)) + "\">") + (EOL)) + "</rule></ruleset>";

    private static final String REFERENCE_TO_RULESET_WITH_DEPRECATED_RULE = ((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + "<description>testdesc</description>") + (EOL)) + "<rule ") + (EOL)) + "ref=\"") + (RuleSetFactoryTest.DEPRECATED_RULE_RULESET_NAME)) + "\">") + (EOL)) + "</rule></ruleset>";

    private static final String DFA = (((((((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + "<description>testdesc</description>") + (EOL)) + "<rule ") + (EOL)) + "name=\"MockRuleName\" ") + (EOL)) + "message=\"avoid the mock rule\" ") + (EOL)) + "dfa=\"true\" ") + (EOL)) + "class=\"net.sourceforge.pmd.lang.rule.MockRule\">") + "<priority>3</priority>") + (EOL)) + "</rule></ruleset>";

    private static final String INCLUDE_EXCLUDE_RULESET = ((((((((((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + "<description>testdesc</description>") + (EOL)) + "<include-pattern>include1</include-pattern>") + (EOL)) + "<include-pattern>include2</include-pattern>") + (EOL)) + "<exclude-pattern>exclude1</exclude-pattern>") + (EOL)) + "<exclude-pattern>exclude2</exclude-pattern>") + (EOL)) + "<exclude-pattern>exclude3</exclude-pattern>") + (EOL)) + "</ruleset>";

    private static final String EXTERNAL_REFERENCE_RULE_SET = ((((((("<?xml version=\"1.0\"?>" + (EOL)) + "<ruleset name=\"test\">") + (EOL)) + "<description>testdesc</description>") + (EOL)) + "<rule ref=\"net/sourceforge/pmd/external-reference-ruleset.xml/MockRule\"/>") + (EOL)) + "</ruleset>";
}

