/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
/**
 * Licensed under the MIT license.
 */
package com.mojang.brigadier.suggestion;


import com.google.common.collect.Lists;
import com.mojang.brigadier.context.StringRange;
import java.util.Collections;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SuggestionsTest {
    @Test
    public void merge_empty() {
        final Suggestions merged = Suggestions.merge("foo b", Collections.emptyList());
        Assert.assertThat(merged.isEmpty(), Matchers.is(true));
    }

    @Test
    public void merge_single() {
        final Suggestions suggestions = new Suggestions(StringRange.at(5), Lists.newArrayList(new Suggestion(StringRange.at(5), "ar")));
        final Suggestions merged = Suggestions.merge("foo b", Collections.singleton(suggestions));
        Assert.assertThat(merged, Matchers.equalTo(suggestions));
    }

    @Test
    public void merge_multiple() {
        final Suggestions a = new Suggestions(StringRange.at(5), Lists.newArrayList(new Suggestion(StringRange.at(5), "ar"), new Suggestion(StringRange.at(5), "az"), new Suggestion(StringRange.at(5), "Az")));
        final Suggestions b = new Suggestions(StringRange.between(4, 5), Lists.newArrayList(new Suggestion(StringRange.between(4, 5), "foo"), new Suggestion(StringRange.between(4, 5), "qux"), new Suggestion(StringRange.between(4, 5), "apple"), new Suggestion(StringRange.between(4, 5), "Bar")));
        final Suggestions merged = Suggestions.merge("foo b", Lists.newArrayList(a, b));
        Assert.assertThat(merged.getList(), Matchers.equalTo(Lists.newArrayList(new Suggestion(StringRange.between(4, 5), "apple"), new Suggestion(StringRange.between(4, 5), "bar"), new Suggestion(StringRange.between(4, 5), "Bar"), new Suggestion(StringRange.between(4, 5), "baz"), new Suggestion(StringRange.between(4, 5), "bAz"), new Suggestion(StringRange.between(4, 5), "foo"), new Suggestion(StringRange.between(4, 5), "qux"))));
    }
}

