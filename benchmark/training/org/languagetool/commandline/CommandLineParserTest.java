/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2012 Daniel Naber (http://www.danielnaber.de)
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


import org.junit.Assert;
import org.junit.Test;


public class CommandLineParserTest {
    @Test
    public void testUsage() throws Exception {
        CommandLineParser parser = new CommandLineParser();
        try {
            parser.parseOptions(new String[]{  });
            Assert.fail();
        } catch (WrongParameterNumberException ignored) {
        }
        CommandLineOptions commandLineOptions = parser.parseOptions(new String[]{ "--help" });
        Assert.assertTrue(commandLineOptions.isPrintUsage());
    }

    @Test
    public void testErrors() throws Exception {
        CommandLineParser parser = new CommandLineParser();
        try {
            parser.parseOptions(new String[]{ "--apply", "--taggeronly" });
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void testSimple() throws Exception {
        CommandLineParser parser = new CommandLineParser();
        CommandLineOptions options;
        options = parser.parseOptions(new String[]{ "filename.txt" });
        Assert.assertNull(options.getLanguage());
        Assert.assertEquals("filename.txt", options.getFilename());
        Assert.assertFalse(options.isVerbose());
        options = parser.parseOptions(new String[]{ "--language", "xx", "filename.txt" });
        Assert.assertEquals("xx", options.getLanguage().getShortCode());
        Assert.assertEquals("filename.txt", options.getFilename());
        Assert.assertFalse(options.isVerbose());
        options = parser.parseOptions(new String[]{ "-l", "xx", "filename.txt" });
        Assert.assertEquals("xx", options.getLanguage().getShortCode());
        Assert.assertEquals("filename.txt", options.getFilename());
        Assert.assertFalse(options.isVerbose());
        options = parser.parseOptions(new String[]{ "-v", "-l", "xx", "filename.txt" });
        Assert.assertEquals("xx", options.getLanguage().getShortCode());
        Assert.assertEquals("filename.txt", options.getFilename());
        Assert.assertTrue(options.isVerbose());
        options = parser.parseOptions(new String[]{ "--version" });
        Assert.assertTrue(options.isPrintVersion());
        options = parser.parseOptions(new String[]{ "--list" });
        Assert.assertTrue(options.isPrintLanguages());
    }
}

