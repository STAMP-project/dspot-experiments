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
package org.languagetool.dev.wordsimilarity;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class GermanQwertzKeyboardDistanceTest {
    @Test
    public void testDistance() {
        GermanQwertzKeyboardDistance distance = new GermanQwertzKeyboardDistance();
        Assert.assertThat(distance.getDistance('q', 'q'), Is.is(0.0F));
        Assert.assertThat(distance.getDistance('q', 'w'), Is.is(1.0F));
        Assert.assertThat(distance.getDistance('q', 'p'), Is.is(9.0F));
        Assert.assertThat(distance.getDistance('q', 'a'), Is.is(1.0F));
        Assert.assertThat(distance.getDistance('t', 'g'), Is.is(1.0F));
        Assert.assertThat(distance.getDistance('a', 's'), Is.is(1.0F));
        Assert.assertThat(distance.getDistance('a', 'g'), Is.is(4.0F));
        Assert.assertThat(distance.getDistance('y', 'x'), Is.is(1.0F));
        Assert.assertThat(distance.getDistance('c', 'n'), Is.is(3.0F));
        Assert.assertThat(distance.getDistance('q', 'y'), Is.is(2.0F));
        Assert.assertThat(distance.getDistance('q', 'm'), Is.is(8.0F));
        Assert.assertThat(distance.getDistance('p', '?'), Is.is(2.0F));
        Assert.assertThat(distance.getDistance('o', '?'), Is.is(3.0F));
        // uppercase:
        Assert.assertThat(distance.getDistance('C', 'n'), Is.is(3.0F));
        Assert.assertThat(distance.getDistance('c', 'N'), Is.is(3.0F));
        Assert.assertThat(distance.getDistance('C', 'N'), Is.is(3.0F));
    }
}

