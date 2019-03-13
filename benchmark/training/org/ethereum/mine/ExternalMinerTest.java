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
package org.ethereum.mine;


import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.Resource;
import org.ethereum.config.SystemProperties;
import org.ethereum.core.Block;
import org.ethereum.core.BlockHeader;
import org.ethereum.facade.EthereumImpl;
import org.ethereum.listener.CompositeEthereumListener;
import org.ethereum.util.ByteUtil;
import org.ethereum.util.blockchain.StandaloneBlockchain;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;


/**
 * Creates an instance
 */
public class ExternalMinerTest {
    private StandaloneBlockchain bc = new StandaloneBlockchain().withAutoblock(false);

    private CompositeEthereumListener listener = new CompositeEthereumListener();

    @Mock
    private EthereumImpl ethereum;

    @InjectMocks
    @Resource
    private BlockMiner blockMiner = new BlockMiner(SystemProperties.getDefault(), listener, bc.getBlockchain(), bc.getBlockchain().getBlockStore(), bc.getPendingState());

    @Test
    public void externalMiner_shouldWork() throws Exception {
        final Block startBestBlock = bc.getBlockchain().getBestBlock();
        final SettableFuture<MinerIfc.MiningResult> futureBlock = SettableFuture.create();
        blockMiner.setExternalMiner(new MinerIfc() {
            @Override
            public ListenableFuture<MiningResult> mine(Block block) {
                // System.out.print("Mining requested");
                return futureBlock;
            }

            @Override
            public boolean validate(BlockHeader blockHeader) {
                return true;
            }

            @Override
            public void setListeners(Collection<MinerListener> listeners) {
            }
        });
        Block b = bc.getBlockchain().createNewBlock(startBestBlock, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        Ethash.getForBlock(SystemProperties.getDefault(), b.getNumber()).mineLight(b).get();
        futureBlock.set(new MinerIfc.MiningResult(ByteUtil.byteArrayToLong(b.getNonce()), b.getMixHash(), b));
        MatcherAssert.assertThat(bc.getBlockchain().getBestBlock().getNumber(), CoreMatchers.is(((startBestBlock.getNumber()) + 1)));
    }
}

