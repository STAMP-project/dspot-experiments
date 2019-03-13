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
package org.languagetool.tokenizers.pl;


import org.junit.Test;
import org.languagetool.language.Polish;
import org.languagetool.tokenizers.SentenceTokenizer;


public class PolishSentenceTokenizerTest {
    private final SentenceTokenizer stokenizer = new org.languagetool.tokenizers.SRXSentenceTokenizer(new Polish());

    @Test
    public final void testTokenize() {
        testSplit("To si? wydarzy?o 3.10.2000 i mam na to dowody.");
        testSplit("To by?o 13.12 - nikt nie zapomni tego przem?wienia.");
        testSplit("Heute ist der 13.12.2004.");
        testSplit("To jest np. ten debil spod jedynki.");
        testSplit("To jest 1. wydanie.");
        testSplit("Dzi? jest 13. rocznica powstania w?chockiego.");
        testSplit("Das in Punkt 3.9.1 genannte Verhalten.");
        testSplit("To jest tzw. premier.");
        testSplit("Jarek kupi? sobie kurteczk?, tj. str?j Marka.");
        testSplit("?Prezydent jest niem?dry?. ", "Tak wysz?o.");
        testSplit("?Prezydent jest niem?dry?, powiedzia? premier");
        // from user bug reports:
        testSplit("Temperatura wody w systemie wynosi 30?C.", "W sk?ad obiegu otwartego wchodzi zbiornik i armatura.");
        testSplit("Zabudowano kolumny o d?ugo?ci 45 m. ", "Woda z uj?cia jest dostarczana do zak?adu.");
        // two-letter initials:
        testSplit("Najlepszym polskim re?yserem by? St. R??ewicz. ", "Chodzi o brata wielkiego poety.");
        // From the abbreviation list:
        testSplit("Ks. Jankowski jest prof. teologii.");
        testSplit("To wydarzy?o si? w 1939 r.", "To by? burzliwy rok.");
        testSplit("Prezydent jest popierany przez 20 proc. spo?ecze?stwa.");
        testSplit("Moje wyst?pienie ma na celu zmobilizowanie zarz?du partii do dzia?a?, kt?re umo?liwi? uzyskanie 40 proc.", "Nie widz? dzi? na scenie politycznej formacji, kt?ra lepiej by ??czy?a r??ne pogl?dy");
        testSplit("To jest zmienna A.", "Za? to jest zmienna B.");
        // SKROTY_BEZ_KROPKI in ENDABREVLIST
        testSplit("Mam ju? 20 mln.", "To powinno mi wystarczy?");
        testSplit("Mam ju? 20 mln. burak?w.");
        // ellipsis
        testSplit("Rytmem tej wiecznie przemijaj?cej ?wiatowej egzystencji [?] rytmem mesja?skiej natury jest szcz??cie.");
        // sic!
        testSplit("W gazecie napisali, ?e pasy (sic!) pogryz?y cz?owieka.");
        // Numbers with dots.
        testSplit("Mam w magazynie dwie skrzynie LMD20. ", "Jestem ?o?nierzem i wiem, jak mo?na ich u?y?");
    }
}

