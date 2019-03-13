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
package org.languagetool.dev.wikipedia.atom;


import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.Language;
import org.languagetool.Languages;


public class ChangeAnalysisTest {
    private static final Language LANGUAGE = Languages.getLanguageForShortCode("de");

    @Test
    public void testAdd() {
        List<WikipediaRuleMatch> oldMatches = makeMatches(1, 2);
        List<WikipediaRuleMatch> newMatches = makeMatches(1, 2, 3);
        ChangeAnalysis analysis = new ChangeAnalysis("fakeTitle", 123L, oldMatches, newMatches);
        Assert.assertThat(analysis.getAddedMatches().size(), CoreMatchers.is(1));
        Assert.assertThat(analysis.getAddedMatches().get(0).getRule().getId(), CoreMatchers.is("ID_3"));
        Assert.assertThat(analysis.getRemovedMatches().size(), CoreMatchers.is(0));
    }

    @Test
    public void testRemove() {
        List<WikipediaRuleMatch> oldMatches = makeMatches(1, 2, 3);
        List<WikipediaRuleMatch> newMatches = makeMatches(1, 2);
        ChangeAnalysis analysis = new ChangeAnalysis("fakeTitle", 123L, oldMatches, newMatches);
        Assert.assertThat(analysis.getAddedMatches().size(), CoreMatchers.is(0));
        Assert.assertThat(analysis.getRemovedMatches().size(), CoreMatchers.is(1));
        Assert.assertThat(analysis.getRemovedMatches().get(0).getRule().getId(), CoreMatchers.is("ID_3"));
    }

    @Test
    public void testMove() {
        List<WikipediaRuleMatch> oldMatches = makeMatches(1, 2, 3);
        List<WikipediaRuleMatch> newMatches = makeMatches(1, 3, 2);
        ChangeAnalysis analysis = new ChangeAnalysis("fakeTitle", 123L, oldMatches, newMatches);
        Assert.assertThat(analysis.getAddedMatches().size(), CoreMatchers.is(1));
        Assert.assertThat(analysis.getAddedMatches().get(0).getRule().getId(), CoreMatchers.is("ID_2"));
        Assert.assertThat(analysis.getRemovedMatches().size(), CoreMatchers.is(1));
        Assert.assertThat(analysis.getRemovedMatches().get(0).getRule().getId(), CoreMatchers.is("ID_2"));
    }
}

