/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.drools.core.util;


import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;


public class KieFunctionsTest {
    @Test
    public void testKieFunctions() {
        Assert.assertEquals(true, KieFunctions.isNull(null));
        Assert.assertEquals(false, KieFunctions.isNull("nothing"));
        Assert.assertEquals(true, KieFunctions.isEmpty(null));
        Assert.assertEquals(true, KieFunctions.isEmpty(""));
        Assert.assertEquals(false, KieFunctions.isEmpty(" "));
        Assert.assertEquals(true, KieFunctions.equalsTo(55, "55"));
        Assert.assertEquals(false, KieFunctions.equalsTo(55, "550"));
        Assert.assertEquals(true, KieFunctions.equalsTo(new BigDecimal("322.123"), "322.123"));
        Assert.assertEquals(false, KieFunctions.equalsTo(new BigDecimal("322.123"), "3322.123"));
        Assert.assertEquals(true, KieFunctions.equalsTo(new BigInteger("123456"), "123456"));
        Assert.assertEquals(false, KieFunctions.equalsTo(new BigInteger("123456"), "1234567"));
        Assert.assertEquals(true, KieFunctions.equalsTo(((String) (null)), null));
        Assert.assertEquals(false, KieFunctions.equalsTo(((String) (null)), "a"));
        Assert.assertEquals(false, KieFunctions.equalsTo("f", null));
        Assert.assertEquals(true, KieFunctions.equalsTo(((Integer) (null)), null));
        Assert.assertEquals(false, KieFunctions.equalsTo(((Integer) (null)), "1"));
        boolean comparitionFailed = false;
        try {
            Assert.assertEquals(false, KieFunctions.equalsTo(44, null));
        } catch (RuntimeException e) {
            comparitionFailed = true;
        }
        Assert.assertEquals(true, comparitionFailed);
        Assert.assertEquals(true, KieFunctions.contains("welcome to jamaica", "jama"));
        Assert.assertEquals(false, KieFunctions.contains("welcome to jamaica", "Jama"));
        Assert.assertEquals(true, KieFunctions.contains(null, null));
        Assert.assertEquals(false, KieFunctions.contains("hello", null));
        Assert.assertEquals(false, KieFunctions.contains(null, "hello"));
        Assert.assertEquals(true, KieFunctions.startsWith("welcome to jamaica", "wel"));
        Assert.assertEquals(false, KieFunctions.startsWith("welcome to jamaica", "Well"));
        Assert.assertEquals(true, KieFunctions.startsWith(null, null));
        Assert.assertEquals(false, KieFunctions.startsWith("hello", null));
        Assert.assertEquals(false, KieFunctions.startsWith(null, "hello"));
        Assert.assertEquals(true, KieFunctions.endsWith("welcome to jamaica", "jamaica"));
        Assert.assertEquals(false, KieFunctions.endsWith("welcome to jamaica", "Jamaica"));
        Assert.assertEquals(true, KieFunctions.endsWith(null, null));
        Assert.assertEquals(false, KieFunctions.endsWith("hello", null));
        Assert.assertEquals(false, KieFunctions.endsWith(null, "hello"));
        Assert.assertEquals(true, KieFunctions.greaterThan(5, "2"));
        Assert.assertEquals(false, KieFunctions.greaterThan(0, "2"));
        Assert.assertEquals(false, KieFunctions.greaterThan(0, "0"));
        Assert.assertEquals(false, KieFunctions.greaterThan(null, "0"));
        Assert.assertEquals(false, KieFunctions.greaterThan(null, null));
        comparitionFailed = false;
        try {
            Assert.assertEquals(false, KieFunctions.greaterThan(2, null));
        } catch (RuntimeException e) {
            comparitionFailed = true;
        }
        Assert.assertEquals(true, comparitionFailed);
        Assert.assertEquals(true, KieFunctions.greaterOrEqualThan(5, "2"));
        Assert.assertEquals(true, KieFunctions.greaterOrEqualThan(2, "2"));
        Assert.assertEquals(false, KieFunctions.greaterOrEqualThan(0, "2"));
        Assert.assertEquals(true, KieFunctions.greaterOrEqualThan(0, "0"));
        Assert.assertEquals(false, KieFunctions.greaterOrEqualThan(null, "0"));
        Assert.assertEquals(false, KieFunctions.greaterOrEqualThan(null, null));
        comparitionFailed = false;
        try {
            Assert.assertEquals(false, KieFunctions.greaterOrEqualThan(2, null));
        } catch (RuntimeException e) {
            comparitionFailed = true;
        }
        Assert.assertEquals(true, comparitionFailed);
        Assert.assertEquals(false, KieFunctions.lessThan(5, "2"));
        Assert.assertEquals(true, KieFunctions.lessThan(0, "2"));
        Assert.assertEquals(false, KieFunctions.lessThan(0, "0"));
        Assert.assertEquals(false, KieFunctions.lessThan(null, "0"));
        Assert.assertEquals(false, KieFunctions.lessThan(null, null));
        comparitionFailed = false;
        try {
            Assert.assertEquals(false, KieFunctions.lessThan(2, null));
        } catch (RuntimeException e) {
            comparitionFailed = true;
        }
        Assert.assertEquals(true, comparitionFailed);
        Assert.assertEquals(false, KieFunctions.lessOrEqualThan(5, "2"));
        Assert.assertEquals(true, KieFunctions.lessOrEqualThan(2, "2"));
        Assert.assertEquals(true, KieFunctions.lessOrEqualThan(0, "2"));
        Assert.assertEquals(true, KieFunctions.lessOrEqualThan(0, "0"));
        Assert.assertEquals(false, KieFunctions.lessOrEqualThan(null, "0"));
        Assert.assertEquals(false, KieFunctions.lessOrEqualThan(null, null));
        comparitionFailed = false;
        try {
            Assert.assertEquals(false, KieFunctions.lessOrEqualThan(2, null));
        } catch (RuntimeException e) {
            comparitionFailed = true;
        }
        Assert.assertEquals(true, comparitionFailed);
        Assert.assertEquals(false, KieFunctions.between(0, "1", "10"));
        Assert.assertEquals(false, KieFunctions.between(11, "1", "10"));
        Assert.assertEquals(true, KieFunctions.between(1, "1", "10"));
        Assert.assertEquals(true, KieFunctions.between(10, "1", "10"));
        Assert.assertEquals(true, KieFunctions.between(2, "1", "10"));
        Assert.assertEquals(false, KieFunctions.between(null, "5", "6"));
        comparitionFailed = false;
        try {
            Assert.assertEquals(false, KieFunctions.between(2, null, "9"));
        } catch (RuntimeException e) {
            comparitionFailed = true;
        }
        Assert.assertEquals(true, comparitionFailed);
        comparitionFailed = false;
        try {
            Assert.assertEquals(false, KieFunctions.between(2, "1", null));
        } catch (RuntimeException e) {
            comparitionFailed = true;
        }
        Assert.assertEquals(true, comparitionFailed);
        Assert.assertEquals(true, KieFunctions.isTrue(true));
        Assert.assertEquals(false, KieFunctions.isTrue(null));
        Assert.assertEquals(false, KieFunctions.isTrue(false));
        Assert.assertEquals(true, KieFunctions.isFalse(false));
        Assert.assertEquals(false, KieFunctions.isFalse(null));
        Assert.assertEquals(false, KieFunctions.isFalse(true));
    }
}

