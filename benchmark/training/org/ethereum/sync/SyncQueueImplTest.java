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
package org.ethereum.sync;


import SyncQueueIfc.HeadersRequest;
import SyncQueueIfc.ValidatedHeaders;
import SyncQueueIfc.ValidatedHeaders.Empty;
import SyncQueueImpl.HeaderElement;
import SyncQueueImpl.HeadersRequestImpl;
import SyncQueueImpl.MAX_CHAIN_LEN;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.ethereum.TestUtils;
import org.ethereum.core.Block;
import org.ethereum.core.BlockHeader;
import org.ethereum.core.BlockHeaderWrapper;
import org.ethereum.crypto.HashUtil;
import org.ethereum.db.ByteArrayWrapper;
import org.ethereum.util.FastByteComparisons;
import org.ethereum.validator.DependentBlockHeaderRule;
import org.junit.Assert;
import org.junit.Test;

import static SyncQueueImpl.MAX_CHAIN_LEN;


/**
 * Created by Anton Nashatyrev on 30.05.2016.
 */
public class SyncQueueImplTest {
    byte[] peer0 = new byte[32];

    private static final int DEFAULT_REQUEST_LEN = 192;

    @Test
    public void test1() {
        List<Block> randomChain = TestUtils.getRandomChain(new byte[32], 0, 1024);
        SyncQueueImpl syncQueue = new SyncQueueImpl(randomChain.subList(0, 32));
        SyncQueueIfc.HeadersRequest headersRequest = syncQueue.requestHeaders(SyncQueueImplTest.DEFAULT_REQUEST_LEN, 1, Integer.MAX_VALUE).iterator().next();
        System.out.println(headersRequest);
        syncQueue.addHeaders(createHeadersFromBlocks(TestUtils.getRandomChain(randomChain.get(16).getHash(), 17, 64), peer0));
        syncQueue.addHeaders(createHeadersFromBlocks(randomChain.subList(32, 1024), peer0));
    }

    @Test
    public void test2() {
        List<Block> randomChain = TestUtils.getRandomChain(new byte[32], 0, 1024);
        SyncQueueImplTest.Peer[] peers = new SyncQueueImplTest.Peer[10];
        peers[0] = new SyncQueueImplTest.Peer(randomChain);
        for (int i = 1; i < (peers.length); i++) {
            peers[i] = new SyncQueueImplTest.Peer(TestUtils.getRandomChain(TestUtils.randomBytes(32), 1, 1024));
        }
    }

    @Test
    public void testHeadersSplit() {
        // 1, 2, 3, 4, 5
        SyncQueueImpl.HeadersRequestImpl headersRequest = new SyncQueueImpl.HeadersRequestImpl(1, 5, false);
        List<SyncQueueIfc.HeadersRequest> requests = headersRequest.split(2);
        assert (requests.size()) == 3;
        // 1, 2
        assert (requests.get(0).getStart()) == 1;
        assert (requests.get(0).getCount()) == 2;
        // 3, 4
        assert (requests.get(1).getStart()) == 3;
        assert (requests.get(1).getCount()) == 2;
        // 5
        assert (requests.get(2).getStart()) == 5;
        assert (requests.get(2).getCount()) == 1;
    }

