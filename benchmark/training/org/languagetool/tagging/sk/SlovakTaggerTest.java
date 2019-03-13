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
package org.languagetool.tagging.sk;


import java.io.IOException;
import org.junit.Test;
import org.languagetool.TestTools;
import org.languagetool.language.Slovak;
import org.languagetool.tokenizers.WordTokenizer;


public class SlovakTaggerTest {
    private SlovakTagger tagger;

    private WordTokenizer tokenizer;

    @Test
    public void testDictionary() throws IOException {
        TestTools.testDictionary(tagger, new Slovak());
    }

    @Test
    public void testTagger() throws IOException {
        TestTools.myAssert("Tu n?jdete vybran? ??sla a obsahy ?asopisu Kult?ra slova.", "Tu/[tu]J|Tu/[tu]PD|Tu/[tu]T -- n?jdete/[n?js?]VKdpb+ -- vybran?/[vybran?]Gtfp1x|vybran?/[vybran?]Gtfp4x|vybran?/[vybran?]Gtfp5x|vybran?/[vybran?]Gtip1x|vybran?/[vybran?]Gtip4x|vybran?/[vybran?]Gtip5x|vybran?/[vybran?]Gtnp1x|vybran?/[vybran?]Gtnp4x|vybran?/[vybran?]Gtnp5x|vybran?/[vybran?]Gtns1x|vybran?/[vybran?]Gtns4x|vybran?/[vybran?]Gtns5x -- ??sla/[??slo]SSnp1|??sla/[??slo]SSnp4|??sla/[??slo]SSnp5|??sla/[??slo]SSns2 -- a/[a]J|a/[a]O|a/[a]Q|a/[a]SUnp1|a/[a]SUnp2|a/[a]SUnp3|a/[a]SUnp4|a/[a]SUnp5|a/[a]SUnp6|a/[a]SUnp7|a/[a]SUns1|a/[a]SUns2|a/[a]SUns3|a/[a]SUns4|a/[a]SUns5|a/[a]SUns6|a/[a]SUns7|a/[a]T|a/[a]W|a/[as]W -- obsahy/[obsah]SSip1|obsahy/[obsah]SSip4|obsahy/[obsah]SSip5 -- ?asopisu/[?asopis]SSis2|?asopisu/[?asopis]SSis3 -- Kult?ra/[kult?ra]SSfs1|Kult?ra/[kult?ra]SSfs5 -- slova/[slovo]SSns2", tokenizer, tagger);
        TestTools.myAssert("blabla", "blabla/[null]null", tokenizer, tagger);
    }
}

