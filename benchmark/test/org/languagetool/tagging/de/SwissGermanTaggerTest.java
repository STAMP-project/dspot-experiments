/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2007 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.tagging.de;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.AnalyzedTokenReadings;


public class SwissGermanTaggerTest {
    @Test
    public void testTagger() throws IOException {
        GermanTagger swissTagger = new SwissGermanTagger();
        GermanTagger germanTagger = new GermanTagger();
        AnalyzedTokenReadings aToken = swissTagger.lookup("gross");
        Assert.assertEquals("gross[gro?/ADJ:PRD:GRU]", GermanTaggerTest.toSortedString(aToken));
        Assert.assertEquals("gro?", aToken.getReadings().get(0).getLemma());
        AnalyzedTokenReadings aToken2 = swissTagger.lookup("Anmassung");
        Assert.assertEquals(("Anmassung[Anma?ung/SUB:AKK:SIN:FEM, " + (("Anma?ung/SUB:DAT:SIN:FEM, " + "Anma?ung/SUB:GEN:SIN:FEM, ") + "Anma?ung/SUB:NOM:SIN:FEM]")), GermanTaggerTest.toSortedString(aToken2));
        Assert.assertEquals("Anma?ung", aToken2.getReadings().get(0).getLemma());
        Assert.assertEquals(swissTagger.lookup("die"), germanTagger.lookup("die"));
        Assert.assertEquals(swissTagger.lookup("Auto"), germanTagger.lookup("Auto"));
    }
}

