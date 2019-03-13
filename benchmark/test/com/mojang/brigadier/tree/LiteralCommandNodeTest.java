/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
/**
 * Licensed under the MIT license.
 */
package com.mojang.brigadier.tree;


import CommandSyntaxException.BUILT_IN_EXCEPTIONS;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;


public class LiteralCommandNodeTest extends AbstractCommandNodeTest {
    private LiteralCommandNode<Object> node;

    private CommandContextBuilder<Object> contextBuilder;

    @Test
    public void testParse() throws Exception {
        final StringReader reader = new StringReader("foo bar");
        node.parse(reader, contextBuilder);
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(" bar"));
    }

    @Test
    public void testParseExact() throws Exception {
        final StringReader reader = new StringReader("foo");
        node.parse(reader, contextBuilder);
        Assert.assertThat(reader.getRemaining(), Matchers.equalTo(""));
    }

    @Test
    public void testParseSimilar() throws Exception {
        final StringReader reader = new StringReader("foobar");
        try {
            node.parse(reader, contextBuilder);
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.literalIncorrect()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void testParseInvalid() throws Exception {
        final StringReader reader = new StringReader("bar");
        try {
            node.parse(reader, contextBuilder);
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.literalIncorrect()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void testUsage() throws Exception {
        Assert.assertThat(node.getUsageText(), Matchers.is("foo"));
    }

    @Test
    public void testSuggestions() throws Exception {
        final Suggestions empty = node.listSuggestions(contextBuilder.build(""), new SuggestionsBuilder("", 0)).join();
        Assert.assertThat(empty.getList(), Matchers.equalTo(Lists.newArrayList(new com.mojang.brigadier.suggestion.Suggestion(StringRange.at(0), "foo"))));
        final Suggestions foo = node.listSuggestions(contextBuilder.build("foo"), new SuggestionsBuilder("foo", 0)).join();
        Assert.assertThat(foo.isEmpty(), Matchers.is(true));
        final Suggestions food = node.listSuggestions(contextBuilder.build("food"), new SuggestionsBuilder("food", 0)).join();
        Assert.assertThat(food.isEmpty(), Matchers.is(true));
        final Suggestions b = node.listSuggestions(contextBuilder.build("b"), new SuggestionsBuilder("b", 0)).join();
        Assert.assertThat(food.isEmpty(), Matchers.is(true));
    }

    @Test
    public void testEquals() throws Exception {
        @SuppressWarnings("unchecked")
        final Command<Object> command = Mockito.mock(Command.class);
        new com.google.common.testing.EqualsTester().addEqualityGroup(literal("foo").build(), literal("foo").build()).addEqualityGroup(literal("bar").executes(command).build(), literal("bar").executes(command).build()).addEqualityGroup(literal("bar").build(), literal("bar").build()).addEqualityGroup(literal("foo").then(literal("bar")).build(), literal("foo").then(literal("bar")).build()).testEquals();
    }

    @Test
    public void testCreateBuilder() throws Exception {
        final LiteralArgumentBuilder<Object> builder = node.createBuilder();
        Assert.assertThat(builder.getLiteral(), Matchers.is(node.getLiteral()));
        Assert.assertThat(builder.getRequirement(), Matchers.is(node.getRequirement()));
        Assert.assertThat(builder.getCommand(), Matchers.is(node.getCommand()));
    }
}

