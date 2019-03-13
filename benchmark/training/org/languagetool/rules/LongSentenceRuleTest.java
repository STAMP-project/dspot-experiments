/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2013 Daniel Naber (http://danielnaber.de/)
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


import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.UserConfig;


public class LongSentenceRuleTest {
    @Test
    public void testMatch() throws Exception {
        JLanguageTool lt = new JLanguageTool(TestTools.getDemoLanguage());
        LongSentenceRule rule = new LongSentenceRule(TestTools.getEnglishMessages(), new UserConfig());
        assertNoMatch(" is a rather short text.", rule, lt);
        assertMatch(("Now this is not " + (((("a a a a a a a a a a a " + "a a a a a a a a a a a ") + "a a a a a a a a a a a ") + "a a a a a a a a a a a ") + "rather that short text.")), 111, 121, rule, lt);
        LongSentenceRule shortRule = new LongSentenceRule(TestTools.getEnglishMessages(), new UserConfig(), 6);
        // shortRule.setDefaultValue(6);
        assertNoMatch("This is a rather short text.", shortRule, lt);
        assertMatch("This is also a rather short text.", 22, 32, shortRule, lt);
        assertNoMatch("These ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ don't count.", shortRule, lt);
        assertNoMatch("one two three four five six.", shortRule, lt);
        assertNoMatch("one two three (four) five six.", shortRule, lt);
        assertMatch("one two three four five six seven.", 24, 33, shortRule, lt);
        assertNoMatch("Eins zwei drei vier f?nf sechs.", shortRule, lt);
    }
}

