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
package org.sonar.scanner.scan.filesystem;


import Result.INVALID;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.ByteOrderMark;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.scanner.scan.filesystem.CharsetValidation.Result;
import org.sonar.scanner.scan.filesystem.CharsetValidation.Validation;


public class ByteCharsetDetectorTest {
    private CharsetValidation validation;

    private ByteCharsetDetector charsets;

    @Test
    public void detectBOM() throws IOException, URISyntaxException {
        byte[] b = ByteOrderMark.UTF_16BE.getBytes();
        assertThat(charsets.detectBOM(b)).isEqualTo(ByteOrderMark.UTF_16BE);
        assertThat(charsets.detectBOM(readFile("UTF-8"))).isEqualTo(ByteOrderMark.UTF_8);
        assertThat(charsets.detectBOM(readFile("UTF-16BE"))).isEqualTo(ByteOrderMark.UTF_16BE);
        assertThat(charsets.detectBOM(readFile("UTF-16LE"))).isEqualTo(ByteOrderMark.UTF_16LE);
        assertThat(charsets.detectBOM(readFile("UTF-32BE"))).isEqualTo(ByteOrderMark.UTF_32BE);
        assertThat(charsets.detectBOM(readFile("UTF-32LE"))).isEqualTo(ByteOrderMark.UTF_32LE);
    }

    @Test
    public void tryUTF8First() {
        Mockito.when(validation.isUTF8(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyBoolean())).thenReturn(Result.newValid(StandardCharsets.UTF_8));
        assertThat(charsets.detect(new byte[1])).isEqualTo(StandardCharsets.UTF_8);
    }

    @Test
    public void tryUTF16heuristics() {
        Mockito.when(validation.isUTF8(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyBoolean())).thenReturn(INVALID);
        Mockito.when(validation.isUTF16(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyBoolean())).thenReturn(Result.newValid(StandardCharsets.UTF_16));
        Mockito.when(validation.isValidUTF16(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyBoolean())).thenReturn(true);
        assertThat(charsets.detect(new byte[1])).isEqualTo(StandardCharsets.UTF_16);
    }

    @Test
    public void failAll() {
        Mockito.when(validation.isUTF8(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyBoolean())).thenReturn(INVALID);
        Mockito.when(validation.isUTF16(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyBoolean())).thenReturn(new Result(Validation.MAYBE, null));
        Mockito.when(validation.isValidWindows1252(ArgumentMatchers.any(byte[].class))).thenReturn(INVALID);
        assertThat(charsets.detect(new byte[1])).isEqualTo(null);
    }

    @Test
    public void failAnsii() {
        Mockito.when(validation.isUTF8(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyBoolean())).thenReturn(new Result(Validation.MAYBE, null));
        Mockito.when(validation.isUTF16(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyBoolean())).thenReturn(Result.newValid(StandardCharsets.UTF_16));
        Mockito.when(validation.isValidUTF16(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyBoolean())).thenReturn(true);
        assertThat(charsets.detect(new byte[1])).isEqualTo(null);
    }

    @Test
    public void tryUserAnsii() {
        Mockito.when(validation.isUTF8(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyBoolean())).thenReturn(new Result(Validation.MAYBE, null));
        Mockito.when(validation.isUTF16(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyBoolean())).thenReturn(Result.newValid(StandardCharsets.UTF_16));
        Mockito.when(validation.isValidUTF16(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyBoolean())).thenReturn(true);
        Mockito.when(validation.tryDecode(ArgumentMatchers.any(byte[].class), ArgumentMatchers.eq(StandardCharsets.ISO_8859_1))).thenReturn(true);
        charsets = new ByteCharsetDetector(validation, StandardCharsets.ISO_8859_1);
        assertThat(charsets.detect(new byte[1])).isEqualTo(StandardCharsets.ISO_8859_1);
    }

    @Test
    public void tryOtherUserCharset() {
        Mockito.when(validation.isUTF8(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyBoolean())).thenReturn(INVALID);
        Mockito.when(validation.isUTF16(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyBoolean())).thenReturn(new Result(Validation.MAYBE, null));
        Mockito.when(validation.tryDecode(ArgumentMatchers.any(byte[].class), ArgumentMatchers.eq(StandardCharsets.ISO_8859_1))).thenReturn(true);
        charsets = new ByteCharsetDetector(validation, StandardCharsets.ISO_8859_1);
        assertThat(charsets.detect(new byte[1])).isEqualTo(StandardCharsets.ISO_8859_1);
    }

    @Test
    public void invalidBOM() {
        byte[] b1 = new byte[]{ ((byte) (255)), ((byte) (255)) };
        assertThat(charsets.detectBOM(b1)).isNull();
        // not enough bytes
        byte[] b2 = new byte[]{ ((byte) (254)) };
        assertThat(charsets.detectBOM(b2)).isNull();
        // empty
        byte[] b3 = new byte[0];
        assertThat(charsets.detectBOM(b3)).isNull();
    }

    @Test
    public void windows1252() throws IOException, URISyntaxException {
        ByteCharsetDetector detector = new ByteCharsetDetector(new CharsetValidation(), StandardCharsets.UTF_8);
        assertThat(detector.detect(readFile("windows-1252"))).isEqualTo(Charset.forName("Windows-1252"));
    }
}

