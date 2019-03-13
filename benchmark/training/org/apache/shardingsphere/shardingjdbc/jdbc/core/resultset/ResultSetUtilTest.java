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
package org.apache.shardingsphere.shardingjdbc.jdbc.core.resultset;


import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import org.apache.shardingsphere.core.exception.ShardingException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ResultSetUtilTest {
    @Test
    public void assertConvertValue() {
        Object object = new Object();
        Assert.assertThat(((String) (ResultSetUtil.convertValue(object, String.class))), CoreMatchers.is(object.toString()));
        Assert.assertThat(((String) (ResultSetUtil.convertValue("1", int.class))), CoreMatchers.is("1"));
    }

    @Test
    public void assertConvertNumberValueSuccess() {
        Assert.assertThat(((String) (ResultSetUtil.convertValue("1", String.class))), CoreMatchers.is("1"));
        Assert.assertThat(((byte) (ResultSetUtil.convertValue(((byte) (1)), byte.class))), CoreMatchers.is(((byte) (1))));
        Assert.assertThat(((short) (ResultSetUtil.convertValue(((short) (1)), short.class))), CoreMatchers.is(((short) (1))));
        Assert.assertThat(((int) (ResultSetUtil.convertValue(new BigDecimal("1"), int.class))), CoreMatchers.is(1));
        Assert.assertThat(((long) (ResultSetUtil.convertValue(new BigDecimal("1"), long.class))), CoreMatchers.is(1L));
        Assert.assertThat(((double) (ResultSetUtil.convertValue(new BigDecimal("1"), double.class))), CoreMatchers.is(1.0));
        Assert.assertThat(((float) (ResultSetUtil.convertValue(new BigDecimal("1"), float.class))), CoreMatchers.is(1.0F));
        Assert.assertThat(((BigDecimal) (ResultSetUtil.convertValue(new BigDecimal("1"), BigDecimal.class))), CoreMatchers.is(new BigDecimal("1")));
        Assert.assertThat(((BigDecimal) (ResultSetUtil.convertValue(((short) (1)), BigDecimal.class))), CoreMatchers.is(new BigDecimal("1")));
        Assert.assertThat(((Date) (ResultSetUtil.convertValue(new Date(0L), Date.class))), CoreMatchers.is(new Date(0L)));
        Assert.assertThat(ResultSetUtil.convertValue(((short) (1)), Object.class), CoreMatchers.is(((Object) (Short.valueOf("1")))));
        Assert.assertThat(ResultSetUtil.convertValue(((short) (1)), String.class), CoreMatchers.is(((Object) ("1"))));
    }

    @Test(expected = ShardingException.class)
    public void assertConvertNumberValueError() {
        ResultSetUtil.convertValue(1, Date.class);
    }

    @Test
    public void assertConvertNullValue() {
        Assert.assertThat(ResultSetUtil.convertValue(null, boolean.class), CoreMatchers.is(((Object) (false))));
        Assert.assertThat(ResultSetUtil.convertValue(null, byte.class), CoreMatchers.is(((Object) ((byte) (0)))));
        Assert.assertThat(ResultSetUtil.convertValue(null, short.class), CoreMatchers.is(((Object) ((short) (0)))));
        Assert.assertThat(ResultSetUtil.convertValue(null, int.class), CoreMatchers.is(((Object) (0))));
        Assert.assertThat(ResultSetUtil.convertValue(null, long.class), CoreMatchers.is(((Object) (0L))));
        Assert.assertThat(ResultSetUtil.convertValue(null, double.class), CoreMatchers.is(((Object) (0.0))));
        Assert.assertThat(ResultSetUtil.convertValue(null, float.class), CoreMatchers.is(((Object) (0.0F))));
        Assert.assertThat(ResultSetUtil.convertValue(null, String.class), CoreMatchers.is(((Object) (null))));
        Assert.assertThat(ResultSetUtil.convertValue(null, Object.class), CoreMatchers.is(((Object) (null))));
        Assert.assertThat(ResultSetUtil.convertValue(null, BigDecimal.class), CoreMatchers.is(((Object) (null))));
        Assert.assertThat(ResultSetUtil.convertValue(null, Date.class), CoreMatchers.is(((Object) (null))));
    }

    @Test
    public void assertConvertDateValueSuccess() {
        Date now = new Date();
        Assert.assertThat(((Date) (ResultSetUtil.convertValue(now, Date.class))), CoreMatchers.is(now));
        Assert.assertThat(((java.sql.Date) (ResultSetUtil.convertValue(now, java.sql.Date.class))), CoreMatchers.is(now));
        Assert.assertThat(((Time) (ResultSetUtil.convertValue(now, Time.class))), CoreMatchers.is(now));
        Assert.assertThat(((Timestamp) (ResultSetUtil.convertValue(now, Timestamp.class))), CoreMatchers.is(new Timestamp(now.getTime())));
    }

    @Test(expected = ShardingException.class)
    public void assertConvertDateValueError() {
        ResultSetUtil.convertValue(new Date(), int.class);
    }
}

