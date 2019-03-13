/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2013 Andriy Rysin
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
package org.languagetool.rules.uk;


import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.Ukrainian;
import org.languagetool.rules.RuleMatch;


public class HiddenCharacterRuleTest {
    @Test
    public void testRule() throws IOException {
        final MixedAlphabetsRule rule = new MixedAlphabetsRule(TestTools.getMessages("uk"));
        final JLanguageTool langTool = new JLanguageTool(new Ukrainian());
        // correct sentences:
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("??????")).length);
        // incorrect sentences:
        RuleMatch[] matches = rule.match(langTool.getAnalyzedSentence("\u0441\u043ci\u00ad\u0442\u0442\u044f"));
        // check match positions:
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals(Arrays.asList("??????"), matches[0].getSuggestedReplacements());
    }
}

