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


import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.JLanguageTool;
import org.languagetool.language.English;


public class EnglishChunkerTest {
    @Test
    public void testAddChunkTags() throws Exception {
        EnglishChunker chunker = new EnglishChunker();
        List<AnalyzedTokenReadings> readingsList = createReadingsList("A short test of the bicycle is needed");
        chunker.addChunkTags(readingsList);
        Assert.assertThat(readingsList.size(), CoreMatchers.is(15));
        // "A short test":
        Assert.assertThat(readingsList.get(0).getChunkTags().toString(), CoreMatchers.is("[B-NP-singular]"));
        Assert.assertThat(readingsList.get(2).getChunkTags().toString(), CoreMatchers.is("[I-NP-singular]"));
        Assert.assertThat(readingsList.get(4).getChunkTags().toString(), CoreMatchers.is("[E-NP-singular]"));
        // "the chunker":
        Assert.assertThat(readingsList.get(8).getChunkTags().toString(), CoreMatchers.is("[B-NP-singular]"));
        Assert.assertThat(readingsList.get(10).getChunkTags().toString(), CoreMatchers.is("[E-NP-singular]"));
        // "is"
        Assert.assertThat(readingsList.get(12).getChunkTags().toString(), CoreMatchers.is("[B-VP]"));
        Assert.assertThat(readingsList.get(14).getChunkTags().toString(), CoreMatchers.is("[I-VP]"));
    }

    @Test
    public void testAddChunkTagsSingular() throws Exception {
        EnglishChunker chunker = new EnglishChunker();
        JLanguageTool lt = new JLanguageTool(new English());
        List<AnalyzedSentence> sentences = lt.analyzeText("The abacus shows how numbers can be stored");
        List<AnalyzedTokenReadings> readingsList = Arrays.asList(sentences.get(0).getTokens());
        chunker.addChunkTags(readingsList);
        // "The abacus":
        Assert.assertThat(readingsList.get(1).getChunkTags().toString(), CoreMatchers.is("[B-NP-singular]"));
        Assert.assertThat(readingsList.get(3).getChunkTags().toString(), CoreMatchers.is("[E-NP-singular]"));
        // "numbers":
        Assert.assertThat(readingsList.get(9).getChunkTags().toString(), CoreMatchers.is("[B-NP-plural, E-NP-plural]"));
    }

    @Test
    public void testContractions() throws Exception {
        JLanguageTool langTool = new JLanguageTool(new English());
        AnalyzedSentence analyzedSentence = langTool.getAnalyzedSentence("I'll be there");
        AnalyzedTokenReadings[] tokens = analyzedSentence.getTokens();
        Assert.assertThat(tokens[1].getChunkTags().get(0), CoreMatchers.is(new ChunkTag("B-NP-singular")));
        Assert.assertThat(tokens[2].getChunkTags().size(), CoreMatchers.is(0));// "'" cannot be mapped as we tokenize differently

        Assert.assertThat(tokens[3].getChunkTags().size(), CoreMatchers.is(0));// "ll" cannot be mapped as we tokenize differently

        Assert.assertThat(tokens[5].getChunkTags().get(0), CoreMatchers.is(new ChunkTag("I-VP")));
    }

    @Test
    public void testTokenize() throws Exception {
        EnglishChunker chunker = new EnglishChunker();
        String expected = "[I, 'm, going, to, London]";
        Assert.assertThat(Arrays.toString(chunker.tokenize("I'm going to London")), CoreMatchers.is(expected));
        Assert.assertThat(Arrays.toString(chunker.tokenize("I?m going to London")), CoreMatchers.is(expected));// different apostrophe char

    }
}

