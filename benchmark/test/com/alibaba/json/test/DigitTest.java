/**
 * Copyright 1999-2017 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.json.test;


import java.util.BitSet;
import junit.framework.TestCase;


public class DigitTest extends TestCase {
    private char[] text = "[-5.041598256063065E-20,-7210028408342716000]".toCharArray();

    private int COUNT = 1000 * 1000;

    public void test_perf() throws Exception {
        for (int i = 0; i < 50; ++i) {
            f_isDigitBitSet();
            f_isDigitArray();
            f_isDigitRange();
            f_isDigitSwitch();
            f_isDigitProhibit();
            System.out.println();
            System.out.println();
        }
    }

    private static final boolean[] digitBits = new boolean[256];

    static {
        for (char ch = '0'; ch <= '9'; ++ch) {
            DigitTest.digitBits[ch] = true;
        }
    }

    private static final DetectProhibitChar digitDetectProhibitChar = new DetectProhibitChar(new char[]{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' });

    private static final BitSet bits = new BitSet();

    static {
        for (char ch = '0'; ch <= '9'; ++ch) {
            DigitTest.bits.set(ch, true);
        }
    }
}

