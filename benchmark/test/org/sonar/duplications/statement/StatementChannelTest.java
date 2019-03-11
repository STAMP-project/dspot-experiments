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
package org.sonar.duplications.statement;


import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.duplications.statement.matcher.AnyTokenMatcher;
import org.sonar.duplications.statement.matcher.TokenMatcher;
import org.sonar.duplications.token.Token;
import org.sonar.duplications.token.TokenQueue;


public class StatementChannelTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNull() {
        StatementChannel.create(((TokenMatcher[]) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptEmpty() {
        StatementChannel.create(new TokenMatcher[]{  });
    }

    @Test
    public void shouldPushForward() {
        TokenQueue tokenQueue = Mockito.mock(TokenQueue.class);
        TokenMatcher matcher = Mockito.mock(TokenMatcher.class);
        List<Statement> output = Mockito.mock(List.class);
        StatementChannel channel = StatementChannel.create(matcher);
        Assert.assertThat(channel.consume(tokenQueue, output), CoreMatchers.is(false));
        ArgumentCaptor<List> matchedTokenList = ArgumentCaptor.forClass(List.class);
        Mockito.verify(matcher).matchToken(ArgumentMatchers.eq(tokenQueue), matchedTokenList.capture());
        Mockito.verifyNoMoreInteractions(matcher);
        Mockito.verify(tokenQueue).pushForward(matchedTokenList.getValue());
        Mockito.verifyNoMoreInteractions(tokenQueue);
        Mockito.verifyNoMoreInteractions(output);
    }

    @Test
    public void shouldCreateStatement() {
        Token token = new Token("a", 1, 1);
        TokenQueue tokenQueue = Mockito.spy(new TokenQueue(Arrays.asList(token)));
        TokenMatcher matcher = Mockito.spy(new AnyTokenMatcher());
        StatementChannel channel = StatementChannel.create(matcher);
        List<Statement> output = Mockito.mock(List.class);
        Assert.assertThat(channel.consume(tokenQueue, output), CoreMatchers.is(true));
        Mockito.verify(matcher).matchToken(ArgumentMatchers.eq(tokenQueue), ArgumentMatchers.anyList());
        Mockito.verifyNoMoreInteractions(matcher);
        ArgumentCaptor<Statement> statement = ArgumentCaptor.forClass(Statement.class);
        Mockito.verify(output).add(statement.capture());
        Assert.assertThat(statement.getValue().getValue(), CoreMatchers.is("a"));
        Assert.assertThat(statement.getValue().getStartLine(), CoreMatchers.is(1));
        Assert.assertThat(statement.getValue().getEndLine(), CoreMatchers.is(1));
        Mockito.verifyNoMoreInteractions(output);
    }

    @Test
    public void shouldNotCreateStatement() {
        TokenQueue tokenQueue = Mockito.spy(new TokenQueue(Arrays.asList(new Token("a", 1, 1))));
        TokenMatcher matcher = Mockito.spy(new AnyTokenMatcher());
        StatementChannel channel = StatementChannel.create(matcher);
        List<Statement> output = Mockito.mock(List.class);
        Assert.assertThat(channel.consume(tokenQueue, output), CoreMatchers.is(true));
        Mockito.verify(matcher).matchToken(ArgumentMatchers.eq(tokenQueue), ArgumentMatchers.anyList());
        Mockito.verifyNoMoreInteractions(matcher);
        Mockito.verify(output).add(ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(output);
    }
}

