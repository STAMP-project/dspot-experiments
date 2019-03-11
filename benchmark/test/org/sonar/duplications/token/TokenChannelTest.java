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
package org.sonar.duplications.token;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.sonar.channel.CodeReader;


public class TokenChannelTest {
    @Test
    public void shouldConsume() {
        TokenChannel channel = new TokenChannel("ABC");
        TokenQueue output = Mockito.mock(TokenQueue.class);
        CodeReader codeReader = new CodeReader("ABCD");
        Assert.assertThat(channel.consume(codeReader, output), CoreMatchers.is(true));
        ArgumentCaptor<Token> token = ArgumentCaptor.forClass(Token.class);
        Mockito.verify(output).add(token.capture());
        Assert.assertThat(token.getValue(), CoreMatchers.is(new Token("ABC", 1, 0)));
        Mockito.verifyNoMoreInteractions(output);
        Assert.assertThat(codeReader.getLinePosition(), CoreMatchers.is(1));
        Assert.assertThat(codeReader.getColumnPosition(), CoreMatchers.is(3));
    }

    @Test
    public void shouldNormalize() {
        TokenChannel channel = new TokenChannel("ABC", "normalized");
        TokenQueue output = Mockito.mock(TokenQueue.class);
        CodeReader codeReader = new CodeReader("ABCD");
        Assert.assertThat(channel.consume(codeReader, output), CoreMatchers.is(true));
        ArgumentCaptor<Token> token = ArgumentCaptor.forClass(Token.class);
        Mockito.verify(output).add(token.capture());
        Assert.assertThat(token.getValue(), CoreMatchers.is(new Token("normalized", 1, 0)));
        Mockito.verifyNoMoreInteractions(output);
        Assert.assertThat(codeReader.getLinePosition(), CoreMatchers.is(1));
        Assert.assertThat(codeReader.getColumnPosition(), CoreMatchers.is(3));
    }

    @Test
    public void shouldNotConsume() {
        TokenChannel channel = new TokenChannel("ABC");
        TokenQueue output = Mockito.mock(TokenQueue.class);
        CodeReader codeReader = new CodeReader("123");
        Assert.assertThat(channel.consume(new CodeReader("123"), output), CoreMatchers.is(false));
        Mockito.verifyZeroInteractions(output);
        Assert.assertThat(codeReader.getLinePosition(), CoreMatchers.is(1));
        Assert.assertThat(codeReader.getColumnPosition(), CoreMatchers.is(0));
    }

    @Test
    public void shouldCorrectlyDeterminePositionWhenTokenSpansMultipleLines() {
        TokenChannel channel = new TokenChannel("AB\nC");
        TokenQueue output = Mockito.mock(TokenQueue.class);
        CodeReader codeReader = new CodeReader("AB\nCD");
        Assert.assertThat(channel.consume(codeReader, output), CoreMatchers.is(true));
        ArgumentCaptor<Token> token = ArgumentCaptor.forClass(Token.class);
        Mockito.verify(output).add(token.capture());
        Assert.assertThat(token.getValue(), CoreMatchers.is(new Token("AB\nC", 1, 0)));
        Mockito.verifyNoMoreInteractions(output);
        Assert.assertThat(codeReader.getLinePosition(), CoreMatchers.is(2));
        Assert.assertThat(codeReader.getColumnPosition(), CoreMatchers.is(1));
    }
}

