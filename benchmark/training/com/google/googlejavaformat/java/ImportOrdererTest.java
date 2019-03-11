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


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for {@link ImportOrderer}.
 */
@RunWith(Parameterized.class)
public class ImportOrdererTest {
    private final String input;

    private final String reordered;

    public ImportOrdererTest(String input, String reordered) {
        this.input = input;
        this.reordered = reordered;
    }

    @Test
    public void reorder() throws FormatterException {
        try {
            String output = ImportOrderer.reorderImports(input);
            assertWithMessage("Expected exception").that(reordered).doesNotMatch("^!!");
            assertWithMessage(input).that(output).isEqualTo(reordered);
        } catch (FormatterException e) {
            if (!(reordered.startsWith("!!"))) {
                throw e;
            }
            assertThat(reordered).endsWith("\n");
            assertThat(e.getMessage()).isEqualTo(("error: " + (reordered.substring(2, ((reordered.length()) - 1)))));
        }
    }
}

