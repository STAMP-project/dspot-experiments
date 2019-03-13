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
package org.languagetool.rules.br;


import java.io.IOException;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.Breton;
import org.languagetool.rules.RuleMatch;


public class MorfologikBretonSpellerRuleTest {
    @Test
    public void testMorfologikSpeller() throws IOException {
        final MorfologikBretonSpellerRule rule = new MorfologikBretonSpellerRule(TestTools.getMessages("br"), new Breton(), null, Collections.emptyList());
        RuleMatch[] matches;
        final JLanguageTool langTool = new JLanguageTool(new Breton());
        // correct sentences:
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Penaos ema? kont ganit?")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("C'hwerc'h merc'h gwerc'h war c'hwerc'h marc'h kalloc'h")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("C?hwerc?h merc?h gwerc?h war c?hwerc?h marc'h kalloc?h")).length);
        // words with hyphens are tokenized internally...
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Evel-just")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Barrek-tre eo LanguageTool")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("C'hwerc'h merc'h gwerc'h war c'hwerc'h marc'h kalloc'h")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("C?hwerc?h merc?h gwerc?h war c?hwerc?h marc'h kalloc?h")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Evel-just")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("Evel-juste")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Barrek-tre eo LanguageTool")).length);
        // test for "LanguageTool":
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("LanguageTool!")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence(",")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("123454")).length);
        // incorrect sentences:
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("Evel-juste")).length);
        matches = rule.match(langTool.getAnalyzedSentence("Evel-juste"));
        // check match positions:
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals(5, matches[0].getFromPos());
        Assert.assertEquals(10, matches[0].getToPos());
        matches = rule.match(langTool.getAnalyzedSentence("C?hreizhig-don"));
        Assert.assertEquals(1, matches.length);
        // check match positions:
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals(0, matches[0].getFromPos());
        Assert.assertEquals(10, matches[0].getToPos());
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("a?h")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("a")).length);
    }
}

