/**
 * This file is part of FNLP (formerly FudanNLP).
 *
 *  FNLP is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  FNLP is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with FudanNLP.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2009-2014 www.fnlp.org. All rights reserved.
 */
package org.fnlp.nlp.cn;


import org.junit.Test;


public class ChineseTransTest {
    private ChineseTrans tc;

    @Test
    public void testToTrad() {
        String str;
        str = "ddd";
        str = tc.toTrad(str);
        str = tc.toTrad(str);
        str = tc.toTrad(str);
        str = tc.toTrad(str);
        System.out.println(str);
    }

    // @Test
    // public void testToTradHash() {
    // String str;
    // str = ChineseTrans.simplified;
    // str = ChineseTrans.toTradwithHash(str);
    // str = ChineseTrans.toTradwithHash(str);
    // str = ChineseTrans.toTradwithHash(str);
    // str = ChineseTrans.toTradwithHash(str);
    // System.out.println(str);
    // }
    @Test
    public void testNormalise() {
        String str;
        str = "?";
        str = tc.normalizeCAP(str, true);
        System.out.println(str);
        System.out.println(str.equals("?"));
    }

    @Test
    public void testToFullWidth() {
        String str;
        str = "http://news.bbc.co.uk/1/hi/programmes/panorama/live_forums/2124808.stm";
        str = tc.toFullWidth(str);
        System.out.println(str);
    }

    @Test
    public void testNormalize() {
        String str;
        str = "???????????????????????????????????????????????????????????????????????????????";
        str = tc.normalize(str);
        System.out.println(str);
    }

    @Test
    public void testToHalfWidth() {
        String str;
        str = "??????????????????????????????????????????????????????????????????????";
        str = tc.toHalfWidth(str);
        System.out.println(str);
    }
}

