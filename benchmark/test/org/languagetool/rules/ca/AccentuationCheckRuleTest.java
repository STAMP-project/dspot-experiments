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
public class AccentuationCheckRuleTest {
    private AccentuationCheckRule rule;

    private JLanguageTool langTool;

    @Test
    public void testRule() throws IOException {
        // correct sentences:
        assertCorrect("?I continues mantenint que aix? va succeir");
        assertCorrect("No hi ha ning? aqu? que begui vi?");
        assertCorrect("Va tocar l'?ria da capo de les variacions Goldberg.");
        assertCorrect("A ponent continua la serra de Fontpobra");
        assertCorrect("com a base de la categoria faria que els enlla?os");
        assertCorrect("De jove faria amistat amb ells");
        assertCorrect("De jove tenia admiraci?");
        assertCorrect("Per tant, espero ansi?s.");
        assertCorrect("M'espero qualsevol cosa.");
        assertCorrect("Carrega de nou l'arxiu.");
        assertCorrect("Espero d'ell moltes coses");
        assertCorrect("cal que abans figuri inscrit en l'Ordre del dia");
        assertCorrect("El lloc era, per?, habitat de molt abans,");
        assertCorrect("i del bel canto del rococ?.");
        assertCorrect("El lloc fou habitat de forma cont?nua");
        assertCorrect("que havia estat habitat fins al regnat de Joan II");
        assertCorrect("L?pez-Pic? publica el seu llibre de poesies");
        assertCorrect("Vaig perdut, tremolo de por.");
        assertCorrect("l'home marra el cam?");
        assertCorrect("vei? que darrere venia el deixeble");
        assertCorrect("Per? el qui begui de l'aigua");
        assertCorrect("a qualsevol qui en mati un altre");
        assertCorrect("tu que prediques de no robar");
        assertCorrect("els seg?ents territoris externs habitats:");
        assertCorrect("Cap faria una cosa aix?.");
        assertCorrect("El cos genera suficient pressi? interna.");
        assertCorrect("Les seues contr?ries.");
        assertCorrect("Aix? ?s una frase de prova.");
        assertCorrect("Amb ren?ncies i esfor?.");
        assertCorrect("He vingut per a cantar");
        assertCorrect("S?n circumst?ncies d'un altre caire.");
        assertCorrect("La ren?ncia del president.");
        assertCorrect("Circumst?ncies extraordin?ries.");
        assertCorrect("Les circumst?ncies que ens envolten.");
        assertCorrect("Ella continua enfadada.");
        assertCorrect("Ell obvia els problemes.");
        assertCorrect("De manera ?bvia.");
        assertCorrect("Ell fa tasques espec?fiques.");
        assertCorrect("Un home ad?lter.");
        assertCorrect("Jo adulter el resultat.");
        assertCorrect("Va deixar els nens at?nits.");
        assertCorrect("La sureda ocupa ?mplies extensions en la muntanya.");
        assertCorrect("F?u una magn?fica digitaci?.");
        assertCorrect("La disputa continua oberta.");
        assertCorrect("La llum tarda 22 minuts.");
        assertCorrect("?s el tretz? municipi m?s habitat de la comarca.");
        assertCorrect("Els h?bitats de la comarca.");
        assertCorrect("Joan Pau II beatifica Paula Montal.");
        assertCorrect("La magn?fica conservaci? del palau.");
        // errors:
        assertIncorrect("El millor de la historia.");
        assertIncorrect("El millor d'aquesta historia.");
        assertIncorrect("L'ultima consideraci?.");
        assertIncorrect("Com s'ha dit les primaries auton?miques s'han ajornat");
        assertIncorrect("Com sabeu les primaries s'han ajornat");
        assertIncorrect("Les continues al?lusions a la vict?ria.");
        assertIncorrect("De positiva influencia en ell.");
        assertIncorrect("tren de llarga distancia");
        // assertIncorrect("com la nostra pr?pia desgracia");
        // assertIncorrect("la seva influencia");
        assertIncorrect("Cal una nova formula que substitueixi el caduc Estat del benestar.");
        assertIncorrect("Porta-la i nosaltres fem la copia i la compulsem.");
        assertIncorrect("Carrega d'arxius.");
        assertIncorrect("Vaig arribar a fer una radio que no va funcionar mai.");
        assertIncorrect("No em fumar? una faria com feia abans.");
        assertIncorrect("M'he fumat una faria.");
        assertIncorrect("Les seues contraries.");
        assertIncorrect("Amb renuncies i esfor?.");
        assertIncorrect("La renuncia del president.");
        assertIncorrect("S?n circumstancies d'un altre caire.");
        assertIncorrect("Circumstancies extraordin?ries.");
        assertIncorrect("Les circumstancies que ens envolten.");
        assertIncorrect("De manera obvia.");
        assertIncorrect("Ell fa tasques especifiques.");
        assertIncorrect("Un home adulter.");
        // assertIncorrect("Va deixar els nens atonits."); del v. "atonir" (=esbalair)
        assertIncorrect("La sureda ocupa amplies extensions en la muntanya.");
        assertIncorrect("F?u una magnifica digitaci?.");
        assertIncorrect("La magnifica conservaci? del palau.");
        final RuleMatch[] matches = rule.match(langTool.getAnalyzedSentence("Les circumstancies que ens envolten s?n circumstancies extraordin?ries."));
        Assert.assertEquals(2, matches.length);
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

