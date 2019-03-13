/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2009 Marcin Mi?kowski (http://www.languagetool.org)
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
package org.languagetool.tools;


import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.language.Polish;
import org.languagetool.rules.RuleMatch;
import org.xml.sax.SAXException;


public class ToolsTest {
    @Test
    public void testCheck() throws IOException, ParserConfigurationException, SAXException {
        final JLanguageTool tool = new JLanguageTool(new Polish());
        List<RuleMatch> matches = tool.check("To jest ca?kowicie prawid?owe zdanie.");
        Assert.assertEquals(0, matches.size());
        List<RuleMatch> matches2 = tool.check("To jest problem problem.");
        Assert.assertEquals(1, matches2.size());
        Assert.assertEquals("WORD_REPEAT_RULE", matches2.get(0).getRule().getId());
    }

    @Test
    public void testCorrect() throws IOException, ParserConfigurationException, SAXException {
        JLanguageTool tool = new JLanguageTool(new Polish());
        tool.setCleanOverlappingMatches(false);
        String correct = Tools.correctText("To jest ca?kowicie prawid?owe zdanie.", tool);
        Assert.assertEquals("To jest ca?kowicie prawid?owe zdanie.", correct);
        correct = Tools.correctText("To jest jest problem.", tool);
        Assert.assertEquals("To jest problem.", correct);
        // more sentences, need to apply more suggestions > 1 in subsequent sentences
        correct = Tools.correctText("To jest jest problem. Ale to ju? ju? nie jest problem.", tool);
        Assert.assertEquals("To jest problem. Ale to ju? nie jest problem.", correct);
        correct = Tools.correctText("To jest jest problem. Ale to ju? ju? nie jest problem. Tak sie nie robi. W tym zdaniu brakuje przecinka bo go zapomnia?em.", tool);
        Assert.assertEquals("To jest problem. Ale to ju? nie jest problem. Tak si? nie robi. W tym zdaniu brakuje przecinka, bo go zapomnia?em.", correct);
    }
}

