/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.duplications.block;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ByteArrayTest {
    @Test
    public void shouldCreateFromInt() {
        int value = 318735379;
        ByteArray byteArray = new ByteArray(value);
        Assert.assertThat(byteArray.toString(), CoreMatchers.is(Integer.toHexString(value)));
    }

    @Test
    public void shouldCreateFromLong() {
        long value = 1368958030029682841L;
        ByteArray byteArray = new ByteArray(value);
        Assert.assertThat(byteArray.toString(), CoreMatchers.is(Long.toHexString(value)));
    }

    @Test
    public void shouldCreateFromHexString() {
        String value = "12FF841344567899";
        ByteArray byteArray = new ByteArray(value);
        Assert.assertThat(byteArray.toString(), CoreMatchers.is(value.toLowerCase()));
    }

    @Test
    public void shouldCreateFromIntArray() {
        ByteArray byteArray = new ByteArray(new int[]{ 68295046 });
        Assert.assertThat(byteArray.toString(), CoreMatchers.is("04121986"));
    }

    @Test
    public void shouldConvertToIntArray() {
        // number of bytes is enough to create exactly one int (4 bytes)
        ByteArray byteArray = new ByteArray(new byte[]{ 4, 18, 25, ((byte) (134)) });
        Assert.assertThat(byteArray.toIntArray(), CoreMatchers.is(new int[]{ 68295046 }));
        // number of bytes is more than 4, but less than 8, so anyway 2 ints
        byteArray = new ByteArray(new byte[]{ 0, 0, 0, 0, 49 });
        Assert.assertThat(byteArray.toIntArray(), CoreMatchers.is(new int[]{ 0, 822083584 }));
    }
}

