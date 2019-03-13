/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2006 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.tokenizers.ro;


import org.junit.Test;
import org.languagetool.Language;
import org.languagetool.TestTools;
import org.languagetool.language.Romanian;
import org.languagetool.tokenizers.SentenceTokenizer;


/**
 *
 *
 * @author Ionu? P?duraru
 */
public class RomanianSentenceTokenizerTest {
    Language lang = new Romanian();

    // accept \n as paragraph:
    private final SentenceTokenizer stokenizer = new org.languagetool.tokenizers.SRXSentenceTokenizer(lang);

    // accept only \n\n as paragraph:
    private final SentenceTokenizer stokenizer2 = new org.languagetool.tokenizers.SRXSentenceTokenizer(lang);

    @Test
    public final void testTokenize() {
        testSplit("Aceasta este o propozitie fara diacritice. ");
        testSplit("Aceasta este o fraza fara diacritice. ", "Propozitia a doua, tot fara diacritice. ");
        testSplit("Aceasta este o propozi?ie cu diacritice. ");
        testSplit("Aceasta este o propozi?ie cu diacritice. ", "Propozi?ia a doua, cu diacritice. ");
        testSplit("O propozi?ie! ", "?i ?nc? o propozi?ie. ");
        testSplit("O propozi?ie... ", "?i ?nc? o propozi?ie. ");
        testSplit("La adresa http://www.archeus.ro g?si?i resurse lingvistice. ");
        testSplit("Data de 10.02.2009 nu trebuie s? fie separator de propozi?ii. ");
        testSplit("Ast?zi suntem ?n data de 07.05.2007. ");
        testSplit("Ast?zi suntem ?n data de 07/05/2007. ");
        testSplit("La anum?rul (1) avem pu?ine informa?ii. ");
        testSplit("To jest 1. wydanie.");
        testSplit("La anum?rul 1. avem pu?ine informa?ii. ");
        testSplit("La anum?rul 13. avem pu?ine informa?ii. ");
        testSplit("La anum?rul 1.3.3 avem pu?ine informa?ii. ");
        testSplit("O singur? propozi?ie... ");
        testSplit("Colegii mei s-au dus... ");
        testSplit("O singur? propozi?ie!!! ");
        testSplit("O singur? propozi?ie??? ");
        testSplit("Propozi?ii: una ?i alta. ");
        testSplit("Domnu' a plecat. ");
        testSplit("Profu' de istorie tre' s? predea lec?ia. ");
        testSplit("Sal'tare! ");
        testSplit("'Nea?a! ");
        testSplit("Deodat'apare un urs. ");
        // accente
        testSplit("A f?cut dou? c?pii. ");
        testSplit("Ionel ad?n? acum ceea ce Maria adun? ?nainte s? vin eu. ");
        // incomplete sentences, need to work for on-thy-fly checking of texts:
        testSplit("Domnu' a plecat");
        testSplit("Domnu' a plecat. ", "El nu a plecat");
        testSplit(("Se pot ?nt?lni ?i abrevieri precum S.U.A. " + "sau B.C.R. ?ntr-o singur? propozi?ie."));
        testSplit("Se pot ?nt?lni ?i abrevieri precum S.U.A. sau B.C.R. ", "Aici sunt dou? propozi?ii.");
        testSplit("Acela?i lucru aici... ", "Aici sunt dou? propozi?ii.");
        testSplit("Acela?i lucru aici... dar cu o singur? propozi?ie.");
        testSplit("?O propozi?ie!? ", "O alta.");
        testSplit("?O propozi?ie!!!? ", "O alta.");
        testSplit("?O propozi?ie?? ", "O alta.");
        testSplit("?O propozi?ie?!?? ", "O alta.");
        testSplit("?O propozi?ie!? ", "O alta.");
        testSplit("?O propozi?ie!!!? ", "O alta.");
        testSplit("?O propozi?ie?? ", "O alta.");
        testSplit("?O propozi?ie???? ", "O alta.");
        testSplit("?O propozi?ie?!?? ", "O alta.");
        testSplit("O prim? propozi?ie. ", "(O alta.)");
        testSplit("A venit domnu' Vasile. ");
        testSplit("A venit domnu' acela. ");
        // one/two returns = paragraph = new sentence:
        TestTools.testSplit(new String[]{ "A venit domnul\n\n", "Vasile." }, stokenizer2);
        TestTools.testSplit(new String[]{ "A venit domnul\n", "Vasile." }, stokenizer);
        TestTools.testSplit(new String[]{ "A venit domnu\'\n\n", "Vasile." }, stokenizer2);
        TestTools.testSplit(new String[]{ "A venit domnu\'\n", "Vasile." }, stokenizer);
        // Missing space after sentence end:
        testSplit("El este din Rom?nia!", "Acum e plecat cu afaceri.");
        testSplit("Temperatura este de 30?C.", "Este destul de cald.");
        testSplit("A alergat 50 m. ", "Deja a obosit.");
        // From the abbreviation list:
        testSplit("Pentru dvs. vom face o excep?ie.");
        testSplit("Pt. dumneavoastr? vom face o excep?ie.");
        testSplit("Pt. dvs. vom face o excep?ie.");
        // din punct de vedere
        testSplit("A expus problema d.p.d.v. artistic.");
        testSplit("A expus problema dpdv. artistic.");
        // ?i a?a mai departe.
        testSplit("Are mere, pere, ?amd. dar nu are alune.");
        testSplit("Are mere, pere, ?.a.m.d. dar nu are alune.");
        testSplit("Are mere, pere, ?.a.m.d. ", "?n schimb, nu are alune.");
        // ?i celelalte
        testSplit("Are mere, pere, ?.c.l. dar nu are alune.");
        testSplit("Are mere, pere, ?.c.l. ", "Nu are alune.");
        // etc. et cetera
        testSplit("Are mere, pere, etc. dar nu are alune.");
        testSplit("Are mere, pere, etc. ", "Nu are alune.");
        // ?.a. - ?i altele
        testSplit("Are mere, pere, ?.a. dar nu are alune.");
        // pag, leg, art
        testSplit("Lec?ia ?ncepe la pag. urm?toare ?i are trei pagini.");
        testSplit("Lec?ia ?ncepe la pag. 20 ?i are trei pagini.");
        testSplit("A ac?ionat ?n conformitate cu lg. 144, art. 33.");
        testSplit("A ac?ionat ?n conformitate cu leg. 144, art. 33.");
        testSplit("A ac?ionat ?n conformitate cu legea nr. 11.");
        testSplit("Lupta a avut loc ?n anul 2000 ?.H. ?i a durat trei ani.");
        // lunile anului, abreviate
        testSplit("Discu?ia a avut loc pe data de dou?zeci aug. ?i a durat dou? ore.");
        testSplit("Discu?ia a avut loc pe data de dou?zeci ian. ?i a durat dou? ore.");
        testSplit("Discu?ia a avut loc pe data de dou?zeci feb. ?i a durat dou? ore.");
        testSplit("Discu?ia a avut loc pe data de dou?zeci ian.", "A durat dou? ore.");
        // M.Ap.N. - Ministerul Ap?r?rii Nationale
        // there are 2 rules for this in segment.srx. Can this be done with only one rule?
        testSplit("A fost ?i la M.Ap.N. dar nu l-au primit. ");
        testSplit("A fost ?i la M.Ap.N. ", "Nu l-au primit. ");
        // sic!
        testSplit("Apo' da' tulai (sic!) c? mult mai e de mers.");
        testSplit("Apo' da' tulai(sic!) c? mult mai e de mers.");
        // [?]
        testSplit("Aici este o fraz? [?] mult prescurtat?.");
        testSplit("Aici este o fraz? [...] mult prescurtat?.");
    }
}

