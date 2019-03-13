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
package org.languagetool.tagging.sv;


import java.io.IOException;
import org.junit.Test;
import org.languagetool.TestTools;
import org.languagetool.language.Swedish;
import org.languagetool.tokenizers.WordTokenizer;


public class SwedishTaggerTest {
    private SwedishTagger tagger;

    private WordTokenizer tokenizer;

    @Test
    public void testDictionary() throws IOException {
        TestTools.testDictionary(tagger, new Swedish());
    }

    @Test
    public void testTagger() throws IOException {
        TestTools.myAssert("Det ?r nog b?st att du f?r en klubba till", "Det/[det]PN -- ?r/[vara]VB:PRS -- nog/[nog]AB -- b?st/[bra]JJ:S|b?st/[b?st]AB|b?st/[god]JJ:S -- att/[att]KN -- du/[du]PN -- f?r/[f?]VB:PRS|f?r/[f?r]NN:OF:PLU:NOM:NEU|f?r/[f?r]NN:OF:SIN:NOM:NEU -- en/[en]NN:OF:SIN:NOM:UTR|en/[en]PN|en/[passant]en passant NN:OF:SIN:NOM:UTR|en/[passanten]en passant NN:BF:SIN:NOM:UTR|en/[passantens]en passant NN:BF:SIN:GEN:UTR|en/[passanter]en passant NN:OF:PLU:NOM:UTR|en/[passanterna]en passant NN:BF:PLU:NOM:UTR|en/[passanternas]en passant NN:BF:PLU:GEN:UTR|en/[passanters]en passant NN:OF:PLU:GEN:UTR|en/[passants]en passant NN:OF:SIN:GEN:UTR -- klubba/[klubba]NN:OF:SIN:NOM:UTR|klubba/[klubba]VB:IMP|klubba/[klubba]VB:INF -- till/[till]AB|till/[till]PP", tokenizer, tagger);
        TestTools.myAssert("Du menar sannolikt \"massera\" om du inte skriver om masarnas era f\u00f6rst\u00e5s.", "Du/[du]PN -- menar/[mena]VB:PRS -- sannolikt/[sannolik]JJ:PN|sannolikt/[sannolikt]AB -- massera/[massera]VB:IMP|massera/[massera]VB:INF -- om/[om]AB|om/[om]KN|om/[om]PP -- du/[du]PN -- inte/[inte]AB -- skriver/[skriva]VB:PRS -- om/[om]AB|om/[om]KN|om/[om]PP -- masarnas/[mas]NN:BF:PLU:GEN:UTR -- era/[era]NN:OF:SIN:NOM:UTR|era/[era]PN -- f?rst?s/[f?rst?]VB:INF:PF|f?rst?s/[f?rst?]VB:PRS:PF|f?rst?s/[f?rst?s]AB", tokenizer, tagger);
    }
}

