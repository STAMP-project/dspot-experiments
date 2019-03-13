/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2006 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.rules.patterns;


import MorfologikBritishSpellerRule.RULE_ID;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.language.BritishEnglish;
import org.languagetool.language.English;
import org.languagetool.language.German;
import org.languagetool.language.GermanyGerman;
import org.languagetool.language.Polish;
import org.languagetool.language.SwissGerman;
import org.languagetool.rules.RuleMatch;
import org.xml.sax.SAXException;


public class FalseFriendRuleTest {
    @Test
    public void testHintsForGermanSpeakers() throws IOException, ParserConfigurationException, SAXException {
        JLanguageTool lt = new JLanguageTool(new English(), new German());
        List<RuleMatch> matches = assertErrors(1, "We will berate you.", lt);
        Assert.assertEquals(matches.get(0).getSuggestedReplacements().toString(), "[provide advice, give advice]");
        assertErrors(0, "We will give you advice.", lt);
        assertErrors(1, "I go to high school in Foocity.", lt);
        List<RuleMatch> matches2 = assertErrors(1, "The chef", lt);
        Assert.assertEquals("[boss, chief]", matches2.get(0).getSuggestedReplacements().toString());
    }

    @Test
    public void testHintsForGermanSpeakersWithVariant() throws IOException, ParserConfigurationException, SAXException {
        JLanguageTool lt = new JLanguageTool(new BritishEnglish(), new SwissGerman());
        List<RuleMatch> matches = assertErrors(1, "We will berate you.", lt);
        Assert.assertEquals(matches.get(0).getSuggestedReplacements().toString(), "[provide advice, give advice]");
        assertErrors(0, "We will give you advice.", lt);
        assertErrors(1, "I go to high school in Berlin.", lt);
        List<RuleMatch> matches2 = assertErrors(1, "The chef", lt);
        Assert.assertEquals("[boss, chief]", matches2.get(0).getSuggestedReplacements().toString());
    }

    @Test
    public void testHintsForDemoLanguage() throws IOException, ParserConfigurationException, SAXException {
        JLanguageTool lt1 = new JLanguageTool(new BritishEnglish(), new GermanyGerman());
        lt1.disableRule(RULE_ID);
        List<RuleMatch> matches1 = assertErrors(1, "And forDemoOnly.", lt1);
        Assert.assertEquals("DEMO_ENTRY", matches1.get(0).getRule().getId());
        JLanguageTool lt2 = new JLanguageTool(new English(), new German());
        lt2.disableRule(RULE_ID);
        List<RuleMatch> matches2 = assertErrors(1, "And forDemoOnly.", lt2);
        Assert.assertEquals("DEMO_ENTRY", matches2.get(0).getRule().getId());
        JLanguageTool lt3 = new JLanguageTool(new AmericanEnglish(), new German());
        lt3.disableRule(MorfologikAmericanSpellerRule.RULE_ID);
        assertErrors(0, "And forDemoOnly.", lt3);
    }

    @Test
    public void testHintsForEnglishSpeakers() throws IOException, ParserConfigurationException, SAXException {
        JLanguageTool lt = new JLanguageTool(new German(), new English());
        assertErrors(1, "Man sollte ihn nicht so beraten.", lt);
        assertErrors(0, "Man sollte ihn nicht so beschimpfen.", lt);
        assertErrors(1, "Ich gehe in Blubbstadt zur Hochschule.", lt);
    }

    @Test
    public void testHintsForPolishSpeakers() throws IOException, ParserConfigurationException, SAXException {
        JLanguageTool lt = new JLanguageTool(new English() {
            @Override
            protected synchronized List<AbstractPatternRule> getPatternRules() {
                return Collections.emptyList();
            }
        }, new Polish());
        assertErrors(1, "This is an absurd.", lt);
        assertErrors(0, "This is absurdity.", lt);
        assertSuggestions(0, "This is absurdity.", lt);
        assertErrors(1, "I have to speak to my advocate.", lt);
        assertSuggestions(3, "My brother is politic.", lt);
    }
}

