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
package org.languagetool.synthesis.uk;


import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class UkrainianSynthesizerTest {
    @Test
    public final void testSynthesizeString() throws IOException {
        UkrainianSynthesizer synth = new UkrainianSynthesizer();
        Assert.assertEquals(synth.synthesize(dummyToken("???????????"), "???????????").length, 0);
        // assertEquals("[??????]", Arrays.toString(synth.synthesize(dummyToken("??????"), "noun:m:v_rod")));
        Assert.assertEquals("[?????]", Arrays.toString(synth.synthesize(dummyToken("?????"), "noun:inanim:n:v_rod")));
        // with regular expressions
        Assert.assertEquals("[????????]", Arrays.toString(getSortedArray(synth.synthesize(dummyToken("????????"), "adj:f:v_rod.*", true))));
        Assert.assertEquals("[????????]", Arrays.toString(getSortedArray(synth.synthesize(dummyToken("????????"), "adj:f:v_rod:compb", true))));
        Assert.assertEquals("[????????, ????????, ?????????, ????????]", Arrays.toString(getSortedArray(synth.synthesize(dummyToken("????????"), "adj:.:v_oru:compb", true))));
    }
}

