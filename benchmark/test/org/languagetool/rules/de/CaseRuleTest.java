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
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.AnalyzedSentence;
import org.languagetool.JLanguageTool;
import org.languagetool.language.GermanyGerman;


public class CaseRuleTest {
    private CaseRule rule;

    private JLanguageTool lt;

    @Test
    public void testRuleActivation() throws IOException {
        Assert.assertTrue(rule.supportsLanguage(new GermanyGerman()));
    }

    @Test
    public void testRule() throws IOException {
        // correct sentences:
        assertGood("Das ist eine Abkehr von Gottes Geboten.");
        assertGood("Dem Hund Futter geben");
        assertGood("Heute spricht Frau Stieg.");
        assertGood("Ein einfacher Satz zum Testen.");
        assertGood("Das Laufen f?llt mir leicht.");
        assertGood("Das Winseln st?rt.");
        assertGood("Das schl?gt nicht so zu Buche.");
        assertGood("Dirk Hetzel ist ein Name.");
        assertGood("Sein Verhalten war okay.");
        assertGood("Hier ein Satz. \"Ein Zitat.\"");
        assertGood("Hier ein Satz. 'Ein Zitat.'");
        assertGood("Hier ein Satz. ?Ein Zitat.?");
        assertGood("Hier ein Satz. ?Ein Zitat.?");
        assertGood("Hier ein Satz. (Noch einer.)");
        assertGood("Hier geht es nach Tel Aviv.");
        assertGood("Unser J?ngster ist da.");
        assertGood("Alles Erfundene ist wahr.");
        assertGood("Sie hat immer ihr Bestes getan.");
        assertGood("Er wird etwas Verr?cktes tr?umen.");
        assertGood("Er wird etwas sch?n Verr?cktes tr?umen.");
        assertGood("Er wird etwas ganz sch?n Verr?cktes tr?umen.");
        assertGood("Mit aufgew?hltem Innerem.");
        assertGood("Mit v?llig aufgew?hltem Innerem.");
        assertGood("Er wird etwas so Verr?cktes tr?umen.");
        assertGood("Tom ist etwas ?ber drei?ig.");
        assertGood("Diese Angriffe bleiben im Verborgenen.");
        assertGood("Ihr sollt mich das wissen lassen.");
        assertGood("Wenn er mich das rechtzeitig wissen l?sst, gerne.");
        assertGood("Und sein v?llig aufgew?hltes Inneres erz?hlte von den Geschehnissen.");
        assertGood("Aber sein aufgew?hltes Inneres erz?hlte von den Geschehnissen.");
        assertGood("Sein aufgew?hltes Inneres erz?hlte von den Geschehnissen.");
        assertGood("Aber sein Inneres erz?hlte von den Geschehnissen.");
        assertGood("Ein Kaninchen, das zaubern kann.");
        assertGood("Keine Ahnung, wie ich das pr?fen sollte.");
        assertGood("Und dann noch Strafrechtsdogmatikerinnen.");
        assertGood("Er kann ihr das bieten, was sie verdient.");
        assertGood("Das fragen sich mittlerweile viele.");
        assertGood("Ich habe gehofft, dass du das sagen w?rdest.");
        assertGood("Eigentlich h?tte ich das wissen m?ssen.");
        assertGood("Mir tut es wirklich leid, Ihnen das sagen zu m?ssen.");
        assertGood("Der Wettkampf endete im Unentschieden.");
        assertGood("Er versuchte, Neues zu tun.");
        assertGood("Du musst das wissen, damit du die Pr?fung bestehst");
        assertGood("Er kann ihr das bieten, was sie verdient.");
        assertGood("Er fragte, ob das gelingen wird.");
        assertGood("Er mag Obst, wie zum Beispel Apfelsinen.");
        assertGood("Er will die Ausgaben f?r Umweltschutz und Soziales k?rzen.");
        assertGood("Die Musicalverfilmung ?Die Sch?ne und das Biest? bricht mehrere Rekorde.");
        assertGood("Joachim Sauer lobte Johannes Rau.");
        assertGood("Im Falle des Menschen ist dessen wirkendes Wollen gegeben.");
        assertGood("Szenario: 1) Zwei Galaxien verschmelzen.");// should be accepted by isNumbering

        assertGood("Existieren Au?erirdische im Universum?");
        assertGood("Tom vollbringt Au?erordentliches.");
        assertGood("Er f?hrt B?ses im Schilde.");
        assertGood("Es gab ?berlebende.");
        assertGood("'Wir werden das stoppen.'");
        assertGood("Wahre Liebe muss das aushalten.");
        assertGood("Du kannst das machen.");
        assertGood("Vor dem Aus stehen.");
        assertGood("Ich Armer!");
        assertGood("Parks Vertraute Choi Soon Sil ist zu drei Jahren Haft verurteilt worden.");
        assertGood("Bei einer Veranstaltung Rechtsextremer passierte es.");
        assertGood("Eine Gruppe Betrunkener singt.");
        assertGood("Bei Betreten des Hauses.");
        assertGood("Das Aus f?r Italien ist bitter.");
        assertGood("Das Aus kam unerwartet.");
        assertGood("Anmeldung bis Fr. 1.12.");
        assertGood("Weil er Unm?ndige sexuell missbraucht haben soll, wurde ein Lehrer verhaftet.");
        assertGood("Tausende Gl?ubige kamen.");
        assertGood("Es kamen Tausende Gl?ubige.");
        assertGood("Das schlie?en Forscher aus den gefundenen Spuren.");
        assertGood("Wieder Verletzter bei Unfall");
        assertGood("Eine Gruppe Aufst?ndischer verw?stete die Bar.");
        assertGood("?Dieser Satz.? Hier kommt der n?chste Satz.");
        assertGood("Dabei werden im Wesentlichen zwei Prinzipien verwendet:");
        assertGood("Er fragte, ob das gelingen oder scheitern wird.");
        assertGood("Einen Tag nach Bekanntwerden des Skandals");
        assertGood("Das machen eher die Erwachsenen.");
        assertGood("Das ist ihr Zuhause.");
        assertGood("Das ist Sandras Zuhause.");
        assertGood("Das machen eher wohlhabende Leute.");
        // assertBad("Sie sind nicht Verst?ndlich");
        assertBad("Das machen der T?ne ist schwierig.");
        assertBad("Sie Vertraute niemandem.");
        assertBad("Beten Lernt man in N?ten.");
        assertBad("Ich gehe gerne Joggen.");
        assertBad("Er ist Gro?.");
        assertBad("Die Zahl ging auf ?ber 1.000 zur?ck.");
        assertBad("Er sammelt Gro?e und kleine Tassen.");
        assertBad("Er sammelt Gro?e, mittlere und kleine Tassen.");
        assertBad("Dann will sie mit London ?ber das Referendum verhandeln.");
        assertBad("Sie kann sich t?glich ?ber vieles freuen.");
        assertBad("Der Vater (51) Fuhr nach Rom.");
        assertBad("Er m?sse ?berlegen, wie er das Problem l?st.");
        assertBad("Er sagte, dass er ?ber einen Stein stolperte.");
        assertBad("Tom ist etwas ?ber Drei?ig.");
        assertBad("Unser warten wird sich lohnen.");
        assertBad("Tom kann mit fast Allem umgehen.");
        assertBad("Dabei ?bersah er sie.");
        assertBad("Der Brief wird am Mittwoch in Br?ssel ?bergeben.");
        assertBad("Damit sollen sie die Versorgung in der Region ?bernehmen.");
        assertBad("Die Unfallursache scheint gekl?rt, ein Lichtsignal wurde ?berfahren.");
        assertBad("Der Lenker hatte die H?chstgeschwindigkeit um 76 km/h ?berschritten.");
        // assertBad("Das Extreme Sportfest");
        // assertBad("Das Extreme Sportfest findet morgen statt.");
        assertGood("Stets suchte er das Extreme.");
        assertGood("Ich m?chte zwei Kilo Zwiebeln.");
        // "NIL" reading in Morphy that used to confuse CaseRule:
        assertGood("Ein Menschenfreund.");
        // works only thanks to addex.txt:
        assertGood("Der Nachfahre.");
        // both can be correct:
        assertGood("Hier ein Satz, \"Ein Zitat.\"");
        assertGood("Hier ein Satz, \"ein Zitat.\"");
        // Exception 'Le':
        assertGood("Schon Le Monde schrieb das.");
        // unknown word:
        assertGood("In Blubberdorf macht man das so.");
        // Exception defined in case_rule_exceptions.txt:
        assertGood("Der Thriller spielt zur Zeit des Zweiten Weltkriegs");
        assertGood("Anders als physikalische Konstanten werden mathematische Konstanten unabh?ngig von jedem physikalischen Ma? definiert.");
        assertGood("Eine besonders einfache Klasse bilden die polylogarithmischen Konstanten.");
        assertGood("Das s?dlich von Berlin gelegene D?rfchen.");
        assertGood("Weil er das kommen sah, traf er Vorkehrungen.");
        assertGood("Sie werden im Allgemeinen gefasst.");
        assertGood("Sie werden im allgemeinen Fall gefasst.");
        // assertBad("Sie werden im allgemeinen gefasst.");
        assertBad("Sie werden im Allgemeinen Fall gefasst.");
        // sentences that used to trigger an error because of incorrect compound tokenization:
        assertGood("Das sind Euroscheine.");
        assertGood("John Stallman isst.");
        assertGood("Das ist die neue Gesellschafterin hier.");
        assertGood("Das ist die neue Dienerin hier.");
        assertGood("Das ist die neue Geigerin hier.");
        assertGood("Die ersten Gespanne erreichen K?ln.");
        assertGood("Er beschrieb den Angeklagten wie einen Schuldigen");
        assertGood("Er beschrieb den Angeklagten wie einen Schuldigen.");
        assertGood("Es dauerte bis ins neunzehnte Jahrhundert");
        assertGood("Das ist das D?mmste, was ich je gesagt habe.");
        assertBad("Das ist das D?mmste Kind.");
        assertGood("Wacht auf, Verdammte dieser Welt!");
        assertGood("Er sagt, dass Geistliche davon betroffen sind.");
        assertBad("Er sagt, dass Geistliche W?rdentr?ger davon betroffen sind.");
        assertBad("Er sagt, dass Geistliche und weltliche W?rdentr?ger davon betroffen sind.");
        assertBad("Er ist begeistert Von der F?lle.");
        assertBad("Er wohnt ?ber einer Garage.");
        assertBad("?Wei?er Rauch? ?ber Athen");
        assertBad("Die Anderen 90 Prozent waren krank.");
        assertGood("Man sagt, Liebe mache blind.");
        assertGood("Die Deutschen sind sehr listig.");
        assertGood("Der Lesestoff bestimmt die Leseweise.");
        assertGood("Ich habe nicht viel von einem Reisenden.");
        assertGood("Die Vereinigten Staaten");
        assertGood("Der Satz vom ausgeschlossenen Dritten.");
        // TODO:
        assertGood("Die Ausgew?hlten werden gut betreut.");
        assertGood("Die ausgew?hlten Leute werden gut betreut.");
        // assertBad("Die ausgew?hlten werden gut betreut.");
        assertBad("Die Ausgew?hlten Leute werden gut betreut.");
        // used to trigger error because of wrong POS tagging:
        assertGood("Die Schlinge zieht sich zu.");
        assertGood("Die Schlingen ziehen sich zu.");
        // used to trigger error because of "abbreviation"
        assertGood("Sie f?llt auf durch ihre hilfsbereite Art. Zudem zeigt sie soziale Kompetenz.");
        assertGood("Das ist es: kein Satz.");
        assertGood("Werner Dahlheim: Die Antike.");
        assertGood("1993: Der talentierte Mr. Ripley");
        assertGood("Ian Kershaw: Der Hitler-Mythos: F?hrerkult und Volksmeinung.");
        assertBad("Das ist es: Kein Satz.");
        assertBad("Wen magst du lieber: Die Giants oder die Dragons?");
        assertGood("Das wirklich Wichtige ist dies:");
        assertGood("Das wirklich wichtige Verfahren ist dies:");
        // assertBad("Das wirklich wichtige ist dies:");
        assertBad("Das wirklich Wichtige Verfahren ist dies:");
        // incorrect sentences:
        assertBad("Die Sch?ne T?r");
        assertBad("Das Blaue Auto.");
        // assertBad("Der Gr?ne Baum.");
        assertBad("Ein Einfacher Satz zum Testen.");
        assertBad("Eine Einfache Frage zum Testen?");
        assertBad("Er kam Fr?her als sonst.");
        assertBad("Er rennt Schneller als ich.");
        assertBad("Das Winseln St?rt.");
        assertBad("Sein verhalten war okay.");
        Assert.assertEquals(1, lt.check("Karten werden vom Auswahlstapel gezogen. Auch [?] Der Auswahlstapel geh?rt zum Inhalt.").size());
        // assertEquals(2, lt.check("Karten werden vom Auswahlstapel gezogen. Auch [...] Der Auswahlstapel geh?rt zum Inhalt.").size());
        Assert.assertEquals(0, lt.check("Karten werden vom Auswahlstapel gezogen. [?] Der Auswahlstapel geh?rt zum Inhalt.").size());
        // assertEquals(1, lt.check("Karten werden vom Auswahlstapel gezogen. [...] Der Auswahlstapel geh?rt zum Inhalt.").size());
        // TODO: error not found:
        // assertBad("So schwer, dass selbst Er ihn nicht hochheben kann.");
        assertGood("Im Norwegischen klingt das sch?ner.");
        assertGood("?bersetzt aus dem Norwegischen von Ingenieur Frederik Dingsbums.");
        assertGood("Dem norwegischen Ingenieur gelingt das gut.");
        assertBad("Dem Norwegischen Ingenieur gelingt das gut.");
        assertGood("Peter Peterson, dessen Namen auf Griechisch Stein bedeutet.");
        assertGood("Peter Peterson, dessen Namen auf Griechisch gut klingt.");
        assertGood("Das dabei Erlernte und Erlebte ist sehr n?tzlich.");
        assertBad("Das dabei erlernte und Erlebte Wissen ist sehr n?tzlich.");
        assertGood("Ein Kapit?n verl?sst als Letzter das sinkende Schiff.");
        assertBad("Diese Regelung wurde als ?berholt bezeichnet.");
        assertBad("Die Dolmetscherin und Der Vorleser gehen spazieren.");
        assertGood("Es hilft, die Harmonie zwischen F?hrer und Gef?hrten zu st?tzen.");
        assertGood("Das Geb?ude des Ausw?rtigen Amts.");
        assertGood("Das Geb?ude des Ausw?rtigen Amtes.");
        assertGood("   Im Folgenden beschreibe ich das Haus.");// triggers WHITESPACE_RULE, but should not trigger CASE_RULE (see github #258)

        assertGood("\"Im Folgenden beschreibe ich das Haus.\"");// triggers TYPOGRAFISCHE_ANFUEHRUNGSZEICHEN, but should not trigger CASE_RULE

        assertGood("Gestern habe ich 10 Spie?e gegessen.");
        assertGood("Die Verurteilten wurden mit dem Fallbeil enthauptet.");
        assertGood("Den Begnadigten kam ihre Reue zugute.");
        assertGood("Die Zahl Vier ist gerade.");
        assertGood("Ich glaube, dass das geschehen wird.");
        assertGood("Ich glaube, dass das geschehen k?nnte.");
        assertGood("Ich glaube, dass mir das gefallen wird.");
        assertGood("Ich glaube, dass mir das gefallen k?nnte.");
        assertGood("Alldem wohnte etwas faszinierend R?tselhaftes inne.");
        assertGood("Schau mich an, Kleine!");
        assertGood("Schau mich an, S??er!");
        assertGood("Wei?t du, in welchem Jahr das geschehen ist?");
        assertGood("Das wissen viele nicht.");
        assertBad("Das sagen haben hier viele.");
        assertGood("Die zum Tode Verurteilten wurden in den Hof gef?hrt.");
        assertGood("Wenn Sie das schaffen, retten Sie mein Leben!");
        assertGood("Etwas Gr?nes, Schleimiges klebte an dem Stein.");
        assertGood("Er bef?rchtet Schlimmeres.");
        // uppercased adjective compounds
        assertGood("Er isst UV-bestrahltes Obst.");
        assertGood("Er isst Na-haltiges Obst.");
        assertGood("Er vertraut auf CO2-arme Wasserkraft");
        assertGood("Das Entweder-oder ist kein Problem.");
        assertGood("Er liebt ihre Makeup-freie Haut.");
    }

