/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
/**
 * Licensed under the MIT license.
 */
package com.mojang.brigadier.tree;


import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;


public class RootCommandNodeTest extends AbstractCommandNodeTest {
    private RootCommandNode<Object> node;

    @Test
    public void testParse() throws Exception {
        final StringReader reader = new StringReader("hello world");
        node.parse(reader, new com.mojang.brigadier.context.CommandContextBuilder(new com.mojang.brigadier.CommandDispatcher(), new Object(), new RootCommandNode(), 0));
        Assert.assertThat(reader.getCursor(), Matchers.is(0));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddChildNoRoot() throws Exception {
        node.addChild(new RootCommandNode());
    }

    @Test
    public void testUsage() throws Exception {
        Assert.assertThat(node.getUsageText(), Matchers.is(""));
    }

    @Test
    public void testSuggestions() throws Exception {
        final CommandContext<Object> context = Mockito.mock(CommandContext.class);
        final Suggestions result = node.listSuggestions(context, new SuggestionsBuilder("", 0)).join();
        Assert.assertThat(result.isEmpty(), Matchers.is(true));
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateBuilder() throws Exception {
        node.createBuilder();
    }

    @Test
    public void testEquals() throws Exception {
        new com.google.common.testing.EqualsTester().addEqualityGroup(new RootCommandNode(), new RootCommandNode()).addEqualityGroup(new RootCommandNode<Object>() {
            {
                addChild(literal("foo").build());
            }
        }, new RootCommandNode<Object>() {
            {
                addChild(literal("foo").build());
            }
        }).testEquals();
    }
}