    @Test
    public void testReverseHeaders1() {
        List<Block> randomChain = TestUtils.getRandomChain(new byte[32], 0, 699);
        List<Block> randomChain1 = TestUtils.getRandomChain(new byte[32], 0, 699);
        SyncQueueImplTest.Peer[] peers = new SyncQueueImplTest.Peer[]{ new SyncQueueImplTest.Peer(randomChain), new SyncQueueImplTest.Peer(randomChain, false), new SyncQueueImplTest.Peer(randomChain1) };
        SyncQueueReverseImpl syncQueue = new SyncQueueReverseImpl(randomChain.get(((randomChain.size()) - 1)).getHash(), true);
        List<BlockHeaderWrapper> result = new ArrayList<>();
        int peerIdx = 1;
        Random rnd = new Random();
        int cnt = 0;
        while (cnt < 1000) {
            System.out.println(("Cnt: " + (cnt++)));
            Collection<SyncQueueIfc.HeadersRequest> headersRequests = syncQueue.requestHeaders(20, 5, Integer.MAX_VALUE);
            if (headersRequests == null)
                break;

            for (SyncQueueIfc.HeadersRequest request : headersRequests) {
                System.out.println(("Req: " + request));
                List<BlockHeader> headers = (rnd.nextBoolean()) ? peers[peerIdx].getHeaders(request) : peers[peerIdx].getRandomHeaders(10);
                // List<BlockHeader> headers = peers[0].getHeaders(request);
                peerIdx = (peerIdx + 1) % (peers.length);
                List<BlockHeaderWrapper> ret = syncQueue.addHeaders(createHeadersFromHeaders(headers, peer0));
                result.addAll(ret);
                System.out.println(("Result length: " + (result.size())));
            }
        } 
        List<BlockHeaderWrapper> extraHeaders = syncQueue.addHeaders(createHeadersFromHeaders(peers[0].getRandomHeaders(10), peer0));
        assert extraHeaders.isEmpty();
        assert cnt != 1000;
        assert (result.size()) == ((randomChain.size()) - 1);
        for (int i = 0; i < ((result.size()) - 1); i++) {
            assert Arrays.equals(result.get((i + 1)).getHash(), result.get(i).getHeader().getParentHash());
        }
        assert Arrays.equals(randomChain.get(0).getHash(), result.get(((result.size()) - 1)).getHeader().getParentHash());
    }

    @Test
    public void testReverseHeaders2() {
        List<Block> randomChain = TestUtils.getRandomChain(new byte[32], 0, 194);
        SyncQueueImplTest.Peer[] peers = new SyncQueueImplTest.Peer[]{ new SyncQueueImplTest.Peer(randomChain), new SyncQueueImplTest.Peer(randomChain) };
        SyncQueueReverseImpl syncQueue = new SyncQueueReverseImpl(randomChain.get(((randomChain.size()) - 1)).getHash(), true);
        List<BlockHeaderWrapper> result = new ArrayList<>();
        int peerIdx = 1;
        int cnt = 0;
        while (cnt < 100) {
            System.out.println(("Cnt: " + (cnt++)));
            Collection<SyncQueueIfc.HeadersRequest> headersRequests = syncQueue.requestHeaders(192, 10, Integer.MAX_VALUE);
            if (headersRequests == null)
                break;

            for (SyncQueueIfc.HeadersRequest request : headersRequests) {
                System.out.println(("Req: " + request));
                List<BlockHeader> headers = peers[peerIdx].getHeaders(request);
                // Removing genesis header, which we will not get from real peers
                Iterator<BlockHeader> it = headers.iterator();
                while (it.hasNext()) {
                    if (FastByteComparisons.equal(it.next().getHash(), randomChain.get(0).getHash()))
                        it.remove();

                } 
                peerIdx = (peerIdx + 1) % 2;
                List<BlockHeaderWrapper> ret = syncQueue.addHeaders(createHeadersFromHeaders(headers, peer0));
                result.addAll(ret);
                System.out.println(("Result length: " + (result.size())));
            }
        } 
        assert cnt != 100;
        assert (result.size()) == ((randomChain.size()) - 1);// - genesis

        for (int i = 0; i < ((result.size()) - 1); i++) {
            assert Arrays.equals(result.get((i + 1)).getHash(), result.get(i).getHeader().getParentHash());
        }
        assert Arrays.equals(randomChain.get(0).getHash(), result.get(((result.size()) - 1)).getHeader().getParentHash());
    }

