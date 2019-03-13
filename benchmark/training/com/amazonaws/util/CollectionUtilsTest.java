/**
 * Copyright (c) 2016. Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CollectionUtilsTest {
    @Test
    public void isEmpty_NullCollection_ReturnsTrue() {
        Assert.assertTrue(CollectionUtils.isNullOrEmpty(null));
    }

    @Test
    public void isEmpty_EmptyCollection_ReturnsTrue() {
        Assert.assertTrue(CollectionUtils.isNullOrEmpty(Collections.emptyList()));
    }

    @Test
    public void isEmpty_NonEmptyCollection_ReturnsFalse() {
        Assert.assertFalse(CollectionUtils.isNullOrEmpty(Arrays.asList("something")));
    }

    @Test
    public void join_NullCollection_ReturnsNull() {
        MatcherAssert.assertThat(CollectionUtils.join(null, ","), Matchers.isEmptyString());
    }

    @Test
    public void join_EmptyCollection_ReturnsNull() {
        MatcherAssert.assertThat(CollectionUtils.join(Collections.<String>emptyList(), ","), Matchers.isEmptyString());
    }

    @Test
    public void join_SingleItemCollection_ReturnsItemAsString() {
        Assert.assertEquals("foo", CollectionUtils.join(Arrays.asList("foo"), ","));
    }

    @Test
    public void join_SingleItemCollectionOfNullString_ReturnsEmptyString() {
        List<String> list = new ArrayList<String>();
        list.add(null);
        Assert.assertEquals("", CollectionUtils.join(list, ","));
    }

    @Test
    public void join_MultiItemCollection_ReturnsItemsJoinedWithSeparator() {
        Assert.assertEquals("foo,bar,baz", CollectionUtils.join(Arrays.asList("foo", "bar", "baz"), ","));
    }

    @Test
    public void join_MultiItemCollectionWithNullItem_ReturnsItemsJoinedWithSeparator() {
        Assert.assertEquals("foo,,baz", CollectionUtils.join(Arrays.asList("foo", null, "baz"), ","));
    }

    @Test
    public void join_MultiItemCollectionWithAllNulls_ReturnsItemsJoinedWithSeparator() {
        Assert.assertEquals(",,", CollectionUtils.join(Arrays.<String>asList(null, null, null), ","));
    }
}

