/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2012 Marcin Mi?kowski (http://www.languagetool.org)
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
package org.languagetool.rules.ca;


import java.io.IOException;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.Catalan;
import org.languagetool.rules.RuleMatch;


public class MorfologikCatalanSpellerRuleTest {
    @Test
    public void testMorfologikSpeller() throws IOException {
        MorfologikCatalanSpellerRule rule = new MorfologikCatalanSpellerRule(TestTools.getMessages("ca"), new Catalan(), null, Collections.emptyList());
        RuleMatch[] matches;
        JLanguageTool langTool = new JLanguageTool(new Catalan());
        // prefixes and suffixes.
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("S'autodefineixin com a populars.")).length);
        // assertEquals(0, rule.match(langTool.getAnalyzedSentence("Redibuixen el futur.")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("L'exdirigent del partit.")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("S'autoprenia.")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("S'autocanta.")).length);
        // word not well-formed with prefix
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("S'autopren.")).length);
        // correct sentences:
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Abacallanada")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Abatre-les-en")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("All? que m?s l'interessa.")).length);
        // checks that "WORDCHARS ?-'" is added to Hunspell .aff file
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Porta'n quatre al col?legi.")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Has de portar-me'n moltes.")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence(",")).length);
        // Spellcheck dictionary contains Valencian and general accentuation
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Franc?s i franc?s.")).length);
        // checks abbreviations
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Viu al n?m. 23 del carrer Nou.")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("N'hi ha de color vermell, blau, verd, etc.")).length);
        // Test for Multiwords.
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Era vox populi.")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Aquell era l'statu quo.")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Va ser la XIV edici?.")).length);
        // test for "LanguageTool":
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("LanguageTool!")).length);
        // test for numbers, punctuation
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence(",")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("123454")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("1234,54")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("1.234,54")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("1 234,54")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("-1 234,54")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Fa una temperatura de 30?C")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Fa una temperatura de 30 ?C")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("Any2010")).length);
        // tests for mixed case words
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("pH")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("McDonald")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("Aix??sUnError")).length);
        // incorrect words:
        matches = rule.match(langTool.getAnalyzedSentence("Bordoy"));
        Assert.assertEquals(1, matches.length);
        // Bord?; Bordoi; Bordo; bordon
        Assert.assertEquals("Bord?", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("Bordoi", matches[0].getSuggestedReplacements().get(1));
        Assert.assertEquals("Bordo", matches[0].getSuggestedReplacements().get(2));
        Assert.assertEquals("Bordon", matches[0].getSuggestedReplacements().get(3));
        matches = rule.match(langTool.getAnalyzedSentence("Mal'aysia"));
        Assert.assertEquals(1, matches.length);
        matches = rule.match(langTool.getAnalyzedSentence("Mala?ysia"));
        Assert.assertEquals(1, matches.length);
        matches = rule.match(langTool.getAnalyzedSentence("Malaysia"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("Mal?isia", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals(1, matches[0].getSuggestedReplacements().size());
        matches = rule.match(langTool.getAnalyzedSentence("quna"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("que", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("una", matches[0].getSuggestedReplacements().get(1));
        Assert.assertEquals("quan", matches[0].getSuggestedReplacements().get(2));
        // capitalized suggestion
        matches = rule.match(langTool.getAnalyzedSentence("Video"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("V?deo", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("b?nner"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("b?ner", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("especialisats"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("especialitzats", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("colaborassi?"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("col?laboraci?", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("colaboraci?"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("col?laboraci?", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss"));
        Assert.assertEquals(1, matches.length);
        matches = rule.match(langTool.getAnalyzedSentence("plassa"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("pla?a", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("De?"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("Deu", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("D?u", matches[0].getSuggestedReplacements().get(1));
        matches = rule.match(langTool.getAnalyzedSentence("joan"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals(0, matches[0].getFromPos());
        Assert.assertEquals(4, matches[0].getToPos());
        Assert.assertEquals("Joan", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("abatusats"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals(0, matches[0].getFromPos());
        Assert.assertEquals(9, matches[0].getToPos());
        Assert.assertEquals("abatussats", matches[0].getSuggestedReplacements().get(0));
        // incomplete multiword
        matches = rule.match(langTool.getAnalyzedSentence("L'statu"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals(2, matches[0].getFromPos());
        Assert.assertEquals(7, matches[0].getToPos());
        Assert.assertEquals("tato", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("arg?it"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals(0, matches[0].getFromPos());
        Assert.assertEquals(6, matches[0].getToPos());
        Assert.assertEquals("argu?t", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("arg?ir", matches[0].getSuggestedReplacements().get(1));
        Assert.assertEquals("arg?int", matches[0].getSuggestedReplacements().get(2));
        matches = rule.match(langTool.getAnalyzedSentence("?ngel"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("?ngel", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("?ngel", matches[0].getSuggestedReplacements().get(1));
        matches = rule.match(langTool.getAnalyzedSentence("ca?essim"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("cac?ssim", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("cass?ssim", matches[0].getSuggestedReplacements().get(1));
        Assert.assertEquals("cass?ssim", matches[0].getSuggestedReplacements().get(2));
        Assert.assertEquals("cas?ssim", matches[0].getSuggestedReplacements().get(3));
        Assert.assertEquals("cas?ssim", matches[0].getSuggestedReplacements().get(4));
        matches = rule.match(langTool.getAnalyzedSentence("coche"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("cotxe", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("cuixa", matches[0].getSuggestedReplacements().get(1));
        Assert.assertEquals("coixa", matches[0].getSuggestedReplacements().get(2));
        matches = rule.match(langTool.getAnalyzedSentence("cantar??"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("cantaria", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("cantera", matches[0].getSuggestedReplacements().get(1));
        // best suggestion first
        matches = rule.match(langTool.getAnalyzedSentence("poguem"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("puguem", matches[0].getSuggestedReplacements().get(0));
        // incorrect mixed case words
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("PH")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("Ph")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("MCDonald")).length);
        matches = rule.match(langTool.getAnalyzedSentence("tAula"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("taula", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("TAula"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("Taula", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("col?Labora"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("col?labora", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("col?labor?"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("col?labora", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("col?labor?", matches[0].getSuggestedReplacements().get(1));
        matches = rule.match(langTool.getAnalyzedSentence("despu?s"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("despr?s", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("dessinstalasio"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("desinstal?l?ssiu", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("desinstal?laci?", matches[0].getSuggestedReplacements().get(1));
        matches = rule.match(langTool.getAnalyzedSentence("matitz?rem"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("matisarem", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("matis?rem", matches[0].getSuggestedReplacements().get(1));
        matches = rule.match(langTool.getAnalyzedSentence("tamitz?ssim"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("tamis?ssim", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("adquireixquen"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("adquirisquen", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("adquiresquen", matches[0].getSuggestedReplacements().get(1));
        matches = rule.match(langTool.getAnalyzedSentence("calificar"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("qualificar", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("desconte"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("descompte", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("atentats"));
        Assert.assertEquals("atemptats", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("s?ntomes"));
        Assert.assertEquals("s?mptomes", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("atentats"));
        Assert.assertEquals("atemptats", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("contable"));
        Assert.assertEquals("comptable", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("desici?"));
        Assert.assertEquals("decisi?", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("Espa?a"));
        Assert.assertEquals("Espanya", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("concenciosament"));
        Assert.assertEquals("conscienciosament", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("excelent"));
        Assert.assertEquals("excel?lent", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("exceleixquen"));
        Assert.assertEquals("excel?lisquen", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("caligrafia"));
        Assert.assertEquals("cal?ligrafia", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("calificaren"));
        Assert.assertEquals("qualificaren", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("Excelentissim"));
        Assert.assertEquals("Excel?lent?ssim", matches[0].getSuggestedReplacements().get(0));
        // Needs Morfologik Speller 2.1.0
        matches = rule.match(langTool.getAnalyzedSentence("milisegons"));
        Assert.assertEquals("mil?lisegons", matches[0].getSuggestedReplacements().get(0));
        /* change in Speller necessary: words of length = 4
        matches = rule.match(langTool.getAnalyzedSentence("nula"));
        assertEquals("nul?la", matches[0].getSuggestedReplacements().get(0));
         */
        // capitalized wrong words
        matches = rule.match(langTool.getAnalyzedSentence("En la Pecra"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("Para", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("Pare", matches[0].getSuggestedReplacements().get(1));
        matches = rule.match(langTool.getAnalyzedSentence("IVa"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("Iva", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("IVA", matches[0].getSuggestedReplacements().get(1));
        matches = rule.match(langTool.getAnalyzedSentence("Dvd"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("DVD", matches[0].getSuggestedReplacements().get(0));
        // deprecated characters of "ela geminada"
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("S'hi havien insta?lat.")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("S'HI HAVIEN INSTA?LAT.")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("a?h")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("a")).length);
        // pronoms febles
        matches = rule.match(langTool.getAnalyzedSentence("Magradaria"));
        Assert.assertEquals("M'agradaria", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("tenvio"));
        Assert.assertEquals("t'envio", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("portan"));
        Assert.assertEquals("porta'n", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("consultins"));
        Assert.assertEquals("consulti'ns", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("portarvos"));
        Assert.assertEquals("portar-vos", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("portemne"));
        Assert.assertEquals("portem-ne", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("dacontentar"));
        Assert.assertEquals("d'acontentar", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("devidents"));
        Assert.assertEquals("de vidents", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("d'evidents", matches[0].getSuggestedReplacements().get(1));
        matches = rule.match(langTool.getAnalyzedSentence("lacomplexat"));
        Assert.assertEquals("la complexat", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("l'acomplexat", matches[0].getSuggestedReplacements().get(1));
        matches = rule.match(langTool.getAnalyzedSentence("dacomplexats"));
        Assert.assertEquals("d'acomplexats", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("lacomplexats"));
        Assert.assertEquals("la complexats", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("acomplexats", matches[0].getSuggestedReplacements().get(1));
        matches = rule.match(langTool.getAnalyzedSentence("veurehi"));
        Assert.assertEquals("veure-hi", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("veurels"));
        Assert.assertEquals("veure'ls", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("veureles"));
        Assert.assertEquals("veure-les", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("lilla"));
        Assert.assertEquals("l'illa", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("portas"));
        Assert.assertEquals("portes", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("mantenir'me"));
        Assert.assertEquals("mantenir-me", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("elcap"));
        Assert.assertEquals("el cap", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("almeu"));
        Assert.assertEquals("al meu", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("delteu"));
        Assert.assertEquals("del teu", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("unshomes"));
        Assert.assertEquals("uns homes", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("pelsseus"));
        Assert.assertEquals("pels seus", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("daquesta"));
        Assert.assertEquals("d'aquesta", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("daquelles"));
        Assert.assertEquals("d'aquelles", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("lah"));
        Assert.assertEquals("la", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("dela"));
        Assert.assertEquals("de la", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("sha"));
        Assert.assertEquals("s'ha", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("xe", matches[0].getSuggestedReplacements().get(1));
        Assert.assertEquals("xa", matches[0].getSuggestedReplacements().get(2));
        matches = rule.match(langTool.getAnalyzedSentence("Sha"));
        Assert.assertEquals("S'ha", matches[0].getSuggestedReplacements().get(0));
        // assertEquals("Xe", matches[0].getSuggestedReplacements().get(1));
        // assertEquals("Xa", matches[0].getSuggestedReplacements().get(2));
        // Ela geminada
        matches = rule.match(langTool.getAnalyzedSentence("La sol?licitud"));
        Assert.assertEquals("sol?licitud", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("La sol-licitud"));
        Assert.assertEquals("sol?licitud", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("SOL.LICITUD"));
        Assert.assertEquals("Sol?licitud", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("La sol?licitud"));
        Assert.assertEquals("sol?licitud", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("La sol?licitud"));
        Assert.assertEquals("sol?licitud", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("un estat sindical-laborista"));
        Assert.assertEquals(0, matches.length);
        matches = rule.match(langTool.getAnalyzedSentence("en un estat sindical.La classe obrera"));
        Assert.assertEquals(0, matches.length);
        matches = rule.match(langTool.getAnalyzedSentence("al-Ladjdjun"));
        Assert.assertEquals(3, matches[0].getFromPos());
        Assert.assertEquals(11, matches[0].getToPos());
        // "ela geminada" error + another spelling error
        matches = rule.match(langTool.getAnalyzedSentence("La sol?licitut"));
        Assert.assertEquals("sol?licitud", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("Il?lustran"));
        Assert.assertEquals("Il?lustren", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("b?l.lica"));
        Assert.assertEquals("b?l?lica", matches[0].getSuggestedReplacements().get(0));
        // maj?scules
        matches = rule.match(langTool.getAnalyzedSentence("De PH 4"));
        Assert.assertEquals("pH", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("De l'any 156 Ac a l'any 2000."));
        Assert.assertEquals("aC", matches[0].getSuggestedReplacements().get(0));
    }
}

