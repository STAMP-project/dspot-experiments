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
package org.languagetool.chunking;


import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.language.GermanyGerman;


public class GermanChunkerTest {
    private final JLanguageTool lt = new JLanguageTool(new GermanyGerman());

    private final GermanChunker chunker = new GermanChunker();

    @Test
    public void testChunking() throws Exception {
        assertFullChunks("Ein/B Haus/I");
        assertFullChunks("Ein/NPP Hund/NPP und/NPP eine/NPP Katze/NPP stehen dort");
        assertFullChunks("Es war die/NPS gr??te/NPS und/NPS erfolgreichste/NPS Erfindung/NPS");
        assertFullChunks("Ger?te/B , deren/NPS Bestimmung/NPS und/NPS Funktion/NPS unklar sind.");
        assertFullChunks("Julia/NPP und/NPP Karsten/NPP sind alt");
        assertFullChunks("Es ist die/NPS ?lteste/NPS und/NPS bekannteste/NPS Ma?nahme/NPS");
        assertFullChunks("Das ist eine/NPS Masseeinheit/NPS und/NPS keine/NPS Gewichtseinheit/NPS");
        assertFullChunks("Sie f?hrt nur eins/NPS ihrer/NPS drei/NPS Autos/NPS");
        assertFullChunks("Da sind er/NPP und/NPP seine/NPP Schwester/NPP");
        // assertFullChunks("Sowohl/NPP sein/NPP Vater/NPP als/NPP auch/NPP seine/NPP Mutter/NPP sind da");  //?
        // assertFullChunks("Sowohl/NPP Tom/NPP als/NPP auch/NPP Maria/NPP sind da");
        // assertFullChunks("Sowohl/NPP er/NPP als/NPP auch/NPP seine/NPP Schwester/NPP sind da");
        assertFullChunks("Rekonstruktionen/NPP oder/NPP der/NPP Wiederaufbau/NPP sind das/NPS Ziel/NPS");
        assertFullChunks("Isolation/NPP und/NPP ihre/NPP ?berwindung/NPP ist das/NPS Thema/NPS");
        assertFullChunks("Es gibt weder/NPP Gerechtigkeit/NPP noch/NPP Freiheit/NPP");
        assertFullChunks("Da sitzen drei/NPP Katzen/NPP");
        assertFullChunks("Der/NPS von/NPS der/NPS Regierung/NPS gepr?fte/NPS Hund/NPS ist gr?n");
        assertFullChunks("Herr/NPP und/NPP Frau/NPP Schr?der/NPP sind betrunken");
        // assertFullChunks("Die/NPS hohe/NPS Zahl/NPS dieser/NPS relativ/NPS kleinen/NPS Verwaltungseinheiten/NPS ist beeindruckend");   //?
        // assertFullChunks("Das ist eine/NPS der/NPS am/NPS meisten/NPS verbreiteten/NPS Krankheiten/NPS");   //?
        assertFullChunks("Das sind 37/NPS Prozent/NPS");
        assertFullChunks("Das sind 37/NPP Prozent/NPP");
        assertFullChunks("Er will die/NPP Arbeitspl?tze/NPP so umgestalten , dass/NPP sie/NPP wie/NPP ein/NPP Spiel/NPP sind.");
        assertFullChunks("So dass Knochenbr?che/NPP und/NPP Platzwunden/NPP die/NPP Regel/NPP sind");
        assertFullChunks("Eine/NPS Veranstaltung/NPS ,/NPS die/NPS immer/NPS wieder/NPS ein/NPS kultureller/NPS H?hepunkt/NPS war");
        assertFullChunks("Und die/NPS ?ltere/NPS der/NPS beiden/NPS T?chter/NPS ist 20.");
        assertFullChunks("Der/NPS Synthese/NPS organischer/NPS Verbindungen/NPS steht nichts im/PP Weg/NPS");
        assertFullChunks("Aber/B die/NPP Kenntnisse/NPP der/NPP Sprache/NPP sind n?tig.");// actually "Aber" should not be tagged

        assertFullChunks("Dort steht die/NPS Pyramide/NPS des/NPS Friedens/NPS und/NPS der/NPS Eintracht/NPS");
        assertFullChunks("Und Teil/B der/NPS dort/NPS ausgestellten/NPS Best?nde/NPS wurde privat finanziert.");
        assertFullChunks("Autor/NPS der/NPS ersten/NPS beiden/NPS B?cher/NPS ist Stephen King/NPS");
        assertFullChunks("Autor/NPS der/NPS beiden/NPS B?cher/NPS ist Stephen King/NPS");
        assertFullChunks("Teil/NPS der/NPS umfangreichen/NPS dort/NPS ausgestellten/NPS Best?nde/NPS stammt von privat");
        assertFullChunks("Ein/NPS Teil/NPS der/NPS umfangreichen/NPS dort/NPS ausgestellten/NPS Best?nde/NPS stammt von privat");
        assertFullChunks("Die/NPS Krankheit/NPS unserer/NPS heutigen/NPS St?dte/NPS und/NPS Siedlungen/NPS ist der/NPS Verkehr/NPS");
        assertFullChunks("Der/B Nil/I ist der/NPS letzte/NPS der/NPS vier/NPS gro?en/NPS Fl?sse/NPS");
        assertFullChunks("Der/NPS letzte/NPS der/NPS vier/NPS gro?en/NPS Fl?sse/NPS ist der/B Nil/I");
        assertFullChunks("Sie kennt eine/NPP Menge/NPP englischer/NPP W?rter/NPP");
        assertFullChunks("Eine/NPP Menge/NPP englischer/NPP W?rter/NPP sind aus/PP dem/NPS Lateinischen/NPS abgeleitet.");
        assertFullChunks("Laut/PP den/PP meisten/PP Quellen/PP ist er 35 Jahre/B alt.");
        assertFullChunks("Bei/PP den/PP sehr/PP niedrigen/PP Oberfl?chentemperaturen/PP verbrennt nichts");
        assertFullChunks("In/PP den/PP alten/PP Religionen/PP ,/PP Mythen/PP und/PP Sagen/PP tauchen Geister/B auf.");
        assertFullChunks("Die/B Stra?e/I ist wichtig f?r/PP die/PP Stadtteile/PP und/PP selbst?ndigen/PP Ortsteile/PP");
        assertFullChunks("Es herrscht gute/NPS Laune/NPS in/PP chemischen/PP Komplexverbindungen/PP");
        assertFullChunks("Funktionen/NPP des/NPP K?rpers/NPP einschlie?lich/PP der/PP biologischen/PP und/PP sozialen/PP Grundlagen/PP");
        assertFullChunks("Das/NPS Dokument/NPS umfasst das f?r/PP ?rzte/PP und/PP ?rztinnen/PP festgestellte/PP Risikoprofil/PP");
        assertFullChunks("In/PP den/PP darauf/PP folgenden/PP Wochen/PP ging es los.");
        assertFullChunks("In/PP nur/PP zwei/PP Wochen/PP geht es los.");
        assertFullChunks("Programme/B , in/PP deren/PP deutschen/PP Installationen/PP nichts funktioniert.");
        assertFullChunks("Nach/PP sachlichen/PP und/PP milit?rischen/PP Kriterien/PP war das unn?tig.");
        assertFullChunks("Mit/PP ?ber/PP 1000/PP Handschriften/PP ist es die/NPS gr??te/NPS Sammlung/NPS");
        assertFullChunks("Es gab Beschwerden/NPP ?ber/PP laufende/PP Sanierungsma?nahmen/PP");
        assertFullChunks("Gesteigerte/B Effizienz/I durch/PP Einsatz/PP gr??erer/PP Maschinen/PP und/PP bessere/PP Kapazit?tsplanung/PP");
        assertFullChunks("Bei/PP sehr/PP guten/PP Beobachtungsbedingungen/PP bin ich dabei");
        assertFullChunks("Die/NPP Beziehungen/NPP zwischen/NPP Kanada/NPP und/NPP dem/NPP Iran/NPP sind unterk?hlt");
        assertFullChunks("Die/PP darauffolgenden/PP Jahre/PP war es kalt");
        assertFullChunks("Die/NPP darauffolgenden/NPP Jahre/NPP waren kalt");
        assertFullChunks("Die/PP letzten/PP zwei/PP Monate/PP war es kalt");
        // assertFullChunks("Die/NPP letzten/NPP zwei/NPP Monate/NPP waren kalt");
        assertFullChunks("Letztes/PP Jahr/PP war kalt");
        assertFullChunks("Letztes/PP Jahr/PP war es kalt");
        assertFullChunks("Es sind Atome/NPP ,/NPP welche/NPP der/NPP Urstoff/NPP aller/NPP K?rper/NPP sind");
        assertFullChunks("Kommentare/NPP ,/NPP Korrekturen/NPP ,/NPP Kritik/NPP bitte nach /dev/null");
        assertFullChunks("Einer/NPS der/NPS beiden/NPS H?fe/NPS war sch?n");
    }

