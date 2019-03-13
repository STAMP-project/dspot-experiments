/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2015 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.rules.de;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class LineExpanderTest {
    private final LineExpander exp = new LineExpander();

    @Test
    public void testExpansion() {
        Assert.assertThat(expand(""), CoreMatchers.is("[]"));
        Assert.assertThat(expand("Das"), CoreMatchers.is("[Das]"));
        Assert.assertThat(expand("Tisch/E"), CoreMatchers.is("[Tisch, Tische]"));
        Assert.assertThat(expand("Tische/N"), CoreMatchers.is("[Tische, Tischen]"));
        Assert.assertThat(expand("Auto/S"), CoreMatchers.is("[Auto, Autos]"));
        Assert.assertThat(expand("klein/A"), CoreMatchers.is("[klein, kleine, kleiner, kleines, kleinen, kleinem]"));
        Assert.assertThat(expand("x/NSE"), CoreMatchers.is("[x, xn, xs, xe]"));
        Assert.assertThat(expand("x/NA"), CoreMatchers.is("[x, xn, xe, xer, xes, xen, xem]"));
        Assert.assertThat(expand("Das  #foo"), CoreMatchers.is("[Das]"));
        Assert.assertThat(expand("Tisch/E  #bla #foo"), CoreMatchers.is("[Tisch, Tische]"));
    }
}

