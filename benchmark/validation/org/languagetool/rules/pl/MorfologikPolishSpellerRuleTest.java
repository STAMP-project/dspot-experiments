/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2012 Marcin Mi?kowski
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
package org.languagetool.rules.pl;


import java.io.IOException;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.Polish;
import org.languagetool.rules.RuleMatch;


public class MorfologikPolishSpellerRuleTest {
    @Test
    public void testMorfologikSpeller() throws IOException {
        final MorfologikPolishSpellerRule rule = new MorfologikPolishSpellerRule(TestTools.getMessages("pl"), new Polish(), null, Collections.emptyList());
        final JLanguageTool langTool = new JLanguageTool(new Polish());
        // correct sentences:
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("To jest test bez jakiegokolwiek b??du.")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("???w na staro?? wydziela dziwn? wo?.")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("???w na staro?? wydziela dziwn? wo? numer 1234.")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("W MI-6 pracuje 15-letni agent.")).length);
        // test for "LanguageTool":
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("LanguageTool jest ?wietny!")).length);
        // test for the ignored uppercase word "Gdym":
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Gdym to zobaczy?, zd?bia?em.")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence(",")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("123454")).length);
        // compound word with ignored part "techniczno"
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Bogactwo nie ro?nie proporcjonalnie do jej rozwoju techniczno-terytorialnego.")).length);
        // compound word with one of the compound prefixes:
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Antypostmodernistyczna batalia hiperfilozof?w")).length);
        // compound words: "trzynastobitowy", "zgni?o???ty"
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Trzynastobitowe przystawki w kolorze zgni?o???tym")).length);
        // incorrect sentences:
        RuleMatch[] matches = rule.match(langTool.getAnalyzedSentence("Zolw"));
        // check match positions:
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals(0, matches[0].getFromPos());
        Assert.assertEquals(4, matches[0].getToPos());
        Assert.assertEquals("???w", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("? Zolw"));
        // check match positions:
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals(3, matches[0].getFromPos());
        Assert.assertEquals(7, matches[0].getToPos());
        Assert.assertEquals("???w", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("?? Zolw"));
        // check match positions:
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals(5, matches[0].getFromPos());
        Assert.assertEquals(9, matches[0].getToPos());
        Assert.assertEquals("???w", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("a?h")).length);
        // tokenizing on prefixes niby- and quasi-
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Niby-artysta spotka? si? z quasi-opiekunem i niby-Francuzem.")).length);
        final RuleMatch[] prunedMatches = rule.match(langTool.getAnalyzedSentence("Clarkem"));
        Assert.assertEquals(1, prunedMatches.length);
        Assert.assertEquals(5, prunedMatches[0].getSuggestedReplacements().size());
        Assert.assertEquals("Clarke", prunedMatches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("Clarkiem", prunedMatches[0].getSuggestedReplacements().get(1));
        Assert.assertEquals("Ciark?", prunedMatches[0].getSuggestedReplacements().get(2));
        Assert.assertEquals("Clarkom", prunedMatches[0].getSuggestedReplacements().get(3));
        Assert.assertEquals("Czark?", prunedMatches[0].getSuggestedReplacements().get(4));
        // There should be a match, this is not a prefix!
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("premoc")).length);
        // "0" instead "o"...
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("dziwneg0")).length);
    }
}

