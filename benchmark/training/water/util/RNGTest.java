package water.util;


import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class RNGTest {
    static final long[] seed = new long[]{ 7234723423423402343L, 1234882173459262304L };

    enum NumType {

        DOUBLE,
        FLOAT,
        LONG,
        INT;}

    static final RNGTest.NumType[] types = new RNGTest.NumType[]{ RNGTest.NumType.DOUBLE, RNGTest.NumType.FLOAT, RNGTest.NumType.LONG, RNGTest.NumType.INT };

    @Test
    public void JavaRandomBadSeed() {
        Assert.assertTrue(((new Random(0).nextLong()) == (new Random(0).nextLong())));
        for (RNGTest.NumType t : RNGTest.types)
            Assert.assertTrue((("JavaRandomBadSeed " + t) + " failed."), RNGTest.ChiSquareTest(new Random(0), t));

    }

    @Test
    public void JavaRandom() {
        Assert.assertTrue(((new Random(RNGTest.seed[0]).nextLong()) == (new Random(RNGTest.seed[0]).nextLong())));
        for (RNGTest.NumType t : RNGTest.types)
            Assert.assertTrue((("JavaRandom " + t) + " failed."), RNGTest.ChiSquareTest(new Random(RNGTest.seed[0]), t));

    }

    @Test
    public void MersenneTwister() {
        Assert.assertTrue(((nextLong()) == (nextLong())));
        for (RNGTest.NumType t : RNGTest.types)
            Assert.assertTrue((("MersenneTwister " + t) + " failed."), RNGTest.ChiSquareTest(new RandomUtils.MersenneTwisterRNG(ArrayUtils.unpackInts(RNGTest.seed[0])), t));

    }

    @Test
    public void XorShift() {
        Assert.assertTrue(((new RandomUtils.XorShiftRNG(RNGTest.seed[0]).nextLong()) == (new RandomUtils.XorShiftRNG(RNGTest.seed[0]).nextLong())));
        for (RNGTest.NumType t : RNGTest.types)
            Assert.assertTrue((("XorShift " + t) + " failed."), RNGTest.ChiSquareTest(new RandomUtils.XorShiftRNG(RNGTest.seed[0]), t));

    }

    @Test
    public void PCG() {
        Assert.assertTrue(((new RandomUtils.PCGRNG(RNGTest.seed[0], RNGTest.seed[1]).nextLong()) == (new RandomUtils.PCGRNG(RNGTest.seed[0], RNGTest.seed[1]).nextLong())));
        for (RNGTest.NumType t : RNGTest.types)
            Assert.assertTrue((("PCG " + t) + " failed."), RNGTest.ChiSquareTest(new RandomUtils.PCGRNG(RNGTest.seed[0], RNGTest.seed[1]), t));

    }
}

