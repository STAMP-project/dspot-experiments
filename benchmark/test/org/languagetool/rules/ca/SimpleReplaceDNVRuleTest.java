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
import org.languagetool.rules.RuleMatch;


/**
 *
 *
 * @author Jaume Ortol?
 */
public class SimpleReplaceDNVRuleTest {
    private SimpleReplaceDNVRule rule;

    private JLanguageTool langTool;

    @Test
    public void testRule() throws IOException {
        // correct sentences:
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Ella ?s molt incauta.")).length);
        // incorrect sentences:
        RuleMatch[] matches = rule.match(langTool.getAnalyzedSentence("L'arxip?leg."));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("arxip?lag", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("colmena"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("buc", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("rusc", matches[0].getSuggestedReplacements().get(1));
        matches = rule.match(langTool.getAnalyzedSentence("colmenes"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("bucs", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("ruscos", matches[0].getSuggestedReplacements().get(1));
        Assert.assertEquals("ruscs", matches[0].getSuggestedReplacements().get(2));
        matches = rule.match(langTool.getAnalyzedSentence("afincaments"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("establiments", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("instal?lacions", matches[0].getSuggestedReplacements().get(1));
        matches = rule.match(langTool.getAnalyzedSentence("Els arxip?legs"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("arxip?lags", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("acev?ssiu"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("enceb?ssiu", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("S'arropeixen"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("arrupeixen", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("arrupen", matches[0].getSuggestedReplacements().get(1));
        matches = rule.match(langTool.getAnalyzedSentence("incautaren"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("confiscaren", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("requisaren", matches[0].getSuggestedReplacements().get(1));
        Assert.assertEquals("comissaren", matches[0].getSuggestedReplacements().get(2));
        Assert.assertEquals("decomissaren", matches[0].getSuggestedReplacements().get(3));
    }
}

