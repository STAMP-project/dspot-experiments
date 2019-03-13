/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2008 Daniel Naber (http://www.danielnaber.de)
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
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.language.GermanyGerman;
import org.languagetool.rules.GenericUnpairedBracketsRule;


public class GenericUnpairedBracketsRuleTest {
    private GenericUnpairedBracketsRule rule;

    private JLanguageTool lt;

    @Test
    public void testGermanRule() throws IOException {
        lt = new JLanguageTool(new GermanyGerman());
        rule = org.languagetool.rules.GenericUnpairedBracketsRuleTest.getBracketsRule(lt);
        // correct sentences:
        assertMatches("(Das sind die S?tze, die sie testen sollen).", 0);
        assertMatches("(Das sind die ?S?tze?, die sie testen sollen).", 0);
        assertMatches("(Das sind die ?S?tze?, die sie testen sollen).", 0);
        assertMatches("(Das sind die S?tze (noch mehr Klammern [schon wieder!]), die sie testen sollen).", 0);
        assertMatches("Das ist ein Satz mit Smiley :-)", 0);
        assertMatches("Das ist auch ein Satz mit Smiley ;-)", 0);
        assertMatches("Das ist ein Satz mit Smiley :)", 0);
        assertMatches("Das ist ein Satz mit Smiley :(", 0);
        // incorrect sentences:
        assertMatches("Die ?S?tze zum Testen.", 1);
        assertMatches("Die ?S?tze zum Testen.", 1);
        assertMatches("Die ?S?tze zum Testen.", 1);
    }
}

