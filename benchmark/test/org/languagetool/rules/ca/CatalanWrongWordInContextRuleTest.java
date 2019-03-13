/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2012 Markus Brenneis
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
import org.languagetool.language.Catalan;


/**
 *
 *
 * @author Jaume Ortol?
 */
public class CatalanWrongWordInContextRuleTest {
    @Test
    public void testRule() throws IOException {
        CatalanWrongWordInContextRule rule = new CatalanWrongWordInContextRule(null);
        JLanguageTool langTool = new JLanguageTool(new Catalan());
        // rendible/rentable
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Una empresa molt rendible.")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("Una empresa molt rentable.")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("Uns cultius rentables.")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("Es venen b? i s?n rentables.")).length);
        Assert.assertEquals("rendibles", rule.match(langTool.getAnalyzedSentence("Uns projectes molt rentables."))[0].getSuggestedReplacements().get(0));
        // assertEquals("rentable", rule.match(langTool.getAnalyzedSentence("Un teixit rendible."))[0].getSuggestedReplacements().get(0));
        // assertEquals(1, rule.match(langTool.getAnalyzedSentence("La policia feia d'escolta.")).length);
        // assertEquals(0, rule.match(langTool.getAnalyzedSentence("La policia feia escoltes telef?niques.")).length);
        // assertEquals(0, rule.match(langTool.getAnalyzedSentence("La policia feia escoltes il?legals.")).length);
        // assertEquals(1, rule.match(langTool.getAnalyzedSentence("Van escoltar el detingut fins al calab?s.")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("Li va infringir un mal terrible.")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("?s un terreny abonat per als problemes.")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("No li va cosir b? les betes.")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("Sempre li seguia la beta.")).length);
        // assertEquals(1, rule.match(langTool.getAnalyzedSentence("un any en el qual la reina Victoria encara era al tro brit?nic")).length);
        // pali, pal?li
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("Sota els palis.")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Els pal?lis.")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("El pal?li i el s?nscrit.")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("El pali i el s?nscrit.")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("Vam comprar xocolate de m?nec.")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("El pic de l'ocell.")).length);
    }
}

