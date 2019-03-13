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
package com.github.pedrovgs.problem78;


import org.junit.Assert;
import org.junit.Test;


/**
 * Before to read this test read AutoBoxingTrick class, this is a really funny Java trick.
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class AutoBoxingTrickTest {
    private AutoBoxingTrick autoBoxingTrick;

    @Test
    public void shouldReturnFalseButReturnsTrue() {
        Assert.assertTrue(autoBoxingTrick.compare(1, 1));
    }

    @Test
    public void shouldReturnFalseAndReturnsFalse() {
        Assert.assertFalse(autoBoxingTrick.compare(1000, 1000));
    }
}