    // a copy of testReverseHeaders2 with #addHeadersAndValidate() instead #addHeaders(),
    // makes sure that nothing is broken
    @Test
    public void testReverseHeaders3() {
        List<Block> randomChain = TestUtils.getRandomChain(new byte[32], 0, 194);
        SyncQueueImplTest.Peer[] peers = new SyncQueueImplTest.Peer[]{ new SyncQueueImplTest.Peer(randomChain), new SyncQueueImplTest.Peer(randomChain) };
        SyncQueueReverseImpl syncQueue = new SyncQueueReverseImpl(randomChain.get(((randomChain.size()) - 1)).getHash(), true);
        List<BlockHeaderWrapper> result = new ArrayList<>();
        int peerIdx = 1;
        int cnt = 0;
        while (cnt < 100) {
            System.out.println(("Cnt: " + (cnt++)));
            Collection<SyncQueueIfc.HeadersRequest> headersRequests = syncQueue.requestHeaders(192, 10, Integer.MAX_VALUE);
            if (headersRequests == null)
                break;

            for (SyncQueueIfc.HeadersRequest request : headersRequests) {
                System.out.println(("Req: " + request));
                List<BlockHeader> headers = peers[peerIdx].getHeaders(request);
                // Removing genesis header, which we will not get from real peers
                Iterator<BlockHeader> it = headers.iterator();
                while (it.hasNext()) {
                    if (FastByteComparisons.equal(it.next().getHash(), randomChain.get(0).getHash()))
                        it.remove();

                } 
                peerIdx = (peerIdx + 1) % 2;
                SyncQueueIfc.ValidatedHeaders ret = syncQueue.addHeadersAndValidate(createHeadersFromHeaders(headers, peer0));
                assert ret.isValid();
                result.addAll(ret.getHeaders());
                System.out.println(("Result length: " + (result.size())));
            }
        } 
        assert cnt != 100;
        assert (result.size()) == ((randomChain.size()) - 1);// - genesis

        for (int i = 0; i < ((result.size()) - 1); i++) {
            assert Arrays.equals(result.get((i + 1)).getHash(), result.get(i).getHeader().getParentHash());
        }
        assert Arrays.equals(randomChain.get(0).getHash(), result.get(((result.size()) - 1)).getHeader().getParentHash());
    }

    @Test
    public void testLongLongestChain() {
        List<Block> randomChain = TestUtils.getRandomAltChain(new byte[32], 0, 10500, 3);
        SyncQueueImpl syncQueue = new SyncQueueImpl(randomChain);
        assert (syncQueue.getLongestChain().size()) == 10500;
    }

    @Test
    public void testWideLongestChain() {
        List<Block> randomChain = TestUtils.getRandomAltChain(new byte[32], 0, 100, 100);
        SyncQueueImpl syncQueue = new SyncQueueImpl(randomChain);
        assert (syncQueue.getLongestChain().size()) == 100;
    }

    @Test
    public void testGapedLongestChain() {
        List<Block> randomChain = TestUtils.getRandomAltChain(new byte[32], 0, 100, 5);
        Iterator<Block> it = randomChain.iterator();
        while (it.hasNext()) {
            if ((it.next().getHeader().getNumber()) == 15)
                it.remove();

        } 
        SyncQueueImpl syncQueue = new SyncQueueImpl(randomChain);
        assert (syncQueue.getLongestChain().size()) == 15;// 0 .. 14

    }

    @Test
    public void testFirstBlockGapedLongestChain() {
        List<Block> randomChain = TestUtils.getRandomAltChain(new byte[32], 0, 100, 5);
        Iterator<Block> it = randomChain.iterator();
        while (it.hasNext()) {
            if ((it.next().getHeader().getNumber()) == 1)
                it.remove();

        } 
        SyncQueueImpl syncQueue = new SyncQueueImpl(randomChain);
        assert (syncQueue.getLongestChain().size()) == 1;// 0

    }

    @Test(expected = AssertionError.class)
    public void testZeroBlockGapedLongestChain() {
        List<Block> randomChain = TestUtils.getRandomAltChain(new byte[32], 0, 100, 5);
        Iterator<Block> it = randomChain.iterator();
        while (it.hasNext()) {
            if ((it.next().getHeader().getNumber()) == 0)
                it.remove();

        } 
        SyncQueueImpl syncQueue = new SyncQueueImpl(randomChain);
        syncQueue.getLongestChain().size();
    }

