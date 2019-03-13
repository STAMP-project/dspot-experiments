/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2012 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool;


import org.junit.Assert;
import org.junit.Test;


public class LanguageTest {
    @Test
    public void testRuleFileName() {
        Assert.assertEquals("[/org/languagetool/rules/en/grammar.xml, /org/languagetool/rules/en/en-GB/grammar.xml]", new BritishEnglish().getRuleFileNames().toString());
        Assert.assertEquals("[/org/languagetool/rules/en/grammar.xml, /org/languagetool/rules/en/en-US/grammar.xml]", new AmericanEnglish().getRuleFileNames().toString());
        Assert.assertEquals("[/org/languagetool/rules/en/grammar.xml]", new English().getRuleFileNames().toString());
        Assert.assertEquals("[/org/languagetool/rules/de/grammar.xml]", new German().getRuleFileNames().toString());
    }

    @Test
    public void testGetTranslatedName() {
        Assert.assertEquals("English", new English().getTranslatedName(TestTools.getMessages("en")));
        Assert.assertEquals("English (British)", new BritishEnglish().getTranslatedName(TestTools.getMessages("en")));
        Assert.assertEquals("Englisch", new English().getTranslatedName(TestTools.getMessages("de")));
        Assert.assertEquals("Englisch (Gro?britannien)", new BritishEnglish().getTranslatedName(TestTools.getMessages("de")));
        Assert.assertEquals("Deutsch", new German().getTranslatedName(TestTools.getMessages("de")));
        Assert.assertEquals("Deutsch (Schweiz)", new SwissGerman().getTranslatedName(TestTools.getMessages("de")));
    }

    @Test
    public void testGetShortNameWithVariant() {
        Assert.assertEquals("en-US", new AmericanEnglish().getShortCodeWithCountryAndVariant());
        Assert.assertEquals("de", new German().getShortCodeWithCountryAndVariant());
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(new GermanyGerman(), new GermanyGerman());
        Assert.assertNotEquals(new AustrianGerman(), new GermanyGerman());
        Assert.assertNotEquals(new AustrianGerman(), new German());
    }

    @Test
    public void testEqualsConsiderVariantIfSpecified() {
        // every language equals itself:
        Assert.assertTrue(new German().equalsConsiderVariantsIfSpecified(new German()));
        Assert.assertTrue(new GermanyGerman().equalsConsiderVariantsIfSpecified(new GermanyGerman()));
        Assert.assertTrue(new English().equalsConsiderVariantsIfSpecified(new English()));
        Assert.assertTrue(new AmericanEnglish().equalsConsiderVariantsIfSpecified(new AmericanEnglish()));
        // equal if variant is the same, but only if specified:
        Assert.assertTrue(new AmericanEnglish().equalsConsiderVariantsIfSpecified(new English()));
        Assert.assertTrue(new English().equalsConsiderVariantsIfSpecified(new AmericanEnglish()));
        Assert.assertFalse(new AmericanEnglish().equalsConsiderVariantsIfSpecified(new BritishEnglish()));
        Assert.assertFalse(new English().equalsConsiderVariantsIfSpecified(new German()));
    }
}