    @Test
    public void testSubstantivierteVerben() throws IOException {
        // correct sentences:
        assertGood("Das fahrende Auto.");
        assertGood("Das k?nnen wir so machen.");
        assertGood("Denn das Fahren ist einfach.");
        assertGood("Das Fahren ist einfach.");
        assertGood("Das Gehen f?llt mir leicht.");
        assertGood("Das Ernten der Kartoffeln ist m?hsam.");
        assertGood("Entschuldige das sp?te Weiterleiten.");
        assertGood("Ich liebe das Lesen.");
        assertGood("Das Betreten des Rasens ist verboten.");
        assertGood("Das haben wir aus eigenem Antrieb getan.");
        assertGood("Das haben wir.");
        assertGood("Das haben wir schon.");
        assertGood("Das lesen sie doch sicher in einer Minute durch.");
        assertGood("Das lesen Sie doch sicher in einer Minute durch!");
        assertGood("Formationswasser, das oxidiert war.");
        // Source of the following examples: http://www.canoo.net/services/GermanSpelling/Amtlich/GrossKlein/pgf57-58.html
        assertGood("Das Lesen f?llt mir schwer.");
        assertGood("Sie h?rten ein starkes Klopfen.");
        assertGood("Wer erledigt das Fensterputzen?");
        assertGood("Viele waren am Zustandekommen des Vertrages beteiligt.");
        assertGood("Die Sache kam ins Stocken.");
        assertGood("Das ist zum Lachen.");
        assertGood("Euer Fernbleiben fiel uns auf.");
        assertGood("Uns half nur noch lautes Rufen.");
        assertGood("Die Mitbewohner begn?gten sich mit Wegsehen und Schweigen.");
        assertGood("Sie wollte auf Biegen und Brechen gewinnen.");
        assertGood("Er klopfte mit Zittern und Zagen an.");
        assertGood("Ich nehme die Tabletten auf Anraten meiner ?rztin.");
        assertGood("Sie hat ihr Soll erf?llt.");
        assertGood("Dies ist ein absolutes Muss.");
        assertGood("Das Lesen f?llt mir schwer.");
        // incorrect sentences:
        assertBad("Das fahren ist einfach.");
        assertBad("Denn das fahren ist einfach.");
        assertBad("Denn das laufen ist einfach.");
        assertBad("Denn das essen ist einfach.");
        assertBad("Denn das gehen ist einfach.");
        assertBad("Das Gro?e Auto wurde gewaschen.");
        assertBad("Ich habe ein Neues Fahrrad.");
        // TODO: detect all the cases not preceded with 'das'
    }

