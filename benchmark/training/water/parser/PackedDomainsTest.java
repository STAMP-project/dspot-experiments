package water.parser;


import org.junit.Assert;
import org.junit.Test;


/**
 * Test suite for PackedDomains
 *
 * Created by vpatryshev on 4/12/17.
 */
public class PackedDomainsTest {
    @Test
    public void testSizeOf() throws Exception {
        Assert.assertEquals(12345, sizeOf(new byte[]{ 57, 48, 0, 0, 5 }));
    }

    @Test
    public void testAsArrayOfStrings() throws Exception {
        final byte[] packed = PackedDomainsTest.pack("", "abc", "?", "", "X");
        final String[] actuals = unpackToStrings(packed);
        Assert.assertArrayEquals(new String[]{ "", "abc", "?", "", "X" }, actuals);
    }

    @Test
    public void testPack() throws Exception {
        byte[] packed = PackedDomainsTest.pack("", "abc", "?", "", "X");
        Assert.assertArrayEquals(new byte[]{ 5, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 97, 98, 99, 3, 0, 0, 0, -30, -120, -98, 0, 0, 0, 0, 1, 0, 0, 0, 88 }, packed);
    }

    @Test
    public void testPack1() throws Exception {
        BufferedString bs = new BufferedString("efabc");
        bs.addBuff("def".getBytes());
        bs.setOff(2);
        bs.setLen(3);
        byte[] packed = PackedDomains.PackedDomains.pack(new BufferedString[]{ new BufferedString(""), bs, new BufferedString("?"), new BufferedString(""), new BufferedString("X") });
        Assert.assertArrayEquals(PackedDomainsTest.pack("", "abc", "?", "", "X"), packed);
    }

    @Test
    public void testPackStringWithOffset() {
        BufferedString bs = new BufferedString("LeftMiddleRight".getBytes(), "Left".length(), "Middle".length());
        byte[] packed = PackedDomains.PackedDomains.pack(new BufferedString[]{ bs });
        String[] unpacked = unpackToStrings(packed);
        Assert.assertArrayEquals(new String[]{ "Middle" }, unpacked);
    }

    private String[] empty = new String[0];

    private String[] first = new String[]{ "", "ANNIHILATION", "Zoo" };

    private String[] second = new String[]{ "aardvark", "absolute", "neo", "x", "xyzzy" };

    private String[] third = new String[]{ "", "abacus", "neolution", "x", "zambezi" };

    private String[] allWords = new String[]{ "", "ANNIHILATION", "Zoo", "aardvark", "abacus", "absolute", "neo", "neolution", "x", "xyzzy", "zambezi" };

    @Test
    public void testMergeEmpties() throws Exception {
        Assert.assertArrayEquals(PackedDomainsTest.pack(empty), PackedDomainsTest.merge(empty, empty));
        Assert.assertArrayEquals(PackedDomainsTest.pack(second), PackedDomainsTest.merge(empty, second));
        Assert.assertArrayEquals(PackedDomainsTest.pack(second), PackedDomainsTest.merge(second, empty));
    }

    @Test
    public void testCalcMergedSize12() throws Exception {
        int size = PackedDomainsTest.calcMergedSize(first, second);
        Assert.assertEquals(PackedDomainsTest.pack("", "ANNIHILATION", "Zoo", "aardvark", "absolute", "neo", "x", "xyzzy").length, size);
    }

    @Test
    public void testMerge12() throws Exception {
        final byte[] merged = PackedDomainsTest.merge(first, second);
        Assert.assertArrayEquals(new String[]{ "", "ANNIHILATION", "Zoo", "aardvark", "absolute", "neo", "x", "xyzzy" }, unpackToStrings(merged));
    }

    @Test
    public void testCalcMergedSize23() throws Exception {
        int size = PackedDomainsTest.calcMergedSize(second, third);
        Assert.assertEquals(PackedDomainsTest.pack("", "aardvark", "abacus", "absolute", "neo", "neolution", "x", "xyzzy", "zambezi").length, size);
    }

    @Test
    public void testMerge23() throws Exception {
        final byte[] merged = PackedDomainsTest.merge(second, third);
        Assert.assertArrayEquals(new String[]{ "", "aardvark", "abacus", "absolute", "neo", "neolution", "x", "xyzzy", "zambezi" }, unpackToStrings(merged));
    }

    @Test
    public void testMerge3() throws Exception {
        Assert.assertArrayEquals(allWords, unpackToStrings(PackedDomains.PackedDomains.merge(PackedDomainsTest.pack(third), PackedDomainsTest.merge(first, second))));
        Assert.assertArrayEquals(allWords, unpackToStrings(PackedDomains.PackedDomains.merge(PackedDomainsTest.pack(first), PackedDomainsTest.merge(second, third))));
    }

    @Test
    public void testMergeIdempotent() throws Exception {
        Assert.assertArrayEquals(PackedDomainsTest.pack(first), PackedDomainsTest.merge(first, first));
        Assert.assertArrayEquals(PackedDomainsTest.pack(second), PackedDomainsTest.merge(second, second));
        Assert.assertArrayEquals(PackedDomainsTest.pack(third), PackedDomainsTest.merge(third, third));
    }
}

