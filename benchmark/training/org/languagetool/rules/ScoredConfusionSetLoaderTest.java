/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2014 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.rules;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.languagetool.JLanguageTool;


@SuppressWarnings("QuestionableName")
public class ScoredConfusionSetLoaderTest {
    @Test
    public void testLoadConfusionSet() throws IOException {
        try (InputStream inputStream = JLanguageTool.getDataBroker().getFromResourceDirAsStream("/yy/neuralnetwork_confusion_sets.txt")) {
            List<ScoredConfusionSet> list = ScoredConfusionSetLoader.loadConfusionSet(inputStream);
            MatcherAssert.assertThat(list.size(), CoreMatchers.is(6));
            MatcherAssert.assertThat(list.get(0).getConfusionTokens().size(), CoreMatchers.is(2));
            MatcherAssert.assertThat(list.get(0).getConfusionTokens().get(0), CoreMatchers.is("their"));
            MatcherAssert.assertThat(list.get(0).getConfusionTokens().get(1), CoreMatchers.is("there"));
            MatcherAssert.assertThat(list.get(0).getTokenDescriptions().size(), CoreMatchers.is(2));
            MatcherAssert.assertThat(list.get(0).getTokenDescriptions().get(0).orElse("fail"), CoreMatchers.is("example 2"));
            MatcherAssert.assertThat(list.get(0).getTokenDescriptions().get(1).orElse("fail"), CoreMatchers.is("example 1"));
            MatcherAssert.assertThat(list.get(1).getTokenDescriptions().get(1).orElse("ok"), CoreMatchers.is("ok"));
            MatcherAssert.assertThat(list.get(0).getScore(), CoreMatchers.is(5.0F));
            MatcherAssert.assertThat(list.get(1).getScore(), CoreMatchers.is(1.1F));
            MatcherAssert.assertThat(list.get(2).getScore(), CoreMatchers.is(0.5F));
            MatcherAssert.assertThat(list.get(3).getScore(), CoreMatchers.is(0.8F));
            MatcherAssert.assertThat(list.get(4).getScore(), CoreMatchers.is(1.2F));
            MatcherAssert.assertThat(list.get(5).getScore(), CoreMatchers.is(1.0F));
            MatcherAssert.assertThat(list.get(5).getConfusionTokens().get(0), CoreMatchers.is("im"));
        }
    }
}

