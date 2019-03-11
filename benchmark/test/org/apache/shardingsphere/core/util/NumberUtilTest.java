/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.core.util;


import java.math.BigInteger;
import org.apache.shardingsphere.core.exception.ShardingException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class NumberUtilTest {
    @Test
    public void assertRoundHalfUpWithShort() {
        short i = 1;
        short j = 2;
        Assert.assertThat(NumberUtil.roundHalfUp(i), CoreMatchers.is(1));
        Assert.assertThat(NumberUtil.roundHalfUp(j), CoreMatchers.is(2));
    }

    @Test
    public void assertRoundHalfUpWithInteger() {
        Assert.assertThat(NumberUtil.roundHalfUp(1), CoreMatchers.is(1));
        Assert.assertThat(NumberUtil.roundHalfUp(2), CoreMatchers.is(2));
    }

    @Test
    public void assertRoundHalfUpWithLong() {
        Assert.assertThat(NumberUtil.roundHalfUp(1L), CoreMatchers.is(1));
        Assert.assertThat(NumberUtil.roundHalfUp(2L), CoreMatchers.is(2));
    }

    @Test
    public void assertRoundHalfUpWithDouble() {
        Assert.assertThat(NumberUtil.roundHalfUp(1.499), CoreMatchers.is(1));
        Assert.assertThat(NumberUtil.roundHalfUp(1.5), CoreMatchers.is(2));
    }

    @Test
    public void assertRoundHalfUpWithFloat() {
        Assert.assertThat(NumberUtil.roundHalfUp(1.499F), CoreMatchers.is(1));
        Assert.assertThat(NumberUtil.roundHalfUp(1.5F), CoreMatchers.is(2));
    }

    @Test
    public void assertRoundHalfUpWithString() {
        Assert.assertThat(NumberUtil.roundHalfUp("1.499"), CoreMatchers.is(1));
        Assert.assertThat(NumberUtil.roundHalfUp("1.5"), CoreMatchers.is(2));
    }

    @Test(expected = ShardingException.class)
    public void assertRoundHalfUpWithInvalidType() {
        NumberUtil.roundHalfUp(new Object());
    }

    @Test
    public void assertGetExactlyNumberForInteger() {
        Assert.assertThat(NumberUtil.getExactlyNumber("100000", 10), CoreMatchers.is(((Number) (100000))));
        Assert.assertThat(NumberUtil.getExactlyNumber("100000", 16), CoreMatchers.is(((Number) (1048576))));
        Assert.assertThat(NumberUtil.getExactlyNumber(String.valueOf(Integer.MIN_VALUE), 10), CoreMatchers.is(((Number) (Integer.MIN_VALUE))));
        Assert.assertThat(NumberUtil.getExactlyNumber(String.valueOf(Integer.MAX_VALUE), 10), CoreMatchers.is(((Number) (Integer.MAX_VALUE))));
    }

    @Test
    public void assertGetExactlyNumberForLong() {
        Assert.assertThat(NumberUtil.getExactlyNumber("100000000000", 10), CoreMatchers.is(((Number) (100000000000L))));
        Assert.assertThat(NumberUtil.getExactlyNumber("100000000000", 16), CoreMatchers.is(((Number) (17592186044416L))));
        Assert.assertThat(NumberUtil.getExactlyNumber(String.valueOf(Long.MIN_VALUE), 10), CoreMatchers.is(((Number) (Long.MIN_VALUE))));
        Assert.assertThat(NumberUtil.getExactlyNumber(String.valueOf(Long.MAX_VALUE), 10), CoreMatchers.is(((Number) (Long.MAX_VALUE))));
    }

    @Test
    public void assertGetExactlyNumberForBigInteger() {
        Assert.assertThat(NumberUtil.getExactlyNumber("10000000000000000000", 10), CoreMatchers.is(((Number) (new BigInteger("10000000000000000000")))));
        Assert.assertThat(NumberUtil.getExactlyNumber("10000000000000000000", 16), CoreMatchers.is(((Number) (new BigInteger("75557863725914323419136")))));
        Assert.assertThat(NumberUtil.getExactlyNumber(String.valueOf(((Long.MIN_VALUE) + 1)), 10), CoreMatchers.is(((Number) ((Long.MIN_VALUE) + 1))));
        Assert.assertThat(NumberUtil.getExactlyNumber(String.valueOf(((Long.MAX_VALUE) - 1)), 10), CoreMatchers.is(((Number) ((Long.MAX_VALUE) - 1))));
    }
}

