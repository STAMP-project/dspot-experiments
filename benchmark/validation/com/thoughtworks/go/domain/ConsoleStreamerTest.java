/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.domain;


import java.util.ArrayList;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;


public class ConsoleStreamerTest {
    @Test
    public void streamProcessesAllLines() throws Exception {
        String[] expected = new String[]{ "First line", "Second line", "Third line" };
        final ArrayList<String> actual = new ArrayList<>();
        ConsoleStreamer console = new ConsoleStreamer(makeConsoleFile(expected).toPath(), 0L);
        console.stream(new Consumer<String>() {
            @Override
            public void accept(String s) {
                actual.add(s);
            }
        });
        Assert.assertArrayEquals(expected, actual.toArray());
        Assert.assertEquals(3L, console.totalLinesConsumed());
    }

    @Test
    public void streamSkipsToStartLine() throws Exception {
        final ArrayList<String> actual = new ArrayList<>();
        ConsoleStreamer console = new ConsoleStreamer(makeConsoleFile("first", "second", "third", "fourth").toPath(), 2L);
        console.stream(new Consumer<String>() {
            @Override
            public void accept(String s) {
                actual.add(s);
            }
        });
        Assert.assertArrayEquals(new String[]{ "third", "fourth" }, actual.toArray());
        Assert.assertEquals(2L, console.totalLinesConsumed());
    }

    @Test
    public void streamAssumesNegativeStartLineIsZero() throws Exception {
        String[] expected = new String[]{ "First line", "Second line", "Third line" };
        final ArrayList<String> actual = new ArrayList<>();
        ConsoleStreamer console = new ConsoleStreamer(makeConsoleFile(expected).toPath(), (-1L));
        console.stream(new Consumer<String>() {
            @Override
            public void accept(String s) {
                actual.add(s);
            }
        });
        Assert.assertArrayEquals(expected, actual.toArray());
        Assert.assertEquals(3L, console.totalLinesConsumed());
    }

    @Test
    public void processesNothingWhenStartLineIsBeyondEOF() throws Exception {
        final ArrayList<String> actual = new ArrayList<>();
        ConsoleStreamer console = new ConsoleStreamer(makeConsoleFile("first", "second").toPath(), 5L);
        console.stream(new Consumer<String>() {
            @Override
            public void accept(String s) {
                actual.add(s);
            }
        });
        Assert.assertTrue(actual.isEmpty());
        Assert.assertEquals(0L, console.totalLinesConsumed());
    }
}

