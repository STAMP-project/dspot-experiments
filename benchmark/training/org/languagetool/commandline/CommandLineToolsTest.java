/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2013 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.commandline;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.xml.sax.SAXException;


public class CommandLineToolsTest {
    private ByteArrayOutputStream out;

    private PrintStream stdout;

    private PrintStream stderr;

    @Test
    public void testCheck() throws IOException, ParserConfigurationException, SAXException {
        JLanguageTool tool = new JLanguageTool(TestTools.getDemoLanguage());
        int matches = CommandLineTools.checkText("Foo.", tool);
        String output = new String(this.out.toByteArray());
        Assert.assertEquals(0, output.indexOf("Time:"));
        Assert.assertEquals(0, matches);
        tool.disableRule("test_unification_with_negation");
        tool.addRule(new org.languagetool.rules.WordRepeatRule(TestTools.getEnglishMessages(), TestTools.getDemoLanguage()));
        matches = CommandLineTools.checkText("To jest problem problem.", tool);
        output = new String(this.out.toByteArray());
        Assert.assertTrue(output.contains("Rule ID: WORD_REPEAT_RULE"));
        Assert.assertEquals(1, matches);
    }
}

