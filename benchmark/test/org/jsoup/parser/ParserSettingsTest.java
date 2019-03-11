package org.jsoup.parser;


import org.jsoup.MultiLocaleRule;
import org.jsoup.nodes.Attributes;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class ParserSettingsTest {
    @Rule
    public MultiLocaleRule rule = new MultiLocaleRule();

    @Test
    @MultiLocaleRule.MultiLocaleTest
    public void caseSupport() {
        ParseSettings bothOn = new ParseSettings(true, true);
        ParseSettings bothOff = new ParseSettings(false, false);
        ParseSettings tagOn = new ParseSettings(true, false);
        ParseSettings attrOn = new ParseSettings(false, true);
        Assert.assertEquals("IMG", bothOn.normalizeTag("IMG"));
        Assert.assertEquals("ID", bothOn.normalizeAttribute("ID"));
        Assert.assertEquals("img", bothOff.normalizeTag("IMG"));
        Assert.assertEquals("id", bothOff.normalizeAttribute("ID"));
        Assert.assertEquals("IMG", tagOn.normalizeTag("IMG"));
        Assert.assertEquals("id", tagOn.normalizeAttribute("ID"));
        Assert.assertEquals("img", attrOn.normalizeTag("IMG"));
        Assert.assertEquals("ID", attrOn.normalizeAttribute("ID"));
    }

    @Test
    @MultiLocaleRule.MultiLocaleTest
    public void attributeCaseNormalization() throws Exception {
        ParseSettings parseSettings = new ParseSettings(false, false);
        String normalizedAttribute = parseSettings.normalizeAttribute("HIDDEN");
        Assert.assertEquals("hidden", normalizedAttribute);
    }

    @Test
    @MultiLocaleRule.MultiLocaleTest
    public void attributesCaseNormalization() throws Exception {
        ParseSettings parseSettings = new ParseSettings(false, false);
        Attributes attributes = new Attributes();
        attributes.put("ITEM", "1");
        Attributes normalizedAttributes = parseSettings.normalizeAttributes(attributes);
        Assert.assertEquals("item", normalizedAttributes.asList().get(0).getKey());
    }
}

