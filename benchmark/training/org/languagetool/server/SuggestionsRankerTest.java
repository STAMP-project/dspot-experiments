/**
 * LanguageTool, a natural language style checker
 *  * Copyright (C) 2018 Fabian Richter
 *  *
 *  * This library is free software; you can redistribute it and/or
 *  * modify it under the terms of the GNU Lesser General Public
 *  * License as published by the Free Software Foundation; either
 *  * version 2.1 of the License, or (at your option) any later version.
 *  *
 *  * This library is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  * Lesser General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU Lesser General Public
 *  * License along with this library; if not, write to the Free Software
 *  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 *  * USA
 */
package org.languagetool.server;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Ignore;
import org.junit.Test;
import org.languagetool.Language;
import org.languagetool.Languages;
import org.languagetool.rules.spelling.suggestions.XGBoostSuggestionsOrderer;


@Ignore("Requires ngram data to run")
public class SuggestionsRankerTest {
    private final Language german = Languages.getLanguageForShortCode("de-DE");

    private final Language english = Languages.getLanguageForShortCode("en-US");

    private final String agent = "ltorg";

    private HTTPServer server;

    @Test
    public void testRankingWithUserDict() throws IOException {
        // no need to also create test tables for logging
        try {
            DatabaseAccess.createAndFillTestTables();
            XGBoostSuggestionsOrderer.setAutoCorrectThresholdForLanguage(english, 0.5F);
            String autoCorrectReplacements = getReplacements(check(english, "This is a mistak.", agent, 3L, UserDictTest.USERNAME1, UserDictTest.API_KEY1));
            System.out.println(autoCorrectReplacements);
            MatcherAssert.assertThat(autoCorrectReplacements, CoreMatchers.allOf(CoreMatchers.containsString("confidence"), CoreMatchers.containsString("autoCorrect")));
            addWord("mistaki", UserDictTest.USERNAME1, UserDictTest.API_KEY1);
            String replacementsData = getReplacements(check(english, "This is a mistak.", agent, 3L, UserDictTest.USERNAME1, UserDictTest.API_KEY1));
            System.out.println(replacementsData);
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> replacements = mapper.readValue(replacementsData, List.class);
            MatcherAssert.assertThat(replacements.get(0).get("value"), CoreMatchers.is("mistaki"));
            MatcherAssert.assertThat(replacements.get(0).get("confidence"), CoreMatchers.is(CoreMatchers.nullValue()));
            MatcherAssert.assertThat(replacements.get(0).containsKey("autoCorrect"), CoreMatchers.is(false));
            MatcherAssert.assertThat(replacements.get(1).get("value"), CoreMatchers.is("mistake"));
            MatcherAssert.assertThat(replacements.get(1).get("confidence"), CoreMatchers.is(CoreMatchers.notNullValue()));
            MatcherAssert.assertThat(replacements.get(1).get("confidence"), CoreMatchers.not(0.0));
        } finally {
            DatabaseAccess.deleteTestTables();
        }
    }

    @Test
    public void testRanking() throws IOException {
        MatcherAssert.assertThat(getReplacements(check(english, "This is a mistak.", agent, 3L)), CoreMatchers.containsString("confidence"));
    }

    @Test
    public void testNotRanking() throws IOException {
        // not ranking when:
        // model for language disabled
        MatcherAssert.assertThat(getReplacements(check(german, "Das ist ein Fehlar.", agent, 2L)), CoreMatchers.not(CoreMatchers.containsString("confidence")));
        MatcherAssert.assertThat(getReplacements(check(german, "Das ist ein Fehlar.", agent, 3L)), CoreMatchers.not(CoreMatchers.containsString("confidence")));
        // in group A of A/B test
        MatcherAssert.assertThat(getReplacements(check(english, "This is a mistak.", agent, 2L)), CoreMatchers.not(CoreMatchers.containsString("confidence")));
        // no A/B test for these clients
        MatcherAssert.assertThat(getReplacements(check(english, "This is a mistak.", null, 3L)), CoreMatchers.not(CoreMatchers.containsString("confidence")));
        MatcherAssert.assertThat(getReplacements(check(english, "This is a mistak.", "webextension-chrome-ng", 3L)), CoreMatchers.not(CoreMatchers.containsString("confidence")));
    }
}

