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
public class StringArgumentTypeTest {
    @Mock
    private CommandContextBuilder<Object> context;

    @Test
    public void testParseWord() throws Exception {
        final StringReader reader = Mockito.mock(StringReader.class);
        Mockito.when(reader.readUnquotedString()).thenReturn("hello");
        Assert.assertThat(StringArgumentType.word().parse(reader), Matchers.equalTo("hello"));
        Mockito.verify(reader).readUnquotedString();
    }

    @Test
    public void testParseString() throws Exception {
        final StringReader reader = Mockito.mock(StringReader.class);
        Mockito.when(reader.readString()).thenReturn("hello world");
        Assert.assertThat(StringArgumentType.string().parse(reader), Matchers.equalTo("hello world"));
        Mockito.verify(reader).readString();
    }

    @Test
    public void testParseGreedyString() throws Exception {
        final StringReader reader = new StringReader("Hello world! This is a test.");
        Assert.assertThat(StringArgumentType.greedyString().parse(reader), Matchers.equalTo("Hello world! This is a test."));
        Assert.assertThat(reader.canRead(), Matchers.is(false));
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertThat(StringArgumentType.string(), Matchers.hasToString("string()"));
    }

    @Test
    public void testEscapeIfRequired_notRequired() throws Exception {
        Assert.assertThat(StringArgumentType.escapeIfRequired("hello"), Matchers.is(Matchers.equalTo("hello")));
        Assert.assertThat(StringArgumentType.escapeIfRequired(""), Matchers.is(Matchers.equalTo("")));
    }

    @Test
    public void testEscapeIfRequired_multipleWords() throws Exception {
        Assert.assertThat(StringArgumentType.escapeIfRequired("hello world"), Matchers.is(Matchers.equalTo("\"hello world\"")));
    }

    @Test
    public void testEscapeIfRequired_quote() throws Exception {
        Assert.assertThat(StringArgumentType.escapeIfRequired("hello \"world\"!"), Matchers.is(Matchers.equalTo("\"hello \\\"world\\\"!\"")));
    }

    @Test
    public void testEscapeIfRequired_escapes() throws Exception {
        Assert.assertThat(StringArgumentType.escapeIfRequired("\\"), Matchers.is(Matchers.equalTo("\"\\\\\"")));
    }

    @Test
    public void testEscapeIfRequired_singleQuote() throws Exception {
        Assert.assertThat(StringArgumentType.escapeIfRequired("\""), Matchers.is(Matchers.equalTo("\"\\\"\"")));
    }
}

