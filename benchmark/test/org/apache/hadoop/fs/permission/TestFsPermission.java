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
package org.apache.hadoop.fs.permission;


import FsPermission.UMASK_LABEL;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;


public class TestFsPermission {
    @Test
    public void testFsAction() {
        // implies
        for (FsAction.FsAction a : FsAction.FsAction.values()) {
            Assert.assertTrue(ALL.implies(a));
        }
        for (FsAction.FsAction a : FsAction.FsAction.values()) {
            Assert.assertTrue((a == (NONE) ? NONE.implies(a) : !(NONE.implies(a))));
        }
        for (FsAction.FsAction a : FsAction.FsAction.values()) {
            Assert.assertTrue(((((a == (READ_EXECUTE)) || (a == (READ))) || (a == (EXECUTE))) || (a == (NONE)) ? READ_EXECUTE.implies(a) : !(READ_EXECUTE.implies(a))));
        }
        // masks
        Assert.assertEquals(EXECUTE, EXECUTE.and(READ_EXECUTE));
        Assert.assertEquals(READ, READ.and(READ_EXECUTE));
        Assert.assertEquals(NONE, WRITE.and(READ_EXECUTE));
        Assert.assertEquals(READ, READ_EXECUTE.and(READ_WRITE));
        Assert.assertEquals(NONE, READ_EXECUTE.and(WRITE));
        Assert.assertEquals(WRITE_EXECUTE, ALL.and(WRITE_EXECUTE));
    }

    /**
     * Ensure that when manually specifying permission modes we get
     * the expected values back out for all combinations
     */
    @Test
    public void testConvertingPermissions() {
        for (short s = 0; s <= 1023; s++) {
            Assert.assertEquals(s, new FsPermission(s).toShort());
            // check string formats
            Assert.assertEquals(s, new FsPermission(String.format("%03o", s)).toShort());
        }
        short s = 0;
        for (boolean sb : new boolean[]{ false, true }) {
            for (FsAction.FsAction u : FsAction.FsAction.values()) {
                for (FsAction.FsAction g : FsAction.FsAction.values()) {
                    for (FsAction.FsAction o : FsAction.FsAction.values()) {
                        // Cover constructor with sticky bit.
                        FsPermission f = new FsPermission(u, g, o, sb);
                        Assert.assertEquals(s, f.toShort());
                        FsPermission f2 = new FsPermission(f);
                        Assert.assertEquals(s, f2.toShort());
                        s++;
                    }
                }
            }
        }
        Assert.assertEquals(1024, s);
    }

    @Test
    public void testSpecialBitsToString() {
        for (boolean sb : new boolean[]{ false, true }) {
            for (FsAction.FsAction u : FsAction.FsAction.values()) {
                for (FsAction.FsAction g : FsAction.FsAction.values()) {
                    for (FsAction.FsAction o : FsAction.FsAction.values()) {
                        FsPermission f = new FsPermission(u, g, o, sb);
                        String fString = f.toString();
                        // Check that sticky bit is represented correctly.
                        if ((f.getStickyBit()) && (f.getOtherAction().implies(EXECUTE)))
                            Assert.assertEquals('t', fString.charAt(8));
                        else
                            if ((f.getStickyBit()) && (!(f.getOtherAction().implies(EXECUTE))))
                                Assert.assertEquals('T', fString.charAt(8));
                            else
                                if ((!(f.getStickyBit())) && (f.getOtherAction().implies(EXECUTE)))
                                    Assert.assertEquals('x', fString.charAt(8));
                                else
                                    Assert.assertEquals('-', fString.charAt(8));



                        Assert.assertEquals(9, fString.length());
                    }
                }
            }
        }
    }

    @Test
    public void testFsPermission() {
        String symbolic = "-rwxrwxrwx";
        for (int i = 0; i < (1 << 10); i++) {
            StringBuilder b = new StringBuilder("----------");
            String binary = String.format("%11s", Integer.toBinaryString(i));
            String permBinary = binary.substring(2, binary.length());
            int len = permBinary.length();
            for (int j = 0; j < len; j++) {
                if ((permBinary.charAt(j)) == '1') {
                    int k = 9 - ((len - 1) - j);
                    b.setCharAt(k, symbolic.charAt(k));
                }
            }
            // Check for sticky bit.
            if ((binary.charAt(1)) == '1') {
                char replacement = ((b.charAt(9)) == 'x') ? 't' : 'T';
                b.setCharAt(9, replacement);
            }
            Assert.assertEquals(i, FsPermission.valueOf(b.toString()).toShort());
        }
    }

