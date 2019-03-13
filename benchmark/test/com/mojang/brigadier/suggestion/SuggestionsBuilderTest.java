/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
/**
 * Licensed under the MIT license.
 */
package com.mojang.brigadier.suggestion;


import com.google.common.collect.Lists;
import com.mojang.brigadier.context.StringRange;
import java.util.List;
import java.util.stream.Collectors;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class SuggestionsBuilderTest {
    private SuggestionsBuilder builder;

    @Test
    public void suggest_appends() {
        final Suggestions result = builder.suggest("world!").build();
        Assert.assertThat(result.getList(), CoreMatchers.equalTo(Lists.newArrayList(new Suggestion(StringRange.between(6, 7), "world!"))));
        Assert.assertThat(result.getRange(), CoreMatchers.equalTo(StringRange.between(6, 7)));
        Assert.assertThat(result.isEmpty(), CoreMatchers.is(false));
    }

    @Test
    public void suggest_replaces() {
        final Suggestions result = builder.suggest("everybody").build();
        Assert.assertThat(result.getList(), CoreMatchers.equalTo(Lists.newArrayList(new Suggestion(StringRange.between(6, 7), "everybody"))));
        Assert.assertThat(result.getRange(), CoreMatchers.equalTo(StringRange.between(6, 7)));
        Assert.assertThat(result.isEmpty(), CoreMatchers.is(false));
    }

    @Test
    public void suggest_noop() {
        final Suggestions result = builder.suggest("w").build();
        Assert.assertThat(result.getList(), CoreMatchers.equalTo(Lists.newArrayList()));
        Assert.assertThat(result.isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void suggest_multiple() {
        final Suggestions result = builder.suggest("world!").suggest("everybody").suggest("weekend").build();
        Assert.assertThat(result.getList(), CoreMatchers.equalTo(Lists.newArrayList(new Suggestion(StringRange.between(6, 7), "everybody"), new Suggestion(StringRange.between(6, 7), "weekend"), new Suggestion(StringRange.between(6, 7), "world!"))));
        Assert.assertThat(result.getRange(), CoreMatchers.equalTo(StringRange.between(6, 7)));
        Assert.assertThat(result.isEmpty(), CoreMatchers.is(false));
    }

    @Test
    public void restart() {
        builder.suggest("won't be included in restart");
        final SuggestionsBuilder other = builder.restart();
        Assert.assertThat(other, CoreMatchers.is(CoreMatchers.not(builder)));
        Assert.assertThat(other.getInput(), CoreMatchers.equalTo(builder.getInput()));
        Assert.assertThat(other.getStart(), CoreMatchers.is(builder.getStart()));
        Assert.assertThat(other.getRemaining(), CoreMatchers.equalTo(builder.getRemaining()));
    }

    @Test
    public void sort_alphabetical() {
        Suggestions result = builder.suggest("2").suggest("4").suggest("6").suggest("8").suggest("30").suggest("32").build();
        List<String> actual = result.getList().stream().map(Suggestion::getText).collect(Collectors.toList());
        Assert.assertThat(actual, CoreMatchers.equalTo(Lists.newArrayList("2", "30", "32", "4", "6", "8")));
    }

    @Test
    public void sort_numerical() {
        Suggestions result = builder.suggest(2).suggest(4).suggest(6).suggest(8).suggest(30).suggest(32).build();
        List<String> actual = result.getList().stream().map(Suggestion::getText).collect(Collectors.toList());
        Assert.assertThat(actual, CoreMatchers.equalTo(Lists.newArrayList("2", "4", "6", "8", "30", "32")));
    }

    @Test
    public void sort_mixed() {
        Suggestions result = builder.suggest("11").suggest("22").suggest("33").suggest("a").suggest("b").suggest("c").suggest(2).suggest(4).suggest(6).suggest(8).suggest(30).suggest(32).suggest("3a").suggest("a3").build();
        List<String> actual = result.getList().stream().map(Suggestion::getText).collect(Collectors.toList());
        Assert.assertThat(actual, CoreMatchers.equalTo(Lists.newArrayList("11", "2", "22", "33", "3a", "4", "6", "8", "30", "32", "a", "a3", "b", "c")));
    }
}

