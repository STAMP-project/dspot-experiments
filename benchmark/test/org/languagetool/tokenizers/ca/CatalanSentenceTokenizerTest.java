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
package org.languagetool.tokenizers.ca;


import org.junit.Test;
import org.languagetool.language.Catalan;
import org.languagetool.tokenizers.SentenceTokenizer;


public class CatalanSentenceTokenizerTest {
    private final SentenceTokenizer stokenizer = new org.languagetool.tokenizers.SRXSentenceTokenizer(new Catalan());

    @Test
    public final void testTokenize() {
        // Simple sentences
        testSplit("Aix? ?s una frase. ", "Aix? ?s una altra frase.");
        testSplit("Aquesta ?s l'egua. ", "Aquell ?s el cavall.");
        testSplit("Aquesta ?s l'egua? ", "Aquell ?s el cavall.");
        testSplit("Vols col?laborar? ", "S?, i tant.");
        testSplit("Com vas d'il?lusi?? ", "B?, b?.");
        testSplit("Com vas d?il?lusi?? ", "B?, b?.");
        testSplit("?s d?abans-d?ahir? ", "B?, b?.");
        testSplit("?s d?abans-d?ahir! ", "B?, b?.");
        testSplit("Qu? vols dir? ", "Ja ho tinc!");
        testSplit("Qu?? ", "Ja ho tinc!");
        testSplit("Ah! ", "Ja ho tinc!");
        testSplit("Ja ho tinc! ", "Qu? vols dir?");
        testSplit("Us explicar? com va anar: ", "?La Maria va engegar el cotxe");
        testSplit("diu que va dir. ", "A mi em feia estrany.");
        testSplit("S?n del s. III dC. ", "S?n importants les pintures.");
        // N., t.
        testSplit("V?s-te?n. ", "A mi em feia estrany.");
        testSplit("V?s-te'n. ", "A mi em feia estrany.");
        testSplit("V?S-TE'N. ", "A mi em feia estrany.");
        testSplit("Canten. ", "A mi em feia estrany.");
        testSplit("Despr?n. ", "A mi em feia estrany.");
        testSplit("(n. 3).");
        testSplit(" n. 3");
        testSplit("n. 3");
        testSplit("(\"n. 3\".");
        testSplit("En el t. 2 de la col?lecci?");
        testSplit("Llan?a't. ", "Fes-ho.");
        // Initials
        testSplit("A l'atenci? d'A. Comes.");
        testSplit("A l'atenci? d'?. Comes.");
        testSplit("N?m. operaci? 220130000138.");
        // Ellipsis
        testSplit("el vi no \u00e9s gens propi de monjos, amb tot...\" vetllant, aix\u00f2 s\u00ed");
        testSplit("Desenganyeu-vos? ", "L??nic problema seri?s de l'home en aquest m?n ?s el de subsistir.");
        testSplit("?s clar? traduir ?s una feina endimoniada");
        testSplit("?El cord? del frare?? surt d'una manera desguitarrada");
        testSplit("convidar el seu heroi ?del ram que sigui?? a prendre caf?.");
        // Abbreviations
        testSplit("No Mr. Spock sin? un altre.");
        testSplit("Vegeu el cap. 24 del llibre.");
        testSplit("Vegeu el cap. IX del llibre.");
        testSplit("Viu al n?m. 24 del carrer de l'Hort.");
        testSplit("El Dr. Joan no vindr?.");
        testSplit("Distingit Sr. Joan,");
        testSplit("Molt Hble. Sr. President");
        testSplit("de Sant Nicolau (del s. XII; cor g?tic del s. XIV) i de Sant ");
        testSplit("Va ser el 5?. classificat.");
        testSplit("Va ser el 5?. ", "I l'altre el 4t.");
        testSplit("Art. 2.1: S?n obligats els...");
        testSplit("Arriba fins a les pp. 50-52.");
        testSplit("Arriba fins a les pp. XI-XII.");
        testSplit("i no ho vol. ", "Malgrat que ?s aix?.");
        testSplit("i ?s del vol. 3 de la col?lecci?");
        // Exception to abbreviations
        testSplit("Ell ?s el n?mero u. ", "Jo el dos.");
        testSplit("T? un trau al cap. ", "Cal portar-lo a l'hospital.");
        testSplit("Aix? passa en el PP. ", "Per?, per altra banda,");
        testSplit("Ceba, all, carabassa, etc. ", "En comprem a la fruiteria.");
        // Units
        testSplit("1 500 m/s. ", "Neix a");
        testSplit("S?n d'1 g. ", "Han estat condicionades.");
        testSplit("S?n d'1 m. ", "Han estat condicionades.");
        testSplit("Hi vivien 50 h. ", "Despr?s el poble va cr?ixer.");
        testSplit("L'acte ser? a les 15.30 h. de la vesprada.");
        // Error: missing space. It is not split in order to trigger other errors.
        testSplit("s'hi enfront? quan G.Oueddei n'esdevingu? l?der");
        testSplit("el jesu?ta alemany J.E. Nithard");
    }
}

