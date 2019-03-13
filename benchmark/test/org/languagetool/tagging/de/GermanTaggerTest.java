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
package org.languagetool.tagging.de;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import morfologik.stemming.DictionaryLookup;
import morfologik.stemming.WordData;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.AnalyzedToken;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.JLanguageTool;


@SuppressWarnings("ConstantConditions")
public class GermanTaggerTest {
    @Test
    public void testLemmaOfForDashCompounds() throws IOException {
        GermanTagger tagger = new GermanTagger();
        AnalyzedTokenReadings aToken = tagger.lookup("Zahn-Arzt-Verband");
        List<String> lemmas = new ArrayList<>();
        for (AnalyzedToken analyzedToken : aToken) {
            lemmas.add(analyzedToken.getLemma());
        }
        Assert.assertTrue(lemmas.contains("Zahnarztverband"));
    }

    @Test
    public void testTagger() throws IOException {
        GermanTagger tagger = new GermanTagger();
        AnalyzedTokenReadings aToken = tagger.lookup("Haus");
        Assert.assertEquals("Haus[Haus/SUB:AKK:SIN:NEU, Haus/SUB:DAT:SIN:NEU, Haus/SUB:NOM:SIN:NEU]", GermanTaggerTest.toSortedString(aToken));
        Assert.assertEquals("Haus", aToken.getReadings().get(0).getLemma());
        Assert.assertEquals("Haus", aToken.getReadings().get(1).getLemma());
        Assert.assertEquals("Haus", aToken.getReadings().get(2).getLemma());
        AnalyzedTokenReadings aToken2 = tagger.lookup("Hauses");
        Assert.assertEquals("Hauses[Haus/SUB:GEN:SIN:NEU]", GermanTaggerTest.toSortedString(aToken2));
        Assert.assertEquals("Haus", aToken2.getReadings().get(0).getLemma());
        Assert.assertNull(tagger.lookup("hauses"));
        Assert.assertNull(tagger.lookup("Gro?"));
        Assert.assertEquals("Lieblingsbuchstabe[Lieblingsbuchstabe/SUB:NOM:SIN:MAS]", GermanTaggerTest.toSortedString(tagger.lookup("Lieblingsbuchstabe")));
        AnalyzedTokenReadings aToken3 = tagger.lookup("gro?er");
        Assert.assertEquals(("gro?er[gro?/ADJ:DAT:SIN:FEM:GRU:SOL, gro?/ADJ:GEN:PLU:FEM:GRU:SOL, gro?/ADJ:GEN:PLU:MAS:GRU:SOL, " + ("gro?/ADJ:GEN:PLU:NEU:GRU:SOL, gro?/ADJ:GEN:SIN:FEM:GRU:SOL, gro?/ADJ:NOM:SIN:MAS:GRU:IND, " + "gro?/ADJ:NOM:SIN:MAS:GRU:SOL]")), GermanTaggerTest.toSortedString(tagger.lookup("gro?er")));
        Assert.assertEquals("gro?", aToken3.getReadings().get(0).getLemma());
        // checks for github issue #635: Some German verbs on the beginning of a sentences are identified only as substantive
        Assert.assertTrue(tagger.tag(Collections.singletonList("Haben"), true).toString().contains("VER"));
        Assert.assertTrue(tagger.tag(Collections.singletonList("K?nnen"), true).toString().contains("VER"));
        Assert.assertTrue(tagger.tag(Collections.singletonList("Gerade"), true).toString().contains("ADJ"));
        // from both german.dict and added.txt:
        AnalyzedTokenReadings aToken4 = tagger.lookup("Interessen");
        Assert.assertEquals(("Interessen[Interesse/SUB:AKK:PLU:NEU, Interesse/SUB:DAT:PLU:NEU, " + "Interesse/SUB:GEN:PLU:NEU, Interesse/SUB:NOM:PLU:NEU]"), GermanTaggerTest.toSortedString(aToken4));
        Assert.assertEquals("Interesse", aToken4.getReadings().get(0).getLemma());
        Assert.assertEquals("Interesse", aToken4.getReadings().get(1).getLemma());
        Assert.assertEquals("Interesse", aToken4.getReadings().get(2).getLemma());
        Assert.assertEquals("Interesse", aToken4.getReadings().get(3).getLemma());
        // words that are not in the dictionary but that are recognized thanks to noun splitting:
        AnalyzedTokenReadings aToken5 = tagger.lookup("Donaudampfschiff");
        Assert.assertEquals(("Donaudampfschiff[Donaudampfschiff/SUB:AKK:SIN:NEU, Donaudampfschiff/SUB:DAT:SIN:NEU, " + "Donaudampfschiff/SUB:NOM:SIN:NEU]"), GermanTaggerTest.toSortedString(aToken5));
        Assert.assertEquals("Donaudampfschiff", aToken5.getReadings().get(0).getLemma());
        Assert.assertEquals("Donaudampfschiff", aToken5.getReadings().get(1).getLemma());
        AnalyzedTokenReadings aToken6 = tagger.lookup("H?userk?mpfe");
        Assert.assertEquals("H?userk?mpfe[H?userkampf/SUB:AKK:PLU:MAS, H?userkampf/SUB:GEN:PLU:MAS, H?userkampf/SUB:NOM:PLU:MAS]", GermanTaggerTest.toSortedString(aToken6));
        Assert.assertEquals("H?userkampf", aToken6.getReadings().get(0).getLemma());
        Assert.assertEquals("H?userkampf", aToken6.getReadings().get(1).getLemma());
        Assert.assertEquals("H?userkampf", aToken6.getReadings().get(2).getLemma());
        AnalyzedTokenReadings aToken7 = tagger.lookup("H?userkampfes");
        Assert.assertEquals("H?userkampfes[H?userkampf/SUB:GEN:SIN:MAS]", GermanTaggerTest.toSortedString(aToken7));
        Assert.assertEquals("H?userkampf", aToken7.getReadings().get(0).getLemma());
        AnalyzedTokenReadings aToken8 = tagger.lookup("H?userkampfs");
        Assert.assertEquals("H?userkampfs[H?userkampf/SUB:GEN:SIN:MAS]", GermanTaggerTest.toSortedString(aToken8));
        Assert.assertEquals("H?userkampf", aToken8.getReadings().get(0).getLemma());
        AnalyzedTokenReadings aToken9 = tagger.lookup("Lieblingsfarben");
        Assert.assertEquals(("Lieblingsfarben[Lieblingsfarbe/SUB:AKK:PLU:FEM, Lieblingsfarbe/SUB:DAT:PLU:FEM, " + "Lieblingsfarbe/SUB:GEN:PLU:FEM, Lieblingsfarbe/SUB:NOM:PLU:FEM]"), GermanTaggerTest.toSortedString(aToken9));
        Assert.assertEquals("Lieblingsfarbe", aToken9.getReadings().get(0).getLemma());
        AnalyzedTokenReadings aToken10 = tagger.lookup("Autolieblingsfarben");
        Assert.assertEquals(("Autolieblingsfarben[Autolieblingsfarbe/SUB:AKK:PLU:FEM, Autolieblingsfarbe/SUB:DAT:PLU:FEM, " + "Autolieblingsfarbe/SUB:GEN:PLU:FEM, Autolieblingsfarbe/SUB:NOM:PLU:FEM]"), GermanTaggerTest.toSortedString(aToken10));
        Assert.assertEquals("Autolieblingsfarbe", aToken10.getReadings().get(0).getLemma());
        AnalyzedTokenReadings aToken11 = tagger.lookup("?brigbleibst");
        Assert.assertEquals("?brigbleibst[?brigbleiben/VER:2:SIN:PR?:NON:NEB]", GermanTaggerTest.toSortedString(aToken11));
        Assert.assertEquals("?brigbleiben", aToken11.getReadings().get(0).getLemma());
        AnalyzedTokenReadings aToken12 = tagger.lookup("IT-Dienstleistungsunternehmen");
        Assert.assertTrue(aToken12.getReadings().get(0).getPOSTag().matches("SUB.*"));
        Assert.assertEquals("IT-Dienstleistungsunternehmen", aToken12.getReadings().get(0).getLemma());
        AnalyzedTokenReadings aToken13 = tagger.lookup("Entweder-oder");
        Assert.assertTrue(aToken13.getReadings().get(0).getPOSTag().matches("SUB.*"));
        Assert.assertEquals("Entweder-oder", aToken13.getReadings().get(0).getLemma());
        AnalyzedTokenReadings aToken14 = tagger.lookup("Verletzter");
        Assert.assertTrue(aToken14.getReadings().get(0).getPOSTag().equals("SUB:NOM:SIN:MAS:ADJ"));
        Assert.assertEquals("Verletzter", aToken14.getReadings().get(0).getLemma());
        Assert.assertTrue(aToken14.getReadings().get(1).getPOSTag().equals("SUB:GEN:PLU:MAS:ADJ"));
        AnalyzedTokenReadings aToken15 = tagger.lookup("erzkatholisch");
        Assert.assertTrue(aToken15.getReadings().get(0).getPOSTag().equals("ADJ:PRD:GRU"));
    }