    @Test
    public void testFsSymbolicConstructorWithNormalInput() {
        // Test cases for symbolic representation
        // Added both Octal and short representation to show with sticky bit
        Assert.assertEquals(777, new FsPermission("+rwx").toOctal());
        Assert.assertEquals(511, new FsPermission("+rwx").toShort());
        Assert.assertEquals(444, new FsPermission("+r").toOctal());
        Assert.assertEquals(292, new FsPermission("+r").toShort());
        Assert.assertEquals(222, new FsPermission("+w").toOctal());
        Assert.assertEquals(146, new FsPermission("+w").toShort());
        Assert.assertEquals(111, new FsPermission("+x").toOctal());
        Assert.assertEquals(73, new FsPermission("+x").toShort());
        Assert.assertEquals(666, new FsPermission("+rw").toOctal());
        Assert.assertEquals(438, new FsPermission("+rw").toShort());
        Assert.assertEquals(333, new FsPermission("+wx").toOctal());
        Assert.assertEquals(219, new FsPermission("+wx").toShort());
        Assert.assertEquals(555, new FsPermission("+rx").toOctal());
        Assert.assertEquals(365, new FsPermission("+rx").toShort());
        // Test case is to test with repeated values in mode.
        // Repeated value in input will be ignored as duplicate.
        Assert.assertEquals(666, new FsPermission("+rwr").toOctal());
        Assert.assertEquals(438, new FsPermission("+rwr").toShort());
        Assert.assertEquals(0, new FsPermission("-rwr").toOctal());
        Assert.assertEquals(0, new FsPermission("-rwr").toShort());
        Assert.assertEquals(1666, new FsPermission("+rwrt").toOctal());
        Assert.assertEquals(950, new FsPermission("+rwrt").toShort());
        Assert.assertEquals(0, new FsPermission("-rwrt").toOctal());
        Assert.assertEquals(0, new FsPermission("-rwrt").toShort());
        Assert.assertEquals(1777, new FsPermission("+rwxt").toOctal());
        Assert.assertEquals(1023, new FsPermission("+rwxt").toShort());
        Assert.assertEquals(0, new FsPermission("-rt").toOctal());
        Assert.assertEquals(0, new FsPermission("-rt").toShort());
        Assert.assertEquals(0, new FsPermission("-rwx").toOctal());
        Assert.assertEquals(0, new FsPermission("-rwx").toShort());
    }

    @Test
    public void testSymbolicPermission() {
        for (int i = 0; i < (TestFsPermission.SYMBOLIC.length); ++i) {
            short val = 511;
            val &= ~(Short.valueOf(TestFsPermission.SYMBOLIC[i][1], 8));
            Assert.assertEquals(val, new FsPermission(TestFsPermission.SYMBOLIC[i][0]).toShort());
        }
        // add sticky bit to "other" when exec enabled
        for (int i = 1; i < (TestFsPermission.SYMBOLIC.length); i += 2) {
            short val = 1023;
            val &= ~(Short.valueOf(TestFsPermission.SYMBOLIC[i][1], 8));
            Assert.assertEquals(val, new FsPermission(((TestFsPermission.SYMBOLIC[i][0]) + "t")).toShort());
        }
    }

    @Test
    public void testUMaskParser() throws IOException {
        Configuration conf = new Configuration();
        // Ensure that we get the right octal values back for all legal values
        for (FsAction.FsAction u : FsAction.FsAction.values()) {
            for (FsAction.FsAction g : FsAction.FsAction.values()) {
                for (FsAction.FsAction o : FsAction.FsAction.values()) {
                    FsPermission f = new FsPermission(u, g, o);
                    String asOctal = String.format("%1$03o", f.toShort());
                    conf.set(UMASK_LABEL, asOctal);
                    FsPermission fromConf = FsPermission.getUMask(conf);
                    Assert.assertEquals(f, fromConf);
                }
            }
        }
    }

    @Test
    public void testSymbolicUmasks() {
        Configuration conf = new Configuration();
        // Test some symbolic to octal settings
        for (int i = 0; i < (TestFsPermission.SYMBOLIC.length); ++i) {
            conf.set(UMASK_LABEL, TestFsPermission.SYMBOLIC[i][0]);
            short val = Short.valueOf(TestFsPermission.SYMBOLIC[i][1], 8);
            Assert.assertEquals(val, FsPermission.getUMask(conf).toShort());
        }
        conf.set(UMASK_LABEL, "a+rw");
        Assert.assertEquals(73, FsPermission.getUMask(conf).toShort());
    }

    @Test
    public void testBadUmasks() {
        Configuration conf = new Configuration();
        for (String b : new String[]{ "1777", "22", "99", "foo", "" }) {
            conf.set(UMASK_LABEL, b);
            try {
                FsPermission.getUMask(conf);
                Assert.fail("Shouldn't have been able to parse bad umask");
            } catch (IllegalArgumentException iae) {
                Assert.assertTrue(("Exception should specify parsing error and invalid umask: " + (iae.getMessage())), isCorrectExceptionMessage(iae.getMessage(), b));
            }
        }
    }

    /**
     * test FsPermission(int) constructor.
     */
    @Test
    public void testIntPermission() {
        // Octal           Decimals        Masked OCT      Masked DEC
        // 100644          33188           644             420
        // 101644          33700           1644            932
        // 40644           16804           644             420
        // 41644           17316           1644            932
        // 644             420             644             420
        // 1644            932             1644            932
        int[][] permission_mask_maps = new int[][]{ // Octal                 Decimal    Unix Symbolic
        new int[]{ 33188, 420, 0 }// 33188    -rw-r--
        // 33188    -rw-r--
        // 33188    -rw-r--
        , new int[]{ 33700, 932, 1 }// 33700    -rw-r-t
        // 33700    -rw-r-t
        // 33700    -rw-r-t
        , new int[]{ 16804, 420, 0 }// 16804    drw-r--
        // 16804    drw-r--
        // 16804    drw-r--
        , new int[]{ 17316, 932, 1 }// 17316    drw-r-t
        // 17316    drw-r-t
        // 17316    drw-r-t
         };
        for (int[] permission_mask_map : permission_mask_maps) {
            int original_permission_value = permission_mask_map[0];
            int masked_permission_value = permission_mask_map[1];
            boolean hasStickyBit = (permission_mask_map[2]) == 1;
            FsPermission fsPermission = new FsPermission(original_permission_value);
            Assert.assertEquals(masked_permission_value, fsPermission.toShort());
            Assert.assertEquals(hasStickyBit, fsPermission.getStickyBit());
        }
    }

