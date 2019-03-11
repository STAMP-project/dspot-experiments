/**
 * Copyright ? 2018 Paul Schaub
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
package org.jivesoftware.smackx.colors;


import ConsistentColor.ConsistentColorSettings;
import org.jivesoftware.smack.test.util.SmackTestSuite;
import org.jivesoftware.smackx.colors.ConsistentColor.Deficiency;
import org.junit.Test;


public class ConsistentColorsTest extends SmackTestSuite {
    // Margin of error we allow due to floating point arithmetic
    private static final float EPS = 0.001F;

    private static final ConsistentColorSettings noDeficiency = new ConsistentColor.ConsistentColorSettings(Deficiency.none);

    private static final ConsistentColorSettings redGreenDeficiency = new ConsistentColor.ConsistentColorSettings(Deficiency.redGreenBlindness);

    private static final ConsistentColorSettings blueBlindnessDeficiency = new ConsistentColor.ConsistentColorSettings(Deficiency.blueBlindness);

    /* Below tests check the test vectors from XEP-0392 ?13.2. */
    @Test
    public void romeoNoDeficiencyTest() {
        String value = "Romeo";
        float[] expected = new float[]{ 0.281F, 0.79F, 1.0F };
        float[] actual = ConsistentColor.RGBFrom(value, ConsistentColorsTest.noDeficiency);
        ConsistentColorsTest.assertRGBEquals(expected, actual, ConsistentColorsTest.EPS);
    }

    @Test
    public void romeoRedGreenBlindnessTest() {
        String value = "Romeo";
        float[] expected = new float[]{ 1.0F, 0.674F, 0.0F };
        float[] actual = ConsistentColor.RGBFrom(value, ConsistentColorsTest.redGreenDeficiency);
        ConsistentColorsTest.assertRGBEquals(expected, actual, ConsistentColorsTest.EPS);
    }

    @Test
    public void romeoBlueBlindnessTest() {
        String value = "Romeo";
        float[] expected = new float[]{ 1.0F, 0.674F, 0.0F };
        float[] actual = ConsistentColor.RGBFrom(value, ConsistentColorsTest.blueBlindnessDeficiency);
        ConsistentColorsTest.assertRGBEquals(expected, actual, ConsistentColorsTest.EPS);
    }

    @Test
    public void julietNoDeficiencyTest() {
        String value = "juliet@capulet.lit";
        float[] expected = new float[]{ 0.337F, 1.0F, 0.0F };
        float[] actual = ConsistentColor.RGBFrom(value, ConsistentColorsTest.noDeficiency);
        ConsistentColorsTest.assertRGBEquals(expected, actual, ConsistentColorsTest.EPS);
    }

    @Test
    public void julietRedGreenBlindnessTest() {
        String value = "juliet@capulet.lit";
        float[] expected = new float[]{ 1.0F, 0.359F, 1.0F };
        float[] actual = ConsistentColor.RGBFrom(value, ConsistentColorsTest.redGreenDeficiency);
        ConsistentColorsTest.assertRGBEquals(expected, actual, ConsistentColorsTest.EPS);
    }

    @Test
    public void julietBlueBlindnessTest() {
        String value = "juliet@capulet.lit";
        float[] expected = new float[]{ 0.337F, 1.0F, 0.0F };
        float[] actual = ConsistentColor.RGBFrom(value, ConsistentColorsTest.blueBlindnessDeficiency);
        ConsistentColorsTest.assertRGBEquals(expected, actual, ConsistentColorsTest.EPS);
    }

    @Test
    public void emojiNoDeficiencyTest() {
        String value = "\ud83d\ude3a";
        float[] expected = new float[]{ 0.347F, 0.756F, 1.0F };
        float[] actual = ConsistentColor.RGBFrom(value, ConsistentColorsTest.noDeficiency);
        ConsistentColorsTest.assertRGBEquals(expected, actual, ConsistentColorsTest.EPS);
    }

    @Test
    public void emojiRedGreenBlindnessTest() {
        String value = "\ud83d\ude3a";
        float[] expected = new float[]{ 1.0F, 0.708F, 0.0F };
        float[] actual = ConsistentColor.RGBFrom(value, ConsistentColorsTest.redGreenDeficiency);
        ConsistentColorsTest.assertRGBEquals(expected, actual, ConsistentColorsTest.EPS);
    }

    @Test
    public void emojiBlueBlindnessTest() {
        String value = "\ud83d\ude3a";
        float[] expected = new float[]{ 1.0F, 0.708F, 0.0F };
        float[] actual = ConsistentColor.RGBFrom(value, ConsistentColorsTest.blueBlindnessDeficiency);
        ConsistentColorsTest.assertRGBEquals(expected, actual, ConsistentColorsTest.EPS);
    }

    @Test
    public void councilNoDeficiencyTest() {
        String value = "council";
        float[] expected = new float[]{ 0.732F, 0.56F, 1.0F };
        float[] actual = ConsistentColor.RGBFrom(value, ConsistentColorsTest.noDeficiency);
        ConsistentColorsTest.assertRGBEquals(expected, actual, ConsistentColorsTest.EPS);
    }

    @Test
    public void councilRedGreenBlindnessTest() {
        String value = "council";
        float[] expected = new float[]{ 0.732F, 0.904F, 0.0F };
        float[] actual = ConsistentColor.RGBFrom(value, ConsistentColorsTest.redGreenDeficiency);
        ConsistentColorsTest.assertRGBEquals(expected, actual, ConsistentColorsTest.EPS);
    }

    @Test
    public void councilBlueBlindnessTest() {
        String value = "council";
        float[] expected = new float[]{ 0.732F, 0.904F, 0.0F };
        float[] actual = ConsistentColor.RGBFrom(value, ConsistentColorsTest.blueBlindnessDeficiency);
        ConsistentColorsTest.assertRGBEquals(expected, actual, ConsistentColorsTest.EPS);
    }
}

