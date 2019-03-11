/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
/**
 * Licensed under the MIT license.
 */
package com.mojang.brigadier.suggestion;


import com.mojang.brigadier.context.StringRange;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class SuggestionTest {
    @Test
    public void apply_insertation_start() {
        final Suggestion suggestion = new Suggestion(StringRange.at(0), "And so I said: ");
        Assert.assertThat(suggestion.apply("Hello world!"), CoreMatchers.equalTo("And so I said: Hello world!"));
    }

    @Test
    public void apply_insertation_middle() {
        final Suggestion suggestion = new Suggestion(StringRange.at(6), "small ");
        Assert.assertThat(suggestion.apply("Hello world!"), CoreMatchers.equalTo("Hello small world!"));
    }

    @Test
    public void apply_insertation_end() {
        final Suggestion suggestion = new Suggestion(StringRange.at(5), " world!");
        Assert.assertThat(suggestion.apply("Hello"), CoreMatchers.equalTo("Hello world!"));
    }

    @Test
    public void apply_replacement_start() {
        final Suggestion suggestion = new Suggestion(StringRange.between(0, 5), "Goodbye");
        Assert.assertThat(suggestion.apply("Hello world!"), CoreMatchers.equalTo("Goodbye world!"));
    }

    @Test
    public void apply_replacement_middle() {
        final Suggestion suggestion = new Suggestion(StringRange.between(6, 11), "Alex");
        Assert.assertThat(suggestion.apply("Hello world!"), CoreMatchers.equalTo("Hello Alex!"));
    }

    @Test
    public void apply_replacement_end() {
        final Suggestion suggestion = new Suggestion(StringRange.between(6, 12), "Creeper!");
        Assert.assertThat(suggestion.apply("Hello world!"), CoreMatchers.equalTo("Hello Creeper!"));
    }

    @Test
    public void apply_replacement_everything() {
        final Suggestion suggestion = new Suggestion(StringRange.between(0, 12), "Oh dear.");
        Assert.assertThat(suggestion.apply("Hello world!"), CoreMatchers.equalTo("Oh dear."));
    }

    @Test
    public void expand_unchanged() {
        final Suggestion suggestion = new Suggestion(StringRange.at(1), "oo");
        Assert.assertThat(suggestion.expand("f", StringRange.at(1)), CoreMatchers.equalTo(suggestion));
    }

    @Test
    public void expand_left() {
        final Suggestion suggestion = new Suggestion(StringRange.at(1), "oo");
        Assert.assertThat(suggestion.expand("f", StringRange.between(0, 1)), CoreMatchers.equalTo(new Suggestion(StringRange.between(0, 1), "foo")));
    }

    @Test
    public void expand_right() {
        final Suggestion suggestion = new Suggestion(StringRange.at(0), "minecraft:");
        Assert.assertThat(suggestion.expand("fish", StringRange.between(0, 4)), CoreMatchers.equalTo(new Suggestion(StringRange.between(0, 4), "minecraft:fish")));
    }

    @Test
    public void expand_both() {
        final Suggestion suggestion = new Suggestion(StringRange.at(11), "minecraft:");
        Assert.assertThat(suggestion.expand("give Steve fish_block", StringRange.between(5, 21)), CoreMatchers.equalTo(new Suggestion(StringRange.between(5, 21), "Steve minecraft:fish_block")));
    }

    @Test
    public void expand_replacement() {
        final Suggestion suggestion = new Suggestion(StringRange.between(6, 11), "strangers");
        Assert.assertThat(suggestion.expand("Hello world!", StringRange.between(0, 12)), CoreMatchers.equalTo(new Suggestion(StringRange.between(0, 12), "Hello strangers!")));
    }
}

