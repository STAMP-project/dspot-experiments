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
package org.apache.shardingsphere.core.merger.dql.orderby;


import OrderDirection.ASC;
import OrderDirection.DESC;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class CompareUtilTest {
    @Test
    public void assertCompareToWhenBothNull() {
        Assert.assertThat(CompareUtil.compareTo(null, null, DESC, ASC), CoreMatchers.is(0));
    }

    @Test
    public void assertCompareToWhenFirstValueIsNullForOrderByAscAndNullOrderByAsc() {
        Assert.assertThat(CompareUtil.compareTo(null, 1, ASC, ASC), CoreMatchers.is((-1)));
    }

    @Test
    public void assertCompareToWhenFirstValueIsNullForOrderByAscAndNullOrderByDesc() {
        Assert.assertThat(CompareUtil.compareTo(null, 1, ASC, DESC), CoreMatchers.is(1));
    }

    @Test
    public void assertCompareToWhenFirstValueIsNullForOrderByDescAndNullOrderByAsc() {
        Assert.assertThat(CompareUtil.compareTo(null, 1, DESC, ASC), CoreMatchers.is(1));
    }

    @Test
    public void assertCompareToWhenFirstValueIsNullForOrderByDescAndNullOrderByDesc() {
        Assert.assertThat(CompareUtil.compareTo(null, 1, DESC, DESC), CoreMatchers.is((-1)));
    }

    @Test
    public void assertCompareToWhenSecondValueIsNullForOrderByAscAndNullOrderByAsc() {
        Assert.assertThat(CompareUtil.compareTo(1, null, ASC, ASC), CoreMatchers.is(1));
    }

    @Test
    public void assertCompareToWhenSecondValueIsNullForOrderByAscAndNullOrderByDesc() {
        Assert.assertThat(CompareUtil.compareTo(1, null, ASC, DESC), CoreMatchers.is((-1)));
    }

    @Test
    public void assertCompareToWhenSecondValueIsNullForOrderByDescAndNullOrderByAsc() {
        Assert.assertThat(CompareUtil.compareTo(1, null, DESC, ASC), CoreMatchers.is((-1)));
    }

    @Test
    public void assertCompareToWhenSecondValueIsNullForOrderByDescAndNullOrderByDesc() {
        Assert.assertThat(CompareUtil.compareTo(1, null, DESC, DESC), CoreMatchers.is(1));
    }

    @Test
    public void assertCompareToWhenAsc() {
        Assert.assertThat(CompareUtil.compareTo(1, 2, ASC, ASC), CoreMatchers.is((-1)));
    }

    @Test
    public void assertCompareToWhenDesc() {
        Assert.assertThat(CompareUtil.compareTo(1, 2, DESC, ASC), CoreMatchers.is(1));
    }
}

