/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
/**
 * Licensed under the MIT license.
 */
package com.mojang.brigadier;


import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CommandDispatcherUsagesTest {
    private CommandDispatcher<Object> subject;

    @Mock
    private Object source;

    @Mock
    private Command<Object> command;

    @Test
    public void testAllUsage_noCommands() throws Exception {
        subject = new CommandDispatcher();
        final String[] results = subject.getAllUsage(subject.getRoot(), source, true);
        Assert.assertThat(results, Matchers.is(Matchers.emptyArray()));
    }

    @Test
    public void testSmartUsage_noCommands() throws Exception {
        subject = new CommandDispatcher();
        final Map<CommandNode<Object>, String> results = subject.getSmartUsage(subject.getRoot(), source);
        Assert.assertThat(results.entrySet(), Matchers.is(Matchers.empty()));
    }

    @Test
    public void testAllUsage_root() throws Exception {
        final String[] results = subject.getAllUsage(subject.getRoot(), source, true);
        Assert.assertThat(results, Matchers.equalTo(new String[]{ "a 1 i", "a 1 ii", "a 2 i", "a 2 ii", "b 1", "c", "e", "e 1", "e 1 i", "e 1 ii", "f 1 i", "f 2 ii", "g", "g 1 i", "h", "h 1 i", "h 2 i ii", "h 3", "i", "i 1", "i 2", "j ...", "k -> h" }));
    }

    @Test
    public void testSmartUsage_root() throws Exception {
        final Map<CommandNode<Object>, String> results = subject.getSmartUsage(subject.getRoot(), source);
        Assert.assertThat(results, Matchers.equalTo(ImmutableMap.builder().put(get("a"), "a (1|2)").put(get("b"), "b 1").put(get("c"), "c").put(get("e"), "e [1]").put(get("f"), "f (1|2)").put(get("g"), "g [1]").put(get("h"), "h [1|2|3]").put(get("i"), "i [1|2]").put(get("j"), "j ...").put(get("k"), "k -> h").build()));
    }

    @Test
    public void testSmartUsage_h() throws Exception {
        final Map<CommandNode<Object>, String> results = subject.getSmartUsage(get("h"), source);
        Assert.assertThat(results, Matchers.equalTo(ImmutableMap.builder().put(get("h 1"), "[1] i").put(get("h 2"), "[2] i ii").put(get("h 3"), "[3]").build()));
    }

    @Test
    public void testSmartUsage_offsetH() throws Exception {
        final StringReader offsetH = new StringReader("/|/|/h");
        offsetH.setCursor(5);
        final Map<CommandNode<Object>, String> results = subject.getSmartUsage(get(offsetH), source);
        Assert.assertThat(results, Matchers.equalTo(ImmutableMap.builder().put(get("h 1"), "[1] i").put(get("h 2"), "[2] i ii").put(get("h 3"), "[3]").build()));
    }
}

