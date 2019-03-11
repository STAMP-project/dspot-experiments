/**
 * Copyright 2006 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.syntax;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * A test for {@link LValue#boundIdentifiers}()}.
 */
@RunWith(JUnit4.class)
public class LValueBoundNamesTest {
    @Test
    public void simpleAssignment() {
        LValueBoundNamesTest.assertBoundNames("x = 1", "x");
    }

    @Test
    public void listAssignment() {
        LValueBoundNamesTest.assertBoundNames("x, y = 1", "x", "y");
    }

    @Test
    public void complexListAssignment() {
        LValueBoundNamesTest.assertBoundNames("x, [y] = 1", "x", "y");
    }

    @Test
    public void arrayElementAssignment() {
        LValueBoundNamesTest.assertBoundNames("x[1] = 1");
    }

    @Test
    public void complexListAssignment2() {
        LValueBoundNamesTest.assertBoundNames("[[x], y], [z, w[1]] = 1", "x", "y", "z");
    }
}

