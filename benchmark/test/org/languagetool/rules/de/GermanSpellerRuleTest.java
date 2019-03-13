/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2012 Daniel Naber (http://www.danielnaber.de)
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.AustrianGerman;
import org.languagetool.language.German;
import org.languagetool.language.GermanyGerman;
import org.languagetool.language.SwissGerman;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.spelling.hunspell.HunspellRule;


public class GermanSpellerRuleTest {
    private static final GermanyGerman GERMAN_DE = new GermanyGerman();

    private static final SwissGerman GERMAN_CH = new SwissGerman();

    // 
    // NOTE: also manually run SuggestionRegressionTest when the suggestions are changing!
    // 
    @Test
    public void filterForLanguage() {
        GermanSpellerRule rule = new GermanSpellerRule(TestTools.getMessages("de"), GermanSpellerRuleTest.GERMAN_DE);
        List<String> list1 = new ArrayList<>(Arrays.asList("Mafiosi s", "foo"));
        rule.filterForLanguage(list1);
        Assert.assertThat(list1, CoreMatchers.is(Arrays.asList("foo")));
        List<String> list2 = new ArrayList<>(Arrays.asList("-bar", "foo"));
        rule.filterForLanguage(list2);
        Assert.assertThat(list2, CoreMatchers.is(Arrays.asList("foo")));
        GermanSpellerRule ruleCH = new SwissGermanSpellerRule(TestTools.getMessages("de"), GermanSpellerRuleTest.GERMAN_CH);
        List<String> list3 = new ArrayList<>(Arrays.asList("Mu?e", "foo"));
        ruleCH.filterForLanguage(list3);
        Assert.assertThat(list3, CoreMatchers.is(Arrays.asList("Musse", "foo")));
    }

    @Test
    public void testSortSuggestion() {
        GermanSpellerRule rule = new GermanSpellerRule(TestTools.getMessages("de"), GermanSpellerRuleTest.GERMAN_DE);
        Assert.assertThat(rule.sortSuggestionByQuality("fehler", Arrays.asList("fehla", "xxx", "Fehler")).toString(), CoreMatchers.is("[Fehler, fehla, xxx]"));
        Assert.assertThat(rule.sortSuggestionByQuality("m?lleimer", Arrays.asList("M?lheimer", "-m?lheimer", "Melkeimer", "M?hlheimer", "M?lleimer")).toString(), CoreMatchers.is("[M?lleimer, M?lheimer, -m?lheimer, Melkeimer, M?hlheimer]"));
    }

    @Test
    public void testProhibited() throws Exception {
        GermanSpellerRule rule = new GermanSpellerRule(TestTools.getMessages("de"), GermanSpellerRuleTest.GERMAN_DE);
        rule.getSuggestions("");// needed to force a proper init

        Assert.assertTrue(rule.isProhibited("Standart-Test"));
        Assert.assertTrue(rule.isProhibited("Weihnachtfreier"));
        Assert.assertFalse(rule.isProhibited("Standard-Test"));
        Assert.assertTrue(rule.isProhibited("Abstellgreis"));
        Assert.assertTrue(rule.isProhibited("Abstellgreise"));
        Assert.assertTrue(rule.isProhibited("Abstellgreisen"));
        Assert.assertTrue(rule.isProhibited("Landstreckenfl?ge"));
        Assert.assertTrue(rule.isProhibited("Landstreckenfl?gen"));
    }

    @Test
    public void testGetAdditionalTopSuggestions() throws Exception {
        GermanSpellerRule rule = new GermanSpellerRule(TestTools.getMessages("de"), GermanSpellerRuleTest.GERMAN_DE);
        JLanguageTool lt = new JLanguageTool(GermanSpellerRuleTest.GERMAN_DE);
        Assert.assertThat(rule.match(lt.getAnalyzedSentence("konservierungsstoffstatistik"))[0].getSuggestedReplacements().toString(), CoreMatchers.is("[Konservierungsstoffstatistik]"));
        Assert.assertThat(rule.match(lt.getAnalyzedSentence("konservierungsstoffsasdsasda"))[0].getSuggestedReplacements().size(), CoreMatchers.is(0));
        Assert.assertThat(rule.match(lt.getAnalyzedSentence("Ventrolateral")).length, CoreMatchers.is(0));
        Assert.assertThat(rule.match(lt.getAnalyzedSentence("Kleindung")).length, CoreMatchers.is(1));// ignored due to ignoreCompoundWithIgnoredWord(), but still in ignore.txt -> ignore.txt must override this

        Assert.assertThat(rule.match(lt.getAnalyzedSentence("Majon?se."))[0].getSuggestedReplacements().toString(), CoreMatchers.is("[Mayonnaise]"));
        assertFirstSuggestion("wars.", "war's", rule, lt);
        assertFirstSuggestion("konservierungsstoffe", "Konservierungsstoffe", rule, lt);
        // assertFirstSuggestion("Ist Ventrolateral", "ventrolateral", rule, lt);
        assertFirstSuggestion("denkte", "dachte", rule, lt);
        assertFirstSuggestion("schwimmte", "schwamm", rule, lt);
        assertFirstSuggestion("gehte", "ging", rule, lt);
        assertFirstSuggestion("greifte", "griff", rule, lt);
        assertFirstSuggestion("geschwimmt", "geschwommen", rule, lt);
        assertFirstSuggestion("gegeht", "gegangen", rule, lt);
        assertFirstSuggestion("getrinkt", "getrunken", rule, lt);
        assertFirstSuggestion("gespringt", "gesprungen", rule, lt);
        assertFirstSuggestion("geruft", "gerufen", rule, lt);
        // assertFirstSuggestion("Au-pair-Agentr", "Au-pair-Agentur", rule, lt); // "Au-pair" from spelling.txt
        assertFirstSuggestion("Netflix-Flm", "Netflix-Film", rule, lt);// "Netflix" from spelling.txt

        assertFirstSuggestion("Bund-L?nder-Kommissio", "Bund-L?nder-Kommission", rule, lt);
        assertFirstSuggestion("Emailaccount", "E-Mail-Account", rule, lt);
        assertFirstSuggestion("Emailacount", "E-Mail-Account", rule, lt);
        assertFirstSuggestion("millionmal", "Million Mal", rule, lt);
        assertFirstSuggestion("millionenmal", "Millionen Mal", rule, lt);
        assertFirstSuggestion("geupdated", "upgedatet", rule, lt);
        assertFirstSuggestion("rosanen", "rosa", rule, lt);
        assertFirstSuggestion("missionariesierung", "Missionierung", rule, lt);
        assertFirstSuggestion("angehangener", "angeh?ngter", rule, lt);
        assertFirstSuggestion("aufgehangene", "aufgeh?ngte", rule, lt);
        assertFirstSuggestion("Germanistiker", "Germanist", rule, lt);
        assertFirstSuggestion("Germanistikern", "Germanisten", rule, lt);
        assertFirstSuggestion("Germanistikerin", "Germanistin", rule, lt);
        assertFirstSuggestion("erh?herung", "Erh?hung", rule, lt);
        assertFirstSuggestion("aufjedenfall", "auf jeden Fall", rule, lt);
        assertFirstSuggestion("Aufjedenfall", "Auf jeden Fall", rule, lt);
        assertFirstSuggestion("funkzunierende", "funktionierende", rule, lt);
        assertFirstSuggestion("funkzuniert", "funktioniert", rule, lt);
        assertFirstSuggestion("Mayonese", "Mayonnaise", rule, lt);
        assertFirstSuggestion("Majon?se", "Mayonnaise", rule, lt);
        assertFirstSuggestion("Salatmajon?se", "Salatmayonnaise", rule, lt);
        assertFirstSuggestion("Physiklaborants", "Physiklaboranten", rule, lt);
        assertFirstSuggestion("interkurelle", "interkulturelle", rule, lt);
        assertFirstSuggestion("Zuende", "Zu Ende", rule, lt);
        assertFirstSuggestion("zuende", "zu Ende", rule, lt);
        assertFirstSuggestion("wolt", "wollt", rule, lt);
        assertFirstSuggestion("allm?hliges", "allm?hliches", rule, lt);
        assertFirstSuggestion("Allm?hllig", "Allm?hlich", rule, lt);
        assertFirstSuggestion("Probiren", "Probieren", rule, lt);
        assertFirstSuggestion("gesetztreu", "gesetzestreu", rule, lt);
        assertFirstSuggestion("wikiche", "wirkliche", rule, lt);
        assertFirstSuggestion("kongratulierst", "gratulierst", rule, lt);
        assertFirstSuggestion("Makeup", "Make-up", rule, lt);
        assertFirstSuggestion("profesionehlle", "professionelle", rule, lt);
        assertFirstSuggestion("profession?hlles", "professionelles", rule, lt);
        assertFirstSuggestion("gehnemigung", "Genehmigung", rule, lt);
        assertFirstSuggestion("korregierungen", "Korrekturen", rule, lt);
        assertFirstSuggestion("Korrigierungen", "Korrekturen", rule, lt);
        assertFirstSuggestion("Ticketresawihrung", "Ticketreservierung", rule, lt);
        assertFirstSuggestion("gin", "ging", rule, lt);
        assertFirstSuggestion("Gleichrechtige", "Gleichberechtigte", rule, lt);
        assertFirstSuggestion("unn?tzliche", "unn?tze", rule, lt);
        assertFirstSuggestion("h?lst", "h?ltst", rule, lt);
        assertFirstSuggestion("erh?lst", "erh?ltst", rule, lt);
        assertFirstSuggestion("Verstehendnis", "Verst?ndnis", rule, lt);
        assertFirstSuggestion("Wohlf?hlsein", "Wellness", rule, lt);
        assertFirstSuggestion("schmetrlinge", "Schmetterlinge", rule, lt);
        assertFirstSuggestion("einlamienirte", "laminierte", rule, lt);
        assertFirstSuggestion("Assecoires", "Accessoires", rule, lt);
        assertFirstSuggestion("Vorraussetzungen", "Voraussetzungen", rule, lt);
        assertFirstSuggestion("aufwechselungsreichem", "abwechslungsreichem", rule, lt);
        assertFirstSuggestion("nachwievor", "nach wie vor", rule, lt);
        assertFirstSuggestion("letztenendes", "letzten Endes", rule, lt);
        assertFirstSuggestion("mitanader", "miteinander", rule, lt);
        assertFirstSuggestion("nocheimal", "noch einmal", rule, lt);
        assertFirstSuggestion("konflikationen", "Komplikationen", rule, lt);
        assertFirstSuggestion("unswar", "und zwar", rule, lt);
        assertFirstSuggestion("fomelare", "Formulare", rule, lt);
        assertFirstSuggestion("immoment", "im Moment", rule, lt);
        assertFirstSuggestion("inordnung", "in Ordnung", rule, lt);
        assertFirstSuggestion("inb?lde", "in B?lde", rule, lt);
        assertFirstSuggestion("unaufbesichtigt", "unbeaufsichtigt", rule, lt);
        assertFirstSuggestion("uberaschend", "?berraschend", rule, lt);
        assertFirstSuggestion("uberagendes", "?berragendes", rule, lt);
        assertFirstSuggestion("unabsichtiges", "unabsichtliches", rule, lt);
        assertFirstSuggestion("organisatives", "organisatorisches", rule, lt);
        assertFirstSuggestion("Medallion", "Medaillon", rule, lt);
        assertFirstSuggestion("diagnosiere", "diagnostiziere", rule, lt);
        assertFirstSuggestion("diagnoziert", "diagnostiziert", rule, lt);
        assertFirstSuggestion("durschnittliche", "durchschnittliche", rule, lt);
        assertFirstSuggestion("durschnitliche", "durchschnittliche", rule, lt);
        assertFirstSuggestion("durchnitliche", "durchschnittliche", rule, lt);
        assertFirstSuggestion("Durschnittswerte", "Durchschnittswerte", rule, lt);
        assertFirstSuggestion("Durschnittsb?rgers", "Durchschnittsb?rgers", rule, lt);
        assertFirstSuggestion("Heileit", "Highlight", rule, lt);
        assertFirstSuggestion("todesbedrohende", "lebensbedrohende", rule, lt);
        assertFirstSuggestion("todesbedrohliches", "lebensbedrohliches", rule, lt);
        assertFirstSuggestion("einf?hlsvoller", "einf?hlsamer", rule, lt);
        assertFirstSuggestion("folklorisch", "folkloristisch", rule, lt);
        assertFirstSuggestion("Religi?sischen", "Religi?sen", rule, lt);
        assertFirstSuggestion("reschaschiert", "recherchiert", rule, lt);
        assertFirstSuggestion("bi?jen", "bisschen", rule, lt);
        assertFirstSuggestion("bisien", "bisschen", rule, lt);
        assertFirstSuggestion("Gruessen", "Gr??en", rule, lt);
        assertFirstSuggestion("Matschscheibe", "Mattscheibe", rule, lt);
        assertFirstSuggestion("Pearl-Harbour", "Pearl Harbor", rule, lt);
        assertFirstSuggestion("Autonomit?t", "Autonomie", rule, lt);
        assertFirstSuggestion("Kompatibelkeit", "Kompatibilit?t", rule, lt);
        assertFirstSuggestion("Sensibelkeit", "Sensibilit?t", rule, lt);
        assertFirstSuggestion("Flexibelkeit", "Flexibilit?t", rule, lt);
        assertFirstSuggestion("WiFi-Direkt", "Wi-Fi Direct", rule, lt);
        assertFirstSuggestion("Wi-Fi-Direct", "Wi-Fi Direct", rule, lt);
        assertFirstSuggestion("hofen", "hoffen", rule, lt);
        assertFirstSuggestion("frustuck", "Fr?hst?ck", rule, lt);
        assertFirstSuggestion("recourcen", "Ressourcen", rule, lt);
        assertFirstSuggestion("famili?rische", "famili?re", rule, lt);
        assertFirstSuggestion("familliarisches", "famili?res", rule, lt);
        assertFirstSuggestion("sommerverie", "Sommerferien", rule, lt);
        assertFirstSuggestion("thelepatie", "Telepathie", rule, lt);
        assertFirstSuggestion("artz", "Arzt", rule, lt);
        assertFirstSuggestion("ber?cksichtung", "Ber?cksichtigung", rule, lt);
        assertFirstSuggestion("okey", "okay", rule, lt);
        assertFirstSuggestion("Energiesparung", "Energieeinsparung", rule, lt);
        assertFirstSuggestion("Deluxe-Version", "De-luxe-Version", rule, lt);
        assertFirstSuggestion("De-luxe-Champagnr", "De-luxe-Champagner", rule, lt);
        assertFirstSuggestion("problemhafte", "problembehaftete", rule, lt);
        assertFirstSuggestion("solltes", "solltest", rule, lt);
        assertFirstSuggestion("Kilimanjaro", "Kilimandscharo", rule, lt);
        assertFirstSuggestion("unzerbrechbare", "unzerbrechliche", rule, lt);
        assertFirstSuggestion("voraussichtige", "voraussichtliche", rule, lt);
        assertFirstSuggestion("Aleine", "Alleine", rule, lt);
        assertFirstSuggestion("abenzu", "ab und zu", rule, lt);
        assertFirstSuggestion("ergeitz", "Ehrgeiz", rule, lt);
        assertFirstSuggestion("chouch", "Couch", rule, lt);
        assertFirstSuggestion("kontaktfreundliche", "kontaktfreudige", rule, lt);
        assertFirstSuggestion("angestegt", "angesteckt", rule, lt);
        assertFirstSuggestion("festellt", "feststellt", rule, lt);
        assertFirstSuggestion("liqide", "liquide", rule, lt);
        assertFirstSuggestion("gelessen", "gelesen", rule, lt);
        assertFirstSuggestion("Getrixe", "Getrickse", rule, lt);
        assertFirstSuggestion("Naricht", "Nachricht", rule, lt);
        assertFirstSuggestion("konektschen", "Connection", rule, lt);
        assertFirstSuggestion("Neukundenaquise", "Neukundenakquise", rule, lt);
        assertFirstSuggestion("Gehorsamkeitsverweigerung", "Gehorsamsverweigerung", rule, lt);
        assertFirstSuggestion("leinensamens", "Leinsamens", rule, lt);
        assertFirstSuggestion("Oldheimer", "Oldtimer", rule, lt);
        assertFirstSuggestion("verhing", "verh?ngte", rule, lt);
        assertFirstSuggestion("vorallendingen", "vor allen Dingen", rule, lt);
        assertFirstSuggestion("unternehmensl?stige", "unternehmungslustige", rule, lt);
        assertFirstSuggestion("proffesionaler", "professioneller", rule, lt);
        assertFirstSuggestion("gesundliches", "gesundheitliches", rule, lt);
        assertFirstSuggestion("eckelt", "ekelt", rule, lt);
        assertFirstSuggestion("geherte", "geehrte", rule, lt);
        assertFirstSuggestion("Kattermesser", "Cuttermesser", rule, lt);
        assertFirstSuggestion("antisemitistischer", "antisemitischer", rule, lt);
        assertFirstSuggestion("unvorsehbares", "unvorhersehbares", rule, lt);
        assertFirstSuggestion("W?rtenberg", "W?rttemberg", rule, lt);
        assertFirstSuggestion("Baden-W?rtenbergs", "Baden-W?rttembergs", rule, lt);
        assertFirstSuggestion("Rechtsschreibungsfehlern", "Rechtschreibfehlern", rule, lt);
        assertFirstSuggestion("indifiziert", "identifiziert", rule, lt);
        assertFirstSuggestion("verbl?te", "verbl?hte", rule, lt);
        assertFirstSuggestion("dreitem", "drittem", rule, lt);
        assertFirstSuggestion("zukuenftliche", "zuk?nftige", rule, lt);
        assertFirstSuggestion("schwarzw?lderkirschtorte", "Schwarzw?lder Kirschtorte", rule, lt);
        assertFirstSuggestion("kolegen", "Kollegen", rule, lt);
        assertFirstSuggestion("gerechtlichkeit", "Gerechtigkeit", rule, lt);
        assertFirstSuggestion("Zuverl?sslichkeit", "Zuverl?ssigkeit", rule, lt);
        assertFirstSuggestion("Krankenhausen", "Krankenh?usern", rule, lt);
        assertFirstSuggestion("jedwilliger", "jedweder", rule, lt);
        assertFirstSuggestion("Betriebsratzimmern", "Betriebsratszimmern", rule, lt);
        assertFirstSuggestion("ausiehst", "aussiehst", rule, lt);
        assertFirstSuggestion("unterbemittelnde", "minderbemittelte", rule, lt);
        assertFirstSuggestion("koregiert", "korrigiert", rule, lt);
        assertFirstSuggestion("Gelangenheitsbest?tigungen", "Gelangensbest?tigungen", rule, lt);
        assertFirstSuggestion("mitenand", "miteinander", rule, lt);
        assertFirstSuggestion("hinunher", "hin und her", rule, lt);
        assertFirstSuggestion("Xter", "X-ter", rule, lt);
        assertFirstSuggestion("Kaufentfehlung", "Kaufempfehlung", rule, lt);
        assertFirstSuggestion("unverzeilige", "unverzeihliche", rule, lt);
        assertFirstSuggestion("Addons", "Add-ons", rule, lt);
        assertFirstSuggestion("Mitgliederinnen", "Mitglieder", rule, lt);
        assertFirstSuggestion("Feinleiner", "Fineliner", rule, lt);
        assertFirstSuggestion("gr??ester", "gr??ter", rule, lt);
        assertFirstSuggestion("verh?ufte", "geh?ufte", rule, lt);
        assertFirstSuggestion("naheste", "n?chste", rule, lt);
        assertFirstSuggestion("fluoreszenzierend", "fluoreszierend", rule, lt);
        assertFirstSuggestion("revalierender", "rivalisierender", rule, lt);
        assertFirstSuggestion("h?herne", "h?rene", rule, lt);
        assertFirstSuggestion("Portfolien", "Portfolios", rule, lt);
        assertFirstSuggestion("Nivo", "Niveau", rule, lt);
        assertFirstSuggestion("dilletanten", "Dilettanten", rule, lt);
        assertFirstSuggestion("intersannt", "interessant", rule, lt);
        assertFirstSuggestion("allereinzigstem", "einzigem", rule, lt);
        assertFirstSuggestion("Einzigste", "Einzige", rule, lt);
        assertFirstSuggestion("namenhafte", "namhafte", rule, lt);
        assertFirstSuggestion("homeophatisch", "hom?opathisch", rule, lt);
        assertFirstSuggestion("verswindet", "verschwindet", rule, lt);
        assertFirstSuggestion("Durschnitt", "Durchschnitt", rule, lt);
        assertFirstSuggestion("Durchnitts", "Durchschnitts", rule, lt);
        assertFirstSuggestion("?berdurschnittlichem", "?berdurchschnittlichem", rule, lt);
        assertFirstSuggestion("Unterdurschnittlicher", "Unterdurchschnittlicher", rule, lt);
        assertFirstSuggestion("h?chstwahrliche", "h?chstwahrscheinliche", rule, lt);
        assertFirstSuggestion("vidasehen", "wiedersehen", rule, lt);
        assertFirstSuggestion("striktliches", "striktes", rule, lt);
        assertFirstSuggestion("preventiert", "verhindert", rule, lt);
        assertFirstSuggestion("zurverf?gung", "zur Verf?gung", rule, lt);
        assertFirstSuggestion("trationelle", "traditionelle", rule, lt);
        assertFirstSuggestion("achsiales", "axiales", rule, lt);
        assertFirstSuggestion("famiele", "Familie", rule, lt);
        assertFirstSuggestion("miters", "Mieters", rule, lt);
        assertFirstSuggestion("besigen", "besiegen", rule, lt);
        assertFirstSuggestion("verziehrte", "verzierte", rule, lt);
        assertFirstSuggestion("pieken", "piken", rule, lt);// Duden insists on this spelling

        assertFirstSuggestion("Erstsemesterin", "Erstsemester", rule, lt);
        assertFirstSuggestion("zauberlicher", "zauberischer", rule, lt);
        assertFirstSuggestion("assessoars", "Accessoires", rule, lt);
        assertFirstSuggestion("k?nntes", "k?nntest", rule, lt);
        assertFirstSuggestion("Casemangement", "Case Management", rule, lt);
        assertFirstSuggestion("Anolierung", "Annullierung", rule, lt);
        assertFirstSuggestion("Liaisonen", "Liaisons", rule, lt);
        assertFirstSuggestion("kinderlichem", "kindlichem", rule, lt);
        assertFirstSuggestion("wiedersprichst", "widersprichst", rule, lt);
        assertFirstSuggestion("unproffesionele", "unprofessionelle", rule, lt);
        assertFirstSuggestion("gefrustuckt", "gefr?hst?ckt", rule, lt);
        assertFirstSuggestion("Durf?hrung", "Durchf?hrung", rule, lt);
        assertFirstSuggestion("verheielte", "verheilte", rule, lt);
        assertFirstSuggestion("ausgew?nlich", "au?ergew?hnlich", rule, lt);
        assertFirstSuggestion("unausweichbaren", "unausweichlichen", rule, lt);
        assertFirstSuggestion("Dampfschiffahrtskapit?n", "Dampfschifffahrtskapit?n", rule, lt);
        assertFirstSuggestion("Helfes-Helfern", "Helfershelfern", rule, lt);
        assertFirstSuggestion("Intelligentsbestie", "Intelligenzbestie", rule, lt);
        assertFirstSuggestion("avantgardische", "avantgardistische", rule, lt);
        assertFirstSuggestion("gewohnheitsbed?rftigen", "gew?hnungsbed?rftigen", rule, lt);
        assertFirstSuggestion("patroliert", "patrouilliert", rule, lt);
        assertFirstSuggestion("beidiges", "beides", rule, lt);
        assertFirstSuggestion("Propagandierte", "Propagierte", rule, lt);
        assertFirstSuggestion("revolutioniesiert", "revolutioniert", rule, lt);
    }

    @Test
    public void testAddIgnoreWords() throws Exception {
        GermanSpellerRuleTest.MyGermanSpellerRule rule = new GermanSpellerRuleTest.MyGermanSpellerRule(TestTools.getMessages("de"), GermanSpellerRuleTest.GERMAN_DE);
        addIgnoreWords("Fu?elmappse");
        JLanguageTool lt = new JLanguageTool(GermanSpellerRuleTest.GERMAN_DE);
        assertCorrect("Fu?elmappse", rule, lt);
        addIgnoreWords("Fu?elmappse/N");
        assertCorrect("Fu?elmappse", rule, lt);
        assertCorrect("Fu?elmappsen", rule, lt);
        addIgnoreWords("Toggeltr?t/NS");
        assertCorrect("Toggeltr?t", rule, lt);
        assertCorrect("Toggeltr?ts", rule, lt);
        assertCorrect("Toggeltr?tn", rule, lt);
        GermanSpellerRuleTest.MyGermanSpellerRule ruleCH = new GermanSpellerRuleTest.MyGermanSpellerRule(TestTools.getMessages("de"), GermanSpellerRuleTest.GERMAN_CH);
        addIgnoreWords("Fu?elmappse/N");
        assertCorrect("Fusselmappse", ruleCH, lt);
        assertCorrect("Fusselmappsen", ruleCH, lt);
    }

    @Test
    public void testDashAndHyphen() throws Exception {
        HunspellRule rule = new GermanSpellerRule(TestTools.getMessages("de"), GermanSpellerRuleTest.GERMAN_DE);
        JLanguageTool lt = new JLanguageTool(GermanSpellerRuleTest.GERMAN_DE);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Ist doch - gut")).length);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Ist doch -- gut")).length);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Stil- und Grammatikpr?fung gut")).length);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Stil-, Text- und Grammatikpr?fung gut")).length);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Er liebt die Stil-, Text- und Grammatikpr?fung.")).length);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Stil-, Text- und Grammatikpr?fung")).length);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Stil-, Text- oder Grammatikpr?fung")).length);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Hierzu z?hlen Einkommen-, K?rperschaft- sowie Gewerbesteuer.")).length);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Miet- und Zinseink?nfte")).length);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("SPD- und CDU-Abgeordnete")).length);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Haupt- und Nebensatz")).length);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Vertuschungs- und Bespitzelungsma?nahmen")).length);// remove "s" from "Vertuschungs" before spell check

        // assertEquals(0, rule.match(lt.getAnalyzedSentence("Au-pair-Agentur")).length); // compound with ignored word from spelling.txt
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Netflix-Film")).length);// compound with ignored word from spelling.txt

        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Bund-L?nder-Kommission")).length);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Des World Wide Webs")).length);// expanded multi-word entry from spelling.txt

        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Der westperuanische Ferienort.")).length);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("?Pumpe?-Nachfolge")).length);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("\"Pumpe\"-Nachfolge")).length);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("?VP- und FP?-Chefverhandler")).length);// first part is from spelling.txt

        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("?-Strahlung")).length);// compound with ignored word from spelling.txt

        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Prim?r-?-Mischkristallen")).length);// compound with ignored word from spelling.txt

        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("supergut")).length);// elativ meaning "sehr gut"

        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("90?-Winkel")).length);
        Assert.assertEquals(1, rule.match(lt.getAnalyzedSentence("Miet und Zinseink?nfte")).length);
        Assert.assertEquals(1, rule.match(lt.getAnalyzedSentence("Stil- und Grammatik gut")).length);
        Assert.assertEquals(1, rule.match(lt.getAnalyzedSentence("Flasch- und Grammatikpr?fung gut")).length);
        // assertEquals(1, rule.match(langTool.getAnalyzedSentence("Haupt- und Neben")).length);  // hunspell accepts this :-(
        // check acceptance of words in ignore.txt ending with "-*"
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Dual-Use-G?ter")).length);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Dual-Use- und Wirtschaftsg?ter")).length);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Test-Dual-Use")).length);
        Assert.assertEquals(1, rule.match(lt.getAnalyzedSentence("Dual-Use")).length);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Einpseudowortmit?f?rlanguagetooltests-Auto")).length);
        // originally from spelling.txt:
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Wichtelm?nnchen")).length);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Wichtelm?nnchens")).length);
        Assert.assertEquals(1, rule.match(lt.getAnalyzedSentence("wichtelm?nnchen")).length);// no reason to accept it as lowercase

        Assert.assertEquals(1, rule.match(lt.getAnalyzedSentence("wichtelm?nnchens")).length);// no reason to accept it as lowercase

        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("vorgeh?ngt")).length);// from spelling.txt

        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("vorgeh?ngten")).length);// from spelling.txt with suffix

        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Vorgeh?ngt")).length);// from spelling.txt, it's lowercase there but we accept uppercase at idx = 0

        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Vorgeh?ngten")).length);// from spelling.txt with suffix, it's lowercase there but we accept uppercase at idx = 0

        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Wichtelm?nnchen-vorgeh?ngt")).length);// from spelling.txt formed hyphenated compound

        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Wichtelm?nnchen-Au-pair")).length);// from spelling.txt formed hyphenated compound

        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Fermi-Dirac-Statistik")).length);// from spelling.txt formed hyphenated compound

        // assertEquals(0, rule.match(lt.getAnalyzedSentence("Au-pair-Wichtelm?nnchen")).length);  // from spelling.txt formed hyphenated compound
        // assertEquals(0, rule.match(lt.getAnalyzedSentence("Secondhandware")).length);  // from spelling.txt formed compound
        // assertEquals(0, rule.match(lt.getAnalyzedSentence("Feynmandiagramme")).length);  // from spelling.txt formed compound
        // assertEquals(0, rule.match(lt.getAnalyzedSentence("Helizit?tsoperator")).length);  // from spelling.txt formed compound
        // assertEquals(0, rule.match(lt.getAnalyzedSentence("Wodkaherstellung")).length);  // from spelling.txt formed compound
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Latte-macchiato-Glas")).length);// formelery from spelling.txt formed compound

        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Werkvertr?gler-Glas")).length);// from spelling.txt formed compound

        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Werkvertr?glerglas")).length);// from spelling.txt formed compound

        Assert.assertEquals(1, rule.match(lt.getAnalyzedSentence("Werkvertr?glerdu")).length);// from spelling.txt formed "compound" with last part too short

        // assertEquals(0, rule.match(lt.getAnalyzedSentence("No-Name-Hersteller")).length);  // from spelling.txt formed compound
        Assert.assertEquals(1, rule.match(lt.getAnalyzedSentence("Helizit?tso")).length);// from spelling.txt formed compound (second part is too short)

        Assert.assertEquals(1, rule.match(lt.getAnalyzedSentence("Feynmand")).length);// from spelling.txt formed compound (second part is too short)

        Assert.assertEquals(1, rule.match(lt.getAnalyzedSentence("Einpseudowortmitssf?rlanguagetooltests-Auto")).length);
        HunspellRule ruleCH = new GermanSpellerRule(TestTools.getMessages("de"), GermanSpellerRuleTest.GERMAN_CH);
        Assert.assertEquals(1, ruleCH.match(lt.getAnalyzedSentence("Einpseudowortmit?f?rlanguagetooltests-Auto")).length);
        Assert.assertEquals(0, ruleCH.match(lt.getAnalyzedSentence("Einpseudowortmitssf?rlanguagetooltests-Auto")).length);
    }

    @Test
    public void testGetSuggestionsFromSpellingTxt() throws Exception {
        GermanSpellerRuleTest.MyGermanSpellerRule ruleGermany = new GermanSpellerRuleTest.MyGermanSpellerRule(TestTools.getMessages("de"), GermanSpellerRuleTest.GERMAN_DE);
        Assert.assertThat(getSuggestions("Ligafu?boll").toString(), CoreMatchers.is("[Ligafu?ball, Ligafu?balls]"));// from spelling.txt

        Assert.assertThat(getSuggestions("free-and-open-source").toString(), CoreMatchers.is("[]"));// to prevent OutOfMemoryErrors: do not create hyphenated compounds consisting of >3 parts

        GermanSpellerRuleTest.MyGermanSpellerRule ruleSwiss = new GermanSpellerRuleTest.MyGermanSpellerRule(TestTools.getMessages("de"), GermanSpellerRuleTest.GERMAN_CH);
        Assert.assertThat(getSuggestions("Ligafu?boll").toString(), CoreMatchers.is("[Ligafussball, Ligafussballs]"));
        Assert.assertThat(getSuggestions("konfliktbereid").toString(), CoreMatchers.is("[konfliktbereit, konfliktbereite]"));
        Assert.assertThat(getSuggestions("konfliktbereitel").toString(), CoreMatchers.is("[konfliktbereite, konfliktbereiten, konfliktbereitem, konfliktbereiter, konfliktbereites, konfliktbereit]"));
    }

    @Test
    public void testIgnoreWord() throws Exception {
        GermanSpellerRuleTest.MyGermanSpellerRule ruleGermany = new GermanSpellerRuleTest.MyGermanSpellerRule(TestTools.getMessages("de"), GermanSpellerRuleTest.GERMAN_DE);
        Assert.assertTrue(ruleGermany.doIgnoreWord("einPseudoWortF?rLanguageToolTests"));// from ignore.txt

        Assert.assertFalse(ruleGermany.doIgnoreWord("Hundh?tte"));
        // compound formed from two valid words, but still incorrect
        Assert.assertFalse(ruleGermany.doIgnoreWord("Frauversteher"));
        // compound formed from two valid words, but still incorrect
        Assert.assertFalse(ruleGermany.doIgnoreWord("Wodkasglas"));
        // compound formed from two valid words, but still incorrect
        Assert.assertFalse(ruleGermany.doIgnoreWord("Author"));
        Assert.assertFalse(ruleGermany.doIgnoreWord("SecondhandWare"));
        // from spelling.txt formed compound
        Assert.assertFalse(ruleGermany.doIgnoreWord("MHDware"));
        // from spelling.txt formed compound
        GermanSpellerRuleTest.MyGermanSpellerRule ruleSwiss = new GermanSpellerRuleTest.MyGermanSpellerRule(TestTools.getMessages("de"), GermanSpellerRuleTest.GERMAN_CH);
        Assert.assertTrue(ruleSwiss.doIgnoreWord("einPseudoWortF?rLanguageToolTests"));
        Assert.assertFalse(ruleSwiss.doIgnoreWord("Ligafu?ball"));// '?' never accepted for Swiss

    }

    private static class MyGermanSpellerRule extends GermanSpellerRule {
        MyGermanSpellerRule(ResourceBundle messages, German language) throws IOException {
            super(messages, language, null, null);
            init();
        }

        boolean doIgnoreWord(String word) throws IOException {
            return ignoreWord(Collections.singletonList(word), 0);
        }
    }

    // note: copied from HunspellRuleTest
    @Test
    public void testRuleWithGermanyGerman() throws Exception {
        HunspellRule rule = new GermanSpellerRule(TestTools.getMessages("de"), GermanSpellerRuleTest.GERMAN_DE);
        JLanguageTool lt = new JLanguageTool(GermanSpellerRuleTest.GERMAN_DE);
        commonGermanAsserts(rule, lt);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Der ?u?ere ?belt?ter.")).length);// umlauts

        Assert.assertEquals(1, rule.match(lt.getAnalyzedSentence("Der ?ussere ?belt?ter.")).length);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Die Mozart'sche Sonate.")).length);
    }

    // note: copied from HunspellRuleTest
    @Test
    public void testRuleWithAustrianGerman() throws Exception {
        AustrianGerman language = new AustrianGerman();
        HunspellRule rule = new AustrianGermanSpellerRule(TestTools.getMessages("de"), language);
        JLanguageTool lt = new JLanguageTool(language);
        commonGermanAsserts(rule, lt);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Der ?u?ere ?belt?ter.")).length);// umlauts

        Assert.assertEquals(1, rule.match(lt.getAnalyzedSentence("Der ?ussere ?belt?ter.")).length);
    }

    // note: copied from HunspellRuleTest
    @Test
    public void testRuleWithSwissGerman() throws Exception {
        SwissGerman language = new SwissGerman();
        HunspellRule rule = new SwissGermanSpellerRule(TestTools.getMessages("de"), language);
        JLanguageTool lt = new JLanguageTool(language);
        commonGermanAsserts(rule, lt);
        Assert.assertEquals(1, rule.match(lt.getAnalyzedSentence("Der ?u?ere ?belt?ter.")).length);// ? not allowed in Swiss

        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Der ?ussere ?belt?ter.")).length);// ss is used instead of ?

    }

    @Test
    public void testGetSuggestions() throws Exception {
        HunspellRule rule = new GermanSpellerRule(TestTools.getMessages("de"), GermanSpellerRuleTest.GERMAN_DE);
        assertCorrection(rule, "Hauk", "Haus", "Haut");
        assertCorrection(rule, "Eisnbahn", "Eisbahn", "Eisenbahn");
        assertCorrection(rule, "Rechtschreipreform", "Rechtschreibreform");
        assertCorrection(rule, "Theatrekasse", "Theaterkasse");
        assertCorrection(rule, "Traprennen", "Trabrennen");
        assertCorrection(rule, "Autuverkehr", "Autoverkehr");
        assertCorrection(rule, "Rechtschreibpr?fun", "Rechtschreibpr?fung");
        assertCorrection(rule, "Rechtschreib-Pr?fun", "Rechtschreib-Pr?fung");
        assertCorrection(rule, "bw.", "bzw.");
        assertCorrection(rule, "kan", "kann", "an");
        assertCorrection(rule, "kan.", "kann.", "an.");
        assertCorrection(rule, "Einzahlungschein", "Einzahlungsschein");
        assertCorrection(rule, "Arbeitamt", "Arbeitet", "Arbeitsamt");
        assertCorrection(rule, "Ordnungsh?tter", "Ordnungsh?ter");
        assertCorrection(rule, "inneremedizin", "innere Medizin");
        assertCorrection(rule, "innereMedizin", "innere Medizin");
        assertCorrection(rule, "Inneremedizin", "Innere Medizin");
        assertCorrection(rule, "InnereMedizin", "Innere Medizin");
        // TODO: requires morfologik-speller change (suggestions for known words):
        assertCorrection(rule, "Arbeitamt", "Arbeitsamt");
        assertCorrection(rule, "Autoverkehrr", "Autoverkehr");
        assertCorrection(rule, "hasslich", "h?sslich", "fasslich");
        assertCorrection(rule, "Stru?e", "Strau?e", "Stra?e", "Str?u?e");
        assertCorrection(rule, "gewohnlich", "gew?hnlich");
        assertCorrection(rule, "gaw?hnlich", "gew?hnlich");
        assertCorrection(rule, "gw?hnlich", "gew?hnlich");
        assertCorrection(rule, "geew?hnlich", "gew?hnlich");
        assertCorrection(rule, "gew?nlich", "gew?hnlich");
        assertCorrection(rule, "au?ergew?hnkich", "au?ergew?hnlich");
        assertCorrection(rule, "agressiv", "aggressiv");
        assertCorrection(rule, "agressivster", "aggressivster");
        assertCorrection(rule, "agressiver", "aggressiver");
        assertCorrection(rule, "agressive", "aggressive");
        assertCorrection(rule, "Algorythmus", "Algorithmus");
        assertCorrection(rule, "Algorhythmus", "Algorithmus");
        assertCorrection(rule, "Amalgan", "Amalgam");
        assertCorrection(rule, "Amaturenbrett", "Armaturenbrett");
        assertCorrection(rule, "Aquise", "Akquise");
        assertCorrection(rule, "Artzt", "Arzt");
        assertCorrection(rule, "aufgrunddessen", "aufgrund dessen");
        assertCorrection(rule, "barfuss", "barfu?");
        assertCorrection(rule, "Batallion", "Bataillon");
        assertCorrection(rule, "Medallion", "Medaillon");
        assertCorrection(rule, "Scheisse", "Schei?e");
        assertCorrection(rule, "Handselvertreter", "Handelsvertreter");
        assertCorrection(rule, "aul", "auf");
        assertCorrection(rule, "Icj", "Ich");// only "ich" (lowercase) is in the lexicon

        // assertCorrection(rule, "Ihj", "Ich");   // only "ich" (lowercase) is in the lexicon - does not work because of the limit
        // three part compounds:
        assertCorrection(rule, "Handelsvertretertrffen", "Handelsvertretertreffen");
        assertCorrection(rule, "Handelsvartretertreffen", "Handelsvertretertreffen");
        assertCorrection(rule, "Handelsvertretertriffen", "Handelsvertretertreffen");
        assertCorrection(rule, "Handelsvertrtertreffen", "Handelsvertretertreffen");
        assertCorrection(rule, "Handselvertretertreffen", "Handelsvertretertreffen");
        assertCorrection(rule, "Arbeidszimmer", "Arbeitszimmer");
        assertCorrection(rule, "Postleidzahl", "Postleitzahl");
        assertCorrection(rule, "vorallem", "vor allem");
        assertCorrection(rule, "wieviel", "wie viel");
        assertCorrection(rule, "wieviele", "wie viele");
        assertCorrection(rule, "wievielen", "wie vielen");
        assertCorrection(rule, "undzwar", "und zwar");
        assertCorrection(rule, "Ambei", "Anbei");
        // TODO: compounds with errors in more than one part
        // totally wrong jwordsplitter split: Hands + elvertretertreffn:
        // assertCorrection(rule, "Handselvertretertreffn", "Handelsvertretertreffen");
    }

    @Test
    public void testGetSuggestionWithPunctuation() throws Exception {
        GermanSpellerRule rule = new GermanSpellerRule(TestTools.getMessages("de"), GermanSpellerRuleTest.GERMAN_DE);
        JLanguageTool lt = new JLanguageTool(GermanSpellerRuleTest.GERMAN_DE);
        assertFirstSuggestion("informationnen.", "Informationen", rule, lt);
        assertFirstSuggestion("Kundigungsfrist.", "K?ndigungsfrist", rule, lt);
        assertFirstSuggestion("aufgeregegt.", "aufgeregt", rule, lt);
        assertFirstSuggestion("informationnen...", "Informationen..", rule, lt);// not 100% perfect, but we can live with this...

        assertFirstSuggestion("arkbeiten-", "arbeiten", rule, lt);
        // assertFirstSuggestion("arkjbeiten-", "arbeiten", rule, lt);
        // commas are actually not part of the word, so the suggestion doesn't include them:
        assertFirstSuggestion("informationnen,", "Informationen", rule, lt);
    }

    @Test
    public void testGetSuggestionOrder() throws Exception {
        HunspellRule rule = new GermanSpellerRule(TestTools.getMessages("de"), GermanSpellerRuleTest.GERMAN_DE);
        assertCorrectionsByOrder(rule, "heisst", "hei?t");// "hei?t" should be first

        assertCorrectionsByOrder(rule, "heissen", "hei?en");
        assertCorrectionsByOrder(rule, "m??te", "musste", "m?sste");
        assertCorrectionsByOrder(rule, "schmohren", "schmoren", "Lehmohren");
        assertCorrectionsByOrder(rule, "F?nomen", "Ph?nomen");
        assertCorrectionsByOrder(rule, "homofob", "homophob");
        assertCorrectionsByOrder(rule, "ueber", "?ber");
        // assertCorrectionsByOrder(rule, "uebel", "?bel");
        assertCorrectionsByOrder(rule, "Aerger", "?rger");
        assertCorrectionsByOrder(rule, "Walt", "Wald");
        assertCorrectionsByOrder(rule, "Rythmus", "Rhythmus");
        assertCorrectionsByOrder(rule, "Rytmus", "Rhythmus");
        assertCorrectionsByOrder(rule, "is", "IS", "die", "in", "im", "ist");// 'ist' should actually be preferred...

        assertCorrectionsByOrder(rule, "Fux", "Fuchs");// fixed in morfologik 2.1.4

        assertCorrectionsByOrder(rule, "sch?nken", "Sch?nken");// "schenken" is missing

    }

    @Test
    public void testPosition() throws IOException {
        HunspellRule rule = new GermanSpellerRule(TestTools.getMessages("de"), GermanSpellerRuleTest.GERMAN_DE);
        JLanguageTool lt = new JLanguageTool(GermanSpellerRuleTest.GERMAN_DE);
        RuleMatch[] match1 = rule.match(lt.getAnalyzedSentence(("Er ist entsetzt, weil beim 'Wiederaufbau' das original-gotische Achsfenster mit reichem Ma?werk ausgebaut " + "und an die s?dliche TeStWoRt gesetzt wurde.")));
        Assert.assertThat(match1.length, CoreMatchers.is(1));
        Assert.assertThat(match1[0].getFromPos(), CoreMatchers.is(126));
        Assert.assertThat(match1[0].getToPos(), CoreMatchers.is(134));
    }

    /**
     * number of suggestions seems to depend on previously checked text
     * fixed by not resusing morfologik Speller object
     */
    @Test
    public void testMorfologikSuggestionsWorkaround() throws IOException {
        HunspellRule rule1 = new GermanSpellerRule(TestTools.getMessages("de"), GermanSpellerRuleTest.GERMAN_DE);
        HunspellRule rule2 = new GermanSpellerRule(TestTools.getMessages("de"), GermanSpellerRuleTest.GERMAN_DE);
        JLanguageTool lt = new JLanguageTool(GermanSpellerRuleTest.GERMAN_DE);
        String sentence1 = "Das Absinken der Motordrehzahl bfi gr??eren Geschwindigkeiten war.";
        String sentence2 = "Welche die Eidgenossenschaft ls Staatenbund wiederhergestellt hat.";
        RuleMatch[] matches11 = rule1.match(lt.getAnalyzedSentence(sentence1));
        RuleMatch[] matches12 = rule1.match(lt.getAnalyzedSentence(sentence2));
        RuleMatch[] matches22 = rule2.match(lt.getAnalyzedSentence(sentence2));
        RuleMatch[] matches21 = rule2.match(lt.getAnalyzedSentence(sentence1));
        Assert.assertTrue(Stream.of(matches11, matches12, matches21, matches22).allMatch(( arr) -> arr.length == 1));
        Assert.assertEquals(matches11[0].getSuggestedReplacements().size(), matches21[0].getSuggestedReplacements().size());
        Assert.assertEquals(matches12[0].getSuggestedReplacements().size(), matches22[0].getSuggestedReplacements().size());
    }
}

