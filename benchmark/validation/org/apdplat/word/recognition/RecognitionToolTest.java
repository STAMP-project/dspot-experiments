/**
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, ???, yang-shangchuan@qq.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.apdplat.word.recognition;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author ???
 */
public class RecognitionToolTest {
    private static final List<String> LIST = new ArrayList<>();

    private static final List<String> QUANTIFIER = new ArrayList<>();

    @Test
    public void testIsEnglish() {
        List<String> text = new ArrayList<>();
        text.add("APDPlat");
        text.add("2word");
        text.add("word2");
        text.add("2word3");
        text.add("word");
        text.add("love");
        String singleStr = "????????????????????????????????????????????????????????????????????????????????????????????????????????a b c d e f g h i j k l m n o p q r s t u v w x y z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z";
        String[] single = singleStr.split("[? ]+");
        Assert.assertEquals(104, single.length);
        for (String s : single) {
            text.add(s);
        }
        List<Boolean> expect = new ArrayList<>();
        expect.add(true);
        expect.add(false);
        expect.add(false);
        expect.add(false);
        expect.add(true);
        expect.add(true);
        for (int i = 0; i < (single.length); i++) {
            expect.add(true);
        }
        for (int i = 0; i < (text.size()); i++) {
            String str = text.get(i);
            boolean result = RecognitionTool.isEnglish(str, 0, str.length());
            Assert.assertEquals(str, expect.get(i), result);
        }
    }

    @Test
    public void testIsNumber() {
        List<String> text = new ArrayList<>();
        text.add("250");
        text.add("n250h");
        text.add("2h");
        text.add("23h");
        text.add("88996661");
        text.add("1997");
        String singleStr = "0 1 2 3 4 5 6 7 8 9????????????????????";
        String[] single = singleStr.split("[? ]+");
        Assert.assertEquals(20, single.length);
        for (String s : single) {
            text.add(s);
        }
        List<Boolean> expect = new ArrayList<>();
        expect.add(true);
        expect.add(false);
        expect.add(false);
        expect.add(false);
        expect.add(true);
        expect.add(true);
        for (int i = 0; i < (single.length); i++) {
            expect.add(true);
        }
        for (int i = 0; i < (text.size()); i++) {
            String str = text.get(i);
            boolean result = RecognitionTool.isNumber(str, 0, str.length());
            Assert.assertEquals(str, expect.get(i), result);
        }
    }

    @Test
    public void testIsEnglishAndNumberMix() {
        List<String> text = new ArrayList<>();
        String singleStr = "????????????????????????????????????????????????????????????????????????????????????????????????????????a b c d e f g h i j k l m n o p q r s t u v w x y z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z";
        String[] single = singleStr.split("[? ]+");
        Assert.assertEquals(104, single.length);
        for (String s : single) {
            text.add(s);
        }
        singleStr = "0 1 2 3 4 5 6 7 8 9????????????????????";
        single = singleStr.split("[? ]+");
        Assert.assertEquals(20, single.length);
        for (String s : single) {
            text.add(s);
        }
        List<String> list = new ArrayList<>();
        Random r = new Random(System.nanoTime());
        for (String s : text) {
            StringBuilder str = new StringBuilder();
            str.append(s);
            int len = r.nextInt(20);
            for (int i = 0; i < len; i++) {
                str.append(text.get(r.nextInt(text.size())));
            }
            list.add(str.toString());
        }
        text.addAll(list);
        for (String s : text) {
            boolean result = RecognitionTool.isEnglishAndNumberMix(s, 0, s.length());
            Assert.assertEquals(s, true, result);
        }
    }

    @Test
    public void testIsChineseNumber() {
        List<String> text = new ArrayList<>();
        text.add("???");
        text.add("?????");
        text.add("??");
        text.add("5?");
        text.add("????");
        text.add("????");
        String singleStr = "? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ?";
        String[] single = singleStr.split(" ");
        Assert.assertEquals(28, single.length);
        for (String s : single) {
            text.add(s);
        }
        for (String item : RecognitionToolTest.LIST) {
            text.add(item);
        }
        List<Boolean> expect = new ArrayList<>();
        expect.add(true);
        expect.add(false);
        expect.add(false);
        expect.add(false);
        expect.add(true);
        expect.add(true);
        for (int i = 0; i < (single.length); i++) {
            expect.add(true);
        }
        for (int i = 0; i < (RecognitionToolTest.LIST.size()); i++) {
            expect.add(true);
        }
        for (int i = 0; i < (text.size()); i++) {
            String str = text.get(i);
            boolean result = RecognitionTool.isChineseNumber(str, 0, str.length());
            Assert.assertEquals(str, expect.get(i), result);
        }
    }

    @Test
    public void testIsQuantifier() {
        for (String str : RecognitionToolTest.QUANTIFIER) {
            boolean result = RecognitionTool.isQuantifier(str, 0, str.length());
            Assert.assertEquals(str, true, result);
        }
    }
}

