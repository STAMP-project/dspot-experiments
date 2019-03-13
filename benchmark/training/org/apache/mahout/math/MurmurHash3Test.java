/**
 * This code is public domain.
 *
 *  The MurmurHash3 algorithm was created by Austin Appleby and put into the public domain.
 *  See http://code.google.com/p/smhasher/
 *
 *  This java port was authored by
 *  Yonik Seeley and was placed into the public domain per
 *  https://github.com/yonik/java_util/blob/master/src/util/hash/MurmurHash3.java.
 */
package org.apache.mahout.math;


import org.junit.Test;


public final class MurmurHash3Test extends MahoutTestCase {
    private static final int[] ANSWERS = new int[]{ 0, -811802586, 2065612011, -1973820300, -818708588, -1956147354, -2126514782, 472861483, -1493386633, -430376696, -1624877393, -2106882614, -340341046, 616829228, 1727002032, -1448302635, -135246589, -1653075707, 1842210035, 1777195176, 1557976087, 26044406, -368833392, -546248118, -1884075546, 2041092867, 2130312244, 1612183186, 1541415000, -1640397063, -1884889816, 1731443091, 639831346, -1845158551, -1922091017, -1587203837, -2070822337, 781146574, 1525237278, 266503910, -2075236007, -2014771619, -486350308, 456864406, -761771575, 1315069734, -1463592256, 1899190790, -47878769, 1314828888, 1310925009, -1171351502, -1341261589, -1115203251, -2085167901, -813283632, 2121921250, -736113257, 5597843, 1142506363, 967446094, 1134916177, -1808756177, -679169905, -1569250030, -1566118325, -1666077778, -338510096 };

    @Test
    public void testCorrectValues() throws Exception {
        byte[] bytes = "Now is the time for all good men to come to the aid of their country".getBytes("UTF-8");
        int hash = 0;
        for (int i = 0; i < (bytes.length); i++) {
            hash = (hash * 31) + ((bytes[i]) & 255);
            bytes[i] = ((byte) (hash));
        }
        // test different offsets.
        for (int offset = 0; offset < 10; offset++) {
            byte[] arr = new byte[(bytes.length) + offset];
            System.arraycopy(bytes, 0, arr, offset, bytes.length);
            for (int len = 0; len < (bytes.length); len++) {
                int h = MurmurHash3.murmurhash3x8632(arr, offset, len, len);
                assertEquals(MurmurHash3Test.ANSWERS[len], h);
            }
        }
    }
}

