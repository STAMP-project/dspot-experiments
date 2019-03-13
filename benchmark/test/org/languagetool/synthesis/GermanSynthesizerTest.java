/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2013 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.synthesis;


import java.io.IOException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class GermanSynthesizerTest {
    private final GermanSynthesizer synthesizer = new GermanSynthesizer();

    @Test
    public void testSynthesize() throws IOException {
        Assert.assertThat(synth("?u?erung", "SUB:NOM:PLU:FEM"), Is.is("[?u?erungen]"));
        Assert.assertThat(synth("?u?erung", "SUB:NOM:PLU:MAS"), Is.is("[]"));
        Assert.assertThat(synth("Haus", "SUB:AKK:PLU:NEU"), Is.is("[H?user]"));
        Assert.assertThat(synth("Haus", ".*", true), Is.is("[H?user, Haus, H?usern, Haus, Hause, H?user, Hauses, H?user, Haus]"));
    }

    @Test
    public void testSynthesizeCompounds() throws IOException {
        Assert.assertThat(synth("Regelsystem", "SUB:NOM:PLU:NEU"), Is.is("[Regelsysteme]"));
        Assert.assertThat(synth("Regelsystem", "SUB:DAT:PLU:NEU"), Is.is("[Regelsystemen]"));
        Assert.assertThat(synth("Regelsystem", ".*:PLU:.*", true), Is.is("[Regelsysteme, Regelsystemen]"));
        Assert.assertThat(synth("K?hlschrankversuch", ".*:PLU:.*", true), Is.is("[K?hlschrankversuche, K?hlschrankversuchen]"));
    }

    @Test
    public void testMorfologikBug() throws IOException {
        // see https://github.com/languagetool-org/languagetool/issues/586
        Assert.assertThat(synth("anfragen", "VER:1:PLU:KJ1:SFT:NEB"), Is.is("[anfragen, Anfragen]"));
    }
}

