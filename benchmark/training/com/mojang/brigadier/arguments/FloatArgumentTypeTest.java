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
public class FloatArgumentTypeTest {
    private FloatArgumentType type;

    @Mock
    private CommandContextBuilder<Object> context;

    @Test
    public void parse() throws Exception {
        final StringReader reader = new StringReader("15");
        Assert.assertThat(FloatArgumentType.floatArg().parse(reader), Matchers.is(15.0F));
        Assert.assertThat(reader.canRead(), Matchers.is(false));
    }

    @Test
    public void parse_tooSmall() throws Exception {
        final StringReader reader = new StringReader("-5");
        try {
            FloatArgumentType.floatArg(0, 100).parse(reader);
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.floatTooLow()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void parse_tooBig() throws Exception {
        final StringReader reader = new StringReader("5");
        try {
            FloatArgumentType.floatArg((-100), 0).parse(reader);
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.floatTooHigh()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void testEquals() throws Exception {
        new com.google.common.testing.EqualsTester().addEqualityGroup(FloatArgumentType.floatArg(), FloatArgumentType.floatArg()).addEqualityGroup(FloatArgumentType.floatArg((-100), 100), FloatArgumentType.floatArg((-100), 100)).addEqualityGroup(FloatArgumentType.floatArg((-100), 50), FloatArgumentType.floatArg((-100), 50)).addEqualityGroup(FloatArgumentType.floatArg((-50), 100), FloatArgumentType.floatArg((-50), 100)).testEquals();
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertThat(FloatArgumentType.floatArg(), Matchers.hasToString("float()"));
        Assert.assertThat(FloatArgumentType.floatArg((-100)), Matchers.hasToString("float(-100.0)"));
        Assert.assertThat(FloatArgumentType.floatArg((-100), 100), Matchers.hasToString("float(-100.0, 100.0)"));
        Assert.assertThat(FloatArgumentType.floatArg(Integer.MIN_VALUE, 100), Matchers.hasToString("float(-2.14748365E9, 100.0)"));
    }
}

