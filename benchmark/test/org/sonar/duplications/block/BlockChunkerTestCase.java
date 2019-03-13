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
package org.sonar.duplications.block;


import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.sonar.duplications.statement.Statement;


/**
 * Any implementation of {@link BlockChunker} should pass these test scenarios.
 */
public abstract class BlockChunkerTestCase {
    /**
     * Given:
     * <pre>
     * String[][] data = {
     *   {"a", "a"},
     *   {"a", "a"},
     *   {"a"},
     *   {"a", "a"},
     *   {"a", "a"}
     * };
     *
     * Statements (where L - literal, C - comma): "LCL", "C", "LCL", "C", "L", "C", "LCL", "C", "LCL"
     * Block size is 5.
     * First block: "LCL", "C", "LCL", "C", "L"
     * Last block: "L", "C", "LCL", "C", "LCL"
     * </pre>
     * Expected: different hashes for first and last blocks
     */
    @Test
    public void testSameChars() {
        List<Statement> statements = BlockChunkerTestCase.createStatementsFromStrings("LCL", "C", "LCL", "C", "L", "C", "LCL", "C", "LCL");
        BlockChunker chunker = createChunkerWithBlockSize(5);
        List<Block> blocks = chunker.chunk("resource", statements);
        Assert.assertThat("first and last block should have different hashes", blocks.get(0).getBlockHash(), CoreMatchers.not(CoreMatchers.equalTo(blocks.get(((blocks.size()) - 1)).getBlockHash())));
    }

    /**
     * TODO Godin: should we allow empty statements in general?
     */
    @Test
    public void testEmptyStatements() {
        List<Statement> statements = BlockChunkerTestCase.createStatementsFromStrings("1", "", "1", "1", "");
        BlockChunker chunker = createChunkerWithBlockSize(3);
        List<Block> blocks = chunker.chunk("resource", statements);
        Assert.assertThat("first and last block should have different hashes", blocks.get(0).getBlockHash(), CoreMatchers.not(CoreMatchers.equalTo(blocks.get(((blocks.size()) - 1)).getBlockHash())));
    }

    /**
     * Given: 5 statements, block size is 3
     * Expected: 4 blocks with correct index and with line numbers
     */
    @Test
    public void shouldBuildBlocksFromStatements() {
        List<Statement> statements = BlockChunkerTestCase.createStatementsFromStrings("1", "2", "3", "4", "5", "6");
        BlockChunker chunker = createChunkerWithBlockSize(3);
        List<Block> blocks = chunker.chunk("resource", statements);
        Assert.assertThat(blocks.size(), CoreMatchers.is(4));
        Assert.assertThat(blocks.get(0).getIndexInFile(), CoreMatchers.is(0));
        Assert.assertThat(blocks.get(0).getStartLine(), CoreMatchers.is(0));
        Assert.assertThat(blocks.get(0).getEndLine(), CoreMatchers.is(2));
        Assert.assertThat(blocks.get(1).getIndexInFile(), CoreMatchers.is(1));
        Assert.assertThat(blocks.get(1).getStartLine(), CoreMatchers.is(1));
        Assert.assertThat(blocks.get(1).getEndLine(), CoreMatchers.is(3));
    }

    @Test
    public void testHashes() {
        List<Statement> statements = BlockChunkerTestCase.createStatementsFromStrings("1", "2", "1", "2");
        BlockChunker chunker = createChunkerWithBlockSize(2);
        List<Block> blocks = chunker.chunk("resource", statements);
        Assert.assertThat("blocks 0 and 2 should have same hash", blocks.get(0).getBlockHash(), CoreMatchers.equalTo(blocks.get(2).getBlockHash()));
        Assert.assertThat("blocks 0 and 1 should have different hash", blocks.get(0).getBlockHash(), CoreMatchers.not(CoreMatchers.equalTo(blocks.get(1).getBlockHash())));
    }

    /**
     * Given: 0 statements
     * Expected: 0 blocks
     */
    @Test
    public void shouldNotBuildBlocksWhenNoStatements() {
        List<Statement> statements = Collections.emptyList();
        BlockChunker blockChunker = createChunkerWithBlockSize(2);
        List<Block> blocks = blockChunker.chunk("resource", statements);
        Assert.assertThat(blocks, CoreMatchers.sameInstance(Collections.EMPTY_LIST));
    }

    /**
     * Given: 1 statement, block size is 2
     * Expected: 0 blocks
     */
    @Test
    public void shouldNotBuildBlocksWhenNotEnoughStatements() {
        List<Statement> statements = BlockChunkerTestCase.createStatementsFromStrings("statement");
        BlockChunker blockChunker = createChunkerWithBlockSize(2);
        List<Block> blocks = blockChunker.chunk("resource", statements);
        Assert.assertThat(blocks, CoreMatchers.sameInstance(Collections.EMPTY_LIST));
    }
}

