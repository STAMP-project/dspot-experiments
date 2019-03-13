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


import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TokenQueueTest {
    TokenQueue tokenQueue;

    @Test
    public void shouldPeekToken() {
        Token token = tokenQueue.peek();
        Assert.assertThat(token, CoreMatchers.is(new Token("a", 1, 0)));
        Assert.assertThat(tokenQueue.size(), CoreMatchers.is(3));
    }

    @Test
    public void shouldPollToken() {
        Token token = tokenQueue.poll();
        Assert.assertThat(token, CoreMatchers.is(new Token("a", 1, 0)));
        Assert.assertThat(tokenQueue.size(), CoreMatchers.is(2));
    }

    @Test
    public void shouldPushTokenAtBegining() {
        Token pushedToken = new Token("push", 1, 0);
        List<Token> pushedTokenList = new ArrayList<>();
        pushedTokenList.add(pushedToken);
        tokenQueue.pushForward(pushedTokenList);
        Assert.assertThat(tokenQueue.peek(), CoreMatchers.is(pushedToken));
        Assert.assertThat(tokenQueue.size(), CoreMatchers.is(4));
    }
}

