/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
/**
 * Licensed under the MIT license.
 */
package com.mojang.brigadier;


import CommandSyntaxException.BUILT_IN_EXCEPTIONS;
import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CommandDispatcherTest {
    private CommandDispatcher<Object> subject;

    @Mock
    private Command<Object> command;

    @Mock
    private Object source;

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateAndExecuteCommand() throws Exception {
        subject.register(LiteralArgumentBuilder.literal("foo").executes(command));
        Assert.assertThat(subject.execute("foo", source), Matchers.is(42));
        Mockito.verify(command).run(ArgumentMatchers.any(CommandContext.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateAndExecuteOffsetCommand() throws Exception {
        subject.register(LiteralArgumentBuilder.literal("foo").executes(command));
        Assert.assertThat(subject.execute(CommandDispatcherTest.inputWithOffset("/foo", 1), source), Matchers.is(42));
        Mockito.verify(command).run(ArgumentMatchers.any(CommandContext.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateAndMergeCommands() throws Exception {
        subject.register(LiteralArgumentBuilder.literal("base").then(LiteralArgumentBuilder.literal("foo").executes(command)));
        subject.register(LiteralArgumentBuilder.literal("base").then(LiteralArgumentBuilder.literal("bar").executes(command)));
        Assert.assertThat(subject.execute("base foo", source), Matchers.is(42));
        Assert.assertThat(subject.execute("base bar", source), Matchers.is(42));
        Mockito.verify(command, Mockito.times(2)).run(ArgumentMatchers.any(CommandContext.class));
    }

    @Test
    public void testExecuteUnknownCommand() throws Exception {
        subject.register(LiteralArgumentBuilder.literal("bar"));
        subject.register(LiteralArgumentBuilder.literal("baz"));
        try {
            subject.execute("foo", source);
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void testExecuteImpermissibleCommand() throws Exception {
        subject.register(LiteralArgumentBuilder.literal("foo").requires(( s) -> false));
        try {
            subject.execute("foo", source);
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void testExecuteEmptyCommand() throws Exception {
        subject.register(LiteralArgumentBuilder.literal(""));
        try {
            subject.execute("", source);
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void testExecuteUnknownSubcommand() throws Exception {
        subject.register(LiteralArgumentBuilder.literal("foo").executes(command));
        try {
            subject.execute("foo bar", source);
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument()));
            Assert.assertThat(ex.getCursor(), Matchers.is(4));
        }
    }

    @Test
    public void testExecuteIncorrectLiteral() throws Exception {
        subject.register(LiteralArgumentBuilder.literal("foo").executes(command).then(LiteralArgumentBuilder.literal("bar")));
        try {
            subject.execute("foo baz", source);
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument()));
            Assert.assertThat(ex.getCursor(), Matchers.is(4));
        }
    }

    @Test
    public void testExecuteAmbiguousIncorrectArgument() throws Exception {
        subject.register(LiteralArgumentBuilder.literal("foo").executes(command).then(LiteralArgumentBuilder.literal("bar")).then(LiteralArgumentBuilder.literal("baz")));
        try {
            subject.execute("foo unknown", source);
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument()));
            Assert.assertThat(ex.getCursor(), Matchers.is(4));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteSubcommand() throws Exception {
        final Command<Object> subCommand = Mockito.mock(Command.class);
        Mockito.when(subCommand.run(ArgumentMatchers.any())).thenReturn(100);
        subject.register(LiteralArgumentBuilder.literal("foo").then(LiteralArgumentBuilder.literal("a")).then(LiteralArgumentBuilder.literal("=").executes(subCommand)).then(LiteralArgumentBuilder.literal("c")).executes(command));
        Assert.assertThat(subject.execute("foo =", source), Matchers.is(100));
        Mockito.verify(subCommand).run(ArgumentMatchers.any(CommandContext.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testParseIncompleteLiteral() throws Exception {
        subject.register(LiteralArgumentBuilder.literal("foo").then(LiteralArgumentBuilder.literal("bar").executes(command)));
        final ParseResults<Object> parse = subject.parse("foo ", source);
        Assert.assertThat(parse.getReader().getRemaining(), Matchers.equalTo(" "));
        Assert.assertThat(parse.getContext().getNodes().size(), Matchers.is(1));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testParseIncompleteArgument() throws Exception {
        subject.register(LiteralArgumentBuilder.literal("foo").then(RequiredArgumentBuilder.argument("bar", IntegerArgumentType.integer()).executes(command)));
        final ParseResults<Object> parse = subject.parse("foo ", source);
        Assert.assertThat(parse.getReader().getRemaining(), Matchers.equalTo(" "));
        Assert.assertThat(parse.getContext().getNodes().size(), Matchers.is(1));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteAmbiguiousParentSubcommand() throws Exception {
        final Command<Object> subCommand = Mockito.mock(Command.class);
        Mockito.when(subCommand.run(ArgumentMatchers.any())).thenReturn(100);
        subject.register(LiteralArgumentBuilder.literal("test").then(RequiredArgumentBuilder.argument("incorrect", IntegerArgumentType.integer()).executes(command)).then(RequiredArgumentBuilder.argument("right", IntegerArgumentType.integer()).then(RequiredArgumentBuilder.argument("sub", IntegerArgumentType.integer()).executes(subCommand))));
        Assert.assertThat(subject.execute("test 1 2", source), Matchers.is(100));
        Mockito.verify(subCommand).run(ArgumentMatchers.any(CommandContext.class));
        Mockito.verify(command, Mockito.never()).run(ArgumentMatchers.any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteAmbiguiousParentSubcommandViaRedirect() throws Exception {
        final Command<Object> subCommand = Mockito.mock(Command.class);
        Mockito.when(subCommand.run(ArgumentMatchers.any())).thenReturn(100);
        final LiteralCommandNode<Object> real = subject.register(LiteralArgumentBuilder.literal("test").then(RequiredArgumentBuilder.argument("incorrect", IntegerArgumentType.integer()).executes(command)).then(RequiredArgumentBuilder.argument("right", IntegerArgumentType.integer()).then(RequiredArgumentBuilder.argument("sub", IntegerArgumentType.integer()).executes(subCommand))));
        subject.register(LiteralArgumentBuilder.literal("redirect").redirect(real));
        Assert.assertThat(subject.execute("redirect 1 2", source), Matchers.is(100));
        Mockito.verify(subCommand).run(ArgumentMatchers.any(CommandContext.class));
        Mockito.verify(command, Mockito.never()).run(ArgumentMatchers.any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteRedirectedMultipleTimes() throws Exception {
        final LiteralCommandNode<Object> concreteNode = subject.register(LiteralArgumentBuilder.literal("actual").executes(command));
        final LiteralCommandNode<Object> redirectNode = subject.register(LiteralArgumentBuilder.literal("redirected").redirect(subject.getRoot()));
        final String input = "redirected redirected actual";
        final ParseResults<Object> parse = subject.parse(input, source);
        Assert.assertThat(parse.getContext().getRange().get(input), Matchers.equalTo("redirected"));
        Assert.assertThat(parse.getContext().getNodes().size(), Matchers.is(1));
        Assert.assertThat(parse.getContext().getRootNode(), Matchers.is(subject.getRoot()));
        Assert.assertThat(parse.getContext().getNodes().get(0).getRange(), Matchers.equalTo(parse.getContext().getRange()));
        Assert.assertThat(parse.getContext().getNodes().get(0).getNode(), Matchers.is(redirectNode));
        final CommandContextBuilder<Object> child1 = parse.getContext().getChild();
        Assert.assertThat(child1, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(child1.getRange().get(input), Matchers.equalTo("redirected"));
        Assert.assertThat(child1.getNodes().size(), Matchers.is(1));
        Assert.assertThat(child1.getRootNode(), Matchers.is(subject.getRoot()));
        Assert.assertThat(child1.getNodes().get(0).getRange(), Matchers.equalTo(child1.getRange()));
        Assert.assertThat(child1.getNodes().get(0).getNode(), Matchers.is(redirectNode));
        final CommandContextBuilder<Object> child2 = child1.getChild();
        Assert.assertThat(child2, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(child2.getRange().get(input), Matchers.equalTo("actual"));
        Assert.assertThat(child2.getNodes().size(), Matchers.is(1));
        Assert.assertThat(child2.getRootNode(), Matchers.is(subject.getRoot()));
        Assert.assertThat(child2.getNodes().get(0).getRange(), Matchers.equalTo(child2.getRange()));
        Assert.assertThat(child2.getNodes().get(0).getNode(), Matchers.is(concreteNode));
        Assert.assertThat(subject.execute(parse), Matchers.is(42));
        Mockito.verify(command).run(ArgumentMatchers.any(CommandContext.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteRedirected() throws Exception {
        final RedirectModifier<Object> modifier = Mockito.mock(RedirectModifier.class);
        final Object source1 = new Object();
        final Object source2 = new Object();
        Mockito.when(modifier.apply(ArgumentMatchers.argThat(Matchers.hasProperty("source", Matchers.is(source))))).thenReturn(Lists.newArrayList(source1, source2));
        final LiteralCommandNode<Object> concreteNode = subject.register(LiteralArgumentBuilder.literal("actual").executes(command));
        final LiteralCommandNode<Object> redirectNode = subject.register(LiteralArgumentBuilder.literal("redirected").fork(subject.getRoot(), modifier));
        final String input = "redirected actual";
        final ParseResults<Object> parse = subject.parse(input, source);
        Assert.assertThat(parse.getContext().getRange().get(input), Matchers.equalTo("redirected"));
        Assert.assertThat(parse.getContext().getNodes().size(), Matchers.is(1));
        Assert.assertThat(parse.getContext().getRootNode(), Matchers.equalTo(subject.getRoot()));
        Assert.assertThat(parse.getContext().getNodes().get(0).getRange(), Matchers.equalTo(parse.getContext().getRange()));
        Assert.assertThat(parse.getContext().getNodes().get(0).getNode(), Matchers.is(redirectNode));
        Assert.assertThat(parse.getContext().getSource(), Matchers.is(source));
        final CommandContextBuilder<Object> parent = parse.getContext().getChild();
        Assert.assertThat(parent, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(parent.getRange().get(input), Matchers.equalTo("actual"));
        Assert.assertThat(parent.getNodes().size(), Matchers.is(1));
        Assert.assertThat(parse.getContext().getRootNode(), Matchers.equalTo(subject.getRoot()));
        Assert.assertThat(parent.getNodes().get(0).getRange(), Matchers.equalTo(parent.getRange()));
        Assert.assertThat(parent.getNodes().get(0).getNode(), Matchers.is(concreteNode));
        Assert.assertThat(parent.getSource(), Matchers.is(source));
        Assert.assertThat(subject.execute(parse), Matchers.is(2));
        Mockito.verify(command).run(ArgumentMatchers.argThat(Matchers.hasProperty("source", Matchers.is(source1))));
        Mockito.verify(command).run(ArgumentMatchers.argThat(Matchers.hasProperty("source", Matchers.is(source2))));
    }

    @Test
    public void testExecuteOrphanedSubcommand() throws Exception {
        subject.register(LiteralArgumentBuilder.literal("foo").then(RequiredArgumentBuilder.argument("bar", IntegerArgumentType.integer())).executes(command));
        try {
            subject.execute("foo 5", source);
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand()));
            Assert.assertThat(ex.getCursor(), Matchers.is(5));
        }
    }

    @Test
    public void testExecute_invalidOther() throws Exception {
        final Command<Object> wrongCommand = Mockito.mock(Command.class);
        subject.register(LiteralArgumentBuilder.literal("w").executes(wrongCommand));
        subject.register(LiteralArgumentBuilder.literal("world").executes(command));
        Assert.assertThat(subject.execute("world", source), Matchers.is(42));
        Mockito.verify(wrongCommand, Mockito.never()).run(ArgumentMatchers.any());
        Mockito.verify(command).run(ArgumentMatchers.any());
    }

    @Test
    public void parse_noSpaceSeparator() throws Exception {
        subject.register(LiteralArgumentBuilder.literal("foo").then(RequiredArgumentBuilder.argument("bar", IntegerArgumentType.integer()).executes(command)));
        try {
            subject.execute("foo$", source);
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void testExecuteInvalidSubcommand() throws Exception {
        subject.register(LiteralArgumentBuilder.literal("foo").then(RequiredArgumentBuilder.argument("bar", IntegerArgumentType.integer())).executes(command));
        try {
            subject.execute("foo bar", source);
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.readerExpectedInt()));
            Assert.assertThat(ex.getCursor(), Matchers.is(4));
        }
    }

    @Test
    public void testGetPath() {
        final LiteralCommandNode<Object> bar = LiteralArgumentBuilder.literal("bar").build();
        subject.register(LiteralArgumentBuilder.literal("foo").then(bar));
        Assert.assertThat(subject.getPath(bar), Matchers.equalTo(Lists.newArrayList("foo", "bar")));
    }

    @Test
    public void testFindNodeExists() {
        final LiteralCommandNode<Object> bar = LiteralArgumentBuilder.literal("bar").build();
        subject.register(LiteralArgumentBuilder.literal("foo").then(bar));
        Assert.assertThat(subject.findNode(Lists.newArrayList("foo", "bar")), Matchers.is(bar));
    }

    @Test
    public void testFindNodeDoesntExist() {
        Assert.assertThat(subject.findNode(Lists.newArrayList("foo", "bar")), Matchers.is(Matchers.nullValue()));
    }
}

