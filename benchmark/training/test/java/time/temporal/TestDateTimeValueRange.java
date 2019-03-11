/**
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
/**
 * This file is available under and governed by the GNU General Public
 * License version 2 only, as published by the Free Software Foundation.
 * However, the following notice accompanied the original version of this
 * file:
 *
 * Copyright (c) 2009-2012, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package test.java.time.temporal;


import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.time.DateTimeException;
import java.time.temporal.ChronoField;
import java.time.temporal.ValueRange;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.java.time.AbstractTest;


/**
 * Test.
 */
@RunWith(DataProviderRunner.class)
public class TestDateTimeValueRange extends AbstractTest {
    // -----------------------------------------------------------------------
    // of(long,long)
    // -----------------------------------------------------------------------
    @Test
    public void test_of_longlong() {
        ValueRange test = ValueRange.of(1, 12);
        Assert.assertEquals(test.getMinimum(), 1);
        Assert.assertEquals(test.getLargestMinimum(), 1);
        Assert.assertEquals(test.getSmallestMaximum(), 12);
        Assert.assertEquals(test.getMaximum(), 12);
        Assert.assertEquals(test.isFixed(), true);
        Assert.assertEquals(test.isIntValue(), true);
    }

    @Test
    public void test_of_longlong_big() {
        ValueRange test = ValueRange.of(1, 123456789012345L);
        Assert.assertEquals(test.getMinimum(), 1);
        Assert.assertEquals(test.getLargestMinimum(), 1);
        Assert.assertEquals(test.getSmallestMaximum(), 123456789012345L);
        Assert.assertEquals(test.getMaximum(), 123456789012345L);
        Assert.assertEquals(test.isFixed(), true);
        Assert.assertEquals(test.isIntValue(), false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_of_longlong_minGtMax() {
        ValueRange.of(12, 1);
    }

    // -----------------------------------------------------------------------
    // of(long,long,long)
    // -----------------------------------------------------------------------
    @Test
    public void test_of_longlonglong() {
        ValueRange test = ValueRange.of(1, 28, 31);
        Assert.assertEquals(test.getMinimum(), 1);
        Assert.assertEquals(test.getLargestMinimum(), 1);
        Assert.assertEquals(test.getSmallestMaximum(), 28);
        Assert.assertEquals(test.getMaximum(), 31);
        Assert.assertEquals(test.isFixed(), false);
        Assert.assertEquals(test.isIntValue(), true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_of_longlonglong_minGtMax() {
        ValueRange.of(12, 1, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_of_longlonglong_smallestmaxminGtMax() {
        ValueRange.of(1, 31, 28);
    }

    // -----------------------------------------------------------------------
    // isValidValue(long)
    // -----------------------------------------------------------------------
    @Test
    public void test_isValidValue_long() {
        ValueRange test = ValueRange.of(1, 28, 31);
        Assert.assertEquals(test.isValidValue(0), false);
        Assert.assertEquals(test.isValidValue(1), true);
        Assert.assertEquals(test.isValidValue(2), true);
        Assert.assertEquals(test.isValidValue(30), true);
        Assert.assertEquals(test.isValidValue(31), true);
        Assert.assertEquals(test.isValidValue(32), false);
    }

    // -----------------------------------------------------------------------
    // isValidIntValue(long)
    // -----------------------------------------------------------------------
    @Test
    public void test_isValidValue_long_int() {
        ValueRange test = ValueRange.of(1, 28, 31);
        Assert.assertEquals(test.isValidValue(0), false);
        Assert.assertEquals(test.isValidValue(1), true);
        Assert.assertEquals(test.isValidValue(31), true);
        Assert.assertEquals(test.isValidValue(32), false);
    }

    @Test
    public void test_isValidValue_long_long() {
        ValueRange test = ValueRange.of(1, 28, ((Integer.MAX_VALUE) + 1L));
        Assert.assertEquals(test.isValidIntValue(0), false);
        Assert.assertEquals(test.isValidIntValue(1), false);
        Assert.assertEquals(test.isValidIntValue(31), false);
        Assert.assertEquals(test.isValidIntValue(32), false);
    }

    @Test(expected = DateTimeException.class)
    public void test_checkValidValueUnsupported_long_long() {
        ValueRange test = ValueRange.of(1, 28, ((Integer.MAX_VALUE) + 1L));
        test.checkValidIntValue(0, ((ChronoField) (null)));
    }

    @Test(expected = DateTimeException.class)
    public void test_checkValidValueInvalid_long_long() {
        ValueRange test = ValueRange.of(1, 28, ((Integer.MAX_VALUE) + 1L));
        test.checkValidIntValue(((Integer.MAX_VALUE) + 2L), ((ChronoField) (null)));
    }

    // -----------------------------------------------------------------------
    // equals() / hashCode()
    // -----------------------------------------------------------------------
    @Test
    public void test_equals1() {
        ValueRange a = ValueRange.of(1, 2, 3, 4);
        ValueRange b = ValueRange.of(1, 2, 3, 4);
        Assert.assertEquals(a.equals(a), true);
        Assert.assertEquals(a.equals(b), true);
        Assert.assertEquals(b.equals(a), true);
        Assert.assertEquals(b.equals(b), true);
        Assert.assertEquals(((a.hashCode()) == (b.hashCode())), true);
    }

    @Test
    public void test_equals2() {
        ValueRange a = ValueRange.of(1, 2, 3, 4);
        Assert.assertEquals(a.equals(ValueRange.of(0, 2, 3, 4)), false);
        Assert.assertEquals(a.equals(ValueRange.of(1, 3, 3, 4)), false);
        Assert.assertEquals(a.equals(ValueRange.of(1, 2, 4, 4)), false);
        Assert.assertEquals(a.equals(ValueRange.of(1, 2, 3, 5)), false);
    }

    @Test
    public void test_equals_otherType() {
        ValueRange a = ValueRange.of(1, 12);
        Assert.assertEquals(a.equals("Rubbish"), false);
    }

    @Test
    public void test_equals_null() {
        ValueRange a = ValueRange.of(1, 12);
        Assert.assertEquals(a.equals(null), false);
    }

    // -----------------------------------------------------------------------
    // toString()
    // -----------------------------------------------------------------------
    @Test
    public void test_toString() {
        Assert.assertEquals(ValueRange.of(1, 1, 4, 4).toString(), "1 - 4");
        Assert.assertEquals(ValueRange.of(1, 1, 3, 4).toString(), "1 - 3/4");
        Assert.assertEquals(ValueRange.of(1, 2, 3, 4).toString(), "1/2 - 3/4");
        Assert.assertEquals(ValueRange.of(1, 2, 4, 4).toString(), "1/2 - 4");
    }
}

