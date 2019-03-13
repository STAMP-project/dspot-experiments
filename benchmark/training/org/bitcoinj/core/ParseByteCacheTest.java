/**
 * Copyright 2011 Steve Coughlan.
 * Copyright 2014 Andreas Schildbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bitcoinj.core;


import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.UnitTestParams;
import org.bitcoinj.store.BlockStore;
import org.junit.Assert;
import org.junit.Test;


public class ParseByteCacheTest {
    private static final int BLOCK_HEIGHT_GENESIS = 0;

    private final byte[] txMessage = Utils.HEX.withSeparator(" ", 2).decode(("f9 be b4 d9 74 78 00 00  00 00 00 00 00 00 00 00" + (((((((((((((((("02 01 00 00 e2 93 cd be  01 00 00 00 01 6d bd db" + "08 5b 1d 8a f7 51 84 f0  bc 01 fa d5 8d 12 66 e9") + "b6 3b 50 88 19 90 e4 b4  0d 6a ee 36 29 00 00 00") + "00 8b 48 30 45 02 21 00  f3 58 1e 19 72 ae 8a c7") + "c7 36 7a 7a 25 3b c1 13  52 23 ad b9 a4 68 bb 3a") + "59 23 3f 45 bc 57 83 80  02 20 59 af 01 ca 17 d0") + "0e 41 83 7a 1d 58 e9 7a  a3 1b ae 58 4e de c2 8d") + "35 bd 96 92 36 90 91 3b  ae 9a 01 41 04 9c 02 bf") + "c9 7e f2 36 ce 6d 8f e5  d9 40 13 c7 21 e9 15 98") + "2a cd 2b 12 b6 5d 9b 7d  59 e2 0a 84 20 05 f8 fc") + "4e 02 53 2e 87 3d 37 b9  6f 09 d6 d4 51 1a da 8f") + "14 04 2f 46 61 4a 4c 70  c0 f1 4b ef f5 ff ff ff") + "ff 02 40 4b 4c 00 00 00  00 00 19 76 a9 14 1a a0") + "cd 1c be a6 e7 45 8a 7a  ba d5 12 a9 d9 ea 1a fb") + "22 5e 88 ac 80 fa e9 c7  00 00 00 00 19 76 a9 14") + "0e ab 5b ea 43 6a 04 84  cf ab 12 48 5e fd a0 b7") + "8b 4e cc 52 88 ac 00 00  00 00")));

    private final byte[] txMessagePart = Utils.HEX.withSeparator(" ", 2).decode(("08 5b 1d 8a f7 51 84 f0  bc 01 fa d5 8d 12 66 e9" + (("b6 3b 50 88 19 90 e4 b4  0d 6a ee 36 29 00 00 00" + "00 8b 48 30 45 02 21 00  f3 58 1e 19 72 ae 8a c7") + "c7 36 7a 7a 25 3b c1 13  52 23 ad b9 a4 68 bb 3a")));

    private BlockStore blockStore;

    private byte[] b1Bytes;

    private byte[] b1BytesWithHeader;

    private byte[] tx1Bytes;

    private byte[] tx1BytesWithHeader;

    private byte[] tx2Bytes;

    private byte[] tx2BytesWithHeader;

    private static final NetworkParameters UNITTEST = UnitTestParams.get();

    private static final NetworkParameters MAINNET = MainNetParams.get();

    @Test
    public void validateSetup() {
        byte[] b1 = new byte[]{ 1, 1, 1, 2, 3, 4, 5, 6, 7 };
        byte[] b2 = new byte[]{ 1, 2, 3 };
        Assert.assertTrue(ParseByteCacheTest.arrayContains(b1, b2));
        Assert.assertTrue(ParseByteCacheTest.arrayContains(txMessage, txMessagePart));
        Assert.assertTrue(ParseByteCacheTest.arrayContains(tx1BytesWithHeader, tx1Bytes));
        Assert.assertTrue(ParseByteCacheTest.arrayContains(tx2BytesWithHeader, tx2Bytes));
        Assert.assertTrue(ParseByteCacheTest.arrayContains(b1BytesWithHeader, b1Bytes));
        Assert.assertTrue(ParseByteCacheTest.arrayContains(b1BytesWithHeader, tx1Bytes));
        Assert.assertTrue(ParseByteCacheTest.arrayContains(b1BytesWithHeader, tx2Bytes));
        Assert.assertFalse(ParseByteCacheTest.arrayContains(tx1BytesWithHeader, b1Bytes));
    }

    @Test
    public void testTransactionsRetain() throws Exception {
        testTransaction(ParseByteCacheTest.MAINNET, txMessage, false, true);
        testTransaction(ParseByteCacheTest.UNITTEST, tx1BytesWithHeader, false, true);
        testTransaction(ParseByteCacheTest.UNITTEST, tx2BytesWithHeader, false, true);
    }

    @Test
    public void testTransactionsNoRetain() throws Exception {
        testTransaction(ParseByteCacheTest.MAINNET, txMessage, false, false);
        testTransaction(ParseByteCacheTest.UNITTEST, tx1BytesWithHeader, false, false);
        testTransaction(ParseByteCacheTest.UNITTEST, tx2BytesWithHeader, false, false);
    }

    @Test
    public void testBlockAll() throws Exception {
        testBlock(b1BytesWithHeader, false, false);
        testBlock(b1BytesWithHeader, false, true);
    }
}

