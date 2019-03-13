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
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;


/**
 *
 *
 * @author Marcin Milkowski
 */
public class MultipleWhitespaceRuleTest {
    @Test
    public void testRule() throws IOException {
        List<RuleMatch> matches;
        JLanguageTool lt = new JLanguageTool(TestTools.getDemoLanguage());
        setUpRule(lt);
        // correct sentences:
        assertGood("This is a test sentence.", lt);
        assertGood("This is a test sentence...", lt);
        assertGood("\n\tThis is a test sentence...", lt);
        assertGood("Multiple tabs\t\tare okay", lt);
        assertGood("\n This is a test sentence...", lt);
        assertGood("\n    This is a test sentence...", lt);
        // Needs isParagraphStart creation. Excluding i = 1 will make the rule ignore multiple white spaces in middle senteces.
        // matches = rule.match(langTool.getAnalyzedSentence("    This is a test sentence..."));
        // assertEquals(0, matches.length);
        // incorrect sentences:
        matches = lt.check("This  is a test sentence.");
        Assert.assertEquals(1, matches.size());
        Assert.assertEquals(4, matches.get(0).getFromPos());
        Assert.assertEquals(6, matches.get(0).getToPos());
        matches = lt.check("\n   This  is a test sentence.");
        Assert.assertEquals(1, matches.size());
        Assert.assertEquals(8, matches.get(0).getFromPos());
        Assert.assertEquals(10, matches.get(0).getToPos());
        matches = lt.check("This is a test   sentence.");
        Assert.assertEquals(1, matches.size());
        Assert.assertEquals(14, matches.get(0).getFromPos());
        Assert.assertEquals(17, matches.get(0).getToPos());
        matches = lt.check("This is   a  test   sentence.");
        Assert.assertEquals(3, matches.size());
        Assert.assertEquals(7, matches.get(0).getFromPos());
        Assert.assertEquals(10, matches.get(0).getToPos());
        Assert.assertEquals(11, matches.get(1).getFromPos());
        Assert.assertEquals(13, matches.get(1).getToPos());
        Assert.assertEquals(17, matches.get(2).getFromPos());
        Assert.assertEquals(20, matches.get(2).getToPos());
        matches = lt.check("\t\t\t    \t\t\t\t  ");
        Assert.assertEquals(2, matches.size());
        // with non-breakable spaces
        matches = lt.check("This \u00a0is a test sentence.");
        Assert.assertEquals(1, matches.size());
        Assert.assertEquals(4, matches.get(0).getFromPos());
        Assert.assertEquals(6, matches.get(0).getToPos());
    }
}