    @Test
    public void testPhraseExceptions() throws IOException {
        // correct sentences:
        assertGood("Das gilt ohne Wenn und Aber.");
        assertGood("Ohne Wenn und Aber");
        assertGood("Das gilt ohne Wenn und Aber bla blubb.");
        // as long as phrase exception isn't complete, there's no error:
        assertGood("Das gilt ohne wenn");
        assertGood("Das gilt ohne wenn und");
        assertGood("wenn und aber");
        assertGood("und aber");
        assertGood("aber");
        // incorrect sentences:
        // error not found here as it's in the XML rules:
        // assertBad("Das gilt ohne wenn und aber.");
    }

    @Test
    public void testCompareLists() throws IOException {
        AnalyzedSentence sentence1 = lt.getAnalyzedSentence("Hier ein Test");
        Assert.assertTrue(rule.compareLists(sentence1.getTokensWithoutWhitespace(), 0, 2, new Pattern[]{ Pattern.compile(""), Pattern.compile("Hier"), Pattern.compile("ein") }));
        Assert.assertTrue(rule.compareLists(sentence1.getTokensWithoutWhitespace(), 1, 2, new Pattern[]{ Pattern.compile("Hier"), Pattern.compile("ein") }));
        Assert.assertTrue(rule.compareLists(sentence1.getTokensWithoutWhitespace(), 0, 3, new Pattern[]{ Pattern.compile(""), Pattern.compile("Hier"), Pattern.compile("ein"), Pattern.compile("Test") }));
        Assert.assertFalse(rule.compareLists(sentence1.getTokensWithoutWhitespace(), 0, 4, new Pattern[]{ Pattern.compile(""), Pattern.compile("Hier"), Pattern.compile("ein"), Pattern.compile("Test") }));
        AnalyzedSentence sentence2 = lt.getAnalyzedSentence("das Heilige R?mische Reich");
        Assert.assertTrue(rule.compareLists(sentence2.getTokensWithoutWhitespace(), 0, 4, new Pattern[]{ Pattern.compile(""), Pattern.compile("das"), Pattern.compile("Heilige"), Pattern.compile("R?mische"), Pattern.compile("Reich") }));
        Assert.assertFalse(rule.compareLists(sentence2.getTokensWithoutWhitespace(), 8, 11, new Pattern[]{ Pattern.compile(""), Pattern.compile("das"), Pattern.compile("Heilige"), Pattern.compile("R?mische"), Pattern.compile("Reich") }));
    }
}

