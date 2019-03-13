/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2006 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.tagging.pl;


import java.io.IOException;
import org.junit.Test;
import org.languagetool.TestTools;
import org.languagetool.language.Polish;
import org.languagetool.tokenizers.WordTokenizer;


public class PolishTaggerTest {
    private PolishTagger tagger;

    private WordTokenizer tokenizer;

    @Test
    public void testDictionary() throws IOException {
        TestTools.testDictionary(tagger, new Polish());
    }

    @Test
    public void testTagger() throws IOException {
        TestTools.myAssert("To jest du?y dom.", "To/[ten]adj:sg:acc:n1.n2:pos|To/[ten]adj:sg:nom.voc:n1.n2:pos|To/[to]conj|To/[to]qub|To/[to]subst:sg:acc:n2|To/[to]subst:sg:nom:n2 -- jest/[by?]verb:fin:sg:ter:imperf:nonrefl -- du?y/[du?y]adj:sg:acc:m3:pos|du?y/[du?y]adj:sg:nom.voc:m1.m2.m3:pos -- dom/[dom]subst:sg:acc:m3|dom/[dom]subst:sg:nom:m3", tokenizer, tagger);
        TestTools.myAssert("Krowa pasie si? na pastwisku.", "Krowa/[krowa]subst:sg:nom:f -- pasie/[pas]subst:sg:loc:m3|pasie/[pas]subst:sg:voc:m3|pasie/[pa??]verb:fin:sg:ter:imperf:refl.nonrefl -- si?/[si?]qub|si?/[si?]siebie:acc:nakc|si?/[si?]siebie:gen:nakc -- na/[na]interj|na/[na]prep:acc|na/[na]prep:loc -- pastwisku/[pastwisko]subst:sg:dat:n2|pastwisku/[pastwisko]subst:sg:loc:n2", tokenizer, tagger);
        TestTools.myAssert("blablabla", "blablabla/[null]null", tokenizer, tagger);
    }
}