    // B = begin, will be expanded to B-NP, I = inner, will be expanded to I-NP
    @Test
    public void testOpenNLPLikeChunking() throws Exception {
        // GermanChunker.setDebug(true);
        assertBasicChunks("Ein/B Haus/I");
        assertBasicChunks("Da steht ein/B Haus/I");
        assertBasicChunks("Da steht ein/B sch?nes/I Haus/I");
        assertBasicChunks("Da steht ein/B sch?nes/I gro?es/I Haus/I");
        assertBasicChunks("Da steht ein/B sehr/I gro?es/I Haus/I");
        assertBasicChunks("Da steht ein/B sehr/I sch?nes/I gro?es/I Haus/I");
        assertBasicChunks("Da steht ein/B sehr/I gro?es/I Haus/I mit Dach/B");
        assertBasicChunks("Da steht ein/B sehr/I gro?es/I Haus/I mit einem/B blauen/I Dach/I");
        assertBasicChunks("Eine/B leckere/I Lasagne/I");
        assertBasicChunks("Herr/B Meier/I isst eine/B leckere/I Lasagne/I");
        assertBasicChunks("Herr/B Schr?dinger/I isst einen/B Kuchen/I");
        assertBasicChunks("Herr/B Schr?dinger/I isst einen/B leckeren/I Kuchen/I");
        assertBasicChunks("Herr/B Karl/I Meier/I isst eine/B leckere/I Lasagne/I");
        assertBasicChunks("Herr/B Finn/I Westerwalbesloh/I isst eine/B leckere/I Lasagne/I");
        assertBasicChunks("Unsere/B sch?ne/I Heimat/I geht den/B Bach/I runter");
        assertBasicChunks("Er meint das/B Haus/I am gr?nen/B Hang/I");
        assertBasicChunks("Ich/B muss dem/B Hund/I Futter/I geben");// TODO: see next line for how it should be (but: 'Pariser Innenstadt' should be one NP)

        // assertChunks("Ich muss dem/B Hund/I Futter/B geben");
        assertBasicChunks("Das/B Wasser/I , das die/B W?rme/I ?bertr?gt");
        assertBasicChunks("Er mag das/B Wasser/I , das/B Meer/I und die/B Luft/I");
        assertBasicChunks("Schon mehr als zwanzig/B Prozent/I der/B Arbeiter/I sind im Streik/B");
        assertBasicChunks("Das/B neue/I Gesetz/I betrifft 1000 B?rger/B");// '1000' sollte evtl. mit in die NP...

        assertBasicChunks("In zwei/B Wochen/I ist Weihnachten/B");
        assertBasicChunks("Eines ihrer/B drei/I Autos/I ist blau");
    }

    @Test
    public void testTemp() throws Exception {
        // GermanChunker.setDebug(true);
        // TODO:
        // assertFullChunks("Seine Novelle, die eigentlich eine Glosse ist, war so.");
        // assertChunks("Das/B Wasser/I , das W?rme/B ?bertr?gt");  // keine Kongruenz bzgl. Genus -> keine NP
        // assertChunks("Das/B Wasser/I , das viel/B W?rme/I ?bertr?gt");  // keine Kongruenz bzgl. Genus -> keine NP
        // assertChunks("Das/B Wasser/I , das wohlige/B W?rme/I ?bertr?gt");  // keine Kongruenz bzgl. Genus -> keine NP
        // das bereits erreichte Ergebnis
        // die privat Krankenversicherten
        // die auf ihre Tochter stolze Frau
        // jemand Sch?nes
        // fast eine Millionen Studenten
        // nahezu alle Studenten
        // rund 40 G?ste
        // ?ber 20 Besucher
        // der flei?ige Dr. Christoph Schmidt
        // der Hund von Peter
        // Peters Hund
        // ein Mann mit einem Radio in der Nase
        // die Hoffnung auf ein baldiges Ende
    }
}

