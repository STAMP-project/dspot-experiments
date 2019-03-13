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
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.FakeLanguage;
import org.languagetool.JLanguageTool;


public class GenericUnpairedBracketsRuleTest {
    private JLanguageTool lt;

    @Test
    public void testRule() throws IOException {
        setUpRule(new FakeLanguage());
        assertMatches(0, "This is ?correct?.");
        assertMatches(0, "\u00bbCorrect\u00ab\n\u00bbAnd \u00bbhere\u00ab it ends.\u00ab");
        assertMatches(0, "?Correct. This is more than one sentence.?");
        assertMatches(0, "\u00bbCorrect. This is more than one sentence.\u00ab\n\u00bbAnd \u00bbhere\u00ab it ends.\u00ab");
        assertMatches(0, "\u00bbCorrect\u00ab\n\n\u00bbAnd here it ends.\u00ab\n\nMore text.");
        assertMatches(0, "?Correct, he said. This is the next sentence.? Here's another sentence.");
        assertMatches(0, "\u00bbCorrect, he said.\n\nThis is the next sentence.\u00ab Here\'s another sentence.");
        assertMatches(0, "\u00bbCorrect, he said.\n\n\n\nThis is the next sentence.\u00ab Here\'s another sentence.");
        assertMatches(0, "This ?is also ?correct??.");
        assertMatches(0, "Good.\n\nThis \u00bbis also \u00bbcorrect\u00ab\u00ab.");
        assertMatches(0, "Good.\n\n\nThis \u00bbis also \u00bbcorrect\u00ab\u00ab.");
        assertMatches(0, "Good.\n\n\n\nThis \u00bbis also \u00bbcorrect\u00ab\u00ab.");
        assertMatches(0, "This is funny :-)");
        assertMatches(0, "This is sad :-( isn't it");
        assertMatches(0, "This is funny :)");
        assertMatches(0, "This is sad :( isn't it");
        assertMatches(1, "This is not correct?");
        assertMatches(1, "This is ?not correct");
        assertMatches(1, "This is correct.\n\n\u00bbBut this is not.");
        assertMatches(1, "This is correct.\n\nBut this is not\u00ab");
        assertMatches(1, "\u00bbThis is correct\u00ab\n\nBut this is not\u00ab");
        assertMatches(1, "\u00bbThis is correct\u00ab\n\nBut this \u00bbis\u00ab not\u00ab");
        assertMatches(1, "This is not correct. No matter if it's more than one sentence?");
        assertMatches(1, "?This is not correct. No matter if it's more than one sentence");
        assertMatches(1, "Correct, he said. This is the next sentence.? Here's another sentence.");
        assertMatches(1, "?Correct, he said. This is the next sentence. Here's another sentence.");
        assertMatches(1, "\u00bbCorrect, he said. This is the next sentence.\n\nHere\'s another sentence.");
        assertMatches(1, "\u00bbCorrect, he said. This is the next sentence.\n\n\n\nHere\'s another sentence.");
    }

    @Test
    public void testRuleMatchPositions() throws IOException {
        setUpRule(new FakeLanguage());
        RuleMatch match1 = lt.check("This ?is a test.").get(0);
        Assert.assertThat(match1.getFromPos(), CoreMatchers.is(5));
        Assert.assertThat(match1.getToPos(), CoreMatchers.is(6));
        Assert.assertThat(match1.getLine(), CoreMatchers.is(0));
        Assert.assertThat(match1.getEndLine(), CoreMatchers.is(0));
        Assert.assertThat(match1.getColumn(), CoreMatchers.is(5));
        Assert.assertThat(match1.getEndColumn(), CoreMatchers.is(6));
        RuleMatch match2 = lt.check("This.\nSome stuff.\nIt \u00bbis a test.").get(0);
        Assert.assertThat(match2.getFromPos(), CoreMatchers.is(21));
        Assert.assertThat(match2.getToPos(), CoreMatchers.is(22));
        Assert.assertThat(match2.getLine(), CoreMatchers.is(2));// first line is 0

        Assert.assertThat(match2.getEndLine(), CoreMatchers.is(2));
        Assert.assertThat(match2.getColumn(), CoreMatchers.is(4));
        Assert.assertThat(match2.getEndColumn(), CoreMatchers.is(5));
    }
}

