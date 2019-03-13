/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2016 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.rules.ngrams;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedToken;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.JLanguageTool;
import org.languagetool.language.Demo;
import org.languagetool.tagging.Tagger;
import org.languagetool.tokenizers.WordTokenizer;


public class GoogleTokenTest {
    @Test
    public void testTokenization() {
        List<GoogleToken> tokens = GoogleToken.getGoogleTokens("This, isn't a test.", false, new GoogleTokenTest.MyWordTokenizer());
        MatcherAssert.assertThat(tokens.get(0).token, CoreMatchers.is("This"));
        MatcherAssert.assertThat(tokens.get(0).posTags.toString(), CoreMatchers.is("[]"));
        MatcherAssert.assertThat(tokens.get(1).token, CoreMatchers.is(","));
        MatcherAssert.assertThat(tokens.get(2).token, CoreMatchers.is("isn"));
        MatcherAssert.assertThat(tokens.get(3).token, CoreMatchers.is("'t"));
        MatcherAssert.assertThat(tokens.get(4).token, CoreMatchers.is("a"));
        MatcherAssert.assertThat(tokens.get(5).token, CoreMatchers.is("test"));
        MatcherAssert.assertThat(tokens.get(6).token, CoreMatchers.is("."));
    }

    @Test
    public void testTokenizationWithPosTag() throws IOException {
        JLanguageTool lt = new JLanguageTool(new GoogleTokenTest.PosTaggingDemo());
        AnalyzedSentence analyzedSentence = lt.getAnalyzedSentence("This, isn't a test.");
        List<GoogleToken> tokens = GoogleToken.getGoogleTokens(analyzedSentence, false, new GoogleTokenTest.MyWordTokenizer());
        MatcherAssert.assertThat(tokens.get(0).token, CoreMatchers.is("This"));
        MatcherAssert.assertThat(tokens.get(0).posTags.toString(), CoreMatchers.is("[This/DT]"));
        MatcherAssert.assertThat(tokens.get(1).token, CoreMatchers.is(","));
        MatcherAssert.assertThat(tokens.get(1).posTags.toString(), CoreMatchers.is("[,/null]"));
        MatcherAssert.assertThat(tokens.get(2).token, CoreMatchers.is("isn"));
        MatcherAssert.assertThat(tokens.get(3).token, CoreMatchers.is("'t"));
        MatcherAssert.assertThat(tokens.get(4).token, CoreMatchers.is("a"));
        MatcherAssert.assertThat(tokens.get(5).token, CoreMatchers.is("test"));
        MatcherAssert.assertThat(tokens.get(5).posTags.toString(), CoreMatchers.is("[test/NN]"));
        MatcherAssert.assertThat(tokens.get(6).token, CoreMatchers.is("."));
    }

    class PosTaggingDemo extends Demo {
        @Override
        public Tagger getTagger() {
            return new Tagger() {
                @Override
                public List<AnalyzedTokenReadings> tag(List<String> sentenceTokens) {
                    List<AnalyzedTokenReadings> tokenReadings = new ArrayList<>();
                    int pos = 0;
                    for (String word : sentenceTokens) {
                        List<AnalyzedToken> l = new ArrayList<>();
                        switch (word) {
                            case "This" :
                                l.add(new AnalyzedToken(word, "DT", word));
                                break;
                            case "is" :
                                l.add(new AnalyzedToken(word, "VBZ", word));
                                break;
                            case "test" :
                                l.add(new AnalyzedToken(word, "NN", word));
                                break;
                            default :
                                l.add(new AnalyzedToken(word, null, word));
                                break;
                        }
                        tokenReadings.add(new AnalyzedTokenReadings(l, pos));
                        pos += word.length();
                    }
                    return tokenReadings;
                }

                @Override
                public AnalyzedTokenReadings createNullToken(String token, int startPos) {
                    return null;
                }

                @Override
                public AnalyzedToken createToken(String token, String posTag) {
                    return null;
                }
            };
        }
    }

    private class MyWordTokenizer extends WordTokenizer {
        @Override
        public List<String> tokenize(String text) {
            List<String> tokens = super.tokenize(text);
            String prev = null;
            Stack<String> l = new Stack<>();
            for (String token : tokens) {
                if ("'".equals(prev)) {
                    if (token.equals("t")) {
                        l.pop();
                        l.push("'t");
                    } else {
                        l.push(token);
                    }
                } else {
                    l.push(token);
                }
                prev = token;
            }
            return l;
        }
    }
}

