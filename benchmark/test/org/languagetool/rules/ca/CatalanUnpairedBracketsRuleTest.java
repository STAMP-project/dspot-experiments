/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2010 Daniel Naber (http://www.languagetool.org)
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
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.language.Catalan;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.TextLevelRule;


public class CatalanUnpairedBracketsRuleTest {
    private TextLevelRule rule;

    private JLanguageTool langTool;

    @Test
    public void testRule() throws IOException {
        // correct sentences:
        assertCorrect("L'?home ?s aix??");
        assertCorrect("l'?home?");
        assertCorrect("\u00ab\"\u00c9s aix\u00ed\" o no\u00bb");
        assertCorrect("\u00ab\"\u00c9s aix\u00ed\", va dir.\u00bb");
        assertCorrect("\u00ab\u00c9s \"aix\u00ed\" o no\u00bb");
        assertCorrect("(l\'execuci\u00f3 a mans d\'\"especialistes\")");
        assertCorrect("(L\'\"especialista\")");
        assertCorrect("\"Vine\", li va dir.");
        assertCorrect("(Una frase de prova).");
        assertCorrect("Aquesta ?s la paraula 'prova'.");
        assertCorrect("This is a sentence with a smiley :-)");
        assertCorrect("This is a sentence with a smiley ;-) and so on...");
        assertCorrect("Aquesta ?s l'hora de les decisions.");
        assertCorrect("Aquesta ?s l?hora de les decisions.");
        assertCorrect("(fig. 20)");
        assertCorrect("\"S\u00f3c la teva filla. El corc\u00f3 no et rosegar\u00e0 m\u00e9s.\"\n\n");
        assertCorrect("\u2013\"Club dels llagoters\" \u2013va repetir en Ron.");
        assertCorrect("\u2014\"Club dels llagoters\" \u2013va repetir en Ron.");
        assertCorrect("?Aix? em porta a demanar-t'ho.");
        assertCorrect("?Aix? em porta (s?) a demanar-t'ho.");
        assertCorrect("al cap\u00edtol 12 \"Llavors i fruits oleaginosos\"");
        assertCorrect("\"Per qu\u00e8 serveixen les forquilles?\" i aquest respon \"per menjar\".");
        assertCorrect("\u00c9s a 60\u00ba 50\' 23\"");
        assertCorrect("?s a 60? 50' 23'");
        assertCorrect("60? 50' 23'");
        assertCorrect("60? 50'");
        // assertCorrect("el grau en 60 parts iguals, tenim el minut (1'):");
        // assertCorrect("el minut en 60 parts iguals, tenim el segon (1\"):");
        assertCorrect("El tr\u00e0iler t\u00e9 una picada d\'ullet quan diu que \"no es pot fer una pel\u00b7l\u00edcula \'slasher\' com si fos una s\u00e8rie\".");
        assertCorrect("El tr\u00e0iler \u2013que t\u00e9 una picada d\'ullet quan diu que \"no es pot fer una pel\u00b7l\u00edcula \'slasher\' com si fos una s\u00e8rie\"\u2013 ja ");
        // assertCorrect("The screen is 20\" wide.");
        assertCorrect("This is a [test] sentence...");
        assertCorrect("The plight of Tamil refugees caused a surge of support from most of the Tamil political parties.[90]");
        assertCorrect("This is what he said: \"We believe in freedom. This is what we do.\"");
        assertCorrect("(([20] [20] [20]))");
        // test for a case that created a false alarm after disambiguation
        assertCorrect("This is a \"special test\", right?");
        // numerical bullets
        assertCorrect("We discussed this in Chapter 1).");
        assertCorrect("The jury recommended that: (1) Four additional deputies be employed.");
        assertCorrect("We discussed this in section 1a).");
        assertCorrect("We discussed this in section iv).");
        // inches exception shouldn't match " here:
        assertCorrect("In addition, the government would pay a $1,000 \"cost of education\" grant to the schools.");
        // assertCorrect("Paradise lost to the alleged water needs of Texas' big cities Thursday.");
        assertCorrect("Porta'l cap ac?.");
        assertCorrect("Porta-me'n cinquanta!");
        // incorrect sentences:
        assertIncorrect("(L\'\"especialista\"");
        assertIncorrect("L'?home ?s aix?");
        assertIncorrect("S\'\u00abesperava \'el\' (segon) \"resultat\"");
        assertIncorrect("l'?home");
        assertIncorrect("Ploraria.\"");
        assertIncorrect("Aquesta ?s l555?hora de les decisions.");
        assertIncorrect("Vine\", li va dir.");
        assertIncorrect("Aquesta ?s l?hora de les decisions.");
        assertIncorrect("(This is a test sentence.");
        assertIncorrect("This is a test with an apostrophe &'.");
        assertIncorrect("&'");
        assertIncorrect("!'");
        assertIncorrect("What?'");
        // this is currently considered incorrect... although people often use smileys this way:
        assertIncorrect("Some text (and some funny remark :-) with more text to follow");
        RuleMatch[] matches;
        matches = rule.match(Collections.singletonList(langTool.getAnalyzedSentence("(This is a test? sentence.")));
        Assert.assertEquals(2, matches.length);
        matches = rule.match(Collections.singletonList(langTool.getAnalyzedSentence("This [is (a test} sentence.")));
        Assert.assertEquals(3, matches.length);
    }

    @Test
    public void testMultipleSentences() throws IOException {
        final JLanguageTool tool = new JLanguageTool(new Catalan());
        tool.enableRule("CA_UNPAIRED_BRACKETS");
        List<RuleMatch> matches;
        matches = tool.check(("Aquesta ?s una sent?ncia m?ltiple amb claud?tors: " + "[Ac\u00ed hi ha un claud\u00e0tor. Amb algun text.] i ac\u00ed continua.\n"));
        Assert.assertEquals(0, matches.size());
        matches = tool.check("\"S\u00f3c la teva filla. El corc\u00f3 no et rosegar\u00e0 m\u00e9s.\"\n\n");
        Assert.assertEquals(0, matches.size());
        matches = tool.check("\"S\u00f3c la teva filla. El corc\u00f3 no et rosegar\u00e0 m\u00e9s\".\n\n");
        Assert.assertEquals(0, matches.size());
        matches = tool.check(("Aquesta ?s una sent?ncia m?ltiple amb claud?tors: " + "[Ac\u00ed hi ha un claud\u00e0tor. Amb algun text. I ac\u00ed continua.\n\n"));
        Assert.assertEquals(1, matches.size());
        matches = tool.check("\u00abEls manaments diuen: \"No desitjar\u00e0s la dona del teu ve\u00ed\"\u00bb");
        // assertEquals(0, matches.size());
        matches = tool.check(("Aquesta ?s una sent?ncia m?ltiple amb par?ntesis " + "(Ac\u00ed hi ha un par\u00e8ntesi. \n\n Amb algun text.) i ac\u00ed continua."));
        Assert.assertEquals(0, matches.size());
    }
}

