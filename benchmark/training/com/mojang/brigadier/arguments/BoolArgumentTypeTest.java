/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
/**
 * Licensed under the MIT license.
 */
package com.mojang.brigadier.arguments;


import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class BoolArgumentTypeTest {
    private BoolArgumentType type;

    @Mock
    private CommandContextBuilder<Object> context;

    @Test
    public void parse() throws Exception {
        final StringReader reader = Mockito.mock(StringReader.class);
        Mockito.when(reader.readBoolean()).thenReturn(true);
        Assert.assertThat(type.parse(reader), Matchers.is(true));
        Mockito.verify(reader).readBoolean();
    }
}