    // Symbolic umask list is generated in linux shell using by the command:
    // umask 0; umask <octal number>; umask -S
    static final String[][] SYMBOLIC = new String[][]{ new String[]{ "u=rwx,g=rwx,o=rwx", "0" }, new String[]{ "u=rwx,g=rwx,o=rw", "1" }, new String[]{ "u=rwx,g=rwx,o=rx", "2" }, new String[]{ "u=rwx,g=rwx,o=r", "3" }, new String[]{ "u=rwx,g=rwx,o=wx", "4" }, new String[]{ "u=rwx,g=rwx,o=w", "5" }, new String[]{ "u=rwx,g=rwx,o=x", "6" }, new String[]{ "u=rwx,g=rwx,o=", "7" }, new String[]{ "u=rwx,g=rw,o=rwx", "10" }, new String[]{ "u=rwx,g=rw,o=rw", "11" }, new String[]{ "u=rwx,g=rw,o=rx", "12" }, new String[]{ "u=rwx,g=rw,o=r", "13" }, new String[]{ "u=rwx,g=rw,o=wx", "14" }, new String[]{ "u=rwx,g=rw,o=w", "15" }, new String[]{ "u=rwx,g=rw,o=x", "16" }, new String[]{ "u=rwx,g=rw,o=", "17" }, new String[]{ "u=rwx,g=rx,o=rwx", "20" }, new String[]{ "u=rwx,g=rx,o=rw", "21" }, new String[]{ "u=rwx,g=rx,o=rx", "22" }, new String[]{ "u=rwx,g=rx,o=r", "23" }, new String[]{ "u=rwx,g=rx,o=wx", "24" }, new String[]{ "u=rwx,g=rx,o=w", "25" }, new String[]{ "u=rwx,g=rx,o=x", "26" }, new String[]{ "u=rwx,g=rx,o=", "27" }, new String[]{ "u=rwx,g=r,o=rwx", "30" }, new String[]{ "u=rwx,g=r,o=rw", "31" }, new String[]{ "u=rwx,g=r,o=rx", "32" }, new String[]{ "u=rwx,g=r,o=r", "33" }, new String[]{ "u=rwx,g=r,o=wx", "34" }, new String[]{ "u=rwx,g=r,o=w", "35" }, new String[]{ "u=rwx,g=r,o=x", "36" }, new String[]{ "u=rwx,g=r,o=", "37" }, new String[]{ "u=rwx,g=wx,o=rwx", "40" }, new String[]{ "u=rwx,g=wx,o=rw", "41" }, new String[]{ "u=rwx,g=wx,o=rx", "42" }, new String[]{ "u=rwx,g=wx,o=r", "43" }, new String[]{ "u=rwx,g=wx,o=wx", "44" }, new String[]{ "u=rwx,g=wx,o=w", "45" }, new String[]{ "u=rwx,g=wx,o=x", "46" }, new String[]{ "u=rwx,g=wx,o=", "47" }, new String[]{ "u=rwx,g=w,o=rwx", "50" }, new String[]{ "u=rwx,g=w,o=rw", "51" }, new String[]{ "u=rwx,g=w,o=rx", "52" }, new String[]{ "u=rwx,g=w,o=r", "53" }, new String[]{ "u=rwx,g=w,o=wx", "54" }, new String[]{ "u=rwx,g=w,o=w", "55" }, new String[]{ "u=rwx,g=w,o=x", "56" }, new String[]{ "u=rwx,g=w,o=", "57" }, new String[]{ "u=rwx,g=x,o=rwx", "60" }, new String[]{ "u=rwx,g=x,o=rw", "61" }, new String[]{ "u=rwx,g=x,o=rx", "62" }, new String[]{ "u=rwx,g=x,o=r", "63" }, new String[]{ "u=rwx,g=x,o=wx", "64" }, new String[]{ "u=rwx,g=x,o=w", "65" }, new String[]{ "u=rwx,g=x,o=x", "66" }, new String[]{ "u=rwx,g=x,o=", "67" }, new String[]{ "u=rwx,g=,o=rwx", "70" }, new String[]{ "u=rwx,g=,o=rw", "71" }, new String[]{ "u=rwx,g=,o=rx", "72" }, new String[]{ "u=rwx,g=,o=r", "73" }, new String[]{ "u=rwx,g=,o=wx", "74" }, new String[]{ "u=rwx,g=,o=w", "75" }, new String[]{ "u=rwx,g=,o=x", "76" }, new String[]{ "u=rwx,g=,o=", "77" }, new String[]{ "u=rw,g=rwx,o=rwx", "100" }, new String[]{ "u=rw,g=rwx,o=rw", "101" }, new String[]{ "u=rw,g=rwx,o=rx", "102" }, new String[]{ "u=rw,g=rwx,o=r", "103" }, new String[]{ "u=rw,g=rwx,o=wx", "104" }, new String[]{ "u=rw,g=rwx,o=w", "105" }, new String[]{ "u=rw,g=rwx,o=x", "106" }, new String[]{ "u=rw,g=rwx,o=", "107" }, new String[]{ "u=rw,g=rw,o=rwx", "110" }, new String[]{ "u=rw,g=rw,o=rw", "111" }, new String[]{ "u=rw,g=rw,o=rx", "112" }, new String[]{ "u=rw,g=rw,o=r", "113" }, new String[]{ "u=rw,g=rw,o=wx", "114" }, new String[]{ "u=rw,g=rw,o=w", "115" }, new String[]{ "u=rw,g=rw,o=x", "116" }, new String[]{ "u=rw,g=rw,o=", "117" }, new String[]{ "u=rw,g=rx,o=rwx", "120" }, new String[]{ "u=rw,g=rx,o=rw", "121" }, new String[]{ "u=rw,g=rx,o=rx", "122" }, new String[]{ "u=rw,g=rx,o=r", "123" }, new String[]{ "u=rw,g=rx,o=wx", "124" }, new String[]{ "u=rw,g=rx,o=w", "125" }, new String[]{ "u=rw,g=rx,o=x", "126" }, new String[]{ "u=rw,g=rx,o=", "127" }, new String[]{ "u=rw,g=r,o=rwx", "130" }, new String[]{ "u=rw,g=r,o=rw", "131" }, new String[]{ "u=rw,g=r,o=rx", "132" }, new String[]{ "u=rw,g=r,o=r", "133" }, new String[]{ "u=rw,g=r,o=wx", "134" }, new String[]{ "u=rw,g=r,o=w", "135" }, new String[]{ "u=rw,g=r,o=x", "136" }, new String[]{ "u=rw,g=r,o=", "137" }, new String[]{ "u=rw,g=wx,o=rwx", "140" }, new String[]{ "u=rw,g=wx,o=rw", "141" }, new String[]{ "u=rw,g=wx,o=rx", "142" }, new String[]{ "u=rw,g=wx,o=r", "143" }, new String[]{ "u=rw,g=wx,o=wx", "144" }, new String[]{ "u=rw,g=wx,o=w", "145" }, new String[]{ "u=rw,g=wx,o=x", "146" }, new String[]{ "u=rw,g=wx,o=", "147" }, new String[]{ "u=rw,g=w,o=rwx", "150" }, new String[]{ "u=rw,g=w,o=rw", "151" }, new String[]{ "u=rw,g=w,o=rx", "152" }, new String[]{ "u=rw,g=w,o=r", "153" }, new String[]{ "u=rw,g=w,o=wx", "154" }, new String[]{ "u=rw,g=w,o=w", "155" }, new String[]{ "u=rw,g=w,o=x", "156" }, new String[]{ "u=rw,g=w,o=", "157" }, new String[]{ "u=rw,g=x,o=rwx", "160" }, new String[]{ "u=rw,g=x,o=rw", "161" }, new String[]{ "u=rw,g=x,o=rx", "162" }, new String[]{ "u=rw,g=x,o=r", "163" }, new String[]{ "u=rw,g=x,o=wx", "164" }, new String[]{ "u=rw,g=x,o=w", "165" }, new String[]{ "u=rw,g=x,o=x", "166" }, new String[]{ "u=rw,g=x,o=", "167" }, new String[]{ "u=rw,g=,o=rwx", "170" }, new String[]{ "u=rw,g=,o=rw", "171" }, new String[]{ "u=rw,g=,o=rx", "172" }, new String[]{ "u=rw,g=,o=r", "173" }, new String[]{ "u=rw,g=,o=wx", "174" }, new String[]{ "u=rw,g=,o=w", "175" }, new String[]{ "u=rw,g=,o=x", "176" }, new String[]{ "u=rw,g=,o=", "177" }, new String[]{ "u=rx,g=rwx,o=rwx", "200" }, new String[]{ "u=rx,g=rwx,o=rw", "201" }, new String[]{ "u=rx,g=rwx,o=rx", "202" }, new String[]{ "u=rx,g=rwx,o=r", "203" }, new String[]{ "u=rx,g=rwx,o=wx", "204" }, new String[]{ "u=rx,g=rwx,o=w", "205" }, new String[]{ "u=rx,g=rwx,o=x", "206" }, new String[]{ "u=rx,g=rwx,o=", "207" }, new String[]{ "u=rx,g=rw,o=rwx", "210" }, new String[]{ "u=rx,g=rw,o=rw", "211" }, new String[]{ "u=rx,g=rw,o=rx", "212" }, new String[]{ "u=rx,g=rw,o=r", "213" }, new String[]{ "u=rx,g=rw,o=wx", "214" }, new String[]{ "u=rx,g=rw,o=w", "215" }, new String[]{ "u=rx,g=rw,o=x", "216" }, new String[]{ "u=rx,g=rw,o=", "217" }, new String[]{ "u=rx,g=rx,o=rwx", "220" }, new String[]{ "u=rx,g=rx,o=rw", "221" }, new String[]{ "u=rx,g=rx,o=rx", "222" }, new String[]{ "u=rx,g=rx,o=r", "223" }, new String[]{ "u=rx,g=rx,o=wx", "224" }, new String[]{ "u=rx,g=rx,o=w", "225" }, new String[]{ "u=rx,g=rx,o=x", "226" }, new String[]{ "u=rx,g=rx,o=", "227" }, new String[]{ "u=rx,g=r,o=rwx", "230" }, new String[]{ "u=rx,g=r,o=rw", "231" }, new String[]{ "u=rx,g=r,o=rx", "232" }, new String[]{ "u=rx,g=r,o=r", "233" }, new String[]{ "u=rx,g=r,o=wx", "234" }, new String[]{ "u=rx,g=r,o=w", "235" }, new String[]{ "u=rx,g=r,o=x", "236" }, new String[]{ "u=rx,g=r,o=", "237" }, new String[]{ "u=rx,g=wx,o=rwx", "240" }, new String[]{ "u=rx,g=wx,o=rw", "241" }, new String[]{ "u=rx,g=wx,o=rx", "242" }, new String[]{ "u=rx,g=wx,o=r", "243" }, new String[]{ "u=rx,g=wx,o=wx", "244" }, new String[]{ "u=rx,g=wx,o=w", "245" }, new String[]{ "u=rx,g=wx,o=x", "246" }, new String[]{ "u=rx,g=wx,o=", "247" }, new String[]{ "u=rx,g=w,o=rwx", "250" }, new String[]{ "u=rx,g=w,o=rw", "251" }, new String[]{ "u=rx,g=w,o=rx", "252" }, new String[]{ "u=rx,g=w,o=r", "253" }, new String[]{ "u=rx,g=w,o=wx", "254" }, new String[]{ "u=rx,g=w,o=w", "255" }, new String[]{ "u=rx,g=w,o=x", "256" }, new String[]{ "u=rx,g=w,o=", "257" }, new String[]{ "u=rx,g=x,o=rwx", "260" }, new String[]{ "u=rx,g=x,o=rw", "261" }, new String[]{ "u=rx,g=x,o=rx", "262" }, new String[]{ "u=rx,g=x,o=r", "263" }, new String[]{ "u=rx,g=x,o=wx", "264" }, new String[]{ "u=rx,g=x,o=w", "265" }, new String[]{ "u=rx,g=x,o=x", "266" }, new String[]{ "u=rx,g=x,o=", "267" }, new String[]{ "u=rx,g=,o=rwx", "270" }, new String[]{ "u=rx,g=,o=rw", "271" }, new String[]{ "u=rx,g=,o=rx", "272" }, new String[]{ "u=rx,g=,o=r", "273" }, new String[]{ "u=rx,g=,o=wx", "274" }, new String[]{ "u=rx,g=,o=w", "275" }, new String[]{ "u=rx,g=,o=x", "276" }, new String[]{ "u=rx,g=,o=", "277" }, new String[]{ "u=r,g=rwx,o=rwx", "300" }, new String[]{ "u=r,g=rwx,o=rw", "301" }, new String[]{ "u=r,g=rwx,o=rx", "302" }, new String[]{ "u=r,g=rwx,o=r", "303" }, new String[]{ "u=r,g=rwx,o=wx", "304" }, new String[]{ "u=r,g=rwx,o=w", "305" }, new String[]{ "u=r,g=rwx,o=x", "306" }, new String[]{ "u=r,g=rwx,o=", "307" }, new String[]{ "u=r,g=rw,o=rwx", "310" }, new String[]{ "u=r,g=rw,o=rw", "311" }, new String[]{ "u=r,g=rw,o=rx", "312" }, new String[]{ "u=r,g=rw,o=r", "313" }, new String[]{ "u=r,g=rw,o=wx", "314" }, new String[]{ "u=r,g=rw,o=w", "315" }, new String[]{ "u=r,g=rw,o=x", "316" }, new String[]{ "u=r,g=rw,o=", "317" }, new String[]{ "u=r,g=rx,o=rwx", "320" }, new String[]{ "u=r,g=rx,o=rw", "321" }, new String[]{ "u=r,g=rx,o=rx", "322" }, new String[]{ "u=r,g=rx,o=r", "323" }, new String[]{ "u=r,g=rx,o=wx", "324" }, new String[]{ "u=r,g=rx,o=w", "325" }, new String[]{ "u=r,g=rx,o=x", "326" }, new String[]{ "u=r,g=rx,o=", "327" }, new String[]{ "u=r,g=r,o=rwx", "330" }, new String[]{ "u=r,g=r,o=rw", "331" }, new String[]{ "u=r,g=r,o=rx", "332" }, new String[]{ "u=r,g=r,o=r", "333" }, new String[]{ "u=r,g=r,o=wx", "334" }, new String[]{ "u=r,g=r,o=w", "335" }, new String[]{ "u=r,g=r,o=x", "336" }, new String[]{ "u=r,g=r,o=", "337" }, new String[]{ "u=r,g=wx,o=rwx", "340" }, new String[]{ "u=r,g=wx,o=rw", "341" }, new String[]{ "u=r,g=wx,o=rx", "342" }, new String[]{ "u=r,g=wx,o=r", "343" }, new String[]{ "u=r,g=wx,o=wx", "344" }, new String[]{ "u=r,g=wx,o=w", "345" }, new String[]{ "u=r,g=wx,o=x", "346" }, new String[]{ "u=r,g=wx,o=", "347" }, new String[]{ "u=r,g=w,o=rwx", "350" }, new String[]{ "u=r,g=w,o=rw", "351" }, new String[]{ "u=r,g=w,o=rx", "352" }, new String[]{ "u=r,g=w,o=r", "353" }, new String[]{ "u=r,g=w,o=wx", "354" }, new String[]{ "u=r,g=w,o=w", "355" }, new String[]{ "u=r,g=w,o=x", "356" }, new String[]{ "u=r,g=w,o=", "357" }, new String[]{ "u=r,g=x,o=rwx", "360" }, new String[]{ "u=r,g=x,o=rw", "361" }, new String[]{ "u=r,g=x,o=rx", "362" }, new String[]{ "u=r,g=x,o=r", "363" }, new String[]{ "u=r,g=x,o=wx", "364" }, new String[]{ "u=r,g=x,o=w", "365" }, new String[]{ "u=r,g=x,o=x", "366" }, new String[]{ "u=r,g=x,o=", "367" }, new String[]{ "u=r,g=,o=rwx", "370" }, new String[]{ "u=r,g=,o=rw", "371" }, new String[]{ "u=r,g=,o=rx", "372" }, new String[]{ "u=r,g=,o=r", "373" }, new String[]{ "u=r,g=,o=wx", "374" }, new String[]{ "u=r,g=,o=w", "375" }, new String[]{ "u=r,g=,o=x", "376" }, new String[]{ "u=r,g=,o=", "377" }, new String[]{ "u=wx,g=rwx,o=rwx", "400" }, new String[]{ "u=wx,g=rwx,o=rw", "401" }, new String[]{ "u=wx,g=rwx,o=rx", "402" }, new String[]{ "u=wx,g=rwx,o=r", "403" }, new String[]{ "u=wx,g=rwx,o=wx", "404" }, new String[]{ "u=wx,g=rwx,o=w", "405" }, new String[]{ "u=wx,g=rwx,o=x", "406" }, new String[]{ "u=wx,g=rwx,o=", "407" }, new String[]{ "u=wx,g=rw,o=rwx", "410" }, new String[]{ "u=wx,g=rw,o=rw", "411" }, new String[]{ "u=wx,g=rw,o=rx", "412" }, new String[]{ "u=wx,g=rw,o=r", "413" }, new String[]{ "u=wx,g=rw,o=wx", "414" }, new String[]{ "u=wx,g=rw,o=w", "415" }, new String[]{ "u=wx,g=rw,o=x", "416" }, new String[]{ "u=wx,g=rw,o=", "417" }, new String[]{ "u=wx,g=rx,o=rwx", "420" }, new String[]{ "u=wx,g=rx,o=rw", "421" }, new String[]{ "u=wx,g=rx,o=rx", "422" }, new String[]{ "u=wx,g=rx,o=r", "423" }, new String[]{ "u=wx,g=rx,o=wx", "424" }, new String[]{ "u=wx,g=rx,o=w", "425" }, new String[]{ "u=wx,g=rx,o=x", "426" }, new String[]{ "u=wx,g=rx,o=", "427" }, new String[]{ "u=wx,g=r,o=rwx", "430" }, new String[]{ "u=wx,g=r,o=rw", "431" }, new String[]{ "u=wx,g=r,o=rx", "432" }, new String[]{ "u=wx,g=r,o=r", "433" }, new String[]{ "u=wx,g=r,o=wx", "434" }, new String[]{ "u=wx,g=r,o=w", "435" }, new String[]{ "u=wx,g=r,o=x", "436" }, new String[]{ "u=wx,g=r,o=", "437" }, new String[]{ "u=wx,g=wx,o=rwx", "440" }, new String[]{ "u=wx,g=wx,o=rw", "441" }, new String[]{ "u=wx,g=wx,o=rx", "442" }, new String[]{ "u=wx,g=wx,o=r", "443" }, new String[]{ "u=wx,g=wx,o=wx", "444" }, new String[]{ "u=wx,g=wx,o=w", "445" }, new String[]{ "u=wx,g=wx,o=x", "446" }, new String[]{ "u=wx,g=wx,o=", "447" }, new String[]{ "u=wx,g=w,o=rwx", "450" }, new String[]{ "u=wx,g=w,o=rw", "451" }, new String[]{ "u=wx,g=w,o=rx", "452" }, new String[]{ "u=wx,g=w,o=r", "453" }, new String[]{ "u=wx,g=w,o=wx", "454" }, new String[]{ "u=wx,g=w,o=w", "455" }, new String[]{ "u=wx,g=w,o=x", "456" }, new String[]{ "u=wx,g=w,o=", "457" }, new String[]{ "u=wx,g=x,o=rwx", "460" }, new String[]{ "u=wx,g=x,o=rw", "461" }, new String[]{ "u=wx,g=x,o=rx", "462" }, new String[]{ "u=wx,g=x,o=r", "463" }, new String[]{ "u=wx,g=x,o=wx", "464" }, new String[]{ "u=wx,g=x,o=w", "465" }, new String[]{ "u=wx,g=x,o=x", "466" }, new String[]{ "u=wx,g=x,o=", "467" }, new String[]{ "u=wx,g=,o=rwx", "470" }, new String[]{ "u=wx,g=,o=rw", "471" }, new String[]{ "u=wx,g=,o=rx", "472" }, new String[]{ "u=wx,g=,o=r", "473" }, new String[]{ "u=wx,g=,o=wx", "474" }, new String[]{ "u=wx,g=,o=w", "475" }, new String[]{ "u=wx,g=,o=x", "476" }, new String[]{ "u=wx,g=,o=", "477" }, new String[]{ "u=w,g=rwx,o=rwx", "500" }, new String[]{ "u=w,g=rwx,o=rw", "501" }, new String[]{ "u=w,g=rwx,o=rx", "502" }, new String[]{ "u=w,g=rwx,o=r", "503" }, new String[]{ "u=w,g=rwx,o=wx", "504" }, new String[]{ "u=w,g=rwx,o=w", "505" }, new String[]{ "u=w,g=rwx,o=x", "506" }, new String[]{ "u=w,g=rwx,o=", "507" }, new String[]{ "u=w,g=rw,o=rwx", "510" }, new String[]{ "u=w,g=rw,o=rw", "511" }, new String[]{ "u=w,g=rw,o=rx", "512" }, new String[]{ "u=w,g=rw,o=r", "513" }, new String[]{ "u=w,g=rw,o=wx", "514" }, new String[]{ "u=w,g=rw,o=w", "515" }, new String[]{ "u=w,g=rw,o=x", "516" }, new String[]{ "u=w,g=rw,o=", "517" }, new String[]{ "u=w,g=rx,o=rwx", "520" }, new String[]{ "u=w,g=rx,o=rw", "521" }, new String[]{ "u=w,g=rx,o=rx", "522" }, new String[]{ "u=w,g=rx,o=r", "523" }, new String[]{ "u=w,g=rx,o=wx", "524" }, new String[]{ "u=w,g=rx,o=w", "525" }, new String[]{ "u=w,g=rx,o=x", "526" }, new String[]{ "u=w,g=rx,o=", "527" }, new String[]{ "u=w,g=r,o=rwx", "530" }, new String[]{ "u=w,g=r,o=rw", "531" }, new String[]{ "u=w,g=r,o=rx", "532" }, new String[]{ "u=w,g=r,o=r", "533" }, new String[]{ "u=w,g=r,o=wx", "534" }, new String[]{ "u=w,g=r,o=w", "535" }, new String[]{ "u=w,g=r,o=x", "536" }, new String[]{ "u=w,g=r,o=", "537" }, new String[]{ "u=w,g=wx,o=rwx", "540" }, new String[]{ "u=w,g=wx,o=rw", "541" }, new String[]{ "u=w,g=wx,o=rx", "542" }, new String[]{ "u=w,g=wx,o=r", "543" }, new String[]{ "u=w,g=wx,o=wx", "544" }, new String[]{ "u=w,g=wx,o=w", "545" }, new String[]{ "u=w,g=wx,o=x", "546" }, new String[]{ "u=w,g=wx,o=", "547" }, new String[]{ "u=w,g=w,o=rwx", "550" }, new String[]{ "u=w,g=w,o=rw", "551" }, new String[]{ "u=w,g=w,o=rx", "552" }, new String[]{ "u=w,g=w,o=r", "553" }, new String[]{ "u=w,g=w,o=wx", "554" }, new String[]{ "u=w,g=w,o=w", "555" }, new String[]{ "u=w,g=w,o=x", "556" }, new String[]{ "u=w,g=w,o=", "557" }, new String[]{ "u=w,g=x,o=rwx", "560" }, new String[]{ "u=w,g=x,o=rw", "561" }, new String[]{ "u=w,g=x,o=rx", "562" }, new String[]{ "u=w,g=x,o=r", "563" }, new String[]{ "u=w,g=x,o=wx", "564" }, new String[]{ "u=w,g=x,o=w", "565" }, new String[]{ "u=w,g=x,o=x", "566" }, new String[]{ "u=w,g=x,o=", "567" }, new String[]{ "u=w,g=,o=rwx", "570" }, new String[]{ "u=w,g=,o=rw", "571" }, new String[]{ "u=w,g=,o=rx", "572" }, new String[]{ "u=w,g=,o=r", "573" }, new String[]{ "u=w,g=,o=wx", "574" }, new String[]{ "u=w,g=,o=w", "575" }, new String[]{ "u=w,g=,o=x", "576" }, new String[]{ "u=w,g=,o=", "577" }, new String[]{ "u=x,g=rwx,o=rwx", "600" }, new String[]{ "u=x,g=rwx,o=rw", "601" }, new String[]{ "u=x,g=rwx,o=rx", "602" }, new String[]{ "u=x,g=rwx,o=r", "603" }, new String[]{ "u=x,g=rwx,o=wx", "604" }, new String[]{ "u=x,g=rwx,o=w", "605" }, new String[]{ "u=x,g=rwx,o=x", "606" }, new String[]{ "u=x,g=rwx,o=", "607" }, new String[]{ "u=x,g=rw,o=rwx", "610" }, new String[]{ "u=x,g=rw,o=rw", "611" }, new String[]{ "u=x,g=rw,o=rx", "612" }, new String[]{ "u=x,g=rw,o=r", "613" }, new String[]{ "u=x,g=rw,o=wx", "614" }, new String[]{ "u=x,g=rw,o=w", "615" }, new String[]{ "u=x,g=rw,o=x", "616" }, new String[]{ "u=x,g=rw,o=", "617" }, new String[]{ "u=x,g=rx,o=rwx", "620" }, new String[]{ "u=x,g=rx,o=rw", "621" }, new String[]{ "u=x,g=rx,o=rx", "622" }, new String[]{ "u=x,g=rx,o=r", "623" }, new String[]{ "u=x,g=rx,o=wx", "624" }, new String[]{ "u=x,g=rx,o=w", "625" }, new String[]{ "u=x,g=rx,o=x", "626" }, new String[]{ "u=x,g=rx,o=", "627" }, new String[]{ "u=x,g=r,o=rwx", "630" }, new String[]{ "u=x,g=r,o=rw", "631" }, new String[]{ "u=x,g=r,o=rx", "632" }, new String[]{ "u=x,g=r,o=r", "633" }, new String[]{ "u=x,g=r,o=wx", "634" }, new String[]{ "u=x,g=r,o=w", "635" }, new String[]{ "u=x,g=r,o=x", "636" }, new String[]{ "u=x,g=r,o=", "637" }, new String[]{ "u=x,g=wx,o=rwx", "640" }, new String[]{ "u=x,g=wx,o=rw", "641" }, new String[]{ "u=x,g=wx,o=rx", "642" }, new String[]{ "u=x,g=wx,o=r", "643" }, new String[]{ "u=x,g=wx,o=wx", "644" }, new String[]{ "u=x,g=wx,o=w", "645" }, new String[]{ "u=x,g=wx,o=x", "646" }, new String[]{ "u=x,g=wx,o=", "647" }, new String[]{ "u=x,g=w,o=rwx", "650" }, new String[]{ "u=x,g=w,o=rw", "651" }, new String[]{ "u=x,g=w,o=rx", "652" }, new String[]{ "u=x,g=w,o=r", "653" }, new String[]{ "u=x,g=w,o=wx", "654" }, new String[]{ "u=x,g=w,o=w", "655" }, new String[]{ "u=x,g=w,o=x", "656" }, new String[]{ "u=x,g=w,o=", "657" }, new String[]{ "u=x,g=x,o=rwx", "660" }, new String[]{ "u=x,g=x,o=rw", "661" }, new String[]{ "u=x,g=x,o=rx", "662" }, new String[]{ "u=x,g=x,o=r", "663" }, new String[]{ "u=x,g=x,o=wx", "664" }, new String[]{ "u=x,g=x,o=w", "665" }, new String[]{ "u=x,g=x,o=x", "666" }, new String[]{ "u=x,g=x,o=", "667" }, new String[]{ "u=x,g=,o=rwx", "670" }, new String[]{ "u=x,g=,o=rw", "671" }, new String[]{ "u=x,g=,o=rx", "672" }, new String[]{ "u=x,g=,o=r", "673" }, new String[]{ "u=x,g=,o=wx", "674" }, new String[]{ "u=x,g=,o=w", "675" }, new String[]{ "u=x,g=,o=x", "676" }, new String[]{ "u=x,g=,o=", "677" }, new String[]{ "u=,g=rwx,o=rwx", "700" }, new String[]{ "u=,g=rwx,o=rw", "701" }, new String[]{ "u=,g=rwx,o=rx", "702" }, new String[]{ "u=,g=rwx,o=r", "703" }, new String[]{ "u=,g=rwx,o=wx", "704" }, new String[]{ "u=,g=rwx,o=w", "705" }, new String[]{ "u=,g=rwx,o=x", "706" }, new String[]{ "u=,g=rwx,o=", "707" }, new String[]{ "u=,g=rw,o=rwx", "710" }, new String[]{ "u=,g=rw,o=rw", "711" }, new String[]{ "u=,g=rw,o=rx", "712" }, new String[]{ "u=,g=rw,o=r", "713" }, new String[]{ "u=,g=rw,o=wx", "714" }, new String[]{ "u=,g=rw,o=w", "715" }, new String[]{ "u=,g=rw,o=x", "716" }, new String[]{ "u=,g=rw,o=", "717" }, new String[]{ "u=,g=rx,o=rwx", "720" }, new String[]{ "u=,g=rx,o=rw", "721" }, new String[]{ "u=,g=rx,o=rx", "722" }, new String[]{ "u=,g=rx,o=r", "723" }, new String[]{ "u=,g=rx,o=wx", "724" }, new String[]{ "u=,g=rx,o=w", "725" }, new String[]{ "u=,g=rx,o=x", "726" }, new String[]{ "u=,g=rx,o=", "727" }, new String[]{ "u=,g=r,o=rwx", "730" }, new String[]{ "u=,g=r,o=rw", "731" }, new String[]{ "u=,g=r,o=rx", "732" }, new String[]{ "u=,g=r,o=r", "733" }, new String[]{ "u=,g=r,o=wx", "734" }, new String[]{ "u=,g=r,o=w", "735" }, new String[]{ "u=,g=r,o=x", "736" }, new String[]{ "u=,g=r,o=", "737" }, new String[]{ "u=,g=wx,o=rwx", "740" }, new String[]{ "u=,g=wx,o=rw", "741" }, new String[]{ "u=,g=wx,o=rx", "742" }, new String[]{ "u=,g=wx,o=r", "743" }, new String[]{ "u=,g=wx,o=wx", "744" }, new String[]{ "u=,g=wx,o=w", "745" }, new String[]{ "u=,g=wx,o=x", "746" }, new String[]{ "u=,g=wx,o=", "747" }, new String[]{ "u=,g=w,o=rwx", "750" }, new String[]{ "u=,g=w,o=rw", "751" }, new String[]{ "u=,g=w,o=rx", "752" }, new String[]{ "u=,g=w,o=r", "753" }, new String[]{ "u=,g=w,o=wx", "754" }, new String[]{ "u=,g=w,o=w", "755" }, new String[]{ "u=,g=w,o=x", "756" }, new String[]{ "u=,g=w,o=", "757" }, new String[]{ "u=,g=x,o=rwx", "760" }, new String[]{ "u=,g=x,o=rw", "761" }, new String[]{ "u=,g=x,o=rx", "762" }, new String[]{ "u=,g=x,o=r", "763" }, new String[]{ "u=,g=x,o=wx", "764" }, new String[]{ "u=,g=x,o=w", "765" }, new String[]{ "u=,g=x,o=x", "766" }, new String[]{ "u=,g=x,o=", "767" }, new String[]{ "u=,g=,o=rwx", "770" }, new String[]{ "u=,g=,o=rw", "771" }, new String[]{ "u=,g=,o=rx", "772" }, new String[]{ "u=,g=,o=r", "773" }, new String[]{ "u=,g=,o=wx", "774" }, new String[]{ "u=,g=,o=w", "775" }, new String[]{ "u=,g=,o=x", "776" }, new String[]{ "u=,g=,o=", "777" } };
}

