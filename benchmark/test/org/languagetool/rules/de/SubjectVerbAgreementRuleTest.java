/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2015 Daniel Naber (http://www.danielnaber.de)
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
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;


public class SubjectVerbAgreementRuleTest {
    private static SubjectVerbAgreementRule rule;

    private static JLanguageTool langTool;

    @Test
    public void testTemp() throws IOException {
        // For debugging, comment in the next three lines:
        // GermanChunker.setDebug(true);
        // assertGood("...");
        // assertBad("...");
        // Hier ist (auch: sind) sowohl Anhalten wie Parken verboten.
        // TODO - false alarms from Tatoeba and Wikipedia:
        // "Die restlichen sechsundachtzig oder siebenundachtzig Prozent sind in der Flasche.",
        // "Die F?hrung des Wortes in Unternehmensnamen ist nur mit Genehmigung zul?ssig.",   // OpenNLP doesn't find 'Unternehmensnamen' as a noun
        // "Die ?ltere der beiden T?chter ist hier.",
        // "Liebe und Hochzeit sind nicht das Gleiche."
        // "Die Typologie oder besser Typographie ist die Klassifikation von Objekten"
        // "Im Falle qualitativer, quantitativer und ?rtlicher Ver?nderung ist dies ein konkretes Einzelding,"
        // "...zu finden, in denen die P?pste selbst Partei waren."
        // "Hauptfigur der beiden B?cher ist Golan Trevize."
        // "Das gr??te und bekannteste Unternehmen dieses Genres ist der Cirque du Soleil."
        // "In Schweden, Finnland, D?nemark und ?sterreich ist die Haltung von Wildtieren erlaubt."
        // "Die einzige Waffe, die keine Waffe der Gewalt ist: die Wahrheit."
        // "Du wei?t ja wie t?richt Verliebte sind."
        // "Freies Assoziieren und Phantasieren ist erlaubt."
        // "In den beiden St?dten Bremen und Bremerhaven ist jeweils eine M?llverbrennungsanlage in Betrieb."
        // "Hauptstadt und gr??te Stadt des Landes ist Sarajevo."
        // "Durch Rutschen, Fallrohre oder Schl?uche ist der Beton bis in die Schalung zu leiten."
        // "Wegen ihres ganzen Erfolgs war sie ungl?cklich."
        // "Eines der bedeutendsten Museen ist das Museo Nacional de Bellas Artes."
        // "Die Nominierung der Filme sowie die Auswahl der Jurymitglieder ist Aufgabe der Festivaldirektion."
        // "Ehemalige Fraktionsvorsitzende waren Schmidt, Kohl und Merkel."
        // "Die H?lfte der ?pfel sind verfault."
        // "... in der Geschichte des Museums, die Sammlung ist seit M?rz 2011 dauerhaft der ?ffentlichkeit zug?nglich."
        // "Ein gutes Aufw?rmen und Dehnen ist zwingend notwendig."
        // "Eine Stammfunktion oder ein unbestimmtes Integral ist eine mathematische Funktion ..."
        // "Wenn die Begeisterung f?r eine Person, Gruppe oder Sache religi?ser Art ist ..."
        // "Ein Staat, dessen Oberhaupt nicht ein K?nig oder eine K?nigin ist."
        // "Des Menschen gr??ter Feind und bester Freund ist ein anderer Mensch."
        // "Die Nauheimer Musiktage, die zu einer Tradition geworden sind und immer wieder ein kultureller H?hepunkt sind."
        // "Ein erheblicher Teil der anderen Transportmaschinen waren schwerbesch?digt."  // ??
        // "Die herrschende Klasse und die Klassengesellschaft war geboren."  // ??
        // "Russland ist der gr??te Staat der Welt und der Vatikan ist der kleinste Staat der Welt.",
        // "Eine Rose ist eine Blume und eine Taube ist ein Vogel.",
        // "Der beste Beobachter und der tiefste Denker ist immer der mildeste Richter.",
        // assertGood("Dumas ist der Familienname folgender Personen.");  // Dumas wird als Plural von Duma erkannt
        // assertGood("Berlin war Hauptstadt des Vergn?gens und der Wintergarten war angesagt.");  // wg. 'und'
        // assertGood("Elemente eines axiomatischen Systems sind:");  // 'Elemente' ist ambig (SIN, PLU)
        // assertGood("Auch wenn Dortmund gr??te Stadt und ein Zentrum dieses Raums ist.");  // unsere 'und'-Regel darf hier nicht matchen
        // assertGood("Die Zielgruppe waren Glaubensangeh?rige im Ausland sowie Reisende.");  // Glaubensangeh?rige hat kein Plural-Reading in Morphy
    }

    @Test
    public void testPrevChunkIsNominative() throws IOException {
        Assert.assertTrue(SubjectVerbAgreementRuleTest.rule.prevChunkIsNominative(getTokens("Die Katze ist s??"), 2));
        Assert.assertTrue(SubjectVerbAgreementRuleTest.rule.prevChunkIsNominative(getTokens("Das Fell der Katzen ist s??"), 4));
        Assert.assertFalse(SubjectVerbAgreementRuleTest.rule.prevChunkIsNominative(getTokens("Dem Mann geht es gut."), 2));
        Assert.assertFalse(SubjectVerbAgreementRuleTest.rule.prevChunkIsNominative(getTokens("Dem alten Mann geht es gut."), 2));
        Assert.assertFalse(SubjectVerbAgreementRuleTest.rule.prevChunkIsNominative(getTokens("Beiden Filmen war kein Erfolg beschieden."), 2));
        Assert.assertFalse(SubjectVerbAgreementRuleTest.rule.prevChunkIsNominative(getTokens("Aber beiden Filmen war kein Erfolg beschieden."), 3));
        // assertFalse(rule.prevChunkIsNominative(getTokens("Der Katzen Fell ist s??"), 3));
    }

    @Test
    public void testArrayOutOfBoundsBug() throws IOException {
        SubjectVerbAgreementRuleTest.rule.match(SubjectVerbAgreementRuleTest.langTool.getAnalyzedSentence("Die nicht Teil des N?herungsmodells sind"));
    }

    @Test
    public void testRuleWithIncorrectSingularVerb() throws IOException {
        List<String> sentences = // http://canoo.net/blog/2012/04/02/ein-drittel-der-schueler-istsind/
        // "Katzen ist sch?n."
        Arrays.asList("Die Autos ist schnell.", "Der Hund und die Katze ist drau?en.", "Ein Hund und eine Katze ist sch?n.", "Der Hund und die Katze ist sch?n.", "Der gro?e Hund und die Katze ist sch?n.", "Der Hund und die graue Katze ist sch?n.", "Der gro?e Hund und die graue Katze ist sch?n.", "Die Kenntnisse ist je nach Bildungsgrad verschieden.", "Die Kenntnisse der Sprachen ist je nach Bildungsgrad verschieden.", "Die Kenntnisse der Sprache ist je nach Bildungsgrad verschieden.", "Die Kenntnisse der europ?ischen Sprachen ist je nach Bildungsgrad verschieden.", "Die Kenntnisse der neuen europ?ischen Sprachen ist je nach Bildungsgrad verschieden.", "Die Kenntnisse der deutschen Sprache ist je nach Bildungsgrad verschieden.", "Die Kenntnisse der aktuellen deutschen Sprache ist je nach Bildungsgrad verschieden.", "Drei Katzen ist im Haus.", "Drei kleine Katzen ist im Haus.", "Viele Katzen ist sch?n.", "Drei Viertel der Erdoberfl?che ist Wasser.", "Die ?ltesten und bekanntesten Ma?nahmen ist die Einrichtung von Schutzgebieten.", "Ein Gramm Pfeffer waren fr?her wertvoll.", "Isolation und ihre ?berwindung ist ein h?ufiges Thema in der Literatur.");
        for (String sentence : sentences) {
            assertBad(sentence);
        }
    }

    @Test
    public void testRuleWithCorrectSingularVerb() throws IOException {
        List<String> sentences = // 'neurowissenschaftlichen' not known
        // Ellen: auch Plural von Elle
        Arrays.asList("All diesen Bereichen ist gemeinsam, dass sie unterfinanziert sind.", "Die Katze ist sch?n.", "Die eine Katze ist sch?n.", "Eine Katze ist sch?n.", "Beiden Filmen war kein Erfolg beschieden.", "In einigen F?llen ist der vermeintliche Besch?tzer schwach.", "Was Wasser f?r die Fische ist.", "In den letzten Jahrzehnten ist die Zusammenarbeit der Astronomie verbessert worden.", "F?r Oberleitungen bei elektrischen Bahnen ist es dagegen anders.", "... deren Thema die Liebe zwischen m?nnlichen Charakteren ist.", "Mehr als das in westlichen Produktionen der Fall ist.", "Da das ein fast aussichtsloses Unterfangen ist.", "Was sehr verbreitet bei der Synthese organischer Verbindungen ist.", "In chemischen Komplexverbindungen ist das Kation wichtig.", "In chemischen Komplexverbindungen ist das As5+-Kation wichtig.", "Die selbstst?ndige Behandlung psychischer St?rungen ist jedoch ineffektiv.", "Die selbstst?ndige Behandlung eigener psychischer St?rungen ist jedoch ineffektiv.", "Im Gegensatz zu anderen akademischen Berufen ist es in der Medizin durchaus ?blich ...", "Im Unterschied zu anderen Branchen ist ?rzten anpreisende Werbung verboten.", "Aus den verf?gbaren Quellen ist es ersichtlich.", "Das M?dchen mit den langen Haaren ist Judy.", "Der Durchschnitt offener Mengen ist nicht notwendig offen.", "Der Durchschnitt vieler offener Mengen ist nicht notwendig offen.", "Der Durchschnitt unendlich vieler offener Mengen ist nicht notwendig offen.", "Der Ausgangspunkt f?r die heute gebr?uchlichen Alphabete ist ...", "Nach sieben m?nnlichen Amtsvorg?ngern ist Merkel ...", "F?r einen japanischen Hamburger ist er g?nstig.", "Derzeitiger B?rgermeister ist seit 2008 der ehemalige Minister M?ller.", "Derzeitiger B?rgermeister der Stadt ist seit 2008 der ehemalige Minister M?ller.", "Die Eingabe mehrerer assoziativer Verkn?pfungen ist beliebig.", "Die inhalative Anwendung anderer Adrenalinpr?parate zur Akutbehandlung asthmatischer Beschwerden ist somit au?erhalb der arzneimittelrechtlichen Zulassung.", "Die Kategorisierung anhand morphologischer Merkmale ist nicht objektivierbar.", "Die Kategorisierung mit morphologischen Merkmalen ist nicht objektivierbar.", "Ute, deren Hauptproblem ihr Mangel an Problemen ist, geht baden.", "Ute, deren Hauptproblem ihr Mangel an realen Problemen ist, geht baden.", "In zwei Wochen ist Weihnachten.", "In nur zwei Wochen ist Weihnachten.", "Mit chemischen Methoden ist es m?glich, das zu erreichen.", "F?r die Stadtteile ist auf kommunalpolitischer Ebene jeweils ein Beirat zust?ndig.", "F?r die Stadtteile und selbst?ndigen Ortsteile ist auf kommunalpolitischer Ebene jeweils ein Beirat zust?ndig.", "Die Qualit?t der Stra?en ist unterschiedlich.", "In deutschen Installationen ist seit Version 3.3 ein neues Feature vorhanden.", "In deren Installationen ist seit Version 3.3 ein neues Feature vorhanden.", "In deren deutschen Installationen ist seit Version 3.3 ein neues Feature vorhanden.", "Die F?hrung des Wortes in Unternehmensnamen ist nur mit Genehmigung zul?ssig.", "Die F?hrung des Wortes in Unternehmensnamen und Institutionen ist nur mit Genehmigung zul?ssig.", "Die Hintereinanderreihung mehrerer Einheitenvorsatznamen oder Einheitenvorsatzzeichen ist nicht zul?ssig.", "Eines ihrer drei Autos ist blau und die anderen sind wei?.", "Eines von ihren drei Autos ist blau und die anderen sind wei?.", "Bei f?nf Filmen war Robert F. Boyle f?r das Production Design verantwortlich.", "Insbesondere das Wasserstoffatom als das einfachste aller Atome war dabei wichtig.", "In den darauf folgenden Wochen war die Partei f?hrungslos", "Gegen die wegen ihrer Sch?nheit bewunderte Phryne ist ein Asebie-Prozess ?berliefert.", "Dieses f?r ?rzte und ?rztinnen festgestellte Risikoprofil ist berufsunabh?ngig.", "Das ist problematisch, da kDa eine Masseeinheit und keine Gewichtseinheit ist.", "Nach sachlichen oder milit?rischen Kriterien war das nicht n?tig.", "Die Pyramide des Friedens und der Eintracht ist ein Bauwerk.", "Ohne Architektur der Griechen ist die westliche Kultur der Neuzeit nicht denkbar.", "Ohne Architektur der Griechen und R?mer ist die westliche Kultur der Neuzeit nicht denkbar.", "Ohne Architektur und Kunst der Griechen und R?mer ist die westliche Kultur der Neuzeit nicht denkbar.", "In denen jeweils f?r eine bestimmte Anzahl Elektronen Platz ist.", "Mit ?ber 1000 Handschriften ist Aristoteles ein Vielschreiber.", "Mit ?ber neun Handschriften ist Aristoteles ein Vielschreiber.", "Die Klammerung assoziativer Verkn?pfungen ist beliebig.", "Die Klammerung mehrerer assoziativer Verkn?pfungen ist beliebig.", "Einen Sonderfall bildete jedoch ?gypten, dessen neue Hauptstadt Alexandria eine Gr?ndung Alexanders und der Ort seines Grabes war.", "Jeder Junge und jedes M?dchen war erfreut.", "Jedes M?dchen und jeder Junge war erfreut.", "Jede Frau und jeder Junge war erfreut.", "Als Wissenschaft vom Erleben des Menschen einschlie?lich der biologischen Grundlagen ist die Psychologie interdisziplin?r.", "Als Wissenschaft vom Erleben des Menschen einschlie?lich der biologischen und sozialen Grundlagen ist die Psychologie interdisziplin?r.", "Als Wissenschaft vom Erleben des Menschen einschlie?lich der biologischen und neurowissenschaftlichen Grundlagen ist die Psychologie interdisziplin?r.", "Als Wissenschaft vom Erleben und Verhalten des Menschen einschlie?lich der biologischen bzw. sozialen Grundlagen ist die Psychologie interdisziplin?r.", "Alle vier Jahre ist dem Volksfest das Landwirtschaftliche Hauptfest angeschlossen.", "Aller Anfang ist schwer.", "Alle Dichtung ist zudem Darstellung von Handlungen.", "Allen drei Varianten ist gemeinsam, dass meistens nicht unter b?rgerlichem...", "Er sagte, dass es neun Uhr war.", "Auch den M?dchen war es untersagt, eine Schule zu besuchen.", "Das dazugeh?rende Modell der Zeichen-Wahrscheinlichkeiten ist unter Entropiekodierung beschrieben.", "Ein ?ber l?ngere Zeit entladener Akku ist zerst?rt.", "Der Fluss mit seinen Oberl?ufen R?o Paran? und R?o Uruguay ist der wichtigste Wasserweg.", "In den alten Mythen und Sagen war die Eiche ein heiliger Baum.", "In den alten Religionen, Mythen und Sagen war die Eiche ein heiliger Baum.", "Zehn Jahre ist es her, seit ich mit achtzehn nach Tokio kam.", "Bei den niedrigen Oberfl?chentemperaturen ist Wassereis hart wie Gestein.", "Bei den sehr niedrigen Oberfl?chentemperaturen ist Wassereis hart wie Gestein.", "Die ?lteste und bekannteste Ma?nahme ist die Einrichtung von Schutzgebieten.", "Die gr??te Dortmunder Gr?nanlage ist der Friedhof.", "Die gr??te Berliner Gr?nanlage ist der Friedhof.", "Die gr??te Bielefelder Gr?nanlage ist der Friedhof.", "Die Pariser Linie ist hier mit 2,2558 mm gerechnet.", "Die Frankfurter Innenstadt ist 7?km entfernt.", "Die Dortmunder Konzernzentrale ist ein markantes Geb?ude an der Bundesstra?e 1.", "Die D?sseldorfer Br?ckenfamilie war urspr?nglich ein Sammelbegriff.", "Die D?ssel ist ein rund 40 Kilometer langer Fluss.", "Die Berliner Mauer war w?hrend der Teilung Deutschlands die Grenze.", "F?r amtliche Dokumente und Formulare ist das anders.", "Wie viele Kilometer ist ihre Stadt von unserer entfernt?", "?ber laufende Sanierungsma?nahmen ist bislang nichts bekannt.", "In den letzten zwei Monate war ich flei?ig wie eine Biene.", "Durch Einsatz gr??erer Maschinen und bessere Kapazit?tsplanung ist die Zahl der Fl?ge gestiegen.", "Die hohe Zahl dieser relativ kleinen Verwaltungseinheiten ist immer wieder Gegenstand von Diskussionen.", "Teil der ausgestellten Best?nde ist auch die Bierdeckel-Sammlung.", "Teil der umfangreichen dort ausgestellten Best?nde ist auch die Bierdeckel-Sammlung.", "Teil der dort ausgestellten Best?nde ist auch die Bierdeckel-Sammlung.", "Der zweite Teil dieses Buches ist in England angesiedelt.", "Eine der am meisten verbreiteten Krankheiten ist die Diagnose", "Eine der verbreitetsten Krankheiten ist hier.", "Die Krankheit unserer heutigen St?dte und Siedlungen ist folgendes.", "Die darauffolgenden Jahre war er ...", "Die letzten zwei Monate war ich flei?ig wie eine Biene.", "Bei sehr guten Beobachtungsbedingungen ist zu erkennen, dass ...", "Die beste Rache f?r Undank und schlechte Manieren ist H?flichkeit.", "Ein Gramm Pfeffer war fr?her wertvoll.", "Die gr??te Stuttgarter Gr?nanlage ist der Friedhof.", "Mancher will Meister sein und ist kein Lehrjunge gewesen.", "Ellen war vom Schock ganz bleich.", "Nun gut, die Nacht ist sehr lang, oder?", "Der Morgen ist angebrochen, die lange Nacht ist vor?ber.", "Die stabilste und h?ufigste Oxidationsstufe ist dabei ?1.", "Man kann nicht eindeutig zuordnen, wer T?ter und wer Opfer war.", "Ich sch?tze, die Batterie ist leer.", "Der gr??te und sch?nste Tempel eines Menschen ist in ihm selbst.", "Begehe keine Dummheit zweimal, die Auswahl ist doch gro? genug!", "Seine gr??te und erfolgreichste Erfindung war die S?ule.", "Egal was du sagst, die Antwort ist Nein.", "... in der Geschichte des Museums, die Sammlung ist seit 2011 zug?nglich.", "Deren Bestimmung und Funktion ist allerdings nicht so klar.", "Sie hat eine Tochter, die Pianistin ist.", "Ja, die Milch ist sehr gut.", "Der als Befestigung gedachte ?stliche Teil der Burg ist weitgehend verfallen.", "Das Kopieren und Einf?gen ist sehr n?tzlich.", "Der letzte der vier gro?en Fl?sse ist die Kolyma.", "In christlichen, islamischen und j?dischen Traditionen ist das h?chste Ziel der meditativen Praxis.", "Der Autor der beiden Spielb?cher war Markus Heitz selbst.", "Der Autor der ersten beiden Spielb?cher war Markus Heitz selbst.", "Das Ziel der elf neuen Vorstandmitglieder ist klar definiert.", "Laut den meisten Quellen ist das Seitenverh?ltnis der Nationalflagge...", "Seine Novelle, die eigentlich eine Glosse ist, war toll.", "F?r in ?sterreich lebende Afrikaner und Afrikanerinnen ist dies nicht ?blich.", "Von urspr?nglich drei Almh?tten ist noch eine erhalten.", "Einer seiner bedeutendsten K?mpfe war gegen den sp?teren Weltmeister.", "Aufgrund stark schwankender Absatzm?rkte war die GEFA-Flug Mitte der 90er Jahre gezwungen, ...", "Der Abzug der Besatzungssoldaten und deren mittlerweile ans?ssigen Angeh?rigen der Besatzungsm?chte war vereinbart.", "Das B?ndnis zwischen der Sowjetunion und Kuba war f?r beide vorteilhaft.", "Knapp acht Monate ist die Niederlage nun her.", "Vier Monate ist die Niederlage nun her.", "Sie liebt Kunst und Kunst war auch kein Problem, denn er w?rde das Geld zur?ckkriegen.");
        for (String sentence : sentences) {
            assertGood(sentence);
        }
    }

    @Test
    public void testRuleWithIncorrectPluralVerb() throws IOException {
        List<String> sentences = // "Herr M?ller sind alt." -- M?ller has a plural reading
        // "Die heute bekannten Bonsai sind h?ufig im japanischen Stil gestaltet."  // plural: Bonsais (laut Duden) - sollte von AgreementRule gefunden werden
        Arrays.asList("Die Katze sind sch?n.", "Die Katze waren sch?n.", "Der Text sind gut.", "Das Auto sind schnell.", "Herr Schr?der sind alt.", "Julia und Karsten ist alt.", "Julia, Heike und Karsten ist alt.", "Herr Karsten Schr?der sind alt.");
        for (String sentence : sentences) {
            assertBad(sentence);
        }
    }

    @Test
    public void testRuleWithCorrectPluralVerb() throws IOException {
        List<String> sentences = Arrays.asList("Die Katzen sind sch?n.", "Frau Meier und Herr M?ller sind alt.", "Frau Julia Meier und Herr Karsten M?ller sind alt.", "Julia und Karsten sind alt.", "Julia, Heike und Karsten sind alt.", "Frau und Herr M?ller sind alt.", "Herr und Frau Schr?der sind alt.", "Herr Meier und Frau Schr?der sind alt.", "Die restlichen 86 Prozent sind in der Flasche.", "Die restlichen sechsundachtzig Prozent sind in der Flasche.", "Die restlichen 86 oder 87 Prozent sind in der Flasche.", "Die restlichen 86 % sind in der Flasche.", "Durch den schnellen Zerfall des Actiniums waren stets nur geringe Mengen verf?gbar.", "Soda und Anilin waren die ersten Produkte des Unternehmens.", "Bob und Tom sind Br?der.", "Letztes Jahr sind wir nach London gegangen.", "Trotz des Regens sind die Kinder in die Schule gegangen.", "Die Zielgruppe sind M?nner.", "M?nner sind die Zielgruppe.", "Die Zielgruppe sind meist junge Erwachsene.", "Die USA sind ein repr?sentativer demokratischer Staat.", "Wesentliche Eigenschaften der H?lle sind oben beschrieben.", "Wesentliche Eigenschaften der H?lle sind oben unter Quantenmechanische Atommodelle und Erkl?rung grundlegender Atomeigenschaften dargestellt.", "Er und seine Schwester sind eingeladen.", "Er und seine Schwester sind zur Party eingeladen.", "Sowohl er als auch seine Schwester sind zur Party eingeladen.", "Rekonstruktionen oder der Wiederaufbau sind wissenschaftlich sehr umstritten.", "Form und Materie eines Einzeldings sind aber nicht zwei verschiedene Objekte.", "Dieses Jahr sind die Birnen gro?.", "Es so umzugestalten, dass sie wie ein Spiel sind.", "Die Zielgruppe sind meist junge Erwachsene.", "Die Ursache eines Hauses sind so Ziegel und Holz.", "Vertreter dieses Ansatzes sind unter anderem Roth und Meyer.", "Sowohl sein Vater als auch seine Mutter sind tot.", "Einige der Inhaltsstoffe sind sch?dlich.", "Diese Woche sind wir schon einen gro?en Schritt weiter.", "Diese Woche sind sie hier.", "Vorsitzende des Vereins waren:", "Weder Gerechtigkeit noch Freiheit sind m?glich, wenn nur das Geld regiert.", "Ein typisches Beispiel sind Birkenpollenallergene.", "Eine weitere Variante sind die Miniatur-Wohnlandschaften.", "Eine Menge englischer W?rter sind aus dem Lateinischen abgeleitet.", "V?lkerrechtlich umstrittenes Territorium sind die Falklandinseln.", "Einige dieser ?lteren Synthesen sind wegen geringer Ausbeuten ...", "Einzelne Atome sind klein.", "Die Haare dieses Jungens sind schwarz.", "Die wichtigsten Mechanismen des Aminos?urenabbaus sind:", "Wasserl?sliche Bariumverbindungen sind giftig.", "Die Schweizer Trinkweise ist dabei die am wenigsten etablierte.", "Die Anordnung der vier Achsen ist damit identisch.", "Die Nauheimer Musiktage, die immer wieder ein kultureller H?hepunkt sind.", "R?umliche und zeitliche Abst?nde sowie die Tr?gheit sind vom Bewegungszustand abh?ngig.", "Solche Gewerbe sowie der Karosseriebau sind traditionell stark vertreten.", "Hundert Dollar sind doch gar nichts!", "Sowohl Tom als auch Maria waren ?berrascht.", "Robben, die die haupts?chliche Beute der Eisb?ren sind.", "Die Albatrosse sind eine Gruppe von Seev?geln", "Die Albatrosse sind eine Gruppe von gro?en Seev?geln", "Die Albatrosse sind eine Gruppe von gro?en bis sehr gro?en Seev?geln", "Vier Elemente, welche der Urstoff aller K?rper sind.", "Die Beziehungen zwischen Kanada und dem Iran sind seitdem abgebrochen.", "Die diplomatischen Beziehungen zwischen Kanada und dem Iran sind seitdem abgebrochen.", "Die letzten zehn Jahre seines Lebens war er erblindet.", "Die letzten zehn Jahre war er erblindet.", "... so dass Knochenbr?che und Platzwunden die Regel sind.", "Die Eigentumsverh?ltnisse an der Gesellschaft sind unver?ndert geblieben.", "Gegenstand der Definition sind f?r ihn die Urbilder.", "Mindestens zwanzig H?user sind abgebrannt.", "Sie hielten geheim, dass sie Geliebte waren.", "Einige waren versp?tet.", "Kommentare, Korrekturen und Kritik sind verboten.", "Kommentare, Korrekturen, Kritik sind verboten.", "Letztere sind wichtig, um die Datensicherheit zu garantieren.", "J?ngere sind oft davon ?berzeugt, im Recht zu sein.", "Verwandte sind selten mehr als Bekannte.", "Ursache waren die hohe Arbeitslosigkeit und die Wohnungsnot.", "Ursache waren unter anderem die hohe Arbeitslosigkeit und die Wohnungsnot.", "Er ahnt nicht, dass sie und sein Sohn ein Paar sind.", "Die Ursachen der vorliegenden Durchblutungsst?rung sind noch unbekannt.", "Der See und das Marschland sind ein Naturschutzgebiet", "Details, Dialoge, wie auch die Typologie der Charaktere sind frei erfunden.", "Die internen Ermittler und auch die Staatsanwaltschaft sind nun am Zug.", "Sie sind so erfolgreich, weil sie eine Einheit sind.");
        for (String sentence : sentences) {
            assertGood(sentence);
        }
    }

    @Test
    public void testRuleWithCorrectSingularAndPluralVerb() throws IOException {
        // Manchmal sind beide Varianten korrekt:
        // siehe http://www.canoo.net/services/OnlineGrammar/Wort/Verb/Numerus-Person/ProblemNum.html
        List<String> sentences = // ugs.
        // ugs.
        Arrays.asList("So mancher Mitarbeiter und manche F?hrungskraft ist im Urlaub.", "So mancher Mitarbeiter und manche F?hrungskraft sind im Urlaub.", "Jeder Sch?ler und jede Sch?lerin ist mal schlecht gelaunt.", "Jeder Sch?ler und jede Sch?lerin sind mal schlecht gelaunt.", "Kaum mehr als vier Prozent der Fl?che ist f?r landwirtschaftliche Nutzung geeignet.", "Kaum mehr als vier Prozent der Fl?che sind f?r landwirtschaftliche Nutzung geeignet.", "Kaum mehr als vier Millionen Euro des Haushalts ist verplant.", "Kaum mehr als vier Millionen Euro des Haushalts sind verplant.", "80 Cent ist nicht genug.", "80 Cent sind nicht genug.", "1,5 Pfund ist nicht genug.", "1,5 Pfund sind nicht genug.", "Hier ist sowohl Anhalten wie Parken verboten.", "Hier sind sowohl Anhalten wie Parken verboten.");
        for (String sentence : sentences) {
            assertGood(sentence);
        }
    }
}

