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


import java.io.IOException;
import org.junit.Test;
import org.languagetool.TestTools;


/**
 *
 *
 * @author Ionu? P?duraru
 */
public class RomanianTaggerTest extends AbstractRomanianTaggerTest {
    /**
     * First we test if the tagger works fine with single words
     */
    @Test
    public void testTaggerMerge() throws Exception {
        // merge - verb indicativ imperfect, persoana ?nt?i, singular
        assertHasLemmaAndPos("mergeam", "merge", "V0s1000ii0");
        // merge - verb indicativ imperfect, persoana ?nt?i, plural
        assertHasLemmaAndPos("mergeam", "merge", "V0p1000ii0");
        // merge - verb indicativ imperfect, persoana ?nt?i, plural
    }

    /**
     * <code>merseser?m</code> had some problems (incorrect lemma - mege -
     * missing "r")
     */
    @Test
    public void testTaggerMerseseram() throws Exception {
        // first make sure lemma is correct (ignore POS)
        assertHasLemmaAndPos("merseser?m", "merge", null);
        // now that lemma is correct, also check POS
        assertHasLemmaAndPos("merseser?m", "merge", "V0p1000im0");
        TestTools.myAssert("merseser?m", "merseser?m/[merge]V0p1000im0", getTokenizer(), getTagger());
    }

    /**
     * A special word: a fi (to be) - eu sunt (i am) + ei sunt (they are)
     */
    @Test
    public void testTagger_Fi() throws Exception {
        // fi - verb indicativ prezent, persoana ?nt?i, singular
        assertHasLemmaAndPos("sunt", "fi", "V0s1000izf");
        // fi verb indicativ prezent, persoana a treia, plural
        assertHasLemmaAndPos("sunt", "fi", "V0p3000izf");
    }

    @Test
    public void testTaggerUserDict() throws Exception {
        assertHasLemmaAndPos("configura?i", "configura", "V0p2000cz0");// de ad?ugat formele pentru infinitiv ?i participiu

        // to be updated when the words from added.txt are moved to romanian.dict
    }

    /**
     * the big picture: test is tagger performs well with a sentence
     */
    @Test
    public void testTagger() throws IOException {
        TestTools.myAssert("Cartea este frumoas?.", "Cartea/[carte]Sfs3aac000 -- este/[fi]V0s3000izb -- frumoas?/[frumos]Afs3an0000", getTokenizer(), getTagger());
    }
}

