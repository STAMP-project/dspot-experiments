package com.iota.iri.crypto;


import Converter.RADIX;
import SpongeFactory.Mode.KERL;
import com.iota.iri.utils.Converter;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Kerl.BYTE_HASH_LENGTH;
import static Kerl.HASH_LENGTH;
import static Kerl.MAX_POWERS_LONG;


/**
 * Created by alon on 04/08/17.
 */
public class KerlTest {
    static final Random seed = new Random();

    Logger log = LoggerFactory.getLogger(CurlTest.class);

    static final Random rnd_seed = new Random();

    // Test conversion functions:
    @Test
    public void tritsFromBigInt() throws Exception {
        long value = 1433452143;
        int size = 50;
        byte[] trits = new byte[size];
        Converter.copyTrits(value, trits, 0, trits.length);
        BigInteger bigInteger = Kerl.bigIntFromTrits(trits, 0, trits.length);
        byte[] outTrits = new byte[size];
        Kerl.tritsFromBigInt(bigInteger, outTrits, 0, size);
        Assert.assertTrue(Arrays.equals(trits, outTrits));
    }

    @Test
    public void bytesFromBigInt() throws Exception {
        int byte_size = 48;
        BigInteger bigInteger = new BigInteger("13190295509826637194583200125168488859623001289643321872497025844241981297292953903419783680940401133507992851240799");
        byte[] outBytes = new byte[BYTE_HASH_LENGTH];
        Kerl.bytesFromBigInt(bigInteger, outBytes);
        BigInteger out_bigInteger = new BigInteger(outBytes);
        Assert.assertTrue(bigInteger.equals(out_bigInteger));
    }

    @Test
    public void loopRandBytesFromBigInt() throws Exception {
        // generate random bytes, turn them to trits and back
        int byte_size = 48;
        int trit_size = 243;
        byte[] inBytes = new byte[byte_size];
        byte[] trits = new byte[HASH_LENGTH];
        byte[] outBytes = new byte[BYTE_HASH_LENGTH];
        for (int i = 0; i < 10000; i++) {
            KerlTest.seed.nextBytes(inBytes);
            BigInteger in_bigInteger = new BigInteger(inBytes);
            Kerl.tritsFromBigInt(in_bigInteger, trits, 0, trit_size);
            BigInteger out_bigInteger = Kerl.bigIntFromTrits(trits, 0, trit_size);
            Kerl.bytesFromBigInt(out_bigInteger, outBytes);
            if ((i % 1000) == 0) {
                System.out.println(String.format("%d iteration: %s", i, in_bigInteger));
            }
            Assert.assertTrue(String.format("bigInt that failed: %s", in_bigInteger), Arrays.equals(inBytes, outBytes));
        }
    }

    @Test
    public void loopRandTritsFromBigInt() throws Exception {
        // generate random bytes, turn them to trits and back
        int byte_size = 48;
        int trit_size = 243;
        byte[] inTrits;
        byte[] bytes = new byte[BYTE_HASH_LENGTH];
        byte[] outTrits = new byte[HASH_LENGTH];
        for (int i = 0; i < 10000; i++) {
            inTrits = KerlTest.getRandomTrits(trit_size);
            inTrits[242] = 0;
            BigInteger in_bigInteger = Kerl.bigIntFromTrits(inTrits, 0, trit_size);
            Kerl.bytesFromBigInt(in_bigInteger, bytes);
            BigInteger out_bigInteger = new BigInteger(bytes);
            Kerl.tritsFromBigInt(out_bigInteger, outTrits, 0, trit_size);
            if ((i % 1000) == 0) {
                System.out.println(String.format("%d iteration: %s", i, in_bigInteger));
            }
            Assert.assertTrue(String.format("bigInt that failed: %s", in_bigInteger), Arrays.equals(inTrits, outTrits));
        }
    }

