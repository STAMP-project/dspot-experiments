/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2012 Jaume Ortol?
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
package org.languagetool.tokenizers.ca;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class CatalanWordTokenizerTest {
    @Test
    public void testTokenize() {
        CatalanWordTokenizer wordTokenizer = new CatalanWordTokenizer();
        List<String> tokens;
        tokens = wordTokenizer.tokenize("name@example.com");
        Assert.assertEquals(tokens.size(), 1);
        tokens = wordTokenizer.tokenize("name@example.com.");
        Assert.assertEquals(tokens.size(), 2);
        tokens = wordTokenizer.tokenize("name@example.com:");
        Assert.assertEquals(tokens.size(), 2);
        tokens = wordTokenizer.tokenize("L'origen de name@example.com.");
        Assert.assertEquals(tokens.size(), 7);
        Assert.assertEquals("[L', origen,  , de,  , name@example.com, .]", tokens.toString());
        tokens = wordTokenizer.tokenize("L'origen de name@example.com i de name2@example.com.");
        Assert.assertEquals(tokens.size(), 13);
        Assert.assertEquals("[L', origen,  , de,  , name@example.com,  , i,  , de,  , name2@example.com, .]", tokens.toString());
        tokens = wordTokenizer.tokenize("L\'\"ala bastarda\".");
        Assert.assertEquals(tokens.size(), 7);
        Assert.assertEquals("[L\', \", ala,  , bastarda, \", .]", tokens.toString());
        tokens = wordTokenizer.tokenize("d\'\"ala bastarda\".");
        Assert.assertEquals(tokens.size(), 7);
        Assert.assertEquals("[d\', \", ala,  , bastarda, \", .]", tokens.toString());
        tokens = wordTokenizer.tokenize("Emporta-te'ls a l'observatori dels mars");
        Assert.assertEquals(tokens.size(), 13);
        Assert.assertEquals("[Emporta, -te, 'ls,  , a,  , l', observatori,  , de, ls,  , mars]", tokens.toString());
        tokens = wordTokenizer.tokenize("Emporta-te?ls a l?observatori dels mars");
        Assert.assertEquals(tokens.size(), 13);
        Assert.assertEquals("[Emporta, -te, ?ls,  , a,  , l?, observatori,  , de, ls,  , mars]", tokens.toString());
        tokens = wordTokenizer.tokenize("?El tren Barcelona-Val?ncia?");
        Assert.assertEquals(tokens.size(), 9);
        Assert.assertEquals("[?, El,  , tren,  , Barcelona, -, Val?ncia, ?]", tokens.toString());
        tokens = wordTokenizer.tokenize("El tren Barcelona-Val?ncia");
        Assert.assertEquals(tokens.size(), 7);
        Assert.assertEquals("[El,  , tren,  , Barcelona, -, Val?ncia]", tokens.toString());
        tokens = wordTokenizer.tokenize("No acabava d?entendre?l b?");
        Assert.assertEquals(tokens.size(), 9);
        Assert.assertEquals("[No,  , acabava,  , d?, entendre, ?l,  , b?]", tokens.toString());
        tokens = wordTokenizer.tokenize("N'hi ha vint-i-quatre");
        Assert.assertEquals(tokens.size(), 6);
        Assert.assertEquals("[N', hi,  , ha,  , vint-i-quatre]", tokens.toString());
        tokens = wordTokenizer.tokenize("Mont-ras");
        Assert.assertEquals(tokens.size(), 1);
        Assert.assertEquals("[Mont-ras]", tokens.toString());
        tokens = wordTokenizer.tokenize("?s d'1 km.");
        Assert.assertEquals(tokens.size(), 7);
        Assert.assertEquals("[?s,  , d', 1,  , km, .]", tokens.toString());
        tokens = wordTokenizer.tokenize("?s d'1,5 km.");
        Assert.assertEquals(tokens.size(), 7);
        Assert.assertEquals("[?s,  , d', 1,5,  , km, .]", tokens.toString());
        tokens = wordTokenizer.tokenize("?s d'5 km.");
        Assert.assertEquals(tokens.size(), 7);
        Assert.assertEquals("[?s,  , d', 5,  , km, .]", tokens.toString());
        tokens = wordTokenizer.tokenize("la direcci? E-SE");
        Assert.assertEquals(tokens.size(), 7);
        Assert.assertEquals("[la,  , direcci?,  , E, -, SE]", tokens.toString());
        tokens = wordTokenizer.tokenize("la direcci? NW-SE");
        Assert.assertEquals(tokens.size(), 7);
        Assert.assertEquals("[la,  , direcci?,  , NW, -, SE]", tokens.toString());
        tokens = wordTokenizer.tokenize("Se'n d?na vergonya");
        Assert.assertEquals(tokens.size(), 6);
        Assert.assertEquals("[Se, 'n,  , d?na,  , vergonya]", tokens.toString());
        tokens = wordTokenizer.tokenize("Em?lia-Romanya");
        Assert.assertEquals(tokens.size(), 3);
        Assert.assertEquals("[Em?lia, -, Romanya]", tokens.toString());
        tokens = wordTokenizer.tokenize("L'Em?lia-Romanya");
        Assert.assertEquals(tokens.size(), 4);
        Assert.assertEquals("[L', Em?lia, -, Romanya]", tokens.toString());
        tokens = wordTokenizer.tokenize("col?laboraci?");
        Assert.assertEquals(tokens.size(), 1);
        tokens = wordTokenizer.tokenize("col.laboraci?");
        Assert.assertEquals(tokens.size(), 1);
        tokens = wordTokenizer.tokenize("col?laboraci?");
        Assert.assertEquals(tokens.size(), 1);
        tokens = wordTokenizer.tokenize("col?Laboraci?");
        Assert.assertEquals(tokens.size(), 1);
        tokens = wordTokenizer.tokenize("Sud-Est");
        Assert.assertEquals(tokens.size(), 3);
        Assert.assertEquals("[Sud, -, Est]", tokens.toString());
        tokens = wordTokenizer.tokenize("Sud-est");
        Assert.assertEquals(tokens.size(), 1);
    }
}

