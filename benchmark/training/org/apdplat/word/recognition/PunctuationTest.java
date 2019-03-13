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
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author ???
 */
public class PunctuationTest {
    private static final List<Character> LIST = new ArrayList<>();

    @Test
    public void testIs() {
        for (char item : PunctuationTest.LIST) {
            boolean result = Punctuation.is(item);
            Assert.assertEquals(true, result);
        }
        Assert.assertEquals(false, Punctuation.is('y'));
        Assert.assertEquals(false, Punctuation.is('s'));
        Assert.assertEquals(false, Punctuation.is('1'));
    }

    @Test
    public void testHas() {
        Assert.assertEquals(true, Punctuation.has("???,???????,?????,2013????????APDPlat???,??Nutch???????"));
        Assert.assertEquals(false, Punctuation.has("2.35"));
        Assert.assertEquals(false, Punctuation.has("80%"));
        Assert.assertEquals(false, Punctuation.has("?????????"));
        Assert.assertEquals(true, Punctuation.has("??????????"));
        Assert.assertEquals(true, Punctuation.has("12?31???????????????????1998??????????????????"));
    }

    @Test
    public void testSeg() {
        String text = "???,???????,?????,2013????????APDPlat???,??Nutch???????";
        List<String> expResult = new ArrayList<>();
        expResult.add("???");
        expResult.add(",");
        expResult.add("???????");
        expResult.add(",");
        expResult.add("?????");
        expResult.add(",");
        expResult.add("2013????????APDPlat???");
        expResult.add(",");
        expResult.add("??Nutch??????");
        expResult.add("?");
        List<String> result = Punctuation.seg(text, true);
        Assert.assertEquals(expResult.toString(), result.toString());
        expResult = new ArrayList<>();
        expResult.add("???");
        expResult.add("???????");
        expResult.add("?????");
        expResult.add("2013????????APDPlat???");
        expResult.add("??Nutch??????");
        result = Punctuation.seg(text, false);
        Assert.assertEquals(expResult.toString(), result.toString());
        text = "???";
        Assert.assertEquals(text, Punctuation.seg(text, true).get(0));
        Assert.assertEquals(text, Punctuation.seg(text, false).get(0));
        text = "?";
        Assert.assertEquals(text, Punctuation.seg(text, true).get(0));
        Assert.assertEquals(text, Punctuation.seg(text, false).get(0));
        text = "? ";
        Assert.assertEquals(text, ((Punctuation.seg(text, true).get(0)) + (Punctuation.seg(text, true).get(1))));
        Assert.assertEquals(text, ((Punctuation.seg(text, false).get(0)) + " "));
    }
}

