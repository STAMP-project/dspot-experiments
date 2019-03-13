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
package org.languagetool.tokenizers.en;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class EnglishWordTokenizerTest {
    @Test
    public void testTokenize() {
        final EnglishWordTokenizer wordTokenizer = new EnglishWordTokenizer();
        final List<String> tokens = wordTokenizer.tokenize("This is\u00a0a test");
        Assert.assertEquals(tokens.size(), 7);
        Assert.assertEquals("[This,  , is, \u00a0, a,  , test]", tokens.toString());
        final List<String> tokens2 = wordTokenizer.tokenize("This\rbreaks");
        Assert.assertEquals(3, tokens2.size());
        Assert.assertEquals("[This, \r, breaks]", tokens2.toString());
        // hyphen with no whitespace
        final List<String> tokens3 = wordTokenizer.tokenize("Now this is-really!-a test.");
        Assert.assertEquals(tokens3.size(), 10);
        Assert.assertEquals("[Now,  , this,  , is-really, !, -a,  , test, .]", tokens3.toString());
        // hyphen at the end of the word
        final List<String> tokens4 = wordTokenizer.tokenize("Now this is- really!- a test.");
        Assert.assertEquals(tokens4.size(), 15);
        Assert.assertEquals("[Now,  , this,  , is, -,  , really, !, -,  , a,  , test, .]", tokens4.toString());
        // mdash
        final List<String> tokens5 = wordTokenizer.tokenize("Now this is?really!?a test.");
        Assert.assertEquals(tokens5.size(), 13);
        Assert.assertEquals("[Now,  , this,  , is, ?, really, !, ?, a,  , test, .]", tokens5.toString());
        final List<String> tokens6 = wordTokenizer.tokenize("fo'c'sle");
        Assert.assertEquals(tokens6.size(), 1);
    }
}

