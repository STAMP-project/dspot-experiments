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
package org.languagetool.rules.pl;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.Polish;


public class PolishUnpairedBracketsRuleTest {
    @Test
    public void testRulePolish() throws IOException {
        Polish language = new Polish();
        PolishUnpairedBracketsRule rule = new PolishUnpairedBracketsRule(TestTools.getEnglishMessages(), language);
        JLanguageTool lt = new JLanguageTool(language);
        Assert.assertEquals(0, getMatches("(To jest zdanie do testowania).", rule, lt));
        Assert.assertEquals(0, getMatches("Piosenka ta trafi\u0142a na wiele list \"Best of...\", w\u0142\u0105czaj\u0105c w to te, kt\u00f3re zosta\u0142y utworzone przez magazyn Rolling Stone.", rule, lt));
        Assert.assertEquals(0, getMatches("A \"B\" C.", rule, lt));
        Assert.assertEquals(0, getMatches("\"A\" B \"C\".", rule, lt));
        Assert.assertEquals(1, getMatches("W tym zdaniu jest niesparowany ?cudzys??w.", rule, lt));
    }
}

