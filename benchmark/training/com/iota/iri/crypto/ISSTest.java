package com.iota.iri.crypto;


import Curl.HASH_LENGTH;
import HashFactory.ADDRESS;
import ISS.NUMBER_OF_FRAGMENT_CHUNKS;
import SpongeFactory.Mode;
import com.iota.iri.model.Hash;
import com.iota.iri.utils.Converter;
import java.util.Arrays;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

import static Sponge.HASH_LENGTH;


/**
 * Created by paul on 7/23/17.
 */
public class ISSTest {
    static String seed = "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN";

    static String message = "JCRNMXX9DIEVJJG9VW9QDUMVDGDVHANQDTCPPOPHLTBUBXULSIALRBVUINDPNGUFZLKDPOK9WBJMYCXF9" + ((((((("MFQN9ZKMROOXHULIDDXRNWMDENBWJWVVA9XPNHQUVDFSMQ9ETWKWGLOLYPWW9GQPVNDYJIRDBWVCBUHUE" + "GELSTLEXGAMMQAHSUEABKUSFOVGYRQBXJMORXIDTIPENPAFIUV9DOGZCAEPRJQOISRZDZBWWQQJVQDS9Y") + "GCMNADNVSUTXXAONPHBFCMWSVFYYXXWDZXFP9SZGLRCHHGKLNAQPMAXHFUUSQEKDAPH9GFVHMYDITCTFS") + "IJEZFADOJVDOEXOTDDPZYLKKDHCGPXYMGRKAGOEQYHTCTGKMZOKMZJLCQOYE9KFVRQLXDPBALUSEQSQDF") + "PPUYALCDYWSHANNQYKIMAZMKQQ9XVCSJHAWXLY9IIREZTSOFRMRGKDQPIEMDXTBDTY9DKOAIUEGNLUSRF") + "ZYPRNUOHFGDYIWFVKIUNYBGBHICRQTLDQQUTJX9DDSQANVKMCDZ9VEQBCHHSATVFIDYR9XUSDJHQDRBVK") + "9JUUZVWGCCWVXAC9ZIOKBWOKCTCJVXIJFBSTLNZCPJMAKDPYLTHMOKLFDNONJLLDBDXNFKPKUBKDU9QFS") + "XGVXS9PEDBDDBGFESSKCWUWMTOGHDLOPRILYYPSAQVTSQYLIPK9ATVMMYSTASHEZEFWBUNR9XKGCHR9MB");

    @Test
    public void testSignatureResolvesToAddressISS() throws Exception {
        int index = 10;
        int nof = 1;
        SpongeFactory[] modes = new Mode[]{ Mode.CURLP81, Mode.KERL };
        byte[] seedTrits = new byte[HASH_LENGTH];
        for (SpongeFactory.Mode mode : modes) {
            Converter.trits(ISSTest.seed, seedTrits, 0);
            byte[] subseed = ISS.subseed(mode, seedTrits, index);
            byte[] key = ISS.key(mode, subseed, nof);
            Kerl curl = new Kerl();
            byte[] messageTrits = Converter.allocateTritsForTrytes(ISSTest.message.length());
            Converter.trits(ISSTest.message, messageTrits, 0);
            curl.absorb(messageTrits, 0, messageTrits.length);
            byte[] messageHash = new byte[Curl.HASH_LENGTH];
            curl.squeeze(messageHash, 0, HASH_LENGTH);
            byte[] normalizedFragment = Arrays.copyOf(ISS.normalizedBundle(messageHash), NUMBER_OF_FRAGMENT_CHUNKS);
            byte[] signature = ISS.signatureFragment(mode, normalizedFragment, key);
            byte[] sigDigest = ISS.digest(mode, normalizedFragment, signature);
            byte[] signedAddress = ISS.address(mode, sigDigest);
            byte[] digest = ISS.digests(mode, key);
            byte[] address = ISS.address(mode, digest);
            Assert.assertTrue(Arrays.equals(address, signedAddress));
        }
    }

    @Test
    public void addressGenerationISS() throws Exception {
        int index = 0;
        int nof = 2;
        SpongeFactory[] modes = new Mode[]{ Mode.CURLP81, Mode.KERL };
        Hash[] hashes = new Hash[]{ ADDRESS.create("D9XCNSCCAJGLWSQOQAQNFWANPYKYMCQ9VCOMROLDVLONPPLDFVPIZNAPVZLQMPFYJPAHUKIAEKNCQIYJZ"), ADDRESS.create("MDWYEJJHJDIUVPKDY9EACGDJUOP9TLYDWETUBOYCBLYXYYYJYUXYUTCTPTDGJYFKMQMCNZDQPTBE9AFIW") };
        for (int i = 0; i < (modes.length); i++) {
            SpongeFactory.Mode mode = modes[i];
            byte[] seedTrits = Converter.allocateTritsForTrytes(ISSTest.seed.length());
            Converter.trits(ISSTest.seed, seedTrits, 0);
            byte[] subseed = ISS.subseed(mode, seedTrits, index);
            byte[] key = ISS.key(mode, subseed, nof);
            byte[] digest = ISS.digests(mode, key);
            byte[] address = ISS.address(mode, digest);
            Hash addressTrytes = ADDRESS.create(address);
            Assert.assertEquals(hashes[i].toString(), addressTrytes.toString());
        }
    }

    static final Random rnd_seed = new Random();
}

