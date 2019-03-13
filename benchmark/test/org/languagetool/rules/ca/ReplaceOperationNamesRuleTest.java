/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2012 Jaume Ortol?
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
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.Catalan;
import org.languagetool.rules.RuleMatch;


/**
 *
 *
 * @author Jaume Ortol?
 */
public class ReplaceOperationNamesRuleTest {
    private ReplaceOperationNamesRule rule;

    private JLanguageTool langTool;

    @Test
    public void testRule() throws IOException {
        // correct sentences:
        // assertCorrect("els etiquetadors sobre els etiquetats.");
        assertCorrect("tot tenyit amb llum de nost?lgia");
        assertCorrect("Ho van fer per duplicat.");
        assertCorrect("Assecat el bra? del riu");
        assertCorrect("el llibre empaquetat");
        assertCorrect("un resultat equilibrat");
        assertCorrect("el nostre equip era bastant equilibrat");
        assertCorrect("un llibre ben empaquetat");
        assertCorrect("l'informe filtrat pel ministre");
        assertCorrect("L'informe filtrat ?s terrible");
        assertCorrect("ha liderat la batalla");
        assertCorrect("Els tinc empaquetats");
        assertCorrect("amb tractament unitari i equilibrat");
        assertCorrect("Processat despr?s de la mort de Carles II");
        assertCorrect("Processat diverses vegades");
        assertCorrect("moltes vegades empaquetat amb pressa");
        assertCorrect("?s llavors embotellat i llan?at al mercat");
        assertCorrect("la comercialitzaci? de vi embotellat amb les firmes comercials");
        assertCorrect("eixia al mercat el vi blanc embotellat amb la marca");
        assertCorrect("que arribi a un equilibrat matrimoni");
        assertCorrect("?s un caf? amb molt de cos i molt equilibrat.");
        assertCorrect("i per tant etiquetat com a observat");
        assertCorrect("Molt equilibrat en les seves caracter?stiques");
        assertCorrect("filtrat per Wikileaks");
        assertCorrect("una vegada filtrat");
        assertCorrect("no equilibrat");
        // errors:
        assertIncorrect("Assecat del bra? del riu");
        assertIncorrect("Cal vigilar el filtrat del vi");
        assertIncorrect("El proc?s d'empaquetat");
        assertIncorrect("Els equilibrats de les rodes");
        // assertIncorrect("Duplicat de claus");
        assertIncorrect("El proc?s d'etiquetat de les ampolles");
        assertIncorrect("El rentat de cotes");
        RuleMatch[] matches = rule.match(langTool.getAnalyzedSentence("El repicat i el rejuntat."));
        Assert.assertEquals(2, matches.length);
        matches = rule.match(langTool.getAnalyzedSentence("El proc?s de relligat dels llibres."));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("relligadura", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("relligament", matches[0].getSuggestedReplacements().get(1));
        Assert.assertEquals("relligada", matches[0].getSuggestedReplacements().get(2));
        matches = rule.match(langTool.getAnalyzedSentence("Els rentats de cervell."));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("rentades", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("rentatges", matches[0].getSuggestedReplacements().get(1));
        Assert.assertEquals("rentaments", matches[0].getSuggestedReplacements().get(2));
    }

    @Test
    public void testPositions() throws IOException {
        final AccentuationCheckRule rule = new AccentuationCheckRule(TestTools.getEnglishMessages());
        final RuleMatch[] matches;
        final JLanguageTool langTool = new JLanguageTool(new Catalan());
        matches = rule.match(langTool.getAnalyzedSentence("S?n circumstancies extraordin?ries."));
        Assert.assertEquals(4, matches[0].getFromPos());
        Assert.assertEquals(18, matches[0].getToPos());
    }
}

