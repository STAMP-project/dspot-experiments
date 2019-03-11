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


import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for array dimension handling, especially mixed array notation and type annotations on
 * dimensions.
 */
@RunWith(Parameterized.class)
public class ArrayDimensionTest {
    private final String input;

    public ArrayDimensionTest(String input) {
        this.input = input;
    }

    @Test
    public void format() throws Exception {
        String source = ("class T {" + (input)) + "}";
        String formatted = new Formatter().formatSource(source);
        String statement = formatted.substring("class T {".length(), ((formatted.length()) - ("}\n".length())));
        // ignore line breaks after declaration-style annotations
        statement = Joiner.on(' ').join(Splitter.on('\n').omitEmptyStrings().trimResults().split(statement));
        assertThat(statement).isEqualTo(input);
    }
}

