/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2005 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.rules.ro;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;


/**
 *
 *
 * @author Ionu? P?duraru
 */
public class SimpleReplaceRuleTest {
    private SimpleReplaceRule rule;

    private JLanguageTool langTool;

    /**
     * Make sure that the suggested word is not the same as the wrong word
     */
    @Test
    public void testInvalidSuggestion() {
        final List<String> invalidSuggestions = new ArrayList<>();
        final List<Map<String, String>> wrongWords = rule.getWrongWords();
        for (Map<String, String> ruleEntry : wrongWords) {
            for (Map.Entry<String, String> entry : ruleEntry.entrySet()) {
                final String fromWord = entry.getKey();
                final String toWord = entry.getValue();
                if ((toWord == null) || (fromWord.equals(toWord))) {
                    invalidSuggestions.add(toWord);
                }
            }
        }
        if (!(invalidSuggestions.isEmpty())) {
            Assert.fail(("Invalid suggestions found for: " + invalidSuggestions));
        }
    }

    @Test
    public void testRule() throws IOException {
        // correct sentences:
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Paisprezece case.")).length);
        // incorrect sentences:
        // at the beginning of a sentence (Romanian replace rule is case-sensitive)
        checkSimpleReplaceRule("Patrusprezece case.", "Paisprezece");
        // inside sentence
        checkSimpleReplaceRule("Satul are patrusprezece case.", "paisprezece");
        checkSimpleReplaceRule("Satul are (patrusprezece) case.", "paisprezece");
        checkSimpleReplaceRule("Satul are ?patrusprezece? case.", "paisprezece");
        checkSimpleReplaceRule("El are ?asesprezece ani.", "?aisprezece");
        checkSimpleReplaceRule("El a luptat pentru ?nt?iele c?r?i.", "?nt?ile");
        checkSimpleReplaceRule("El are cinsprezece c?r?i.", "cincisprezece");
        checkSimpleReplaceRule("El a fost patruzecioptist.", "pa?optist");
        checkSimpleReplaceRule("M-am adresat ?nt?iei venite.", "?nt?ii");
        checkSimpleReplaceRule("M-am adresat ?nt?ielor venite.", "?nt?ilor");
        checkSimpleReplaceRule("A ajuns al dou?zecelea.", "dou?zecilea");
        checkSimpleReplaceRule("A ajuns al zecilea.", "zecelea");
        checkSimpleReplaceRule("A primit jumate de litru de lapte.", "jum?tate");
        // multiple words / compounds
        // space-delimited
        checkSimpleReplaceRule("aqua forte", "acvaforte");
        checkSimpleReplaceRule("aqua forte.", "acvaforte");
        checkSimpleReplaceRule("A folosit ?aqua forte?.", "acvaforte");
        checkSimpleReplaceRule("Aqua forte.", "Acvaforte");
        checkSimpleReplaceRule("este aqua forte", "acvaforte");
        checkSimpleReplaceRule("este aqua forte.", "acvaforte");
        checkSimpleReplaceRule("este Aqua Forte.", "Acvaforte");
        checkSimpleReplaceRule("este AquA Forte.", "Acvaforte");
        checkSimpleReplaceRule("A primit jumate de litru de lapte ?i este aqua forte.", "jum?tate", "acvaforte");
        checkSimpleReplaceRule("du-te vino", "du-te-vino");
        // dash-delimited
        checkSimpleReplaceRule("cou-boi", "cowboy");
        checkSimpleReplaceRule("cow-boy", "cowboy");
        checkSimpleReplaceRule("cau-boi", "cowboy");
        checkSimpleReplaceRule("Cau-boi", "Cowboy");
        checkSimpleReplaceRule("cowboy");// correct, no replacement

        checkSimpleReplaceRule("Iat? un cau-boi", "cowboy");
        checkSimpleReplaceRule("Iat? un cau-boi.", "cowboy");
        checkSimpleReplaceRule("Iat? un (cau-boi).", "cowboy");
        checkSimpleReplaceRule("v?car=cau-boi", "cowboy");
        // multiple suggestions
        checkSimpleReplaceRule("A fost ad?ogit? o alt? regul?.", "ad?ugit?/ad?ugat?");
        checkSimpleReplaceRule("A venit adinioarea.", "adineaori/adineauri");
        // words with multiple wrong forms
        checkSimpleReplaceRule("A pus axterix.", "asterisc");
        checkSimpleReplaceRule("A pus axterics.", "asterisc");
        checkSimpleReplaceRule("A pus asterics.", "asterisc");
    }
}

