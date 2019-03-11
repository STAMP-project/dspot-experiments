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


public class FontColorTest {
    @Test
    public void testResetColor() {
        char resetColor = 61440;
        Assert.assertTrue(FontColor.isValid(resetColor));
    }

    @Test
    public void testFirstColor() {
        char firstColor = 57344;
        Assert.assertTrue(FontColor.isValid(firstColor));
    }

    @Test
    public void testLastColor() {
        char lastColor = 61439;
        Assert.assertTrue(FontColor.isValid(lastColor));
    }

    @Test
    public void testBetweenColor() {
        char betweenColor = 60303;
        Assert.assertTrue(FontColor.isValid(betweenColor));
    }

    @Test
    public void testInvalidColor() {
        char invalidColor = 41231;
        Assert.assertFalse(FontColor.isValid(invalidColor));
    }
}

