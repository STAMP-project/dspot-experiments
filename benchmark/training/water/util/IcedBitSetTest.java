package water.util;


import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class IcedBitSetTest {
    @Test
    public void fill8() {
        int len = 8;
        IcedBitSet bs = new IcedBitSet(len);
        Integer[] idx = new Integer[((int) (Math.floor((len / 2))))];
        IcedBitSetTest.fill(bs, idx);
        Assert.assertEquals(bs.size(), 32);
        IcedBitSetTest.check(bs, 0, idx);
    }

    @Test
    public void fill17() {
        int len = 17;
        IcedBitSet bs = new IcedBitSet(len);
        Integer[] idx = new Integer[((int) (Math.floor((len / 2))))];
        IcedBitSetTest.fill(bs, idx);
        Assert.assertEquals(bs.size(), 32);
        IcedBitSetTest.check(bs, 0, idx);
    }

    @Test
    public void fill16() {
        int len = 16;
        IcedBitSet bs = new IcedBitSet(len);
        Integer[] idx = new Integer[((int) (Math.floor((len / 2))))];
        IcedBitSetTest.fill(bs, idx);
        Assert.assertEquals(bs.size(), 32);
        IcedBitSetTest.check(bs, 0, idx);
    }

    @Test
    public void fill32() {
        int len = 32;
        IcedBitSet bs = new IcedBitSet(len);
        Integer[] idx = new Integer[((int) (Math.floor((len / 2))))];
        IcedBitSetTest.fill(bs, idx);
        Assert.assertEquals(bs.size(), len);
        IcedBitSetTest.check(bs, 0, idx);
    }

    @Test
    public void fill33() {
        int len = 33;
        IcedBitSet bs = new IcedBitSet(len);
        Integer[] idx = new Integer[((int) (Math.floor((len / 2))))];
        IcedBitSetTest.fill(bs, idx);
        Assert.assertEquals(bs.size(), Math.max(32, len));
        IcedBitSetTest.check(bs, 0, idx);
    }

    @Test
    public void fillHalf() {
        int len = 10 + ((int) (10000 * (new Random().nextDouble())));
        IcedBitSet bs = new IcedBitSet(len);
        Integer[] idx = new Integer[((int) (Math.floor((len / 2))))];
        IcedBitSetTest.fill(bs, idx);
        Assert.assertEquals(bs.size(), Math.max(32, len));
        IcedBitSetTest.check(bs, 0, idx);
    }

    @Test
    public void fillSparse() {
        int len = 10 + ((int) (10000 * (new Random().nextDouble())));
        IcedBitSet bs = new IcedBitSet(len);
        Integer[] idx = new Integer[((int) (Math.floor((len / 200))))];
        IcedBitSetTest.fill(bs, idx);
        Assert.assertEquals(bs.size(), Math.max(32, len));
        IcedBitSetTest.check(bs, 0, idx);
    }

    @Test
    public void clear() {
        int len = 10 + ((int) (10000 * (new Random().nextDouble())));
        IcedBitSet bs = new IcedBitSet(len);
        Integer[] idx = new Integer[((int) (Math.floor((len / 200))))];
        IcedBitSetTest.fill(bs, idx);
        Assert.assertEquals(bs.size(), Math.max(32, len));
        IcedBitSetTest.check(bs, 0, idx);
        for (Integer I : idx)
            bs.clear(I);

        IcedBitSetTest.check(bs, 0, new Integer[]{  });
    }
}

