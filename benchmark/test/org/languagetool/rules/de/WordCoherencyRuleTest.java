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
package org.languagetool.rules.de;


import java.io.IOException;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.language.GermanyGerman;
import org.languagetool.rules.RuleMatch;


public class WordCoherencyRuleTest {
    private final JLanguageTool lt = new JLanguageTool(new GermanyGerman());

    @Test
    public void testRule() throws IOException {
        // correct sentences:
        assertGood("Das ist aufwendig, aber nicht zu aufwendig.");
        assertGood("Das ist aufwendig. Aber nicht zu aufwendig.");
        assertGood("Das ist aufw?ndig, aber nicht zu aufw?ndig.");
        assertGood("Das ist aufw?ndig. Aber nicht zu aufw?ndig.");
        // errors:
        assertError("Das ist aufwendig, aber nicht zu aufw?ndig.");
        assertError("Das ist aufwendig. Aber nicht zu aufw?ndig.");
        assertError("Das ist aufwendiger, aber nicht zu aufw?ndig.");
        assertError("Das ist aufwendiger. Aber nicht zu aufw?ndig.");
        assertError("Das ist aufw?ndig, aber nicht zu aufwendig.");
        assertError("Das ist aufw?ndig. Aber nicht zu aufwendig.");
        assertError("Das ist aufw?ndiger, aber nicht zu aufwendig.");
        assertError("Das ist aufw?ndiger. Aber nicht zu aufwendig.");
        assertError("Delfin und Delphin");
        assertError("Delfins und Delphine");
        assertError("essentiell und essenziell");
        assertError("essentieller und essenzielles");
        assertError("Differential und Differenzial");
        assertError("Differentials und Differenzials");
        assertError("Facette und Fassette");
        assertError("Facetten und Fassetten");
        assertError("Joghurt und Jogurt");
        assertError("Joghurts und Jogurt");
        assertError("Joghurt und Jogurts");
        assertError("Joghurts und Jogurts");
        assertError("Ketchup und Ketschup");
        assertError("Ketchups und Ketschups");
        assertError("Kommuniqu? und Kommunikee");
        assertError("Kommuniqu?s und Kommunikees");
        assertError("Necessaire und Nessess?r");
        assertError("Necessaires und Nessess?rs");
        assertError("Orthographie und Orthografie");
        assertError("Orthographien und Orthografien");
        assertError("Potential und Potenzial");
        assertError("Potentials und Potenziale");
        assertError("Portemonnaie und Portmonee");
        assertError("Portemonnaies und Portmonees");
        assertError("potentiell und potenziell");
        assertError("potentielles und potenzieller");
        assertError("Schenke und Sch?nke");
        // see TODO comment in WordCoherencyRule:
        // assertError("Schenken und Sch?nken");
        assertError("substantiell und substanziell");
        assertError("substantieller und substanzielles");
        assertError("Thunfisch und Tunfisch");
        assertError("Thunfische und Tunfische");
        assertError("Xylophon und Xylofon");
        assertError("Xylophone und Xylofone");
        assertError("selbst?ndig und selbstst?ndig");
        assertError("selbst?ndiges und selbstst?ndiger");
        assertError("Bahnhofsplatz und Bahnhofplatz");
        // TODO: known to fail because jWordSplitters list is not complete:
        // assertError("Testketchup und Testketschup");
    }

    @Test
    public void testCallIndependence() throws IOException {
        assertGood("Das ist aufwendig.");
        assertGood("Aber nicht zu aufw?ndig.");// this won't be noticed, the calls are independent of each other

    }

    @Test
    public void testMatchPosition() throws IOException {
        List<RuleMatch> ruleMatches = lt.check("Das ist aufwendig. Aber nicht zu aufw?ndig");
        MatcherAssert.assertThat(ruleMatches.size(), Is.is(1));
        MatcherAssert.assertThat(ruleMatches.get(0).getFromPos(), Is.is(33));
        MatcherAssert.assertThat(ruleMatches.get(0).getToPos(), Is.is(42));
    }

    @Test
    public void testRuleCompleteTexts() throws IOException {
        Assert.assertEquals(0, lt.check("Das ist aufw?ndig. Aber hallo. Es ist wirklich aufw?ndig.").size());
        Assert.assertEquals(1, lt.check("Das ist aufwendig. Aber hallo. Es ist wirklich aufw?ndig.").size());
        Assert.assertEquals(1, lt.check("Das ist aufw?ndig. Aber hallo. Es ist wirklich aufwendig.").size());
        // also find full forms:
        Assert.assertEquals(0, lt.check("Das ist aufwendig. Aber hallo. Es ist wirklich aufwendiger als so.").size());
        Assert.assertEquals(1, lt.check("Das ist aufwendig. Aber hallo. Es ist wirklich aufw?ndiger als so.").size());
        Assert.assertEquals(1, lt.check("Das ist aufw?ndig. Aber hallo. Es ist wirklich aufwendiger als so.").size());
        Assert.assertEquals(1, lt.check("Das ist das aufw?ndigste. Aber hallo. Es ist wirklich aufwendiger als so.").size());
        Assert.assertEquals(1, lt.check("Das ist das aufw?ndigste. Aber hallo. Es ist wirklich aufwendig.").size());
        // cross-paragraph checks
        Assert.assertEquals(1, lt.check("Das ist das aufw\u00e4ndigste.\n\nAber hallo. Es ist wirklich aufwendig.").size());
    }
}

