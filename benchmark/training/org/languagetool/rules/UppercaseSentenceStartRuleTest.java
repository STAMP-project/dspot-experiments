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
package org.languagetool.rules;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;


public class UppercaseSentenceStartRuleTest {
    private final UppercaseSentenceStartRule rule = new UppercaseSentenceStartRule(TestTools.getEnglishMessages(), TestTools.getDemoLanguage(), Example.wrong("<marker>a</marker> sentence."), Example.fixed("<marker>A</marker> sentence."));

    private final JLanguageTool lt = new JLanguageTool(TestTools.getDemoLanguage());

    @Test
    public void testRule() throws IOException {
        // correct sentences:
        assertGood("this");
        assertGood("a) This is a test sentence.");
        assertGood("iv. This is a test sentence...");
        assertGood("\"iv. This is a test sentence...\"");
        assertGood("?iv. This is a test sentence...");
        assertGood("This");
        assertGood("This is");
        assertGood("This is a test sentence");
        assertGood("");
        assertGood("http://www.languagetool.org");
        // incorrect sentences:
        RuleMatch[] matches = rule.match(lt.analyzeText("this is a test sentence."));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals(0, matches[0].getFromPos());
        Assert.assertEquals(4, matches[0].getToPos());
        RuleMatch[] matches2 = rule.match(lt.analyzeText("this!"));
        Assert.assertEquals(1, matches2.length);
        Assert.assertEquals(0, matches2[0].getFromPos());
        Assert.assertEquals(4, matches2[0].getToPos());
        RuleMatch[] matches3 = rule.match(lt.analyzeText("'this is a sentence'."));
        Assert.assertEquals(1, matches3.length);
        RuleMatch[] matches4 = rule.match(lt.analyzeText("\"this is a sentence.\""));
        Assert.assertEquals(1, matches4.length);
        RuleMatch[] matches5 = rule.match(lt.analyzeText("?this is a sentence."));
        Assert.assertEquals(1, matches5.length);
        RuleMatch[] matches6 = rule.match(lt.analyzeText("?this is a sentence."));
        Assert.assertEquals(1, matches6.length);
        RuleMatch[] matches7 = rule.match(lt.analyzeText("?this is a sentence."));
        Assert.assertEquals(1, matches7.length);
    }
}