    // make sure we use the version of the POS data that was extended with post spelling reform data
    @Test
    public void testExtendedTagger() throws IOException {
        GermanTagger tagger = new GermanTagger();
        Assert.assertEquals("Ku?[Ku?/SUB:AKK:SIN:MAS, Ku?/SUB:DAT:SIN:MAS, Ku?/SUB:NOM:SIN:MAS]", GermanTaggerTest.toSortedString(tagger.lookup("Ku?")));
        Assert.assertEquals("Kuss[Kuss/SUB:AKK:SIN:MAS, Kuss/SUB:DAT:SIN:MAS, Kuss/SUB:NOM:SIN:MAS]", GermanTaggerTest.toSortedString(tagger.lookup("Kuss")));
        Assert.assertEquals("Ha?[Ha?/SUB:AKK:SIN:MAS, Ha?/SUB:DAT:SIN:MAS, Ha?/SUB:NOM:SIN:MAS]", GermanTaggerTest.toSortedString(tagger.lookup("Ha?")));
        Assert.assertEquals("Hass[Hass/SUB:AKK:SIN:MAS, Hass/SUB:DAT:SIN:MAS, Hass/SUB:NOM:SIN:MAS]", GermanTaggerTest.toSortedString(tagger.lookup("Hass")));
        Assert.assertEquals("mu?[m?ssen/VER:MOD:1:SIN:PR?, m?ssen/VER:MOD:3:SIN:PR?]", GermanTaggerTest.toSortedString(tagger.lookup("mu?")));
        Assert.assertEquals("muss[m?ssen/VER:MOD:1:SIN:PR?, m?ssen/VER:MOD:3:SIN:PR?]", GermanTaggerTest.toSortedString(tagger.lookup("muss")));
    }

