/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
/**
 * Licensed under the MIT license.
 */
package com.mojang.brigadier.builder;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;


public class LiteralArgumentBuilderTest {
    private LiteralArgumentBuilder<Object> builder;

    @Mock
    private Command<Object> command;

    @Test
    public void testBuild() throws Exception {
        final LiteralCommandNode<Object> node = builder.build();
        Assert.assertThat(node.getLiteral(), Matchers.is("foo"));
    }

    @Test
    public void testBuildWithExecutor() throws Exception {
        final LiteralCommandNode<Object> node = builder.executes(command).build();
        Assert.assertThat(node.getLiteral(), Matchers.is("foo"));
        Assert.assertThat(node.getCommand(), Matchers.is(command));
    }

    @Test
    public void testBuildWithChildren() throws Exception {
        builder.then(RequiredArgumentBuilder.argument("bar", IntegerArgumentType.integer()));
        builder.then(RequiredArgumentBuilder.argument("baz", IntegerArgumentType.integer()));
        final LiteralCommandNode<Object> node = builder.build();
        Assert.assertThat(node.getChildren(), Matchers.hasSize(2));
    }
}

