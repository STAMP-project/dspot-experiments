/**
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.config;


import org.ethereum.core.Block;
import org.ethereum.util.blockchain.StandaloneBlockchain;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Stan Reshetnyk on 29.12.16.
 */
public class DaoLightMiningTest {
    // configure
    final int FORK_BLOCK = 20;

    final int FORK_BLOCK_AFFECTED = 10;// hardcoded in DAO config


    @Test
    public void testDaoExtraData() {
        final StandaloneBlockchain sb = createBlockchain(true);
        for (int i = 0; i < ((FORK_BLOCK) + 30); i++) {
            Block b = sb.createBlock();
            // System.out.println("Created block " + b.getNumber() + " " + getData(b.getExtraData()));
        }
        Assert.assertEquals("EthereumJ powered", getData(sb, ((FORK_BLOCK) - 1)));
        Assert.assertEquals("dao-hard-fork", getData(sb, FORK_BLOCK));
        Assert.assertEquals("dao-hard-fork", getData(sb, (((FORK_BLOCK) + (FORK_BLOCK_AFFECTED)) - 1)));
        Assert.assertEquals("EthereumJ powered", getData(sb, ((FORK_BLOCK) + (FORK_BLOCK_AFFECTED))));
    }

    @Test
    public void testNoDaoExtraData() {
        final StandaloneBlockchain sb = createBlockchain(false);
        for (int i = 0; i < ((FORK_BLOCK) + 30); i++) {
            Block b = sb.createBlock();
        }
        Assert.assertEquals("EthereumJ powered", getData(sb, ((FORK_BLOCK) - 1)));
        Assert.assertEquals("", getData(sb, FORK_BLOCK));
        Assert.assertEquals("", getData(sb, (((FORK_BLOCK) + (FORK_BLOCK_AFFECTED)) - 1)));
        Assert.assertEquals("EthereumJ powered", getData(sb, ((FORK_BLOCK) + (FORK_BLOCK_AFFECTED))));
    }
}

