/**
 * Copyright (c) 2016?2017 Andrei Tomashpolskiy and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bt.torrent;


import BitOrder.BIG_ENDIAN;
import BitOrder.LITTLE_ENDIAN;
import bt.TestUtil;
import bt.data.Bitfield;
import bt.data.ChunkDescriptor;
import bt.protocol.Protocols;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;


public class BitfieldTest extends BaseBitfieldTest {
    @Test
    public void testBitfield() {
        List<ChunkDescriptor> chunks = // piece #6 is not verified
        Arrays.asList(BaseBitfieldTest.completeChunk, BaseBitfieldTest.emptyChunk, BaseBitfieldTest.completeChunk, BaseBitfieldTest.emptyChunk, BaseBitfieldTest.emptyChunk, BaseBitfieldTest.emptyChunk, BaseBitfieldTest.completeChunk, BaseBitfieldTest.completeChunk);
        Bitfield bitfield = new Bitfield(chunks);
        bitfield.markVerified(0);
        bitfield.markVerified(2);
        bitfield.markVerified(7);
        byte expectedBitfieldLE = ((byte) (161));
        byte expectedBitfieldBE = ((byte) (133));
        Assert.assertArrayEquals(new byte[]{ expectedBitfieldLE }, bitfield.toByteArray(LITTLE_ENDIAN));
        Assert.assertArrayEquals(new byte[]{ expectedBitfieldBE }, bitfield.toByteArray(BIG_ENDIAN));
        Assert.assertEquals(8, bitfield.getPiecesTotal());
        Assert.assertEquals(3, bitfield.getPiecesComplete());
        Assert.assertEquals(5, bitfield.getPiecesIncomplete());
        Assert.assertEquals(0, bitfield.getPiecesSkipped());
        Assert.assertEquals(8, bitfield.getPiecesNotSkipped());
        Assert.assertEquals(5, bitfield.getPiecesRemaining());
        BitSet bitmask = bitfield.getBitmask();
        Assert.assertEquals(3, bitmask.cardinality());
        Assert.assertEquals(8, bitmask.length());
        Assert.assertTrue(bitfield.isComplete(0));
        Assert.assertFalse(bitfield.isComplete(1));
        Assert.assertTrue(bitfield.isComplete(2));
        Assert.assertFalse(bitfield.isComplete(3));
        Assert.assertFalse(bitfield.isComplete(4));
        Assert.assertFalse(bitfield.isComplete(5));
        Assert.assertTrue(bitfield.isComplete(6));
        Assert.assertTrue(bitfield.isComplete(7));
        Assert.assertTrue(bitfield.isVerified(0));
        Assert.assertFalse(bitfield.isVerified(1));
        Assert.assertTrue(bitfield.isVerified(2));
        Assert.assertFalse(bitfield.isVerified(3));
        Assert.assertFalse(bitfield.isVerified(4));
        Assert.assertFalse(bitfield.isVerified(5));
        Assert.assertFalse(bitfield.isVerified(6));
        Assert.assertTrue(bitfield.isVerified(7));
        Assert.assertEquals(COMPLETE_VERIFIED, bitfield.getPieceStatus(0));
        Assert.assertEquals(INCOMPLETE, bitfield.getPieceStatus(1));
        Assert.assertEquals(COMPLETE_VERIFIED, bitfield.getPieceStatus(2));
        Assert.assertEquals(INCOMPLETE, bitfield.getPieceStatus(3));
        Assert.assertEquals(INCOMPLETE, bitfield.getPieceStatus(4));
        Assert.assertEquals(INCOMPLETE, bitfield.getPieceStatus(5));
        Assert.assertEquals(COMPLETE, bitfield.getPieceStatus(6));
        Assert.assertEquals(COMPLETE_VERIFIED, bitfield.getPieceStatus(7));
    }

    @Test
    public void testBitfield_NumberOfPiecesNotDivisibleBy8() {
        List<ChunkDescriptor> chunks = // piece #8 is not verified
        Arrays.asList(BaseBitfieldTest.completeChunk, BaseBitfieldTest.emptyChunk, BaseBitfieldTest.completeChunk, BaseBitfieldTest.emptyChunk, BaseBitfieldTest.emptyChunk, BaseBitfieldTest.emptyChunk, BaseBitfieldTest.emptyChunk, BaseBitfieldTest.completeChunk, BaseBitfieldTest.completeChunk, BaseBitfieldTest.emptyChunk, BaseBitfieldTest.completeChunk);
        Bitfield bitfield = new Bitfield(chunks);
        bitfield.markVerified(0);
        bitfield.markVerified(2);
        bitfield.markVerified(7);
        bitfield.markVerified(10);
        short expectedBitfieldLE = ((short) (41248));
        short expectedBitfieldBE = ((short) (34052));
        Assert.assertArrayEquals(Protocols.getShortBytes(expectedBitfieldLE), bitfield.toByteArray(LITTLE_ENDIAN));
        Assert.assertArrayEquals(Protocols.getShortBytes(expectedBitfieldBE), bitfield.toByteArray(BIG_ENDIAN));
        Assert.assertEquals(11, bitfield.getPiecesTotal());
        Assert.assertEquals(4, bitfield.getPiecesComplete());
        Assert.assertEquals(7, bitfield.getPiecesIncomplete());
        Assert.assertEquals(0, bitfield.getPiecesSkipped());
        Assert.assertEquals(11, bitfield.getPiecesNotSkipped());
        Assert.assertEquals(7, bitfield.getPiecesRemaining());
        BitSet bitmask = bitfield.getBitmask();
        Assert.assertEquals(4, bitmask.cardinality());
        Assert.assertEquals(11, bitmask.length());
        Assert.assertTrue(bitfield.isComplete(0));
        Assert.assertFalse(bitfield.isComplete(1));
        Assert.assertTrue(bitfield.isComplete(2));
        Assert.assertFalse(bitfield.isComplete(3));
        Assert.assertFalse(bitfield.isComplete(4));
        Assert.assertFalse(bitfield.isComplete(5));
        Assert.assertFalse(bitfield.isComplete(6));
        Assert.assertTrue(bitfield.isComplete(7));
        Assert.assertTrue(bitfield.isComplete(8));
        Assert.assertFalse(bitfield.isComplete(9));
        Assert.assertTrue(bitfield.isComplete(10));
        Assert.assertTrue(bitfield.isVerified(0));
        Assert.assertFalse(bitfield.isVerified(1));
        Assert.assertTrue(bitfield.isVerified(2));
        Assert.assertFalse(bitfield.isVerified(3));
        Assert.assertFalse(bitfield.isVerified(4));
        Assert.assertFalse(bitfield.isVerified(5));
        Assert.assertFalse(bitfield.isVerified(6));
        Assert.assertTrue(bitfield.isVerified(7));
        Assert.assertFalse(bitfield.isVerified(8));
        Assert.assertFalse(bitfield.isVerified(9));
        Assert.assertTrue(bitfield.isVerified(10));
        Assert.assertEquals(COMPLETE_VERIFIED, bitfield.getPieceStatus(0));
        Assert.assertEquals(INCOMPLETE, bitfield.getPieceStatus(1));
        Assert.assertEquals(COMPLETE_VERIFIED, bitfield.getPieceStatus(2));
        Assert.assertEquals(INCOMPLETE, bitfield.getPieceStatus(3));
        Assert.assertEquals(INCOMPLETE, bitfield.getPieceStatus(4));
        Assert.assertEquals(INCOMPLETE, bitfield.getPieceStatus(5));
        Assert.assertEquals(INCOMPLETE, bitfield.getPieceStatus(6));
        Assert.assertEquals(COMPLETE_VERIFIED, bitfield.getPieceStatus(7));
        Assert.assertEquals(COMPLETE, bitfield.getPieceStatus(8));
        Assert.assertEquals(INCOMPLETE, bitfield.getPieceStatus(9));
        Assert.assertEquals(COMPLETE_VERIFIED, bitfield.getPieceStatus(10));
    }

    @Test
    public void testBitfield_Skipped() {
        List<ChunkDescriptor> chunks = Arrays.asList(BaseBitfieldTest.completeChunk, BaseBitfieldTest.emptyChunk, BaseBitfieldTest.completeChunk, BaseBitfieldTest.emptyChunk, BaseBitfieldTest.emptyChunk, BaseBitfieldTest.emptyChunk, BaseBitfieldTest.completeChunk, BaseBitfieldTest.completeChunk);
        Bitfield bitfield = new Bitfield(chunks);
        bitfield.markVerified(0);
        bitfield.markVerified(2);
        bitfield.markVerified(6);
        bitfield.markVerified(7);
        Assert.assertEquals(0, bitfield.getPiecesSkipped());
        bitfield.skip(0);
        Assert.assertEquals(1, bitfield.getPiecesSkipped());
        Assert.assertEquals(7, bitfield.getPiecesNotSkipped());
        Assert.assertEquals(4, bitfield.getPiecesRemaining());
        bitfield.skip(1);
        Assert.assertEquals(2, bitfield.getPiecesSkipped());
        Assert.assertEquals(6, bitfield.getPiecesNotSkipped());
        Assert.assertEquals(3, bitfield.getPiecesRemaining());
        bitfield.unskip(0);
        Assert.assertEquals(1, bitfield.getPiecesSkipped());
        Assert.assertEquals(7, bitfield.getPiecesNotSkipped());
        Assert.assertEquals(3, bitfield.getPiecesRemaining());
    }

    @Test
    public void testBitfield_Exceptional_markVerified_NotComplete() {
        List<ChunkDescriptor> chunks = Arrays.asList(BaseBitfieldTest.completeChunk, BaseBitfieldTest.emptyChunk);
        Bitfield bitfield = new Bitfield(chunks);
        TestUtil.assertExceptionWithMessage(( it) -> {
            bitfield.markVerified(1);
            return null;
        }, "Chunk is not complete: 1");
    }
}

