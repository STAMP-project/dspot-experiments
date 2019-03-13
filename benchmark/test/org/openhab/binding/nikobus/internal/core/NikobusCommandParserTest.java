/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nikobus.internal.core;


import org.junit.Test;


/**
 *
 *
 * @author Davy Vanherbergen
 * @since 1.3.0
 */
public class NikobusCommandParserTest {
    @Test
    public void canSplitCommands() throws InterruptedException {
        testSplit(new String[]{ "AA#N1234", "56\r#N12345", "6\rDD" }, new NikobusCommand[]{ new NikobusCommand("AA"), new NikobusCommand("#N123456", 2) });
        String[] tmp = new String[60];
        for (int i = 0; i < 60; i++) {
            tmp[i] = "#N123456\r";
        }
        testSplit(new String[]{ "#N123456\r" }, new NikobusCommand[]{ new NikobusCommand("#N123456", 1) });
        // when long presses are received, only full long presses should
        // be published
        testSplit(tmp, new NikobusCommand[]{ new NikobusCommand("#N123456", 25), new NikobusCommand("#N123456", 37), new NikobusCommand("#N123456", 49) });
        testSplit(new String[]{ "AA#N123456\r#$5012#N12345", "6\rDD" }, new NikobusCommand[]{ new NikobusCommand("AA"), new NikobusCommand("#N123456"), new NikobusCommand("#"), new NikobusCommand("$5012"), new NikobusCommand("#N123456") });
        testSplit(new String[]{ "AA#N1234", "56\r#N12345", "6\rDD" }, new NikobusCommand[]{ new NikobusCommand("AA"), new NikobusCommand("#N123456", 2) });
    }
}

