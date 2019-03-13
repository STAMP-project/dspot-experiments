/**
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.rendering;


import org.junit.Assert;
import org.junit.Test;


public class FontUnderlineTest {
    private static final char START_UNDERLINE = 61441;

    private static final char END_UNDERLINE = 61442;

    @Test
    public void testStartUnderline() {
        Assert.assertTrue(FontUnderline.isValid(FontUnderlineTest.START_UNDERLINE));
    }

    @Test
    public void testEndUnderline() {
        Assert.assertTrue(FontUnderline.isValid(FontUnderlineTest.END_UNDERLINE));
    }

    @Test
    public void testInvalidUnderline() {
        char invalidUnderline = 61443;
        Assert.assertFalse(FontUnderline.isValid(invalidUnderline));
    }

    @Test
    public void testMarkUnderlined() {
        String testString = "string";
        Assert.assertTrue(FontUnderline.markUnderlined(testString).equals((((FontUnderlineTest.START_UNDERLINE) + testString) + (FontUnderlineTest.END_UNDERLINE))));
    }
}

