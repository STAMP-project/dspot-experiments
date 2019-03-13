/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2011 Daniel Naber (http://www.danielnaber.de)
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.Languages;
import org.languagetool.rules.patterns.AbstractPatternRule;


public class RuleTest {
    private static final Map<String, Integer> idToExpectedMatches = new HashMap<>();

    static {
        RuleTest.idToExpectedMatches.put("STYLE_REPEATED_WORD_RULE_DE", 2);
    }

    @Test
    public void testJavaRules() throws IOException {
        Map<String, String> idsToClassName = new HashMap<>();
        Set<Class> ruleClasses = new HashSet<>();
        if ((Languages.getWithDemoLanguage().size()) <= 1) {
            System.err.println("***************************************************************************");
            System.err.println("WARNING: found only these languages - the tests might not be complete:");
            System.err.println(Languages.getWithDemoLanguage());
            System.err.println("***************************************************************************");
        }
        for (Language language : Languages.getWithDemoLanguage()) {
            JLanguageTool lt = new JLanguageTool(language);
            List<Rule> allRules = lt.getAllRules();
            for (Rule rule : allRules) {
                if (!(rule instanceof AbstractPatternRule)) {
                    assertIdUniqueness(idsToClassName, ruleClasses, language, rule);
                    assertIdValidity(language, rule);
                    Assert.assertTrue(rule.supportsLanguage(language));
                    testExamples(rule, lt);
                }
            }
        }
    }
}

