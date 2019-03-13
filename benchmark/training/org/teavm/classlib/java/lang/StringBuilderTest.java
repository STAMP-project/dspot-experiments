/**
 * Copyright 2013 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.lang;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class StringBuilderTest {
    @Test
    public void integerAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(23);
        Assert.assertEquals("23", sb.toString());
    }

    @Test
    public void integerInserted() {
        StringBuilder sb = new StringBuilder("[]");
        sb.insert(1, 23);
        Assert.assertEquals("[23]", sb.toString());
        sb = new StringBuilder("[]");
        sb.insert(1, 10);
        Assert.assertEquals("[10]", sb.toString());
        sb = new StringBuilder("[]");
        sb.insert(1, 100);
        Assert.assertEquals("[100]", sb.toString());
    }

    @Test
    public void largeIntegerAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(123456);
        Assert.assertEquals("123456", sb.toString());
    }

    @Test
    public void negativeIntegerAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append((-23));
        Assert.assertEquals("-23", sb.toString());
    }

    @Test
    public void maxIntegerAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(2147483647);
        Assert.assertEquals("2147483647", sb.toString());
    }

    @Test
    public void longAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(23L);
        Assert.assertEquals("23", sb.toString());
    }

    @Test
    public void longAppended2() {
        StringBuilder sb = new StringBuilder();
        sb.append(2971215073L);
        Assert.assertEquals("2971215073", sb.toString());
    }

    @Test
    public void negativeLongAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append((-23L));
        Assert.assertEquals("-23", sb.toString());
    }

    @Test
    public void largeLongAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(12345678901234L);
        Assert.assertEquals("12345678901234", sb.toString());
    }

    @Test
    public void maxLongAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(9223372036854775807L);
        Assert.assertEquals("9223372036854775807", sb.toString());
    }

    @Test
    public void floatAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(1.234E25F);
        Assert.assertEquals("1.234E25", sb.toString());
    }

    @Test
    public void floatAppended2() {
        StringBuilder sb = new StringBuilder();
        sb.append(9.8765E30F);
        Assert.assertEquals("9.8765E30", sb.toString());
    }

    @Test
    public void negativeFloatAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append((-1.234E25F));
        Assert.assertEquals("-1.234E25", sb.toString());
    }

    @Test
    public void negativeFloatAppended2() {
        StringBuilder sb = new StringBuilder();
        sb.append(9.8765E30F);
        Assert.assertEquals("9.8765E30", sb.toString());
    }

    @Test
    public void maxFloatAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(3.402823E38F);
        Assert.assertEquals("3.402823E38", sb.toString());
    }

    @Test
    public void smallFloatAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(1.234E-25F);
        Assert.assertEquals("1.234E-25", sb.toString());
    }

    @Test
    public void smallFloatAppended2() {
        StringBuilder sb = new StringBuilder();
        sb.append(9.8764E-30F);
        Assert.assertEquals("9.8764E-30", sb.toString());
    }

    @Test
    public void negativeSmallFloatAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append((-1.234E-25F));
        Assert.assertEquals("-1.234E-25", sb.toString());
    }

    @Test
    public void negativeSmallFloatAppended2() {
        StringBuilder sb = new StringBuilder();
        sb.append((-9.8764E-30F));
        Assert.assertEquals("-9.8764E-30", sb.toString());
    }

    @Test
    public void minFloatAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(1.17549E-38F);
        Assert.assertEquals("1.17549E-38", sb.toString());
    }

    @Test
    public void normalFloatAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(1200.0F);
        Assert.assertEquals("1200.0", sb.toString());
    }

    @Test
    public void normalSmallFloatAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(0.023F);
        Assert.assertEquals("0.023", sb.toString());
    }

    @Test
    public void zeroFloatAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(0.0F);
        Assert.assertEquals("0.0", sb.toString());
    }

    @Test
    public void oneFloatAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(1.0F);
        Assert.assertEquals("1.0", sb.toString());
    }

    @Test
    public void nanFloatAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(Float.NaN);
        Assert.assertEquals("NaN", sb.toString());
    }

    @Test
    public void positiveInfinityAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(Float.POSITIVE_INFINITY);
        Assert.assertEquals("Infinity", sb.toString());
    }

    @Test
    public void negativeInfinityAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(Float.NEGATIVE_INFINITY);
        Assert.assertEquals("-Infinity", sb.toString());
    }

    @Test
    public void doubleAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(1.23456789E150);
        Assert.assertEquals("1.23456789E150", sb.toString());
    }

    @Test
    public void powTenDoubleAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(10.0);
        Assert.assertEquals("10.0", sb.toString());
        sb.setLength(0);
        sb.append(20.0);
        Assert.assertEquals("20.0", sb.toString());
        sb.setLength(0);
        sb.append(100.0);
        Assert.assertEquals("100.0", sb.toString());
        sb.setLength(0);
        sb.append(1000.0);
        Assert.assertEquals("1000.0", sb.toString());
        sb.setLength(0);
        sb.append(0.1);
        Assert.assertEquals("0.1", sb.toString());
        sb.setLength(0);
        sb.append(0.01);
        Assert.assertEquals("0.01", sb.toString());
        sb.setLength(0);
        sb.append(1.0E20);
        Assert.assertEquals("1.0E20", sb.toString());
        sb.setLength(0);
        sb.append(2.0E20);
        Assert.assertEquals("2.0E20", sb.toString());
        sb.setLength(0);
        sb.append(1.0E-12);
        Assert.assertEquals("1.0E-12", sb.toString());
    }

    @Test
    public void negativeDoubleAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append((-1.23456789E150));
        Assert.assertEquals("-1.23456789E150", sb.toString());
    }

    @Test
    public void smallDoubleAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(1.23456789E-150);
        Assert.assertEquals("1.23456789E-150", sb.toString());
    }

    @Test
    public void maxDoubleAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(1.79769313486231E308);
        Assert.assertEquals("1.79769313486231E308", sb.toString());
    }

    @Test
    public void minDoubleAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(3.0E-308);
        Assert.assertEquals("3.0E-308", sb.toString());
    }

    @Test
    public void zeroDoubleAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(0);
        Assert.assertEquals("0", sb.toString());
    }

    @Test
    public void doubleInfinityAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(Double.POSITIVE_INFINITY);
        Assert.assertEquals("Infinity", sb.toString());
    }

    @Test
    public void doubleNaNAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(Double.NaN);
        Assert.assertEquals("NaN", sb.toString());
    }

    @Test
    public void normalDoubleAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(1200.0);
        Assert.assertEquals("1200.0", sb.toString());
    }

    @Test
    public void normalSmallDoubleAppended() {
        StringBuilder sb = new StringBuilder();
        sb.append(0.023);
        Assert.assertEquals("0.023", sb.toString());
    }

    @Test
    public void appendsCodePoint() {
        StringBuilder sb = new StringBuilder();
        sb.appendCodePoint(969356);
        Assert.assertEquals(56178, sb.charAt(0));
        Assert.assertEquals(56972, sb.charAt(1));
    }

    @Test
    public void deletesRange() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= 9; ++i) {
            sb.append(((char) ('0' + i)));
        }
        sb.delete(4, 6);
        Assert.assertEquals(8, sb.length());
        Assert.assertEquals('0', sb.charAt(0));
        Assert.assertEquals('3', sb.charAt(3));
        Assert.assertEquals('6', sb.charAt(4));
        Assert.assertEquals('9', sb.charAt(7));
    }

    @Test
    public void deletesNothing() {
        StringBuilder sb = new StringBuilder();
        sb.delete(0, 0);
        Assert.assertEquals(0, sb.length());
    }

    @Test
    public void replacesRangeWithSequenceOfSameLength() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= 9; ++i) {
            sb.append(((char) ('0' + i)));
        }
        sb.replace(4, 6, "ab");
        Assert.assertEquals(10, sb.length());
        Assert.assertEquals('0', sb.charAt(0));
        Assert.assertEquals('3', sb.charAt(3));
        Assert.assertEquals('a', sb.charAt(4));
        Assert.assertEquals('6', sb.charAt(6));
        Assert.assertEquals('9', sb.charAt(9));
    }

    @Test
    public void replacesRangeWithShorterSequence() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= 9; ++i) {
            sb.append(((char) ('0' + i)));
        }
        sb.replace(4, 6, "a");
        Assert.assertEquals(9, sb.length());
        Assert.assertEquals('0', sb.charAt(0));
        Assert.assertEquals('3', sb.charAt(3));
        Assert.assertEquals('a', sb.charAt(4));
        Assert.assertEquals('6', sb.charAt(5));
        Assert.assertEquals('9', sb.charAt(8));
    }

    @Test
    public void replacesRangeWithLongerSequence() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= 9; ++i) {
            sb.append(((char) ('0' + i)));
        }
        sb.replace(4, 6, "abc");
        Assert.assertEquals(11, sb.length());
        Assert.assertEquals('0', sb.charAt(0));
        Assert.assertEquals('3', sb.charAt(3));
        Assert.assertEquals('a', sb.charAt(4));
        Assert.assertEquals('c', sb.charAt(6));
        Assert.assertEquals('6', sb.charAt(7));
        Assert.assertEquals('9', sb.charAt(10));
    }

    @Test
    public void searchedBackward() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= 9; ++i) {
            sb.append(((char) ('0' + i)));
        }
        Assert.assertEquals(3, sb.lastIndexOf("345"));
        Assert.assertEquals((-1), sb.lastIndexOf("35"));
    }

    @Test
    public void substringWithUpperBoundAtEndWorks() {
        Assert.assertEquals("23", "123".substring(1, 3));
    }
}

