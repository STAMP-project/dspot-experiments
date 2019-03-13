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
package org.languagetool.languagemodel;


import java.io.File;
import java.net.URL;
import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.languagetool.JLanguageTool;


public class LuceneSingleIndexLanguageModelTest extends LanguageModelTest {
    @Test
    public void testLanguageModel() throws Exception {
        URL ngramUrl = JLanguageTool.getDataBroker().getFromResourceDirAsUrl("/yy/ngram-index");
        try (LuceneLanguageModel model = new LuceneLanguageModel(new File(ngramUrl.getFile()))) {
            MatcherAssert.assertThat(model.getCount("the"), CoreMatchers.is(55L));
            MatcherAssert.assertThat(model.getCount(Arrays.asList("the", "nice")), CoreMatchers.is(3L));
            MatcherAssert.assertThat(model.getCount(Arrays.asList("the", "nice", "building")), CoreMatchers.is(1L));
            MatcherAssert.assertThat(model.getCount("not-in-here"), CoreMatchers.is(0L));
            MatcherAssert.assertThat(model.getTotalTokenCount(), CoreMatchers.is(3L));
        }
    }
}

