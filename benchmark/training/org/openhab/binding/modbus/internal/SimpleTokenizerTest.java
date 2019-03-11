/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.modbus.internal;


import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openhab.model.item.binding.BindingConfigParseException;


@RunWith(Parameterized.class)
public class SimpleTokenizerTest {
    private String inputString;

    private char delimiter;

    private String[] expectedTokens;

    public SimpleTokenizerTest(String inputString, char delimiter, String[] expectedTokens) {
        this.inputString = inputString;
        this.delimiter = delimiter;
        this.expectedTokens = expectedTokens;
    }

    @Test
    public void testParsing() throws BindingConfigParseException {
        Assert.assertThat(new SimpleTokenizer(delimiter).parse(inputString), CoreMatchers.is(CoreMatchers.equalTo(Arrays.asList(expectedTokens))));
    }
}

