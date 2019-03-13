/**
 * LanguageTool, a natural language style checker
 *  * Copyright (C) 2018 Fabian Richter
 *  *
 *  * This library is free software; you can redistribute it and/or
 *  * modify it under the terms of the GNU Lesser General Public
 *  * License as published by the Free Software Foundation; either
 *  * version 2.1 of the License, or (at your option) any later version.
 *  *
 *  * This library is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  * Lesser General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU Lesser General Public
 *  * License along with this library; if not, write to the Free Software
 *  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 *  * USA
 */
package org.languagetool.rules.de;


import java.io.IOException;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.Languages;
import org.languagetool.rules.UnitConversionRuleTestHelper;


public class UnitConversionRuleTest {
    /* Still problematic:
    Der Weg ist 10 km (20 Meilen) lang.
    6'682 Hektar
    zahlende Geb?hr betrug bis zum 4. Juli 2005 5 Pfund,
    7,92 inch = 0,201168 m = 20,1168 cm
    Brennwert 210 kJ/100 g (50 kcal/100 g).
    69.852 Fu? (?ber 21 Kilometer)
    Als inoffizieller Nachfolger der 64'er
    ihre Flugh?he lag bei bis zu 18.000?m (60.000 ft).
    5.808,5?km (3.610 Meilen)
    3 000 Meilen lang
     */
    private final UnitConversionRuleTestHelper unitConversionRuleTestHelper = new UnitConversionRuleTestHelper();

    @Test
    public void match() throws IOException {
        Language lang = Languages.getLanguageForShortCode("de");
        JLanguageTool lt = new JLanguageTool(lang);
        UnitConversionRule rule = new UnitConversionRule(JLanguageTool.getMessageBundle(lang));
        assertMatches("Ich bin 6 Fu? gro?.", 1, "1,83 Meter", rule, lt);
        assertMatches("Ich bin 6 Fu? (2,02 m) gro?.", 1, "1,83 Meter", rule, lt);
        assertMatches("Ich bin 6 Fu? (1,82 m) gro?.", 0, null, rule, lt);
        assertMatches("Der Weg ist 100 Meilen lang.", 1, "160,93 Kilometer", rule, lt);
        assertMatches("Der Weg ist 10 km (20 Meilen) lang.", 1, "6,21", rule, lt);
        assertMatches("Der Weg ist 10 km (6,21 Meilen) lang.", 0, null, rule, lt);
        assertMatches("Der Weg ist 100 Meilen (160,93 Kilometer) lang.", 0, null, rule, lt);
        assertMatches("Die Ladung ist 10.000,75 Pfund schwer.", 1, "4,54 Tonnen", rule, lt);
        assertMatches("Sie ist 5\'6\" gro\u00df.", 1, "1,68 m", rule, lt);
        assertMatches("Meine neue Wohnung ist 500 sq ft gro?.", 1, "46,45 Quadratmeter", rule, lt);
    }
}