    @Test
    public void testNoParentGapeLongestChain() {
        List<Block> randomChain = TestUtils.getRandomAltChain(new byte[32], 0, 100, 5);
        // Moving #15 blocks to the end to be sure it didn't trick SyncQueue
        Iterator<Block> it = randomChain.iterator();
        List<Block> blockSaver = new ArrayList<>();
        while (it.hasNext()) {
            Block block = it.next();
            if ((block.getHeader().getNumber()) == 15) {
                blockSaver.add(block);
                it.remove();
            }
        } 
        randomChain.addAll(blockSaver);
        SyncQueueImpl syncQueue = new SyncQueueImpl(randomChain);
        // We still have linked chain
        assert (syncQueue.getLongestChain().size()) == 100;
        List<Block> randomChain2 = TestUtils.getRandomAltChain(new byte[32], 0, 100, 5);
        Iterator<Block> it2 = randomChain2.iterator();
        List<Block> blockSaver2 = new ArrayList<>();
        while (it2.hasNext()) {
            Block block = it2.next();
            if ((block.getHeader().getNumber()) == 15) {
                blockSaver2.add(block);
            }
        } 
        // Removing #15 blocks
        for (int i = 0; i < 5; ++i) {
            randomChain.remove(((randomChain.size()) - 1));
        }
        // Adding wrong #15 blocks
        assert (blockSaver2.size()) == 5;
        randomChain.addAll(blockSaver2);
        assert (getLongestChain().size()) == 15;// 0 .. 14

    }

    @Test
    public void testValidateChain() {
        List<Block> randomChain = TestUtils.getRandomChain(new byte[32], 0, 100);
        SyncQueueImpl queue = new SyncQueueImpl(randomChain);
        byte[] nodeId = HashUtil.randomPeerId();
        List<Block> chain = TestUtils.getRandomChain(randomChain.get(((randomChain.size()) - 1)).getHash(), 100, (((MAX_CHAIN_LEN) - 100) - 1));
        queue.addHeaders(createHeadersFromBlocks(chain, nodeId));
        List<SyncQueueImpl.HeaderElement> longestChain = queue.getLongestChain();
        // no validator is set
        Assert.assertEquals(Empty, queue.validateChain(longestChain));
        chain = TestUtils.getRandomChain(chain.get(((chain.size()) - 1)).getHash(), ((MAX_CHAIN_LEN) - 1), MAX_CHAIN_LEN);
        queue.addHeaders(createHeadersFromBlocks(chain, nodeId));
        chain = TestUtils.getRandomChain(chain.get(((chain.size()) - 1)).getHash(), ((2 * (MAX_CHAIN_LEN)) - 1), MAX_CHAIN_LEN);
        // the chain is invalid
        queue.withParentHeaderValidator(SyncQueueImplTest.RedRule);
        SyncQueueIfc.ValidatedHeaders ret = queue.addHeadersAndValidate(createHeadersFromBlocks(chain, nodeId));
        Assert.assertFalse(ret.isValid());
        Assert.assertArrayEquals(nodeId, ret.getNodeId());
        // the chain is valid
        queue.withParentHeaderValidator(SyncQueueImplTest.GreenRule);
        ret = queue.addHeadersAndValidate(createHeadersFromBlocks(chain, nodeId));
        Assert.assertEquals(Empty, ret);
    }

    @Test
    public void testEraseChain() {
        List<Block> randomChain = TestUtils.getRandomChain(new byte[32], 0, 1024);
        SyncQueueImpl queue = new SyncQueueImpl(randomChain);
        List<Block> chain1 = TestUtils.getRandomChain(randomChain.get(((randomChain.size()) - 1)).getHash(), 1024, ((MAX_CHAIN_LEN) / 2));
        queue.addHeaders(createHeadersFromBlocks(chain1, HashUtil.randomPeerId()));
        List<Block> chain2 = TestUtils.getRandomChain(randomChain.get(((randomChain.size()) - 1)).getHash(), 1024, (((MAX_CHAIN_LEN) / 2) - 1));
        queue.addHeaders(createHeadersFromBlocks(chain2, HashUtil.randomPeerId()));
        List<SyncQueueImpl.HeaderElement> longestChain = queue.getLongestChain();
        long maxNum = longestChain.get(((longestChain.size()) - 1)).header.getNumber();
        Assert.assertEquals(((1024 + ((MAX_CHAIN_LEN) / 2)) - 1), maxNum);
        Assert.assertEquals(((1024 + ((MAX_CHAIN_LEN) / 2)) - 1), queue.getHeadersCount());
        List<Block> chain3 = TestUtils.getRandomChain(chain1.get(((chain1.size()) - 1)).getHash(), (1024 + ((MAX_CHAIN_LEN) / 2)), ((MAX_CHAIN_LEN) / 10));
        // the chain is invalid and must be erased
        queue.withParentHeaderValidator(new DependentBlockHeaderRule() {
            @Override
            public boolean validate(BlockHeader header, BlockHeader dependency) {
                // chain2 should become best after erasing
                return (header.getNumber()) < (chain2.get(((chain2.size()) - 2)).getNumber());
            }
        });
        queue.addHeadersAndValidate(createHeadersFromBlocks(chain3, HashUtil.randomPeerId()));
        longestChain = queue.getLongestChain();
        Assert.assertEquals((maxNum - 1), queue.getHeadersCount());
        Assert.assertEquals(chain2.get(((chain2.size()) - 1)).getHeader(), longestChain.get(((longestChain.size()) - 1)).header.getHeader());
    }

