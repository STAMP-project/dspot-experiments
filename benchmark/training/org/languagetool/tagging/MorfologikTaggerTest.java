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
package org.languagetool.tagging;


import java.net.URL;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MorfologikTaggerTest {
    @Test
    public void testTag() {
        URL url = MorfologikTaggerTest.class.getResource("/org/languagetool/tagging/test.dict");
        MorfologikTagger tagger = new MorfologikTagger(url);
        List<TaggedWord> result1 = tagger.tag("lowercase");
        Assert.assertThat(result1.size(), CoreMatchers.is(2));
        Assert.assertThat(result1.get(0).getLemma(), CoreMatchers.is("lclemma"));
        Assert.assertThat(result1.get(0).getPosTag(), CoreMatchers.is("POS1"));
        Assert.assertThat(result1.get(1).getLemma(), CoreMatchers.is("lclemma2"));
        Assert.assertThat(result1.get(1).getPosTag(), CoreMatchers.is("POS1a"));
        List<TaggedWord> result2 = tagger.tag("Lowercase");
        Assert.assertThat(result2.size(), CoreMatchers.is(0));
        List<TaggedWord> result3 = tagger.tag("sch?n");
        Assert.assertThat(result3.size(), CoreMatchers.is(1));
        Assert.assertThat(result3.get(0).getLemma(), CoreMatchers.is("testlemma"));
        Assert.assertThat(result3.get(0).getPosTag(), CoreMatchers.is("POSTEST"));
        List<TaggedWord> noResult = tagger.tag("noSuchWord");
        Assert.assertThat(noResult.size(), CoreMatchers.is(0));
    }
}

