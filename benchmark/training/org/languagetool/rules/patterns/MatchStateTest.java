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
package org.languagetool.rules.patterns;


import Match.CaseConversion.PRESERVE;
import Match.CaseConversion.STARTLOWER;
import Match.CaseConversion.STARTUPPER;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.FakeLanguage;


public class MatchStateTest {
    @Test
    public void testConvertCase() {
        MatchState startUpper = getMatchState(STARTUPPER);
        Assert.assertNull(startUpper.convertCase(null, "Y", new FakeLanguage("en")));
        Assert.assertThat(startUpper.convertCase("", "Y", new FakeLanguage("en")), CoreMatchers.is(""));
        Assert.assertThat(startUpper.convertCase("x", "Y", new FakeLanguage("en")), CoreMatchers.is("X"));
        Assert.assertThat(startUpper.convertCase("xxx", "Yyy", new FakeLanguage("en")), CoreMatchers.is("Xxx"));
        // special case for Dutch:
        Assert.assertThat(startUpper.convertCase("ijsselmeer", "Uppercase", new FakeLanguage("nl")), CoreMatchers.is("IJsselmeer"));
        Assert.assertThat(startUpper.convertCase("ijsselmeer", "lowercase", new FakeLanguage("nl")), CoreMatchers.is("IJsselmeer"));
        Assert.assertThat(startUpper.convertCase("ij", "Uppercase", new FakeLanguage("nl")), CoreMatchers.is("IJ"));
        MatchState preserve = getMatchState(PRESERVE);
        Assert.assertThat(preserve.convertCase("xxx", "Yyy", new FakeLanguage("en")), CoreMatchers.is("Xxx"));
        Assert.assertThat(preserve.convertCase("xxx", "yyy", new FakeLanguage("en")), CoreMatchers.is("xxx"));
        Assert.assertThat(preserve.convertCase("xxx", "YYY", new FakeLanguage("en")), CoreMatchers.is("XXX"));
        // special case for Dutch:
        Assert.assertThat(preserve.convertCase("ijsselmeer", "Uppercase", new FakeLanguage("nl")), CoreMatchers.is("IJsselmeer"));
        Assert.assertThat(preserve.convertCase("ijsselmeer", "lowercase", new FakeLanguage("nl")), CoreMatchers.is("ijsselmeer"));
        Assert.assertThat(preserve.convertCase("ijsselmeer", "ALLUPPER", new FakeLanguage("nl")), CoreMatchers.is("IJSSELMEER"));
        MatchState startLower = getMatchState(STARTLOWER);
        Assert.assertThat(startLower.convertCase("xxx", "YYY", new FakeLanguage("en")), CoreMatchers.is("xxx"));
        Assert.assertThat(startLower.convertCase("xxx", "yyy", new FakeLanguage("en")), CoreMatchers.is("xxx"));
        Assert.assertThat(startLower.convertCase("xxx", "Yyy", new FakeLanguage("en")), CoreMatchers.is("xxx"));
        Assert.assertThat(startLower.convertCase("XXX", "Yyy", new FakeLanguage("en")), CoreMatchers.is("xXX"));
        Assert.assertThat(startLower.convertCase("Xxx", "Yyy", new FakeLanguage("en")), CoreMatchers.is("xxx"));
    }
}

