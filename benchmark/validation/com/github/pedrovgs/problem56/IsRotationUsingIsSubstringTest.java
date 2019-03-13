/**
 * Copyright (C) 2014 Pedro Vicente G?mez S?nchez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pedrovgs.problem56;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class IsRotationUsingIsSubstringTest {
    private IsRotationUsingIsSubstring isRotation;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullInstancesAsFirstParameter() {
        isRotation.check(null, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullInstancesAsSecondParameter() {
        isRotation.check("", null);
    }

    @Test
    public void shouldReturnFalseIfS2IsNotRotationOfS1() {
        String s1 = "waterbottle";
        String s2 = "pedro";
        boolean result = isRotation.check(s1, s2);
        Assert.assertFalse(result);
    }

    @Test
    public void shouldReturnTrueIfS2IsNotRotationOfS1() {
        String s1 = "waterbottle";
        String s2 = "erbottlewat";
        boolean result = isRotation.check(s1, s2);
        Assert.assertTrue(result);
    }
}

