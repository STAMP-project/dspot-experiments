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
package org.languagetool.rules.de;


import GermanToken.POSType.DETERMINER;
import GermanToken.POSType.NOMEN;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.AnalyzedToken;
import org.languagetool.AnalyzedTokenReadings;


public class GermanHelperTest {
    @Test
    public void testHasReadingOfType() throws Exception {
        AnalyzedTokenReadings readings = new AnalyzedTokenReadings(new AnalyzedToken("der", "ART:DEF:DAT:SIN:FEM", null), 0);
        Assert.assertTrue(GermanHelper.hasReadingOfType(readings, DETERMINER));
        Assert.assertFalse(GermanHelper.hasReadingOfType(readings, NOMEN));
    }

    @Test
    public void testGetDeterminerNumber() throws Exception {
        Assert.assertThat(GermanHelper.getDeterminerNumber("ART:DEF:DAT:SIN:FEM"), CoreMatchers.is("SIN"));
    }

    @Test
    public void testGetDeterminerDefiniteness() throws Exception {
        Assert.assertThat(GermanHelper.getDeterminerDefiniteness("ART:DEF:DAT:SIN:FEM"), CoreMatchers.is("DEF"));
    }

    @Test
    public void testGetDeterminerCase() throws Exception {
        Assert.assertThat(GermanHelper.getDeterminerCase("ART:DEF:DAT:SIN:FEM"), CoreMatchers.is("DAT"));
    }

    @Test
    public void testGetDeterminerGender() throws Exception {
        Assert.assertThat(GermanHelper.getDeterminerGender(null), CoreMatchers.is(""));
        Assert.assertThat(GermanHelper.getDeterminerGender(""), CoreMatchers.is(""));
        Assert.assertThat(GermanHelper.getDeterminerGender("ART:DEF:DAT:SIN:FEM"), CoreMatchers.is("FEM"));
    }
}