    @Test
    public void testTaggerBaseforms() throws IOException {
        GermanTagger tagger = new GermanTagger();
        List<AnalyzedToken> readings1 = tagger.lookup("?brigbleibst").getReadings();
        Assert.assertEquals(1, readings1.size());
        Assert.assertEquals("?brigbleiben", readings1.get(0).getLemma());
        List<AnalyzedToken> readings2 = tagger.lookup("Haus").getReadings();
        Assert.assertEquals(3, readings2.size());
        Assert.assertEquals("Haus", readings2.get(0).getLemma());
        Assert.assertEquals("Haus", readings2.get(1).getLemma());
        Assert.assertEquals("Haus", readings2.get(2).getLemma());
        List<AnalyzedToken> readings3 = tagger.lookup("H?user").getReadings();
        Assert.assertEquals(3, readings3.size());
        Assert.assertEquals("Haus", readings3.get(0).getLemma());
        Assert.assertEquals("Haus", readings3.get(1).getLemma());
        Assert.assertEquals("Haus", readings3.get(2).getLemma());
    }

    @Test
    public void testTag() throws IOException {
        GermanTagger tagger = new GermanTagger();
        List<String> upperCaseWord = Arrays.asList("Das");
        List<AnalyzedTokenReadings> readings = tagger.tag(upperCaseWord, false);
        Assert.assertEquals("[Das[Das/null*]]", readings.toString());
        List<AnalyzedTokenReadings> readings2 = tagger.tag(upperCaseWord, true);
        Assert.assertTrue(readings2.toString().startsWith("[Das[der/ART:"));
    }

    @Test
    public void testTagWithManualDictExtension() throws IOException {
        // words not originally in Morphy but added in LT 1.8 (moved from added.txt to german.dict)
        GermanTagger tagger = new GermanTagger();
        List<AnalyzedTokenReadings> readings = tagger.tag(Collections.singletonList("Wichtigtuerinnen"));
        Assert.assertEquals(("[Wichtigtuerinnen[Wichtigtuerin/SUB:AKK:PLU:FEM*," + "Wichtigtuerin/SUB:DAT:PLU:FEM*,Wichtigtuerin/SUB:GEN:PLU:FEM*,Wichtigtuerin/SUB:NOM:PLU:FEM*]]"), readings.toString());
    }

    @Test
    public void testDictionary() throws IOException {
        Dictionary dictionary = Dictionary.read(JLanguageTool.getDataBroker().getFromResourceDirAsUrl("/de/german.dict"));
        DictionaryLookup dl = new DictionaryLookup(dictionary);
        for (WordData wd : dl) {
            if (((wd.getTag()) == null) || ((wd.getTag().length()) == 0)) {
                System.err.println((((("**** Warning: the word " + (wd.getWord())) + "/") + (wd.getStem())) + " lacks a POS tag in the dictionary."));
            }
        }
    }
}

