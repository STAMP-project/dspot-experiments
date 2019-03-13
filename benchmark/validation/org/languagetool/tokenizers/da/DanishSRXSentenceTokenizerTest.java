/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2010 Esben Aaberg
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
package org.languagetool.tokenizers.da;


import org.junit.Test;
import org.languagetool.language.Danish;
import org.languagetool.tokenizers.SRXSentenceTokenizer;


/**
 *
 *
 * @author Esben Aaberg
 */
public class DanishSRXSentenceTokenizerTest {
    private final SRXSentenceTokenizer stokenizer = new SRXSentenceTokenizer(new Danish());

    @Test
    public void testTokenize() {
        // NOTE: sentences here need to end with a space character so they
        // have correct whitespace when appended:
        testSplit("Dette er en s?tning.");
        testSplit("Dette er en s?tning. ", "Her er den n?ste.");
        testSplit("En s?tning! ", "Yderlige en.");
        testSplit("En s?tning... ", "Yderlige en.");
        testSplit("P? hjemmesiden http://www.stavekontrolden.dk bygger vi stavekontrollen.");
        testSplit("Den 31.12. g?r ikke!");
        testSplit("Den 3.12.2011 g?r ikke!");
        testSplit("I det 18. og tidlige 19. ?rhundrede hentede amerikansk kunst det meste af sin inspiration fra Europa.");
        testSplit("Hendes Majest?t Dronning Margrethe II (Margrethe Alexandrine ??rhildur Ingrid, Danmarks dronning) (f?dt 16. april 1940 p? Amalienborg Slot) er siden 14. januar 1972 Danmarks regent.");
        testSplit("Hun har residensbolig i Christian IX's Pal? p? Amalienborg Slot.");
        testSplit("Tronf?lgeren ledte herefter statsr?dsm?derne under Kong Frederik 9.'s frav?r.");
        testSplit("Marie Hvidt, Frederik IV - En letsindig alvorsmand, Gads Forlag, 2004.");
        testSplit("Da vi f?rste gang bes?gte Restaurant Chr. IV, var vi de eneste g?ster.");
        testSplit("I dag er det den 25.12.2010.");
        testSplit("I dag er det d. 25.12.2010.");
        testSplit("I dag er den 13. december.");
        testSplit("Arrangementet starter ca. 17:30 i dag.");
        testSplit("Arrangementet starter ca. 17:30.");
        testSplit("Det er n?vnt i punkt 3.6.4 Rygbelastende helkropsvibrationer.");
        testSplit("Rent praktisk er det ogs? lettest lige at m?des, s? der kan udveksles n?gler og brugsanvisninger etc.");
        testSplit("Andre partier incl. borgerlige partier har deres s?rlige problemer: nogle samarbejder med apartheidstyret i Sydafrika, med NATO-landet Tyrkiet etc., men det skal s? sandelig ikke begrunde en SF-offensiv for et samarbejde med et parti.");
        testSplit("Hvad nu,, den bliver ogs?.");
        testSplit("Det her er det.. ", "Og her forts?tter det.");
        testSplit("Dette er en(!) s?tning.");
        testSplit("Dette er en(!!) s?tning.");
        testSplit("Dette er en(?) s?tning.");
        testSplit("Dette er en(??) s?tning.");
        testSplit("Dette er en(???) s?tning.");
        testSplit("Milit?r v?rnepligt blev indf?rt (traktaten kr?vede, at den tyske h?r ikke oversteg 100.000 mand).");
        testSplit("Siden illustrerede hun \"Historierne om Regnar Lodbrog\" 1979 og \"Bjarkem\u00e5l\" 1982 samt Poul \u00d8rums \"Komedie i Florens\" 1990.");
    }
}

