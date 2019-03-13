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
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.duplications.statement.matcher.TokenMatcher;
import org.sonar.duplications.token.Token;
import org.sonar.duplications.token.TokenQueue;


public class StatementChannelDisptacherTest {
    @Test(expected = IllegalStateException.class)
    public void shouldThrowAnException() {
        TokenMatcher tokenMatcher = Mockito.mock(TokenMatcher.class);
        StatementChannel channel = StatementChannel.create(tokenMatcher);
        StatementChannelDisptacher dispatcher = new StatementChannelDisptacher(Arrays.asList(channel));
        TokenQueue tokenQueue = Mockito.mock(TokenQueue.class);
        Mockito.when(tokenQueue.peek()).thenReturn(new Token("a", 1, 0)).thenReturn(null);
        List<Statement> statements = Mockito.mock(List.class);
        dispatcher.consume(tokenQueue, statements);
    }

    @Test
    public void shouldConsume() {
        TokenMatcher tokenMatcher = Mockito.mock(TokenMatcher.class);
        Mockito.when(tokenMatcher.matchToken(ArgumentMatchers.any(TokenQueue.class), ArgumentMatchers.anyList())).thenReturn(true);
        StatementChannel channel = StatementChannel.create(tokenMatcher);
        StatementChannelDisptacher dispatcher = new StatementChannelDisptacher(Arrays.asList(channel));
        TokenQueue tokenQueue = Mockito.mock(TokenQueue.class);
        Mockito.when(tokenQueue.peek()).thenReturn(new Token("a", 1, 0)).thenReturn(null);
        List<Statement> statements = Mockito.mock(List.class);
        Assert.assertThat(dispatcher.consume(tokenQueue, statements), CoreMatchers.is(true));
        Mockito.verify(tokenQueue, Mockito.times(2)).peek();
        Mockito.verifyNoMoreInteractions(tokenQueue);
        Mockito.verifyNoMoreInteractions(statements);
    }
}

