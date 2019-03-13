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
package org.languagetool.synthesis.ro;


import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class RomanianSynthesizerTest {
    @Test
    public final void testSynthesizeStringString() throws IOException {
        RomanianSynthesizer synth = new RomanianSynthesizer();
        Assert.assertEquals(synth.synthesize(dummyToken("blablabla"), "blablabla").length, 0);
        // a alege
        // forma de infinitiv
        Assert.assertEquals("[alege]", Arrays.toString(synth.synthesize(dummyToken("alege"), "V000000f00")));
        // conjunctiv, pers a doua plural
        Assert.assertEquals("[alege?i]", Arrays.toString(synth.synthesize(dummyToken("alege"), "V0p2000cz0")));
        // a fi
        Assert.assertEquals("[fi]", Arrays.toString(synth.synthesize(dummyToken("fi"), "V000000f0f")));
        // indicativ prezent, pers a doua plural
        Assert.assertEquals("[sunte?i]", Arrays.toString(synth.synthesize(dummyToken("fi"), "V0p2000izf")));
        // indicativ prezent, pers a treia plural
        Assert.assertEquals("[sunt]", Arrays.toString(synth.synthesize(dummyToken("fi"), "V0p3000izf")));
        // indicativ prezent, pers ?nt?i plural
        Assert.assertEquals("[sunt]", Arrays.toString(synth.synthesize(dummyToken("fi"), "V0s1000izf")));
        // RegExp
        // indicativ prezent, pers a doua plural SAU indicativ prezent, pers a treia plural
        Assert.assertEquals("[sunte?i, sunt]", Arrays.toString(synth.synthesize(dummyToken("fi"), "V0p2000izf|V0p3000izf", true)));
        // diverse
        // indicativ, mai mult ca perfect, persoana ?nt?i, plural
        Assert.assertEquals("[merseser?m]", Arrays.toString(synth.synthesize(dummyToken("merge"), "V0p1000im0")));
        // indicativ, mai mult ca perfect, persoana ?nt?i, singular
        Assert.assertEquals("[mersesem]", Arrays.toString(synth.synthesize(dummyToken("merge"), "V0s1000im0")));
        Assert.assertEquals("[leg?tura]", Arrays.toString(synth.synthesize(dummyToken("leg?tur?"), "Sfs3aac000")));
        Assert.assertEquals("[leg?tur?]", Arrays.toString(synth.synthesize(dummyToken("leg?tur?"), "Sfs3anc000")));
        // user data (/ro/added.txt)
        Assert.assertEquals("[configura?i]", Arrays.toString(synth.synthesize(dummyToken("configura"), "V0p2000cz0")));// no reg exp

        Assert.assertEquals("[configura?i, configurezi]", Arrays.toString(synth.synthesize(dummyToken("configura"), "V0.2000cz0", true)));// using reg exp

        // assertEquals("[enum?r]", Arrays.toString(synth.synthesize(
        // dummyToken("enumera"), "V0s1000cz0")));
        // commented out as "a enumera" contains an extra form (.dict spelling error - "enum?m" instead of "enum?r"). To be fixed.
    }
}

