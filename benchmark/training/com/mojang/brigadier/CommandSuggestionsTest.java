/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
/**
 * Licensed under the MIT license.
 */
package com.mojang.brigadier;


import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CommandSuggestionsTest {
    private CommandDispatcher<Object> subject;

    @Mock
    private Object source;

    @Test
    public void getCompletionSuggestions_rootCommands() throws Exception {
        subject.register(LiteralArgumentBuilder.literal("foo"));
        subject.register(LiteralArgumentBuilder.literal("bar"));
        subject.register(LiteralArgumentBuilder.literal("baz"));
        final Suggestions result = subject.getCompletionSuggestions(subject.parse("", source)).join();
        Assert.assertThat(result.getRange(), Matchers.equalTo(StringRange.at(0)));
        Assert.assertThat(result.getList(), Matchers.equalTo(Lists.newArrayList(new com.mojang.brigadier.suggestion.Suggestion(StringRange.at(0), "bar"), new com.mojang.brigadier.suggestion.Suggestion(StringRange.at(0), "baz"), new com.mojang.brigadier.suggestion.Suggestion(StringRange.at(0), "foo"))));
    }

    @Test
    public void getCompletionSuggestions_rootCommands_withInputOffset() throws Exception {
        subject.register(LiteralArgumentBuilder.literal("foo"));
        subject.register(LiteralArgumentBuilder.literal("bar"));
        subject.register(LiteralArgumentBuilder.literal("baz"));
        final Suggestions result = subject.getCompletionSuggestions(subject.parse(CommandSuggestionsTest.inputWithOffset("OOO", 3), source)).join();
        Assert.assertThat(result.getRange(), Matchers.equalTo(StringRange.at(3)));
        Assert.assertThat(result.getList(), Matchers.equalTo(Lists.newArrayList(new com.mojang.brigadier.suggestion.Suggestion(StringRange.at(3), "bar"), new com.mojang.brigadier.suggestion.Suggestion(StringRange.at(3), "baz"), new com.mojang.brigadier.suggestion.Suggestion(StringRange.at(3), "foo"))));
    }

    @Test
    public void getCompletionSuggestions_rootCommands_partial() throws Exception {
        subject.register(LiteralArgumentBuilder.literal("foo"));
        subject.register(LiteralArgumentBuilder.literal("bar"));
        subject.register(LiteralArgumentBuilder.literal("baz"));
        final Suggestions result = subject.getCompletionSuggestions(subject.parse("b", source)).join();
        Assert.assertThat(result.getRange(), Matchers.equalTo(StringRange.between(0, 1)));
        Assert.assertThat(result.getList(), Matchers.equalTo(Lists.newArrayList(new com.mojang.brigadier.suggestion.Suggestion(StringRange.between(0, 1), "bar"), new com.mojang.brigadier.suggestion.Suggestion(StringRange.between(0, 1), "baz"))));
    }

    @Test
    public void getCompletionSuggestions_rootCommands_partial_withInputOffset() throws Exception {
        subject.register(LiteralArgumentBuilder.literal("foo"));
        subject.register(LiteralArgumentBuilder.literal("bar"));
        subject.register(LiteralArgumentBuilder.literal("baz"));
        final Suggestions result = subject.getCompletionSuggestions(subject.parse(CommandSuggestionsTest.inputWithOffset("Zb", 1), source)).join();
        Assert.assertThat(result.getRange(), Matchers.equalTo(StringRange.between(1, 2)));
        Assert.assertThat(result.getList(), Matchers.equalTo(Lists.newArrayList(new com.mojang.brigadier.suggestion.Suggestion(StringRange.between(1, 2), "bar"), new com.mojang.brigadier.suggestion.Suggestion(StringRange.between(1, 2), "baz"))));
    }

    @Test
    public void getCompletionSuggestions_subCommands() throws Exception {
        subject.register(LiteralArgumentBuilder.literal("parent").then(LiteralArgumentBuilder.literal("foo")).then(LiteralArgumentBuilder.literal("bar")).then(LiteralArgumentBuilder.literal("baz")));
        final Suggestions result = subject.getCompletionSuggestions(subject.parse("parent ", source)).join();
        Assert.assertThat(result.getRange(), Matchers.equalTo(StringRange.at(7)));
        Assert.assertThat(result.getList(), Matchers.equalTo(Lists.newArrayList(new com.mojang.brigadier.suggestion.Suggestion(StringRange.at(7), "bar"), new com.mojang.brigadier.suggestion.Suggestion(StringRange.at(7), "baz"), new com.mojang.brigadier.suggestion.Suggestion(StringRange.at(7), "foo"))));
    }

    @Test
    public void getCompletionSuggestions_movingCursor_subCommands() throws Exception {
        subject.register(LiteralArgumentBuilder.literal("parent_one").then(LiteralArgumentBuilder.literal("faz")).then(LiteralArgumentBuilder.literal("fbz")).then(LiteralArgumentBuilder.literal("gaz")));
        subject.register(LiteralArgumentBuilder.literal("parent_two"));
        testSuggestions("parent_one faz ", 0, StringRange.at(0), "parent_one", "parent_two");
        testSuggestions("parent_one faz ", 1, StringRange.between(0, 1), "parent_one", "parent_two");
        testSuggestions("parent_one faz ", 7, StringRange.between(0, 7), "parent_one", "parent_two");
        testSuggestions("parent_one faz ", 8, StringRange.between(0, 8), "parent_one");
        testSuggestions("parent_one faz ", 10, StringRange.at(0));
        testSuggestions("parent_one faz ", 11, StringRange.at(11), "faz", "fbz", "gaz");
        testSuggestions("parent_one faz ", 12, StringRange.between(11, 12), "faz", "fbz");
        testSuggestions("parent_one faz ", 13, StringRange.between(11, 13), "faz");
        testSuggestions("parent_one faz ", 14, StringRange.at(0));
        testSuggestions("parent_one faz ", 15, StringRange.at(0));
    }

    @Test
    public void getCompletionSuggestions_subCommands_partial() throws Exception {
        subject.register(LiteralArgumentBuilder.literal("parent").then(LiteralArgumentBuilder.literal("foo")).then(LiteralArgumentBuilder.literal("bar")).then(LiteralArgumentBuilder.literal("baz")));
        final ParseResults<Object> parse = subject.parse("parent b", source);
        final Suggestions result = subject.getCompletionSuggestions(parse).join();
        Assert.assertThat(result.getRange(), Matchers.equalTo(StringRange.between(7, 8)));
        Assert.assertThat(result.getList(), Matchers.equalTo(Lists.newArrayList(new com.mojang.brigadier.suggestion.Suggestion(StringRange.between(7, 8), "bar"), new com.mojang.brigadier.suggestion.Suggestion(StringRange.between(7, 8), "baz"))));
    }

    @Test
    public void getCompletionSuggestions_subCommands_partial_withInputOffset() throws Exception {
        subject.register(LiteralArgumentBuilder.literal("parent").then(LiteralArgumentBuilder.literal("foo")).then(LiteralArgumentBuilder.literal("bar")).then(LiteralArgumentBuilder.literal("baz")));
        final ParseResults<Object> parse = subject.parse(CommandSuggestionsTest.inputWithOffset("junk parent b", 5), source);
        final Suggestions result = subject.getCompletionSuggestions(parse).join();
        Assert.assertThat(result.getRange(), Matchers.equalTo(StringRange.between(12, 13)));
        Assert.assertThat(result.getList(), Matchers.equalTo(Lists.newArrayList(new com.mojang.brigadier.suggestion.Suggestion(StringRange.between(12, 13), "bar"), new com.mojang.brigadier.suggestion.Suggestion(StringRange.between(12, 13), "baz"))));
    }

    @Test
    public void getCompletionSuggestions_redirect() throws Exception {
        final LiteralCommandNode<Object> actual = subject.register(LiteralArgumentBuilder.literal("actual").then(LiteralArgumentBuilder.literal("sub")));
        subject.register(LiteralArgumentBuilder.literal("redirect").redirect(actual));
        final ParseResults<Object> parse = subject.parse("redirect ", source);
        final Suggestions result = subject.getCompletionSuggestions(parse).join();
        Assert.assertThat(result.getRange(), Matchers.equalTo(StringRange.at(9)));
        Assert.assertThat(result.getList(), Matchers.equalTo(Lists.newArrayList(new com.mojang.brigadier.suggestion.Suggestion(StringRange.at(9), "sub"))));
    }

    @Test
    public void getCompletionSuggestions_redirectPartial() throws Exception {
        final LiteralCommandNode<Object> actual = subject.register(LiteralArgumentBuilder.literal("actual").then(LiteralArgumentBuilder.literal("sub")));
        subject.register(LiteralArgumentBuilder.literal("redirect").redirect(actual));
        final ParseResults<Object> parse = subject.parse("redirect s", source);
        final Suggestions result = subject.getCompletionSuggestions(parse).join();
        Assert.assertThat(result.getRange(), Matchers.equalTo(StringRange.between(9, 10)));
        Assert.assertThat(result.getList(), Matchers.equalTo(Lists.newArrayList(new com.mojang.brigadier.suggestion.Suggestion(StringRange.between(9, 10), "sub"))));
    }

    @Test
    public void getCompletionSuggestions_movingCursor_redirect() throws Exception {
        final LiteralCommandNode<Object> actualOne = subject.register(LiteralArgumentBuilder.literal("actual_one").then(LiteralArgumentBuilder.literal("faz")).then(LiteralArgumentBuilder.literal("fbz")).then(LiteralArgumentBuilder.literal("gaz")));
        final LiteralCommandNode<Object> actualTwo = subject.register(LiteralArgumentBuilder.literal("actual_two"));
        subject.register(LiteralArgumentBuilder.literal("redirect_one").redirect(actualOne));
        subject.register(LiteralArgumentBuilder.literal("redirect_two").redirect(actualOne));
        testSuggestions("redirect_one faz ", 0, StringRange.at(0), "actual_one", "actual_two", "redirect_one", "redirect_two");
        testSuggestions("redirect_one faz ", 9, StringRange.between(0, 9), "redirect_one", "redirect_two");
        testSuggestions("redirect_one faz ", 10, StringRange.between(0, 10), "redirect_one");
        testSuggestions("redirect_one faz ", 12, StringRange.at(0));
        testSuggestions("redirect_one faz ", 13, StringRange.at(13), "faz", "fbz", "gaz");
        testSuggestions("redirect_one faz ", 14, StringRange.between(13, 14), "faz", "fbz");
        testSuggestions("redirect_one faz ", 15, StringRange.between(13, 15), "faz");
        testSuggestions("redirect_one faz ", 16, StringRange.at(0));
        testSuggestions("redirect_one faz ", 17, StringRange.at(0));
    }

    @Test
    public void getCompletionSuggestions_redirectPartial_withInputOffset() throws Exception {
        final LiteralCommandNode<Object> actual = subject.register(LiteralArgumentBuilder.literal("actual").then(LiteralArgumentBuilder.literal("sub")));
        subject.register(LiteralArgumentBuilder.literal("redirect").redirect(actual));
        final ParseResults<Object> parse = subject.parse(CommandSuggestionsTest.inputWithOffset("/redirect s", 1), source);
        final Suggestions result = subject.getCompletionSuggestions(parse).join();
        Assert.assertThat(result.getRange(), Matchers.equalTo(StringRange.between(10, 11)));
        Assert.assertThat(result.getList(), Matchers.equalTo(Lists.newArrayList(new com.mojang.brigadier.suggestion.Suggestion(StringRange.between(10, 11), "sub"))));
    }

    @Test
    public void getCompletionSuggestions_redirect_lots() throws Exception {
        final LiteralCommandNode<Object> loop = subject.register(LiteralArgumentBuilder.literal("redirect"));
        subject.register(LiteralArgumentBuilder.literal("redirect").then(LiteralArgumentBuilder.literal("loop").then(RequiredArgumentBuilder.argument("loop", IntegerArgumentType.integer()).redirect(loop))));
        final Suggestions result = subject.getCompletionSuggestions(subject.parse("redirect loop 1 loop 02 loop 003 ", source)).join();
        Assert.assertThat(result.getRange(), Matchers.equalTo(StringRange.at(33)));
        Assert.assertThat(result.getList(), Matchers.equalTo(Lists.newArrayList(new com.mojang.brigadier.suggestion.Suggestion(StringRange.at(33), "loop"))));
    }

    @Test
    public void getCompletionSuggestions_execute_simulation() throws Exception {
        final LiteralCommandNode<Object> execute = subject.register(LiteralArgumentBuilder.literal("execute"));
        subject.register(LiteralArgumentBuilder.literal("execute").then(LiteralArgumentBuilder.literal("as").then(RequiredArgumentBuilder.argument("name", StringArgumentType.word()).redirect(execute))).then(LiteralArgumentBuilder.literal("store").then(RequiredArgumentBuilder.argument("name", StringArgumentType.word()).redirect(execute))).then(LiteralArgumentBuilder.literal("run").executes(( c) -> 0)));
        final ParseResults<Object> parse = subject.parse("execute as Dinnerbone as", source);
        final Suggestions result = subject.getCompletionSuggestions(parse).join();
        Assert.assertThat(result.isEmpty(), Matchers.is(true));
    }

    @Test
    public void getCompletionSuggestions_execute_simulation_partial() throws Exception {
        final LiteralCommandNode<Object> execute = subject.register(LiteralArgumentBuilder.literal("execute"));
        subject.register(LiteralArgumentBuilder.literal("execute").then(LiteralArgumentBuilder.literal("as").then(LiteralArgumentBuilder.literal("bar").redirect(execute)).then(LiteralArgumentBuilder.literal("baz").redirect(execute))).then(LiteralArgumentBuilder.literal("store").then(RequiredArgumentBuilder.argument("name", StringArgumentType.word()).redirect(execute))).then(LiteralArgumentBuilder.literal("run").executes(( c) -> 0)));
        final ParseResults<Object> parse = subject.parse("execute as bar as ", source);
        final Suggestions result = subject.getCompletionSuggestions(parse).join();
        Assert.assertThat(result.getRange(), Matchers.equalTo(StringRange.at(18)));
        Assert.assertThat(result.getList(), Matchers.equalTo(Lists.newArrayList(new com.mojang.brigadier.suggestion.Suggestion(StringRange.at(18), "bar"), new com.mojang.brigadier.suggestion.Suggestion(StringRange.at(18), "baz"))));
    }
}

