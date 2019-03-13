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
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.language.GermanyGerman;
import org.languagetool.rules.RuleMatch;


/**
 *
 *
 * @author Daniel Naber
 */
public class AgreementRuleTest {
    private AgreementRule rule;

    private JLanguageTool lt;

    @Test
    public void testDetNounRule() throws IOException {
        // correct sentences:
        assertGood("So ist es in den USA.");
        assertGood("Das ist der Tisch.");
        assertGood("Das ist das Haus.");
        assertGood("Das ist die Frau.");
        assertGood("Das ist das Auto der Frau.");
        assertGood("Das geh?rt dem Mann.");
        assertGood("Das Auto des Mannes.");
        assertGood("Das interessiert den Mann.");
        assertGood("Das interessiert die M?nner.");
        assertGood("Das Auto von einem Mann.");
        assertGood("Das Auto eines Mannes.");
        assertGood("Des gro?en Mannes.");
        assertGood("Und nach der Nummerierung kommt die ?berschrift.");
        assertGood("Sie wiesen dieselben Verzierungen auf.");
        assertGood("Die erw?hnte Konferenz ist am Samstag.");
        assertGood("Sie erreichten 5 Prozent.");
        assertGood("Sie erreichten mehrere Prozent Zustimmung.");
        assertGood("Die Bestandteile, aus denen Schwefel besteht.");
        assertGood("Ich tat f?r ihn, was kein anderer Autor f?r ihn tat.");
        assertGood("Ich tat f?r ihn, was keine andere Autorin f?r ihn tat.");
        assertGood("Ich tat f?r ihn, was kein anderes Kind f?r ihn tat.");
        assertGood("Ich tat f?r ihn, was dieser andere Autor f?r ihn tat.");
        assertGood("Ich tat f?r ihn, was diese andere Autorin f?r ihn tat.");
        assertGood("Ich tat f?r ihn, was dieses andere Kind f?r ihn tat.");
        assertGood("Ich tat f?r ihn, was jener andere Autor f?r ihn tat.");
        assertGood("Ich tat f?r ihn, was jeder andere Autor f?r ihn tat.");
        assertGood("Ich tat f?r ihn, was jede andere Autorin f?r ihn tat.");
        assertGood("Ich tat f?r ihn, was jedes andere Kind f?r ihn tat.");
        assertGood("Klebe ein Preisschild auf jedes einzelne Produkt.");
        assertGood("Eine Stadt, in der zurzeit eine rege Baut?tigkeit herrscht.");
        assertGood("... wo es zu einer regen Baut?tigkeit kam.");
        assertGood("Mancher ausscheidende Politiker hinterl?sst eine L?cke.");
        assertGood("Kern einer jeden Trag?die ist es, ..");
        assertGood("Das wenige Sekunden alte Baby schrie laut.");
        assertGood("Meistens sind das Frauen, die damit besser umgehen k?nnen.");
        assertGood("Er fragte, ob das Spa? macht.");
        assertGood("Das viele Geld wird ihr helfen.");
        assertGood("Er verspricht jedem hohe Gewinne.");
        assertGood("Er versprach allen Renditen jenseits von 15 Prozent.");
        assertGood("Sind das Eier aus Bodenhaltung?");
        assertGood("Dir macht doch irgendwas Sorgen.");
        assertGood("Sie fragte, ob das wirklich Kunst sei.");
        assertGood("F?r ihn ist das Alltag.");
        assertGood("F?r die Religi?sen ist das Blasphemie.");
        assertGood("Das ist ein super Tipp.");
        assertGood("Er nahm allen Mut zusammen und ging los.");
        assertGood("Sie kann einem Angst einjagen.");
        assertGood("Damit sollten zum einen neue Energien gef?rdert werden, zum anderen der Sozialbereich.");
        assertGood("Nichts ist mit dieser einen Nacht zu vergleichen.");
        assertGood("dann muss Schule dem Rechnung tragen.");
        assertGood("Das Dach von meinem Auto.");
        assertGood("Das Dach von meinen Autos.");
        assertGood("Das Dach meines Autos.");
        assertGood("Das Dach meiner Autos.");
        assertGood("Das Dach meines gro?en Autos.");
        assertGood("Das Dach meiner gro?en Autos.");
        assertGood("Dann schlug er so kr?ftig wie er konnte mit den Schwingen.");
        assertGood("Also wenn wir Gl?ck haben, ...");
        assertGood("Wenn wir Pech haben, ...");
        assertGood("Ledorn ?ffnete eines der an ihr vorhandenen F?cher.");
        assertGood("Auf der einen Seite endlose D?nen");
        assertGood("In seinem Maul hielt er einen blutigen Fleischklumpen.");
        assertGood("Gleichzeitig dachte er intensiv an Nebelschwaden, aus denen Wolken ja bestanden.");
        assertGood("Warum stellte der blo? immer wieder dieselben Fragen?");
        assertGood("Bei der Hinreise.");
        assertGood("Schlie?lich tauchten in einem Waldst?ck unter ihnen Schienen auf.");
        assertGood("Das Wahlrecht, das Frauen damals zugesprochen bekamen.");
        assertGood("Es war Karl, dessen Leiche Donnerstag gefunden wurde.");
        assertGood("Erst recht ich Arbeiter.");
        assertGood("Erst recht wir Arbeiter.");
        assertGood("Erst recht wir flei?igen Arbeiter.");
        assertGood("Dann lud er Freunde ein.");
        assertGood("Dann lud sie Freunde ein.");
        assertGood("Aller Kommunikation liegt dies zugrunde.");
        assertGood("Pragmatisch w?hlt man solche Formeln als Axiome.");
        assertGood("Der eine Polizist rief dem anderen zu...");
        assertGood("Das eine Kind rief dem anderen zu...");
        assertGood("Er wollte seine Interessen wahrnehmen.");
        assertGood("... wo Krieg den Unschuldigen Leid und Tod bringt.");
        assertGood("Der Abschuss eines Papageien.");
        assertGood("Die Beibehaltung des Art. 1 ist geplant.");
        assertGood("Die Verschiebung des bisherigen Art. 1 ist geplant.");
        assertGood("In diesem Fall hatte das Vorteile.");
        assertGood("So hat das Konsequenzen.");
        assertGood("Ein f?r viele wichtiges Anliegen.");
        assertGood("Das weckte bei vielen ungute Erinnerungen.");
        assertGood("Etwas, das einem Angst macht.");
        assertGood("Einem geschenkten Gaul schaut man nicht ins Maul.");
        assertGood("Das erfordert K?nnen.");
        assertGood("Ist das Kunst?");
        assertGood("Ist das Kunst oder Abfall?");
        assertGood("Die Zeitdauer, w?hrend der Wissen n?tzlich bleibt, wird k?rzer.");
        assertGood("Es sollte nicht viele solcher Bilder geben");
        assertGood("In den 80er Jahren.");
        // relative clauses:
        assertGood("Das Recht, das Frauen einger?umt wird.");
        assertGood("Der Mann, in dem quadratische Fische schwammen.");
        assertGood("Der Mann, durch den quadratische Fische schwammen.");
        assertGood("Gutenberg, der quadratische Mann.");
        assertGood("Die gr??te Stuttgarter Gr?nanlage ist der Friedhof.");
        assertGood("Die meisten Lebensmittel enthalten das.");// Lebensmittel has NOG as gender in Morphy

        // TODO: Find agreement errors in relative clauses
        assertBad("Gutenberg, die Genie.");
        // assertBad("Gutenberg, die gr??te Genie.");
        // assertBad("Gutenberg, die gr??te Genie aller Zeiten.");
        // assertGood("Die w?rmsten Monate sind August und September, die k?ltesten Januar und Februar.");
        // some of these used to cause false alarms:
        assertGood("Das M?nchener Fest.");
        assertGood("Das M?nchner Fest.");
        assertGood("Die Planung des M?nchener Festes.");
        assertGood("Das Berliner Wetter.");
        assertGood("Den Berliner Arbeitern ist das egal.");
        assertGood("Das Haus des Berliner Arbeiters.");
        assertGood("Es geh?rt dem Berliner Arbeiter.");
        assertGood("Das Stuttgarter Auto.");
        assertGood("Das Bielefelder Radio.");
        assertGood("Das G?tersloher Radio.");
        assertGood("Das wirklich Wichtige kommt jetzt erst.");
        assertGood("Besonders wenn wir Wermut oder Absinth trinken.");
        assertGood("Ich w?nsche dir alles Gute.");
        assertGood("Es ist nicht bekannt, mit welchem Alter Kinder diese F?higkeit erlernen.");
        assertGood("Dieser ist nun in den Ortungsbereich des einen Roboters gefahren.");
        assertGood("Wenn dies gro?en Erfolg hat, werden wir es weiter f?rdern.");
        assertGood("Die Ereignisse dieses einen Jahres waren sehr schlimm.");
        assertGood("Er musste einen Hochwasser f?hrenden Fluss nach dem anderen ?berqueren.");
        assertGood("Darf ich Ihren F?ller f?r ein paar Minuten ausleihen?");
        assertGood("Bringen Sie diesen Gep?ckaufkleber an Ihrem Gep?ck an.");
        assertGood("Extras, die den Wert Ihres Autos erh?hen.");
        assertGood("Er hat einen 34-j?hrigen Sohn.");
        assertGood("Die Polizei erwischte die Diebin, weil diese Ausweis und Visitenkarte hinterlie?.");
        assertGood("Dieses Vers?umnis soll vertuscht worden sein - es wurde Anzeige erstattet.");
        assertGood("Die Firmen - nicht nur die ausl?ndischen, auch die katalanischen - treibt diese Frage um.");
        // TODO: assertGood("Der Obst und Getr?nke f?hrende Fachmarkt.");
        assertGood("Stell dich dem Leben l?chelnd!");
        assertGood("Die Messe wird auf das vor der Stadt liegende Ausstellungsgel?nde verlegt.");
        assertGood("Sie sind ein den Frieden liebendes Volk.");
        // assertGood("Zum Teil sind das Krebsvorstufen.");
        assertGood("Er sagt, dass das Rache bedeutet.");
        assertGood("Wenn das K?he sind, bin ich ein Elefant.");
        assertGood("Karl sagte, dass sie niemandem Bescheid gegeben habe.");
        assertGood("Es blieb nur dieser eine Satz.");
        assertGood("Oder ist das Mathematikern vorbehalten?");
        assertGood("Wenn hier einer Fragen stellt, dann ich.");
        assertGood("Wenn einer Katzen mag, dann meine Schwester.");
        assertGood("Ergibt das Sinn?");
        assertGood("Sie ist ?ber die Ma?en sch?n.");
        assertGood("Ich vertraue ganz auf die Meinen.");
        assertGood("Was n?tzt einem Gesundheit, wenn man sonst ein Idiot ist?");
        assertGood("Auch das hatte sein Gutes.");
        assertGood("Auch wenn es sein Gutes hatte, war es doch traurig.");
        assertGood("Er wollte doch nur jemandem Gutes tun.");
        assertGood("und das erst Jahrhunderte sp?tere Auftauchen der Legende");
        assertGood("Texas und New Mexico, beides spanische Kolonien, sind...");
        // incorrect sentences:
        assertBad("Ein Buch mit einem ganz ?hnlichem Titel.");
        assertBad("Meiner Chef raucht.");
        assertBad("Er hat eine 34-j?hrigen Sohn.");
        assertBad("Es sind die Tisch.", "dem Tisch", "den Tisch", "der Tisch", "die Tische");
        assertBad("Es sind das Tisch.", "dem Tisch", "den Tisch", "der Tisch");
        assertBad("Es sind die Haus.", "das Haus", "dem Haus", "die H?user");
        assertBad("Es sind der Haus.", "das Haus", "dem Haus", "der H?user");
        assertBad("Es sind das Frau.", "der Frau", "die Frau");
        assertBad("Das Auto des Mann.", "dem Mann", "den Mann", "der Mann", "des Mannes", "des Manns");
        assertBad("Das interessiert das Mann.", "dem Mann", "den Mann", "der Mann");
        assertBad("Das interessiert die Mann.", "dem Mann", "den Mann", "der Mann", "die M?nner");
        assertBad("Das Auto ein Mannes.", "ein Mann", "eines Mannes");
        assertBad("Das Auto einem Mannes.", "einem Mann", "einem Manne", "eines Mannes");
        assertBad("Das Auto einer Mannes.", "eines Mannes");
        assertBad("Das Auto einen Mannes.", "einen Mann", "eines Mannes");
        // assertBad("Das erw?hnt Auto bog nach rechts ab.");    // TODO
        assertGood("Das erlaubt Forschern, neue Versuche durchzuf?hren.");
        assertGood("Dies erm?glicht Forschern, neue Versuche durchzuf?hren.");
        assertBad("Die erw?hnt Konferenz ist am Samstag.");
        assertBad("Die erw?hntes Konferenz ist am Samstag.");
        assertBad("Die erw?hnten Konferenz ist am Samstag.");
        assertBad("Die erw?hnter Konferenz ist am Samstag.");
        assertBad("Die erw?hntem Konferenz ist am Samstag.");
        assertBad("Des gro?er Mannes.");
        assertBad("Das Dach von meine Auto.", "mein Auto", "meine Autos", "meinem Auto");
        assertBad("Das Dach von meinen Auto.", "mein Auto", "meinem Auto", "meinen Autos");
        assertBad("Das Dach mein Autos.", "mein Auto", "meine Autos", "meinen Autos", "meiner Autos", "meines Autos");
        assertBad("Das Dach meinem Autos.", "meine Autos", "meinem Auto", "meinen Autos", "meiner Autos", "meines Autos");
        assertBad("Das Dach meinem gro?en Autos.");
        assertBad("Das Dach mein gro?en Autos.");
        assertBad("Das Klientel der Partei.", "Der Klientel", "Die Klientel");// gender used to be wrong in Morphy data

        assertGood("Die Klientel der Partei.");
        assertBad("Der Haus ist gro?", "Das Haus", "Dem Haus", "Der H?user");
        assertBad("Aber der Haus ist gro?", "das Haus", "dem Haus", "der H?user");
        assertBad("Ich habe einen Feder gefunden.", "eine Feder", "einer Feder");
        assertGood("Wenn die Gott zugeschriebenen Eigenschaften stimmen, dann...");
        assertGood("Dieses Gr?nkern genannte Getreide ist aber nicht backbar.");
        assertGood("Au?erdem unterst?tzt mich Herr M?ller beim abheften");
        assertGood("Au?erdem unterst?tzt mich Frau M?ller beim abheften");
        assertBad("Der Zustand meiner Gehirns.");
        assertBad("Lebensmittel sind da, um den menschliche K?rper zu ern?hren.");
        assertBad("Geld ist da, um den menschliche ?berleben sicherzustellen.");
        assertBad("Sie hatte das kleinen Kaninchen.");
        assertBad("Frau M?ller hat das wichtigen Dokument gefunden.");
        assertBad("Ich gebe dir ein kleine Kaninchen.");
        assertBad("Ich gebe dir ein kleinen Kaninchen.");
        assertBad("Ich gebe dir ein kleinem Kaninchen.");
        assertBad("Ich gebe dir ein kleiner Kaninchen.");
        // assertBad("Ich gebe dir ein klein Kaninchen.");  // already detected by MEIN_KLEIN_HAUS
        assertGood("Ich gebe dir ein kleines Kaninchen.");
        assertBad("Ich gebe dir das kleinen Kaninchen.");
        assertBad("Ich gebe dir das kleinem Kaninchen.");
        assertBad("Ich gebe dir das kleiner Kaninchen.");
        // assertBad("Ich gebe dir das kleines Kaninchen.");  // already detected by ART_ADJ_SOL
        // assertBad("Ich gebe dir das klein Kaninchen.");  // already detected by MEIN_KLEIN_HAUS
        assertGood("Ich gebe dir das kleine Kaninchen.");
        assertGood("Die Top 3 der Umfrage");
        assertGood("Dein Vorschlag befindet sich unter meinen Top 5.");
        assertGood("Unter diesen rief das gro?en Unmut hervor.");
        assertGood("Bei mir l?ste das Panik aus.");
        assertBad("Hier steht Ihre Text.");
        assertBad("Hier steht ihre Text.");
        assertBad("Ich wei? nicht mehr, was unser langweiligen Thema war.");
        assertGood("Aber mein Wissen ?ber die Antike ist ausbauf?hig.");
        assertBad("Er ging ins K?che.");
        assertBad("Er ging ans Luft.");
        assertBad("Eine Niereninsuffizienz f?hrt zur St?rungen des Wasserhaushalts.");
        assertBad("Er stieg durchs Fensters.");
        // TODO: not yet detected:
        // assertBad("Erst recht wir flei?iges Arbeiter.");
        // assertBad("Erst recht ich flei?iges Arbeiter.");
        // assertBad("Das Dach meine gro?en Autos.");
        // assertBad("Das Dach meinen gro?en Autos.");
        // assertBad("Das Dach meine Autos.");
        // assertBad("Es ist das Haus dem Mann.");
        // assertBad("Das interessiert der M?nner.");
        // assertBad("Das interessiert der Mann.");
        // assertBad("Das geh?rt den Mann."); // detected by DEN_DEM
        // assertBad("Es sind der Frau.");
    }

