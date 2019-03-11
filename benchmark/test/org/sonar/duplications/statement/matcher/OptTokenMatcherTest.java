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


import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.duplications.token.Token;
import org.sonar.duplications.token.TokenQueue;


public class OptTokenMatcherTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNull() {
        new OptTokenMatcher(null);
    }

    @Test
    public void shouldMatch() {
        TokenQueue tokenQueue = Mockito.spy(new TokenQueue());
        TokenMatcher delegate = Mockito.mock(TokenMatcher.class);
        OptTokenMatcher matcher = new OptTokenMatcher(delegate);
        List<Token> output = Mockito.mock(List.class);
        Assert.assertThat(matcher.matchToken(tokenQueue, output), CoreMatchers.is(true));
        Mockito.verify(delegate).matchToken(tokenQueue, output);
        Mockito.verifyNoMoreInteractions(delegate);
        Mockito.verifyNoMoreInteractions(tokenQueue);
        Mockito.verifyNoMoreInteractions(output);
    }
}

