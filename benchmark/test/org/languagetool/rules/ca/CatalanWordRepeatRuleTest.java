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
package org.languagetool.rules.ca;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.Catalan;
import org.languagetool.rules.RuleMatch;


public class CatalanWordRepeatRuleTest {
    /* Test method for 'org.languagetool.rules.ca.CatalanWordRepeatRule.match(AnalyzedSentence)' */
    @Test
    public void testRule() throws IOException {
        final CatalanWordRepeatRule rule = new CatalanWordRepeatRule(TestTools.getMessages("ca"), new Catalan());
        RuleMatch[] matches;
        JLanguageTool langTool = new JLanguageTool(new Catalan());
        // correct
        matches = rule.match(langTool.getAnalyzedSentence("Sempre pensa en en Joan."));
        Assert.assertEquals(0, matches.length);
        matches = rule.match(langTool.getAnalyzedSentence("Els els portar? aviat."));
        Assert.assertEquals(0, matches.length);
        matches = rule.match(langTool.getAnalyzedSentence("Maximili? I i Maria de Borgonya"));
        Assert.assertEquals(0, matches.length);
        matches = rule.match(langTool.getAnalyzedSentence("De la A a la z"));
        Assert.assertEquals(0, matches.length);
        matches = rule.match(langTool.getAnalyzedSentence("Entre I i II."));
        Assert.assertEquals(0, matches.length);
        matches = rule.match(langTool.getAnalyzedSentence("fills de Sigebert I i Brunegilda"));
        Assert.assertEquals(0, matches.length);
        matches = rule.match(langTool.getAnalyzedSentence("del segle I i del segle II"));
        Assert.assertEquals(0, matches.length);
        matches = rule.match(langTool.getAnalyzedSentence("entre el cap?tol I i el II"));
        Assert.assertEquals(0, matches.length);
        matches = rule.match(langTool.getAnalyzedSentence("cada una una casa"));
        Assert.assertEquals(0, matches.length);
        matches = rule.match(langTool.getAnalyzedSentence("cada un un llibre"));
        Assert.assertEquals(0, matches.length);
        // incorrect
        matches = rule.match(langTool.getAnalyzedSentence("Tots els els homes s?n iguals."));
        Assert.assertEquals(1, matches.length);
        matches = rule.match(langTool.getAnalyzedSentence("Maximili? i i Maria de Borgonya"));
        Assert.assertEquals(1, matches.length);
    }
}

