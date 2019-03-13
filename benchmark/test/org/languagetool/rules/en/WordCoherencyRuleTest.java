/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2005 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.rules.en;


import java.io.IOException;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;


public class WordCoherencyRuleTest {
    private final JLanguageTool lt = new JLanguageTool(new AmericanEnglish());

    @Test
    public void testRule() throws IOException {
        // correct sentences:
        assertGood("He likes archeology. She likes archeology, too.");
        assertGood("He likes archaeology. She likes archaeology, too.");
        // errors:
        assertError("He likes archaeology. She likes archeology, too.");
    }

    @Test
    public void testCallIndependence() throws IOException {
        assertGood("He likes archaeology.");
        assertGood("She likes archeology, too.");// this won't be noticed, the calls are independent of each other

    }

    @Test
    public void testMatchPosition() throws IOException {
        List<RuleMatch> ruleMatches = lt.check("He likes archaeology. She likes archeology, too.");
        MatcherAssert.assertThat(ruleMatches.size(), Is.is(1));
        MatcherAssert.assertThat(ruleMatches.get(0).getFromPos(), Is.is(32));
        MatcherAssert.assertThat(ruleMatches.get(0).getToPos(), Is.is(42));
    }

    @Test
    public void testRuleCompleteTexts() throws IOException {
        Assert.assertEquals(0, lt.check("He likes archaeology. Really? She likes archaeology, too.").size());
        Assert.assertEquals(1, lt.check("He likes archaeology. Really? She likes archeology, too.").size());
        Assert.assertEquals(1, lt.check("He likes archeology. Really? She likes archaeology, too.").size());
        Assert.assertEquals(1, lt.check("Mix of upper case and lower case: Westernize and westernise.").size());
    }
}

