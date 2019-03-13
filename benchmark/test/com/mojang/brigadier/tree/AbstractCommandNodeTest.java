/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
/**
 * Licensed under the MIT license.
 */
package com.mojang.brigadier.tree;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractCommandNodeTest {
    @Mock
    private Command command;

    @Test
    public void testAddChild() throws Exception {
        final CommandNode<Object> node = getCommandNode();
        node.addChild(LiteralArgumentBuilder.literal("child1").build());
        node.addChild(LiteralArgumentBuilder.literal("child2").build());
        node.addChild(LiteralArgumentBuilder.literal("child1").build());
        Assert.assertThat(node.getChildren(), Matchers.hasSize(2));
    }

    @Test
    public void testAddChildMergesGrandchildren() throws Exception {
        final CommandNode<Object> node = getCommandNode();
        node.addChild(LiteralArgumentBuilder.literal("child").then(LiteralArgumentBuilder.literal("grandchild1")).build());
        node.addChild(LiteralArgumentBuilder.literal("child").then(LiteralArgumentBuilder.literal("grandchild2")).build());
        Assert.assertThat(node.getChildren(), Matchers.hasSize(1));
        Assert.assertThat(node.getChildren().iterator().next().getChildren(), Matchers.hasSize(2));
    }

    @Test
    public void testAddChildPreservesCommand() throws Exception {
        final CommandNode<Object> node = getCommandNode();
        node.addChild(LiteralArgumentBuilder.literal("child").executes(command).build());
        node.addChild(LiteralArgumentBuilder.literal("child").build());
        Assert.assertThat(node.getChildren().iterator().next().getCommand(), Matchers.is(command));
    }

    @Test
    public void testAddChildOverwritesCommand() throws Exception {
        final CommandNode<Object> node = getCommandNode();
        node.addChild(LiteralArgumentBuilder.literal("child").build());
        node.addChild(LiteralArgumentBuilder.literal("child").executes(command).build());
        Assert.assertThat(node.getChildren().iterator().next().getCommand(), Matchers.is(command));
    }
}

