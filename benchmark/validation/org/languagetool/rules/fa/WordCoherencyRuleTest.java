/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2014 Ebrahim Byagowi <ebrahim@gnu.org>
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
package org.languagetool.rules.fa;


import java.io.IOException;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.Persian;
import org.languagetool.rules.TextLevelRule;
import org.languagetool.rules.patterns.PatternRuleTest;


public class WordCoherencyRuleTest extends PatternRuleTest {
    @Test
    public void testRules() throws IOException {
        JLanguageTool langTool = new JLanguageTool(new Persian());
        TextLevelRule rule = new WordCoherencyRule(TestTools.getMessages("fa"));
        MatcherAssert.assertThat(rule.match(Collections.singletonList(langTool.getAnalyzedSentence("??? ?? ???? ???."))).length, Is.is(0));
        MatcherAssert.assertThat(rule.match(Collections.singletonList(langTool.getAnalyzedSentence("??? ?? ???? ???. ??? ?? ???? ???."))).length, Is.is(1));
    }
}

