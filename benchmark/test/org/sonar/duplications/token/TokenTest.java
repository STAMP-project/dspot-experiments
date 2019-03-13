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


public class TokenTest {
    @Test
    public void shouldBeEqual() {
        Token firstToken = new Token("MyValue", 1, 3);
        Token secondToken = new Token("MyValue", 1, 3);
        Assert.assertThat(firstToken, CoreMatchers.is(secondToken));
    }

    @Test
    public void shouldNotBeEqual() {
        Token firstToken = new Token("MyValue", 1, 3);
        Token secondToken = new Token("MySecondValue", 1, 3);
        Token thirdToken = new Token("MyValue", 3, 3);
        Assert.assertThat(firstToken, CoreMatchers.not(CoreMatchers.is(secondToken)));
        Assert.assertThat(firstToken, CoreMatchers.not(CoreMatchers.is(thirdToken)));
    }
}

