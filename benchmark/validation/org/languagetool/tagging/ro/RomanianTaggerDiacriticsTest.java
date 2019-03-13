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
package org.languagetool.tagging.ro;


import org.junit.Test;


/**
 * These tests are kept to make sure UTF-8 dictionaries are correctly read.
 * Prior to morfologik 1.1.4 some words containing diacritics were not correctly
 * returned.
 *
 * @author Ionu? P?duraru
 */
public class RomanianTaggerDiacriticsTest extends AbstractRomanianTaggerTest {
    /**
     * Prior to morfologik 1.1.4: For "merseser?m" the lemma is incorect: "mege"
     * instead of "merge". If the dictionary is used from
     * command-line(/fsa_morph -d ...), the correct lemma is returned.
     */
    @Test
    public void testTaggerMerseseram() throws Exception {
        // these tests are using "test_diacritics.dict"
        assertHasLemmaAndPos("f?cusem", "face", "004");
        assertHasLemmaAndPos("cu?itul", "cu?it", "002");
        // make sure lemma is correct (POS is hard-coded, not important)
        assertHasLemmaAndPos("merseser?m", "merge", "002");
    }

    @Test
    public void testTaggerCuscaCutit() throws Exception {
        // these tests are using "test_diacritics.dict"
        // all these are correct, they are here just to prove that "some" words
        // are correctly returned
        assertHasLemmaAndPos("cu?c?", "cu?c?", "001");
        assertHasLemmaAndPos("cu?it", "cu?it", "001");
        assertHasLemmaAndPos("cu?itul", "cu?it", "002");
    }
}

