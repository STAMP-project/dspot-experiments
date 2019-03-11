/**
 * The MIT License
 * Copyright ? 2010 JmxTrans team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.googlecode.jmxtrans.util;


import org.junit.Test;


public class NumberUtilsTest {
    @Test
    public void testIsNumeric() {
        assertThat(NumberUtils.isNumeric(null)).isFalse();
        assertThat(NumberUtils.isNumeric("")).isTrue();// this is "true" for historical

        // reasons
        assertThat(NumberUtils.isNumeric("  ")).isFalse();
        assertThat(NumberUtils.isNumeric("123")).isTrue();
        assertThat(NumberUtils.isNumeric("12 3")).isFalse();
        assertThat(NumberUtils.isNumeric("ab2c")).isFalse();
        assertThat(NumberUtils.isNumeric("12-3")).isFalse();
        assertThat(NumberUtils.isNumeric("12.3")).isTrue();
        assertThat(NumberUtils.isNumeric("12.3.3.3")).isFalse();
        assertThat(NumberUtils.isNumeric(".2")).isTrue();
        assertThat(NumberUtils.isNumeric(".")).isFalse();
        assertThat(NumberUtils.isNumeric(1L)).isTrue();
        assertThat(NumberUtils.isNumeric(2)).isTrue();
        assertThat(NumberUtils.isNumeric(((Object) ("3.2")))).isTrue();
        assertThat(NumberUtils.isNumeric(((Object) ("abc")))).isFalse();
        assertThat(NumberUtils.isNumeric(Boolean.FALSE)).isFalse();
        assertThat(NumberUtils.isNumeric(String.valueOf(Double.NEGATIVE_INFINITY))).isFalse();
    }

    @Test
    public void testIsValidNumber() {
        assertThat(NumberUtils.isValidNumber(Long.MAX_VALUE)).isTrue();
        assertThat(NumberUtils.isValidNumber(Long.MIN_VALUE)).isTrue();
        assertThat(NumberUtils.isValidNumber(Double.MIN_VALUE)).isTrue();
        assertThat(NumberUtils.isValidNumber(Double.MAX_VALUE)).isTrue();
        assertThat(NumberUtils.isValidNumber(Float.MAX_VALUE)).isTrue();
        assertThat(NumberUtils.isValidNumber(Float.MIN_VALUE)).isTrue();
        assertThat(NumberUtils.isValidNumber(0L)).isTrue();
        assertThat(NumberUtils.isValidNumber(0.0)).isTrue();
        assertThat(NumberUtils.isValidNumber(0)).isTrue();
        assertThat(NumberUtils.isValidNumber(15)).isTrue();
        assertThat(NumberUtils.isValidNumber("1")).isTrue();
        assertThat(NumberUtils.isValidNumber("1.00")).isTrue();
        assertThat(NumberUtils.isValidNumber("-1.00")).isTrue();
        assertThat(NumberUtils.isValidNumber("-1")).isTrue();
        assertThat(NumberUtils.isValidNumber(Double.NaN)).isFalse();
        assertThat(NumberUtils.isValidNumber(Double.POSITIVE_INFINITY)).isFalse();
        assertThat(NumberUtils.isValidNumber(Double.NEGATIVE_INFINITY)).isFalse();
        assertThat(NumberUtils.isValidNumber(String.valueOf(Double.NaN))).isFalse();
        assertThat(NumberUtils.isValidNumber(String.valueOf(Double.POSITIVE_INFINITY))).isFalse();
        assertThat(NumberUtils.isValidNumber(String.valueOf(Double.NEGATIVE_INFINITY))).isFalse();
        assertThat(NumberUtils.isValidNumber(Float.NaN)).isFalse();
        assertThat(NumberUtils.isValidNumber(Float.POSITIVE_INFINITY)).isFalse();
        assertThat(NumberUtils.isValidNumber(Float.NEGATIVE_INFINITY)).isFalse();
        assertThat(NumberUtils.isValidNumber(String.valueOf(Float.NaN))).isFalse();
        assertThat(NumberUtils.isValidNumber(String.valueOf(Float.POSITIVE_INFINITY))).isFalse();
        assertThat(NumberUtils.isValidNumber(String.valueOf(Float.NEGATIVE_INFINITY))).isFalse();
    }
}

