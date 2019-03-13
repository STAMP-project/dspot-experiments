/**
 * Copyright 2014 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.lang;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class CharacterTest {
    @Test
    public void digitsRecognized() {
        Assert.assertEquals(2, Character.digit('2', 10));
        Assert.assertEquals((-1), Character.digit('.', 10));
        Assert.assertEquals(6, Character.digit('\u096c', 10));
        Assert.assertEquals(15, Character.digit('F', 16));
    }

    @Test
    public void classesRecognized() {
        Assert.assertEquals(Character.DECIMAL_DIGIT_NUMBER, Character.getType('2'));
        Assert.assertEquals(Character.UPPERCASE_LETTER, Character.getType('Q'));
        Assert.assertEquals(Character.LOWERCASE_LETTER, Character.getType('w'));
        Assert.assertEquals(Character.MATH_SYMBOL, Character.getType(8695));
        Assert.assertEquals(Character.NON_SPACING_MARK, Character.getType(65061));
        Assert.assertEquals(Character.DECIMAL_DIGIT_NUMBER, Character.getType(120793));
    }
}