    @Test
    public void testVieleWenige() throws IOException {
        assertGood("Zusammenschluss mehrerer d?rflicher Siedlungen an einer Furt");
        assertGood("F?r einige markante Szenen");
        assertGood("F?r einige markante Szenen baute Hitchcock ein Schloss.");
        assertGood("Haben Sie viele gl?ckliche Erfahrungen in Ihrer Kindheit gemacht?");
        assertGood("Es gibt viele gute Sachen auf der Welt.");
        assertGood("Viele englische W?rter haben lateinischen Ursprung");
        assertGood("Ein Bericht ?ber Fruchtsaft, einige ?hnliche Erzeugnisse und Fruchtnektar");
        assertGood("Der Typ, der seit einiger Zeit immer wieder hierher kommt.");
        assertGood("Jede Schnittmenge abz?hlbar vieler offener Mengen");
        assertGood("Es kam zur Fusion der genannten und noch einiger weiterer Unternehmen.");
        assertGood("Zu dieser Fragestellung gibt es viele unterschiedliche Meinungen.");
    }

    @Test
    public void testDetNounRuleErrorMessages() throws IOException {
        // check detailed error messages:
        assertBadWithMessage("Das Fahrrads.", "bez?glich Kasus");
        assertBadWithMessage("Der Fahrrad.", "bez?glich Genus");
        assertBadWithMessage("Das Fahrr?der.", "bez?glich Numerus");
        assertBadWithMessage("Die Tischen sind ecking.", "bez?glich Kasus");
        assertBadWithMessage("Die Tischen sind ecking.", "und Genus");
        // TODO: input is actually correct
        assertBadWithMessage("Bei dem Papierabz?ge von Digitalbildern bestellt werden.", "bez?glich Kasus, Genus oder Numerus.");
    }

