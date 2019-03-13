/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2014 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.language.rules.ast;


import java.io.IOException;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.Asturian;
import org.languagetool.rules.RuleMatch;


public class MorfologikAsturianSpellerRuleTest {
    @Test
    public void testMorfologikSpeller() throws IOException {
        Asturian language = new Asturian();
        MorfologikAsturianSpellerRule rule = new MorfologikAsturianSpellerRule(TestTools.getMessages("en"), language, null, Collections.emptyList());
        JLanguageTool langTool = new JLanguageTool(language);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("?Festeyate colos correutores gramaticales?")).length);
        RuleMatch[] matches = rule.match(langTool.getAnalyzedSentence("?Af?yeste colos correutores gramaticales?"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals(1, matches[0].getFromPos());
        Assert.assertEquals(9, matches[0].getToPos());
        Assert.assertEquals("Afayesti", matches[0].getSuggestedReplacements().get(0));
    }
}

