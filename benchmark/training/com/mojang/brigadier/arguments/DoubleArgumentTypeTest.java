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
public class DoubleArgumentTypeTest {
    private DoubleArgumentType type;

    @Mock
    private CommandContextBuilder<Object> context;

    @Test
    public void parse() throws Exception {
        final StringReader reader = new StringReader("15");
        Assert.assertThat(DoubleArgumentType.doubleArg().parse(reader), Matchers.is(15.0));
        Assert.assertThat(reader.canRead(), Matchers.is(false));
    }

    @Test
    public void parse_tooSmall() throws Exception {
        final StringReader reader = new StringReader("-5");
        try {
            DoubleArgumentType.doubleArg(0, 100).parse(reader);
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.doubleTooLow()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void parse_tooBig() throws Exception {
        final StringReader reader = new StringReader("5");
        try {
            DoubleArgumentType.doubleArg((-100), 0).parse(reader);
            Assert.fail();
        } catch (final CommandSyntaxException ex) {
            Assert.assertThat(ex.getType(), Matchers.is(BUILT_IN_EXCEPTIONS.doubleTooHigh()));
            Assert.assertThat(ex.getCursor(), Matchers.is(0));
        }
    }

    @Test
    public void testEquals() throws Exception {
        new com.google.common.testing.EqualsTester().addEqualityGroup(DoubleArgumentType.doubleArg(), DoubleArgumentType.doubleArg()).addEqualityGroup(DoubleArgumentType.doubleArg((-100), 100), DoubleArgumentType.doubleArg((-100), 100)).addEqualityGroup(DoubleArgumentType.doubleArg((-100), 50), DoubleArgumentType.doubleArg((-100), 50)).addEqualityGroup(DoubleArgumentType.doubleArg((-50), 100), DoubleArgumentType.doubleArg((-50), 100)).testEquals();
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertThat(DoubleArgumentType.doubleArg(), Matchers.hasToString("double()"));
        Assert.assertThat(DoubleArgumentType.doubleArg((-100)), Matchers.hasToString("double(-100.0)"));
        Assert.assertThat(DoubleArgumentType.doubleArg((-100), 100), Matchers.hasToString("double(-100.0, 100.0)"));
        Assert.assertThat(DoubleArgumentType.doubleArg(Integer.MIN_VALUE, 100), Matchers.hasToString("double(-2.147483648E9, 100.0)"));
    }
}

