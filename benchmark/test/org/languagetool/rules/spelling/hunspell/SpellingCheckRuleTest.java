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
package org.languagetool.rules.spelling.hunspell;


import GermanSpellerRule.RULE_ID;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.language.GermanyGerman;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.spelling.SpellingCheckRule;


public class SpellingCheckRuleTest {
    @Test
    public void testIgnoreSuggestionsWithHunspell() throws IOException {
        JLanguageTool lt = new JLanguageTool(new GermanyGerman());
        Assert.assertThat(lt.check("Das ist ein einPseudoWortF?rLanguageToolTests").size(), CoreMatchers.is(0));// no error, as this word is in ignore.txt

        List<RuleMatch> matches = lt.check("Das ist ein Tibbfehla");
        Assert.assertThat(matches.size(), CoreMatchers.is(1));
        Assert.assertThat(matches.get(0).getRule().getId(), CoreMatchers.is(RULE_ID));
    }

    @Test
    public void testIgnorePhrases() throws IOException {
        JLanguageTool lt = new JLanguageTool(new GermanyGerman());
        Assert.assertThat(lt.check("Ein Test mit Auriensis Fantasiewortus").size(), CoreMatchers.is(2));
        for (Rule rule : lt.getAllActiveRules()) {
            if (rule instanceof SpellingCheckRule) {
                acceptPhrases(Arrays.asList("Auriensis Fantasiewortus", "fudeldu laberwort"));
            } else {
                lt.disableRule(rule.getId());
            }
        }
        Assert.assertThat(lt.check("Ein Test mit Auriensis Fantasiewortus").size(), CoreMatchers.is(0));
        Assert.assertThat(lt.check("Ein Test mit Auriensis und Fantasiewortus").size(), CoreMatchers.is(2));// the words on their own are not ignored

        Assert.assertThat(lt.check("fudeldu laberwort").size(), CoreMatchers.is(0));
        Assert.assertThat(lt.check("Fudeldu laberwort").size(), CoreMatchers.is(0));// Uppercase at sentence start is okay

        Assert.assertThat(lt.check("Fudeldu Laberwort").size(), CoreMatchers.is(2));// Different case somewhere other than at sentence start is not okay

    }
}

