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
package org.languagetool.rules.uk;


import java.io.IOException;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.Ukrainian;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.UppercaseSentenceStartRule;


public class UppercaseSentenceStartRuleTest {
    @Test
    public void testUkrainian() throws IOException {
        Ukrainian ukrainian = new Ukrainian();
        UppercaseSentenceStartRule rule = new UppercaseSentenceStartRule(TestTools.getEnglishMessages(), ukrainian);
        JLanguageTool lt = new JLanguageTool(ukrainian);
        Assert.assertEquals(0, rule.match(lt.analyzeText("????? ??????? ?? ??????? ? ??????? ??????.")).length);
        RuleMatch[] matches = rule.match(lt.analyzeText("????? ??????? ?? ??????? ? ????????? ??????."));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals(1, matches[0].getSuggestedReplacements().size());
        Assert.assertEquals("?????", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals(new ArrayList<RuleMatch>(), lt.check("\u0426\u0435\u0439 \u0441\u043f\u0438\u0441\u043e\u043a \u0437 \u0434\u0435\u043a\u0456\u043b\u044c\u043a\u043e\u0445 \u0440\u044f\u0434\u043a\u0456\u0432:\n\n\u0440\u044f\u0434\u043e\u043a 1,\n\n\u0440\u044f\u0434\u043e\u043a 2,\n\n\u0440\u044f\u0434\u043e\u043a 3."));
        Assert.assertEquals(0, lt.check("\u0426\u0435\u0439 \u0441\u043f\u0438\u0441\u043e\u043a \u0437 \u0434\u0435\u043a\u0456\u043b\u044c\u043a\u043e\u0445 \u0440\u044f\u0434\u043a\u0456\u0432:\n\n\u0440\u044f\u0434\u043e\u043a 1;\n\n\u0440\u044f\u0434\u043e\u043a 2;\n\n\u0440\u044f\u0434\u043e\u043a 3.").size());
        Assert.assertEquals(0, lt.check("\u0426\u0435\u0439 \u0441\u043f\u0438\u0441\u043e\u043a \u0437 \u0434\u0435\u043a\u0456\u043b\u044c\u043a\u043e\u0445 \u0440\u044f\u0434\u043a\u0456\u0432:\n\n 1) \u0440\u044f\u0434\u043e\u043a 1;\n\n2) \u0440\u044f\u0434\u043e\u043a 2;\n\n3)\u0440\u044f\u0434\u043e\u043a 3.").size());
    }
}

