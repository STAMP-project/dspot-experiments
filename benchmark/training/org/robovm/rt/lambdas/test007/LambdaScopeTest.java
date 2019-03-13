/**
 * Copyright (C) 2015 The Android Open Source Project
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
package org.robovm.rt.lambdas.test007;


import org.junit.Assert;
import org.junit.Test;


/**
 * Test returning a lambda expression accessing local values that will be used outside his
 * definition scope.
 */
public class LambdaScopeTest {
    @Test
    public void test001() {
        Assert.assertEquals(35, UseLambda.testAddDouble(15.4, 9.08));
    }
}

