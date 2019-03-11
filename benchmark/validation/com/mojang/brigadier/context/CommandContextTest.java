/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
/**
 * Licensed under the MIT license.
 */
package com.mojang.brigadier.context;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CommandContextTest {
    private CommandContextBuilder<Object> builder;

    @Mock
    private Object source;

    @Mock
    private CommandDispatcher<Object> dispatcher;

    @Mock
    private CommandNode<Object> rootNode;

    @Test(expected = IllegalArgumentException.class)
    public void testGetArgument_nonexistent() throws Exception {
        builder.build("").getArgument("foo", Object.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetArgument_wrongType() throws Exception {
        final CommandContext<Object> context = builder.withArgument("foo", new ParsedArgument(0, 1, 123)).build("123");
        context.getArgument("foo", String.class);
    }

    @Test
    public void testGetArgument() throws Exception {
        final CommandContext<Object> context = builder.withArgument("foo", new ParsedArgument(0, 1, 123)).build("123");
        Assert.assertThat(context.getArgument("foo", int.class), Matchers.is(123));
    }

    @Test
    public void testSource() throws Exception {
        Assert.assertThat(builder.build("").getSource(), Matchers.is(source));
    }

    @Test
    public void testRootNode() throws Exception {
        Assert.assertThat(builder.build("").getRootNode(), Matchers.is(rootNode));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEquals() throws Exception {
        final Object otherSource = new Object();
        final Command<Object> command = Mockito.mock(Command.class);
        final Command<Object> otherCommand = Mockito.mock(Command.class);
        final CommandNode<Object> rootNode = Mockito.mock(CommandNode.class);
        final CommandNode<Object> otherRootNode = Mockito.mock(CommandNode.class);
        final CommandNode<Object> node = Mockito.mock(CommandNode.class);
        final CommandNode<Object> otherNode = Mockito.mock(CommandNode.class);
        new com.google.common.testing.EqualsTester().addEqualityGroup(new CommandContextBuilder(dispatcher, source, rootNode, 0).build(""), new CommandContextBuilder(dispatcher, source, rootNode, 0).build("")).addEqualityGroup(new CommandContextBuilder(dispatcher, source, otherRootNode, 0).build(""), new CommandContextBuilder(dispatcher, source, otherRootNode, 0).build("")).addEqualityGroup(new CommandContextBuilder(dispatcher, otherSource, rootNode, 0).build(""), new CommandContextBuilder(dispatcher, otherSource, rootNode, 0).build("")).addEqualityGroup(new CommandContextBuilder(dispatcher, source, rootNode, 0).withCommand(command).build(""), new CommandContextBuilder(dispatcher, source, rootNode, 0).withCommand(command).build("")).addEqualityGroup(new CommandContextBuilder(dispatcher, source, rootNode, 0).withCommand(otherCommand).build(""), new CommandContextBuilder(dispatcher, source, rootNode, 0).withCommand(otherCommand).build("")).addEqualityGroup(new CommandContextBuilder(dispatcher, source, rootNode, 0).withArgument("foo", new ParsedArgument(0, 1, 123)).build("123"), new CommandContextBuilder(dispatcher, source, rootNode, 0).withArgument("foo", new ParsedArgument(0, 1, 123)).build("123")).addEqualityGroup(new CommandContextBuilder(dispatcher, source, rootNode, 0).withNode(node, StringRange.between(0, 3)).withNode(otherNode, StringRange.between(4, 6)).build("123 456"), new CommandContextBuilder(dispatcher, source, rootNode, 0).withNode(node, StringRange.between(0, 3)).withNode(otherNode, StringRange.between(4, 6)).build("123 456")).addEqualityGroup(new CommandContextBuilder(dispatcher, source, rootNode, 0).withNode(otherNode, StringRange.between(0, 3)).withNode(node, StringRange.between(4, 6)).build("123 456"), new CommandContextBuilder(dispatcher, source, rootNode, 0).withNode(otherNode, StringRange.between(0, 3)).withNode(node, StringRange.between(4, 6)).build("123 456")).testEquals();
    }
}