    @Test
    public void testRegression() throws IOException {
        JLanguageTool lt = new JLanguageTool(new GermanyGerman());
        // used to be not detected > 1.0.1:
        String str = "Und so.\r\nDie Bier.";
        List<RuleMatch> matches = lt.check(str);
        Assert.assertEquals(1, matches.size());
    }

    @Test
    public void testDetAdjNounRule() throws IOException {
        // correct sentences:
        assertGood("Das ist der riesige Tisch.");
        assertGood("Der riesige Tisch ist gro?.");
        assertGood("Die Kanten der der riesigen Tische.");
        assertGood("Den riesigen Tisch mag er.");
        assertGood("Es mag den riesigen Tisch.");
        assertGood("Die Kante des riesigen Tisches.");
        assertGood("Dem riesigen Tisch fehlt was.");
        assertGood("Die riesigen Tische sind gro?.");
        assertGood("Der riesigen Tische wegen.");
        assertGood("An der roten Ampel.");
        assertGood("Dann hat das nat?rlich Nachteile.");
        // incorrect sentences:
        assertBad("Es sind die riesigen Tisch.");
        // assertBad("Dort, die riesigen Tischs!");    // TODO: error not detected because of comma
        assertBad("Als die riesigen Tischs kamen.");
        assertBad("Als die riesigen Tisches kamen.");
        assertBad("Der riesigen Tisch und so.");
        assertBad("An der roter Ampel.");
        assertBad("An der rote Ampel.");
        assertBad("An der rotes Ampel.");
        assertBad("An der rotem Ampel.");
        assertBad("Er hatte ihn aus dem 1,4 Meter tiefem Wasser gezogen.");
        assertBad("Er hatte ihn aus dem 1,4 Meter tiefem Wasser gezogen.");
        assertBad("Er hatte eine sehr schweren Infektion.");
        assertBad("Ein fast 5 Meter hohem Haus.");
        assertBad("Ein f?nf Meter hohem Haus.");
        // TODO: not yet detected:
        // assertBad("An der rot Ampel.");
    }
}

