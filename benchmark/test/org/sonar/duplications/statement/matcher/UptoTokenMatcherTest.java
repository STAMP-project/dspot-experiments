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
package org.sonar.duplications.statement.matcher;


import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.duplications.token.Token;
import org.sonar.duplications.token.TokenQueue;


public class UptoTokenMatcherTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNull() {
        new UptoTokenMatcher(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptEmpty() {
        new UptoTokenMatcher(new String[]{  });
    }

    @Test
    public void shouldMatch() {
        Token t1 = new Token("a", 1, 1);
        Token t2 = new Token(";", 2, 1);// should stop on this token

        Token t3 = new Token(";", 3, 1);
        TokenQueue tokenQueue = Mockito.spy(new TokenQueue(Arrays.asList(t1, t2, t3)));
        List<Token> output = Mockito.mock(List.class);
        UptoTokenMatcher matcher = new UptoTokenMatcher(new String[]{ ";" });
        Assert.assertThat(matcher.matchToken(tokenQueue, output), CoreMatchers.is(true));
        Mockito.verify(tokenQueue, Mockito.times(2)).poll();
        Mockito.verify(tokenQueue).peek();
        Mockito.verifyNoMoreInteractions(tokenQueue);
        Mockito.verify(output).add(t1);
        Mockito.verify(output).add(t2);
        Mockito.verifyNoMoreInteractions(output);
    }

    @Test
    public void shouldMatchAnyOfProvidedTokens() {
        Token t1 = new Token("a", 1, 1);
        Token t2 = new Token("{", 2, 1);
        Token t3 = new Token("b", 3, 1);
        Token t4 = new Token("}", 4, 1);
        TokenQueue tokenQueue = Mockito.spy(new TokenQueue(Arrays.asList(t1, t2, t3, t4)));
        List<Token> output = Mockito.mock(List.class);
        UptoTokenMatcher matcher = new UptoTokenMatcher(new String[]{ "{", "}" });
        Assert.assertThat(matcher.matchToken(tokenQueue, output), CoreMatchers.is(true));
        Assert.assertThat(matcher.matchToken(tokenQueue, output), CoreMatchers.is(true));
        Mockito.verify(tokenQueue, Mockito.times(4)).poll();
        Mockito.verify(tokenQueue, Mockito.times(2)).peek();
        Mockito.verifyNoMoreInteractions(tokenQueue);
        Mockito.verify(output).add(t1);
        Mockito.verify(output).add(t2);
        Mockito.verify(output).add(t3);
        Mockito.verify(output).add(t4);
        Mockito.verifyNoMoreInteractions(output);
    }

    @Test
    public void shouldNotMatch() {
        Token t1 = new Token("a", 1, 1);
        Token t2 = new Token("b", 2, 1);
        TokenQueue tokenQueue = Mockito.spy(new TokenQueue(Arrays.asList(t1, t2)));
        List<Token> output = Mockito.mock(List.class);
        UptoTokenMatcher matcher = new UptoTokenMatcher(new String[]{ ";" });
        Assert.assertThat(matcher.matchToken(tokenQueue, output), CoreMatchers.is(false));
        Mockito.verify(tokenQueue, Mockito.times(2)).poll();
        Mockito.verify(tokenQueue, Mockito.times(2)).peek();
        Mockito.verifyNoMoreInteractions(tokenQueue);
        Mockito.verify(output).add(t1);
        Mockito.verify(output).add(t2);
        Mockito.verifyNoMoreInteractions(output);
    }
}

