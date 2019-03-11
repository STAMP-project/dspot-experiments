package com.iota.iri.model;


import Hash.NULL_HASH;
import Hash.SIZE_IN_TRITS;
import SpongeFactory.Mode.CURLP81;
import TransactionViewModel.TRINARY_SIZE;
import com.iota.iri.TransactionTestUtils;
import com.iota.iri.crypto.SpongeFactory;
import com.iota.iri.utils.Converter;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

import static Hash.NULL_HASH;
import static Hash.SIZE_IN_BYTES;
import static Hash.SIZE_IN_TRITS;


public class HashTest {
    @Test
    public void calculate() throws Exception {
        Hash hash = TransactionHash.calculate(CURLP81, TransactionTestUtils.getRandomTransactionTrits());
        Assert.assertNotEquals(0, hash.hashCode());
        Assert.assertNotEquals(null, hash.bytes());
        Assert.assertNotEquals(null, hash.trits());
    }

    @Test
    public void calculate1() throws Exception {
        Hash hash = TransactionHash.calculate(TransactionTestUtils.getRandomTransactionTrits(), 0, 729, SpongeFactory.create(CURLP81));
        Assert.assertNotEquals(null, hash.bytes());
        Assert.assertNotEquals(0, hash.hashCode());
        Assert.assertNotEquals(null, hash.trits());
    }

    @Test
    public void calculate2() throws Exception {
        byte[] trits = TransactionTestUtils.getRandomTransactionTrits();
        byte[] bytes = Converter.allocateBytesForTrits(trits.length);
        Converter.bytes(trits, bytes);
        Hash hash = TransactionHash.calculate(bytes, TRINARY_SIZE, SpongeFactory.create(CURLP81));
        Assert.assertNotEquals(0, hash.hashCode());
        Assert.assertNotEquals(null, hash.bytes());
        Assert.assertNotEquals(null, hash.trits());
    }

    @Test
    public void trailingZeros() throws Exception {
        Hash hash = NULL_HASH;
        Assert.assertEquals(SIZE_IN_TRITS, hash.trailingZeros());
    }

    @Test
    public void trits() throws Exception {
        Hash hash = TransactionHash.calculate(CURLP81, TransactionTestUtils.getRandomTransactionTrits());
        Assert.assertFalse(Arrays.equals(new byte[SIZE_IN_TRITS], hash.trits()));
    }

    @Test
    public void equals() throws Exception {
        byte[] trits = TransactionTestUtils.getRandomTransactionTrits();
        Hash hash = TransactionHash.calculate(CURLP81, trits);
        Hash hash1 = TransactionHash.calculate(CURLP81, trits);
        Assert.assertTrue(hash.equals(hash1));
        Assert.assertFalse(hash.equals(NULL_HASH));
        Assert.assertFalse(hash.equals(TransactionHash.calculate(CURLP81, TransactionTestUtils.getRandomTransactionTrits())));
    }

    @Test
    public void hashCodeTest() throws Exception {
        byte[] trits = TransactionTestUtils.getRandomTransactionTrits();
        Hash hash = TransactionHash.calculate(CURLP81, trits);
        Assert.assertNotEquals(hash.hashCode(), 0);
        Assert.assertEquals(NULL_HASH.hashCode(), (-240540129));
    }

    @Test
    public void toStringTest() throws Exception {
        byte[] trits = TransactionTestUtils.getRandomTransactionTrits();
        Hash hash = TransactionHash.calculate(CURLP81, trits);
        Assert.assertEquals(NULL_HASH.toString(), "999999999999999999999999999999999999999999999999999999999999999999999999999999999");
        Assert.assertNotEquals(hash.toString(), "999999999999999999999999999999999999999999999999999999999999999999999999999999999");
        Assert.assertNotEquals(hash.toString().length(), 0);
    }

    @Test
    public void bytes() throws Exception {
        byte[] trits = TransactionTestUtils.getRandomTransactionTrits();
        Hash hash = TransactionHash.calculate(CURLP81, trits);
        Assert.assertTrue(Arrays.equals(new byte[SIZE_IN_BYTES], NULL_HASH.bytes()));
        Assert.assertFalse(Arrays.equals(new byte[SIZE_IN_BYTES], hash.bytes()));
        Assert.assertNotEquals(0, hash.bytes().length);
    }

    @Test
    public void compareTo() throws Exception {
        byte[] trits = TransactionTestUtils.getRandomTransactionTrits();
        Hash hash = TransactionHash.calculate(CURLP81, trits);
        Assert.assertEquals(hash.compareTo(NULL_HASH), (-(NULL_HASH.compareTo(hash))));
    }
}

