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
package org.languagetool.rules.spelling.hunspell;


import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.AustrianGerman;
import org.languagetool.language.GermanyGerman;
import org.languagetool.language.SwissGerman;


public class HunspellRuleTest {
    @Test
    public void testRuleWithGerman() throws Exception {
        HunspellRule rule = new HunspellRule(TestTools.getMessages("de"), new GermanyGerman(), null);
        JLanguageTool langTool = new JLanguageTool(new GermanyGerman());
        commonGermanAsserts(rule, langTool);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Der ?u?ere ?belt?ter.")).length);// umlauts

        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("Der ?ussere ?belt?ter.")).length);
        // ignore URLs:
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Unter http://foo.org/bar steht was.")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("dasdassda http://foo.org/bar steht was.")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("Unter http://foo.org/bar steht dasdassda.")).length);
        // check the correct calculation of error position
        // note that emojis have string length 2
        Assert.assertEquals(6, rule.match(langTool.getAnalyzedSentence("Hallo men Schatz!"))[0].getFromPos());
        Assert.assertEquals(9, rule.match(langTool.getAnalyzedSentence("Hallo men Schatz!"))[0].getToPos());
        Assert.assertEquals(9, rule.match(langTool.getAnalyzedSentence("Hallo ? men Schatz!"))[0].getFromPos());
        Assert.assertEquals(12, rule.match(langTool.getAnalyzedSentence("Hallo ? men Schatz!"))[0].getToPos());
        Assert.assertEquals(11, rule.match(langTool.getAnalyzedSentence("Hallo ?? men Schatz!"))[0].getFromPos());
        Assert.assertEquals(14, rule.match(langTool.getAnalyzedSentence("Hallo ?? men Schatz!"))[0].getToPos());
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Mir geht es ?gut?.")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("Mir geht es ?gtu?.")).length);
    }

    @Test
    public void testRuleWithAustrianGerman() throws Exception {
        HunspellRule rule = new HunspellRule(TestTools.getMessages("de"), new AustrianGerman(), null);
        JLanguageTool langTool = new JLanguageTool(new GermanyGerman());
        commonGermanAsserts(rule, langTool);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Der ?u?ere ?belt?ter.")).length);// umlauts

        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("Der ?ussere ?belt?ter.")).length);
    }

    @Test
    public void testRuleWithSwissGerman() throws Exception {
        HunspellRule rule = new HunspellRule(TestTools.getMessages("de"), new SwissGerman(), null);
        JLanguageTool langTool = new JLanguageTool(new GermanyGerman());
        commonGermanAsserts(rule, langTool);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("Der ?u?ere ?belt?ter.")).length);// ? not allowed in Swiss

        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Der ?ussere ?belt?ter.")).length);// ss is used instead of ?

    }
}

