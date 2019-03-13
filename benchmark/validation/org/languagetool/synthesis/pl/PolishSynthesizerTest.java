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
package org.languagetool.synthesis.pl;


import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class PolishSynthesizerTest {
    @Test
    public final void testSynthesizeString() throws IOException {
        PolishSynthesizer synth = new PolishSynthesizer();
        Assert.assertEquals(synth.synthesize(dummyToken("blablabla"), "blablabla").length, 0);
        Assert.assertEquals("[Aarona]", Arrays.toString(synth.synthesize(dummyToken("Aaron"), "subst:sg:gen:m1")));
        Assert.assertEquals("[Abchazem]", Arrays.toString(synth.synthesize(dummyToken("Abchaz"), "subst:sg:inst:m1")));
        // assertEquals("[niedu?y]", Arrays.toString(synth.synthesize(dummyToken("niedu?y"), "adj:sg:nom:m:pos:neg")));
        Assert.assertEquals("[mia?a]", Arrays.toString(synth.synthesize(dummyToken("mie?"), "verb:praet:sg:f:ter:imperf:refl.nonrefl")));
        Assert.assertEquals("[brzydziej]", Arrays.toString(synth.synthesize(dummyToken("brzydko"), "adv:com")));
        // with regular expressions
        Assert.assertEquals("[tonera]", Arrays.toString(getSortedArray(synth.synthesize(dummyToken("toner"), "subst:sg:gen:m.*", true))));
        Assert.assertEquals("[niedu?ego, niedu?y]", Arrays.toString(getSortedArray(synth.synthesize(dummyToken("niedu?y"), "adj:sg.*(m[0-9]?|m.n):pos", true))));
        Assert.assertEquals("[mia?, mia?a, mia?am, mia?a?, mia?em, mia?e?, mia?o, mia?om, mia?o?]", Arrays.toString(getSortedArray(synth.synthesize(dummyToken("mie?"), ".*praet:sg.*", true))));
    }
}

