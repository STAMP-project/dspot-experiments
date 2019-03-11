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
import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.duplications.token.Token;
import org.sonar.duplications.token.TokenQueue;


public class ForgetLastTokenMatcherTest {
    @Test
    public void shouldMatch() {
        TokenQueue tokenQueue = Mockito.spy(new TokenQueue());
        Token token = new Token("a", 0, 0);
        List<Token> output = new java.util.ArrayList(Arrays.asList(token));
        ForgetLastTokenMatcher matcher = new ForgetLastTokenMatcher();
        Assert.assertThat(matcher.matchToken(tokenQueue, output), CoreMatchers.is(true));
        Assert.assertThat(output.size(), CoreMatchers.is(0));
        Mockito.verify(tokenQueue).pushForward(ArgumentMatchers.eq(Collections.singletonList(token)));
    }
}

