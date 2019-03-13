/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2012 Jaume Ortol? i Font
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
package org.languagetool.synthesis.ca;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class CatalanSynthesizerTest {
    private final CatalanSynthesizer synth = new CatalanSynthesizer();

    @Test
    public final void testSynthesizeStringString() throws IOException {
        Assert.assertEquals(0, synth.synthesize(dummyToken("blablabla"), "blablabla").length);
        Assert.assertEquals("[nostres]", synth("nostre", "PX1CP0P0"));
        Assert.assertEquals("[presidents]", synth("president", "NCMP000"));
        Assert.assertEquals("[comprovat]", synth("comprovar", "VMP00SM.?"));
        Assert.assertEquals("[arribe, arribi]", synth("arribar", "VMSP3S00"));
        Assert.assertEquals("[arribe, arribi]", synthRegex("arribar", "VMSP3S.0"));
        Assert.assertEquals("[alb?rxics]", synthRegex("alb?rxic", "NCMP000"));
        // with regular expressions:
        Assert.assertEquals("[comprovades, comprovats, comprovada, comprovat]", synthRegex("comprovar", "V.P.*"));
        Assert.assertEquals("[contestant, contestar]", synthRegex("contestar", "VM[GN]0000.?"));
        // with special definite article:
        Assert.assertEquals("[les universitats, la universitat]", synthNonRegex("universitat", "DT"));
        Assert.assertEquals("[les ?niques, l'?nica, els ?nics, l'?nic]", synthNonRegex("?nic", "DT"));
        Assert.assertEquals("[per les ?niques, per l'?nica, pels ?nics, per l'?nic]", synthNonRegex("?nic", "DTper"));
    }
}