    @Test
    public void limitBigIntFromTrits() {
        // this confirms that the long math does not produce an overflow.
        byte[] trits = new byte[MAX_POWERS_LONG];
        Arrays.fill(trits, ((byte) (1)));
        BigInteger result = Kerl.bigIntFromTrits(trits, 0, trits.length);
        Arrays.fill(trits, ((byte) (1)));
        BigInteger expected = BigInteger.ZERO;
        for (int i = trits.length; (i--) > 0;) {
            expected = expected.multiply(BigInteger.valueOf(RADIX)).add(BigInteger.valueOf(trits[i]));
        }
        Assert.assertTrue("Overflow in long math", expected.equals(result));
    }

    @Test
    public void kerlOneAbsorb() throws Exception {
        byte[] initial_value = Converter.allocatingTritsFromTrytes("EMIDYNHBWMBCXVDEFOFWINXTERALUKYYPPHKP9JJFGJEIUY9MUDVNFZHMMWZUYUSWAIOWEVTHNWMHANBH");
        Sponge k = SpongeFactory.create(KERL);
        k.absorb(initial_value, 0, initial_value.length);
        byte[] hash_value = new byte[Curl.HASH_LENGTH];
        k.squeeze(hash_value, 0, hash_value.length);
        String hash = Converter.trytes(hash_value);
        Assert.assertEquals("EJEAOOZYSAWFPZQESYDHZCGYNSTWXUMVJOVDWUNZJXDGWCLUFGIMZRMGCAZGKNPLBRLGUNYWKLJTYEAQX", hash);
    }

    @Test
    public void kerlMultiSqueeze() throws Exception {
        byte[] initial_value = Converter.allocatingTritsFromTrytes("9MIDYNHBWMBCXVDEFOFWINXTERALUKYYPPHKP9JJFGJEIUY9MUDVNFZHMMWZUYUSWAIOWEVTHNWMHANBH");
        Sponge k = SpongeFactory.create(KERL);
        k.absorb(initial_value, 0, initial_value.length);
        byte[] hash_value = new byte[(Curl.HASH_LENGTH) * 2];
        k.squeeze(hash_value, 0, hash_value.length);
        String hash = Converter.trytes(hash_value);
        Assert.assertEquals("G9JYBOMPUXHYHKSNRNMMSSZCSHOFYOYNZRSZMAAYWDYEIMVVOGKPJBVBM9TDPULSFUNMTVXRKFIDOHUXXVYDLFSZYZTWQYTE9SPYYWYTXJYQ9IFGYOLZXWZBKWZN9QOOTBQMWMUBLEWUEEASRHRTNIQWJQNDWRYLCA", hash);
    }

    @Test
    public void kerlMultiAbsorbMultiSqueeze() throws Exception {
        byte[] initial_value = Converter.allocatingTritsFromTrytes("G9JYBOMPUXHYHKSNRNMMSSZCSHOFYOYNZRSZMAAYWDYEIMVVOGKPJBVBM9TDPULSFUNMTVXRKFIDOHUXXVYDLFSZYZTWQYTE9SPYYWYTXJYQ9IFGYOLZXWZBKWZN9QOOTBQMWMUBLEWUEEASRHRTNIQWJQNDWRYLCA");
        Sponge k = SpongeFactory.create(KERL);
        k.absorb(initial_value, 0, initial_value.length);
        byte[] hash_value = new byte[(Curl.HASH_LENGTH) * 2];
        k.squeeze(hash_value, 0, hash_value.length);
        String hash = Converter.trytes(hash_value);
        Assert.assertEquals("LUCKQVACOGBFYSPPVSSOXJEKNSQQRQKPZC9NXFSMQNRQCGGUL9OHVVKBDSKEQEBKXRNUJSRXYVHJTXBPDWQGNSCDCBAIRHAQCOWZEBSNHIJIGPZQITIBJQ9LNTDIBTCQ9EUWKHFLGFUVGGUWJONK9GBCDUIMAYMMQX", hash);
    }
}

