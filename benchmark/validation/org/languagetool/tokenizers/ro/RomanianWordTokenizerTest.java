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
package org.languagetool.tokenizers.ro;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class RomanianWordTokenizerTest {
    @Test
    public void testTokenize() {
        // basic test - simple words, no diacritics
        RomanianWordTokenizer w = new RomanianWordTokenizer();
        List<String> testList = w.tokenize("Aceaste mese sunt bune");
        Assert.assertEquals(7, testList.size());
        Assert.assertEquals("[Aceaste,  , mese,  , sunt,  , bune]", testList.toString());
        // basic test - simle words, with diacritics
        testList = w.tokenize("Aceast? carte este frumoas?");
        Assert.assertEquals(7, testList.size());
        Assert.assertEquals("[Aceast?,  , carte,  , este,  , frumoas?]", testList.toString());
        // test for "-"
        testList = w.tokenize("nu-?i doresc");
        Assert.assertEquals(5, testList.size());
        Assert.assertEquals("[nu, -, ?i,  , doresc]", testList.toString());
        // test for "?"
        testList = w.tokenize("zicea ?merge");
        Assert.assertEquals(4, testList.size());
        Assert.assertEquals("[zicea,  , ?, merge]", testList.toString());
        // test for "?" with white space
        testList = w.tokenize("zicea ? merge");
        Assert.assertEquals(5, testList.size());
        Assert.assertEquals("[zicea,  , ?,  , merge]", testList.toString());
        // test for "?"
        testList = w.tokenize("zicea merge?");
        Assert.assertEquals(4, testList.size());
        Assert.assertEquals("[zicea,  , merge, ?]", testList.toString());
        // test for "?" and "?"
        testList = w.tokenize("zicea ?merge bine?");
        Assert.assertEquals(7, testList.size());
        Assert.assertEquals("[zicea,  , ?, merge,  , bine, ?]", testList.toString());
        // ?i-am
        testList = w.tokenize("?i-am");
        Assert.assertEquals(3, testList.size());
        Assert.assertEquals("[?i, -, am]", testList.toString());
        // test for "?" and "?"
        testList = w.tokenize("zicea ?merge bine?");
        Assert.assertEquals(7, testList.size());
        Assert.assertEquals("[zicea,  , ?, merge,  , bine, ?]", testList.toString());
        // test for "<" and ">"
        testList = w.tokenize("zicea <<merge bine>>");
        Assert.assertEquals(9, testList.size());
        Assert.assertEquals("[zicea,  , <, <, merge,  , bine, >, >]", testList.toString());
        // test for "%"
        testList = w.tokenize("avea 15% ap?");
        Assert.assertEquals(6, testList.size());
        Assert.assertEquals("[avea,  , 15, %,  , ap?]", testList.toString());
        // test for "?"
        testList = w.tokenize("are 30?C");
        Assert.assertEquals(5, testList.size());
        Assert.assertEquals("[are,  , 30, ?, C]", testList.toString());
        // test for "="
        testList = w.tokenize("fructe=mere");
        Assert.assertEquals(3, testList.size());
        Assert.assertEquals("[fructe, =, mere]", testList.toString());
        // test for "|"
        testList = w.tokenize("pere|mere");
        Assert.assertEquals(testList.size(), 3);
        Assert.assertEquals("[pere, |, mere]", testList.toString());
        // test for "\n"
        testList = w.tokenize("pere\nmere");
        Assert.assertEquals(3, testList.size());
        Assert.assertEquals("[pere, \n, mere]", testList.toString());
        // test for "\r"
        testList = w.tokenize("pere\rmere");
        Assert.assertEquals(3, testList.size());
        Assert.assertEquals("[pere, \r, mere]", testList.toString());
        // test for "\n\r"
        testList = w.tokenize("pere\n\rmere");
        Assert.assertEquals(4, testList.size());
        Assert.assertEquals("[pere, \n, \r, mere]", testList.toString());
        // test for URLs
        testList = w.tokenize("www.LanguageTool.org");
        Assert.assertEquals(1, testList.size());
    }
}

