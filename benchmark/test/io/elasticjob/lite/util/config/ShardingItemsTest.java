/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */
package io.elasticjob.lite.util.config;


import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ShardingItemsTest {
    @Test
    public void assertTtoItemListWhenNull() {
        Assert.assertThat(ShardingItems.toItemList(null), CoreMatchers.is(Collections.EMPTY_LIST));
    }

    @Test
    public void assertToItemListWhenEmpty() {
        Assert.assertThat(ShardingItems.toItemList(""), CoreMatchers.is(Collections.EMPTY_LIST));
    }

    @Test
    public void assertToItemList() {
        Assert.assertThat(ShardingItems.toItemList("0,1,2"), CoreMatchers.is(Arrays.asList(0, 1, 2)));
    }

    @Test
    public void assertToItemListForDuplicated() {
        Assert.assertThat(ShardingItems.toItemList("0,1,2,2"), CoreMatchers.is(Arrays.asList(0, 1, 2)));
    }

    @Test
    public void assertToItemsStringWhenEmpty() {
        Assert.assertThat(ShardingItems.toItemsString(Collections.<Integer>emptyList()), CoreMatchers.is(""));
    }

    @Test
    public void assertToItemsString() {
        Assert.assertThat(ShardingItems.toItemsString(Arrays.asList(0, 1, 2)), CoreMatchers.is("0,1,2"));
    }
}

