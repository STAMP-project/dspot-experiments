/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
/**
 * Licensed under the MIT license.
 */
package com.mojang.brigadier.builder;


import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ArgumentBuilderTest {
    private ArgumentBuilderTest.TestableArgumentBuilder<Object> builder;

    @Test
    public void testArguments() throws Exception {
        final RequiredArgumentBuilder<Object, ?> argument = RequiredArgumentBuilder.argument("bar", IntegerArgumentType.integer());
        builder.then(argument);
        Assert.assertThat(builder.getArguments(), Matchers.hasSize(1));
        Assert.assertThat(builder.getArguments(), IsCollectionContaining.hasItem(((CommandNode<Object>) (argument.build()))));
    }

    @Test
    public void testRedirect() throws Exception {
        final CommandNode<Object> target = Mockito.mock(CommandNode.class);
        builder.redirect(target);
        Assert.assertThat(builder.getRedirect(), Matchers.is(target));
    }

    @Test(expected = IllegalStateException.class)
    public void testRedirect_withChild() throws Exception {
        final CommandNode<Object> target = Mockito.mock(CommandNode.class);
        builder.then(LiteralArgumentBuilder.literal("foo"));
        builder.redirect(target);
    }

    @Test(expected = IllegalStateException.class)
    public void testThen_withRedirect() throws Exception {
        final CommandNode<Object> target = Mockito.mock(CommandNode.class);
        builder.redirect(target);
        builder.then(LiteralArgumentBuilder.literal("foo"));
    }

    private static class TestableArgumentBuilder<S> extends ArgumentBuilder<S, ArgumentBuilderTest.TestableArgumentBuilder<S>> {
        @Override
        protected ArgumentBuilderTest.TestableArgumentBuilder<S> getThis() {
            return this;
        }

        @Override
        public CommandNode<S> build() {
            return null;
        }
    }
}

