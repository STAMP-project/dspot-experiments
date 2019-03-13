/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2018 Oleg Serikov
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
package org.languagetool.rules.spelling.morfologik.suggestions_ordering;


import java.io.IOException;
import org.junit.Test;
import org.languagetool.Language;
import org.languagetool.language.Demo;


public class SuggestionsOrdererTest {
    private String originalConfigNgramsPathValue;

    private boolean originalConfigMLSuggestionsOrderingEnabledValue;

    @Test
    public void orderSuggestionsUsingModelNonExistingRuleId() throws IOException {
        Language language = new Demo();
        String rule_id = "rule_id";
        testOrderingHappened(language, rule_id);
    }

    @Test
    public void orderSuggestionsUsingModelExistingRuleId() throws IOException {
        Language language = new Demo();
        String rule_id = "MORFOLOGIK_RULE_EN_US";
        testOrderingHappened(language, rule_id);
    }

    @Test
    public void orderSuggestionsWithEnabledML() throws IOException {
        SuggestionsOrdererConfig.setMLSuggestionsOrderingEnabled(true);
        orderSuggestionsUsingModelExistingRuleId();
    }

    @Test
    public void orderSuggestionsWithDisabledML() throws IOException {
        SuggestionsOrdererConfig.setMLSuggestionsOrderingEnabled(false);
        orderSuggestionsUsingModelExistingRuleId();
    }
}

