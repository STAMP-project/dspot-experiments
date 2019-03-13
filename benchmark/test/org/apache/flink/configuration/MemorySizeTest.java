/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.configuration;


import java.io.IOException;
import org.apache.flink.core.testutils.CommonTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link MemorySize} class.
 */
public class MemorySizeTest {
    @Test
    public void testUnitConversion() {
        final MemorySize zero = new MemorySize(0);
        Assert.assertEquals(0, zero.getBytes());
        Assert.assertEquals(0, zero.getKibiBytes());
        Assert.assertEquals(0, zero.getMebiBytes());
        Assert.assertEquals(0, zero.getGibiBytes());
        Assert.assertEquals(0, zero.getTebiBytes());
        final MemorySize bytes = new MemorySize(955);
        Assert.assertEquals(955, bytes.getBytes());
        Assert.assertEquals(0, bytes.getKibiBytes());
        Assert.assertEquals(0, bytes.getMebiBytes());
        Assert.assertEquals(0, bytes.getGibiBytes());
        Assert.assertEquals(0, bytes.getTebiBytes());
        final MemorySize kilos = new MemorySize(18500);
        Assert.assertEquals(18500, kilos.getBytes());
        Assert.assertEquals(18, kilos.getKibiBytes());
        Assert.assertEquals(0, kilos.getMebiBytes());
        Assert.assertEquals(0, kilos.getGibiBytes());
        Assert.assertEquals(0, kilos.getTebiBytes());
        final MemorySize megas = new MemorySize(((15 * 1024) * 1024));
        Assert.assertEquals(15728640, megas.getBytes());
        Assert.assertEquals(15360, megas.getKibiBytes());
        Assert.assertEquals(15, megas.getMebiBytes());
        Assert.assertEquals(0, megas.getGibiBytes());
        Assert.assertEquals(0, megas.getTebiBytes());
        final MemorySize teras = new MemorySize((((((2L * 1024) * 1024) * 1024) * 1024) + 10));
        Assert.assertEquals(2199023255562L, teras.getBytes());
        Assert.assertEquals(2147483648L, teras.getKibiBytes());
        Assert.assertEquals(2097152, teras.getMebiBytes());
        Assert.assertEquals(2048, teras.getGibiBytes());
        Assert.assertEquals(2, teras.getTebiBytes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalid() {
        new MemorySize((-1));
    }

    @Test
    public void testStandardUtils() throws IOException {
        final MemorySize size = new MemorySize(1234567890L);
        final MemorySize cloned = CommonTestUtils.createCopySerializable(size);
        Assert.assertEquals(size, cloned);
        Assert.assertEquals(size.hashCode(), cloned.hashCode());
        Assert.assertEquals(size.toString(), cloned.toString());
    }

    @Test
    public void testParseBytes() {
        Assert.assertEquals(1234, MemorySize.parseBytes("1234"));
        Assert.assertEquals(1234, MemorySize.parseBytes("1234b"));
        Assert.assertEquals(1234, MemorySize.parseBytes("1234 b"));
        Assert.assertEquals(1234, MemorySize.parseBytes("1234bytes"));
        Assert.assertEquals(1234, MemorySize.parseBytes("1234 bytes"));
    }

    @Test
    public void testParseKibiBytes() {
        Assert.assertEquals(667766, MemorySize.parse("667766k").getKibiBytes());
        Assert.assertEquals(667766, MemorySize.parse("667766 k").getKibiBytes());
        Assert.assertEquals(667766, MemorySize.parse("667766kb").getKibiBytes());
        Assert.assertEquals(667766, MemorySize.parse("667766 kb").getKibiBytes());
        Assert.assertEquals(667766, MemorySize.parse("667766kibibytes").getKibiBytes());
        Assert.assertEquals(667766, MemorySize.parse("667766 kibibytes").getKibiBytes());
    }

    @Test
    public void testParseMebiBytes() {
        Assert.assertEquals(7657623, MemorySize.parse("7657623m").getMebiBytes());
        Assert.assertEquals(7657623, MemorySize.parse("7657623 m").getMebiBytes());
        Assert.assertEquals(7657623, MemorySize.parse("7657623mb").getMebiBytes());
        Assert.assertEquals(7657623, MemorySize.parse("7657623 mb").getMebiBytes());
        Assert.assertEquals(7657623, MemorySize.parse("7657623mebibytes").getMebiBytes());
        Assert.assertEquals(7657623, MemorySize.parse("7657623 mebibytes").getMebiBytes());
    }

    @Test
    public void testParseGibiBytes() {
        Assert.assertEquals(987654, MemorySize.parse("987654g").getGibiBytes());
        Assert.assertEquals(987654, MemorySize.parse("987654 g").getGibiBytes());
        Assert.assertEquals(987654, MemorySize.parse("987654gb").getGibiBytes());
        Assert.assertEquals(987654, MemorySize.parse("987654 gb").getGibiBytes());
        Assert.assertEquals(987654, MemorySize.parse("987654gibibytes").getGibiBytes());
        Assert.assertEquals(987654, MemorySize.parse("987654 gibibytes").getGibiBytes());
    }

    @Test
    public void testParseTebiBytes() {
        Assert.assertEquals(1234567, MemorySize.parse("1234567t").getTebiBytes());
        Assert.assertEquals(1234567, MemorySize.parse("1234567 t").getTebiBytes());
        Assert.assertEquals(1234567, MemorySize.parse("1234567tb").getTebiBytes());
        Assert.assertEquals(1234567, MemorySize.parse("1234567 tb").getTebiBytes());
        Assert.assertEquals(1234567, MemorySize.parse("1234567tebibytes").getTebiBytes());
        Assert.assertEquals(1234567, MemorySize.parse("1234567 tebibytes").getTebiBytes());
    }

    @Test
    public void testUpperCase() {
        Assert.assertEquals(1L, MemorySize.parse("1 B").getBytes());
        Assert.assertEquals(1L, MemorySize.parse("1 K").getKibiBytes());
        Assert.assertEquals(1L, MemorySize.parse("1 M").getMebiBytes());
        Assert.assertEquals(1L, MemorySize.parse("1 G").getGibiBytes());
        Assert.assertEquals(1L, MemorySize.parse("1 T").getTebiBytes());
    }

    @Test
    public void testTrimBeforeParse() {
        Assert.assertEquals(155L, MemorySize.parseBytes("      155      "));
        Assert.assertEquals(155L, MemorySize.parseBytes("      155      bytes   "));
    }

    @Test
    public void testParseInvalid() {
        // null
        try {
            MemorySize.parseBytes(null);
            Assert.fail("exception expected");
        } catch (NullPointerException ignored) {
        }
        // empty
        try {
            MemorySize.parseBytes("");
            Assert.fail("exception expected");
        } catch (IllegalArgumentException ignored) {
        }
        // blank
        try {
            MemorySize.parseBytes("     ");
            Assert.fail("exception expected");
        } catch (IllegalArgumentException ignored) {
        }
        // no number
        try {
            MemorySize.parseBytes("foobar or fubar or foo bazz");
            Assert.fail("exception expected");
        } catch (IllegalArgumentException ignored) {
        }
        // wrong unit
        try {
            MemorySize.parseBytes("16 gjah");
            Assert.fail("exception expected");
        } catch (IllegalArgumentException ignored) {
        }
        // multiple numbers
        try {
            MemorySize.parseBytes("16 16 17 18 bytes");
            Assert.fail("exception expected");
        } catch (IllegalArgumentException ignored) {
        }
        // negative number
        try {
            MemorySize.parseBytes("-100 bytes");
            Assert.fail("exception expected");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseNumberOverflow() {
        MemorySize.parseBytes("100000000000000000000000000000000 bytes");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseNumberTimeUnitOverflow() {
        MemorySize.parseBytes("100000000000000 tb");
    }

    @Test
    public void testParseWithDefaultUnit() {
        Assert.assertEquals(7, MemorySize.parse("7", MemoryUnit.MEGA_BYTES).getMebiBytes());
        Assert.assertNotEquals(7, MemorySize.parse("7340032", MemoryUnit.MEGA_BYTES));
        Assert.assertEquals(7, MemorySize.parse("7m", MemoryUnit.MEGA_BYTES).getMebiBytes());
        Assert.assertEquals(7168, MemorySize.parse("7", MemoryUnit.MEGA_BYTES).getKibiBytes());
        Assert.assertEquals(7168, MemorySize.parse("7m", MemoryUnit.MEGA_BYTES).getKibiBytes());
        Assert.assertEquals(7, MemorySize.parse("7 m", MemoryUnit.MEGA_BYTES).getMebiBytes());
        Assert.assertEquals(7, MemorySize.parse("7mb", MemoryUnit.MEGA_BYTES).getMebiBytes());
        Assert.assertEquals(7, MemorySize.parse("7 mb", MemoryUnit.MEGA_BYTES).getMebiBytes());
        Assert.assertEquals(7, MemorySize.parse("7mebibytes", MemoryUnit.MEGA_BYTES).getMebiBytes());
        Assert.assertEquals(7, MemorySize.parse("7 mebibytes", MemoryUnit.MEGA_BYTES).getMebiBytes());
    }
}

