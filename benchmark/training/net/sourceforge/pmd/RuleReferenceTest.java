/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;


import RulePriority.HIGH;
import RulePriority.MEDIUM_HIGH;
import net.sourceforge.pmd.lang.Dummy2LanguageModule;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.properties.StringProperty;
import org.junit.Assert;
import org.junit.Test;


public class RuleReferenceTest {
    @Test
    public void testRuleSetReference() {
        RuleReference ruleReference = new RuleReference();
        RuleSetReference ruleSetReference = new RuleSetReference("somename");
        ruleReference.setRuleSetReference(ruleSetReference);
        Assert.assertEquals("Not same rule set reference", ruleSetReference, ruleReference.getRuleSetReference());
    }

    @Test
    public void testOverride() {
        final StringProperty PROPERTY1_DESCRIPTOR = new StringProperty("property1", "Test property", null, 0.0F);
        MockRule rule = new MockRule();
        rule.definePropertyDescriptor(PROPERTY1_DESCRIPTOR);
        rule.setLanguage(LanguageRegistry.getLanguage(Dummy2LanguageModule.NAME));
        rule.setName("name1");
        rule.setProperty(PROPERTY1_DESCRIPTOR, "value1");
        rule.setMessage("message1");
        rule.setDescription("description1");
        rule.addExample("example1");
        rule.setExternalInfoUrl("externalInfoUrl1");
        rule.setPriority(HIGH);
        final StringProperty PROPERTY2_DESCRIPTOR = new StringProperty("property2", "Test property", null, 0.0F);
        RuleReference ruleReference = new RuleReference();
        ruleReference.setRule(rule);
        ruleReference.definePropertyDescriptor(PROPERTY2_DESCRIPTOR);
        ruleReference.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        ruleReference.setMinimumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.3"));
        ruleReference.setMaximumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.7"));
        ruleReference.setDeprecated(true);
        ruleReference.setName("name2");
        ruleReference.setProperty(PROPERTY1_DESCRIPTOR, "value2");
        ruleReference.setProperty(PROPERTY2_DESCRIPTOR, "value3");
        ruleReference.setMessage("message2");
        ruleReference.setDescription("description2");
        ruleReference.addExample("example2");
        ruleReference.setExternalInfoUrl("externalInfoUrl2");
        ruleReference.setPriority(MEDIUM_HIGH);
        validateOverridenValues(PROPERTY1_DESCRIPTOR, PROPERTY2_DESCRIPTOR, ruleReference);
    }

    @Test
    public void testDeepCopyOverride() {
        final StringProperty PROPERTY1_DESCRIPTOR = new StringProperty("property1", "Test property", null, 0.0F);
        MockRule rule = new MockRule();
        rule.definePropertyDescriptor(PROPERTY1_DESCRIPTOR);
        rule.setLanguage(LanguageRegistry.getLanguage(Dummy2LanguageModule.NAME));
        rule.setName("name1");
        rule.setProperty(PROPERTY1_DESCRIPTOR, "value1");
        rule.setMessage("message1");
        rule.setDescription("description1");
        rule.addExample("example1");
        rule.setExternalInfoUrl("externalInfoUrl1");
        rule.setPriority(HIGH);
        final StringProperty PROPERTY2_DESCRIPTOR = new StringProperty("property2", "Test property", null, 0.0F);
        RuleReference ruleReference = new RuleReference();
        ruleReference.setRule(rule);
        ruleReference.definePropertyDescriptor(PROPERTY2_DESCRIPTOR);
        ruleReference.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        ruleReference.setMinimumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.3"));
        ruleReference.setMaximumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.7"));
        ruleReference.setDeprecated(true);
        ruleReference.setName("name2");
        ruleReference.setProperty(PROPERTY1_DESCRIPTOR, "value2");
        ruleReference.setProperty(PROPERTY2_DESCRIPTOR, "value3");
        ruleReference.setMessage("message2");
        ruleReference.setDescription("description2");
        ruleReference.addExample("example2");
        ruleReference.setExternalInfoUrl("externalInfoUrl2");
        ruleReference.setPriority(MEDIUM_HIGH);
        validateOverridenValues(PROPERTY1_DESCRIPTOR, PROPERTY2_DESCRIPTOR, ((RuleReference) (ruleReference.deepCopy())));
    }

    @Test
    public void testNotOverride() {
        final StringProperty PROPERTY1_DESCRIPTOR = new StringProperty("property1", "Test property", null, 0.0F);
        MockRule rule = new MockRule();
        rule.definePropertyDescriptor(PROPERTY1_DESCRIPTOR);
        rule.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        rule.setMinimumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.3"));
        rule.setMaximumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.7"));
        rule.setName("name1");
        rule.setProperty(PROPERTY1_DESCRIPTOR, "value1");
        rule.setMessage("message1");
        rule.setDescription("description1");
        rule.addExample("example1");
        rule.setExternalInfoUrl("externalInfoUrl1");
        rule.setPriority(HIGH);
        RuleReference ruleReference = new RuleReference();
        ruleReference.setRule(rule);
        ruleReference.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        ruleReference.setMinimumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.3"));
        ruleReference.setMaximumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.7"));
        ruleReference.setDeprecated(false);
        ruleReference.setName("name1");
        ruleReference.setProperty(PROPERTY1_DESCRIPTOR, "value1");
        ruleReference.setMessage("message1");
        ruleReference.setDescription("description1");
        ruleReference.addExample("example1");
        ruleReference.setExternalInfoUrl("externalInfoUrl1");
        ruleReference.setPriority(HIGH);
        Assert.assertEquals("Override failed", LanguageRegistry.getLanguage(DummyLanguageModule.NAME), ruleReference.getLanguage());
        Assert.assertNull("Override failed", ruleReference.getOverriddenLanguage());
        Assert.assertEquals("Override failed", LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.3"), ruleReference.getMinimumLanguageVersion());
        Assert.assertNull("Override failed", ruleReference.getOverriddenMinimumLanguageVersion());
        Assert.assertEquals("Override failed", LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.7"), ruleReference.getMaximumLanguageVersion());
        Assert.assertNull("Override failed", ruleReference.getOverriddenMaximumLanguageVersion());
        Assert.assertEquals("Override failed", false, ruleReference.isDeprecated());
        Assert.assertNull("Override failed", ruleReference.isOverriddenDeprecated());
        Assert.assertEquals("Override failed", "name1", ruleReference.getName());
        Assert.assertNull("Override failed", ruleReference.getOverriddenName());
        Assert.assertEquals("Override failed", "value1", ruleReference.getProperty(PROPERTY1_DESCRIPTOR));
        Assert.assertEquals("Override failed", "message1", ruleReference.getMessage());
        Assert.assertNull("Override failed", ruleReference.getOverriddenMessage());
        Assert.assertEquals("Override failed", "description1", ruleReference.getDescription());
        Assert.assertNull("Override failed", ruleReference.getOverriddenDescription());
        Assert.assertEquals("Override failed", 1, ruleReference.getExamples().size());
        Assert.assertEquals("Override failed", "example1", ruleReference.getExamples().get(0));
        Assert.assertNull("Override failed", ruleReference.getOverriddenExamples());
        Assert.assertEquals("Override failed", "externalInfoUrl1", ruleReference.getExternalInfoUrl());
        Assert.assertNull("Override failed", ruleReference.getOverriddenExternalInfoUrl());
        Assert.assertEquals("Override failed", HIGH, ruleReference.getPriority());
        Assert.assertNull("Override failed", ruleReference.getOverriddenPriority());
    }
}

