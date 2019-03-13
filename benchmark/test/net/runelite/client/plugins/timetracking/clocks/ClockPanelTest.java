/**
 * Copyright (c) 2018, Jamy C <https://github.com/jamyc>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.timetracking.clocks;


import java.time.format.DateTimeParseException;
import org.junit.Assert;
import org.junit.Test;


public class ClockPanelTest {
    @Test
    public void properColonSeparatedTimeStringShouldReturnCorrectSeconds() {
        Assert.assertEquals(5, ClockPanel.stringToSeconds("5"));
        Assert.assertEquals(50, ClockPanel.stringToSeconds("50"));
        Assert.assertEquals(120, ClockPanel.stringToSeconds("2:00"));
        Assert.assertEquals(120, ClockPanel.stringToSeconds("0:120"));
        Assert.assertEquals(120, ClockPanel.stringToSeconds("0:0:120"));
        Assert.assertEquals(1200, ClockPanel.stringToSeconds("20:00"));
        Assert.assertEquals(50, ClockPanel.stringToSeconds("00:00:50"));
        Assert.assertEquals(121, ClockPanel.stringToSeconds("00:02:01"));
        Assert.assertEquals(3660, ClockPanel.stringToSeconds("01:01:00"));
        Assert.assertEquals(9000, ClockPanel.stringToSeconds("2:30:00"));
        Assert.assertEquals(9033, ClockPanel.stringToSeconds("02:30:33"));
        Assert.assertEquals(82800, ClockPanel.stringToSeconds("23:00:00"));
        Assert.assertEquals(400271, ClockPanel.stringToSeconds("111:11:11"));
    }

    @Test
    public void properIntuitiveTimeStringShouldReturnCorrectSeconds() {
        Assert.assertEquals(5, ClockPanel.stringToSeconds("5s"));
        Assert.assertEquals(50, ClockPanel.stringToSeconds("50s"));
        Assert.assertEquals(120, ClockPanel.stringToSeconds("2m"));
        Assert.assertEquals(120, ClockPanel.stringToSeconds("120s"));
        Assert.assertEquals(1200, ClockPanel.stringToSeconds("20m"));
        Assert.assertEquals(121, ClockPanel.stringToSeconds("2m1s"));
        Assert.assertEquals(121, ClockPanel.stringToSeconds("2m     1s"));
        Assert.assertEquals(3660, ClockPanel.stringToSeconds("1h 1m"));
        Assert.assertEquals(3660, ClockPanel.stringToSeconds("61m"));
        Assert.assertEquals(3660, ClockPanel.stringToSeconds("3660s"));
        Assert.assertEquals(9000, ClockPanel.stringToSeconds("2h 30m"));
        Assert.assertEquals(9033, ClockPanel.stringToSeconds("2h 30m 33s"));
        Assert.assertEquals(82800, ClockPanel.stringToSeconds("23h"));
        Assert.assertEquals(400271, ClockPanel.stringToSeconds("111h 11m 11s"));
    }

    @Test
    public void incorrectTimeStringShouldThrowException() {
        Class numberEx = NumberFormatException.class;
        Class dateTimeEx = DateTimeParseException.class;
        tryFail("a", numberEx);
        tryFail("abc", numberEx);
        tryFail("aa:bb:cc", numberEx);
        tryFail("01:12=", numberEx);
        tryFail("s", dateTimeEx);
        tryFail("1s 1m", dateTimeEx);
        tryFail("20m:10s", dateTimeEx);
        tryFail("20hh10m10s", dateTimeEx);
    }
}