    private static class Peer {
        Map<ByteArrayWrapper, Block> blocks = new HashMap<>();

        List<Block> chain;

        boolean returnGenesis;

        public Peer(List<Block> chain) {
            this(chain, true);
        }

        public Peer(List<Block> chain, boolean returnGenesis) {
            this.returnGenesis = returnGenesis;
            this.chain = chain;
            for (Block block : chain) {
                blocks.put(new ByteArrayWrapper(block.getHash()), block);
            }
        }

        public List<BlockHeader> getHeaders(long startBlockNum, int count, boolean reverse) {
            return getHeaders(startBlockNum, count, reverse, 0);
        }

        public List<BlockHeader> getHeaders(SyncQueueIfc.HeadersRequest req) {
            if ((req.getHash()) == null) {
                return getHeaders(req.getStart(), req.getCount(), req.isReverse(), req.getStep());
            } else {
                Block block = blocks.get(new ByteArrayWrapper(req.getHash()));
                if (block == null)
                    return Collections.emptyList();

                return getHeaders(block.getNumber(), req.getCount(), req.isReverse(), req.getStep());
            }
        }

        public List<BlockHeader> getRandomHeaders(int count) {
            List<BlockHeader> ret = new ArrayList<>();
            Random rnd = new Random();
            for (int i = 0; i < count; i++) {
                ret.add(chain.get(rnd.nextInt(chain.size())).getHeader());
            }
            return ret;
        }

        public List<BlockHeader> getHeaders(long startBlockNum, int count, boolean reverse, int step) {
            step = (step == 0) ? 1 : step;
            List<BlockHeader> ret = new ArrayList<>();
            int i = ((int) (startBlockNum));
            for (; (((count--) > 0) && (i >= (returnGenesis ? 0 : 1))) && (i <= (chain.get(((chain.size()) - 1)).getNumber())); i += (reverse) ? -step : step) {
                ret.add(chain.get(i).getHeader());
            }
            // step = step == 0 ? 1 : step;
            // 
            // if (reverse) {
            // startBlockNum = startBlockNum - (count - 1 ) * step;
            // }
            // 
            // startBlockNum = Math.max(startBlockNum, chain.get(0).getNumber());
            // startBlockNum = Math.min(startBlockNum, chain.get(chain.size() - 1).getNumber());
            // long endBlockNum = startBlockNum + (count - 1) * step;
            // endBlockNum = Math.max(endBlockNum, chain.get(0).getNumber());
            // endBlockNum = Math.min(endBlockNum, chain.get(chain.size() - 1).getNumber());
            // List<BlockHeader> ret = new ArrayList<>();
            // int startIdx = (int) (startBlockNum - chain.get(0).getNumber());
            // for (int i = startIdx; i < startIdx + (endBlockNum - startBlockNum + 1); i+=step) {
            // ret.add(chain.get(i).getHeader());
            // }
            return ret;
        }

        public List<Block> getBlocks(Collection<BlockHeaderWrapper> hashes) {
            List<Block> ret = new ArrayList<>();
            for (BlockHeaderWrapper hash : hashes) {
                Block block = blocks.get(new ByteArrayWrapper(hash.getHash()));
                if (block != null)
                    ret.add(block);

            }
            return ret;
        }
    }

    static final DependentBlockHeaderRule RedRule = new DependentBlockHeaderRule() {
        @Override
        public boolean validate(BlockHeader header, BlockHeader dependency) {
            return false;
        }
    };

    static final DependentBlockHeaderRule GreenRule = new DependentBlockHeaderRule() {
        @Override
        public boolean validate(BlockHeader header, BlockHeader dependency) {
            return true;
        }
    };
}

