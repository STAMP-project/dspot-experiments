/**
 * This file is part of Bisq.
 *
 * bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with bisq. If not, see <http://www.gnu.org/licenses/>.
 */
package bisq.common.util;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class UtilitiesTest {
    @Test
    public void testConcatenateByteArrays() {
        Assert.assertTrue(Arrays.equals(new byte[]{ 1, 2 }, Utilities.concatenateByteArrays(new byte[]{ 1 }, new byte[]{ 2 })));
        Assert.assertTrue(Arrays.equals(new byte[]{ 1, 2, 3 }, Utilities.concatenateByteArrays(new byte[]{ 1 }, new byte[]{ 2 }, new byte[]{ 3 })));
        Assert.assertTrue(Arrays.equals(new byte[]{ 1, 2, 3, 4 }, Utilities.concatenateByteArrays(new byte[]{ 1 }, new byte[]{ 2 }, new byte[]{ 3 }, new byte[]{ 4 })));
        Assert.assertTrue(Arrays.equals(new byte[]{ 1, 2, 3, 4, 5 }, Utilities.concatenateByteArrays(new byte[]{ 1 }, new byte[]{ 2 }, new byte[]{ 3 }, new byte[]{ 4 }, new byte[]{ 5 })));
    }

    @Test
    public void testToStringList() {
        Assert.assertTrue(Utilities.commaSeparatedListToSet(null, false).isEmpty());
        Assert.assertTrue(Utilities.commaSeparatedListToSet(null, true).isEmpty());
        Assert.assertTrue(Utilities.commaSeparatedListToSet("", false).isEmpty());
        Assert.assertTrue(Utilities.commaSeparatedListToSet("", true).isEmpty());
        Assert.assertTrue(Utilities.commaSeparatedListToSet(" ", false).isEmpty());
        Assert.assertEquals(1, Utilities.commaSeparatedListToSet(" ", true).size());
        Assert.assertTrue(Utilities.commaSeparatedListToSet(",", false).isEmpty());
        Assert.assertTrue(Utilities.commaSeparatedListToSet(",", true).isEmpty());
        Assert.assertEquals(1, Utilities.commaSeparatedListToSet(",test1", false).size());
        Assert.assertEquals(1, Utilities.commaSeparatedListToSet(", , test1", false).size());
        Assert.assertEquals(2, Utilities.commaSeparatedListToSet(", , test1", true).size());
        Assert.assertEquals(1, Utilities.commaSeparatedListToSet("test1,", false).size());
        Assert.assertEquals(1, Utilities.commaSeparatedListToSet("test1, ,", false).size());
        Assert.assertEquals(1, Utilities.commaSeparatedListToSet("test1", false).size());
        Assert.assertEquals(2, Utilities.commaSeparatedListToSet("test1, test2", false).size());
    }

    @Test
    public void testIntegerToByteArray() {
        Assert.assertEquals("0000", Utilities.bytesAsHexString(Utilities.integerToByteArray(0, 2)));
        Assert.assertEquals("ffff", Utilities.bytesAsHexString(Utilities.integerToByteArray(65535, 2)));
        Assert.assertEquals("0011", Utilities.bytesAsHexString(Utilities.integerToByteArray(17, 2)));
        Assert.assertEquals("1100", Utilities.bytesAsHexString(Utilities.integerToByteArray(4352, 2)));
        Assert.assertEquals("dd22", Utilities.bytesAsHexString(Utilities.integerToByteArray(56610, 2)));
        Assert.assertEquals("7fffffff", Utilities.bytesAsHexString(Utilities.integerToByteArray(2147483647, 4)));// Integer.MAX_VALUE

        Assert.assertEquals("80000000", Utilities.bytesAsHexString(Utilities.integerToByteArray(-2147483648, 4)));// Integer.MIN_VALUE

        Assert.assertEquals("00110011", Utilities.bytesAsHexString(Utilities.integerToByteArray(1114129, 4)));
        Assert.assertEquals("ffeeffef", Utilities.bytesAsHexString(Utilities.integerToByteArray((-1114129), 4)));
    }

    @Test
    public void testByteArrayToInteger() {
        Assert.assertEquals(0, Utilities.byteArrayToInteger(Utilities.decodeFromHex("0000")));
        Assert.assertEquals(65535, Utilities.byteArrayToInteger(Utilities.decodeFromHex("ffff")));
        Assert.assertEquals(4352, Utilities.byteArrayToInteger(Utilities.decodeFromHex("1100")));
        Assert.assertEquals(17, Utilities.byteArrayToInteger(Utilities.decodeFromHex("0011")));
        Assert.assertEquals(56610, Utilities.byteArrayToInteger(Utilities.decodeFromHex("dd22")));
        Assert.assertEquals(2147483647, Utilities.byteArrayToInteger(Utilities.decodeFromHex("7fffffff")));
        Assert.assertEquals(-2147483648, Utilities.byteArrayToInteger(Utilities.decodeFromHex("80000000")));
        Assert.assertEquals(1114129, Utilities.byteArrayToInteger(Utilities.decodeFromHex("00110011")));
        Assert.assertEquals((-1114129), Utilities.byteArrayToInteger(Utilities.decodeFromHex("ffeeffef")));
    }
}

