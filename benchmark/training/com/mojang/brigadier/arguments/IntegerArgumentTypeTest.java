/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
/**
 * Licensed under the MIT license.
 */
package com.mojang.brigadier.arguments;


import CommandSyntaxException.BUILT_IN_EXCEPTIONS;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class IntegerArgumentTypeTest {
    private IntegerArgumentType type;

    @Mock
    private CommandContextBuilder<Object> context;

    @Test
    public void parse() throws Exception {
        final StringReader reader = new StringReader("15");
        Assert.assertThat(IntegerArgumentType.integer().parse(reader), Matchers.is(15));
        Assert.assertThat(reader.canRead(), Matchers.is(false));
    }

    @Test
    public void parse_tooSmall() throws Exception {
        final StringReader reader = new StringReader("-5");
        try {
            IntegerArgumentType.integer(0, 100).parse(reader);
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.integerTooLow()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void parse_tooBig() throws Exception {
        final StringReader reader = new StringReader("5");
        try {
            IntegerArgumentType.integer((-100), 0).parse(reader);
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.integerTooHigh()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void testEquals() throws Exception {
        new com.google.common.testing.EqualsTester().addEqualityGroup(IntegerArgumentType.integer(), IntegerArgumentType.integer()).addEqualityGroup(IntegerArgumentType.integer((-100), 100), IntegerArgumentType.integer((-100), 100)).addEqualityGroup(IntegerArgumentType.integer((-100), 50), IntegerArgumentType.integer((-100), 50)).addEqualityGroup(IntegerArgumentType.integer((-50), 100), IntegerArgumentType.integer((-50), 100)).testEquals();
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertThat(IntegerArgumentType.integer(), Matchers.hasToString("integer()"));
        Assert.assertThat(IntegerArgumentType.integer((-100)), Matchers.hasToString("integer(-100)"));
        Assert.assertThat(IntegerArgumentType.integer((-100), 100), Matchers.hasToString("integer(-100, 100)"));
        Assert.assertThat(IntegerArgumentType.integer(Integer.MIN_VALUE, 100), Matchers.hasToString("integer(-2147483648, 100)"));
    }
}

