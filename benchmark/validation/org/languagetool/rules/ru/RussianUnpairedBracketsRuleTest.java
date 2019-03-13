/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2010 Daniel Naber (http://www.languagetool.org)
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
package org.languagetool.rules.ru;


import java.io.IOException;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.Russian;
import org.languagetool.rules.RuleMatch;


public class RussianUnpairedBracketsRuleTest {
    @Test
    public void testRuleRussian() throws IOException {
        RussianUnpairedBracketsRule rule = new RussianUnpairedBracketsRule(TestTools.getEnglishMessages(), new Russian());
        RuleMatch[] matches;
        JLanguageTool langTool = new JLanguageTool(new Russian());
        // correct sentences:
        matches = rule.match(Collections.singletonList(langTool.getAnalyzedSentence("(? ???? ? ????? ?? ??????????, ? ???? ?? ?? ???? ????).")));
        Assert.assertEquals(0, matches.length);
        // correct sentences:
        matches = rule.match(Collections.singletonList(langTool.getAnalyzedSentence("????? ??????? ?????? ?????? ?????? ??????????????? ??????? (1824).")));
        Assert.assertEquals(0, matches.length);
        matches = rule.match(Collections.singletonList(langTool.getAnalyzedSentence("\u0410 \"\u0431\" \u0414.")));
        Assert.assertEquals(0, matches.length);
        matches = rule.match(Collections.singletonList(langTool.getAnalyzedSentence("?), ?), ?)..., ??), ??) ? 1?)")));
        Assert.assertEquals(0, matches.length);
        // incorrect sentences:
        matches = rule.match(Collections.singletonList(langTool.getAnalyzedSentence("? ????? ????? ??? ????? ? ??? 1823 ? ???????? ????? ? ?????? '??????? ??????.")));
        Assert.assertEquals(1, matches.length);
    }
}

