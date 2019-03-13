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
package org.languagetool.rules.pl;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;


/**
 *
 *
 * @author Ionu? P?duraru
 */
public class SimpleReplaceRuleTest {
    private SimpleReplaceRule rule;

    private JLanguageTool langTool;

    @Test
    public void testRule() throws IOException {
        // correct sentences:
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Wszystko w porz?dku.")).length);
        // no checking lemmas:
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Pola lodowe")).length);
        // with immunized tokens:
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Witamy prez. Komorowskiego!")).length);
        // incorrect sentences:
        // at the beginning of a sentence (Romanian replace rule is case-sensitive)
        checkSimpleReplaceRule("Piaty przypadek.", "Pi?ty");
        // inside sentence
        checkSimpleReplaceRule("To piaty przypadek.", "pi?ty");
    }
}

