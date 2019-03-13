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


public class MorfologikSpellerTest {
    @Test
    public void testIsMisspelled() throws IOException {
        MorfologikSpeller speller = new MorfologikSpeller("/xx/spelling/test.dict");
        Assert.assertTrue(speller.convertsCase());
        Assert.assertFalse(speller.isMisspelled("wordone"));
        Assert.assertFalse(speller.isMisspelled("Wordone"));
        Assert.assertFalse(speller.isMisspelled("wordtwo"));
        Assert.assertFalse(speller.isMisspelled("Wordtwo"));
        Assert.assertFalse(speller.isMisspelled("Uppercase"));
        Assert.assertFalse(speller.isMisspelled("H?user"));
        Assert.assertTrue(speller.isMisspelled("Hauser"));
        Assert.assertTrue(speller.isMisspelled("wordones"));
        Assert.assertTrue(speller.isMisspelled("nosuchword"));
    }

    @Test
    public void testGetSuggestions() throws IOException {
        MorfologikSpeller spellerDist1 = new MorfologikSpeller("/xx/spelling/test.dict", 1);
        MorfologikSpeller spellerDist2 = new MorfologikSpeller("/xx/spelling/test.dict", 2);
        Assert.assertThat(spellerDist1.getSuggestions("wordone").toString(), Is.is("[]"));
        Assert.assertThat(spellerDist1.getSuggestions("wordonex").toString(), Is.is("[wordone]"));
        Assert.assertThat(spellerDist2.getSuggestions("wordone").toString(), Is.is("[]"));
        Assert.assertThat(spellerDist2.getSuggestions("wordonex").toString(), Is.is("[wordone]"));
        Assert.assertThat(spellerDist1.getSuggestions("wordonix").toString(), Is.is("[]"));
        Assert.assertThat(spellerDist2.getSuggestions("wordonix").toString(), Is.is("[wordone]"));
        Assert.assertThat(spellerDist2.getSuggestions("wordoxix").toString(), Is.is("[]"));
    }
}

