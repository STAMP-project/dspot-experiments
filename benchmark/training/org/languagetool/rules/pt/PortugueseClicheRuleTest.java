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
package org.languagetool.rules.pt;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;


/**
 *
 *
 * @author Tiago F. Santos based on SimpleReplaceRule test
 * @since 3.6
 */
public class PortugueseClicheRuleTest {
    private PortugueseClicheRule rule;

    private JLanguageTool langTool;

    @Test
    public void testRule() throws IOException {
        // correct sentences:
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Evite as frases-feitas e as express?es idiom?ticas.")).length);
        // incorrect sentences:
        // at the beginning of a sentence (Romanian replace rule is case-sensitive)
        checkSimpleReplaceRule("Teste. A todo o vapor!", "O mais r?pido poss?vel");
        // inside sentence
        checkSimpleReplaceRule("Teste, a todo o vapor!", "o mais r?pido poss?vel");
    }
}

