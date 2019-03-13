/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2015 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.languagemodel;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.rules.ngrams.Probability;


@SuppressWarnings("MagicNumber")
public class MultiLanguageModelTest {
    @Test
    public void test() {
        try (LanguageModel lm1 = new MultiLanguageModelTest.FakeLanguageModel(0.5F);LanguageModel lm2 = new MultiLanguageModelTest.FakeLanguageModel(0.2F);MultiLanguageModel lm = new MultiLanguageModel(Arrays.asList(lm1, lm2))) {
            List<String> ngram = Arrays.asList("foo", "bar", "blah");
            Assert.assertEquals(0.7F, lm.getPseudoProbability(ngram).getProb(), 0.01F);
            Assert.assertEquals(0.5F, lm.getPseudoProbability(ngram).getCoverage(), 0.01F);
        }
    }

    private static class FakeLanguageModel implements LanguageModel {
        private final float fakeValue;

        FakeLanguageModel(float fakeValue) {
            this.fakeValue = fakeValue;
        }

        @Override
        public Probability getPseudoProbability(List<String> context) {
            return new Probability(fakeValue, 0.5F);
        }

        @Override
        public void close() {
        }
    }
}

