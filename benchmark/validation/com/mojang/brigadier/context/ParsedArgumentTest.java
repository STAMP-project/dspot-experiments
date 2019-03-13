/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 */
/**
 * Licensed under the MIT license.
 */
package com.mojang.brigadier.context;


import com.mojang.brigadier.StringReader;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ParsedArgumentTest {
    @Test
    public void testEquals() throws Exception {
        new com.google.common.testing.EqualsTester().addEqualityGroup(new ParsedArgument(0, 3, "bar"), new ParsedArgument(0, 3, "bar")).addEqualityGroup(new ParsedArgument(3, 6, "baz"), new ParsedArgument(3, 6, "baz")).addEqualityGroup(new ParsedArgument(6, 9, "baz"), new ParsedArgument(6, 9, "baz")).testEquals();
    }

    @Test
    public void getRaw() throws Exception {
        final StringReader reader = new StringReader("0123456789");
        final ParsedArgument<Object, String> argument = new ParsedArgument(2, 5, "");
        Assert.assertThat(argument.getRange().get(reader), Matchers.equalTo("234"));
    }
}

