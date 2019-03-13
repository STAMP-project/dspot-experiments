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
package org.languagetool.tokenizers.pl;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.Language;
import org.languagetool.language.Polish;


public class PolishWordTokenizerTest {
    @Test
    public void testTokenize() {
        final PolishWordTokenizer wordTokenizer = new PolishWordTokenizer();
        final List<String> tokens = wordTokenizer.tokenize("To jest\u00a0 test");
        Assert.assertEquals(tokens.size(), 6);
        Assert.assertEquals("[To,  , jest, \u00a0,  , test]", tokens.toString());
        final List<String> tokens2 = wordTokenizer.tokenize("To\r\u0142amie");
        Assert.assertEquals(3, tokens2.size());
        Assert.assertEquals("[To, \r, \u0142amie]", tokens2.toString());
        // hyphen with no whitespace
        final List<String> tokens3 = wordTokenizer.tokenize("A to jest-naprawd?-test!");
        Assert.assertEquals(tokens3.size(), 6);
        Assert.assertEquals("[A,  , to,  , jest-naprawd?-test, !]", tokens3.toString());
        // hyphen at the end of the word
        final List<String> tokens4 = wordTokenizer.tokenize("Niemiecko- i angielsko-polski");
        Assert.assertEquals(tokens4.size(), 6);
        Assert.assertEquals("[Niemiecko, -,  , i,  , angielsko-polski]", tokens4.toString());
        // hyphen probably instead of mdash
        final List<String> tokens5 = wordTokenizer.tokenize("Widz? krow? -i to dobrze!");
        Assert.assertEquals(11, tokens5.size());
        Assert.assertEquals("[Widz?,  , krow?,  , -, i,  , to,  , dobrze, !]", tokens5.toString());
        // mdash
        final List<String> tokens6 = wordTokenizer.tokenize("A to jest zdanie?rzeczywi?cie?z wtr?ceniem.");
        Assert.assertEquals(tokens6.size(), 14);
        Assert.assertEquals("[A,  , to,  , jest,  , zdanie, ?, rzeczywi?cie, ?, z,  , wtr?ceniem, .]", tokens6.toString());
        // compound words with hyphens
        final String compoundSentence = "To jest kobieta-wojownik w polsko-czeskim ubraniu, kt?ra wys?a?a dwa SMS-y.";
        List<String> compoundTokens = wordTokenizer.tokenize(compoundSentence);
        Assert.assertEquals(21, compoundTokens.size());
        Assert.assertEquals("[To,  , jest,  , kobieta-wojownik,  , w,  , polsko-czeskim,  , ubraniu, ,,  , kt?ra,  , wys?a?a,  , dwa,  , SMS-y, .]", compoundTokens.toString());
        // now setup the tagger...
        Language pl = new Polish();
        wordTokenizer.setTagger(pl.getTagger());
        compoundTokens = wordTokenizer.tokenize(compoundSentence);
        // we should get 4 more tokens: two hyphen tokens and two for the split words
        Assert.assertEquals(25, compoundTokens.size());
        Assert.assertEquals(("[To,  , jest,  , kobieta, -, wojownik,  , " + ("w,  , polsko, -, czeskim,  , ubraniu, ,,  " + ", kt?ra,  , wys?a?a,  , dwa,  , SMS-y, .]")), compoundTokens.toString());
        compoundTokens = wordTokenizer.tokenize("Mia?a osiemna?cie-dwadzie?cia lat.");
        Assert.assertEquals(8, compoundTokens.size());
        Assert.assertEquals("[Mia?a,  , osiemna?cie, -, dwadzie?cia,  , lat, .]", compoundTokens.toString());
        // now three-part adja-adja-adj...:
        compoundTokens = wordTokenizer.tokenize("S?ownik polsko-niemiecko-indonezyjski");
        Assert.assertEquals(7, compoundTokens.size());
        Assert.assertEquals("[S?ownik,  , polsko, -, niemiecko, -, indonezyjski]", compoundTokens.toString());
        // number ranges:
        compoundTokens = wordTokenizer.tokenize("Impreza odb?dzie si? w dniach 1-23 maja.");
        Assert.assertEquals(16, compoundTokens.size());
        Assert.assertEquals("[Impreza,  , odb?dzie,  , si?,  , w,  , dniach,  , 1, -, 23,  , maja, .]", compoundTokens.toString());
        // number ranges:
        compoundTokens = wordTokenizer.tokenize("Impreza odb?dzie si? w dniach 1--23 maja.");
        Assert.assertEquals(18, compoundTokens.size());
        Assert.assertEquals("[Impreza,  , odb?dzie,  , si?,  , w,  , dniach,  , 1, -, , -, 23,  , maja, .]", compoundTokens.toString());
    }
}

