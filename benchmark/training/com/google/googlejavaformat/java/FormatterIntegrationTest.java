/**
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.googlejavaformat.java;


import com.google.googlejavaformat.Newlines;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Integration test for google-java-format.
 */
@RunWith(Parameterized.class)
public class FormatterIntegrationTest {
    private final String name;

    private final String input;

    private final String expected;

    private final String separator;

    public FormatterIntegrationTest(String name, String input, String expected) {
        this.name = name;
        this.input = input;
        this.expected = expected;
        this.separator = Newlines.getLineEnding(expected);
    }

    @Test
    public void format() {
        try {
            String output = new Formatter().formatSource(input);
            Assert.assertEquals(("bad output for " + (name)), expected, output);
        } catch (FormatterException e) {
            Assert.fail(String.format("Formatter crashed on %s: %s", name, e.getMessage()));
        }
    }

    @Test
    public void idempotentLF() {
        try {
            String mangled = expected.replace(separator, "\n");
            String output = new Formatter().formatSource(mangled);
            Assert.assertEquals(("bad output for " + (name)), mangled, output);
        } catch (FormatterException e) {
            Assert.fail(String.format("Formatter crashed on %s: %s", name, e.getMessage()));
        }
    }

    @Test
    public void idempotentCR() throws IOException {
        try {
            String mangled = expected.replace(separator, "\r");
            String output = new Formatter().formatSource(mangled);
            Assert.assertEquals(("bad output for " + (name)), mangled, output);
        } catch (FormatterException e) {
            Assert.fail(String.format("Formatter crashed on %s: %s", name, e.getMessage()));
        }
    }

    @Test
    public void idempotentCRLF() {
        try {
            String mangled = expected.replace(separator, "\r\n");
            String output = new Formatter().formatSource(mangled);
            Assert.assertEquals(("bad output for " + (name)), mangled, output);
        } catch (FormatterException e) {
            Assert.fail(String.format("Formatter crashed on %s: %s", name, e.getMessage()));
        }
    }
}

