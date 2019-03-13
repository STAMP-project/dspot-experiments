package buildcraft.test.lib.misc;


import NbtSquishConstants.VANILLA;
import buildcraft.lib.misc.HashUtil;
import buildcraft.lib.nbt.NbtSquisher;
import buildcraft.test.lib.nbt.NbtSquisherTester;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class HashUtilTester {
    private static final byte[] HASH = new byte[]{ 0, 1, 5, 9, ((byte) (255)), ((byte) (188)) };

    private static final String STR = "00010509ffbc";

    private static final byte[] DATA;

    // Hardcoded hash value from previous runs
    private static final byte[] DATA_HASH;

    static {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        NbtSquisher.squish(NbtSquisherTester.nbtSmall, VANILLA);
        DATA = baos.toByteArray();
        DATA_HASH = HashUtil.convertStringToHash("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
    }

    @Test
    public void testStringToHash() {
        byte[] hash = HashUtil.convertStringToHash(HashUtilTester.STR);
        Assert.assertArrayEquals(HashUtilTester.HASH, hash);
    }

    @Test
    public void testHashToString() {
        String str = HashUtil.convertHashToString(HashUtilTester.HASH);
        Assert.assertEquals(HashUtilTester.STR, str);
    }

    @Test
    public void testHashBasic() {
        System.out.println(Arrays.toString(HashUtilTester.DATA));
        // We can't test this hash against anything -- the bpt format itself
        byte[] hash = HashUtil.computeHash(HashUtilTester.DATA);
        System.out.println(HashUtil.convertHashToString(hash));
        Assert.assertArrayEquals(HashUtilTester.DATA_HASH, hash);
    }
}

