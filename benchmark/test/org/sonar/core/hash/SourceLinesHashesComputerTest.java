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
package org.sonar.core.hash;


import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class SourceLinesHashesComputerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void addLine_throws_NPE_is_line_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("line can not be null");
        new SourceLineHashesComputer(1).addLine(null);
    }

    @Test
    public void hash_of_empty_string_is_empty_string() {
        assertThat(SourceLinesHashesComputerTest.hashSingleLine("")).isEqualTo("");
    }

    @Test
    public void tab_and_spaces_are_ignored_from_hash() {
        assertThat(SourceLinesHashesComputerTest.hashSingleLine(" ")).isEqualTo("");
        assertThat(SourceLinesHashesComputerTest.hashSingleLine("\t")).isEqualTo("");
        assertThat(SourceLinesHashesComputerTest.hashSingleLine("\t \t \t\t  ")).isEqualTo("");
        String abHash = SourceLinesHashesComputerTest.hashSingleLine("ab");
        assertThat(SourceLinesHashesComputerTest.hashSingleLine("a b")).isEqualTo(abHash);
        assertThat(SourceLinesHashesComputerTest.hashSingleLine("a\tb")).isEqualTo(abHash);
        assertThat(SourceLinesHashesComputerTest.hashSingleLine("\t a\t \tb\t  ")).isEqualTo(abHash);
    }

    @Test
    public void hash_of_line_is_md5_of_UTF_char_array_as_an_hex_string() {
        String lineWithAccentAndSpace = "Yolo l?l?";
        assertThat(SourceLinesHashesComputerTest.hashSingleLine(lineWithAccentAndSpace)).isEqualTo(DigestUtils.md5Hex("Yolol?l?".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void getLineHashes_returns_line_hash_in_order_of_addLine_calls() {
        String line1 = "line 1";
        String line2 = "line 1 + 1";
        String line3 = "line 10 - 7";
        SourceLineHashesComputer underTest = new SourceLineHashesComputer();
        underTest.addLine(line1);
        underTest.addLine(line2);
        underTest.addLine(line3);
        assertThat(underTest.getLineHashes()).containsExactly(SourceLinesHashesComputerTest.hashSingleLine(line1), SourceLinesHashesComputerTest.hashSingleLine(line2), SourceLinesHashesComputerTest.hashSingleLine(line3));
    }
}

