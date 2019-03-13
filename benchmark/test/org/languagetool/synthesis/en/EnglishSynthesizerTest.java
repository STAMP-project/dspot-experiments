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
package org.languagetool.synthesis.en;


import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class EnglishSynthesizerTest {
    @Test
    public void testSynthesizeStringString() throws IOException {
        EnglishSynthesizer synth = new EnglishSynthesizer();
        Assert.assertEquals(synth.synthesize(dummyToken("blablabla"), "blablabla").length, 0);
        Assert.assertEquals("[was, were]", Arrays.toString(synth.synthesize(dummyToken("be"), "VBD")));
        Assert.assertEquals("[presidents]", Arrays.toString(synth.synthesize(dummyToken("president"), "NNS")));
        Assert.assertEquals("[tested]", Arrays.toString(synth.synthesize(dummyToken("test"), "VBD")));
        Assert.assertEquals("[tested]", Arrays.toString(synth.synthesize(dummyToken("test"), "VBD", false)));
        // with regular expressions
        Assert.assertEquals("[tested]", Arrays.toString(synth.synthesize(dummyToken("test"), "VBD", true)));
        Assert.assertEquals("[tested, testing]", Arrays.toString(synth.synthesize(dummyToken("test"), "VBD|VBG", true)));
        // with special indefinite article
        Assert.assertEquals("[a university, the university]", Arrays.toString(synth.synthesize(dummyToken("university"), "+DT", false)));
        Assert.assertEquals("[an hour, the hour]", Arrays.toString(synth.synthesize(dummyToken("hour"), "+DT", false)));
        Assert.assertEquals("[an hour]", Arrays.toString(synth.synthesize(dummyToken("hour"), "+INDT", false)));
        // indefinite article and other changes...
        Assert.assertEquals("[an hour]", Arrays.toString(synth.synthesize(dummyToken("hours", "hour"), "NN\\+INDT", true)));
        // indefinite article and other changes...
        Assert.assertEquals("[the hour]", Arrays.toString(synth.synthesize(dummyToken("hours", "hour"), "NN\\+DT", true)));
    }
}

