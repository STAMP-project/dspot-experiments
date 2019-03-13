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
package org.languagetool.rules.spelling.morfologik;


import java.io.IOException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class MorfologikMultiSpellerTest {
    @Test
    public void testIsMisspelled() throws IOException {
        MorfologikMultiSpeller speller = getSpeller();
        // from test.dict:
        Assert.assertFalse(speller.isMisspelled("wordone"));
        Assert.assertFalse(speller.isMisspelled("wordtwo"));
        // from test2.txt:
        Assert.assertFalse(speller.isMisspelled("Abc"));
        Assert.assertFalse(speller.isMisspelled("wordthree"));
        Assert.assertFalse(speller.isMisspelled("wordfour"));
        Assert.assertFalse(speller.isMisspelled("?blich"));
        Assert.assertFalse(speller.isMisspelled("sch?n"));
        Assert.assertFalse(speller.isMisspelled("F?n"));
        Assert.assertFalse(speller.isMisspelled("F?n"));
        Assert.assertFalse(speller.isMisspelled("F?n"));
        // from both test.dict and test2.txt:
        Assert.assertFalse(speller.isMisspelled("H?user"));
        // not in any of the files:
        Assert.assertTrue(speller.isMisspelled("notthere"));
        Assert.assertTrue(speller.isMisspelled("Fun"));
        Assert.assertTrue(speller.isMisspelled("F?ns"));
        Assert.assertTrue(speller.isMisspelled("AF?n"));
    }

    @Test
    public void testGetSuggestions() throws IOException {
        MorfologikMultiSpeller speller = getSpeller();
        Assert.assertThat(speller.getSuggestions("wordone").toString(), Is.is("[]"));// a non-misspelled word

        Assert.assertThat(speller.getSuggestions("wordones").toString(), Is.is("[wordone]"));
        Assert.assertThat(speller.getSuggestions("Abd").toString(), Is.is("[Abc]"));
        Assert.assertThat(speller.getSuggestions("Fxn").toString(), Is.is("[F?n, F?n, F?n]"));
        Assert.assertThat(speller.getSuggestions("H?users").toString(), Is.is("[H?user]"));
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidFileName() throws IOException {
        new MorfologikMultiSpeller("/xx/spelling/test.dict.README", "/xx/spelling/test2.txt", null, 1);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidFile() throws IOException {
        new MorfologikMultiSpeller("/xx/spelling/no-such-file", "/xx/spelling/test2.txt", null, 1);
    }
}

