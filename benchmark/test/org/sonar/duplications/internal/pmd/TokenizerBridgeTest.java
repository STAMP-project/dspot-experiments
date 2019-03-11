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
package org.sonar.duplications.internal.pmd;


import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import net.sourceforge.pmd.cpd.TokenEntry;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.sonar.api.batch.sensor.cpd.internal.TokensLine;


public class TokenizerBridgeTest {
    private TokenizerBridge bridge;

    @Test
    public void shouldClearCacheInTokenEntry() {
        bridge.chunk("file.txt", new InputStreamReader(new ByteArrayInputStream(new byte[0]), StandardCharsets.UTF_8));
        TokenEntry token = new TokenEntry("image", "srcId", 0);
        Assert.assertThat(token.getIndex(), CoreMatchers.is(0));
        Assert.assertThat(token.getIdentifier(), CoreMatchers.is(1));
    }

    @Test
    public void test() {
        // To be sure that token index will be relative to file - run twice:
        bridge.chunk("file.txt", new InputStreamReader(new ByteArrayInputStream(new byte[0]), StandardCharsets.UTF_8));
        List<TokensLine> lines = bridge.chunk("file.txt", new InputStreamReader(new ByteArrayInputStream(new byte[0]), StandardCharsets.UTF_8));
        Assert.assertThat(lines.size(), CoreMatchers.is(3));
        TokensLine line = lines.get(0);
        // 2 tokens on 1 line
        Assert.assertThat(line.getStartUnit(), CoreMatchers.is(1));
        Assert.assertThat(line.getEndUnit(), CoreMatchers.is(2));
        Assert.assertThat(line.getStartLine(), CoreMatchers.is(1));
        Assert.assertThat(line.getEndLine(), CoreMatchers.is(1));
        Assert.assertThat(line.getHashCode(), CoreMatchers.is("t1t2".hashCode()));
        line = lines.get(1);
        // 1 token on 2 line
        Assert.assertThat(line.getStartUnit(), CoreMatchers.is(3));
        Assert.assertThat(line.getEndUnit(), CoreMatchers.is(3));
        Assert.assertThat(line.getStartLine(), CoreMatchers.is(2));
        Assert.assertThat(line.getEndLine(), CoreMatchers.is(2));
        Assert.assertThat(line.getHashCode(), CoreMatchers.is("t3".hashCode()));
        line = lines.get(2);
        // 3 tokens on 4 line
        Assert.assertThat(line.getStartUnit(), CoreMatchers.is(4));
        Assert.assertThat(line.getEndUnit(), CoreMatchers.is(6));
        Assert.assertThat(line.getStartLine(), CoreMatchers.is(4));
        Assert.assertThat(line.getEndLine(), CoreMatchers.is(4));
        Assert.assertThat(line.getHashCode(), CoreMatchers.is("t1t3t3".hashCode()));
    }
}

