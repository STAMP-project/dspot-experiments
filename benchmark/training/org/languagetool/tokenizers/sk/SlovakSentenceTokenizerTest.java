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
package org.languagetool.tokenizers.sk;


import org.junit.Test;
import org.languagetool.Language;
import org.languagetool.TestTools;
import org.languagetool.language.Slovak;
import org.languagetool.tokenizers.SentenceTokenizer;


public class SlovakSentenceTokenizerTest {
    private final Language lang = new Slovak();

    // accept \n as paragraph:
    private final SentenceTokenizer stokenizer = new org.languagetool.tokenizers.SRXSentenceTokenizer(lang);

    // accept only \n\n as paragraph:
    private final SentenceTokenizer stokenizer2 = new org.languagetool.tokenizers.SRXSentenceTokenizer(lang);

    @Test
    public final void testTokenize() {
        testSplit("This is a sentence. ");
        // NOTE: sentences here need to end with a space character so they
        // have correct whitespace when appended:
        testSplit("Dies ist ein Satz.");
        testSplit("Dies ist ein Satz. ", "Noch einer.");
        testSplit("Ein Satz! ", "Noch einer.");
        testSplit("Ein Satz... ", "Noch einer.");
        testSplit("Unter http://www.test.de gibt es eine Website.");
        testSplit("Das ist,, also ob es bla.");
        testSplit("Das ist es.. ", "So geht es weiter.");
        testSplit("Das hier ist ein(!) Satz.");
        testSplit("Das hier ist ein(!!) Satz.");
        testSplit("Das hier ist ein(?) Satz.");
        testSplit("Das hier ist ein(???) Satz.");
        testSplit("Das hier ist ein(???) Satz.");
        // ganzer Satz kommt oder nicht:
        testSplit("Das war es: gar nichts.");
        testSplit("Das war es: Dies ist ein neuer Satz.");
        // incomplete sentences, need to work for on-thy-fly checking of texts:
        testSplit("Here's a");
        testSplit("Here's a sentence. ", "And here's one that's not comp");
        testSplit("?Prezydent jest niem?dry?. ", "Tak wysz?o.");
        testSplit("?Prezydent jest niem?dry?, powiedzia? premier");
        testSplit("Das Schreiben ist auf den 3.10. datiert.");
        testSplit("Das Schreiben ist auf den 31.1. datiert.");
        testSplit("Das Schreiben ist auf den 3.10.2000 datiert.");
        testSplit("Toto 2. vydanie bolo rozobran? za 1,5 roka.");
        testSplit("Festival Ba?ant Pohoda sl?vi svoje 10. v?ro?ie.");
        testSplit("Dlho odkladan? parlamentn? vo?by v Angole sa uskuto?nia 5. septembra.");
        testSplit("Das in Punkt 3.9.1 genannte Verhalten.");
        // From the abbreviation list:
        testSplit("Ak? s? skuto?n? pr??iny tzv. transforma?n?ch pr?znakov?");
        testSplit("Ak? pr?platky zamestnancovi (napr. za nad?as) stanovuje Z?konn?k pr?ce?");
        testSplit("Po?as nepr?tomnosti zastupuje MUDr. Marianna Krup?ov?.");
        testSplit("Starovek? Egypt vznikol okolo r. 3150 p.n.l. (tzn. 3150 pred Kr.). ", "A zanikol v r. 31 pr. Kr.");
        // from user bug reports:
        testSplit("Temperatura wody w systemie wynosi 30?C.", "W sk?ad obiegu otwartego wchodzi zbiornik i armatura.");
        testSplit("Zabudowano kolumny o d?ugo?ci 45 m. ", "Woda z uj?cia jest dostarczana do zak?adu.");
        // two-letter initials:
        testSplit("Najlepszym polskim re?yserem by? St. R??ewicz. ", "Chodzi o brata wielkiego poety.");
        testSplit("Nore M. hroz? za podvod 10 a? 15 rokov.");
        testSplit("To jest zmienna A.", "Za? to jest zmienna B.");
        // Numbers with dots.
        testSplit("Mam w magazynie dwie skrzynie LMD20. ", "Jestem ?o?nierzem i wiem, jak mo?na ich u?y?");
        // ellipsis
        testSplit("Rytmem tej wiecznie przemijaj?cej ?wiatowej egzystencji [?] rytmem mesja?skiej natury jest szcz??cie.");
        // Tests taken from LanguageTool's SentenceSplitterTest.py:
        testSplit("This is a sentence. ");
        testSplit("This is a sentence. ", "And this is another one.");
        testSplit("This is a sentence.", "Isn't it?", "Yes, it is.");
        testSplit("Don't split strings like U. S. A. either.");
        testSplit("Don't split strings like U.S.A. either.");
        testSplit("Don't split... ", "Well you know. ", "Here comes more text.");
        testSplit("Don't split... well you know. ", "Here comes more text.");
        testSplit("The \".\" should not be a delimiter in quotes.");
        testSplit("\"Here he comes!\" she said.");
        testSplit("\"Here he comes!\", she said.");
        testSplit("\"Here he comes.\" ", "But this is another sentence.");
        testSplit("\"Here he comes!\". ", "That's what he said.");
        testSplit("The sentence ends here. ", "(Another sentence.)");
        // known to fail:
        // testSplit(new String[]{"He won't. ", "Really."});
        testSplit("He won't go. ", "Really.");
        testSplit("He won't say no.", "Not really.");
        testSplit("This is it: a test.");
        // one/two returns = paragraph = new sentence:
        TestTools.testSplit(new String[]{ "He won\'t\n\n", "Really." }, stokenizer2);
        TestTools.testSplit(new String[]{ "He won\'t\n", "Really." }, stokenizer);
        TestTools.testSplit(new String[]{ "He won\'t\n\n", "Really." }, stokenizer2);
        TestTools.testSplit(new String[]{ "He won\'t\nReally." }, stokenizer2);
        // Missing space after sentence end:
        testSplit("James is from the Ireland!", "He lives in Spain now.");
    }
}

