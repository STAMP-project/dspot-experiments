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
package org.languagetool.rules.en;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.AnalyzedSentence;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.spelling.SpellingCheckRule;


public class SpellingCheckRuleTest {
    @Test
    public void testIgnoreSuggestionsWithMorfologik() throws IOException {
        JLanguageTool lt = new JLanguageTool(new AmericanEnglish());
        Assert.assertThat(lt.check("This is anArtificialTestWordForLanguageTool.").size(), CoreMatchers.is(0));// no error, as this word is in ignore.txt

        Assert.assertThat(lt.check("How an ab initio calculation works.").size(), CoreMatchers.is(0));// As a multi-word entry in spelling.txt "ab initio" must be accepted

        Assert.assertThat(lt.check("Test adjoint").size(), CoreMatchers.is(0));// in spelling.txt

        Assert.assertThat(lt.check("Test Adjoint").size(), CoreMatchers.is(0));// in spelling.txt (lowercase)

        List<RuleMatch> matches2 = lt.check("This is a real typoh.");
        Assert.assertThat(matches2.size(), CoreMatchers.is(1));
        Assert.assertThat(matches2.get(0).getRule().getId(), CoreMatchers.is("MORFOLOGIK_RULE_EN_US"));
        List<RuleMatch> matches3 = lt.check("This is anotherArtificialTestWordForLanguageTol.");// note the typo

        Assert.assertThat(matches3.size(), CoreMatchers.is(1));
        Assert.assertThat(matches3.get(0).getSuggestedReplacements().toString(), CoreMatchers.is("[anotherArtificialTestWordForLanguageTool]"));
    }

    @Test
    public void testIgnorePhrases() throws IOException {
        JLanguageTool langTool = new JLanguageTool(new AmericanEnglish());
        Assert.assertThat(langTool.check("A test with myfoo mybar").size(), CoreMatchers.is(2));
        for (Rule rule : langTool.getAllActiveRules()) {
            if (rule instanceof SpellingCheckRule) {
                acceptPhrases(Arrays.asList("myfoo mybar", "Myy othertest"));
            } else {
                langTool.disableRule(rule.getId());
            }
        }
        Assert.assertThat(langTool.check("A test with myfoo mybar").size(), CoreMatchers.is(0));
        Assert.assertThat(langTool.check("A test with myfoo and mybar").size(), CoreMatchers.is(2));// the words on their own are not ignored

        Assert.assertThat(langTool.check("myfoo mybar here").size(), CoreMatchers.is(0));
        Assert.assertThat(langTool.check("Myfoo mybar here").size(), CoreMatchers.is(0));
        Assert.assertThat(langTool.check("MYfoo mybar here").size(), CoreMatchers.is(2));
        Assert.assertThat(langTool.check("Myy othertest is okay").size(), CoreMatchers.is(0));
        Assert.assertThat(langTool.check("And Myy othertest is okay").size(), CoreMatchers.is(0));
        Assert.assertThat(langTool.check("But Myy Othertest is not okay").size(), CoreMatchers.is(2));
        Assert.assertThat(langTool.check("But myy othertest is not okay").size(), CoreMatchers.is(2));
    }

    @Test
    public void testIsUrl() throws IOException {
        SpellingCheckRuleTest.MySpellCheckingRule rule = new SpellingCheckRuleTest.MySpellCheckingRule();
        rule.test();
    }

    static class MySpellCheckingRule extends SpellingCheckRule {
        MySpellCheckingRule() {
            super(TestTools.getEnglishMessages(), new AmericanEnglish(), null);
        }

        @Override
        public String getId() {
            return null;
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public RuleMatch[] match(AnalyzedSentence sentence) throws IOException {
            return null;
        }

        void test() throws IOException {
            Assert.assertTrue(isUrl("http://www.test.de"));
            Assert.assertTrue(isUrl("http://www.test-dash.com"));
            Assert.assertTrue(isUrl("https://www.test-dash.com"));
            Assert.assertTrue(isUrl("ftp://www.test-dash.com"));
            Assert.assertTrue(isUrl("http://www.test-dash.com/foo/path-dash"));
            Assert.assertTrue(isUrl("http://www.test-dash.com/foo/???-dash"));
            Assert.assertTrue(isUrl("http://www.test-dash.com/foo/%C3%B-dash"));
            Assert.assertTrue(isUrl("www.languagetool.org"));
            Assert.assertFalse(isUrl("languagetool.org"));// currently not detected

            Assert.assertTrue(isEMail("martin.mustermann@test.de"));
            Assert.assertTrue(isEMail("martin.mustermann@test.languagetool.de"));
            Assert.assertTrue(isEMail("martin-mustermann@test.com"));
            Assert.assertFalse(isEMail("@test.de"));
            Assert.assertFalse(isEMail("f.test@test"));
            Assert.assertFalse(isEMail("f@t.t"));
        }
    }
}

