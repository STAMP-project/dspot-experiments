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
public class LongArgumentTypeTest {
    private LongArgumentType type;

    @Mock
    private CommandContextBuilder<Object> context;

    @Test
    public void parse() throws Exception {
        final StringReader reader = new StringReader("15");
        Assert.assertThat(LongArgumentType.longArg().parse(reader), Matchers.is(15L));
        Assert.assertThat(reader.canRead(), Matchers.is(false));
    }

    @Test
    public void parse_tooSmall() throws Exception {
        final StringReader reader = new StringReader("-5");
        try {
            LongArgumentType.longArg(0, 100).parse(reader);
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.longTooLow()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void parse_tooBig() throws Exception {
        final StringReader reader = new StringReader("5");
        try {
            LongArgumentType.longArg((-100), 0).parse(reader);
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.longTooHigh()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void testEquals() throws Exception {
        new com.google.common.testing.EqualsTester().addEqualityGroup(LongArgumentType.longArg(), LongArgumentType.longArg()).addEqualityGroup(LongArgumentType.longArg((-100), 100), LongArgumentType.longArg((-100), 100)).addEqualityGroup(LongArgumentType.longArg((-100), 50), LongArgumentType.longArg((-100), 50)).addEqualityGroup(LongArgumentType.longArg((-50), 100), LongArgumentType.longArg((-50), 100)).testEquals();
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertThat(LongArgumentType.longArg(), Matchers.hasToString("longArg()"));
        Assert.assertThat(LongArgumentType.longArg((-100)), Matchers.hasToString("longArg(-100)"));
        Assert.assertThat(LongArgumentType.longArg((-100), 100), Matchers.hasToString("longArg(-100, 100)"));
        Assert.assertThat(LongArgumentType.longArg(Long.MIN_VALUE, 100), Matchers.hasToString("longArg(-9223372036854775808, 100)"));
    }
}

