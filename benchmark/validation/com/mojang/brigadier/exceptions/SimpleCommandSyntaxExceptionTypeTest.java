/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
/**
 * Licensed under the MIT license.
 */
package com.mojang.brigadier.exceptions;


import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class SimpleCommandSyntaxExceptionTypeTest {
    @Test
    public void createWithContext() throws Exception {
        final SimpleCommandExceptionType type = new SimpleCommandExceptionType(new LiteralMessage("error"));
        final StringReader reader = new StringReader("Foo bar");
        reader.setCursor(5);
        final CommandSyntaxException exception = type.createWithContext(reader);
        Assert.assertThat(exception.getType(), Matchers.is(type));
        Assert.assertThat(exception.getInput(), Matchers.is("Foo bar"));
        Assert.assertThat(exception.getCursor(), Matchers.is(5));
    }

    @Test
    public void getContext_none() throws Exception {
        final CommandSyntaxException exception = new CommandSyntaxException(Mockito.mock(CommandExceptionType.class), new LiteralMessage("error"));
        Assert.assertThat(exception.getContext(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void getContext_short() throws Exception {
        final CommandSyntaxException exception = new CommandSyntaxException(Mockito.mock(CommandExceptionType.class), new LiteralMessage("error"), "Hello world!", 5);
        Assert.assertThat(exception.getContext(), Matchers.equalTo("Hello<--[HERE]"));
    }

    @Test
    public void getContext_long() throws Exception {
        final CommandSyntaxException exception = new CommandSyntaxException(Mockito.mock(CommandExceptionType.class), new LiteralMessage("error"), "Hello world! This has an error in it. Oh dear!", 20);
        Assert.assertThat(exception.getContext(), Matchers.equalTo("...d! This ha<--[HERE]"));
    }
}

