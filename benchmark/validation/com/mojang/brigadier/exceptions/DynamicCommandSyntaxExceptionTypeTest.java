/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
/**
 * Licensed under the MIT license.
 */
package com.mojang.brigadier.exceptions;


import com.mojang.brigadier.StringReader;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class DynamicCommandSyntaxExceptionTypeTest {
    private DynamicCommandExceptionType type;

    @Test
    public void createWithContext() throws Exception {
        final StringReader reader = new StringReader("Foo bar");
        reader.setCursor(5);
        final CommandSyntaxException exception = type.createWithContext(reader, "World");
        Assert.assertThat(exception.getType(), Matchers.is(type));
        Assert.assertThat(exception.getInput(), Matchers.is("Foo bar"));
        Assert.assertThat(exception.getCursor(), Matchers.is(5));
    }
}

