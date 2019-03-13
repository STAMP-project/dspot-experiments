/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2016 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.rules;


import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.Languages;
import org.languagetool.rules.spelling.CachingWordListLoader;
import org.languagetool.rules.spelling.SpellingCheckRule;


public class WordListValidatorTest {
    private static final Pattern VALID_CHARS = Pattern.compile(("[0-9a-zA-Z??????????????????????????&" + (((((("??????????????????????????????????????????????????????????????????????" + "???????????") + "?")// for Portuguese
     + "?")// for Catalan
     + "'???????????????")// for Dutch (inhabitants) proper names mostly
     + "./-]+") + "|[khmcd?]?m[??]|?[CFR]|CO?-?.*")));

    // Words that are valid but with special characters so that we don't want to
    // allow them in general:
    private static final Set<String> VALID_WORDS = new HashSet<>(// Greek letters / Mathematics and physics variables
    Arrays.asList("Mondel?z", "Bra?", "Djuve?", "Djuve?reis", "Hidsch?b/S", "Dvo??k/S", "Erdo?an/S", "?ngstr?m", "?ngstr?m", "?ngstr?ms", "'Ndrangheta", "McDonald's", "?m", "?g", "?l", "CD&V", "C&A", "P&O", "S&P", "?SSR", "V&D", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?"));

    @Test
    public void testWordListValidity() throws IOException {
        Set<String> checked = new HashSet<>();
        for (Language lang : Languages.get()) {
            if (lang.getShortCode().equals("ru")) {
                // skipping, Cyrillic chars not part of the validation yet
                continue;
            }
            JLanguageTool lt = new JLanguageTool(lang);
            List<Rule> rules = lt.getAllActiveRules();
            for (Rule rule : rules) {
                if (rule instanceof SpellingCheckRule) {
                    SpellingCheckRule sRule = ((SpellingCheckRule) (rule));
                    String file = sRule.getSpellingFileName();
                    if ((JLanguageTool.getDataBroker().resourceExists(file)) && (!(checked.contains(file)))) {
                        System.out.println(("Checking " + file));
                        CachingWordListLoader loader = new CachingWordListLoader();
                        List<String> words = loader.loadWords(file);
                        validateWords(words, file);
                        checked.add(file);
                    }
                }
            }
        }
    }
}

