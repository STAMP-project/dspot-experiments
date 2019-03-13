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
package org.sonar.duplications.java;


import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.sonar.duplications.block.BlockChunker;
import org.sonar.duplications.index.CloneGroup;
import org.sonar.duplications.index.ClonePart;
import org.sonar.duplications.statement.StatementChunker;
import org.sonar.duplications.token.TokenChunker;


/**
 * From <a href="http://research.cs.queensu.ca/TechReports/Reports/2007-541.pdf">A Survey on Software Clone Detection Research (2007 year)</a>:
 * <ul>
 * <li>Type 1: Identical code fragments except for variations in whitespace (may be also variations in layout) and comments.
 * Type 1 is widely know as Exact clones.</li>
 * <li>Type 2: Structurally/syntactically identical fragments except for variations in identifiers, literals, types, layout and comments.
 * The reserved words and the sentence structures are essentially the same.</li>
 * <li>Type 3: Copied fragments with further modifications. Statements can be changed,
 * added and/or deleted in addition to variations in identifiers, literals, types, layout
 * and comments.</li>
 * </ul>
 */
public class JavaDuplicationsFunctionalTest {
    @Test
    public void type1() {
        String fragment0 = source("if (a >= b) {", "  c = d + b; // Comment1", "  d = d + 1;}", "else", "  c = d - a; // Comment2");
        String fragment1 = source("if (a>=b) {", "  // Comment1", "  c=d+b;", "  d=d+1;", "} else // Comment2", "  c=d-a;");
        List<CloneGroup> duplications = JavaDuplicationsFunctionalTest.detect2(fragment0, fragment1);
        Assert.assertThat(duplications.size(), CoreMatchers.is(1));
        ClonePart part = duplications.get(0).getOriginPart();
        Assert.assertThat(part.getStartLine(), CoreMatchers.is(1));
        Assert.assertThat(part.getEndLine(), CoreMatchers.is(5));
    }

    /**
     * Supports only subset of Type 2.
     *
     * @see #type2
     * @see #literalsNormalization()
     */
    @Test
    public void type2_literals() {
        String fragment0 = source("if (a >= b) {", "  c = b + 1; // Comment1", "  d = '1';}", "else", "  c = d - a; // Comment2");
        String fragment1 = source("if (a >= b) {", "  c = b + 2; // Comment1", "  d = '2';}", "else", "  c = d - a; // Comment2");
        List<CloneGroup> duplications = JavaDuplicationsFunctionalTest.detect2(fragment0, fragment1);
        Assert.assertThat(duplications.size(), CoreMatchers.is(1));
        ClonePart part = duplications.get(0).getOriginPart();
        Assert.assertThat(part.getStartLine(), CoreMatchers.is(1));
        Assert.assertThat(part.getEndLine(), CoreMatchers.is(5));
    }

    @Test
    public void type2() {
        String fragment0 = source("if (a >= b) {", "  c = d + b; // Comment1", "  d = d + 1;}", "else", "  c = d - a; // Comment2");
        String fragment1 = source("if (m >= n) {", "  // Comment3", "  y = x + n; // Comment1", "  x = x + 5;}", "else", "  y = x - m; // Comment2");
        List<CloneGroup> duplications = JavaDuplicationsFunctionalTest.detect2(fragment0, fragment1);
        Assert.assertThat(duplications.size(), CoreMatchers.is(0));
    }

    /**
     * Does not support Type 3, however able to detect inner parts.
     */
    @Test
    public void type3() {
        String fragment0 = source("public int getSoLinger() throws SocketException {", "  Object o = impl.getOption( SocketOptions.SO_LINGER);", "  if (o instanceof Integer) {", "    return((Integer) o).intValue();", "  }", "  else return -1;", "}");
        String fragment1 = source("public synchronized int getSoTimeout() throws SocketException {", "  Object o = impl.getOption( SocketOptions.SO_TIMEOUT);", "  if (o instanceof Integer) {", "    return((Integer) o).intValue();", "  }", "  else return -0;", "}");
        List<CloneGroup> duplications = JavaDuplicationsFunctionalTest.detect2(fragment0, fragment1);
        Assert.assertThat(duplications.size(), CoreMatchers.is(1));
        ClonePart part = duplications.get(0).getOriginPart();
        Assert.assertThat(part.getStartLine(), CoreMatchers.is(3));
        Assert.assertThat(part.getEndLine(), CoreMatchers.is(6));
    }

    private static final int BLOCK_SIZE = 1;

    private static TokenChunker TOKEN_CHUNKER = JavaTokenProducer.build();

    private static StatementChunker STATEMENT_CHUNKER = JavaStatementBuilder.build();

    private static BlockChunker BLOCK_CHUNKER = new BlockChunker(JavaDuplicationsFunctionalTest.BLOCK_SIZE);

    /**
     * See SONAR-2837
     */
    @Test
    public void initializationOfMultidimensionalArray() {
        List<CloneGroup> duplications = detect("int[][] idx = new int[][] { { 1, 2 }, { 3, 4 } };");
        Assert.assertThat(duplications.size(), CoreMatchers.is(0));
    }

    /**
     * See SONAR-2782
     */
    @Test
    public void chainOfCases() {
        List<CloneGroup> duplications = detect("switch (a) {", "  case 'a': case 'b': case 'c':", "    doSomething();", "  case 'd': case 'e': case 'f':", "    doSomethingElse();", "}");
        Assert.assertThat(duplications.size(), CoreMatchers.is(0));
    }

    @Test
    public void literalsNormalization() {
        List<CloneGroup> duplications = detect("String s = \"abc\";", "String s = \"def\";");
        Assert.assertThat(duplications.size(), CoreMatchers.is(1));
        duplications = detect("int i = 1;", "int i = 2;");
        Assert.assertThat(duplications.size(), CoreMatchers.is(1));
    }
}

