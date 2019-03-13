/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
/**
 * Licensed under the MIT license.
 */
package com.mojang.brigadier.tree;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;


public class ArgumentCommandNodeTest extends AbstractCommandNodeTest {
    private ArgumentCommandNode<Object, Integer> node;

    private CommandContextBuilder<Object> contextBuilder;

    @Test
    public void testParse() throws Exception {
        final StringReader reader = new StringReader("123 456");
        node.parse(reader, contextBuilder);
        Assert.assertThat(contextBuilder.getArguments().containsKey("foo"), Matchers.is(true));
        Assert.assertThat(contextBuilder.getArguments().get("foo").getResult(), Matchers.is(123));
    }

    @Test
    public void testUsage() throws Exception {
        Assert.assertThat(node.getUsageText(), Matchers.is("<foo>"));
    }

    @Test
    public void testSuggestions() throws Exception {
        final Suggestions result = node.listSuggestions(contextBuilder.build(""), new SuggestionsBuilder("", 0)).join();
        Assert.assertThat(result.isEmpty(), Matchers.is(true));
    }

    @Test
    public void testEquals() throws Exception {
        @SuppressWarnings("unchecked")
        final Command<Object> command = ((Command<Object>) (Mockito.mock(Command.class)));
        new com.google.common.testing.EqualsTester().addEqualityGroup(argument("foo", integer()).build(), argument("foo", integer()).build()).addEqualityGroup(argument("foo", integer()).executes(command).build(), argument("foo", integer()).executes(command).build()).addEqualityGroup(argument("bar", com.mojang.brigadier.arguments.IntegerArgumentType.integer((-100), 100)).build(), argument("bar", com.mojang.brigadier.arguments.IntegerArgumentType.integer((-100), 100)).build()).addEqualityGroup(argument("foo", com.mojang.brigadier.arguments.IntegerArgumentType.integer((-100), 100)).build(), argument("foo", com.mojang.brigadier.arguments.IntegerArgumentType.integer((-100), 100)).build()).addEqualityGroup(argument("foo", integer()).then(argument("bar", integer())).build(), argument("foo", integer()).then(argument("bar", integer())).build()).testEquals();
    }

    @Test
    public void testCreateBuilder() throws Exception {
        final RequiredArgumentBuilder<Object, Integer> builder = node.createBuilder();
        Assert.assertThat(builder.getName(), Matchers.is(node.getName()));
        Assert.assertThat(builder.getType(), Matchers.is(node.getType()));
        Assert.assertThat(builder.getRequirement(), Matchers.is(node.getRequirement()));
        Assert.assertThat(builder.getCommand(), Matchers.is(node.getCommand()));
    }
}

