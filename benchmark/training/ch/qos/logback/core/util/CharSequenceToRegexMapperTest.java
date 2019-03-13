/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core.util;


import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class CharSequenceToRegexMapperTest {
    static Locale KO_LOCALE = new Locale("ko", "KR");

    Locale oldLocale = Locale.getDefault();

    @Test
    public void findMinMaxLengthsInSymbolsWithTrivialInputs() {
        String[] symbols = new String[]{ "a", "bb" };
        int[] results = CharSequenceToRegexMapper.findMinMaxLengthsInSymbols(symbols);
        Assert.assertEquals(1, results[0]);
        Assert.assertEquals(2, results[1]);
    }

    @Test
    public void emptyStringValuesShouldBeIgnoredByFindMinMaxLengthsInSymbols() {
        String[] symbols = new String[]{ "aaa", "" };
        int[] results = CharSequenceToRegexMapper.findMinMaxLengthsInSymbols(symbols);
        Assert.assertEquals(3, results[0]);
        Assert.assertEquals(3, results[1]);
    }
}

