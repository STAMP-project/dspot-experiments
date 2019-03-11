/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
/**
 * Licensed under the MIT license.
 */
package com.mojang.brigadier.builder;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class RequiredArgumentBuilderTest {
    @Mock
    private ArgumentType<Integer> type;

    private RequiredArgumentBuilder<Object, Integer> builder;

    @Mock
    private Command<Object> command;

    @Test
    public void testBuild() throws Exception {
        final ArgumentCommandNode<Object, Integer> node = builder.build();
        Assert.assertThat(node.getName(), Matchers.is("foo"));
        Assert.assertThat(node.getType(), Matchers.is(type));
    }

    @Test
    public void testBuildWithExecutor() throws Exception {
        final ArgumentCommandNode<Object, Integer> node = builder.executes(command).build();
        Assert.assertThat(node.getName(), Matchers.is("foo"));
        Assert.assertThat(node.getType(), Matchers.is(type));
        Assert.assertThat(node.getCommand(), Matchers.is(command));
    }

    @Test
    public void testBuildWithChildren() throws Exception {
        builder.then(RequiredArgumentBuilder.argument("bar", IntegerArgumentType.integer()));
        builder.then(RequiredArgumentBuilder.argument("baz", IntegerArgumentType.integer()));
        final ArgumentCommandNode<Object, Integer> node = builder.build();
        Assert.assertThat(node.getChildren(), Matchers.hasSize(2));
    }
}

