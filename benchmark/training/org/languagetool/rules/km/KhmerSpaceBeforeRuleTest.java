/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2011 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.rules.km;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.Khmer;


/**
 *
 *
 * @author Nathan Wells
 */
public class KhmerSpaceBeforeRuleTest {
    @Test
    public void testSpaceBeforeRule() throws IOException {
        final Khmer language = new Khmer();
        KhmerSpaceBeforeRule rule = new KhmerSpaceBeforeRule(TestTools.getEnglishMessages(), language);
        JLanguageTool langTool = new JLanguageTool(language);
        // correct sentences:
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("??????????? ?????????????????")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("????? ?????????")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("???????????? ?????????????????????")).length);
        // incorrect sentences:
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("?????????????????????????????")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("???????????????")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("??????????????????????????????????")).length);
    }
}

