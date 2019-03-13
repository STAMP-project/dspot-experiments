/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2013 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.chunking;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.languagetool.AnalyzedToken;
import org.languagetool.AnalyzedTokenReadings;


public class EnglishChunkFilterTest {
    @Test
    public void testSingular() {
        assertChunks("He/B-NP owns/B-VP a/B-NP nice/I-NP house/I-NP in/X Berlin/B-NP ./.", "He/B-NP-singular,E-NP-singular owns/B-VP a/B-NP-singular nice/I-NP-singular house/E-NP-singular in/X Berlin/B-NP-singular,E-NP-singular ./.");
    }

    @Test
    public void testPluralByPluralNoun() throws IOException {
        String input = "I/X have/N-VP ten/B-NP books/I-NP ./.";
        List<ChunkTaggedToken> tokens = makeTokens(input);
        tokens.remove(3);// 'books'

        AnalyzedTokenReadings readings = new AnalyzedTokenReadings(Arrays.asList(new AnalyzedToken("books", "NNS", "book"), new AnalyzedToken("books", "VBZ", "book")), 0);
        tokens.add(3, new ChunkTaggedToken("books", Collections.singletonList(new ChunkTag("I-NP")), readings));
        assertChunks(tokens, "I/X have/N-VP ten/B-NP-plural books/E-NP-plural ./.");
    }
}

